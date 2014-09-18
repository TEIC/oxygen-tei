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
import ro.sync.ecss.extensions.commons.table.operations.TableCustomizerConstants.ColumnWidthsType;

/**
 * Contains information about the table element 
 * (number of rows, columns, table title).
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class TableInfo {
  
  /**
   * The title
   */
  private final String title;
  
  /**
   * Number of rows
   */
  private final int rowsNumber;
  
  /**
   * Number of cols
   */
  private final int columnsNumber;
  
  /**
   * <code>true</code> if should generate header.
   */
  private final boolean generateHeader;
  
  /**
   * <code>true</code> if should generate footer. 
   */
  private final boolean generateFooter;
  
  /**
   * Frame value.
   */
  private final String frame;
  
  /**
   * Row separator value.
   */
  private final String rowsep;
  
  /**
   * Column separator value.
   */
  private final String colsep;
  
  /**
   * Alignment value.
   */
  private final String align;
  
  /**
   * The table model. 
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   */
  private final int tableModel;
  
  /**
   * Constant for HTML table model.
   */
  public static final int TABLE_MODEL_HTML = 0;
  
  /**
   * Constant for CALS table model.
   */
  public static final int TABLE_MODEL_CALS = 1;
  
  /**
   * Constant for custom table model specific for a document type (proprietary table model).
   */
  public static final int TABLE_MODEL_CUSTOM = 2;
  
  /**
   * The simple table model for DITA.
   */
  public static final int TABLE_MODEL_DITA_SIMPLE = 3;
  
  /**
   * The choice table model for DITA.
   */
  public static final int TABLE_MODEL_DITA_CHOICE = 4;

  /**
   * The column widths type.
   */
  private final ColumnWidthsType columnsWidthsType;
  
  /**
   * Default number of rows
   */
  public static final int DEFAULT_ROWS_COUNT = 3;
  
  /**
   * Default number of columns for DITA choice table.
   */
  public static final int DEFAULT_COLUMNS_COUNT_CHOICE_TABLE = 2;
  
  /**
   * Default number of columns
   */
  public static final int DEFAULT_COLUMNS_COUNT = 2;
  
  /**
   * Constructor.
   * 
   * @param title The table title.
   * @param rowsNumber The number of rows.
   * @param columnsNumber The number of columns.
   * @param generateHeader If <code>true</code> generate table header.
   * @param generateFooter If <code>true</code> generate table footer.
   * @param frame Specifies how the table is to be framed.
   * @param tableModel The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   */
  public TableInfo(
      String title, 
      int rowsNumber, 
      int columnsNumber, 
      boolean generateHeader,
      boolean generateFooter, 
      String frame, 
      int tableModel) {
        this(title, rowsNumber, columnsNumber, generateHeader, generateFooter, 
            frame, tableModel, null, null, null, null);
  }

  /**
   * Constructor.
   * 
   * @param title The table title.
   * @param rowsNumber The number of rows.
   * @param columnsNumber The number of columns.
   * @param generateHeader If <code>true</code> generate table header.
   * @param generateFooter If <code>true</code> generate table footer.
   * @param frame Specifies how the table is to be framed.
   * @param tableModel The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * @param columnsWidthsType The columns widths type.
   * @param rowsep Specifies the row separator value.
   * @param colsep Specifies the column separator value
   * @param align  Specifies the alignment for the current table.
   */
  public TableInfo(
      String title, 
      int rowsNumber, 
      int columnsNumber, 
      boolean generateHeader,
      boolean generateFooter, 
      String frame, 
      int tableModel, 
      ColumnWidthsType columnsWidthsType,
      String rowsep,
      String colsep,
      String align) {
        this.title = title;
        this.rowsNumber = rowsNumber;
        this.columnsNumber = columnsNumber;
        this.generateHeader = generateHeader;
        this.generateFooter = generateFooter;
        this.frame = frame;
        this.tableModel = tableModel;
        this.columnsWidthsType = columnsWidthsType;
        this.rowsep = rowsep;
        this.colsep = colsep;
        this.align = align;
  }

  /**
   * Returns the title of the table. 
   * 
   * @return The title of the table.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Return the number of rows.
   * 
   * @return The number of rows.
   */
  public int getRowsNumber() {
    return rowsNumber;
  }

  /**
   * Return the number of columns.
   * 
   * @return The number of columns.
   */
  public int getColumnsNumber() {
    return columnsNumber;
  }

  /**
   * @return If <code>true</code> then table header will be generated.
   */
  public boolean isGenerateHeader() {
    return generateHeader;
  }

  /**
   * @return If <code>true</code> then table footer will be generated.
   */
  public boolean isGenerateFooter() {
    return generateFooter;
  }

  /**
   * @return Specifies the table frame.
   */
  public String getFrame() {
    return frame;
  }
  
  /**
   * Obtain the value for the row separator attribute.
   * 
   * @return Specifies the row separator value.
   */
  public String getRowsep() {
    return rowsep;
  }
  
  /**
   * Obtain the value for the column separator attribute.
   * 
   * @return Specifies the column separator value.
   */
  public String getColsep() {
    return colsep;
  }
  
  /**
   * Obtain the value for the alignment attribute.
   * 
   * @return Specifies the alignment value.
   */
  public String getAlign() {
    return align;
  }
  
  /**
   * @return Returns the table model. 
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   */
  public int getTableModel() {
    return tableModel;
  }
  
  /**
   * @return Returns the columns widths type(proportional, fixed, dynamic).
   */
  public ColumnWidthsType getColumnsWidthsType() {
    return columnsWidthsType;
  }
  
}