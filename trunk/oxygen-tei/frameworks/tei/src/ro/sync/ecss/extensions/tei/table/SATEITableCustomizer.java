/**
 * Copyright 2011 Syncro Soft SRL, Romania. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:

 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 * 
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Syncro Soft SRL ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Syncro Soft SRL OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Syncro Soft SRL.
 */
package ro.sync.ecss.extensions.tei.table;

import java.awt.Component;
import java.awt.Frame;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog;
import ro.sync.ecss.extensions.commons.table.operations.TableInfo;

/**
 * Customize a TEI table. It is used on standalone implementation.
 */
public class SATEITableCustomizer {
  /**
   * The last table info specified by the user. Session level persistence. 
   */
  private static TableInfo tableInfo;

  /**
   * Ask the user to customize a new table.
   * 
   * @param authorAccess Access to author functionality.
   * @return The information from the user or <code>null</code> if canceled.
   */
  public TableInfo customizeTable(AuthorAccess authorAccess) {
    SATableCustomizerDialog tableCustomizerDialog = new SATEITableCustomizerDialog(
        (Frame) authorAccess.getWorkspaceAccess().getParentFrame());
    tableCustomizerDialog.setLocationRelativeTo((Component) authorAccess.getWorkspaceAccess().getParentFrame());
    TableInfo newTableInfo = tableCustomizerDialog.showDialog(tableInfo);
    
    // Store the new table info only if not cancel pressed.
    if (newTableInfo != null) {
      tableInfo = newTableInfo;
    }
    return newTableInfo;
  }
}