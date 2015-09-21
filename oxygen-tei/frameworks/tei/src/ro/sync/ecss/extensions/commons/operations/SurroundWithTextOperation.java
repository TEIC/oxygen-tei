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
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;

/**
 * Surround with text operation.
 */

@WebappCompatible
public class SurroundWithTextOperation implements AuthorOperation {
  /**
   * Argument describing the header.
   */
  private static final String ARGUMENT_HEADER = "header";
  
  /**
   * Argument describing the footer.
   */
  private static final String ARGUMENT_FOOTER = "footer";
  
  /**
   * Arguments.
   */
  private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
    new ArgumentDescriptor(
        ARGUMENT_HEADER,
        ArgumentDescriptor.TYPE_STRING,
        "The text to append before"),
    new ArgumentDescriptor(
        ARGUMENT_FOOTER,
        ArgumentDescriptor.TYPE_STRING,
        "The text to append after")
  };
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    //Header
    Object headerArgVal = args.getArgumentValue(ARGUMENT_HEADER);
    //Footer
    Object footerArgVal = args.getArgumentValue(ARGUMENT_FOOTER);

    if (headerArgVal != null
        && headerArgVal instanceof String
        && footerArgVal != null
        && footerArgVal instanceof String) {
      if (!authorAccess.getEditorAccess().hasSelection()) {
        //Select current word
        authorAccess.getEditorAccess().selectWord();
      }
      int selStart = authorAccess.getEditorAccess().getSelectionStart();
      int selEnd = authorAccess.getEditorAccess().getSelectionEnd();
      // Surround in two text sequences.
      authorAccess.getDocumentController().surroundInText(
          (String) headerArgVal, (String) footerArgVal, selStart, selEnd);
    } else {
      throw new IllegalArgumentException(
          "One or both of the argument values are not declared, they are: header - " + headerArgVal 
          + ", footer - " + footerArgVal);
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
    return "Surround a selection with text. Places a header at the start of the selection and a footer at the end." +
    		"If no selection exists, the word at caret will be surrounded.";
  }
}