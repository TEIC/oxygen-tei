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

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;

import ro.sync.ecss.component.AuthorViewport;
import ro.sync.ecss.component.editor.EditingContext;
import ro.sync.ecss.dom.AuthorDocumentImpl;
import ro.sync.ecss.dom.AuthorSentinelNode;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.Content;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.layout.AbstractLayoutBox;
import ro.sync.ecss.layout.BoxTestBase;
import ro.sync.ecss.layout.BoxTestBase.BoxInfo;
import ro.sync.ecss.layout.StaticEditBox;
import ro.sync.exml.OxygenSystemProperties;
import ro.sync.exml.editor.DocumentTypeRepository;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.options.Options;
import ro.sync.exml.view.graphics.Point;
import ro.sync.util.URLUtil;
import ro.sync.util.editorvars.EditorVariables;

/**
 * Tests for changing an attribute value.
 * 
 * @author alex_jitianu
 */
public class ChangeAttributeOperationTest extends EditorAuthorExtensionTestBase {
  
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
   * <p><b>Description:</b> After changing an attribute value we can choose to 
   * start editing it if it is represented in-place using an editor.</p>
   * <p><b>Bug ID:</b> EXM-25185</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testEditAttributeLocation() throws Exception {
    open(URLUtil.correct(new File("test/EXM-14621/personal/personal2.xml")), false);
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    AuthorDocumentImpl authorDocument = viewport.getController().getAuthorDocument();
    Content content = authorDocument.getContent();
    String string = content.getString(0, content.getLength());
    string = string.replace((char) 0, 'X');

    // Invoke the button form control "Add subordinates"
    AuthorNode personNode = authorDocument.getRootElement().getContentNodes().get(1);
    flushAWTBetter();
    AbstractLayoutBox nearestLayoutBox = vViewport.getNearestLayoutBox((AuthorSentinelNode) personNode);
    BoxInfo boxInfo = BoxTestBase.findBoxInfo(StaticEditBox.class, 4, nearestLayoutBox);
    assertNotNull("A box must be indentified", boxInfo);
    vViewport.edit((StaticEditBox) boxInfo.box, null, new Point(boxInfo.x, boxInfo.y));
    flushAWTBetter();
    // Execute the button.
    sendKey(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), KeyEvent.VK_SPACE);
    
    // Test that we are editing.
    assertTrue("After the insert we must be editing", viewport.isEditingInplace());
    EditingContext editingContext = viewport.getHost().getInplaceEditorHelper().getEditingContext();
    // Make sure we are editing the right attribute.
    assertEquals("Node: <before> (-1, -1) parent element: <link manager=\"Big.Boss\" , subordinates=\"\" ,> (128, 129), content Editor args: [text, @subordinates, 40], idx 1",
        editingContext.toString());
    // Verify the document content. A new attribute should have been inserted.
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
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
        "    <person id=\"one.worker\">\n" + 
        "        <name>\n" + 
        "            <family>Worker</family>\n" + 
        "            <given>One</given>\n" + 
        "        </name>\n" + 
        "        <email>one@oxygenxml.com</email>\n" + 
        "        <link manager=\"Big.Boss\" subordinates=\"\"/>\n" + 
        "    </person>\n" + 
        "</personnel>\n" + 
        "", false);
  }
}
