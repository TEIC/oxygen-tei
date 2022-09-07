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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;

/**
 * An implementation of an operation which runs a sequence of actions, defined as a list of IDs.
 * The actions must be defined by the corresponding framework, or one of the common actions for all frameworks
 * supplied by Oxygen.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(false)
public class ExecuteMultipleActionsOperation implements AuthorOperation {
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Actions IDs argument name.
   */
  public static final String ACTION_IDS = "actionIDs";

  /**
   * Constructor.
   */
  public ExecuteMultipleActionsOperation() {
    //The list of action IDs is a line separated list.
    arguments = new ArgumentDescriptor[1];
    ArgumentDescriptor argumentDescriptor = 
      new ArgumentDescriptor(
          ACTION_IDS, 
          ArgumentDescriptor.TYPE_STRING, 
          "The IDs of the actions that will be executed in sequence, separated either by new lines or by commas.");
    arguments[0] = argumentDescriptor;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Run a sequence of actions defined in the associated document type.";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    Object actionIDs = args.getArgumentValue(ACTION_IDS);
    List<Object> actions = getActions(authorAccess, actionIDs);
    for (Object action : actions) {
      authorAccess.getEditorAccess().getActionsProvider().invokeAction(action);
    }
  }

  /**
   * Get all the actions from this operation.
   *
   * @param authorAccess Author access.
   * @param actionIDs Action ids.
   * @return The list with all actions.
   * 
   * @throws IllegalArgumentException
   * @throws AuthorOperationException
   */
  public List<Object> getActions(AuthorAccess authorAccess, Object actionIDs) throws AuthorOperationException {
    List<Object> actions = new ArrayList<Object>();
    if (actionIDs != null) {
      //Split it.
      String ids = (String) actionIDs;
      String[] allIds = ids.contains(",") ? ids.split(",") : ids.split("\n");
      Map<String, Object> authorExtensionActions = authorAccess.getEditorAccess().getActionsProvider().getAuthorExtensionActions();
      Map<String, Object> authorCommonActions = authorAccess.getEditorAccess().getActionsProvider().getAuthorCommonActions();
      for (int i = 0; i < allIds.length; i++) {
        String id = allIds[i].trim();
        if (id.isEmpty()) {
          continue;
        }
        boolean actionFound = false;
        if (authorExtensionActions != null) {
          Object action = authorExtensionActions.get(id);
          if (action != null) {
            actionFound = true;
            actions.add(action);
          }
        }

        if (!actionFound && authorCommonActions != null) {
          Object action = authorCommonActions.get(id);
          if (action != null) {
            actionFound = true;
            actions.add(action);
          }
        }

        if (!actionFound) {
          throw new AuthorOperationException("Could not find an extension action with the ID: \'" + id + "\'");
        }
      }
    } else {
      throw new AuthorOperationException("The actions IDs were not specified as a parameter.");
    }
    
    return actions;
  }
  

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }
}