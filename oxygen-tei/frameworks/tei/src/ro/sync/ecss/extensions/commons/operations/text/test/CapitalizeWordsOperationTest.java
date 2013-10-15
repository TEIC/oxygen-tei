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
package ro.sync.ecss.extensions.commons.operations.text.test;

import java.io.File;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.commons.operations.text.CapitalizeWordsOperation;
import ro.sync.ecss.layout.DumpConfiguration;
import ro.sync.exml.Tags;
import ro.sync.util.URLUtil;

/**
 * Test for the operation {@link CapitalizeWordsOperation}.
 * 
 * @author Costi
 */
public class CapitalizeWordsOperationTest extends EditorAuthorExtensionTestBase {
  /**
   * <p><b>Description:</b> Test for 'Form Words' operation when selection contains only text,
   *                        without markup.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormWordsSimpleText() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formWords.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("paragraph text used", 0);
    moveCaretRelativeTo("paragraph text used", "paragraph text used".length(), true);
    
    // Execute 'Capitalize words' action
    invokeCommonAction(Tags.CAPITALIZE_WORDS, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>section title</title>\n" + 
        "        <para>Paragraph Text Used to test <emphasis role=\"bold\">exm-20398</emphasis>.</para>\n" + 
        "        <para>Paragraph2 text. </para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>first test.</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>second test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>third test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>Fourth test</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>",
        true);
    assertEquals("Paragraph Text Used", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'Form Words' operation when selection is unbalanced.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormWordsUnbalancedText() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formWords.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("to test", 0);
    moveCaretRelativeTo("to test", "to test".length() + 7, true);
    
    // Execute 'Capitalize words' action
    invokeCommonAction(Tags.CAPITALIZE_WORDS, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>section title</title>\n" + 
        "        <para>paragraph text used To Test <emphasis role=\"bold\">Exm-20398</emphasis>.</para>\n" + 
        "        <para>Paragraph2 text. </para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>first test.</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>second test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>third test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>Fourth test</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>", 
        true);
    assertEquals("To Test Exm-2", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'Form Words' operation when selection is highly unbalanced.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormWordsUnbalancedText2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formWords.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("list", 0);
    moveCaretRelativeTo("list", "list".length() + 14, true);
    
    // Execute 'Capitalize words' action
    invokeCommonAction(Tags.CAPITALIZE_WORDS, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>section title</title>\n" + 
        "        <para>paragraph text used to test <emphasis role=\"bold\">exm-20398</emphasis>.</para>\n" + 
        "        <para>Paragraph2 text. </para>\n" + 
        "        <para>Test List:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>First Test.</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>second test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>third test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>Fourth test</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>", 
        true);
    assertEquals("List:First Test", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'Form words' operation when all document text is selected.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormWordsAllTextSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formWords.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("test article", 0);
    moveCaretRelativeTo("test article", "test article".length() + 150, true);
    
    // Execute 'Capitalize words' action
    invokeCommonAction(Tags.CAPITALIZE_WORDS, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Test Article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section Title</title>\n" + 
        "        <para>Paragraph Text Used To Test <emphasis role=\"bold\">Exm-20398</emphasis>.</para>\n" + 
        "        <para>Paragraph2 Text. </para>\n" + 
        "        <para>Test List:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>First Test.</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>Second Test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>Third Test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>Fourth Test</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>", 
        true);
    assertEquals(
        "Test ArticleSection TitleParagraph Text Used To Test Exm-20398.Paragraph2 Text. Test List:First Test.Second TestThird TestFourth Test",
        vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'Form words' operation when we have read-only content.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormWordsReadOnlyText() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formWordsEntity.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("T", 0);
    vViewport.moveTo(vViewport.getController().getAuthorDocument().getLength(), true);
    // Execute 'Capitalize words' action
    invokeCommonAction(Tags.CAPITALIZE_WORDS, Tags.SOURCE);
    
    // Assert content after perform action
    StringBuilder stringBuilder = new StringBuilder();
    vViewport.getRootBox().dump(
        stringBuilder, new DumpConfiguration(false), vViewport.createLayoutContext());
    assertEquals(
        "<RootBox>[]\n" + 
    		"BlockElementBox: <#document>[]\n" + 
    		"  BlockElementBox: <article>[]\n" + 
    		"    BlockElementBox: <title>[]\n" + 
    		"      ParagraphBox[]\n" + 
    		"        LineBox: <title>[]\n" + 
    		"          InlineStaticContentForElementBox: <before>[]\n" + 
    		"            StaticTextBox: 'Article: '[]\n" + 
    		"          DocumentTextBox: 'T'[](Length:1, StartRel:1)\n" + 
    		"    BlockElementBox: <sect1>[]\n" + 
    		"      BlockElementBox: <title>[]\n" + 
    		"        ParagraphBox[]\n" + 
    		"          LineBox: <title>[]\n" + 
    		"            InlineStaticContentForElementBox: <before>[]\n" + 
    		"              StaticTextBox: 'Section '[]\n" + 
    		"              StaticTextBox: '1'[]\n" + 
    		"              StaticTextBox: ': '[]\n" + 
    		"            DocumentTextBox: 'T'[](Length:1, StartRel:1)\n" + 
    		"      BlockElementBox: <para>[]\n" + 
    		"        ParagraphBox[]\n" + 
    		"          LineBox: <para>[]\n" + 
    		"            DocumentTextBox: 'Paragraph '[](Length:10, StartRel:1)\n" + 
    		"            InlineElementBox: <ent>[]\n" + 
    		"              [shape][]\n" + 
    		"              InlineElementBox: <emphasis>[]\n" + 
    		"                [shape][]\n" + 
    		"                DocumentTextBox: 'sometext'[](Length:8, StartRel:1)\n" + 
    		"                [shape][]\n" + 
    		"              [shape][]\n" + 
    		"            DocumentTextBox: '.'[](Length:1, StartRel:1)\n",
    		stringBuilder.toString());
  }
}