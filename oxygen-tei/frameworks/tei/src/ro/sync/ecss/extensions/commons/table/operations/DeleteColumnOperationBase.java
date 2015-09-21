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
import java.util.Iterator;
import java.util.List;

import javax.swing.text.BadLocationException;




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSelectionModel;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.SelectionInterpretationMode;
import ro.sync.ecss.extensions.api.access.AuthorTableAccess;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Base implementation for operations used to delete a table column.
 */

public abstract class DeleteColumnOperationBase extends AbstractTableOperation {
  
  /**
   * The table element.
   */
  protected AuthorElement tableElem = null;
  
  /**
   * The index of the deleted column. 
   */
  protected int deletedColumnIndex = -1;

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
   * Delete table column.
   * 
   * @param authorAccess The access to Author operations.
   * @param columnIntervals The intervals of the column to be deleted.
   * If <code>null</code>, the column at caret offset is deleted.
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
    int deletedRowIndex = -1;
    deletedColumnIndex = -1;

    // Test if the operation is available for the node at the caret position 
    try {

      AuthorTableAccess tableAccess = authorAccess.getTableAccess();
      if (columnIntervals != null && columnIntervals.size() > 0) {
        ContentInterval firstInterval = columnIntervals.get(0);
        tableElem = getElementAncestor(
            authorAccess.getDocumentController().getNodeAtOffset(firstInterval.getStartOffset()),
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
                    Integer colSpan = spanProvider.getColSpan(currentCell);
                    // Determine if the current cell has colspan
                    if (colSpan != null && colSpan > 1) {
                      selectionIterator.remove();
                      break;
                    } else {
                      // The cell does not have any colspan, we can use it as a reference for delete operation
                      authorAccess.getEditorAccess().setCaretPosition(currentCell.getStartOffset() + 1);
                      break loop;
                    }
                  }
                }
              }
            }
          }
        }
      } 
      // Look at caret offset
      AuthorNode nodeAtCaret = 
        authorAccess.getDocumentController().getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset());
      AuthorElement currentCellElem = 
        getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_CELL);
      AuthorElement currentRowElem = 
        getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_ROW);
      tableElem = 
        getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_TABLE);

      if (currentCellElem != null && tableElem != null) {
        // Determine the index of column to be deleted
        int[] cellIndices = tableAccess.getTableCellIndex(currentCellElem);
        deletedRowIndex = cellIndices[0];
        deletedColumnIndex = cellIndices[1];

        List<int[]> intervals = new ArrayList<int[]>();
        // For all the table rows delete the cell at the column index
        int rowsNumber = tableAccess.getTableRowCount(tableElem);
        int colsNumber = tableAccess.getTableNumberOfColumns(tableElem);
        AuthorTableCellSpanProvider spanProvider = 
          tableHelper.getTableCellSpanProvider(tableElem);
        for (int i = 0; i < rowsNumber; i++) {
          AuthorElement cell = tableAccess.getTableCellAt(i, deletedColumnIndex, tableElem);
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
          authorAccess.getDocumentController().multipleDelete(tableElem, startOffsets, endOffsets);
        }

        tableHelper.updateTableColumnNumber(
            authorAccess, 
            tableElem, 
            colsNumber - 1);
        
        if(deletedColumnIndex != -1) {
          updateAppliableColWidthsNumber(
              authorAccess, 
              tableElem, 
              deletedColumnIndex);
        }

        // EXM-10373 Try to set the caret position in the row to a position adjacent to the deleted cell
        AuthorElement caretCell = tableAccess.getTableCellAt(deletedRowIndex, deletedColumnIndex, tableElem);
        if (caretCell != null) {
          authorAccess.getEditorAccess().setCaretPosition(caretCell.getStartOffset()
              // EXM-18328 Try to set the caret position in the cell following the deleted cell
              + (placeCaretInNextCell ? 1 : 0));
        } else {
          authorAccess.getEditorAccess().setCaretPosition(currentRowElem.getEndOffset());
        }

        handled = true;
      } else {
        // This operation is available only if the caret is positioned inside a table cell 
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException(
          "The operation cannot be performed due to: " + e.getMessage(), e);
    }

    return handled;
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
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public final void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
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