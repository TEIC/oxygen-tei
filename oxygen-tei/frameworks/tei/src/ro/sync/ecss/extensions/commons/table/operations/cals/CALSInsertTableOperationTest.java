/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2016 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.table.operations.cals;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog;
import ro.sync.ecss.extensions.commons.table.operations.TableCustomizerConstants.ColumnWidthsType;
import ro.sync.ecss.extensions.dita.topic.table.SADITATableCustomizer;
import ro.sync.exml.options.Options;
import ro.sync.io.IOUtil;

/**
 * Test cases.
 * 
 * @author sorin_carbunaru
 */
public class CALSInsertTableOperationTest extends EditorAuthorExtensionTestBase  {
  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Options.clearInstanceTest();
    SADITATableCustomizer.clearForTests();
  }
  
  /**
   * <p><b>Description:</b> test the persistence of the "Generate title"
   * check box state.<p>
   * <p><b>Bug ID:</b> EXM-37027</p>
   * 
   * @throws Exception
   * 
   * @author sorin_carbunaru
   */
  public void testComponentsPersistenceDuringTheSameSession() throws Exception {
    SATableCustomizerDialog dialog = null;
    try {
      initEditor("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_ldd_b32_xw\">\n" + 
          "  <title></title>\n" + 
          "  <body>\n" + 
          "    <p>HERE</p>\n" + 
          "  </body>\n" + 
          "</topic>\n" + 
          "");

      // Move caret
      moveCaretRelativeTo("HERE", "HERE".length() + 1);

      // Invoke "Insert Table"
      new Thread() {
        @Override
        public void run() {
          invokeActionForID(ACTION_ID_INSERT_TABLE);
        }
      }.start();
      //Wait
      flushAWTBetter();

      dialog = (SATableCustomizerDialog) findDialog("Insert Table");
      assertNotNull(dialog);

      assertTrue(dialog.getTitleCheckbox().isSelected());

      dialog.getTitleCheckbox().setSelected(false);
      flushAWTBetter();

      sendKey(dialog, KeyEvent.VK_ENTER);
      flushAWTBetter();

      doUndo();
      flushAWTBetter();

      // Invoke "Insert Table"
      new Thread() {
        @Override
        public void run() {
          invokeActionForID(ACTION_ID_INSERT_TABLE);
        }
      }.start();
      //Wait
      flushAWTBetter();

      dialog = (SATableCustomizerDialog) findDialog("Insert Table");
      assertNotNull(dialog);

      assertFalse(dialog.getTitleCheckbox().isSelected());
    } finally {
      if (dialog != null) {
        dialog.setVisible(false);
        dialog.dispose();
      }
    }
  }
  
  /**
   * <p><b>Description:</b> test the persistence of the "Insert Table" settings.<p>
   * <p><b>Bug ID:</b> EXM-37027</p>
   * 
   * @throws Exception
   * 
   * @author sorin_carbunaru
   */
  public void testComponentsPersistenceBetweenSessions() throws Exception {
    SATableCustomizerDialog dialog = null;
    File configFile = null;
    try {
      // Save the options to a different file
      Options.getInstance("fake");
      
      initEditor("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_ldd_b32_xw\">\n" + 
          "  <title></title>\n" + 
          "  <body>\n" + 
          "    <p>HERE</p>\n" + 
          "  </body>\n" + 
          "</topic>\n" + 
          "");

      // Move caret
      moveCaretRelativeTo("HERE", "HERE".length() + 1);

      // Invoke "Insert Table"
      new Thread() {
        @Override
        public void run() {
          invokeActionForID(ACTION_ID_INSERT_TABLE);
        }
      }.start();
      //Wait
      flushAWTBetter();

      dialog = (SATableCustomizerDialog) findDialog("Insert Table");
      assertNotNull(dialog);

      // Check initial settings
      JCheckBox titleCheckbox = dialog.getTitleCheckbox();
      JSpinner rowsSpinner = dialog.getRowsSpinner();
      JSpinner columnsSpinner = dialog.getColumnsSpinner();
      JCheckBox headerCheckbox = dialog.getHeaderCheckbox();
      JComboBox colWidthsCombobox = dialog.getColWidthsCombobox();
      JComboBox frameCombo = dialog.getFrameCombo();
      JComboBox rowsepCombo = dialog.getRowsepCombo();
      JComboBox colsepCombo = dialog.getColsepCombo();
      JComboBox alignCombo = dialog.getAlignCombo();

      assertTrue(titleCheckbox.isSelected());
      assertEquals(3, rowsSpinner.getValue());
      assertEquals(2, columnsSpinner.getValue());
      assertTrue(headerCheckbox.isSelected());
      assertEquals("PROPORTIONAL_COL_WIDTHS", colWidthsCombobox.getSelectedItem().toString());
      assertEquals("all", frameCombo.getSelectedItem().toString());
      assertEquals("1", rowsepCombo.getSelectedItem().toString());
      assertEquals("1", colsepCombo.getSelectedItem().toString());
      assertEquals("<unspecified>", alignCombo.getSelectedItem().toString());

      // Change settings
      titleCheckbox.setSelected(false);
      rowsSpinner.setValue(4);
      columnsSpinner.setValue(4);
      headerCheckbox.setSelected(false);
      colWidthsCombobox.setSelectedItem(ColumnWidthsType.DYNAMIC_COL_WIDTHS);
      frameCombo.setSelectedItem("sides");
      rowsepCombo.setSelectedItem("0");
      colsepCombo.setSelectedItem("0");
      alignCombo.setSelectedItem("justify");
      flushAWTBetter();

      sendKey(dialog, KeyEvent.VK_ENTER);
      flushAWTBetter();

      // Save options on disk
      Options.getInstance().dumpConfigurationNow();

      // The configuration file contains the table options
      configFile = Options.getInstance().getCurrentVersionConfigFileForTests();
      String content = IOUtil.readFile(configFile);
      assertTrue(content.indexOf("TABLE_CUSTOMIZER_OPTIONS") != -1);

      // Undo table insertion
      doUndo();
      flushAWTBetter();

      // Clear customizer instance
      SADITATableCustomizer.clearForTests();
      
      // Clear instance in order to reload options from disk
      Options.clearInstanceTest();
      
      // Load from the "fake" configuration file
      Options.getInstance("fake");

      // Invoke "Insert Table"
      new Thread() {
        @Override
        public void run() {
          invokeActionForID(ACTION_ID_INSERT_TABLE);
        }
      }.start();
      //Wait
      flushAWTBetter();

      // Get the new dialog
      dialog = (SATableCustomizerDialog) findDialog("Insert Table");
      assertNotNull(dialog);
      
      // Get the components of the new dialog
      titleCheckbox = dialog.getTitleCheckbox();
      rowsSpinner = dialog.getRowsSpinner();
      columnsSpinner = dialog.getColumnsSpinner();
      headerCheckbox = dialog.getHeaderCheckbox();
      colWidthsCombobox = dialog.getColWidthsCombobox();
      frameCombo = dialog.getFrameCombo();
      rowsepCombo = dialog.getRowsepCombo();
      colsepCombo = dialog.getColsepCombo();
      alignCombo = dialog.getAlignCombo();

      assertFalse(titleCheckbox.isSelected());
      assertEquals(4, rowsSpinner.getValue());
      assertEquals(4, columnsSpinner.getValue());
      assertFalse(headerCheckbox.isSelected());
      assertEquals("DYNAMIC_COL_WIDTHS", colWidthsCombobox.getSelectedItem().toString());
      assertEquals("sides", frameCombo.getSelectedItem().toString());
      assertEquals("0", rowsepCombo.getSelectedItem().toString());
      assertEquals("0", colsepCombo.getSelectedItem().toString());
      assertEquals("justify", alignCombo.getSelectedItem().toString());
    } finally {
      if (configFile != null) {
        configFile.delete();
      }

      if (dialog != null) {
        dialog.setVisible(false);
        dialog.dispose();
      }
    
    }
  }
  
}
