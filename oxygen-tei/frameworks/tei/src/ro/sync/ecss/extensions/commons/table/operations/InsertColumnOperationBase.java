/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2009 Syncro Soft SRL, Romania.  All rights
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
import java.util.Arrays;
import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorOperationStoppedByUserException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.SelectionInterpretationMode;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.table.operations.TableColumnSpecificationInformation;
import ro.sync.ecss.extensions.commons.ExtensionTags;

/**
 * Operation used to insert a table column.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class InsertColumnOperationBase extends AbstractTableOperation {
  
  /**
   * Constructor.
   * 
   * @param documentTypeHelper Document type helper, has methods specific to a 
   * document type.
   */
  public InsertColumnOperationBase(AuthorTableHelper documentTypeHelper) {
    super(documentTypeHelper);
  }

  /**
   * Arguments.
   */
  private static final ArgumentDescriptor[] ARGUMENTS = 
    new ArgumentDescriptor[] { NAMESPACE_ARGUMENT_DESCRIPTOR };
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    
    // namespace argument
    String namespace = null;
    Object namespaceObj =  args.getArgumentValue(NAMESPACE_ARGUMENT);
    if (namespaceObj instanceof String) {
      namespace = (String) namespaceObj;
    } 
    // Insert column
    performInsertColumn(authorAccess, namespace, null, null, false, null, null);
  }
  
  /**
   * Insert column.
   * 
   * @param authorAccess The author access.
   * @param namespace The cells namespace.
   * @param fragments An array of AuthorDocumentFragments that are used as content of the inserted cells.  
   * @param columnSpecification The column specification data.
   * @param cellsFragments If the value is <code>true</code> then the fragments 
   * where originally cells. 
   * @param insertRowOperation The insert row operation used to insert new rows when 
   * there are fragments that cannot be inserted in the new column. 
   * @param insertTableOperation The insert table operation used to insert the column 
   * wrapped in a new table when the insert offset is not inside a table.
   * @throws IllegalArgumentException
   * @throws AuthorOperationException
   */
  public void performInsertColumn(
      AuthorAccess authorAccess, 
      String namespace, 
      AuthorDocumentFragment[] fragments, 
      TableColumnSpecificationInformation columnSpecification, 
      boolean cellsFragments, 
      InsertRowOperationBase insertRowOperation,
      InsertTableOperationBase insertTableOperation) throws AuthorOperationException {
    try {
      // Find the index where the new column will be inserted
      int newColumnIndex = -1;
      
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      AuthorNode nodeAtCaret = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
      
      // Find the table element to create table span support
      AuthorElement tableElement = getElementAncestor(
          nodeAtCaret, AuthorTableHelper.TYPE_TABLE);
      if (tableElement != null) {
        // Create the table support
        AuthorTableCellSpanProvider tableSupport = 
          tableHelper.getTableCellSpanProvider(tableElement);
        if (isTableElement(nodeAtCaret, AuthorTableHelper.TYPE_ROW)) {
          // The caret is inside a table row. 
          // Try to find the insertion index analyzing the left and right cell column index
          newColumnIndex = findColumnIndex(authorAccess, caretOffset + 1);
          if (newColumnIndex == -1) {
            // No cell after, try to find a cell before
            newColumnIndex = findColumnIndex(authorAccess, caretOffset - 1);
            if (newColumnIndex != -1) {
              // Insert after left column
              newColumnIndex ++;
            } else {
              // There are no neighboring cells, use the first column
              newColumnIndex = 0;
            }
          }
        } else {
          // The caret is not inside a table row. Find the nearest table cell that include the caret
          AuthorElement cell = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_CELL);
          if (cell != null) {
            int[] cellIndex = authorAccess.getTableAccess().getTableCellIndex(cell);
            if (cellIndex != null) {
              Integer colSpan = tableSupport.getColSpan(cell);
              newColumnIndex = cellIndex[1] + (colSpan != null ? colSpan.intValue() : 1);
            } else {            
              throw new AuthorOperationException(
                  "Cannot obtain the index of cell in table. The cell is: " + cell);
            }
          } else {
            throw new AuthorOperationException(
            "Cannot find a cell in the table at the current caret position.");
          }
        }
        
        // The current number of columns 
        int numberOfColumns = authorAccess.getTableAccess().getTableNumberOfColumns(tableElement);

        // Update the column specification for the table only if the table is empty or it already 
        // contains some column specifications.
        if (authorAccess.getTableAccess().getTableNumberOfColumns(tableElement) == 0 ||
            tableSupport.hasColumnSpecifications(tableElement)) {
          updateColumnCellsSpan(
              authorAccess, tableSupport, tableElement, newColumnIndex, columnSpecification, namespace);
        }

        // Insert the new entries in the rows in the appropriate places
        insertNewColumnCells(
            authorAccess, tableSupport, tableElement, newColumnIndex, namespace,
            fragments, cellsFragments, insertRowOperation);

        tableHelper.updateTableColumnNumber(
            authorAccess, 
            tableElement,
            numberOfColumns + 1);
      } else {
        insertTableOperation.insertTable(fragments, cellsFragments, authorAccess, namespace, tableHelper);
      }
      
    } catch (BadLocationException e) {
      throw new AuthorOperationException(
          "The operation cannot be performed due to: " + e.getMessage(), 
          e);
    }
  }

  /**
   * Increments the column span of the cells intersecting the new column.
   * A cell intersects the column to insert if its start column index is less than
   * the new column index and the end column index of the cell is greater or equal
   * than the new column ((startColSpan < newColumnIndex) && (endColSpan >= newColumnIndex)).
   * 
   * @param authorAccess The author access.
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param tableSupport The table cell span provider.
   * @param tableElem    The table element.
   * @param newColumnIndex The index of the column to insert.
   * @param columnSpecification The table column specification data.
   * @param namespace    The namespace to be used.
   * @throws AuthorOperationException  When the insertion fails.
   */
  protected void updateColumnCellsSpan(
      AuthorAccess authorAccess, 
      AuthorTableCellSpanProvider tableSupport,
      AuthorElement tableElem,
      int newColumnIndex,
      TableColumnSpecificationInformation columnSpecification, 
      String namespace) throws AuthorOperationException {
    
    int rowCount = authorAccess.getTableAccess().getTableRowCount(tableElem);
    if (newColumnIndex > 0) {
      if (rowCount != -1) {
        AuthorElement prevCell = null;
        // For each row from end to start
        for (int i = rowCount - 1; i >=0; i--) {
          // Check if there is a cell at the column where the insertion will occur
          AuthorElement cell = authorAccess.getTableAccess().getTableCellAt(i, newColumnIndex, tableElem);
          if(prevCell == cell) {
            continue;
          }
          prevCell = cell;
          if (cell != null) {
            int[] cellIndices = authorAccess.getTableAccess().getTableColSpanIndices(cell);
            int colSpanStart = cellIndices[0];
            int colSpanEnd = cellIndices[1];
            if (colSpanStart < newColumnIndex && newColumnIndex <= colSpanEnd) {
              // The cell spans over the added column, adjust its column span
              tableHelper.updateTableColSpan(
                  authorAccess,
                  tableSupport,
                  cell,
                  colSpanStart + 1, // adjust for 1 base
                  colSpanEnd + 2); // adjust for 1 base + increment
            }
          }
        }
      }
    }
  }

  /**
   * Insert the cells for the new column.
   * 
   * @param authorAccess The author access.
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param tableSupport The table cell span provider.
   * @param tableElement The table element.
   * @param newColumnIndex The column index, 0 based.
   * @param namespace The namespace to be used.
   * @param fragments The source fragments from which the attributes and content
   * must be copied.If there are more fragments than inserted cells, the contents 
   * of the remaining fragments are inserted in the last cell.
   * @param cellsFragment <code>true</code> if the fragments represents cells.
   * @param insertRowOperation The insert row operation used to insert new rows when 
   * there are fragments that cannot be inserted in the new column. 
   * 
   * @throws AuthorOperationException 
   */
  private void insertNewColumnCells(
      AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableSupport,
      AuthorElement tableElement,
      int newColumnIndex,
      String namespace, 
      AuthorDocumentFragment[] fragments, 
      boolean cellsFragment, 
      InsertRowOperationBase insertRowOperation) throws AuthorOperationException {
    // Flag to retain if the warning for incompatibility between table structure
    // and new column was shown
    boolean incompatibilityWarnShown = false;
    // Get the row count
    int rowCount = authorAccess.getTableAccess().getTableRowCount(tableElement);
    if (rowCount != -1) {
      // The list containing cells element names
      List<String> cellsElementNames = new ArrayList<String>();
      // The list containing cells insertion offsets
      List<Integer> cellsInsertionOffsets = new ArrayList<Integer>();
      
      for (int i = 0; i < rowCount; i++) {
        if (newColumnIndex > 0) {          
          // Check if there is a cell at the column where the insertion will occur
          AuthorElement cell = authorAccess.getTableAccess().getTableCellAt(i, newColumnIndex, tableElement);
          if (cell != null) {
            // Is an insertion necessary? 
            int[] cellIndices = authorAccess.getTableAccess().getTableColSpanIndices(cell);
            int colSpanStart = cellIndices[0];
            int colSpanEnd = cellIndices[1];
            if (colSpanStart < newColumnIndex && newColumnIndex <= colSpanEnd) {
              // Skip to the next row
              if (!incompatibilityWarnShown) {
                incompatibilityWarnShown = checkForCompatibility(authorAccess, fragments, i);
              }
              continue;
            }
          }
          
          // Check if there is a cell to the left of the location where the insertion should occur
          cell = authorAccess.getTableAccess().getTableCellAt(i, newColumnIndex - 1, tableElement);
          if (cell == null) {
            // If there's no cell then skip to the next row
            if (!incompatibilityWarnShown) {
              incompatibilityWarnShown = checkForCompatibility(authorAccess, fragments, i);
            }
            continue;
          }
        }
        // Find the insertion offset (after the closest cell in the row starting from the left)
        int insertionOffset = findCellInsertionOffset(authorAccess, tableElement, i, newColumnIndex);
        
        if (insertionOffset != -1) {
          cellsInsertionOffsets.add(insertionOffset);
          // Update valid insertion offsets count
          String cellElementName =
              getCellElementName(authorAccess.getTableAccess().getTableRow(i, tableElement), newColumnIndex);
          cellsElementNames.add(cellElementName);
          if (cellElementName == null) {
            throw new AuthorOperationException(
                "The table model does not accept a new column at the given position.");
          }
        } else {
          // An offset was skipped 
          if (!incompatibilityWarnShown) {
            incompatibilityWarnShown = checkForCompatibility(authorAccess, fragments, i);
          }
        }
      }
      
      if (fragments == null) {
        // Insert empty cells
        String defaultContentForEmptyCells = getDefaultContentForEmptyCells();
        if (defaultContentForEmptyCells != null) {
          for (int i = cellsInsertionOffsets.size() - 1; i >= 0; i--) {
            StringBuilder xmlFragment = new StringBuilder();
            xmlFragment.append("<").append(cellsElementNames.get(i))
                    //EXM-26376 Also append the namespace.
                    .append(" xmlns=\"").append(namespace).append("\">");
            xmlFragment.append(defaultContentForEmptyCells);
            xmlFragment.append("</").append(cellsElementNames.get(i)).append(">");
            
            authorAccess.getDocumentController().insertXMLFragment(
                xmlFragment.toString(), 
                cellsInsertionOffsets.get(i));
          }
        } else {
          // Create insertion offsets array 
          int[] contentInsertOffsets = new int[cellsInsertionOffsets.size()];
          for (int i = 0; i < cellsInsertionOffsets.size(); i++) {
            contentInsertOffsets[i] = cellsInsertionOffsets.get(i);
          }
          // Insert empty cells
          authorAccess.getDocumentController().insertMultipleElements(tableElement, 
              cellsElementNames.toArray(new String[0]), contentInsertOffsets, namespace);
        }
        
        // Set the caret inside the first cell(we know is an empty element)
        authorAccess.getEditorAccess().setCaretPosition(cellsInsertionOffsets.get(0) + 1);
      } else {
        // Insert cells with content fragments
        
        int insertCellsCount = cellsInsertionOffsets.size();
        int[] contentInsertOffsets = new int[insertCellsCount];
        String[] xmlFragments = new String[insertCellsCount];

        // Determine the cells fragments 
        for (int i = 0; i < insertCellsCount; i++) {
          contentInsertOffsets[i] = cellsInsertionOffsets.get(i);
          // Create current cell fragment
          xmlFragments[i] = TableOperationsUtil.createCellXMLFragment(authorAccess, fragments, 
              cellsFragment, cellsElementNames.get(i), i, namespace, tableHelper);
        }
        
        // Create document fragments
        AuthorDocumentFragment[] fragmentsToInsert =
            authorAccess.getDocumentController().createNewDocumentFragmentsInContext(
                xmlFragments, contentInsertOffsets);


        boolean newRowsInserted = false;
        // Insert rows for the remaining cells
        if (insertRowOperation != null && insertCellsCount <= fragments.length - 1) {
          int tableRowCount = authorAccess.getTableAccess().getTableRowCount(tableElement);
          AuthorElement lastTableRow = authorAccess.getTableAccess().getTableRow(tableRowCount - 1, tableElement);
          if (lastTableRow == null) {
            AuthorOperationException exception = new AuthorOperationException(
            "The operation failed because the last table row could not be determined.");
            exception.setOperationRejectedOnPurpose(true);
            throw exception;
          }
          int insertRowsOffset = lastTableRow.getEndOffset() + 1;
          // Set flag to retain that new rows are inserted (in this case the selection
          // will not be marked as column)
          newRowsInserted = true;

          StringBuilder rowsFragments = new StringBuilder();
          String cellName = insertRowOperation.getCellElementName(tableElement, newColumnIndex); 
          // New rows must be inserted for all the remaining fragments and the fragments
          // will be inserted as cells at newColumnIndex
          for (int i = insertCellsCount; i < fragments.length; i++) {
            // Insert row
            try {
              String cellFragment = TableOperationsUtil.createCellXMLFragment(
                  authorAccess, fragments, cellsFragment, cellName, i, namespace, tableHelper); 
              String rowXMLFragment = insertRowOperation.getRowXMLFragment(
                  authorAccess, tableElement, namespace, cellFragment, newColumnIndex);
              if (rowXMLFragment == null) {
                throw new AuthorOperationException("The column cannot be inserted.");
              }
              rowsFragments.append(rowXMLFragment);
            } catch (BadLocationException e) {
              throw  new AuthorOperationException("The column cannot be inserted.");
            }
          }
          // Update fragments list
          fragmentsToInsert = Arrays.copyOf(fragmentsToInsert, fragmentsToInsert.length + 1);
          fragmentsToInsert[fragmentsToInsert.length - 1] =
            authorAccess.getDocumentController().createNewDocumentFragmentInContext(
                rowsFragments.toString(), insertRowsOffset);

          // Update offsets list
          contentInsertOffsets = Arrays.copyOf(contentInsertOffsets, contentInsertOffsets.length + 1);
          contentInsertOffsets[contentInsertOffsets.length - 1] = insertRowsOffset;
        }
        
        // Insert fragments with content
        boolean multipleFragmentsInserted =
            authorAccess.getDocumentController().insertMultipleFragments(
                tableElement, fragmentsToInsert, contentInsertOffsets);
        if (multipleFragmentsInserted) {
          int delta = 0;
          // Set the caret inside the first cell(we know is an empty element).
          authorAccess.getEditorAccess().setCaretPosition(contentInsertOffsets[0] + 1);
          // Select cells
          for (int i = 0; i < contentInsertOffsets.length; i++) {
            int currentOffset = contentInsertOffsets[i];
            int length = fragmentsToInsert[i].getLength();
            // Select current cell
            authorAccess.getEditorAccess().getAuthorSelectionModel().addSelection(
                currentOffset + delta, currentOffset + delta + length);
            // Update offset
            delta += length;
          }
          
          if (!newRowsInserted) {
            authorAccess.getEditorAccess().getAuthorSelectionModel().setSelectionInterpretationMode(
                SelectionInterpretationMode.TABLE_COLUMN);
          }
        } else {
          // The insert operation failed.
        }
      }
    } else {
      throw new AuthorOperationException(
          "Could not obtain the number of rows. Table is invalid.");
    }
  }

  
  /**
   * Check if all the fragments can be inserted in the new column.
   * If there are fragments for which there is no corresponding new cell, an exception 
   * is thrown.
   * 
   * @param authorAccess The Author access.
   * @param fragments The cell content fragments.
   * @param rowIndex The row number for each a fragment cannot be inserted. 
   * 
   * @return <code>true</code> if user did not canceled the operation.
   *
   * @throws AuthorOperationException 
   */
  private boolean checkForCompatibility(
      AuthorAccess authorAccess, AuthorDocumentFragment[] fragments, int rowIndex) throws AuthorOperationException {
    boolean messageShow = false;
    if (fragments != null && rowIndex < fragments.length) {
      int response = authorAccess.getWorkspaceAccess().showConfirmDialog(
          // Title
          authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.INSERT_COLUMN),
          // Message
          authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.HANDLE_PASTE_COLUMN_FAIL_MESSAGE), 
          new String[] {
              authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.INSERT_COLUMN),
              authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.CANCEL) },
          new int[] { 1, 0 });

      if (response != 1) {
        // Cancel
        throw new AuthorOperationStoppedByUserException("Cancelled by user");
      }
      messageShow = true;
      
    }
    return messageShow;
  }

  /**
   * Find the column index of the cell at the specified offset.
   * 
   * @param authorAccess The author access.
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param offset The offset of the searched column.
   * @return The index of the column at offset (0 based).
   * @throws BadLocationException
   * @throws AuthorOperationException
   */
  private int findColumnIndex(AuthorAccess authorAccess, int offset)
      throws BadLocationException, AuthorOperationException {
    int newColumnIndex = -1;
    // The caret is inside a table row
    AuthorNode relativeCell = 
      authorAccess.getDocumentController().getNodeAtOffset(offset);
    if (isTableElement(relativeCell, AuthorTableHelper.TYPE_CELL)) {
      int[] cellIndex = authorAccess.getTableAccess().getTableCellIndex((AuthorElement) relativeCell);
      if (cellIndex != null) {
        newColumnIndex = cellIndex[1];
      } else {            
        throw new AuthorOperationException(
            "Cannot obtain the index of cell in table. The cell is: " + relativeCell);
      }
    }
    return newColumnIndex;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }

  /**
   * Get the description for this operation.
   * 
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Insert a table column.";
  }
  
  //////////////////////////////
  //////// Abstract methods
  
  /**
   * Get the name of the element that will be inserted as a cell into the table.
   * 
   * @param rowElement The row element where the new cell will be inserted. 
   * @param newColumnIndex The new column index. 0 based.
   * @return The name of cell element.
   */
  protected abstract String getCellElementName(AuthorElement rowElement, int newColumnIndex);
  
  /**
   * Get the default content that must be introduced in empty cells.
   * 
   * @return The default content that must be introduced in empty cells.
   * Default: <code>null</code>.
   * 
   * @since 14.1
   */
  protected String getDefaultContentForEmptyCells() {
    return null;
  }
}