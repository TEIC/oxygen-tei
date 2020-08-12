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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.webapp.testing.MockAuthorDocumentFactory;

/**
 * @author costi_dumitrescu
 *
 */
public class InsertOrReplaceFragmentOperationTest {

  /**
   * <p><b>Description:</b> Test the replace mode of the operation.</p>
   * <p><b>Bug ID:</b> EXM-33410</p>
   *
   * @author costi_dumitrescu
   *
   * @throws Exception
   */
  @Test
  public void testReplaceOperationInWebapp() throws Exception {
   String xmlContent =
       "<article xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\">" + 
       "    <title>MathML</title>" + 
       "    <para><b><i>ceva</i></b></para>" + 
       "</article>";
    String cssContent = "";
    AuthorDocumentModel documentModel = MockAuthorDocumentFactory.create(xmlContent, cssContent);
    AuthorDocumentController controller = documentModel.getAuthorDocumentController();
    
    // Place the caret inside the 'i' node.
    AuthorNode[] is = controller.findNodesByXPath("//i", true, true, true);
    assertEquals(1, is.length);
    documentModel.getSelectionModel().moveTo(is[0].getStartOffset() + 1);
    
    // Replace the fragment: '<b><i>ceva</i></b>'
    Map<String, Object> args = new HashMap<String, Object>();
    args.put(InsertOrReplaceFragmentOperation.ARGUMENT_FRAGMENT, "<c><d>altceva</d></c>");
    args.put(InsertOrReplaceFragmentOperation.ARGUMENT_XPATH_LOCATION, 
        "(./ancestor-or-self::*[local-name() = 'b'])[1]");
    args.put(InsertOrReplaceFragmentOperation.ARGUMENT_RELATIVE_LOCATION, 
        InsertOrReplaceFragmentOperation.POSITION_REPLACE);
    args.put(InsertOrReplaceFragmentOperation.SCHEMA_AWARE_ARGUMENT, "false");
    
    documentModel.getActionsManager().invokeOperation(InsertOrReplaceFragmentOperation.class.getName(), args, -1);
    
    // Assert that it was properly replaced.
    AuthorNode[] bs = controller.findNodesByXPath("//b", true, true, true); 
    assertEquals(0, bs.length);
    
    AuthorNode[] cs = controller.findNodesByXPath("//*[local-name() = 'c']", true, true, true);
    assertEquals(1, cs.length);
    List<AuthorNode> csChildren = ((AuthorElement)cs[0]).getContentNodes();
    assertEquals(1, csChildren.size());
    assertEquals("d", csChildren.get(0).getDisplayName());
    assertEquals("altceva", csChildren.get(0).getTextContent());
  }
}
