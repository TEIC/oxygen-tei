/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2009 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.id.test;

import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.commons.id.GenerateIDsOperation;
import ro.sync.exml.editor.EditorPage;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.editor.xmleditor.prettyprint.PrettyPrintAnalyser;
import ro.sync.exml.editor.xmleditor.prettyprint.PrettyPrinter;
import ro.sync.exml.options.OptionTags;
import ro.sync.exml.options.Options;
import ro.sync.exml.workspace.api.editor.page.author.DisplayModeConstants;
import ro.sync.ui.UiUtil;
import ro.sync.util.URLUtil;

/**
 * Test for the operation {@link GenerateIDsOperation}.
 * 
 * @author mihaela
 */
public class GenerateIDsOperationTest extends EditorAuthorExtensionTestBase {
  
  /**
   * <p><b>Description:</b> Test for 'Generate IDs' operation.</p>
   * <p><b>Bug ID:</b> EXM-29244</p>
   *
   * @author mihaela
   *
   * @throws Exception When it fails.
   */
  public void testGenerateIDs() throws Exception {
    Options.getInstance().setIntegerProperty(OptionTags.TAGS_DISPLAY_MODE, 
        DisplayModeConstants.DISPLAY_MODE_FULL_TAGS_WITH_ATTRS);
    
    open(URLUtil.correct(new File("test/EXM-29244/test.dita")), true);
    
    // Select paragraph text
    moveCaretRelativeTo("Paragraph 1", -1);
    moveCaretRelativeTo("Paragraph 1", "Paragraph 1".length() + 1, true);
    
    Map<String, Object> authorExtensionActions = vViewport.getAuthorAccess().
        getEditorAccess().getActionsProvider().getAuthorExtensionActions();
    final Action action = (Action) authorExtensionActions.get("generate.ids");
    
    // Execute 'Generate IDs' action on current selection
    UiUtil.invokeSynchronously(new Runnable() {
      
      @Override
      public void run() {
        action.actionPerformed(null); 
      }
    });
    
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/task.dtd\">\n" + 
        "<task id=\"#GENERATED_ID\">\n" + 
        "    <title>Pruning</title>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p id=\"#GENERATED_ID\">Paragraph 1</p>\n" + 
        "            <p>Paragraph 2</p>\n" + 
        "            <p>Par<u>ag</u>raph 3</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", 
        true);
    
    assertEquals(indexOf("Paragraph 1") - 1, vViewport.getSelectionStart());
    assertEquals(indexOf("Paragraph 1") + "Paragraph 1".length() + 1, vViewport.getSelectionEnd());

    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        vViewport.getController().getUndoManager().undo();
      }
    });
    
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/task.dtd\">\n" + 
        "<task id=\"#GENERATED_ID\">\n" + 
        "    <title>Pruning</title>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Paragraph 1</p>\n" + 
        "            <p>Paragraph 2</p>\n" + 
        "            <p>Par<u>ag</u>raph 3</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", 
        true);
    
    // Generate ids on multiple nodes
    moveCaretRelativeTo("Paragraph 1", -1);
    moveCaretRelativeTo("raph 3", "raph 3".length() + 1, true);
    
    // Execute 'Generate IDs' action on current selection
    UiUtil.invokeSynchronously(new Runnable() {
      
      @Override
      public void run() {
        action.actionPerformed(null); 
      }
    });
    
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/task.dtd\">\n" + 
        "<task id=\"#GENERATED_ID\">\n" + 
        "    <title>Pruning</title>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p id=\"#GENERATED_ID\">Paragraph 1</p>\n" + 
        "            <p id=\"#GENERATED_ID\">Paragraph 2</p>\n" + 
        "            <p id=\"#GENERATED_ID\">Par<u>ag</u>raph 3</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", 
        true);
    
    assertEquals(indexOf("Paragraph 1") - 1, vViewport.getSelectionStart());
    assertEquals(indexOf("raph 3") + "raph 3".length() + 1, vViewport.getSelectionEnd());
    
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        vViewport.getController().getUndoManager().undo();
      }
    });
    flushAWTBetter();
    
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/task.dtd\">\n" + 
        "<task id=\"#GENERATED_ID\">\n" + 
        "    <title>Pruning</title>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Paragraph 1</p>\n" + 
        "            <p>Paragraph 2</p>\n" + 
        "            <p>Par<u>ag</u>raph 3</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", 
        true);
    
    moveCaretRelativeTo("Paragraph 1", -1);
    int para1 = vViewport.getCaretOffset();
    moveCaretRelativeTo("Paragraph 2", -1);
    int para2 = vViewport.getCaretOffset();
    moveCaretRelativeTo("raph 3", "raph 3".length() + 1);
    int para3 = vViewport.getCaretOffset();
    
    // Generate ids on multiple nodes
    int para1EndOffset = para1 + "Paragraph 1".length() + 2;
    vViewport.getAPISelectionModel().addSelection(para1, para1EndOffset);
    int para2EndOffset = para2 + "Paragraph 2".length() + 2;
    vViewport.getAPISelectionModel().addSelection(para2, para2EndOffset);
    vViewport.getAPISelectionModel().addSelection(para2EndOffset, para3);
    
    
    // Execute 'Generate IDs' action on current selection
    UiUtil.invokeSynchronously(new Runnable() {
      
      @Override
      public void run() {
        action.actionPerformed(null); 
      }
    });
    
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/task.dtd\">\n" + 
        "<task id=\"#GENERATED_ID\">\n" + 
        "    <title>Pruning</title>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p id=\"#GENERATED_ID\">Paragraph 1</p>\n" + 
        "            <p id=\"#GENERATED_ID\">Paragraph 2</p>\n" + 
        "            <p id=\"#GENERATED_ID\">Par<u>ag</u>raph 3</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", 
        true);
    
    List<int[]> selectionIntervals = vViewport.getSelectionModel().getSelectionIntervals();
    assertEquals(3, selectionIntervals.size());
    
    int[] is1 = selectionIntervals.get(0);
    assertEquals(para1, is1[0]);
    assertEquals(para1EndOffset, is1[1]);

    int[] is2 = selectionIntervals.get(1);
    assertEquals(para2, is2[0]);
    assertEquals(para2EndOffset, is2[1]);
    
    int[] is3 = selectionIntervals.get(2);
    assertEquals(para2EndOffset, is3[0]);
    assertEquals(para3, is3[1]);
    
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        vViewport.getController().getUndoManager().undo();
      }
    });
    flushAWTBetter();
    
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/task.dtd\">\n" + 
        "<task id=\"#GENERATED_ID\">\n" + 
        "    <title>Pruning</title>\n" + 
        "    <taskbody>\n" + 
        "        <context>\n" + 
        "            <p>Paragraph 1</p>\n" + 
        "            <p>Paragraph 2</p>\n" + 
        "            <p>Par<u>ag</u>raph 3</p>\n" + 
        "        </context>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "", 
        true);
    
  }
  
  /**
   * Verify author document.
   * 
   * @param expectedDoc
   * @param useOriginalDocumentForSerialization if true, use original document when serialize.
   * @throws Exception
   */
  @Override
  protected void verifyDocument(String expectedDoc, boolean useOriginalDocumentForSerialization) throws Exception {
    String result = serializeDocumentViewport(
        ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
        PrettyPrintAnalyser.getIndentOptions(editor), useOriginalDocumentForSerialization);
    result = result.replaceAll("id=\".*?\"", "id=\"#GENERATED_ID\"");
    assertEquals(
        PrettyPrinter.prettyPrint(new StringReader(expectedDoc.trim()), null),
        PrettyPrinter.prettyPrint(new StringReader(result.trim()), null));
  }
  
}