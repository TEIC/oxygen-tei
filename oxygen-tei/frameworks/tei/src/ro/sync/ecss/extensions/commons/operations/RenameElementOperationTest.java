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
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.commons.id.GenerateIDElementsInfo;
import ro.sync.ecss.extensions.dita.id.DITAUniqueAttributesRecognizer;
import ro.sync.exml.editor.EditorPage;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.editor.xmleditor.prettyprint.PrettyPrintAnalyser;
import ro.sync.exml.options.Options;
import ro.sync.ui.UiUtil;
import ro.sync.util.URLUtil;

/**
 * Tests for renaming an element.
 * 
 * @author alex_jitianu
 */
public class RenameElementOperationTest extends EditorAuthorExtensionTestBase {
  
  /**
   * <p><b>Description:</b> Test for rename multiple elements which corresponds to a xpath expression.</p>
   * <p><b>Bug ID:</b> EXM-31169</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRenameMultipleElements() throws Exception {
    
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 2);
      
      open(URLUtil.correct(new File("test/EXM-30631/test.xml")), false, false);
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());

      final RenameElementOperation op = new RenameElementOperation();

      moveCaretRelativeTo("Delete operation", 0);

      Map userValues = new HashMap();
      userValues.put(RenameElementOperation.ARGUMENT_ELEMENT_NAME, "ul");
      userValues.put(RenameElementOperation.ARGUMENT_ELEMENT_XPATH_LOCATION, "//ol");

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
          "    <title>Delete operation</title>\n" + 
          "    <body>\n" + 
          "        <p>Delete operation scenarios based on nested elements.</p>\n" + 
          "        <ul>\n" + 
          "      <li>First level - 1 item</li>\n" + 
          "      <li>First level - 2 item<ul>\n" + 
          "          <li>Second level - 1 item</li>\n" + 
          "          <li>Second level - 2 item</li>\n" + 
          "        </ul></li>\n" + 
          "      <li>First level - 3 item<ul>\n" + 
          "          <li>Second level - 3 item</li>\n" + 
          "        </ul></li>\n" + 
          "    </ul>\n" + 
          "        <p>--------------------</p>\n" + 
          "        <ul>\n" + 
          "      <li>\n" + 
          "        <p>Only one list item</p>\n" + 
          "      </li>\n" + 
          "    </ul>\n" + 
          "        <p>---------------</p>\n" + 
          "        <ul>\n" + 
          "      <li>Text<p>Para</p></li>\n" + 
          "    </ul>\n" + 
          "    </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }

  /**
   * <p><b>Description:</b> Test for rename the current element.</p>
   * <p><b>Bug ID:</b> EXM-31169</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRenameCurrentElement() throws Exception {
    int indentSize = Options.getInstance().getIntegerProperty(Options.EDITOR_INDENT_SIZE);
    try {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, 2);
      
      open(URLUtil.correct(new File("test/EXM-30631/test.xml")), false, false);
      new GenerateIDElementsInfo(true, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[] {
      "topic/topic"}).saveToOptions(vViewport.getAuthorAccess());
  
      final RenameElementOperation op = new RenameElementOperation();
  
      moveCaretRelativeTo("Delete operation", 0);
  
      Map userValues = new HashMap();
      userValues.put(RenameElementOperation.ARGUMENT_ELEMENT_NAME, "ul");
      userValues.put(RenameElementOperation.ARGUMENT_ELEMENT_XPATH_LOCATION, "");
  
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
          "    <ul>Delete operation</ul>\n" + 
          "    <body>\n" + 
          "        <p>Delete operation scenarios based on nested elements.</p>\n" + 
          "        <ol>\n" + 
          "            <li>First level - 1 item</li>\n" + 
          "            <li>First level - 2 item<ol>\n" + 
          "                <li>Second level - 1 item</li>\n" + 
          "                <li>Second level - 2 item</li>\n" + 
          "            </ol></li>\n" + 
          "            <li>First level - 3 item<ol>\n" + 
          "                <li>Second level - 3 item</li>\n" + 
          "            </ol></li>\n" + 
          "        </ol>\n" + 
          "        <p>--------------------</p>\n" + 
          "        <ol>\n" + 
          "            <li>\n" + 
          "                <p>Only one list item</p>\n" + 
          "            </li>\n" + 
          "        </ol>\n" + 
          "        <p>---------------</p>\n" + 
          "        <ol>\n" + 
          "            <li>Text<p>Para</p></li>\n" + 
          "        </ol>\n" + 
          "    </body>\n" + 
          "</topic>\n" + 
          "", document);
    } finally {
      Options.getInstance().setIntegerProperty(Options.EDITOR_INDENT_SIZE, indentSize);
    }
  }
}