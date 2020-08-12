/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2011 Syncro Soft SRL, Romania.  All rights
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

import javax.swing.Action;

import ro.sync.ecss.common.CommonAccess;
import ro.sync.ecss.common.CommonAccessCustomizer;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.exml.xinclude.XIncludeInfo;
import ro.sync.util.URLUtil;

/**
 *  Test for insert xinclude operation.
 * @author radu_coravu
 */
public class InsertXIncludeOperationTest extends EditorAuthorExtensionTestBase {
  
  private XIncludeInfo result = null;
  
  /**
   * <p><b>Description:</b> Insert XInclude</p>
   * <p><b>Bug ID:</b> EXM-20746</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testInsertXInclude() throws Exception {
    CommonAccess.setCommonAccessCustomizer(new CommonAccessCustomizer() {
      
      @Override
      public XIncludeInfo chooseXInclude(AuthorAccess authorAccess) {
        return result;
      }
    });
    open(URLUtil.correct(new File("test/EXM-20746/section1.xml")), true);
    
    // Go between paragraphs
    moveCaretRelativeTo("dockbookx.dtd.", "dockbookx.dtd.".length() + 1);
    
    // Apply the action
    Action insertXIncludeAction = getDinamicToolbarAction("Insert XInclude...");
    result = new XIncludeInfo("test.xml", "id1", true, true);
    invokeAction(insertXIncludeAction);
    
    assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<sect1 xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\">\n" + 
    		"    <title>First section</title>\n" + 
    		"    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
    		"    with the dockbookx.dtd.</para>\n" + 
    		"    <xi:include href=\"test.xml\" xpointer=\"id1\" xmlns:xi=\"http://www.w3.org/2001/XInclude\">\n" + 
    		"        <xi:fallback/>\n" + 
    		"    </xi:include>\n" + 
    		"    <para>Text</para>\n" + 
    		"</sect1>\n" + 
    		"");
  }

  /**
   * <p><b>Description:</b> Insert XInclude</p>
   * <p><b>Bug ID:</b> EXM-20746</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testInsertXInclude_ReusePrefix() throws Exception {
    CommonAccess.setCommonAccessCustomizer(new CommonAccessCustomizer() {

      @Override
      public XIncludeInfo chooseXInclude(AuthorAccess authorAccess) {
        return result;
      }
    });
    open(URLUtil.correct(new File("test/EXM-20746/section2.xml")), true);
    
    // Go between paragraphs
    moveCaretRelativeTo("dockbookx.dtd.", "dockbookx.dtd.".length() + 1);
    
    // Apply the action
    Action insertXIncludeAction = getDinamicToolbarAction("Insert XInclude...");
    result = new XIncludeInfo("test.xml", "id1", true, true);
    invokeAction(insertXIncludeAction);
    
    assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<sect1 xmlns:x=\"http://www.w3.org/2001/XInclude\" xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\">\n" + 
    		"    <title>First section</title>\n" + 
    		"    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in conformity\n" + 
    		"    with the dockbookx.dtd.</para>\n" + 
    		"    <x:include href=\"test.xml\" xpointer=\"id1\">\n" + 
    		"        <x:fallback/>\n" + 
    		"    </x:include>\n" + 
    		"    <para>Text</para>\n" + 
    		"</sect1>\n" + 
    		"");
  }

  /**
   * <p><b>Description:</b> Insert XInclude</p>
   * <p><b>Bug ID:</b> EXM-20746</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testInsertXInclude_DB4() throws Exception {
    CommonAccess.setCommonAccessCustomizer(new CommonAccessCustomizer() {
      
      @Override
      public XIncludeInfo chooseXInclude(AuthorAccess authorAccess) {
        return result;
      }
    });
    open(URLUtil.correct(new File("test/EXM-20746/section1_db4.xml")), true);
    
    // Go between paragraphs
    moveCaretRelativeTo("dockbookx.dtd.", "dockbookx.dtd.".length() + 1);
    
    // Apply the action
    Action insertXIncludeAction = getDinamicToolbarAction("Insert XInclude...");
    result = new XIncludeInfo("test.xml", "id1", true, true);
    invokeAction(insertXIncludeAction);
    
    assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE sect1 PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
    		"                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
    		"<sect1>\n" + 
    		"    <title>First section</title>\n" + 
    		"    <para>This is a sample showing that &lt;oXygen/&gt; can be used to edit documents in\n" + 
    		"        conformity with the dockbookx.dtd.</para>\n" + 
    		"    <xi:include href=\"test.xml\" xpointer=\"id1\" xmlns:xi=\"http://www.w3.org/2001/XInclude\">\n" + 
    		"        <xi:fallback/>\n" + 
    		"    </xi:include>\n" + 
    		"</sect1>\n" + 
    		"");
  }

}