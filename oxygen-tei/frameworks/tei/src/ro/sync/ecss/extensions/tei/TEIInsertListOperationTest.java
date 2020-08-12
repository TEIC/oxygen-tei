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
package ro.sync.ecss.extensions.tei;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ro.sync.ecss.component.AuthorViewport;
import ro.sync.ecss.extensions.ArgumentsMapImpl;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.commons.id.GenerateIDElementsInfo;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.ui.UiUtil;
import ro.sync.util.URLUtil;

/**
 * Tests for {@link TEIInsertListOperation}.
 */
public class TEIInsertListOperationTest extends EditorAuthorExtensionTestBase {

  /**
   * <p><b>Description:</b> Test convert selected paragraphs to ordered list.</p>
   * <p><b>Bug ID:</b> EXM-36984</p>
   *
   * @author adriana_sbircea
   * @author alina_iordache
   *
   * @throws Exception
   */
  public void testConvertParagraphsToOrderedList() throws Exception {
  
    // Open document
    open(URLUtil.correct(new File("test/EXM-36984/testParaTEI.xml")), false, false);
    flushAWTBetter();
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    
    //No ID generation
    GenerateIDElementsInfo generateIDElementsInfo = new GenerateIDElementsInfo(
        false, "pattern", new String[0]);
    generateIDElementsInfo.saveToOptions(authorEditorPage.getAuthorAccess()); 
    
    // Select the entire paragraphs (with sentinels)
    moveCaretRelativeTo("Some text", -1);
    moveCaretRelativeTo("Other para.", "Other para.".length() + 1, true);
    flushAWTBetter();
    
    // Convert selection to an ordered list
    final TEIInsertListOperation op = new TEIInsertListOperation();
    Map userValues = new HashMap();
    userValues.put(TEIInsertListOperation.LIST_TYPE_ARGUMENT, TEIInsertListOperation.ORDERED_LIST);
    
    final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
    argumentsMap.copyMap(userValues);
    
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    
    // Assert content
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
        "  <teiHeader>\n" + 
        "      <fileDesc>\n" + 
        "         <titleStmt>\n" + 
        "            <title>Title</title>\n" + 
        "         </titleStmt>\n" + 
        "         <publicationStmt>\n" + 
        "            <p>Publication Information</p>\n" + 
        "         </publicationStmt>\n" + 
        "         <sourceDesc>\n" + 
        "            <p>Information about the source</p>\n" + 
        "         </sourceDesc>\n" + 
        "      </fileDesc>\n" + 
        "  </teiHeader>\n" + 
        "  <text>\n" + 
        "      <body>\n" + 
        "            <list type=\"ordered\">\n" + 
        "                <item>Some text here.</item>\n" + 
        "                <item>Second paragraph.</item>\n" + 
        "                <item ana=\"aaaa\" copyOf=\"copy\">Other para.</item>\n" + 
        "            </list>\n" + 
        "        </body>\n" + 
        "  </text>\n" + 
        "</TEI>\n" + 
        "", 
        serializeDocumentViewport(viewport, true));
  }

  /**
   * <p><b>Description:</b> Test convert selected paragraphs to itemized list.</p>
   * <p><b>Bug ID:</b> EXM-36984</p>
   *
   * @author adriana_sbircea
   * @author alina_iordache
   *
   * @throws Exception
   */
  public void testConvertParagraphsToItemizedList() throws Exception {
  
    // Open document
    open(URLUtil.correct(new File("test/EXM-36984/testParaTEI.xml")), false, false);
    flushAWTBetter();
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    
    //No ID generation
    GenerateIDElementsInfo generateIDElementsInfo = new GenerateIDElementsInfo(
        false, "pattern", new String[0]);
    generateIDElementsInfo.saveToOptions(authorEditorPage.getAuthorAccess()); 
    
    // Select the entire paragraphs (with sentinels)
    moveCaretRelativeTo("Some text", -1);
    moveCaretRelativeTo("Other para.", "Other para.".length() + 1, true);
    flushAWTBetter();
    
    // Convert selection to an itemized list
    final TEIInsertListOperation op = new TEIInsertListOperation();
    Map userValues = new HashMap();
    userValues.put(TEIInsertListOperation.LIST_TYPE_ARGUMENT, TEIInsertListOperation.ITEMIZED_LIST);
    
    final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
    argumentsMap.copyMap(userValues);
    
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    
    // Assert content
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
        "  <teiHeader>\n" + 
        "      <fileDesc>\n" + 
        "         <titleStmt>\n" + 
        "            <title>Title</title>\n" + 
        "         </titleStmt>\n" + 
        "         <publicationStmt>\n" + 
        "            <p>Publication Information</p>\n" + 
        "         </publicationStmt>\n" + 
        "         <sourceDesc>\n" + 
        "            <p>Information about the source</p>\n" + 
        "         </sourceDesc>\n" + 
        "      </fileDesc>\n" + 
        "  </teiHeader>\n" + 
        "  <text>\n" + 
        "      <body>\n" + 
        "            <list type=\"bulleted\">\n" + 
        "                <item>Some text here.</item>\n" + 
        "                <item>Second paragraph.</item>\n" + 
        "                <item ana=\"aaaa\" copyOf=\"copy\">Other para.</item>\n" + 
        "            </list>\n" + 
        "        </body>\n" + 
        "  </text>\n" + 
        "</TEI>\n" + 
        "", 
        serializeDocumentViewport(viewport, true));
  }

  /**
   * <p><b>Description:</b> Test insert ordered list.</p>
   * <p><b>Bug ID:</b> EXM-36984</p>
   *
   * @author adriana_sbircea
   * @author alina_iordache
   *
   * @throws Exception
   */
  public void testInsertOrderedList() throws Exception {
  
    // Open document
    open(URLUtil.correct(new File("test/EXM-36984/testParaTEI.xml")), false, false);
    flushAWTBetter();
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    
    //No ID generation
    GenerateIDElementsInfo generateIDElementsInfo = new GenerateIDElementsInfo(
        false, "pattern", new String[0]);
    generateIDElementsInfo.saveToOptions(authorEditorPage.getAuthorAccess()); 
    
    // No selection
    moveCaretRelativeTo("Some text", -1);
    flushAWTBetter();
    
    // Convert selection to an ordered list
    final TEIInsertListOperation op = new TEIInsertListOperation();
    Map userValues = new HashMap();
    userValues.put(TEIInsertListOperation.LIST_TYPE_ARGUMENT, TEIInsertListOperation.ORDERED_LIST);
    
    final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
    argumentsMap.copyMap(userValues);
    
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    
    // Assert content
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
        "  <teiHeader>\n" + 
        "      <fileDesc>\n" + 
        "         <titleStmt>\n" + 
        "            <title>Title</title>\n" + 
        "         </titleStmt>\n" + 
        "         <publicationStmt>\n" + 
        "            <p>Publication Information</p>\n" + 
        "         </publicationStmt>\n" + 
        "         <sourceDesc>\n" + 
        "            <p>Information about the source</p>\n" + 
        "         </sourceDesc>\n" + 
        "      </fileDesc>\n" + 
        "  </teiHeader>\n" + 
        "  <text>\n" + 
        "      <body>\n" + 
        "            <list type=\"ordered\">\n" + 
        "                <item/>\n" + 
        "            </list>\n" + 
        "         <p>Some text here.</p>\n" + 
        "         <p xml:id=\"p_mwv_dzj_lx\">Second paragraph.</p>\n" + 
        "         <p ana=\"aaaa\" copyOf=\"copy\">Other para.</p>\n" + 
        "      </body>\n" + 
        "  </text>\n" + 
        "</TEI>\n" + 
        "", 
        serializeDocumentViewport(viewport, true));
  }

  /**
   * <p><b>Description:</b> Test convert insert unordered list.</p>
   * <p><b>Bug ID:</b> EXM-36984</p>
   *
   * @author adriana_sbircea
   * @author alina_iordache
   *
   * @throws Exception
   */
  public void testInsertItemizedList() throws Exception {
  
    // Open document
    open(URLUtil.correct(new File("test/EXM-36984/testParaTEI.xml")), false, false);
    flushAWTBetter();
    
    AuthorEditorPage authorEditorPage = (AuthorEditorPage) editor.getCurrentPage();
    AuthorViewport viewport = authorEditorPage.getViewport();
    
    //No ID generation
    GenerateIDElementsInfo generateIDElementsInfo = new GenerateIDElementsInfo(
        false, "pattern", new String[0]);
    generateIDElementsInfo.saveToOptions(authorEditorPage.getAuthorAccess()); 
    
    // No selection
    moveCaretRelativeTo("Some text", -1);
    flushAWTBetter();
    
    // Insert an itemized list
    final TEIInsertListOperation op = new TEIInsertListOperation();
    Map userValues = new HashMap();
    userValues.put(TEIInsertListOperation.LIST_TYPE_ARGUMENT, TEIInsertListOperation.ITEMIZED_LIST);
    
    final ArgumentsMapImpl argumentsMap = new ArgumentsMapImpl();
    argumentsMap.copyMap(userValues);
    
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(vViewport.getAuthorAccess(), argumentsMap);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    
    // Assert content
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
        "<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
        "<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
        "  <teiHeader>\n" + 
        "      <fileDesc>\n" + 
        "         <titleStmt>\n" + 
        "            <title>Title</title>\n" + 
        "         </titleStmt>\n" + 
        "         <publicationStmt>\n" + 
        "            <p>Publication Information</p>\n" + 
        "         </publicationStmt>\n" + 
        "         <sourceDesc>\n" + 
        "            <p>Information about the source</p>\n" + 
        "         </sourceDesc>\n" + 
        "      </fileDesc>\n" + 
        "  </teiHeader>\n" + 
        "  <text>\n" + 
        "      <body>\n" + 
        "            <list type=\"bulleted\">\n" + 
        "                <item/>\n" + 
        "            </list>\n" + 
        "         <p>Some text here.</p>\n" + 
        "         <p xml:id=\"p_mwv_dzj_lx\">Second paragraph.</p>\n" + 
        "         <p ana=\"aaaa\" copyOf=\"copy\">Other para.</p>\n" + 
        "      </body>\n" + 
        "  </text>\n" + 
        "</TEI>\n" + 
        "", 
        serializeDocumentViewport(viewport, true));
  }
}