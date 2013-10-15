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

import java.util.Iterator;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.AuthorParentNode;
import ro.sync.util.editorvars.EditorVariables;

/**
 * Utility to detect an editor variable in the Author page and move the caret to that place.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class MoveCaretUtil {
  
  /**
   * Logger for logging. 
   */
  private static final Logger logger = Logger.getLogger(MoveCaretUtil.class.getName());

  /**
   * Check if the imposed editor variable caret offset can be found in the XML fragment.
   *
   * @param xmlFragment The XML fragment.
   *
   * @return <code>true</code> if the imposed editor variable caret offset can be found in the XML
   *         fragment.
   */
  public static boolean hasImposedEditorVariableCaretOffset(String xmlFragment) {
    return xmlFragment.contains(EditorVariables.UNIQUE_CARET_MARKER_FOR_AUTHOR);
  }
  
  /**
   * Move the caret to the offset imposed by a certain editor variable present in the Author page.
   *
   * @param authorAccess    The author access.
   * @param insertionOffset The offset where the operation inserted the XML fragment.
   */
  public static void moveCaretToImposedEditorVariableOffset(
      AuthorAccess authorAccess, int insertionOffset) { 
    AuthorNode caretPI = detectCaretPI(authorAccess.getDocumentController(), insertionOffset);
    if (caretPI != null) {
      int caretOffset = caretPI.getStartOffset();
      authorAccess.getDocumentController().deleteNode(caretPI);
      authorAccess.getEditorAccess().setCaretPosition(caretOffset);
    } else {
      //Defaults to 0 for CT's
    }
  }
  
  /**
   * Detect the editor template offset.
   *
   * @return The editor template offset.
   */
  private static AuthorNode detectCaretPI(AuthorDocumentController ctrl, int startSearch) {
    try {
      return detectCaretPI(ctrl.getNodeAtOffset(startSearch));
    } catch (BadLocationException e) {
      logger.warn(e, e);
    }
    return null;
  }

  /**
   * Detects an unique caret marker processing instruction.
   * 
   * @param node The current node.
   *
   * @throws BadLocationException 
   */
  private static AuthorNode detectCaretPI(AuthorNode node) throws BadLocationException {
    if (node.getType() == AuthorNode.NODE_TYPE_PI
        && node.getTextContent().equals(EditorVariables.UNIQUE_CARET_MARKER_PI_NAME_FOR_AUTHOR)) {
      return node;
    }
    if (node instanceof AuthorParentNode) {
      List<AuthorNode> contentNodes = ((AuthorParentNode)node).getContentNodes();
      for (Iterator<AuthorNode> iterator = contentNodes.iterator(); iterator.hasNext();) {
        AuthorNode detectPI = detectCaretPI(iterator.next());
        if (detectPI != null) {
          return detectPI;
        }
      }
    }
    return null;
  }
}