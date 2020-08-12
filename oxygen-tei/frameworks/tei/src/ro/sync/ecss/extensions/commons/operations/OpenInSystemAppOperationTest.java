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

import ro.sync.ecss.dom.AuthorDocumentImpl;
import ro.sync.ecss.extensions.ArgumentsMapImpl;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.layout.BoxTestBase;

/**
 * Open in system application operations tests.
 * 
 * @author alex_jitianu
 */
public class OpenInSystemAppOperationTest extends BoxTestBase {

  /**
   * <p><b>Description:</b> Editor variables must be expanded.</p>
   * <p><b>Bug ID:</b> EXM-33238</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  @SuppressWarnings("null")
  public void testExpandEditorVariables() throws Exception {
    String xml = "<root>A</root>";
    String css = "*{display:block;}";
    
    setDocumentAndStylesheetContents(xml, css);
    
    AuthorDocumentImpl authorDocument = vViewport.getController().getAuthorDocument();
    String content = authorDocument.getText().replace('\0', 'X');
    vViewport.moveTo(content.indexOf("A"));
    
    // We will use the ${system()} editor variable which expands 
    System.setProperty("test_var", "file:/bla.xml");
    
    OpenInSystemAppOperation op = new OpenInSystemAppOperation();
    ArgumentsMapImpl args = new ArgumentsMapImpl();
    args.setArgument(OpenInSystemAppOperation.ARGUMENT_RESOURCE_PATH, "'${system(test_var)}'");
    
    AuthorOperationException thrown = null;
    try {
      op.doOperation(vViewport.getAuthorAccess(), args);
    } catch (AuthorOperationException ex) {
      thrown = ex;
    }
    
    assertNotNull(thrown);
    
    assertEquals("Resource does not exists: file:/bla.xml", thrown.getMessage());
    
  }
}
