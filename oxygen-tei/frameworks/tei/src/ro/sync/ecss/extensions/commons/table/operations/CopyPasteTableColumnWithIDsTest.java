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

import javax.swing.AbstractAction;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.dita.topic.table.SADITATableCustomizerDialog;
import ro.sync.ecss.layout.table.selection.TableColumnMouseEventHandler;
import ro.sync.exml.Tags;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.view.ViewportMouseEvent;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.ui.UiUtil;
import ro.sync.util.URLUtil;

/**
 * Tests for copy and paste columns with cells havind IDs attributes set. 
 * 
 * @author mihaela
 */
public class CopyPasteTableColumnWithIDsTest extends EditorAuthorExtensionTestBase {

  /**
   * <p><b>Description:</b> Test copy paste an entire column from an author table
   * in a non-table context.</p>
   * <p><b>Bug ID:</b> EXM-30049</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testCopyPasteTableColumnInNonTableContext() throws Exception {
    open(URLUtil.correct(new File("test/EXM-30049/testTable.dita")), true);

    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    final AbstractAction copyAction = editor.getAction(Tags.EDIT, Tags.COPY_VERB);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        copyAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    moveCaretRelativeTo("Context", 0);
    final AbstractAction pasteAction = editor.getAction(Tags.EDIT, Tags.EDIT_PASTE);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(2000);
    
    SADITATableCustomizerDialog ditaTableCustomizerDialog = (SADITATableCustomizerDialog) getWindowOfClass(SADITATableCustomizerDialog.class.getName());
    ditaTableCustomizerDialog.getOkButton().doClick();
    
    flushAWTBetter();

    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p><table frame=\"all\" rowsep=\"1\" colsep=\"1\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry>Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry>1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>3</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"2\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                                <entry>4</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                                <entry>5</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
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
   * <p><b>Description:</b> Test cut paste an entire column from an author table
   * in a non-table context.</p>
   * <p><b>Bug ID:</b> EXM-30049</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testCutPasteTableColumnInNonTableContext() throws Exception {
    open(URLUtil.correct(new File("test/EXM-30049/testTable.dita")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);

    final AbstractAction cutAction = editor.getAction(Tags.EDIT, Tags.EDIT_CUT);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        cutAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    moveCaretRelativeTo("Context", 0);
    final AbstractAction pasteAction = editor.getAction(Tags.EDIT, Tags.EDIT_PASTE);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(2000);
    
    SADITATableCustomizerDialog ditaTableCustomizerDialog = (SADITATableCustomizerDialog) getWindowOfClass(SADITATableCustomizerDialog.class.getName());
    ditaTableCustomizerDialog.getOkButton().doClick();
    
    flushAWTBetter();
  
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p><table frame=\"all\" rowsep=\"1\" colsep=\"1\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
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
    
    // Another paste
    moveCaretRelativeTo("Context", 0);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(2000);
    
    ditaTableCustomizerDialog = (SADITATableCustomizerDialog) getWindowOfClass(SADITATableCustomizerDialog.class.getName());
    ditaTableCustomizerDialog.getOkButton().doClick();
    
    flushAWTBetter();
  
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p><table frame=\"all\" rowsep=\"1\" colsep=\"1\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table><table frame=\"all\" rowsep=\"1\" colsep=\"1\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"1\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry>Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry>1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>3</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
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
   * <p><b>Description:</b> Test copy paste an entire column in an author table</p>
   * <p><b>Bug ID:</b> EXM-30049</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testCopyPasteTableColumnInTableContext() throws Exception {
    open(URLUtil.correct(new File("test/EXM-30049/testTable.dita")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    final AbstractAction copyAction = editor.getAction(Tags.EDIT, Tags.COPY_VERB);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        copyAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    moveCaretRelativeTo("Column 2", 0);
    final AbstractAction pasteAction = editor.getAction(Tags.EDIT, Tags.EDIT_PASTE);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(400);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"3\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"newCol3\" colnum=\"3\" colwidth=\"1*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                                <entry>Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                                <entry>4</entry>\n" + 
        "                                <entry>1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                                <entry>5</entry>\n" + 
        "                                <entry>2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
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

  /**
   * <p><b>Description:</b> Test cut paste an entire column in an author table</p>
   * <p><b>Bug ID:</b> EXM-30049</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testCutPasteTableColumnInTableContext() throws Exception {
    open(URLUtil.correct(new File("test/EXM-30049/testTable.dita")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);
    
    final AbstractAction cutAction = editor.getAction(Tags.EDIT, Tags.EDIT_CUT);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        cutAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    moveCaretRelativeTo("Column 2", 0);
    final AbstractAction pasteAction = editor.getAction(Tags.EDIT, Tags.EDIT_PASTE);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(400);
    
    String result = serializeDocumentViewport(authorPage.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"2\">\n" + 
        "                        <colspec colname=\"c2\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c1\" colnum=\"2\" colwidth=\"1*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry>4</entry>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>5</entry>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>6</entry>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table> for the current task</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", result);
    
    moveCaretRelativeTo("Column 2", 0);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(400);
    
    result = serializeDocumentViewport(authorPage.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"3\">\n" + 
        "                        <colspec colname=\"c2\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c1\" colnum=\"2\" colwidth=\"1*\"/>\n" + 
        "                        <colspec colname=\"c1\" colnum=\"3\" colwidth=\"1*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                                <entry>Column 1</entry>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry>4</entry>\n" + 
        "                                <entry>1</entry>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>5</entry>\n" + 
        "                                <entry>2</entry>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry>6</entry>\n" + 
        "                                <entry>3</entry>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
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
   * <p><b>Description:</b> Test cut paste an entire column in an author table</p>
   * <p><b>Bug ID:</b> EXM-30049</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testCutPasteTableColumnInOtherFile() throws Exception {
    open(URLUtil.correct(new File("test/EXM-30049/testTable.dita")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);

    final AbstractAction cutAction = editor.getAction(Tags.EDIT, Tags.EDIT_CUT);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        cutAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    
    open(URLUtil.correct(new File("test/EXM-30049/testTable1.dita")), true);
    
    final AuthorEditorPage authorPage1 = 
      (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    moveCaretRelativeTo("Column 2", 0);
    final AbstractAction pasteAction = editor.getAction(Tags.EDIT, Tags.EDIT_PASTE);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(400);
    
    String result = serializeDocumentViewport(authorPage1.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"3\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c1\" colnum=\"3\" colwidth=\"1*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                                <entry>4</entry>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                                <entry>5</entry>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                                <entry>6</entry>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table> for the current task</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", result);
    
    moveCaretRelativeTo("Column 2", 0);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(400);
    
    result = serializeDocumentViewport(authorPage1.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"4\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c1\" colnum=\"3\" colwidth=\"1*\"/>\n" + 
        "                        <colspec colname=\"c1\" colnum=\"4\" colwidth=\"1*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                                <entry>4</entry>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                                <entry>5</entry>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                                <entry>6</entry>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
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
   * <p><b>Description:</b> Test copy paste an entire column in an author table</p>
   * <p><b>Bug ID:</b> EXM-30049</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testCopyPasteTableColumnInOtherFile() throws Exception {
    open(URLUtil.correct(new File("test/EXM-30049/testTable.dita")), true);
  
    final AuthorEditorPage authorPage = 
        (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    
    // Move the mouse over the first column
    moveCaretRelativeTo("Column 1", 0);
    Rectangle caret = authorPage.getViewport().getCaretShape();
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_MOVED, ViewportMouseEvent.BUTTON1, 1));
    authorPage.getViewport().mouseEvent(new ViewportMouseEvent(caret.x + 30, caret.y - TableColumnMouseEventHandler.DISTANCE_ABOVE_COLUMN, 
        false, ViewportMouseEvent.STATE_PRESSED, ViewportMouseEvent.BUTTON1, 1));
    Thread.sleep(500);

    final AbstractAction copyAction = editor.getAction(Tags.EDIT, Tags.COPY_VERB);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        copyAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(200);
    
    
    open(URLUtil.correct(new File("test/EXM-30049/testTable1.dita")), true);
    
    final AuthorEditorPage authorPage1 = 
      (AuthorEditorPage)editorManager.getSelectedEditor().getCurrentPage();
    moveCaretRelativeTo("Column 2", 0);
    final AbstractAction pasteAction = editor.getAction(Tags.EDIT, Tags.EDIT_PASTE);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(400);
    
    String result = serializeDocumentViewport(authorPage1.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"3\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"newCol3\" colnum=\"3\" colwidth=\"1*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                                <entry>4</entry>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                                <entry>5</entry>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                                <entry>6</entry>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table> for the current task</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", result);
    
    moveCaretRelativeTo("Column 2", 0);
    // Browse for database resource
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        pasteAction.actionPerformed(null);
      }
    });
    flushAWTBetter();
    Thread.sleep(400);
    
    result = serializeDocumentViewport(authorPage1.getViewport(), true);
    result = result.replaceAll("id=\"table_.*\"", "id=\"#GENERATED_ID\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"../../frameworks/dita/css_classed/hide_colspec.css\" ?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA General Task//EN\" \"task.dtd\">\n" + 
        "<task id=\"task_kwd_zgb_ff\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <shortdesc/>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Context<table frame=\"all\" id=\"#GENERATED_ID\">\n" + 
        "                    <title/>\n" + 
        "                    <tgroup cols=\"4\">\n" + 
        "                        <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"c2\" colnum=\"2\" colwidth=\"1.0*\"/>\n" + 
        "                        <colspec colname=\"newCol4\" colnum=\"3\" colwidth=\"1*\"/>\n" + 
        "                        <colspec colname=\"newCol3\" colnum=\"4\" colwidth=\"1*\"/>\n" + 
        "                        <thead>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                                <entry>Column 2</entry>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                                <entry id=\"id1\">Column 1</entry>\n" + 
        "                            </row>\n" + 
        "                        </thead>\n" + 
        "                        <tbody>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                                <entry>4</entry>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                                <entry id=\"id2\">1</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                                <entry>5</entry>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                                <entry id=\"id3\">2</entry>\n" + 
        "                            </row>\n" + 
        "                            <row>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                                <entry>6</entry>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                                <entry id=\"id4\">3</entry>\n" + 
        "                            </row>\n" + 
        "                        </tbody>\n" + 
        "                    </tgroup>\n" + 
        "                </table> for the current task</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", result);
  }
}
