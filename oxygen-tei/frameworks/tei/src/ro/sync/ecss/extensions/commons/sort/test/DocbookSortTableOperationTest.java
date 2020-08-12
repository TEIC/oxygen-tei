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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.exml.editor.EditorPage;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.util.URLUtil;

/**
 * Test class for Docbook table sort operation.
 */
public class DocbookSortTableOperationTest extends EditorAuthorExtensionTestBase {

  /**
   * <p><b>Description:</b> Test the Sort operation for Docbook (with and without row span).</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testDocbookCALSSortTable() throws Exception {
    String originalContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<article xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
    		"    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
    		"    <info>\n" + 
    		"        <title>Welcome to Docbook Support in oXygen</title>\n" + 
    		"    </info>\n" + 
    		"    <sect1>\n" + 
    		"        <title>Lists and Tables</title>\n" + 
    		"        <table xml:id=\"ex.calstable\">\n" + 
    		"            <title>Sample CALS Table with no specified width and proportional column widths</title>\n" + 
    		"            <tgroup cols=\"5\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
    		"                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"0.32*\"/>\n" + 
    		"                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"1.49*\"/>\n" + 
    		"                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1.15*\"/>\n" + 
    		"                <colspec colnum=\"4\" colname=\"c4\" colwidth=\"0.4*\"/>\n" + 
    		"                <colspec colnum=\"5\" colname=\"c5\" colwidth=\"1.67*\"/>\n" + 
    		"                <thead>\n" + 
    		"                    <row>\n" + 
    		"                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
    		"                        <entry>a3</entry>\n" + 
    		"                        <entry>a4</entry>\n" + 
    		"                        <entry>a5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </thead>\n" + 
    		"                <tfoot>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>f1</entry>\n" + 
    		"                        <entry>f2</entry>\n" + 
    		"                        <entry>f3</entry>\n" + 
    		"                        <entry>f4</entry>\n" + 
    		"                        <entry>f5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </tfoot>\n" + 
    		"                <tbody>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>b1</entry>\n" + 
    		"                        <entry>b2</entry>\n" + 
    		"                        <entry>b3</entry>\n" + 
    		"                        <entry>b4</entry>\n" + 
    		"                        <entry morerows=\"1\" valign=\"middle\">\n" + 
    		"                            <para>\n" + 
    		"                                <emphasis role=\"bold\">Vertical</emphasis> Span </para>\n" + 
    		"                        </entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>c1</entry>\n" + 
    		"                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" morerows=\"1\" valign=\"bottom\"\n" + 
    		"                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
    		"                        <entry>c4</entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>d1</entry>\n" + 
    		"                        <entry>d4</entry>\n" + 
    		"                        <entry>d5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </tbody>\n" + 
    		"            </tgroup>\n" + 
    		"        </table>\n" + 
    		"        <para><code>Docbook 5</code> also supports the <abbrev>HTML</abbrev> tables:</para>\n" + 
    		"        <table width=\"445\" frame=\"border\" rules=\"all\">\n" + 
    		"            <caption>Sample HTML Table with fixed width.</caption>\n" + 
    		"            <col width=\"80%\"/>\n" + 
    		"            <col width=\"20%\"/>\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>Person Name</th>\n" + 
    		"                    <th>Age</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Alexander Jane</td>\n" + 
    		"                    <td>26</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Bart </td>\n" + 
    		"                    <td>24</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Jane</td>\n" + 
    		"                    <td>22</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td colspan=\"2\">\n" + 
    		"                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
    		"                            department</emphasis>\n" + 
    		"                    </td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </sect1>\n" + 
    		"</article>\n" + 
    		"";
    open(URLUtil.correct(new File("test/EXM-12505/testDocbookCALS.xml")), true);

    // Move the caret after title
    moveCaretRelativeTo("Horizontal Span", 0);

    flushAWTBetter();

    invokeActionForID("sort");
    Thread.sleep(500);

    assertEquals("The sort operation couldn't be performed.\n" + 
    		"The 'Sort' operation is unavailable for tables with multiple rowspan cells.", lastErrorMessage);

    verifyDocument(originalContent, true);

    // Move the caret after title
    moveCaretRelativeTo("Jane", 0);

    flushAWTBetter();

    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog("Sort");
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    sleep(1000);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();

    verifyDocument( 
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<article xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\"\n" + 
    		"    xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" + 
    		"    <info>\n" + 
    		"        <title>Welcome to Docbook Support in oXygen</title>\n" + 
    		"    </info>\n" + 
    		"    <sect1>\n" + 
    		"        <title>Lists and Tables</title>\n" + 
    		"        <table xml:id=\"ex.calstable\">\n" + 
    		"            <title>Sample CALS Table with no specified width and proportional column widths</title>\n" + 
    		"            <tgroup cols=\"5\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
    		"                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"0.32*\"/>\n" + 
    		"                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"1.49*\"/>\n" + 
    		"                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1.15*\"/>\n" + 
    		"                <colspec colnum=\"4\" colname=\"c4\" colwidth=\"0.4*\"/>\n" + 
    		"                <colspec colnum=\"5\" colname=\"c5\" colwidth=\"1.67*\"/>\n" + 
    		"                <thead>\n" + 
    		"                    <row>\n" + 
    		"                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
    		"                        <entry>a3</entry>\n" + 
    		"                        <entry>a4</entry>\n" + 
    		"                        <entry>a5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </thead>\n" + 
    		"                <tfoot>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>f1</entry>\n" + 
    		"                        <entry>f2</entry>\n" + 
    		"                        <entry>f3</entry>\n" + 
    		"                        <entry>f4</entry>\n" + 
    		"                        <entry>f5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </tfoot>\n" + 
    		"                <tbody>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>b1</entry>\n" + 
    		"                        <entry>b2</entry>\n" + 
    		"                        <entry>b3</entry>\n" + 
    		"                        <entry>b4</entry>\n" + 
    		"                        <entry morerows=\"1\" valign=\"middle\">\n" + 
    		"                            <para>\n" + 
    		"                                <emphasis role=\"bold\">Vertical</emphasis> Span </para>\n" + 
    		"                        </entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>c1</entry>\n" + 
    		"                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" morerows=\"1\" valign=\"bottom\"\n" + 
    		"                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
    		"                        <entry>c4</entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>d1</entry>\n" + 
    		"                        <entry>d4</entry>\n" + 
    		"                        <entry>d5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </tbody>\n" + 
    		"            </tgroup>\n" + 
    		"        </table>\n" + 
    		"        <para><code>Docbook 5</code> also supports the <abbrev>HTML</abbrev> tables:</para>\n" + 
    		"        <table width=\"445\" frame=\"border\" rules=\"all\">\n" + 
    		"            <caption>Sample HTML Table with fixed width.</caption>\n" + 
    		"            <col width=\"80%\"/>\n" + 
    		"            <col width=\"20%\"/>\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>Person Name</th>\n" + 
    		"                    <th>Age</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td colspan=\"2\">\n" + 
    		"                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
    		"                            department</emphasis>\n" + 
    		"                    </td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Jane</td>\n" + 
    		"                    <td>22</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Bart </td>\n" + 
    		"                    <td>24</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Alexander Jane</td>\n" + 
    		"                    <td>26</td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </sect1>\n" + 
    		"</article>\n" + 
    		"", true);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation for Docbook (with row span and tfoot element).</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testDocbookCALSSortTable2() throws Exception {
    String originalContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
    		"                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
    		"<article>\n" + 
    		"    <title>Welcome to Docbook Support in oXygen</title>\n" + 
    		"    <sect1>\n" + 
    		"        <title>CALS Tables and Lists</title>\n" + 
    		"        <table>\n" + 
    		"            <title>Sample CALS Table with no specified width and proportional column widths</title>\n" + 
    		"            <tgroup cols=\"5\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
    		"                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"0.32*\"/>\n" + 
    		"                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"1.49*\"/>\n" + 
    		"                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1.15*\"/>\n" + 
    		"                <colspec colnum=\"4\" colname=\"c4\" colwidth=\"0.4*\"/>\n" + 
    		"                <colspec colnum=\"5\" colname=\"c5\" colwidth=\"1.67*\"/>\n" + 
    		"                <thead>\n" + 
    		"                    <row>\n" + 
    		"                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
    		"                        <entry>a3</entry>\n" + 
    		"                        <entry>a4</entry>\n" + 
    		"                        <entry>a5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </thead>\n" + 
    		"                <tfoot>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>f1</entry>\n" + 
    		"                        <entry>f2</entry>\n" + 
    		"                        <entry>f3</entry>\n" + 
    		"                        <entry>f4</entry>\n" + 
    		"                        <entry>f5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </tfoot>\n" + 
    		"                <tbody>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>b1</entry>\n" + 
    		"                        <entry>b2</entry>\n" + 
    		"                        <entry>b3</entry>\n" + 
    		"                        <entry>b4</entry>\n" + 
    		"                        <entry morerows=\"1\" valign=\"middle\">\n" + 
    		"                            <para>\n" + 
    		"                                <emphasis role=\"bold\">Vertical</emphasis> Span </para>\n" + 
    		"                        </entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>c1</entry>\n" + 
    		"                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" morerows=\"1\" valign=\"bottom\"\n" + 
    		"                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
    		"                        <entry>c4</entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>d1</entry>\n" + 
    		"                        <entry>d4</entry>\n" + 
    		"                        <entry>d5</entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>a1</entry>\n" + 
    		"                        <entry>a</entry>\n" + 
    		"                        <entry>a</entry>\n" + 
    		"                        <entry>a</entry>\n" + 
    		"                        <entry>a</entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>b</entry>\n" + 
    		"                        <entry>b</entry>\n" + 
    		"                        <entry>b</entry>\n" + 
    		"                        <entry>b</entry>\n" + 
    		"                        <entry>b10</entry>\n" + 
    		"                    </row>\n" + 
    		"                </tbody>\n" + 
    		"            </tgroup>\n" + 
    		"        </table>\n" + 
    		"    </sect1>\n" + 
    		"</article>\n" + 
    		"";
    open(URLUtil.correct(new File("test/EXM-12505/testDB4RowSpan.xml")), true);
  
    // Move the caret after title
    moveCaretRelativeTo("f2", 0);
  
    flushAWTBetter();
  
    invokeActionForID("sort");
    Thread.sleep(500);
  
    assertEquals("The sort operation couldn't be performed.\n" + 
    		"The 'Sort' operation is unavailable for tables with multiple rowspan cells.", lastErrorMessage);
  
    verifyDocument(originalContent, true);
  
    // Move the caret after title
    moveCaretRelativeTo("f1", -1);
    flushAWTBetter();
    
    moveCaretRelativeTo("f5", 2, true);
  
    invokeActionForID("sort");
    Thread.sleep(500);
    
    assertEquals("The sort operation couldn't be performed.\n" + 
        "The 'Sort' operation is unavailable for tables with multiple rowspan cells.", lastErrorMessage);
    
    moveCaretRelativeTo("a1", -1);
    flushAWTBetter();
    
    moveCaretRelativeTo("b10", 1, true);
    flushAWTBetter();
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog("Sort");
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument( 
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
    		"                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
    		"<article>\n" + 
    		"    <title>Welcome to Docbook Support in oXygen</title>\n" + 
    		"    <sect1>\n" + 
    		"        <title>CALS Tables and Lists</title>\n" + 
    		"        <table>\n" + 
    		"            <title>Sample CALS Table with no specified width and proportional column widths</title>\n" + 
    		"            <tgroup cols=\"5\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
    		"                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"0.32*\"/>\n" + 
    		"                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"1.49*\"/>\n" + 
    		"                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1.15*\"/>\n" + 
    		"                <colspec colnum=\"4\" colname=\"c4\" colwidth=\"0.4*\"/>\n" + 
    		"                <colspec colnum=\"5\" colname=\"c5\" colwidth=\"1.67*\"/>\n" + 
    		"                <thead>\n" + 
    		"                    <row>\n" + 
    		"                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
    		"                        <entry>a3</entry>\n" + 
    		"                        <entry>a4</entry>\n" + 
    		"                        <entry>a5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </thead>\n" + 
    		"                <tfoot>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>f1</entry>\n" + 
    		"                        <entry>f2</entry>\n" + 
    		"                        <entry>f3</entry>\n" + 
    		"                        <entry>f4</entry>\n" + 
    		"                        <entry>f5</entry>\n" + 
    		"                    </row>\n" + 
    		"                </tfoot>\n" + 
    		"                <tbody>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>b1</entry>\n" + 
    		"                        <entry>b2</entry>\n" + 
    		"                        <entry>b3</entry>\n" + 
    		"                        <entry>b4</entry>\n" + 
    		"                        <entry morerows=\"1\" valign=\"middle\">\n" + 
    		"                            <para>\n" + 
    		"                                <emphasis role=\"bold\">Vertical</emphasis> Span </para>\n" + 
    		"                        </entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>c1</entry>\n" + 
    		"                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" morerows=\"1\" valign=\"bottom\"\n" + 
    		"                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
    		"                        <entry>c4</entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>d1</entry>\n" + 
    		"                        <entry>d4</entry>\n" + 
    		"                        <entry>d5</entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>b</entry>\n" + 
    		"                        <entry>b</entry>\n" + 
    		"                        <entry>b</entry>\n" + 
    		"                        <entry>b</entry>\n" + 
    		"                        <entry>b10</entry>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <entry>a1</entry>\n" + 
    		"                        <entry>a</entry>\n" + 
    		"                        <entry>a</entry>\n" + 
    		"                        <entry>a</entry>\n" + 
    		"                        <entry>a</entry>\n" + 
    		"                    </row>\n" + 
    		"                </tbody>\n" + 
    		"            </tgroup>\n" + 
    		"        </table>\n" + 
    		"    </sect1>\n" + 
    		"</article>\n" + 
    		"", true);
  }
  
  /**
   * <p><b>Description:</b> Test sorting a DocBook table with two criteria.</p>
   * <p><b>Bug ID:</b> EXM-27895</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void testSortDocbookTableEXM_27895() throws Exception {
    open(URLUtil.correct(new File("test/EXM-12505/testEXM_27895.xml")), true);
  
    // Move the caret after title
    moveCaretRelativeTo("bart", 0);
  
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog("Sort");
    JComboBox combo = findComponent(dialog, JComboBox.class, 0);
    combo.setSelectedIndex(1);
    
    combo = findComponent(dialog, JComboBox.class, 1);
    combo.setSelectedIndex(1);
    
    JCheckBox cb = findComponent(dialog, JCheckBox.class, 1);
    cb.setSelected(true);
    sleep(500);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
  
    verifyDocument( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>\n" + 
        "            <table width=\"445\" frame=\"border\" rules=\"all\">\n" + 
        "                <caption>Sample HTML Table with fixed width.</caption>\n" + 
        "                <col width=\"80%\"/>\n" + 
        "                <col width=\"20%\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>Person Name</th>\n" + 
        "                        <th>Age</th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>aserehe</td>\n" + 
        "                        <td>3</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>bart</td>\n" + 
        "                        <td>3</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }
  
  /**
   * <p><b>Description:</b> Sort operation should apply on the nearest sortable parent Docbook4 CALS</p>
   * <p><b>Bug ID:</b> EXM-27906</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void testSortClosestParent_Docbook4CALS_EXM_27906() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27906/sortClosestParentDocbook4CALS.xml")), true);
  
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
    
    // Sort the table from the footer of the big table.
    moveCaretRelativeTo("foot table1", 1);
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
    
    // Sort the list from the footer of the big table.
    moveCaretRelativeTo("foot list1", 1);
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></entry>\n" + 
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
   * <p><b>Description:</b> Sort operation should apply on the nearest sortable parent Docbook5 HTML</p>
   * <p><b>Bug ID:</b> EXM-27906</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void testSortClosestParent_Docbook5HTML_EXM_27906() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27906/sortClosestParentDocbook5HTML.xml")), true);
  
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>\n" + 
        "                            <itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <thead>\n" + 
        "                                            <tr>\n" + 
        "                                                <th/>\n" + 
        "                                            </tr>\n" + 
        "                                        </thead>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>head table1</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>head table2</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>head table3</td>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist>\n" + 
        "                        </th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <tr>\n" + 
        "                        <td><variablelist>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term1</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table1</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table2</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table3</td>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term2</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list2</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term3</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                        </variablelist></td>\n" + 
        "                    </tr>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td><para>top table1</para><orderedlist >\n" + 
        "                            <listitem>\n" + 
        "                                <para>item1<table frame=\"void\">\n" + 
        "                                    <caption/>\n" + 
        "                                    <col width=\"1.0*\"/>\n" + 
        "                                    <thead>\n" + 
        "                                        <tr>\n" + 
        "                                            <th/>\n" + 
        "                                        </tr>\n" + 
        "                                    </thead>\n" + 
        "                                    <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                </table></para>\n" + 
        "                            </listitem>\n" + 
        "                            <listitem>\n" + 
        "                                <para>item2</para>\n" + 
        "                            </listitem>\n" + 
        "                            <listitem>\n" + 
        "                                <para>item3</para>\n" + 
        "                            </listitem>\n" + 
        "                        </orderedlist></td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table2</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table3</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>\n" + 
        "                            <itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <thead>\n" + 
        "                                            <tr>\n" + 
        "                                                <th/>\n" + 
        "                                            </tr>\n" + 
        "                                        </thead>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>head table1</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>head table2</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>head table3</td>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist>\n" + 
        "                        </th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <tr>\n" + 
        "                        <td><variablelist>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term1</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table1</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table2</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table3</td>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term2</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list2</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term3</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                        </variablelist></td>\n" + 
        "                    </tr>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td><para>top table1</para><orderedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item3</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item1<table frame=\"void\">\n" + 
        "                                            <caption/>\n" + 
        "                                            <col width=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                </tr>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                        </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </orderedlist></td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table2</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table3</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>\n" + 
        "                            <itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <thead>\n" + 
        "                                            <tr>\n" + 
        "                                                <th/>\n" + 
        "                                            </tr>\n" + 
        "                                        </thead>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>head table1</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>head table2</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>head table3</td>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist>\n" + 
        "                        </th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <tr>\n" + 
        "                        <td><variablelist>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term1</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table1</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table2</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table3</td>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term2</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list2</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term3</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                        </variablelist></td>\n" + 
        "                    </tr>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table3</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table2</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>\n" + 
        "                            <para>top table1</para>\n" + 
        "                            <orderedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item3</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item1<table frame=\"void\">\n" + 
        "                                            <caption/>\n" + 
        "                                            <col width=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                </tr>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                        </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </orderedlist>\n" + 
        "                        </td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>\n" + 
        "                            <itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <thead>\n" + 
        "                                            <tr>\n" + 
        "                                                <th/>\n" + 
        "                                            </tr>\n" + 
        "                                        </thead>\n" + 
        "                                        <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist>\n" + 
        "                        </th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <tr>\n" + 
        "                        <td><variablelist>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term1</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table1</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table2</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table3</td>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term2</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list2</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term3</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                        </variablelist></td>\n" + 
        "                    </tr>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table3</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table2</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>\n" + 
        "                            <para>top table1</para>\n" + 
        "                            <orderedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item3</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item1<table frame=\"void\">\n" + 
        "                                            <caption/>\n" + 
        "                                            <col width=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                </tr>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                        </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </orderedlist>\n" + 
        "                        </td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>\n" + 
        "                            <itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"void\">\n" + 
        "                                            <caption/>\n" + 
        "                                            <col width=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                </tr>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                        </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist>\n" + 
        "                        </th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <tr>\n" + 
        "                        <td><variablelist>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term1</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <tbody>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table1</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table2</td>\n" + 
        "                                            </tr>\n" + 
        "                                            <tr>\n" + 
        "                                                <td>foot table3</td>\n" + 
        "                                            </tr>\n" + 
        "                                        </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term2</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list2</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term3</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                        </variablelist></td>\n" + 
        "                    </tr>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table3</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table2</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>\n" + 
        "                            <para>top table1</para>\n" + 
        "                            <orderedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item3</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item1<table frame=\"void\">\n" + 
        "                                            <caption/>\n" + 
        "                                            <col width=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                </tr>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                        </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </orderedlist>\n" + 
        "                        </td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
    
    // Sort the table from the footer of the big table.
    moveCaretRelativeTo("foot table1", 1);
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>\n" + 
        "                            <itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"void\">\n" + 
        "                                            <caption/>\n" + 
        "                                            <col width=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                </tr>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                        </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist>\n" + 
        "                        </th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <tr>\n" + 
        "                        <td><variablelist>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term1</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list1<table frame=\"void\">\n" + 
        "                                        <caption/>\n" + 
        "                                        <col width=\"1.0*\"/>\n" + 
        "                                        <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                </tbody>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term2</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list2</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                            <varlistentry>\n" + 
        "                                <term>term3</term>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>foot list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </varlistentry>\n" + 
        "                        </variablelist></td>\n" + 
        "                    </tr>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table3</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table2</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>\n" + 
        "                            <para>top table1</para>\n" + 
        "                            <orderedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item3</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item1<table frame=\"void\">\n" + 
        "                                            <caption/>\n" + 
        "                                            <col width=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                </tr>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                        </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </orderedlist>\n" + 
        "                        </td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
    
    // Sort the list from the footer of the big table.
    moveCaretRelativeTo("foot list1", 1);
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <col width=\"1.0*\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th>\n" + 
        "                            <itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"void\">\n" + 
        "                                            <caption/>\n" + 
        "                                            <col width=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                </tr>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                        </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist>\n" + 
        "                        </th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tfoot>\n" + 
        "                    <tr>\n" + 
        "                        <td><variablelist>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term3</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term2</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term1</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list1<table frame=\"void\">\n" + 
        "                                                <caption/>\n" + 
        "                                                <col width=\"1.0*\"/>\n" + 
        "                                                <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                </tbody>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                            </variablelist></td>\n" + 
        "                    </tr>\n" + 
        "                </tfoot>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table3</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>top table2</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>\n" + 
        "                            <para>top table1</para>\n" + 
        "                            <orderedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item3</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item1<table frame=\"void\">\n" + 
        "                                            <caption/>\n" + 
        "                                            <col width=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                </tr>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                </tr>\n" + 
        "                                                <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                </tr>\n" + 
        "                                            </tbody>\n" + 
        "                                        </table></para>\n" + 
        "                                </listitem>\n" + 
        "                            </orderedlist>\n" + 
        "                        </td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }

  /**
   * <p><b>Description:</b> Sort operation should apply on the nearest sortable parent Docbook5 HTML</p>
   * <p><b>Bug ID:</b> EXM-27906</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void testSortClosestParent_Docbook5CALS_EXM_27906() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27906/sortClosestParentDocbook5CALS.xml")), true);
  
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry><itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"all\">\n" + 
        "                                        <title/>\n" + 
        "                                        <tgroup cols=\"1\">\n" + 
        "                                            <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry/>\n" + 
        "                                                </row>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry>head table1</entry>\n" + 
        "                                                </row>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry>head table2</entry>\n" + 
        "                                                </row>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry>head table3</entry>\n" + 
        "                                                </row>\n" + 
        "                                            </tbody>\n" + 
        "                                        </tgroup>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry><variablelist>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term1</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list1<table frame=\"all\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <tbody>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table1</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table2</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table3</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term2</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term3</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                            </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry><para>top table1</para><orderedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item1<table frame=\"all\">\n" + 
        "                                        <title/>\n" + 
        "                                        <tgroup cols=\"1\">\n" + 
        "                                            <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry/>\n" + 
        "                                                </row>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                        </tgroup>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>item3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </orderedlist></entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry><itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"all\">\n" + 
        "                                        <title/>\n" + 
        "                                        <tgroup cols=\"1\">\n" + 
        "                                            <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry/>\n" + 
        "                                                </row>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry>head table1</entry>\n" + 
        "                                                </row>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry>head table2</entry>\n" + 
        "                                                </row>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry>head table3</entry>\n" + 
        "                                                </row>\n" + 
        "                                            </tbody>\n" + 
        "                                        </tgroup>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry><variablelist>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term1</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list1<table frame=\"all\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <tbody>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table1</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table2</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table3</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term2</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term3</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                            </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry><para>top table1</para><orderedlist>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry><itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"all\">\n" + 
        "                                        <title/>\n" + 
        "                                        <tgroup cols=\"1\">\n" + 
        "                                            <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry/>\n" + 
        "                                                </row>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry>head table1</entry>\n" + 
        "                                                </row>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry>head table2</entry>\n" + 
        "                                                </row>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry>head table3</entry>\n" + 
        "                                                </row>\n" + 
        "                                            </tbody>\n" + 
        "                                        </tgroup>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry><variablelist>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term1</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list1<table frame=\"all\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <tbody>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table1</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table2</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table3</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term2</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term3</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                            </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>\n" + 
        "                                <para>top table1</para>\n" + 
        "                                <orderedlist>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist>\n" + 
        "                            </entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry><itemizedlist>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list1<table frame=\"all\">\n" + 
        "                                        <title/>\n" + 
        "                                        <tgroup cols=\"1\">\n" + 
        "                                            <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                            <thead>\n" + 
        "                                                <row>\n" + 
        "                                                    <entry/>\n" + 
        "                                                </row>\n" + 
        "                                            </thead>\n" + 
        "                                            <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                        </tgroup>\n" + 
        "                                    </table></para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list2</para>\n" + 
        "                                </listitem>\n" + 
        "                                <listitem>\n" + 
        "                                    <para>head list3</para>\n" + 
        "                                </listitem>\n" + 
        "                            </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry><variablelist>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term1</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list1<table frame=\"all\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <tbody>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table1</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table2</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table3</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term2</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term3</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                            </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>\n" + 
        "                                <para>top table1</para>\n" + 
        "                                <orderedlist>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist>\n" + 
        "                            </entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry><itemizedlist>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry><variablelist>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term1</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list1<table frame=\"all\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <tbody>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table1</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table2</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                    <row>\n" + 
        "                                                        <entry>foot table3</entry>\n" + 
        "                                                    </row>\n" + 
        "                                                </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term2</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term3</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                            </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>\n" + 
        "                                <para>top table1</para>\n" + 
        "                                <orderedlist>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist>\n" + 
        "                            </entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
    
    // Sort the table from the footer of the big table.
    moveCaretRelativeTo("foot table1", 1);
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry><itemizedlist>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry><variablelist>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term1</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list1<table frame=\"all\">\n" + 
        "                                            <title/>\n" + 
        "                                            <tgroup cols=\"1\">\n" + 
        "                                                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                            </tgroup>\n" + 
        "                                        </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term2</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                                <varlistentry>\n" + 
        "                                    <term>term3</term>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>foot list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </varlistentry>\n" + 
        "                            </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>\n" + 
        "                                <para>top table1</para>\n" + 
        "                                <orderedlist>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist>\n" + 
        "                            </entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
    
    // Sort the list from the footer of the big table.
    moveCaretRelativeTo("foot list1", 1);
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
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://docbook.org/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title/>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry><itemizedlist>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>head table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <row>\n" + 
        "                            <entry><variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>foot table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></entry>\n" + 
        "                        </row>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table3</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>top table2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>\n" + 
        "                                <para>top table1</para>\n" + 
        "                                <orderedlist>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry/>\n" + 
        "                                                  </row>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table1</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table2</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  <row>\n" + 
        "                                                  <entry>inner table3</entry>\n" + 
        "                                                  </row>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist>\n" + 
        "                            </entry>\n" + 
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
   * <p><b>Description:</b> Sort operation should apply on the nearest sortable parent Docbook4 HTML</p>
   * <p><b>Bug ID:</b> EXM-27906</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void testSortClosestParent_Docbook4HTML_EXM_27906() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27906/sortClosestParentDocbook4HTML.xml")), true);
  
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <tr>\n" + 
        "                            <th>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></th>\n" + 
        "                        </tr>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <tr>\n" + 
        "                            <td>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table2</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table3</td>\n" + 
        "                        </tr>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <tr>\n" + 
        "                            <th>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></th>\n" + 
        "                        </tr>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <tr>\n" + 
        "                            <td>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table2</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table3</td>\n" + 
        "                        </tr>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <tr>\n" + 
        "                            <th>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></th>\n" + 
        "                        </tr>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <tr>\n" + 
        "                            <td>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table3</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table2</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <tr>\n" + 
        "                            <th>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></th>\n" + 
        "                        </tr>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <tr>\n" + 
        "                            <td>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table3</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table2</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <tr>\n" + 
        "                            <th>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></th>\n" + 
        "                        </tr>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <tr>\n" + 
        "                            <td>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table3</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table2</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
    
    // Sort the table from the footer of the big table.
    moveCaretRelativeTo("foot table1", 1);
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <tr>\n" + 
        "                            <th>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></th>\n" + 
        "                        </tr>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <tr>\n" + 
        "                            <td>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table3</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table2</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
    
    // Sort the list from the footer of the big table.
    moveCaretRelativeTo("foot list1", 1);
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
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title/>\n" + 
        "    <sect1>\n" + 
        "        <title/>\n" + 
        "        <para>\n" + 
        "            <table frame=\"all\" id=\"table_wf3_txs_bl\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"3\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                    <colspec colname=\"c3\" colnum=\"3\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <tr>\n" + 
        "                            <th>head<itemizedlist id=\"ul_hz3_dys_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>head list1<table frame=\"all\" id=\"table_s1s_2ys_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>head table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </itemizedlist></th>\n" + 
        "                        </tr>\n" + 
        "                    </thead>\n" + 
        "                    <tfoot>\n" + 
        "                        <tr>\n" + 
        "                            <td>foot<variablelist>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term3</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list3</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term2</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list2</para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                    <varlistentry>\n" + 
        "                                        <term>term1</term>\n" + 
        "                                        <listitem>\n" + 
        "                                            <para>foot list1<table frame=\"all\">\n" + 
        "                                                  <title/>\n" + 
        "                                                  <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>foot table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                  </tgroup>\n" + 
        "                                                </table></para>\n" + 
        "                                        </listitem>\n" + 
        "                                    </varlistentry>\n" + 
        "                                </variablelist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tfoot>\n" + 
        "                    <tbody>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table3</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table2</td>\n" + 
        "                        </tr>\n" + 
        "                        <tr>\n" + 
        "                            <td>top table1<orderedlist id=\"ol_wxk_vxs_bl\">\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item3</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item2</para>\n" + 
        "                                    </listitem>\n" + 
        "                                    <listitem>\n" + 
        "                                        <para>item1<table frame=\"all\" id=\"table_gb5_xxs_bl\">\n" + 
        "                                                <title/>\n" + 
        "                                                <tgroup cols=\"1\">\n" + 
        "                                                  <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                                                  <thead>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <th/>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </thead>\n" + 
        "                                                  <tbody>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table1</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table2</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  <tr>\n" + 
        "                                                  <td>inner table3</td>\n" + 
        "                                                  </tr>\n" + 
        "                                                  </tbody>\n" + 
        "                                                </tgroup>\n" + 
        "                                            </table></para>\n" + 
        "                                    </listitem>\n" + 
        "                                </orderedlist></td>\n" + 
        "                        </tr>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", true);
  }
}