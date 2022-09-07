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

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Replaces the content of the:
 * - specified element (indicated by an XPath expression) or
 * - fully selected element or
 * - element at caret (if the selection is empty or a node is not entirely selected).
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class ReplaceElementContentOperation implements AuthorOperation {
  /**
   * The argument that specifies the fragment that will be inserted as the element content. 
   */
  public static final String ARGUMENT_FRAGMENT = "fragment";
  /**
   * The XPath location that identifies the element. Empty/null for the current element.
   */
  static final String ARGUMENT_ELEMENT_XPATH_LOCATION = "elementLocation";

  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;

  /**
   * Constructor.
   */
  public ReplaceElementContentOperation() {
    arguments = new ArgumentDescriptor[] {
        new ArgumentDescriptor(
            ARGUMENT_ELEMENT_XPATH_LOCATION, 
            ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
            "An XPath expression indicating the element whose attribute will be changed.\n"
            + "Note: If it is not defined then the element at the caret position or the current fully selected element will be used."), 
        new ArgumentDescriptor(
            ARGUMENT_FRAGMENT,
            ArgumentDescriptor.TYPE_FRAGMENT,
            "The new content of the element.") 
    };
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    try {
      AuthorNode node = getNodeToReplaceContent(authorAccess, args);
      int startContentOffset = node.getStartOffset() + 1;
      int endContentOffset = node.getEndOffset() - 1;

      AuthorDocumentController documentController = authorAccess.getDocumentController();

      //Delete previous content.
      if (startContentOffset <= endContentOffset) {
        boolean delete = documentController.delete(startContentOffset, endContentOffset);
        if (!delete) {
          throw new AuthorOperationException("Could not delete old content node.");
        }
      }
      
      String fragment = getFragmentToInsert(args);
      if (fragment.length() > 0) {
        AuthorDocumentFragment documentFragment = documentController.createNewDocumentFragmentInContext(
            fragment, startContentOffset);
        //Insert the new fragment.
        documentController.insertFragment(startContentOffset, documentFragment);
      }

    } catch (BadLocationException e) {
      throw new AuthorOperationException("Could not determine the node for which to replace the content.", e);
    }
  }

  /**
   * Get the fragment to be inserted.
   * 
   * @param args The arguments map of the operation.
   * 
   * @return The fragment (As string)
   */
  private static String getFragmentToInsert(ArgumentsMap args) {
    String fragment = null;
    Object fragmentArgument = args.getArgumentValue(ARGUMENT_FRAGMENT);

    if (fragmentArgument == null) {
      fragment = "";
    } else if (fragmentArgument instanceof String) {
      fragment = (String) fragmentArgument;
    } else {
      throw new IllegalArgumentException("Incorrect fragment argument: " + fragmentArgument);
    }

    return fragment;
  }

  /**
   * Determine the node for which the content will be replaced.
   * 
   * @param authorAccess The author access.
   * @param args The operations arguments.
   * @return The node.
   * @throws BadLocationException 
   * @throws AuthorOperationException 
   */
  private static AuthorNode getNodeToReplaceContent(AuthorAccess authorAccess, ArgumentsMap args) 
      throws BadLocationException, AuthorOperationException {
    AuthorNode node = null;
    // Try to determine the node from the XPath location.
    Object xpathLocation = args.getArgumentValue(ARGUMENT_ELEMENT_XPATH_LOCATION);
    if (xpathLocation instanceof String && !((String) xpathLocation).isEmpty()) {
      AuthorNode[] results = authorAccess.getDocumentController().findNodesByXPath(
          (String) xpathLocation, true, true, true);
      if (results.length > 0 && results[0] instanceof AuthorElement) {
        node = results[0];          
      } else {
        throw new AuthorOperationException("The element XPath location does not identify an element: " + xpathLocation);
      }
    } else {
      // Determine the node from the selection
      node = authorAccess.getEditorAccess().getFullySelectedNode();
      if (node == null) {
        int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
        node = authorAccess.getDocumentController().getNodeAtOffset(caretOffset); 
      }
    }

    return node;
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
    return "Replaces the content of the fully selected element or the content of "
        + "the element at caret (if the selection is empty or a node is not entirely selected)";
  }
}