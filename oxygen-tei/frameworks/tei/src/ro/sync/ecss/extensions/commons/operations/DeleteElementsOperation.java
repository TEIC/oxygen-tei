/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2009 Syncro Soft SRL, Romania.  All rights
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * An implementation of a delete operation that deletes all the nodes identified by a XPath expression.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class DeleteElementsOperation implements AuthorOperation {
  /**
   * The XPath location that identifies the element.
   * Empty/null for the current element.
   * The value is <code>elementLocation</code>.
   */
  public static final String ARGUMENT_ELEMENT_XPATH_LOCATIONS = "elementLocations";
  
  /**
   * The arguments of the operation.
   */
  protected ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor for the delete element operation.
   */
  public DeleteElementsOperation() {
    arguments = new ArgumentDescriptor[1];    
    // Argument defining the elements that will be modified.
    ArgumentDescriptor argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_ELEMENT_XPATH_LOCATIONS, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating the elements to be deleted.\n"
          + "Note: If it is not defined then the element at the caret position will be used.");
    arguments[0] = argumentDescriptor;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    // The XPath location.
    Object xpathLocation = args.getArgumentValue(ARGUMENT_ELEMENT_XPATH_LOCATIONS);
    AuthorNode[] nodesToDelete = null;
    if (xpathLocation instanceof String && ((String)xpathLocation).trim().length() > 0) {
      nodesToDelete =
        authorAccess.getDocumentController().findNodesByXPath(((String) xpathLocation).trim(), true, true, true);
      if (nodesToDelete.length == 0) {
        throw new AuthorOperationException("The element XPath location does not identify a node: " + xpathLocation);
      }
    } else {
      try {
        nodesToDelete = new AuthorNode[1];
        nodesToDelete[0] = authorAccess.getDocumentController().getNodeAtOffset(
            authorAccess.getEditorAccess().getCaretOffset());
      } catch (BadLocationException e) {
        throw new AuthorOperationException("Cannot identify the current node", e);
      }
    }
    
    if (nodesToDelete.length > 0) {
      // Sort the found nodes by start offset
      Arrays.sort(nodesToDelete, new Comparator<AuthorNode>() {
        @Override
        public int compare(AuthorNode o1, AuthorNode o2) {
          int toRet = 0;
          if (o1.getStartOffset() < o2.getStartOffset()) {
            toRet = - 1;
          } else {
            toRet = 1;
          }
          return toRet;
        }
      });
      
      // It is possible to be nested elements, so select only the top level elements. 
      // By deleting them, the children elements will be also deleted.
      List<AuthorNode> finalNodesToDelete = new ArrayList<AuthorNode>();
      for (int i = 0; i < nodesToDelete.length; i++) {
        boolean isContained = false;
        AuthorNode node = nodesToDelete[i];
        // The array is sort by start offset, so search only inside the previously computed ones 
        for (int j = finalNodesToDelete.size() - 1; j >= 0; j--) {
          // Check if one of the already computed nodes contains the current node
          AuthorNode authorNode = finalNodesToDelete.get(j);
          if (authorNode.getEndOffset() > node.getEndOffset()) {
            isContained = true;
            break;
          }
        }
        
        // If the current node is not child of any other node to delete, add it to the list
        if (!isContained) {
          finalNodesToDelete.add(node);
        }
      }
      
      // List with all the nodes that have as parent the document
      List<AuthorNode> nodesWithDocParent = null;
      // List with all nodes that have as parent an element from the document
      List<AuthorNode> nodesToDel = null;
      for (int i = 0; i < finalNodesToDelete.size(); i++) {
        AuthorNode currentNode = finalNodesToDelete.get(i);
        if (currentNode.getParent() != null && 
            currentNode.getParent().getType() == AuthorNode.NODE_TYPE_DOCUMENT) {
          // The parent is the document
          if (nodesWithDocParent == null) {
            nodesWithDocParent = new ArrayList<AuthorNode>();
          }
          
          nodesWithDocParent.add(currentNode);
        } else if (currentNode.getParent() != null) {
          // The parent is not the document, so it should be included in the list 
          // of nodes that will be delete at same time
          if (nodesToDel == null) {
            nodesToDel = new ArrayList<AuthorNode>();
          }
          
          nodesToDel.add(currentNode);
        }
      }
      
      
      // Delete all the nodes that have as parent the document.
      if (nodesWithDocParent != null) {
        for (int i = 0; i < nodesWithDocParent.size(); i++) {
          authorAccess.getDocumentController().deleteNode(nodesWithDocParent.get(i));
        }
      }
      
      if (nodesToDel != null) {
        // Get the common parent
        AuthorNode commonAncestor;
        // Get the common parent
        commonAncestor = authorAccess.getDocumentController().getCommonAncestor(finalNodesToDelete.toArray(new AuthorNode[0]));
        if (finalNodesToDelete.contains(commonAncestor)) {
          // It is possible that the method returns a node from the list
          commonAncestor = commonAncestor.getParent();
        }

        // Obtain the start and end offsets of the final nodes that will be deleted
        int[] startOffsets = new int[finalNodesToDelete.size()];
        int[] endOffsets = new int[finalNodesToDelete.size()];

        for (int j = 0; j < finalNodesToDelete.size(); j++) {
          AuthorNode node = finalNodesToDelete.get(j);
          startOffsets[j] = node.getStartOffset();
          endOffsets[j] = node.getEndOffset();
        }

        // Delete all the nodes identified by the given XPath expression
        if (commonAncestor != null && commonAncestor instanceof AuthorElement) {
          // A parent to be refreshed was found
          authorAccess.getDocumentController().multipleDelete((AuthorElement) commonAncestor, startOffsets, endOffsets);
        }
      }
    }
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
   */
  @Override
  public String getDescription() {
    return "Deletes the elements specified by an XPath expression or the element at the caret position";
  }
}