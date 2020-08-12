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

import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.util.URLUtil;

/**
 * @author mihaela
 *
 */
public class TableOperationsUtilTest extends EditorAuthorExtensionTestBase {

  /**
   * <p><b>Description:</b> Test that delete comment sentinel does not block the 
   * Author.</p>
   * <p><b>Bug ID:</b> EXM-25410</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testDeleteBeforeRoot1() throws Exception {
    open(URLUtil.correct(new File("test/EXM-25410/test.dita")), true, false);
    
    moveCaretRelativeTo("Comment", "Comment".length());
    
    Component focusOwner = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    sendKey(focusOwner, KeyEvent.VK_DELETE);
    
    assertEquals(initialXML, getXMLContent(true));
    
    moveCaretRelativeTo("After", "After".length());
    
    focusOwner = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    sendKey(focusOwner, KeyEvent.VK_DELETE);
    
    assertEquals(initialXML, getXMLContent(true));
  }
  

  /**
   * <p><b>Description:</b> Test that delete comment sentinel does not block the 
   * Author.</p>
   * <p><b>Bug ID:</b> EXM-25918</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testDeleteCommentStartSentinel() throws Exception {
    open(URLUtil.correct(new File("test/EXM-25410/test1.xml")), true, true);
    
    moveCaretRelativeTo("Comment1", 0);
    
    Component focusOwner = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    sendKey(focusOwner, KeyEvent.VK_BACK_SPACE);
    
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE concept PUBLIC \"-//OASIS//DTD DITA Concept//EN\" \"ditabase.dtd\">\n" + 
    		"<concept id=\"conceptId\">\n" + 
    		" <title></title>\n" + 
    		" <shortdesc></shortdesc>\n" + 
    		"  <prolog>\n" + 
    		"    <metadata>\n" + 
    		"      <keywords>\n" + 
    		"        <indexterm></indexterm>\n" + 
    		"      </keywords>\n" + 
    		"    </metadata>\n" + 
    		"  </prolog>\n" + 
    		" <conbody>\n" + 
    		"   <p><d4p_MathML> Comment1  <m:math display=\"block\" xmlns:m=\"http://www.w3.org/1998/Math/MathML\"/>\n" + 
    		"                <!-- Comment2 -->\n" + 
    		"            </d4p_MathML></p>\n" + 
    		" </conbody>\n" + 
    		"</concept>\n" + 
    		"", getXMLContent(true));
  }
  
  /**
   * <p><b>Description:</b> Test that delete comment sentinel does not block the 
   * Author.</p>
   * <p><b>Bug ID:</b> EXM-25410</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testDeleteBeforeRoot2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-25410/sample.xml")), true, false);
    
    moveCaretRelativeTo("Comment", "Comment".length());
    
    Component focusOwner = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    sendKey(focusOwner, KeyEvent.VK_DELETE);
    
    assertNull(TableOperationsUtil.getTableElementContainingOffset(
        vViewport.getCaretOffset(), 
        vViewport.getAuthorAccess(), 
        "test"));
    assertEquals(initialXML, getXMLContent(true));
    moveCaretRelativeTo("After", "After".length());
    
    focusOwner = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    sendKey(focusOwner, KeyEvent.VK_DELETE);
    
    assertNull(TableOperationsUtil.getTableElementContainingOffset(
        vViewport.getCaretOffset(), 
        vViewport.getAuthorAccess(), 
        "test"));
    assertEquals(initialXML, getXMLContent(true));
    
  }
}