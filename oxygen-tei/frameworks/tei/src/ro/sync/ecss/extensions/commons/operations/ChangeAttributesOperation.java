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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Operation that can change/insert/remove one or more attributes of one or more elements.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class ChangeAttributesOperation implements AuthorOperation {
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(ChangeAttributesOperation.class.getName());
  /**
   * The name of the argument that provides the names of the attributes to change/insert/remove.
   */
  static final String ARGUMENT_ATTRIBUTE_NAMES = "attributeNames";
  /**
   * The name of the argument that provides the values of the attributes to change/insert/remove.
   */
  static final String ARGUMENT_ATTRIBUTE_VALUES = "values";
  /**
   * The name of the argument that provides the elements whose attributes to change/insert/remove.
   * The value of the argument is an XPath expression.<br/><br/>
   * Empty or <code>null</code> for the current element.
   */
  static final String ARGUMENT_ELEMENTS_XPATH_LOCATIONS = "elementLocations";
  /**
   * If <code>true</code>, it removes an attribute if an empty value is provided for it.<br/>
   * If <code>false</code>, it will add/keep an attribute even if its value is empty. 
   */
  static final String ARGUMENT_REMOVE_IF_EMPTY_VALUE = "removeIfEmpty";
  /**
   * The argument descriptors of this operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor.
   */
  public ChangeAttributesOperation() {
    arguments = new ArgumentDescriptor[4];
    
    ArgumentDescriptor argDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_ELEMENTS_XPATH_LOCATIONS, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating the elements whose attributes will be affected.\n"
          + "Note: If it is not defined, then the element at the caret position will be used.");
    arguments[0] = argDescriptor;
    
    argDescriptor = new ArgumentDescriptor(
        ARGUMENT_ATTRIBUTE_NAMES,
        ArgumentDescriptor.TYPE_STRING,
        "The names of the attributes to change/insert/remove, each on a new line."
        + " The provided values can be local names or Clark notations {attribute_namespace}attibute_name.");
    arguments[1] = argDescriptor;
    
    argDescriptor = new ArgumentDescriptor(
        ARGUMENT_ATTRIBUTE_VALUES,
        ArgumentDescriptor.TYPE_STRING,
        "The attribute values, each on a new line. An empty value will remove"
        + " the corresponding attribute if 'removeIfEmpty' is set to 'true' (it is by default).");
    arguments[2] = argDescriptor;
    
    argDescriptor = new ArgumentDescriptor(
        ARGUMENT_REMOVE_IF_EMPTY_VALUE,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "'true' means the attribute should be removed if an empty value is provided. " +
        "The default value is 'true'.",
        new String[] {
            AuthorConstants.ARG_VALUE_TRUE,
            AuthorConstants.ARG_VALUE_FALSE,
        }, 
        AuthorConstants.ARG_VALUE_TRUE);
    arguments[3] = argDescriptor;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    AuthorDocumentController docController = authorAccess.getDocumentController();

    Object names = args.getArgumentValue(ARGUMENT_ATTRIBUTE_NAMES);
    Object values = args.getArgumentValue(ARGUMENT_ATTRIBUTE_VALUES);
    Object xpathLocations = args.getArgumentValue(ARGUMENT_ELEMENTS_XPATH_LOCATIONS);

    if (names instanceof String && !((String) names).isEmpty()) {
      // Attributes as Clark notations ({namespace}name).
      String attrNamesString = ((String) names).trim();
      String[] splitAttrNames = attrNamesString.split("\n");
      String[] attrNames = new String[splitAttrNames.length];
      String[] attrNamespaces = new String[splitAttrNames.length];
      for (int i = 0; i < splitAttrNames.length; i++) {
        String token = splitAttrNames[i].trim();
        if (token.startsWith("{") && token.contains("}")) {
          if (token.indexOf('}') < token.length() - 1) {
            attrNames[i] = token.substring(token.indexOf('}') + 1).trim();
            attrNamespaces[i] = token.substring(token.indexOf('{') + 1, token.indexOf('}')).trim();
          } else {
            throw new AuthorOperationException("Incorrect Clark notation. The attribute name "
                + "must be specified after the namespace value '{" 
                + token.substring(token.indexOf('{') + 1, token.indexOf('}')).trim() + "}'.");
          }
        } else {
          attrNames[i] = token.trim();
          attrNamespaces[i] = "";
        }
      }
      
      // Attribute values
      String[] attrValues = null;
      if (values instanceof String) {
        // Don't trim. Allow empty values for removing an attribute value.
        String attrValuesString = ((String) values);
        attrValues = attrValuesString.split("\n");
      }

      // Perhaps remove attribute if value is empty
      Object removeIfEmpty = args.getArgumentValue(ARGUMENT_REMOVE_IF_EMPTY_VALUE);
      if (removeIfEmpty == null) {
        removeIfEmpty = AuthorConstants.ARG_VALUE_TRUE;
      }

      // The elements whose attributes to edit
      AuthorElement[] targetElements = detectTargetElements(authorAccess, xpathLocations);
      // Do edit the attribute
      for (AuthorElement elem : targetElements) {
        for (int i = 0; i < attrNames.length; i++) {
          CommonsOperationsUtil.setAttributeValue(
              docController, 
              elem, 
              new QName(
                  attrNamespaces[i],
                  attrNames[i],
                  XMLConstants.DEFAULT_NS_PREFIX),
              attrValues != null && i < attrValues.length ? attrValues[i].trim() : "",
              AuthorConstants.ARG_VALUE_TRUE.equals(removeIfEmpty));
        }
      }
    } else {
      throw new IllegalArgumentException("The argument 'attributeNames' was not defined!");
    }
  }

  /**
   * Detect the target elements whose attributes to edit.
   * 
   * @param authorAccess    Access to Author functionality.
   * @param xpathLocations  The XPath location provided by the user for the target elements.
   * 
   * @return the elements returned by the XPath expression or the element at caret.
   * 
   * @throws AuthorOperationException
   */
  private static AuthorElement[] detectTargetElements(
      AuthorAccess authorAccess, 
      Object xpathLocations)
      throws AuthorOperationException {
    AuthorElement[] targetElements = null;
    AuthorDocumentController docController = authorAccess.getDocumentController();
    if (xpathLocations instanceof String && !((String) xpathLocations).isEmpty()) {
      List<AuthorNode> results = Arrays.asList(
          docController.findNodesByXPath((String) xpathLocations, true, true, true));
      if (!results.isEmpty()) {
        Iterator<AuthorNode> iterator = results.iterator();
        while (iterator.hasNext()) {
          AuthorNode next = iterator.next();
          if (!(next instanceof AuthorElement)) {
            iterator.remove();
            logger.warn("The current node is not an element. It won't be taken into account. Node: " + next);
          }
        }
        targetElements = results.toArray(new AuthorElement[results.size()]);
      } else {
        throw new AuthorOperationException("The element XPath location does not identify any elements: " + xpathLocations);
      }
    } else {
      // XPath location is null. Try to get node at offset.
      targetElements = detectElementAtOffset(authorAccess);
    }
    return targetElements;
  }

  /**
   * Detect element at offset.
   * 
   * @param authorAccess Access to Author API.
   * 
   * @return Element at offset.
   * 
   * @throws AuthorOperationException if detection could not be done (wrong caret location).
   */
  private static AuthorElement[] detectElementAtOffset(AuthorAccess authorAccess)
      throws AuthorOperationException {
    AuthorNode node = null;
    try {
      node = authorAccess.getDocumentController()
          .getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset());
    } catch (BadLocationException e) {
      throw new AuthorOperationException("Cannot identify the current element.", e);
    }
    while (node != null && !(node instanceof AuthorElement)) {
      node = node.getParent();
    }
    
    AuthorElement[] targetElements = null;
    if (node instanceof AuthorElement) {
      targetElements = new AuthorElement[] {(AuthorElement) node};
    } else {
      throw new AuthorOperationException("You need to have the caret inside an element.");
    }
    
    return targetElements;
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
    return "Add/change/remove one or more attributes for one or more elements.";
  }
}