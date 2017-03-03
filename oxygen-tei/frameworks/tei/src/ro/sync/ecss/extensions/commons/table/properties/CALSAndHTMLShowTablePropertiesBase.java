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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.table.operations.TableOperationsUtil;

/**
 * Base class for edit properties on CALS and HTML tables.
 * 
 * @author adriana_sbircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class CALSAndHTMLShowTablePropertiesBase extends ShowTablePropertiesBaseOperation {
  /**
   * Array with common possible values for horizontal alignment 
   */
  public static final String[] HORIZONTAL_ALIGN_VALUES = new String[] {
    TablePropertiesConstants.LEFT,
    TablePropertiesConstants.RIGHT,
    TablePropertiesConstants.CENTER,
    TablePropertiesConstants.JUSTIFY,
    TablePropertiesConstants.CHAR,
  };
  
  /**
   * Array with common possible values for vertical alignment 
   */
  public static final String[] VERTICAL_ALIGN_VALUES = new String[] {
    TablePropertiesConstants.TOP,
    TablePropertiesConstants.MIDDLE,
    TablePropertiesConstants.BOTTOM,
  };
  
  /**
   * Constructor.
   * 
   * @param helper The table helper.
   */
  public CALSAndHTMLShowTablePropertiesBase(TablePropertiesHelper helper) {
    super(helper);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.ShowTablePropertiesBaseOperation#getCategoriesAndProperties(java.util.List)
   */
  @Override
  protected List<TabInfo> getCategoriesAndProperties(
      List<Integer[]> selections) {
    List<TabInfo> categories = new ArrayList<TabInfo>();

    //Table tab
    TabInfo tableInformation = getTableInformation(selections);
    if (tableInformation != null) {
      categories.add(tableInformation);
    }
    
    // Row tab
    TabInfo rowsInformation = getRowsInformation(selections);
    if (rowsInformation != null) {
      categories.add(rowsInformation);
    }
    
    // Column tab
    TabInfo columnsInformation = getColumnsInformation(selections);
    if (columnsInformation != null) {
      categories.add(columnsInformation);
    }
    
    // Cell tab
    TabInfo cellsInformation = getCellsInformation(selections);
    if (cellsInformation != null) {
      categories.add(cellsInformation);
    }
    return categories;
  }
  
  /**
   * Obtain the information for columns tab. This information will contain the properties 
   * which will be edited, the columns on which those properties applies and some context 
   * information.
   * 
   * @param selections          The list with the selection intervals.
   * 
   * @return  The tab information object or <code>null</code> is there are no 
   *          properties to edit for columns.   
   */
  private TabInfo getColumnsInformation(List<Integer[]> selections) {
    TabInfo columnsTabInfo = null;
    // Compute the selected cells
    List<AuthorElement> cells = TableOperationsUtil.getTableElementsOfType(
        authorAccess, selections, TablePropertiesConstants.TYPE_CELL, tableHelper);
    // Compute the colspecs
    List<AuthorElement> colSpecs = getColSpecs(getCellIndexes(cells));
    // Obtain the attribute which will be edited
    List<TableProperty> attributes = getColumnsAttributes();
    List<TableProperty> columnsProperties = new ArrayList<TableProperty>();
    ArrayList<AuthorElement> nodes = new ArrayList<AuthorElement>();
    Map<TableProperty, String> commonValues = new HashMap<TableProperty, String>();
    
    nodes.addAll(colSpecs);
    if (!colSpecs.isEmpty()) {
      // Found colspecs
      for (int i = 0; i < colSpecs.size(); i++) {
        AuthorElement currentElement = colSpecs.get(i);
        for (int j = 0; j < attributes.size(); j++) {
          // Initialize the common value for every attribute
          if (i == 0) {
            commonValues.put(attributes.get(j), TablePropertiesConstants.NOT_COMPUTED);
          }
          // Obtain the common value
          commonValues.put(attributes.get(j), getCommonValue(currentElement, 
              attributes.get(j).getAttributeName(), commonValues.get(attributes.get(j))));
        }
      }
      
      for (int i = 0; i < attributes.size(); i++) {
        TableProperty currentAttribute = attributes.get(i);
        // Creare the table property object for current attribute
        TableProperty tb = getAttrProperty(
            colSpecs, 
            commonValues.get(currentAttribute), 
            currentAttribute);
        if (tb != null) {
          columnsProperties.add(tb);
        }
      }
      
      // Create the tab information
      columnsTabInfo = new TabInfo(
          colSpecs.size() > 1 ? ExtensionTags.COLUMNS : ExtensionTags.COLUMN, 
              columnsProperties, 
              colSpecs);
      // Set the context infotmation
      columnsTabInfo.setContextInfo(MessageFormat.format(
          authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.AFFECTED_COLUMNS), 
          colSpecs.size()));
    }
    
    return columnsTabInfo;
  }

  /**
   * Obtain the tab information for cells.
   * 
   * @param selections          The list with the selection intervals.
   * 
   * @return  The tab information object or <code>null</code> is there are no 
   *          properties to edit for cells. 
   */
  private TabInfo getCellsInformation(List<Integer[]> selections) {
    TabInfo cellsTabInfo = null;
    // Obtain the cells which will be affected.
    List<AuthorElement> cells = TableOperationsUtil.getTableElementsOfType(
        authorAccess, selections, TablePropertiesConstants.TYPE_CELL, tableHelper);
    List<TableProperty> cellsProperties = new ArrayList<TableProperty>();
    ArrayList<AuthorElement> nodes = new ArrayList<AuthorElement>();
    Map<TableProperty, String> commonValues = new HashMap<TableProperty, String>();
    List<TableProperty> attributes = getCellsAttributes();
    nodes.addAll(cells);
    if (!cells.isEmpty()) {
      // Obtain the common value for every property
      for (int i = 0; i < cells.size(); i++) {
        AuthorElement currentElement = cells.get(i);
        for (int j = 0; j < attributes.size(); j++) {
          // Initialize the common value for every attribute
          if (i == 0) {
            commonValues.put(attributes.get(j), TablePropertiesConstants.NOT_COMPUTED);
          }
          
          commonValues.put(attributes.get(j), getCommonValue(currentElement, attributes.get(j).getAttributeName(), commonValues.get(attributes.get(j))));
        }
      }

      for (int i = 0; i < attributes.size(); i++) {
        // Obtain the table property object
        TableProperty currentAttribute = attributes.get(i);
        TableProperty tb = getAttrProperty(
            cells, 
            commonValues.get(currentAttribute), 
            currentAttribute);
        if (tb != null) {
          cellsProperties.add(tb);
        }
      }
      // Create the tab info
      cellsTabInfo = new TabInfo(cells.size() > 1 ? ExtensionTags.CELLS : ExtensionTags.CELL, cellsProperties, nodes);

      // Set the context information
      cellsTabInfo.setContextInfo(MessageFormat.format(
          authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.AFFECTED_CELLS), 
          cells.size()));
    }
    
    return cellsTabInfo;
  }

  /**
   * Obtain the value for the given attribute set on the given element.
   * 
   * @return A list with allowed attributes.
   */
  protected List<TableProperty> getCellsAttributes() {
    List<TableProperty> attrs = new ArrayList<TableProperty>();
    // Horizontal align
    attrs.add(new TableProperty(
        TablePropertiesConstants.ALIGN, 
        ExtensionTags.HORIZONTAL_ALIGNMENT, 
        Arrays.asList(HORIZONTAL_ALIGN_VALUES), 
        null, 
        ExtensionTags.HORIZONTAL_ALIGNMENT, 
        GuiElements.RADIO_BUTTONS, 
        null,
        true, 
        true));
    // Vertical align
    attrs.add(new TableProperty(
        TablePropertiesConstants.VALIGN, 
        ExtensionTags.VERTICAL_ALIGNMENT, 
        Arrays.asList(VERTICAL_ALIGN_VALUES), 
        null, 
        ExtensionTags.VERTICAL_ALIGNMENT, 
        GuiElements.RADIO_BUTTONS,
        null,
        true, 
        true));
    // Column separator
    attrs.add(new TableProperty(
        TablePropertiesConstants.COLSEP, 
        ExtensionTags.COLUMN_SEPARATOR, 
        Arrays.asList(new String[] {"0", "1"}), 
        null, 
        ExtensionTags.SEPARATORS, 
        GuiElements.COMBOBOX,
        null,
        true, 
        true));
    // Row separator
    attrs.add(new TableProperty(
        TablePropertiesConstants.ROWSEP, 
        ExtensionTags.ROW_SEPARATOR, 
        Arrays.asList(new String[] {"0", "1"}), 
        null, 
        ExtensionTags.SEPARATORS, 
        GuiElements.COMBOBOX,
        null,
        true, 
        true));
    
    return attrs;
  }

  /**
   * Obtain the information for rows tab. This information will contain the properties 
   * which will be edited, the rows on which those properties applies and some context 
   * information.
   * 
   * @param selections          The list with the selection intervals.
   * 
   * @return The tab info object or <code>null</code> is there are no properties to edit for rows.   
   */
  private TabInfo getRowsInformation(List<Integer[]> selections) {
    TabInfo tabInfo = null;
    List<TableProperty> rowsProperties = new  ArrayList<TableProperty>();
    // Obtain the rows from all the selected nodes
    List<AuthorElement> collectedRows = TableOperationsUtil.getTableElementsOfType(
        authorAccess, selections, TablePropertiesConstants.TYPE_ROW, tableHelper);
    List<TableProperty> attributesToEdit = getRowsAttributesToEdit();
    // Check if row or child of a row
    // If a parent of a row, do not add type property, because you have no idea what
    // row(s) to transform, add only attributes which for all the rows in the table.
    // No row element
    if (!collectedRows.isEmpty()) {
      // Row Types
      String rowType = null;
      Map<TableProperty, String> commonValues = new HashMap<TableProperty, String>();
      // We add the preserve option for row type only if the type of the rows is mixed
      // If all the rows have the same type, then we add only the supported values
      // And the current value is preserve
      for (int i = 0; i < collectedRows.size(); i++) {
        AuthorElement rowElement = collectedRows.get(i);
        AuthorNode parent = rowElement.getParent();
        // Obtain the parent
        String currentType = null;
        if (parent.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
          if (tableHelper.isTableBody((AuthorElement) parent)) {
            currentType = TablePropertiesConstants.ROW_TYPE_BODY;
          } else if (tableHelper.isTableHead((AuthorElement) parent)) {
            currentType = TablePropertiesConstants.ROW_TYPE_HEADER;
          } else if (tableHelper.allowsFooter() && tableHelper.isTableFoot((AuthorElement) parent)) {
            currentType = TablePropertiesConstants.ROW_TYPE_FOOTER;
          }
        }
        if (rowType == null) {
          rowType = currentType;
        } else if (!rowType.equals(currentType) && !rowType.equals(TablePropertiesConstants.PRESERVE)) {
          rowType = TablePropertiesConstants.PRESERVE;
          
        }

        //Check value for rowsep and valign for every node
        // If all have the same value for the same attr, that will be the current value, 
        // else preserve will be the current value
        for (int j = 0; j < attributesToEdit.size(); j++) {
          // Initialize the common value for every attribute
          if (i == 0) {
            commonValues.put(attributesToEdit.get(j), TablePropertiesConstants.NOT_COMPUTED);
          }
          String commonValue = getCommonValue(
              rowElement, attributesToEdit.get(j).getAttributeName(), commonValues.get(attributesToEdit.get(j)));
          commonValues.put(attributesToEdit.get(j), commonValue);
        }
      }

      Map<String, String> icons = new HashMap<String, String>();
      // We have a row element, so add the type property
      ArrayList<String> attrValues = new ArrayList<String>();
      attrValues.add(TablePropertiesConstants.ROW_TYPE_HEADER);
      attrValues.add(TablePropertiesConstants.ROW_TYPE_BODY);
      // Icons for preview
      icons.put(TablePropertiesConstants.ROW_TYPE_HEADER, TablePropertiesConstants.ICON_ROW_TYPE_HEADER);
      icons.put(TablePropertiesConstants.ROW_TYPE_BODY, TablePropertiesConstants.ICON_ROW_TYPE_BODY);
      if (tableHelper.allowsFooter()) {
        attrValues.add(TablePropertiesConstants.ROW_TYPE_FOOTER);
        icons.put(TablePropertiesConstants.ROW_TYPE_FOOTER, TablePropertiesConstants.ICON_ROW_TYPE_FOOTER);
      }

      if (rowType != null && rowType.equals(TablePropertiesConstants.PRESERVE)) {
        attrValues.add(TablePropertiesConstants.PRESERVE);
        icons.put(TablePropertiesConstants.PRESERVE, TablePropertiesConstants.EMPTY_ICON);
      }
      // Check if the row type can be changed
      boolean active = checkRowSpans(collectedRows, TablePropertiesConstants.TYPE_GROUP);
      // Add the property
      rowsProperties.add(new TableProperty(
      TablePropertiesConstants.ROW_TYPE_PROPERTY, 
      ExtensionTags.ROW_TYPE, 
      attrValues, 
      rowType, 
      ExtensionTags.ROW_TYPE, 
      GuiElements.RADIO_BUTTONS, 
      icons, 
      false,
      active));

      // Check if the current table allows align and rowsep for rows
      ///Row attributes
      for (int i = 0; i < attributesToEdit.size(); i++) {
        TableProperty currentAttribute = attributesToEdit.get(i);
        TableProperty tb = getAttrProperty(
            collectedRows, 
            commonValues.get(currentAttribute), 
            currentAttribute);
        if (tb != null) {
          rowsProperties.add(tb);
        }
      }
    }

    if (!rowsProperties.isEmpty()) {
      tabInfo = new TabInfo(collectedRows.size() > 1 ? ExtensionTags.ROWS : ExtensionTags.ROW, rowsProperties, collectedRows);
      // Set the context information
      tabInfo.setContextInfo(MessageFormat.format(
          authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.AFFECTED_ROWS), collectedRows.size()));
    }

    return tabInfo;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.ShowTablePropertiesBaseOperation#computeFragmentsToMoveInsideFooter(java.util.List, java.util.List, ro.sync.ecss.extensions.commons.table.properties.TabInfo, java.util.List, ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  protected boolean computeFragmentsToMoveInsideFooter(
      List<AuthorDocumentFragment> fragments,
      List<Position> offsets, 
      TabInfo tabInfo, 
      List<AuthorElement> nodesToModify,
      AuthorElement currentNode) throws AuthorOperationException {
    int offset = -1;
    boolean deleteParent = false;
    // Footer -> Change all the rows to footer
    if (!tableHelper.isTableFoot((AuthorElement) currentNode.getParentElement())) {
      try {
        AuthorElement tableFoot = tableHelper.getFirstChildOfTypeFromParentWithType(
            currentNode, TablePropertiesHelper.TYPE_FOOTER, TablePropertiesHelper.TYPE_GROUP);
        if (tableFoot == null) { 
          // We have to create also the footer element
          AuthorElement elementAncestor = 
              TableOperationsUtil.getElementAncestor(currentNode, TablePropertiesHelper.TYPE_GROUP, tableHelper);
          // Insert after header or before body
          List<AuthorNode> contentNodes = elementAncestor.getContentNodes();
          for (int i = 0; i < contentNodes.size(); i++) {
            AuthorNode elementAncestorChild = contentNodes.get(i);
            if (elementAncestorChild instanceof AuthorElement
                && tableHelper.getElementType((AuthorElement) elementAncestorChild) == TablePropertiesConstants.TYPE_HEADER) {
              // Insert after header
              authorAccess.getDocumentController().insertXMLFragment(
                  tableHelper.getElementTag(
                      TablePropertiesConstants.TYPE_FOOTER), 
                      elementAncestorChild, 
                      AuthorConstants.POSITION_AFTER);
              break;
            } else if (elementAncestorChild instanceof AuthorElement
                && tableHelper.getElementType((AuthorElement) elementAncestorChild) == TablePropertiesConstants.TYPE_BODY) {
              // Insert before body
              authorAccess.getDocumentController().insertXMLFragment(
                  tableHelper.getElementTag(TablePropertiesConstants.TYPE_FOOTER), 
                  elementAncestorChild, 
                  AuthorConstants.POSITION_BEFORE);
              break;
            } 
          }
          
          //Obtain the footer node
          tableFoot = tableHelper.getFirstChildOfTypeFromParentWithType(
              currentNode, TablePropertiesHelper.TYPE_FOOTER, TablePropertiesHelper.TYPE_GROUP);
        }

        List<AuthorNode> contentNodes = currentNode.getParentElement().getContentNodes();
        // Check if the parent should be deleted.
        if (tabInfo.getNodes().containsAll(contentNodes)) {
          deleteParent = true;
        }
        // The footer element exists in the document
        if (tableFoot != null) {
          // The insert index will be the start offset of the first row
          offset = tableFoot.getStartOffset() + 1;
          Position pos = authorAccess.getDocumentController().createPositionInContent(offset);
          offsets.add(pos);
          processFragment(currentNode, fragments, false);
          if (!nodesToModify.contains(currentNode)) {
            nodesToModify.add(currentNode);
          }
        } 
      } catch (BadLocationException e) {
        throw new AuthorOperationException(e.getMessage(), e);
      }
    }
    
    return deleteParent;
  }
  
  @Override
  protected boolean computeFragmentMoveInsideHeader(
      List<AuthorDocumentFragment> fragments,
      List<Position> offsets, 
      TabInfo tabInfo, 
      List<AuthorElement> nodesToModify,
      AuthorElement currentNode) throws AuthorOperationException {
    int offset = -1;
    boolean deleteParent = false;
    // Header -> Change all the rows to header
    if (!tableHelper.isTableHead((AuthorElement) currentNode.getParentElement())) {
      try {
        AuthorElement tableHead = tableHelper.getFirstChildOfTypeFromParentWithType(
            currentNode, TablePropertiesHelper.TYPE_HEADER, TablePropertiesHelper.TYPE_GROUP);
        if (tableHead == null) { 
          // We have to create also the header element
          AuthorElement tgroupElem = TableOperationsUtil.getElementAncestor(currentNode, TablePropertiesHelper.TYPE_GROUP, tableHelper);
          List<AuthorElement> children = new ArrayList<AuthorElement>();
          // Check for footer
          if (tableHelper.allowsFooter()) {
            TableOperationsUtil.getChildElements(tgroupElem, TablePropertiesConstants.TYPE_FOOTER, children, tableHelper);
          }
          // No footer found, check for body
          if (children.isEmpty()) {
            TableOperationsUtil.getChildElements(tgroupElem, TablePropertiesConstants.TYPE_BODY, children, tableHelper);
          }
          
          if (!children.isEmpty()) {
            authorAccess.getDocumentController().insertXMLFragment(
                tableHelper.getElementTag(TablePropertiesConstants.TYPE_HEADER), 
                children.get(0).getStartOffset());
          }

          // Obtain the head node
          tableHead = tableHelper.getFirstChildOfTypeFromParentWithType(
              currentNode, TablePropertiesHelper.TYPE_HEADER, TablePropertiesHelper.TYPE_GROUP);
        }

        // Check if the parent should be deleted
        List<AuthorNode> contentNodes = currentNode.getParentElement().getContentNodes();
        if (tabInfo.getNodes().containsAll(contentNodes)) {
          deleteParent = true;
        }

        if (tableHead != null) {
          // The insert index will be the start offset of the first row
          offset = tableHead.getEndOffset();
          Position pos = authorAccess.getDocumentController().createPositionInContent(offset);
          offsets.add(pos);
          processFragment(currentNode, fragments, true);
          if (!nodesToModify.contains(currentNode)) {
            nodesToModify.add(currentNode);
          }
        } 
      } catch (BadLocationException e) {
        throw new AuthorOperationException(e.getMessage(), e);
      }
    }
    return deleteParent;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.ShowTablePropertiesBaseOperation#computeFragmentsToMoveInsideBody(java.util.List, java.util.List, ro.sync.ecss.extensions.commons.table.properties.TabInfo, java.util.List, ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  protected boolean computeFragmentsToMoveInsideBody(List<AuthorDocumentFragment> fragments,
      List<Position> offsets, TabInfo tabInfo, List<AuthorElement> nodesToModify,
      AuthorElement currentNode) throws AuthorOperationException {
      boolean deleteParent = false; 
      int offset = -1;
    // Body -> Change all the rows to body
    if (!tableHelper.isTableBody((AuthorElement) currentNode.getParentElement())) {
      try {
        AuthorElement tableBody = tableHelper.getFirstChildOfTypeFromParentWithType(
            currentNode, TablePropertiesHelper.TYPE_BODY, TablePropertiesHelper.TYPE_GROUP);
        if (tableBody == null) {
          // We have to create also the body element
          AuthorElement elementAncestor = TableOperationsUtil.getElementAncestor(
              currentNode, TablePropertiesHelper.TYPE_GROUP, tableHelper);
          authorAccess.getDocumentController().insertXMLFragment(
              tableHelper.getElementTag(TablePropertiesConstants.TYPE_BODY), 
              elementAncestor, 
              AuthorConstants.POSITION_INSIDE_LAST);
          // Obtain the body node
          tableBody = tableHelper.getFirstChildOfTypeFromParentWithType(
              currentNode, TablePropertiesHelper.TYPE_BODY, TablePropertiesHelper.TYPE_GROUP);
        }

        // Check if the parent should be deleted
        List<AuthorNode> contentNodes = currentNode.getParentElement().getContentNodes();
        if (tabInfo.getNodes().containsAll(contentNodes)) {
          deleteParent = true;
        }
        
        if (tableBody != null) {
          if (tableHelper.isTableHead((AuthorElement) currentNode.getParentElement())) {
            // If from header, obtain the start offset of the first child from the body element
            // The insert index will be the start offset of the first row
            offset = tableBody.getStartOffset() + 1;
          } else if (tableHelper.isTableFoot((AuthorElement) currentNode.getParentElement())) {
            // If from footer, obtain the 
            // The insert index will be the start offset of the first row
            offset = tableBody.getEndOffset();
          }
        } 
        if (offset != -1) {
          // We have an insert offset 
          Position pos = authorAccess.getDocumentController().createPositionInContent(offset);
          offsets.add(pos);
          processFragment(currentNode, fragments, false);
          if (!nodesToModify.contains(currentNode)) {
            nodesToModify.add(currentNode);
          }
        }
      } catch (BadLocationException e) {
        throw new AuthorOperationException(e.getMessage(), e);
      }
    }
    
    return deleteParent;
  }
  
  /**
   * Process the fragments and add them to the fragments to insert.
   * 
   * @param currentNode The current row node.
   * @param fragments   The list with fragment which will be inserted.
   * @param moveToHeader <code>true</code> if the current node is moved from body/footer to header.
   */
  protected void processFragment(AuthorElement currentNode, List<AuthorDocumentFragment> fragments, boolean moveToHeader) throws BadLocationException {
    fragments.add(authorAccess.getDocumentController().createDocumentFragment(currentNode, true));
  }
  
  /**
   * Obtain the attributes qualified name and render string (for rows).
   * 
   * @return A list with attributes to edit.
   */
  protected List<TableProperty> getRowsAttributesToEdit() {
    List<TableProperty> attrs = new ArrayList<TableProperty>(2);
    // Vertical align
    attrs.add(new TableProperty(
        TablePropertiesConstants.VALIGN, 
        ExtensionTags.VERTICAL_ALIGNMENT, 
        Arrays.asList(VERTICAL_ALIGN_VALUES), 
        null, 
        ExtensionTags.VERTICAL_ALIGNMENT, 
        GuiElements.RADIO_BUTTONS, 
        null,
        true, 
        true));
    // Row separators
    attrs.add(new TableProperty(
        TablePropertiesConstants.ROWSEP, 
        ExtensionTags.ROW_SEPARATOR, 
        Arrays.asList(new String[] {"0", "1"}), 
        null, 
        ExtensionTags.SEPARATORS, 
        GuiElements.COMBOBOX, 
        null,
        true, 
        true));
    
    return attrs;
  }
  /**
   * Obtain the value for the given attribute set on the given element.
   * 
   * @return A list with allowed attributes.
   */
  protected List<TableProperty> getColumnsAttributes() {
    List<TableProperty> attrs = new ArrayList<TableProperty>();
    // Horizontal align
    attrs.add(new TableProperty(
        TablePropertiesConstants.ALIGN, 
        ExtensionTags.HORIZONTAL_ALIGNMENT, 
        Arrays.asList(HORIZONTAL_ALIGN_VALUES), 
        null, 
        ExtensionTags.HORIZONTAL_ALIGNMENT, 
        GuiElements.RADIO_BUTTONS, 
        null,
        true, 
        true));
    // Column separator
    attrs.add(new TableProperty(
        TablePropertiesConstants.COLSEP, 
        ExtensionTags.COLUMN_SEPARATOR, 
        Arrays.asList(new String[] {"0", "1"}), 
        null, 
        ExtensionTags.SEPARATORS, 
        GuiElements.COMBOBOX, 
        null,
        true, 
        true));
    // Row separator
    attrs.add(new TableProperty(
        TablePropertiesConstants.ROWSEP, 
        ExtensionTags.ROW_SEPARATOR, 
        Arrays.asList(new String[] {"0", "1"}), 
        null, 
        ExtensionTags.SEPARATORS, 
        GuiElements.COMBOBOX, 
        null,
        true, 
        true));
    
    return attrs;
  }
  
  /**
   * Obtain the colspecs elements for the given cells indexes.
   * 
   * @param map A map containing the table elements and cells indexes.
   * 
   * @return A list with the colspecs elements for the given cells indexes.
   */
  protected abstract List<AuthorElement> getColSpecs(Map<AuthorElement, Set<Integer>> map);
  
  /**
   * Obtain the indexes for selected cells.
   * 
   * @param cells The selected cells.
   * 
   * @return A map containing the cell indexes based on the parent tgroup. 
   */
  protected abstract Map<AuthorElement, Set<Integer>> getCellIndexes(List<AuthorElement> cells);
}
