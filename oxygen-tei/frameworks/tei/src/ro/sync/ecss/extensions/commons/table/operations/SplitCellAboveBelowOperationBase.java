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
 * Base operation for splitting a table cell.
 * The new cell resulting from the split operation will be positioned above or below the original cell.  
 */

public abstract class SplitCellAboveBelowOperationBase extends AbstractTableOperation {
  
  /**
   * Possible value of 'Split point' argument.
   * The value is <code>Above</code>.
   */
  private static final String SPLIT_ABOVE = "Above";
  
  /**
   * Possible value of 'Split point' argument.
   * The value is <code>Below</code>.
   */
  private static final String SPLIT_BELOW= "Below";
  
  /**
   * Argument name constant.
   * The value is <code>Split point</code>.
   */
  private static final String ARGUMENT_NAME = "Split point";
  
  /**
   * Constructor.
   * 
   * @param tableHelper Document type specific table information helper.
   */
  public SplitCellAboveBelowOperationBase(AuthorTableHelper tableHelper) {
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
        String argVal = (String) args.getArgumentValue(ARGUMENT_NAME);
        splitCell(cell, authorAccess, SPLIT_ABOVE.equals(argVal));
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
   * Split the cell. 
   * 
   * @param cell          The table cell to split.
   * @param authorAccess  Access to author functionality. 
   * @param above         If <code>true</code> the cell will be split above.
   * @throws AuthorOperationException 
   * @throws BadLocationException 
   */
  public void splitCell(AuthorElement cell, AuthorAccess authorAccess, boolean above) throws AuthorOperationException, BadLocationException {
    // Find the table ancestor element for create the table support 
    AuthorElement tableElem = 
      getElementAncestor(cell, AuthorTableHelper.TYPE_TABLE);
    if (tableElem != null) {
      // Create table support          
      AuthorTableCellSpanProvider tableSupport = tableHelper.getTableCellSpanProvider(tableElem);

      Integer cellRowSpanInteger = tableSupport.getRowSpan(cell);
      int cellRowSpan = (cellRowSpanInteger != null ? cellRowSpanInteger.intValue() : 1);
      if (cellRowSpan > 1) {
        // The split operation is possible for cell at caret position
        
        // Find the location where the newly split cell will be inserted
        int[] location = authorAccess.getTableAccess().getTableCellIndex(cell);
        if (location != null) {
          if (!above) {
            ////////////////
            // Split below

            // The split will occur on the last row (row + rowspan - 1)
            int insertionOffset = findCellInsertionOffset(                    
                authorAccess,
                tableElem, 
                location[0] + cellRowSpan - 1,
                location[1]);

            if (insertionOffset != -1) {
              // Create a clean copy of the node. 
              AuthorDocumentFragment emptyCellFragment = 
                createEmptyCell(authorAccess, cell, getIgnoredAttributes());
              AuthorDocumentController controller = authorAccess.getDocumentController();

              // Insert the copy at the determined offset
              controller.insertFragment(insertionOffset, emptyCellFragment);

              // Set caret inside the new entry
              authorAccess.getEditorAccess().setCaretPosition(insertionOffset + 1);

              // Decrease the number of row span
              tableHelper.updateTableRowSpan(authorAccess, cell, cellRowSpan - 1);
            } else {
              throw new AuthorOperationException(
              "Could not determine the location where the split will occur.");
            }
          } else {
            /////////////////
            // Split above
            
            // The split will occur on the next row (row + 1)
            int insertionOffset = findCellInsertionOffset(
                authorAccess, tableElem, location[0] + 1, location[1]);
            
            if (insertionOffset != -1) {
              AuthorDocumentController controller = authorAccess.getDocumentController();
              
              // Decrease the number of row span
              tableHelper.updateTableRowSpan(authorAccess, cell, cellRowSpan - 1);
              
              // Create a fragment containing an exact copy of the cell
              AuthorDocumentFragment contentFragment  = 
                controller.createDocumentFragment(cell, true);
              // Insert the copy at the determined offset
              controller.insertFragment(insertionOffset, contentFragment);
              
              // Clear the old cell
              clearCell(authorAccess, cell, getIgnoredAttributes());
              
              // Set caret inside the empty cell
              authorAccess.getEditorAccess().setCaretPosition(cell.getStartOffset() + 1);
            } else {
              throw new AuthorOperationException(
                "Could not determine the location where the split will occur.");
            }
          }
        } else {
          throw new AuthorOperationException(
              "Invalid table. Could not determine the location of the cell.");
        }
      } else {
        throw new AuthorOperationException(
        "The cell you are trying to split does not span over multiple rows.");
      }
    } else {
      throw new AuthorOperationException(
      "The table cell must be inside a table element.");
    } 
  }
  
  /**
   * Remove the specified attributes from a cell.
   * 
   * @param authorAccess  Access to author functionality. 
   * @param cellElem      The table cell to remove the specified attributes for.
   * @param removedAttributes The attributes which should be removed.
   */
  private void clearCell(AuthorAccess authorAccess, 
      AuthorElement cellElem,
      String[] removedAttributes) {
    AuthorDocumentController controller = authorAccess.getDocumentController();
    // Remove specified attributes
    for (int i = 0; i < removedAttributes.length; i++) {
      controller.removeAttribute(removedAttributes[i], cellElem);
    }
    if (cellElem.getStartOffset() + 1 < cellElem.getEndOffset()) {
      // If there is some content, delete it.
      controller.delete(cellElem.getStartOffset() + 1, cellElem.getEndOffset() - 1);
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
            new String[] {SPLIT_ABOVE, SPLIT_BELOW}, 
            SPLIT_ABOVE)
    };
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  public String getDescription() {
    return "This operation splits the content of a table cell that spans over multiple rows.";
  }
  
  /**
   * @return The attributes which should be skipped, when creating a copy of the split cell.
   */
  protected abstract String[] getIgnoredAttributes();
}