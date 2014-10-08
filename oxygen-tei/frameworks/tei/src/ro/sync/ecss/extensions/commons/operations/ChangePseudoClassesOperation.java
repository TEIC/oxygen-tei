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

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * An implementation of an operation to set a list of pseudo class values to nodes identified by an XPath expression
 * and to remove a list of values from nodes identified by an XPath expression.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class ChangePseudoClassesOperation  implements AuthorOperation {
  
  /**
   * Set XPath locations.
   */
  private static final String ARGUMENT_ELEMENT_SET_XPATH_LOCATIONS = "setLocations";
  /**
   * Set pseudo class names
   */
  private static final String ARGUMENT_SET_PSEUDOCLASS_NAMES = "setPseudoClassNames";
  /**
   * Remove XPath locations
   */
  private static final String ARGUMENT_ELEMENT_REMOVE_XPATH_LOCATIONS = "removeLocations";
  /**
   * Remove pseudo class names.
   */
  private static final String ARGUMENT_REMOVE_PSEUDOCLASS_NAMES = "removePseudoClassNames";
  /**
   * The array of arguments.
   */
  private ArgumentDescriptor[] arguments;

  /**
   * Constructor.
   */
  public ChangePseudoClassesOperation() {

    arguments = new ArgumentDescriptor[4];
    
    // Set XPath locations.
    ArgumentDescriptor argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_ELEMENT_SET_XPATH_LOCATIONS, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating a list of nodes on which the specified list of pseudo classes will be set.\n"
          + "Note: If it is not defined, then the element at the caret position will be used.");
    arguments[0] = argumentDescriptor;
    
    // A space-separated list of pseudo class names which will be set on the matched nodes.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_SET_PSEUDOCLASS_NAMES,
        ArgumentDescriptor.TYPE_STRING,
        "A space-separated list of pseudo class names which will be set on the matched nodes.");
    arguments[1] = argumentDescriptor;    
  
    // An XPath expression indicating a list of nodes from which the specified list of pseudo classes will be removed.
    argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_ELEMENT_REMOVE_XPATH_LOCATIONS, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating a list of nodes from which the specified list of pseudo classes will be removed.\n"
          + "Note: If it is not defined, then the element at the caret position will be used.");
    arguments[2] = argumentDescriptor;
    
    // A space-separated list of pseudo class names which will be removed from the matched nodes.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_REMOVE_PSEUDOCLASS_NAMES,
        ArgumentDescriptor.TYPE_STRING,
        "A space-separated list of pseudo class names which will be removed from the matched nodes.");
    arguments[3] = argumentDescriptor;    
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
   */
  @Override
  public String getDescription() {
    return "Sets a set of pseudo classes to all nodes identified by an XPath expression.\n"
        + "Removes a set of pseudo classes from all nodes identified by an XPath expression.";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    //Set
    setOrRemovePseudoClasses(authorAccess, args, ARGUMENT_ELEMENT_SET_XPATH_LOCATIONS, ARGUMENT_SET_PSEUDOCLASS_NAMES, true);
    //Remove
    setOrRemovePseudoClasses(authorAccess, args, ARGUMENT_ELEMENT_REMOVE_XPATH_LOCATIONS, ARGUMENT_REMOVE_PSEUDOCLASS_NAMES, false);
  }
  
  /**
   * Set or remove a list of pseudo classes from a list of nodes identified by an XPath location.
   * @param authorAccess The Author APi Access.
   * @param args Arguments map.
   * @param xpathLocationKey Xpath locations key
   * @param pseudoClassNamesKey Pseudo class names key
   * @param setClasses <code>true</code> to set the classes, <code>false</code> to remove them.
   * @throws AuthorOperationException
   */
  private static void setOrRemovePseudoClasses(AuthorAccess authorAccess, 
      ArgumentsMap args, String xpathLocationKey, String pseudoClassNamesKey, boolean setClasses) throws AuthorOperationException{
    // The Set XPath location.
    Object xpathLocations = args.getArgumentValue(xpathLocationKey);
    // The pseudo class name.
    Object pseudoClassNames = args.getArgumentValue(pseudoClassNamesKey);
      
    if (pseudoClassNames instanceof String) {
      String[] splitPseudoClasses = ((String) pseudoClassNames).split(" ");
      if(splitPseudoClasses != null && splitPseudoClasses.length > 0){
        List<AuthorElement> targetElements = new ArrayList<AuthorElement>();
        if (xpathLocations instanceof String && ((String)xpathLocations).trim().length() > 0) {
          AuthorNode[] results =
              authorAccess.getDocumentController().findNodesByXPath((String) xpathLocations, true, true, true);
          for (int i = 0; i < results.length; i++) {
            AuthorNode authorNode = results[i];
            if(authorNode.getType() == AuthorNode.NODE_TYPE_ELEMENT){
              targetElements.add((AuthorElement) authorNode);
            }
          }
          
          if(targetElements.size() == 0) {
            throw new AuthorOperationException("The element XPath location does not identify an element: " + xpathLocations);
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
            targetElements.add((AuthorElement) node);
          } else {
            throw new AuthorOperationException("You need to have the carret inside an element.");
          }
        }
        
        //Iterate the target elements and set the pseudo classes to them.
        for (int i = 0; i < targetElements.size(); i++) {
          AuthorElement targetElement = targetElements.get(i);
          for (int j = 0; j < splitPseudoClasses.length; j++) {
            if(setClasses){
              //Set
              authorAccess.getDocumentController().setPseudoClass(splitPseudoClasses[j], targetElement);
            } else {
              //Remove
              authorAccess.getDocumentController().removePseudoClass(splitPseudoClasses[j], targetElement);
            }
          }
        }
      }
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