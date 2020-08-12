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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSelectionModel;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.SelectionInterpretationMode;
import ro.sync.ecss.extensions.api.access.AuthorTableAccess;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.properties.TableHelperConstants;

/**
 * Base implementation for operations used to delete table columns. If there are selections 
 * in the table, all the columns that intersect the selections are removed.
 * If there is no selection in the table, the column at caret is deleted.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class DeleteColumnOperationBase extends AbstractTableOperation {
  
  /**
   * The table element.
   */
  protected AuthorElement tableElem = null;
  
  /**
   * The index of the deleted column. 
   */
  protected List<Integer> deletedColumnsIndices = null;

  /**
   * Constructor.
   * 
   * @param documentTypeHelper The table helper specific to a document type. 
   * An implementation of {@link ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper}.
   */
  public DeleteColumnOperationBase(AuthorTableHelper documentTypeHelper) {
    super(documentTypeHelper);
  }

  /**
   * Delete table columns. The columns are detected in the following order:
   * <ul>
   *   <li>from the given column intervals</li>
   *   <li>from the selection</li>
   *   <li>from the caret position</li>
   * </ul> 
   * 
   * @param authorAccess The access to Author operations.
   * @param columnIntervals The intervals of the column to be deleted.
   * If <code>null</code>, the column at caret offset is deleted.
   * 
   * @param placeCaretInNextCell <code>true</code> to place caret in the next cell.
   * @return <code>true</code> if a column is deleted
   * 
   * @throws IllegalArgumentException
   * @throws AuthorOperationException
   */
  public boolean performDeleteColumn(AuthorAccess authorAccess, List<ContentInterval> columnIntervals, 
      boolean placeCaretInNextCell) 
  throws AuthorOperationException {
    boolean handled = false;
    // Reset temporary fields.
    tableElem = null;
    int rowIndex = -1;
    deletedColumnsIndices = new ArrayList<Integer>();
    
    List<Position> caretOffsetsForDeleteColumns = new ArrayList<Position>(); 
  
    // Test if the operation is available for the node at the caret position 
    try {
      AuthorTableAccess tableAccess = authorAccess.getTableAccess();
      
      AuthorDocumentController documentController = authorAccess.getDocumentController();
      if (columnIntervals != null && columnIntervals.size() > 0) {
        ContentInterval firstInterval = columnIntervals.get(0);
        tableElem = getElementAncestor(
            documentController.getNodeAtOffset(firstInterval.getStartOffset()),
            AuthorTableHelper.TYPE_TABLE);

        if (!canDeleteColumn()) {
          return false;
        }
        
        // Try to delete the selected columns
        if (tableElem != null) {
          AuthorTableCellSpanProvider spanProvider = 
            tableHelper.getTableCellSpanProvider(tableElem);
          Iterator<ContentInterval> selectionIterator = columnIntervals.iterator();
          int tableRowCount = tableAccess.getTableRowCount(tableElem);
          int tabelColumnCount = tableAccess.getTableNumberOfColumns(tableElem);
          List<AuthorElement> selectedCells = new ArrayList<AuthorElement>();
          loop: for (int i = 0; i < tableRowCount; i++) {
            for (int j = 0; j < tabelColumnCount; j++) {
              AuthorElement currentCell = tableAccess.getTableCellAt(i, j, tableElem);
              if (currentCell != null) {
                // Iterate through selection intervals to determine if the current cell is selected
                selectionIterator = columnIntervals.iterator();
                while (selectionIterator.hasNext()) {
                  ContentInterval selectionInterval = 
                    selectionIterator.next();
                  if (currentCell.getStartOffset() == selectionInterval.getStartOffset() 
                      && currentCell.getEndOffset() == selectionInterval.getEndOffset() - 1) {
                    // Found a selected cell
                    selectedCells.add(currentCell);
                    // Determine if the current cell has colspan
                    Integer colSpan = spanProvider.getColSpan(currentCell);
                    if (colSpan != null && colSpan > 1) {
                      selectionIterator.remove();
                      break;
                    } else {
                      // The cell does not have any colspan, we can use it as a reference for delete operation
                      caretOffsetsForDeleteColumns.add(documentController.createPositionInContent(currentCell.getStartOffset() + 1));
                      break loop;
                    }
                  }
                }
              }
            }
          }
          // If there is no cell without colspan to be used as reference, we will determine
          // the reference cell from the first common column of the selection
          if (caretOffsetsForDeleteColumns.isEmpty()) {
            List<Integer> commonCols = new ArrayList<Integer>();
            int[] indices = tableAccess.getTableColSpanIndices(selectedCells.get(0));
            for (int i = indices[0]; i <= indices[1]; i++) {
              commonCols.add(i);
              
            }
            for (int i = 1; i < selectedCells.size(); i++) {
              commonCols = computeCommonCols(commonCols, tableAccess.getTableColSpanIndices(selectedCells.get(i)));
            }
            int referenceColumn = commonCols.get(0);
            AuthorElement referenceCell = null;
            for (AuthorElement cell : selectedCells) {
              if (tableAccess.getTableCellIndex(cell)[1] == referenceColumn) {
               referenceCell = cell;
               break;
              }
            }
            if (referenceCell != null) {
              caretOffsetsForDeleteColumns.add(documentController.createPositionInContent(referenceCell.getStartOffset() + 1));
            }
          }
        }
      } else if(authorAccess.getEditorAccess().hasSelection()) {
        Map<Integer, List<AuthorElement>> mapColumnIndexToSelCells = new HashMap<Integer, List<AuthorElement>>();
        
        tableElem = getElementAncestor(
            documentController.getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset()),
            AuthorTableHelper.TYPE_TABLE);
        
        // Determine the selected cells
        List<AuthorElement> selectedCells = TableOperationsUtil.getTableElementsOfTypeFromSelection(authorAccess, TableHelperConstants.TYPE_CELL, 
            TableOperationsUtil.createTableHelper(tableHelper), tableElem);
        for (AuthorElement cellElement : selectedCells) {
          int[] cellIndices = tableAccess.getTableCellIndex(cellElement);
          int columnNumber = cellIndices[1];
          List<AuthorElement> cellsList = mapColumnIndexToSelCells.get(columnNumber);
          if (cellsList == null) {
            cellsList = new ArrayList<AuthorElement>(1);
            mapColumnIndexToSelCells.put(columnNumber, cellsList);
          }
          cellsList.add(cellElement);
        }
        
        Set<Integer> columnsToDelete = mapColumnIndexToSelCells.keySet();
        for (Integer columnToDelete : columnsToDelete) {
          List<AuthorElement> cells = mapColumnIndexToSelCells.get(columnToDelete);
          AuthorElement firstCell = cells.get(0);
          int offset = firstCell.getStartOffset() + 1;
          caretOffsetsForDeleteColumns.add(documentController.createPositionInContent(offset));
        }
      } else {
        caretOffsetsForDeleteColumns.add(documentController.createPositionInContent(authorAccess.getEditorAccess().getCaretOffset()));
        tableElem = getElementAncestor(
            documentController.getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset()),
            AuthorTableHelper.TYPE_TABLE);
      }
      
      // Sort caret offsets of the deleted columns descending
      for (int i = 0; i < caretOffsetsForDeleteColumns.size() - 1; i++) {
        for (int j = i + 1; j < caretOffsetsForDeleteColumns.size(); j++) {
          if (caretOffsetsForDeleteColumns.get(i).getOffset() < caretOffsetsForDeleteColumns.get(j).getOffset()) {
            Position aux = caretOffsetsForDeleteColumns.get(i);
            caretOffsetsForDeleteColumns.set(i, caretOffsetsForDeleteColumns.get(j));
            caretOffsetsForDeleteColumns.set(j, aux);
          }
        }
      }
      
      int initialColsNo = tableAccess.getTableNumberOfColumns(tableElem);
      for (Position caretPosition : caretOffsetsForDeleteColumns) {
        authorAccess.getEditorAccess().setCaretPosition(caretPosition.getOffset());
        
        // Look at caret offset
        AuthorNode nodeAtCaret = 
            documentController.getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset());
        AuthorElement currentCellElem = 
            getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_CELL);
        AuthorElement currentRowElem = 
            getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_ROW);
        tableElem = 
            getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_TABLE);
        
        if (currentCellElem != null && tableElem != null) {
          // Determine the index of column to be deleted
          int[] cellIndices = tableAccess.getTableCellIndex(currentCellElem);
          rowIndex = cellIndices[0];
          deletedColumnsIndices.add(cellIndices[1]);
          
          List<int[]> intervals = new ArrayList<int[]>();
          // For all the table rows delete the cell at the column index
          int rowsNumber = tableAccess.getTableRowCount(tableElem);
          AuthorTableCellSpanProvider spanProvider = 
              tableHelper.getTableCellSpanProvider(tableElem);
          for (int i = 0; i < rowsNumber; i++) {
            AuthorElement cell = tableAccess.getTableCellAt(i,
                deletedColumnsIndices.get(deletedColumnsIndices.size() - 1), tableElem);
            if (cell != null) {
              int[] spanIndices = tableAccess.getTableColSpanIndices(cell);
              Integer colSpanInteger = spanProvider.getColSpan(cell);
              Integer rowSpanInteger = spanProvider.getRowSpan(cell);
              int colSpan = colSpanInteger != null ? colSpanInteger.intValue() : 1;
              int rowSpan = rowSpanInteger != null ? rowSpanInteger.intValue() : 1;
              if (colSpan == 1) {
                // Delete the cell only if the column span is 1 (one).
                intervals.add(new int[] {cell.getStartOffset(), cell.getEndOffset()});
              } else {
                // Decrease the column span of the cell with one
                updateTableColSpan(authorAccess, spanProvider, cell, spanIndices[0] + 1, spanIndices[1] + 1);
              }
              if (rowSpan > 1) {
                // Skip the next cells above, since they are actually parts of the same cell.
                i += (rowSpan - 1);
              }
            } else {
              // The current row does not have a cell at column index.
            }
          }
          
          if (!intervals.isEmpty()) {
            // Create the arrays with the intervals.
            int[] startOffsets = new int[intervals.size()];
            int[] endOffsets = new int[intervals.size()];
            for (int i = 0; i < startOffsets.length; i++) {
              int[] interval = intervals.get(i);
              startOffsets[i] = interval[0];
              endOffsets[i] = interval[1];
            }
            // Delete the given intervals.
            documentController.multipleDelete(tableElem, startOffsets, endOffsets);
          }
          
          if(deletedColumnsIndices != null && deletedColumnsIndices.size() > 0) {
            // Update colspec for the deleted column (do not check if there is at least a cell deleted, because 
            // maybe we have deleted a column having no cell, but only cells that spans over)
            updateColspec(authorAccess, deletedColumnsIndices.get(deletedColumnsIndices.size() - 1));
            
            updateAppliableColWidthsNumber(
                authorAccess, 
                tableElem, 
                deletedColumnsIndices.get(deletedColumnsIndices.size() - 1));
          }
          
          // EXM-10373 Try to set the caret position in the row to a position adjacent to the deleted cell
          AuthorElement caretCell = tableAccess.getTableCellAt(rowIndex,
              deletedColumnsIndices.get(deletedColumnsIndices.size() - 1), tableElem);
          if (caretCell != null) {
            authorAccess.getEditorAccess().setCaretPosition(caretCell.getStartOffset()
                // EXM-18328 Try to set the caret position in the cell following the deleted cell
                + (placeCaretInNextCell ? 1 : 0));
          } else {
            // EXM-35813: Properly place the caret after delete the last column
            authorAccess.getEditorAccess().setCaretPosition(currentRowElem.getEndOffset() - 1);
          }
          
          handled = true;
        } else {
          // This operation is available only if the caret is positioned inside a table cell 
        }
      }
      
      if (authorAccess.getTableAccess().getTableNumberOfColumns(tableElem) == 0) {
        // If there are no more columns left, delete the residual table element
        AuthorNode tableElementForDeletion = tableHelper.getTableElementForDeletion(tableElem);
        if (tableElementForDeletion != null) {
          documentController.deleteNode(tableElementForDeletion);
          tableElem = null;
        }
      } else {
        // Delete any empty rows
        DeleteRowOperationBase deleteRowOperation = new DeleteRowOperationBase(tableHelper) {
          @Override
          protected SplitCellAboveBelowOperationBase createSplitCellOperation() {
            return null;
          }
        };
        int tableRowCount = tableAccess.getTableRowCount(tableElem);
        for (int i = tableRowCount - 1; i >= 0; i--) {
          AuthorElement tableRow = tableAccess.getTableRow(i, tableElem);
          if (tableRow != null && tableRow.getStartOffset() + 1 == tableRow.getEndOffset()) {
            deleteRowOperation.performDeleteRows(authorAccess, tableRow.getStartOffset(), tableRow.getEndOffset() + 1);
          }
        }
      }
      
      // Update the number of columns
      if (tableElem != null) {
        tableHelper.updateTableColumnNumber(
            authorAccess, 
            tableElem, 
            // subtract the number of deleted columns from the initial column number
            // to get the number of remaining columns
            initialColsNo - caretOffsetsForDeleteColumns.size());
      }
      
    } catch (BadLocationException e) {
      throw new AuthorOperationException(
          "The operation cannot be performed due to: " + e.getMessage(), e);
    }
    
    return handled;
  }

  /**
   * Compute the common columns of the previous cell and the current one.
   * 
   * @param commonCols    the common columns computed until now.
   * @param spanIndices   the span indices of the current cell.
   */
  private List<Integer> computeCommonCols(List<Integer> commonCols, int[] colSpanIndices) {
    List<Integer> aux = new ArrayList<Integer>();
    for (Integer col : commonCols) {
      if (col >= colSpanIndices[0] && col <= colSpanIndices[1]) {
        aux.add(col);
      }
    }
    return aux;
  }

  /**
   * Update the colspec of a table for a given column. 
   * 
   * @param authorAccess The Author access.
   * @param deletedColumnIndex The index of the deleted column.
   */
  protected void updateColspec(AuthorAccess authorAccess, Integer deletedColumnIndex) {
    // Nothing to do here
  }

  /**
   * If the table has anything else to update when a column is deleted...
   * 
   * @param authorAccess The author access.
   * @param tableElem    The table access.
   * @param deletedColumnIndex The deleted column index.
   */
  protected void updateAppliableColWidthsNumber(AuthorAccess authorAccess, AuthorElement tableElem, int deletedColumnIndex) {
    //Nothing to do here.
  }

  /**
   * Delete the table column at the caret position. 
   * For this operation the caret must be inside a table cell.
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.AbstractTableOperation#doOperationInternal(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  protected void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    List<ContentInterval> selectionIntervals = null;
    AuthorSelectionModel selectionModel = authorAccess.getEditorAccess().getAuthorSelectionModel();
    SelectionInterpretationMode mode = selectionModel.getSelectionInterpretationMode();

    // If there is a selection for which selection interpretation mode is table column, 
    // try to find a cell from the selected column that has no colspan specified.
    // That cell will represent the reference cell for delete operation.
    if (mode == SelectionInterpretationMode.TABLE_COLUMN) {
      // Try to delete the selected columns
      selectionIntervals = selectionModel.getSelectionIntervals();
    }    
    performDeleteColumn(authorAccess, selectionIntervals, true);
  }

  /**
   * Update the column span for the table cell that is included into the deleted
   * column.
   * 
   * @param authorAccess  The author access.
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param spanProvider  The table span provider.
   * The object responsible for providing information 
   * about the cell spanning.
   * @param cell          The table cell.
   * @param colStartIndex The new column start index, 1 based.
   * @param colEndIndex   The new column end index, 1 based.
   * 
   * @throws AuthorOperationException When the operation fails.
   */
  protected abstract void updateTableColSpan(
      AuthorAccess authorAccess,
      AuthorTableCellSpanProvider spanProvider, 
      AuthorElement cell, 
      int colStartIndex, 
      int colEndIndex) throws AuthorOperationException;

  /**
   * No arguments for this operation.
   *  
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Delete the current table column.";
  }
  
  /**
   * @return <code>true</code> if a column from the specified table can be deleted. 
   * <code>false</code> otherwise.
   */
  protected boolean canDeleteColumn() {
    return true;
  }
}