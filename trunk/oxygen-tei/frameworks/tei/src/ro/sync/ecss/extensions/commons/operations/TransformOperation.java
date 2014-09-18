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

import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentType;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.util.editorvars.EditorVariables;

/**
 * An implementation of an operation to apply a script (XSLT or XQuery) on a element and replacing it with
 * the result of the transformation or inserting the result in the document.
 */
@API(type=APIType.EXTENDABLE, src=SourceType.PUBLIC)
public abstract class TransformOperation implements AuthorOperation {
  
  /**
   * Logger for logging. 
   */
  private static final Logger logger = Logger.getLogger(TransformOperation.class.getName());
  
  /**
   * The name of a parameter containing the location path of the current element inside the
   * source element. This can be accessed in the script to perform context sensitive actions. 
   */
  public static final String CURRENT_ELEMENT_LOCATION = "currentElementLocation";
  
  /**
   * The name of the operation action indicating a replace of the target node with the result of
   * the transformation.
   */
  public final static String ACTION_REPLACE = "Replace";
  
  /**
   * The name of the operation action indicating that the transformation result should be inserted 
   * at the caret position.
   */
  public final static String ACTION_AT_CARET = "At caret position";

  /**
   * The name of the operation action indicating that the transformation result should be inserted 
   * before the target node.
   */
  public final static String ACTION_INSERT_BEFORE = AuthorConstants.POSITION_BEFORE;

  /**
   * The name of the operation action indicating that the transformation result should be inserted 
   * after the target node.
   */
  public final static String ACTION_INSERT_AFTER = AuthorConstants.POSITION_AFTER;
  
  /**
   * The name of the operation action indicating that the transformation result should be inserted 
   * as the first child of the target node.
   */
  public final static String ACTION_INSERT_AS_FIRST_CHILD = AuthorConstants.POSITION_INSIDE_FIRST;

  /**
   * The name of the operation action indicating that the transformation result should be inserted 
   * as the last child of the target node.
   */
  public final static String ACTION_INSERT_AS_LAST_CHILD = AuthorConstants.POSITION_INSIDE_LAST;
  
  /**
   * Constant for the caret position indicating that the same caret position offset should be preserved.
   */
  public final static String CARET_POSITION_PRESERVE = "Preserve";

  /**
   * Constant for the caret position indicating that the caret should be positioned just before the
   * inserted fragment.
   */
  public final static String CARET_POSITION_BEFORE = "Before";

  /**
   * Constant for the caret position indicating that the caret should be positioned just at the
   * start of the inserted fragment, inside that fragment.
   */
  public final static String CARET_POSITION_START = "Start";
  
  /**
   * Constant for the caret position indicating that the caret should be positioned just at the
   * start of the inserted fragment, in the first editable position.
   */
  public final static String CARET_POSITION_EDITABLE = "First editable position";  

  /**
   * Constant for the caret position indicating that the caret should be positioned just at the
   * end of the inserted fragment, inside that fragment.
   */
  public final static String CARET_POSITION_END = "End";

  /**
   * Constant for the caret position indicating that the caret should be positioned just after the
   * inserted fragment.
   */
  public final static String CARET_POSITION_AFTER = "After";
  
  /**
   * The XPath location that identifies the source element.
   * Empty/null for the current element.
   * The value is <code>sourceLocation</code>.
   */
  private static final String ARGUMENT_XPATH_SOURCE = "sourceLocation";
  
  /**
   * The XPath location that identifies the target node.
   * This target is the reference for the action that will be executed with 
   * the transformation result.
   * Empty/null for the current node.
   * The value is <code>targetLocation</code>.
   */
  private static final String ARGUMENT_XPATH_TARGET = "targetLocation";
  
  /**
   * The XSLT or XQuery script. The value is <code>script</code>.
   */
  protected String ARGUMENT_SCRIPT = "script";
  
  /**
   * The action to be executed on the target (replace, insert at caret, insert after, 
   * insert before, insert as first child, insert as last child. 
   * The value is <code>action</code>.
   */
  private static final String ARGUMENT_ACTION = "action";
  
  /**
   * The caret position after the action is executed. 
   * The value is <code>caretPosition</code>.
   */
  private static final String ARGUMENT_CARET_POSITION = "caretPosition";
  
  /**
   * The parameter for controlling the expansion of editor variables. <code>true</code> by default 
   * The value is <code>expandEditorVariables</code>.
   */
  private static final String ARGUMENT_EXPAND_EDITOR_VARIABLES = "expandEditorVariables";
  
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor.
   */
  public TransformOperation() {
    arguments = new ArgumentDescriptor[6];
    
    // Argument defining the element that will be the source of transformation.
    ArgumentDescriptor argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_XPATH_SOURCE, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating the element that the script will be applied on.\n"
          + "Note: If it is not defined then the element at the caret position will be used.");
    arguments[0] = argumentDescriptor;
    
    
    // Argument defining the target node that will be used as a reference for action.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_XPATH_TARGET,
        ArgumentDescriptor.TYPE_XPATH_EXPRESSION,
        "An XPath expression indicating the insert location for the result of the transformation.\n" +
        "Note: If it is not defined then the insert location will be at the caret.");
    arguments[1] = argumentDescriptor;
    
    // Argument defining the script that will be executed.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_SCRIPT,
        ArgumentDescriptor.TYPE_SCRIPT,
        "The script. The base system ID for this will be the framework file, so any include/import " +
        "reference will be resolved relative to the \".framework\" file that contains this action definition");
    arguments[2] = argumentDescriptor;
    
    // Argument defining the action that will be executed.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_ACTION,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "The insert action relative to the node determined by the target XPath expression.\n" +
        "It can be: " 
        + ACTION_REPLACE + ", " +
        ACTION_AT_CARET + ", " +
        ACTION_INSERT_BEFORE + ", " +
        ACTION_INSERT_AFTER + ", " +
        ACTION_INSERT_AS_FIRST_CHILD + " or " +
        ACTION_INSERT_AS_LAST_CHILD + ".\n",
        new String[] {
            ACTION_REPLACE,
            ACTION_AT_CARET,
            ACTION_INSERT_BEFORE,
            ACTION_INSERT_AFTER,
            ACTION_INSERT_AS_FIRST_CHILD,
            ACTION_INSERT_AS_LAST_CHILD
        }, 
        ACTION_REPLACE);    
    arguments[3] = argumentDescriptor;
    
    // Argument defining the caret position after the action.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_CARET_POSITION,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "The position of the caret after the action is executed.\n" +
        "It can be: "             
        + CARET_POSITION_PRESERVE +", " +
        CARET_POSITION_BEFORE + ", " +
        CARET_POSITION_START + ", " +
        CARET_POSITION_EDITABLE +", " +  
        CARET_POSITION_END + " or " +
        CARET_POSITION_AFTER + ".\n",
        new String[] {
            CARET_POSITION_PRESERVE,
            CARET_POSITION_BEFORE,
            CARET_POSITION_START,
            CARET_POSITION_EDITABLE,  
            CARET_POSITION_END,
            CARET_POSITION_AFTER
        }, 
        CARET_POSITION_EDITABLE);
    arguments[4] = argumentDescriptor;
    
    // Argument defining if the editor variables in the returned result can be edited.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_EXPAND_EDITOR_VARIABLES,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "Parameter controlling the expansion of editor variables returned by the script processing.\n" +
        "Expansion is enabled by default.",
        new String[] { "true", "false" }, 
        "true");
    arguments[5] = argumentDescriptor;
  }

  /**
   * Applies the transformation and executes the specified action with the result.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
    throws AuthorOperationException {
    
    // The source XPath location.
    Object xpathSource = args.getArgumentValue(ARGUMENT_XPATH_SOURCE);
    // The target XPath location.
    Object xpathTarget = args.getArgumentValue(ARGUMENT_XPATH_TARGET);
    // The script.
    Object xscript = args.getArgumentValue(ARGUMENT_SCRIPT);
    // The action.
    Object action = args.getArgumentValue(ARGUMENT_ACTION);
    // The caret position after the operation.
    Object caretPosition = args.getArgumentValue(ARGUMENT_CARET_POSITION);
    
    if (!(xscript instanceof String)) {
        throw new IllegalArgumentException("The argument \"script\" was not defined as a string object!");
    }
  
    // Get the XSLT script.
    String script = (String)xscript;

    // Get the current node and the current element 
    // (they may be different if the current node is not an element)
    AuthorNode currentNode = null;
    AuthorElement currentElement = null;
    AuthorNode node = null;
    try {
      node = authorAccess.getDocumentController().getNodeAtOffset(
          authorAccess.getEditorAccess().getCaretOffset());
    } catch (BadLocationException e) {
      throw new AuthorOperationException("Cannot identify the current node", e);
    }
    currentNode = node;
    while (node != null && !(node instanceof AuthorElement)) {
      node = node.getParent();
    }
    if (node instanceof AuthorElement) {
      currentElement = (AuthorElement)node;
    } else {
      currentElement = authorAccess.getDocumentController().getAuthorDocumentNode().getRootElement();
    }
    
    // Get the source, this will be the input for the script
    AuthorElement sourceElement;
    if (xpathSource instanceof String && !"".equals(xpathSource)) {
      AuthorNode[] results =
        authorAccess.getDocumentController().findNodesByXPath((String) xpathSource, true, true, false);
      if (results.length > 0 && results[0] instanceof AuthorElement) {
        sourceElement = (AuthorElement) results[0];
      } else {
        throw new AuthorOperationException("The source XPath location does not identify an element: " + xpathSource);
      }
    } else {
      // If no source parameter is specified we will get the current element.
      sourceElement = currentElement;
    }
    
    String currentElementLocation ="";
    
    AuthorNode tmp = currentElement;
    if (tmp.isDescendentOf(sourceElement)) {
      while (tmp != sourceElement) {
        AuthorElement parent = ((AuthorElement)tmp.getParent());
        int index = parent.getContentNodes().indexOf(tmp) + 1;
        currentElementLocation = "/*[" + index + "]" + currentElementLocation;
        tmp = parent;
      }
      currentElementLocation = "/*[1]" + currentElementLocation; 
    } else {
      currentElementLocation ="/..";
    }
    
    // The target element is where the result is put, depending on the action it can replace this element
    // or it can be inserted relative to this element.
    AuthorNode targetNode;
    if (xpathTarget instanceof String && !"".equals(xpathTarget)) {
      AuthorNode[] results =
        authorAccess.getDocumentController().findNodesByXPath((String) xpathTarget, true, true, false);
      if (results.length > 0) {
        targetNode = results[0];
      } else {
        throw new AuthorOperationException("The target XPath location does not identify a node: " + xpathTarget);
      }
    } else {
      // if the target is not specified we take that as the current element.
      targetNode = currentNode;
    }
    
    // We serialize the source then give that as input to the XSLT script.
    String serializedSource = null;
    try {
      final AuthorDocumentFragment sourceFragment = 
        authorAccess.getDocumentController().createDocumentFragment(sourceElement, true);
      serializedSource = authorAccess.getDocumentController().serializeFragmentToXML(sourceFragment);
    } catch (BadLocationException e) {
      logger.error(e, e);
      throw new AuthorOperationException("Could not serialize source", e);
    }
    if (serializedSource == null) {
      throw new AuthorOperationException("Cannot serialize the source element: " + xpathSource);
    }      
    //EXM-25520 Add doctype to correctly resolve default values.
    AuthorDocumentType doctype = authorAccess.getDocumentController().getDoctype();
    if(doctype != null) {
      String serializedDT = doctype.serializeDoctype();
      if(serializedDT != null) {
        serializedSource = serializedDT + serializedSource;
      }
    }
    org.xml.sax.InputSource is = new org.xml.sax.InputSource(new StringReader(script));
    String baseLocation = authorAccess.getUtilAccess().expandEditorVariables(EditorVariables.FRAMEWORK_URL, 
        authorAccess.getEditorAccess().getEditorLocation());
    if(baseLocation != null && baseLocation.contains(EditorVariables.FRAMEWORK_URL)) {
      //This means the framework is internal, we cannot set it as a base system ID to the source.
      //But let's use instead the place from where the XML was loaded.
      baseLocation = authorAccess.getEditorAccess().getEditorLocation().toString();
    }
    is.setSystemId(baseLocation);
    
    Source xslSrc = new SAXSource(is);
  
    // Create the transformer
    Transformer t = null;
    try {
      t = createTransformer(authorAccess, xslSrc);
    } catch (TransformerConfigurationException e) {
      logger.debug(e, e);
      throw new AuthorOperationException("Cannot create a transformer from the provided script:\n" 
          + script + "\nReason:" + e.getMessage());
    }
    
    // Apply the transformation.
    if (t != null) {
      t.setParameter(CURRENT_ELEMENT_LOCATION, currentElementLocation);
      StringWriter sw = new StringWriter();
      org.xml.sax.InputSource id = new org.xml.sax.InputSource(new StringReader(serializedSource));
      id.setSystemId(sourceElement.getXMLBaseURL().toString());
      Source xmlSrc = new SAXSource(id);
      try {
        t.transform(xmlSrc, new StreamResult(sw));
      } catch (TransformerException e) {
        throw new AuthorOperationException("The script cannot be executed: " + e.getMessageAndLocation());
      }
      
      ///true if we want to expand editor variables
      boolean expandEditorVariables =
          args.getArgumentValue(ARGUMENT_EXPAND_EDITOR_VARIABLES) == null
          || "true".equals(args.getArgumentValue(ARGUMENT_EXPAND_EDITOR_VARIABLES));
      boolean hasCaretMarker = false;
      // Remove XML header if present.
      String result = sw.toString();
      if (result.startsWith("<?xml ")) {
        // remove the xml header
        int index = result.indexOf("?>");
        if (index != -1) {
          result = result.substring(index+2);
        }
      }
      
      //Also expand the editor variables.
      if(expandEditorVariables) {
        //Expand all other editor variables which were output by the stylesheet.
        result = authorAccess.getUtilAccess().expandEditorVariables(
            result, authorAccess.getEditorAccess().getEditorLocation());
        int indexOfCaret = result.indexOf(EditorVariables.CT_CARET_EDITOR_VARIABLE);
        if(indexOfCaret != -1) {
          result =
              result.substring(0, indexOfCaret)
              + EditorVariables.UNIQUE_CARET_MARKER_FOR_AUTHOR
              + result.substring(
                  indexOfCaret + EditorVariables.CT_CARET_EDITOR_VARIABLE.length(),
                  result.length());
          hasCaretMarker = true;
        }
        
        //And expand the selection editor variable as well.
        int indexOfSelection = result.indexOf(EditorVariables.CT_SELECTION_EDITOR_VARIABLE);
        if (indexOfSelection != -1) {
          String selXML = "";
          try {
            if (authorAccess.getEditorAccess().hasSelection()) {
              //Serialize the current selection.
              AuthorDocumentFragment selFrag =
                authorAccess.getDocumentController().createDocumentFragment(
                    authorAccess.getEditorAccess().getSelectionStart(),
                    authorAccess.getEditorAccess().getSelectionEnd() - 1);
              selXML = authorAccess.getDocumentController().serializeFragmentToXML(selFrag);
            }
          } catch(BadLocationException ex) {
            logger.error(ex, ex);
          }
          //Expand selection editor variable.
          result = result.substring(0, indexOfSelection) + selXML + result.substring(
              indexOfSelection + EditorVariables.CT_SELECTION_EDITOR_VARIABLE.length(), result.length());
        }
      }

      // Store initial caret information
      int offset = authorAccess.getEditorAccess().getCaretOffset();
      int insertionOffset = authorAccess.getEditorAccess().getCaretOffset();
      if (ACTION_REPLACE.equals(action)) {
        insertionOffset = targetNode.getStartOffset();
      } else if (!ACTION_AT_CARET.equals(action)) {
        String xpath = xpathTarget != null && ((String) xpathTarget).trim().length() > 0 ? (String) xpathTarget : ".";
        // Evaluate the expression and obtain the offset of the first node from the result
        insertionOffset =
          authorAccess.getDocumentController().getXPathLocationOffset(
              xpath, (String) action);
      }
      
      // We create a position to keep track of the end offset of the inserted fragment.
      Position endOffsetPos = null;
      try {
        endOffsetPos = authorAccess.getDocumentController().createPositionInContent(insertionOffset);
      } catch (BadLocationException e1) {
        logger.error(e1, e1);
      }
      
      // Put the result of the XSLT transformation back into the document.
      if (ACTION_REPLACE.equals(action)) {
        if (targetNode.getParent().getType() == AuthorNode.NODE_TYPE_DOCUMENT) {
          AuthorDocumentFragment authorFragment = authorAccess.getDocumentController().createNewDocumentFragmentInContext(
              result, 
              targetNode.getStartOffset());
          // Root replace.
          authorAccess.getDocumentController().replaceRoot(authorFragment);
        } else {
          authorAccess.getDocumentController().insertXMLFragment(
              result, targetNode, ACTION_INSERT_BEFORE);
          authorAccess.getDocumentController().deleteNode(targetNode);
        }
      } else if (ACTION_AT_CARET.equals(action)){
        authorAccess.getDocumentController().insertXMLFragment(
            result, authorAccess.getEditorAccess().getCaretOffset());
      } else {
        authorAccess.getDocumentController().insertXMLFragment(
          result, targetNode, (String)action);
      }
      
      // Get additional caret information.
      int startOffset = insertionOffset + 1;
      int endOffset = endOffsetPos != null ? endOffsetPos.getOffset() - 1 : startOffset;
      
      if (offset < startOffset) {
        offset = startOffset;
      }
      if (offset > endOffset) {
        offset = endOffset;
      }
      // Set the caret location.
      if(hasCaretMarker) {
        //Move the caret to the right place.
        MoveCaretUtil.moveCaretToImposedEditorVariableOffset(authorAccess, insertionOffset);
      } else if (CARET_POSITION_BEFORE.equals(caretPosition)) {
        authorAccess.getEditorAccess().setCaretPosition(startOffset - 1);
      } else if (CARET_POSITION_START.equals(caretPosition)) {
        authorAccess.getEditorAccess().setCaretPosition(startOffset);
      } else if (CARET_POSITION_PRESERVE.equals(caretPosition)) {
        authorAccess.getEditorAccess().setCaretPosition(offset);
      } else if (CARET_POSITION_END.equals(caretPosition)) {
        authorAccess.getEditorAccess().setCaretPosition(endOffset);
      } else if (CARET_POSITION_AFTER.equals(caretPosition)) {
        authorAccess.getEditorAccess().setCaretPosition(endOffset + 1);
      } else { // default: CARET_POSITION_EDITABLE
        try {
          authorAccess.getEditorAccess().goToNextEditablePosition(startOffset - 1, endOffset);
        } catch (BadLocationException e) {
          logger.error(e, e);
        }
      }        
    }
  }

  /**
   * Creates a Transformer from a given script.
   * 
   * @param authorAccess Access to different Author resources.
   * @param scriptSrc The XSLT or XQuery script.
   * @return A JAXP Transformer that will perform the transformation defined in the given script. 
   * @throws TransformerConfigurationException
   */
  protected abstract Transformer createTransformer(AuthorAccess authorAccess, Source scriptSrc)
      throws TransformerConfigurationException;
 
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
    return "Run a script on a source element and then replace or insert the result in a target node.";
  }
}