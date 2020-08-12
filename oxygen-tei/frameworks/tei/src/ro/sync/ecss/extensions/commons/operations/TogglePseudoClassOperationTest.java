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
import ro.sync.ecss.dom.AuthorDocumentImpl;
import ro.sync.ecss.dom.AuthorSentinelNode;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.layout.AbstractLayoutBox;
import ro.sync.ecss.layout.BlockElementBox;
import ro.sync.ecss.layout.BoxTestBase;
import ro.sync.ecss.layout.BoxTestBase.BoxInfo;
import ro.sync.ecss.layout.DumpConfiguration;
import ro.sync.ecss.layout.StaticEditBox;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.view.graphics.Point;
import ro.sync.util.URLUtil;

/**
 * Tests for changing a pseudo class value.
 * 
 * @author dan
 */
public class TogglePseudoClassOperationTest extends EditorAuthorExtensionTestBase {

  /**
   * <p><b>Description:</b> Using the DITA framework to test the operation.
   * The toggle pseudo class operation is used for showing the colspecs.</p>
   * <p><b>Bug ID:</b> EXM-28767</p>
   *
   * @author dan
   *
   * @throws Exception
   */
  public void testTogglePseudoClass() throws Exception {
    
    open(URLUtil.correct(new File("test/EXM-28767/togglePseudoClass.dita")), false, false);
    flushAWTBetter();    
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    AuthorDocumentImpl authorDocument = viewport.getController().getAuthorDocument();

    
    //
    //
    // Colspecs that are not visible.
    //
    //
    DumpConfiguration config = new DumpConfiguration(true);
    config.setReportOffsets(true);
    config.setReportWidth(true);
    config.setReportHeight(false);
    config.setReportX(false);
    config.setReportY(false);
    config.setReportMinimumWidth(false);
    config.setReportMaximumWidth(false);
    
    StringBuilder sb = new StringBuilder();
    vViewport.getRootBox().dump(sb, config, vViewport.createLayoutContext());
    assertEquals(
        "The colspecs should not be visible by default.",
        "", BoxTestBase.grep(sb.toString(), "<colspec"));
    
    
    
    AuthorNode tableNode = authorDocument.getElementsByLocalName("topic")[0].getElementsByLocalName("body")[0].getElementsByLocalName("table")[0];
    AbstractLayoutBox nlBox = vViewport.getNearestLayoutBox((AuthorSentinelNode) tableNode);
    assertEquals("A layout box must be indentified", BlockElementBox.class, nlBox.getClass());

    // Invoke the button form control that displays the table colspecs.
    BoxInfo boxInfo = BoxTestBase.findBoxInfo(StaticEditBox.class, 1, nlBox);
    vViewport.edit((StaticEditBox) boxInfo.box, null, new Point(boxInfo.x, boxInfo.y));
    flushAWTBetter();
    
    // Execute the button.
    sendKey(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), KeyEvent.VK_SPACE);
    flushAWTBetter();
    
    //
    //
    // Colspecs are now visible.
    //
    //
    sb = new StringBuilder();   
    vViewport.getRootBox().dump(sb, config, vViewport.createLayoutContext());
    assertTrue(
        "The colspecs should be now visible",        
        sb.toString().contains("<colspec"));
    
    tableNode = authorDocument.getElementsByLocalName("topic")[0].getElementsByLocalName("body")[0].getElementsByLocalName("table")[0];
    nlBox = vViewport.getNearestLayoutBox((AuthorSentinelNode) tableNode);
    assertEquals("A layout box must be indentified", BlockElementBox.class, nlBox.getClass());

    // Invoke the button form control that displays the table colspecs.
    boxInfo = BoxTestBase.findBoxInfo(StaticEditBox.class, 1, nlBox);
    vViewport.edit((StaticEditBox) boxInfo.box, null, new Point(boxInfo.x, boxInfo.y));
    flushAWTBetter();
    
    // Execute the button.
    sendKey(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), KeyEvent.VK_SPACE);
    flushAWTBetter();
    
    //
    //
    // Again, the colspecs are not visible.
    //
    //
    config = new DumpConfiguration(true);
    config.setReportOffsets(true);
    config.setReportWidth(true);
    sb = new StringBuilder();
    vViewport.getRootBox().dump(sb, config, vViewport.createLayoutContext());
    assertEquals(
        "The colspecs should not be visible by default.",
        "", BoxTestBase.grep(sb.toString(), "<colspec"));
    
  }
}
