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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.Segment;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.contentcompletion.xml.ContextElement;
import ro.sync.contentcompletion.xml.WhatElementsCanGoHereContext;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorOperationStoppedByUserException;
import ro.sync.ecss.extensions.api.AuthorSchemaManager;
import ro.sync.ecss.extensions.api.AuthorSelectionModel;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.AuthorNodeUtil;
import ro.sync.ecss.extensions.api.node.AuthorParentNode;

/**
 * Toggle "surround with element" operation.
 * 
 * Case 1: If there is no selection in the document:
 *  - if the caret is inside a word
 * then the word is wrapped in the given element (or unwrapped if it is already included in the element)
 *  - else the element is inserted at caret position. 
 *  
 *  Case 2: If there is a selection, it is wrapped in the given element 
 *  (or unwrapped if it is already included in the element)
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class ToggleSurroundWithElementOperation implements AuthorOperation {
  /**
   * Logger for logging.
   */
  private static final Logger logger =
    Logger.getLogger(ToggleSurroundWithElementOperation.class.getName());
  
  /**
   * The element argument.
   */
  public static final String ARGUMENT_ELEMENT = "element";

  /**
   * The arguments array.
   */
  private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
    new ArgumentDescriptor(
        ARGUMENT_ELEMENT,
        ArgumentDescriptor.TYPE_FRAGMENT,
        "The element to surround with."),
    new ArgumentDescriptor(
        SCHEMA_AWARE_ARGUMENT, 
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "This argument applies only on the surround with element operation and controls if the insertion is schema aware or not. " +
        "When schema aware is enabled and the element insertion is not allowed, a dialog will be shown proposing insertion solutions, like:\n" +
        " - splitting an ancestor of the node at insertion offset and inserting the element between the resulted elements;\n" +
        " - inserting the element somewhere in the proximity of the insertion offset (left or right without skipping content);\n" + 
        " - inserting the element at insertion offset, even it is not allowed.\n\n" +
        "Note: if a selection exists the surround with element operation is not schema aware.\n" + 
        "Possible values are: " 
        + AuthorConstants.ARG_VALUE_TRUE + ", " +
        AuthorConstants.ARG_VALUE_FALSE + ". Default value is " + AuthorConstants.ARG_VALUE_TRUE + ".",
        new String[] {
            AuthorConstants.ARG_VALUE_TRUE,
            AuthorConstants.ARG_VALUE_FALSE,
        }, 
        AuthorConstants.ARG_VALUE_TRUE)
  };
  
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @SuppressWarnings("null")
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    // Surround in element.
    Object elementArg = args.getArgumentValue(ARGUMENT_ELEMENT);
    Object schemaAwareArgumentValue = args.getArgumentValue(SCHEMA_AWARE_ARGUMENT);
    
    if (elementArg != null && elementArg instanceof String && ((String)elementArg).length() > 0) {
      String fragment = (String) elementArg;
      AuthorElement wrapNode = getElementFromFragment(fragment, authorAccess);
      boolean schemaAware = !AuthorConstants.ARG_VALUE_FALSE.equals(schemaAwareArgumentValue);
      authorAccess.getDocumentController().beginCompoundEdit();
      try {
        if (authorAccess.getEditorAccess().hasSelection()) {
            performToggleSelection(authorAccess, fragment, wrapNode, schemaAware);
        } else {
          // No selection. 
          int[] wordAtCaret = authorAccess.getEditorAccess().getWordAtCaret();
          int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
          
          // Determine if the caret is placed inside a node that matches the
          // surround fragment.
          AuthorElement elementAtCaret = getElementAtCaretOffset(authorAccess);
          AuthorElement elementMatchingRef = getElementMatchingReferenceElement(
              elementAtCaret, authorAccess, wrapNode, true);
          
          // Determine if the caret is inside a word
          boolean insideWord = 
                wordAtCaret != null && 
                // The caret is not placed at the word start
                wordAtCaret[0] != caretOffset && 
                // The caret is not placed at the word end
                wordAtCaret[1] != caretOffset;
          
          if (elementMatchingRef != null) {
            // The caret is placed inside a node that matches the surround fragment
            int newCaretOffset = 0;
            if (insideWord) {
              // The caret is placed inside a word that matches the surround fragment
              int newOffsets[] = unwrap(elementMatchingRef, wordAtCaret[0], wordAtCaret[1] - 1, authorAccess);
              int currentOffset = newOffsets[0];
              int startSentinelsCount = 0;
              while (currentOffset < newOffsets[1]) {
                OffsetInformation info = authorAccess.getDocumentController().getContentInformationAtOffset(currentOffset);
                if (info.getPositionType() == OffsetInformation.ON_START_MARKER) {
                  // Found a start sentinel
                  startSentinelsCount ++;
                } else {
                  // Not a start sentinel
                  break;
                }
                currentOffset ++;
              }
              // Determine the new caret offset
              newCaretOffset = caretOffset + startSentinelsCount - (wordAtCaret[0] - newOffsets[0]);
            } else {
              // Split at caret position
              newCaretOffset = unwrap(elementMatchingRef, caretOffset, caretOffset - 1, authorAccess)[0];
            }
            // Update the caret position
            authorAccess.getEditorAccess().setCaretPosition(newCaretOffset);
          } else {
            if (insideWord) {
              // Wrap word at caret
              CommonsOperationsUtil.surroundWithFragment(authorAccess, fragment, wordAtCaret[0], wordAtCaret[1] - 1);
              authorAccess.getEditorAccess().setCaretPosition(caretOffset + 1);
            } else {
              //Schema aware insertion can be performed.
              CommonsOperationsUtil.surroundWithFragment(authorAccess, schemaAware, fragment);
            }
          }
        }
      } catch (AuthorOperationStoppedByUserException ex) {
        // Ignore
        authorAccess.getDocumentController().cancelCompoundEdit();
      } catch (AuthorOperationException ex) {
        authorAccess.getDocumentController().cancelCompoundEdit();
        throw ex;
      } catch (Exception e) {
        logger.error(e, e);
        authorAccess.getDocumentController().cancelCompoundEdit();
        throw new AuthorOperationException("The operation could not be executed."); 
      } finally {
        authorAccess.getDocumentController().endCompoundEdit();
      }
    } else {
      throw new IllegalArgumentException("The value of the 'element' argument was not specified.");
    }
  }
  
  /**
   * Gets the selected intervals from the selection model, balances them and returns inclusive intervals.
   *  
   * @param authorAccess Author access.
   * 
   * @return Intervals inclusive at both ends.
   */
  private List<int[]> getSelectedIntervals(AuthorAccess authorAccess) {
    AuthorSelectionModel authorSelectionModel = authorAccess.getEditorAccess().getAuthorSelectionModel();
    List<ContentInterval> selectionIntervals = authorSelectionModel.getSelectionIntervals();
    List<int[]> toProcessInterals = new ArrayList<int[]>(selectionIntervals.size());
    
    for (int i = 0; i < selectionIntervals.size(); i++) {
      ContentInterval contentInterval = selectionIntervals.get(i);
      int[] balancedSelection = authorAccess.getEditorAccess().getBalancedSelection(
        contentInterval.getStartOffset(), contentInterval.getEndOffset());
      
      toProcessInterals.add(new int[] {balancedSelection[0], balancedSelection[1] - 1});
    }

    return toProcessInterals;
  }

  /**
   * Toggles the selected content.
   * 
   * @param authorAccess Author access.
   * @param fragment The fragment to either wrap or unwrap the selection.
   * @param wrapNode The actual node that is wrapped/unwrapped from the previous fragment.
   * We give both to avoid being computed again.
   * @param schemaAware <code>true</code> if the operation can interogate the schema.
   * 
   * @throws AuthorOperationException 
   * @throws BadLocationException
   */
  private void performToggleSelection(
      AuthorAccess authorAccess, 
      String fragment, 
      AuthorElement wrapNode,
      boolean schemaAware) throws AuthorOperationException, BadLocationException {
    boolean caretAtStart = authorAccess.getEditorAccess().getCaretOffset() == authorAccess.getEditorAccess().getSelectionStart();
    
    List<IntervalAndAction> toProcess = new ArrayList<IntervalAndAction>();
    AuthorDocumentController ctrl = authorAccess.getDocumentController();
    AuthorDocumentFragment authorFragment = ctrl.createNewDocumentFragmentInContext(
        fragment, authorAccess.getEditorAccess().getCaretOffset());
    
    List<int[]> toProcessIntervals = getSelectedIntervals(authorAccess);
    // Split the initial intervals into smaller ones that are valid to be wrapped/unwrapped.
    for (Iterator<int[]> iterator = toProcessIntervals.iterator(); iterator.hasNext();) {
      collectToggleIntervals(authorAccess, toProcess, ctrl, wrapNode, authorFragment, iterator.next(), true, schemaAware);
    }
    
    // The previous iteration can leave these intervals randomized. We need the sorted
    // further on.
    sortAscending(toProcess);
    
    // Condense consecutive intervals.
    IntervalAndAction prevIntervalAction = null;
    for (Iterator<IntervalAndAction> iterator = toProcess.iterator(); iterator.hasNext();) {
      IntervalAndAction intervalAndAction = iterator.next(); 
      
      int[] contentInterval = intervalAndAction.interval;
      if (prevIntervalAction != null
          && prevIntervalAction.interval[1] + 1 == contentInterval[0]
          && prevIntervalAction.action == intervalAndAction.action) {
        prevIntervalAction.interval[1]  = contentInterval[1];
        prevIntervalAction.entireIntervalWrapped = prevIntervalAction.entireIntervalWrapped && intervalAndAction.entireIntervalWrapped;
        // Keep the same reference to the previous interval.
        iterator.remove();
      } else {
        // Not merged. Update the previous.
        prevIntervalAction = intervalAndAction;
      }
    }

    // If we have some intervals to process.
    if (!toProcess.isEmpty()) {
      boolean allWrapped = isAllWrapped(toProcess);
      
      // The node that contains all the intervals. We will fire this node
      // as changed at the end of the processing.
      AuthorNode toRefresh = null;
      
      // Disable all notifications to avoid to much processing like CSS styles reset
      // and re-layouts.
      authorAccess.getDocumentController().disableLayoutUpdate();
      try {
        AuthorDocumentController documentController = authorAccess.getDocumentController();
        // If at least one interval must be wrapped we will wrap them all.
        boolean masterSurround = false;

        if (logger.isDebugEnabled()) {
          logger.debug("All intervals are fully wrapped: " + allWrapped);
        }
        
        // Collects the intervals that must be surrounded. We use positions because 
        // at some point we might decide that we should wrap intervals that were 
        // previously unwrapped.
        // The ia.entireIntervalWrapped and allWrapped flags should avoid such situations so this is mainly a precaution.
        List<Position[]> toSurround = new ArrayList<Position[]>(toProcess.size());
        // A "true" to surround and a false to just insert the fragment.
        List<Integer> surround = new ArrayList<Integer>(toProcess.size()); 
        int size = toProcess.size();
        for (int i = toProcess.size() - 1; i >= 0; i--) {
          IntervalAndAction ia = toProcess.get(i);
          int[] part = ia.interval;
          int[] affectedInterval = part;
          if (logger.isDebugEnabled()) {
            logger.debug("Process interval: " + ia.action);
            logger.debug("         Content: '" + ctrl.serializeFragmentToXML(ctrl.createDocumentFragment(part[0], part[1])) + "'") ;
          }

          int action = ia.action;
          if (ia.action == IntervalAndAction.ACTION_SURROUND) {
            // We have a selection
            int startOffset = part[0];
            // The selection end offset is exclusive
            int endOffset = part[1];

            if (
                // If this interval is entirely wrapped but we have other intervals 
                // that are not, we must wrap them all. By not entering this block
                // we leave the current interval untouched (and wrapped).
                (!ia.entireIntervalWrapped || allWrapped)) {
              // Unwrap all the selected nodes matching the fragment node
              UnwrapResult result = unwrapElementsMatchingReferenceElement(
                  startOffset, endOffset, wrapNode, authorAccess);

              // If at least one unwrap call tells us that we should wrap we will wrap
              // all intervals. We either wrap or unwrap all. The ia.entireIntervalWrapped and allWrapped
              // flags should avoid such situation so this is just a precaution.
              masterSurround = masterSurround || result.performSurround;
              
              // The interval that was processed by unwrap. It's either the initial interval or 
              // the one returned by unwrap.
              affectedInterval = result.intervalToSurround;
            } else {
              action = IntervalAndAction.ACTION_SKIP;
            }
          } else {
            masterSurround = true;
          }
          
          toSurround.add(new Position[] {
              documentController.createPositionInContent(affectedInterval[0]),
              documentController.createPositionInContent(affectedInterval[1])});
          
          if (logger.isDebugEnabled()) {
            logger.debug("Before " + part[0] + ", " + part[1] + " => " + affectedInterval[0] + ", " + affectedInterval[1]);
          }

          surround.add(action);
        }

        // Compute the ancestor of the altered intervals after the intervals 
        // have been processed for unwrap.
        int offsetN = toSurround.get(0)[1].getOffset();
        int offset1 = toSurround.get(toProcess.size() - 1)[0].getOffset();
        // The node that contains all the intervals. We will fire this node
        // as changed at the end of the processing.
        toRefresh = 
            authorAccess.getDocumentController().getCommonParentNode(
                authorAccess.getDocumentController().getAuthorDocumentNode(), offset1, offsetN);

        List<ContentInterval> toSelect = null;
        AuthorSelectionModel authorSelectionModel = authorAccess.getEditorAccess().getAuthorSelectionModel();
        
        if (masterSurround) {
          size = toSurround.size();
          if (size > 1) {
            // If we have just one interval there is no point in doing anything 
        	// for the selection. The interval will automatically be selected afterwards.
            toSelect = new ArrayList<ContentInterval>(toSurround.size());
          }
          for (int i = size - 1; i >= 0; i--) {
            Position[] is = toSurround.get(i);
            Integer action = surround.get(i);
            if (action == IntervalAndAction.ACTION_SURROUND) {
              // The fragment can be altered when surrounding. For example the redundant namespace declaration 
              // are removed. because of that we need to use a fresh fragment for each surround operation.
              AuthorDocumentFragment newAuthorFragment = ctrl.createNewDocumentFragmentInContext(
                  fragment, is[0].getOffset());
              
              authorAccess.getDocumentController().surroundInFragment(
                  newAuthorFragment, 
                  is[0].getOffset(),
                  is[1].getOffset());
            } else if (action == IntervalAndAction.ACTION_INSERT) {
              authorAccess.getDocumentController().insertFragment(is[0].getOffset(), authorFragment);
            }
            
            // The previous action sets teh selection arround the affected interval.
            if (toSelect != null) {
              if (action == IntervalAndAction.ACTION_SKIP) {
                toSelect.add(new ContentInterval(is[0].getOffset(), is[1].getOffset() + 1));
              } else {
                toSelect.add(authorSelectionModel.getSelectionInterval());
              }
            }
          }
        } else {
          // All nodes were unwrapped. We just set those intervals as selected.
          toSelect = new ArrayList<ContentInterval>(toSurround.size());
          // Set the selection intervals the intervals that will be surrounded.
          for (int i = toSurround.size() - 1; i >= 0; i--) {
            Position[] positions = toSurround.get(i);
            int start = !caretAtStart ? positions[0].getOffset() : positions[1].getOffset() + 1;
            int end = !caretAtStart ? positions[1].getOffset() + 1 : positions[0].getOffset();
            toSelect.add(new ContentInterval(start, end));
          }
        }
        
        // Set a selection over the processed intervals. It will not always be
        // the same thing as the initial selection but it's close enough. 
        authorSelectionModel.setSelectionIntervals(toSelect, true);
      } finally {
        // At the end of the operation we fire a notification on the ancestor of all changes.
        authorAccess.getDocumentController().enableLayoutUpdate(toRefresh);
      }
    }
  }

  /**
   * Check if all intervals contain content that is already wrapped inside the toggle element.
   * 
   * @param toProcess The intervals to process.
   * 
   * @return <code>true</code> if all intervals are to be unwrapped.
   */
  private boolean isAllWrapped(List<IntervalAndAction> toProcess) {
    boolean allWrapped = true;
    for (IntervalAndAction intervalAndAction : toProcess) {
      if (!intervalAndAction.entireIntervalWrapped) {
        allWrapped = false;
        break;
      }
    }
    return allWrapped;
  }

  /**
   * Makes sure the intervals are sorted ascending.
   * 
   * @param toProcess Intervals to sort.
   */
  private void sortAscending(List<IntervalAndAction> toProcess) {
    Collections.sort(toProcess, new Comparator<IntervalAndAction>() {
      @Override
      public int compare(IntervalAndAction o1, IntervalAndAction o2) {
        return o1.interval[0] - o2.interval[0];
      }
    });
  }

  /**
   * A processed interval and its action. The only reason for this class to exist
   * is that the schema aware surround might reach an empty node (like a para) and 
   * in that case the action is to just insert the fragment in it.
   */
  private static class IntervalAndAction {
    /**
     * The interval must be surrounded.
     */
    private static final int ACTION_SURROUND = 0;
    /**
     * We should just insert inside the interval.
     */
    private static final int ACTION_INSERT = 2;
    /**
     * No action can be performed on this interval.
     */
    private static final int ACTION_INVALID = 3;
    /**
     * This interval should be skipped from processing.
     */
    private static final int ACTION_SKIP = 4;
    /**
     * The interval. Inclusive.
     */
    int[] interval;
    /**
     * The action to be performed on the interval.
     */
    int action = ACTION_SURROUND;
    /**
     * <code>true</code> if the entire interval is already in the context of a toggle element.
     */
    private boolean entireIntervalWrapped;
    
    /**
     * Constructor.
     * 
     * @param interval The interval.
     * @param action The action to be performed with it. Either ACTION_SURROUND or ACTION_INSERT.
     */
    public IntervalAndAction(int[] interval, int action) {
      this.interval = interval;
      this.action = action;
    }

    /**
     * Constructor.
     *  
     * @param is           The interval.
     * @param action       The action to be performed with it. Either ACTION_SURROUND or ACTION_INSERT.
     * @param fullyWrapped <code>true</code> if the entire interval is already in the context of a toggle element.
     */
    public IntervalAndAction(int[] is, int action, boolean fullyWrapped) {
      interval = is;
      this.action = action;
      entireIntervalWrapped = fullyWrapped;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "[" + interval[0] + ", " + interval[1] + "], action: " + action + ", fullyWrapped: "+ entireIntervalWrapped;
    }
  }
  
  /**
   * Identifies the intervals that can be toggled. Either wrapped or unwrapped.
   * If an interval can't be toggled it will be split into smaller parts.
   * 
   * @param authorAccess Author access.
   * @param collectedIntervals The list with the intervals that can be toggled.
   * @param ctrl Author document controller.
   * @param wrapNode The node to wrap/unwrap. We pass this to avoid the overhead of 
   * computing it from the fragment.
   * @param authorFragment The fragment to wrap/unwrap.
   * @param balancedInterval The interval to process.
   * @param raw <code>true</code> if this interval comes from the selection model (unprocessed).
   * @param schemaAware <code>true</code> if schema information should be used to decide if the toggle is possible.
   * 
   * @throws BadLocationException
   */
  private void collectToggleIntervals(
      AuthorAccess authorAccess, 
      List<IntervalAndAction> collectedIntervals,
      AuthorDocumentController ctrl, 
      AuthorElement wrapNode, 
      AuthorDocumentFragment authorFragment, 
      int[] balancedInterval,
      boolean raw,
      boolean schemaAware)
      throws BadLocationException {
    IntervalAndAction wrapAction = new IntervalAndAction(balancedInterval, IntervalAndAction.ACTION_SURROUND);
    if (schemaAware) {
      // If schema aware mode is on we will query the schema if the operation is valid.
      wrapAction = canToggleSchemaAware(authorAccess, balancedInterval[0], balancedInterval[1], wrapNode, authorFragment);
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("Tested '" + ctrl.serializeFragmentToXML(
          ctrl.createDocumentFragment(balancedInterval[0], balancedInterval[1])) + 
          "' - action: " + wrapAction) ;
    }
    if (wrapAction.action != IntervalAndAction.ACTION_INVALID) {
      collectedIntervals.add(wrapAction);
    } else if (raw) {
      // These intervals as just as they came from the selection model. They might not be balanced (balanced= begin and end in the same element).
      List<int[]> balancedIntervals = getEquiIntervalFromMarker(
          authorAccess, 
          balancedInterval);
      for (int[] is : balancedIntervals) {
        collectToggleIntervals(authorAccess, collectedIntervals, ctrl, wrapNode, authorFragment, is, false, schemaAware);
      }
    } else {
      // Already balanced intervals that can't be wrapped/unwrapped. Split them into smaller intervals.
      AuthorNode fullySelectedNode = authorAccess.getEditorAccess().getFullySelectedNode(balancedInterval[0], balancedInterval[1] + 1);
      if (logger.isDebugEnabled()) {
        logger.debug("Go deep |" + ctrl.serializeFragmentToXML(ctrl.createDocumentFragment(balancedInterval[0], balancedInterval[1])) + "| fully " + (fullySelectedNode != null));
      }
      if (fullySelectedNode != null) {
        if (logger.isDebugEnabled()) {
          logger.debug("Fully selected node " + fullySelectedNode);
        }
        if (balancedInterval[0] + 1 == balancedInterval[1]) {
          // An empty node. We are forced to treat it individually. In this situation we 
          // can only check if the surround with fragment can be inserted inside.
          boolean add = true;
          if (schemaAware) {
            AuthorSchemaManager authorSchemaManager = authorAccess.getDocumentController().getAuthorSchemaManager();
            if (authorSchemaManager != null &&
                !authorSchemaManager.hasLoadingErrors() &&
                !authorSchemaManager.isLearnSchema()) {
              add = authorSchemaManager.canInsertDocumentFragment(authorFragment, balancedInterval[0] + 1, AuthorSchemaManager.VALIDATION_MODE_LAX);
            }
          }
          
          if (add) {
            collectedIntervals.add(new IntervalAndAction(new int[] {balancedInterval[0] + 1, balancedInterval[1]}, IntervalAndAction.ACTION_INSERT));
          }
        } else {
          collectToggleIntervals(authorAccess, collectedIntervals, ctrl, wrapNode, authorFragment, new int[] {balancedInterval[0] + 1, balancedInterval[1] - 1}, false, schemaAware);
        }
      } else {
        // An interval that can't be wrapped and represents just a portion of a node.
        // Split the interval in smaller parts. For example [aaa<e>text</e>bb] will split into these intervals: [aaa], [<e>text</e>], [bb]
        // and these intervals are further checked.
        // Since the interval is balanced I think we could just get the node at caret and we should obtain the parent of the interval.
        AuthorNode commonParentNode = 
            authorAccess.getDocumentController().getCommonParentNode(
                authorAccess.getDocumentController().getAuthorDocumentNode(), balancedInterval[0], balancedInterval[1]);
        if (logger.isDebugEnabled()) {
          logger.debug("Common " + commonParentNode);
        }
        if (commonParentNode instanceof AuthorParentNode) {
          List<AuthorNode> contentNodes = ((AuthorParentNode) commonParentNode).getContentNodes();
          List<int[]> split = new ArrayList<int[]>();
          int start = balancedInterval[0];
          int end = balancedInterval[1];

          if (logger.isDebugEnabled()) {
            logger.debug("Interval "+ start + ", " + end);
          }
          for (AuthorNode authorNode : contentNodes) {
            if (logger.isDebugEnabled()) {
              logger.debug("Child " + authorNode);
            }
            if (authorNode.getStartOffset() >= start && authorNode.getEndOffset() <= end) {
              if (start < authorNode.getStartOffset()) {
                if (logger.isDebugEnabled()) {
                  logger.debug("Add interval " + start + ",  " + (authorNode.getStartOffset() - 1));
                }
                split.add(new int[] {start, authorNode.getStartOffset() - 1});
              }
              split.add(new int[] {authorNode.getStartOffset(), authorNode.getEndOffset()});
              if (logger.isDebugEnabled()) {
                logger.debug("Addd interval" + authorNode.getStartOffset() + ", " + authorNode.getEndOffset());
              }
              start = authorNode.getEndOffset() + 1;
            } else if (authorNode.getStartOffset() > end) {
              break;
            }
          }

          if (logger.isDebugEnabled()) {
            logger.debug("At end " + start + ", " + end);
          }
          if (start <= end) {
            split.add(new int[] {start, end});
          }

          if (split.size() > 1) {
            // If we got just one it means that we have a text only interval and it can't be wrapped
            // nor can it be split further on.
            // We will let this interval untouched.
            for (int i = 0; i < split.size(); i++) {
              collectToggleIntervals(authorAccess, collectedIntervals, ctrl, wrapNode, authorFragment, split.get(i), false, schemaAware);
            }
          }
        }
      }
    }
  }

  /**
   * Extend selection over the sentinels. 
   * 
   * @param startOffset Start selection offset.
   * @param endOffset End selection offset.
   * @param maxStartOffset Maxim start offset.
   * @param maxEndOffset Maxim end offset.
   * @param authorAccess The object which provides access to Author functions.
   * @return Extended selection offsets.
   * @throws BadLocationException 
   */
  private int[] extendSelectionOverSentinels (
      int startOffset, int endOffset, int maxStartOffset, 
      int maxEndOffset, AuthorAccess authorAccess) throws BadLocationException {
    AuthorNode commonParentNode =
      authorAccess.getDocumentController().getCommonParentNode(
          authorAccess.getDocumentController().getAuthorDocumentNode(), startOffset, endOffset);
    if (commonParentNode != null) {
      boolean extended = false;
      if ((commonParentNode.getStartOffset() == startOffset - 1) && 
          (startOffset - 1 >= maxStartOffset)) {
        startOffset --;
        // Extend the selection over a start sentinel
        extended = true;
      }
      if ((commonParentNode.getEndOffset() == endOffset + 1) && 
          (endOffset + 1 <= maxEndOffset)) {
        endOffset ++;
        // Extend the selection over an end sentinel
        extended = true;
      }
      if (extended) {
        // Try to extend the selection over the parent sentinels
        return extendSelectionOverSentinels(startOffset, endOffset, maxStartOffset, maxEndOffset, authorAccess);
      }
    }
    
    // Return extended selection offsets
    return new int[] {startOffset, endOffset};
  }

  /**
   * Identify the element at caret.
   * 
   * @param authorAccess Author access.
   * @return The element at caret offset.
   * @throws AuthorOperationException if the caret is not inside an element.
   */
  private AuthorElement getElementAtCaretOffset(AuthorAccess authorAccess) throws AuthorOperationException {
    AuthorElement targetElement;
    AuthorNode node = null;
    try {
      node = authorAccess.getDocumentController().getNodeAtOffset(
          authorAccess.getEditorAccess().getCaretOffset());
    } catch (BadLocationException e) {
      logger.error(e, e);
      throw new AuthorOperationException("Cannot identify the current element", e);
    }
    while (node != null && !(node instanceof AuthorElement)) {
      // Search in parents
      node = node.getParent();
    }
    if (node instanceof AuthorElement) {
      // Found the element at caret offset.
      targetElement = (AuthorElement)node;
    } else {
      throw new AuthorOperationException("The carret is not inside an element.");
    }
    return targetElement;
  }


  /**
   * Get the element that matches the given node (same name, namespace and attributes). 
   * The search starts from the given <code>startElement </code> and continues with its parents. 
   * If <code>topElement</code> is <code>true</code> then the top parent matching element 
   * is returned.
   *  
   * @param element The starting element.
   * @param authorAccess Author access.
   * @param referenceElement  The reference element.
   * @param topElement <code>true</code> to return the top matching element.
   * @return The matching element.
   */
  private AuthorElement getElementMatchingReferenceElement(
      AuthorElement element,
      AuthorAccess authorAccess, 
      AuthorElement referenceElement, 
      boolean topElement) {
    AuthorElement matchingElement = null;
    if (elementMatchesReferenceElement(element, referenceElement)) {
      matchingElement = element;
    } 

    AuthorNode root = authorAccess.getDocumentController().getAuthorDocumentNode().getRootElement();

    while(matchingElement == null || topElement) {
      // Search in parents
      AuthorNode parent = element.getParent();
      if (parent instanceof AuthorElement && parent != root) {
        element = (AuthorElement) parent;
        if (elementMatchesReferenceElement(element, referenceElement)) {
          matchingElement = element;
        } 
      } else {
        break;
      }

    }
    return matchingElement;
  }

  /**
   * Check if an element matches the reference element (it has the same name, 
   * namespace and all the attributes from the reference element)
   *  
   * @param element The element.
   * @param referenceElement The reference element.
   * @return <code>true</code> if the element matches the reference element.
   */
  private boolean elementMatchesReferenceElement(AuthorElement element,
      AuthorElement referenceElement) {
    boolean match = true;
    // Check the element name
    if (element.getName().equals(referenceElement.getName())) {
      // Check the namespace
      if (element.getNamespace().equals(referenceElement.getNamespace())) {
        // Check the attributes
        int attributesCount = referenceElement.getAttributesCount();
        for (int i = 0; i < attributesCount; i++) {
          String attrName = referenceElement.getAttributeAtIndex(i);
          if (!attrName.startsWith("xmlns")) {
            AttrValue elemAttr = element.getAttribute(attrName);
            AttrValue refElemAttr = referenceElement.getAttribute(attrName);
            if (elemAttr == null || !refElemAttr.getValue().equals(elemAttr.getValue())) {
              match = false;
              break;
            }
          }
        }
      } else {
        match = false;
      }
    } else {
      match = false;
    }
    return match;
  }

  /**
   * Determine the element from the given fragment. If the fragment contains more 
   * than one element then an exception is thrown. 
   * 
   * @param fragment The given fragment.
   * @param authorAccess Author access.
   * @return The element from the given fragment.
   * 
   * @throws AuthorOperationException 
   */
  private AuthorElement getElementFromFragment(
      String fragment, 
      AuthorAccess authorAccess) 
  throws AuthorOperationException {
    AuthorElement element = null;
    AuthorDocumentFragment authorFragment = authorAccess.getDocumentController().createNewDocumentFragmentInContext(
        fragment, authorAccess.getEditorAccess().getCaretOffset());
    List<AuthorNode> contentNodes = authorFragment.getContentNodes();
    
    if (contentNodes.size() != 1 || authorFragment.containsSimpleText()) {
      // The fragment must contain only one element
      throw new AuthorOperationException("The value of the 'element' argument is not valid. " +
      		"It must be an XML fragment containing a single element.");
    } else {
      AuthorNode authorNode = contentNodes.get(0);
      if (authorNode instanceof AuthorElement) {
        if (((AuthorElement) authorNode).getContentNodes().size() > 0) {
          throw new AuthorOperationException("The value of the 'element' argument is not valid. " +
              "It must be an XML fragment containing a single element.");
        } else {
          element = (AuthorElement) authorNode;
        }
      } else {
        throw new AuthorOperationException("The value of the 'element' argument is not valid. " +
            "It must be an XML fragment containing a single element.");
      }
    }
    return element;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
   */
  @Override
  public String getDescription() {
    return "Toggle \"surround with element\" operation.\n" + 
    		" If there is no selection in the document and the caret is inside a word,\n" +
    		"the word is wrapped in the given element (or unwrapped if it is already\n" +
    		"included in the element), else the fragment is inserted at caret position.\n" + 
    		" If there is a selection in the document, it is wrapped in the given element\n" +
    		"(or unwrapped if it is already included in the element).\n"; 
  }
  
  /**
   * Unwrap element between the given offsets.
   * 
   * @param element Element to unwrap.
   * @param start Interval start offset (inclusive).
   * @param end Interval end offset (inclusive).
   * @param authorAccess Author access.
   * @return The updated interval offsets.
   */
  private int[] unwrap(AuthorElement element, int start, int end, AuthorAccess authorAccess) {
    try {
      boolean split = start > end;
      AuthorDocumentController controller = authorAccess.getDocumentController();
      int elemStart = element.getStartOffset();
      int elemEnd = element.getEndOffset();

      // If the selection contains the entire content of a node, extend selection
      // to cover the node
      int[] updatedOffsets = extendSelectionOverSentinels(start, end, 
          element.getStartOffset(), element.getEndOffset(), authorAccess);
      start = updatedOffsets[0];
      end = updatedOffsets[1];
      // Determine if we must unwrap all the element content
     if (start > elemStart && end < elemEnd) {
       // Create end fragment
       AuthorDocumentFragment endFragment = controller.createDocumentFragment(end + 1, elemEnd);
       
       // Delete end content
       controller.delete(end + 1, elemEnd);
       
       // Insert end node
       AuthorNode node = controller.getNodeAtOffset(elemStart + 1);
       int insertOffset = node.getEndOffset() + 1;
       controller.insertFragment(insertOffset, endFragment);
       
       int sentinelsNumber = 0;
       int interval = start < end ? (end - start + 1) : 0;
       // Create fragment
       AuthorDocumentFragment fragment = null;
       if (start <= node.getEndOffset() - 1) {
         // Create the fragment containing the content that must be unwrapped
         fragment = controller.createDocumentFragment(start, node.getEndOffset() - 1);
         sentinelsNumber = (fragment.getLength() - interval) / 2;
         
         // Delete the content
         controller.delete(start, node.getEndOffset() - 1);
         // Insert fragment
         node = controller.getNodeAtOffset(elemStart + 1);
         insertOffset = node.getEndOffset() + 1;
         controller.insertFragment(insertOffset, fragment);
       }

        start = insertOffset + sentinelsNumber;
        end = start + interval - 1;
      } else if (elemStart >= start && end >= elemEnd - 1) {
        if (elemStart == elemEnd - 1) {
          // Empty element, just delete it
          controller.deleteNode(element);
        } else {
          // Create fragment
          AuthorDocumentFragment fragment = 
            controller.createDocumentFragment(elemStart + 1, elemEnd - 1);

          // Delete the content
          controller.delete(elemStart, elemEnd);

          // Insert fragment
          controller.insertFragment(elemStart, fragment);
        }

        // Update offsets
        // Two sentinels were removed
        if (start > elemStart) {
          start -= 1;
        }
        if (end == elemEnd - 1) {
          end -= 1;
        } else {
          end -= 2;
        }
      } else if (elemStart < start) {
        // Check that the interval does not intersects only the element 
        // end sentinel 
        if (start < elemEnd) {

          // Create fragment
          AuthorDocumentFragment fragment = 
            controller.createDocumentFragment(start, elemEnd - 1);   

          // Delete the element content
          controller.delete(start, elemEnd - 1);

          // Insert fragment
          AuthorNode node = controller.getNodeAtOffset(elemStart + 1);
          int insertOffset = node.getEndOffset() + 1;
          controller.insertFragment(insertOffset, fragment);

          // Update offsets
          if (split) {
            start = insertOffset + (fragment.getLength() / 2);
            end = start;
          } else {
            start = insertOffset;
            end = insertOffset + fragment.getLength() + end - (elemEnd + 1);
          }
          
        } else {
          start++;
        }
      } else {
        // Check that the interval does not intersects only the element 
        // start sentinel 
        if (end > elemStart) {

          // Create fragment
          AuthorDocumentFragment fragment = 
            controller.createDocumentFragment(elemStart + 1, end);   

          // Delete the element content
          controller.delete(elemStart + 1, end);

          // Insert fragment
          controller.insertFragment(elemStart, fragment);

          // Update offsets
          if (split) { 
            start = elemStart + (fragment.getLength() / 2);
            end = start;
          } else {
            end = elemStart + fragment.getLength() - 1;
          }
        } else {
          end--;
        }
      }   

    } catch (BadLocationException e) {
      logger.error(e, e);
    }
    return new int[] {start, end};
  }
  
  /**
   * The result of an unwrap operation.
   *  
   * @author alex_jitianu
   */
  private static class UnwrapResult {
    /**
     * <code>true</code> if the interval should be passed further on through the surround 
     * process.
     */
    boolean performSurround;
    /**
     * The interval to be passed further on through the surround process. Inclusive margins.
     */
    int[] intervalToSurround;
    
    /** Constructor.
     * 
     * @param performSurround <code>true</code> to signal that a surround should be performed.
     * @param intervalToSurround The interval that should be surrounded. Inclusive margins.
     */
    public UnwrapResult(boolean performSurround, int[] intervalToSurround) {
      this.performSurround = performSurround;
      this.intervalToSurround = intervalToSurround;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("[");
      if (intervalToSurround != null) {
        builder.append(intervalToSurround[0] + ", " + intervalToSurround[1]);
      }
      builder.append("] perform surround ").append(performSurround);
      return builder.toString();
    }
  }

  /**
   * Unwrap elements included in the given interval that match the given 
   * reference element(it has the same name, namespace and attributes).
   * 
   * @param start Interval start offset.
   * @param end Interval end offset.
   * @param referenceElement The reference element.
   * @param authorAccess The Author access.
   * @return <code>true</code> if the interval contains some content that was not unwrapped.
   * @throws AuthorOperationException
   */
  private UnwrapResult unwrapElementsMatchingReferenceElement(
          int start, int end, AuthorElement referenceElement, 
          AuthorAccess authorAccess) throws AuthorOperationException {
    boolean unwrappedContent = false;
    int[] processedInterval = new int[] {start, end};
    try {
      AuthorDocumentController controller = authorAccess.getDocumentController();

      // Get the content included between start and end offset
      Segment content = new Segment();
      controller.getChars(start, end - start + 1, content);
      char ch = content.first();
      int currentOffset = start;
      AuthorElement startElement = null;
      AuthorElement endElement = null;
      boolean unwrapStart = false;
      boolean unwrapEnd = false;
      // Create the mask for the interval content to mark the offsets that 
      // are not wrapped in elements that matches the reference element
      boolean[] mask = new boolean[end - start + 1];
      // Iterate the content to search the nodes partially included in the
      // given interval, that match the reference node
      while(ch != Segment.DONE) {
        if (ch == 0) {
          // This is a sentinel
          // Check if this is an element start offset
          OffsetInformation info = controller.getContentInformationAtOffset(currentOffset);
          AuthorNode node = info.getNodeForMarkerOffset();
          if ((node instanceof AuthorElement)) {
            int nodeStart = node.getStartOffset();
            int nodeEnd = node.getEndOffset();
            if (elementMatchesReferenceElement((AuthorElement) node, referenceElement)) {

              // We found a node that match the reference node so it must be unwrapped
              int intervalStart = Math.max(start, nodeStart);
              int intervalEnd = Math.min(end, nodeEnd);
              for (int i = intervalStart - start; i <= intervalEnd - start; i++) {
                // Mark the elements that match the reference element
                mask[i] = true;
              }

              if (info.getPositionType() == OffsetInformation.ON_START_MARKER) {
                if (end < nodeEnd) {
                  // Found a node that match the reference node 
                  // and it is partially included in the interval 
                  endElement = (AuthorElement) node;
                  break;
                } 
              } else if (info.getPositionType() == OffsetInformation.ON_END_MARKER) {
                if (start > nodeStart) {
                  // Found a node that match the reference node 
                  // and it is partially included in the interval
                  startElement = (AuthorElement) node;
                  unwrapStart = false;
                }
              }
            } else {
              // Found a sentinel, other than the reference sentinels, ignore it
              mask[currentOffset - start] = true;
              
              if (info.getPositionType() == OffsetInformation.ON_START_MARKER) {
                if (end < nodeEnd) {
                  // Found a node that does not match the reference node 
                  // and it is partially included in the interval 
                  unwrapEnd = true;
                } 
              } else if (info.getPositionType() == OffsetInformation.ON_END_MARKER) {
                if (start > nodeStart) {
                  // Found a node that does not match the reference node 
                  // and it is partially included in the interval
                  unwrapStart = true;
                }
              }
            }
          } 
        } else if (Character.isWhitespace(ch)) {
          // Found a whitespace, ignore it
          mask[currentOffset - start] = true;
        }
        // Move to the next character
        ch = content.next();
        currentOffset++;
      }

      // Check the mask to see if there is unwrapped content left in the interval
      for (int i = 0; i < mask.length; i++) {
        if(!mask[i]) {
          unwrappedContent = true;
          break;
        }
      }
      
      AuthorNode intervalParentElement = null;
      if (!unwrappedContent || unwrapStart || unwrapEnd) {
        // Unwrap the nodes that match the reference node and are partially included
        // in the given interval
        if (endElement != null) {
          int[] updatedOffsets = unwrap(endElement, start, end, authorAccess);
          start = updatedOffsets[0];
          end = updatedOffsets[1];
        }
        if (startElement != null) {
          int[] updatedOffsets = unwrap(startElement, start, end, authorAccess);
          start = updatedOffsets[0];
          end = updatedOffsets[1];
        }
      } else {
        // Insert the unwrapped content in the proximity nodes that match the
        // reference node
        if (startElement != null) {
          // Create fragment
          int startOffset = startElement.getEndOffset() + 1;
          int endOffset = endElement != null ? (endElement.getStartOffset() - 1) : end;
          AuthorDocumentFragment fragment = controller.createDocumentFragment(
              startOffset, 
              endOffset);

          AuthorDocumentFragment endFragment = null;
          int delta = 0;
          if (endElement != null) {
            // Create end fragment
            delta = endElement.getEndOffset() - end;
            endFragment = controller.createDocumentFragment(
                endElement.getStartOffset() + 1, endElement.getEndOffset() - 1);
            controller.deleteNode(endElement);
          }

          controller.delete(startOffset, endOffset);


          int insertOffset = startElement.getEndOffset();
          // Insert fragments
          if (endElement != null) {
            controller.insertFragment(insertOffset, endFragment);
          }
          controller.insertFragment(insertOffset, fragment);

          AuthorNode node = controller.getNodeAtOffset(insertOffset);

          // Update offsets
          end = node.getEndOffset() - delta;
          intervalParentElement = node;
          unwrappedContent = false;
        } else if (endElement != null) {
          // Create fragment
          AuthorDocumentFragment fragment = controller.createDocumentFragment(
              start, endElement.getStartOffset() - 1);

          int delta = endElement.getEndOffset() - end;
          controller.delete(
              start, endElement.getStartOffset() - 1);


          int insertOffset = endElement.getStartOffset() + 1;
          // Insert fragments
          controller.insertFragment(insertOffset, fragment);

          AuthorNode node = controller.getNodeAtOffset(insertOffset);

          // Update offsets
          end = node.getEndOffset() - delta;
          intervalParentElement = node;
          unwrappedContent = false;
        }
      }


      // Iterate the content to search the nodes that must be unwrapped
      content = new Segment();
      controller.getChars(start, end - start + 1, content);
      currentOffset = start;
      ch = content.first();
      List<Integer> startOffsetsToUnwrap = new ArrayList<Integer>();
      while(ch != Segment.DONE) {
        if (ch == 0) {
          // This is a sentinel
          OffsetInformation info = controller.getContentInformationAtOffset(currentOffset);
          AuthorNode node = info.getNodeForMarkerOffset();
          if ((node instanceof AuthorElement)) {
            if (elementMatchesReferenceElement((AuthorElement) node, referenceElement)) {
              int nodeStart = node.getStartOffset();
              int nodeEnd = node.getEndOffset();
              // Check if this is an element start offset
              if (info.getPositionType() == OffsetInformation.ON_START_MARKER) {
                if (end >= nodeEnd) {
                  // Found the start offset of an element that must be unwrapped
                  startOffsetsToUnwrap.add(0, nodeStart);
                }
              } 
            } 
          } 
        } 
        // Move to the next character
        ch = content.next();
        currentOffset++;
      }

      for (Integer offset : startOffsetsToUnwrap) {
        AuthorNode node = controller.getNodeAtOffset(offset + 1);
        // Unwrap node
        int[] updatedOffsets = unwrap((AuthorElement) node, start, end, authorAccess);
        // Update the start and end offsets
        start = updatedOffsets[0];
        end = updatedOffsets[1];
      }

      // Update the selection
      processedInterval = new int[] {start, end};

      // If one of the selection parent match the wrap node, the selected content is unwrapped
      // <b>aa<i>yy[SEL_START]xx[SEL_END]zz</i>bb</b>
      AuthorNode commonParentNode = authorAccess.getDocumentController().getCommonParentNode(
          authorAccess.getDocumentController().getAuthorDocumentNode(),
          start, end);
      if (commonParentNode instanceof AuthorElement) {
        AuthorElement splitElement = getElementMatchingReferenceElement(
            (AuthorElement) commonParentNode, authorAccess, referenceElement, true);
        if (splitElement != null && splitElement != intervalParentElement) {
          // Split content
          int[] newOffsets = unwrap(splitElement, start, end, authorAccess);
          // Update selection
          processedInterval = new int[] {newOffsets[0], newOffsets[1]};
          unwrappedContent = false;
        }
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException("The operation could not be executed.");
    }

    return new UnwrapResult(unwrappedContent, processedInterval);
  }
  
  /**
   * Checks if the given interval can be wrapped or unwrapped.
   *  
   * @param authorAccess Author access.
   * @param start The interval start.
   * @param end The interval end. Inclusive.
   * @param wrapNode The node to wrap/unwrap.
   * @param surroundFragment The fragment to surround with.
   * 
   * @return The action that can be performed on the interval. Never <code>null</code>.
   * 
   * @throws BadLocationException 
   */
  private IntervalAndAction canToggleSchemaAware(
      AuthorAccess authorAccess, 
      int start,
      int end,
      AuthorElement wrapNode,
      AuthorDocumentFragment surroundFragment) throws BadLocationException {
    int action = IntervalAndAction.ACTION_SURROUND;
    boolean fullyWrapped = false;

    // Check if we have a valid schema manager.
    AuthorSchemaManager authorSchemaManager = authorAccess.getDocumentController().getAuthorSchemaManager();
    if (authorSchemaManager != null &&
        !authorSchemaManager.hasLoadingErrors() &&
        !authorSchemaManager.isLearnSchema()) {
      action = IntervalAndAction.ACTION_INVALID;

      AuthorNode commonParentNode = 
          authorAccess.getDocumentController().getCommonParentNode(
              authorAccess.getDocumentController().getAuthorDocumentNode(), start, end);
      boolean canUnwrap = true;
      // Check if we can unwrap. This is a very lightweight check and not that schema aware...
      // If a node is selected and it must be unwrapped, ideally we should also
      // check if it's children can be inserted it its place (with respect to the schema).
      fullyWrapped = isFullyWrappedInterval(authorAccess, start, end, wrapNode);
      canUnwrap = fullyWrapped;
      
      if (!canUnwrap) {
        if (commonParentNode instanceof AuthorElement) {
          canUnwrap = elementMatchesReferenceElement((AuthorElement) commonParentNode, wrapNode); 
        }
      }
      
      boolean canWrap = false;
      if (!canUnwrap) {
        // Check if we can wrap.
        canWrap = canWrap(
            authorAccess, 
            surroundFragment, 
            start, 
            end,
            commonParentNode, 
            AuthorSchemaManager.VALIDATION_MODE_LAX);
      }
      
      if (canWrap || canUnwrap) {
        action = IntervalAndAction.ACTION_SURROUND;
      }
    }

    return new IntervalAndAction(new int[] {start, end},  action, fullyWrapped);
  }
  
  /**
   * Checks if the entire interval in wrapped in the toggle element. Some code based on the one
   * from {@link #unwrapElementsMatchingReferenceElement(int, int, AuthorElement, AuthorAccess)}
   * (unfortunately copied-it was difficult to extract something sommon) but with a little different interpretations.
   * 
   * @param authorAccess     Author access.
   * @param start            Interval start. 
   * @param end              Interval end. Inclusive. 
   * @param referenceElement The element to toggle.
   * 
   * @return <code>true</code> if the interval is fully contained in a 
   * toggle element context.
   *  
   * @throws BadLocationException Bad offsets. 
   */
  private boolean isFullyWrappedInterval(
      AuthorAccess authorAccess, 
      int start, 
      int end, AuthorElement referenceElement) throws BadLocationException {
    AuthorDocumentController controller = authorAccess.getDocumentController();

    // Get the content included between start and end offset
    Segment content = new Segment();
    controller.getChars(start, end - start + 1, content);
    char ch = content.first();
    int currentOffset = start;
    // Create the mask for the interval content to mark the offsets that 
    // are not wrapped in elements that matches the reference element
    // Handles the case of empty elements In that situation there is nothing to be unwrapped.
    short[] mask = new short[end - start + 1];
    // Signals the fact that the character is not in a toggle element context.
    short notWrapped = 0;
    // Signals the fact that the character is in a toggle element context.
    short wrapped = 1;
    // A neutral sentinel marker.
    short neutral = 2;
    // Iterate the content to search the nodes partially included in the
    // given interval, that match the reference node
    while(ch != Segment.DONE) {
      if (ch == 0) {
        // This is a sentinel
        // Check if this is an element start offset
        OffsetInformation info = controller.getContentInformationAtOffset(currentOffset);
        AuthorNode node = info.getNodeForMarkerOffset();
        if ((node instanceof AuthorElement)) {
          int nodeStart = node.getStartOffset();
          int nodeEnd = node.getEndOffset();
          if (elementMatchesReferenceElement((AuthorElement) node, referenceElement)) {
            // We found a node that match the reference node so it must be unwrapped
            int intervalStart = Math.max(start, nodeStart);
            int intervalEnd = Math.min(end, nodeEnd);
            for (int i = intervalStart - start; i <= intervalEnd - start; i++) {
              // Mark the elements that match the reference element
              mask[i] = wrapped;
            }

            if (info.getPositionType() == OffsetInformation.ON_START_MARKER) {
              if (end < nodeEnd) {
                // Found a node that match the reference node 
                // and it is partially included in the interval 
                break;
              } 
            }
          } else {
            // Found a sentinel, other than the reference sentinels, ignore it
            mask[currentOffset - start] = neutral;
          }
        } 
      } else if (Character.isWhitespace(ch)) {
        // Found a whitespace, ignore it
        mask[currentOffset - start] = neutral;
      }
      // Move to the next character
      ch = content.next();
      currentOffset++;
    }

    boolean unwrappedContent = false;
    // The interval also contains wrapped characters. Handles the case of empty elements.
    // In that situation there is nothing to be unwrapped.
    boolean hasWrapped = false;
    if (logger.isDebugEnabled()) {
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < mask.length; i++) {
        b.append(mask[i]).append(",");
      }
      logger.debug("Mask: " + b.toString());
    }
    
    // Check the mask to see if there is unwrapped content left in the interval
    for (int i = 0; i < mask.length; i++) {
      if(mask[i] == notWrapped) {
        unwrappedContent = true;
        break;
      } else if(mask[i] == wrapped) {
        hasWrapped = true;
      }
    }
    
    boolean fullySelected = !unwrappedContent && hasWrapped;
    if (!fullySelected) {
      // If one of the selection parent matches the wrap node, the selected content is unwrapped
      // <b>aa<i>yy[SEL_START]xx[SEL_END]zz</i>bb</b>
      AuthorNode commonParentNode = authorAccess.getDocumentController().getCommonParentNode(
          authorAccess.getDocumentController().getAuthorDocumentNode(),
          start, end);
      if (logger.isDebugEnabled()) {
        logger.debug("CommonParentNode " + commonParentNode);
      }
      if (commonParentNode instanceof AuthorElement) {
        AuthorElement toggleElement = getElementMatchingReferenceElement(
            (AuthorElement) commonParentNode, authorAccess, referenceElement, true);
        
        if (logger.isDebugEnabled()) {
          logger.debug("Ancestor toggle element: " + toggleElement);
        }
        fullySelected = toggleElement != null;
      }
    }
    
    return fullySelected;
  }
  
  /**
   * Method used to check if the documents fragments can be surrounded by the first parameter document fragment.
   * It first checks if the surrounding fragment is accepted by the schema at the given offset.
   * The second check is performed by altering the elements context adding the elements from the surrounding fragment, and
   * then it checks if the fragments to be surrounded are accepted in the new context.
   * 
   * @param authorAccess Author access.
   * @param surroundInFragment The fragment used to surround the array of document fragments. 
   * @param start The offset of the surround operation.
   * @param end The end offset to wrap. Inclusive.
   * @param parentOfChange The node containing the change.
   * @param validationMode The validation mode.
   * @return <code>true</code> if the surround operation is allowed by the schema.
   * @throws BadLocationException 
   */
  private boolean canWrap(
      AuthorAccess authorAccess,
      AuthorDocumentFragment surroundInFragment, 
      int start,
      int end,
      AuthorNode parentOfChange, 
      short validationMode) throws BadLocationException {
    boolean canWrap = true;
    AuthorSchemaManager authorSchemaManager = authorAccess.getDocumentController().getAuthorSchemaManager();
    if (authorSchemaManager != null &&
        !authorSchemaManager.hasLoadingErrors() &&
        !authorSchemaManager.isLearnSchema()) {

      // Test if the surround fragment is valid at the given offset.
      canWrap = authorSchemaManager.canInsertDocumentFragment(surroundInFragment, start, validationMode);

      if (canWrap) {
        // Get the context and derive it with the nodes from the surround fragment.
        WhatElementsCanGoHereContext whatElementsCanGoHereContext = null;
        if (parentOfChange instanceof AuthorElement) {
          whatElementsCanGoHereContext = authorSchemaManager.createWhatElementsCanGoHereContext(parentOfChange.getStartOffset()  + 1);
        } else {
          whatElementsCanGoHereContext = new WhatElementsCanGoHereContext();
        }
        // Get the path of elements from the fragment and derive the context.
        AuthorElement[] elementsPath = getElementsPath(surroundInFragment);
        for (int i = 0; i < elementsPath.length; i++) {
          pushContextElement(whatElementsCanGoHereContext, elementsPath[i].getName());
        }

        // Check if the selected interval can be inserted in the derived context.
        AuthorDocumentFragment authorFragment = authorAccess.getDocumentController().createDocumentFragment(start, end);
        canWrap = authorSchemaManager.canInsertDocumentFragments(
            new AuthorDocumentFragment[] {authorFragment}, whatElementsCanGoHereContext, validationMode);
      }
    }
    
    return canWrap;
  }
  
  /**
   * Derive the given context by adding the given element.
   * 
   * @param context An element context.
   * @param elementName Element name to push in the context.
   */
  private void pushContextElement(WhatElementsCanGoHereContext context, String elementName) {
    ContextElement contextElement = new ContextElement();
    contextElement.setQName(elementName);
    context.pushContextElement(contextElement, null);
  }

  /**
   * Given a document fragment it returns the path of elements until the first leaf.
   * 
   * @param fragment The document fragment to check.
   * @return The path of elements to first leaf.
   */
  public static AuthorElement[] getElementsPath(AuthorDocumentFragment fragment) {
    LinkedList<AuthorElement> path = new LinkedList<AuthorElement>();
    AuthorNode firstLeaf = AuthorNodeUtil.getFirstLeaf(fragment);
    while (firstLeaf != null) {
      if (firstLeaf instanceof AuthorElement) {
        path.addFirst((AuthorElement) firstLeaf);
      }
      
      firstLeaf = firstLeaf.getParent();
    }
    
    return path.toArray(new AuthorElement[0]);
  }

  /**
   * If the given interval is not balanced (balanced = starts and ends in the same element) it 
   * will be split into multiple balanced intervals.
   * 
   * @param authorAccess     Author access.
   * @param interval  The interval to split..
   *
   * @return The list of intervals.
   *
   * @throws BadLocationException
   */
  public static List<int[]> getEquiIntervalFromMarker(
      AuthorAccess authorAccess, int[] interval) throws BadLocationException {
    List<int[]> toReturn = new ArrayList<int[]>(1);
    AuthorDocumentController ctrl = authorAccess.getDocumentController();
    
    //Special processing if not balanced.
    int startOffset = interval[0];
    int endOffset = interval[1];
    AuthorNode startNode = ctrl.getNodeAtOffset(startOffset);
    AuthorNode endNode = ctrl.getNodeAtOffset(endOffset + 1);
    if(startNode == endNode) {
      if (logger.isDebugEnabled()) {
        logger.debug("Same node:" + startNode);
      }
      
      if (logger.isDebugEnabled()) {
        logger.debug("SO:" + startOffset + " EO:" + endOffset);
      }
      //Already balanced
      toReturn.add(interval);
    } else {
      AuthorNode common = authorAccess.getDocumentController().getCommonParentNode(
          authorAccess.getDocumentController().getAuthorDocumentNode(), startOffset, endOffset);
      if (logger.isDebugEnabled()) {
        logger.debug("SO:" + startOffset + " EO:" + endOffset);
      }
      if (logger.isDebugEnabled()) {
        logger.debug("startNode:" + startNode + " endNode: " + endNode);
      }
      while (common != startNode && startNode != null) {
        if (logger.isDebugEnabled()) {
          logger.debug("Split at start end:" + startNode.getEndOffset());
        }
        //We have to split the start.
        if(startOffset <= startNode.getEndOffset() - 1) {
          toReturn.add(new int[] {startOffset, startNode.getEndOffset() - 1});
        }
        startOffset = startNode.getEndOffset() + 1;
        startNode = startNode.getParent();
      }
      
      int commonIndex = toReturn.size();
      while (common != endNode && endNode != null) {
        if(endNode.getStartOffset() + 1 <= endOffset) {
          toReturn.add(
              commonIndex,
              new int[] {endNode.getStartOffset() + 1, endOffset});
        }
        endOffset = endNode.getStartOffset() - 1;
        endNode = endNode.getParent();
      }
      
      if (startOffset <= endOffset) {
        toReturn.add(commonIndex, new int[]{startOffset, endOffset});
      }
    }
    return toReturn;
  }
}