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
package ro.sync.ecss.extensions.tei;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.table.operations.AuthorTableDeleteColumnArguments;
import ro.sync.ecss.extensions.api.table.operations.AuthorTableDeleteRowArguments;
import ro.sync.ecss.extensions.api.table.operations.AuthorTableDeleteRowsArguments;
import ro.sync.ecss.extensions.api.table.operations.AuthorTableInsertColumnArguments;
import ro.sync.ecss.extensions.api.table.operations.AuthorTableOperationsHandler;
import ro.sync.ecss.extensions.commons.table.operations.TableOperationsUtil;
import ro.sync.ecss.extensions.tei.table.DeleteColumnOperation;
import ro.sync.ecss.extensions.tei.table.DeleteRowOperation;
import ro.sync.ecss.extensions.tei.table.InsertColumnOperation;
import ro.sync.ecss.extensions.tei.table.InsertRowOperation;
import ro.sync.ecss.extensions.tei.table.InsertTableOperation;

/**
 * Author table operations handler for TEIP4 framework.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class TEIAuthorTableOperationsHandler extends AuthorTableOperationsHandler {

  /**
   * Insert column operation.
   */
  private InsertColumnOperation insertColumnOperation = null;
  
  /**
   * Insert row operation.
   */
  private InsertRowOperation insertRowOperation = null;
  
  /**
   * Insert table operation.
   */
  private InsertTableOperation insertTableOperation = null;
  
  /**
   * Delete column operation.
   */
  private DeleteColumnOperation deleteColumnOperation = null;
  
  /**
   * Delete row operation.
   */
  private DeleteRowOperation deleteRowOperation = null;
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(TEIAuthorTableOperationsHandler.class
      .getName());

  private final String namespace;
  
  /**
   * Constructor.
   * 
   * @param namespace The namespace. 
   */
  public TEIAuthorTableOperationsHandler(String namespace) {
    this.namespace = namespace;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.table.operations.AuthorTableOperationsHandler#handleInsertColumn(ro.sync.ecss.extensions.api.table.operations.AuthorTableInsertColumnArguments)
   */
  @Override
  public boolean handleInsertColumn(AuthorTableInsertColumnArguments tablePasteColumnsArgs) throws AuthorOperationException {
    boolean handled = false;
    try {
      // Perform operation
      if (insertRowOperation == null) {
        insertRowOperation = new InsertRowOperation();
      }
      if (insertColumnOperation == null) {
        insertColumnOperation = new InsertColumnOperation();
      }
      if (insertTableOperation == null) {
        insertTableOperation = new InsertTableOperation();
      }
      insertColumnOperation.performInsertColumn(
          tablePasteColumnsArgs.getAuthorAccess(), 
          namespace, 
          tablePasteColumnsArgs.getColumnFragments(), 
          tablePasteColumnsArgs.getColumnSpecificationInformation(),
          tablePasteColumnsArgs.areFragmentsWrappedInCells(), 
          insertRowOperation,
          insertTableOperation);
      // The paste operation was handled
      handled = true;
    } catch (IllegalArgumentException e) {
      logger.warn("Paste column failed.", e);
    } 
    return handled;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.table.operations.AuthorTableOperationsHandler#handleDeleteColumn(ro.sync.ecss.extensions.api.table.operations.AuthorTableDeleteColumnArguments)
   */
  @Override
  public boolean handleDeleteColumn(AuthorTableDeleteColumnArguments arguments)
      throws AuthorOperationException {
    if (deleteColumnOperation == null) {
      deleteColumnOperation = new DeleteColumnOperation();
    }
    return deleteColumnOperation.performDeleteColumn(arguments.getAuthorAccess(), 
        arguments.getColumnCellsIntervals(), false);
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.table.operations.AuthorTableOperationsHandler#handleDeleteRow(ro.sync.ecss.extensions.api.table.operations.AuthorTableDeleteRowArguments)
   */
  @SuppressWarnings({ "deprecation", "javadoc" })
  @Override
  public boolean handleDeleteRow(AuthorTableDeleteRowArguments arguments)
      throws AuthorOperationException {
    if (deleteRowOperation == null) {
      deleteRowOperation = new DeleteRowOperation();
    }
    ContentInterval rowInterval = arguments.getRowInterval();
    return deleteRowOperation.performDeleteRows(arguments.getAuthorAccess(), rowInterval.getStartOffset(), rowInterval.getEndOffset());
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.table.operations.AuthorTableOperationsHandler#handleDeleteRows(ro.sync.ecss.extensions.api.table.operations.AuthorTableDeleteRowsArguments)
   */
  @Override
  public boolean handleDeleteRows(AuthorTableDeleteRowsArguments arguments)
      throws AuthorOperationException {
    if (deleteRowOperation == null) {
      deleteRowOperation = new DeleteRowOperation();
    }
    return deleteRowOperation.performDeleteRows(arguments.getAuthorAccess(), arguments.getContentIntervals());
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.table.operations.AuthorTableOperationsHandler#getTableElementContainingOffset(ro.sync.ecss.extensions.api.AuthorAccess, int)
   */
  @Override
  public AuthorElement getTableElementContainingOffset(AuthorAccess access, int offset) {
    String tableElementName = TEIDocumentTypeHelper.ELEMENT_NAME_TABLE;
    return TableOperationsUtil.getTableElementContainingOffset(offset, namespace, 
        access, tableElementName);
  }
}
