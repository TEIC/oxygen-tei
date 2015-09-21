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




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Identical with {@link InsertFragmentOperation} with the difference that the selection will be removed. 
 */

@WebappCompatible
public class InsertOrReplaceFragmentOperation extends InsertFragmentOperation {
  /**
   * The value for the ARGUMENT_RELATIVE_LOCATION that causes the fragment to overwrite the
   * node selected by the XPath. 
   */
  static final String POSITION_REPLACE = "Replace";
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
    throws AuthorOperationException {
    // Delete selection.
    AuthorDocumentController controller = authorAccess.getDocumentController();
    controller.beginCompoundEdit();
    boolean deleteSelection = false;
    try {
      if (authorAccess.getEditorAccess().hasSelection()) {
        // If selection exists delete it.
        deleteSelection = true;
        authorAccess.getEditorAccess().deleteSelection();
      }
      
      Object xpathLocation = args.getArgumentValue(ARGUMENT_XPATH_LOCATION);
      Object relativeLocation = args.getArgumentValue(ARGUMENT_RELATIVE_LOCATION);
      
      if (POSITION_REPLACE.equals(relativeLocation)) {
        // If we should insert the fragment replacing an existing node, first delete that node.
        if (xpathLocation instanceof String) {
          String xpathLocationStr = (String) xpathLocation;
          if (xpathLocationStr.trim().length() > 0) {
            AuthorNode[] nodes = authorAccess.getDocumentController().findNodesByXPath(xpathLocationStr, true, true, true);
            if (nodes != null && nodes.length > 0) {
              authorAccess.getEditorAccess().select(nodes[0].getStartOffset(), nodes[0].getEndOffset() + 1);
              authorAccess.getEditorAccess().deleteSelection();
            }
          }
        }
        
        // Set the XPath location to null, so that the fragment will be inserted at the caret position.
        xpathLocation = null;
      }

      Object fragment = args.getArgumentValue(ARGUMENT_FRAGMENT);
      Object argumentValue = args.getArgumentValue(ARGUMENT_GO_TO_NEXT_EDITABLE_POSITION);
      if (argumentValue == null) {
        argumentValue = AuthorConstants.ARG_VALUE_TRUE;
      }
      
      boolean goToFirstEditablePosition = AuthorConstants.ARG_VALUE_TRUE.equals(argumentValue);
      Object schemaAwareArgumentValue = args.getArgumentValue(SCHEMA_AWARE_ARGUMENT);

      // Insert the fragment.
      doOperationInternal(authorAccess, fragment, xpathLocation, relativeLocation, 
          goToFirstEditablePosition, schemaAwareArgumentValue);
    } catch (AuthorOperationException e) {
      if(deleteSelection) {
        // Paste was rejected, undo selection removal
        controller.cancelCompoundEdit();
      }
      throw e;
    } finally {
      controller.endCompoundEdit();
    }

  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
   */
  @Override
  public String getDescription() {
    return "Insert a document fragment. If selection is present, the selection will be replaced with the given fragment.";
  }
  
  /**
   * The relative location of the deleted node.
   */
  private static ArgumentDescriptor ARGUMENT_DESCRIPTOR_RELATIVE_LOCATION = new ArgumentDescriptor(
      ARGUMENT_RELATIVE_LOCATION, 
      ArgumentDescriptor.TYPE_CONSTANT_LIST,
      "The insert position relative to the node determined by the XPath expression.\n" +
      "Can be: " 
      + AuthorConstants.POSITION_BEFORE + ", " +
      AuthorConstants.POSITION_INSIDE_FIRST + ", " +
      AuthorConstants.POSITION_INSIDE_LAST + ", " +
      POSITION_REPLACE + " or " +
      AuthorConstants.POSITION_AFTER + ".\n" +
      "Note: If the XPath expression is not defined this argument is ignored",
      new String[] {
          AuthorConstants.POSITION_BEFORE,
          AuthorConstants.POSITION_INSIDE_FIRST,
          AuthorConstants.POSITION_INSIDE_LAST,
          AuthorConstants.POSITION_AFTER,
          POSITION_REPLACE,
      }, 
      AuthorConstants.POSITION_INSIDE_FIRST);
  
  /**
   * The arguments descriptors for this operation.
   */
  private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
      // Argument defining the XML fragment that will be inserted.
      ARGUMENT_DESCRIPTOR_FRAGMENT,
      // Argument defining the location where the operation will be executed as an XPath expression.
      ARGUMENT_DESCRIPTOR_XPATH_LOCATION,
      // Argument defining the relative position to the node obtained from the XPath location.
      ARGUMENT_DESCRIPTOR_RELATIVE_LOCATION,
      // Argument defining if the fragment insertion is schema aware.
      SCHEMA_AWARE_ARGUMENT_DESCRIPTOR,
      // Argument defining if the fragment insertion is schema aware.
      ARGUMENT_DESCRIPTOR_GO_TO_NEXT_EDITABLE_POSITION,
  };
  
  /**
   * @see ro.sync.ecss.extensions.commons.operations.InsertFragmentOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }
}