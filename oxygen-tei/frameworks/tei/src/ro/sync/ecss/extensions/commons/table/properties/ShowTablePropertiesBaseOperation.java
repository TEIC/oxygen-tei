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
package ro.sync.ecss.extensions.commons.table.properties;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Position;

import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import ro.sync.ecss.extensions.commons.table.operations.TableOperationsUtil;
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
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ShowTablePropertiesBaseOperation.class.getName());

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
      throws AuthorOperationException {
    this.authorAccess = authorAccess;
    try {
      showTableProperties(args);
    } catch (AuthorOperationException e) {
      // Show error message
      authorAccess.getWorkspaceAccess().showErrorMessage(e.getMessage(), e);
    }
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
   * @param args the arguments the operation was invoked with.
   * 
   * @throws AuthorOperationException When the action cannot be performed.
   */
  public void showTableProperties(ArgumentsMap args) throws AuthorOperationException {
    // Keep the old position of the caret to restore it
    int oldCaretOffset = authorAccess.getEditorAccess().getCaretOffset(); //NOSONAR java:S1941 
    
    List<Integer[]> selections = getSelections();
    List<TabInfo> categoriesAndAttributes = getCategoriesAndProperties(selections);
    
    if (categoriesAndAttributes.isEmpty()) {
      throw new AuthorOperationException(authorAccess.getAuthorResourceBundle().getMessage(
          ExtensionTags.CANNOT_PERFORM_OPERATION_NO_ELEMENT_TO_EDIT_PROPERTIES_FOR));
    }
    
    EditedTablePropertiesInfo tableInfo = null;
    EditedTablePropertiesInfo editedTablePropertiesInfo = new EditedTablePropertiesInfo(categoriesAndAttributes, getSelectedTab(selections));
    
    if(args.getArgumentValue("tableInfo") != null) {
      tableInfo = getTableInfoFromDescriptor((Map) args.getArgumentValue("tableInfo"), categoriesAndAttributes);
    } else {
      // Show the 'Table properties' dialog
      tableInfo = showDialog(editedTablePropertiesInfo);
    }
    if (tableInfo != null) {
      applyChanges(tableInfo);
      
      // Restore the caret position after structure modifications
      authorAccess.getEditorAccess().setCaretPosition(oldCaretOffset);
    }
  }
  
  /**
   * Get the edited table properties info based on a descriptor.
   * 
   * @param tableInforDescriptor the table info descriptor.
   * @param categoriesAndAttributes the table properties categories (tabs) and attributes information.
   * 
   * @return the edited table properties info
   */
  private static EditedTablePropertiesInfo getTableInfoFromDescriptor(Map<String, List> tableInforDescriptor, List<TabInfo> categoriesAndAttributes) {
    List<TabInfo> modifications = new ArrayList<TabInfo>();
    EditedTablePropertiesInfo tableInfo = null;
    
    for (TabInfo tabInfo : categoriesAndAttributes) {
      List<TableProperty> modifiedProperties = new ArrayList<>();
      
      List<Map<String, Object>> attributes = tableInforDescriptor.get(tabInfo.getTabKey());
      if(attributes == null) {
        continue;
      }
      for(Map<String, Object> attribute : attributes) {
        String attributeName = (String)attribute.get("attributeName");
        boolean isAttribute = (Boolean)attribute.get("attribute");
        String value = (String)attribute.get("currentValue");
        modifiedProperties.add(
            new TableProperty(attributeName, null, null, value, isAttribute));
      }
      if( ! modifiedProperties.isEmpty()) {
        List<AuthorElement> nodes = tabInfo.getNodes();
        
        modifications.add(new TabInfo(tabInfo.getTabKey(), modifiedProperties, nodes));
      }
    }

    // There is at least one property modified, so create the table information
    if (!modifications.isEmpty()) {
      tableInfo = new EditedTablePropertiesInfo(modifications);
    }
    
    return tableInfo;
  }

  /**
   * Apply the user changes.
   * 
   * @param caretOffset the caret offset.
   * @param tableInfo the table changes information.
   * 
   * @throws AuthorOperationException
   */
  private void applyChanges(EditedTablePropertiesInfo tableInfo)
      throws AuthorOperationException {

    // First of all, modify the attributes
    // Obtain the nodes whose attributes will be modified and the corresponding attributes  
    applyAttributesChanges(tableInfo);
    
    List<TabInfo> fragmentsAndOffsetsToInsert = getFragmentsAndOffsetsToInsert(tableInfo);
    
    for (int i = 0; i < fragmentsAndOffsetsToInsert.size(); i++) {
      TabInfo tabInfo = fragmentsAndOffsetsToInsert.get(i);
      
      applyTabChanges(tabInfo);
    }
  }
  
  /**
   * Apply the changes determined by a dialog tab.
   * 
   * @param tabInfo the dialog tab info.
   */
  private void applyTabChanges(TabInfo tabInfo) {
    List<AuthorElement> nodesToDelete = tabInfo.getNodes();
    AuthorNode commonAncestor = authorAccess.getDocumentController().getStrictCommonAncestor(nodesToDelete.toArray(new AuthorElement[0]));
    
    if (! (commonAncestor instanceof AuthorElement)) {
      return;
    }
    
    for (Iterator iterator = nodesToDelete.iterator(); iterator.hasNext();) {
      AuthorElement authorElement = (AuthorElement) iterator.next();
      if (nodesToDelete.contains(authorElement.getParentElement())) {
        iterator.remove();
      }
    }

    sortInDocumentOrder(nodesToDelete);

    // Compute the start and end offsets arrays
    int[] startOffsets = new int[nodesToDelete.size()];
    int[] endOffsets = new int[nodesToDelete.size()];
    
    for (int j = 0; j < nodesToDelete.size(); j++) {
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

  /**
   * Sort the nodes list based on the document order.
   * 
   * @param nodesToDelete the list to sort.
   */
  private static void sortInDocumentOrder(List<AuthorElement> nodesToDelete) {
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
  }

  /**
   * Apply the attributes changes.
   * 
   * @param tableInfo the changes table info.
   */
  private void applyAttributesChanges(EditedTablePropertiesInfo tableInfo) {
    List<TabInfo> attributesModifications = getElementsWithModifiedAttributes(tableInfo);
    // For every given node, modify the attributes
    for (int i = 0; i < attributesModifications.size(); i++) {
      TabInfo tabInfo = attributesModifications.get(i);
      List<AuthorElement> authorElements = tabInfo.getNodes();
      List<TableProperty> attrsToModify = tabInfo.getProperties();
      AuthorNode commonAncestor = authorAccess.getDocumentController().getCommonAncestor(authorElements.toArray(new AuthorElement[0]));
      
      if (commonAncestor == null) {
        continue;
      }
      
      int[] offsets = new int[authorElements.size()];
      for (int j = 0; j < authorElements.size(); j++) {
        AuthorElement authorElement = authorElements.get(j);
        offsets[j] = authorElement.getStartOffset();
      }

      Map<String, AttrValue> attributes = new LinkedHashMap<String, AttrValue>();
      for (int k = 0; k < attrsToModify.size(); k++) {
        TableProperty attr = attrsToModify.get(k);
        attributes.put(attr.getAttributeName(), attr.getCurrentValue() != null ? 
            new AttrValue(attr.getCurrentValue()) : null);
      }
      if(!attributes.isEmpty()) {
        authorAccess.getDocumentController().setMultipleAttributes(commonAncestor.getStartOffset(), offsets, attributes);
      }
    }
  }

  /**
   * Show the table properties dialog.
   * 
   * @param editedTablePropertiesInfo information about the editable table properties.
   * 
   * @return the modified table properties.
   * 
   * @throws AuthorOperationException
   */
  private EditedTablePropertiesInfo showDialog(EditedTablePropertiesInfo editedTablePropertiesInfo) {
    EditedTablePropertiesInfo tableInfo = null;
  
    Platform platform = authorAccess.getWorkspaceAccess().getPlatform();
    if (platform == Platform.STANDALONE) {
      Frame parentFrame = (Frame) authorAccess.getWorkspaceAccess().getParentFrame();
      SATablePropertiesCustomizerDialog saTablePropertiesCustomizer = new SATablePropertiesCustomizerDialog(
          parentFrame, 
          authorAccess.getAuthorResourceBundle(),
          authorAccess.getWorkspaceAccess()){
        @Override
        public String getHelpPageID() {
          return ShowTablePropertiesBaseOperation.this.getHelpPageID();
        }
      };
      
      // Obtain the modified properties for the current table
      tableInfo = saTablePropertiesCustomizer.getTablePropertiesInformation(
          editedTablePropertiesInfo);
    } else if (platform == Platform.ECLIPSE) {
      //Eclipse table customization
      ECTablePropertiesCustomizerDialog ecTablePropertiesCustomizer = new ECTablePropertiesCustomizerDialog(
          (Shell) authorAccess.getWorkspaceAccess().getParentFrame(), 
          authorAccess.getAuthorResourceBundle(),
          authorAccess.getWorkspaceAccess(), ShowTablePropertiesBaseOperation.this.getHelpPageID());

      // Obtain the modified properties for the current table
      tableInfo = ecTablePropertiesCustomizer.getTablePropertiesInformation(
          editedTablePropertiesInfo);
    }
    return tableInfo;
  }

  /**
   * Check if there is a selection. The selection can be single selection or multiple selection
   * If no selection, we must use the node at caret position
   * 
   * @return the current selections.
   * 
   * @throws AuthorOperationExceptio if the action cannot handle the current selection.
   */
  private List<Integer[]> getSelections() throws AuthorOperationException {
    List<Integer[]> selections = new ArrayList<Integer[]>();
    // Check the selection first
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
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      selections.add(new Integer[] {caretOffset, caretOffset});
    }
    
    // Check if only one table is selected
    List<AuthorElement> tableElements = 
        TableOperationsUtil.getTableElementsOfType(authorAccess, selections, 
            TablePropertiesConstants.TYPE_TABLE, tableHelper);
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
    return selections;
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
      TableOperationsUtil.getChildElements(currentRow, TablePropertiesConstants.TYPE_CELL, children, tableHelper);
      for (int j = 0; j < children.size(); j++) {
        // Get the row spans
        int[] tableRowSpanIndices = authorAccess.getTableAccess().getTableRowSpanIndices(children.get(j));
        if (tableRowSpanIndices != null && tableRowSpanIndices[1] - tableRowSpanIndices[0] > 0) {
          // Current row has row spans
          // Obtain the parent
          AuthorElement tgroup = TableOperationsUtil.getElementAncestor(currentRow, parentType, tableHelper);
          if (tgroup != null) {
            List<AuthorElement> rows = new ArrayList<AuthorElement>();
            // Get all row children from the parent
            TableOperationsUtil.getChildElements(tgroup, TablePropertiesConstants.TYPE_ROW, rows, tableHelper);
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
   * 
   * @return a list tab info objects which contains all the fragments which will be modified and the 
   * corresponding offsets (the offsets where the fragments will be inserted).
   */
  protected List<TabInfo> getFragmentsAndOffsetsToInsert(
      EditedTablePropertiesInfo tableInfo) throws AuthorOperationException {
    List<TabInfo> tabsInfo = tableInfo.getCategories();
    List<TabInfo> modifications = new ArrayList<TabInfo>();
    for (int i = 0; i < tabsInfo.size(); i++) {
      List<AuthorDocumentFragment> fragments = new ArrayList<AuthorDocumentFragment>();
      List<Position> offsets = new ArrayList<Position>();
      // Obtain the current tab info
      TabInfo tabInfo = tabsInfo.get(i);
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
    List<AuthorElement> tableElements = 
        TableOperationsUtil.getTableElementsOfType(authorAccess, selections, 
            TablePropertiesConstants.TYPE_TABLE, tableHelper);
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
        } else if (
            (computedValue == null && addPreserve) 
                || (computedValue != null 
                    && !computedValue.equals(currentVal) 
                    && !computedValue.equals(TablePropertiesConstants.ATTR_NOT_SET) 
                    && !computedValue.equals(TablePropertiesConstants.PRESERVE)
                    && addPreserve)) {
          // Preserve
          computedValue = TablePropertiesConstants.PRESERVE;
        }
      } else if (computedValue != null
          && !computedValue.equals(TablePropertiesConstants.NOT_COMPUTED) 
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
                        AuthorElement tableElement = TableOperationsUtil.getElementAncestor(authorElement, TablePropertiesConstants.TYPE_TABLE, tableHelper);
                        TableOperationsUtil.getChildElements(tableElement, TablePropertiesConstants.TYPE_ROW, rowchildren, tableHelper);
                        // Obtain the entirely selected children
                        TableOperationsUtil.computeElementsList(
                            selectedRowChildren, 
                            tableElement, 
                            editorAccess.getBalancedSelection(currentSel[0], currentSel[1])[0], 
                            editorAccess.getBalancedSelection(currentSel[0], currentSel[1])[1], 
                            TablePropertiesConstants.TYPE_ROW,
                            true, 
                            tableHelper);
                        // The table is not entirely selected
                        if (rowchildren.size() > selectedRowChildren.size()) {
                          tab = TAB_TYPE.ROW_TAB;
                          rowchildren = new ArrayList<AuthorElement>();
                          // Compute the selected rows
                          TableOperationsUtil.computeElementsList(
                              rowchildren, 
                              tableElement, 
                              currentSel[0], 
                              currentSel[1], 
                              TablePropertiesConstants.TYPE_ROW,
                              false, 
                              tableHelper);
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
      LOGGER.debug(e.getMessage(), e);
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
    TableOperationsUtil.getChildElements(element, TablePropertiesConstants.TYPE_CELL, children, tableHelper);
    // Obtain the selected children
    List<AuthorElement> selectedCellChildren = new ArrayList<AuthorElement>();
    TableOperationsUtil.computeElementsList(selectedCellChildren , element, 
        selStart, selEnd, TablePropertiesConstants.TYPE_CELL, true, tableHelper);
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
    } else if (tableHelper.isNodeOfType(element, TablePropertiesConstants.TYPE_ROW)) {
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
  
  /**
   * Get the ID of the help page which will be called by the end user.
   * @return the ID of the help page which will be called by the end user or <code>null</code>.
   */
  protected String getHelpPageID(){
    return null;
  }
}
