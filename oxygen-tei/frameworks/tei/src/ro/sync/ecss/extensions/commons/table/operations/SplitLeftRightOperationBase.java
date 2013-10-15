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
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Operation for splitting a table cell.
 * The new cell resulting from the split operation will be positioned to the left
 * or to the right of the original cell.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class SplitLeftRightOperationBase extends AbstractTableOperation {
  
  /**
   * Possible value of 'Split point' argument.
   * The value is <code>Left</code>.
   */
  private static final String SPLIT_LEFT = "Left";
  
  /**
   * Possible value of 'Split point' argument.
   * The value is <code>Right</code>.
   */
  private static final String SPLIT_RIGHT = "Right";
  
  /**
   * Argument name constant.
   * The value is <code>Split name</code>.
   */
  private static final String ARGUMENT_NAME = "Split point";
  
  /**
   * Constructor.
   * 
   * @param tableHelper Document type specific table information helper. 
   */
  public SplitLeftRightOperationBase(AuthorTableHelper tableHelper) {
    super(tableHelper);
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
  throws IllegalArgumentException, AuthorOperationException {
    try {
      // Find the table cell that contains the caret.
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      AuthorNode nodeAtCaret = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);      
      AuthorElement cell = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_CELL);
      if (cell != null) {
        // Find the table ancestor element for create the table support.
        AuthorElement tableElem = 
          getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_TABLE);
        if (tableElem != null) {
          // Create table support for get cell horizontal span.
          AuthorTableCellSpanProvider tableSupport = 
            tableHelper.getTableCellSpanProvider(tableElem);

          // Verify if the column span is defined for this cell.
          tableHelper.checkTableColSpanIsDefined(authorAccess, tableSupport, cell);
          
          Integer cellColSpanInteger = tableSupport.getColSpan(cell);
          int cellColSpan = (cellColSpanInteger != null ? cellColSpanInteger.intValue() : 1);
          if (cellColSpan > 1) {
            // The split operation is possible for cell at caret position.
            
            // Split to the left or to the right.
            boolean splitLeft = SPLIT_LEFT.equals(args.getArgumentValue(ARGUMENT_NAME));
            
            // Create a fragment for the empty cell to be inserted.
            AuthorDocumentController controller = authorAccess.getDocumentController();
            AuthorDocumentFragment emptyCellFragment = createEmptyCell(
                authorAccess,
                cell,
                getAttributesSkippedAtCopy());

            // Determine the insert point depending on the 'Split point' argument.
            int insertOffset = -1;
            if (splitLeft) {
              // Insert the copied cell before the current cell.
              insertOffset = cell.getStartOffset();
            } else {
              // Insert the copied cell after the current cell.
              insertOffset = cell.getEndOffset() + 1;
            }
            // Decrease the column span.
            decreaseColSpan(authorAccess, tableSupport, cell, splitLeft);
            // Insert the empty cell.
            controller.insertFragment(insertOffset, emptyCellFragment);
            // Set the caret position inside the empty cell.
            authorAccess.getEditorAccess().setCaretPosition(insertOffset + 1);
          } else {
            AuthorOperationException ex = new AuthorOperationException(
            "The cell you are trying to split does not span over multiple columns.");   
            ex.setOperationRejectedOnPurpose(true);
            throw ex;
          }
        } else {
          throw new AuthorOperationException(
          "The table cell must be inside a table element.");
        } 
      } else {
        AuthorOperationException ex = new AuthorOperationException("The caret must be inside a table cell.");
        ex.setOperationRejectedOnPurpose(true);
        throw ex;
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException(
          "The operation cannot be performed due to: " + e.getMessage(), e);
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[] {
        new ArgumentDescriptor(
            ARGUMENT_NAME, 
            ArgumentDescriptor.TYPE_CONSTANT_LIST,
            "The location relative to the source cell for the newly split cell.",
            new String[] {SPLIT_LEFT, SPLIT_RIGHT}, 
            SPLIT_LEFT)
    };
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  public String getDescription() {
    return "This operation splits the content of a table cell that spans over multiple columns.";
  }
  
  /**
   * Decrement the column span of the <code>cell</code>. 
   * 
   * @param authorAccess Access to author functionality. 
   * @param tableSupport  The table cell span support.
   * @param cell  The table cell to update the column span for.
   * @param left If <code>true</code> then the column span of the cell will be 
   * shrunk starting from the left. 
   * @throws AuthorOperationException When the column span cannot be decreased.
   */
  private void decreaseColSpan(
      AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableSupport, 
      AuthorElement cell, 
      boolean left) throws AuthorOperationException {
    
    int[] cellColSpanIndices = authorAccess.getTableAccess().getTableColSpanIndices(cell);
    
    // Start column of the cell, 1 based.
    int startColumn = cellColSpanIndices[0] + 1;
    // End column of the cell, 1 based.
    int endColumn = cellColSpanIndices[1] + 1;
    
    if (left) {
      tableHelper.updateTableColSpan(
          authorAccess, tableSupport, 
          cell, 
          startColumn + 1, 
          endColumn);
    } else {
      tableHelper.updateTableColSpan(
          authorAccess, tableSupport, 
          cell, 
          startColumn, 
          endColumn - 1);
    }
  }
  
  /**
   * @return The attributes which should be skipped when creating a copy of 
   * the split cell.
   */
  protected abstract String[] getAttributesSkippedAtCopy();
}