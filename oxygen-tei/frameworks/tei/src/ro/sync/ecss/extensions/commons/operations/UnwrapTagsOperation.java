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

import javax.swing.text.BadLocationException;




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Unwrap tags operation. 
 */

@WebappCompatible
public class UnwrapTagsOperation implements AuthorOperation {
  /**
   * The location of the element to unwrap argument.
   */
  private static final String ARGUMENT_XPATH_UNWRAP_ELEMENT = "unwrapElementLocation";
  
  /**
   * The arguments array.
   */
  private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
    new ArgumentDescriptor(
        ARGUMENT_XPATH_UNWRAP_ELEMENT,
        ArgumentDescriptor.TYPE_XPATH_EXPRESSION,
        "An XPath expression indicating the element to unwrap.\n" +
        "Note: If it is not defined then the element at the caret is unwrapped."),
  };
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    AuthorNode nodeToUnwrap = null;
    // Determine the element to unwrap
    Object xpathLocation = args.getArgumentValue(ARGUMENT_XPATH_UNWRAP_ELEMENT);
    if (xpathLocation instanceof String && ((String) xpathLocation).length() > 0) {
      // An xPath expression was set
      AuthorNode[] results =
        authorAccess.getDocumentController().findNodesByXPath((String) xpathLocation, true, true, true);
      if (results.length > 0 && results[0] instanceof AuthorElement) {
        nodeToUnwrap = results[0];          
      } else {
        throw new AuthorOperationException("The XPath expression does not identify an element: " + xpathLocation);
      }
    } else {
      AuthorNode node = null;
      try {
        // Determine the element at caret offset
        int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
        node = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
      } catch (BadLocationException e) {
        throw new AuthorOperationException("Cannot identify the current element", e);
      }
      while (node != null && !(node instanceof AuthorElement)) {
        node = node.getParent();
      }
      if (node instanceof AuthorElement) {
        nodeToUnwrap = node;
      } else {
        throw new AuthorOperationException("You need to have the carret inside an element.");
      }
    }

    // Begin compound edit
    authorAccess.getDocumentController().beginCompoundEdit();
    try {
      // Unwrap the element
      CommonsOperationsUtil.unwrapTags(authorAccess, nodeToUnwrap);
    } catch (BadLocationException e) {
      // Cancel compound edit
      authorAccess.getDocumentController().cancelCompoundEdit();
      throw new AuthorOperationException("The unwrap cannot be performed.");
    }
    // End compound edit
    authorAccess.getDocumentController().endCompoundEdit();
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
    return "Unwrap element tags.";
  }
}