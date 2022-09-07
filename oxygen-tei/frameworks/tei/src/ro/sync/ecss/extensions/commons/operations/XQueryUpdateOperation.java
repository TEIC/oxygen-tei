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

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.dom.wrappers.mutable.AuthorSource;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.exml.workspace.api.util.InternalTransformerAccess;

/**
 * An implementation of an operation that applies an XQuery Update script. 
 * The changes are performed directly over the Author nodes model.
 * 
 * The script will be executed in the context of the caret node. 
 * If the XQuery script declares the selection variable (see the following code snippet), 
 * it will also receive the selected nodes (assuming that the selection consists entirely of nodes).
 * 
 * THe following code snippet converts the selected paragraphs in a list.
 * 
 * <pre>
 * declare namespace oxyxq = "http://www.oxygenxml.com/ns/xqu";
(: This variable will be linked to the selected nodes assuming that there are 
actually fully selected nodes. For example this selection will return null: 
<p>{SEL_START}text{SEL_END} in para</p>
but this will give two "p" elements:
{SEL_END}<p>text</p><p>text2</p>{SEL_END}

If a multiple selection exists it will also be processed and forwarded. 
Again, only fully selected nodes will be passed.
:)
declare variable $oxyxq:selection external;

(: We will process either the selection or the context node :)
let $toProcess := if (empty($oxyxq:selection)) then
    (.)
else
    ($oxyxq:selection)

return
    if (not(empty($toProcess))) then
        (
        (: Create the list :)
        let $ul :=
        &lt;ul>
            {
                for $sel in $toProcess
                return
                    &lt;li>{$sel}&lt;/li>
            }
        &lt;/ul>
        
        return
            (
            (: Delete the processed nodes :)
            for $sel in $toProcess
            return
                delete node $sel,
            (: Inserts the constructed list :)
            insert node $ul
                before $toProcess[1]
            )
        )
    else
        ()
 *  </pre>
 * 
 * 
 * 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(false)
public class XQueryUpdateOperation implements AuthorOperation {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(XQueryUpdateOperation.class.getName());
  
  /**
   * The script argument.
   * The value is <code>script</code>.
   */
  public static final String ARGUMENT_SCRIPT = "script";
  /**
   * External parameters for xquery.
   */
  public static final String ARGUMENT_SCRIPT_PARAMETERS = "externalParams";
  /**
   * Make XInclude elements transparent in document model.
   */
  public static final String ARGUMENT_EXPAND_XINCLUDE_REFERENCES = "expandXincludeReferences";
  /**
   * Split tokens: comma and end line.
   */
  private static final String TOKEN_COMMA_END_LINE = ",\n";
  /**
   * Equals token.
   */
  private static final String TOKEN_EQUALS = "=";
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
 /**
  * External parameters of the xquery script.
  */
  private Map<String, String> externalArguments = null;
  /**
   * Constructor.
   */
  public XQueryUpdateOperation() {
    arguments = new ArgumentDescriptor[3];
    // We only have one argument that can be either an URL or the actual script.
    ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_SCRIPT,
        ArgumentDescriptor.TYPE_STRING,
        "A path to the script or the script itself.\n"
            + "When using a path the following apply:\n"
            + "- a relative path is resolved to the framework directory. \n"
            + "- the ${framework} editor variable can also be used to refer resources from the framework directory. \n"
            + "- the path is passed through the catalog mappings.\n" + 
            "If you provide the actual script, the base system ID for this will be the framework file, so any include/import " +
            "reference will be resolved relative to the \".framework\" file that contains this action definition");
    arguments[0] = argumentDescriptor;
    
    // EXM-37485 Provide external parameters to script.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_SCRIPT_PARAMETERS,
        ArgumentDescriptor.TYPE_STRING,
        "Provide external parameters to the xquery script.\n"
        + "Should be inserted as name=value pairs separated by comma or line break.");
    arguments[1] = argumentDescriptor;
    
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_EXPAND_XINCLUDE_REFERENCES,
        ArgumentDescriptor.TYPE_CONSTANT_LIST, 
        "Add the elements referred through XInclude to the document model.",
        new String[] {
            AuthorConstants.ARG_VALUE_TRUE,
            AuthorConstants.ARG_VALUE_FALSE
        }, 
        AuthorConstants.ARG_VALUE_FALSE);
    arguments[2] = argumentDescriptor;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    Object paramsArgument = args.getArgumentValue(ARGUMENT_SCRIPT_PARAMETERS);
    
    if (paramsArgument instanceof String && 
        // Default value was changed.
        !((String) paramsArgument).trim().isEmpty()) {
      externalArguments = new HashMap<>();
      // Tokenize the string and get the parameters and their values
      StringTokenizer commaTokenizer = new StringTokenizer((String) paramsArgument, TOKEN_COMMA_END_LINE);
      while (commaTokenizer.hasMoreElements()) {
        // key = value pairs.
        String pair = (String) commaTokenizer.nextElement();
        int indexOfEqual = pair.indexOf(TOKEN_EQUALS);
        if (indexOfEqual != -1) {
          String param = pair.substring(0, indexOfEqual);
          String value = pair.substring(indexOfEqual + 1, pair.length());
          externalArguments.put(param.trim(), value);
        } else {
          throw new IllegalArgumentException("The arguments should be defined as key=value pairs.");
        }
      }
      
    }
    
    Object script = args.getArgumentValue(ARGUMENT_SCRIPT);
    if (script instanceof String) {
      Source xQuerySource = null;
      // Try to parse it as an URL.
      URL url = CommonsOperationsUtil.expandAndResolvePath(authorAccess, (String) script);
      if (url != null) {
        xQuerySource = new StreamSource(url.toExternalForm());
      } else {
        // Probably an XQuery script directly..
        String xq = (String) script;
        if (logger.isDebugEnabled()) {
          logger.debug("Execute " + xq);
        }
        xQuerySource = new StreamSource(new StringReader(xq));
      }
      
      AuthorDocumentController documentController = authorAccess.getDocumentController();
      documentController.beginCompoundEdit();
      try {
        // Create an XQuery update enable processor.
        Transformer queryTransformer  = ((InternalTransformerAccess)authorAccess.getXMLUtilAccess()).internalCreateXQueryUpdateTransformer(
            xQuerySource, 
            null);
        
        // Now set the external parameters to transformer
        if (externalArguments != null) {
          Set<Entry<String, String>> entrySet = externalArguments.entrySet();
          for (Entry<String, String> entry : entrySet) {
            queryTransformer.setParameter(entry.getKey(), entry.getValue());
          }
        }
        
        // Create a special author model source.
        Object expandXInclude = args.getArgumentValue(ARGUMENT_EXPAND_XINCLUDE_REFERENCES);
        if (expandXInclude == null) {
          expandXInclude = false;
        }
        Source s = new AuthorSource(authorAccess, Boolean.valueOf(expandXInclude.toString()));
        Writer writer = new StringWriter();
        Result result = new StreamResult(writer);
        
        queryTransformer.transform(s, result);
      } catch (IllegalArgumentException | TransformerException e) {
        logger.error(e, e);
        throw new AuthorOperationException("Execution failed: " + e.getMessage(), e);
      } finally {
        documentController.endCompoundEdit();
      }
    } else {
      throw new IllegalArgumentException("The argument value was not defined, it is " + script);
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
    return "Executes an XQuery Update script.";
  }
  
}