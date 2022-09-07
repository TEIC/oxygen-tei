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

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Operation for joining the content of two cells in the same column, from adjacent rows. 
 * The operation is possible only for cells that span over the same number of columns. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class JoinCellAboveBelowOperationBase extends AbstractTableOperation {
  
  /**
   * Possible value of <code>Join direction</code> argument.
   */
  public static final String JOIN_ABOVE = "Above";
  
  /**
   * Possible value of <code>Join direction</code> argument.
   */
  public static final String JOIN_BELOW = "Below";
  
  /**
   * <code>Join direction</code> argument name constant.
   */
  private static final String ARGUMENT_NAME = "Join direction";
  
  /**
   * Constructor.
   * 
   * @param tableHelper The document type specific table helper.
   */
  public JoinCellAboveBelowOperationBase(AuthorTableHelper tableHelper) {
    super(tableHelper);
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AbstractTableOperation#doOperationInternal(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  protected void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args)
  throws AuthorOperationException {
    try {
      // Find the table cell that contains the caret
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      AuthorNode nodeAtCaret = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);      
      
      AuthorElement cell = null;
      if (authorAccess.getEditorAccess().hasSelection()) {
        // Maybe there is a selected cell
        AuthorNode selectedNode = authorAccess.getEditorAccess().getFullySelectedNode();
        if (selectedNode != null && isTableElement(selectedNode, AuthorTableHelper.TYPE_CELL)) {
          // Found that the selection represents a cell
          cell = (AuthorElement) selectedNode;
        }
      }
      if (cell == null) {
        // Get cell at caret
        cell = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_CELL);
      }
      
      if(cell != null) {
        // Find the table ancestor element for creating the table support 
        AuthorElement tableElem = getElementAncestor(
            nodeAtCaret, AuthorTableHelper.TYPE_TABLE);
        if(tableElem != null) {
          // Find the cells above and below
          AuthorElement cellAbove = null;
          AuthorElement cellBelow = null;
          String joinDirection = (String) args.getArgumentValue(ARGUMENT_NAME);
          if(JOIN_BELOW.equals(joinDirection)) {
            // Join the content of cell at caret with the content of the cell below
            cellAbove = cell;
            cellBelow = authorAccess.getTableAccess().getTableCellBelow(cell);            
            if(cellBelow == null) {
              AuthorOperationException ex = new AuthorOperationException("There is no cell below.");
              ex.setOperationRejectedOnPurpose(true);
              throw ex;
            }
          } else {
            // Join the content of cell at caret with the content of the cell above
            cellAbove = authorAccess.getTableAccess().getTableCellAbove(cell);
            cellBelow = cell;
            if(cellAbove == null) {
              AuthorOperationException ex = new AuthorOperationException("There is no cell above.");
              ex.setOperationRejectedOnPurpose(true);
              throw ex;
            }
          }
          
          // Create table support for get cell horizontal span
          AuthorTableCellSpanProvider tableSupport = tableHelper.getTableCellSpanProvider(tableElem);
          Integer colSpanCellAboveInteger = tableSupport.getColSpan(cellAbove);
          Integer colSpanCellBelowInteger = tableSupport.getColSpan(cellBelow);
          int colSpanCellAbove = 
            (colSpanCellAboveInteger != null ? colSpanCellAboveInteger.intValue() : 1);
          int colSpanCellBelow = 
            (colSpanCellBelowInteger != null ? colSpanCellBelowInteger.intValue() : 1);
          if(colSpanCellAbove != colSpanCellBelow) {
            AuthorOperationException ex = new AuthorOperationException("The column spans of the involved cells are different.");
            ex.setOperationRejectedOnPurpose(true);
            throw ex;
          }

          AuthorDocumentController controller = authorAccess.getDocumentController();
          
          // Keep the fragment content from the below cell
          AuthorDocumentFragment joinFragment = null;
          if (cellBelow.getStartOffset() + 1 < cellBelow.getEndOffset()) {
            joinFragment = controller.createDocumentFragment(
                cellBelow.getStartOffset() + 1, cellBelow.getEndOffset() - 1);
          }
          
          //EXM-23327 If we are removing the last cell on the row
          //Then we must also remove the row
          AuthorElement tableRowToRemove = null;
          int[] indexOfBelowTable = authorAccess.getTableAccess().getTableCellIndex(cellBelow);
          if(indexOfBelowTable != null) {
            int rowIndex = indexOfBelowTable[0];
            AuthorElement tableRow = authorAccess.getTableAccess().getTableRow(rowIndex, tableElem);
            if(tableRow != null && tableRow.getContentNodes().size() == 1) {
              //Contains only the cell which will get deleted
              tableRowToRemove = tableRow;
            }
          }
          
          // Delete the below cell.
          boolean deletedCell = controller.deleteNode(cellBelow);

          // Insert the fragment in the above cell
          if (joinFragment != null) {
            controller.insertFragment(cellAbove.getEndOffset(), joinFragment);
          } 
          
          // Increase the row span
          Integer cellAboveRowSpanInteger = tableSupport.getRowSpan(cellAbove);
          Integer cellBelowRowSpanInteger = tableSupport.getRowSpan(cellBelow);
          int cellAboveRowSpan = 
            (cellAboveRowSpanInteger != null ? cellAboveRowSpanInteger.intValue() : 1);
          int cellBelowRowSpan = 
            (cellBelowRowSpanInteger != null ? cellBelowRowSpanInteger.intValue() : 1);

          tableHelper.updateTableRowSpan(
              authorAccess, cellAbove, cellAboveRowSpan + cellBelowRowSpan);
          
          if (joinFragment == null) {
            // Move the caret at the end of the cell above, the destination cell 
            authorAccess.getEditorAccess().setCaretPosition(cellAbove.getEndOffset());
          } else {
            // The caret must be positioned at the end of the moved cell content
          }
          if(deletedCell && tableRowToRemove != null) {
            //EXM-23327 If we are removing the last cell on the row then we must also remove the row.
            DeleteRowOperationBase deleteRowOperation = createDeleteRowOperation();
            if(deleteRowOperation != null) {
              deleteRowOperation.performDeleteRows(authorAccess, tableRowToRemove.getStartOffset(), tableRowToRemove.getEndOffset() + 1);
              //Move the caret on the cell above.
              authorAccess.getEditorAccess().setCaretPosition(cellAbove.getEndOffset());
            }
          }
        } else {
          throw new AuthorOperationException(
          "The table cell must be inside a table element.");
        } 
      } else {
        AuthorOperationException ex = new AuthorOperationException("The caret must be inside a table cell or a cell must be selected.");
        ex.setOperationRejectedOnPurpose(true);
        throw ex;
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException(
          "The operation cannot be performed due to: " + e.getMessage(), e);
    }
  }

  /**
   * Create a delete row operation necessary to delete the row which will now be empty.
   * Can be overwritten by custom code.
   * @return a delete row operation necessary to delete the row which will now be empty.
   */
  protected DeleteRowOperationBase createDeleteRowOperation() {
    //Used to delete a table row and update calspecs.
    return new DeleteRowOperationBase(tableHelper) {
      @Override
      protected SplitCellAboveBelowOperationBase createSplitCellOperation() {
        return null;
      }
    };
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[] {
        new ArgumentDescriptor(
            ARGUMENT_NAME, 
            ArgumentDescriptor.TYPE_CONSTANT_LIST,
            "The join direction relative to the source cell.",
            new String[] {JOIN_ABOVE, JOIN_BELOW}, 
            JOIN_ABOVE)
    };
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "This operation joins the content of two cells from adjacent rows.";
  }
}