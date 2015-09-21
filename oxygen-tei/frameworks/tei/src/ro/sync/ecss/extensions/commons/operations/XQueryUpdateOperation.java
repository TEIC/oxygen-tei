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

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;




import ro.sync.ecss.dom.wrappers.mutable.AuthorSource;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.util.URLUtil;

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

@WebappCompatible(false)
public class XQueryUpdateOperation implements AuthorOperation {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(XQueryUpdateOperation.class.getName());
  
  /**
   * The script argument.
   * The value is <code>script</code>.
   */
  public static final String ARGUMENT_SCRIPT = "script";
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor.
   */
  public XQueryUpdateOperation() {
    arguments = new ArgumentDescriptor[1];
    // We only have one argument that can be either an URL or the actual script.
    ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_SCRIPT,
        ArgumentDescriptor.TYPE_STRING,
        "The script to be executed or an URL pointing to the script. Editor variables are accespted in the URL.");
    arguments[0] = argumentDescriptor;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
    throws AuthorOperationException {
    Object fragment = args.getArgumentValue(ARGUMENT_SCRIPT);
    if (fragment instanceof String) {
      Source xQuerySource = null;
      // Try to parse it as an URL.
      String expanded = authorAccess.getUtilAccess().expandEditorVariables((String) fragment, null);
      try {
        URL url = new URL(expanded);
        xQuerySource = new StreamSource(url.toExternalForm());
      } catch (MalformedURLException e1) {
        // Not an URL;
        try {
          File file = new File(expanded);
          if (file.exists()) {
            URL url = URLUtil.correct(file);
            xQuerySource = new StreamSource(url.toExternalForm());
          }
        } catch (MalformedURLException e) {
          // Definitely not an URL.
        }
      }
      
      if (xQuerySource  == null) {
        // Probably an XQuery script directly..
        String xq = (String) fragment;
        if (logger.isDebugEnabled()) {
          logger.debug("Execute " + xq);
        }
        xQuerySource = new StreamSource(new StringReader(xq));
      }
      
      AuthorDocumentController documentController = authorAccess.getDocumentController();
      documentController.beginCompoundEdit();
      try {
        // Create an XQuery update enable processor.
        Transformer queryTransformer  = authorAccess.getXMLUtilAccess().createXQueryUpdateTransformer(
            xQuerySource, 
            null);
        
        // Create a special author model source.
        Source s = new AuthorSource(authorAccess);
        Writer writer = new StringWriter();
        Result result = new StreamResult(writer);
        
        queryTransformer.transform(s, result);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        throw new AuthorOperationException("Execution failed: " + e.getMessage(), e);
      } catch (TransformerConfigurationException e) {
        e.printStackTrace();
        throw new AuthorOperationException("Execution failed: " + e.getMessage(), e);
      } catch (TransformerException e) {
        e.printStackTrace();
        throw new AuthorOperationException("Execution failed: " + e.getMessage(), e);
      } finally {
        documentController.endCompoundEdit();
      }
    } else {
      throw new IllegalArgumentException("The argument value was not defined, it is " + fragment);
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