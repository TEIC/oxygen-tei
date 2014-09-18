/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2012 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.table.operations;

import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Utility class for table operations.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class TableOperationsUtil {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(TableOperationsUtil.class.getName());

  /**
   * Create a cell fragment for a specific offset, having the name of the cell and 
   * a source fragment from which the attributes and content must be copied.
   * 
   * @param authorAccess                The author access.
   * @param fragments                   The list of all content fragments.
   * @param cellsFragment               <code>true</code> if the fragments represents cells.
   * @param cellElementName             The cell name.
   * @param currentFragmentIndex        The index of the fragment that must be used for attributes and content.
   * @param namespace                   The cell namespace.
   * @param tableHelper                 Author table helper.
   * @param imposedAttributesFragments  Imposed attributes for the created cell.
   *                                    Each fragment has the following form: "attribute_name=\"attribute_value\""
   *
   * @return The cell fragment.
   *
   * @throws AuthorOperationException 
   */
  public static String createCellXMLFragment(
      AuthorAccess authorAccess,
      AuthorDocumentFragment[] fragments, 
      boolean cellsFragment, 
      String cellElementName, 
      int currentFragmentIndex, 
      String namespace, 
      AuthorTableHelper tableHelper,
      String... imposedAttributesFragments) throws AuthorOperationException {

    // Get all attributes/vales pairs from the corrsponding fragment
    StringBuilder additionalAttributes = new StringBuilder();

    // Get the cell content
    StringBuilder cellContent = new StringBuilder();
    if (fragments.length > currentFragmentIndex) {
  
      AuthorDocumentFragment currentFragmentContent = fragments[currentFragmentIndex];
  
      if (cellsFragment) {
        // Determine the attributes of the cell fragment
        List<AuthorNode> contentNodes = currentFragmentContent.getContentNodes();
        if (contentNodes.size() == 1) {
          AuthorNode cellNode = contentNodes.get(0);
          if (cellNode instanceof AuthorElement) {
            AuthorElement element = (AuthorElement)cellNode;
  
            // Get attributes
            int attributesCount = element.getAttributesCount();
            // Get all attributes from the fragment
            for (int j = 0; j < attributesCount; j++) {
              String attrName = element.getAttributeAtIndex(j);
              if (attrName != null
                  && !attrName.startsWith("xmlns")
                  && !isIgnoredAttribute(attrName, tableHelper)) {
                AttrValue attrValue = element.getAttribute(attrName);

                // Copy only the attributes declared in document
                if (attrValue.isSpecified()) {
                  String rawValue = attrValue.getRawValue();
                  // Populate additional attributes string
                  additionalAttributes.append(" " + attrName + "=\"" + rawValue + "\"");
                }
              }
            }
          }
        }
      }
  
      // Determine the content
      String contentFromFragment = getContentFromFragment(authorAccess, cellsFragment, currentFragmentContent);
      if (contentFromFragment != null) {
        cellContent.append(contentFromFragment);
      }
    }
    
    // Construct the cell fragment
    StringBuilder cellXMLFragment = new StringBuilder("<");
    cellXMLFragment.append(cellElementName);
    if (namespace != null) {
      cellXMLFragment.append(" xmlns=\"" + namespace + "\"");
    }
    if (imposedAttributesFragments != null) {
      for (int i = 0; i < imposedAttributesFragments.length; i++) {
        cellXMLFragment.append(" ").append(imposedAttributesFragments[i]);
      }
    }
    cellXMLFragment.append(additionalAttributes.toString());
    String content = cellContent.toString();
    if (content.length() == 0) {
      // Close tag
      cellXMLFragment.append("/>");
    } else {
      // Close start tag
      cellXMLFragment.append(">");
      // Add content
      cellXMLFragment.append(content);
      // Add end tag
      cellXMLFragment.append("</").append(cellElementName).append(">");
    }
    return cellXMLFragment.toString();
  }

  /**
   * Check if the attribute should be ignored.
   * 
   * @param attrName The attribute name.
   * @param tableHelper Author table helper
   * 
   * @return <code>true</code> if the attribute should be ignored.
   */
  public static boolean isIgnoredAttribute(String attrName, AuthorTableHelper tableHelper) {
    boolean shouldBeIgnored = false;
    
    // Check if the given attribute name is an ignored column attribute name
    String[] ignoredAttributes = tableHelper.getIgnoredColumnAttributes();
    if (ignoredAttributes != null) {
      for (int i = 0; i < ignoredAttributes.length; i++) {
        if (attrName.equals(ignoredAttributes[i])) {
          shouldBeIgnored = true;
          break;
        }
      }
    }
    if (!shouldBeIgnored) {
      // Check if the given attribute name is an ignored row attribute name
      ignoredAttributes = tableHelper.getIgnoredRowAttributes();
      if (ignoredAttributes != null) {
        for (int i = 0; i < ignoredAttributes.length; i++) {
          if (attrName.equals(ignoredAttributes[i])) {
            shouldBeIgnored = true;
            break;
          }
        }
      }
    }
    
    return shouldBeIgnored;
  }

  /**
   * Get the given fragment content. If the cellsFragment parameter is <code>true</code>, 
   * the returned content represent the content of the cell, otherwise the fragment itself.
   * 
   * @param authorAccess The author access.
   * @param cellsFragment <code>true</code> if the fragment represent a cell fragment
   * @param fragment The Author fragment.
   * @return The fragment content.
   */
  public static String getContentFromFragment(AuthorAccess authorAccess, boolean cellsFragment,
      AuthorDocumentFragment fragment) {
    String contentFromFragment = null;
    try {
      if (cellsFragment) {
        // Get content
        AuthorDocumentFragment content = authorAccess.getDocumentController().unwrapDocumentFragment( 
            fragment);
        if (content != null) {
          contentFromFragment = authorAccess.getDocumentController().serializeFragmentToXML(content);
        }
      } else {
        // Determine content
        contentFromFragment = authorAccess.getDocumentController().serializeFragmentToXML(fragment);
      }
    } catch (BadLocationException e) {
      logger.error(e, e);
    }
    return contentFromFragment;
  }
  
  /**
   * Check if the node has the given namespace and name
   * 
   * @param node The node to check.
   * @param name The name to compare the node name with.
   * @param namespace The namespace to compare the node namespace with.
   * @return <code>true</code> if the node has the given namespace and name.
   */
  public static boolean nodeHasProperties(AuthorNode node, String name, String namespace) {
    boolean match = false;
    // Check namespace
    if (namespace == null || "".equals(namespace)) {
      match = node.getNamespace() == null || "".equals(node.getNamespace());
    } else {
      match = namespace.equals(node.getNamespace());
    }
    
    // Check node name
    if (match) {
      String nodeName = node.getName();
      int separatorOffset = nodeName.indexOf(':');
      if (separatorOffset > 0) {
        nodeName = nodeName.substring(separatorOffset);
      }
      match = nodeName.equals(name);
    }
    return match;
  }
  
  /**
   * Returns the element representing the table that contains the given offset and 
   * has the given properties (name, class attribute). Used for DITA and DITA Maps
   * table operations.
   *  
   * @param offset            The offset to search the parent table element for.
   * @param access            Access to Author operations. 
   * @param tableClassValues  Possible table class attributes values.
   *
   * @return The table element that contains the given offset.
   */
  public static AuthorElement getTableElementContainingOffset(
      int offset, AuthorAccess access, String... tableClassValues) {
    AuthorElement tableNode = null;
    try {
      if (tableClassValues != null && tableClassValues.length > 0) {
        AuthorNode currentNode = access.getDocumentController().getNodeAtOffset(offset);
        AuthorElement rootElement = access.getDocumentController().getAuthorDocumentNode().getRootElement();
        if (currentNode != null && currentNode.getStartOffset() > rootElement.getStartOffset()
            && currentNode.getEndOffset() < rootElement.getEndOffset()) {
          loop: while (currentNode != rootElement) {
            if (currentNode instanceof AuthorElement) {
              AuthorElement currentElement = (AuthorElement) currentNode;
              AttrValue classAttribute = currentElement.getAttribute("class");
              if (classAttribute != null) {
                String classValue = classAttribute.getRawValue();
                if (classValue != null) {
                  // Check if the class value is table class value
                  for (String tableClassValue : tableClassValues) {
                    if (classValue.contains(tableClassValue)) {
                      // Found the closest table node
                      tableNode = currentElement;
                      break loop;
                    }
                  }
                }
              }
            }
            // Maybe the parent is a table element
            currentNode = currentNode.getParent();
          }
        }
      }
    } catch (BadLocationException e) {
      logger.warn(e, e);
    }
    return tableNode;
  }
  
  /**
   * Returns the element representing the table that contains the given offset and 
   * has the given properties (name, namespace).
   *  
   * @param offset            The offset to search the parent table element for.
   * @param namespace         The table node namespace.
   * @param access            Access to Author operations. 
   * @param tableElementNames Possible table element names.
   *
   * @return The table element that contains the given offset.
   */
  public static AuthorElement getTableElementContainingOffset(int offset, String namespace, 
      AuthorAccess access, String... tableElementNames) {
    AuthorElement tableElement = null;
    try {
      if (tableElementNames != null && tableElementNames.length > 0) {
        AuthorNode currentNode = access.getDocumentController().getNodeAtOffset(offset);
        AuthorElement rootElement = access.getDocumentController().getAuthorDocumentNode().getRootElement();
        if (currentNode != null && currentNode.getStartOffset() > rootElement.getStartOffset()
            && currentNode.getEndOffset() < rootElement.getEndOffset()) {
          loop: while (currentNode != rootElement) {
            if (currentNode instanceof AuthorElement) {
              for (String tableElementName : tableElementNames) {
                if (nodeHasProperties(currentNode, tableElementName, namespace)) {
                  tableElement = (AuthorElement) currentNode;
                  // Found the closest table node
                  break loop;
                }
              }
            }
            currentNode = currentNode.getParent();
          }
        }
      }
    } catch (BadLocationException e) {
      logger.warn(e, e);
    }
    return tableElement;
  }
}