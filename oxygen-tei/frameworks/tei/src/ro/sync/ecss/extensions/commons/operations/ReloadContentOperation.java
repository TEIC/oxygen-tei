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
package ro.sync.ecss.extensions.commons.operations;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;

/**
 * Reloads the content of the editor by reading again from the URL used to open it.
 * 
 * @since 19
 *
 * @author cristi_talau
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class ReloadContentOperation implements AuthorOperation {

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Reloads the content of the editor by reading again from the URL used to open it";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    String docUrl = authorAccess.getDocumentController().getAuthorDocumentNode().getSystemID();
    // Record the caret position before the reload.
    int caretBefore = authorAccess.getEditorAccess().getAuthorSelectionModel().getSelectionInterval().getEndOffset();
    
    URL systemIdUrl;
    Reader docReader;
    try {
      systemIdUrl = new URL(docUrl);
      docReader = authorAccess.getUtilAccess().createReader(systemIdUrl, "UTF-8");
      try {
        authorAccess.getEditorAccess().reloadContent(docReader, false);
      } finally {
        try {
          docReader.close();
        } catch (IOException e) {
          // Ignore it.
        }
      }
    } catch (IOException e) {
      throw new AuthorOperationException("Could not read the content of the editor from its URL: " + docUrl, e);
    }
    
    // Try to maintain the caret around the same position as before the reload.
    int endOffset = authorAccess.getDocumentController().getAuthorDocumentNode().getEndOffset();
    if (caretBefore >= endOffset) {
      caretBefore = endOffset - 1;
    }
    authorAccess.getEditorAccess().getAuthorSelectionModel().setSelection(caretBefore, caretBefore);
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[0];
  }

}
