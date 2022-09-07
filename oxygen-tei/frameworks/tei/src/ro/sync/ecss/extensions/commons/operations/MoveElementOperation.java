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

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
import ro.sync.ecss.extensions.api.WebappCompatible;
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
@WebappCompatible
public class MoveElementOperation implements AuthorOperation {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(MoveElementOperation.class.getName());
  
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
   * A string representation of an XML fragment. 
   * The moved node will be inserted in the first leaf will be this fragment 
   * and this fragment containing the moved node will be placed at the destination.
   */
  private static final String ARGUMENT_SURROUND_FRAGMENT = "surroundFragment";
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
   * Controls whether the XPaths should be run as if all track changes have been applied or not.
   */
  private static final String ARGUMENT_PROCESS_CHANGE_MARKERS = "processTrackedChangesForXPathLocations";
  
  /**
   * This parameter controls if the changes must be preserved in the moved content, regardless of the track changes state
   */
  private static final String ARGUMENT_ALWAYS_PRESERVE_TRACKED_CHANGES_IN_MOVED_CONTENT = "alwaysPreserveTrackedChangesInMovedContent";
  
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor. 
   */
  public MoveElementOperation() {
    arguments = new ArgumentDescriptor[8];
    
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
        ARGUMENT_SURROUND_FRAGMENT,
        ArgumentDescriptor.TYPE_FRAGMENT,
        "A string representation of an XML fragment. The moved node will be inserted in the first leaf will be this fragment "
        + "and this fragment containing the moved node will be placed at the destination.");
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
    
    // Process track changes when finding nodes using XPath.
    argumentDescriptor = new ArgumentDescriptor(
            ARGUMENT_PROCESS_CHANGE_MARKERS,
            ArgumentDescriptor.TYPE_CONSTANT_LIST,
            "When nodes are located via XPath, if you have nodes deleted with change tracking in the document "
            + "they are considered as being present by default.\n"
            + "But if you set this argument to 'true', the nodes "
            + "deleted with track changes will be ignored when the xpath locations are computed. ",
                new String[] {
                    AuthorConstants.ARG_VALUE_TRUE,
                    AuthorConstants.ARG_VALUE_FALSE
                }, 
                AuthorConstants.ARG_VALUE_FALSE);
    arguments[6] = argumentDescriptor;
    
    // Argument to control if the changes must always be preserved before the insert.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_ALWAYS_PRESERVE_TRACKED_CHANGES_IN_MOVED_CONTENT, 
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "Always preserve track changes in the moved content, regardless of the track changes state",
        new String[] {
            AuthorConstants.ARG_VALUE_TRUE,
            AuthorConstants.ARG_VALUE_FALSE
        }, 
        AuthorConstants.ARG_VALUE_FALSE);
    arguments[7] = argumentDescriptor;
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
      throws AuthorOperationException {
    // True if the moved node should be selected.
    boolean selectNode = false;
    String sourceLocation = (String) args.getArgumentValue(ARGUMENT_SOURCE_LOCATION);
    AuthorNode toMoveNode = null;
    boolean processTrackChanges = AuthorConstants.ARG_VALUE_TRUE.equals(
        args.getArgumentValue(ARGUMENT_PROCESS_CHANGE_MARKERS));

    if (sourceLocation != null && sourceLocation.trim().length() > 0) {
      toMoveNode = executeLocationXPath(authorAccess, sourceLocation, processTrackChanges);
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
    // If the node to be moved is the selected node, we will try to select it
    // after the move operation.
    if (authorAccess.getEditorAccess().getFullySelectedNode() == toMoveNode) {
      selectNode = true;
    }
    if (toMoveNode != null) {
      AuthorNode toDeleteNode = toMoveNode;
      String toDeleteLocation = (String) args.getArgumentValue(ARGUMENT_DELETE_LOCATION);
      if (toDeleteLocation != null 
    		  //EXM-39907 Consider empty value as null
    		  && ! toDeleteLocation.isEmpty()) {
        // We have an explicit node to delete.
        toDeleteNode = executeLocationXPath(authorAccess, toDeleteLocation, processTrackChanges);
      }

      if (logger.isDebugEnabled()) {
        logger.debug("To delete " + toDeleteNode);
      }

      moveNode(toMoveNode, toDeleteNode, selectNode, authorAccess, args);
    }
  }
  
  /**
   * Move the element.
   * 
   * @param toMoveNode     The node to be moved.
   * @param toDeleteNode   The node to be deleted
   * @param selectNode  <code>true</code> if the node should be selected after the move operation
   * @param authorAccess The author access.
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param args The map of arguments.
   * 
   * @throws AuthorOperationException
   */
  private static void moveNode(AuthorNode toMoveNode, AuthorNode toDeleteNode, boolean selectNode,
      AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    String targetLocationXPath = (String) args.getArgumentValue(ARGUMENT_TARGET_LOCATION);
    if (targetLocationXPath != null) {
      // Evaluate the expression and obtain the offset of the first node from the result
      String relativePosition = (String) args.getArgumentValue(ARGUMENT_RELATIVE_LOCATION);
      AuthorDocumentController ctrl = authorAccess.getDocumentController();
      try {
        ctrl.beginCompoundEdit();

        boolean moveOnlyContent = AuthorConstants.ARG_VALUE_TRUE.equals(
            args.getArgumentValue(ARGUMENT_MOVE_ONLY_CONTENT));
        boolean alwaysPreserveTrackedChanges = isAlwaysPreserveTrackChangesTrue(args);
        selectNode = moveOnlyContent ? false : selectNode;
        
        // The fragment to move. Created before doing any document altering.
        AuthorDocumentFragment fragmentToMove = getFragmentToMove(toMoveNode, ctrl, alwaysPreserveTrackedChanges, moveOnlyContent);

        boolean processTrackChanges = AuthorConstants.ARG_VALUE_TRUE.equals(
            args.getArgumentValue(ARGUMENT_PROCESS_CHANGE_MARKERS));
        int insertionOffset = ctrl.getXPathLocationOffset(
                targetLocationXPath, relativePosition, processTrackChanges);
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

          moveFragment(fragmentToMove, insertionOffset, selectNode, toDeleteNode, authorAccess, args);
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
  
  /**
   * Get the fragment to be moved.
   * 
   * @param toMoveNode The context node.
   * @param ctrl Author document controller.
   * @param alwaysPreserveTrackedChanges <code>true</code> to preserve track changes
   * @param moveOnlyContent <code>true</code> if only the content of the node is moved.
   * 
   * @return The Author document fragment.
   * @throws BadLocationException
   */
  private static AuthorDocumentFragment getFragmentToMove(
      AuthorNode toMoveNode, 
      AuthorDocumentController ctrl, 
      boolean alwaysPreserveTrackedChanges, 
      boolean moveOnlyContent) throws BadLocationException {
    AuthorDocumentFragment fragmentToMove = null;
    int nodeStartOffset = toMoveNode.getStartOffset();
    int nodeEndOffset = toMoveNode.getEndOffset();
    
    if (moveOnlyContent) {
      // we cannot select the moved node, because we move its content...
      if (nodeStartOffset + 1 != nodeEndOffset) {
        // Not an empty node.
        if (alwaysPreserveTrackedChanges) {
          fragmentToMove = ctrl.createDocumentFragment(nodeStartOffset + 1, nodeEndOffset - 1, true);
        } else {
          fragmentToMove = ctrl.createDocumentFragment(nodeStartOffset + 1, nodeEndOffset - 1);
        }
      }
    } else if (alwaysPreserveTrackedChanges) {
      fragmentToMove = ctrl.createDocumentFragment(nodeStartOffset, nodeEndOffset, true);
    } else {
      fragmentToMove = ctrl.createDocumentFragment(toMoveNode, true);
    }
    return fragmentToMove;
  }
   
  /**
   * Move fragment.
   * 
   * @param fragmentToMove The fragment to be moved.
   * @param insertionOffset The location where the fragement is moved.
   * @param selectNode  <code>true</code> if the node should be selected after the move operation
   * @param toDeleteNode   The node to be deleted
   * @param authorAccess The author access.
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param args The map of arguments.
   * 
   * @throws AuthorOperationException
   * @throws BadLocationException
   */
  private static void moveFragment(AuthorDocumentFragment fragmentToMove, int insertionOffset,
      boolean selectNode, AuthorNode toDeleteNode, AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException, BadLocationException {
    AuthorDocumentController ctrl = authorAccess.getDocumentController();
    
    boolean toggleTrackChanges = false;
    if (isAlwaysPreserveTrackChangesTrue(args) && authorAccess.getReviewController().isTrackingChanges()) {
      // Deactivate TC
      authorAccess.getReviewController().toggleTrackChanges();
      toggleTrackChanges = true;
    }
    try {
      String fragment = (String) args.getArgumentValue(ARGUMENT_SURROUND_FRAGMENT);
      if (fragment != null) {
        // We add the node in a fragment, so we will not select it anymore
        selectNode = false;
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
    } finally {
      if (toggleTrackChanges) {
        authorAccess.getReviewController().toggleTrackChanges();
      }
    }
    // Keep a position after the insertion offset
    Position insertPosition = ctrl.createPositionInContent(insertionOffset + 1);            
    // 3. Delete the node.
    ctrl.deleteNode(toDeleteNode);
    // Select the moved node if that was selected before the move operation 
    if (selectNode) {
      AuthorNode toSelectNode = ctrl.getNodeAtOffset(insertPosition.getOffset());
      if (toSelectNode != null) {
        authorAccess.getEditorAccess().select(toSelectNode.getStartOffset(), toSelectNode.getEndOffset() + 1);
      }
    } else {
      // Set the caret inside the moved node.
      authorAccess.getEditorAccess().setCaretPosition(insertPosition.getOffset());
    }
  }
  
  /**
   * Check if {@link #ARGUMENT_ALWAYS_PRESERVE_TRACKED_CHANGES_IN_MOVED_CONTENT} argument 
   * value is <code>true</code>.
   * 
   * @param args The arguments.
   * @return <code>true</code> if the value of the argument is "true".
   */
  private static boolean isAlwaysPreserveTrackChangesTrue(ArgumentsMap args) {
    return AuthorConstants.ARG_VALUE_TRUE.equals(
        args.getArgumentValue(ARGUMENT_ALWAYS_PRESERVE_TRACKED_CHANGES_IN_MOVED_CONTENT));
  }
  
  /**
   * Executes the given XPath expression and identifies the corresponding author node.
   * 
   * @param authorAccess Author Access.
   * @param xPathExpression Xpath expression.
   * @param processTrackChanges Process track changes.
   * 
   * @return The node identified by the XPath expression.
   * 
   * @throws AuthorOperationException If the XPath expression doesn't identify a node. 
   */
  private static AuthorNode executeLocationXPath(AuthorAccess authorAccess, String xPathExpression, boolean processTrackChanges) 
      throws AuthorOperationException {
    AuthorNode toReturn = null;
    AuthorNode[] locatedNodes = authorAccess.getDocumentController().findNodesByXPath(xPathExpression, true, true, true, processTrackChanges);
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
