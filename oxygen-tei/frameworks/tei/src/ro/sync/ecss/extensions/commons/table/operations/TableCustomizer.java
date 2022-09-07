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
package ro.sync.ecss.extensions.commons.table.operations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(TableCustomizer.class.getName());
  
  /**
   * The key for storing the table customizer options.
   */
  private static final String TABLE_CUSTOMIZER_OPTIONS_KEY = "TABLE_CUSTOMIZER_OPTIONS";
  
  /**
   * The last table info specified by the user. Session level persistence. 
   */
  protected TableInfo tableInfo;
  
  /**
   * Customize a table. 
   * <br>
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
   * <br>
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
    return customizeTable(authorAccess, predefinedRowsCount, predefinedColumnsCount, TableInfo.TABLE_MODEL_HTML);
  }

  /**
   * Show table customizer dialog and return new table information.
   * 
   * @param authorAccess The Author access.
   * @param predefinedRowsCount Predefined number of rows.
   * @param predefinedColumnsCount Predefined number of columns.
   * @param defaultTableModel The default model of the table that will be inserted.
   * @return  The table information provided by the user or null if customization 
   * operation is canceled.
   */
  protected abstract TableInfo showCustomizeTableDialog(
      AuthorAccess authorAccess,
      int predefinedRowsCount, 
      int predefinedColumnsCount, 
      int defaultTableModel);

  /**
   * Customize a table. 
   * <br>
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
   * @param defaultTableModel The default model of the table that will be inserted.
   * @return The table information provided by the user or <code>null</code>
   * if customization operation is canceled.
   */
  public TableInfo customizeTable(AuthorAccess authorAccess, int predefinedRowsCount, 
      int predefinedColumnsCount, int defaultTableModel) {
    if (tableInfo == null) {
      // The first time the customizer is shown in the current session,
      // load the options from the previous session, if possible
      String tableCustomizerOptions = authorAccess.getOptionsStorage().getOption(
          TABLE_CUSTOMIZER_OPTIONS_KEY,
          null);
      tableInfo = getTableInfoObject(tableCustomizerOptions);
    }
    
    TableInfo newTableInfo = showCustomizeTableDialog(authorAccess, predefinedRowsCount, predefinedColumnsCount, defaultTableModel);
    // Store the new table info only if not cancel pressed.
    if (newTableInfo != null) {
      int oldRowsCount = -1;
      int oldColumnsCount = -1;
      if (predefinedColumnsCount > 0 && predefinedRowsCount > 0 && 
          // Check that this values are not changed
          predefinedRowsCount != newTableInfo.getRowsNumber() &&
          predefinedColumnsCount != newTableInfo.getColumnsNumber()) {
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
      } else {
        tableInfo = newTableInfo;
      }
      
      String serializedTableInfo = serializeTableCustomizerOptions();
      if (serializedTableInfo != null) {
        authorAccess.getOptionsStorage().setOption(
            TABLE_CUSTOMIZER_OPTIONS_KEY,
            serializedTableInfo);
      }
    }
    return newTableInfo;
  }

  /**
   * Get the table info object corresponding to the given string serialization.
   * 
   * @param tableCustomizerOptions the options serialization to be deserialized.
   */
  private static TableInfo getTableInfoObject(String tableCustomizerOptions) {
    TableInfo tableInfoToRet = null;
    
    if (tableCustomizerOptions != null) {
      ObjectInputStream objectInputStream = null;
      try {
        byte [] data = DatatypeConverter.parseBase64Binary(tableCustomizerOptions);
        objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        tableInfoToRet = (TableInfo) objectInputStream.readObject();
      } catch (IOException | ClassNotFoundException e) {
        logger.error(e.getMessage(), e);
      } finally {
        // Make sure we try to close the stream
        if (objectInputStream != null) {
          try {
            objectInputStream.close();
          } catch (IOException e) {
            logger.error(e.getMessage(), e);
          }
        }
      }
    }
    
    return tableInfoToRet;
  }

  /**
   * Serialize the table info (the customizer's options).
   * 
   * @return the serialized options.
   * 
   * @throws IOException 
   */
  private String serializeTableCustomizerOptions() {
    String serializedOptions = null;
    ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
    ObjectOutputStream objOutStream = null;
    try {
      objOutStream = new ObjectOutputStream(byteArrayOutStream);
      objOutStream.writeObject(tableInfo);
      serializedOptions = DatatypeConverter.printBase64Binary(byteArrayOutStream.toByteArray()); 
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } finally {
      // Make sure we try to close the stream
      if (objOutStream != null) {
        try {
          objOutStream.close();
        } catch (IOException e) {
          logger.error(e.getMessage(), e);
        }
      }
    }
    return serializedOptions;
  }
}