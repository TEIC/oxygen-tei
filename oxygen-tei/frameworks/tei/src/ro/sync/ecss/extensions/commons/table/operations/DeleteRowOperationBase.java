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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.basic.util.Equaler;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.access.AuthorTableAccess;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.properties.TableHelperConstants;

/**
 * Operation used to delete table rows. If there is a selection in the table all the rows that intersect
 * that selection are removed. If there is no selection in the table, the row at caret is deleted.  
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class DeleteRowOperationBase extends AbstractTableOperation {
  
  /**
   * Constructor.
   * 
   * @param documentTypeHelper The table helper specific to a document type. 
   * An implementation of {@link ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper}.
   */
  public DeleteRowOperationBase(AuthorTableHelper documentTypeHelper) {
    super(documentTypeHelper);
  }
  
  /**
   * Delete table rows.
   * The rows that must be deleted are determined in the following order:
   * <ul>
   * <li>by the list of content intervals if not <code>null</code></li>
   * <li>all the rows that intersect the selection</li>
   * <li>the row at caret offset</li>
   * </ul> 
   * 
   * @param authorAccess The access to Author operations.
   * @param contentIntervals The content intervals that intersects the rows that must be deleted.
   * Each interval contains two integers, one for start interval offset and one for end interval offset. 
   * @return <code>true</code> if the rows are deleted.
   * 
   * @throws AuthorOperationException
   */
  public boolean performDeleteRows(AuthorAccess authorAccess, List<ContentInterval> contentIntervals)
      throws AuthorOperationException {
    boolean handled = false;
    try {
      AuthorDocumentController authorDocumentController = authorAccess.getDocumentController();
      AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
      // The table at caret
      boolean rowsFromContentIntervals = contentIntervals != null && !contentIntervals.isEmpty();
      AuthorNode nodeAtCaret = authorDocumentController.getNodeAtOffset(
          rowsFromContentIntervals ? contentIntervals.get(0).getStartOffset() : 
            authorAccess.getEditorAccess().getCaretOffset());
      AuthorElement tableElement = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_TABLE); 

      if (tableElement != null) {
        // The rows to be deleted
        List<AuthorElement> selectedRows = new ArrayList<AuthorElement>(1);

        // Determine the rows that must be deleted
        if (rowsFromContentIntervals) {
          // Attempt 1: Check if we can determine the row to be deleted
          // from the given content intervals
          
          List<Integer[]> intervals = new ArrayList<Integer[]>(contentIntervals.size());
          for (ContentInterval cInterval : contentIntervals) {
            intervals.add(new Integer[] {cInterval.getStartOffset(), cInterval.getEndOffset()});
          }
          selectedRows = TableOperationsUtil.getTableElementsOfType(
              authorAccess,
              intervals,
              TableHelperConstants.TYPE_ROW,
              TableOperationsUtil.createTableHelper(tableHelper));
        } else {
          if(authorEditorAccess.hasSelection()) {
            // Attempt 2: Determine all the rows that intersect the selection, to be deleted
            selectedRows = TableOperationsUtil.getTableElementsOfTypeFromSelection(
                authorAccess,
                TableHelperConstants.TYPE_ROW,
                TableOperationsUtil.createTableHelper(tableHelper),
                tableElement);
          } else {
            // Attempt 3: Determine the row at caret
            selectedRows.add(getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_ROW));
          }
        }

        // Check if there are rows to be deleted
        if (!selectedRows.isEmpty()) {
          AuthorTableAccess authorTableAccess = authorAccess.getTableAccess();
          if (selectedRows.size() == authorTableAccess.getTableRowCount(tableElement)) {
            // If all the rows are to be deleted, then delete the entire table element
            
            // We have to distinguish between the cases when our tableElement variable
            // is the "table" element itself and those when it actually is some other element,
            // such as "tgroup" in CALS tables
            AuthorNode tableElementForDeletion = tableHelper.getTableElementForDeletion(tableElement);
            if (tableElementForDeletion != null) {
              authorDocumentController.deleteNode(tableElementForDeletion);
            }
          } else {
            // The last selected row (the user can select multiple rows to delete)
            AuthorElement lastSelectedRow = selectedRows.get(selectedRows.size() - 1);
            List<Integer> rowsToBeDeletedIndices = new ArrayList<Integer>();
            int tableRowsCount = authorTableAccess.getTableRowCount(tableElement);
            // The closest unselected (i.e. which won't be deleted) row
            // from before the last selected one
            AuthorElement beforeElement = null;
            // The closest unselected (i.e. which won't be deleted) row 
            // from after the last selected one
            AuthorElement afterElement = null;
            boolean foundLastSelElement = false;
            for (int i = 0; i < tableRowsCount; i++) {
              AuthorElement currentRow = authorTableAccess.getTableRow(i, tableElement);
              if (selectedRows.contains(currentRow)) {
                // Add the indices of the rows to be deleted (in ascending order) 
                rowsToBeDeletedIndices.add(i);
                
                if (Equaler.verifyEquals(lastSelectedRow, currentRow)) {
                  foundLastSelElement = true;
                }
              } else {
                // Try to find the closest unselected/undeleted row, in order to 
                // place the caret in it at the end of the operation
                if (!foundLastSelElement) {
                  beforeElement = currentRow;
                } else if (afterElement == null){
                  afterElement = currentRow;
                }
              }
            }

            // Create the position used to set the caret 
            // in the proper place after the operation ends 
            Position caretPos = null;
            if (afterElement != null) {
              // first try to set the caret in the first undeleted row
              // from after the last deleted row (the row which was selected
              // last and where the caret was blinking before invoking the action)
              caretPos = authorDocumentController.createPositionInContent(afterElement.getStartOffset() + 1);
            } else if (beforeElement != null){
              // now try to set the caret in the first undeleted row
              // from before the last deleted row (the row which was selected
              // last and where the caret was blinking before invoking the action)
              caretPos = authorDocumentController.createPositionInContent(beforeElement.getStartOffset() + 1);
            }

            Map<AuthorElement, Integer> rowspans = new HashMap<AuthorElement, Integer>();
            int deleteRowsCount = rowsToBeDeletedIndices.size();
            int currentDeleteRowIndex = -1;
            int tableColumnsCount = authorTableAccess.getTableNumberOfColumns(tableElement);
            // A list with all the rows to be deleted
            List<AuthorElement> rowsToDelete = new ArrayList<AuthorElement>();
            AuthorTableCellSpanProvider spanProvider = tableHelper.getTableCellSpanProvider(tableElement);
            for (int j = 0; j < deleteRowsCount; j++) {
              currentDeleteRowIndex = rowsToBeDeletedIndices.get(j);
              SplitCellAboveBelowOperationBase splitCellOp = null;
              // Update the 'rowspan' for the row cells that span more that one row
              for (int i = tableColumnsCount - 1; i >= 0; i--) {
                AuthorElement cell = authorTableAccess.getTableCellAt(currentDeleteRowIndex, i, tableElement);
                if (cell != null) {
                  int[] indices = authorTableAccess.getTableCellIndex(cell);
                  Integer colSpanInteger = spanProvider.getColSpan(cell);
                  Integer rowSpanInteger = spanProvider.getRowSpan(cell);
                  int colSpan = colSpanInteger != null ? colSpanInteger.intValue() : 1;
                  int rowSpan = rowSpanInteger != null ? rowSpanInteger.intValue() : 1;
                  if (rowSpan > 1) {
                    if (indices[0] == currentDeleteRowIndex) {
                      // Split the cell before deleting it.
                      if (splitCellOp == null) {
                        splitCellOp = createSplitCellOperation(); 
                      }
                      splitCellOp.splitCell(cell, authorAccess, true);
                    } else {
                      // Decrease the row span of the cell with one
                      // The actual update will be performed later
                      Integer currentSpan = rowspans.get(cell);
                      if (currentSpan == null) {
                        // First decrease the span based on the cell attributes
                        rowspans.put(cell, rowSpan - 1);
                      } else {
                        // After the first rowspan decrease, use the value from the map
                        rowspans.put(cell, currentSpan - 1);
                      }
                    }
                  }
                  if (colSpan > 1) {
                    // Skip the next cells on the left, since they are actually parts of the same cell.
                    i -= (colSpan - 1);
                  }
                } else {
                  // The current row does not have a cell at column index.
                }
              }

              AuthorElement tableRow = authorTableAccess.getTableRow(rowsToBeDeletedIndices.get(j), tableElement);
              rowsToDelete.add(tableRow);
              // If we delete all the children of an element (tbody, thead, tfoot), 
              // then also delete the parent which would otherwise remain empty.
              // To do this, also add the parent to the list of elements to be delted
              if (rowsToDelete.containsAll(tableRow.getParentElement().getContentNodes())) {
                rowsToDelete.add(0, (AuthorElement) tableRow.getParentElement());
                rowsToDelete.removeAll(tableRow.getParentElement().getContentNodes());
              }
            }
            
            // Update row spans
            Set<AuthorElement> keySet = rowspans.keySet();
            for (AuthorElement cell : keySet) {
              tableHelper.updateTableRowSpan(authorAccess, cell, rowspans.get(cell));
            }
            
            // Sort in document order
            Collections.sort(rowsToDelete, new Comparator<AuthorElement>() {
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
            int[] startOffsets = new int[rowsToDelete.size()];
            int[] endOffsets = new int[rowsToDelete.size()];
            int n = rowsToDelete.size();
            for (int j = 0; j < n; j++) {
              startOffsets[j] = rowsToDelete.get(j).getStartOffset();
              endOffsets[j] = rowsToDelete.get(j).getEndOffset();
            }

            // Multiple delete
            authorAccess.getDocumentController().multipleDelete(tableElement, 
                startOffsets, 
                endOffsets);
            
            handled = true;

            // Update the number of rows
            tableHelper.updateTableRowNumber(authorAccess, tableElement, -rowsToBeDeletedIndices.size());

            // Place the caret inside the proper remaining row, if there is such a row
            int remainingRowsCount = authorTableAccess.getTableRowCount(tableElement);
            if (remainingRowsCount > 0) {
              if (caretPos != null) {
                authorEditorAccess.setCaretPosition(caretPos.getOffset());
              } else {
                // Fallback (just in case)
                AuthorElement tableRow = authorTableAccess.getTableRow(0, tableElement);
                if (tableRow != null) {
                  authorEditorAccess.setCaretPosition(tableRow.getStartOffset() + 1);
                }
              }
            }
          }
        }
      } else {
        throw new AuthorOperationException("The caret must be inside a table");
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException(
          "The operation cannot be performed due to: " + e.getMessage(), e);
    }

    return handled;
  }

  /**
   * Delete table rows.
   * The row that must be deleted is determined in the following order:
   * <ul>
   * <li>by startRowOffset and endRowOffset if both are bigger than <code>0</code></li>
   * <li>all the rows that intersect the selection</li>
   * <li>the row at caret offset</li>
   * </ul> 
   * 
   * @param authorAccess The access to Author operations.
   * @param startRowOffset The start row offset. 
   * @param endRowOffset The end row offset.
   * @return <code>true</code> if at least one row is deleted.
   * 
   * @throws IllegalArgumentException
   * @throws AuthorOperationException
   */
  public boolean performDeleteRows(AuthorAccess authorAccess, int startRowOffset, int endRowOffset)
      throws AuthorOperationException {
    List<ContentInterval> contentIntervals = null;
    if (startRowOffset >= 0 && endRowOffset >= 0) {
      contentIntervals = new ArrayList<ContentInterval>(1);
      contentIntervals.add(new ContentInterval(startRowOffset, endRowOffset));
    }
    return performDeleteRows(authorAccess, contentIntervals);
  }
  
  /**
   * Delete the table rows. For this operation the caret must be inside a table cell.
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.AbstractTableOperation#doOperationInternal(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public final void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    performDeleteRows(authorAccess, -1, -1);
  }

  /**
   * No arguments for this operation.
   *  
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[] {
        CHANGE_TRACKING_BEHAVIOR_ARGUMENT
    };
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Delete table rows. If there is a selection in the table all the rows that intersect that selection are removed."
        + " If there is no selection in the table, the row at caret is deleted.";
  }
  
  /**
   * Create the split cell operation. 
   * The operation is needed to split the cells that span over multiple rows and start on the row to be deleted.
   * 
   * @return The split cell operation.
   */
  protected abstract SplitCellAboveBelowOperationBase createSplitCellOperation();
}