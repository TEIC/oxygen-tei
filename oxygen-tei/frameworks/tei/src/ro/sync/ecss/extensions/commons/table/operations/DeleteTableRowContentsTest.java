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
package ro.sync.ecss.extensions.commons.table.operations;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.status.MessageListener;
import ro.sync.exml.status.OxygenAppender;
import ro.sync.exml.view.ViewportMouseEvent;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.util.URLUtil;

/**
 * Tests for delete rows contents for DITA, Docbook, TEi and XHTML frameworks.
 * 
 * @author radu_coravu
 */
public class DeleteTableRowContentsTest extends EditorAuthorExtensionTestBase {

  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  /**
   * <p><b>Description:</b> Test delete row content from an author table for DITA.</p>
   * <p><b>Bug ID:</b> EXM-23621</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testDeleteTableRowDITA() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTable.dita")), true);

    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first row
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);

    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    String expectedResult1 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
    		"<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
    		"<task id=\"task_kwd_zgb_ff\">\n" + 
    		"    <title>Task title</title>\n" + 
    		"    <shortdesc/>\n" + 
    		"    <taskbody>\n" + 
    		"        <context>\n" + 
    		"            <p>Context<table frame=\"all\" id=\"table_vp2_1hb_ff\">\n" + 
    		"                    <title/>\n" + 
    		"                    <tgroup cols=\"2\">\n" + 
    		"                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
    		"                        <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
    		"                        <thead>\n" + 
    		"                            <row>\n" + 
    		"                                <entry/>\n" + 
    		"                                <entry/>\n" + 
    		"                            </row>\n" + 
    		"                        </thead>\n" + 
    		"                        <tbody>\n" + 
    		"                            <row>\n" + 
    		"                                <entry>1</entry>\n" + 
    		"                                <entry>4</entry>\n" + 
    		"                            </row>\n" + 
    		"                            <row>\n" + 
    		"                                <entry>2</entry>\n" + 
    		"                                <entry>5</entry>\n" + 
    		"                            </row>\n" + 
    		"                            <row>\n" + 
    		"                                <entry>3</entry>\n" + 
    		"                                <entry>6</entry>\n" + 
    		"                            </row>\n" + 
    		"                        </tbody>\n" + 
    		"                    </tgroup>\n" + 
    		"                </table> for the current task</p>\n" + 
    		"        </context>\n" + 
    		"    </taskbody>\n" + 
    		"</task>\n";
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted contents from selected row", expectedResult1, result);
    
    // Move the mouse over the first row
    moveCaretRelativeTo("1", 0);
    caret = authorPage.getViewport().getCaretShape();
        authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    String expectedResult2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
    		"<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
    		"<task id=\"task_kwd_zgb_ff\">\n" + 
    		"    <title>Task title</title>\n" + 
    		"    <shortdesc/>\n" + 
    		"    <taskbody>\n" + 
    		"        <context>\n" + 
    		"            <p>Context<table frame=\"all\" id=\"table_vp2_1hb_ff\">\n" + 
    		"                    <title/>\n" + 
    		"                    <tgroup cols=\"2\">\n" + 
    		"                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
    		"                        <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
    		"                        <thead>\n" + 
    		"                            <row>\n" + 
    		"                                <entry/>\n" + 
    		"                                <entry/>\n" + 
    		"                            </row>\n" + 
    		"                        </thead>\n" + 
    		"                        <tbody>\n" + 
    		"                            <row>\n" + 
    		"                                <entry/>\n" + 
    		"                                <entry/>\n" + 
    		"                            </row>\n" + 
    		"                            <row>\n" + 
    		"                                <entry>2</entry>\n" + 
    		"                                <entry>5</entry>\n" + 
    		"                            </row>\n" + 
    		"                            <row>\n" + 
    		"                                <entry>3</entry>\n" + 
    		"                                <entry>6</entry>\n" + 
    		"                            </row>\n" + 
    		"                        </tbody>\n" + 
    		"                    </tgroup>\n" + 
    		"                </table> for the current task</p>\n" + 
    		"        </context>\n" + 
    		"    </taskbody>\n" + 
    		"</task>\n";
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted contents of selected row", expectedResult2, result);
    
    doUndo();
    
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult1, result);
    
    doRedo();
    
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult2, result);
    
    
    // EXM-23621 Delete entire row
    
        authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_CLICKED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);

    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    String expectedResult3 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"table_vp2_1hb_ff\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"2\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry/>\n" + 
        "                                <entry/>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry>2</entry>\n" + 
        "                                <entry>5</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>3</entry>\n" + 
        "                                <entry>6</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table> for the current task</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n";
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row", expectedResult3, result);
    
    doUndo();
    
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult2, result);
    
    doRedo();
    
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult3, result);
    
  }

  /**
   * <p><b>Description:</b> Test delete row contents from an author table for Docbook4.</p>
   * <p><b>Bug ID:</b> EXM-23621</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testDeleteTableRowDocbook4() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableDocbook.xml")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first row
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
  
    String expectedResult1 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/docbook/css/hide_colspec.css\"?>\n" + 
    		"<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
    		"                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
    		"<article>\n" + 
    		"    <title>Article Title</title>\n" + 
    		"    <sect1>\n" + 
    		"        <title>Section1 Title</title>\n" + 
    		"        <para>Text<table frame=\"void\">\n" + 
    		"                <caption/>\n" + 
    		"                <col width=\"50%\"/>\n" + 
    		"                <col width=\"50%\"/>\n" + 
    		"                <thead>\n" + 
    		"                    <tr>\n" + 
    		"                        <th/>\n" + 
    		"                        <th/>\n" + 
    		"                    </tr>\n" + 
    		"                </thead>\n" + 
    		"                <tbody>\n" + 
    		"                    <tr>\n" + 
    		"                        <td>1</td>\n" + 
    		"                        <td>4</td>\n" + 
    		"                    </tr>\n" + 
    		"                    <tr>\n" + 
    		"                        <td>2</td>\n" + 
    		"                        <td>5</td>\n" + 
    		"                    </tr>\n" + 
    		"                    <tr>\n" + 
    		"                        <td>3</td>\n" + 
    		"                        <td>6</td>\n" + 
    		"                    </tr>\n" + 
    		"                </tbody>\n" + 
    		"            </table></para>\n" + 
    		"    </sect1>\n" + 
    		"</article>";
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row contents", expectedResult1, result);
    
    // Move the mouse over the second row
    moveCaretRelativeTo("2", 0);
    caret = authorPage.getViewport().getCaretShape();
        authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
  
    String expectedResult2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/docbook/css/hide_colspec.css\"?>\n" + 
    		"<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
    		"                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
    		"<article>\n" + 
    		"    <title>Article Title</title>\n" + 
    		"    <sect1>\n" + 
    		"        <title>Section1 Title</title>\n" + 
    		"        <para>Text<table frame=\"void\">\n" + 
    		"                <caption/>\n" + 
    		"                <col width=\"50%\"/>\n" + 
    		"                <col width=\"50%\"/>\n" + 
    		"                <thead>\n" + 
    		"                    <tr>\n" + 
    		"                        <th/>\n" + 
    		"                        <th/>\n" + 
    		"                    </tr>\n" + 
    		"                </thead>\n" + 
    		"                <tbody>\n" + 
    		"                    <tr>\n" + 
    		"                        <td>1</td>\n" + 
    		"                        <td>4</td>\n" + 
    		"                    </tr>\n" + 
    		"                    <tr>\n" + 
    		"                        <td/>\n" + 
    		"                        <td/>\n" + 
    		"                    </tr>\n" + 
    		"                    <tr>\n" + 
    		"                        <td>3</td>\n" + 
    		"                        <td>6</td>\n" + 
    		"                    </tr>\n" + 
    		"                </tbody>\n" + 
    		"            </table></para>\n" + 
    		"    </sect1>\n" + 
    		"</article>";
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row contents", expectedResult2, result);
    
    doUndo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult1, result);
    
    doRedo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult2, result);
    
    // Delete entire empty row
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_CLICKED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);

    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
  
    String expectedResult3 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/docbook/css/hide_colspec.css\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text<table frame=\"void\">\n" + 
        "                <caption/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <col width=\"50%\"/>\n" + 
        "                <thead>\n" + 
        "                    <tr>\n" + 
        "                        <th/>\n" + 
        "                        <th/>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>1</td>\n" + 
        "                        <td>4</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>3</td>\n" + 
        "                        <td>6</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>";
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row", expectedResult3, result);
    
    doUndo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult2, result);
    
    doRedo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult3, result);
  }

  /**
   * <p><b>Description:</b> Test delete row contents from an author table for XHTML.</p>
   * <p><b>Bug ID:</b> EXM-23621</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testDeleteTableRowXHTML() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableXHTML.xhtml")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first row
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 5, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 5, caret.y + 10, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);

    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    String expectedResult1 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <p>Text</p>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th></th>\n" + 
    		"                    <th></th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>1</td>\n" + 
    		"                    <td>4</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>2</td>\n" + 
    		"                    <td>5</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>3</td>\n" + 
    		"                    <td>6</td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>";
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row contents", expectedResult1, result);
    
    // Move the mouse over the first row
    moveCaretRelativeTo("1", 0);
    caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 5, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 5, caret.y + 10, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    String expectedResult2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <p>Text</p>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th></th>\n" + 
    		"                    <th></th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td></td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>2</td>\n" + 
    		"                    <td>5</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>3</td>\n" + 
    		"                    <td>6</td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>";
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row contents", expectedResult2, result);
    
    doUndo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult1, result);
    
    doRedo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult2, result);
  }

  /**
   * <p><b>Description:</b> Test delete row contents from an author table for TEI.</p>
   * <p><b>Bug ID:</b> EXM-23621</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testDeleteTableRowTEI() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableTEI.xml")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first row
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
  
    String expectedResult1 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_lite.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"  <teiHeader>\n" + 
    		"    <fileDesc>\n" + 
    		"      <titleStmt>\n" + 
    		"        <title>Title</title>\n" + 
    		"      </titleStmt>\n" + 
    		"      <publicationStmt>\n" + 
    		"        <p>Publication information</p>        \n" + 
    		"      </publicationStmt>\n" + 
    		"      <sourceDesc>\n" + 
    		"        <p>Information about the source</p>\n" + 
    		"      </sourceDesc>\n" + 
    		"    </fileDesc>\n" + 
    		"  </teiHeader>\n" + 
    		"  <text>\n" + 
    		"    <body>\n" + 
    		"      <p>Some text here.<table rows=\"3\" cols=\"2\">\n" + 
    		"          <head/>\n" + 
    		"          <row role=\"label\">\n" + 
    		"            <cell/>\n" + 
    		"            <cell/>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>1</cell>\n" + 
    		"            <cell>4</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>2</cell>\n" + 
    		"            <cell>5</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>3</cell>\n" + 
    		"            <cell>6</cell>\n" + 
    		"          </row>\n" + 
    		"        </table></p>\n" + 
    		"    </body>\n" + 
    		"  </text>\n" + 
    		"</TEI>";
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row contents", expectedResult1, result);
    
    // Move the mouse over the second row
    moveCaretRelativeTo("2", 0);
    caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
  
    String expectedResult2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_lite.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"  <teiHeader>\n" + 
    		"    <fileDesc>\n" + 
    		"      <titleStmt>\n" + 
    		"        <title>Title</title>\n" + 
    		"      </titleStmt>\n" + 
    		"      <publicationStmt>\n" + 
    		"        <p>Publication information</p>        \n" + 
    		"      </publicationStmt>\n" + 
    		"      <sourceDesc>\n" + 
    		"        <p>Information about the source</p>\n" + 
    		"      </sourceDesc>\n" + 
    		"    </fileDesc>\n" + 
    		"  </teiHeader>\n" + 
    		"  <text>\n" + 
    		"    <body>\n" + 
    		"      <p>Some text here.<table rows=\"3\" cols=\"2\">\n" + 
    		"          <head/>\n" + 
    		"          <row role=\"label\">\n" + 
    		"            <cell/>\n" + 
    		"            <cell/>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>1</cell>\n" + 
    		"            <cell>4</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell/>\n" + 
    		"            <cell/>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>3</cell>\n" + 
    		"            <cell>6</cell>\n" + 
    		"          </row>\n" + 
    		"        </table></p>\n" + 
    		"    </body>\n" + 
    		"  </text>\n" + 
    		"</TEI>";
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row contents", expectedResult2, result);
    
    doUndo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult1, result);
    
    doRedo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult2, result);
    
    // Delete entire empty row
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_CLICKED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
  
    String expectedResult3 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_lite.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
        "  <teiHeader>\n" + 
        "    <fileDesc>\n" + 
        "      <titleStmt>\n" + 
        "        <title>Title</title>\n" + 
        "      </titleStmt>\n" + 
        "      <publicationStmt>\n" + 
        "        <p>Publication information</p>        \n" + 
        "      </publicationStmt>\n" + 
        "      <sourceDesc>\n" + 
        "        <p>Information about the source</p>\n" + 
        "      </sourceDesc>\n" + 
        "    </fileDesc>\n" + 
        "  </teiHeader>\n" + 
        "  <text>\n" + 
        "    <body>\n" + 
        "      <p>Some text here.<table rows=\"2\" cols=\"2\">\n" + 
        "                    <head/>\n" + 
        "                    <row role=\"label\">\n" + 
        "                        <cell/>\n" + 
        "                        <cell/>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>1</cell>\n" + 
        "                        <cell>4</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>3</cell>\n" + 
        "                        <cell>6</cell>\n" + 
        "                    </row>\n" + 
        "                </table></p>\n" + 
        "    </body>\n" + 
        "  </text>\n" + 
        "</TEI>";
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row", expectedResult3, result);
    
    doUndo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult2, result);
    
    doRedo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult3, result);
  }

  /**
   * <p><b>Description:</b> Test delete row contents from an author table with row span for XHTML.</p>
   * <p><b>Bug ID:</b> EXM-23621</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testDeleteTableRowWithSpanXHTML() throws Exception {
    open(URLUtil.correct(new File("test/EXM-23338/xhtmltable.xhtml")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the second row
    moveCaretRelativeTo("R1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 5, caret.y + 25, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 5, caret.y + 25, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    String expectedResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>Column1</th>\n" + 
    		"                    <th>Column2</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td rowspan=\"2\">R1</td>\n" + 
    		"                    <td>3</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>R3</td>\n" + 
    		"                    <td>5</td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>";
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row contents", 
        expectedResult, result);
    
    doUndo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>Column1</th>\n" + 
    		"                    <th>Column2</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td rowspan=\"2\">R1</td>\n" + 
    		"                    <td>3</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>4</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>R3</td>\n" + 
    		"                    <td>5</td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>",
    		result);
    
    doRedo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult, result);
    
    // Delete entire empty row
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 5, caret.y + 25, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 5, caret.y + 25, 
        false, ViewportMouseEvent.STATE_CLICKED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    expectedResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
        "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <head>\n" + 
        "        <title></title>\n" + 
        "    </head>\n" + 
        "    <body>\n" + 
        "        <table frame=\"void\">\n" + 
        "            <caption></caption>\n" + 
        "            <col width=\"50%\" />\n" + 
        "            <col width=\"50%\" />\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Column1</th>\n" + 
        "                    <th>Column2</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>R1</td>\n" + 
        "                    <td>3</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>R3</td>\n" + 
        "                    <td>5</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>";
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row contents", 
        expectedResult, result);
    
    doUndo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
        "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <head>\n" + 
        "        <title></title>\n" + 
        "    </head>\n" + 
        "    <body>\n" + 
        "        <table frame=\"void\">\n" + 
        "            <caption></caption>\n" + 
        "            <col width=\"50%\" />\n" + 
        "            <col width=\"50%\" />\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Column1</th>\n" + 
        "                    <th>Column2</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td rowspan=\"2\">R1</td>\n" + 
        "                    <td>3</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td></td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>R3</td>\n" + 
        "                    <td>5</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>",
        result);
    
    doRedo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult, result);
  }

  /**
   * <p><b>Description:</b> Test delete row contents from an author table with row span for TEI.</p>
   * <p><b>Bug ID:</b> EXM-23621</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testDeleteTableRowWithSpanTEI() throws Exception {
    open(URLUtil.correct(new File("test/EXM-23338/teiTable.xml")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the second row
    moveCaretRelativeTo("R1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 28, caret.y + 30, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 28, caret.y + 30, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);

    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    String expectedResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<TEI\n" + 
    		"  xmlns:xi=\"http://www.w3.org/2001/XInclude\"\n" + 
    		"  xmlns:svg=\"http://www.w3.org/2000/svg\"\n" + 
    		"  xmlns:math=\"http://www.w3.org/1998/Math/MathML\"\n" + 
    		"  xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"  <teiHeader>\n" + 
    		"    <fileDesc>\n" + 
    		"      <titleStmt>\n" + 
    		"        <title>Title</title>\n" + 
    		"      </titleStmt>\n" + 
    		"      <publicationStmt>\n" + 
    		"        <p>Publication Information</p>\n" + 
    		"      </publicationStmt>\n" + 
    		"      <sourceDesc>\n" + 
    		"        <p>Information about the source</p>\n" + 
    		"      </sourceDesc>\n" + 
    		"    </fileDesc>\n" + 
    		"  </teiHeader>\n" + 
    		"  <text>\n" + 
    		"    <body>\n" + 
    		"      <p>Some text here.<table rows=\"4\" cols=\"2\">\n" + 
    		"          <head/>\n" + 
    		"          <row role=\"label\">\n" + 
    		"            <cell>Column1</cell>\n" + 
    		"            <cell>Column2</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell rows=\"2\">R1</cell>\n" + 
    		"            <cell>1</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell/>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>R2</cell>\n" + 
    		"            <cell>3</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>R3</cell>\n" + 
    		"            <cell>4</cell>\n" + 
    		"          </row>\n" + 
    		"        </table></p>\n" + 
    		"    </body>\n" + 
    		"  </text>\n" + 
    		"</TEI>";
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row contents", expectedResult, result);
    
    doUndo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<TEI\n" + 
    		"  xmlns:xi=\"http://www.w3.org/2001/XInclude\"\n" + 
    		"  xmlns:svg=\"http://www.w3.org/2000/svg\"\n" + 
    		"  xmlns:math=\"http://www.w3.org/1998/Math/MathML\"\n" + 
    		"  xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"  <teiHeader>\n" + 
    		"    <fileDesc>\n" + 
    		"      <titleStmt>\n" + 
    		"        <title>Title</title>\n" + 
    		"      </titleStmt>\n" + 
    		"      <publicationStmt>\n" + 
    		"        <p>Publication Information</p>\n" + 
    		"      </publicationStmt>\n" + 
    		"      <sourceDesc>\n" + 
    		"        <p>Information about the source</p>\n" + 
    		"      </sourceDesc>\n" + 
    		"    </fileDesc>\n" + 
    		"  </teiHeader>\n" + 
    		"  <text>\n" + 
    		"    <body>\n" + 
    		"      <p>Some text here.<table rows=\"4\" cols=\"2\">\n" + 
    		"          <head/>\n" + 
    		"          <row role=\"label\">\n" + 
    		"            <cell>Column1</cell>\n" + 
    		"            <cell>Column2</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell rows=\"2\">R1</cell>\n" + 
    		"            <cell>1</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>2</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>R2</cell>\n" + 
    		"            <cell>3</cell>\n" + 
    		"          </row>\n" + 
    		"          <row>\n" + 
    		"            <cell>R3</cell>\n" + 
    		"            <cell>4</cell>\n" + 
    		"          </row>\n" + 
    		"        </table></p>\n" + 
    		"    </body>\n" + 
    		"  </text>\n" + 
    		"</TEI>",
    		result);
    
    doRedo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult, result);
    
    // Delete entire empty row
        authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 28, caret.y + 30, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 28, caret.y + 30, 
        false, ViewportMouseEvent.STATE_CLICKED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    expectedResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<TEI\n" + 
        "  xmlns:xi=\"http://www.w3.org/2001/XInclude\"\n" + 
        "  xmlns:svg=\"http://www.w3.org/2000/svg\"\n" + 
        "  xmlns:math=\"http://www.w3.org/1998/Math/MathML\"\n" + 
        "  xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
        "  <teiHeader>\n" + 
        "    <fileDesc>\n" + 
        "      <titleStmt>\n" + 
        "        <title>Title</title>\n" + 
        "      </titleStmt>\n" + 
        "      <publicationStmt>\n" + 
        "        <p>Publication Information</p>\n" + 
        "      </publicationStmt>\n" + 
        "      <sourceDesc>\n" + 
        "        <p>Information about the source</p>\n" + 
        "      </sourceDesc>\n" + 
        "    </fileDesc>\n" + 
        "  </teiHeader>\n" + 
        "  <text>\n" + 
        "    <body>\n" + 
        "      <p>Some text here.<table rows=\"3\" cols=\"2\">\n" + 
        "                    <head/>\n" + 
        "                    <row role=\"label\">\n" + 
        "                        <cell>Column1</cell>\n" + 
        "                        <cell>Column2</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>R1</cell>\n" + 
        "                        <cell>1</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>R2</cell>\n" + 
        "                        <cell>3</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>R3</cell>\n" + 
        "                        <cell>4</cell>\n" + 
        "                    </row>\n" + 
        "                </table></p>\n" + 
        "    </body>\n" + 
        "  </text>\n" + 
        "</TEI>";
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted selected row ", expectedResult, result);
    
    doUndo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<TEI\n" + 
        "  xmlns:xi=\"http://www.w3.org/2001/XInclude\"\n" + 
        "  xmlns:svg=\"http://www.w3.org/2000/svg\"\n" + 
        "  xmlns:math=\"http://www.w3.org/1998/Math/MathML\"\n" + 
        "  xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
        "  <teiHeader>\n" + 
        "    <fileDesc>\n" + 
        "      <titleStmt>\n" + 
        "        <title>Title</title>\n" + 
        "      </titleStmt>\n" + 
        "      <publicationStmt>\n" + 
        "        <p>Publication Information</p>\n" + 
        "      </publicationStmt>\n" + 
        "      <sourceDesc>\n" + 
        "        <p>Information about the source</p>\n" + 
        "      </sourceDesc>\n" + 
        "    </fileDesc>\n" + 
        "  </teiHeader>\n" + 
        "  <text>\n" + 
        "    <body>\n" + 
        "      <p>Some text here.<table rows=\"4\" cols=\"2\">\n" + 
        "          <head/>\n" + 
        "          <row role=\"label\">\n" + 
        "            <cell>Column1</cell>\n" + 
        "            <cell>Column2</cell>\n" + 
        "          </row>\n" + 
        "          <row>\n" + 
        "            <cell rows=\"2\">R1</cell>\n" + 
        "            <cell>1</cell>\n" + 
        "          </row>\n" + 
        "          <row>\n" + 
        "            <cell/>\n" + 
        "          </row>\n" + 
        "          <row>\n" + 
        "            <cell>R2</cell>\n" + 
        "            <cell>3</cell>\n" + 
        "          </row>\n" + 
        "          <row>\n" + 
        "            <cell>R3</cell>\n" + 
        "            <cell>4</cell>\n" + 
        "          </row>\n" + 
        "        </table></p>\n" + 
        "    </body>\n" + 
        "  </text>\n" + 
        "</TEI>",
        result);
    
    doRedo();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals(expectedResult, result);
  }
  
  /**
   * <p><b>Description:</b> Test that a BadLocationException is not thrown
   * when deleting a table row</p>
   * <p><b>Bug ID:</b> EXM-31176</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testDeleteTableRowEXM_31176() throws Exception {
    final StringBuilder mess = new StringBuilder();
    OxygenAppender appender = new OxygenAppender(Level.ALL, true);

    //Catch all logger errors to fail.
    appender.addMessageListener(new MessageListener() {
      @Override
      public void appendMessage(String message, Level level) {
        System.err.println(message);
        mess.append(message);
      }
    });
    LogManager.getRootLogger().addAppender(appender);
    open(URLUtil.correct(new File("test/EXM-31176/test.dita")), true);

    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the before last row
    moveCaretRelativeTo("Snowdrop", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
        authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x - 10, caret.y + 10, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    deleteSynchronously(true);
    flushAWTBetter();
    Thread.sleep(200);
    
    String expectedResult1 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>Care and Preparation</title>\n" + 
        "    <body>\n" + 
        "        <p>When caring for your flower garden you want to feed your plants properly, control pests\n" + 
        "            and weeds. Good soil is a must to successful gardening, landscaping, and healthy\n" + 
        "            flowers. You have to\n" + 
        "            <indexterm>tasks<indexterm>preparation</indexterm></indexterm>balance the soil structure\n" + 
        "            with nutrients and regulate the pH to cover your plants'\n" + 
        "            needs.<?oxy_comment_start author=\"Mary\" timestamp=\"20120510T121702+0300\" comment=\"Let&apos;s add information about fertilizers.\"?><?oxy_comment_start author=\"John\" timestamp=\"20120510T121814+0300\" comment=\"We must include information about fertilizers impact to the environment.\" mid=\"1\"?>\n" + 
        "            And above all, remember that many flower gardens fail because they just don't get enough\n" + 
        "            of your attention.<?oxy_comment_end?><?oxy_comment_end mid=\"1\"?></p>\n" + 
        "        <p>\n" + 
        "            <table frame=\"none\">\n" + 
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
        "                            <entry>Chrysanthemum</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>well drained</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Gardenia</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>acidic</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Gerbera</entry>\n" + 
        "                            <entry>annual</entry>\n" + 
        "                            <entry>sandy, well-drained</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Iris</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>slightly acidic</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Lilac</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>alkaline</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>Salvia</entry>\n" + 
        "                            <entry>perennial</entry>\n" + 
        "                            <entry>average</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table>\n" + 
        "        </p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Content must be removed from the selected row", expectedResult1, result);
    
    String console = mess.toString();
    assertFalse("BadLocationException occured when trying to delete table content",
        console.contains("javax.swing.text.BadLocationException"));
  }
  
  
//  /**
//   * <p><b>Description:</b> Test delete row contents from an author table for Docbook5.</p>
//   * <p><b>Bug ID:</b> EXM-23621</p>
//   *
//   * @author radu_coravu
//   *
//   * @throws Exception
//   */
//  public void testDeleteTableRowDocbook5() throws Exception {
//    open(URLUtil.correct(new File("test/EXM-18945/testTableDocbook5.xml")));
//  
//    final AuthorEditorPage authorPage = 
//        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
//    
//    // Move the mouse over the first row
//    moveCaretRelativeTo("Column 1", 0);
//    Rectangle caret = authorPage.getViewport().getCaretShape();
//    int caretX = caret.x - 10;
//    if (PlatformDetector.isLinux()) {
//      caretX = caret.x - 15;
//    }
//    MouseEventData srcEvtData = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(caretX, caret.y + 10));
//    robotAWTHelper.enterClickAndLeave(srcEvtData);
//    
//    deleteSynchronously(true);
//    flushAWTBetter();
//    Thread.sleep(200);
//  
//    String expectedResult1 =
//        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
//        "<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
//        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
//        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
//        "    <info>\n" + 
//        "        <title>Article Template Title</title>\n" + 
//        "    </info>\n" + 
//        "    <sect1>\n" + 
//        "        <title>Section1 Title</title>\n" + 
//        "        <subtitle>Section1 Subtitle</subtitle>\n" + 
//        "        <para>Text<table frame=\"all\">\n" + 
//        "                <title/>\n" + 
//        "                <tgroup cols=\"2\">\n" + 
//        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
//        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
//        "                    <thead>\n" + 
//        "                        <row>\n" + 
//        "                            <entry/>\n" + 
//        "                            <entry/>\n" + 
//        "                        </row>\n" + 
//        "                    </thead>\n" + 
//        "                    <tbody>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>1</entry>\n" + 
//        "                            <entry>4</entry>\n" + 
//        "                        </row>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>2</entry>\n" + 
//        "                            <entry>5</entry>\n" + 
//        "                        </row>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>3</entry>\n" + 
//        "                            <entry>6</entry>\n" + 
//        "                        </row>\n" + 
//        "                    </tbody>\n" + 
//        "                </tgroup>\n" + 
//        "            </table></para>\n" + 
//        "    </sect1>\n" + 
//        "</article>";
//    String result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals("Should have deleted selected row contents", expectedResult1, result);
//    
//    moveCaretRelativeTo("2", 0);
//    caret = authorPage.getViewport().getCaretShape();
//    caretX = caret.x - 10;
//    if (PlatformDetector.isLinux()) {
//      caretX = caret.x - 15;
//    }
//    srcEvtData = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(caretX, caret.y + 10));
//    robotAWTHelper.enterClickAndLeave(srcEvtData);
//    
//    deleteSynchronously(true);
//    flushAWTBetter();
//    Thread.sleep(200);
//  
//    String expectedResult2 =
//        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
//        "<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
//        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
//        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
//        "    <info>\n" + 
//        "        <title>Article Template Title</title>\n" + 
//        "    </info>\n" + 
//        "    <sect1>\n" + 
//        "        <title>Section1 Title</title>\n" + 
//        "        <subtitle>Section1 Subtitle</subtitle>\n" + 
//        "        <para>Text<table frame=\"all\">\n" + 
//        "                <title/>\n" + 
//        "                <tgroup cols=\"2\">\n" + 
//        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
//        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
//        "                    <thead>\n" + 
//        "                        <row>\n" + 
//        "                            <entry/>\n" + 
//        "                            <entry/>\n" + 
//        "                        </row>\n" + 
//        "                    </thead>\n" + 
//        "                    <tbody>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>1</entry>\n" + 
//        "                            <entry>4</entry>\n" + 
//        "                        </row>\n" + 
//        "                        <row>\n" + 
//        "                            <entry/>\n" + 
//        "                            <entry/>\n" + 
//        "                        </row>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>3</entry>\n" + 
//        "                            <entry>6</entry>\n" + 
//        "                        </row>\n" + 
//        "                    </tbody>\n" + 
//        "                </tgroup>\n" + 
//        "            </table></para>\n" + 
//        "    </sect1>\n" + 
//        "</article>";
//    result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals("Should have deleted selected row", expectedResult2, result);
//    
//    doUndo();
//    result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals(expectedResult1, result);
//    
//    doRedo();
//    result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals(expectedResult2, result);
//    
//    // Delete entire empty row
//    caretX = caret.x - 10;
//    if (PlatformDetector.isLinux()) {
//      caretX = caret.x - 15;
//    }
//    srcEvtData = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(caretX, caret.y + 10));
//    robotAWTHelper.enterClickAndLeave(srcEvtData);
//    
//    deleteSynchronously(true);
//    flushAWTBetter();
//    Thread.sleep(200);
//  
//    String expectedResult3 =
//        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
//        "<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
//        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
//        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
//        "    <info>\n" + 
//        "        <title>Article Template Title</title>\n" + 
//        "    </info>\n" + 
//        "    <sect1>\n" + 
//        "        <title>Section1 Title</title>\n" + 
//        "        <subtitle>Section1 Subtitle</subtitle>\n" + 
//        "        <para>Text<table frame=\"all\">\n" + 
//        "                <title/>\n" + 
//        "                <tgroup cols=\"2\">\n" + 
//        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
//        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
//        "                    <thead>\n" + 
//        "                        <row>\n" + 
//        "                            <entry/>\n" + 
//        "                            <entry/>\n" + 
//        "                        </row>\n" + 
//        "                    </thead>\n" + 
//        "                    <tbody>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>1</entry>\n" + 
//        "                            <entry>4</entry>\n" + 
//        "                        </row>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>3</entry>\n" + 
//        "                            <entry>6</entry>\n" + 
//        "                        </row>\n" + 
//        "                    </tbody>\n" + 
//        "                </tgroup>\n" + 
//        "            </table></para>\n" + 
//        "    </sect1>\n" + 
//        "</article>";
//    result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals("Should have deleted selected row contents", expectedResult3, result);
//    
//    doUndo();
//    result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals(expectedResult2, result);
//    
//    doRedo();
//    result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals(expectedResult3, result);
//  }
}