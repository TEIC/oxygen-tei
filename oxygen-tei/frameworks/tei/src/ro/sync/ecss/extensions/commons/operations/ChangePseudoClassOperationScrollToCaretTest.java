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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;

import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.layout.BoxTestBase;
import ro.sync.ecss.webapp.actions.AuthorActionsManagerImpl;
import ro.sync.exml.editor.persistance.ExtensionClasspath;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.ui.UiUtil;

/**
 * Tests for changing a pseudo class value should not leave the caret outside the visible rectangle.
 * 
 * @author dan
 */
public class ChangePseudoClassOperationScrollToCaretTest extends BoxTestBase {
  
  /**
   * Avoid failing the test because of error messages. 
   * 
   * @see ro.sync.ecss.layout.BoxTestBase#appendMessage(java.lang.String, org.apache.log4j.Level)
   */
  @Override
  public void appendMessage(String message, Level level) {
    //  Avoid failing the test because of error messages. 
  }
  
  /**
   * <p><b>Description:</b> 
   * The pseudo classes change may result in the caret being pushed out of the screen.
   * This test makes sure that after a pseudo class change, the viewport is scrolled so that the caret is visible.</p>
   * <p><b>Bug ID:</b> EXM-30627</p>
   *
   * @author dan
   *
   * @throws Exception
   */
  public void testScrollToCaret() throws Exception {
    
    String xml = 
        "<root>"
        + "<section>"
        + "  <title>Chapter 1</title>"
        + "  <para>long long long long long long long long long long long long long long long long long long long long long text </para>"
        + "</section>"
        + "<section>"
        + "  <title>Chapter 2</title>"
        + "  <para>long long long long long long long long long long long long long long long long long long long long long text </para>"
        + "</section>"
        + "<section>"
        + "  <title>Chapter 3</title>"
        + "  <para>long long long long long long long long long long long long long long long long long long long long long text [X]</para>"
        + "</section>"
        + "</root>";
    
    String css = 
        "* {\n" + 
        "    display:block;\n" + 
        "}\n" + 
        "title {\n" + 
        "    font-weight:bold;\n" + 
        "}\n" + 
        "para:large {\n" + 
        "    margin: 200px;\n" + 
        "}";
    
    setDocumentAndStylesheetContents(xml, css);
    
    String doctxt = document.getText().replace('\0', 'S');    
    int caret = doctxt.indexOf("[X]");
    vViewport.getAuthorAccess().getEditorAccess().setCaretPosition(caret);
    flushAWTBetter();
    
      
    final Map<String, Object> args = new HashMap<String, Object>();
    args.put("setLocations", "//para");
    args.put("setPseudoClassNames", "large");
    args.put("removeLocations", null);
    args.put("removePseudoClassNames", null);

    
    ExtensionClasspath classpath = new ExtensionClasspath(new String[] {""},"");
    final AuthorActionsManagerImpl am = new AuthorActionsManagerImpl(
        vViewport.getAuthorAccess(), "DITA", 
        null, classpath, null, vViewport.getAPISelectionModel(), vViewport.getController());

    //Set the pseudo class names, pushing the caret out of the screen.
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          am.invokeOperation(ChangePseudoClassesOperation.class.getName(), args, -1);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } 
      }
    });
    flushAWTBetter();
    
    Rectangle cRectangle = vViewport.modelToView(caret);    
    java.awt.Rectangle caretRect = new java.awt.Rectangle(cRectangle.x, cRectangle.y, cRectangle.width, cRectangle.height);
    java.awt.Rectangle visibleRect = vComponent.getVisibleRect();
    assertTrue("The visible rectangle " + visibleRect + " should contain the caret rectangle: " + caretRect + ".", 
        visibleRect.contains(caretRect));
  }

}
