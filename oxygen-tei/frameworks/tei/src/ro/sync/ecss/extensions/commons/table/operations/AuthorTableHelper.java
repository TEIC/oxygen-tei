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

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Document type specific table information helper. 
 * It contains methods that are specific to a document type and are used to obtain
 * table and table cells related information. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public interface AuthorTableHelper {
  /**
   * The cell type.
   */
  int TYPE_CELL = 0;
  /**
   * The row type.
   */
  int TYPE_ROW = 1;
  /**
   * The table type.
   */
  int TYPE_TABLE = 2;
  
  /**
   * Check if an {@link AuthorNode} is a table cell node. 
   * 
   * @param node The {@link AuthorNode} to be checked.
   * @return <code>true</code> if the node is a table cell node, <code>false</code> otherwise.
   */
  boolean isTableCell(AuthorNode node);

  /**
   * Check if an {@link AuthorNode} is a table row node. 
   * 
   * @param node The {@link AuthorNode} to be checked.
   * @return <code>true</code> if the node is a table row node, <code>false</code> otherwise.
   */
  boolean isTableRow(AuthorNode node);
  
  /**
   * Check if an {@link AuthorNode} is a table node.
   * 
   * @param node The {@link AuthorNode} to be checked.
   * @return <code>true</code> if the node is a table node, <code>false</code> otherwise.
   */
  boolean isTable(AuthorNode node);

  /**
   * Create the table cell span provider for a specific table element.
   * 
   * @param tableElement The element rendered as a table. Its 'display' CSS property
   * is set to 'table'. 
   * @return  The table cell span provider. Must not be <code>null</code>.
   */
  AuthorTableCellSpanProvider getTableCellSpanProvider(AuthorElement tableElement);
  
  /**
   * Check if the column span is defined for a table cell.
   * <p> I.E. for DocBook the column span is defined by the 'colspec' element. 
   * If it is missing then the column span is not defined.
   * 
   * @param authorAccess The author access. 
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param tableCellSpanProvider The table cell span provider.
   * @param cellElement  The cell element to be tested.
   * @throws AuthorOperationException When the column span is not defined for the table cell.
   */
  void checkTableColSpanIsDefined(
      AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableCellSpanProvider, 
      AuthorElement cellElement) throws AuthorOperationException;

  /**
   * Update the column span of the cell by modifying the indices of start and end column.
   * For example, for the DocBook CALS tables  the <code>namest</code> and <code>nameend</code> 
   * attributes will be set according to the <code>startCol</code> and <code>endCol</code> supplied values.    
   * 
   * @param authorAccess The author access. 
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param tableCellSpanProvider The object responsible for providing information 
   * about the cell spanning.
   * @param cellElem The cell element whose column span will be updated.
   * @param startCol The new index of start column. It is 1 based and inclusive.
   * @param endCol The new index of end column. It is 1 based and inclusive.
   * @throws AuthorOperationException  When the column specifications 
   * for start or end columns are missing.  
   */
  void updateTableColSpan(
      AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableCellSpanProvider,
      AuthorElement cellElem,
      int startCol,
      int endCol) throws AuthorOperationException;
  
  /**
   * Updates the cell row span to a specified value.
   * For example, for the DocBook CALS tables the <code>morerows</code> attribute 
   * value will be updated.
   * 
   * @param authorAccess The author access. 
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param cellElem The cell element whose row span will be updated.
   * @param rowSpan The new row span value. It is 1 based.
   */
  void updateTableRowSpan(AuthorAccess authorAccess, AuthorElement cellElem, int rowSpan);
  
  /**
   * Update the table columns number.
   * For example, for the DocBook CALS tables the <code>cols</code> attribute 
   * value will be updated.
   * 
   * @param authorAccess The author access. 
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param tableElement The element rendered as a table. Its 'display' CSS property
   * is set to 'table'.
   * @param colNum The updated number of columns.
   */
  void updateTableColumnNumber(AuthorAccess authorAccess, AuthorElement tableElement, int colNum);

  /**
   * Update the table rows number.
   * 
   * @param authorAccess The author access. 
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param tableElement The element rendered as a table. Its 'display' CSS property
   * is set to 'table'.
   * @param relativeValue The number of rows to increase or decrease the current number of table rows.
   * If the number of rows must be decreased then the argument must be negative.
   */
  void updateTableRowNumber(AuthorAccess authorAccess, AuthorElement tableElement, int relativeValue);
  
  /**
   * Gets the attributes which should be skipped when using the current row as 
   * template for insert operation.
   * 
   * @return The attributes which should be skipped.
   */
  String[] getIgnoredRowAttributes();
  
  /**
   * Gets the attributes which should be skipped when inserting a new column and
   *  the attributes from source cell fragments must be copied.
   * 
   * @return The attributes which should be skipped.
   */
  String[] getIgnoredColumnAttributes();
}