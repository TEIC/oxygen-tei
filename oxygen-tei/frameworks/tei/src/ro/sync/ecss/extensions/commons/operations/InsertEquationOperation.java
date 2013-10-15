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

import java.util.List;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.contentcompletion.xml.NameValue;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorDocumentType;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSchemaManager;
import ro.sync.ecss.extensions.api.access.AuthorWorkspaceAccess;
import ro.sync.ecss.images.ImageHandlerDispatcher;
import ro.sync.ecss.images.xmlimages.AbstractXMLImageHandler.CannotEditException;
import ro.sync.ecss.images.xmlimages.XMLImageHandler;

/**
 * Operation used to insert an MathML Equation in any documents.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class InsertEquationOperation implements AuthorOperation {
  /**
   * Logger for logging. 
   */
  protected static final Logger logger = Logger.getLogger(InsertEquationOperation.class.getName());

  /**
   * The fragment argument.
   * The value is <code>fragment</code>.
   */
  private static final String ARGUMENT_FRAGMENT_WITH_MATHML = "fragment";
  
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * The MathML fragment representing the default equation.
   */
  protected static final String MATH_ML = 
      "<mml:math xmlns:mml='http://www.w3.org/1998/Math/MathML'><mml:mrow>" + 
      "<mml:msup><mml:mi>a</mml:mi><mml:mn>2</mml:mn></mml:msup>" +
      "<mml:mo>=</mml:mo>" +
      "<mml:msup><mml:mi>b</mml:mi><mml:mn>2</mml:mn></mml:msup>" +
      "<mml:mo>+</mml:mo>" +
      "<mml:msup><mml:mi>c</mml:mi><mml:mn>2</mml:mn>" + 
      "</mml:msup></mml:mrow></mml:math>";

  /**
   * Constructor to assign arguments.
   */
  public InsertEquationOperation() {
    arguments = new ArgumentDescriptor[1];
    // Argument defining the XML fragment that will be inserted.
    ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_FRAGMENT_WITH_MATHML,
        ArgumentDescriptor.TYPE_FRAGMENT,
        "The fragment of XML containing the default MathML content which should be inserted");
    arguments[0] = argumentDescriptor;
  }
  
  /**
  * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
  */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
  throws IllegalArgumentException, AuthorOperationException {
    try {
      AuthorWorkspaceAccess workspace = authorAccess.getWorkspaceAccess(); 
      //Start editing the sample MML
      XMLImageHandler handler =
          ImageHandlerDispatcher.getInstance().getXMLImageHandlerFor("mathml");
      if(handler != null) {
        String serializedDoctype = null;

        AuthorDocumentController controller = authorAccess.getDocumentController();
        AuthorDocumentType dt = controller.getDoctype();
        if(dt != null){
          serializedDoctype = dt.getContent();
        }

        // Determines the entities that can be used by the mathML editor.
        // If the entities list does not contain MathML entities names,
        // then the MathML editor should use code character entities.
        List<NameValue> allowedEntities = null;
        AuthorSchemaManager asm = controller.getAuthorSchemaManager();
        if(asm != null){
          allowedEntities = asm.getEntities();
        }         
        //The default MathML to edit
        String xmlFragment = createDefaultFragmentToEdit(authorAccess, asm);
        Object fragment = args.getArgumentValue(ARGUMENT_FRAGMENT_WITH_MATHML);
        if (fragment instanceof String) {
          if(! ((String)fragment).isEmpty()){
            //If we have some XML content
            xmlFragment = (String)fragment;
          }
        }
        String detectedMathMLContent = extractMathMLFragment(xmlFragment);
        String mml = null;
        if (detectedMathMLContent != null) {
          mml = handler.editImage(
              detectedMathMLContent,
              serializedDoctype,
              authorAccess.getEditorAccess().getEditorLocation().toString(), 
              allowedEntities);

        }
        if(mml != null){      
          xmlFragment = xmlFragment.replace(detectedMathMLContent, mml);
        }
        if (mml != null 
            // Unable to detect mathML content so insert the entire fragment
            || detectedMathMLContent == null) {
          int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
          // Inserts the equation
          authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
              xmlFragment, caretOffset);
        } else {
          // The user canceled the dialog
        }
      } else {
        //Maybe running from the Author component applet
        workspace.showInformationMessage("MathML editing is not supported.");
      }
    } catch (CannotEditException e) {
      throw new AuthorOperationException(e.getMessage(), e);
    }
  }

  /**
  * The MathML XML fragment can be customized by the developer.
  * 
  * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
  */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }

  /**
  * @see ro.sync.ecss.extensions.api.Extension#getDescription()
  */
  @Override
  public String getDescription() {
    return "Insert a MathML equation.";
  }
  
  /**
   * Return default fragment.
   * 
   * @param authorAccess Author access.
   * @param asm The author schema manager.
   * 
   * @return The default fragment.
   */
  protected String createDefaultFragmentToEdit(AuthorAccess authorAccess, AuthorSchemaManager asm) {
    return MATH_ML;
  }
  
  /**
   * Extract from the XML fragment the MathML fragment.
   * 
   * @param xmlFragment The XML fragment.
   * 
   * @return The MathML fragment.
   */
  private String extractMathMLFragment(String xmlFragment) {

    //<lcEquation>
    //<math xmlns="http://www.w3.org/1998/Math/MathML"> </math>
    //</lcEquation>
    String mathMLToEdit = null;
    int namespaceIndex = xmlFragment.indexOf("http://www.w3.org/1998/Math/MathML");
    if (namespaceIndex != -1) {
      String stringBeforeNamespace = xmlFragment.substring(0, namespaceIndex);
      int lastIndexOfLT = stringBeforeNamespace.lastIndexOf("<");
      if (lastIndexOfLT != -1) {
        // We found the start of the MathML fragment
        String mathmlElemName = null;
        for (int i = lastIndexOfLT + 1; i <= namespaceIndex; i++) {
          char ch = xmlFragment.charAt(i);
          if (Character.isWhitespace(ch)) {
            mathmlElemName = xmlFragment.substring(lastIndexOfLT + 1, i);
            break;
          }
        }
        if (mathmlElemName != null) {
          String stringAfterNamespace = xmlFragment.substring(namespaceIndex, xmlFragment.length());
          String mathMLEndTag = "</" + mathmlElemName + ">";
          int endMathML = stringAfterNamespace.indexOf(mathMLEndTag);
          if (endMathML != -1) {
            // We found the MathML fragment to edit
            mathMLToEdit = xmlFragment.substring(lastIndexOfLT, namespaceIndex + endMathML + mathMLEndTag.length());
          }
        }
      }
    }
    return mathMLToEdit;
  }
}