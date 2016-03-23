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

import java.util.Map;




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorOperationStoppedByUserException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.commons.table.operations.AbstractTableOperation;
import ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper;
import ro.sync.ecss.extensions.commons.table.operations.InsertTableOperationBase;
import ro.sync.ecss.extensions.commons.table.operations.TableCustomizerConstants;
import ro.sync.ecss.extensions.commons.table.operations.TableCustomizerConstants.ColumnWidthsType;
import ro.sync.ecss.extensions.commons.table.operations.TableInfo;
import ro.sync.ecss.extensions.commons.table.operations.TableOperationsUtil;
import ro.sync.exml.workspace.api.Platform;

/**
 * Operation used to insert a XHTML table.
 */

@WebappCompatible(false)
public class InsertTableOperation implements AuthorOperation, InsertTableOperationBase {

	/**
	 * Namespace for XHTML v.1.x.
	 */
  private static final String NAMESPACE = "http://www.w3.org/1999/xhtml";
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
    throws IllegalArgumentException, AuthorOperationException {
    Object tableInfoObj = args.getArgumentValue(
        AbstractTableOperation.TABLE_INFO_ARGUMENT_NAME);
    TableInfo tableInfo = tableInfoObj != null ? 
        new TableInfo((Map<String, Object>) tableInfoObj) : null; 
    insertTable(null, false, authorAccess, null, null, tableInfo);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertTableOperationBase#insertTable(ro.sync.ecss.extensions.api.node.AuthorDocumentFragment[], boolean, ro.sync.ecss.extensions.api.AuthorAccess, java.lang.String, ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper, ro.sync.ecss.extensions.commons.table.operations.TableInfo)
   */
  @Override
  public void insertTable(AuthorDocumentFragment[] fragments, boolean cellsFragments,
      AuthorAccess authorAccess, String namespace, AuthorTableHelper tableHelper,
      TableInfo tableInfo)
      throws AuthorOperationException {
    if (tableInfo == null) {
      int rowsCount = 0;
      int columnsCount = 0;
      if (fragments != null) {
        rowsCount = fragments.length;
        columnsCount = 1;
      }
      Platform platform = authorAccess.getWorkspaceAccess().getPlatform();
      if (Platform.STANDALONE.equals(platform)) {
        //SWING
        tableInfo = SAXHTMLTableCustomizerInvoker.getInstance().customizeTable(authorAccess, rowsCount, columnsCount);
      } else if (Platform.ECLIPSE.equals(platform)) {
        //SWT
        tableInfo = ECXHTMLTableCustomizerInvoker.getInstance().customizeTable(authorAccess, rowsCount, columnsCount);
      }
    }
    if (tableInfo != null) {
      // Insert the table.
      authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
          getTableXMLFragment(
              tableInfo, NAMESPACE, fragments, cellsFragments, authorAccess, tableHelper).toString(),
          authorAccess.getEditorAccess().getCaretOffset());
    } else {
      // User canceled the operation.
      throw new AuthorOperationStoppedByUserException("Cancelled by user");
    }
  }

  /**
   * Add table body.
   * 
   * @param tableXMLFragment  The string buffer representing the table XML fragment.
   *                          The table body fragment will be added to this table fragment.
   * @param tableInfo         The table info containing informations about the table rows and columns number.
   * 
   * @throws AuthorOperationException 
   */
  private static void addTableBody(StringBuilder tableXMLFragment, TableInfo tableInfo,
      AuthorDocumentFragment[] fragments, boolean cellsFragments, 
      AuthorAccess authorAccess, AuthorTableHelper tableHelper, String namespace) throws AuthorOperationException {
    tableXMLFragment.append("<tbody>");
    for (int i = 0; i < tableInfo.getRowsNumber(); i++) {
      tableXMLFragment.append("<tr>");
      for (int j = 0; j < tableInfo.getColumnsNumber(); j++) {
        if (fragments != null) {
          int index = i;
          if (tableInfo.isGenerateFooter()) {
            index++;
          }
          if (tableInfo.isGenerateHeader()) {
            index++;
          }
          String cellXMLFragment = TableOperationsUtil.createCellXMLFragment(
              authorAccess, 
              fragments, 
              cellsFragments, 
              "td", 
              index, 
              namespace, 
              tableHelper);
          tableXMLFragment.append(cellXMLFragment);
        } else {
          tableXMLFragment.append("<td></td>"); 
        }
      }
      tableXMLFragment.append("</tr>"); 
    }
    tableXMLFragment.append("</tbody>");
  }

  /**
   * Add table column specifications. The table will be inserted with proportional column widths.
   * 
   * @param tableXMLFragment  The string buffer representing the table XML fragment.
   *                          The table columns specification fragment will be added to this table fragment.
   * @param tableInfo         The table info containing informations about the table columns number 
   */
  private static void addTableCols(StringBuilder tableXMLFragment, TableInfo tableInfo) {
    ColumnWidthsType columnsWidthsType = tableInfo.getColumnsWidthsType();
    if (columnsWidthsType != ColumnWidthsType.DYNAMIC_COL_WIDTHS) {
      String colWidth = null;
      if (columnsWidthsType == ColumnWidthsType.PROPORTIONAL_COL_WIDTHS) {
        float proportionalWidth = (float)100 / tableInfo.getColumnsNumber();
        // Proportional widths
        colWidth = (int)(Math.round(proportionalWidth * 100.0) / 100.0) + "%";
      } else {
        // Fixed widths
        colWidth = TableCustomizerConstants.FIXED_COL_WIDTH_DEFAULT_VALUE;
      }
      for (int i = 1; i <= tableInfo.getColumnsNumber(); i++) {
        tableXMLFragment.append("<col width=\"" + colWidth + "\"/>");
      }
    }
  }
  
  /**
   * Add table footer.
   * 
   * @param tableXMLFragment  The string buffer representing the table XML fragment.
   *                          The table footer fragment will be added to this table fragment.
   * @param tableInfo The table info containing informations about the table columns number.
   * 
   * @throws AuthorOperationException 
   */
  private static void addTableFooter(StringBuilder tableXMLFragment, TableInfo tableInfo, 
      AuthorDocumentFragment[] fragments, boolean cellsFragments, AuthorAccess authorAccess, 
      AuthorTableHelper tableHelper, String namespace) throws AuthorOperationException {
    if (tableInfo.isGenerateFooter()) {
      tableXMLFragment.append("<tfoot><tr>");
      for (int i = 1; i <= tableInfo.getColumnsNumber(); i++) {
        if (i == 1 && fragments != null && fragments.length > 0) {
          int index = tableInfo.isGenerateHeader() ? 1 : 0;
          String cellXMLFragment = TableOperationsUtil.createCellXMLFragment(
              authorAccess, 
              fragments, 
              cellsFragments, 
              "td", 
              index, 
              namespace, 
              tableHelper);
          tableXMLFragment.append(cellXMLFragment);
        } else {
          tableXMLFragment.append("<td></td>");
        }
      }
      tableXMLFragment.append("</tr></tfoot>");
    }
  }

  /**
   * Add table header.
   * 
   * @param tableXMLFragment  The string buffer representing the table XML fragment.
   *                          The table header fragment will be added to this table fragment.
   * @param tableInfo         The table info containing informations about the table columns number.
   * 
   * @throws AuthorOperationException 
   */
  private static void addTableHeader(StringBuilder tableXMLFragment, TableInfo tableInfo, 
      AuthorDocumentFragment[] fragments, boolean cellsFragments, AuthorAccess authorAccess, 
      AuthorTableHelper tableHelper, String namespace) throws AuthorOperationException {
    if (tableInfo.isGenerateHeader()) {
      tableXMLFragment.append("<thead><tr>");
      for (int i = 1; i <= tableInfo.getColumnsNumber(); i++) {
        if (i == 1 && fragments != null && fragments.length > 0) {
          String cellXMLFragment = TableOperationsUtil.createCellXMLFragment(
              authorAccess, 
              fragments, 
              cellsFragments, 
              "th", 
              0, 
              namespace, 
              tableHelper);
          tableXMLFragment.append(cellXMLFragment);
        } else {
          tableXMLFragment.append("<th></th>");
        }
      }
      tableXMLFragment.append("</tr></thead>");
    }
  }

  /**
   * No arguments. The operation will display a dialog for choosing the table attributes.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Insert a XHTML table";
  }

  /**
   * Compute the XML fragment for the table to be inserted.
   *  
   * @param tableInfo The table information.
   * @param namespace The table element namespace.
   *
   * @return The XML fragment for the table to be inserted.
   * 
   * @throws AuthorOperationException 
   */
  private static StringBuilder getTableXMLFragment(TableInfo tableInfo, String namespace, 
      AuthorDocumentFragment[] fragments, boolean cellsFragments, 
      AuthorAccess authorAccess, AuthorTableHelper tableHelper) throws AuthorOperationException {
    // Create the table XML fragment.
    StringBuilder tableXMLFragment = new StringBuilder();

    // Table element.
    // Title was specified, insert a table with title.
    tableXMLFragment.append("<table");
    tableXMLFragment.append(" xmlns=\"").append(namespace).append("\"");
    // EXM-23110 Don't insert the frame attribute if the user specifies 
    // "<unspecified>" as value for frame attribute
    if (tableInfo.getFrame() != null) {
      tableXMLFragment.append(" frame=\"").append(tableInfo.getFrame()).append("\"");
    }
    // EXM-29536 Add align attribute
    if (tableInfo.getAlign() != null) {
      tableXMLFragment.append(" align=\"").append(tableInfo.getAlign()).append("\"");
    }
    
    tableXMLFragment.append(">");

    if (tableInfo.getTitle() != null) {
      tableXMLFragment.append("<caption>").append(authorAccess.getXMLUtilAccess().escapeTextValue(tableInfo.getTitle())).append("</caption>");
    }

    // Add table column specifications.
    addTableCols(tableXMLFragment, tableInfo);

    // Add table header.
    addTableHeader(tableXMLFragment, tableInfo, fragments, cellsFragments, authorAccess, tableHelper, namespace);

    // Add table footer.
    addTableFooter(tableXMLFragment, tableInfo, fragments, cellsFragments, authorAccess, tableHelper, namespace);

    // Add table body.
    addTableBody(tableXMLFragment, tableInfo, fragments, cellsFragments, authorAccess, tableHelper, namespace);

    tableXMLFragment.append("</table>");
    
    return tableXMLFragment;
  }
}