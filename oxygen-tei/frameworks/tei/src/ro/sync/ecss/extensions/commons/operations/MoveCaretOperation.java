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

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Author operation capable of moving the caret relative to an XML node
 * identified by an XPath expression. The operation can also select an
 * XML element or its content.
 * 
 * @author sorin_carbunaru
 * @author bogdan_dumitru
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(true)
public class MoveCaretOperation implements AuthorOperation {
  
  /**
   * Argument name. This argument specifies the location relative to which the caret is moved.
   */
  private static final String XPATH_LOCATION = "xpathLocation";
  
  /**
   * Argument name. This argument specifies the position relative to the node obtained from the XPath location
   * where the caret will be moved.
   */
  private static final String POSITION = "position";

  /**
   * Argument name. This argument specifies if the operation should select something related to the node
   * obtained from the XPath location.
   */
  private static final String SELECTION = "selection";
  
  /**
   * The descriptor for the "xpathLocation" argument.
   */
  private static final ArgumentDescriptor XPATH_LOCATION_ARGUMENT_DESCRIPTOR = 
      new ArgumentDescriptor(
          XPATH_LOCATION, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression identifying the node relative to which "
          + "the caret will be moved. If the expression identifies more than one node, "
          + "only the first one will be taken into account.", 
          ".");
  
  /**
   * The descriptor for the "position" argument.
   */
  private static final ArgumentDescriptor POSITION_DESCRIPTOR = 
      new ArgumentDescriptor(
          POSITION, 
          ArgumentDescriptor.TYPE_CONSTANT_LIST, 
          "The position relative to the node obtained from the XPath expression " + 
          "where the caret will be moved. When also choosing to perform a selection, "
          + "\"Before\" and \"Inside, at the beginning\" will both place the caret "
          + "at the beginning of the selection. In the same way, \"After\" and "
          + "\"Inside, at the end\" will place the caret at the end of the selection.", 
          new String[] {
              AuthorConstants.POSITION_BEFORE, 
              AuthorConstants.POSITION_AFTER,
              AuthorConstants.POSITION_INSIDE_AT_THE_BEGINNING,
              AuthorConstants.POSITION_INSIDE_AT_THE_END}, 
          AuthorConstants.POSITION_INSIDE_AT_THE_BEGINNING);
  
  /**
   * The descriptor for the "selection" argument.
   */
  private static final ArgumentDescriptor SELECTION_DESCRIPTOR = 
      new ArgumentDescriptor(
          SELECTION, 
          ArgumentDescriptor.TYPE_CONSTANT_LIST, 
          "This argument specifies if the operation should select the element " + 
          "obtained from the XPath expression, its content or nothing at all.", 
          new String[] {AuthorConstants.SELECT_NONE, 
              AuthorConstants.SELECT_CONTENT,           
              AuthorConstants.SELECT_ELEMENT}, 
          AuthorConstants.SELECT_NONE);
  
  /**
   * The arguments of the operation.
   */
  private static final ArgumentDescriptor[] ARGUMENTS =  new ArgumentDescriptor[] {
    XPATH_LOCATION_ARGUMENT_DESCRIPTOR, 
    POSITION_DESCRIPTOR, 
    SELECTION_DESCRIPTOR, 
  };
  
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Author operation capable of moving the caret relative to an XML node " + 
        "identified by an XPath expression. The operation can also select an " + 
        "XML element or its content.";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {

    String xpath = (String) args.getArgumentValue(XPATH_LOCATION);
    String position = (String) args.getArgumentValue(POSITION);
    String selection = (String) args.getArgumentValue(SELECTION);
    
    AuthorDocumentController ctrl = authorAccess.getDocumentController();
    AuthorNode[] xpathResult = ctrl.findNodesByXPath(xpath, true, true, true);
    
    if (xpathResult.length != 0) {
      // If there are several nodes returned by the XPath expression,
      // consider only the first one
      AuthorNode node = xpathResult[0];
      
      if (selection.equals(AuthorConstants.SELECT_NONE)) {
        if (position.equals(AuthorConstants.POSITION_BEFORE)) {
          // move caret before node
          authorAccess.getEditorAccess().setCaretPosition(node.getStartOffset());
        } else if (position.equals(AuthorConstants.POSITION_AFTER)) {
          // move caret after node
          authorAccess.getEditorAccess().setCaretPosition(node.getEndOffset() + 1);
        } else if (position.equals(AuthorConstants.POSITION_INSIDE_AT_THE_BEGINNING)) {
          // move caret inside the element, at the beginning
          authorAccess.getEditorAccess().setCaretPosition(node.getStartOffset() + 1);
        } else if (position.equals(AuthorConstants.POSITION_INSIDE_AT_THE_END)) {
          // move caret inside the element, at the end
          authorAccess.getEditorAccess().setCaretPosition(node.getEndOffset());
        }
      } else if (selection.equals(AuthorConstants.SELECT_CONTENT)) {
        // move caret and select content of node element
        if (position.equals(AuthorConstants.POSITION_BEFORE) ||
            position.equals(AuthorConstants.POSITION_INSIDE_AT_THE_BEGINNING)) {
          authorAccess.getEditorAccess().select(node.getEndOffset(), node.getStartOffset() + 1);
        } else {
          authorAccess.getEditorAccess().select(node.getStartOffset() + 1, node.getEndOffset());
        }
      } else if (selection.equals(AuthorConstants.SELECT_ELEMENT)) {
        // move caret and select node element
        if (position.equals(AuthorConstants.POSITION_BEFORE) ||
            position.equals(AuthorConstants.POSITION_INSIDE_AT_THE_BEGINNING)) {
          authorAccess.getEditorAccess().select(node.getEndOffset() + 1, node.getStartOffset());
        } else {
          authorAccess.getEditorAccess().select(node.getStartOffset(), node.getEndOffset() + 1);
        }
      }
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
