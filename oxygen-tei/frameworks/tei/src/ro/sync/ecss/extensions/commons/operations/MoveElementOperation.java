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

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.AuthorNodeUtil;

/**
 * Flexible operation for moving an element to another location. XPath expressions
 * are used to identify the source element and the target location.
 * 
 * @author alex_jitianu
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class MoveElementOperation implements AuthorOperation {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(MoveElementOperation.class.getName());
  
  /**
   * An XPath expression that identifies the content to be moved.
   */
  private static final String ARGUMENT_SOURCE_LOCATION = "sourceLocation";
  /**
   * An XPath expression that identifies the node to be removed. Optional. If missing
   * the same node identified as being moved will be removed.
   */
  private static final String ARGUMENT_DELETE_LOCATION = "deleteLocation";
  /**
   * A string representation of an XML fragment. The moved node will be wrapped
   * in this string before moving it in the destination.
   */
  private static final String ARGUMENT_SURROUND_FRAGMENT_LOCATION = "surroundFragment";
  /**
   * An XPath expression that identifies the location where the node must be moved to.
   */
  private static final String ARGUMENT_TARGET_LOCATION = "targetLocation";
  /**
   * The insert position argument.
   */
  private static final String ARGUMENT_RELATIVE_LOCATION = "insertPosition";
  /**
   * Controls whether the source element is moved entirely or just its content.
   */
  private static final String ARGUMENT_MOVE_ONLY_CONTENT = "moveOnlySourceContentNodes";
  
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor. 
   */
  public MoveElementOperation() {
    arguments = new ArgumentDescriptor[6];
    
    ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_SOURCE_LOCATION,
        ArgumentDescriptor.TYPE_XPATH_EXPRESSION,
        "An XPath expression that identifies the node to be moved. If missing, the node at caret will be moved.");
    arguments[0] = argumentDescriptor;
    
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_MOVE_ONLY_CONTENT,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "Controls whether the source element is moved entirely or just its content "
        + "(the content of a node consists in text, other nodes or both). "
        + "If true, the copied fragment consists in just the content of the node "
        + "identified by the argument 'sourceLocation'. If false, the entire node will be copied.",
        new String[] {
            AuthorConstants.ARG_VALUE_TRUE,
            AuthorConstants.ARG_VALUE_FALSE
        }, 
        AuthorConstants.ARG_VALUE_FALSE);
    arguments[1] = argumentDescriptor;

    
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_DELETE_LOCATION,
        ArgumentDescriptor.TYPE_XPATH_EXPRESSION,
        "An XPath expression that identifies the node to be removed. Optional. "
        + "If missing, the node identified by 'sourceLocation' as being moved will be removed.");
    arguments[2] = argumentDescriptor;
    
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_SURROUND_FRAGMENT_LOCATION,
        ArgumentDescriptor.TYPE_FRAGMENT,
        "A string representation of an XML fragment. The moved node will be wrapped "
        + "in this string before moving it in the destination.");
    arguments[3] = argumentDescriptor;
    
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_TARGET_LOCATION,
        ArgumentDescriptor.TYPE_XPATH_EXPRESSION,
        "An XPath expression that identifies the location where the node must be moved to.");
    arguments[4] = argumentDescriptor;
    
    // Argument defining the relative position to the node obtained from the XPath location.
    argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_RELATIVE_LOCATION, 
          ArgumentDescriptor.TYPE_CONSTANT_LIST,
          "The insert position relative to the node determined by the \"targetLocation\" XPath expression.\n" +
          "Can be: " 
          + AuthorConstants.POSITION_BEFORE + ", " +
          AuthorConstants.POSITION_INSIDE_FIRST + ", " +
          AuthorConstants.POSITION_INSIDE_LAST + " or " +
          AuthorConstants.POSITION_AFTER + ".\n" +
          "Note: If the XPath expression is not defined this argument is ignored",
          new String[] {
              AuthorConstants.POSITION_BEFORE,
              AuthorConstants.POSITION_INSIDE_FIRST,
              AuthorConstants.POSITION_INSIDE_LAST,
              AuthorConstants.POSITION_AFTER,
          }, 
          AuthorConstants.POSITION_INSIDE_FIRST);
    arguments[5] = argumentDescriptor;
  }
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Moves an element to a different location. The element to move as well "
        + "as the destination are provided through XPath expressions.";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    String sourceLocation = (String) args.getArgumentValue(ARGUMENT_SOURCE_LOCATION);
    AuthorNode toMoveNode = null;
    if (sourceLocation != null && sourceLocation.trim().length() > 0) {
      toMoveNode = executeLocationXPath(authorAccess, sourceLocation);
    } else {
      // Defaults to the node at caret.
      int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
      try {
        toMoveNode = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
      } catch (BadLocationException e) {
        // Shouldn't happen.
        logger.error(e, e);
      }
    }
     
    if (logger.isDebugEnabled()) {
      logger.debug("To move " + toMoveNode);
    }
    
    if (toMoveNode != null) {
      AuthorNode toDeleteNode = toMoveNode;
      String toDeleteLocation = (String) args.getArgumentValue(ARGUMENT_DELETE_LOCATION);
      if (toDeleteLocation != null) {
        // We have an explicit node to delete.
        toDeleteNode = executeLocationXPath(authorAccess, toDeleteLocation);
      }

      if (logger.isDebugEnabled()) {
        logger.debug("To delete " + toDeleteNode);
      }

      String targetLocationXPath = (String) args.getArgumentValue(ARGUMENT_TARGET_LOCATION);
      if (targetLocationXPath != null) {
        // Evaluate the expression and obtain the offset of the first node from the result
        String relativePosition = (String) args.getArgumentValue(ARGUMENT_RELATIVE_LOCATION);
        AuthorDocumentController ctrl = authorAccess.getDocumentController();
        try {
          ctrl.beginCompoundEdit();

          // The fragment to move. Created before doing any document altering.
          AuthorDocumentFragment fragmentToMove = null;

          String moveOnlyContent = (String) args.getArgumentValue(ARGUMENT_MOVE_ONLY_CONTENT);
          if (moveOnlyContent == null) {
            // By default, we are copying the entire node.
            moveOnlyContent = AuthorConstants.ARG_VALUE_FALSE;
          }

          if (AuthorConstants.ARG_VALUE_TRUE.equals(moveOnlyContent)) {
            if (toMoveNode.getStartOffset() + 1 != toMoveNode.getEndOffset()) {
              // Not an empty node.
              fragmentToMove = ctrl.createDocumentFragment(toMoveNode.getStartOffset() + 1, toMoveNode.getEndOffset() - 1);
            }
          } else {
            fragmentToMove = ctrl.createDocumentFragment(toMoveNode, true);
          }

          int insertionOffset =
              ctrl.getXPathLocationOffset(
                  targetLocationXPath, relativePosition);
          if (logger.isDebugEnabled()) {
            logger.debug("Insert location for fragment: " + insertionOffset);
          }

          if (insertionOffset != -1) {
            if (toDeleteNode.getStartOffset() < insertionOffset 
                && insertionOffset <= toDeleteNode.getEndOffset()) {
              // The insertion offset in inside the node to delete.
              throw new AuthorOperationException("Trying to move inside the node that will be removed. Node to remove: " 
                  + toDeleteNode + ". Computed insertion offset " + insertionOffset);
            }

            String fragment = (String) args.getArgumentValue(ARGUMENT_SURROUND_FRAGMENT_LOCATION);
            if (fragment != null) {
              // 1. The fragment is optional. Insert the fragment, if any.
              AuthorDocumentFragment xmlFragment = ctrl.createNewDocumentFragmentInContext(fragment, insertionOffset);
              ctrl.insertFragment(insertionOffset, xmlFragment);

              // 1.2 Relocated the insertion offset inside the first leaf of the fragment.
              AuthorNode firstLeaf = AuthorNodeUtil.getFirstLeaf(xmlFragment);
              if (firstLeaf != null) {
                insertionOffset += firstLeaf.getStartOffset() + 1;
              }

              if (logger.isDebugEnabled()) {
                logger.debug("Insert location for moved: " + insertionOffset);
              }
            }

            // 2. Move the node inside the given fragment.
            if (fragmentToMove != null) {
              // Can be null if we are moving the content of an empty node.
              ctrl.insertFragment(insertionOffset, fragmentToMove);
            }

            // 3. Delete the node.
            ctrl.deleteNode(toDeleteNode);
          } else {
            throw new AuthorOperationException("The XPath expression: " + targetLocationXPath + " - doesn't identify any node");
          }
        } catch (BadLocationException e) {
          throw new AuthorOperationException("Unable to move the element because of: ", e);
        } finally {
          ctrl.endCompoundEdit();
        }
      } else {
        throw new AuthorOperationException("The argument 'targetLocation' was not specified.");
      }
    }
  }
  
  /**
   * Executes the given XPath expression and identifies the corresponding author node.
   * 
   * @return The node identified by the XPath expression.
   * 
   * @throws AuthorOperationException If the XPath expression doesn't identify a node. 
   */
  private AuthorNode executeLocationXPath(AuthorAccess authorAccess, String xPathExpression) 
      throws AuthorOperationException {
    AuthorNode toReturn = null;
    AuthorNode[] locatedNodes = authorAccess.getDocumentController().findNodesByXPath(xPathExpression, true, true, true);
    if (locatedNodes.length > 0) {
      if (locatedNodes.length > 1) {
        // Is bad practice to write an XPath expression that identifies multiple nodes.
        logger.warn("More than one nodes identified by: " + xPathExpression + ". Only the first node will be processed");
      }
      
      toReturn = locatedNodes[0];
    } else {
      throw new AuthorOperationException("The XPath expression: " + xPathExpression + " - doesn't identify any node");
    }
    
    return toReturn;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }

}
