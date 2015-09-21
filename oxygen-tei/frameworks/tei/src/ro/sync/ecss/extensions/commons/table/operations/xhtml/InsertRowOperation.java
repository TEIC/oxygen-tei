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




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase;

/**
 * Operation used to insert a table row for XHTML documents. 
 */

@WebappCompatible(false)
public class InsertRowOperation extends InsertRowOperationBase implements XHTMLConstants {

  /**
   * The name of the argument that specify if the new table row will be inserted 
   * into the table header or not. 
   * The value is <code>header row</code>.
   */
  private static final String ARGUMENT_HEADER_ROW = "header row";
  
  /**
   * Possible value of the argument ARGUMENT_HEADER_ROW.
   * The value is <code>yes</code>.
   */
  private static final String ARGUMENT_VALUE_YES = "yes";

  /**
   * Possible value of the argument ARGUMENT_HEADER_ROW.
   * The value is <code>no</code>
   */
  private static final String ARGUMENT_VALUE_NO = "no";
  
  /**
   * If true then the table row will be inserted in header. 
   * In this case we will insert 'th' elements.
   */
  private boolean headerRow;
  
  /**
   * Constructor.
   */
  public InsertRowOperation() {
    super(new XHTMLDocumentTypeHelper());
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#getOperationArguments()
   */
  @Override
  protected ArgumentDescriptor[] getOperationArguments() {
    ArgumentDescriptor[] argumentDescriptors = super.getOperationArguments();
    ArgumentDescriptor[] newArgs = new ArgumentDescriptor[argumentDescriptors.length + 1];
    System.arraycopy(argumentDescriptors, 0, newArgs, 0, argumentDescriptors.length);
    
    newArgs[newArgs.length - 1] = 
      new ArgumentDescriptor(
          ARGUMENT_HEADER_ROW, 
          ArgumentDescriptor.TYPE_CONSTANT_LIST,
          "The argument specify if the table row will be inserted into the header. " +
          "In this case the name of inserted cells will be 'th'.",
          new String[] {ARGUMENT_VALUE_YES, ARGUMENT_VALUE_NO}, 
          ARGUMENT_VALUE_YES);
    
    return newArgs;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    headerRow = 
      ARGUMENT_VALUE_YES.equals(args.getArgumentValue(ARGUMENT_HEADER_ROW));
    super.doOperation(authorAccess, args);
  }
    
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#getCellElementName(AuthorElement, int)
   */
  @Override
  protected String getCellElementName(AuthorElement tableElement, int columnIndex) {
    if (headerRow) {
      return ELEMENT_NAME_TH;
    } else {
      return ELEMENT_NAME_TD;      
    }
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#getRowElementName(AuthorElement)
   */
  @Override
  protected String getRowElementName(AuthorElement tableElement) {
    return ELEMENT_NAME_TR;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertRowOperationBase#useCurrentRowTemplateOnInsert()
   */
  @Override
  protected boolean useCurrentRowTemplateOnInsert() {
    return true;
  }
}