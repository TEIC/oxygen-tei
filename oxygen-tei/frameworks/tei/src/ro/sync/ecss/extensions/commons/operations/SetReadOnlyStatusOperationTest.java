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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;

import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.WebappMessage;
import ro.sync.ecss.extensions.api.webapp.WebappMessagesProvider;
import ro.sync.ecss.webapp.testing.MockAuthorDocumentFactory;

/**
 * @author cristi_talau
 *
 */
public class SetReadOnlyStatusOperationTest {

  /**
   * <p><b>Description:</b> Test the read-only mode setting.</p>
   * <p><b>Bug ID:</b> EXM-34226</p>
   *
   * @author cristi_talau
   *
   * @throws Exception
   */
  @Test
  public void testReadOnlyStatus() throws Exception {
   String xmlContent =
       "<article xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\">" + 
       "<title>MathML</title>" + 
       "<para><b><i>ceva</i></b></para>" + 
       "</article>";
    String cssContent = "";
    AuthorDocumentModel documentModel = MockAuthorDocumentFactory.create(xmlContent, cssContent);
    AuthorDocumentController controller = documentModel.getAuthorDocumentController();
    WebappMessagesProvider messageProvider = documentModel.getMessageProvider();
    documentModel.getSelectionModel().moveTo(
        controller.getAuthorDocumentNode().getRootElement().getStartOffset() + 2);
    
    // Make the document read-only and then change it.
    documentModel.getActionsManager().invokeOperation(SetReadOnlyStatusOperation.class.getName(), 
        ImmutableMap.<String, Object>of(
            SetReadOnlyStatusOperation.ARGUMENT_READ_ONLY, AuthorConstants.ARG_VALUE_TRUE), 
        -1);
    documentModel.getActionsSupport().insertCharAtCurrentOffset('x');
    assertEquals(xmlContent, CharStreams.toString(documentModel.createReader()));
    assertEquals(1, messageProvider.getMessages().size());
    messageProvider.clearMessages();
    
    // Make the document writable.
    documentModel.getActionsManager().invokeOperation(SetReadOnlyStatusOperation.class.getName(), 
        ImmutableMap.<String, Object>of(
            SetReadOnlyStatusOperation.ARGUMENT_READ_ONLY, AuthorConstants.ARG_VALUE_FALSE), 
        -1);
    documentModel.getActionsSupport().insertCharAtCurrentOffset('x');
    assertEquals(xmlContent.length() + 1, 
        CharStreams.toString(documentModel.createReader()).length());
    assertTrue(messageProvider.getMessages().isEmpty());
  }
  
  
  /**
   * <p><b>Description:</b> Test the read-only reason setting.</p>
   * <p><b>Bug ID:</b> WA-420</p>
   *
   * @author cristi_talau
   *
   * @throws Exception
   */
  @Test
  public void testReadOnlyReasonStatus() throws Exception {
   String xmlContent =
       "<article xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\">" + 
       "<title>MathML</title>" + 
       "<para><b><i>ceva</i></b></para>" + 
       "</article>";
    String cssContent = "";
    AuthorDocumentModel documentModel = MockAuthorDocumentFactory.create(xmlContent, cssContent);
    AuthorDocumentController controller = documentModel.getAuthorDocumentController();
    WebappMessagesProvider messageProvider = documentModel.getMessageProvider();
    documentModel.getSelectionModel().moveTo(
        controller.getAuthorDocumentNode().getRootElement().getStartOffset() + 2);
    
    // Make the document read-only and then change it.
    documentModel.getActionsManager().invokeOperation(SetReadOnlyStatusOperation.class.getName(), 
        ImmutableMap.<String, Object>of(
            SetReadOnlyStatusOperation.ARGUMENT_READ_ONLY, AuthorConstants.ARG_VALUE_TRUE,
            SetReadOnlyStatusOperation.ARGUMENT_READ_ONLY_REASON, "Because"), 
        -1);
    documentModel.getActionsSupport().insertCharAtCurrentOffset('x');
    
    List<WebappMessage> messages = messageProvider.getMessages();
    assertEquals(1, messages.size());
    assertEquals("Because", messages.get(0).getMessage());
    messageProvider.clearMessages();
  }


}
