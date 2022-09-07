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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.ExecuteMultipleActionsWithExtraAskValuesOperation;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.editor.AuthorExtensionAskAction;

/**
 * An implementation of an operation which runs a sequence of webapp-compatible
 * ({@link WebappCompatible}) actions, defined as a list of IDs.
 * 
 * This class is also marked as webapp-compatible.
 * 
 * The actions must be defined by the corresponding framework, or one of the common actions for all frameworks
 * supplied by Oxygen.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class ExecuteMultipleWebappCompatibleActionsOperation extends ExecuteMultipleActionsOperation 
implements ExecuteMultipleActionsWithExtraAskValuesOperation {
  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteMultipleWebappCompatibleActionsOperation.class.getName());
  
  /**
   * Do the operation taking into account the provided asks variables values.
   * 
   * @param authorAccess The Author access.
   * @param args The arguments.
   * @param asksValues The list of expanded asks variables for all inner actions.
   * @throws AuthorOperationException
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args, List<String> asksValues)
      throws AuthorOperationException {
    Object actionIDs = args.getArgumentValue(ACTION_IDS);
    Deque<String> asks = asksValues == null ? null : new ArrayDeque<>(asksValues);
    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug("Asks values: " + asksValues);
    }
    List<Object> actions = getActions(authorAccess, actionIDs);
    
    for (Object action : actions) {
      if (asks != null && !asks.isEmpty() && action instanceof AuthorExtensionAskAction) {
        AuthorExtensionAskAction askAction = (AuthorExtensionAskAction) action;
        List<String> actionAsksList = new ArrayList<>();
        int asksVariablesCount = askAction.countAsksVariables();
        if (asksVariablesCount > 0) {
          if (asksVariablesCount <= asks.size()) {
            for (int i = 0; i < asksVariablesCount; i++) {
              actionAsksList.add(asks.removeFirst());
            }
          } 
        }
        askAction.performActionWithValues(actionAsksList);
      } else {
        authorAccess.getEditorAccess().getActionsProvider().invokeAction(action);
      }
    }
  }
  
  /**
   * @see ro.sync.ecss.extensions.ExecuteMultipleActionsWithExtraAskValuesOperation#getActions(ro.sync.ecss.extensions.api.AuthorAccess, java.util.Map)
   */
  @Override
  public List<Object> getActions(AuthorAccess authorAccess, Map args)
      throws AuthorOperationException {
    Object actionIDs = args.get(ExecuteMultipleWebappCompatibleActionsOperation.ACTION_IDS);
    return getActions(authorAccess, actionIDs);
  }
}
