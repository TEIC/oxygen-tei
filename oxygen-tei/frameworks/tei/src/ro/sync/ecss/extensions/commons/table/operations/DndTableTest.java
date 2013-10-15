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
import java.io.File;
import java.util.List;

import junit.extensions.jfcunit.RobotTestHelper;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.keyboard.JFCKeyStroke;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.options.OptionTags;
import ro.sync.exml.options.Options;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.util.URLUtil;

/**
 * Test for drag and drop table columns.
 * 
 * @author costi
 */
public class DndTableTest extends EditorAuthorExtensionTestBase {

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
  private boolean showCaretPosInfo;
  private boolean showAnnotTooltio;

  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    showCaretPosInfo = Options.getInstance().getBooleanProperty(OptionTags.SHOW_CARET_POSITION_INFO);
    showAnnotTooltio = Options.getInstance().getBooleanProperty(OptionTags.CODE_INSIGHT_SHOW_ANNOTATIONS_TOOLTIP);
    
    Options.getInstance().setBooleanProperty(OptionTags.SHOW_CARET_POSITION_INFO, false);
    Options.getInstance().setBooleanProperty(OptionTags.CODE_INSIGHT_SHOW_ANNOTATIONS_TOOLTIP, false);
    
    try {
      robotAWTHelper = new MyRobotTestHelper();
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    robotAWTHelper = null;
    editorManager.closeAllWithoutSaveFromTCs();
    
    Options.getInstance().setBooleanProperty(
        OptionTags.SHOW_CARET_POSITION_INFO, showCaretPosInfo);
    Options.getInstance().setBooleanProperty(
        OptionTags.CODE_INSIGHT_SHOW_ANNOTATIONS_TOOLTIP, showAnnotTooltio);
  }

  /**
   * <p><b>Description:</b> Test drag and drop an entire column from an author table.</p>
   * <p><b>Bug ID:</b> EXM-18945</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDndTableColumnDITA() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTable.dita")));

    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();

    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    flushAWTBetter();
    Rectangle caret = authorPage.getViewport().getCaretShape();
    int fromX = caret.getX() + 10;
    int fromY = caret.getY()- 10;
    
    // To
    moveCaretRelativeTo("Column 2", "Column 2".length());
    flushAWTBetter();
    caret = authorPage.getViewport().getCaretShape();
    int toX = caret.getX() + 20;
    int toY = caret.getY() + 50;
    
    MouseEventData fromEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(fromX, fromY));
    robotAWTHelper.enterClickAndLeave(fromEvent);
    flushAWTBetter();

    MouseEventData toEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(toX, toY));
    
    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
    flushAWTBetter();
    sleep(200);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should perform a Dnd of the entire column", 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"table_vp2_1hb_ff\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"2\">\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                                <entry>Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry>4</entry>\n" + 
        "                                <entry>1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>5</entry>\n" + 
        "                                <entry>2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>6</entry>\n" + 
        "                                <entry>3</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table> for the current task</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", result);
  }

//  /**
//   * <p><b>Description:</b> Test drag and drop an entire column from an author table.</p>
//   * <p><b>Bug ID:</b> EXM-18945</p>
//   *
//   * @author costi
//   *
//   * @throws Exception
//   */
//  public void testDndTableColumnDocbook4() throws Exception {
//    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableDocbook.xml")));
//  
//    final AuthorEditorPage authorPage = 
//        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
//  
//    // Move the mouse over the first column
//    moveCaretRelativeTo("Column 1", 0);
//    flushAWTBetter();
//    Rectangle caret = authorPage.getViewport().getCaretShape();
//    int fromX = caret.getX() + 10;
//    int fromY = caret.getY()- 10;
//    
//    // To
//    moveCaretRelativeTo("Column 2", "Column 2".length());
//    flushAWTBetter();
//    caret = authorPage.getViewport().getCaretShape();
//    int toX = caret.getX();
//    int toY = caret.getY();
//    
//    MouseEventData fromEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(fromX, fromY));
//    robotAWTHelper.enterClickAndLeave(fromEvent);
//    flushAWTBetter();
//
//    MouseEventData toEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(toX, toY));
//    
//    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
//    flushAWTBetter();
//    sleep(200);
//    String result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals("Should perform a Dnd of the entire column", 
//        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
//        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
//        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
//        "<article>\n" + 
//        "    <title>Article Title</title>\n" + 
//        "    <sect1>\n" + 
//        "        <title>Section1 Title</title>\n" + 
//        "        <para>Text<table frame=\"void\">\n" + 
//        "                <caption/>\n" + 
//        "                <thead>\n" + 
//        "                    <tr>\n" + 
//        "                        <th>Column 2</th>\n" + 
//        "                        <th>Column 1</th>\n" + 
//        "                    </tr>\n" + 
//        "                </thead>\n" + 
//        "                <tbody>\n" + 
//        "                    <tr>\n" + 
//        "                        <td>4</td>\n" + 
//        "                        <td>1</td>\n" + 
//        "                    </tr>\n" + 
//        "                    <tr>\n" + 
//        "                        <td>5</td>\n" + 
//        "                        <td>2</td>\n" + 
//        "                    </tr>\n" + 
//        "                    <tr>\n" + 
//        "                        <td>6</td>\n" + 
//        "                        <td>3</td>\n" + 
//        "                    </tr>\n" + 
//        "                </tbody>\n" + 
//        "            </table></para>\n" + 
//        "    </sect1>\n" + 
//        "</article>", result);
//  }

//  /**
//   * <p><b>Description:</b> Test drag and drop an entire column from an author table.</p>
//   * <p><b>Bug ID:</b> EXM-18945</p>
//   *
//   * @author costi
//   *
//   * @throws Exception
//   */
//  public void testDndTableColumnDocbook5() throws Exception {
//    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableDocbook5.xml")));
//  
//    final AuthorEditorPage authorPage = 
//        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
//  
//    // Move the mouse over the first column
//    moveCaretRelativeTo("Column 1", 0);
//    flushAWTBetter();
//    Rectangle caret = authorPage.getViewport().getCaretShape();
//    int fromX = caret.getX() + 10;
//    int fromY = caret.getY()- 10;
//    
//    // To
//    moveCaretRelativeTo("Column 2", "Column 2".length());
//    flushAWTBetter();
//    caret = authorPage.getViewport().getCaretShape();
//    int toX = caret.getX();
//    int toY = caret.getY();
//    
//    MouseEventData fromEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(fromX, fromY));
//    robotAWTHelper.enterClickAndLeave(fromEvent);
//    flushAWTBetter();
//
//    MouseEventData toEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(toX, toY));
//    
//    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
//    flushAWTBetter();
//    sleep(200);
//    String result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals("Should perform a Dnd of the entire column", 
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
//        "                    <thead>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>Column 2</entry>\n" + 
//        "                            <entry>Column 1</entry>\n" + 
//        "                        </row>\n" + 
//        "                    </thead>\n" + 
//        "                    <tbody>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>4</entry>\n" + 
//        "                            <entry>1</entry>\n" + 
//        "                        </row>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>5</entry>\n" + 
//        "                            <entry>2</entry>\n" + 
//        "                        </row>\n" + 
//        "                        <row>\n" + 
//        "                            <entry>6</entry>\n" + 
//        "                            <entry>3</entry>\n" + 
//        "                        </row>\n" + 
//        "                    </tbody>\n" + 
//        "                </tgroup>\n" + 
//        "            </table></para>\n" + 
//        "    </sect1>\n" + 
//        "</article>", result);
//  }

  /**
   * <p><b>Description:</b> Test drag and drop an entire column from an author table.</p>
   * <p><b>Bug ID:</b> EXM-18945</p>
   *
   * @author costi
   *
   * @throws Exception
   */
//  public void testDndTableColumnTEI() throws Exception {
//    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableTEI.xml")));
//  
//    final AuthorEditorPage authorPage = 
//        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
//  
//    // Move the mouse over the first column
//    moveCaretRelativeTo("Column 1", 0);
//    flushAWTBetter();
//    Rectangle caret = authorPage.getViewport().getCaretShape();
//    int fromX = caret.getX() + 10;
//    int fromY = caret.getY()- 10;
//    
//    // To
//    moveCaretRelativeTo("Column 2", "Column 2".length());
//    flushAWTBetter();
//    caret = authorPage.getViewport().getCaretShape();
//    int toX = caret.getX();
//    int toY = caret.getY();
//    
//    MouseEventData fromEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(fromX, fromY));
//    robotAWTHelper.enterClickAndLeave(fromEvent);
//    flushAWTBetter();
//
//    MouseEventData toEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(toX, toY));
//    
//    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
//    flushAWTBetter();
//    sleep(200);
//  
//    String result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals("Should perform a Dnd of the entire column", 
//        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
//        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/teilite.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
//        "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
//        "  <teiHeader>\n" + 
//        "    <fileDesc>\n" + 
//        "      <titleStmt>\n" + 
//        "        <title>Title</title>\n" + 
//        "      </titleStmt>\n" + 
//        "      <publicationStmt>\n" + 
//        "        <p>Publication information</p>        \n" + 
//        "      </publicationStmt>\n" + 
//        "      <sourceDesc>\n" + 
//        "        <p>Information about the source</p>\n" + 
//        "      </sourceDesc>\n" + 
//        "    </fileDesc>\n" + 
//        "  </teiHeader>\n" + 
//        "  <text>\n" + 
//        "    <body>\n" + 
//        "      <p>Some text here.<table rows=\"3\" cols=\"2\">\n" + 
//        "                    <head/>\n" + 
//        "                    <row role=\"label\">\n" + 
//        "                        <cell>Column 2</cell>\n" + 
//        "                        <cell>Column 1</cell>\n" + 
//        "                    </row>\n" + 
//        "                    <row>\n" + 
//        "                        <cell>4</cell>\n" + 
//        "                        <cell>1</cell>\n" + 
//        "                    </row>\n" + 
//        "                    <row>\n" + 
//        "                        <cell>5</cell>\n" + 
//        "                        <cell>2</cell>\n" + 
//        "                    </row>\n" + 
//        "                    <row>\n" + 
//        "                        <cell>6</cell>\n" + 
//        "                        <cell>3</cell>\n" + 
//        "                    </row>\n" + 
//        "                </table></p>\n" + 
//        "    </body>\n" + 
//        "  </text>\n" + 
//        "</TEI>", result);
//  }

  /**
   * <p><b>Description:</b> Test drag and drop an entire column from an author table.</p>
   * <p><b>Bug ID:</b> EXM-18945</p>
   *
   * @author costi
   *
   * @throws Exception
   */
//  public void testDndTableColumnXHTML() throws Exception {
//    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableXHTML.xhtml")));
//  
//    final AuthorEditorPage authorPage = 
//        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
//  
//    // Move the mouse over the first column
//    moveCaretRelativeTo("Column 1", 0);
//    flushAWTBetter();
//    Rectangle caret = authorPage.getViewport().getCaretShape();
//    int fromX = caret.getX() + 10;
//    int fromY = caret.getY()- 10;
//    
//    // To
//    moveCaretRelativeTo("Column 2", "Column 2".length());
//    flushAWTBetter();
//    caret = authorPage.getViewport().getCaretShape();
//    int toX = caret.getX();
//    int toY = caret.getY();
//    
//    MouseEventData fromEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(fromX, fromY));
//    robotAWTHelper.enterClickAndLeave(fromEvent);
//    flushAWTBetter();
//
//    MouseEventData toEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(toX, toY));
//    
//    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
//    flushAWTBetter();
//    sleep(200);
//  
//    String result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals("Should perform a Dnd of the entire column", 
//        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
//        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
//        "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
//        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
//        "    <head>\n" + 
//        "        <title></title>\n" + 
//        "    </head>\n" + 
//        "    <body>\n" + 
//        "        <p>Text</p>\n" + 
//        "        <table frame=\"void\">\n" + 
//        "            <caption></caption>\n" + 
//        "            <thead>\n" + 
//        "                <tr>\n" + 
//        "                    <th>Column 2</th>\n" + 
//        "                    <th>Column 1</th>\n" + 
//        "                </tr>\n" + 
//        "            </thead>\n" + 
//        "            <tbody>\n" + 
//        "                <tr>\n" + 
//        "                    <td>4</td>\n" + 
//        "                    <td>1</td>\n" + 
//        "                </tr>\n" + 
//        "                <tr>\n" + 
//        "                    <td>5</td>\n" + 
//        "                    <td>2</td>\n" + 
//        "                </tr>\n" + 
//        "                <tr>\n" + 
//        "                    <td>6</td>\n" + 
//        "                    <td>3</td>\n" + 
//        "                </tr>\n" + 
//        "            </tbody>\n" + 
//        "        </table>\n" + 
//        "    </body>\n" + 
//        "</html>", result);
//  }

  /**
   * <p><b>Description:</b> Test drag and drop an entire column from an author table.</p>
   * <p><b>Bug ID:</b> EXM-18945</p>
   *
   * @author costi
   *
   * @throws Exception
   */
//  public void testDnd2TableColumnDITA() throws Exception {
//    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTable.dita")));
//  
//    final AuthorEditorPage authorPage = 
//        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
//  
//    // Move the mouse over the first column
//    moveCaretRelativeTo("Column 1", 0);
//    flushAWTBetter();
//    Rectangle caret = authorPage.getViewport().getCaretShape();
//    int fromX = caret.getX() + 10;
//    int fromY = caret.getY()- 10;
//    
//    // To
//    moveCaretRelativeTo("Column 2", "Column 2".length());
//    flushAWTBetter();
//    caret = authorPage.getViewport().getCaretShape();
//    int toX = caret.getX();
//    int toY = caret.getY();
//    
//    MouseEventData fromEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(fromX, fromY));
//    robotAWTHelper.enterClickAndLeave(fromEvent);
//    flushAWTBetter();
//
//    MouseEventData toEvent = new MouseEventData(
//        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
//        1000, new Point(toX, toY));
//    
//    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
//    flushAWTBetter();
//    Thread.sleep(200);
//    
//    String result = serializeDocumentViewport(authorPage.getViewport(), true);
//    assertEquals("Should perform a Dnd of the entire column", 
//        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
//        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
//        "<task id=\"task_kwd_zgb_ff\">\n" + 
//        "    <title>Task title</title>\n" + 
//        "    <shortdesc/>\n" + 
//        "    <taskbody>\n" + 
//        "        <context>\n" + 
//        "            <p>Context<table frame=\"all\" id=\"table_vp2_1hb_ff\">\n" + 
//        "                    <title/>\n" + 
//        "                    <tgroup cols=\"2\">\n" + 
//        "                        <thead>\n" + 
//        "                            <row>\n" + 
//        "                                <entry>Column 2</entry>\n" + 
//        "                                <entry>Column 1</entry>\n" + 
//        "                            </row>\n" + 
//        "                        </thead>\n" + 
//        "                        <tbody>\n" + 
//        "                            <row>\n" + 
//        "                                <entry>4</entry>\n" + 
//        "                                <entry>1</entry>\n" + 
//        "                            </row>\n" + 
//        "                            <row>\n" + 
//        "                                <entry>5</entry>\n" + 
//        "                                <entry>2</entry>\n" + 
//        "                            </row>\n" + 
//        "                            <row>\n" + 
//        "                                <entry>6</entry>\n" + 
//        "                                <entry>3</entry>\n" + 
//        "                            </row>\n" + 
//        "                        </tbody>\n" + 
//        "                    </tgroup>\n" + 
//        "                </table> for the current task</p>\n" + 
//        "        </context>\n" + 
//        "    </taskbody>\n" + 
//        "</task>\n" + 
//        "", result);
//  }
  
  /**
   * <p><b>Description:</b> Test preserve selection for drag and drop an entire column from an author table.</p>
   * <p><b>Bug ID:</b> EXM-23360</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDndTableColumnPreserveSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableDocbook5.xml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
  
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    flushAWTBetter();
    Rectangle caret = authorPage.getViewport().getCaretShape();
    int fromX = caret.getX() + 10;
    int fromY = caret.getY()- 10;
    
    // To
    moveCaretRelativeTo("Column 2", "Column 2".length());
    flushAWTBetter();
    caret = authorPage.getViewport().getCaretShape();
    int toX = caret.getX();
    int toY = caret.getY();
    
    MouseEventData fromEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(fromX, fromY));
    robotAWTHelper.enterClickAndLeave(fromEvent);
    flushAWTBetter();

    MouseEventData toEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(toX, toY));
    
    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
    flushAWTBetter();
    sleep(200);
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should perform a Dnd of the entire column", 
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
        "                <tgroup cols=\"2\">\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Column 2</entry>\n" + 
        "                            <entry>Column 1</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>4</entry>\n" + 
        "                            <entry>1</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>5</entry>\n" + 
        "                            <entry>2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>6</entry>\n" + 
        "                            <entry>3</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>", result);
    
    List<int[]> selectionIntervals = vViewport.getSelectionModel().getSelectionIntervals();
    assertEquals(4, selectionIntervals.size());
    
    int[] interval = selectionIntervals.get(0);
    assertEquals(212, interval[0]);
    assertEquals(222, interval[1]);
    
    interval = selectionIntervals.get(1);
    assertEquals(229, interval[0]);
    assertEquals(232, interval[1]);
    
    interval = selectionIntervals.get(2);
    assertEquals(237, interval[0]);
    assertEquals(240, interval[1]);
    
    interval = selectionIntervals.get(3);
    assertEquals(245, interval[0]);
    assertEquals(248, interval[1]);
    
    doUndo();
    flushAWTBetter();
    
    selectionIntervals = vViewport.getSelectionModel().getSelectionIntervals();
    assertEquals(4, selectionIntervals.size());
    
    interval = selectionIntervals.get(0);
    assertEquals(202, interval[0]);
    assertEquals(212, interval[1]);
    
    interval = selectionIntervals.get(1);
    assertEquals(226, interval[0]);
    assertEquals(229, interval[1]);
    
    interval = selectionIntervals.get(2);
    assertEquals(234, interval[0]);
    assertEquals(237, interval[1]);
    
    interval = selectionIntervals.get(3);
    assertEquals(242, interval[0]);
    assertEquals(245, interval[1]);
    
    doRedo();
    flushAWTBetter();
    
    selectionIntervals = vViewport.getSelectionModel().getSelectionIntervals();
    assertEquals(4, selectionIntervals.size());
    
    interval = selectionIntervals.get(0);
    assertEquals(212, interval[0]);
    assertEquals(222, interval[1]);
    
    interval = selectionIntervals.get(1);
    assertEquals(229, interval[0]);
    assertEquals(232, interval[1]);
    
    interval = selectionIntervals.get(2);
    assertEquals(237, interval[0]);
    assertEquals(240, interval[1]);
    
    interval = selectionIntervals.get(3);
    assertEquals(245, interval[0]);
    assertEquals(248, interval[1]);
  }

  /**
   * <p><b>Description:</b> Test namespaces after undo redo a column insertion.</p>
   * <p><b>Bug ID:</b> EXM-23508</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testNamespacesAfterUndoRedo() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableDocbook5.xml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    flushAWTBetter();
    Rectangle caret = authorPage.getViewport().getCaretShape();
    int fromX = caret.getX() + 10;
    int fromY = caret.getY()- 10;
    
    // To
    moveCaretRelativeTo("Column 2", "Column 2".length());
    flushAWTBetter();
    caret = authorPage.getViewport().getCaretShape();
    int toX = caret.getX();
    int toY = caret.getY();
    
    MouseEventData fromEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(fromX, fromY));
    robotAWTHelper.enterClickAndLeave(fromEvent);
    flushAWTBetter();

    MouseEventData toEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(toX, toY));
    
    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
    flushAWTBetter();
    sleep(500);
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
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
    "                <tgroup cols=\"2\">\n" + 
    "                    <thead>\n" + 
    "                        <row>\n" + 
    "                            <entry>Column 2</entry>\n" + 
    "                            <entry>Column 1</entry>\n" + 
    "                        </row>\n" + 
    "                    </thead>\n" + 
    "                    <tbody>\n" + 
    "                        <row>\n" + 
    "                            <entry>4</entry>\n" + 
    "                            <entry>1</entry>\n" + 
    "                        </row>\n" + 
    "                        <row>\n" + 
    "                            <entry>5</entry>\n" + 
    "                            <entry>2</entry>\n" + 
    "                        </row>\n" + 
    "                        <row>\n" + 
    "                            <entry>6</entry>\n" + 
    "                            <entry>3</entry>\n" + 
    "                        </row>\n" + 
    "                    </tbody>\n" + 
    "                </tgroup>\n" + 
    "            </table></para>\n" + 
    "    </sect1>\n" + 
    "</article>";
    assertEquals("Should perform a Dnd of the entire column", 
        expectedResult, result);
    
    doUndo();
    flushAWTBetter();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should perform a Dnd of the entire column", 
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
        "                <tgroup cols=\"2\">\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Column 1</entry>\n" + 
        "                            <entry>Column 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>1</entry>\n" + 
        "                            <entry>4</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>2</entry>\n" + 
        "                            <entry>5</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>3</entry>\n" + 
        "                            <entry>6</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>", result);
    
    doRedo();
    flushAWTBetter();
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should perform a Dnd of the entire column", 
        expectedResult, result);
  }
}
