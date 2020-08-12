/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2015 Syncro Soft SRL, Romania.  All rights
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
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ro.sync.ecss.extensions.AuthorAccessImpl;
import ro.sync.ecss.extensions.AuthorAccessUtilImpl;
import ro.sync.ecss.extensions.api.access.AuthorUtilAccess;
import ro.sync.ecss.extensions.api.access.AuthorXMLUtilAccess;
import ro.sync.io.FileSystemUtil;
import ro.sync.util.URLUtil;
import ro.sync.util.editorvars.EditorVariables;
import ro.sync.util.editorvars.EditorVariables.FrameworkRewritePolicy;
import ro.sync.xml.catalogresolver.CatalogResolverFactory;

/**
 * Utility methods tests.
 * 
 * @author alex_jitianu
 */
public class CommonsOperationsUtilTest extends TestCase {

  /**
   * <p><b>Description:</b> Expands a path relative to the frameworks directory.</p>
   * <p><b>Bug ID:</b> EXM-34963</p>
   *
   * @author alex_jitianu
   *
   * @throws Exception
   */
  public void testExpandPath() throws Exception {
    AuthorAccessImpl authorAccess = Mockito.mock(AuthorAccessImpl.class);
    AuthorUtilAccess utilAccess = Mockito.mock(AuthorAccessUtilImpl.class);

    File frDir = new File("test/frameworks/EXM-34963");
    frDir.mkdirs();

    try {
      File frFile = new File(frDir, "fr.framework");
      new File(frDir, "test.xml").createNewFile();
      final String frameworkStoreLocation = URLUtil.correct(frFile).toString();

      Mockito.when(authorAccess.getUtilAccess()).thenReturn(utilAccess);
      Mockito.when(utilAccess.expandEditorVariables(Mockito.anyString(), (URL) Mockito.any())).thenAnswer(new Answer<String>() {
        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
          Object[] arguments = invocation.getArguments();
          String expanded =  EditorVariables.expandEditorVariables((String) arguments[0], (String) arguments[1]);
          expanded = EditorVariables.expandFrameworksVariables(expanded, frameworkStoreLocation, FrameworkRewritePolicy.REWRITE_ABSOLUTE);

          return expanded;
        }
      });
      Mockito.when(utilAccess.convertFileToURL((File) Mockito.any())).then(new Answer<URL>() {
        @Override
        public URL answer(InvocationOnMock invocation) throws Throwable {
          return URLUtil.correct((File) invocation.getArguments()[0]);
        }
      });
      
      AuthorXMLUtilAccess xmlUtilAccess = new AuthorAccessUtilImpl();
      Mockito.when(authorAccess.getXMLUtilAccess()).thenReturn(xmlUtilAccess);
      
      final String toMap = "http://www.oxygenxml.com/fake.xsl";
      final String mapped = "file://mapped.xsl";
      CatalogResolverFactory.addPriorityURIResolver(new URIResolver() {
        @Override
        public Source resolve(String href, String base) throws TransformerException {
          if (href.equals(toMap)) {
            return new StreamSource(mapped);
          }
          return null;
        }
      });

      URL expected = URLUtil.resolveRelativeSystemIDs(new URL(frameworkStoreLocation), "test.xml");

      URL expandPath = CommonsOperationsUtil.expandAndResolvePath(authorAccess, "test.xml");
      assertEquals(expected.toString(), expandPath.toString());

      expandPath = CommonsOperationsUtil.expandAndResolvePath(authorAccess, "${framework}/test.xml");
      assertEquals(expected.toString(), expandPath.toString());

      expandPath = CommonsOperationsUtil.expandAndResolvePath(authorAccess, "${frameworkDir}/test.xml");
      assertEquals(expected.toString(), expandPath.toString());
      
      String script = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" + 
          "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" + 
          "    exclude-result-prefixes=\"xs\"\n" + 
          "    version=\"2.0\">\n" + 
          "    \n" + 
          "</xsl:stylesheet>";
      
      expandPath = CommonsOperationsUtil.expandAndResolvePath(authorAccess, script);
      assertNull("Unexpected path expanding: " + expandPath, expandPath);
      
      expandPath = CommonsOperationsUtil.expandAndResolvePath(authorAccess, toMap);
      assertEquals("The URL is passed through the catalog", mapped, expandPath.toString());
    } finally {
      FileSystemUtil.deleteRecursivelly(frDir);
    }
  }
}
