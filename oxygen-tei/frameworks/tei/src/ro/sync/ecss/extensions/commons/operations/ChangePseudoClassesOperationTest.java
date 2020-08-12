/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2012 Syncro Soft SRL, Romania.  All rights
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

import ro.sync.ecss.extensions.ArgumentsMapImpl;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.layout.BoxTestBase;
import ro.sync.ecss.layout.DumpConfiguration;
import ro.sync.ui.UiUtil;

/**
 * Test case for toggle pseudo class.
 * 
 * @author alex_jitianu
 */
public class ChangePseudoClassesOperationTest extends BoxTestBase {

  /**
   * <p><b>Description:</b> When toggling multiple pseudo classes using ChangePseudoClassesOperation,
   * we only fire a layout update at the end. This test just checks that after the 
   * operation changes the layout is consistent. 
   * </p>
   * <p><b>Bug ID:</b> EXM-32522</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception if it fails.
   */
  public void testDisableLayout() throws Exception {
    String css = 
        "root, para, section {\n" + 
        "    display:block;\n" + 
        "}\n" + 
        "section:-oxy-section {\n" + 
        "    content:\"SECTION CLASS ON\";\n" + 
        "}" +
        "para:-oxy-para {\n" + 
        "    content:\"PARA CLASS ON\";\n" + 
        "}"
        ;
    
      String xmlDoc =  
        "<root>\n" + 
        "    <section/>\n" +
        "    <section/>\n" + 
        "    <para/>\n" +
        "    <para/>\n" + 
        "</root>";
    
      setDocumentAndStylesheetContents(xmlDoc, css);
      int maxWidth = charWidth * 50;
      setViewportWidth(maxWidth);
      
      final ChangePseudoClassesOperation op = new ChangePseudoClassesOperation();
      final ArgumentsMapImpl args = new ArgumentsMapImpl();
      // We set a class on the "para" elements.
      args.setArgument("setLocations", "//section");
      args.setArgument("setPseudoClassNames", "-oxy-section");
      args.setArgument("removeLocations", "//para");
      args.setArgument("removePseudoClassNames", "-oxy-para");
    
      //Set the pseudo class names
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), args);
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });

      AuthorNode[] sectionNode = vViewport.getController().findNodesByXPath("//section", true, true, true);
      assertTrue(((AuthorElement)sectionNode[0]).hasPseudoClass("-oxy-section"));
      assertTrue(((AuthorElement)sectionNode[1]).hasPseudoClass("-oxy-section"));
      
      DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
      dumpConfiguration.setReportMaximumWidth(false);
      dumpConfiguration.setReportMinimumWidth(false);
      dumpConfiguration.setReportOffsets(false);
      dumpConfiguration.setReportWidth(false);
      
      StringBuilder dump = new StringBuilder();
      rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
      
      assertOccurences(dump.toString(), "SECTION CLASS ON", 2);
      assertOccurences(dump.toString(), "Para CLASS ON", 0);
      
      // Toggle the classes. We now set a class on the "para" and remove the one from the "section".
      args.setArgument("setLocations", "//para");
      args.setArgument("setPseudoClassNames", "-oxy-para");
      args.setArgument("removeLocations", "//section");
      args.setArgument("removePseudoClassNames", "-oxy-section");
    
      //Set the pseudo class names
      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), args);
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (AuthorOperationException e) {
            e.printStackTrace();
          }
        }
      });
      
      sectionNode = vViewport.getController().findNodesByXPath("//section", true, true, true);
      assertFalse(((AuthorElement)sectionNode[0]).hasPseudoClass("-oxy-section"));
      assertFalse(((AuthorElement)sectionNode[1]).hasPseudoClass("-oxy-section"));
      
      AuthorNode[] paraNode = vViewport.getController().findNodesByXPath("//para", true, true, true);
      assertTrue(((AuthorElement)paraNode[0]).hasPseudoClass("-oxy-para"));
      assertTrue(((AuthorElement)paraNode[1]).hasPseudoClass("-oxy-para"));
      
      dump.setLength(0);
      rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
      
      assertOccurences(dump.toString(), "SECTION CLASS ON", 0);
      assertOccurences(dump.toString(), "PARA CLASS ON", 2);
      
  }

  /**
   * Counts the number of occurrences of the given string in another string.
   *
   * @param dump The hay stack.
   * @param token Token to search;
   * @param expectedCounter The expected number of occurrences.
   */
  private void assertOccurences(String dump, String token, int expectedCounter) {
    int counter = 0;
    int index = -1;
    while ((index = dump.indexOf(token, index + 1)) != -1) {
      counter ++;
    }
    
    assertEquals("Pseudo class related text " + token + "should appear " + expectedCounter + " :\n"  + dump, expectedCounter, counter);
  }
}
