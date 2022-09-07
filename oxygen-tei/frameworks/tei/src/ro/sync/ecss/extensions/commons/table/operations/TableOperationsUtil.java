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
package ro.sync.ecss.extensions.commons.table.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.contentcompletion.xml.CIAttribute;
import ro.sync.contentcompletion.xml.CIElement;
import ro.sync.contentcompletion.xml.WhatElementsCanGoHereContext;
import ro.sync.ecss.css.CSS;
import ro.sync.ecss.css.Styles;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSchemaManager;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.commons.table.properties.TableHelper;
import ro.sync.ecss.extensions.commons.table.properties.TableHelperConstants;
import ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper;
import ro.sync.ecss.extensions.commons.table.support.CALSColSpec;
import ro.sync.ecss.extensions.commons.table.support.CALSTableCellInfoProvider;

/**
 * Utility class for table operations.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public final class TableOperationsUtil {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(TableOperationsUtil.class.getName());

  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private TableOperationsUtil() {
    // Private to avoid instantiations
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
  }
  
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
   * @param authorTableHelper                 Author table helper.
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
      AuthorTableHelper authorTableHelper,
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
                  && !isIgnoredAttribute(attrName, authorTableHelper)) {
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
  
  /**
   * Check if a choice table can be inserted in the current context.
   * 
   * @param authorAccess The author access.
   * 
   * @return <code>true</code> if a choice table can be inserted in the given context.
   */
  public static boolean isChoiceTableAllowed(AuthorAccess authorAccess) {
    // Find out if a choice table can be inserted.
    boolean canInsertChoiceTable = false;

    try {
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();

      AuthorSchemaManager authorSchemaManager =
          authorAccess.getDocumentController().getAuthorSchemaManager();

      // Create context
      WhatElementsCanGoHereContext context =
          authorSchemaManager.createWhatElementsCanGoHereContext(caretOffset);

      if (context != null) {
        List<CIElement> childrenElements = authorSchemaManager.whatElementsCanGoHere(context);
        if (childrenElements != null) {
          elemsIter : for (int i = 0; i < childrenElements.size(); i++) {
            // Iterate through possible elements and obtain the attributes.
            CIElement currentElement = childrenElements.get(i);
            List<CIAttribute> attributes = currentElement.getAttributesWithDefaultValues();
            if (attributes != null) {
              for (int j = 0; j < attributes.size(); j++) {
                // Iterate through current element attributes.
                CIAttribute currentAttribute = attributes.get(j);
                String attrDefaultValue = currentAttribute.getDefaultValue();
                // Check if the class attribute is a choicetable.
                if ("class".equals(currentAttribute.getName())
                    && attrDefaultValue != null
                    && attrDefaultValue.contains(" task/choicetable ")) {
                  canInsertChoiceTable = true;
                  break elemsIter;
                }
              }
            }
          }
        }
      }

      if (!canInsertChoiceTable) {
        // Find out if we are in a <step> or <cmd> element and force a choicetable insertion after <cmd>.
        AuthorNode currentNode = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
        if (currentNode instanceof AuthorElement) {
          AuthorElement currentElement = (AuthorElement) currentNode;
          AttrValue classAttribute = currentElement.getAttribute("class");
          if (classAttribute != null && classAttribute.getValue() != null &&
              (classAttribute.getValue().contains(" task/step ") || 
                  classAttribute.getValue().contains("task/cmd"))) {
              // We are in a <step> element or a <cmd> element. Force choicetable 
              //insertion at the end of this element
              canInsertChoiceTable = true;
          }
        }
      }
    } catch (BadLocationException e) {
      logger.warn(e, e);
    }
    
    return canInsertChoiceTable;
  }
  
  /**
   * Check if a table other than choicetable is allowed here.
   * 
   * @param authorAccess The author access.
   * 
   * @return <code>true</code> if a choice table can be inserted in the given context.
   */
  public static boolean areOtherTablesThanChoicetableAllowed(AuthorAccess authorAccess) {
    boolean toReturn = false;
    
    try {
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      
      AuthorSchemaManager authorSchemaManager =
          authorAccess.getDocumentController().getAuthorSchemaManager();
      
      // Create context
      WhatElementsCanGoHereContext context =
          authorSchemaManager.createWhatElementsCanGoHereContext(caretOffset);
      
      if (context != null) {
        List<CIElement> childrenElements = authorSchemaManager.whatElementsCanGoHere(context);
        if (childrenElements != null) {
          elemsIter : for (int i = 0; i < childrenElements.size(); i++) {
            // Iterate through possible elements and obtain the attributes.
            CIElement currentElement = childrenElements.get(i);
            List<CIAttribute> attributes = currentElement.getAttributesWithDefaultValues();
            if (attributes != null) {
              for (int j = 0; j < attributes.size(); j++) {
                // Iterate through current element attributes.
                CIAttribute currentAttribute = attributes.get(j);
                String attrDefaultValue = currentAttribute.getDefaultValue();
                // Check the class attribute
                if ("class".equals(currentAttribute.getName())
                    && attrDefaultValue != null
                    && (attrDefaultValue.contains(" topic/table ")
                        || attrDefaultValue.contains(" topic/simpletable ")  
                           && !attrDefaultValue.contains(" task/choicetable "))) {
                  toReturn = true;
                  break elemsIter;
                }
              }
            }
          }
        }
      }
    } catch (BadLocationException e) {
      logger.warn(e, e);
    }
    
    return toReturn;
  }
  
  /**
   * Check if a properties table is allowed as a global element.
   * 
   * @param authorAccess The author access.
   * 
   * @return <code>true</code> if the "properties" table element is a global element of the schema.
   */
  public static boolean isPropertiesTableGlobalElement(AuthorAccess authorAccess) {
    boolean toReturn = false;
    AuthorSchemaManager authorSchemaManager = authorAccess.getDocumentController().getAuthorSchemaManager();
    List<CIElement> globalElements = authorSchemaManager.getGlobalElements();
    if (globalElements != null) {
      elemsIter : for (int i = 0; i < globalElements.size(); i++) {
        // Iterate through possible elements and obtain the attributes.
        CIElement currentElement = globalElements.get(i);
        List<CIAttribute> attributes = currentElement.getAttributesWithDefaultValues();
        if (attributes != null) {
          for (int j = 0; j < attributes.size(); j++) {
            // Iterate through current element attributes.
            CIAttribute currentAttribute = attributes.get(j);
            String attrDefaultValue = currentAttribute.getDefaultValue();
            // Check the class attribute
            if ("class".equals(currentAttribute.getName())
                && attrDefaultValue != null
                && attrDefaultValue.contains(" reference/properties ")) {
              toReturn = true;
              break elemsIter;
            }
          }
        }
      }
    }

    return toReturn;
  }
  
  /**
   * Collects all the table elements having the given type, determined by the selection intervals.
   * 
   * @param authorAccess The author access 
   * @param type       The type of the elements to be collected.
   * Can be one of TYPE_ prefixed constants from {@link TableHelperConstants}.
   * @param tableHelper Utility class to determine information about table nodes. 
   * @param tableElement The table parent elements.
   * 
   * @return A list with all the elements used to populate the tabs in 
   * "Table Properties" dialog.
   */
  public static List<AuthorElement> getTableElementsOfTypeFromSelection(AuthorAccess authorAccess,
      int type, TableHelper tableHelper, AuthorElement tableElement) {
    // Determine the rows that intersect the selection
    List<ContentInterval> selectionIntervals = authorAccess.getEditorAccess().getAuthorSelectionModel().getSelectionIntervals();
    // Check the selection first
    List<Integer[]> selections = new ArrayList<Integer[]>();
    // Obtain all the selection intervals
    if (selectionIntervals != null && !selectionIntervals.isEmpty()) {
      for (int i = 0; i < selectionIntervals.size(); i++) {
        int startOffset = selectionIntervals.get(i).getStartOffset();
        int endOffset = selectionIntervals.get(i).getEndOffset();
        // Check that start selection offset is inside the table 
        if ((tableElement.getStartOffset() <= startOffset && startOffset <= tableElement.getEndOffset()) ||
            // Check that end selection offset is inside the table
            (tableElement.getStartOffset() <= endOffset && endOffset <= tableElement.getEndOffset())) {
          selections.add(new Integer[] {startOffset, endOffset});
        }
      }
    }

    return  getTableElementsOfType(authorAccess, selections, type, tableHelper);
  }
  
  /**
   * Collects all the table elements having the given type, determined by the selection intervals.
   * 
   * @param authorAccess The author access 
   * @param selections The currently selected nodes. They can be mixed.
   * @param type       The type of the elements to be collected.
   * Can be one of TYPE_ prefixed constants from {@link TableHelperConstants}.
   * @param tableHelper Utility class to determine information about table nodes. 
   * 
   * @return A list with all the elements used to populate the tabs in 
   * "Table Properties" dialog.
   */
  public static List<AuthorElement> getTableElementsOfType(AuthorAccess authorAccess,
      List<Integer[]> selections, int type, TableHelper tableHelper) {
    List<AuthorElement> elements = new ArrayList<AuthorElement>();
    try {
      // For every selection interval, obtain the elements whose properties will be modified.
      for (int i = 0; i < selections.size(); i++) {
        Integer[] sel = selections.get(i);
        int startOffset = sel[0];
        int endOffset = sel[1];
        List<AuthorNode> nodesToSelect = null;
        if (startOffset != endOffset) {
          nodesToSelect = authorAccess.getDocumentController().getNodesToSelect(startOffset, endOffset);
        } else {
          // Actually there is not selection is just the caret position
          nodesToSelect = new ArrayList<AuthorNode>();
          nodesToSelect.add(authorAccess.getDocumentController().getNodeAtOffset(startOffset));
        }
        for (int j = 0; j < nodesToSelect.size(); j++) {
          AuthorNode node = nodesToSelect.get(j);
          if (node instanceof AuthorElement) {
            computeElementsList(elements, (AuthorElement) node, startOffset, 
                endOffset != -1 ? endOffset : startOffset, type, false, tableHelper);
          }
        }
      }
    } catch (BadLocationException e) {
      // Do nothing, elements array will be empty
      logger.debug(e.getMessage(), e);
    }

    return elements;
  }
  
  /**
   * Computes all the nodes of the given type starting from the given node, which are
   * in the given selection.
   * 
   * @param elementsList    The list which will contain the elements.
   * @param node            The starting node.
   * @param startOffset     Selection start.
   * @param endOffset       Selection end.
   * @param type            The elements type.
   * Can be one of TYPE_ prefixed constants from {@link TableHelperConstants}.
   * @param fullySelected   <code>true</code> if the nodes should be entire contained by the selection. 
   * @param tableHelper     Utility class to determine information about table nodes. 
   */
  public static void computeElementsList(
      List<AuthorElement> elementsList, 
      AuthorElement node, 
      int startOffset, 
      int endOffset, 
      int type, 
      boolean fullySelected, 
      TableHelper tableHelper) {
    if (tableHelper.isNodeOfType(node, type) && !elementsList.contains(node)) {
        elementsList.add(node);
    } else if (getElementAncestor(node, type, tableHelper) != null) {
      AuthorElement elementAncestor = getElementAncestor(node, type, tableHelper);
      if (!elementsList.contains(elementAncestor)) {
        elementsList.add(elementAncestor);
      }
    } else if (type != TableHelperConstants.TYPE_TABLE) {
      // A parent of element with given type
      List<AuthorElement> collectedElements = new ArrayList<AuthorElement>();
      getChildElements(node, type, collectedElements, tableHelper);
      for (int j = 0; j < collectedElements.size(); j++) {
        AuthorElement currentElement = collectedElements.get(j);
        int currentElementStartOffset = currentElement.getStartOffset();
        int currentElementEndOffset = currentElement.getEndOffset();
        boolean selectionContainsCurrentElement =
            (startOffset >= currentElementStartOffset && startOffset <= currentElementEndOffset)
             || (endOffset > currentElementStartOffset && endOffset <= currentElementEndOffset)
             || (currentElementStartOffset >= startOffset && currentElementStartOffset < endOffset)
             || (currentElementEndOffset >= startOffset && currentElementEndOffset < endOffset);
        boolean addElement = 
            // Node is from caret position, so add all its children
            startOffset == endOffset 
            // or selection is inside the current element or contains it
            // Maybe the selection includes the current node
            || (!fullySelected && selectionContainsCurrentElement)
            || (fullySelected && currentElementStartOffset >= startOffset && currentElementEndOffset <= endOffset); 
        if (addElement && !elementsList.contains(currentElement)) {
          elementsList.add(currentElement);
        }
      }
    }
    
    // For cals table tgroup and table elements are considered table elements, 
    // so add both types of node in the list.
    // Check the collected nodes parents and children
    if (type == TableHelperConstants.TYPE_TABLE) {
      for (int i = 0; i < elementsList.size(); i++) {
        // Check the parent 
        AuthorElement authorElement = elementsList.get(i);
        AuthorNode parent = authorElement.getParent();
        if (parent instanceof AuthorElement && tableHelper.isTable((AuthorElement) parent)) {
          if (!elementsList.contains(parent)) {
            elementsList.add((AuthorElement) parent);
          }
        }
        
        // Check the children
        List<AuthorNode> children = node.getContentNodes();
        for (int j = 0; j < children.size(); j++) {
          AuthorNode child = children.get(j);
          if (child instanceof AuthorElement  && tableHelper.isTable((AuthorElement) child)
              && tableHelper.isTableGroup((AuthorElement) child)) {
            if (!elementsList.contains(child)) {
              elementsList.add((AuthorElement) child);
            }
          }
        }
      }
    }
  }
  
  /**
   * Search for an ancestor {@link AuthorNode} with the specified type. 
   * 
   * @param node The starting node.
   * @param type The type of the ancestor.
   * @param tableHelper  Utility class to determine information about table nodes. 
   * @return     The ancestor node of the given <code>node</code> or the <code>node</code> 
   * itself if the type matches.
   */
  public static AuthorElement getElementAncestor(AuthorNode node, int type, TableHelper tableHelper) {
    AuthorElement parentCell = null;
    while (node instanceof AuthorElement) {
      // If the current node has the same type as the given type
      // Return that node
      if (tableHelper.isNodeOfType((AuthorElement) node, type)) {
        parentCell = (AuthorElement) node;
        break;
      }
      node = node.getParent();
    }
    
    return parentCell;
  }
  
  /**
   *    * Obtain a list of children with the given type.
   * 
   * @param node The parent node.
   * @param type The child elements type. 
   * Can be one of TYPE_ prefixed constants from {@link TableHelperConstants}. 
   * @param children The list with collected children. Empty when the function is called.
   * @param tableHelper  Utility class to determine information about table nodes. 
   */
  public static void getChildElements(AuthorElement node, int type, List<AuthorElement> children, TableHelper tableHelper) {
    if (tableHelper.isNodeOfType(node, type)) {
      children.add(node);
    } else {
      // Check the children
      List<AuthorNode> contentNodes = node.getContentNodes();
      for (int i = 0; i < contentNodes.size(); i++) {
        AuthorNode authorNode = contentNodes.get(i);
        if (authorNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
          getChildElements((AuthorElement) authorNode, type, children, tableHelper);
        }
      }
    }
  }
  
  /**
   * Obtain the indexes for selected cells.
   * 
   * @param cells           The selected cells.
   * @param authorAccess    The author access.
   * @param tableHelper     Utility class to determine information about table nodes.
   * @param isCals          <code>true</code> if it is a CALS table
   * 
   * @return A map between the table element and a set of the cell's column indexes. 
   */
  public static Map<AuthorElement, Set<Integer>> getCellIndexes(
      List<AuthorElement> cells, 
      AuthorAccess authorAccess, 
      TableHelper tableHelper, 
      boolean isCals) {
    Map<AuthorElement, Set<Integer>> indexes = new HashMap<AuthorElement, Set<Integer>>();
    for (int i = 0; i < cells.size(); i++) {
      // For every computed cell, obtain the parent tgroup
      AuthorElement tableElement = getElementAncestor(cells.get(i), 
          isCals ? TablePropertiesHelper.TYPE_GROUP : TablePropertiesHelper.TYPE_TABLE, tableHelper);
      Set<Integer> set = indexes.get(tableElement);
      if (set == null) {
        set = new HashSet<Integer>();
      }
      
      if (isCals) {
        // Obtain the column span indices for a cell
        int[] tableColSpanIndices = authorAccess.getTableAccess().getTableColSpanIndices(cells.get(i));
        for (int j = 0; tableColSpanIndices != null && j < tableColSpanIndices.length; j++) {
          // Add all indices
          set.add(tableColSpanIndices[j] + 1);
        }
      } else {
        // Add the index
        set.add(authorAccess.getTableAccess().getTableCellIndex(cells.get(i))[1]);
      }

      indexes.put(tableElement, set);
    }

    return indexes;
  }
  
  /**
   * Create a {@link TableHelper} starting from an {@link AuthorTableHelper}. 
   *  
   * @param authorTableHelper The Author table helper
   * @return The {@link TableHelper}
   */
  public static TableHelper createTableHelper(final AuthorTableHelper authorTableHelper) {
    return new TableHelper() {

      @Override
      public boolean isTableGroup(AuthorElement node) {
        return false;
      }

      @Override
      public boolean isTable(AuthorElement node) {
        return authorTableHelper.isTable(node);
      }

      @Override
      public boolean isNodeOfType(AuthorElement node, int type) {
        boolean toReturn = false;
        // Check the given type
        switch (type) {
          case TableHelperConstants.TYPE_ROW:
            toReturn = authorTableHelper.isTableRow(node);
            break;
          case TableHelperConstants.TYPE_TABLE:
            toReturn = authorTableHelper.isTable(node);
            break;
          case TableHelperConstants.TYPE_CELL: 
            toReturn = authorTableHelper.isTableCell(node);
            break;
        }

        return toReturn;
      }
    };
  }
  
  /**
   * Place the caret in the first cell of a table that was just inserted (a result of this operation
   * is send as parameter)
   *  
   * @param authorAccess Author access.
   * @param tableInfo Table information.
   * @param controller Controller.
   * @param result Insert operation result.
   */
  public static void placeCaretInFirstCell(AuthorAccess authorAccess, TableInfo tableInfo,
      AuthorDocumentController controller, SchemaAwareHandlerResult result) {
    if (result != null && tableInfo.getTitle() == null) {
      Integer offs = (Integer) result.getResult(SchemaAwareHandlerResult.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET);
      if (offs != null) {
        try {
          AuthorNode tableNode = controller.getNodeAtOffset(offs + 1);
          if (tableNode instanceof AuthorElement) {
            // Find the first cell to place the caret in.
            AuthorElement firstCell = getFirstCell((AuthorElement) tableNode, authorAccess.getEditorAccess());

            if (firstCell != null) {
              // Place the caret in the first cell
              authorAccess.getEditorAccess().setCaretPosition(firstCell.getStartOffset() + 1);
            }
          }
        } catch (BadLocationException e) {
          // Nothing to do.
          logger.debug(e.getMessage(), e);
        }
      }
    }
  }
  /**
   * Get the first cell element encountered, starting from the given parent element.
   * 
   * @param parentElement The parent element.
   * @param authorEditorAccess Author editor access.
   * @return The first cell element
   */
  private static AuthorElement getFirstCell(AuthorElement parentElement, AuthorEditorAccess authorEditorAccess) {
    AuthorElement toRet = null;
    // Check the children
    List<AuthorNode> contentNodes = parentElement.getContentNodes();
    if (contentNodes != null) {
      for (int i = 0; i < contentNodes.size(); i++) {
        AuthorNode child = contentNodes.get(i);
        // Check only elements
        if (child.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
          AuthorElement childElement = (AuthorElement) child;
          // Check if this is a cell
          Styles styles = authorEditorAccess.getStyles(childElement);
          if (CSS.TABLE_CELL.equals(styles.getDisplay())) {
            // Found a cell
            toRet = childElement;
          } else {
            // Check the children
            AuthorElement firstCell = getFirstCell(childElement, authorEditorAccess);
            if (firstCell != null) {
              // Found a cell
              toRet = firstCell;
            }
          }
          if (toRet != null) {
            break;
          } 
        }
      }
    }
    
    return toRet;
  }

  
  /**
   * Remove invalid column names from CALS table cells. Remove references to column names which are not defined in the table.
   * @param authorAccess Author Access
   * @param tableElement The table element
   * @param cells        The list of cells.
   */
  public static void removeInvalidColNamesFromCALSTableCells(AuthorAccess authorAccess, AuthorElement tableElement, List<AuthorElement> cells) {
    if(cells != null && ! cells.isEmpty()){
      CALSTableCellInfoProvider cellInfoProvider = new CALSTableCellInfoProvider(false);
      cellInfoProvider.init(tableElement);
      // Determine the column specification 
      Set<CALSColSpec> colSpecs = cellInfoProvider.getColSpecs();
      for (int i = 0; i < cells.size(); i++) {
        AuthorElement cell = cells.get(i);
        String[] attributesToCheck = new String[]{"colname", "namest", "nameend"};
        Set<String> definedColumnNamed = new HashSet<String>();
        Iterator<CALSColSpec> iter = colSpecs.iterator();
        while(iter.hasNext()){
          definedColumnNamed.add(iter.next().getColumnName());
        }

        for (int j = 0; j < attributesToCheck.length; j++) {
          AttrValue colnameValue = cell.getAttribute(attributesToCheck[j]);
          if(colnameValue != null){
            //Is it properly mapped to a table column name?
            boolean foundColname = definedColumnNamed.contains(colnameValue.getValue());
            if(! foundColname){
              //Remove the attribute
              authorAccess.getDocumentController().removeAttribute(attributesToCheck[j], cell);
            }
          }
        }
      }
    }
  }
  
  /**
   * Propagate the change of a column name in the entire table.
   * @param authorAccess Author access
   * @param helper       Table helper.
   * @param currentElement Current element on which the attribute which should be changed.
   * @param attributeName  Name of changed attribute
   * @param newValue       The new attribute value
   * @return <code>true</code> if this method handled the change.
   */
  public static boolean handleColumnSpecAttributeChange(AuthorAccess authorAccess, AuthorTableHelper helper, AuthorElement currentElement,
      String attributeName, AttrValue newValue) {
    boolean handled = false;
    AttrValue oldValue = currentElement.getAttribute(attributeName);
    if(oldValue != null && oldValue.isSpecified()){
      String currentValue = oldValue.getValue();
      List<String> interestingAttrs = new ArrayList<String>();
      
      //Interesting attribute names.
      interestingAttrs.add("colname");
      interestingAttrs.add("namest");
      interestingAttrs.add("nameend");
      if(interestingAttrs.contains(attributeName)){
        //Interesting attribute...we need to make the changes in all places where the old value is referenced.
        AuthorNode node = currentElement;
        //Find the table element...
        AuthorElement tableElement = null;
        while(node != null) {
          if (authorAccess.getEditorAccess().getStyles(node).isTable()) {
            if(node.getType() == AuthorNode.NODE_TYPE_ELEMENT){
              tableElement = (AuthorElement) node;
            }
            break;
          } else {
            node = node.getParent();
          }
        }
        Set<String> existingColNames = new HashSet<String>();
        if(tableElement != null){
          List<AuthorElement> toChange = new ArrayList<AuthorElement>();
          List<AuthorNode> nodes = tableElement.getContentNodes();
          if(nodes != null && ! nodes.isEmpty()){
            for (int i = 0; i < nodes.size(); i++) {
              AuthorNode childNode = nodes.get(i);
              if(childNode.getType() == AuthorNode.NODE_TYPE_ELEMENT){
                AuthorElement childElement = (AuthorElement) childNode;
                if(helper.isColspec(childElement)){
                  //Column specification
                  toChange.add(childElement);
                  AttrValue attribute = childElement.getAttribute("colname");
                  if(attribute != null && attribute.isSpecified()){
                    existingColNames.add(attribute.getValue());
                  }
                } else if(helper.isTableRow(childElement)){
                  iterateCells(helper, toChange, childElement);
                } else {
                  Styles styles = authorAccess.getEditorAccess().getStyles(childElement);
                  if(styles.isTableHeaderGroup() || styles.isTableRowGroup()){
                    //Find the row elements...
                    List<AuthorNode> rowNodes = childElement.getContentNodes();
                    if(rowNodes != null){
                      for (int j = 0; j < rowNodes.size(); j++) {
                        AuthorNode rowNode = rowNodes.get(j);
                        if(helper.isTableRow(rowNode) && rowNode.getType() == AuthorNode.NODE_TYPE_ELEMENT){
                          //Row
                          iterateCells(helper, toChange, ((AuthorElement)rowNode));
                        }
                      }
                    }
                  }
                }
              } 
            }
          }
          
          if(!toChange.isEmpty() && toChange.contains(currentElement) 
              //If user is renaming from one column name to another, abstain from doing anything...
              && ! existingColNames.contains(newValue.getValue())){
            handled = true;
            try{
              authorAccess.getDocumentController().beginCompoundEdit();
              for (int i = 0; i < toChange.size(); i++) {
                AuthorElement elem = toChange.get(i);
                for (int j = 0; j < interestingAttrs.size(); j++) {
                  String attr = interestingAttrs.get(j);
                  AttrValue currentAttr = elem.getAttribute(attr);
                  if(currentAttr != null && currentAttr.isSpecified() && currentValue.equals(currentAttr.getValue())){
                    //We need to update this...
                    authorAccess.getDocumentController().setAttribute(attr, newValue, elem);
                  }
                }
              }
            } finally {
              authorAccess.getDocumentController().endCompoundEdit();  
            }
          }
        }
      }
    }
    return handled;
  }

  /**
   * Find all cells and add them to the list.
   * 
   * @param helper The helper.
   * @param toChange List where to add all cells.
   * @param rowElement The current row.
   */
  private static void iterateCells(AuthorTableHelper helper, List<AuthorElement> toChange,
      AuthorElement rowElement) {
    //Table row, add cells.
    List<AuthorNode> cellNodes = rowElement.getContentNodes();
    if(cellNodes != null){
      for (int j = 0; j < cellNodes.size(); j++) {
        AuthorNode cellNode = cellNodes.get(j);
        if(helper.isTableCell(cellNode) && cellNode.getType() == AuthorNode.NODE_TYPE_ELEMENT){
          //Cell
          toChange.add((AuthorElement) cellNode);
        }
      }
    }
  }
}