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

import javax.swing.text.BadLocationException;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * An implementation of an operation to change the value of an attribute.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class ChangeAttributeOperation implements AuthorOperation {
  /**
   * The attribute local name argument. The value is <code>name</code>.
   */
  private static final String ARGUMENT_ATTRIBUTE_NAME = "name";
  /**
   * The attribute namespace argument. The value is <code>namespace</code>.
   */
  private static final String ARGUMENT_ATTRIBUTE_NAMESPACE = "namespace";
  /**
   * The XPath location that identifies the element.
   * Empty/null for the current element.
   * The value is <code>elementLocation</code>.
   */
  private static final String ARGUMENT_ELEMENT_XPATH_LOCATION = "elementLocation";
  /**
   * The new value for the attribute - empty/null to remove it. The value is <code>value</code>.
   */
  private static final String ARGUMENT_VALUE = "value";
  /**
   * After changing the attribute, automatically enter in editing mode. Only possible
   * if an in-place editor exists for that attribute.
   */
  private static final String ARGUMENT_EDIT_ATTRIBUTE = "editAttribute";
  /**
   * If <code>true</code> it removes the attribute if an empty value is provided.
   * If <code>false</code> it will add/keep the attribute even if the value is empty. 
   */
  private static final String ARGUMENT_REMOVE_IF_EMPTY_VALUE = "removeIfEmpty";
  
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor.
   */
  public ChangeAttributeOperation() {
    arguments = new ArgumentDescriptor[6];
    
    // Argument defining the element that will be modified.
    ArgumentDescriptor argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_ELEMENT_XPATH_LOCATION, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating the element whose attribute will be changed.\n"
          + "Note: If it is not defined then the element at the caret position will be used.");
    arguments[0] = argumentDescriptor;
    
    
    // Argument defining the attribute name that will be inserted.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_ATTRIBUTE_NAME,
        ArgumentDescriptor.TYPE_STRING,
        "The attribute local name.");
    arguments[1] = argumentDescriptor;
    
    // Argument defining the attribute namespace that will be inserted.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_ATTRIBUTE_NAMESPACE,
        ArgumentDescriptor.TYPE_STRING,
        "The attribute namespace. Leave it empty for no namespace.");
    arguments[2] = argumentDescriptor;
    
    // Argument defining the attribute value that will be inserted.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_VALUE,
        ArgumentDescriptor.TYPE_STRING,
        "The attribute value. Set it empty to remove the attribute.");
    arguments[3] = argumentDescriptor;
    
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_REMOVE_IF_EMPTY_VALUE,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "True means that the attribute should be removed if an empty value is provided. " +
        "The default behavior is to remove it.",
        new String[] {
            AuthorConstants.ARG_VALUE_TRUE,
            AuthorConstants.ARG_VALUE_FALSE,
        }, 
        AuthorConstants.ARG_VALUE_TRUE);
    arguments[4] = argumentDescriptor;
    
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_EDIT_ATTRIBUTE,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "If an in-place editor exists for this attribute, it will automatically " +
        "activate the in-pace editor and it will start editing.",
        new String[] {
            AuthorConstants.ARG_VALUE_TRUE,
            AuthorConstants.ARG_VALUE_FALSE,
        }, 
        AuthorConstants.ARG_VALUE_TRUE);
    arguments[5] = argumentDescriptor;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
    throws AuthorOperationException {
    
    // The value.
    Object value = args.getArgumentValue(ARGUMENT_VALUE);
    // The XPath location.
    Object xpathLocation = args.getArgumentValue(ARGUMENT_ELEMENT_XPATH_LOCATION);
    // The name.
    Object name = args.getArgumentValue(ARGUMENT_ATTRIBUTE_NAME);
    // The namespace.
    Object namespaceObj = args.getArgumentValue(ARGUMENT_ATTRIBUTE_NAMESPACE);
      
    if (name instanceof String) {
      AuthorElement targetElement;
      if (xpathLocation instanceof String) {
        AuthorNode[] results =
          authorAccess.getDocumentController().findNodesByXPath((String) xpathLocation, true, true, true);
        if (results.length > 0 && results[0] instanceof AuthorElement) {
          targetElement = (AuthorElement) results[0];          
        } else {
          throw new AuthorOperationException("The element XPath location does not identify an element: " + xpathLocation);
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
      }
      
      String attributeName = ((String)name).trim();
      String namespace = null;
      if (namespaceObj instanceof String) {
        namespace = ((String)namespaceObj).trim();
      }
      
      // now we identified a targetElement.
      Object removeIfEmpty = args.getArgumentValue(ARGUMENT_REMOVE_IF_EMPTY_VALUE);
      if (removeIfEmpty == null) {
        removeIfEmpty = AuthorConstants.ARG_VALUE_TRUE;
      }
      
      Object editAttribute = args.getArgumentValue(ARGUMENT_EDIT_ATTRIBUTE);
      if (editAttribute == null) {
        editAttribute = AuthorConstants.ARG_VALUE_TRUE;
      }
      
      boolean editAttributeBoolean = AuthorConstants.ARG_VALUE_TRUE.equals(editAttribute);
      
      String usedAttributeQName = CommonsOperationsUtil.setAttributeValue(
          authorAccess.getDocumentController(), 
          targetElement, 
          new QName(namespace, attributeName, XMLConstants.DEFAULT_NS_PREFIX),
          (String) value,
          AuthorConstants.ARG_VALUE_TRUE.equals(removeIfEmpty));
      
      if (!(value == null 
          || ("".equals(value) 
              && AuthorConstants.ARG_VALUE_TRUE.equals(removeIfEmpty)))) {
        if (editAttributeBoolean) {
          // Position inside the first editable position if requested.
          authorAccess.getEditorAccess().editAttribute(targetElement, usedAttributeQName);
        }
      }
    } else {
      throw new IllegalArgumentException("The argument \"name\" was not defined!");
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
   */
  @Override
  public String getDescription() {
    return "Add/modify/delete an attribute.";
  }
}