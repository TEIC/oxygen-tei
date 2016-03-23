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




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper;
import ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase;
import ro.sync.ecss.extensions.commons.table.operations.InsertTableCellsContentConstants;

/**
 * Operation used to insert a table row for DocBook v.4 or v.5 and for DITA CALS tables.. 
 */

@WebappCompatible(false)
public class InsertRowOperation extends InsertRowOperationBase implements CALSConstants,
  InsertTableCellsContentConstants{
  
  /**
   * The fragment that must be introduced in the table cells
   */
  protected String cellContent;
  
  /**
   * Constructor.
   */
  public InsertRowOperation() {
    super(new CALSDocumentTypeHelper());
  }
  
  /**
   * Constructor.
   * 
   * @param helper Table helper 
   */
  public InsertRowOperation(AuthorTableHelper helper) {
    super(helper);
  }
  
  @Override
  protected void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    Object cellFragmentObj =  args.getArgumentValue(CELL_FRAGMENT_ARGUMENT_NAME);
    if (cellFragmentObj instanceof String) {
      cellContent = (String) cellFragmentObj;
    } 
    if ("".equals(cellContent)) {
      cellContent = null;
    }
    super.doOperationInternal(authorAccess, args);
  }
    
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#getCellElementName(AuthorElement, int)
   */
  @Override
  protected String getCellElementName(AuthorElement tableElement, int columnIndex) {
    return ELEMENT_NAME_ENTRY;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#getRowElementName(AuthorElement)
   */
  @Override
  protected String getRowElementName(AuthorElement tableElement) {
    return ELEMENT_NAME_ROW;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#useCurrentRowTemplateOnInsert()
   */
  @Override
  protected boolean useCurrentRowTemplateOnInsert() {
    return true;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#getOperationArguments()
   */
  @Override
  protected ArgumentDescriptor[] getOperationArguments() {
    ArgumentDescriptor[] arguments = null;
    ArgumentDescriptor[] args = super.getOperationArguments();
    if (args != null) {
      arguments = new ArgumentDescriptor[args.length + 1];
      for (int i = 0; i < args.length; i++) {
        arguments[i] = args[i];
      }
      arguments[args.length] = CELL_FRAGMENT_ARGUMENT;
    } else {
      arguments = CELL_FRAGMENT_ARGUMENT_IN_ARRAY;
    }
    return arguments;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#getDefaultContentForEmptyCells()
   */
  @Override
  protected String getDefaultContentForEmptyCells() {
    return cellContent;
  }
}