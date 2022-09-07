/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2022 Syncro Soft SRL, Romania.  All rights
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.xml.namespace.QName;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.contentcompletion.xml.CIElement;
import ro.sync.contentcompletion.xml.WhatElementsCanGoHereContext;
import ro.sync.ecss.css.CSS;
import ro.sync.ecss.css.Styles;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSchemaManager;
import ro.sync.ecss.extensions.api.ClassPathResourcesAccess;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.UniqueAttributesProcessor;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.NamespaceContext;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResultInsertConstants;
//import ro.sync.ecss.markers.MarkerBase;
import ro.sync.util.editorvars.EditorVariables;

/**
 * Util methods for common Author operations.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public final class CommonsOperationsUtil {
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(CommonsOperationsUtil.class.getName());
  
  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private CommonsOperationsUtil() {
    // Private to avoid instantiations
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
  }
  
  /**
   * Interface used to check the elements that will be converted in other elements 
   * (table cells or list entries)
   */
  public abstract static class ConversionElementHelper {
    
    /**
     * Check if a block node can be converted in other node (cell or list entry).
     * If this method returns false, the block node is treated like an inline node.
     * 
     * @param node The node to check 
     * @param authorAccess The author access
     * @return <code>true</code> if the conversion can not be completed for this node
     * @throws AuthorOperationException 
     */
    public abstract boolean blockContentMustBeConverted(AuthorNode node, AuthorAccess authorAccess) throws AuthorOperationException;

    /**
     * Create the author document fragment to be inserted in a table cell/list item
     * 
     * @param controller The document controller.
     * @param start The start offset.
     * @param end The end offset.
     * @return The fragment. If <code>null</code>, a document fragment from the provided offsets will be created.
     * 
     * @throws BadLocationException When the given offset is not in content.
     * @throws AuthorOperationException When the operation could not be completed.
     */
    public AuthorDocumentFragment createAuthorDocumentFragment(AuthorDocumentController controller, int start, int end) 
        throws AuthorOperationException, BadLocationException {
      return null;
    }
  }
  
  /**
   * Class containing the new fragment and info about it.
   */
  public static class SelectedFragmentInfo {
    /**
     * The current fragment.
     */
    private AuthorDocumentFragment selectedFragment;
    /**
     * Attributes associated with the current fragment.
     */
    private Map<String, String> attributes;
    
    /**
     * Constructor.
     * 
     * @param selectedFragment The current fragment.
     * @param attributes Attributes associated with the current fragment.
     */
    public SelectedFragmentInfo(AuthorDocumentFragment selectedFragment,
        Map<String, String> attributes) {
      super();
      this.selectedFragment = selectedFragment;
      this.attributes = attributes;
    }

    /**
     * @return Returns the selected fragment.
     */
    public AuthorDocumentFragment getSelectedFragment() {
      return selectedFragment;
    }

    /**
     * @param selectedFragment The selected fragment to set.
     */
    public void setSelectedFragment(AuthorDocumentFragment selectedFragment) {
      this.selectedFragment = selectedFragment;
    }

    /**
     * @return Returns the attributes.
     */
    public Map<String, String> getAttributes() {
      return attributes;
    }
    /**
     * @param attributes The attributes to set.
     */
    public void setAttributes(Map<String, String> attributes) {
      this.attributes = attributes;
    }
  }

  /**
   * Get the specified attributes for a specific node.
   * 
   * @param node The node.
   * @param includeID <code>true</code> to also include the id attribute.
   * @return The attributes.
   */
  static Map<String, String> getAttributes(AuthorNode node, boolean includeID) {
    Map<String, String> attributes = null;
    if (node instanceof AuthorElement) {
      AuthorElement element = (AuthorElement) node;
      int attributesCount = element.getAttributesCount();
      for (int i = 0; i < attributesCount; i++) {
        // Check attributes
        String attrName = element.getAttributeAtIndex(i);
        AttrValue attrValue = element.getAttribute(attrName);
        if (attrValue.isSpecified()) {
          if (attributes == null) {
            // Init the map
            attributes = new LinkedHashMap<String, String>();
          }
          if (includeID || (!"id".equals(attrName) && !"xml:id".equals(attrName))) {
            // Add the attribute
            attributes.put(attrName, attrValue.getValue());
          }
        }
      }
    }
    return attributes;
  }
  
  
  /**
   * Serialize attributes with a space before.
   * 
   * @param attributes The attributes to serialize.
   * @param attributesToSkip The names of the attributes to skip.
   * @return The serialization.
   */
  public static String serializeAttributes(Map<String, String> attributes, Collection<String> attributesToSkip) {
    StringBuilder stringBuilder = new StringBuilder();
    for (Map.Entry<String, String> attr : attributes.entrySet()) {
      String attrName = attr.getKey();
      if (!attributesToSkip.contains(attrName)) {
        String attributeValue = attr.getValue();
        stringBuilder.append(" ").append(attrName).append("=\"").append(attributeValue).append("\"");
      }
    }
    return stringBuilder.toString();
  }

  /**
   * Create fragment.
   * 
   * @param controller Author document controller.
   * @param start Start offset
   * @param end End offset
   * @param helper The conversion helper
   * @return The fragment 
   * @throws BadLocationException 
   * @throws AuthorOperationException 
   */
  private static AuthorDocumentFragment createAuthorDocumentFragment(AuthorDocumentController controller,
      int start, int end, ConversionElementHelper helper) throws BadLocationException, AuthorOperationException {
    AuthorDocumentFragment fragment = helper.createAuthorDocumentFragment(controller, start, end);
    if (fragment == null) {
      if (start <= end) {
        fragment = controller.createDocumentFragment(start, end);
      } else {
        // Empty fragment
        fragment = controller.createNewDocumentFragmentInContext("", start);
      }
    }
    
    return fragment;
  }
  
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
      } else if (caretOffset <= nodeEndOffset){
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
    // For the caret editor variable, a processing instruction is initially created,
    // which is later removed and the caret is placed there. Detect the offset 
    // of the processing instruction, where the caret will be moved.
    int piOffset = authorAccess.getEditorAccess().hasSelection() ? Math.min(
        authorAccess.getEditorAccess().getSelectionStart(), authorAccess.getEditorAccess().getSelectionEnd()): 
      authorAccess.getEditorAccess().getCaretOffset();

    if (authorAccess.getEditorAccess().hasSelection()) {
      // We have selection. Do a simple insert.
      surroundWithFragment(authorAccess, xmlFragment, 
          authorAccess.getEditorAccess().getSelectionStart(), 
          authorAccess.getEditorAccess().getSelectionEnd() - 1); 
    } else {
      int insertionOffset = authorAccess.getEditorAccess().getCaretOffset();
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
            piOffset = off.intValue();
          }
        }
      }
    }

    if (moveCaretToSpecifiedPosition) {
      //Detect the position in the Author page where the caret should be placed.
      MoveCaretUtil.moveCaretToImposedEditorVariableOffset(authorAccess, piOffset);
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
      if (attrLocalName.equals(getLocalName(attributeName))) {
        String attrNS = element.getAttributeNamespace(getPrefix(attributeName));
        //Same local name.
        if ((attrNSURI == null && attrNS == null) 
            || (attrNSURI != null && attrNSURI.equals(attrNS))) {
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
    return locateResourceInClasspathFolder(authorAccess, "resources", resourceFileName);
  }
  
  /**
   * Locate a certain resource in the classpath using its file name.
   * @param authorAccess Author access.
   * @param folderName The name of the folder.
   * @param resourceFileName The resource file name.
   * @return The URL of the resource or <code>null</code>.
   */
  public static URL locateResourceInClasspathFolder(AuthorAccess authorAccess, String folderName, String resourceFileName){
    //Try to detect them in the classpath resources
    URL resourceURL = null;
    ClassPathResourcesAccess classPathResourcesAccess = authorAccess.getClassPathResourcesAccess();
    if (classPathResourcesAccess != null) {
      URL[] resources = classPathResourcesAccess.getClassPathResources();
      if(resources != null) {
        List<URL> proposedResourceURLs = new ArrayList<URL>();
        for (int i = 0; i < resources.length; i++) {
          URL resource = resources[i];
          String resourceStr = resource.toExternalForm();
          //Find the reuse folder
          if(resourceStr.endsWith("/" + folderName + "/")
              || resourceStr.endsWith("/" + folderName)){
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
              try (InputStream is = url.openStream()) {
                is.read();
                resourceURL = url;
                break;
              } catch (IOException e) { //NOSONAR java:S1166 Not interested on preserving the original exception.
                //Ignore
              }
            }
          }
        }
      }
    }
    return resourceURL;
  }
  
  /**
   * Resolves a path relative to the framework directory. Editor variables are
   * also accepted and expanded. The path is also passed through the catalog mappings.
   * 
   * @param authorAccess Author access.
   * @param path The path to resolve. Can be a file path, an URL path or a path relative
   * to the framework directory. Editor variables are also accepted. The path is 
   * also passed through the catalog mappings.
   * 
   * @return An URL or null if unable to expand the path to an URL.
   */
  public static URL expandAndResolvePath(AuthorAccess authorAccess, String path) {
    URL url = null;
    String expanded = authorAccess.getUtilAccess().expandEditorVariables(path, null);
    try {
      url = new URL(expanded);
    } catch (MalformedURLException e1) {
      // Not an URL;
      try {
        File file = new File(expanded);
        if (file.exists()) {
          url = authorAccess.getUtilAccess().convertFileToURL(file);
        }
      } catch (MalformedURLException e) {
        // Definitely not an URL.
      } catch (AccessControlException e) {
        // This can happen if the provided path cannot be accessed because access is blocked
        // by the security manager (or the path does not exists)
        logger.debug("Cannot access the path: " + path, e);
      }
      if (url == null) {
        // Maybe it has provided just a path relative to the framework directory.
        try {
          String frameworkDir = authorAccess.getUtilAccess().expandEditorVariables(EditorVariables.FRAMEWORK_DIRECTORY, null);
          File file = new File(frameworkDir, expanded);
          if (file.exists()) {
            // You can create a file over all sort of content that is not actually 
            // a file path so we make sure it exists.
            url = authorAccess.getUtilAccess().convertFileToURL(file);
          }
        } catch (MalformedURLException e) {
          // Not an URL. 
        } catch (AccessControlException e) {
          // This can happen if the provided path cannot be accessed because access is blocked
          // by the security manager (or the path does not exists)
          logger.debug("Cannot access the path: " + path, e);
        }
      }
    }

    if (url != null) {
      URL resolved = authorAccess.getXMLUtilAccess().resolvePathThroughCatalogs(null, url.toString(), true, true);
      if (resolved != null) {
        url = resolved;
      }
    }
    
    return url;
  }
  
  
  /**
   * Get the proxy from an qualified element or attribute name.
   * 
   * @param qName q name
   * @return the proxy or an empty string. Null if the argument is null.
   */
  public static String getPrefix(String qName) {
    String prefix = null;
    if (qName != null) {
      int idx = qName.indexOf(':');
      prefix = "";
      if (idx != -1) {
        prefix = qName.substring(0, idx);
      }
    }
    return prefix;
  }

  /**
   * Get the local name from an qualified element or attribute name.
   * 
   * @param qName q name
   * @return the local name, or null if the argument is null.
   */
  public static String getLocalName(String qName) {
    String local = qName;
    if (qName != null) {
      int idx = qName.lastIndexOf(':');
      if (idx != -1) {
        local = qName.substring(idx + 1);
      }
    }
    return local;
  }

  /**
   * Remove unwanted attributes.
   * 
   * @param skippedAttributes The attributes to be deleted.
   * @param fragment The author document fragment to be cleared.
   * @param controller The author document controller.
   */
  public static void removeUnwantedAttributes(String[] skippedAttributes, AuthorDocumentFragment fragment,
      AuthorDocumentController controller) {
    List fragNodes = fragment.getContentNodes();
    // Remove attributes
    if (fragNodes != null && !fragNodes.isEmpty()) {
      AuthorNode node = (AuthorNode) fragNodes.get(0);
      if (node.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
        AuthorElement clonedElement = (AuthorElement) node;
        Set<String> skippedAttrsSet = new HashSet<String>();
        if(skippedAttributes != null) {
          //Add skipped attributes.
          skippedAttrsSet.addAll(Arrays.asList(skippedAttributes));
        }
        //Also delegate to unique attributes processor.
        UniqueAttributesProcessor attrsProcessor = controller.getUniqueAttributesProcessor();
        if(attrsProcessor != null) {
          int attrsCount = clonedElement.getAttributesCount();
          for (int i = 0; i < attrsCount; i++) { 
            String attrQName = clonedElement.getAttributeAtIndex(i);
            if(! attrsProcessor.copyAttributeOnSplit(attrQName, clonedElement)) {
              skippedAttrsSet.add(attrQName);
            }
          }
        }
        //Remove all attributes which should have been skipped,
        if (! skippedAttrsSet.isEmpty()) {
          Iterator<String> iter = skippedAttrsSet.iterator();
          while(iter.hasNext()) {
            clonedElement.removeAttribute(iter.next());
          }
        }
      }
    }
  }

  /**
   * Remove current selection from Author.
   * 
   * @param authorAccess Author access.
   * 
   * @return A list with start positions for empty elements (after remove is done).
   */
  public static List<Position> removeCurrentSelection(AuthorAccess authorAccess) {
    List<Position> positions = new ArrayList<>();
    // Remove selection
    if (authorAccess.getEditorAccess().hasSelection()) {
      // Get the selection intervals
      List<ContentInterval> selectionIntervals = authorAccess.getEditorAccess().getAuthorSelectionModel().getSelectionIntervals();

      positions = removeIntervals(authorAccess, selectionIntervals);
    }
    return positions;
  }

  /**
   * Remove current selection from Author.
   * 
   * @param authorAccess Author access.
   * @param selectionIntervals The intervals.
   * 
   * @return A list with start positions for empty elements (after remove is done).
   */
  public static List<Position> removeIntervals(AuthorAccess authorAccess,
      List<ContentInterval> selectionIntervals) {
    List<Position> positions = new ArrayList<>();
    if (selectionIntervals != null) {
      if (selectionIntervals.size() > 1) {
        Collections.sort(selectionIntervals, new Comparator<ContentInterval>() {
          /**
           * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
           */
          @Override
          public int compare(ContentInterval interval1, ContentInterval interval2) {
            int result = 0;
            if (interval1 != null && interval2 != null) {
              result = interval1.getStartOffset() > interval2.getStartOffset() ? -1 : 1;
            }
            return result;
          }
        });
      }

      // Remove selection
      try {
        for (ContentInterval selection : selectionIntervals) {
          // Delete current selection
          int[] balancedSelection = authorAccess.getEditorAccess().getBalancedSelection(selection.getStartOffset(), selection.getEndOffset());
          authorAccess.getDocumentController().delete(balancedSelection[0], balancedSelection[1] - 1);
          // Check if there is an empty element left here and keep its start position...
          AuthorNode node = authorAccess.getDocumentController().getNodeAtOffset(balancedSelection[0]);
          if (node != null && 
              node != authorAccess.getDocumentController().getAuthorDocumentNode().getRootElement() && 
              node.getStartOffset() + 1 == node.getEndOffset()) {
            // Empty element, keep position
            positions.add(authorAccess.getDocumentController().createPositionInContent(node.getStartOffset()));
          }
        }
      } catch (Exception e) {
        // Nothing to do
        logger.debug(e.getMessage(), e);
      }
    }
    return positions;
  }

  /**
   * Get selected content fragments to be converted to cell or list entries fragments.
   * 
   * @param authorAccess The author access.
   * @param helper Used to check if the elements from selection can be converted 
   * in other elements (table cells or list entries)
   * @return The selected content fragments to be converted to cell fragments.
   * @throws AuthorOperationException 
   */
  public static List<SelectedFragmentInfo> getSelectedFragmentsForConversions(
      AuthorAccess authorAccess, ConversionElementHelper helper) throws AuthorOperationException {
    List<SelectedFragmentInfo> result = null;
    if (authorAccess.getEditorAccess().hasSelection()) {
      // Get the selection intervals
      List<ContentInterval> selectionIntervals = authorAccess.getEditorAccess().getAuthorSelectionModel().getSelectionIntervals();
      try {
        result = getFragmentsForConversions(authorAccess, helper, selectionIntervals);
      } catch (BadLocationException e) {
        logger.debug(e, e);
      }
    }
    return result;
  }

  /**
   * Get selected content fragments to be converted to cell or list entries fragments.
   * 
   * @param authorAccess The author access.
   * @param helper Used to check if the elements from selection can be converted 
   * in other elements (table cells or list entries)
   * @param intervals The intervals to convert.
   * 
   * @return The selected content fragments to be converted to cell fragments.
   * 
   * @throws BadLocationException 
   * @throws AuthorOperationException 
   */
  public static List<SelectedFragmentInfo> getFragmentsForConversions(AuthorAccess authorAccess,
      ConversionElementHelper helper, List<ContentInterval> intervals)
      throws BadLocationException, AuthorOperationException {
    // This is the list containing the content intervals to be added on each row/list entry 
    List<SelectedFragmentInfo> result = new ArrayList<>();
    for (ContentInterval contentInterval : intervals) {
      // Check each selection interval
      result.addAll(getFragmentsForConversions(authorAccess, helper, contentInterval));
    }
    return result;
  }

  /**
   * Get selected content fragments to be converted to cell or list entry fragments.
   * 
   * @param authorAccess The author access.
   * @param helper Used to check if the elements from selection can be converted 
   * in other elements (table cells or list entries)
   * @param interval The interval to convert.
   * 
   * @return The selected content fragments to be converted to cell fragments.
   * 
   * @throws BadLocationException 
   * @throws AuthorOperationException 
   */
  private static List<SelectedFragmentInfo> getFragmentsForConversions(AuthorAccess authorAccess, ConversionElementHelper helper,
      ContentInterval contentInterval) throws BadLocationException, AuthorOperationException {
    List<SelectedFragmentInfo> result = new ArrayList<>();
    AuthorDocumentController controller = authorAccess.getDocumentController();
    // Create the content segment to iterate
    int start = contentInterval.getStartOffset();
    int maxEndOffset = contentInterval.getEndOffset();
    
    Segment content = new Segment();
    int len = maxEndOffset - contentInterval.getStartOffset();
    controller.getChars(start, len, content);
    
    AuthorNode lastNode = null;
    char ch = content.first();
    int startInterval = contentInterval.getStartOffset();
    int currentOffset = start;
    while(ch != Segment.DONE) {
      boolean intervalEmpty = startInterval == currentOffset;
      if (ch == 0) {
        // Sentinel
        OffsetInformation info = controller.getContentInformationAtOffset(currentOffset);
        AuthorNode node = info.getNodeForMarkerOffset();
        
        Styles styles = authorAccess.getEditorAccess().getStyles(node);
        String display = styles.getDisplay();
        
        // Check if this is an block element (or a list item)
        if ((CSS.BLOCK.equals(display) || CSS.LIST_ITEM.equals(display)) 
            && helper.blockContentMustBeConverted(node, authorAccess)) {
          result.addAll(finishCurrentFragment(startInterval, currentOffset, helper, controller));
          
          // If we are on the start tag of a fully contained list item, mark the list item content as 
          // a fragment to be converted.
          if (CSS.LIST_ITEM.equals(display) 
              && node.getStartOffset() == currentOffset
              && node.getEndOffset() <= contentInterval.getEndOffset()) {
            result.add(getFragmentForNode(node, helper, controller));

            // Skip the entire element. 
            content.setIndex(content.getIndex() + node.getEndOffset() - node.getStartOffset());
            currentOffset = node.getEndOffset();
          } else if (intervalEmpty
              // Do not emit fragment twice for the same empty node (if we let to execute 
              // the code only on marker start, if the selection does not include the start 
              // and includes only the marker end, then no fragment will be emitted for that 
              // element, which is not correct)
              &&  lastNode != node &&
              // Check that this is an empty node
              node.getStartOffset() + 1 == node.getEndOffset()) {
            // Register interval
            lastNode = node;
            result.add(getFragmentForNode(node, helper, controller));
          }
          // Jump over this interval
          startInterval = currentOffset + 1;
        }
      }
      // Jump to next char
      ch = content.next();
      currentOffset ++;
    }
 
    // Maybe there is an interval left?
    if (startInterval < maxEndOffset) {
      AuthorDocumentFragment selectedFragment = createAuthorDocumentFragment(controller, startInterval, maxEndOffset - 1, helper);
      result.add(new SelectedFragmentInfo(selectedFragment, null));
    }
    
    return result;
  }

  /**
   * Return the fragment for the given node to be converted.
   * 
   * @param node The node to be converted.
   * @param helper Used to check if the elements from selection can be converted 
   * in other elements (table cells or list entries)
   * @param controller The document controller.
   * @return The fragment.
   * @throws BadLocationException
   * @throws AuthorOperationException
   */
  private static SelectedFragmentInfo getFragmentForNode(AuthorNode node, ConversionElementHelper helper, 
      AuthorDocumentController controller)
      throws BadLocationException, AuthorOperationException {
    AuthorDocumentFragment selectedFragment = 
        createAuthorDocumentFragment(controller, node.getStartOffset() + 1, node.getEndOffset() - 1, helper);
    Map<String, String> attributes = getAttributes(node, true);
    return new SelectedFragmentInfo(selectedFragment, attributes);
  }

  /**
   * Return the fragment to be conerted for a given interval.
   * 
   * @param startInterval The start of the interval.
   * @param currentOffset The current offset - the end of the interval.
   * @param helper Used to check if the elements from selection can be converted 
   * in other elements (table cells or list entries)
   * @param controller The document controller.
   * @return The fragments - empty list if no fragment has to be converted.
   * 
   * @throws BadLocationException
   * @throws AuthorOperationException
   */
  private static List<SelectedFragmentInfo> finishCurrentFragment(int startInterval, int currentOffset, ConversionElementHelper helper,
      AuthorDocumentController controller)
      throws BadLocationException, AuthorOperationException {
    if (startInterval == currentOffset) {
      return Collections.emptyList();
    } else {
      AuthorNode node = controller.getNodeAtOffset(currentOffset);
      boolean currentNodeFullyIncluded = 
          startInterval <= node.getStartOffset() + 1 && currentOffset >= node.getEndOffset();
        // Register interval
        AuthorDocumentFragment selectedFragment = 
            createAuthorDocumentFragment(controller, startInterval, currentOffset - 1, helper);
        Map<String, String> attributes = getAttributes(node, currentNodeFullyIncluded);
        return Collections.singletonList(new SelectedFragmentInfo(selectedFragment, attributes));
    }
  }
  
  /**
   * Remove empty elements.
   * 
   * @param authorAccess The Author access.
   * @param emptyElementsPositions Positions for empty elements
   */
  public static void removeEmptyElements(AuthorAccess authorAccess, Collection<Position> emptyElementsPositions) {
    if (!emptyElementsPositions.isEmpty()) {
      for (Position position : emptyElementsPositions) {
        try {
          AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(position.getOffset() + 1);
          while (nodeAtOffset != null && 
              nodeAtOffset != authorAccess.getDocumentController().getAuthorDocumentNode().getRootElement()) {
            if (nodeAtOffset.getStartOffset() + 1 == nodeAtOffset.getEndOffset()) {
              AuthorNode parentNode = nodeAtOffset.getParent();
              authorAccess.getDocumentController().deleteNode(nodeAtOffset);
              nodeAtOffset = parentNode;
            } else {
              nodeAtOffset = null;
            }
          }
        } catch (BadLocationException e) {
          // Do nothing
          logger.debug(e.getMessage(), e);
        }
      }
    }
  }
  
  /**
   * Check if an element with the given local name is allowed at the caret offset.
   * 
   * @param elementLocalName        the local name of the element whose allowance we check.
   * @param offset                  the offset where the allowance of the element is checked.
   * @param authorSchemaManager     the Author schema manager.
   * 
   * @return                        <code>true</code> if an element with the given local name is allowed at the caret offset,
   *                                    <code>false</code> otherwise.
   * 
   * @throws BadLocationException  When the offset is below zero or greater than the content.
   */
  public static boolean isAllowedElement(String elementLocalName, int offset, AuthorSchemaManager authorSchemaManager) throws BadLocationException {
    boolean isElementAccepted = false;
    if (authorSchemaManager != null) {
      // We will ask the schema manager if the element is allowed.
      WhatElementsCanGoHereContext context = authorSchemaManager.createWhatElementsCanGoHereContext(offset);
      if (context != null) {
        List<CIElement> whatElementsCanGoHere = authorSchemaManager.whatElementsCanGoHere(context);
        if (whatElementsCanGoHere != null) {
          for (CIElement ciElement : whatElementsCanGoHere) {
            if (elementLocalName.equals(ciElement.getName())) {
              isElementAccepted = true;
              break;
            }
          }
        }
      }
    } else {
      // No schema; everything is accepted.
      isElementAccepted = true;
    }
    return isElementAccepted;
  }
  
}
