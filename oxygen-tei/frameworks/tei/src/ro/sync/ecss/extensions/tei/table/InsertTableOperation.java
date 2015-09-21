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
package ro.sync.ecss.extensions.tei.table;

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
import ro.sync.ecss.extensions.commons.table.operations.TableInfo;
import ro.sync.ecss.extensions.commons.table.operations.TableOperationsUtil;
import ro.sync.exml.workspace.api.Platform;

/**
 * The operation used to insert a TEI table. 
 */

@WebappCompatible(false)
public class InsertTableOperation implements AuthorOperation, InsertTableOperationBase {
  
  /**
   * Argument specifying a default namespace for the table.
   * The value is <code>defaultNamespace</code>
   */
  private static final String ARGUMENT_NAME = "defaultNamespace";
  
  /**
   * Arguments.
   */
  private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
    new ArgumentDescriptor(ARGUMENT_NAME, ArgumentDescriptor.TYPE_STRING, "The table namespace"),
    AbstractTableOperation.TABLE_INFO_ARGUMENT_DESCRIPTOR
  };
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    Object defaultNamespaceObj =  args.getArgumentValue(ARGUMENT_NAME);
    String namespace = null;
    if (defaultNamespaceObj != null && defaultNamespaceObj instanceof String) {
      namespace = (String) defaultNamespaceObj;
    }
    Object tableInfoObj = args.getArgumentValue(
        AbstractTableOperation.TABLE_INFO_ARGUMENT_NAME);
    TableInfo tableInfo = tableInfoObj != null ? 
        new TableInfo((Map<String, Object>) tableInfoObj) : null; 
    insertTable(null, false, authorAccess, namespace, null, tableInfo);
  }

  /**
   * Add the body of this table.
   * 
   * @param tableXMLFragment  The table XML fragment buffer to which to add the table body representation.
   * @param tableInfo         Information about the table.
   * @param fragments         An array of {@link AuthorDocumentFragment}s that are used as content
   *                          of the inserted cells.  
   * @param cellsFragments    If the value is <code>true</code> then the fragments where originally cells. 
   * @param authorAccess      The author access.
   * @param tableHelper       Table helper.
   * @param namespace         The namespace.
   * 
   * @throws AuthorOperationException 
   */
  private void addTableBody(StringBuilder tableXMLFragment, TableInfo tableInfo, 
      AuthorDocumentFragment[] fragments, boolean cellsFragments, 
      AuthorAccess authorAccess, AuthorTableHelper tableHelper, String namespace) throws AuthorOperationException {
    int rows = tableInfo.getRowsNumber();
    int cols = tableInfo.getColumnsNumber();

    for (int i = 0; i < rows; i++) {
      tableXMLFragment.append("<row>");
      for (int j = 0; j < cols; j++) {
        if (j == 0 && fragments != null) {
          String cellXMLFragment = TableOperationsUtil.createCellXMLFragment(
              authorAccess, 
              fragments, 
              cellsFragments, 
              "cell", 
              tableInfo.isGenerateHeader() ? (i + 1) : i, 
              namespace, 
              tableHelper);
          tableXMLFragment.append(cellXMLFragment);
        } else {
          tableXMLFragment.append("<cell/>");
        }
      }
      tableXMLFragment.append("</row>");
    }
  }

  /**
   * Add a header to the table.
   * 
   * @param tableXMLFragment  The table XML fragment buffer to which to add the table header representation.
   * @param tableInfo         Information about the table.
   * @param namespace         The namespace.
   * @param tableHelper       Table helper.
   * @param authorAccess      Author access.
   * @param cellsFragments    <code>true</code> if the fragments are wrapped in cells.
   * @param fragments         Cells fragments.
   *
   * @throws AuthorOperationException 
   */
  private void addTableHeader(StringBuilder tableXMLFragment, TableInfo tableInfo, 
      AuthorDocumentFragment[] fragments, boolean cellsFragments, 
      AuthorAccess authorAccess, AuthorTableHelper tableHelper, String namespace) throws AuthorOperationException {
    tableXMLFragment.append("<row role=\"label\">");

    int cols = tableInfo.getColumnsNumber();
    for (int i = 1; i <= cols; i++) {
      if (i == 1 && fragments != null) {
        String cellXMLFragment = TableOperationsUtil.createCellXMLFragment(
            authorAccess, 
            fragments, 
            cellsFragments, 
            "cell", 
            0, 
            namespace, 
            tableHelper);
        tableXMLFragment.append(cellXMLFragment);
      } else {
        tableXMLFragment.append("<cell/>");
      }
    }
    tableXMLFragment.append("</row>");
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Insert a TEI table";
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
      // Show the 'Insert table' dialog
      Platform platform = authorAccess.getWorkspaceAccess().getPlatform();
      if(Platform.STANDALONE.equals(platform)) {
        tableInfo = SATEITableCustomizer.getInstance().customizeTable(
            authorAccess, rowsCount, columnsCount);
      } else if (Platform.ECLIPSE.equals(platform)) {
        tableInfo = ECTEITableCustomizer.getInstance().customizeTable(
            authorAccess, rowsCount, columnsCount);
      }
    }
    if (tableInfo != null) {
      // Create the table XML fragment
      StringBuilder tableXMLFragment = new StringBuilder();
      // Table element
      tableXMLFragment.append("<table ");
      if (namespace != null) {
        tableXMLFragment.append("xmlns=\"").append(namespace).append("\" ");
      }
      tableXMLFragment.append("rows=\"").append(tableInfo.getRowsNumber()).append("\" cols=\"").
      append(tableInfo.getColumnsNumber()).append("\">");
      if(tableInfo.getTitle() != null) {
        // Title was specified, insert a table with title
        tableXMLFragment.append("<head>" + tableInfo.getTitle() + "</head>");
      }

      if (tableInfo.isGenerateHeader()) {
        // Add table header
        addTableHeader(tableXMLFragment, tableInfo, fragments, cellsFragments, 
            authorAccess, tableHelper, namespace);
      }

      // Add table body
      addTableBody(tableXMLFragment, tableInfo, fragments, cellsFragments, 
          authorAccess, tableHelper, namespace);

      tableXMLFragment.append("</table>");

      // Insert the table 
      authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
          tableXMLFragment.toString(), 
          authorAccess.getEditorAccess().getCaretOffset());
    } else {
      // User canceled the operation 
      throw new AuthorOperationStoppedByUserException("Cancelled by user");
    }
  }
}