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
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

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
import ro.sync.ecss.extensions.api.AuthorOperationStoppedByUserException;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

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
          // We have a selection
          int startOffset = authorAccess.getEditorAccess().getBalancedSelectionStart();
          // The selection end offset is exclusive
          int endOffset = authorAccess.getEditorAccess().getBalancedSelectionEnd() - 1;
          
          // Determine if the caret is at selection start
          boolean caretAtStart = authorAccess.getEditorAccess().getSelectionStart() == 
            authorAccess.getEditorAccess().getCaretOffset();

          // Unwrap all the selected nodes matching the fragment node
          boolean surroundSelectionWithFragment = unwrapElementsMatchingReferenceElement(
              startOffset, endOffset, wrapNode, authorAccess, caretAtStart);

          if (surroundSelectionWithFragment) {
            // Surround with fragment
            CommonsOperationsUtil.surroundWithFragment(authorAccess, schemaAware, fragment);
          }
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
   * Select between the given offsets.
   * 
   * @param startSelection Start selection offset.
   * @param endSelection End selection offset.
   * @param authorAccess Author access.
   * @param caretAtStart If <code>true</code> the caret must be at the start of selection.
   */
  private void select(int startSelection, int endSelection, AuthorAccess authorAccess, boolean caretAtStart) {
    if (caretAtStart) {
      authorAccess.getEditorAccess().select(endSelection, startSelection);
    } else {
      authorAccess.getEditorAccess().select(startSelection, endSelection);
    }
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
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
   */
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
   * Unwrap elements included in the given interval that match the given 
   * reference element(it has the same name, namespace and attributes).
   * 
   * @param start Interval start offset.
   * @param end Interval end offset.
   * @param referenceElement The reference element.
   * @param authorAccess The Author access.
   * @param caretAtStart  If the caret should be placed at the selection start.
   * @return <code>true</code> if the interval contains some content that was not unwrapped.
   * @throws AuthorOperationException
   */
  private boolean unwrapElementsMatchingReferenceElement(
          int start, int end, AuthorElement referenceElement, 
          AuthorAccess authorAccess, boolean caretAtStart) throws AuthorOperationException {
    boolean unwrappedContent = false;
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
      select(start, end + 1, authorAccess, caretAtStart);

      // If one of the selection parent match the wrap node, the selected content is unwrapped
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
          select(newOffsets[0], newOffsets[1] + 1, authorAccess, caretAtStart);
          unwrappedContent = false;
        }
      } 

    } catch (BadLocationException e) {
      throw new AuthorOperationException("The operation could not be executed.");
    }

    return unwrappedContent;
  }
}