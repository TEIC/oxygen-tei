/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2016 Syncro Soft SRL, Romania.  All rights
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

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.common.CommonAccess;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorActionEventHandler;
import ro.sync.ecss.extensions.api.AuthorActionEventHandler.AuthorActionEventType;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;

/**
 * Operation that promotes or demotes a list item.
 * 
 * @author sorin_carbunaru
 * @author cristi_talau
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class PromoteDemoteItemOperation implements AuthorOperation {

  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(PromoteDemoteItemOperation.class.getName());
  
  /**
   * An XPath expression that identifies the content to be moved.
   */
  private static final String ARGUMENT_PROMOTE = "promote";
  
  /**
   * The arguments of the operation.
   */
  private static final ArgumentDescriptor[] arguments = {
      new ArgumentDescriptor(
          ARGUMENT_PROMOTE,
          ArgumentDescriptor.TYPE_CONSTANT_LIST,
          "Should be true to promote the list item at the caret position, or false to demote it.",
          new String[] {
              AuthorConstants.ARG_VALUE_TRUE,
              AuthorConstants.ARG_VALUE_FALSE,
          }, 
          AuthorConstants.ARG_VALUE_FALSE)  
  };
  
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Promotes or demotes a list item.";
  }
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    AuthorActionEventHandler eventHandler = CommonAccess.getAuthorActionEventHandler(authorAccess);
    if (eventHandler != null) {
      boolean promote = AuthorConstants.ARG_VALUE_TRUE.equals(args.getArgumentValue(ARGUMENT_PROMOTE));
      AuthorActionEventType eventType = promote ? AuthorActionEventType.SHIFT_TAB : AuthorActionEventType.TAB;
      
      boolean canHandleEvent = eventHandler.canHandleEvent(authorAccess, eventType);
      if (canHandleEvent) {
        eventHandler.handleEvent(authorAccess, eventType);
      }
    } else {
      logger.debug("No event handler set for the current framework.");
    }
    
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }


  
}
