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
package ro.sync.ecss.extensions.commons.table.operations.cals;

import java.util.Iterator;
import java.util.List;




import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper;
import ro.sync.ecss.extensions.commons.table.operations.DeleteColumnOperationBase;
import ro.sync.ecss.extensions.commons.table.support.CALSColSpec;
import ro.sync.ecss.extensions.commons.table.support.CALSTableCellInfoProvider;


/**
 * Operation used to delete a CALS table column.
 */

@WebappCompatible
public class DeleteColumnOperation extends DeleteColumnOperationBase implements CALSConstants {
  
  /**
   * Default constructor.
   */
  public DeleteColumnOperation() {
    this(new CALSDocumentTypeHelper());
  }
  
  /**
   * Constructor.
   * @param documentTypeHelper The document type helper.
   */
  public DeleteColumnOperation(AuthorTableHelper documentTypeHelper) {
    super(documentTypeHelper);
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.DeleteColumnOperationBase#performDeleteColumn(ro.sync.ecss.extensions.api.AuthorAccess, java.util.List, boolean)
   */
  @Override
  public boolean performDeleteColumn(AuthorAccess authorAccess,
      List<ContentInterval> columnIntervals, boolean placeCaretInNextCell)
      throws AuthorOperationException {
    boolean handle = false;
    try {
      // Do super.
      handle = super.performDeleteColumn(authorAccess, columnIntervals, placeCaretInNextCell);
      
      if (handle) {
        // Delete the column specification of the deleted column
        CALSTableCellInfoProvider spanProvider = 
          (CALSTableCellInfoProvider) tableHelper.getTableCellSpanProvider(tableElem);

        // Get the 'colspec' of the deleted column.
        CALSColSpec colSpec = spanProvider.getColSpec(deletedColumnIndex + 1);
        if (colSpec != null) {
          // Find the 'colspec' element in the children of the 'tgroup' element
          List contentNodes = tableElem.getContentNodes();
          // All the column indices of those columns that follows the deleted one, must be decreased.
          boolean shouldDecreaseColNum = false;
          // The 'colspec' element of the deleted column must be deleted after the iteration.
          // EXM-24308 Remove the colspec element even if it has no associated name
          AuthorElement toRemove = spanProvider.getColSpecElement(colSpec);
          for (Iterator iterator = contentNodes.iterator(); iterator.hasNext();) {
            AuthorNode node = (AuthorNode) iterator.next();
            if (isElement(node, ELEMENT_NAME_COLSPEC)) {
              // The current node is 'colspec' 
              AuthorElement colSpecElem = (AuthorElement) node;
              if (colSpecElem == toRemove) {
                // This is the one to be removed.
                // The columns following this one should decrease their indices.
                shouldDecreaseColNum = true;
              }
              if (shouldDecreaseColNum) {
                AttrValue colNumAttrVal = colSpecElem.getAttribute(ATTRIBUTE_NAME_COLNUM);
                if (colNumAttrVal != null && colNumAttrVal.getValue() != null) {
                  try {
                    int colNum = Integer.parseInt(colNumAttrVal.getValue());
                    // Decrease the "colnum".
                    authorAccess.getDocumentController().setAttribute(
                        ATTRIBUTE_NAME_COLNUM,
                        new AttrValue("" + (colNum - 1)),
                        colSpecElem);
                  } catch (NumberFormatException e) {
                    // Nothing to do
                  }
                }
              }
            }
          }
          if (toRemove != null) {
            // Remember the last caret position but adjust it
            // after the removal of the column specifications
            int newCaretOffset = authorAccess.getEditorAccess().getCaretOffset() - 
            (toRemove.getEndOffset() - toRemove.getStartOffset() + 1);
            // Remove the 'colspec' of the deleted column.
            authorAccess.getDocumentController().deleteNode(toRemove);

            // Restore caret position.
            authorAccess.getEditorAccess().setCaretPosition(newCaretOffset);
          }
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    
    return handle;
  }

  /**
   * @throws AuthorOperationException 
   * @see ro.sync.ecss.extensions.commons.table.operations.DeleteColumnOperationBase#updateTableColSpan(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider, ro.sync.ecss.extensions.api.node.AuthorElement, int, int)
   */
  @Override
  protected void updateTableColSpan(AuthorAccess authorAccess,
      AuthorTableCellSpanProvider spanProvider, AuthorElement cell, int colStartIndex,
      int colEndIndex) throws AuthorOperationException {
    if (deletedColumnIndex + 1 == colStartIndex ) {
      tableHelper.updateTableColSpan(
          authorAccess, 
          spanProvider, 
          cell, 
          colStartIndex + 1, 
          colEndIndex);
    } else if (deletedColumnIndex + 1 == colEndIndex) {
      tableHelper.updateTableColSpan(
          authorAccess, 
          spanProvider, 
          cell, 
          colStartIndex, 
          colEndIndex - 1);      
    } 
  }
}