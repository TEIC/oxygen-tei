/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2012 Syncro Soft SRL, Romania.  All rights
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

/**
 * Base for frameworks table customizers.
 * It is used on standalone implementation.
 * 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class TableCustomizer {
  
  /**
   * The last table info specified by the user. Session level persistence. 
   */
  protected TableInfo tableInfo;
  
  /**
   * Customize a table. 
   * <br/>
   * A table customizer dialog is shown, giving the possibility to choose the 
   * properties of a new table to be inserted in the document. An object containing 
   * the new table information is returned. 
   * 
   * @param authorAccess Access to Author operations.
   * @return The table information provided by the user or <code>null</code>
   * if customization operation is canceled.
   */
  public TableInfo customizeTable(AuthorAccess authorAccess) {
    return customizeTable(authorAccess, -1, -1);
  }
  
  /**
   * Customize a table. 
   * <br/>
   * A table customizer dialog is shown, giving the possibility to choose the 
   * properties of a new table to be inserted in the document. An object containing 
   * the new table information is returned. 
   * 
   * @param authorAccess Access to Author operations.
   * @param predefinedRowsCount The predefined number of rows, <code>-1</code> 
   * if the user can control the number of inserted column.
   * @param predefinedColumnsCount The predefined number of columns, <code>-1</code> 
   * if the user can control the number of inserted column.
   * If predefined columns count and predefined rows count values are positive 
   * then the dialog will not contain any field for defining the table columns
   * and rows count and the inserted table will use the predefined values.    
   * @return The table information provided by the user or <code>null</code>
   * if customization operation is canceled.
   */
  public TableInfo customizeTable(AuthorAccess authorAccess, int predefinedRowsCount, int predefinedColumnsCount) {
    TableInfo newTableInfo = showCustomizeTableDialog(authorAccess, predefinedRowsCount, predefinedColumnsCount);
    // Store the new table info only if not cancel pressed.
    if (newTableInfo != null) {
      int oldRowsCount = -1;
      int oldColumnsCount = -1;
      if (predefinedColumnsCount > 0 && predefinedRowsCount > 0) {
        if (tableInfo != null) {
          oldRowsCount = tableInfo.getRowsNumber();
          oldColumnsCount = tableInfo.getColumnsNumber();
        } else {
          oldRowsCount = TableInfo.DEFAULT_ROWS_COUNT;
          oldColumnsCount = TableInfo.DEFAULT_COLUMNS_COUNT;
        }
        
        // Create table info
        tableInfo = new TableInfo(
            newTableInfo.getTitle(), 
            oldRowsCount, 
            oldColumnsCount, 
            newTableInfo.isGenerateHeader(), 
            newTableInfo.isGenerateFooter(), 
            newTableInfo.getFrame(), 
            newTableInfo.getTableModel(), 
            newTableInfo.getColumnsWidthsType(),
            newTableInfo.getRowsep(),
            newTableInfo.getColsep(),
            newTableInfo.getAlign());
        
        // Update predefined rows count
        if (tableInfo.isGenerateHeader() || tableInfo.isGenerateFooter()) {
          if (tableInfo.isGenerateHeader()) {
            predefinedRowsCount = predefinedRowsCount - 1;
          }
          if (tableInfo.isGenerateFooter()) {
            predefinedRowsCount = predefinedRowsCount - 1;
          }
          if (predefinedRowsCount <= 0) {
            predefinedRowsCount = 1;
          }

          newTableInfo = new TableInfo(
              newTableInfo.getTitle(), 
              predefinedRowsCount, 
              predefinedColumnsCount, 
              newTableInfo.isGenerateHeader(), 
              newTableInfo.isGenerateFooter(), 
              newTableInfo.getFrame(), 
              newTableInfo.getTableModel(), 
              newTableInfo.getColumnsWidthsType(),
              newTableInfo.getRowsep(),
              newTableInfo.getColsep(),
              newTableInfo.getAlign());
        }
      } else {
        tableInfo = newTableInfo;
      }
    }
    return newTableInfo;
  }

  /**
   * Show table customizer dialog and return new table information.
   * 
   * @param authorAccess The Author access.
   * @param predefinedRowsCount Predefined number of rows.
   * @param predefinedColumnsCount Predefined number of columns.
   * @return  The table information provided by the user or null if customization 
   * operation is canceled.
   */
  protected abstract TableInfo showCustomizeTableDialog(
      AuthorAccess authorAccess,
      int predefinedRowsCount, 
      int predefinedColumnsCount);
}