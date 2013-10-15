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

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.commons.table.operations.JoinRowCellsOperationBase;

/**
 * This is the CALS tables implementation of the operation used to join 
 * the content of two or more cells from the same table row.
 * If selection exists, the cell at selection start offset determines the destination cell where
 * the content of the next cells will be moved. 
 * If there is no selection, then the caret must be between
 * two table cells. 
 * The operation modifies the <code>namest</code> and <code>nameend</code>
 * attributes of the destination cell.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class JoinRowCellsOperation extends JoinRowCellsOperationBase implements CALSConstants {

  /**
   * Constructor.
   */
  public JoinRowCellsOperation() {
    super(new CALSDocumentTypeHelper());
  }
  
  /**
   * Generates column specifications for the given table and inserts them into it.
   * 
   * @param authorAccess Access.
   * @param tableSpanSupport Span support.
   * @param tableElement The table element.
   * 
   * @throws AuthorOperationException Failed to insert the column specifications into the table.
   */
  @Override
  protected void generateColumnSpecifications(
      AuthorAccess authorAccess, 
      AuthorTableCellSpanProvider tableSpanSupport, 
      AuthorElement tableElement) throws AuthorOperationException {
    int cols = authorAccess.getTableAccess().getTableNumberOfColumns(tableElement);
    StringBuffer colSpecs = new StringBuffer();
    for (int i = 1; i <= cols; i++) {
      colSpecs.append("<colspec colname=\"c").append(i).append("\" colnum=\"").append(i).append("\"");
      String namespace = tableElement.getNamespace();
      if (namespace != null && namespace.length() > 0) {
        colSpecs.append(" xmlns=\"").append(namespace).append("\"");
      }
      colSpecs.append("/>");
    }
    authorAccess.getDocumentController().insertXMLFragment(colSpecs.toString(), tableElement.getStartOffset() + 1);
    
    tableSpanSupport.init(tableElement);
  }
}