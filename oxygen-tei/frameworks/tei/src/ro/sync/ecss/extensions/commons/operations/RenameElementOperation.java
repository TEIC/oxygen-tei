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
 * An implementation of an operation that renames one or more elements identified by the given XPath expression.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class RenameElementOperation implements AuthorOperation {
  /**
   * The XPath location that identifies the elements.
   * Empty/null for the current element.
   * The value is <code>elementLocation</code>.
   */
  public static final String ARGUMENT_ELEMENT_XPATH_LOCATION = "elementLocation";
  
  /**
   * The new name for the element(s) which will be renamed. 
   */
  public static final String ARGUMENT_ELEMENT_NAME = "elementName";
  
  /**
   * The arguments of the operation.
   */
  protected ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor for the rename element operation.
   */
  public RenameElementOperation() {
    arguments = new ArgumentDescriptor[2];
    // The new name of the renamed element(s)
    ArgumentDescriptor argumentDescriptor = 
        new ArgumentDescriptor(
            ARGUMENT_ELEMENT_NAME, 
            ArgumentDescriptor.TYPE_STRING, 
            "A string representing the new elements' qualified name. To declare "
            + "a new namespace, append to the qualified name the pound (#) "
            + "character and the namespace declaration.");

    arguments[0] = argumentDescriptor;
    
    // Argument defining the elements that will be modified.
    argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_ELEMENT_XPATH_LOCATION, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating the elements to be renamed.\n"
          + "Note: If it is not defined then the element at the caret position will be used.");
    arguments[1] = argumentDescriptor;
    
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    // The new name of the elements which will be renamed
    Object elementName = args.getArgumentValue(ARGUMENT_ELEMENT_NAME);
    // The XPath location.
    Object xpathLocation = args.getArgumentValue(ARGUMENT_ELEMENT_XPATH_LOCATION);
    
    AuthorNode[] nodesToRename = null;
    // Obtain all the elements which will be renamed
    if (xpathLocation != null && xpathLocation instanceof String && ((String)xpathLocation).trim().length() > 0) {
      nodesToRename =
        authorAccess.getDocumentController().findNodesByXPath(((String) xpathLocation).trim(), true, true, true);
      if (nodesToRename.length == 0) {
        throw new AuthorOperationException("The element XPath location does not identify a node: " + xpathLocation);
      }
    } else {
      try {
        // Try to obtain the current element
        nodesToRename = new AuthorNode[1];
        nodesToRename[0] = authorAccess.getDocumentController().getNodeAtOffset(
            authorAccess.getEditorAccess().getCaretOffset());
      } catch (BadLocationException e) {
        throw new AuthorOperationException("Cannot identify the current node", e);
      }
    }
    
    // Check if the given name is a string value
    if (elementName instanceof String) {
      // Rename every collected element
      for (int i = 0; i < nodesToRename.length; i++) {
        if (nodesToRename[i] instanceof AuthorElement) {
          authorAccess.getDocumentController().renameElement(
              (AuthorElement)nodesToRename[i], (String)elementName);
        }
      }
    } else {
      throw new AuthorOperationException("The elements' new name/qname does not represent a valid name: " + elementName);
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
    return "Renames the elements identified by the given XPath expression or the "
        + "current element if no XPath is provided.";
  }
}