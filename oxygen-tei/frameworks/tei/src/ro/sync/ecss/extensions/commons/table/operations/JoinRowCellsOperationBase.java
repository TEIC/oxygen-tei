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
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Operation used to join the content of two or more cells from a table row.
 * If there is a selection, the cell at selection start offset determines the destination
 * cell where the content of the next cells will be moved. If there is no selection then
 * it is assumed that the caret is between two table cells.
 */

public abstract class JoinRowCellsOperationBase extends AbstractTableOperation {
  
  /**
   * Constructor.
   * 
   * @param tableHelper Table helper with methods specific to a document type.
   */
  public JoinRowCellsOperationBase(AuthorTableHelper tableHelper) {
    super(tableHelper);
  }

  /**
   *  Join the contents of one or more cells.
   *  
   * @see ro.sync.ecss.extensions.commons.table.operations.AbstractTableOperation#doOperationInternal(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  protected void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args)
  throws IllegalArgumentException, AuthorOperationException {
    try {
      // The table cell where where the content of the joined cells will be moved
      AuthorElement firstCell = null;
      // The last cell involved in the join operation
      AuthorElement lastCell = null;

      if(authorAccess.getEditorAccess().hasSelection()) {
        int start = authorAccess.getEditorAccess().getSelectionStart();
        int end = authorAccess.getEditorAccess().getSelectionEnd();
        AuthorNode fullySelectedNode = authorAccess.getEditorAccess().getFullySelectedNode();
        if (fullySelectedNode != null && 
            // Not empty row
            fullySelectedNode.getStartOffset() < fullySelectedNode.getEndOffset() &&
            tableHelper.isTableRow(fullySelectedNode)) {
          start++;
          end--;
        }
        firstCell = getCell(authorAccess, start, true);
        lastCell = getCell(authorAccess, end, false);        
      } else {
        AuthorNode nodeAtCaret = 
          authorAccess.getDocumentController().getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset());
        if(tableHelper.isTableRow(nodeAtCaret)) {
          // Caret is inside a table row
          AuthorNode nodeBeforeCaret = 
            authorAccess.getDocumentController().getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset() - 1);
          if(tableHelper.isTableCell(nodeBeforeCaret)) {
            firstCell = (AuthorElement) nodeBeforeCaret;
          } else {
            // There is no cell before the caret, the operation is not possible
          }
          
          AuthorNode nodeAfterCaret = 
            authorAccess.getDocumentController().getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset() + 1);
          if(tableHelper.isTableCell(nodeAfterCaret)) {
            lastCell = (AuthorElement) nodeAfterCaret;
          } else {
            // There is no cell after the caret, the operation is not possible
          }
        } else {
          // The caret is not between two cells, the operation is not possible
        }
      }
      
      if(firstCell != null && lastCell != null && firstCell != lastCell) {
        // The cells must be inside the same row
        AuthorElement startSelTableRow = (AuthorElement) firstCell.getParent();
        AuthorElement endSelTableRow = (AuthorElement) lastCell.getParent();        
        // Test if the cells have the same parent row
        if(startSelTableRow == endSelTableRow) {
          // The tgroup element
          AuthorElement tgroupElement = getElementAncestor(
              startSelTableRow, AuthorTableHelper.TYPE_TABLE);
          if (tgroupElement != null) {
            // Create the table support
            AuthorTableCellSpanProvider tableSupport = 
              tableHelper.getTableCellSpanProvider(tgroupElement);

            // If the table does not have the column specifications ask the user to generate them.
            if (!tableSupport.hasColumnSpecifications(tgroupElement) 
                && authorAccess.getWorkspaceAccess().showConfirmDialog(
                    "Column specifications", 
                    "Join cells operation requires column specifications. Do you want to generate them?", 
                    new String[] {"Yes", "No"}, 
                    new int[] {1, 0}) == 1) {
              generateColumnSpecifications(authorAccess, tableSupport, tgroupElement);
            }
            
            // Test if the span specifications for first and last cells are defined
            tableHelper.checkTableColSpanIsDefined(authorAccess, tableSupport, firstCell); 
            tableHelper.checkTableColSpanIsDefined(authorAccess, tableSupport, lastCell);
            
            // Source cells. Their content will be moved into the content of the first cell
            List sourceCells = getCellsBetweenOffsets(startSelTableRow,
                firstCell.getEndOffset() + 1, lastCell.getEndOffset());

            // The table cell where the content of the source cells will be moved
            AuthorElement destinationTableCell = firstCell;

            // Verify the row span for all cells to be moved. It must be the same.
            checkForSameRowSpan(tableSupport, destinationTableCell, sourceCells);
            
            // Update column span of the destination cell
            updateColSpanForMergedCell(
                authorAccess,
                tableSupport,
                destinationTableCell,
                sourceCells);

            // Move the content of the source cells to the destination cell
            int nodesCount = sourceCells.size();
            for (int i = 0 ; i < nodesCount; i++) {
              AuthorElement cell = (AuthorElement) sourceCells.get(i);
              // Copy the content of the joined cells
              AuthorDocumentFragment fragmentToMove = null;
              if (cell.getEndOffset() - cell.getStartOffset() > 1) {
                fragmentToMove = authorAccess.getDocumentController().createDocumentFragment(
                    cell.getStartOffset() + 1, cell.getEndOffset() - 1);
              } else {
                // Empty table cell
              }

              // Delete the content of the copied cells 
              authorAccess.getDocumentController().deleteNode(cell);
              if (fragmentToMove != null) {
                authorAccess.getDocumentController().insertFragment(
                    destinationTableCell.getEndOffset(), fragmentToMove);
              }
            }
            authorAccess.getEditorAccess().setCaretPosition(destinationTableCell.getEndOffset());
          }
        } else {
          AuthorOperationException ex = new AuthorOperationException("The selected cells must be from the same table row.");
          ex.setOperationRejectedOnPurpose(true);
          throw ex;
        }          
      } else {
        AuthorOperationException ex = new AuthorOperationException(
            "The operation is enabled only when there is a selection that contains " +
            "at least two cells from the same row of a table or when the caret is " +
            "positioned between two horizontally adjacent cells."
        );
        ex.setOperationRejectedOnPurpose(true);
        throw ex;
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException("Operation failed: " + e.getMessage());
    }
  }
  
  /**
   * Test if the destination and source cells have the same row span.
   * 
   * @param tableSupport The table support.
   * @param destinationCell The destination cell.
   * @param sourceCells The list with source cells.
   * @throws AuthorOperationException   Thrown when row span is different.
   */
  private void checkForSameRowSpan(AuthorTableCellSpanProvider tableSupport,
      AuthorElement destinationCell, List sourceCells) throws AuthorOperationException {
    Integer rowSpan = tableSupport.getRowSpan(destinationCell);
    int rSpan = (rowSpan != null ? rowSpan.intValue() : 1);
    
    for (Iterator iterator = sourceCells.iterator(); iterator.hasNext();) {
      AuthorElement currentCell = (AuthorElement) iterator.next();
      Integer currentRowSpan = tableSupport.getRowSpan(currentCell);
      int currentRSpan = (currentRowSpan != null ? currentRowSpan.intValue() : 1);
      if(rSpan != currentRSpan) {
        AuthorOperationException ex = new AuthorOperationException(
        "Join operation is possible only for cells with the same row span.");
        ex.setOperationRejectedOnPurpose(true);
        throw ex;
      }
    }
  }

  /**
   * Find the last cell for the join operation. 
   * This is the last cell whose content will be moved in the destination cell.
   * 
   * @param authorAccess The author access.
   * @param selectionOffset The selection end offset
   * @return The last cell involved in the join operation. Might be <code>null</code>.
   * @throws BadLocationException If method fails.
   */
  protected AuthorElement getCell(AuthorAccess authorAccess, int selectionOffset, boolean start) throws BadLocationException {
    AuthorElement cellElement = null;
    
    AuthorNode endSelNode = authorAccess.getDocumentController().getNodeAtOffset(selectionOffset);
    if (tableHelper.isTableRow(endSelNode)) {
      // The selection starts or ends inside the table row
      // At next/previous offset we must have the cell that will be the last cell
      AuthorNode node = 
        authorAccess.getDocumentController().getNodeAtOffset(
            start ? selectionOffset + 1 : selectionOffset - 1);
      if (tableHelper.isTableCell(node)) {
        cellElement = (AuthorElement) node;
      }
    } else {
      cellElement = getElementAncestor(endSelNode, AuthorTableHelper.TYPE_CELL);
    }
    return cellElement;
  }

  /**
   * No arguments.
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
    return "Join the content of the selected cells. The operation is available only if the selected " + 
    		"cells are from the same row and they have the same column span.";
  }

  /**
   * Compute the list with the cells from a table row that intersect 
   * the interval <code>[startOffset, endOffset]</code>.
   * 
   * @param rowNode     The node representing the current row.
   * @param startOffset The start offset, 0 based and inclusive.
   * @param endOffset   The end offset, 0 based and inclusive.
   */
  private List getCellsBetweenOffsets(AuthorElement rowNode, int startOffset, int endOffset) {
    List cells = new ArrayList(); 
    List contentNodes = rowNode.getContentNodes();
    for (Iterator iterator = contentNodes.iterator(); iterator.hasNext();) {
      AuthorNode node = (AuthorNode) iterator.next();
      if ( // Test if node contains start offset
          (node.getStartOffset() <= startOffset && node.getEndOffset() >= startOffset)
          // Test if node is inside [start, endOffset]
          || (node.getStartOffset() > startOffset && node.getEndOffset() < endOffset)
          // Test if node contains endOffset
          ||(node.getStartOffset() <= endOffset && node.getEndOffset() >= endOffset)) {
        
        if (tableHelper.isTableCell(node)) {
          cells.add(node);
        }
      }
    }
    return cells;
  }

  /**
   * Updates the column span information for the cells involved in the join operation.
   * 
   * @param authorAccess The author access.
   * @param tableCellSpanProvider The table cells span info provider.
   * @param destCellElement The destination cell element.
   * @param sourceCells The list with the cells that will be joined.
   * @throws AuthorOperationException if it fails.
   */
  private void updateColSpanForMergedCell(
      AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableCellSpanProvider, 
      AuthorElement destCellElement, 
      List sourceCells)
  throws AuthorOperationException {
    int[] destCellColSpanIndices = authorAccess.getTableAccess().getTableColSpanIndices(destCellElement);
    AuthorElement lastElem = (AuthorElement) sourceCells.get(sourceCells.size() - 1); 
    int[] endCellColSpanIndices = authorAccess.getTableAccess().getTableColSpanIndices(lastElem);
                
    tableHelper.updateTableColSpan(
        authorAccess, 
        tableCellSpanProvider, 
        destCellElement, 
        destCellColSpanIndices[0] + 1,
        endCellColSpanIndices[1] + 1);
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