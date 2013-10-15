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

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResultInsertConstants;

/**
 * Util methods for common Author operations.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class CommonsOperationsUtil {

  /**
   * Unwrap node tags.
   * 
   * @param authorAccess The Author access.
   * @param nodeToUnwrap The node to unwrap.
   * 
   * @throws BadLocationException
   */
  public static void unwrapTags(AuthorAccess authorAccess, AuthorNode nodeToUnwrap)
  throws BadLocationException {
    // Unwrap the node
    if (nodeToUnwrap != null && 
        (nodeToUnwrap != authorAccess.getDocumentController().getAuthorDocumentNode().getRootElement())) {
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      int nodeStartOffset = nodeToUnwrap.getStartOffset();
      int nodeEndOffset = nodeToUnwrap.getEndOffset();
      int nextOffset;
      
      
      if (caretOffset <= nodeStartOffset) {
        // The caret offset remains the same
        nextOffset = caretOffset;
      } else if (caretOffset > nodeStartOffset && caretOffset <= nodeEndOffset){
        // The caret offset must be moved to the left 
        nextOffset = caretOffset - 1;
      } else {
        // The caret offset must be moved to the left 
        nextOffset = caretOffset - 2;
      }
      
      //Strip the tags of the node
      //Copy its content first.
      int contentStart = nodeToUnwrap.getStartOffset() + 1;
      int contentEnd = nodeToUnwrap.getEndOffset() - 1;

      AuthorDocumentFragment unwrapped = null;
      if (contentStart <= contentEnd) {
        // Create a fragment from the node content
        unwrapped = authorAccess.getDocumentController().createDocumentFragment(contentStart, contentEnd);
      }

      // Remove the entire node
      boolean deleteNode = authorAccess.getDocumentController().deleteNode(nodeToUnwrap);
      if (deleteNode && unwrapped != null) {
        // Add the content
        authorAccess.getDocumentController().insertFragment(nodeStartOffset, unwrapped);
        // Update the caret position
        authorAccess.getEditorAccess().setCaretPosition(nextOffset);
      }
    }
  }
  
  /**
   * Surround selection with fragment.
   * 
   * @param authorAccess Author access.
   * @param schemaAware <code>true</code> for schema aware operation
   * @param xmlFragment The xml fragment
   * @throws AuthorOperationException
   */
  public static void surroundWithFragment(
      AuthorAccess authorAccess, boolean schemaAware, String xmlFragment)
  throws AuthorOperationException {
    //The XML may contain an editor template for caret positioning.
    boolean moveCaretToSpecifiedPosition =
      MoveCaretUtil.hasImposedEditorVariableCaretOffset(xmlFragment);
    int insertionOffset = authorAccess.getEditorAccess().getCaretOffset();

    if (authorAccess.getEditorAccess().hasSelection()) {
      // We have selection. Do a simple insert.
      insertionOffset = surroundWithFragment(authorAccess, xmlFragment, 
          authorAccess.getEditorAccess().getSelectionStart(), 
          authorAccess.getEditorAccess().getSelectionEnd() - 1); 
    } else {
      // No selection. Schema aware insertion can be performed.
      if (!schemaAware) {
        authorAccess.getDocumentController().insertXMLFragment(xmlFragment, insertionOffset);
      } else {
        // There is no XPath and no selection, do insert.
        SchemaAwareHandlerResult result =
          authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
              xmlFragment, insertionOffset);

        //Keep the insertion offset.
        if (result != null) {
          Integer off = (Integer) result.getResult(
              SchemaAwareHandlerResultInsertConstants.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET);
          if (off != null) {
            insertionOffset = off.intValue(); 
          }
        }
      }
    }

    if (moveCaretToSpecifiedPosition) {
      //Detect the position in the Author page where the caret should be placed.
      MoveCaretUtil.moveCaretToImposedEditorVariableOffset(authorAccess, insertionOffset);
    }
  }

  /**
   * Surround the content between start and end offset with the given fragment.
   * 
   * @param authorAccess Author access.
   * @param xmlFragment The xml fragment
   * @param start The start offset.
   * @param end The end offset.
   * @return Insertion offset.
   * @throws AuthorOperationException
   */
  public static int surroundWithFragment(AuthorAccess authorAccess, String xmlFragment, int start, int end)
    throws AuthorOperationException {
    // We have selection. Do a simple insert.
    authorAccess.getDocumentController().surroundInFragment(
        xmlFragment, 
        start,
        end);

    //Modify the offset to be used for restoring the caret position.
    return authorAccess.getEditorAccess().getSelectionStart();
  }
}
