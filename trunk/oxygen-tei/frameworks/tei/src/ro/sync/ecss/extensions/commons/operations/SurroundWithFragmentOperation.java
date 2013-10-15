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

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

/**
 * Surround with fragment operation. If selection exists the selected fragment is surrounded in a given fragment. If no selection exists,
 * the given fragment is simply inserted at the given position. In this case the insertion can be schema aware.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class SurroundWithFragmentOperation implements AuthorOperation {
  /**
   * The name of the fragment element.
   */
  private static final String ARGUMENT_NAME = "fragment";
  
  /**
   * The arguments array.
   */
  private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
    new ArgumentDescriptor(
        ARGUMENT_NAME,
        ArgumentDescriptor.TYPE_FRAGMENT,
    "The fragment to surround with. The first leaf will be the destination of the text to surround."),
    new ArgumentDescriptor(
        SCHEMA_AWARE_ARGUMENT, 
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "Controlling if the insertion is schema aware or not. " +
        "When the schema aware is enabled and the fragments insertion is not allowed a dialog will be shown, proposing solutions, like:\n" +
        " - insert the fragments inside a new element. The name of the element to wrap the fragments in is computed by analyzing the left or right siblings;\n" + 
        " - split an ancestor of the node at insertion offset and insert the fragments between the resulted elements;\n" +
        " - insert the fragments somewhere in the proximity of the insertion offset(left or right without skipping content);\n" + 
        "Note: if a selection exists the surround with fragment operation is not schema aware.\n" + 
        "Can be: " 
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
    Object argVal = args.getArgumentValue(ARGUMENT_NAME);
    if (argVal != null && argVal instanceof String) {
      Object schemaAwareArgumentValue = args.getArgumentValue(SCHEMA_AWARE_ARGUMENT);
      boolean schemaAware = AuthorConstants.ARG_VALUE_FALSE.equals(schemaAwareArgumentValue) ? false : true;
      // If there is a selection, surround the selection with the given fragment else 
      // try to insert the fragment at caret
      CommonsOperationsUtil.surroundWithFragment(authorAccess, schemaAware, (String) argVal);
    } else {
      throw new IllegalArgumentException("The argument value was not defined, it is " + argVal);
    }
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
    return "Surround the selected text with a document fragment. "
            + "If there is no selection the fragment will be inserted at the current position.";
  }
}