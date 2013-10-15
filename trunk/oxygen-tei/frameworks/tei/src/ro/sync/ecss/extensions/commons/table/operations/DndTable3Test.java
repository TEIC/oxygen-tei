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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import junit.extensions.jfcunit.RobotTestHelper;
import junit.extensions.jfcunit.TestHelper;
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
public class DndTable3Test extends EditorAuthorExtensionTestBase {

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
  public void testCopyDndTableColumnDocbook5() throws Exception {
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
    moveCaretRelativeTo("Column 2", "Column ".length());
    flushAWTBetter();
    caret = authorPage.getViewport().getCaretShape();
    int toX = caret.getX();
    int toY = caret.getY() + 20;
    
    MouseEventData fromEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(fromX, fromY));
    robotAWTHelper.enterClickAndLeave(fromEvent);
    flushAWTBetter();

    MouseEventData toEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(toX, toY));
    
    int mods = InputEvent.CTRL_MASK;
    JFCKeyStroke[] strokes = TestHelper.getKeyMapping().getKeyStrokes(KeyEvent.VK_CONTROL);
    strokes[0].setModifiers(mods);
    robotAWTHelper.keyPressed(authorPage.getSwingComponent(), strokes[0]);
    flushAWTBetter();
    sleep(200);
    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
    flushAWTBetter();
    sleep(200);
    robotAWTHelper.keyReleased(authorPage.getSwingComponent(), strokes[0]);
    flushAWTBetter();
    sleep(200);
  
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should perform a copy Dnd of the entire column", 
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
        "                <tgroup cols=\"3\">\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Column 1</entry>\n" + 
        "                            <entry>Column 2</entry>\n" + 
        "                            <entry>Column 1</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry>1</entry>\n" + 
        "                            <entry>4</entry>\n" + 
        "                            <entry>1</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>2</entry>\n" + 
        "                            <entry>5</entry>\n" + 
        "                            <entry>2</entry>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry>3</entry>\n" + 
        "                            <entry>6</entry>\n" + 
        "                            <entry>3</entry>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "            </table></para>\n" + 
        "    </sect1>\n" + 
        "</article>", result);
  }

  /**
   * <p><b>Description:</b> Test drag and drop an entire column from an author table.</p>
   * <p><b>Bug ID:</b> EXM-18945</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testCopyDndTableColumnTEI() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableTEI.xml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
  
 // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    flushAWTBetter();
    Rectangle caret = authorPage.getViewport().getCaretShape();
    int fromX = caret.getX() + 10;
    int fromY = caret.getY()- 10;
    
    // To
    moveCaretRelativeTo("Column 2", "Column ".length());
    flushAWTBetter();
    caret = authorPage.getViewport().getCaretShape();
    int toX = caret.getX();
    int toY = caret.getY() + 50;
    
    MouseEventData fromEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(fromX, fromY));
    robotAWTHelper.enterClickAndLeave(fromEvent);
    flushAWTBetter();

    MouseEventData toEvent = new MouseEventData(
        this, authorPage.getSwingComponent(), 1, MouseEvent.BUTTON1_MASK, false, 
        1000, new Point(toX, toY));
    
    int mods = InputEvent.CTRL_MASK;
    JFCKeyStroke[] strokes = TestHelper.getKeyMapping().getKeyStrokes(KeyEvent.VK_CONTROL);
    strokes[0].setModifiers(mods);
    robotAWTHelper.keyPressed(authorPage.getSwingComponent(), strokes[0]);
    sleep(200);
    robotAWTHelper.enterDragAndLeave(fromEvent, toEvent, 1);
    flushAWTBetter();
    sleep(200);
    robotAWTHelper.keyReleased(authorPage.getSwingComponent(), strokes[0]);
    sleep(200);
  
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should perform a copy Dnd of the entire column", 
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
        "      <p>Some text here.<table rows=\"3\" cols=\"3\">\n" + 
        "                    <head/>\n" + 
        "                    <row role=\"label\">\n" + 
        "                        <cell>Column 1</cell>\n" + 
        "                        <cell>Column 2</cell>\n" + 
        "                        <cell>Column 1</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>1</cell>\n" + 
        "                        <cell>4</cell>\n" + 
        "                        <cell>1</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>2</cell>\n" + 
        "                        <cell>5</cell>\n" + 
        "                        <cell>2</cell>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <cell>3</cell>\n" + 
        "                        <cell>6</cell>\n" + 
        "                        <cell>3</cell>\n" + 
        "                    </row>\n" + 
        "                </table></p>\n" + 
        "    </body>\n" + 
        "  </text>\n" + 
        "</TEI>", result);
  }
}
