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
package ro.sync.ecss.extensions.commons.operations;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ro.sync.ecss.extensions.ArgumentsMapImpl;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.id.GenerateIDElementsInfo;
import ro.sync.exml.editor.EditorPage;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.editor.xmleditor.prettyprint.PrettyPrintAnalyser;
import ro.sync.exml.options.Options;
import ro.sync.ui.UiUtil;
import ro.sync.util.URLUtil;

/**
 * Tests for moving an element around.
 * 
 * @author alex_jitianu
 */
public class MoveElementOperationTest extends EditorAuthorExtensionTestBase {
  
  /**
   * <p><b>Description:</b> Moves an element.</p>
   * <p><b>Bug ID:</b> EXM-30647</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testMoveElement1() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 4);
      
      open(URLUtil.correct(new File("test/EXM-30647/genericMove.dita")), false, false);

      // We don't want IDs being generated for the newly inserted lists.
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());
      
      moveCaretRelativeTo("Bold", 0);
      final MoveElementOperation op = new MoveElementOperation();

      Map userValues = new HashMap();
      // Moves the current li
      userValues.put("sourceLocation", "ancestor-or-self::b[1]");
      // After second paragraph.
      userValues.put("targetLocation", "ancestor-or-self::p[1]/following-sibling::p[1]");
      userValues.put("insertPosition", AuthorConstants.POSITION_AFTER);
      // Delete the entire paragraph. An ancestor of the node to move.
      userValues.put("deleteLocation", "ancestor-or-self::p[1]");
      // Surround the moved nodes inside a new list.
      userValues.put("surroundFragment", "<p/>");

      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);

      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
      
      int caretOffset = vViewport.getCaretOffset();
      AuthorNode node = vViewport.getController().getNodeAtOffset(caretOffset);
      assertEquals("b", node.getName());
      assertEquals(node.getStartOffset() + 1, caretOffset);


      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_ang_hrw_j4\">\n" + 
          "    <title>To move test</title>\n" + 
          "    <body>\n" + 
          "        <p>Second para</p>\n" + 
          "        <p><b>Bold</b></p>\n" + 
          "    </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }
  
  /**
   * <p><b>Description:</b> Moves an element. By default it noves the element at caret position.</p>
   * <p><b>Bug ID:</b> EXM-30647</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testMoveElementNoSource() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      // We don't want IDs being generated for the newly inserted lists.
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 4);
      
      open(URLUtil.correct(new File("test/EXM-30647/genericMove.dita")), false, false);
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {"topic/topic"}).saveToOptions(
          vViewport.getAuthorAccess());

      moveCaretRelativeTo("Bold", 0);
      final MoveElementOperation op = new MoveElementOperation();

      Map userValues = new HashMap();
      // After second paragraph.
      userValues.put("targetLocation", "ancestor-or-self::p[1]/following-sibling::p[1]");
      userValues.put("insertPosition", AuthorConstants.POSITION_AFTER);
      // Delete the entire paragraph. An ancestor of the node to move.
      userValues.put("deleteLocation", "ancestor-or-self::p[1]");
      // Surround the moved nodes inside a new list.
      userValues.put("surroundFragment", "<p/>");

      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);

      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });


      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_ang_hrw_j4\">\n" + 
          "    <title>To move test</title>\n" + 
          "    <body>\n" + 
          "        <p>Second para</p>\n" + 
          "        <p><b>Bold</b></p>\n" + 
          "    </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }

  /**
   * <p><b>Description:</b> Moves an element.</p>
   * <p><b>Bug ID:</b> EXM-30647</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testMoveElement2() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 4);
      
      open(URLUtil.correct(new File("test/EXM-30647/genericMove.dita")), false, false);
      
      // We don't want IDs being generated for the newly inserted lists.
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());
  
      moveCaretRelativeTo("Bold", 0);
      final MoveElementOperation op = new MoveElementOperation();
  
      Map userValues = new HashMap();
      // Moves the current li
      userValues.put("sourceLocation", "ancestor-or-self::b[1]");
      // After second paragraph.
      userValues.put("targetLocation", "ancestor-or-self::p[1]/following-sibling::p[1]");
      userValues.put("insertPosition", AuthorConstants.POSITION_INSIDE_FIRST);
      // Delete the entire paragraph. An ancestor of the node to move.
      userValues.put("deleteLocation", "ancestor-or-self::p[1]");
      // No surround with fragment.
      userValues.put("surroundFragment", null);
  
      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);
  
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
  
  
      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_ang_hrw_j4\">\n" + 
          "    <title>To move test</title>\n" + 
          "    <body>\n" + 
          "        <p><b>Bold</b>Second para</p>\n" + 
          "    </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }
  
  /**
   * <p><b>Description:</b> Moves an element. Only the content is moved in this test.</p>
   * <p><b>Bug ID:</b> EXM-30647</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testMoveElement3() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 4);
      
      open(URLUtil.correct(new File("test/EXM-30647/genericMove.dita")), false, false);
      
      // We don't want IDs being generated for the newly inserted lists.
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());
      
      String initialDocument = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
  
      moveCaretRelativeTo("Bold", 0);
      final MoveElementOperation op = new MoveElementOperation();
  
      Map userValues = new HashMap();
      // Moves the current li
      userValues.put("sourceLocation", "ancestor-or-self::p[1]");
      userValues.put("moveOnlySourceContentNodes", "true");
      // After second paragraph.
      userValues.put("targetLocation", "ancestor-or-self::p[1]/following-sibling::p[1]");
      userValues.put("insertPosition", AuthorConstants.POSITION_INSIDE_FIRST);
      // Delete the entire paragraph. An ancestor of the node to move.
      userValues.put("deleteLocation", "ancestor-or-self::p[1]");
      // No surround with fragment.
      userValues.put("surroundFragment", null);
  
      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);
  
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
  
  
      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_ang_hrw_j4\">\n" + 
          "    <title>To move test</title>\n" + 
          "    <body>\n" + 
          "        <p><b/>First. <b>Bold</b> element.Second para</p>\n" + 
          "    </body>\n" + 
          "</topic>\n" + 
          "", document);
      
      // UNDO
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          editor.getCurrentPage().undo();
        }
      });
      flushAWTBetter();
      
      document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals(initialDocument, document);
      
      // Move the contents of an empty element. This is more of a extreme situation. 
      moveCaretRelativeTo("First", -1);
      
      userValues.put("sourceLocation", "ancestor-or-self::b[1]");
      userValues.put("deleteLocation", null);
      argumentsMap.copyMap(userValues);
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
  
      document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_ang_hrw_j4\">\n" + 
          "    <title>To move test</title>\n" + 
          "    <body>\n" + 
          "        <p>First. <b>Bold</b> element.</p>\n" + 
          "        <p>Second para</p>\n" + 
          "    </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }
  
  /**
   * <p><b>Description:</b> Moves an element. Error situations.</p>
   * <p><b>Bug ID:</b> EXM-30647</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testMoveElementErrorCases() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 4);
      
      open(URLUtil.correct(new File("test/EXM-30647/genericMove.dita")), false, false);
      
      // We don't want IDs being generated for the newly inserted lists.
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());
      
      String initialDocument = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);

      moveCaretRelativeTo("Bold", 0);
      final MoveElementOperation op = new MoveElementOperation();
  
      Map userValues = new HashMap();
      // The XPath doesn't identify any node.
      userValues.put("sourceLocation", "ancestor-or-self::bla[1]");
      // After second paragraph.
      userValues.put("targetLocation", "ancestor-or-self::p[1]/following-sibling::p[3]");
      userValues.put("insertPosition", AuthorConstants.POSITION_INSIDE_FIRST);
      // Delete the entire paragraph. An ancestor of the node to move.
      userValues.put("deleteLocation", "ancestor-or-self::p[1]/following-sibling::p[1]");
      // No surround with fragment.
      userValues.put("surroundFragment", null);
  
      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);
  
      final AuthorOperationException[] expected = new AuthorOperationException[1];
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            expected[0] = e;
          }
        }
      });
      
      assertNotNull("An argument is invalid. An exception should be thrown.", expected[0]);
      assertEquals("The XPath expression: ancestor-or-self::bla[1] - doesn't identify any node", expected[0].getMessage());
      
      // Correct the source.
      userValues.put("sourceLocation", "ancestor-or-self::b[1]");
      
      argumentsMap.copyMap(userValues);

      expected[0] = null;
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            expected[0] = e;
          }
        }
      });
      
      assertNotNull("An argument is invalid. An exception should be thrown.", expected[0]);
      assertEquals("The XPath expression: ancestor-or-self::p[1]/following-sibling::p[3] - doesn't identify any node", expected[0].getMessage());

      
      // Correct the target.
      userValues.put("targetLocation", "ancestor-or-self::p[1]/following-sibling::p[1]");
      
      argumentsMap.copyMap(userValues);

      expected[0] = null;
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            expected[0] = e;
          }
        }
      });
      
      assertNotNull("An argument is invalid. An exception should be thrown.", expected[0]);
      assertEquals("Trying to move inside the node that will be removed. Node to remove: <p class=\"- topic/p \" ,> (43, 55). Computed insertion offset 44", expected[0].getMessage());

      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals(initialDocument, document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }
  
  /**
   * <p><b>Description:</b> Moves an element.</p>
   * <p><b>Bug ID:</b> EXM-30647</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testMoveOperationLists() throws Exception {
    
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 2);
      
      open(URLUtil.correct(new File("test/EXM-30647/ditaMoveScenarios.dita")), false, false);
      
      // We don't want IDs being generated for the newly inserted lists.
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());

      final MoveElementOperation op = new MoveElementOperation();

      // 1. A sort of a DEMOTE. The current list item
      moveCaretRelativeTo("Second level - 2 item", 0);

      Map userValues = new HashMap();
      // Moves the current li
      userValues.put("sourceLocation", "ancestor-or-self::li[1]");
      // After the current li.
      userValues.put("targetLocation", "ancestor-or-self::li[1]");
      userValues.put("insertPosition", AuthorConstants.POSITION_AFTER);
      // The node being moves is the one being deleted.
      userValues.put("deleteLocation", null);
      // Surround the moved nodes inside a new list.
      userValues.put("surroundFragment", "<li><ol></ol></li>");

      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);

      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });


      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_vpl_fjw_j4\">\n" + 
          "  <title>MoveElementOperation scenarios</title>\n" + 
          "  <body>\n" + 
          "    <p>Move operation scenarios based on nested lists.</p>\n" + 
          "    <ol>\n" + 
          "      <li>First level - 1 item</li>\n" + 
          "      <li>First level - 2 item<ol>\n" + 
          "        <li>Second level - 1 item</li>\n" + 
          "          <li>\n" + 
          "            <ol>\n" + 
          "              <li>Second level - 2 item</li>\n" + 
          "            </ol>\n" + 
          "          </li>\n" + 
          "        </ol></li>\n" + 
          "      <li>First level - 3 item<ol>\n" + 
          "        <li>Second level - 3 item</li>\n" + 
          "        </ol></li>\n" + 
          "    </ol>\n" + 
          "    <p>--------------------</p>\n" + 
          "    <ol>\n" + 
          "      <li>\n" + 
          "        <p>Only one list item</p>\n" + 
          "      </li>\n" + 
          "    </ol>\n" + 
          "    <p>---------------</p>\n" + 
          "    <ol>\n" + 
          "      <li>Text<p>Para</p></li>\n" + 
          "    </ol>\n" + 
          "  </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }

  /**
   * <p><b>Description:</b> Moves an element.</p>
   * <p><b>Bug ID:</b> EXM-30647</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testMoveOperationListsPromote() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 2);
      
      open(URLUtil.correct(new File("test/EXM-30647/ditaMoveScenarios.dita")), false, false);
      // We don't want IDs being generated for the newly inserted lists.
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());
      
      final MoveElementOperation op = new MoveElementOperation();
  
      // 1. A sort of a PROMOTE. The current list item
      moveCaretRelativeTo("Second level - 2 item", 0);
  
      Map userValues = new HashMap();
      // Moves the current li
      userValues.put("sourceLocation", "ancestor-or-self::li[1]");
      // After the current li.
      userValues.put("targetLocation", "ancestor-or-self::ol[1]/parent::li[1]");
      userValues.put("insertPosition", AuthorConstants.POSITION_AFTER);
      // The node being moves is the one being deleted.
      userValues.put("deleteLocation", null);
      // Surround the moved nodes inside a new list.
      userValues.put("surroundFragment", null);
  
      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);
  
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
  
  
      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_vpl_fjw_j4\">\n" + 
          "  <title>MoveElementOperation scenarios</title>\n" + 
          "  <body>\n" + 
          "    <p>Move operation scenarios based on nested lists.</p>\n" + 
          "    <ol>\n" + 
          "      <li>First level - 1 item</li>\n" + 
          "      <li>First level - 2 item<ol>\n" + 
          "        <li>Second level - 1 item</li>\n" + 
          "        </ol></li>\n" + 
          "      <li>Second level - 2 item</li>\n" + 
          "      <li>First level - 3 item<ol>\n" + 
          "        <li>Second level - 3 item</li>\n" + 
          "        </ol></li>\n" + 
          "    </ol>\n" + 
          "    <p>--------------------</p>\n" + 
          "    <ol>\n" + 
          "      <li>\n" + 
          "        <p>Only one list item</p>\n" + 
          "      </li>\n" + 
          "    </ol>\n" + 
          "    <p>---------------</p>\n" + 
          "    <ol>\n" + 
          "      <li>Text<p>Para</p></li>\n" + 
          "    </ol>\n" + 
          "  </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }

  /**
   * <p><b>Description:</b> Moves an element.</p>
   * <p><b>Bug ID:</b> EXM-30647</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testMoveOperationListsPromote2() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 2);
      
      open(URLUtil.correct(new File("test/EXM-30647/ditaMoveScenarios.dita")), false, false);
      
      // We don't want IDs being generated for the newly inserted lists.
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());
      
      final MoveElementOperation op = new MoveElementOperation();
  
      // 1. A sort of a PROMOTE. The current list item
      moveCaretRelativeTo("Second level - 3 item", 0);
  
      Map userValues = new HashMap();
      // Moves the current li
      userValues.put("sourceLocation", "ancestor-or-self::li[1]");
      // After the current li.
      userValues.put("targetLocation", "ancestor-or-self::ol[1]/parent::li[1]");
      userValues.put("insertPosition", AuthorConstants.POSITION_AFTER);
      // The node being moves is the one being deleted.
      userValues.put("deleteLocation", "ancestor-or-self::ol[1]");
      // Surround the moved nodes inside a new list.
      userValues.put("surroundFragment", null);
  
      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);
  
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
  
  
      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_vpl_fjw_j4\">\n" + 
          "  <title>MoveElementOperation scenarios</title>\n" + 
          "  <body>\n" + 
          "    <p>Move operation scenarios based on nested lists.</p>\n" + 
          "    <ol>\n" + 
          "      <li>First level - 1 item</li>\n" + 
          "      <li>First level - 2 item<ol>\n" + 
          "        <li>Second level - 1 item</li>\n" + 
          "        <li>Second level - 2 item</li>\n" + 
          "        </ol></li>\n" + 
          "      <li>First level - 3 item</li>\n" + 
          "      <li>Second level - 3 item</li>\n" + 
          "    </ol>\n" + 
          "    <p>--------------------</p>\n" + 
          "    <ol>\n" + 
          "      <li>\n" + 
          "        <p>Only one list item</p>\n" + 
          "      </li>\n" + 
          "    </ol>\n" + 
          "    <p>---------------</p>\n" + 
          "    <ol>\n" + 
          "      <li>Text<p>Para</p></li>\n" + 
          "    </ol>\n" + 
          "  </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }

  /**
   * <p><b>Description:</b> Moves an element.</p>
   * <p><b>Bug ID:</b> EXM-30647</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testMoveOperationListsPromote3() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      // We don't want IDs being generated for the newly inserted lists.
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 2);
      
      open(URLUtil.correct(new File("test/EXM-30647/ditaMoveScenarios.dita")), false, false);
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());
      
      final MoveElementOperation op = new MoveElementOperation();
  
      // 1. A sort of a PROMOTE. The current list item
      moveCaretRelativeTo("Only one list item", 0);
  
      Map userValues = new HashMap();
      // Moves the current li
      userValues.put("sourceLocation", "ancestor-or-self::li[1]/p");
      // After the current li.
      userValues.put("targetLocation", "ancestor-or-self::ol[1]");
      userValues.put("insertPosition", AuthorConstants.POSITION_AFTER);
      // The node being moves is the one being deleted.
      userValues.put("deleteLocation", "ancestor-or-self::ol[1]");
      // Surround the moved nodes inside a new list.
      userValues.put("surroundFragment", null);
  
      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);
  
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
  
  
      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_vpl_fjw_j4\">\n" + 
          "  <title>MoveElementOperation scenarios</title>\n" + 
          "  <body>\n" + 
          "    <p>Move operation scenarios based on nested lists.</p>\n" + 
          "    <ol>\n" + 
          "      <li>First level - 1 item</li>\n" + 
          "      <li>First level - 2 item<ol>\n" + 
          "        <li>Second level - 1 item</li>\n" + 
          "        <li>Second level - 2 item</li>\n" + 
          "        </ol></li>\n" + 
          "      <li>First level - 3 item<ol>\n" + 
          "        <li>Second level - 3 item</li>\n" + 
          "        </ol></li>\n" + 
          "    </ol>\n" + 
          "    <p>--------------------</p>\n" + 
          "    <p>Only one list item</p>\n" + 
          "    <p>---------------</p>\n" + 
          "    <ol>\n" + 
          "      <li>Text<p>Para</p></li>\n" + 
          "    </ol>\n" + 
          "  </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }

  /**
   * <p><b>Description:</b> Moves an element taking track changes into account.</p>
   * <p><b>Bug ID:</b> EXM-35374</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testMoveElementCTEXM_35374() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 4);
      
      open(URLUtil.correct(new File("test/EXM-30647/genericMoveTC.dita")), false, false);
  
      // We don't want IDs being generated for the newly inserted lists.
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());
      
      moveCaretRelativeTo("Bold", 0);
      final MoveElementOperation op = new MoveElementOperation();
  
      Map userValues = new HashMap();
      // Moves the current li
      userValues.put("sourceLocation", "ancestor-or-self::b[1]");
      // After second paragraph.
      userValues.put("targetLocation", "ancestor-or-self::p[1]/following-sibling::p[1]");
      userValues.put("insertPosition", AuthorConstants.POSITION_AFTER);
      // Delete the entire paragraph. An ancestor of the node to move.
      userValues.put("deleteLocation", "ancestor-or-self::p[1]");
      // Surround the moved nodes inside a new list.
      userValues.put("surroundFragment", "<p/>");
      userValues.put("processTrackedChangesForXPathLocations", "true");
  
      final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
      argumentsMap.copyMap(userValues);
  
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
  
      String document = serializeDocumentViewport(
          ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
          PrettyPrintAnalyser.getIndentOptions(editor), true);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
          "<topic id=\"topic_ang_hrw_j4\">\n" + 
          "    <title>To move test</title>\n" + 
          "    <body>\n" + 
          "        <?oxy_delete author=\"radu_coravu\" timestamp=\"20160106T152317+0200\" content=\"&lt;p&gt;DELETED para&lt;/p&gt;\"?>\n" + 
          "        <p>Second para</p>\n" + 
          "        <p><b>Bold</b></p>\n" + 
          "    </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }
  
  /**
   * <p><b>Description:</b> Moves topicref elements using DITA Map framework actions.</p>
   * <p><b>Bug ID:</b> EXM-37081</p>
   *
   * @author george
   *
   * @throws Exception
   */
  public void testMoveElementDITAMapEXM_37081() throws Exception {
      open(URLUtil.correct(new File("test/EXM-37081/test.ditamap")));
  
      moveCaretRelativeTo("Test", 6);
      
      invokeAction("Move Down");
      flushAWTBetter();
      
      verifyDocument(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">\n" + 
          "<map>\n" + 
          " <title>Test</title>\n" + 
          "  <topicref href=\"topic2.dita\"/>\n" + 
          "  <topicref href=\"topic1.dita\"/>\n" + 
          "  <topicref href=\"topic3.dita\"/>\n" + 
          "  <topicref href=\"topic4.dita\"/>\n" + 
          "</map>\n", true);
      
      invokeAction("Move Down");
      flushAWTBetter();
      
      verifyDocument(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">\n" + 
          "<map>\n" + 
          " <title>Test</title>\n" + 
          "  <topicref href=\"topic2.dita\"/>\n" + 
          "  <topicref href=\"topic3.dita\"/>\n" + 
          "  <topicref href=\"topic1.dita\"/>\n" + 
          "  <topicref href=\"topic4.dita\"/>\n" + 
          "</map>\n", true);
      
      invokeAction("Demote");
      flushAWTBetter();
      
      verifyDocument(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">\n" + 
          "<map>\n" + 
          " <title>Test</title>\n" + 
          "  <topicref href=\"topic2.dita\"/>\n" + 
          "  <topicref href=\"topic3.dita\">\n" + 
          "    <topicref href=\"topic1.dita\"/>\n" + 
          "  </topicref>\n" + 
          "  <topicref href=\"topic4.dita\"/>\n" + 
          "</map>\n", true);
      
      invokeAction("Promote");
      flushAWTBetter();
      
      verifyDocument(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">\n" + 
          "<map>\n" + 
          " <title>Test</title>\n" + 
          "  <topicref href=\"topic2.dita\"/>\n" + 
          "  <topicref href=\"topic3.dita\"/>\n" + 
          "  <topicref href=\"topic1.dita\"/>\n" + 
          "  <topicref href=\"topic4.dita\"/>\n" + 
          "</map>\n", true);
      
      invokeAction("Move Up");
      flushAWTBetter();
      verifyDocument(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">\n" + 
          "<map>\n" + 
          " <title>Test</title>\n" + 
          "  <topicref href=\"topic2.dita\"/>\n" + 
          "  <topicref href=\"topic1.dita\"/>\n" + 
          "  <topicref href=\"topic3.dita\"/>\n" + 
          "  <topicref href=\"topic4.dita\"/>\n" + 
          "</map>\n", true);
      
      
      invokeAction("Move Up");
      flushAWTBetter();
      verifyDocument(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">\n" + 
          "<map>\n" + 
          " <title>Test</title>\n" + 
          "  <topicref href=\"topic1.dita\"/>\n" + 
          "  <topicref href=\"topic2.dita\"/>\n" + 
          "  <topicref href=\"topic3.dita\"/>\n" + 
          "  <topicref href=\"topic4.dita\"/>\n" + 
          "</map>\n", true);
  }
}
