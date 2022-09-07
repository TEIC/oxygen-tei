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
import java.util.Optional;

import com.google.common.base.Splitter;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.common.CommonAccess;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.XPathVersion;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.transformation.TransformationFeedback;
import ro.sync.exml.workspace.api.editor.transformation.TransformationScenarioNotFoundException;

/**
 * An implementation of an operation which runs a single transformation scenario.
 * 
 * @author adrian_sorop
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(false)
public class ExecuteCustomizableTransformationScenarioOperation implements AuthorOperation {
  
  /**
   * XPath expression representing "current element" 
   */
  private static final String XPATH_EXPRESSION_CURRENT_ELEMENT = ".";

  /**
   * Pseudo class that marks the current processed element.
   */
  private static final String MARK_IN_PROGRESS_PSEUDO_CLASS = "-oxy-transformation-in-progress"; 
  
  /**
   * Pseudo class that marks other elements like the processed one.
   */
  private static final String MARK_OTHERS_IN_PROGRESS_PSEUDO_CLASS = "-oxy-transformation-in-progress-others";
  
  /**
   * Splits on "\n" and trim results.
   */
  private static final Splitter SPLITTER_ON_END_LINE = Splitter.on("\n").trimResults();
  
  /**
   * Splits on "=" and trim results
   */
  private static final Splitter SPLITTER_ON_EQUALS = Splitter.on("=").trimResults();
  
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Scenario name param name.
   */
  private static final String SCENARIO_NAME = "scenarioName";
  
  /**
   * Scenario parameters argument. Pairs key=value separated by new line.
   */
  private static final String SCENARIO_PARAMETERS = "scenarioParameters";
  
  /**
   * -oxy-transformation-in-progress
   */
  private static final String MARK_IN_PROGRESS_XPATH_LOCATION = "markInProgressXPathLocation";
  
  /**
   * -oxy-transformation-in-progress-others
   */
  private static final String MARK_OTHERS_IN_PROGRESS_XPATH_LOCATION = "markOthersInProgressXPathLocation";
  
  /**
   * Constructor. 
   */
  public ExecuteCustomizableTransformationScenarioOperation() {
    //The list of scenario names is a line separated list.
    arguments = new ArgumentDescriptor[4];
    
    ArgumentDescriptor argumentDescriptor = 
      new ArgumentDescriptor(
          SCENARIO_NAME, 
          ArgumentDescriptor.TYPE_STRING, 
          "The name of the transformation scenario which will be executed.");
    arguments[0] = argumentDescriptor;
    
    argumentDescriptor = 
        new ArgumentDescriptor(
            SCENARIO_PARAMETERS, 
            ArgumentDescriptor.TYPE_STRING,
            "Provided parameters for the transformation scenario.\n"
            + "Parameters are inserted as name=value pairs separated by line breaks.\n"
            + "The set parameters are taken into account for XSLT, DITA, Chemistry and ANT Transformation scenario types.");
    arguments[1] = argumentDescriptor;
    
    
    argumentDescriptor = 
        new ArgumentDescriptor(
            MARK_IN_PROGRESS_XPATH_LOCATION, 
            ArgumentDescriptor.TYPE_STRING,
            "XPath expression that identifies the element(s) on which a specific  "
            + "'-oxy-transformation-in-progress' pseudo class is set before transformation is started."
            + "The pseudo class is reset when the transformation ends."
            + "If this XPath expression is not defined, the current node is used.");
    arguments[2] = argumentDescriptor;
    
    argumentDescriptor = 
        new ArgumentDescriptor(
            MARK_OTHERS_IN_PROGRESS_XPATH_LOCATION, 
            ArgumentDescriptor.TYPE_STRING,
            "XPath expression that indentifies other elements on which a specific '-oxy-transformation-in-progress-others' "
            + "pseudo class is set before the transformation is started."
            + "The pseudo class is reset when the transformation ends.");
    arguments[3] = argumentDescriptor;
  }
  
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
   */
  @Override
  public String getDescription() {
    return "Run a named transformation scenario with specific parameters defined in the associated document type.";
  }


  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    //Get the list of scenario names.
    Object scenarioName = args.getArgumentValue(SCENARIO_NAME);
    // Scenario parameters
    Object paramsArgument = args.getArgumentValue(SCENARIO_PARAMETERS);
    
    // sanity check
    if (scenarioName == null) {
      throw new AuthorOperationException("The scenario name was not specified as a parameter.");
    }
    
    Map scenarioArguments = null;
    
    if (paramsArgument instanceof String && 
        // Default value was changed.
        !((String) paramsArgument).trim().isEmpty()) {
      
      try {
        scenarioArguments = SPLITTER_ON_END_LINE.withKeyValueSeparator(SPLITTER_ON_EQUALS).split(String.valueOf(paramsArgument));
      } catch (IllegalArgumentException e) {
        throw new AuthorOperationException("The arguments should be defined as key=value pairs.");
      }
    }
    
    String xpathOfCurrentProcessedNode = 
        Optional.ofNullable(args.getArgumentValue(MARK_IN_PROGRESS_XPATH_LOCATION))
        .map(String.class::cast)
        .map(String::trim)
        .filter(t -> !t.isEmpty())
        .orElse(XPATH_EXPRESSION_CURRENT_ELEMENT);
    
    String xpathOfOtherNodesLikeMeThatWontBeProcessedNow = 
        Optional.ofNullable(args.getArgumentValue(MARK_OTHERS_IN_PROGRESS_XPATH_LOCATION))
        .map(String.class::cast)
        .orElse(null);
    
    AuthorDocumentController ctrl = authorAccess.getDocumentController();
    List<AuthorElement> current = findElementsByXPath(ctrl, xpathOfCurrentProcessedNode);
    
    // others = all - current
    List<AuthorElement> others = findElementsByXPath(ctrl, xpathOfOtherNodesLikeMeThatWontBeProcessedNow);
    others.removeAll(current);
    
    setPseudoClassToElements(ctrl, current, MARK_IN_PROGRESS_PSEUDO_CLASS);
    setPseudoClassToElements(ctrl, others, MARK_OTHERS_IN_PROGRESS_PSEUDO_CLASS);
    
    TransformationFeedback transformationFeedback = new TransformationFeedback() {
      @Override
      public void transformationStopped() {
        removePseudoClassToElements(ctrl, current, MARK_IN_PROGRESS_PSEUDO_CLASS);
        removePseudoClassToElements(ctrl, others, MARK_OTHERS_IN_PROGRESS_PSEUDO_CLASS);
      }

      @Override
      public void transformationFinished(boolean success) {
        removePseudoClassToElements(ctrl, current, MARK_IN_PROGRESS_PSEUDO_CLASS);
        removePseudoClassToElements(ctrl, others, MARK_OTHERS_IN_PROGRESS_PSEUDO_CLASS);
      }
    };
    
    try {
      authorAccess.getEditorAccess().runTransformationScenario(String.valueOf(scenarioName), scenarioArguments, transformationFeedback);
    } catch (TransformationScenarioNotFoundException e) { //NOSONAR java:S1166 It's thrown another exception
      //Show the problem.
      throw new AuthorOperationException(e.getMessage(), e);
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
   * Finds the author elements selected by the given XPath expression.
   * @param ctrl The document controller.
   * @param xpath The given XPath expression.
   * 
   * @return The identified elements or an empty list, if no elements were found.
   * @throws AuthorOperationException If the XPath fails to apply.
   */
  private static List<AuthorElement> findElementsByXPath(AuthorDocumentController ctrl, String xpath) throws AuthorOperationException {

    List<AuthorElement> elements = new ArrayList<>();
    if (xpath == null) {
      return elements;
    }

    AuthorNode[] nodesFound = ctrl.findNodesByXPath(
        xpath,
        null,
        // skip text
        true, 
        // skip CData
        true, 
        // skip comments
        true,
        // skip track changes markers
        false,
        // XPath version
        XPathVersion.XPATH_2_0,
        // apply to referenced content
        true);

    for (AuthorNode authorNode : nodesFound) {
      if (authorNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
        elements.add((AuthorElement) authorNode);
      }
    }

    return elements;
  }
  
  /**
   * Sets a pseudo class to elements.
   */
  private static void setPseudoClassToElements(AuthorDocumentController ctrl, List<AuthorElement> elements, String pseudoClass) {
    if (pseudoClass != null) {
      CommonAccess.invokeLater(() -> elements.forEach(el -> ctrl.setPseudoClass(pseudoClass, el)));
    }
  }
  
  /**
   * Removes a pseudo class from elements.
   */
  private static void removePseudoClassToElements(AuthorDocumentController ctrl, List<AuthorElement> elements, String pseudoClass) {
    if (pseudoClass != null) {
      CommonAccess.invokeLater(() -> elements.forEach(el -> ctrl.removePseudoClass(pseudoClass, el)));
    }
  }
  
}