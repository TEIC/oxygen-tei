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

import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.XPathVersion;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * <p>A base class for the operations that changes a pseudo-class from an element.</p>
 *  
 * @see TogglePseudoClassOperation
 * @see SetPseudoClassOperation
 * @see RemovePseudoClassOperation
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class PseudoClassOperation implements AuthorOperation {
  /**
   * Logger for logging. 
   */
  private static final Logger logger = LoggerFactory.getLogger(PseudoClassOperation.class.getName());
  
  /**
   * The pseudo class local name argument. The value is <code>name</code>.
   */
  private static final String ARGUMENT_PSEUDOCLASS_NAME = "name";
  
  /**
   * The XPath location that identifies the elements on which the pseudo-class will be set/reset.
   * Empty/null for the current element.
   * The value is <code>elementLocation</code>.
   */
  private static final String ARGUMENT_ELEMENT_XPATH_LOCATION = "elementLocation";
  
  /**
   * Argument name. The value should be <code>true</code> in order to include comments, text and CDATA nodes,
   * <code>false</code> to ignore them.
   */
  private static final String ARGUMENT_INCLUDE_ALL_NODES = "includeAllNodes";
 
  /**
   * Value for the "includeAllNodes" argument.
   */
  private static final String YES = "yes";
  
  /**
   * Value for the "includeAllNodes" argument.
   */
  private static final String NO = "no";
  
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor.
   */
  public PseudoClassOperation() {
    arguments = new ArgumentDescriptor[3];
    
    // Argument defining the element that will be modified.
    ArgumentDescriptor argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_ELEMENT_XPATH_LOCATION, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating the element or elements whose pseudo-class will be changed.\n"
          + "Note: If this is not defined then the element at the caret position will be used.");
    arguments[0] = argumentDescriptor;
    
    // Argument defining pseudo-class name that will be modified.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_PSEUDOCLASS_NAME,
        ArgumentDescriptor.TYPE_STRING,
        "The pseudo-class name.");
    arguments[1] = argumentDescriptor;    
    
    // The value should be true in order to include comments, text and CDATA nodes,
    // false to ignore them.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_INCLUDE_ALL_NODES,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "The value should be \"yes\" in order to include comments, text and CDATA nodes "
            + "in the XPath execution, \"no\" to ignore them.", 
        new String [] {YES, NO},
        NO);
    arguments[2] = argumentDescriptor;    
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
    throws AuthorOperationException {
    
    // The XPath location.
    Object xpathLocation = args.getArgumentValue(ARGUMENT_ELEMENT_XPATH_LOCATION);
    // The pseudo class name.
    Object name = args.getArgumentValue(ARGUMENT_PSEUDOCLASS_NAME);
    // The includeAllNodes argument
    Object includeAllNodes = args.getArgumentValue(ARGUMENT_INCLUDE_ALL_NODES);
      
    if (name instanceof String) {
      String pseudoClassName = ((String)name).trim();
      AuthorElement targetElement;
      if (xpathLocation instanceof String) {
        if (includeAllNodes instanceof String) {
          boolean includeAll = includeAllNodes.equals(YES); 
          AuthorNode[] results =
              authorAccess.getDocumentController().findNodesByXPath(
                  (String) xpathLocation,
                  null,
                  !includeAll,
                  !includeAll,
                  !includeAll,
                  false,
                  XPathVersion.XPATH_2_0,
                  true);
          if (results.length > 0) {
            for (int i = 0; i < results.length; i++) {
              AuthorNode node = results[i];
              if (node instanceof AuthorElement) {
                targetElement = (AuthorElement)node;
                execute(authorAccess, pseudoClassName, targetElement);
              } else {
                logger.warn("The XPath location selected a node different from element - " + node.getDisplayName() + ": " + xpathLocation);
              }
            }
          } else {
            logger.warn("The XPath location returned no results: " + xpathLocation);
          }
        }
      } else {
        AuthorNode node = null;
        try {
          node = authorAccess.getDocumentController().getNodeAtOffset(
              authorAccess.getEditorAccess().getCaretOffset());
        } catch (BadLocationException e) {
          throw new AuthorOperationException("Cannot identify the current element", e);
        }
        while (node != null && !(node instanceof AuthorElement)) {
          node = node.getParent();
        }
        if (node instanceof AuthorElement) {
          targetElement = (AuthorElement)node;
        } else {
          throw new AuthorOperationException("You need to have the carret inside an element.");
        }
        execute(authorAccess, pseudoClassName, targetElement);      
      }
    } else {
      throw new IllegalArgumentException("The argument \"name\" was not defined!");
    }
  }

  /**
   * Executes a pseudo-class change to an element.
   * 
   * @param authorAccess The access.
   * @param pseudoClassName The name of the pseudo class.
   * @param targetElement The element that is changed.
   */
  protected abstract void execute(AuthorAccess authorAccess, String pseudoClassName, AuthorElement targetElement);
 
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }
  
}