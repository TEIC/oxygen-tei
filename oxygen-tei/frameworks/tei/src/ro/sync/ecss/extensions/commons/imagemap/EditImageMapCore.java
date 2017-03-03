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
package ro.sync.ecss.extensions.commons.imagemap;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.imagemap.SupportedFrameworks;


/**
 * Core methods to be used from the operations and from the image map decorators.
 *
 * @author mircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class EditImageMapCore {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(EditImageMapCore.class.getName());
  
  /**
   * Get the fully selected node if any.
   * 
   * @param ctrl          Author document controller.
   * @param selStart      Selection start (inclusive).
   * @param selEnd        Selection end (exclusive).
   * @param hasSelection  <code>true</code> if has selection.
   * 
   * @return The fully selected node, if any. 
   */
  protected AuthorNode getFullySelectedNode(AuthorDocumentController ctrl, int selStart, int selEnd, boolean hasSelection) {
    AuthorNode toReturn = null;
    if (hasSelection) {
      try {
        OffsetInformation info = ctrl.getContentInformationAtOffset(selStart);
        if (info.getPositionType() == OffsetInformation.ON_START_MARKER) {
          // The selection starts in a sentinel.
          // Find the node which has the sentinel
          AuthorNode node = ctrl.getNodeAtOffset(selStart + 1);
          // A node starts where selection starts.
          if (node.getStartOffset() == selStart && node.getEndOffset() == selEnd - 1) {
            // The node ends at selection end, it is fit to return...
            toReturn = node;
          }
        }
      } catch (BadLocationException e) {
        logger.error(e, e);
      }
    }
    return toReturn;
  }
  
  /**
   * Check the current node and its parents for a specified property value. It might be the node 
   * name, an attribute value, etc.
   * 
   * @param authorAccess      The author access.
   * @param interestNode      The node of interest if available when calling the method. If <code>null</code>
   *                          it will be determined from the AuthorAccess, from the caret position.
   * @param properties2Check  The properties to check.
   * @return  The identified node if any.
   * @throws BadLocationException
   */
  protected final AuthorNode findNodeOfInterest(
      AuthorAccess authorAccess, AuthorNode interestNode, String[] properties2Check) throws BadLocationException {
    AuthorDocumentController documentController = authorAccess.getDocumentController();
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    
    AuthorNode nodeToEdit = interestNode;
    if (nodeToEdit == null) {
      nodeToEdit = getFullySelectedNode(
          documentController,
          editorAccess.getSelectionStart(),
          editorAccess.getSelectionEnd(),
          editorAccess.hasSelection());
    }
    if (nodeToEdit == null) {
      // No fully selected node. Search starting from caret.
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      nodeToEdit = documentController.getNodeAtOffset(caretOffset);
    }
    
    while (nodeToEdit != null && !isNodeOfInterest(nodeToEdit,  properties2Check)) {
      nodeToEdit = nodeToEdit.getParent();
    }
    return nodeToEdit;
  }
  
  /**
   * Check if one of the specified properties matches.
   * 
   * @param nodeToEdit        The node to check.
   * @param properties2Check  The array of properties to check.
   * @return <code>true</code> if the node is eligible, <code>false</code> otherwise.
   */
  private boolean isNodeOfInterest(AuthorNode nodeToEdit, String[] properties2Check) {
    if (properties2Check != null && properties2Check.length > 0) {
      for (int i = 0; i < properties2Check.length; i++) {
        if (isNodeOfInterest(nodeToEdit, properties2Check[i])) {
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * Check if the node is of interest.
   * 
   * @param nodeToEdit      The node to edit candidate.
   * @param property2Check  The property value to check.
   * @return  <code>true</code> if the node is eligible, <code>false</code> otherwise.
   */
  protected boolean isNodeOfInterest(AuthorNode nodeToEdit, String property2Check) {
    if (nodeToEdit.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
      return property2Check.equals(((AuthorElement) nodeToEdit).getLocalName());
    }
    return false;
  }
  
  /**
   * Gets the nodes to edit.
   * 
   * @param authorAccess        The Author access.
   * @param interestNode        The node of interest if available when calling the method. If <code>null</code>
   *                            it will be determined from the AuthorAccess, from the caret position.
   * @param doSurroundIfMissing If <code>true</code> the missing part of the image map will be added.
   * @return  The nodes to edit.
   * @throws  BadLocationException
   * @throws  AuthorOperationException
   */
  public abstract AuthorNode[] getNodesOfInterest(
      AuthorAccess authorAccess,
      AuthorNode interestNode,
      boolean doSurroundIfMissing) throws BadLocationException, AuthorOperationException;

  /**
   * Detect the supported framework.
   * 
   * @param namespaceURI  The namespace uri of the element.
   * @return  The supported framework.
   */
  public abstract SupportedFrameworks getSupportedFramework(String namespaceURI);
}