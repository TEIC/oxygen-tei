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
package ro.sync.ecss.extensions.commons.table.operations.xhtml;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.commons.table.operations.DeleteColumnOperationBase;
import ro.sync.ecss.extensions.commons.table.support.HTMLTableCellInfoProvider;

/**
 * Operation used to delete an XHTML table column.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class DeleteColumnOperation extends DeleteColumnOperationBase implements XHTMLConstants {
  /**
   * Constructor.
   */
  public DeleteColumnOperation() {
    super(new XHTMLDocumentTypeHelper());
  }

  /**
  * @see ro.sync.ecss.extensions.commons.table.operations.DeleteColumnOperationBase#updateColspec(ro.sync.ecss.extensions.api.AuthorAccess, java.lang.Integer)
  */
  @Override
  public void updateColspec(AuthorAccess authorAccess, Integer deletedColumnIndex) {

    // Delete the column specification of the deleted column
    HTMLTableCellInfoProvider spanProvider = 
        (HTMLTableCellInfoProvider) tableHelper.getTableCellSpanProvider(tableElem);
    if(deletedColumnIndex >= 0){
      // The 'colspec' element of the deleted column must be deleted after the iteration.
      AuthorElement toRemove = spanProvider.getColSpec(deletedColumnIndex);
      if (toRemove != null) {
        // Remember the last caret position but adjust it
        // after the removal of the column specifications
        int newCaretOffset = authorAccess.getEditorAccess().getCaretOffset() - 
            (toRemove.getEndOffset() - toRemove.getStartOffset() + 1);
        boolean decreasedSpan = false;
        AttrValue span = toRemove.getAttribute(HTMLTableCellInfoProvider.ATTR_NAME_SPAN);
        if (span != null && span.getValue() != null) {
          try {
            int colNum = Integer.parseInt(span.getValue());
            if(colNum > 2){
              // Decrease the "colnum".
              authorAccess.getDocumentController().setAttribute(
                  HTMLTableCellInfoProvider.ATTR_NAME_SPAN,
                  new AttrValue("" + (colNum - 1)),
                  toRemove);
              decreasedSpan = true;
            } else if(colNum == 2){
              //Remove attribute
              authorAccess.getDocumentController().removeAttribute(
                  HTMLTableCellInfoProvider.ATTR_NAME_SPAN,
                  toRemove);
              decreasedSpan = true;
            }
          } catch (NumberFormatException e) {
            // Nothing to do
          }
        }

        if(! decreasedSpan){
          // Remove the 'colspec' of the deleted column.
          authorAccess.getDocumentController().deleteNode(toRemove);
        }

        // Restore caret position.
        authorAccess.getEditorAccess().setCaretPosition(newCaretOffset);
      }
    }
  }
  
  /**
   * Update the column span for the table cell that is included into the deleted
   * column.
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.DeleteColumnOperationBase#updateTableColSpan(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider, ro.sync.ecss.extensions.api.node.AuthorElement, int, int)
   */
  @Override
  protected void updateTableColSpan(AuthorAccess authorAccess,
      AuthorTableCellSpanProvider spanProvider, AuthorElement cell, int colStartIndex,
      int colEndIndex) throws AuthorOperationException {
    // Update the table column span after deleting a column.
    tableHelper.updateTableColSpan(
        authorAccess,
        spanProvider, 
        cell, 
        colStartIndex,
        colEndIndex - 1);
  }
}