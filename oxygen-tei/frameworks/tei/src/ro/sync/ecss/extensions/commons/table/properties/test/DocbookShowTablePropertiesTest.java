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
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import ro.sync.ecss.component.ui.review.AuthorReviewListModel;
import ro.sync.ecss.component.ui.review.AuthorReviewPanel;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlight.PersistentHighlightType;
import ro.sync.ecss.extensions.commons.table.properties.TablePropertiesConstants;
import ro.sync.ecss.markers.AttrValueChangeMarker;
import ro.sync.ecss.markers.MarkerBase;
import ro.sync.ecss.markers.PersistentMarkerUtil;
import ro.sync.ecss.ue.AuthorDocumentControllerImpl;
import ro.sync.exml.editor.AdditionalDockableViewer;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.options.OptionConstants;
import ro.sync.exml.options.OptionTags;
import ro.sync.exml.options.Options;
import ro.sync.util.URLUtil;


/**
 * TC for "Table properties" action.
 * 
 * @author adriana_sbircea
 */

public class DocbookShowTablePropertiesTest extends TablePropertiesTestBase {
  /**
   * <p><b>Description:</b> Test for transform row from caret position from header to body.
   * </p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromHeaderToBody() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS.xml")), true);
  
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
    assertNotNull(dialog);
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
        "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A1</entry>\n" + 
        "                        <entry>B1</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from body to footer (footer does not exists).
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromBodyToFooter() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS.xml")), true);
  
    moveCaretRelativeTo("A1", 1, false);
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
    
    JRadioButton rowTypeFooterRadioBtn = findRadioButton(tabbedPane, "Footer");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeFooterRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeBodyRadioBtn.isSelected());
    
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowTypeFooterRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <row>\n" + 
        "                        <entry>A1</entry>\n" + 
        "                        <entry>B1</entry>\n" + 
        "                    </row>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform rows from body to header (with rows span).
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   * @author costi
   *
   * @throws Exception
   */
  public void testRowModifyTypeWithRowSpans() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS2.xml")), true);
  
    moveCaretRelativeTo("A3", 1, false);
    moveCaretRelativeTo("A4", 1, true);
    vViewport.getAPISelectionModel().addSelection(163, 177);
    vViewport.getAPISelectionModel().addSelection(223, 238);
    
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
        "<!DOCTYPE sect1 PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<sect1>\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in\n" + 
        "        conformity with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry morerows=\"1\">B3B4</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A4</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <row>\n" + 
        "                        <entry>A1</entry>\n" + 
        "                        <entry>B10</entry>\n" + 
        "                    </row>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA</entry>\n" + 
        "                        <entry>TB</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA1</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA2</entry>\n" + 
        "                        <entry morerows=\"1\">B4B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA4</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA3</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform rows from body to header (with rows span).
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   * @author costi
   *
   * @throws Exception
   */
  public void testRowModifyTypeWithRowSpans2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS2.xml")), true);
  
    moveCaretRelativeTo("A3", 1, false);
    moveCaretRelativeTo("A4", 1, true);
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
        "<!DOCTYPE sect1 PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<sect1>\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry morerows=\"1\">B3B4</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A4</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <row>\n" + 
        "                        <entry>A1</entry>\n" + 
        "                        <entry>B10</entry>\n" + 
        "                    </row>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA</entry>\n" + 
        "                        <entry>TB</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA1</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA2</entry>\n" + 
        "                        <entry morerows=\"1\">B4B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA4</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA3</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform rows from body to header (with rows span).
   * The action should show an error message.
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   * @author costi
   *
   * @throws Exception
   */
  public void testRowModifyTypeWithRowSpans3() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS2.xml")), true);
  
    vViewport.getAPISelectionModel().addSelection(161, 179);
    vViewport.getAPISelectionModel().addSelection(220, 233);
    flushAWTBetter();
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);

    JDialog dialog = findDialog(TABLE_PROPERTIES);
    sleep(200);
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    selectTab(tabbedPane, "Rows");
    JRadioButton rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    JRadioButton rowTypeFooterRadioBtn = findRadioButton(tabbedPane, "Footer");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    assertNotNull(rowTypeFooterRadioBtn);
    assertFalse(rowTypeHeaderRadioBtn.isEnabled());
    assertFalse(rowTypeBodyRadioBtn.isEnabled());
    assertFalse(rowTypeFooterRadioBtn.isEnabled());
    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, 
        TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    JRadioButton rowAlignTopRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.TOP);
    assertNotNull(rowAlignNotSetRadioButton);
    assertNotNull(rowAlignTopRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    rowAlignTopRadioButton.setSelected(true);
    
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
    
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE sect1 PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<sect1>\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <row>\n" + 
        "                        <entry>A1</entry>\n" + 
        "                        <entry>B10</entry>\n" + 
        "                    </row>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <row valign=\"top\">\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry morerows=\"1\">B3B4</entry>\n" + 
        "                    </row>\n" + 
        "                    <row valign=\"top\">\n" + 
        "                        <entry>A4</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA</entry>\n" + 
        "                        <entry>TB</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA1</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row valign=\"top\">\n" + 
        "                        <entry>TA2</entry>\n" + 
        "                        <entry morerows=\"1\">B4B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA4</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>TA3</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for transform row from caret position from body to footer (footer does not exists).
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowFromCaretPositionModifyTypeFromBodyToFooterWithAttrModification() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS.xml")), true);
  
    moveCaretRelativeTo("A1", 1, false);
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
    JRadioButton rowTypeFooterRadioBtn = findRadioButton(tabbedPane, "Footer");
    JRadioButton rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeFooterRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeBodyRadioBtn.isSelected());

    
    JComboBox rowSepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(rowSepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowSepCombo.getSelectedItem());
    
    JRadioButton rowAlignTopRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.TOP);
    assertNotNull(rowAlignTopRadioButton);
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowTypeFooterRadioBtn.setSelected(true);
    rowSepCombo.setSelectedItem("1");
    rowAlignTopRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <row valign=\"top\" rowsep=\"1\">\n" + 
        "                        <entry>A1</entry>\n" + 
        "                        <entry>B1</entry>\n" + 
        "                    </row>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }
  
  /**
   * <p><b>Description:</b> Test move HTML row from header to body and change attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testHTMLRowModifyTypeFromHeaderToBodyWithAttrModification() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookHTML.xml")), true);
    
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
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    JRadioButton rowHalignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowHalignNotSetRadioButton);
    JRadioButton rowHalignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(rowHalignRightRadioButton);
    assertTrue(rowHalignNotSetRadioButton.isSelected());
    
    JRadioButton rowValignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowValignNotSetRadioButton);
    JRadioButton rowValignMiddleRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.MIDDLE);
    assertNotNull(rowValignMiddleRadioButton);
    assertTrue(rowValignNotSetRadioButton.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    rowHalignRightRadioButton.setSelected(true);
    rowValignMiddleRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <tbody>\n" + 
        "                    <tr align=\"right\" valign=\"middle\">\n" + 
        "                        <td>C1</td>\n" + 
        "                        <td>C2</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C3</td>\n" + 
        "                        <td>C4</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C5</td>\n" + 
        "                        <td>C6</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C7</td>\n" + 
        "                        <td>C8</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test move HTML row from body to header and change attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testHTMLRowModifyTypeFromBodyToHeaderWithAttrModification() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookHTML.xml")), true);
    
    moveCaretRelativeTo("C3", 1, false);
    moveCaretRelativeTo("C5", 1, true);
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
    assertTrue(rowTypeBodyRadioBtn.isSelected());

    
    JRadioButton rowHalignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowHalignNotSetRadioButton);
    JRadioButton rowHalignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(rowHalignRightRadioButton);
    assertTrue(rowHalignNotSetRadioButton.isSelected());
    
    JRadioButton rowValignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowValignNotSetRadioButton);
    JRadioButton rowValignMiddleRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.MIDDLE);
    assertNotNull(rowValignMiddleRadioButton);
    assertTrue(rowValignNotSetRadioButton.isSelected());
    
    rowTypeBodyRadioBtn.setSelected(true);
    rowHalignRightRadioButton.setSelected(true);
    rowValignMiddleRadioButton.setSelected(true);
    
    
    
    
    rowTypeHeaderRadioBtn.setSelected(true);
    rowHalignRightRadioButton.setSelected(true);
    rowValignMiddleRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>C1</th>\n" + 
        "                        <th>C2</th>\n" + 
        "                    </tr>\n" + 
        "                    <tr align=\"right\" valign=\"middle\">\n" + 
        "                        <th>C3</th>\n" + 
        "                        <th>C4</th>\n" + 
        "                    </tr>\n" + 
        "                    <tr align=\"right\" valign=\"middle\">\n" + 
        "                        <th>C5</th>\n" + 
        "                        <th>C6</th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>C7</td>\n" + 
        "                        <td>C8</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test move HTML row from header to body and change attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testCALSModifyCellsAttrs() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS4.xml")), true);
    
    moveCaretRelativeTo("A1", 1, false);
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
    selectTab(tabbedPane, "Cell");
    JRadioButton rowAlignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(rowAlignRightRadioButton);
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());

    
    JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());
    
    JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
    
    JRadioButton rowValignMiddleRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.MIDDLE);
    assertNotNull(rowValignMiddleRadioButton);
    JRadioButton rowValignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowValignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowAlignRightRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("0");
    rowValignMiddleRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A1</entry>\n" + 
        "                        <entry>B1</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test change cells attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testCALSModifyMultipleCellsAttrs() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS4.xml")), true);
    
    moveCaretRelativeTo("A1", 1, false);
    moveCaretRelativeTo("B3", 1, true);
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
    selectTab(tabbedPane, "Cells");
    JRadioButton rowAlignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(rowAlignRightRadioButton);
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());

    
    JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());
    
    JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
    
    JRadioButton rowValignMiddleRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.MIDDLE);
    assertNotNull(rowValignMiddleRadioButton);
    JRadioButton rowValignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowValignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowAlignRightRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("0");
    rowValignMiddleRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A1</entry>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B1</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A2</entry>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A3</entry>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B3</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test change cells attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testHTMLModifyMultipleCellsAttrs() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookHTML1.xml")), true);
    
    moveCaretRelativeTo("C1", 1, false);
    moveCaretRelativeTo("C4", 1, true);
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
    selectTab(tabbedPane, "Cells");
    JRadioButton alignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(alignRightRadioButton);
    JRadioButton alignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(alignNotSetRadioButton);
    assertTrue(alignNotSetRadioButton.isSelected());
    
    JRadioButton valignMiddleRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.MIDDLE);
    assertNotNull(valignMiddleRadioButton);
    JRadioButton valignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(valignNotSetRadioButton);
    
    alignRightRadioButton.setSelected(true);
    valignMiddleRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th align=\"right\" valign=\"middle\">C1</th>\n" + 
        "                        <th align=\"right\" valign=\"middle\">C2</th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td align=\"right\" valign=\"middle\">C3</td>\n" + 
        "                        <td align=\"right\" valign=\"middle\">C4</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C5</td>\n" + 
        "                        <td>C6</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C7</td>\n" + 
        "                        <td>C8</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test edit attributes already set on table.</p>
   * <p><b>Bug ID:</b> EXM-29548</p>
   *
   * @author adriana_sbircea
   * @author costi
   *
   * @throws Exception
   */
  public void testHTMLModifyAttributesOnTable() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookHTML1.xml")), true);
    
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
    selectTab(tabbedPane, "Table");
    JComboBox frameCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(frameCombo);
    assertEquals("void", frameCombo.getSelectedItem());
    
    frameCombo.setSelectedItem("above");
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text <table frame=\"above\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>C1</th>\n" + 
        "                        <th>C2</th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>C3</td>\n" + 
        "                        <td>C4</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C5</td>\n" + 
        "                        <td>C6</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C7</td>\n" + 
        "                        <td>C8</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test change cells attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testCALSAttrsOnCells() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS4.xml")), true);
    
    moveCaretRelativeTo("A1", 1, false);
    moveCaretRelativeTo("B3", 1, true);
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
    selectTab(tabbedPane, "Cells");
    JRadioButton rowAlignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(rowAlignRightRadioButton);
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());

    
    JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());
    
    JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
    
    JRadioButton rowValignMiddleRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.MIDDLE);
    assertNotNull(rowValignMiddleRadioButton);
    JRadioButton rowValignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowValignNotSetRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    rowAlignRightRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("0");
    rowValignMiddleRadioButton.setSelected(true);
    
    rowAlignRightRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("0");
    rowValignMiddleRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A1</entry>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B1</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A2</entry>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A3</entry>\n" + 
        "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B3</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test change table attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testCALSAttrsOnTable() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS4.xml")), true);
    
    moveCaretRelativeTo("A1", 1, false);
    moveCaretRelativeTo("B3", 1, true);
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
    selectTab(tabbedPane, "Table");
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    JRadioButton rowAlignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(rowAlignRightRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());

    
    JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());
    
    JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
    
    JComboBox frameCombo = findComponent(tabbedPane, JComboBox.class, 2);
    assertNotNull(frameCombo);
    assertEquals("all", frameCombo.getSelectedItem());
    
    rowAlignRightRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("0");
    frameCombo.setSelectedItem("topbot");
    rowsepCombo.setSelectedItem("0");
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"topbot\" colsep=\"0\" rowsep=\"0\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\" align=\"right\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>A1</entry>\n" + 
        "                        <entry>B1</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test edit attributes on columns.</p>
   * <p><b>Bug ID:</b> EXM-29548</p>
   *
   * @author adriana_sbircea
   * @author costi
   *
   * @throws Exception
   */
  public void testHTMLModifyAttributesOnColumns() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookHTML1.xml")), true);
    
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
    selectTab(tabbedPane, "Column");
    JRadioButton alignCenterRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.CENTER);
    assertNotNull(alignCenterRadioButton);
    JRadioButton alignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(alignNotSetRadioButton);
    assertTrue(alignNotSetRadioButton.isSelected());
    
    JRadioButton valignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(valignNotSetRadioButton);
    
    alignCenterRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"50%\" align=\"center\"/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>C1</th>\n" + 
        "                        <th>C2</th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>C3</td>\n" + 
        "                        <td>C4</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C5</td>\n" + 
        "                        <td>C6</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C7</td>\n" + 
        "                        <td>C8</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test change columns attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testCALSAttrsOnColumns() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS4.xml")), true);
    
    moveCaretRelativeTo("A1", 1, false);
    moveCaretRelativeTo("B3", 1, true);
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
    selectTab(tabbedPane, "Columns");
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    JRadioButton rowAlignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(rowAlignRightRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());
    
    JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
    
    rowAlignRightRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("0");
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
        "        with the dockbookx.dtd.</para>\n" + 
        "    <para>\n" + 
        "        <table frame=\"all\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\" align=\"right\" colsep=\"0\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\" align=\"right\" colsep=\"0\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>A1</entry>\n" + 
        "                        <entry>B1</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test change columns attributes (with row spans).</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testCALSAttrsOnColumnsWithSpans() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS5.xml")), true);
    flushAWTBetter();
    sleep(100);
    
    vViewport.getAPISelectionModel().addSelection(382, 399);
    vViewport.getAPISelectionModel().addSelection(419, 423);
    vViewport.getAPISelectionModel().addSelection(443, 447);
    vViewport.getAPISelectionModel().addSelection(482, 507);
    
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
    selectTab(tabbedPane, "Columns");
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    JRadioButton alignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(alignRightRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.PRESERVE, colsepCombo.getSelectedItem());
    
    JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.PRESERVE, rowsepCombo.getSelectedItem());
    
    alignRightRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title>Article Template Title</title>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para><table xml:id=\"ex.calstable\" frame=\"topbot\">\n" + 
        "            <title>Sample CALS Table with no specified width and proportional column widths</title>\n" + 
        "            <tgroup cols=\"5\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                    <colspec colnum=\"1\" colname=\"c1\" colwidth=\"0.32*\" colsep=\"1\" rowsep=\"0\"\n" + 
        "                        align=\"right\"/>\n" + 
        "                    <colspec colnum=\"2\" colname=\"c2\" colwidth=\"1.49*\" colsep=\"0\" align=\"right\"/>\n" + 
        "                    <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1.15*\" colsep=\"0\" align=\"right\"/>\n" + 
        "                    <colspec colnum=\"4\" colname=\"c4\" colwidth=\"0.4*\" colsep=\"0\" rowsep=\"0\"/>\n" + 
        "                    <colspec colnum=\"5\" colname=\"c5\" colwidth=\"1.67*\" colsep=\"0\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
        "                            <entry>a3</entry>\n" + 
        "                            <entry>a4</entry>\n" + 
        "                            <entry>a5</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row valign=\"top\">\n" + 
        "                            <entry>f1</entry>\n" + 
        "                            <entry>f2</entry>\n" + 
        "                            <entry>f3</entry>\n" + 
        "                            <entry>f4</entry>\n" + 
        "                            <entry>f5</entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>b1</entry>\n" + 
        "                            <entry>b2</entry>\n" + 
        "                            <entry>b3</entry>\n" + 
        "                            <entry>b4</entry>\n" + 
        "                            <entry morerows=\"1\" valign=\"middle\">\n" + 
        "                                <para>\n" + 
        "                                    <emphasis role=\"bold\">Vertical</emphasis> Span </para>\n" + 
        "                            </entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>c1</entry>\n" + 
        "                            <entry namest=\"c2\" nameend=\"c3\" morerows=\"1\">Spans <emphasis role=\"bold\"\n" + 
        "                                    >Both</emphasis> directions</entry>\n" + 
        "                            <entry>c4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>d1</entry>\n" + 
        "                            <entry>d4</entry>\n" + 
        "                            <entry>d5</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "        </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test for selecting the correct tab </p>
   * <p><b>Bug ID:</b> EXM-10753</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testHTMLSelectTab() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookHTML1.xml")), true);
    
    moveCaretRelativeTo("C1", 1, false);
    moveCaretRelativeTo("C3", 1, true);
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
   assertEquals("Cells tab should be selected", 3, tabbedPane.getSelectedIndex());
  }
  
  /**
   * <p><b>Description:</b> Test for selecting the correct tab.</p>
   * <p><b>Bug ID:</b> EXM-10753</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testHTMLSelectRowTab() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookHTML1.xml")), true);
    
    moveCaretRelativeTo("C1", 1, false);
    moveCaretRelativeTo("C4", 1, true);
    flushAWTBetter();
    sleep(200);
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("show.table.properties");
      }
    }).start();
    sleep(100);
  
    JDialog dialog = findDialog(TABLE_PROPERTIES);
    JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
    assertEquals("Row tab should be selected", 1, tabbedPane.getSelectedIndex());
  }
  
  /**
   * <p><b>Description:</b>  Test for selecting the correct tab.</p>
   * <p><b>Bug ID:</b> EXM-10753</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testCALSSelectCellTab() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS4.xml")), true);
    
    moveCaretRelativeTo("A1", 1, false);
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
    assertEquals("Cell tab should be selected", 3, tabbedPane.getSelectedIndex());
  }

  /**
   * <p><b>Description:</b>  Test for selecting the correct tab.</p>
   * <p><b>Bug ID:</b> EXM-10753</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testCALSSelectTableTab() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookCALS4.xml")), true);
    
    moveCaretRelativeTo("A", 1, false);
    moveCaretRelativeTo("B3", 1, true);
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
    assertEquals("Table tab should be selected", 0, tabbedPane.getSelectedIndex());
  }

  /**
   * <p><b>Description:</b> Check that the modification of the attributes works
   * also with change tracking.</p>
   * <p><b>Bug ID:</b> EXM-10753</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testChangeAttributesWithTrackChanges() throws Exception {

    int oldProp = Options.getInstance().getIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE);
    String author = Options.getInstance().getStringProperty(OptionTags.CHANGE_TRACKING_AUTHOR);
    try {
      Options.getInstance().setIntegerProperty(
          OptionTags.TRACK_CHANGES_INITIAL_STATE,
          OptionConstants.TRACK_CHANGES_INITIAL_STATE_ALWAYS_ON);

      Options.getInstance().setStringProperty(OptionTags.CHANGE_TRACKING_AUTHOR, "adriana");
      open(URLUtil.correct(new File("test/EXM-29474/docbookCALS4.xml")), true);

      final AuthorDocumentControllerImpl ctrl = vViewport.getController();
      ctrl.getAuthorMarksManager().setFixedTimeStampForTCs("1234567890");
      moveCaretRelativeTo("A1", 1, false);
      moveCaretRelativeTo("B2", 1, true);
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
      selectTab(tabbedPane, "Cells");
      JRadioButton rowAlignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
      assertNotNull(rowAlignRightRadioButton);
      JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
      assertNotNull(rowAlignNotSetRadioButton);
      assertTrue(rowAlignNotSetRadioButton.isSelected());


      JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
      assertNotNull(colsepCombo);
      assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());

      JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
      assertNotNull(rowsepCombo);
      assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());

      JRadioButton rowValignMiddleRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.MIDDLE);
      assertNotNull(rowValignMiddleRadioButton);
      JRadioButton rowValignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
      assertNotNull(rowValignNotSetRadioButton);
      assertTrue(rowAlignNotSetRadioButton.isSelected());

      rowAlignRightRadioButton.setSelected(true);
      colsepCombo.setSelectedItem("0");
      rowValignMiddleRadioButton.setSelected(true);
      findButtonAndClick(dialog, "OK");
      flushAWTBetter();
      sleep(200);

      verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
          "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
          "    <title>First section</title>\n" + 
          "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
          "        with the dockbookx.dtd.</para>\n" + 
          "    <para>\n" + 
          "        <table frame=\"all\">\n" + 
          "            <title/>\n" + 
          "            <tgroup cols=\"2\">\n" + 
          "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
          "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
          "                <thead>\n" + 
          "                    <row>\n" + 
          "                        <entry>A</entry>\n" + 
          "                        <entry>B</entry>\n" + 
          "                    </row>\n" + 
          "                </thead>\n" + 
          "                <tbody>\n" + 
          "                    <row>\n" + 
          "                        <?oxy_attributes align=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" valign=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" colsep=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\"?>\n" + 
          "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A1</entry>\n" + 
          "                        <?oxy_attributes align=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" valign=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" colsep=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\"?>\n" + 
          "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B1</entry>\n" + 
          "                    </row>\n" + 
          "                    <row>\n" + 
          "                        <?oxy_attributes align=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" valign=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" colsep=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\"?>\n" + 
          "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A2</entry>\n" + 
          "                        <?oxy_attributes align=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" valign=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" colsep=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\"?>\n" + 
          "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B2</entry>\n" + 
          "                    </row>\n" + 
          "                    <row>\n" + 
          "                        <entry>A3</entry>\n" + 
          "                        <entry>B3</entry>\n" + 
          "                    </row>\n" + 
          "                </tbody>\n" + 
          "            </tgroup>\n" + 
          "        </table>\n" + 
          "    </para>\n" + 
          "</sect1>\n" + 
          "", true);

      AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
      AuthorReviewPanel panel =
          (AuthorReviewPanel)authorEditorPage.getAdditionalDockableViewer(
              AdditionalDockableViewer.VIEWER_ID_AUTHOR_REVIEW);
      panel.componentVisibleStateChanged(true);
      AuthorReviewListModel authorReviewModel = panel.getReviewListModel();

      assertEquals("Attribute align inserted by adriana: null\n" + 
          "align=\"right\"\n" + 
          "----------\n" + 
          "Attribute colsep inserted by adriana: null\n" + 
          "colsep=\"0\"\n" + 
          "----------\n" + 
          "Attribute valign inserted by adriana: null\n" + 
          "valign=\"middle\"\n" + 
          "----------\n" + 
          "Attribute align inserted by adriana: null\n" + 
          "align=\"right\"\n" + 
          "----------\n" + 
          "Attribute colsep inserted by adriana: null\n" + 
          "colsep=\"0\"\n" + 
          "----------\n" + 
          "Attribute valign inserted by adriana: null\n" + 
          "valign=\"middle\"\n" + 
          "----------\n" + 
          "Attribute align inserted by adriana: null\n" + 
          "align=\"right\"\n" + 
          "----------\n" + 
          "Attribute colsep inserted by adriana: null\n" + 
          "colsep=\"0\"\n" + 
          "----------\n" + 
          "Attribute valign inserted by adriana: null\n" + 
          "valign=\"middle\"\n" + 
          "----------\n" + 
          "Attribute align inserted by adriana: null\n" + 
          "align=\"right\"\n" + 
          "----------\n" + 
          "Attribute colsep inserted by adriana: null\n" + 
          "colsep=\"0\"\n" + 
          "----------\n" + 
          "Attribute valign inserted by adriana: null\n" + 
          "valign=\"middle\"\n" + 
          "----------\n" + 
          "", getReviews(authorReviewModel.getAllMarkers()));

    } finally {
      Options.getInstance().setIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE, oldProp);
      Options.getInstance().setStringProperty(OptionTags.CHANGE_TRACKING_AUTHOR, author);
    }
  }
  
  /**
   * <p><b>Description:</b> Check that the modification of the attributes works
   * also with change tracking.</p>
   * <p><b>Bug ID:</b> EXM-10753</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testChangeAttributesWithTrackChanges2() throws Exception {
    int oldProp = Options.getInstance().getIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE);
    String author = Options.getInstance().getStringProperty(OptionTags.CHANGE_TRACKING_AUTHOR);
    try {
      Options.getInstance().setIntegerProperty(
          OptionTags.TRACK_CHANGES_INITIAL_STATE,
          OptionConstants.TRACK_CHANGES_INITIAL_STATE_ALWAYS_ON);
      Options.getInstance().setStringProperty(OptionTags.CHANGE_TRACKING_AUTHOR, "adriana");
      
      open(URLUtil.correct(new File("test/EXM-29474/docbookCalsTrackChanges.xml")), true);
  
      final AuthorDocumentControllerImpl ctrl = vViewport.getController();
      ctrl.getAuthorMarksManager().setFixedTimeStampForTCs("1234567890");
      moveCaretRelativeTo("A1", 1, false);
      moveCaretRelativeTo("B2", 1, true);
      flushAWTBetter();
  
      new Thread(new Runnable() {
        @Override
        public void run() {
          invokeActionForID("show.table.properties");
        }
      }).start();
      sleep(100);
  
      JDialog dialog = findDialog(TABLE_PROPERTIES);
      sleep(100);
      JTabbedPane tabbedPane = findComponent(dialog, JTabbedPane.class, 0);
      selectTab(tabbedPane, "Cells");
      JRadioButton alignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
      assertNotNull(alignRightRadioButton);
      JRadioButton alignPreserveRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.PRESERVE);
      assertNotNull(alignPreserveRadioButton);
      assertTrue(alignPreserveRadioButton.isSelected());
  
  
      JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
      assertNotNull(colsepCombo);
      assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());
  
      JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
      assertNotNull(rowsepCombo);
      assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
  
      JRadioButton valignMiddleRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.MIDDLE);
      assertNotNull(valignMiddleRadioButton);
      JRadioButton valignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET, 1);
      assertNotNull(valignNotSetRadioButton);
      System.out.println("valignNotSetRadioButton.isSelected() " + valignNotSetRadioButton.isSelected());
      assertTrue(valignNotSetRadioButton.isSelected());
  
      alignRightRadioButton.setSelected(true);
      colsepCombo.setSelectedItem("0");
      valignMiddleRadioButton.setSelected(true);
      findButtonAndClick(dialog, "OK");
      flushAWTBetter();
      sleep(200);
  
      verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
          "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
          "    <title>First section</title>\n" + 
          "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
          "        with the dockbookx.dtd.</para>\n" + 
          "    <para>\n" + 
          "        <table frame=\"all\">\n" + 
          "            <title/>\n" + 
          "            <tgroup cols=\"2\">\n" + 
          "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
          "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
          "                <thead>\n" + 
          "                    <row>\n" + 
          "                        <entry>A</entry>\n" + 
          "                        <entry>B</entry>\n" + 
          "                    </row>\n" + 
          "                </thead>\n" + 
          "                <tbody>\n" + 
          "                    <row>\n" + 
          "                        <?oxy_attributes align=\"&lt;change type=&quot;modified&quot; oldValue=&quot;center&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" valign=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" colsep=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\"?>\n" + 
          "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A1</entry>\n" + 
          "                        <?oxy_attributes align=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" valign=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" colsep=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\"?>\n" + 
          "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B1</entry>\n" + 
          "                    </row>\n" + 
          "                    <row>\n" + 
          "                        <?oxy_attributes align=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" valign=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" colsep=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\"?>\n" + 
          "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">A2</entry>\n" + 
          "                        <?oxy_attributes align=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" valign=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\" colsep=\"&lt;change type=&quot;inserted&quot; author=&quot;adriana&quot; timestamp=&quot;1234567890&quot; /&gt;\"?>\n" + 
          "                        <entry align=\"right\" valign=\"middle\" colsep=\"0\">B2</entry>\n" + 
          "                    </row>\n" + 
          "                    <row>\n" + 
          "                        <entry>A3</entry>\n" + 
          "                        <entry>B3</entry>\n" + 
          "                    </row>\n" + 
          "                </tbody>\n" + 
          "            </tgroup>\n" + 
          "        </table>\n" + 
          "    </para>\n" + 
          "</sect1>\n" + 
          "<?oxy_options track_changes=\"on\"?>\n" + 
          "", true);
  
      AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
      AuthorReviewPanel panel =
          (AuthorReviewPanel)authorEditorPage.getAdditionalDockableViewer(
              AdditionalDockableViewer.VIEWER_ID_AUTHOR_REVIEW);
      panel.componentVisibleStateChanged(true);
      AuthorReviewListModel authorReviewModel = panel.getReviewListModel();
  
      assertEquals(
          "Attribute align modified by adriana: null\n" + 
          "align=\"right\"\n" + 
          "----------\n" + 
          "Attribute colsep inserted by adriana: null\n" + 
          "colsep=\"0\"\n" + 
          "----------\n" + 
          "Attribute valign inserted by adriana: null\n" + 
          "valign=\"middle\"\n" + 
          "----------\n" + 
          "Attribute align inserted by adriana: null\n" + 
          "align=\"right\"\n" + 
          "----------\n" + 
          "Attribute colsep inserted by adriana: null\n" + 
          "colsep=\"0\"\n" + 
          "----------\n" + 
          "Attribute valign inserted by adriana: null\n" + 
          "valign=\"middle\"\n" + 
          "----------\n" + 
          "Attribute align inserted by adriana: null\n" + 
          "align=\"right\"\n" + 
          "----------\n" + 
          "Attribute colsep inserted by adriana: null\n" + 
          "colsep=\"0\"\n" + 
          "----------\n" + 
          "Attribute valign inserted by adriana: null\n" + 
          "valign=\"middle\"\n" + 
          "----------\n" + 
          "Attribute align inserted by adriana: null\n" + 
          "align=\"right\"\n" + 
          "----------\n" + 
          "Attribute colsep inserted by adriana: null\n" + 
          "colsep=\"0\"\n" + 
          "----------\n" + 
          "Attribute valign inserted by adriana: null\n" + 
          "valign=\"middle\"\n" + 
          "----------\n" + 
          "", getReviews(authorReviewModel.getAllMarkers()));
    } finally {
      Options.getInstance().setIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE, oldProp);
      Options.getInstance().setStringProperty(OptionTags.CHANGE_TRACKING_AUTHOR, author);
    }
  }

  /**
   * <p><b>Description:</b> Test move HTML row from header to body and change attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testHTMLRowModifyTypeFromHeaderToFooterWithAttrModification() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/docbookHTML.xml")), true);
    
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
    JRadioButton rowTypeFooterRadioBtn = findRadioButton(tabbedPane, "Footer");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeFooterRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    
    JRadioButton rowHalignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowHalignNotSetRadioButton);
    JRadioButton rowHalignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(rowHalignRightRadioButton);
    assertTrue(rowHalignNotSetRadioButton.isSelected());
    
    JRadioButton rowValignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowValignNotSetRadioButton);
    JRadioButton rowValignMiddleRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.MIDDLE);
    assertNotNull(rowValignMiddleRadioButton);
    assertTrue(rowValignNotSetRadioButton.isSelected());
    
    rowTypeFooterRadioBtn.setSelected(true);
    rowHalignRightRadioButton.setSelected(true);
    rowValignMiddleRadioButton.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <tfoot>\n" + 
        "                    <tr align=\"right\" valign=\"middle\">\n" + 
        "                        <td>C1</td>\n" + 
        "                        <td>C2</td>\n" + 
        "                    </tr>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>C3</td>\n" + 
        "                        <td>C4</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C5</td>\n" + 
        "                        <td>C6</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>C7</td>\n" + 
        "                        <td>C8</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test move HTML row from body to header and change attributes.</p>
   * <p><b>Bug ID:</b> EXM-29474</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testInnerTable() throws Exception {
    open(URLUtil.correct(new File("test/EXM-29474/db4InnerTable.xml")), true);
    
    moveCaretRelativeTo("Inner", 1, false);
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Main</entry>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>\n" + 
        "                                <para>\n" + 
        "                                    <table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"50%\"/>\n" + 
        "                                        <col width=\"50%\"/>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>Inner</td>\n" + 
        "                                                <td/>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>A1</td>\n" + 
        "                                                <td/>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td/>\n" + 
        "                                                <td/>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td/>\n" + 
        "                                                <td/>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table>\n" + 
        "                                </para>\n" + 
        "                            </entry>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>A2</entry>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>A3</entry>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
    
    moveCaretRelativeTo("Main", 1, false);
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
    sleep(200);
    rowTypeHeaderRadioBtn = findRadioButton(tabbedPane, "Header");
    rowTypeBodyRadioBtn = findRadioButton(tabbedPane, "Body");
    assertNotNull(rowTypeHeaderRadioBtn);
    assertNotNull(rowTypeBodyRadioBtn);
    // Header should be selected
    assertTrue(rowTypeHeaderRadioBtn.isSelected());
    rowTypeBodyRadioBtn.setSelected(true);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>Main</entry>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>\n" + 
        "                                <para>\n" + 
        "                                    <table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"50%\"/>\n" + 
        "                                        <col width=\"50%\"/>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>Inner</td>\n" + 
        "                                                <td/>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>A1</td>\n" + 
        "                                                <td/>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td/>\n" + 
        "                                                <td/>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td/>\n" + 
        "                                                <td/>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table>\n" + 
        "                                </para>\n" + 
        "                            </entry>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>A2</entry>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>A3</entry>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Table Properties can be used inside DocBook informal tables.</p>
   * <p><b>Bug ID:</b> EXM-34393</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testUseTablePropertiesInInformalTable() throws Exception {
    open(URLUtil.correct(new File("test/EXM-34393/docbook5InformalTable.xml")), true);
    
    moveCaretRelativeTo("A1", 1, false);
    moveCaretRelativeTo("B3", 1, true);
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
    selectTab(tabbedPane, "Columns");
    JRadioButton rowAlignNotSetRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.ATTR_NOT_SET);
    assertNotNull(rowAlignNotSetRadioButton);
    JRadioButton rowAlignRightRadioButton = findRadioButton(tabbedPane, TablePropertiesConstants.RIGHT);
    assertNotNull(rowAlignRightRadioButton);
    assertTrue(rowAlignNotSetRadioButton.isSelected());
    
    JComboBox colsepCombo = findComponent(tabbedPane, JComboBox.class, 0);
    assertNotNull(colsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, colsepCombo.getSelectedItem());
    
    JComboBox rowsepCombo = findComponent(tabbedPane, JComboBox.class, 1);
    assertNotNull(rowsepCombo);
    assertEquals(TablePropertiesConstants.ATTR_NOT_SET, rowsepCombo.getSelectedItem());
    
    rowAlignRightRadioButton.setSelected(true);
    colsepCombo.setSelectedItem("0");
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
        "    <title>First section</title>\n" + 
        "    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documen<informaltable\n" + 
        "            frame=\"all\">\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\" align=\"right\" colsep=\"0\"/>\n" + 
        "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\" align=\"right\" colsep=\"0\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>A</entry>\n" + 
        "                        <entry>B</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>A1</entry>\n" + 
        "                        <entry>B1</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A2</entry>\n" + 
        "                        <entry>B2</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry>A3</entry>\n" + 
        "                        <entry>B3</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </informaltable>ts in conformity with the dockbookx.dtd.</para>\n" + 
        "</sect1>\n" + 
        "", true);
  }

  /**
   * Serialize the markers from list.
   * 
   * @param markers The markers.
   * 
   * @return A string containing the dump of the review view.
   */
  public static String getReviews(List<MarkerBase> markers) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < markers.size(); i++) {
      MarkerBase currentMarker = markers.get(i);
      if (currentMarker.getType() == PersistentHighlightType.CHANGE_INSERT) {
        sb.append("Inserted by ").append(currentMarker.getAuthor()).append(": ");
        sb.append(currentMarker.getAuthorComment()).append("\n");
        sb.append(PersistentMarkerUtil.getMarkerContent(vViewport, currentMarker, 160));
        sb.append("\n----------\n");
      } else if (currentMarker.getType() == PersistentHighlightType.CHANGE_DELETE) {
        sb.append("Deleted by ").append(currentMarker.getAuthor()).append(": ");
        sb.append(currentMarker.getAuthorComment()).append("\n");
        sb.append(PersistentMarkerUtil.getMarkerContent(vViewport, currentMarker, 160));
        sb.append("\n----------\n");
      } else if (currentMarker.getType() == PersistentHighlightType.CHANGE_ATTRIBUTE_DELETED) {
        sb.append("Attribute " + AttrValueChangeMarker.getAttributeName(currentMarker) + " deleted by ").append(currentMarker.getAuthor()).append(": ");
        sb.append(currentMarker.getAuthorComment()).append("\n");
        sb.append(PersistentMarkerUtil.getMarkerContent(vViewport, currentMarker, 160));
        sb.append("\n----------\n");
      } else if (currentMarker.getType() == PersistentHighlightType.CHANGE_ATTRIBUTE_INSERTED) {
        sb.append("Attribute " + AttrValueChangeMarker.getAttributeName(currentMarker) + " inserted by ").append(currentMarker.getAuthor()).append(": ");
        sb.append(currentMarker.getAuthorComment()).append("\n");
        sb.append(PersistentMarkerUtil.getMarkerContent(vViewport, currentMarker, 160));
        sb.append("\n----------\n");
      } else if (currentMarker.getType() == PersistentHighlightType.CHANGE_ATTRIBUTE_MODIFIED) {
        sb.append("Attribute " + AttrValueChangeMarker.getAttributeName(currentMarker) + " modified by ").append(currentMarker.getAuthor()).append(": ");
        sb.append(currentMarker.getAuthorComment()).append("\n");
        sb.append(PersistentMarkerUtil.getMarkerContent(vViewport, currentMarker, 160));
        sb.append("\n----------\n");
      }
    }

    return sb.toString();
  }
}
