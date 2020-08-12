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
package ro.sync.ecss.extensions.commons.sort.test;

import java.io.File;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JRadioButton;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlight;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlightsListener;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.exml.editor.EditorPage;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.util.URLUtil;

/**
 *  Test class for DITA table sort operation.
 */
public class DITASortTableOperationTest  extends EditorAuthorExtensionTestBase {
  /**
   * <p><b>Description:</b> Test the Sort operation.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITACALSTableWithRowSpan() throws Exception {
    String originalContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "  <title>Care and Preparation</title>\n" + 
        "  <body>\n" + 
        "    <p>When caring for your flower garden you want to feed your plants properly, control pests\n" + 
        "      and weeds. Good soil is a must to successful gardening, landscaping, and healthy\n" + 
        "      flowers. You have to\n" + 
        "      <indexterm>tasks<indexterm>preparation</indexterm></indexterm>balance the soil structure\n" + 
        "      with nutrients and regulate the pH to cover your plants' needs.<table frame=\"none\"\n" + 
        "        id=\"table_zyt_jmy_tk\">\n" + 
        "        <title>Flowers</title>\n" + 
        "        <tgroup cols=\"3\">\n" + 
        "          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"171.0pt\"/>\n" + 
        "          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"99.0pt\"/>\n" + 
        "          <colspec colname=\"newCol3\" colnum=\"3\" colwidth=\"200px\"/>\n" + 
        "          <thead>\n" + 
        "            <row>\n" + 
        "              <entry>Flower</entry>\n" + 
        "              <entry>Type</entry>\n" + 
        "              <entry>Soil</entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "          <tbody>\n" + 
        "            <row>\n" + 
        "              <entry>Chrysanthemum</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>well drained</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry morerows=\"1\">GardeniaGerbera</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>acidic</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>annual</entry>\n" + 
        "              <entry>sandy, well-drained</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>Iris</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>slightly acidic</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>Lilac</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>alkaline</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>Salvia</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>average</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>Snowdrop</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>humus-rich</entry>\n" + 
        "            </row>\n" + 
        "          </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table></p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "";
    open(URLUtil.correct(new File("test/EXM-12505/care2.dita")), true);
    
    moveCaretRelativeTo("Gerbera", 1);
    flushAWTBetter();
  
    invokeActionForID("sort");
    
    flushAWTBetter();
    sleep(200);
    
    assertEquals("The sort operation couldn't be performed.\n" + 
    		"The 'Sort' operation is unavailable for tables with multiple rowspan cells.", lastErrorMessage);
    
    verifyDocument(originalContent, true);
    
  }
  
  /**
   * <p><b>Description:</b> Test the Sort operation.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITACALSTable() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/care.dita")), true);
  
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    moveCaretRelativeTo("Gerbera", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>Care and Preparation</title>\n" + 
        "    <body>\n" + 
        "        <p>When caring for your flower garden you want to feed your plants properly, control pests\n" + 
        "            and weeds. Good soil is a must to successful gardening, landscaping, and healthy\n" + 
        "            flowers. You have to\n" + 
        "            <indexterm>tasks<indexterm>preparation</indexterm></indexterm>balance the soil structure\n" + 
        "            with nutrients and regulate the pH to cover your plants' needs.<table frame=\"none\"\n" + 
        "                id=\"table_zyt_jmy_tk\">\n" + 
        "                <title>Flowers</title>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"171.0pt\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"99.0pt\"/>\n" + 
        "                    <colspec colname=\"newCol3\" colnum=\"3\" colwidth=\"200px\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Flower</entry>\n" + 
        "                            <entry>Type</entry>\n" + 
        "                            <entry>Soil</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>Snowdrop</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>humus-rich</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Salvia</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>average</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Lilac</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>alkaline</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Iris</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>slightly acidic</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Gerbera</entry>\n" + 
        "                            <entry>annual</entry>\n" + 
        "                            <entry>sandy, well-drained</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Gardenia</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>acidic</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Chrysanthemum</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>well drained</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table></p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITASimpleTable() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/care3.dita")), true);
  
    
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("A", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
    		"<topic id=\"care\">\n" + 
    		"  <title>Care and Preparation</title>\n" + 
    		"  <body>\n" + 
    		"    <p>When caring for your flower garden you want to feed your plants properly, control pests and\n" + 
    		"            weeds. Good soil is a must to successful gardening, landscaping, and healthy flowers.\n" + 
    		"                <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"simpletable_nd5_gyd_5k\">\n" + 
    		"                <sthead>\n" + 
    		"                    <stentry>A</stentry>\n" + 
    		"                    <stentry>B</stentry>\n" + 
    		"                </sthead>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>f</stentry>\n" + 
    		"                    <stentry>y</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>b</stentry>\n" + 
    		"                    <stentry>z</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>a</stentry>\n" + 
    		"                    <stentry>x</stentry>\n" + 
    		"                </strow>\n" + 
    		"            </simpletable></p>\n" + 
    		"  </body>\n" + 
    		"</topic>\n" + 
    		"", true);
  }

  /**
   * <p><b>Description:</b> Test the table sort operation with selection.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITACALSTableWithSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/care2.dita")), true);

    moveCaretRelativeTo("Lilac", 1);
    moveCaretRelativeTo("humus-rich", 1, true);
    flushAWTBetter();

    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);

    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JRadioButton selectedElmsRadio = findComponent(dialog, JRadioButton.class, 0);
    assertNotNull(selectedElmsRadio);
    assertTrue(selectedElmsRadio.isSelected());
    JRadioButton AllElmsRadio = findComponent(dialog, JRadioButton.class, 1);
    assertNotNull(AllElmsRadio);
    assertFalse(AllElmsRadio.isEnabled());

    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "  <title>Care and Preparation</title>\n" + 
        "  <body>\n" + 
        "    <p>When caring for your flower garden you want to feed your plants properly, control pests\n" + 
        "      and weeds. Good soil is a must to successful gardening, landscaping, and healthy\n" + 
        "      flowers. You have to\n" + 
        "      <indexterm>tasks<indexterm>preparation</indexterm></indexterm>balance the soil structure\n" + 
        "      with nutrients and regulate the pH to cover your plants' needs.<table frame=\"none\"\n" + 
        "        id=\"table_zyt_jmy_tk\">\n" + 
        "        <title>Flowers</title>\n" + 
        "        <tgroup cols=\"3\">\n" + 
        "          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"171.0pt\"/>\n" + 
        "          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"99.0pt\"/>\n" + 
        "          <colspec colname=\"newCol3\" colnum=\"3\" colwidth=\"200px\"/>\n" + 
        "          <thead>\n" + 
        "            <row>\n" + 
        "              <entry>Flower</entry>\n" + 
        "              <entry>Type</entry>\n" + 
        "              <entry>Soil</entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "          <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>Chrysanthemum</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>well drained</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry morerows=\"1\">GardeniaGerbera</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>acidic</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>annual</entry>\n" + 
        "                            <entry>sandy, well-drained</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Iris</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>slightly acidic</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Snowdrop</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>humus-rich</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Salvia</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>average</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Lilac</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>alkaline</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table></p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test the table sort operation with selection.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITACALSTableWithSelectionInRowSpan() throws Exception {
    String originalContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "  <title>Care and Preparation</title>\n" + 
        "  <body>\n" + 
        "    <p>When caring for your flower garden you want to feed your plants properly, control pests\n" + 
        "      and weeds. Good soil is a must to successful gardening, landscaping, and healthy\n" + 
        "      flowers. You have to\n" + 
        "      <indexterm>tasks<indexterm>preparation</indexterm></indexterm>balance the soil structure\n" + 
        "      with nutrients and regulate the pH to cover your plants' needs.<table frame=\"none\"\n" + 
        "        id=\"table_zyt_jmy_tk\">\n" + 
        "        <title>Flowers</title>\n" + 
        "        <tgroup cols=\"3\">\n" + 
        "          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"171.0pt\"/>\n" + 
        "          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"99.0pt\"/>\n" + 
        "          <colspec colname=\"newCol3\" colnum=\"3\" colwidth=\"200px\"/>\n" + 
        "          <thead>\n" + 
        "            <row>\n" + 
        "              <entry>Flower</entry>\n" + 
        "              <entry>Type</entry>\n" + 
        "              <entry>Soil</entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "          <tbody>\n" + 
        "            <row>\n" + 
        "              <entry>Chrysanthemum</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>well drained</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry morerows=\"1\">GardeniaGerbera</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>acidic</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>annual</entry>\n" + 
        "              <entry>sandy, well-drained</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>Iris</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>slightly acidic</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>Lilac</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>alkaline</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>Salvia</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>average</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>Snowdrop</entry>\n" + 
        "              <entry>perennial</entry>\n" + 
        "              <entry>humus-rich</entry>\n" + 
        "            </row>\n" + 
        "          </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table></p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "";
    open(URLUtil.correct(new File("test/EXM-12505/care2.dita")), true);
  
    moveCaretRelativeTo("Chrysanthemum", 1);
    moveCaretRelativeTo("Iris", 1, true);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
  
    assertEquals("The sort operation couldn't be performed.\n" + 
    		"The 'Sort' operation is unavailable for tables with multiple rowspan cells.", lastErrorMessage);
    verifyDocument(originalContent, true);
    
    moveCaretRelativeTo("acidic", 0);
    moveCaret(getCaretPosition() + "acidic".length(), true);
    flushAWTBetter();
  
    lastErrorMessage = null;
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
  
    assertEquals("The sort operation couldn't be performed.\n" + 
        "The 'Sort' operation is unavailable for tables with multiple rowspan cells.", lastErrorMessage);
    verifyDocument(originalContent, true);
    
    moveCaretRelativeTo("slightly acidic", 0);
    moveCaret(getCaretPosition() + "slightly acidic".length(), true);
    flushAWTBetter();
  
    lastErrorMessage = null;
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
  
    assertNull("The sort operation should work.", lastErrorMessage);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation.</p>
   * <p><b>Bug ID:</b> EXM-32899</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITA_SL_List() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/ditaList.dita")), true);
    
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("B", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_zmz_skl_5k\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>\n" + 
        "            <ol id=\"ol_dq3_tkl_5k\">\n" + 
        "                <li>E</li>\n" + 
        "                <li>D</li>\n" + 
        "                <li>C</li>\n" + 
        "                <li>B</li>\n" + 
        "                <li>A</li>\n" + 
        "            </ol>\n" + 
        "        </p>\n" + 
        "        <p>\n" + 
        "            <sl>\n" + 
        "                <sli>CS</sli>\n" + 
        "                <sli>BS</sli>\n" + 
        "                <sli>ES</sli>\n" + 
        "                <sli>AS</sli>\n" + 
        "                <sli>DS</sli>\n" + 
        "            </sl>\n" + 
        "            <parml>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>V</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>A</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>X</pt>\n" + 
        "                    <pd>S</pd>\n" + 
        "                </plentry>\n" + 
        "            </parml>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    moveCaretRelativeTo("BS", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_zmz_skl_5k\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>\n" + 
        "            <ol id=\"ol_dq3_tkl_5k\">\n" + 
        "                <li>E</li>\n" + 
        "                <li>D</li>\n" + 
        "                <li>C</li>\n" + 
        "                <li>B</li>\n" + 
        "                <li>A</li>\n" + 
        "            </ol>\n" + 
        "        </p>\n" + 
        "        <p>\n" + 
        "            <sl>\n" + 
        "                <sli>ES</sli>\n" + 
        "                <sli>DS</sli>\n" + 
        "                <sli>CS</sli>\n" + 
        "                <sli>BS</sli>\n" + 
        "                <sli>AS</sli>\n" + 
        "            </sl>\n" + 
        "            <parml>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>V</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>A</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>X</pt>\n" + 
        "                    <pd>S</pd>\n" + 
        "                </plentry>\n" + 
        "            </parml>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    moveCaretRelativeTo("V", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_zmz_skl_5k\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>\n" + 
        "            <ol id=\"ol_dq3_tkl_5k\">\n" + 
        "                <li>E</li>\n" + 
        "                <li>D</li>\n" + 
        "                <li>C</li>\n" + 
        "                <li>B</li>\n" + 
        "                <li>A</li>\n" + 
        "            </ol>\n" + 
        "        </p>\n" + 
        "        <p>\n" + 
        "            <sl>\n" + 
        "                <sli>ES</sli>\n" + 
        "                <sli>DS</sli>\n" + 
        "                <sli>CS</sli>\n" + 
        "                <sli>BS</sli>\n" + 
        "                <sli>AS</sli>\n" + 
        "            </sl>\n" + 
        "            <parml>\n" + 
        "                <plentry>\n" + 
        "                    <pt>X</pt>\n" + 
        "                    <pd>S</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>V</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>A</pd>\n" + 
        "                </plentry>\n" + 
        "            </parml>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITAUnorderedListWithSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/ditaList2.dita")), true);
    
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("B", 1);
    moveCaretRelativeTo("C", 1, true);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
    		"<topic id=\"topic_bfr_2ll_5k\">\n" + 
    		"  <title>Topic title</title>\n" + 
    		"  <body>\n" + 
    		"    <p>\n" + 
    		"      <ul id=\"ul_hrx_2ll_5k\">\n" + 
    		"                <li>C</li>\n" + 
    		"                <li>B</li>\n" + 
    		"                <li>A</li>\n" + 
    		"                <li>I</li>\n" + 
    		"            </ul>\n" + 
    		"    </p>\n" + 
    		"  </body>\n" + 
    		"</topic>\n" + 
    		"", true);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation with XSD:DATE.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITAWithXSD_DATE() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/ditaXSD_Date.dita")), true);
  
    moveCaretRelativeTo("A", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog("Sort");
    JComboBox typeCombo = findComponent(dialog, JComboBox.class, 1);
    typeCombo.setSelectedIndex(2);
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
    		"<topic id=\"topic_zkt_dyr_wk\">\n" + 
    		"  <title>Topic title</title>\n" + 
    		"  <body>\n" + 
    		"    <p>\n" + 
    		"      <table frame=\"all\" id=\"table_pgb_q3k_wk\">\n" + 
    		"        <title/>\n" + 
    		"        <tgroup cols=\"5\">\n" + 
    		"          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"3.35*\"/>\n" + 
    		"          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1*\"/>\n" + 
    		"          <colspec colname=\"c3\" colnum=\"3\" colwidth=\"2.16*\"/>\n" + 
    		"          <colspec colname=\"c4\" colnum=\"4\" colwidth=\"2.16*\"/>\n" + 
    		"          <colspec colname=\"c5\" colnum=\"5\" colwidth=\"2.16*\"/>\n" + 
    		"          <thead>\n" + 
    		"            <row>\n" + 
    		"              <entry>A</entry>\n" + 
    		"              <entry>B</entry>\n" + 
    		"              <entry>C</entry>\n" + 
    		"              <entry>D</entry>\n" + 
    		"              <entry>E</entry>\n" + 
    		"            </row>\n" + 
    		"          </thead>\n" + 
    		"          <tbody>\n" + 
    		"                        <row>\n" + 
    		"                            <entry>\n" + 
    		"                                <p>2020-04-25Z</p>\n" + 
    		"                            </entry>\n" + 
    		"                            <entry>3</entry>\n" + 
    		"                            <entry>b</entry>\n" + 
    		"                            <entry>fhd</entry>\n" + 
    		"                            <entry>AE</entry>\n" + 
    		"                        </row>\n" + 
    		"                        <row>\n" + 
    		"                            <entry>2008-12-19Z</entry>\n" + 
    		"                            <entry>5</entry>\n" + 
    		"                            <entry>n</entry>\n" + 
    		"                            <entry>hj</entry>\n" + 
    		"                            <entry>zzzzzzzz</entry>\n" + 
    		"                        </row>\n" + 
    		"                        <row>\n" + 
    		"                            <entry>\n" + 
    		"                                <p>2002-09-10Z </p>\n" + 
    		"                            </entry>\n" + 
    		"                            <entry>1</entry>\n" + 
    		"                            <entry>a</entry>\n" + 
    		"                            <entry>dsf</entry>\n" + 
    		"                            <entry>wr</entry>\n" + 
    		"                        </row>\n" + 
    		"                    </tbody>\n" + 
    		"        </tgroup>\n" + 
    		"      </table>\n" + 
    		"    </p>\n" + 
    		"  </body>\n" + 
    		"</topic>\n" + 
    		"", true);
  }
  
  /**
   * Keeps some highlight information.
   */
  private StringBuilder sb = new StringBuilder();

  /**
   * <p><b>Description:</b> Sort with change tracking.</p>
   * <p><b>Bug ID:</b> EXM-27847</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortWithChangeTrackingInTablesEXM_27847() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/care4.dita")), true);
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);

    //Add a persistent highlight listener.
    vViewport.getController().addAuthorPersistentHighlightListener(new AuthorPersistentHighlightsListener() {
      @Override
      public void highlightsChanged() {
        sb.append("HIGHLIGHTS CHANGED\n");
      }
      
      @Override
      public void highlightUpdated(AuthorPersistentHighlight highlight) {
        sb.append("H UPDATED " + highlight + "\n");
      }
      
      @Override
      public void highlightRemoved(AuthorPersistentHighlight highlight) {
        sb.append("H REMOVED " + highlight + "\n");        
      }
      
      @Override
      public void highlightAdded(AuthorPersistentHighlight highlight) {
        sb.append("H ADDED " + highlight + "\n");        
      }
    });
    
    moveCaretRelativeTo("Chrys", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    
    assertEquals(
        //First table contents are removed.
        "H REMOVED Change Type:Commented, Author:radu_coravu, Timestamp:20130724T145307+0300, Range:44:48, Comment:dssadsadsas\n" + 
    		"H REMOVED Change Type:Inserted, Author:radu_coravu, Timestamp:20130724T145320+0300, Range:58:65\n" + 
    		"H REMOVED Change Type:Deleted, Author:radu_coravu, Timestamp:20130724T145326+0300, Range:75:78\n" +
    		//AND TRIGGERS A HIGHLIGHTS CHANGED WHEN STUFF IS INSERTED BACK.
    		"HIGHLIGHTS CHANGED\n", sb.toString());
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
    		"<topic id=\"care\">\n" + 
    		"  <title>T</title>\n" + 
    		"  <body>\n" + 
    		"    <table frame=\"none\" id=\"table_zyt_jmy_tk\">\n" + 
    		"      <title>A</title>\n" + 
    		"      <tgroup cols=\"2\">\n" + 
    		"        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"171.0pt\"/>\n" + 
    		"        <colspec colname=\"newCol3\" colnum=\"2\" colwidth=\"200px\"/>\n" + 
    		"        <thead>\n" + 
    		"          <row>\n" + 
    		"            <entry>Flower</entry>\n" + 
    		"            <entry>Soil</entry>\n" + 
    		"          </row>\n" + 
    		"        </thead>\n" + 
    		"        <tbody>\n" + 
    		"                    <row>\n" + 
    		"                        <entry><?oxy_delete author=\"radu_coravu\" timestamp=\"20130724T145326+0300\" content=\"Snow\"?>drop</entry>\n" + 
    		"                        <entry>humus-rich</entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>Chrysant<?oxy_comment_start author=\"radu_coravu\" timestamp=\"20130724T145307+0300\" comment=\"dssadsadsas\"?>hemum<?oxy_comment_end?></entry>\n" + 
    		"                        <entry>well\n" + 
    		"                            dr<?oxy_insert_start author=\"radu_coravu\" timestamp=\"20130724T145320+0300\"?>inserted<?oxy_insert_end?>ained</entry>\n" + 
    		"                    </row>\n" + 
    		"                </tbody>\n" + 
    		"      </tgroup>\n" + 
    		"    </table>\n" + 
    		"  </body>\n" + 
    		"</topic>\n" + 
    		"", true);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation with ol element fully selected.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testSortDITAOrderedList2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/ditaList.dita")), true);
    
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("A", -1);
    moveCaretRelativeTo("D", 2, true);
    
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    assertNotNull(dialog);
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    assertNotNull(combo);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_zmz_skl_5k\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>\n" + 
        "            <ol id=\"ol_dq3_tkl_5k\">\n" + 
        "                <li>E</li>\n" + 
        "                <li>D</li>\n" + 
        "                <li>C</li>\n" + 
        "                <li>B</li>\n" + 
        "                <li>A</li>\n" + 
        "            </ol>\n" + 
        "        </p>\n" + 
        "        <p>\n" + 
        "            <sl>\n" + 
        "                <sli>CS</sli>\n" + 
        "                <sli>BS</sli>\n" + 
        "                <sli>ES</sli>\n" + 
        "                <sli>AS</sli>\n" + 
        "                <sli>DS</sli>\n" + 
        "            </sl>\n" + 
        "            <parml>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>V</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>A</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>X</pt>\n" + 
        "                    <pd>S</pd>\n" + 
        "                </plentry>\n" + 
        "            </parml>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation with the table element fully selected.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testSortDITASimpleTable2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/care3.dita")), true);
  
    
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("A", -2);
    moveCaretRelativeTo("z", 3, true);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    assertNotNull(dialog);
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    assertNotNull(combo);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
    		"<topic id=\"care\">\n" + 
    		"  <title>Care and Preparation</title>\n" + 
    		"  <body>\n" + 
    		"    <p>When caring for your flower garden you want to feed your plants properly, control pests and\n" + 
    		"            weeds. Good soil is a must to successful gardening, landscaping, and healthy flowers.\n" + 
    		"                <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"simpletable_nd5_gyd_5k\">\n" + 
    		"                <sthead>\n" + 
    		"                    <stentry>A</stentry>\n" + 
    		"                    <stentry>B</stentry>\n" + 
    		"                </sthead>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>f</stentry>\n" + 
    		"                    <stentry>y</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>b</stentry>\n" + 
    		"                    <stentry>z</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>a</stentry>\n" + 
    		"                    <stentry>x</stentry>\n" + 
    		"                </strow>\n" + 
    		"            </simpletable></p>\n" + 
    		"  </body>\n" + 
    		"</topic>\n" + 
    		"", true);
  }
  
  /**
   * <p><b>Description:</b> Test the sorting of the nearest sortable parent when tables and lists are imbricated.</p>
   * <p><b>Bug ID:</b> EXM-27906</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testSortClosestParentEXM_27906() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27906/sortClosestParentDITA.dita")), true);
  
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    // Sort the inner table.
    moveCaretRelativeTo("inner table3", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(0);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_l31_sxs_bl\">\n" + 
        "  <title/>\n" + 
        "  <body>\n" + 
        "    <p>\n" + 
        "      <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "        <title/>\n" + 
        "        <tgroup cols=\"3\">\n" + 
        "          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "          <thead>\n" + 
        "            <row>\n" + 
        "              <entry>head<ul id=\"ul_hz3_dys_bl\">\n" + 
        "                  <li>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                      <title/>\n" + 
        "                      <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                          <row>\n" + 
        "                            <entry/>\n" + 
        "                          </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                          <row>\n" + 
        "                            <entry>head table1</entry>\n" + 
        "                          </row>\n" + 
        "                          <row>\n" + 
        "                            <entry>head table2</entry>\n" + 
        "                          </row>\n" + 
        "                          <row>\n" + 
        "                            <entry>head table3</entry>\n" + 
        "                          </row>\n" + 
        "                        </tbody>\n" + 
        "                      </tgroup>\n" + 
        "                    </table></li>\n" + 
        "                  <li>head list2</li>\n" + 
        "                  <li>head list3</li>\n" + 
        "                </ul></entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "          <tbody>\n" + 
        "            <row>\n" + 
        "              <entry>top table1<ol id=\"ol_wxk_vxs_bl\">\n" + 
        "                  <li>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                      <title/>\n" + 
        "                      <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                          <row>\n" + 
        "                            <entry/>\n" + 
        "                          </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                </tbody>\n" + 
        "                      </tgroup>\n" + 
        "                    </table></li>\n" + 
        "                  <li>item2</li>\n" + 
        "                  <li>item3</li>\n" + 
        "                </ol></entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>top table2</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>top table3</entry>\n" + 
        "            </row>\n" + 
        "          </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table>\n" + 
        "    </p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    // Sort the list from the table body.
    moveCaretRelativeTo("item1", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_l31_sxs_bl\">\n" + 
        "  <title/>\n" + 
        "  <body>\n" + 
        "    <p>\n" + 
        "      <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "        <title/>\n" + 
        "        <tgroup cols=\"3\">\n" + 
        "          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "          <thead>\n" + 
        "            <row>\n" + 
        "              <entry>head<ul id=\"ul_hz3_dys_bl\">\n" + 
        "                  <li>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                      <title/>\n" + 
        "                      <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                          <row>\n" + 
        "                            <entry/>\n" + 
        "                          </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                          <row>\n" + 
        "                            <entry>head table1</entry>\n" + 
        "                          </row>\n" + 
        "                          <row>\n" + 
        "                            <entry>head table2</entry>\n" + 
        "                          </row>\n" + 
        "                          <row>\n" + 
        "                            <entry>head table3</entry>\n" + 
        "                          </row>\n" + 
        "                        </tbody>\n" + 
        "                      </tgroup>\n" + 
        "                    </table></li>\n" + 
        "                  <li>head list2</li>\n" + 
        "                  <li>head list3</li>\n" + 
        "                </ul></entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "          <tbody>\n" + 
        "            <row>\n" + 
        "              <entry>top table1<ol id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <li>item3</li>\n" + 
        "                                    <li>item2</li>\n" + 
        "                                    <li>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                </thead>\n" + 
        "                                                <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></li>\n" + 
        "                                </ol></entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>top table2</entry>\n" + 
        "            </row>\n" + 
        "            <row>\n" + 
        "              <entry>top table3</entry>\n" + 
        "            </row>\n" + 
        "          </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table>\n" + 
        "    </p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    // Sort the big table.
    moveCaretRelativeTo("top table1", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_l31_sxs_bl\">\n" + 
        "  <title/>\n" + 
        "  <body>\n" + 
        "    <p>\n" + 
        "      <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "        <title/>\n" + 
        "        <tgroup cols=\"3\">\n" + 
        "          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "          <thead>\n" + 
        "            <row>\n" + 
        "              <entry>head<ul id=\"ul_hz3_dys_bl\">\n" + 
        "                  <li>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                      <title/>\n" + 
        "                      <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                          <row>\n" + 
        "                            <entry/>\n" + 
        "                          </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                          <row>\n" + 
        "                            <entry>head table1</entry>\n" + 
        "                          </row>\n" + 
        "                          <row>\n" + 
        "                            <entry>head table2</entry>\n" + 
        "                          </row>\n" + 
        "                          <row>\n" + 
        "                            <entry>head table3</entry>\n" + 
        "                          </row>\n" + 
        "                        </tbody>\n" + 
        "                      </tgroup>\n" + 
        "                    </table></li>\n" + 
        "                  <li>head list2</li>\n" + 
        "                  <li>head list3</li>\n" + 
        "                </ul></entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "          <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<ol id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <li>item3</li>\n" + 
        "                                    <li>item2</li>\n" + 
        "                                    <li>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                </thead>\n" + 
        "                                                <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></li>\n" + 
        "                                </ol></entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table>\n" + 
        "    </p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    // Sort the table from the header of the big table.
    moveCaretRelativeTo("head table1", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_l31_sxs_bl\">\n" + 
        "  <title/>\n" + 
        "  <body>\n" + 
        "    <p>\n" + 
        "      <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "        <title/>\n" + 
        "        <tgroup cols=\"3\">\n" + 
        "          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "          <thead>\n" + 
        "            <row>\n" + 
        "              <entry>head<ul id=\"ul_hz3_dys_bl\">\n" + 
        "                  <li>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                      <title/>\n" + 
        "                      <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                          <row>\n" + 
        "                            <entry/>\n" + 
        "                          </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                </tbody>\n" + 
        "                      </tgroup>\n" + 
        "                    </table></li>\n" + 
        "                  <li>head list2</li>\n" + 
        "                  <li>head list3</li>\n" + 
        "                </ul></entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "          <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<ol id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <li>item3</li>\n" + 
        "                                    <li>item2</li>\n" + 
        "                                    <li>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                </thead>\n" + 
        "                                                <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></li>\n" + 
        "                                </ol></entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table>\n" + 
        "    </p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    // Sort the list from the header of the big table.
    moveCaretRelativeTo("head list1", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_l31_sxs_bl\">\n" + 
        "  <title/>\n" + 
        "  <body>\n" + 
        "    <p>\n" + 
        "      <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "        <title/>\n" + 
        "        <tgroup cols=\"3\">\n" + 
        "          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "          <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "          <thead>\n" + 
        "            <row>\n" + 
        "              <entry>head<ul id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <li>head list3</li>\n" + 
        "                                    <li>head list2</li>\n" + 
        "                                    <li>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                </thead>\n" + 
        "                                                <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></li>\n" + 
        "                                </ul></entry>\n" + 
        "            </row>\n" + 
        "          </thead>\n" + 
        "          <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<ol id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <li>item3</li>\n" + 
        "                                    <li>item2</li>\n" + 
        "                                    <li>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                </thead>\n" + 
        "                                                <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></li>\n" + 
        "                                </ol></entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "        </tgroup>\n" + 
        "      </table>\n" + 
        "    </p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
  }
  
  /**
   * <p><b>Description:</b> Test the sort operation using different locale from system</p>
   * <p><b>Bug ID:</b> EXM-27984</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testSortWithDifferentLocale() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/sortWithLocale.dita")), true);
  
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("peach", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(0);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
    		"<topic id=\"care\">\n" + 
    		"  <title></title>\n" + 
    		"  <body>\n" + 
    		"    <p>\n" + 
    		"        <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"simpletable_nd5_gyd_5k\">\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>peach</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>péché</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>pêche</stentry>\n" + 
    		"                </strow>\n" + 
    		"            </simpletable></p>\n" + 
    		"  </body>\n" + 
    		"</topic>\n" + 
    		"", true);
    
    Locale.setDefault(new Locale("fr", "fr"));
    
    moveCaretRelativeTo("peach", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(0);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
    		"<topic id=\"care\">\n" + 
    		"  <title></title>\n" + 
    		"  <body>\n" + 
    		"    <p>\n" + 
    		"        <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"simpletable_nd5_gyd_5k\">\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>peach</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>pêche</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>péché</stentry>\n" + 
    		"                </strow>\n" + 
    		"            </simpletable></p>\n" + 
    		"  </body>\n" + 
    		"</topic>\n" + 
    		"", true);
  }
  
  /**
   * <p><b>Description:</b> Test that the language used to sort the values is detected from document.</p>
   * <p><b>Bug ID:</b> EXM-27984</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testSortWithLangFromDocument() throws Exception {
    // language is "fr-FR"
    // The order is: peach, pêche, péché 
    open(URLUtil.correct(new File("test/EXM-12505/sortWithLangInDocument.dita")), true);
  
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("peach", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(0);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "  <title></title>\n" + 
        "  <body>\n" + 
        "    <p>\n" + 
        "        <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"simpletable_nd5_gyd_5k\"\n" + 
        "                xml:lang=\"Fr-fR\">\n" + 
        "                <strow>\n" + 
        "                    <stentry>peach</stentry>\n" + 
        "                </strow>\n" + 
        "                <strow>\n" + 
        "                    <stentry>pêche</stentry>\n" + 
        "                </strow>\n" + 
        "                <strow>\n" + 
        "                    <stentry>péché</stentry>\n" + 
        "                </strow>\n" + 
        "            </simpletable></p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    // Language is "fr"
    // The order is: peach, pêche, péché
    open(URLUtil.correct(new File("test/EXM-12505/sortWithLangInDocument2.dita")), true);
    
    editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("peach", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(0);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "  <title></title>\n" + 
        "  <body>\n" + 
        "    <p>\n" + 
        "        <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"simpletable_nd5_gyd_5k\" xml:lang=\"fR\">\n" + 
        "                <strow>\n" + 
        "                    <stentry>peach</stentry>\n" + 
        "                </strow>\n" + 
        "                <strow>\n" + 
        "                    <stentry>pêche</stentry>\n" + 
        "                </strow>\n" + 
        "                <strow>\n" + 
        "                    <stentry>péché</stentry>\n" + 
        "                </strow>\n" + 
        "            </simpletable></p>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "", true);
    
    // Language is "error"
    // The order is: peach, péché, pêche
    open(URLUtil.correct(new File("test/EXM-12505/sortWithLangInDocument3.dita")), true);
    
    editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("peach", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(0);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
    		"<topic id=\"care\">\n" + 
    		"  <title></title>\n" + 
    		"  <body>\n" + 
    		"    <p>\n" + 
    		"        <simpletable frame=\"all\" relcolwidth=\"1.0* 1.0*\" id=\"simpletable_nd5_gyd_5k\"\n" + 
    		"                xml:lang=\"error\">\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>peach</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>péché</stentry>\n" + 
    		"                </strow>\n" + 
    		"                <strow>\n" + 
    		"                    <stentry>pêche</stentry>\n" + 
    		"                </strow>\n" + 
    		"            </simpletable></p>\n" + 
    		"  </body>\n" + 
    		"</topic>\n" + 
    		"", true);
  }

  /**
   * <p><b>Description:</b> Test the table sort operation.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITACALSTableWithDateType() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/fisierSortare.dita")), true);
  
    moveCaretRelativeTo("04/01/", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
  
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    sleep(1000);
    JRadioButton AllElmsRadio = findComponent(dialog, JRadioButton.class, 1);
    assertNotNull(AllElmsRadio);
    assertTrue(AllElmsRadio.isEnabled());
    
    JComboBox combo = findComponent(dialog, JComboBox.class, 1);
    combo.setSelectedIndex(2);
  
    JComboBox combo1 = findComponent(dialog, JComboBox.class, 2);
    combo1.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    sleep(200);
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
    		"<topic id=\"topic_lmv_ftt_dl\">\n" + 
    		"  <title>Sort</title>\n" + 
    		"  <body>\n" + 
    		"    <p>Table:<table frame=\"all\" id=\"table_k1g_htt_dl\">\n" + 
    		"        <title/>\n" + 
    		"        <tgroup cols=\"3\">\n" + 
    		"          <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.02*\"/>\n" + 
    		"          <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1*\"/>\n" + 
    		"          <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.04*\"/>\n" + 
    		"          <thead>\n" + 
    		"            <row>\n" + 
    		"              <entry>Name</entry>\n" + 
    		"              <entry>Age</entry>\n" + 
    		"              <entry>Birthdate</entry>\n" + 
    		"            </row>\n" + 
    		"          </thead>\n" + 
    		"          <tbody>\n" + 
    		"                        <row>\n" + 
    		"                            <entry>John</entry>\n" + 
    		"                            <entry>22</entry>\n" + 
    		"                            <entry>04/01/1991</entry>\n" + 
    		"                        </row>\n" + 
    		"                        <row>\n" + 
    		"                            <entry>John</entry>\n" + 
    		"                            <entry>22</entry>\n" + 
    		"                            <entry>03/12/1991</entry>\n" + 
    		"                        </row>\n" + 
    		"                        <row>\n" + 
    		"                            <entry>Anna</entry>\n" + 
    		"                            <entry>34</entry>\n" + 
    		"                            <entry>06/25/1979</entry>\n" + 
    		"                        </row>\n" + 
    		"                    </tbody>\n" + 
    		"        </tgroup>\n" + 
    		"      </table></p>\n" + 
    		"  </body>\n" + 
    		"</topic>\n" + 
    		"", true);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortDITAOrderedList() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/ditaList.dita")), true);
    
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    moveCaretRelativeTo("B", 1);
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"topic_zmz_skl_5k\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>\n" + 
        "            <ol id=\"ol_dq3_tkl_5k\">\n" + 
        "                <li>E</li>\n" + 
        "                <li>D</li>\n" + 
        "                <li>C</li>\n" + 
        "                <li>B</li>\n" + 
        "                <li>A</li>\n" + 
        "            </ol>\n" + 
        "        </p>\n" + 
        "        <p>\n" + 
        "            <sl>\n" + 
        "                <sli>CS</sli>\n" + 
        "                <sli>BS</sli>\n" + 
        "                <sli>ES</sli>\n" + 
        "                <sli>AS</sli>\n" + 
        "                <sli>DS</sli>\n" + 
        "            </sl>\n" + 
        "            <parml>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>V</pt>\n" + 
        "                    <pd>D</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>C</pt>\n" + 
        "                    <pd>A</pd>\n" + 
        "                </plentry>\n" + 
        "                <plentry>\n" + 
        "                    <pt>X</pt>\n" + 
        "                    <pd>S</pd>\n" + 
        "                </plentry>\n" + 
        "            </parml>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "", true);
  }
}