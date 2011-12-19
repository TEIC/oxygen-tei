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

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.commons.table.operations.TableInfo;

/**
 * The operation used to insert a TEI table. 
 */
public class InsertTableOperation implements AuthorOperation {
  
  /**
   * Argument specifying a default namespace for the table.
   * The value is <code>defaultNamespace</code>
   */
  private static final String ARGUMENT_NAME = "defaultNamespace";
  
  /**
   * Arguments.
   */
  private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
    new ArgumentDescriptor(ARGUMENT_NAME, ArgumentDescriptor.TYPE_STRING, "The table namespace")
  };
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    // Show the 'Insert table' dialog
    TableInfo tableInfo = null;
    if(authorAccess.getWorkspaceAccess().isStandalone()) {
      tableInfo = new SATEITableCustomizer().customizeTable(authorAccess);
    } else {
      tableInfo = new ECTEITableCustomizer().customizeTable(authorAccess);
    }
    if (tableInfo != null) {
      // Create the table XML fragment
      StringBuffer tableXMLFragment = new StringBuffer();
      // Table element
      tableXMLFragment.append("<table ");
      Object defaultNamespaceObj =  args.getArgumentValue(ARGUMENT_NAME);
      if (defaultNamespaceObj != null && defaultNamespaceObj instanceof String) {
        tableXMLFragment.append("xmlns=\"").append(defaultNamespaceObj).append("\" ");
      }
      tableXMLFragment.append("rows=\"").append(tableInfo.getRowsNumber()).append("\" cols=\"").
      append(tableInfo.getColumnsNumber()).append("\">");
      if(tableInfo.getTitle() != null) {
        // Title was specified, insert a table with title
        tableXMLFragment.append("<head>" + tableInfo.getTitle() + "</head>");
      }
      
      if (tableInfo.isGenerateHeader()) {
        // Add table header
        addTableHeader(tableXMLFragment, tableInfo);
      }
      
      // Add table body
      addTableBody(tableXMLFragment, tableInfo);
      
      tableXMLFragment.append("</table>");
      
      // Insert the table 
      authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
          tableXMLFragment.toString(), 
          authorAccess.getEditorAccess().getCaretOffset());
    } else {
      // User canceled the operation 
    }
  }

  /**
   * Add the body of this table.
   * 
   * @param tableXMLFragment The table XML fragment buffer to which to add the 
   * table body representation.
   * @param tableInfo Information about the table.
   */
  private void addTableBody(StringBuffer tableXMLFragment, TableInfo tableInfo) {
    int rows = tableInfo.getRowsNumber();
    int cols = tableInfo.getColumnsNumber();
    for (int i = 0; i < rows; i++) {
      tableXMLFragment.append("<row>");
      for (int j = 0; j < cols; j++) {
        tableXMLFragment.append("<cell/>");
      }
      tableXMLFragment.append("</row>");
    }
  }

  /**
   * Add a header to the table.
   * 
   * @param tableXMLFragment The table XML fragment buffer to which to add the 
   * table header representation.
   * @param tableInfo Information about the table.
   */
  private void addTableHeader(StringBuffer tableXMLFragment, TableInfo tableInfo) {
    tableXMLFragment.append("<row role=\"label\">");
    int cols = tableInfo.getColumnsNumber();
    for (int i = 1; i <= cols; i++) {
      tableXMLFragment.append("<cell/>");
    }
    tableXMLFragment.append("</row>");
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  public String getDescription() {
    return "Insert a TEI table";
  }
}