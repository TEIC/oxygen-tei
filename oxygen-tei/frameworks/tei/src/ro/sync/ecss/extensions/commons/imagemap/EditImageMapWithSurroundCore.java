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

import java.util.Arrays;

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorNode;


/**
 * Core for the frameworks that need to surround the "image" in an "image map".
 *
 * @author mircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class EditImageMapWithSurroundCore extends EditImageMapCore {
  /**
   * Gets the nodes to edit.
   * 
   * @param authorAccess        The Author access.
   * @param interestNode        The node of interest if available when calling the method. If <code>null</code>
   *                            it will be determined from the AuthorAccess, from the caret position.
   * @param doSurroundIfMissing If <code>true</code> the missing part of the image map will be added.
   * @return  The nodes to edit.
   * @throws BadLocationException
   * @throws AuthorOperationException 
   */
  @Override
  public final AuthorNode[] getNodesOfInterest(
      AuthorAccess authorAccess,
      AuthorNode interestNode,
      boolean doSurroundIfMissing) throws BadLocationException, AuthorOperationException {
    AuthorDocumentController documentController = authorAccess.getDocumentController();
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    
    // Get the criteria to look for when searching for the nodes of interest.
    String[] nodesOfInterestCriteria =
        getNodesOfInterestCriteria(documentController.getAuthorDocumentNode().getRootElement().getNamespace());
    // The properties that describes an image map.
    String[] imageMapProps = Arrays.copyOf(nodesOfInterestCriteria, 1);
    // The properties that describes the image.
    String[] imageProps = Arrays.copyOfRange(nodesOfInterestCriteria, 1, 2);
    
    // Find the image map.
    AuthorNode nodeToEdit = findNodeOfInterest(authorAccess, interestNode, imageMapProps);
    
    // No image map ???
    if (doSurroundIfMissing && nodeToEdit == null) {
      // We don't have a map, maybe we have an image
      nodeToEdit = findNodeOfInterest(authorAccess, interestNode, imageProps);
      if (nodeToEdit != null) {
        String fragStart = nodesOfInterestCriteria[2];
        String fragEnd = nodesOfInterestCriteria[3];
        
        boolean needComplexSurround = needComplexSurround(nodeToEdit);
        
        if (needComplexSurround && nodesOfInterestCriteria.length == 6) {
          fragStart = nodesOfInterestCriteria[4];
          fragEnd = nodesOfInterestCriteria[5];
        }
        
        // Create the fragment from the image node.
        AuthorDocumentFragment frag = documentController.createDocumentFragment(nodeToEdit, true);
        // Delete it.
        documentController.deleteNode(nodeToEdit);
        // Serialize it.
        String fragStr = documentController.serializeFragmentToXML(frag);
        // "Surround" it and put it back.
        documentController.insertXMLFragment(
            fragStart + fragStr + fragEnd,
            editorAccess.getCaretOffset());
        // Re-find the image map.
        nodeToEdit = findNodeOfInterest(authorAccess, interestNode, imageMapProps);
      }
    }
    
    if (nodeToEdit != null) {
      return new AuthorNode[] { nodeToEdit };
    }
    
    return null;
  }
  
  /**
   * Check if the edited node need more complex surrounding.
   * 
   * @param nodeToEdit  The node to edit.
   * @return  <code>true</code> if complex surrounding need to be performed.
   */
  protected boolean needComplexSurround(AuthorNode nodeToEdit) {
    return false;
  }

  /**
   * Get the criteria for nodes of interest, and the start and end of the fragment to surround with.
   * 
   * @param namespace The namespace of the document.
   * @return A 4 items array with the main property, the secondary property, and the start + end of 
   *         the fragment to surround with.
   */
  protected abstract String[] getNodesOfInterestCriteria(String namespace);
}