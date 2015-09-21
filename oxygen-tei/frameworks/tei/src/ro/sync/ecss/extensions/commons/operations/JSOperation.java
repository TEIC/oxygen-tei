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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.util.editorvars.EditorVariables;

/**
 * An implementation of an operation to run a Javascript using the current Author API.
 */

@WebappCompatible
public class JSOperation implements AuthorOperation {
    
  /**
   * The JS script. The value is <code>script</code>.
   */
  private String ARGUMENT_SCRIPT = "script";
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
   */
  @Override
  public String getDescription() {
    return "Run Javascript code which calls AuthorAccess API by using the predefined field 'authorAccess'.";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    Object scriptValue = args.getArgumentValue(ARGUMENT_SCRIPT);

    if (!(scriptValue instanceof String)) {
      throw new IllegalArgumentException("The argument \"script\" was not defined as a string object!");
    }

    // Get the Javascript script.
    String script = (String)scriptValue;
    //Use it
    org.mozilla.javascript.Context cx = org.mozilla.javascript.Context.enter();
    try{
      org.mozilla.javascript.Scriptable globalScope = cx.initStandardObjects();
      
      String baseLocation = authorAccess.getUtilAccess().expandEditorVariables(EditorVariables.FRAMEWORK_URL, 
          authorAccess.getEditorAccess().getEditorLocation());
      if(baseLocation == null || baseLocation.contains(EditorVariables.FRAMEWORK_URL)) {
        //This means the framework is internal, we cannot set it as a base system ID to the source.
        //But let's use instead the place from where the XML was loaded.
        baseLocation = authorAccess.getEditorAccess().getEditorLocation().toString();
      }
      
      //Also evaluate a common script in a common location
      URL commonScriptURL = new URL(new URL(baseLocation), "commons.js");
      InputStream commonIS = null;
      try {
        commonIS = commonScriptURL.openStream();
        cx.evaluateReader(globalScope, new InputStreamReader(commonIS, "UTF8"), commonScriptURL.toExternalForm(), 1, null);
      } catch (IOException e) {
        //Ignore
      } finally{
        if(commonIS != null){
          try {
            commonIS.close();
          } catch (IOException e) {
            //Ignore
          }
        }
      }
      //The current script UTL
      URL dummyScriptURL = new URL(new URL(baseLocation), "currentScript.js");
      // Now evaluate the string we've collected. We'll ignore the result.
      cx.evaluateString(globalScope, script, dummyScriptURL.toString(), 1, null);
      
      //Aliases for author access
      Object wrappedDispatcher =  org.mozilla.javascript.Context.javaToJS(authorAccess, globalScope);
      org.mozilla.javascript.ScriptableObject.defineProperty(globalScope, "authorAccess", wrappedDispatcher, org.mozilla.javascript.ScriptableObject.CONST);
      
      Object fObj = globalScope.get("doOperation", globalScope);
      org.mozilla.javascript.Function f = (org.mozilla.javascript.Function)fObj;
      f.call(cx, globalScope, globalScope, new Object[0]);
    } catch(org.mozilla.javascript.RhinoException ex){
      throw new AuthorOperationException("Could not evaluate script: " + ex.getMessage(), ex);
    } catch (MalformedURLException e) {
      throw new AuthorOperationException("Could not evaluate script: " + e.getMessage(), e);
    } finally{
      org.mozilla.javascript.Context.exit();
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
     // Argument defining the script that will be executed.
    ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_SCRIPT,
        ArgumentDescriptor.TYPE_SCRIPT,
        "The Javascript content to execute. It must have a function called doOperation() which can use the predefined variable \"authorAccess\".\n"
        + "The \"authorAccess\" variable has access to the entire Java API \"ro.sync.ecss.extensions.api.AuthorAccess\".");
    return new ArgumentDescriptor[]{argumentDescriptor};
  }
}