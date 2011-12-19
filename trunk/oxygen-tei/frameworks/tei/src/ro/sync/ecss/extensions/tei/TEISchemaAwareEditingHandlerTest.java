/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2007 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.tei;

import java.io.File;

import ro.sync.ecss.component.AuthorSchemaAwareOptions;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.exml.options.OptionTags;
import ro.sync.exml.options.Options;
import ro.sync.util.URLUtil;

/**
 * @author alex_jitianu
 */
public class TEISchemaAwareEditingHandlerTest extends EditorAuthorExtensionTestBase {
  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    Options.getInstance().setObjectProperty(OptionTags.AUTHOR_EDITING_MODE, AuthorSchemaAwareOptions.DEFAULT);
    Options.getInstance().setBooleanProperty(OptionTags.VALIDATE_AS_YOU_TYPE, false);
    super.setUp();
  }
  
  /**
   * <p><b>Description:</b> When typing, as a fallback, insert a paragraph.</p>
   * <p><b>Bug ID:</b> EXM-21364</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testTypingFallback() throws Exception {
    open(URLUtil.correct(new File("test/EXM-16505/testEXM-16505_3.xml")), true);

    int offset = getDocumentContent().indexOf("text1") + "text1".length();
    //Move between ordered list
    moveCaret(offset + 2);
    
    Options.getInstance().setObjectProperty(OptionTags.AUTHOR_EDITING_MODE, AuthorSchemaAwareOptions.DEFAULT);
    //Type something
    sendString("para");
    
    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
        "    <teiHeader>\n" + 
        "        <fileDesc>\n" + 
        "            <titleStmt>\n" + 
        "                <title>Title</title>\n" + 
        "            </titleStmt>\n" + 
        "            <publicationStmt>\n" + 
        "                <p>Publication Information</p>\n" + 
        "            </publicationStmt>\n" + 
        "            <sourceDesc>\n" + 
        "                <p>Information about the source</p>\n" + 
        "            </sourceDesc>\n" + 
        "        </fileDesc>\n" + 
        "    </teiHeader>\n" + 
        "    <text>\n" + 
        "        <body>\n" + 
        "            <list type=\"ordered\">\n" + 
        "                <item>text1</item>\n" + 
        "            </list>\n" + 
        "            <p>para</p>\n" + 
        "            <list type=\"ordered\">\n" + 
        "                <item>text2</item>\n" + 
        "            </list>\n" + 
        "        </body>\n" + 
        "    </text>\n" + 
        "</TEI>",
        serializeDocumentViewport(vViewport, true));
    
    offset = getDocumentContent().indexOf("text1");
    //Move between title and section
    moveCaret(offset - 3);
    //Type something
    flushAWTBetter();
    sendString("para");
    
    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" +  
        "    <teiHeader>\n" + 
        "        <fileDesc>\n" + 
        "            <titleStmt>\n" + 
        "                <title>Title</title>\n" + 
        "            </titleStmt>\n" + 
        "            <publicationStmt>\n" + 
        "                <p>Publication Information</p>\n" + 
        "            </publicationStmt>\n" + 
        "            <sourceDesc>\n" + 
        "                <p>Information about the source</p>\n" + 
        "            </sourceDesc>\n" + 
        "        </fileDesc>\n" + 
        "    </teiHeader>\n" + 
        "    <text>\n" + 
        "        <body>\n" + 
        "            <list type=\"ordered\">\n" + 
        "                <item>text1</item>\n" + 
        "            </list>\n" + 
        "            <p>para</p>\n" + 
        "            <list type=\"ordered\">\n" + 
        "                <item>text2</item>\n" + 
        "            </list>\n" + 
        "        </body>\n" + 
        "    </text>\n" + 
        "</TEI>",
        serializeDocumentViewport(vViewport, true));
  }
}