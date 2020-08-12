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
package ro.sync.ecss.extensions.commons.table.properties.test;

import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import ro.sync.ecss.extensions.commons.table.properties.TablePropertiesConstants;
import ro.sync.exml.IDEAccess;
import ro.sync.exml.IDEAccessAdapter;
import ro.sync.util.URLUtil;


/**
 * Test class for DITA table properties.
 *  
 * @author adriana_sbircea
 */
public class DITAShowTablePropertiesTest extends TablePropertiesTestBase {

  /**
   * <p><b>Description:</b> Test for transform row from caret position from body to header (header exists).
   * Header exists and has a row.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromBodyToHeader() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS.dita")), true);
  
    moveCaretRelativeTo("C3", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeBodyRadioBtn.isSelected());
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    
    rowTypeHeaderRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        // Second row from body should be added as last row in header element.
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for modify attr for row from caret position.
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyAttrs() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS.dita")), true);
  
    moveCaretRelativeTo("C1", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    assertNotNull(tabbedPane);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeBodyRadioBtn.isSelected());
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowSepCombo.setSelectedItem("1");
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row rowsep=\"1\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for modify attr for row from caret position.
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionRemoveAtrr() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS1.dita")), true);
  
    moveCaretRelativeTo("C1", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeBodyRadioBtn.isSelected());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals("1", rowSepCombo.getSelectedItem());
    
    rowSepCombo.setSelectedItem(TablePropertiesConstants.ATTR_NOT_SET);
    sleep(200);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        // Row should have no attributes because the combo selection is ""
        "                        <row>\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from header to body (body exists).
   * All children of header are removed, so the header element should be also removed.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromHeaderToBody() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS.dita")), true);
  
    moveCaretRelativeTo("HEADER COL 1", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");

    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());

    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        // No header element because all its children were moved.
        "                    <tbody>\n" + 
        // The row from header is now in tbody
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from header to body (body does not exist).
   * Body element should be also created in the correct place.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromHeaderToBody2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS3.dita")), true);
  
    moveCaretRelativeTo("C4", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());

    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row rowsep=\"1\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        // TBody should be created and the row should be moved inside the body element.
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from body to header (header does not exist).
   * Header element should be also created in the correct place.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromBodyToHead2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS2.dita")), true);
  
    moveCaretRelativeTo("C4", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeBodyRadioBtn.isSelected());
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowTypeHeaderRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        // The header should be inserted after colspec elementes
        "                    <thead>\n" + 
        "                        <row>\n" + 
        // The row from body should be moved to header
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row rowsep=\"1\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from body to header (header does not exist).
   * Header element should be also created in the correct place.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromBodyToHeader3() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS5.dita")), true);
  
    moveCaretRelativeTo("C4", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeBodyRadioBtn.isSelected());
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowTypeHeaderRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" +
        // Thead should be inserted as first child
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row rowsep=\"1\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from header to body (body does not exist).
   * Body element should be also created in the correct place.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionBetweenRows() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS6.dita")), true);
  
    moveCaretRelativeTo("CALS 1", 1);
    moveCaretRelativeTo("C2", 4, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Rows");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.PRESERVE, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "  <title>Topic title</title>\n" + 
        "  <body>\n" + 
        "    <p>CALS 1</p>\n" + 
        "    <p>\n" + 
        "      <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "        <title/>\n" + 
        "        <tgroup cols=\"2\">\n" + 
        "          <tbody>\n" + 
        "            <row rowsep=\"1\">\n" + 
        "              <entry>C1</entry>\n" + 
        "              <entry>C2</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>C3</entry>\n" + 
        "              <entry>C4</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>C5</entry>\n" + 
        "              <entry>C6</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>C7</entry>\n" + 
        "              <entry>C8</entry>\n" + 
        "            </row>\n" + 
        "          </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table>\n" + 
        "    </p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for modify attr for row with one row selected.
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionRemoveAtrrSingleSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS1.dita")), true);
  
    moveCaretRelativeTo("CALS 1", 1);
    moveCaretRelativeTo("C1", -3, false);
    moveCaretRelativeTo("C2", 3, true);
    
    flushAWTBetter();
    sleep(1000);
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeBodyRadioBtn.isSelected());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    JRadioButton preserveRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.PRESERVE);
    assertNull("<preserve> should not be included in the combo", preserveRadioButton);
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals("Only one row is selected, should not be added '<preserve>' value to combo", "1", rowSepCombo.getSelectedItem());
    
    String expected = "0 1 <not set> ";
    StringBuffer actual = new StringBuffer();
    for (int i = 0; i < rowSepCombo.getItemCount(); i++) {
      actual.append(rowSepCombo.getItemAt(i)).append(" ");
    }
    assertEquals("<preserve> should not be included in the combo", expected, actual.toString());
    
    
    rowSepCombo.setSelectedItem(TablePropertiesConstants.ATTR_NOT_SET);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        // Row should have no attributes because the combo selection is ""
        "                        <row>\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from body to header (header does not exist).
   * Header element should be also created in the correct place.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromBodyToHeaderMultipleSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS5.dita")), true);
  
    moveCaretRelativeTo("CALS 1", 1);
    moveCaretRelativeTo("C1", 1, false);
    moveCaretRelativeTo("C6", 1, true);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Rows");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeBodyRadioBtn.isSelected());
    rowTypeHeaderRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <thead>\n" + 
        "                        <row rowsep=\"1\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from header to body (body does not exist).
   * Body element should be also created in the correct place.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromHeadToBodyMultipleSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS6.dita")), true);
  
    moveCaretRelativeTo("CALS 1", 1);
    moveCaretRelativeTo("C1", 1, false);
    moveCaretRelativeTo("C6", 1, true);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Rows");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals("<preserve>", rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    JRadioButton rowAlignTopRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.TOP);
    assertNotNull(rowAlignTopRadioButton);
    assertTrue("All the rows have the same value for valign, so '<preserve>' should not be added",
        rowAlignNotSetRadioButton.isSelected());
    
    rowAlignTopRadioButton.setSelected(true);
    sleep(200);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "  <title>Topic title</title>\n" + 
        "  <body>\n" + 
        "    <p>CALS 1</p>\n" + 
        "    <p>\n" + 
        "      <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "        <title/>\n" + 
        "        <tgroup cols=\"2\">\n" + 
        "          <thead>\n" + 
        // valign should be set to top
        "            <row rowsep=\"1\" valign=\"top\">\n" + 
        "              <entry>C1</entry>\n" + 
        "              <entry>C2</entry>\n" + 
        "            </row>\n" + 
        // valign should be set to top
        "            <row valign=\"top\">\n" + 
        "              <entry>C3</entry>\n" + 
        "              <entry>C4</entry>\n" + 
        "            </row>\n" + 
        // valign should be set to top
        "            <row valign=\"top\">\n" + 
        "              <entry>C5</entry>\n" + 
        "              <entry>C6</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>C7</entry>\n" + 
        "              <entry>C8</entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "        </tgroup>\n" + 
        "      </table>\n" + 
        "    </p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for modify row type for a selection which includes 
   * two or more entries from the same row.
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowsSelFromHeadToBody() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS.dita")), true);
  
    moveCaretRelativeTo("CALS 1", 1);
    moveCaretRelativeTo("HEADER COL 1", 1, false);
    moveCaretRelativeTo("HEADER COL 2", 1, true);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(1000);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from header to body (body does not exist).
   * Body element should be also created in the correct place.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromHeadToBody3() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS6.dita")), true);
  
    moveCaretRelativeTo("C4", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);

    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <thead>\n" + 
        "                        <row rowsep=\"1\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        // Tbody is the last child
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test if the user sets a value for an attr which is not
   * from schema, the combo for that attr presents '<preserved>' value in combo.
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowContainsAttrWithValueNotFromSchema() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS7.dita")), true);
  
    moveCaretRelativeTo("CALS 1", 1);
    moveCaretRelativeTo("C1", 1, false);
    moveCaretRelativeTo("C7", 1, true);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Rows");
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, 
        TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    JRadioButton rowAlignTopRadioButton = findRadioButton(tabbedPane, 
        TablePropertiesConstants.TOP);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    rowAlignTopRadioButton.setSelected(true);
    findButtonAndClick(dialog, "Cancel");
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row rowsep=\"5\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from body and header to body (body exists).
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testChangeRowTypeForRowsWithDifferentTypes() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS.dita")), true);
  
    moveCaretRelativeTo("CALS 1", 1);
    moveCaretRelativeTo("HEADER COL 1", 1, false);
    moveCaretRelativeTo("C1", 2, true);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Rows");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    JRadioButton rowTypePreserveRadioBtn = findRadioButton(tabbedPane, TablePropertiesConstants.PRESERVE);
    assertNotNull(rowTypeBodyRadioBtn);
    assertNotNull(rowTypePreserveRadioBtn);
    // Header should be selected
    assertTrue(rowTypePreserveRadioBtn.isSelected());

    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }


  /**
   * <p><b>Description:</b> Test for simple table.
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSimpleTableChangeRowTypeForRowsWithDifferentTypes2() throws Exception {
    final StringBuilder sb = new StringBuilder();
    IDEAccess.setInstance(new IDEAccessAdapter() {
    /**
     * @see ro.sync.exml.IDEAccessAdapter#showErrorMessage(java.lang.String)
     */
    @Override
    public void showErrorMessage(String message) {
      sb.append(message);
    }
    });
    open(URLUtil.correct(new File("test/EXM-29474/ditaSimple.dita")), true);
  
    moveCaretRelativeTo("A", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_gl1_b5w_2n\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>\n" + 
        "            <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"simpletable_h31_25w_2n\">\n" + 
        "                <strow>\n" + 
        "                    <stentry>A</stentry>\n" + 
        "                    <stentry>B</stentry>\n" + 
        "                </strow>\n" + 
        "                <strow>\n" + 
        "                    <stentry>A1</stentry>\n" + 
        "                    <stentry>B1</stentry>\n" + 
        "                </strow>\n" + 
        "                <strow>\n" + 
        "                    <stentry>A2</stentry>\n" + 
        "                    <stentry>B2</stentry>\n" + 
        "                </strow>\n" + 
        "                <strow>\n" + 
        "                    <stentry>A3</stentry>\n" + 
        "                    <stentry>B3</stentry>\n" + 
        "                </strow>\n" + 
        "            </simpletable>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for simple table
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRelTableChangeRowTypeForRowsWithDifferentTypes() throws Exception {
    final StringBuilder sb = new StringBuilder();
    IDEAccess.setInstance(new IDEAccessAdapter() {
    /**
     * @see ro.sync.exml.IDEAccessAdapter#showErrorMessage(java.lang.String)
     */
    @Override
    public void showErrorMessage(String message) {
      sb.append(message);
    }
    });
    open(URLUtil.correct(new File("test/EXM-29474/flowers.ditamap")), true);
  
    moveCaretRelativeTo("A1", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    assertEquals("Current action cannot be performed"
        + " because there is no element whose properties can be modified.", sb.toString());
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/map.dtd\">\n" + 
        "<map title=\"Growing Flowers\">\n" + 
        "    <reltable>\n" + 
        "        <relheader>\n" + 
        "            <relcolspec> Header1 </relcolspec>\n" + 
        "            <relcolspec> Header2 </relcolspec>\n" + 
        "        </relheader>\n" + 
        "        <relrow>\n" + 
        "            <relcell> A1 </relcell>\n" + 
        "            <relcell> B1 </relcell>\n" + 
        "        </relrow>\n" + 
        "        <relrow>\n" + 
        "            <relcell> A2 </relcell>\n" + 
        "            <relcell> B2 </relcell>\n" + 
        "        </relrow>\n" + 
        "    </reltable>\n" + 
        "</map>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for simple table.
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRelTableChangeRowTypeForRowsWithDifferentTypes2() throws Exception {
    final StringBuilder sb = new StringBuilder();
    IDEAccess.setInstance(new IDEAccessAdapter() {
    /**
     * @see ro.sync.exml.IDEAccessAdapter#showErrorMessage(java.lang.String)
     */
    @Override
    public void showErrorMessage(String message) {
      sb.append(message);
    }
    });
    open(URLUtil.correct(new File("test/EXM-29474/flowers.ditamap")), true);
  
    moveCaretRelativeTo("Header1", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());

    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/map.dtd\">\n" + 
        "<map title=\"Growing Flowers\">\n" + 
        "    <reltable>\n" + 
        "        <relrow>\n" + 
        "            <relcell> Header1 </relcell>\n" + 
        "            <relcell> Header2 </relcell>\n" + 
        "        </relrow>\n" + 
        "        <relrow>\n" + 
        "            <relcell> A1 </relcell>\n" + 
        "            <relcell> B1 </relcell>\n" + 
        "        </relrow>\n" + 
        "        <relrow>\n" + 
        "            <relcell> A2 </relcell>\n" + 
        "            <relcell> B2 </relcell>\n" + 
        "        </relrow>\n" + 
        "    </reltable>\n" + 
        "</map>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for modify attributes for table elements from caret position.
   * </p>
   * <p><b>Bug ID:</b> EXM-29534</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testModifyTableAttributes() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS1.dita")), true);
  
    moveCaretRelativeTo("CALS 1", 1);
    moveCaretRelativeTo("C1", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    assertNotNull(tabbedPane);
    selectTab(tabbedPane, "Table");
    JRadioButton alignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(alignNotSetRadioButton);
    JRadioButton alignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(alignRightRadioButton);
    assertTrue(alignNotSetRadioButton.isSelected());
    
    JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());
    
    
    JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
    
    JComboBox frameCombo = findComponent(tabbedPane, JComboBox.class, 2);
    assertNotNull(frameCombo);
    assertEquals("all", frameCombo.getSelectedItem());
    
    frameCombo.setSelectedItem("top");
    alignRightRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("1");
    sleep(200);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"top\" id=\"table_nvj_kkr_cn\" colsep=\"1\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\" align=\"right\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row rowsep=\"1\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for modify attributes for table elements from caret position.
   * </p>
   * <p><b>Bug ID:</b> EXM-29534</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testModifyColumnAttributes() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaCALS1.dita")), true);
  
    moveCaretRelativeTo("C1", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    assertNotNull(tabbedPane);
    selectTab(tabbedPane, "Column");
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    JRadioButton alignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(alignRightRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());
    
    JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
    
    alignRightRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("1");
    sleep(200);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\" align=\"right\" colsep=\"1\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row rowsep=\"1\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    
    moveCaretRelativeTo("C1", 1, false);
    moveCaretRelativeTo("C2", 1, true);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    dialog = findDialog(TABLE_PROPERTIES);
    
    tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    assertNotNull(tabbedPane);
    selectTab(tabbedPane, "Columns");
     JRadioButton rowAlignPreserveRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.PRESERVE);
    assertNotNull(rowAlignPreserveRadioButton);
     rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignPreserveRadioButton.isSelected());
    
    colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.PRESERVE, colsepCombo.getSelectedItem());
    
    rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
    
    rowAlignNotSetRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("1");
    sleep(200);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ry4_2kr_cn\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>CALS 1</p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_nvj_kkr_cn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\" colsep=\"1\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\" colsep=\"1\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>HEADER COL 1</entry>\n" + 
        "                            <entry>HEADER COL 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row rowsep=\"1\">\n" + 
        "                            <entry>C1</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                            <entry>C4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C5</entry>\n" + 
        "                            <entry>C6</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>C7</entry>\n" + 
        "                            <entry>C8</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for choice table.
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testChoiceTableChangeRowTypeForRowsWithDifferentTypes2() throws Exception {
    final StringBuilder sb = new StringBuilder();
    IDEAccess.setInstance(new IDEAccessAdapter() {
    /**
     * @see ro.sync.exml.IDEAccessAdapter#showErrorMessage(java.lang.String)
     */
    @Override
    public void showErrorMessage(String message) {
      sb.append(message);
    }
    });
    open(URLUtil.correct(new File("test/EXM-29474/ditaChoice.dita")), true);
  
    moveCaretRelativeTo("A", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_wys_g4b_qn\">\n" + 
        "    <title/>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p/>\n" + 
        "        </context>\n" + 
        "        <steps>\n" + 
        "            <step>\n" + 
        "                <cmd/>\n" + 
        "                <choicetable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"choicetable_ljc_h4b_qn\">\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A</choption>\n" + 
        "                        <chdesc>B</chdesc>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A1</choption>\n" + 
        "                        <chdesc>B1</chdesc>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A2</choption>\n" + 
        "                        <chdesc>B2</chdesc>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A3</choption>\n" + 
        "                        <chdesc>B3</chdesc>\n" + 
        "                    </chrow>\n" + 
        "                </choicetable>\n" + 
        "            </step>\n" + 
        "        </steps>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from body to header (header exists).
   * Header exists and has a row.</p>
   * <p><b>Bug ID:</b> EXM-29548</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testInnerCALSAndSimpleTable() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaInnerTable.dita")), true);
  
    moveCaretRelativeTo("C1", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeBodyRadioBtn.isSelected());
    
    rowTypeHeaderRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_gmh_tzp_qn\">\n" + 
        "    <title/>\n" + 
        "    <body>\n" + 
        "        <p>\n" + 
        "            <table frame=\"all\" id=\"table_qgx_y1q_qn\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Main</entry>\n" + 
        "                            <entry>CH</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>\n" + 
        "                                <p>\n" + 
        "                                    <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\"\n" + 
        "                                        id=\"simpletable_ql3_z1q_qn\">\n" + 
        "                                        <sthead>\n" + 
        "                                            <stentry>Inner</stentry>\n" + 
        "                                            <stentry/>\n" + 
        "                                        </sthead>\n" + 
        "                                        <strow>\n" + 
        "                                            <stentry>AI</stentry>\n" + 
        "                                            <stentry>BI</stentry>\n" + 
        "                                        </strow>\n" + 
        "                                        <strow>\n" + 
        "                                            <stentry>CI</stentry>\n" + 
        "                                            <stentry>DI</stentry>\n" + 
        "                                        </strow>\n" + 
        "                                        <strow>\n" + 
        "                                            <stentry>EI</stentry>\n" + 
        "                                            <stentry>FI</stentry>\n" + 
        "                                        </strow>\n" + 
        "                                    </simpletable>\n" + 
        "                                </p>\n" + 
        "                            </entry>\n" + 
        "                            <entry>C1</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>B2</entry>\n" + 
        "                            <entry>C2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>B3</entry>\n" + 
        "                            <entry>C3</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    moveCaretRelativeTo("Inner", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    dialog = findDialog(TABLE_PROPERTIES);
    
    tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    
    rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_gmh_tzp_qn\">\n" + 
        "  <title></title>\n" + 
        "  <body>\n" + 
        "    <p>\n" + 
        "      <table frame=\"all\" id=\"table_qgx_y1q_qn\">\n" + 
        "        <title/>\n" + 
        "        <tgroup cols=\"2\">\n" + 
        "          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "          <thead>\n" + 
        "            <row>\n" + 
        "              <entry>Main</entry>\n" + 
        "              <entry>CH</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>\n" + 
        "                <p>\n" + 
        "                  <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"simpletable_ql3_z1q_qn\">\n" + 
        "                    <strow>\n" + 
        "                      <stentry>Inner</stentry>\n" + 
        "                      <stentry/>\n" + 
        "                    </strow>\n" + 
        "                    <strow>\n" + 
        "                      <stentry>AI</stentry>\n" + 
        "                      <stentry>BI</stentry>\n" + 
        "                    </strow>\n" + 
        "                    <strow>\n" + 
        "                      <stentry>CI</stentry>\n" + 
        "                      <stentry>DI</stentry>\n" + 
        "                    </strow>\n" + 
        "                    <strow>\n" + 
        "                      <stentry>EI</stentry>\n" + 
        "                      <stentry>FI</stentry>\n" + 
        "                    </strow>\n" + 
        "                  </simpletable>\n" + 
        "                </p>\n" + 
        "              </entry>\n" + 
        "              <entry>C1</entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "          <tbody>\n" + 
        "            <row>\n" + 
        "              <entry>B2</entry>\n" + 
        "              <entry>C2</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>B3</entry>\n" + 
        "              <entry>C3</entry>\n" + 
        "            </row>\n" + 
        "          </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table>\n" + 
        "    </p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from body to header (header exists).
   * Header exists and has a row.</p>
   * <p><b>Bug ID:</b> EXM-29548</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testInnerChoiceAndSimpleTable() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaTaskinner.dita")), true);
  
    moveCaretRelativeTo("Choice", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_wpp_tzp_qn\">\n" + 
        "    <title/>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p/>\n" + 
        "        </context>\n" + 
        "        <steps>\n" + 
        "            <step>\n" + 
        "                <cmd/>\n" + 
        "                <choicetable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"choicetable_zny_tcq_qn\">\n" + 
        "                    <chrow>\n" + 
        "                        <choption>Choice</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>\n" + 
        "                            <p>\n" + 
        "                                <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\"\n" + 
        "                                    id=\"simpletable_od3_5cq_qn\">\n" + 
        "                                    <sthead>\n" + 
        "                                        <stentry>Simple</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </sthead>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A3</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A4</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A5</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                </simpletable>\n" + 
        "                            </p>\n" + 
        "                        </choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A1</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A2</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                </choicetable>\n" + 
        "            </step>\n" + 
        "        </steps>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", true);
    
    moveCaretRelativeTo("Simple", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    dialog = findDialog(TABLE_PROPERTIES);
    
    tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    
    rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_wpp_tzp_qn\">\n" + 
        "    <title></title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p></p>\n" + 
        "        </context>\n" + 
        "        <steps>\n" + 
        "            <step>\n" + 
        "                <cmd></cmd>\n" + 
        "                <choicetable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"choicetable_zny_tcq_qn\">\n" + 
        "                    <chrow>\n" + 
        "                        <choption>Choice</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>\n" + 
        "                            <p>\n" + 
        "                                <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\"\n" + 
        "                                    id=\"simpletable_od3_5cq_qn\">\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>Simple</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A3</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A4</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A5</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                </simpletable>\n" + 
        "                            </p>\n" + 
        "                        </choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A1</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A2</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                </choicetable>\n" + 
        "            </step>\n" + 
        "        </steps>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from body to header (header exists).
   * Header exists and has a row.</p>
   * <p><b>Bug ID:</b> EXM-29548</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testInnerChoiceTableUndo() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/ditaTaskinner.dita")), true);
  
    moveCaretRelativeTo("Choice", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Row");
    
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_wpp_tzp_qn\">\n" + 
        "    <title/>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p/>\n" + 
        "        </context>\n" + 
        "        <steps>\n" + 
        "            <step>\n" + 
        "                <cmd/>\n" + 
        "                <choicetable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"choicetable_zny_tcq_qn\">\n" + 
        "                    <chrow>\n" + 
        "                        <choption>Choice</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>\n" + 
        "                            <p>\n" + 
        "                                <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\"\n" + 
        "                                    id=\"simpletable_od3_5cq_qn\">\n" + 
        "                                    <sthead>\n" + 
        "                                        <stentry>Simple</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </sthead>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A3</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A4</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A5</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                </simpletable>\n" + 
        "                            </p>\n" + 
        "                        </choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A1</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A2</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                </choicetable>\n" + 
        "            </step>\n" + 
        "        </steps>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", true);
    
    doUndo();

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_wpp_tzp_qn\">\n" + 
        "    <title/>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p/>\n" + 
        "        </context>\n" + 
        "        <steps>\n" + 
        "            <step>\n" + 
        "                <cmd/>\n" + 
        "                <choicetable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"choicetable_zny_tcq_qn\">\n" + 
        "                    <chhead>\n" + 
        "                        <choptionhd>Choice</choptionhd>\n" + 
        "                        <chdeschd/>\n" + 
        "                    </chhead>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>\n" + 
        "                            <p>\n" + 
        "                                <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\"\n" + 
        "                                    id=\"simpletable_od3_5cq_qn\">\n" + 
        "                                    <sthead>\n" + 
        "                                        <stentry>Simple</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </sthead>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A3</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A4</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                    <strow>\n" + 
        "                                        <stentry>A5</stentry>\n" + 
        "                                        <stentry/>\n" + 
        "                                    </strow>\n" + 
        "                                </simpletable>\n" + 
        "                            </p>\n" + 
        "                        </choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A1</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>A2</choption>\n" + 
        "                        <chdesc/>\n" + 
        "                    </chrow>\n" + 
        "                </choicetable>\n" + 
        "            </step>\n" + 
        "        </steps>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for modify attribute for cells from discontinuous selection.
   * </p>
   * <p><b>Bug ID:</b> EXM-30519</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testCellModifyAttributeWithDifferentValues() throws Exception {
    open(URLUtil.correct(new File("test/EXM-30519/cereal-en.dita")), true);
  
    moveCaretRelativeTo("(unranked):", 1, false);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    assertNotNull(tabbedPane);
    selectTab(tabbedPane, "Cell");
    JRadioButton alignRadioBtn = findRadioButton(tabbedPane, "center");
    assertNotNull(alignRadioBtn);
    assertTrue(alignRadioBtn.isSelected());
    
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ysf_3sj_hl\">\n" + 
        "    <title>Cereal</title>\n" + 
        "    <body>\n" + 
        "        <p>From Wikipedia, the free encyclopedia.</p>\n" + 
        "        <table id=\"table_emd_jsj_hl\">\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colnum=\"1\" colname=\"col1\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"col2\"/>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"col1\" nameend=\"col2\">Cereal</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"col1\" nameend=\"col2\">\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/File:Various_grains_edit2.jpg\"\n" + 
        "                                format=\"html\" scope=\"external\">\n" + 
        "                                <image href=\"images/Various_grains.jpg\" id=\"image_c31_qdk_hl\">\n" + 
        "                                    <alt>Bread Image</alt>\n" + 
        "                                </image>\n" + 
        "                            </xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"col1\" nameend=\"col2\">Various cereals and their\n" + 
        "                            products</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"col1\" nameend=\"col2\">\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Biological_classification\"\n" + 
        "                                format=\"html\" scope=\"external\">Scientific classification</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>Kingdom:</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Plantae\" format=\"html\"\n" + 
        "                                scope=\"external\">Plantae</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry align=\"center\">(unranked):</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Angiosperms\" format=\"html\"\n" + 
        "                                scope=\"external\">Angiosperms</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>(unranked):</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Monocots\" format=\"html\"\n" + 
        "                                scope=\"external\">Monocots</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>(unranked):</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Commelinids\" format=\"html\"\n" + 
        "                                scope=\"external\">Commelinids</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>Order:</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Poales\" format=\"html\"\n" + 
        "                                scope=\"external\">Poales</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>Family:</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Poaceae\" format=\"html\"\n" + 
        "                                scope=\"external\">Poaceae</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    
    vViewport.getAPISelectionModel().addSelection(225, 237);
    vViewport.getAPISelectionModel().addSelection(195, 210);
    vViewport.getAPISelectionModel().addSelection(182, 195);
    
    flushAWTBetter();
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    dialog = findDialog(TABLE_PROPERTIES);
    
    tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    assertNotNull(tabbedPane);
    selectTab(tabbedPane, "Cells");
    alignRadioBtn = findRadioButton(tabbedPane, TablePropertiesConstants.PRESERVE);
    assertNotNull(alignRadioBtn);
    assertTrue(alignRadioBtn.isSelected());
    JRadioButton alignCenterRadioBtn = findRadioButton(tabbedPane, "center");
    assertNotNull(alignCenterRadioBtn);
    alignCenterRadioBtn.setSelected(true);
    
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_ysf_3sj_hl\">\n" + 
        "    <title>Cereal</title>\n" + 
        "    <body>\n" + 
        "        <p>From Wikipedia, the free encyclopedia.</p>\n" + 
        "        <table id=\"table_emd_jsj_hl\">\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colnum=\"1\" colname=\"col1\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"col2\"/>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"col1\" nameend=\"col2\">Cereal</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"col1\" nameend=\"col2\">\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/File:Various_grains_edit2.jpg\"\n" + 
        "                                format=\"html\" scope=\"external\">\n" + 
        "                                <image href=\"images/Various_grains.jpg\" id=\"image_c31_qdk_hl\">\n" + 
        "                                    <alt>Bread Image</alt>\n" + 
        "                                </image>\n" + 
        "                            </xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"col1\" nameend=\"col2\">Various cereals and their\n" + 
        "                            products</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"col1\" nameend=\"col2\">\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Biological_classification\"\n" + 
        "                                format=\"html\" scope=\"external\">Scientific classification</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>Kingdom:</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Plantae\" format=\"html\"\n" + 
        "                                scope=\"external\">Plantae</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry align=\"center\">(unranked):</entry>\n" + 
        "                        <entry align=\"center\">\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Angiosperms\" format=\"html\"\n" + 
        "                                scope=\"external\">Angiosperms</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>(unranked):</entry>\n" + 
        "                        <entry align=\"center\">\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Monocots\" format=\"html\"\n" + 
        "                                scope=\"external\">Monocots</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>(unranked):</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Commelinids\" format=\"html\"\n" + 
        "                                scope=\"external\">Commelinids</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>Order:</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Poales\" format=\"html\"\n" + 
        "                                scope=\"external\">Poales</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>Family:</entry>\n" + 
        "                        <entry>\n" + 
        "                            <xref href=\"http://en.wikipedia.org/wiki/Poaceae\" format=\"html\"\n" + 
        "                                scope=\"external\">Poaceae</xref>\n" + 
        "                        </entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }
}
