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

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;

import junit.extensions.jfcunit.RobotTestHelper;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.keyboard.JFCKeyStroke;

import org.apache.log4j.LogManager;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;

import ro.sync.ecss.component.ui.actions.edit.CutOrCopyAction;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.ui.UiUtil;
import ro.sync.util.URLUtil;

/**
 * Tests for deleting columns for DITA, Docbook, TEi and XHTML frameworks.
 * 
 * @author costi
 */
public class DeleteTableColumnTest extends EditorAuthorExtensionTestBase {

  private static class MyRobotTestHelper extends RobotTestHelper {
    /**
     * @throws AWTException
     */
    public MyRobotTestHelper() throws AWTException {
      super();
    }

    @Override
    public void keyPressed(Component ultimate, JFCKeyStroke stroke) {
      super.keyPressed(ultimate, stroke);
    }
    @Override
    public void keyReleased(Component ultimate, JFCKeyStroke stroke) {
      super.keyReleased(ultimate, stroke);
    }
  }
  
  /**
   * Robot helper.
   */
  private MyRobotTestHelper robotAWTHelper;
  
  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    try {
      robotAWTHelper = new MyRobotTestHelper();
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for DITA.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteTableColumnDITA() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTable.dita")));

    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
    
    //Cut column.
    final CutOrCopyAction cut = new CutOrCopyAction(vViewport, true);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        cut.actionPerformed(null);    
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"table_vp2_1hb_ff\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c2\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry>4</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>5</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>6</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table> for the current task</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", result);
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for Docbook4.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteTableColumnDocbook4() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableDocbook.xml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
    
    //Cut column.
    final CutOrCopyAction cut = new CutOrCopyAction(vViewport, true);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        cut.actionPerformed(null);    
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
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
        "                        <th>Column 2</th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>4</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>5</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>6</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>", result);
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for XHTML.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeletePasteTableColumnXHTML() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableXHTML.xhtml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);

    //Cut column.
    final CutOrCopyAction cut = new CutOrCopyAction(vViewport, true);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        cut.actionPerformed(null);    
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
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
        "                    <th>Column 2</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>4</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>5</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>6</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>", result);
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for TEI.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteTableColumnTEI() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableTEI.xml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
    
    //Cut column.
    final CutOrCopyAction cut = new CutOrCopyAction(vViewport, true);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        cut.actionPerformed(null);    
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
  
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/teilite.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
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
        "      <p>Some text here.<table rows=\"3\" cols=\"1\">\n" + 
        "                    <head/>\n" + 
        "                    <row role=\"label\">\n" + 
        "                        <cell>Column 2</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>4</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>5</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>6</cell>\n" + 
        "                    </row>\n" + 
        "                </table></p>\n" + 
        "    </body>\n" + 
        "  </text>\n" + 
        "</TEI>", result);
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for Docbook5.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteTableColumnDocbook5() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableDocbook5.xml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
    
    //Cut column.
    final CutOrCopyAction cut = new CutOrCopyAction(vViewport, true);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        cut.actionPerformed(null);    
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
  
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title>Article Template Title</title>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <subtitle>Section1 Subtitle</subtitle>\n" + 
        "        <para>Text<table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"1\">\n" + 
        "                    <colspec colname=\"c2\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Column 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>5</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>6</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>", result);
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for DITA.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteTableColumn2DITA() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTable.dita")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
    
    invokeAction(ACTION_NAME_DELETE_COLUMN);
    flushAWTBetter();
    Thread.sleep(200);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"table_vp2_1hb_ff\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c2\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry>4</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>5</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>6</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table> for the current task</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", result);
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for Docbook4.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteTableColumn2Docbook4() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableDocbook.xml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
    
    invokeAction(ACTION_NAME_DELETE_COLUMN);
    flushAWTBetter();
    Thread.sleep(200);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
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
        "                        <th>Column 2</th>\n" + 
        "                    </tr>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>4</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>5</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td>6</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>", result);
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for XHTML.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeletePasteTableColumn2XHTML() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableXHTML.xhtml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
  
    invokeAction(ACTION_NAME_DELETE_COLUMN);
    flushAWTBetter();
    Thread.sleep(200);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
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
        "                    <th>Column 2</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>4</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>5</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>6</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>", result);
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for TEI.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteTableColumn2TEI() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableTEI.xml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
    
    invokeAction(ACTION_NAME_DELETE_COLUMN);
    flushAWTBetter();
    Thread.sleep(200);
  
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/teilite.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
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
        "      <p>Some text here.<table rows=\"3\" cols=\"1\">\n" + 
        "                    <head/>\n" + 
        "                    <row role=\"label\">\n" + 
        "                        <cell>Column 2</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>4</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>5</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>6</cell>\n" + 
        "                    </row>\n" + 
        "                </table></p>\n" + 
        "    </body>\n" + 
        "  </text>\n" + 
        "</TEI>", result);
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from an author table for Docbook5.</p>
   * <p><b>Bug ID:</b> EXM-23305</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteTableColumn2Docbook5() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/testTableDocbook5.xml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
    
    invokeAction(ACTION_NAME_DELETE_COLUMN);
    flushAWTBetter();
    Thread.sleep(200);
  
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have deleted the entire selected column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
        "    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
        "    <info>\n" + 
        "        <title>Article Template Title</title>\n" + 
        "    </info>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <subtitle>Section1 Subtitle</subtitle>\n" + 
        "        <para>Text<table frame=\"all\">\n" + 
        "                <title/>\n" + 
        "                <tgroup cols=\"1\">\n" + 
        "                    <colspec colname=\"c2\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Column 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>5</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>6</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>", result);
  }

  /**
   * <p><b>Description:</b> MultipleDelete must clear the edits list after UNDO, 
   * otherwise a javax.swing.undo.CannotUndoException exception is thrown after
   * UNDO/REDO/UNDO sequence .</p>
   * <p><b>Bug ID:</b> EXM-23403</p>
   *
   * @author costi
   * @throws Exception 
   */
  public void testDeleteUndoRedoTableColumnXHTML() throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    WriterAppender appender = new WriterAppender(new SimpleLayout(), bos);
    try {
      LogManager.getRootLogger().addAppender(appender);
      open(URLUtil.correct(new File("test/EXM-18945/testTableXHTML.xhtml")));

      final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();

      // Move the mouse over the first column
      moveCaretRelativeTo("Column 1", 0);
      Rectangle caret = authorPage.getViewport().getCaretShape();
      MouseEventData srcEvtData = new MouseEventData(
          this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
          1000, new Point(caret.x + 30, caret.y - 10));
      robotAWTHelper.enterClickAndLeave(srcEvtData);

      //Cut column.
      final CutOrCopyAction cut = new CutOrCopyAction(vViewport, true);
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          cut.actionPerformed(null);    
        }
      });
      flushAWTBetter();
      Thread.sleep(200);

      String result = serializeDocumentViewport(authorPage.getViewport(), true);
      assertEquals("Should have deleted the entire selected column", 
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
          "                    <th>Column 2</th>\n" + 
          "                </tr>\n" + 
          "            </thead>\n" + 
          "            <tbody>\n" + 
          "                <tr>\n" + 
          "                    <td>4</td>\n" + 
          "                </tr>\n" + 
          "                <tr>\n" + 
          "                    <td>5</td>\n" + 
          "                </tr>\n" + 
          "                <tr>\n" + 
          "                    <td>6</td>\n" + 
          "                </tr>\n" + 
          "            </tbody>\n" + 
          "        </table>\n" + 
          "    </body>\n" + 
          "</html>", result);

      String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
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
      "                    <th>Column 1</th>\n" + 
      "                    <th>Column 2</th>\n" + 
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

      doUndo();
      result = serializeDocumentViewport(authorPage.getViewport(), true);
      assertEquals(expResult, result);

      doRedo();
      doUndo();

      result = serializeDocumentViewport(authorPage.getViewport(), true);
      assertEquals(expResult, result);
      
      String contents = bos.toString();
      assertTrue("Should be no CannotUndoException in the console...", 
          contents.indexOf("javax.swing.undo.CannotUndoException") == -1);
    } finally {
      LogManager.getRootLogger().removeAppender(appender);
    }
  }

  /**
   * <p><b>Description:</b> Test delete an entire column from choice table for DITA.</p>
   * <p><b>Bug ID:</b> EXM-24849</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteChoiceTableColumnDITA() throws Exception {
    open(URLUtil.correct(new File("test/EXM-24849/testTask2.dita")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("11", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    MouseEventData srcEvtData = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(caret.x + 30, caret.y - 10));
    robotAWTHelper.enterClickAndLeave(srcEvtData);
    
    //Cut column.
    final CutOrCopyAction cut = new CutOrCopyAction(vViewport, true);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        cut.actionPerformed(null);    
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Shouldn't have deleted the entire selected column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_mg2_ktg_1h\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context for the current task</p>\n" + 
        "        </context>\n" + 
        "        <steps>\n" + 
        "            <step>\n" + 
        "                <cmd>Task step.</cmd>\n" + 
        "                <choicetable frame=\"none\" relcolwidth=\"1.0* 1.0*\" id=\"choicetable_tb4_1vg_1h\">\n" + 
        "                    <chhead>\n" + 
        "                        <choptionhd>11</choptionhd>\n" + 
        "                        <chdeschd>22</chdeschd>\n" + 
        "                    </chhead>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>33</choption>\n" + 
        "                        <chdesc>44</chdesc>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>55</choption>\n" + 
        "                        <chdesc>66</chdesc>\n" + 
        "                    </chrow>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>77</choption>\n" + 
        "                        <chdesc>88</chdesc>\n" + 
        "                    </chrow>\n" + 
        "                </choicetable>\n" + 
        "            </step>\n" + 
        "        </steps>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "" + 
        "", result);
  }
}
