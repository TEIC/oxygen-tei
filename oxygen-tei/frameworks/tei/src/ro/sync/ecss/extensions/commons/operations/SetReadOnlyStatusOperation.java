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
import ro.sync.exml.workspace.api.editor.ReadOnlyReason;

/**
 * Operation that sets the read-only status of a document.
 * 
 * @author cristi_talau
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class SetReadOnlyStatusOperation implements AuthorOperation {
  /**
   * The read-only status argument.
   * The value is <code>true</code> if the document should be made read-only.
   */
  public static final String ARGUMENT_READ_ONLY = "read-only";

  /**
   * The reason for the document being read-only.
   * 
   * If the document is set as read-only and the parameter is not specified, a deafult message will 
   * be presented to the user when trying to edit the document..
   */
  public static final String ARGUMENT_READ_ONLY_REASON = "reason";

  /**
   * The code for the reason for the document being read-only. It will be accessible through API.
   * 
   * The difference between this argument and {@link #ARGUMENT_READ_ONLY_REASON} is that this code does
   * not change with the UI language. 
   */
  public static final String ARGUMENT_READ_ONLY_CODE = "reason-code";
  
  /**
   * The arguments descriptor.
   */
  protected static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
    new ArgumentDescriptor(
      ARGUMENT_READ_ONLY, 
      ArgumentDescriptor.TYPE_CONSTANT_LIST,
      "Sets the read-only status of the current document." +
      "Can be: \n" +
      " - " + AuthorConstants.ARG_VALUE_TRUE + ": for making the document read-only\n" +
      " - " + AuthorConstants.ARG_VALUE_FALSE + ": for making the document writable\n" +
      "Default value is " + AuthorConstants.ARG_VALUE_TRUE + ".",
      new String[] {
          AuthorConstants.ARG_VALUE_TRUE,
          AuthorConstants.ARG_VALUE_FALSE,
      }, 
      AuthorConstants.ARG_VALUE_TRUE),
    new ArgumentDescriptor(
      ARGUMENT_READ_ONLY_REASON, 
      ArgumentDescriptor.TYPE_STRING,
      "The reason for the document being read-only. It will be displayed when the user tries to edit the document.\n" +
      "If not specified, a default message will be displayed to the user."),
    new ArgumentDescriptor(
        ARGUMENT_READ_ONLY_CODE, 
        ArgumentDescriptor.TYPE_STRING,
        "The code for the reason for the document being read-only. It will be accessible through API. Optional.")
    };


  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Changes the document read-only status.";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    boolean shouldMakeReadOnly = AuthorConstants.ARG_VALUE_TRUE.equals(
        "" + args.getArgumentValue(ARGUMENT_READ_ONLY));
    String reason = (String) args.getArgumentValue(ARGUMENT_READ_ONLY_REASON);
    String reasonCode = (String) args.getArgumentValue(ARGUMENT_READ_ONLY_CODE);
    
    if (shouldMakeReadOnly) {
      authorAccess.getEditorAccess().setReadOnly(new ReadOnlyReason(reason, reasonCode));
    } else {
      authorAccess.getEditorAccess().setEditable(!shouldMakeReadOnly);  
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }

}
