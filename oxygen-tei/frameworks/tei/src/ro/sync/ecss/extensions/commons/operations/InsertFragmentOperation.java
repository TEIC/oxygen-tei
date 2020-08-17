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

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResultInsertConstants;

/**
 * An implementation of an insert operation for an argument of type fragment.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class InsertFragmentOperation implements AuthorOperation {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(InsertFragmentOperation.class.getName());
  
  /**
   * The fragment argument.
   * The value is <code>fragment</code>.
   */
  public static final String ARGUMENT_FRAGMENT = "fragment";
  /**
   * Argument defining the XML fragment that will be inserted.
   */
  protected static final ArgumentDescriptor ARGUMENT_DESCRIPTOR_FRAGMENT = new ArgumentDescriptor(
      ARGUMENT_FRAGMENT,
      ArgumentDescriptor.TYPE_FRAGMENT,
      "The fragment to be inserted");
  /**
   * The insert location argument.
   * The value is <code>insertLocation</code>.
   */
  public static final String ARGUMENT_XPATH_LOCATION = "insertLocation";
  /**
   * Argument defining the location where the operation will be executed as an XPath expression.
   */
  protected static final ArgumentDescriptor ARGUMENT_DESCRIPTOR_XPATH_LOCATION = new ArgumentDescriptor(
      ARGUMENT_XPATH_LOCATION, 
      ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
      "An XPath expression indicating the insert location for the fragment.\n" +
      "Note: If it is not defined then the insert location will be at the caret.");
  /**
   * The insert position argument.
   * The value is <code>insertPosition</code>.
   */
  public static final String ARGUMENT_RELATIVE_LOCATION = "insertPosition";

  /**
   * Argument defining the relative position to the node obtained from the XPath location.
   */
  protected static final ArgumentDescriptor ARGUMENT_DESCRIPTOR_RELATIVE_LOCATION = new ArgumentDescriptor(
      ARGUMENT_RELATIVE_LOCATION, 
      ArgumentDescriptor.TYPE_CONSTANT_LIST,
      "The insert position relative to the node determined by the XPath expression.\n" +
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
  
  /**
   * Detect and position the caret inside the first edit location. It can be either 
   * an offset inside the content or an in-place editor.
   */
  public static final String ARGUMENT_GO_TO_NEXT_EDITABLE_POSITION = "goToNextEditablePosition";
  /**
   * Argument defining if the fragment insertion is schema aware.
   */
  protected static final ArgumentDescriptor ARGUMENT_DESCRIPTOR_GO_TO_NEXT_EDITABLE_POSITION = new ArgumentDescriptor(
      ARGUMENT_GO_TO_NEXT_EDITABLE_POSITION, 
      ArgumentDescriptor.TYPE_CONSTANT_LIST,
      "After inserting the fragment, the first editable position is detected and " +
      "the caret is placed at that location. It handles any in-place editors used " +
      "to edit attributes. It will be ignored if the fragment specifies a caret " +
      "position using the caret editor variable.",
      new String[] {
          AuthorConstants.ARG_VALUE_TRUE,
          AuthorConstants.ARG_VALUE_FALSE,
      }, 
      AuthorConstants.ARG_VALUE_TRUE);
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor.
   */
  public InsertFragmentOperation() {
    arguments = new ArgumentDescriptor[] {
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
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
    throws AuthorOperationException {
    Object fragment = args.getArgumentValue(ARGUMENT_FRAGMENT);
    Object xpathLocation = args.getArgumentValue(ARGUMENT_XPATH_LOCATION);
    Object relativeLocation = args.getArgumentValue(ARGUMENT_RELATIVE_LOCATION);
    
    Object argumentValue = args.getArgumentValue(ARGUMENT_GO_TO_NEXT_EDITABLE_POSITION);
    if (argumentValue == null) {
      argumentValue = AuthorConstants.ARG_VALUE_TRUE;
    }
    
    boolean goToFirstEditablePosition = AuthorConstants.ARG_VALUE_TRUE.equals(argumentValue);
    Object schemaAwareArgumentValue = args.getArgumentValue(SCHEMA_AWARE_ARGUMENT);
    
    doOperationInternal(authorAccess, fragment, xpathLocation, relativeLocation, goToFirstEditablePosition,
        schemaAwareArgumentValue);
  }

  /**
   * Performs the insert operation.
   * 
   * @param authorAccess The author access used to access the document.
   * @param fragment The fragment to be inserted.
   * @param xpathLocation The XPath location where the insertion takes place. If null, insert at caret position.
   * @param relativeLocation The location of the insertion relative to the node selected by the XPath.
   * @param goToFirstEditablePosition <code>true</code> if we should go to the first editable 
   *  position in the fragment after insertion.
   * @param schemaAwareArgumentValue <code>true</code> if the insertion should be schema aware.
   * 
   * @throws AuthorOperationException
   */
  protected void doOperationInternal(AuthorAccess authorAccess, Object fragment, Object xpathLocation,
      Object relativeLocation, boolean goToFirstEditablePosition, Object schemaAwareArgumentValue)
      throws AuthorOperationException {
    if (fragment instanceof String) {
      String xmlFragment = (String) fragment;
      
      //The XML may contain an editor template for caret positioning.
      boolean moveCaretToSpecifiedPosition =
        MoveCaretUtil.hasImposedEditorVariableCaretOffset(xmlFragment);
      int insertionOffset = authorAccess.getEditorAccess().getCaretOffset();
      
      if (AuthorConstants.ARG_VALUE_FALSE.equals(schemaAwareArgumentValue)) {
        // Insert fragment at specfied position.
        if (moveCaretToSpecifiedPosition || goToFirstEditablePosition) {
          //Compute the offset where the insertion will take place.
          if (xpathLocation != null && ((String)xpathLocation).trim().length() > 0) {
            // Evaluate the expression and obtain the offset of the first node from the result
            insertionOffset =
              authorAccess.getDocumentController().getXPathLocationOffset(
                  (String) xpathLocation, (String) relativeLocation);
          }
        }

        authorAccess.getDocumentController().insertXMLFragment(
            xmlFragment, (String) xpathLocation, (String) relativeLocation);                
      } else {
        // Insert fragment schema aware.
        SchemaAwareHandlerResult result =
          authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
              xmlFragment, (String) xpathLocation, (String) relativeLocation);
        //Keep the insertion offset.
        if (result != null) {
          Integer off = (Integer) result.getResult(
              SchemaAwareHandlerResultInsertConstants.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET);
          if (off != null) {
            insertionOffset = off.intValue(); 
          }
        }
      }
      
      if (moveCaretToSpecifiedPosition) {
        //Detect the position in the Author page where the caret should be placed.
        MoveCaretUtil.moveCaretToImposedEditorVariableOffset(authorAccess, insertionOffset);
      } else if (goToFirstEditablePosition) {
        // Position inside the first editable position if requested but do not 
        // exceed the already set caret position.
        try {
          int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
          authorAccess.getEditorAccess().goToNextEditablePosition(insertionOffset, caretOffset);
        } catch (BadLocationException e) {
          logger.error(e, e);
        }
      }
    } else {
      throw new IllegalArgumentException("The argument value was not defined, it is " + fragment);
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
    return "Insert a document fragment.";
  }
}