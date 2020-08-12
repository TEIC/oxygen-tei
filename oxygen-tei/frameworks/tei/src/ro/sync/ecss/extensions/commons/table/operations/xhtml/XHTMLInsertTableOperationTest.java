/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2016 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.table.operations.xhtml;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JDialog;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.util.URLUtil;

/** 
 * Test cases for inserting XHTML tables.
 * 
 * @author sorin_carbunaru
 */
public class XHTMLInsertTableOperationTest extends EditorAuthorExtensionTestBase {
  /**
   * <p><b>Description:</b> check the default structure of an XHTML table after inserting it.<p>
   * <p><b>Bug ID:</b> EXM-36625</p>
   * 
   * @throws Exception
   * 
   * @author sorin_carbunaru
   */
  public void testDefaultXHTMLTableStructure() throws Exception {
    // Open the test file
    open(URLUtil.correct(new File("test/bug36625/sampleMinimal.xml")), true);

    // Move caret
    moveCaretRelativeTo("HERE", "HERE".length() + 1);
    
    // Invoke "Insert Table"
    new Thread() {
      @Override
      public void run() {
        invokeActionForID(ACTION_ID_INSERT_TABLE);
      }
    }.start();
    //Wait
    flushAWTBetter();
    
    JDialog dialog = findDialog("Insert Table");
    assertNotNull(dialog);
    
    sendKey(dialog, KeyEvent.VK_ENTER);
    
    // Check result
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE html>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <head xml:lang=\"en\">\n" + 
        "        <title dir=\"ltr\" lang=\"en\">Table</title>\n" + 
        "    </head>\n" + 
        "    <body>\n" + 
        "        <p>HERE</p>\n" + 
        "        <table>\n" + 
        "            <caption></caption>\n" + 
        "            <colgroup>\n" + 
        "                <col />\n" + 
        "                <col />\n" + 
        "            </colgroup>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th></th>\n" + 
        "                    <th></th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td></td>\n" + 
        "                    <td></td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td></td>\n" + 
        "                    <td></td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td></td>\n" + 
        "                    <td></td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n" + 
        "", false);
  }
}
