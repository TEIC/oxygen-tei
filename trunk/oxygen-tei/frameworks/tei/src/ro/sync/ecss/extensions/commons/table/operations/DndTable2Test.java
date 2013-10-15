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
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.JButton;

import junit.extensions.jfcunit.RobotTestHelper;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.keyboard.JFCKeyStroke;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.commons.table.operations.xhtml.SAXHTMLTableCustomizerDialog;
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
public class DndTable2Test extends EditorAuthorExtensionTestBase {

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
   * <p><b>Description:</b> Test DND row from an author table for XHTML.</p>
   * <p><b>Bug ID:</b> EXM-23338</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDeleteTableRowXHTML() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableXHTML.xhtml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    flushAWTBetter();
    Rectangle caret = authorPage.getViewport().getCaretShape();
    int fromX = caret.getX() - 5;
    int fromY = caret.getY() + 10;

    // To
    moveCaretRelativeTo("5", -2);
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
    Thread.sleep(200);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    assertEquals("Should have moved the selected row", 
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
        "            <thead></thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>1</td>\n" + 
        "                    <td>4</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <th>Column 1</th>\n" + 
        "                    <th>Column 2</th>\n" + 
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
        "</html>", result);
    
    List<int[]> selectionIntervals = vViewport.getSelectionModel().getSelectionIntervals();
    assertEquals(1, selectionIntervals.size());
    
    int[] is = selectionIntervals.get(0);
    assertEquals(27, is[0]);
    assertEquals(49, is[1]);
  }
  
  /**
   * <p><b>Description:</b> Test keep selections after reject insert table operation.</p>
   * <p><b>Bug ID:</b> EXM-18945</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testSelectionAfterDndXHTML() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableXHTML.xhtml")));
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
  
 // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    flushAWTBetter();
    Rectangle caret = authorPage.getViewport().getCaretShape();
    int fromX = caret.getX() + 10;
    int fromY = caret.getY()- 10;
    
    // To
    moveCaretRelativeTo("Text", "Text".length());
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
  
    Window xhtmlTableCustomizerDialog = getWindowOfClass(SAXHTMLTableCustomizerDialog.class.getName());
    assertTrue(xhtmlTableCustomizerDialog.isShowing());
    JButton cancelBtn = findComponent(xhtmlTableCustomizerDialog, JButton.class, 3);
    cancelBtn.doClick();
    flushAWTBetter();
    
    List<int[]> selectionIntervals = vViewport.getSelectionModel().getSelectionIntervals();
    assertEquals(4, selectionIntervals.size());
    
    int[] is = selectionIntervals.get(0);
    assertEquals(18, is[0]);
    assertEquals(28, is[1]);
    
    is = selectionIntervals.get(1);
    assertEquals(42, is[0]);
    assertEquals(45, is[1]);
    
    is = selectionIntervals.get(2);
    assertEquals(50, is[0]);
    assertEquals(53, is[1]);
    
    is = selectionIntervals.get(3);
    assertEquals(58, is[0]);
    assertEquals(61, is[1]);
    
  }

  /**
   * <p><b>Description:</b> Test drag and drop an entire column from an author table.</p>
   * <p><b>Bug ID:</b> EXM-18945</p>
   *
   * @author costi
   *
   * @throws Exception
   */
  public void testDndTableColumnXHTMLKeepSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-18945/dnd/testTableXHTML.xhtml")));
  
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
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Column 2</th>\n" + 
        "                    <th>Column 1</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>4</td>\n" + 
        "                    <td>1</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>5</td>\n" + 
        "                    <td>2</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>6</td>\n" + 
        "                    <td>3</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>", result);
    
    List<int[]> selectionIntervals = vViewport.getSelectionModel().getSelectionIntervals();
    assertEquals(4, selectionIntervals.size());
    
    int[] is = selectionIntervals.get(0);
    assertEquals(28, is[0]);
    assertEquals(38, is[1]);
    
    is = selectionIntervals.get(1);
    assertEquals(45, is[0]);
    assertEquals(48, is[1]);
    
    is = selectionIntervals.get(2);
    assertEquals(53, is[0]);
    assertEquals(56, is[1]);
    
    is = selectionIntervals.get(3);
    assertEquals(61, is[0]);
    assertEquals(64, is[1]);
  }
}
