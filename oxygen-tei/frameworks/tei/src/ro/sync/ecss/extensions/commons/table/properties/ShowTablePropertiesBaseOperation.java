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
package ro.sync.ecss.extensions.commons.table.properties;

import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.eclipse.swt.widgets.Shell;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.SelectionInterpretationMode;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.table.properties.EditedTablePropertiesInfo.TAB_TYPE;
import ro.sync.exml.workspace.api.Platform;

/**
 * Base class for operations that shows a dialog which allows the user to modify 
 * some properties for a table.
 * 
 * @author adriana_sbircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class ShowTablePropertiesBaseOperation implements AuthorOperation {
  /**
   * The table properties helper.
   */
  protected TablePropertiesHelper tableHelper;
  /**
   * The current author access.
   */
  protected AuthorAccess authorAccess;
  
  /**
   * Constructor.
   * 
   * @param tableHelper The table properties helper.
   */
  public ShowTablePropertiesBaseOperation(TablePropertiesHelper tableHelper) {
    this.tableHelper = tableHelper; 
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    this.authorAccess = authorAccess;
    showTableProperties();
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return null;
  }

  /**
   * Shows the table properties and process all the modifications.
   * 
   * @throws AuthorOperationException When the action cannot be performed.
   */
  public void showTableProperties() throws AuthorOperationException {
    try {
      // Check if there is a selection. The selection can be single selection or multiple selection
      // If no selection, we must use the node at caret position 
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      List<AuthorElement> nodes = new ArrayList<AuthorElement>();
      // Check the selection first
      List<Integer[]> selections = new ArrayList<Integer[]>();
      if (authorAccess.getEditorAccess().hasSelection()) {
        List<ContentInterval> selectionIntervals = 
            authorAccess.getEditorAccess().getAuthorSelectionModel().getSelectionIntervals();
        // Obtain all the selection intervals
        if (selectionIntervals != null && !selectionIntervals.isEmpty()) {
          for (int i = 0; i < selectionIntervals.size(); i++) {
            int startOffset = selectionIntervals.get(i).getStartOffset();
            int endOffset = selectionIntervals.get(i).getEndOffset();
            selections.add(new Integer[] {startOffset, endOffset});
          }
        }
      } else {
        // No selection, use the caret position
        selections.add(new Integer[] {caretOffset, caretOffset});
      }
      
      // Check if only one table is selected
      List<AuthorElement> tableElements = 
          getAllElementsToCollectProperties(selections, TablePropertiesConstants.TYPE_TABLE);
      int tableElementsNumber = 0;
      for (int i = 0; i < tableElements.size() && tableElementsNumber < 2; i++) {
        if (!tableHelper.isNodeOfType(tableElements.get(i), TablePropertiesConstants.TYPE_GROUP)) {
          tableElementsNumber++;
        }
      }
      
      // Cannot modify properties for more than one table 
      if (tableElementsNumber > 1) {
        throw new AuthorOperationException(authorAccess.getAuthorResourceBundle().
            getMessage(ExtensionTags.CANNOT_PERFORM_TABLE_PROPERTIES_OPERATION));
      }
      
      // Show the 'Table properties' dialog
      EditedTablePropertiesInfo tableInfo = null;
      List<TabInfo> categoriesAndAttributes = getCategoriesAndProperties(selections);
      if (!categoriesAndAttributes.isEmpty()) {
        // We have at least one category, show the dialog
        EditedTablePropertiesInfo editedTablePropertiesInfo = new EditedTablePropertiesInfo(categoriesAndAttributes, getSelectedTab(selections));
        Platform platform = authorAccess.getWorkspaceAccess().getPlatform();
        if (Platform.STANDALONE.equals(platform)) {
          SATablePropertiesCustomizerDialog saTablePropertiesCustomizer = new SATablePropertiesCustomizerDialog(
              (Frame) authorAccess.getWorkspaceAccess().getParentFrame(), 
              authorAccess.getAuthorResourceBundle(),
              authorAccess.getWorkspaceAccess());
          saTablePropertiesCustomizer.setLocationRelativeTo(
              (Component) authorAccess.getWorkspaceAccess().getParentFrame());

          // Obtain the modified properties for the current table
          tableInfo = saTablePropertiesCustomizer.getTablePropertiesInformation(
              editedTablePropertiesInfo);
        } else if (Platform.ECLIPSE.equals(platform)) {
          //Eclipse table customization
          ECTablePropertiesCustomizerDialog ecTablePropertiesCustomizer = new ECTablePropertiesCustomizerDialog(
              (Shell) authorAccess.getWorkspaceAccess().getParentFrame(), 
              authorAccess.getAuthorResourceBundle(),
              authorAccess.getWorkspaceAccess());

          // Obtain the modified properties for the current table
          tableInfo = ecTablePropertiesCustomizer.getTablePropertiesInformation(
              editedTablePropertiesInfo);
        }
      } else {
        throw new AuthorOperationException(authorAccess.getAuthorResourceBundle().getMessage(
            ExtensionTags.CANNOT_PERFORM_OPERATION_NO_ELEMENT_TO_EDIT_PROPERTIES_FOR));
      }
      if (tableInfo != null) {
        // First of all, modify the attributes
        // Obtain the nodes whose attributes will be modified and the corresponding attributes  
        List<TabInfo> attributesModifications = getElementsWithModifiedAttributes(tableInfo);
        // For every given node, modify the attributes
        for (int i = 0; i < attributesModifications.size(); i++) {
          TabInfo tabInfo = attributesModifications.get(i);
          List<AuthorElement> authorElements = tabInfo.getNodes();
          List<TableProperty> attrsToModify = tabInfo.getProperties();
          AuthorNode commonAncestor = authorAccess.getDocumentController().getCommonAncestor(authorElements.toArray(new AuthorElement[0]));
          if (commonAncestor != null) {
            int[] offsets = new int[authorElements.size()];
            for (int j = 0; j < authorElements.size(); j++) {
              AuthorElement authorElement = authorElements.get(j);
              offsets[j] = authorElement.getStartOffset() + 1;
            }

            Map<String, AttrValue> attributes = new LinkedHashMap<String, AttrValue>();
            for (int k = 0; k < attrsToModify.size(); k++) {
              TableProperty attr = attrsToModify.get(k);
              attributes.put(attr.getAttributeName(), attr.getCurrentValue() != null ? 
                  new AttrValue(attr.getCurrentValue()) : null);
            }
            authorAccess.getDocumentController().setMultipleAttributes(commonAncestor.getStartOffset() + 1, offsets, attributes);
          }
        }
        
        // Delete the nodes that are changed and insert the new fragments
        List<TabInfo> fragmentsAndOffsetsToInsert = getFragmentsAndOffsetsToInsert(tableInfo, nodes);
        for (int i = 0; i < fragmentsAndOffsetsToInsert.size(); i++) {
          TabInfo tabInfo = fragmentsAndOffsetsToInsert.get(i);
          List<AuthorElement> nodesToDelete = tabInfo.getNodes();
          AuthorNode commonAncestor = authorAccess.getDocumentController().getCommonAncestor(nodesToDelete.toArray(new AuthorElement[0]));
          if (commonAncestor instanceof AuthorElement) {
            for (Iterator iterator = nodesToDelete.iterator(); iterator.hasNext();) {
              AuthorElement authorElement = (AuthorElement) iterator.next();
              if (nodesToDelete.contains(authorElement.getParentElement())) {
                iterator.remove();
              }
            }

            // Sort in document order
            Collections.sort(nodesToDelete, new Comparator<AuthorElement>() {
              @Override
              public int compare(AuthorElement o1, AuthorElement o2) {
                int toRet = 0;
                if (o1.getStartOffset() < o2.getStartOffset()) {
                  toRet = - 1;
                } else {
                  toRet = 1;
                }
                return toRet;
              }
            });

            // Compute the start and end offsets arrays
            int[] startOffsets = new int[nodesToDelete.size()];
            int[] endOffsets = new int[nodesToDelete.size()];
            int n = nodesToDelete.size();
            for (int j = 0; j < n; j++) {
              startOffsets[j] = nodesToDelete.get(j).getStartOffset();
              endOffsets[j] = nodesToDelete.get(j).getEndOffset();
            }

            // Multiple delete
            authorAccess.getDocumentController().multipleDelete((AuthorElement) commonAncestor, startOffsets, endOffsets);
            // Insert the new fragments
            int[] offsets = new int[tabInfo.getFragmentsToInsert().size()];
            for (int j = 0; j < tabInfo.getFragmentsToInsert().size(); j++) {
              offsets[j] = tabInfo.getInsertOffsets()[j].getOffset(); 
            }
            authorAccess.getDocumentController().insertMultipleFragments(
                (AuthorElement)commonAncestor, tabInfo.getFragmentsToInsert().toArray(new AuthorDocumentFragment[0]), offsets);
          }
        }
        // Restore the caret position after structure modifications
        authorAccess.getEditorAccess().setCaretPosition(caretOffset);
      }
    } catch (AuthorOperationException e) {
      // Show error message
      authorAccess.getWorkspaceAccess().showErrorMessage(e.getMessage());
    }
  }

  /**
   * Obtain all the elements with all the modified attributes.
   * 
   * @param tableInfo     The obtained table information from the table properties dialog.
   * 
   * @return A map containing all the elements whose attributes will be modified 
   * and the corresponding attributes.
   */
  protected List<TabInfo> getElementsWithModifiedAttributes(EditedTablePropertiesInfo tableInfo) {
    List<TabInfo> toModify = new ArrayList<TabInfo>();
    List<TableProperty> attrs;
    List<TabInfo> categories = tableInfo.getCategories();
    for (int i = 0; i < categories.size(); i++) {
      attrs = new ArrayList<TableProperty>();
      TabInfo tabInfo = categories.get(i);
      List<TableProperty> props = tabInfo.getProperties();
      for (int j = 0; j < props.size(); j++) {
        // Check if the property is an attribute
        TableProperty prop = props.get(j);
        if (prop.isAttribute()) {
          if (!TablePropertiesConstants.PRESERVE.equals(prop.getCurrentValue())) {
            if (TablePropertiesConstants.ATTR_NOT_SET.equals(prop.getCurrentValue())) {
              // For not set value add null as current value
              prop.setCurrentValue(null);
            }
            
            // add the attribute to the list
            attrs.add(prop);
          }
        }
      }
      
      toModify.add(new TabInfo(tabInfo.getTabKey(), attrs, tabInfo.getNodes()));
    }
    
    return toModify;
  }
  
  /**
   * Collects all the elements that will be used to populate the tabs in 
   * "Table Properties" dialog.
   * 
   * @param selections The currently selected nodes. They can be mixed.
   * @param type       The type of the elements to be collected.
   * 
   * @return A list with all the elements used to populate the tabs in 
   * "Table Properties" dialog.
   */
  protected List<AuthorElement> getAllElementsToCollectProperties(List<Integer[]> selections, int type) {
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
            computeElementsList(elements, (AuthorElement) node, startOffset, endOffset != -1 ? endOffset : startOffset, type, false);
          }
        }
      }
    } catch (BadLocationException e) {
      // Do nothing, elements array will be empty
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
   * @param fullySelected   <code>true</code> if the nodes should be entire contained by the selection. 
   */
  private void computeElementsList(
      List<AuthorElement> elementsList, 
      AuthorElement node, 
      int startOffset, 
      int endOffset, 
      int type, 
      boolean fullySelected) {
    if (tableHelper.isNodeOfType(node, type) && !elementsList.contains(node)) {
        elementsList.add(node);
    } else if (tableHelper.getElementAncestor(node, type) != null) {
      AuthorElement elementAncestor = tableHelper.getElementAncestor(node, type);
      if (!elementsList.contains(elementAncestor)) {
        elementsList.add(elementAncestor);
      }
    } else if (type != TablePropertiesConstants.TYPE_TABLE) {
      // A parent of element with given type
      List<AuthorElement> collectedElements = new ArrayList<AuthorElement>();
      tableHelper.getChildElements(node, type, collectedElements);
      for (int j = 0; j < collectedElements.size(); j++) {
        boolean addElement = false;
        AuthorElement currentElement = collectedElements.get(j);
        if (
            // Node is from caret position, so add all its children
            startOffset == endOffset 
            // or selection is inside the current element or contains it
            // Maybe the selection includes the current node
            || !fullySelected && (startOffset >= currentElement.getStartOffset() && startOffset <= currentElement.getEndOffset() 
            || endOffset > currentElement.getStartOffset() && endOffset <= currentElement.getEndOffset()
            || currentElement.getStartOffset() >= startOffset && currentElement.getStartOffset() < endOffset 
            || currentElement.getEndOffset() >= startOffset && currentElement.getEndOffset() < endOffset)
            || fullySelected 
            && currentElement.getStartOffset() >= startOffset 
            && currentElement.getEndOffset() <= endOffset) {
          addElement = true;
        } 
        if (addElement && !elementsList.contains(currentElement)) {
          elementsList.add(currentElement);
        }
      }
    }
    
    // For cals table tgroup and table elements are considered table elements, 
    // so add both types of node in the list.
    // Check the collected nodes parents and children
    if (type == TablePropertiesConstants.TYPE_TABLE) {
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
   * Check if the selected rows can be moved (row spans don't exceed collected rows range).
   * 
   * @param collectedRows The rows to be checked.
   * @param parentType    The type of the parent element.
   * 
   * @return  <code>true</code> if the rows can be moved.
   */
  protected boolean checkRowSpans(List<AuthorElement> collectedRows, int parentType) {
    boolean toReturn = true;
    // Check every collected row
    rows:
    for (int i = 0; i < collectedRows.size(); i++) {
      List<AuthorElement> children = new ArrayList<AuthorElement>();
      AuthorElement currentRow = collectedRows.get(i);
      // Obtain the cell children
      tableHelper.getChildElements(currentRow, TablePropertiesConstants.TYPE_CELL, children);
      for (int j = 0; j < children.size(); j++) {
        // Get the row spans
        int[] tableRowSpanIndices = authorAccess.getTableAccess().getTableRowSpanIndices(children.get(j));
        if (tableRowSpanIndices != null && tableRowSpanIndices[1] - tableRowSpanIndices[0] > 0) {
          // Current row has row spans
          // Obtain the parent
          AuthorElement tgroup = tableHelper.getElementAncestor(currentRow, parentType);
          if (tgroup != null) {
            List<AuthorElement> rows = new ArrayList<AuthorElement>();
            // Get all row children from the parent
            tableHelper.getChildElements(tgroup, TablePropertiesConstants.TYPE_ROW, rows);
            // Get the rows on which the current row spans  
            List<AuthorElement> subList = rows.subList(tableRowSpanIndices[0], tableRowSpanIndices[1] + 1);
            // Check that all rows from the interval have the same parent
            int elementType = tableHelper.getElementType((AuthorElement) subList.get(0).getParent());
            for (int k = 1; k < subList.size(); k++) {
              if (elementType != tableHelper.getElementType((AuthorElement) subList.get(k).getParent())) {
                // Found two rows with different parent
                toReturn = false;
                break rows;
              }
            }
            // The collected rows does not include all the rows from current span
            if (!collectedRows.containsAll(subList)) {
              toReturn = false;
              break rows;
            }
          }
        }
      }
    }
    
    return toReturn;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return null;
  }

  /**
   * Obtain a map with all the fragments which will be modified and the 
   * corresponding offsets (the offsets where the fragments will be inserted).
   * 
   * @param tableInfo     The obtained table information from the table properties dialog.
   * @param nodes         The selected nodes.
   * 
   * @return a list tab info objects which contains all the fragments which will be modified and the 
   * corresponding offsets (the offsets where the fragments will be inserted).
   */
  protected List<TabInfo> getFragmentsAndOffsetsToInsert(
      EditedTablePropertiesInfo tableInfo, List<AuthorElement> nodes) throws AuthorOperationException {
    List<TabInfo> categories = tableInfo.getCategories();
    List<TabInfo> modifications = new ArrayList<TabInfo>();
    for (int i = 0; i < categories.size(); i++) {
      List<AuthorDocumentFragment> fragments = new ArrayList<AuthorDocumentFragment>();
      List<Position> offsets = new ArrayList<Position>();
      // Obtain the current tab info
      TabInfo tabInfo = categories.get(i);
      TabInfo modification = null;
      List<AuthorElement> nodesToModify = new ArrayList<AuthorElement>();
      List<TableProperty> props = tabInfo.getProperties();
      for (int j = 0; j < props.size(); j++) {
        TableProperty prop = props.get(j);
        // Attributes were already set
        if (!prop.isAttribute()) {
          modification = new TabInfo(tabInfo.getTabKey(), tabInfo.getProperties(), null);
          String currentValue = prop.getCurrentValue();
          // Check row type
          if (TablePropertiesConstants.ROW_TYPE_PROPERTY.equals(prop.getAttributeName())) {
            for (int k = 0; k < tabInfo.getNodes().size(); k++) {
              boolean deleteParent = false;
              AuthorElement currentNode = tabInfo.getNodes().get(k);
              if (TablePropertiesConstants.ROW_TYPE_BODY.equals(currentValue)) {
                // Move inside body
                deleteParent = computeFragmentsToMoveInsideBody(fragments, offsets, tabInfo, nodesToModify,
                    currentNode); 
              } else if (TablePropertiesConstants.ROW_TYPE_HEADER.equals(currentValue)) {
                // Mode inside header
                deleteParent = computeFragmentMoveInsideHeader(fragments, offsets, tabInfo, nodesToModify,
                    currentNode);
              } else {
                // Move inside footer
                deleteParent = computeFragmentsToMoveInsideFooter(fragments, offsets, tabInfo, nodesToModify,
                   currentNode);
              }
              // The parent does not contain any nodes, so delete it
              if (deleteParent && !nodesToModify.contains(currentNode.getParentElement())) {
                nodesToModify.add(0, (AuthorElement) currentNode.getParentElement());
              }
            }
            
            // Set to the modification tab info, the computed nodes to be deleted, fragments to insert and positions
            modification.setNodes(nodesToModify);
            modification.setFragmentsToInsert(fragments);
            modification.setInsertOffsets(offsets.toArray(new Position[0]));
          }
          // If we found some modifications for the current tab info, add them to the modifications.
          modifications.add(modification);
        }
      }
    }
    
    return modifications;
  }
  
  /**
   * Obtain the information for table tab. This information will contain the properties 
   * which will be edited, the table elements on which those properties applies and some context 
   * information.
   * 
   * @param selections          The list with the selection intervals.
   * 
   * @return The tab info object or <code>null</code> is there are no properties to edit for table.   
   */
  protected TabInfo getTableInformation(List<Integer[]> selections) {
    TabInfo tabInfo = null;
    // Determine the table element
    List<AuthorElement> tableElements = getAllElementsToCollectProperties(selections, TablePropertiesConstants.TYPE_TABLE);
    if (!tableElements.isEmpty()) {
      // Found a table element, obtain the properties
      List<TableProperty> tableProperties = new ArrayList<TableProperty>();
      ArrayList<AuthorElement> nodes = new ArrayList<AuthorElement>();
      Map<TableProperty, String> commonValues = new HashMap<TableProperty, String>();
      nodes.addAll(tableElements);
      //Attributes
      List<TableProperty> attributes = getTableAttribute();

      for (int i = 0; i < tableElements.size(); i++) {
        AuthorElement currentElement = tableElements.get(i);
        for (int j = 0; j < attributes.size(); j++) {
          // Initialize the common value for every attribute	
          if (i == 0) {
            commonValues.put(attributes.get(j), TablePropertiesConstants.NOT_COMPUTED);
          }
          String commonValue = getCommonValue(currentElement, attributes.get(j).getAttributeName(), commonValues.get(attributes.get(j)));
          commonValues.put(attributes.get(j), commonValue);
        }
      }

      for (int i = 0; i < attributes.size(); i++) {
        TableProperty currentAttribute = attributes.get(i);
        // Compute the values for the given attribute
        TableProperty tb = getAttrProperty(
            tableElements, 
            commonValues.get(currentAttribute), 
            currentAttribute);
        if (tb != null) {
          tableProperties.add(tb);
        }
      }
      
      // Create the tab info
      tabInfo = new TabInfo(ExtensionTags.TABLE, tableProperties, nodes);
    }

    return tabInfo;
  }


  /**
   * Obtain the table property object for the given attribute.
   * 
   * @param collectedElements       The list of all rows which will be edited.
   * @param detectedAttributeValue  Current value of the attribute. It is the value set on the element(s).
   * @param currentAttribute        The current attribute.
   * 
   * @return a {@link TableProperty} object for the given attribute.
   */
  protected TableProperty getAttrProperty(
      List<AuthorElement> collectedElements, 
      String detectedAttributeValue, 
      TableProperty currentAttribute) {
    TableProperty tableProperty = null;
    ArrayList<String> attrValues = new ArrayList<String>();
    Map<String, String> icons = new HashMap<String, String>();

    if (!collectedElements.isEmpty()) {
      if (currentAttribute.getValues() != null) {
        attrValues.addAll(currentAttribute.getValues());
      }

      for (int i = 0; i < attrValues.size(); i++) {
        String value = attrValues.get(i);
        // Compute the icon for the current value
        if (value.equalsIgnoreCase(TablePropertiesConstants.LEFT)) {
          // Horizontal align left
          icons.put(value, TablePropertiesConstants.ICON_ALIGN_LEFT);
        } else if (value.equalsIgnoreCase(TablePropertiesConstants.RIGHT)) {
          // Horizontal align right
          icons.put(value, TablePropertiesConstants.ICON_ALIGN_RIGHT);
        } else if (value.equalsIgnoreCase(TablePropertiesConstants.CENTER)) {
          // Horizontal align center
          icons.put(value, TablePropertiesConstants.ICON_ALIGN_CENTER);
        } else if (value.equalsIgnoreCase(TablePropertiesConstants.JUSTIFY)) {
          // Horizontal align justify
          icons.put(value, TablePropertiesConstants.ICON_ALIGN_JUSTIFY);
        } else if (value.equalsIgnoreCase(TablePropertiesConstants.TOP)) {
          // Vertical align top
          icons.put(value, TablePropertiesConstants.ICON_VALIGN_TOP);
        } else if (value.equalsIgnoreCase(TablePropertiesConstants.MIDDLE)) {
          // Vertical align middle
          icons.put(value, TablePropertiesConstants.ICON_VALIGN_MIDDLE);
        } else if (value.equalsIgnoreCase(TablePropertiesConstants.BOTTOM)) {
          // Vertical align bottom
          icons.put(value, TablePropertiesConstants.ICON_VALIGN_BOTTOM);
        } else {
          // Empty icon for the rest of the values
          icons.put(value, TablePropertiesConstants.EMPTY_ICON);
        }
      }
      // Add also the icons computed earlier
      if (currentAttribute.getIcons() != null) { 
        icons.putAll(currentAttribute.getIcons());
      }

      String currentValue = null;
      if (collectedElements.size() > 1) {
        if (detectedAttributeValue == null || 
            TablePropertiesConstants.NOT_COMPUTED.equals(detectedAttributeValue)) {
          // The attr is not set on any of the selected elements, 
          // so add the empty property and select it.
          currentValue = TablePropertiesConstants.ATTR_NOT_SET;
        } else if (!attrValues.contains(detectedAttributeValue)) {
          currentValue = TablePropertiesConstants.PRESERVE;
          attrValues.add(TablePropertiesConstants.PRESERVE);
          icons.put(TablePropertiesConstants.PRESERVE, TablePropertiesConstants.EMPTY_ICON);
        } else {
          currentValue = detectedAttributeValue;
        }
      } else if (collectedElements.size() == 1) {
        currentValue = TablePropertiesConstants.ATTR_NOT_SET;
        // Find the current value
        AttrValue attribute = collectedElements.get(0).getAttribute(currentAttribute.getAttributeName());

        if (attribute != null) {
          currentValue = attribute.getValue();
          if (!attrValues.contains(currentValue)) {
            currentValue = TablePropertiesConstants.PRESERVE;
            attrValues.add(TablePropertiesConstants.PRESERVE);
            // Empty icon for preserve value
            icons.put(TablePropertiesConstants.PRESERVE, TablePropertiesConstants.EMPTY_ICON);
          }
        }
      }

      // Add also empty value, maybe the user wants to remove the attribute
      attrValues.add(TablePropertiesConstants.ATTR_NOT_SET);
      icons.put(TablePropertiesConstants.ATTR_NOT_SET, TablePropertiesConstants.EMPTY_ICON);
      tableProperty = new TableProperty(currentAttribute.getAttributeName(), currentAttribute.getAttributeRenderString(), attrValues, currentValue, true);
      tableProperty.setGuiType(currentAttribute.getGuiType());
      tableProperty.setParentGroup(currentAttribute.getParentGroup());
      tableProperty.setIcons(icons);
    }
    
    return tableProperty;
  }
  
  /**
   * Obtain the common value for the given attribute set on the given element.
   * 
   * @param currentElem   The element to check for given attribute. 
   * @param attrQname     The attribute qualified name.
   * @param currentValue  The currently computed common value for the given attribute.
   * 
   * @return The common value between the given value and the attribute values set on the given element. 
   */
  protected String getCommonValue(AuthorElement currentElem, String attrQname, String currentValue) {
    String computedValue = currentValue;
    boolean addPreserve = !tableHelper.isTable(currentElem);
    boolean isTableElement = tableHelper.isTable(currentElem) 
            && !tableHelper.isTableGroup(currentElem);
    // Check if the given attribute can be set on the given element
    boolean notAllowed = TablePropertiesConstants.ALIGN.equals(attrQname) && isTableElement;
    if (!notAllowed && !isTableElement) {
      // Get the attribute
      AttrValue attribute = currentElem.getAttribute(attrQname);
      if (attribute != null) {
        // Obtain the current value
        String currentVal = attribute.getValue();
        // Compute the common value
        if (TablePropertiesConstants.NOT_COMPUTED.equals(computedValue)) {
          computedValue = currentVal;
        } else if (computedValue == null && addPreserve 
            || computedValue != null && !computedValue.equals(currentVal) 
            && !computedValue.equals(TablePropertiesConstants.ATTR_NOT_SET) 
            && !computedValue.equals(TablePropertiesConstants.PRESERVE)
            && addPreserve) {
          // Preserve
          computedValue = TablePropertiesConstants.PRESERVE;
        }
      } else if (!TablePropertiesConstants.NOT_COMPUTED.equals(computedValue) 
          && computedValue != null 
          && !computedValue.equals(TablePropertiesConstants.PRESERVE) 
          && addPreserve) {
        // preserve
        computedValue = TablePropertiesConstants.PRESERVE;
      } else if (TablePropertiesConstants.NOT_COMPUTED.equals(computedValue)){
        computedValue = null;
      }
    } else if (isTableElement) {
      // There is only one table element (and not tgroup), so obtain the value for the attribute
      // and it will represent the common value
      AttrValue attribute = currentElem.getAttribute(attrQname);
      if (attribute != null) {
        // Obtain the current value
        String currentVal = attribute.getValue();
        computedValue = currentVal;
      }
    }
    
    return computedValue;
  }
  
  /**
   * Obtain the tab that will be selected in the "Table Properties" dialog.
   * 
   * @param selections The currently selected nodes or the node at caret position.
   * 
   * @return the tab that will be selected in the "Table Properties" dialog.
   */
  protected TAB_TYPE getSelectedTab(List<Integer[]> selections) {
    // Start with table tab if no other appropiate tab is found
    TAB_TYPE tab = TAB_TYPE.TABLE_TAB;
    try {
      // First check the selection interpretation mode
      AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
      SelectionInterpretationMode selectionInterpretationMode = editorAccess.getAuthorSelectionModel().getSelectionInterpretationMode();
      if (selectionInterpretationMode != null) {
        if (selectionInterpretationMode == SelectionInterpretationMode.TABLE_COLUMN) {
          tab = TAB_TYPE.COLUMN_TAB;
        } else if (selectionInterpretationMode == SelectionInterpretationMode.TABLE_ROW) {
          tab = TAB_TYPE.ROW_TAB;
        } else {
          // Already we consider that the table tab is selected and there is no need to check
          // again the table selection
        }
      } else {
        // nodes to select 
        if (selections.size() == 1 && selections.get(0)[0].intValue() == selections.get(0)[1].intValue()) {
          // Caret offset, do not iterate through the children, get the node at offset
          AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(selections.get(0)[0]);
          if (nodeAtOffset instanceof AuthorElement) {
            if (tableHelper.isTableCell((AuthorElement) nodeAtOffset)) {
              // The caret is inside a cell
              tab = TAB_TYPE.CELL_TAB;
            } else if (tableHelper.isTableRow((AuthorElement) nodeAtOffset)) {
              // The caret is inside a row
              tab = TAB_TYPE.ROW_TAB;
            }
          }
        } else {
          // Selection
          selections:
            for (int i = 0; i < selections.size(); i++) {
              Integer[] currentSel = selections.get(i);
              // Balance the selection and obtain the fully selected node
              AuthorNode fullySelectedNode = editorAccess.getFullySelectedNode(
                  editorAccess.getBalancedSelection(currentSel[0], currentSel[1])[0], 
                  editorAccess.getBalancedSelection(currentSel[0], currentSel[1])[1]);
              if (fullySelectedNode instanceof AuthorElement) {
                // We have a fully selected node
                tab = getTabType((AuthorElement) fullySelectedNode);
                if (tab == TAB_TYPE.CELL_TAB) {
                  // Cell is the context
                  break;
                }
              } else {
                // Balance selection and obtain the nodes that are selected (not fully selected)
                List<AuthorNode> nodesToSelect = authorAccess.getDocumentController().
                    getNodesToSelect(editorAccess.getBalancedSelection(currentSel[0], currentSel[1])[0], 
                        editorAccess.getBalancedSelection(currentSel[0], currentSel[1])[1]);
                for (int j = 0; j < nodesToSelect.size(); j++) {
                  // Check every selected node, until a context is found
                  AuthorNode authorNode = nodesToSelect.get(j);
                  if (authorNode instanceof AuthorElement) {
                    AuthorElement authorElement = (AuthorElement) authorNode;
                    tab = getTabType(authorElement);
                    if (tab ==  TAB_TYPE.CELL_TAB) {
                      break selections;
                    } else {
                      List<AuthorElement> rowchildren = new ArrayList<AuthorElement>();
                      if (tab != TAB_TYPE.ROW_TAB) {
                        // Check all rows
                        List<AuthorElement> selectedRowChildren = new ArrayList<AuthorElement>();
                        AuthorElement tableElement = tableHelper.getElementAncestor(authorElement, TablePropertiesConstants.TYPE_TABLE);
                        tableHelper.getChildElements(tableElement, TablePropertiesConstants.TYPE_ROW, rowchildren);
                        // Obtain the entirely selected children
                        computeElementsList(
                            selectedRowChildren, 
                            tableElement, 
                            editorAccess.getBalancedSelection(currentSel[0], currentSel[1])[0], 
                            editorAccess.getBalancedSelection(currentSel[0], currentSel[1])[1], 
                            TablePropertiesConstants.TYPE_ROW,
                            true);
                        // The table is not entirely selected
                        if (rowchildren.size() > selectedRowChildren.size()) {
                          tab = TAB_TYPE.ROW_TAB;
                          rowchildren = new ArrayList<AuthorElement>();
                          // Compute the selected rows
                          computeElementsList(
                              rowchildren, 
                              tableElement, 
                              currentSel[0], 
                              currentSel[1], 
                              TablePropertiesConstants.TYPE_ROW,
                              false);
                        } 
                      }

                      // Check if all the cell children are included in selection
                      // Obtain all the children 
                      if (!rowchildren.isEmpty()) {
                        for (int k = 0; k < rowchildren.size(); k++) {
                          AuthorElement selectedRow = rowchildren.get(k);
                          tab = checkForCellTab(selectedRow, currentSel[0], currentSel[1], tab);
                          if (tab == TAB_TYPE.CELL_TAB) {
                            break selections;
                          }
                        }
                      } else {
                        // Check all the children cells for the current selected element
                        // which is not necessary a row element.
                        tab = checkForCellTab(authorElement, currentSel[0], currentSel[1], tab);
                        if (tab == TAB_TYPE.CELL_TAB) {
                          break selections;
                        }
                      }
                    }
                  }
                }
              }
            }
        }
      }
    } catch (Throwable e) {
      // Nothing to do
    }
    return tab;
  }
  
  /**
   * Checks if the cells tab should be selected. Starts from the given node and 
   * computes all its entirely selected cell children. If there are no isolated 
   * cells, the tab type is the given one.
   * 
   * @param element       The element whose cell chindren will be checked.
   * @param selStart      The selection start.
   * @param selEnd        The selection end.
   * @param currentTab    The current type of the selected tab/
   * 
   * @return The newly computed tab type.
   */
  private TAB_TYPE checkForCellTab(AuthorElement element, int selStart, int selEnd, TAB_TYPE currentTab) {
    List<AuthorElement> children = new ArrayList<AuthorElement>();
    tableHelper.getChildElements(element, TablePropertiesConstants.TYPE_CELL, children);
    // Obtain the selected children
    List<AuthorElement> selectedCellChildren = new ArrayList<AuthorElement>();
    computeElementsList(selectedCellChildren , element, selStart, selEnd, TablePropertiesConstants.TYPE_CELL, true);
    if (!selectedCellChildren.isEmpty() && children.size() > selectedCellChildren.size()) {
      // Isolated cells
      currentTab = TAB_TYPE.CELL_TAB;
    }
    
    return currentTab;
  }
  
  /**
   * Obtain the tab for the given element. This method check the element type and
   * according to it decides what tab should be selected.
   * 
   * @param element A table element.
   *  
   * @return The type for the tab which should be selected.
   */
  private TAB_TYPE getTabType(AuthorElement element) {
    TAB_TYPE tab = TAB_TYPE.TABLE_TAB;
    if (tableHelper.isNodeOfType(element, TablePropertiesConstants.TYPE_CELL)) {
      // Found an isolated cell, so the tab should be "Cell(s)" tab
      tab = TAB_TYPE.CELL_TAB;
    } if (tableHelper.isNodeOfType(element, TablePropertiesConstants.TYPE_ROW)) {
      // Entire table
      tab = TAB_TYPE.ROW_TAB;
    } else {
      // The tab is already set to table
    }
    
    return tab;
  }
  
  /**
   * Obtain the categories from the table properties dialog. The categories maps 
   * the tab name to the list of properties that will be modified in the corresponding 
   * tab panel. Every property will be modified using a combobox/radios which will contain the 
   * possible values for that property. The label string for the combobox/radios group will be
   * the provided render string of the property or the property name, if a render 
   * string is not provided.
   * 
   * @param selections  The currently selected nodes or the node at caret position.
   * 
   * @return A list of tab info objects containing the tab names and the corresponding properties list.
   */
  protected abstract List<TabInfo> getCategoriesAndProperties(List<Integer[]> selections);
  
  /**
   * Obtain the table attributes.
   * 
   * @return A list with {@link TableProperty} objects containing the table attributes
   * qualified name, render string and possible values.
   */
  protected abstract List<TableProperty> getTableAttribute();
  
  /**
   * Computes the fragment and position, inside footer element, for the given node.
   * 
   * @param fragments     A list with already computed fragments. The new fragment 
   *                      will be added to this list.
   * @param offsets       A list with positions where the given fragments will be inserted.
   * @param tabInfo       The current edited tab info.
   * @param nodesToModify A list containing all the nodes that will be deleted.
   * @param currentNode   The node to be checked if it should be moved.
   * 
   * @return <code>true</code> if the parent of the given node parent should be also deleted.
   * 
   * @throws AuthorOperationException If the new parent fragment could not be inserted.
   */
  protected abstract boolean computeFragmentsToMoveInsideFooter(
      List<AuthorDocumentFragment> fragments,
      List<Position> offsets, 
      TabInfo tabInfo, 
      List<AuthorElement> nodesToModify,
      AuthorElement currentNode) throws AuthorOperationException;
  /**
   * Computes the fragment and position, inside header element, for the given node.
   * 
   * @param fragments     A list with already computed fragments. The new fragment 
   *                      will be added to this list.
   * @param offsets       A list with positions where the given fragments will be inserted.
   * @param tabInfo       The current edited tab info.
   * @param nodesToModify A list containing all the nodes that will be deleted.
   * @param currentNode   The node to be checked if it should be moved.
   * 
   * @return <code>true</code> if the parent of the given node parent should be also deleted.
   * 
   * @throws AuthorOperationException If the new parent fragment could not be inserted.
   */
  protected abstract boolean computeFragmentMoveInsideHeader(
      List<AuthorDocumentFragment> fragments,
      List<Position> offsets, 
      TabInfo tabInfo, 
      List<AuthorElement> nodesToModify,
      AuthorElement currentNode) throws AuthorOperationException;
  /**
   * Computes the fragment and position, inside body element, for the given node.
   * 
   * @param fragments     A list with already computed fragments. The new fragment 
   *                      will be added to this list.
   * @param offsets       A list with positions where the given fragments will be inserted.
   * @param tabInfo       The current edited tab info.
   * @param nodesToModify A list containing all the nodes that will be deleted.
   * @param currentNode   The node to be checked if it should be moved.
   * 
   * @return <code>true</code> if the parent of the given node parent should be also deleted.
   * 
   * @throws AuthorOperationException If the new parent fragment could not be inserted.
   */
  protected abstract boolean computeFragmentsToMoveInsideBody(
      List<AuthorDocumentFragment> fragments,
      List<Position> offsets, 
      TabInfo tabInfo, 
      List<AuthorElement> nodesToModify,
      AuthorElement currentNode) throws AuthorOperationException;
}
