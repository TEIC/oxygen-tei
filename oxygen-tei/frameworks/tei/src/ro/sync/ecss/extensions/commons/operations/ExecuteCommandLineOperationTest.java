/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2016 Syncro Soft SRL, Romania.  All rights
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
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;

import ro.sync.ecss.extensions.AuthorExtensionActionCore;
import ro.sync.ecss.extensions.AuthorWorkspaceAccessImpl;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.exml.IDEAccess;
import ro.sync.exml.IDEAccessAdapter;
import ro.sync.io.IOUtil;
import ro.sync.junit.JFCTestCase;
import ro.sync.ui.UiUtil;

/**
 * Test cases for ExecuteCommandLineOperation.
 *  
 * @author sorin_carbunaru
 */
public class ExecuteCommandLineOperationTest  extends JFCTestCase {

  /**
   * <p><b>Description:</b> test ExecuteCommandLineOperation 
   * by applying an XSLT on an XML. Show console.</p>
   * <p><b>Bug ID:</b> EXM-29044</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testXSLTOperationCurrentNode_showConsole() throws Exception {
    IDEAccess ideAccess = IDEAccess.getInstance();
    File outputFile = null;
    
    try {
      final Boolean[] addedResults = new Boolean[1];
      addedResults[0] = false;
      
      // New IDEAccess to inspect the console
      IDEAccess.setInstance(new IDEAccessAdapter() {
        @Override
        public void addResult(String key, String line) {
          addedResults[0] = true;
          System.out.println(key + " " + line);
        }
      });

      final ExecuteCommandLineOperation op = new ExecuteCommandLineOperation();
      
      final AuthorAccess mockedAuthorAccess = Mockito.mock(AuthorAccess.class);
      Mockito.when(mockedAuthorAccess.getWorkspaceAccess()).thenReturn(new AuthorWorkspaceAccessImpl(null, null, null));

      AuthorExtensionActionCore actionCore = new AuthorExtensionActionCore(
          mockedAuthorAccess,
          null,
          null,
          null,
          null,
          null);

      outputFile = new File("test/EXM-29044/outputFile.dita");
      outputFile.createNewFile();
      String saxonEEJar = new File("lib/saxon9ee.jar").getAbsolutePath();
      String cmdLine = "java -jar \"" + saxonEEJar + "\" -s:emptySample.dita -xsl:theStylesheet.xsl -o:" + outputFile.getName();

      // Operation arguments
      Map userValues = new HashMap();
      userValues.put("name", "OperationTestProcess");
      userValues.put("workingDirectory", new File("test/EXM-29044").getAbsolutePath());
      userValues.put("cmdLine", cmdLine);
      // Show console
      userValues.put("showConsole", "true");

      final ArgumentsMap operationArguments = actionCore.getOperationArguments(userValues, op);
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(mockedAuthorAccess, operationArguments);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
      sleep(500);

      String actual = IOUtil.read(new FileReader(outputFile)).toString();
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "  \n" + 
          "  \n" + 
          "    \n" + 
          "    UITE CEVA\n" + 
          "  \n" + 
          "  \n" + 
          "", 
          actual);
      
      assertTrue("Some results should have been added.", addedResults[0]);
      
      
    } finally {
      IDEAccess.setInstance(ideAccess);
      if (outputFile != null) {
        outputFile.delete();
      }
    }
  }
  
  /**
   * <p><b>Description:</b> test ExecuteCommandLineOperation 
   * by applying an XSLT on an XML. Don't show console.</p>
   * <p><b>Bug ID:</b> EXM-29044</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testXSLTOperationCurrentNode_dontShowConsole() throws Exception {
    IDEAccess ideAccess = IDEAccess.getInstance();
    File outputFile = null;
    
    try {
      final Boolean[] addedResults = new Boolean[1];
      addedResults[0] = false;
      
      // New IDEAccess to inspect the console
      IDEAccess.setInstance(new IDEAccessAdapter() {
        @Override
        public void addResult(String key, String line) {
          addedResults[0] = true;
        }
      });

      final ExecuteCommandLineOperation op = new ExecuteCommandLineOperation();
      
      final AuthorAccess mockedAuthorAccess = Mockito.mock(AuthorAccess.class);
      Mockito.when(mockedAuthorAccess.getWorkspaceAccess()).thenReturn(new AuthorWorkspaceAccessImpl(null, null, null));

      AuthorExtensionActionCore actionCore = new AuthorExtensionActionCore(
          mockedAuthorAccess,
          null,
          null,
          null,
          null,
          null);

      outputFile = new File("test/EXM-29044/outputFile.dita");
      outputFile.createNewFile();
      String saxonEEJar = new File("lib/saxon9ee.jar").getAbsolutePath();
      String cmdLine = "java -jar \"" + saxonEEJar + "\" -s:emptySample.dita -xsl:theStylesheet.xsl -o:" + outputFile.getName();
      
      // Operation arguments
      Map userValues = new HashMap();
      userValues.put("name", "OperationTestProcess");
      userValues.put("workingDirectory", new File("test/EXM-29044").getAbsolutePath());
      userValues.put("cmdLine", cmdLine);
      // Don't show console
      userValues.put("showConsole", "false");

      final ArgumentsMap operationArguments = actionCore.getOperationArguments(userValues, op);
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(mockedAuthorAccess, operationArguments);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
      sleep(500);

      String actual = IOUtil.read(new FileReader(outputFile)).toString();
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "  \n" + 
          "  \n" + 
          "    \n" + 
          "    UITE CEVA\n" + 
          "  \n" + 
          "  \n" + 
          "", 
          actual);
      
      assertFalse("No should have been added.", addedResults[0]);
      
      
    } finally {
      IDEAccess.setInstance(ideAccess);
      if (outputFile != null) {
        outputFile.delete();
      }
    }
  }
  
}
