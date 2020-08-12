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

import javax.swing.Action;

import ro.sync.ecss.component.AuthorViewport;
import ro.sync.ecss.component.editor.EditingContext;
import ro.sync.ecss.dom.AuthorDocumentImpl;
import ro.sync.ecss.extensions.ArgumentsMapImpl;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.Content;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.AuthorParentNode;
import ro.sync.exml.OxygenSystemProperties;
import ro.sync.exml.editor.DocumentTypeRepository;
import ro.sync.exml.editor.EditorPage;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.editor.xmleditor.prettyprint.PrettyPrintAnalyser;
import ro.sync.exml.options.Options;
import ro.sync.ui.UiUtil;
import ro.sync.util.URLUtil;
import ro.sync.util.editorvars.EditorVariables;

/**
 * Test for insert fragment operation.
 * 
 * @author alex_jitianu
 */
public class InsertFragmentOperationTest extends EditorAuthorExtensionTestBase {
  
  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    System.setProperty(
        OxygenSystemProperties.COM_OXYGENXML_EDITOR_HOME_URL,
        URLUtil.correct(new File(".")).toString());
    EditorVariables.setFrameworksDirForTest(new File("test/EXM-25375").getAbsoluteFile());
    Options.clearInstanceTest();
    DocumentTypeRepository.getInstance().reloadFromOptions();
    
    super.setUp();
  }

  /**
   * <p><b>Description:</b> After inserting a fragment we will position the user
   * inside the first editable position.</p>
   * <p><b>Bug ID:</b> EXM-25185</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testFindEditLocation() throws Exception {
    open(URLUtil.correct(new File("test/EXM-14621/personal/personal2.xml")), false, false);
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    AuthorDocumentImpl authorDocument = viewport.getController().getAuthorDocument();
    Content content = authorDocument.getContent();
    String string = content.getString(0, content.getLength());
    string = string.replace((char) 0, 'X');
  
    // Move at the end of the first person.
    moveCaret(authorDocument.getRootElement().getContentNodes().get(0).getEndOffset());
    
    Action action = (Action) authorEditorPage.getAuthorExtensionActions().get("add.person");
    assertNotNull(action);
    invokeAction(action);
    Thread.sleep(500);
    
    // Test that we are editing.
    assertTrue("After the insert we must be editing", viewport.isEditingInplace());
    EditingContext editingContext = viewport.getHost().getInplaceEditorHelper().getEditingContext();
    // Make sure we are editing the right attribute.
    String editingContextString = editingContext.toString();
    editingContextString = editingContextString.replaceAll("id=\"([^\"]*)\"", "id=\"new.id\"");
    assertEquals("Node: <person id=\"new.id\" , contr=\"false\" ,> (93, 104), content Editor args: [check, contr, @contr, Yes], idx 0",
        editingContextString);
    // Verify the document content.
    
    String document = serializeDocumentViewport(
        ((AuthorEditorPage)editor.getEditorPage(EditorPage.PAGE_AUTHOR)).getViewport(),
        PrettyPrintAnalyser.getIndentOptions(editor), false);
    document = document.replaceAll("id=\"person_([^\"]*)\"", "id=\"new.id\"");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE personnel PUBLIC \"PERSONNEL\" \"../../personal.dtd\">\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"personal2.css\"?>\n" + 
        "<personnel>\n" + 
        "    <person id=\"Big.Boss\">\n" + 
        "        <name>\n" + 
        "            <family>Boss</family>\n" + 
        "            <given>Big</given>\n" + 
        "        </name>\n" + 
        "        <email>chief@oxygenxml.com</email>\n" + 
        "        <link subordinates=\"one.worker\"/>\n" + 
        "    </person>\n" + 
        "    <person id=\"new.id\">\n" + 
        "        <name>\n" + 
        "            <family/>\n" + 
        "            <given/>\n" + 
        "        </name>\n" + 
        "        <email/>\n" + 
        "        <link/>\n" + 
        "    </person>\n" + 
        "    <person id=\"one.worker\">\n" + 
        "        <name>\n" + 
        "            <family>Worker</family>\n" + 
        "            <given>One</given>\n" + 
        "        </name>\n" + 
        "        <email>one@oxygenxml.com</email>\n" + 
        "        <link manager=\"Big.Boss\"/>\n" + 
        "    </person>\n" + 
        "</personnel>\n" + 
        "", document);
  }

  /**
   * <p>This is actually a test for WSAuthorEditorPageBase.goToNextEditablePosition(int)</p>
   * 
   * <p><b>Description:</b> After inserting a fragment we will position the user
   * inside the first editable position:
   * - the first leaf element.
   * - the first editor.
   * </p>
   * <p><b>Bug ID:</b> EXM-25185</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testFindEditLocation_InsideLeaf() throws Exception {
    open(URLUtil.correct(new File("test/EXM-14621/personal/personal2.xml")), false, false);
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    AuthorDocumentImpl authorDocument = viewport.getController().getAuthorDocument();
    Content content = authorDocument.getContent();
    String string = content.getString(0, content.getLength());
    string = string.replace((char) 0, 'X');
  
    // Move at the end of the first person.
    AuthorParentNode person = (AuthorParentNode) authorDocument.getRootElement().getContentNodes().get(0);
    AuthorParentNode nameNode = (AuthorParentNode) person.getContentNodes().get(0);
    AuthorNode familyNode = nameNode.getContentNodes().get(0);
    
    moveCaret(nameNode.getStartOffset());
    flushAWTBetter();
    viewport.getAuthorAccess().getEditorAccess().goToNextEditablePosition(viewport.getCaretOffset(), -1);
    assertEquals("The first editable position must be inside ", familyNode.getStartOffset() + 1, viewport.getCaretOffset());
    
    moveCaret(person.getStartOffset());
    flushAWTBetter();
    viewport.getAuthorAccess().getEditorAccess().goToNextEditablePosition(viewport.getCaretOffset(), -1);
    assertEquals("The first editable position must be inside ", person.getStartOffset() + 1, viewport.getCaretOffset());
    assertTrue("We must be editing in place", viewport.isEditingInplace());
    assertEquals("Node: <person id=\"Big.Boss\" , contr=\"false\" ,> (55, 92), content Editor args: [check, contr, @contr, Yes], idx 0", viewport.getHost().getInplaceEditorHelper().getEditingContext().toString());
    
    viewport.getAuthorAccess().getEditorAccess().goToNextEditablePosition(viewport.getCaretOffset(), -1);
    assertEquals("The first editable position must be inside ", person.getStartOffset() + 1, viewport.getCaretOffset());
    assertTrue("We must be editing in place", viewport.isEditingInplace());
    assertEquals("Node: <person id=\"Big.Boss\" , contr=\"false\" ,> (55, 92), content Editor args: [text, id, @id, 15], idx 1", viewport.getHost().getInplaceEditorHelper().getEditingContext().toString());
  }

  /**
   * <p><b>Description:</b> After inserting a fragment we will position the user
   * inside the first editable position. The next editable position must not exceed
   * the inserted fragment interval.</p>
   * <p><b>Bug ID:</b> EXM-25521</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testFindEditLocation_TextOnlyFragment() throws Exception {
    open(URLUtil.correct(new File("test/EXM-14621/personal/personal2.xml")), false, false);
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    AuthorDocumentImpl authorDocument = viewport.getController().getAuthorDocument();
    
    final InsertFragmentOperation op = new InsertFragmentOperation();
    
    Map userValues = new HashMap();
    userValues.put("fragment", "text");
    userValues.put("insertLocation", "//family[1]");
    userValues.put("insertPosition", AuthorConstants.POSITION_INSIDE_LAST);
    userValues.put("goToNextEditablePosition", AuthorConstants.ARG_VALUE_TRUE);
    userValues.put(AuthorOperation.SCHEMA_AWARE_ARGUMENT, AuthorConstants.ARG_VALUE_FALSE);
    
    
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
        PrettyPrintAnalyser.getIndentOptions(editor), false);
    document = document.replaceAll("id=\"person_([^\"]*)\"", "id=\"new.id\"");
    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE personnel PUBLIC \"PERSONNEL\" \"../../personal.dtd\">\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"personal2.css\"?>\n" + 
        "<personnel>\n" + 
        "    <person id=\"Big.Boss\">\n" + 
        "        <name>\n" + 
        "            <family>Bosstext</family>\n" + 
        "            <given>Big</given>\n" + 
        "        </name>\n" + 
        "        <email>chief@oxygenxml.com</email>\n" + 
        "        <link subordinates=\"one.worker\"/>\n" + 
        "    </person>\n" + 
        "    <person id=\"one.worker\">\n" + 
        "        <name>\n" + 
        "            <family>Worker</family>\n" + 
        "            <given>One</given>\n" + 
        "        </name>\n" + 
        "        <email>one@oxygenxml.com</email>\n" + 
        "        <link manager=\"Big.Boss\"/>\n" + 
        "    </person>\n" + 
        "</personnel>\n" + 
        "", document);
    
    Content content = authorDocument.getContent();
    String string = content.getString(0, content.getLength());
    string = string.replace((char) 0, 'X');
  
    assertEquals(string.indexOf("Bosstext") + "Bosstext".length(), vViewport.getCaretOffset());
  }

  /**
   * <p><b>Description:</b> After inserting a fragment we will position the user
   * inside the first editable position. The next editable position must not exceed
   * the inserted fragment interval and should work properly if we have an insert location.</p>
   * <p><b>Bug ID:</b> EXM-25521</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testFindEditLocation_WithInsertLocation() throws Exception {
    open(URLUtil.correct(new File("test/EXM-14621/personal/personal2.xml")), false, false);
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    AuthorDocumentImpl authorDocument = viewport.getController().getAuthorDocument();
    
    final InsertFragmentOperation op = new InsertFragmentOperation();
    
    Map userValues = new HashMap();
    userValues.put("fragment", "<a>text12</a><b>test22</b>");
    userValues.put("insertLocation", "//family[1]");
    userValues.put("insertPosition", AuthorConstants.POSITION_INSIDE_FIRST);
    userValues.put("goToNextEditablePosition", AuthorConstants.ARG_VALUE_TRUE);
    userValues.put(AuthorOperation.SCHEMA_AWARE_ARGUMENT, AuthorConstants.ARG_VALUE_FALSE);
    
    
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
        PrettyPrintAnalyser.getIndentOptions(editor), false);
    document = document.replaceAll("id=\"person_([^\"]*)\"", "id=\"new.id\"");
    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE personnel PUBLIC \"PERSONNEL\" \"../../personal.dtd\">\n" + 
        "<?xml-stylesheet type=\"text/css\" href=\"personal2.css\"?>\n" + 
        "<personnel>\n" + 
        "    <person id=\"Big.Boss\">\n" + 
        "        <name>\n" + 
        "            <family><a>text12</a><b>test22</b>Boss</family>\n" + 
        "            <given>Big</given>\n" + 
        "        </name>\n" + 
        "        <email>chief@oxygenxml.com</email>\n" + 
        "        <link subordinates=\"one.worker\"/>\n" + 
        "    </person>\n" + 
        "    <person id=\"one.worker\">\n" + 
        "        <name>\n" + 
        "            <family>Worker</family>\n" + 
        "            <given>One</given>\n" + 
        "        </name>\n" + 
        "        <email>one@oxygenxml.com</email>\n" + 
        "        <link manager=\"Big.Boss\"/>\n" + 
        "    </person>\n" + 
        "</personnel>\n" + 
        "", document);
    
    Content content = authorDocument.getContent();
    String string = content.getString(0, content.getLength());
    string = string.replace((char) 0, 'X');
  
    assertEquals(string.indexOf("text12"), vViewport.getCaretOffset());
  }
}
