/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2015 Syncro Soft SRL, Romania.  All rights
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
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.access.AuthorTableAccess;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.properties.TableHelperConstants;

/**
 * Operation for joining the content of selected cells.
 */

public abstract class JoinOperationBase extends AbstractTableOperation {
  /**
   * Cursor outlide the table error message
   */
  public static final String CURSOR_OUTSIDE_THE_TABLE_ERROR_MESSAGE = 
      "Please place the cursor inside the table before invoking the Join operation.";
  /**
   * Select at least two adjacent cells
   */
  public static final String SELECT_AT_LEAST_TWO_ADJACENT_CELLS_ERROR_MESSAGE = 
      "Select at least two adjacent cells before invoking the Join operation.";
  /**
   * Rectangular selection error message
   */
  public static final String RECTANGULAR_SELECTIONS_ERROR_MESSAGE = 
      "Select a rectangular section of the table before invoking the Join operation.";
  /**
   * Separator between column and row indexes for a cell
   */
  private static final String ROW_COL_SEPARATOR = "-";

  /**
   * Constructor.
   * 
   * @param tableHelper Table helper with methods specific to a document type.
   */
  public JoinOperationBase(AuthorTableHelper tableHelper) {
    super(tableHelper);
  }
  
  /**
   * Information about a group of cells that must be joined 
   */
  private static class JoinGroupInformation {
    /**
     * Group start row
     */
    private int startRow = -1;
    /**
     * Group end row
     */
    private int endRow = -1;
    
    /**
     * Group start column
     */
    private int startColumn = -1;
    /**
     * Group end column
     */
    private int endColumn = -1;
    /**
     * The map between cell indexes and the corresponding node
     */
    private Map<String, AuthorNode> groupMap = new HashMap<String, AuthorNode>();
  }

  /**
   * Join the contents of selected cells.
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.AbstractTableOperation#doOperationInternal(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  protected void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args) throws IllegalArgumentException, 
    AuthorOperationException {
    try {
      if(authorAccess.getEditorAccess().hasSelection()) {
        // Determine the table element at caret 
        AuthorElement tableElement = getElementAncestor(authorAccess.getDocumentController().getNodeAtOffset(
            authorAccess.getEditorAccess().getCaretOffset()), AuthorTableHelper.TYPE_TABLE);

        if (tableElement != null) {
          // Check the selection if it is inside a table
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

          List<AuthorElement> cellElements = TableOperationsUtil.getTableElementsOfType(
              authorAccess, selections, TableHelperConstants.TYPE_CELL, TableOperationsUtil.createTableHelper(tableHelper));

          if (cellElements != null && cellElements.size() > 1) {
            joinCells(authorAccess, tableElement, cellElements);
          } else {
            AuthorOperationException ex = new AuthorOperationException(SELECT_AT_LEAST_TWO_ADJACENT_CELLS_ERROR_MESSAGE);
            ex.setOperationRejectedOnPurpose(true);
            throw ex;
          }
        } else {
          AuthorOperationException ex = new AuthorOperationException(CURSOR_OUTSIDE_THE_TABLE_ERROR_MESSAGE);
          ex.setOperationRejectedOnPurpose(true);
          throw ex;
        }
      } else {
        AuthorOperationException ex = new AuthorOperationException(SELECT_AT_LEAST_TWO_ADJACENT_CELLS_ERROR_MESSAGE);
        ex.setOperationRejectedOnPurpose(true);
        throw ex;
      }
    } catch (BadLocationException e) {
      AuthorOperationException ex = new AuthorOperationException(CURSOR_OUTSIDE_THE_TABLE_ERROR_MESSAGE);
      ex.setOperationRejectedOnPurpose(true);
      throw ex;
    }
  }

  /**
   * Join table cells.
   * 
   * @param authorAccess The author access.
   * @param tableElement The table element.
   * @param cellElements The cells fragments.
   * 
   * @throws AuthorOperationException
   * @throws BadLocationException
   */
  public void joinCells(AuthorAccess authorAccess, AuthorElement tableElement,
      List<AuthorElement> cellElements) throws AuthorOperationException, BadLocationException {
    Map<String, AuthorNode> mapCellToNode = new HashMap<String, AuthorNode>();
    
    AuthorTableAccess tableAccess = authorAccess.getTableAccess();
    for (AuthorElement cellElement : cellElements) {
      int[] tableRowSpanIndices = tableAccess.getTableRowSpanIndices(cellElement);
      int[] tableColSpanIndices = tableAccess.getTableColSpanIndices(cellElement);
      // Rows
      int startRow = tableRowSpanIndices[0];
      int endRow = tableRowSpanIndices[1];
      // Columns
      int startColumn = tableColSpanIndices[0];
      int endColumn = tableColSpanIndices[1];
      // Map the selected cells to this node
      for (int i = startRow; i <= endRow; i++) {
        for (int j = startColumn; j <= endColumn; j++) {
          mapCellToNode.put(i + ROW_COL_SEPARATOR + j, cellElement);
        }
      }
    }
    
    int columnsCount = tableAccess.getTableNumberOfColumns(tableElement);
    int rowsCount = tableAccess.getTableRowCount(tableElement);
    
    List<JoinGroupInformation> allGroupsToJoin = new ArrayList<JoinGroupInformation>();
    
    for (int i = 0; i < rowsCount; i++) {
      for (int j = 0; j < columnsCount; j++) {
        int[] currentCell = new int[] {i, j};
        String cellRepresentation = currentCell[0] + ROW_COL_SEPARATOR + currentCell[1];
        // Check if this cell is selected
        AuthorNode authorNode = mapCellToNode.remove(cellRepresentation);
        if (authorNode != null) {
          // This cell is selected
          Map<String, AuthorNode> currentGroupToJoin = new HashMap<String, AuthorNode>();
          JoinGroupInformation groupInformation = new JoinGroupInformation();
          groupInformation.startRow = i;
          groupInformation.startColumn = j;
          groupInformation.endRow = i;
          groupInformation.endColumn = j;
          groupInformation.groupMap = currentGroupToJoin;
          populateJoinGroupStartingFrom(groupInformation, 
              authorNode, cellRepresentation, mapCellToNode);
          if (currentGroupToJoin.size() > 1) {
            // Check if the selection is rectangular
            boolean rectangular = true;
            for (int k = groupInformation.startRow; k <= groupInformation.endRow; k++) {
              for (int l = groupInformation.startColumn; l <= groupInformation.endColumn; l++) {
                if (!currentGroupToJoin.containsKey(k + ROW_COL_SEPARATOR + l)) {
                  rectangular = false;
                  break;
                }
              }
            }
            
            if (rectangular) {
              allGroupsToJoin.add(groupInformation);
            } else {
              AuthorOperationException ex = new AuthorOperationException(RECTANGULAR_SELECTIONS_ERROR_MESSAGE);
              ex.setOperationRejectedOnPurpose(true);
              throw ex;
            }
          } else {
            // Single cell group, ignore
          }
        } 
      }
    }
    
    AuthorTableCellSpanProvider tableSupport = tableHelper.getTableCellSpanProvider(tableElement);
    
    // If empty rows result after join, they will be removed
    List<AuthorElement> tableRowsToRemove = null;
    Position nextSelectionStartOffset = null;
    Position nextSelectionEndOffset = null;
    try {
      authorAccess.getDocumentController().disableLayoutUpdate();

      // If the table does not have the column specifications ask the user to generate them.
      if (!tableSupport.hasColumnSpecifications(tableElement) 
          && authorAccess.getWorkspaceAccess().showConfirmDialog(
              "Column specifications", 
              "Join cells operation requires column specifications. Do you want to generate them?", 
              new String[] {"Yes", "No"}, 
              new int[] {1, 0}) == 1) {
        generateColumnSpecifications(authorAccess, tableSupport, tableElement);
      }
      
      // Join cells
      int size = allGroupsToJoin.size();
      if (size == 0) {
        AuthorOperationException ex = new AuthorOperationException(SELECT_AT_LEAST_TWO_ADJACENT_CELLS_ERROR_MESSAGE);
        ex.setOperationRejectedOnPurpose(true);
        throw ex;
      }
      
      for (int i = size - 1; i >= 0; i--) {
        JoinGroupInformation joinGroupInformation = allGroupsToJoin.get(i);

        Map<String, AuthorNode> groupMap = joinGroupInformation.groupMap;
        // The table cell where the content of the source cells will be moved
        int destinationRow = joinGroupInformation.startRow;
        int destinationColumn = joinGroupInformation.startColumn;
        AuthorNode destinationCell = groupMap.get(destinationRow + ROW_COL_SEPARATOR + destinationColumn);
        AuthorNode lastCellOnFirstRow = groupMap.get(joinGroupInformation.startRow + ROW_COL_SEPARATOR + 
            joinGroupInformation.endColumn);
        if (destinationCell instanceof AuthorElement && lastCellOnFirstRow instanceof AuthorElement) {
          // Test if the span specifications for first and last cells are defined
          tableHelper.checkTableColSpanIsDefined(authorAccess, tableSupport, (AuthorElement) destinationCell); 
          tableHelper.checkTableColSpanIsDefined(authorAccess, tableSupport, (AuthorElement) lastCellOnFirstRow);

          // Update the column span
          tableHelper.updateTableColSpan(
              authorAccess, 
              tableSupport, 
              (AuthorElement) destinationCell, 
              destinationColumn + 1,
              joinGroupInformation.endColumn + 1);

          // Update the row span
          tableHelper.updateTableRowSpan(
              authorAccess, 
              (AuthorElement) destinationCell, 
              joinGroupInformation.endRow - joinGroupInformation.startRow + 1);

          // Remove joined cells
          // Move the content of the source cells to the destination cell
          for (int k = joinGroupInformation.startRow; k <= joinGroupInformation.endRow; k++) {
            for (int l = joinGroupInformation.startColumn; l <= joinGroupInformation.endColumn; l++) {
              AuthorNode cellNode = groupMap.get(k + ROW_COL_SEPARATOR + l);
              if (cellNode instanceof AuthorElement && cellNode != destinationCell) {
                AuthorElement cell = (AuthorElement) cellNode;
                // Copy the content of the joined cells
                AuthorDocumentFragment fragmentToMove = null;
                if (cell.getEndOffset() - cell.getStartOffset() > 1) {
                  fragmentToMove = authorAccess.getDocumentController().createDocumentFragment(
                      cell.getStartOffset() + 1, cell.getEndOffset() - 1);
                } else {
                  // Empty table cell
                }

                AuthorNode deletedCellParentRow = cell.getParent();
                
                // Delete the content of the copied cells 
                authorAccess.getDocumentController().deleteNode(cell);
                
                // Check if an empty row remains
                if (deletedCellParentRow instanceof AuthorElement) {
                  if (deletedCellParentRow.getStartOffset() + 1 == deletedCellParentRow.getEndOffset()) {
                    if (tableRowsToRemove == null) {
                      tableRowsToRemove = new ArrayList<AuthorElement>(1);
                    }
                    tableRowsToRemove.add((AuthorElement) deletedCellParentRow);
                  }
                }
                
                if (fragmentToMove != null) {
                  authorAccess.getDocumentController().insertFragment(
                      destinationCell.getEndOffset(), fragmentToMove);
                }
              }
            }
          }
          // Select the destination cell
          int startOffset = destinationCell.getStartOffset();
          int endOffset = destinationCell.getEndOffset();
          authorAccess.getEditorAccess().select(startOffset, endOffset + 1);
          // Create positions
          nextSelectionStartOffset = authorAccess.getDocumentController().createPositionInContent(startOffset);
          nextSelectionEndOffset = authorAccess.getDocumentController().createPositionInContent(endOffset  +1);
        }
      }
      
    } finally {
      authorAccess.getDocumentController().enableLayoutUpdate(tableElement);
    }
    if(tableRowsToRemove != null) {
      //EXM-23327 If we are removing the last cell on the row then we must also remove the row.
      DeleteRowOperationBase deleteRowOperation = new DeleteRowOperationBase(tableHelper) {
        @Override
        protected SplitCellAboveBelowOperationBase createSplitCellOperation() {
          return null;
        }
      };
      
      for (int j = tableRowsToRemove.size() - 1; j >= 0; j--) {
        AuthorElement tableRowToRemove = tableRowsToRemove.get(j);
        deleteRowOperation.performDeleteRows(authorAccess, tableRowToRemove.getStartOffset(), 
            tableRowToRemove.getEndOffset() + 1);
      }
      if (nextSelectionStartOffset != null && nextSelectionEndOffset != null) {
        // Select the destination cell
        authorAccess.getEditorAccess().select(nextSelectionStartOffset.getOffset(), 
            nextSelectionEndOffset.getOffset());
      }
    }
  }

  /**
   * Populate join cells group starting from a given cell.
   * 
   * @param groupInformation  Information about group start and end row and column.
   * @param authorNode The cell node.
   * @param cellRepresentation Cell representation (like column_number-row_number)
   * @param mapCellToNode The map between cell representations and nodes
   * @throws AuthorOperationException 
   */
  private void populateJoinGroupStartingFrom(
      JoinGroupInformation groupInformation, AuthorNode authorNode, 
      String cellRepresentation, Map<String, AuthorNode> mapCellToNode) throws AuthorOperationException {
    // Add it to the group
    groupInformation.groupMap.put(cellRepresentation, authorNode);
    int sepIndex = cellRepresentation.indexOf(ROW_COL_SEPARATOR);
    try {
      int row = Integer.parseInt(cellRepresentation.substring(0, sepIndex));
      int column = Integer.parseInt(cellRepresentation.substring(sepIndex + 1, cellRepresentation.length()));
      
      // If left cell is selected it means that the group selection is not rectangular, stop.
      if (column > 0) {
        int leftCellColumn = column - 1;
        String leftColumnRepresentation = row + ROW_COL_SEPARATOR + leftCellColumn;
        AuthorNode leftCell = mapCellToNode.remove(leftColumnRepresentation);
        if (leftCell != null) {
          AuthorOperationException ex = new AuthorOperationException(RECTANGULAR_SELECTIONS_ERROR_MESSAGE);
          ex.setOperationRejectedOnPurpose(true);
          throw ex;
        }
      }
      
      // Check the right and south cells
      // South cell
      int southCellRow = row + 1;
      String southCellRepresentation = southCellRow + ROW_COL_SEPARATOR + column;
      AuthorNode southCell = mapCellToNode.remove(southCellRepresentation);
      if (southCell != null) {
        // Determine group end row
        groupInformation.endRow = Math.max(groupInformation.endRow, southCellRow);
        // Populate the join group starting from south cell
        populateJoinGroupStartingFrom(groupInformation, southCell, southCellRepresentation, mapCellToNode);
      }
      
      // Right cell
      int rightCellColumn = column + 1;
      String rightCellRepresentation = row + ROW_COL_SEPARATOR + rightCellColumn;
      AuthorNode rightCell = mapCellToNode.remove(rightCellRepresentation);
      if (rightCell != null) {
        // Determine group end column
        groupInformation.endColumn = Math.max(groupInformation.endColumn, rightCellColumn);
        // Populate the join group starting from right cell
        populateJoinGroupStartingFrom(groupInformation, rightCell, rightCellRepresentation, mapCellToNode);
      }
      
      
    } catch (NumberFormatException e) {
      // Nothing to do...
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Join the content of the selected cells.";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return null;
  }
  
  
  /**
   * Generates column specifications for the given table and inserts them 
   * into the document.
   * 
   * @param authorAccess Author access. 
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param tableCellSpanProvider Table cell span provider.
   * @param tableElement The table element.
   * 
   * @throws AuthorOperationException Failed to insert the column specifications into the table.
   */
  protected abstract void generateColumnSpecifications(
      AuthorAccess authorAccess, 
      AuthorTableCellSpanProvider tableCellSpanProvider, 
      AuthorElement tableElement) throws AuthorOperationException;
}