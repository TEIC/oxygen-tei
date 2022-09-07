/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2022 Syncro Soft SRL, Romania.  All rights
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
import java.io.StringReader;
import java.net.URL;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.basic.io.IOUtil;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorOperationWithCustomUndoBehavior;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;

/**
 * Reloads the content of the editor by reading again from the URL used to open it.
 * 
 * @since 19
 *
 * @author cristi_talau
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class ReloadContentOperation implements AuthorOperation, AuthorOperationWithCustomUndoBehavior {

  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ReloadContentOperation.class.getName());

  /**
   * The name of the arguments that is used to control the modified status of the editor 
   * after the operation is finished.
   */
  static final String ARGUMENT_MARK_AS_NOT_MODIFIED = "markAsNotModified";
  
  /**
   * The name of the arguments that is used to control whether we force reloading the document 
   * even if the content did not change.
   */
  static final String ARGUMENT_FORCED = "forced";

  /**
   * The name of the arguments that is used to control whether we discard all undo-able edits 
   * after the reload.
   */
  static final String ARGUMENT_DISCARD_UNDOABLE_EDITS = "discard_undoable_edits";
  
  /**
   * The arguments of the operation.
   */
  private static ArgumentDescriptor[] arguments = new ArgumentDescriptor[]{
    new ArgumentDescriptor(
      ARGUMENT_MARK_AS_NOT_MODIFIED, 
      ArgumentDescriptor.TYPE_STRING, 
      "After reloading the content the editor may appear as modified. Sometimes, the content is already "
      + "present on the file server, so the user should not save it again. This flags can be used in these "
      + "cases to prevent the editor from showing as modified",
      new String[] {
        AuthorConstants.ARG_VALUE_TRUE,
        AuthorConstants.ARG_VALUE_FALSE,
      }, 
      AuthorConstants.ARG_VALUE_FALSE
    ),
    new ArgumentDescriptor(
        ARGUMENT_FORCED, 
        ArgumentDescriptor.TYPE_STRING, 
        "Control whether we force reloading the document even if the content did not change.",
        new String[] {
          AuthorConstants.ARG_VALUE_TRUE,
          AuthorConstants.ARG_VALUE_FALSE,
        }, 
        AuthorConstants.ARG_VALUE_TRUE
      ),
    new ArgumentDescriptor(
        ARGUMENT_DISCARD_UNDOABLE_EDITS, 
        ArgumentDescriptor.TYPE_STRING, 
        "Control whether we discard undoable edits.",
        new String[] {
          AuthorConstants.ARG_VALUE_TRUE,
          AuthorConstants.ARG_VALUE_FALSE,
        }, 
        AuthorConstants.ARG_VALUE_FALSE
      )
  };
  
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
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    // Record the caret position before the reload.
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    int caretBefore = editorAccess.getAuthorSelectionModel().getSelectionInterval().getEndOffset(); //NOSONAR java:S1941 this can be modified
    
    URL systemIdUrl = editorAccess.getEditorLocation();
    Reader contentReader = null;
    boolean modified = true;
    try {
      contentReader = authorAccess.getUtilAccess().createReader(systemIdUrl, "UTF-8");
      if (isArgumentTrue(args, ARGUMENT_FORCED)) {
        editorAccess.reloadContent(contentReader, isArgumentTrue(args, ARGUMENT_DISCARD_UNDOABLE_EDITS));
      } else {
        modified = reloadFromReaderIfModified(editorAccess, contentReader);
      }
    } catch (IOException e) {
      throw new AuthorOperationException("Could not read the content of the editor from its URL: " + systemIdUrl, e);
    } finally {
      if (contentReader != null) {
        try {
          contentReader.close();
        } catch (IOException e) {
          // Ignore
          LOGGER.debug("Could not close reader.", e);
        }
      }
    }
    
    if (isArgumentTrue(args, ARGUMENT_MARK_AS_NOT_MODIFIED)) {
      authorAccess.getEditorAccess().setModified(false);
    }
    if(modified) {
      // Try to maintain the caret around the same position as before the reload.
      int endOffset = authorAccess.getDocumentController().getAuthorDocumentNode().getEndOffset();
      if (caretBefore >= endOffset) {
        caretBefore = endOffset - 1;
      }
      authorAccess.getEditorAccess().getAuthorSelectionModel().setSelection(caretBefore, caretBefore);
    }
  }

  /**
   * @param args The arguments map.
   * @param argumentName The argument name.
   * @return <code>true</code> if the argument is either the "true" string or the boolean <code>true</code>.
   */
  private static boolean isArgumentTrue(ArgumentsMap args, String argumentName) {
    Object argumentValue = args.getArgumentValue(argumentName);
    return Boolean.TRUE.equals(argumentValue) || 
        AuthorConstants.ARG_VALUE_TRUE.equals(argumentValue);
  }
  
  /**
   * Reloads the document from the given reader.
   * 
   * @param editorAccess The editor access.
   * @param contentReader The reader.
   * 
   * @throws IOException If reading the content fails.
   */
  private static boolean reloadFromReaderIfModified(AuthorEditorAccess editorAccess, Reader contentReader) throws IOException {
    String contentFromUrl = IOUtil.read(contentReader).toString();
    String contentFromDoc = IOUtil.read(editorAccess.createContentReader()).toString();
    boolean modified = true;
    if (!contentFromUrl.equals(contentFromDoc)) {
      try (StringReader cachedReader = new StringReader(contentFromUrl)) {
        editorAccess.reloadContent(cachedReader, false);
      }
    } else {
      modified = false;
    }
    
    return modified;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }

}
