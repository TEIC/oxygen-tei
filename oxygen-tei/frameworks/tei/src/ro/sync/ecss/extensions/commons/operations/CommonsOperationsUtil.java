/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2011 Syncro Soft SRL, Romania.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistribution of source or in binary form is allowed only with
 *  the prior written permission of Syncro Soft SRL.
 *
 *  2. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  3. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  4. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Syncro Soft SRL (http://www.sync.ro/)."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  5. The names "Oxygen" and "Syncro Soft SRL" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact support@oxygenxml.com.
 *
 *  6. Products derived from this software may not be called "Oxygen",
 *  nor may "Oxygen" appear in their name, without prior written
 *  permission of the Syncro Soft SRL.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE SYNCRO SOFT SRL OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 */
package ro.sync.ecss.extensions.commons.operations;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.xml.namespace.QName;




import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.NamespaceContext;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResultInsertConstants;
import ro.sync.util.Equaler;
import ro.sync.xml.XmlUtil;

/**
 * Util methods for common Author operations.
 */

public class CommonsOperationsUtil {

  /**
   * Unwrap node tags.
   * 
   * @param authorAccess The Author access.
   * @param nodeToUnwrap The node to unwrap.
   * 
   * @throws BadLocationException
   */
  public static void unwrapTags(AuthorAccess authorAccess, AuthorNode nodeToUnwrap)
  throws BadLocationException {
    // Unwrap the node
    if (nodeToUnwrap != null && 
        (nodeToUnwrap != authorAccess.getDocumentController().getAuthorDocumentNode().getRootElement())) {
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      int nodeStartOffset = nodeToUnwrap.getStartOffset();
      int nodeEndOffset = nodeToUnwrap.getEndOffset();
      int nextOffset;
      
      
      if (caretOffset <= nodeStartOffset) {
        // The caret offset remains the same
        nextOffset = caretOffset;
      } else if (caretOffset > nodeStartOffset && caretOffset <= nodeEndOffset){
        // The caret offset must be moved to the left 
        nextOffset = caretOffset - 1;
      } else {
        // The caret offset must be moved to the left 
        nextOffset = caretOffset - 2;
      }
      
      //Strip the tags of the node
      //Copy its content first.
      int contentStart = nodeToUnwrap.getStartOffset() + 1;
      int contentEnd = nodeToUnwrap.getEndOffset() - 1;

      AuthorDocumentFragment unwrapped = null;
      if (contentStart <= contentEnd) {
        // Create a fragment from the node content
        unwrapped = authorAccess.getDocumentController().createDocumentFragment(contentStart, contentEnd);
      }

      // Remove the entire node
      boolean deleteNode = authorAccess.getDocumentController().deleteNode(nodeToUnwrap);
      if (deleteNode && unwrapped != null) {
        // Add the content
        authorAccess.getDocumentController().insertFragment(nodeStartOffset, unwrapped);
        // Update the caret position
        authorAccess.getEditorAccess().setCaretPosition(nextOffset);
      }
    }
  }
  
  /**
   * Surround selection with fragment.
   * 
   * @param authorAccess Author access.
   * @param schemaAware <code>true</code> for schema aware operation
   * @param xmlFragment The xml fragment
   * @throws AuthorOperationException
   */
  public static void surroundWithFragment(
      AuthorAccess authorAccess, boolean schemaAware, String xmlFragment)
  throws AuthorOperationException {
    //The XML may contain an editor template for caret positioning.
    boolean moveCaretToSpecifiedPosition =
      MoveCaretUtil.hasImposedEditorVariableCaretOffset(xmlFragment);
    int insertionOffset = authorAccess.getEditorAccess().getCaretOffset();

    if (authorAccess.getEditorAccess().hasSelection()) {
      // We have selection. Do a simple insert.
      insertionOffset = surroundWithFragment(authorAccess, xmlFragment, 
          authorAccess.getEditorAccess().getSelectionStart(), 
          authorAccess.getEditorAccess().getSelectionEnd() - 1); 
    } else {
      // No selection. Schema aware insertion can be performed.
      if (!schemaAware) {
        authorAccess.getDocumentController().insertXMLFragment(xmlFragment, insertionOffset);
      } else {
        // There is no XPath and no selection, do insert.
        SchemaAwareHandlerResult result =
          authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
              xmlFragment, insertionOffset);

        //Keep the insertion offset.
        if (result != null) {
          Integer off = (Integer) result.getResult(
              SchemaAwareHandlerResultInsertConstants.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET);
          if (off != null) {
            insertionOffset = off.intValue(); 
          }
        }
      }
    }

    if (moveCaretToSpecifiedPosition) {
      //Detect the position in the Author page where the caret should be placed.
      MoveCaretUtil.moveCaretToImposedEditorVariableOffset(authorAccess, insertionOffset);
    }
  }

  /**
   * Surround the content between start and end offset with the given fragment.
   * 
   * @param authorAccess Author access.
   * @param xmlFragment The xml fragment
   * @param start The start offset. Inclusive.
   * @param end The end offset. Inclusive.
   * @return Insertion offset.
   * @throws AuthorOperationException
   */
  public static int surroundWithFragment(AuthorAccess authorAccess, String xmlFragment, int start, int end)
    throws AuthorOperationException {
    // We have selection. Do a simple insert.
    // EXM-29818 Balance the selection
    int[] balancedSelection = authorAccess.getEditorAccess().getBalancedSelection(start, end + 1);
    authorAccess.getDocumentController().surroundInFragment(
        xmlFragment, 
        balancedSelection[0],
        // Inclusive end offset
        balancedSelection[1] - 1);

    //Modify the offset to be used for restoring the caret position.
    return authorAccess.getEditorAccess().getSelectionStart();
  }
  
  /**
   * Sets an attribute value. If the value is <code>null</code> the attribute will
   * be removed from the element. If the value is the empty string and removeIfEmpty
   * is <code>true</code> the attribute will also be removed.
   * 
   * @param ctrl Attribute controller. 
   * @param targetElement The target element.
   * @param attributeQName Attribute to edit.
   * @param value Current value. Illegal characters in the value WILL NOT be escaped. 
   * @param removeIfEmpty <code>true</code> to remove the attribute when an empty 
   * value is set.
   * 
   * @return The QName with which the attribute was committed. From the given attributeQName
   * only the local name and namespace are taken into account. 
   */
  public static String setAttributeValue(
      AuthorDocumentController ctrl, 
      AuthorElement targetElement, 
      QName attributeQName, 
      String value,
      boolean removeIfEmpty) {
    return setAttributeValue(ctrl, targetElement, attributeQName, value, value, removeIfEmpty);
  }
  
  /**
   * Sets an attribute value. If the value is <code>null</code> the attribute will
   * be removed from the element. If the value is the empty string and removeIfEmpty
   * is <code>true</code> the attribute will also be removed.
   * 
   * @param ctrl Attribute controller. 
   * @param targetElement The target element.
   * @param attributeQName Attribute to edit.
   * @param value Current value. Illegal characters in the value WILL NOT be escaped. All entities
   * must be already escaped in this value. For example:   <pre>ab&amp;quot;c&amp;amp;&amp;#36;</pre>
   * @param normalizedValue The value with normalized whitespaces and expanded entities. For example: <pre>ab"c&$</pre>
   * @param removeIfEmpty <code>true</code> to remove the attribute when an empty 
   * value is set.
   * 
   * @return The QName with which the attribute was committed. From the given attributeQName
   * only the local name and namespace are taken into account. 
   */
  public static String setAttributeValue(
      AuthorDocumentController ctrl, 
      AuthorElement targetElement, 
      QName attributeQName, 
      String value,
      String normalizedValue,
      boolean removeIfEmpty) {
    boolean addNamespaceDeclaration = false;    
    String attributeName = attributeQName.getLocalPart();
    String prefix = null;
    String namespace = attributeQName.getNamespaceURI();
    if (namespace != null && !"".equals(namespace)) {
      String attributeQNameOnElement = getAttributeQName(
          targetElement, 
          attributeQName.getLocalPart(), 
          attributeQName.getNamespaceURI());
      if (attributeQNameOnElement != null) {
        // The attribute is already present on this element. We should use this QName
        // just in case there are multiple prefixes bounded for the same namespace.
        attributeName = attributeQNameOnElement;
      } else {
        NamespaceContext namespaceContext = targetElement.getNamespaceContext();
        prefix = namespaceContext.getPrefixForNamespace(namespace);
        if (prefix != null && !"".equals(prefix)) {
          attributeName = prefix + ":" + attributeName;
        } else if (attributeQName.getPrefix() != null && !attributeQName.getPrefix().isEmpty()
            && namespaceContext.getNamespaceForPrefix(attributeQName.getPrefix()) == null) {
          // Use the given prefix.
          prefix = attributeQName.getPrefix();
          addNamespaceDeclaration = true;
          attributeName = prefix + ":" + attributeName;
        } else {
          prefix = buildFreshPrefix(namespaceContext);
          addNamespaceDeclaration = true;
          attributeName = prefix + ":" + attributeName;
        }
      }
    }
    
    
    if (value == null 
        || ("".equals(value) 
            && removeIfEmpty)) {
      //Remove it.
      if (!addNamespaceDeclaration) {
        ctrl.removeAttribute(attributeName, targetElement);
      } else {
        //The prefix was not declared in the document.??!!
      }
    } else {
      if (addNamespaceDeclaration) {
        //Add a namespace declaration.
        AttrValue nsAttrValue = new AttrValue(namespace);
        ctrl.setAttribute("xmlns:" + prefix, nsAttrValue, targetElement);
      }
      AttrValue attrValue = new AttrValue(normalizedValue, value, true);
      ctrl.setAttribute(attributeName, attrValue, targetElement);
    }
    
    return attributeName;
  }

  /**
   * Identifies in the already existing attributes of an element the one with the
   * given name and namespace. Returns the QName used in the element for that attribute.
   *
   * @param element       The element.
   * @param attrLocalName Attribute local name
   * @param attrNSURI     Attribute namespace URI.
   *
   * @return The QName as it's being used in the element.
   */
  private static String getAttributeQName(AuthorElement element, String attrLocalName,
      String attrNSURI) {
    String match = null;
    int attrsCount = element.getAttributesCount();
    for (int i = 0; i < attrsCount; i++) {
      String attributeName = element.getAttributeAtIndex(i);
      if (attrLocalName.equals(XmlUtil.getLocalName(attributeName))) {
        //Same local name.
        if (Equaler.verifyEquals(attrNSURI, 
            element.getAttributeNamespace(XmlUtil.getProxy(attributeName)))) {
          //Found it...
          match = attributeName;
          break;
        }
      }
    }
    
    return match;
  }

  /**
   * Generates a prefix that is not yet bound to a namespace.
   * 
   * @param namespaceContext Namespace context.
   * 
   * @return A prefix not bound in the given context.
   */
  public static String buildFreshPrefix(NamespaceContext namespaceContext) {
    String prefix = null;
    //The prefix is not bound yet, we have to find a candidate
    int candidate = 1;
    while (namespaceContext.getNamespaceForPrefix("ns" + candidate) != null) {
      candidate ++;
    }
    prefix = "ns" + candidate;
    
    return prefix;
  }
  
  /**
   * Locate a certain resource in the classpath using its file name.
   * @param authorAccess Author access.
   * @param resourceFileName The resource file name.
   * @return The URL of the resource or <code>null</code>.
   */
  public static URL locateResourceInClasspath(AuthorAccess authorAccess, String resourceFileName){
    //Try to detect them in the classpath resources
    URL resourceURL = null;
    URL[] resources = authorAccess.getClassPathResourcesAccess().getClassPathResources();
    if(resources != null) {
      List<URL> proposedResourceURLs = new ArrayList<URL>();
      for (int i = 0; i < resources.length; i++) {
        URL resource = resources[i];
        String resourceStr = resource.toExternalForm();
        //Find the reuse folder
        if(resourceStr.endsWith("/resources/")
            || resourceStr.endsWith("/resources")){
          //Found it.
          try {
            proposedResourceURLs.add(new URL(resource, resourceFileName));
          } catch (MalformedURLException e) {
            //Ignore.
          }
        }
      }
      if(! proposedResourceURLs.isEmpty()){
        //Fallback
        resourceURL = proposedResourceURLs.get(0);
        if(proposedResourceURLs.size() > 1){
          //Find the first one which exists.
          for (int i = 0; i < proposedResourceURLs.size(); i++) {
            URL url = proposedResourceURLs.get(i);
            InputStream is = null;
            try {
              //If we can read from the stream, we can use it.
              is = url.openStream();
              is.read();
              resourceURL = url;
              break;
            } catch (IOException e) {
              //Ignore
            } finally{
              if (is != null){
                try {
                  is.close();
                } catch (IOException e) {
                  //Ignore
                }
              }
            }
          }
        }
      }
    }
    return resourceURL;
  }
}
