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

import java.io.File;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.exml.workspace.api.process.ProcessController;

/**
 * Author operation allowing the execution of command lines.
 * 
 * @author sorin_carbunaru
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(false)
public class ExecuteCommandLineOperation implements AuthorOperation {
  
  /**
   * true
   */
  private static final String TRUE_STRING = "true";
  
  /**
   * false
   */
  private static final String FALSE_STRING = "false";

  /**
   * The name of the operation.
   */
  private static final String NAME = "name";
  
  /**
   * The working directory where the command line is executed.
   */
  private static final String WORKING_DIRECTORY = "workingDirectory";
  
  /**
   * The command line to be executed.
   */
  private static final String CMD_LINE = "cmdLine";
  
  /**
   * <code>True</code> to show the console when running the command line.
   */
  private static final String SHOW_CONSOLE = "showConsole";

  /**
   * <code>True</code> to wait until the command terminates to return.
   */
  private static final String WAIT = "wait";
  
  /**
   * The descriptor of the "name" argument.
   */
  private static final ArgumentDescriptor NAME_ARGUMENT_DESCRIPTOR = new ArgumentDescriptor(
      NAME,
      ArgumentDescriptor.TYPE_STRING,
      "The name of the operation. It will also be used as the name of the console.",
      "Command Line Operation");
  
  /**
   * The descriptor of the "workingDirectory" argument.
   */
  private static final ArgumentDescriptor WORKING_DIRECTORY_ARGUMENT_DESCRIPTOR = new ArgumentDescriptor(
      WORKING_DIRECTORY,
      ArgumentDescriptor.TYPE_STRING,
      "The path to the working directory where the command line will be executed.",
      ".");

  /**
   * The descriptor of the "cmdLine" argument.
   */
  private static final ArgumentDescriptor CMD_LINE_ARGUMENT_DESCRIPTOR = new ArgumentDescriptor(
      CMD_LINE, 
      ArgumentDescriptor.TYPE_STRING, 
      "The command line to be executed. It can contain oXygen editor variables. "
      + "IMPORTANT: This operation is meant for executing programs. "
      + "Not any string that a command line interpreter (or shell) accepts can be run by "
      + "this operation."
      + "For example, the \"dir\" command line from Windows is not accepted as it is. "
      + "The accepted form is \"cmd /c dir\".");
  
  /**
   * The descriptor of the "showConsole" argument.
   */
  private static final ArgumentDescriptor SHOW_CONSOLE_ARGUMENT_DESCRIPTOR = new ArgumentDescriptor(
      SHOW_CONSOLE, 
      ArgumentDescriptor.TYPE_CONSTANT_LIST, 
      "True to show the console when running the command line.", 
      new String[] {TRUE_STRING, FALSE_STRING},
      FALSE_STRING);


  /**
   * The descriptor of the "wait" argument.
   */
  private static final ArgumentDescriptor WAIT_ARGUMENT_DESCRIPTOR = new ArgumentDescriptor(
      WAIT,
      ArgumentDescriptor.TYPE_CONSTANT_LIST,
      "True to wait until the command terminates to return.",
      new String[] {TRUE_STRING, FALSE_STRING},
      FALSE_STRING);

  /**
   * The arguments of the operation.
   */
  private static final ArgumentDescriptor[] ARGUMENTS =  new ArgumentDescriptor[] {
    NAME_ARGUMENT_DESCRIPTOR, 
    WORKING_DIRECTORY_ARGUMENT_DESCRIPTOR, 
    CMD_LINE_ARGUMENT_DESCRIPTOR, 
    SHOW_CONSOLE_ARGUMENT_DESCRIPTOR,
    WAIT_ARGUMENT_DESCRIPTOR
  };

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {

    // Get the arguments
    String name = (String) args.getArgumentValue(NAME);
    File workingDir = new File((String) args.getArgumentValue(WORKING_DIRECTORY));
    String cmdLine = (String) args.getArgumentValue(CMD_LINE);
    boolean showCmd = TRUE_STRING.equals(args.getArgumentValue(SHOW_CONSOLE));
    boolean wait = TRUE_STRING.equals(args.getArgumentValue(WAIT));

    // Now start the process
    if (wait) {
      // Create the process
      ProcessController processController = authorAccess.getWorkspaceAccess().createProcess(
          null,
          name,
          workingDir,
          cmdLine,
          showCmd);
      // This method blocks until the process ends.
      processController.start(); 
    } else {
      // Start the process (without waiting its end)
      authorAccess.getWorkspaceAccess().startProcess(
          name,
          workingDir,
          cmdLine,
          showCmd);
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Author operation allowing the execution of command lines.";
  }

}
