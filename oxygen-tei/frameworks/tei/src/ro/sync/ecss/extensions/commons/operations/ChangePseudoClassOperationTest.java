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

import ro.sync.ecss.component.AuthorViewport;
import ro.sync.ecss.dom.AuthorDocumentImpl;
import ro.sync.ecss.extensions.ArgumentsMapImpl;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.layout.DumpConfiguration;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.ui.UiUtil;
import ro.sync.util.URLUtil;

/**
 * Tests for changing a pseudo class value.
 * @author radu_coravu
 */
public class ChangePseudoClassOperationTest extends EditorAuthorExtensionTestBase {
  
  /**
   * The arguments map.
   */
  private ArgumentsMapImpl args;
  /**
   * <p><b>Description:</b> Using the DITA framework to test the operation.
   * The toggle pseudo class operation is used for showing the colspecs.</p>
   * <p><b>Bug ID:</b> EXM-30667</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testTogglePseudoClassEXM_30667() throws Exception {
    
    open(URLUtil.correct(new File("test/EXM-28767/togglePseudoClass.dita")), false, false);
    flushAWTBetter();    
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    final AuthorViewport viewport = authorEditorPage.getViewport();

    final ChangePseudoClassesOperation op = new ChangePseudoClassesOperation();
    args = new ArgumentsMapImpl();
    args.setArgument("setLocations", "//row");
    args.setArgument("setPseudoClassNames", "abc def");
    args.setArgument("removeLocations", null);
    args.setArgument("removePseudoClassNames", null);

    //Set the pseudo class names
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(viewport.getAuthorAccess(), args);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    
    //Now check the rows have the pseudo attributes set to them.
    AuthorNode[] rowNodes = viewport.getController().findNodesByXPath("//row", true, true, true);
    assertEquals(2, rowNodes.length);
    assertTrue(((AuthorElement)rowNodes[0]).hasPseudoClass("abc"));
    assertTrue(((AuthorElement)rowNodes[0]).hasPseudoClass("def"));
    assertTrue(((AuthorElement)rowNodes[1]).hasPseudoClass("abc"));
    assertTrue(((AuthorElement)rowNodes[1]).hasPseudoClass("def"));
    
    //Now set a third property and remove one of the existing ones.
    args = new ArgumentsMapImpl();
    args.setArgument("setLocations", "//row");
    args.setArgument("setPseudoClassNames", "hij");
    args.setArgument("removeLocations", "//row");
    args.setArgument("removePseudoClassNames", "def");
    
    //Set the pseudo class names and remove some.
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(viewport.getAuthorAccess(), args);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    
    //Now check the rows have the pseudo attributes set to them.
    rowNodes = viewport.getController().findNodesByXPath("//row", true, true, true);
    assertEquals(2, rowNodes.length);
    assertTrue(((AuthorElement)rowNodes[0]).hasPseudoClass("abc"));
    assertFalse(((AuthorElement)rowNodes[0]).hasPseudoClass("def"));
    assertTrue(((AuthorElement)rowNodes[0]).hasPseudoClass("hij"));
    assertTrue(((AuthorElement)rowNodes[1]).hasPseudoClass("abc"));
    assertFalse(((AuthorElement)rowNodes[1]).hasPseudoClass("def"));
    assertTrue(((AuthorElement)rowNodes[1]).hasPseudoClass("hij"));
  }
  /**
   * <p><b>Description:</b> Pseudo classes should be case insensitive.</p>
   * <p><b>Bug ID:</b> EXM-30628</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testPseudoClassesSensitivityEXM_30628() throws Exception {
    documentTypeIsRequired = false;
    open(URLUtil.correct(new File("test/EXM-30628/test.xml")), false, false);
    flushAWTBetter();    
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    final AuthorViewport viewport = authorEditorPage.getViewport();
    AuthorDocumentImpl authorDocument = viewport.getController().getAuthorDocument();
  
    final ChangePseudoClassesOperation op = new ChangePseudoClassesOperation();
    args = new ArgumentsMapImpl();
    args.setArgument("setLocations", "//para");
    args.setArgument("setPseudoClassNames", "staticContent");
    args.setArgument("removeLocations", null);
    args.setArgument("removePseudoClassNames", null);
  
    System.err.println("==========================================");
    
    //Set the pseudo class names
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(viewport.getAuthorAccess(), args);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    Thread.sleep(1000);
    
    DumpConfiguration config = new DumpConfiguration(true);
    config.setReportOffsets(false);
    config.setReportWidth(false);
    config.setReportHeight(false);
    config.setReportX(false);
    config.setReportY(false);
    config.setReportMinimumWidth(false);
    config.setReportMaximumWidth(false);
    
    StringBuilder sb = new StringBuilder();
    vViewport.getRootBox().dump(sb, config, vViewport.createLayoutContext());
    assertEquals(
        "Should have the static content activated",
        "<RootBox>[]\n" + 
        "BlockElementBox: <#document>[]\n" + 
        "  BlockElementBox: <root>[]\n" + 
        "    BlockElementBox: <para>[]\n" + 
        "      ParagraphBox[]\n" + 
        "        LineBox: <para>[]\n" + 
        "          InlineStaticContentForElementBox: <para>[]\n" + 
        "            StaticTextBox: 'STATIC'[]\n" + 
        "          DocumentTextBox: 'text'[](Length:4, StartRel:1)\n" + 
        "" + 
        "", sb.toString());
    
    //Now assert the layout.
    documentTypeIsRequired = true;
  }
  /**
   * <p><b>Description:</b> Toggle pseudo attributes without XPath specified.</p>
   * <p><b>Bug ID:</b> EXM-30667</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testTogglePseudoClassEXM_30667_2() throws Exception {
    
    open(URLUtil.correct(new File("test/EXM-28767/togglePseudoClass.dita")), false, false);
    flushAWTBetter();    
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    final AuthorViewport viewport = authorEditorPage.getViewport();
  
    final ChangePseudoClassesOperation op = new ChangePseudoClassesOperation();
    args = new ArgumentsMapImpl();
    //Empty set locations
    args.setArgument("setLocations", "");
    args.setArgument("setPseudoClassNames", "abc def");
    args.setArgument("removeLocations", null);
    args.setArgument("removePseudoClassNames", null);
    AuthorNode titleNode = viewport.getController().findNodesByXPath("//title", true, true, true)[0];
    //Move in title
    vViewport.moveTo(titleNode.getStartOffset() + 1);
  
    //Set the pseudo class names
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(viewport.getAuthorAccess(), args);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    
    assertTrue(((AuthorElement)titleNode).hasPseudoClass("abc"));
    assertTrue(((AuthorElement)titleNode).hasPseudoClass("def"));
  }
}
