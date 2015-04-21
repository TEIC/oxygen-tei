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

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Operation used to delete a table row.
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
   * Delete table row.
   * The row that must be deleted is determined in the following order:
   * <ul>
   * <li>by startRowOffset and endRowOffset if both are bigger than <code>0</code></li>
   * <li>selected row, if any</li>
   * <li>the row at caret offset</li>
   * </ul> 
   * 
   * @param authorAccess The access to Author operations.
   * @param startRowOffset The start row offset. 
   * @param endRowOffset The end row offset.
   * @return <code>true</code> if a row is deleted
   * 
   * @throws IllegalArgumentException
   * @throws AuthorOperationException
   */
  public boolean performDeleteRows(AuthorAccess authorAccess, int startRowOffset, int endRowOffset) throws AuthorOperationException {
    boolean handled = false;
    // Test if the operation is available for the node at the caret position 
    try {
      
      AuthorElement tableElem = null; 
      AuthorElement rowElem = null; 
      if (startRowOffset > 0 && endRowOffset > 0) {
        AuthorNode rowNode = authorAccess.getDocumentController().getNodeAtOffset(startRowOffset + 1);
        if (rowNode.getStartOffset() == startRowOffset && rowNode.getEndOffset() == endRowOffset - 1) {
          if (isTableElement(rowNode, AuthorTableHelper.TYPE_ROW)) {
            // The row element to delete 
            rowElem = (AuthorElement) rowNode;
            tableElem = getElementAncestor(
                rowNode,
                AuthorTableHelper.TYPE_TABLE);
          }
        }
      } else {
          AuthorNode row = authorAccess.getEditorAccess().getFullySelectedNode();
          if (row != null && row instanceof AuthorElement && isTableElement(row, AuthorTableHelper.TYPE_ROW)) {
            // The row element to delete 
            rowElem = (AuthorElement) row;
            tableElem = getElementAncestor(
                row,
                AuthorTableHelper.TYPE_TABLE);
          } else {
          // Determine the node at caret.
          AuthorNode referenceNode = 
            authorAccess.getDocumentController().getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset());
          // Determine the table.
          tableElem = getElementAncestor(
              referenceNode,
              AuthorTableHelper.TYPE_TABLE);
          // Determine the table row .
          rowElem = getElementAncestor(referenceNode, AuthorTableHelper.TYPE_ROW);
        }
      }
      int deletedRowIndex = -1;
      if (tableElem != null && rowElem != null) {
        int rowCount = authorAccess.getTableAccess().getTableRowCount(tableElem);
        for (int i = 0; i < rowCount; i++) {
          AuthorElement element = authorAccess.getTableAccess().getTableRow(i, tableElem);
          if (element == rowElem) {
            deletedRowIndex = i;
            break;
          }
        }
      }
      if (deletedRowIndex != -1) {
        SplitCellAboveBelowOperationBase splitCellOp = null;

        // For all the table rows delete the cell at the column index
        int colsNumber = authorAccess.getTableAccess().getTableNumberOfColumns(tableElem);
        AuthorTableCellSpanProvider spanProvider = 
          tableHelper.getTableCellSpanProvider(tableElem);

        // Update the 'rowspan' for the row cells that span more that one row
        for (int i = colsNumber - 1; i >= 0; i--) {
          AuthorElement cell = authorAccess.getTableAccess().getTableCellAt(deletedRowIndex, i, tableElem);
          if (cell != null) {
            int[] indices = authorAccess.getTableAccess().getTableCellIndex(cell);
            Integer colSpanInteger = spanProvider.getColSpan(cell);
            Integer rowSpanInteger = spanProvider.getRowSpan(cell);
            int colSpan = colSpanInteger != null ? colSpanInteger.intValue() : 1;
            int rowSpan = rowSpanInteger != null ? rowSpanInteger.intValue() : 1;
            if (rowSpan > 1) {
              if (indices[0] == deletedRowIndex) {
                // Split the cell before deleting it.
                if (splitCellOp == null) {
                  splitCellOp = createSplitCellOperation(); 
                }
                splitCellOp.splitCell(cell, authorAccess, true);
              } else {
                // Decrease the column span of the cell with one
                tableHelper.updateTableRowSpan(
                    authorAccess, 
                    cell, 
                    rowSpan - 1);
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

        // Delete the row
        authorAccess.getDocumentController().deleteNode(rowElem);

        // Update the number of rows
        tableHelper.updateTableRowNumber(authorAccess, tableElem, -1);

        int tableRowCount = authorAccess.getTableAccess().getTableRowCount(tableElem);

        // Check if there are any rows left after deletion...
        if (tableRowCount > 0) {
          if (deletedRowIndex >= tableRowCount) {
            // Adjust the new index.
            deletedRowIndex = tableRowCount - 1;
          }
          // Get the new row to put the caret in.
          AuthorElement element = authorAccess.getTableAccess().getTableRow(deletedRowIndex, tableElem);
          if (element != null) {
            // Set the caret INSIDE the new row that is eligible for deletion.
            authorAccess.getEditorAccess().setCaretPosition(element.getStartOffset() + 1);
          }
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
   * Delete the table row at the caret position. 
   * For this operation the caret must be inside a table cell.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public final void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    performDeleteRows(authorAccess, -1, -1);
  }

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
    return "Delete the current table row.";
  }
  
  /**
   * Create the split cell operation. 
   * The operation is needed to split the cells that span over multiple rows and
   * start on the row to be deleted.
   * 
   * @return The split cell operation.
   */
  protected abstract SplitCellAboveBelowOperationBase createSplitCellOperation();
}