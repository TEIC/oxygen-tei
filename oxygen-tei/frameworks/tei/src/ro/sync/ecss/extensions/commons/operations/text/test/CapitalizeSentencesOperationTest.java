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
import ro.sync.ecss.extensions.commons.operations.text.CapitalizeSentencesOperation;
import ro.sync.exml.Tags;
import ro.sync.util.URLUtil;

/**
 * Test for the operation {@link CapitalizeSentencesOperation}.
 * 
 * @author Costi
 */
public class CapitalizeSentencesOperationTest extends EditorAuthorExtensionTestBase {
  /**
   * <p><b>Description:</b> Test for 'Form Sentences' operation when selection contains only text,
   *                        without markup.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormSentencesSimpleText() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formSentences.xml")), true);
      
    // Select paragraph text
    moveCaretRelativeTo("paragraph text used to test", 0);
    moveCaretRelativeTo("paragraph text used to test", "paragraph text used to test".length(), true);
    
    // Execute 'Capitalize Sentences' action
    invokeCommonAction(Tags.CAPITALIZE_SENTENCES, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>section title</title>\n" + 
        "        <para>Paragraph text used to test <emphasis role=\"bold\">exm-20398</emphasis>. second\n" + 
        "            sentence inline.</para>\n" + 
        "        <para>new pa<emphasis role=\"bold\">ra</emphasis>graph01</para>\n" + 
        "        <para>new line</para>\n" + 
        "        <para>paragraph2 text. </para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "            <listitem>\n" + 
        "                <para>first test.</para>\n" + 
        "            </listitem>\n" + 
        "            <listitem>\n" + 
        "                <para>second test</para>\n" + 
        "            </listitem>\n" + 
        "            <listitem>\n" + 
        "                <para>third test</para>\n" + 
        "            </listitem>\n" + 
        "            <listitem>\n" + 
        "                <para>Fourth test</para>\n" + 
        "            </listitem>\n" + 
        "        </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals("Paragraph text used to test", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'Form Sentences' operation when selection is unbalanced.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormSentencesUnbalancedText() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formSentences.xml")), true);
      
    // Select paragraph text
    moveCaretRelativeTo("paragraph text", 0);
    moveCaretRelativeTo("paragraph text", "paragraph text".length() + 18, true);
    
    // Execute 'Capitalize Sentences' action
    invokeCommonAction(Tags.CAPITALIZE_SENTENCES, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>section title</title>\n" + 
        "        <para>Paragraph text used to test <emphasis role=\"bold\">exm-20398</emphasis>. second\n" + 
        "            sentence inline.</para>\n" + 
        "        <para>new pa<emphasis role=\"bold\">ra</emphasis>graph01</para>\n" + 
        "        <para>new line</para>\n" + 
        "        <para>paragraph2 text. </para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "            <listitem>\n" + 
        "                <para>first test.</para>\n" + 
        "            </listitem>\n" + 
        "            <listitem>\n" + 
        "                <para>second test</para>\n" + 
        "            </listitem>\n" + 
        "            <listitem>\n" + 
        "                <para>third test</para>\n" + 
        "            </listitem>\n" + 
        "            <listitem>\n" + 
        "                <para>Fourth test</para>\n" + 
        "            </listitem>\n" + 
        "        </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals("Paragraph text used to test exm", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'Form Sentences' operation when selection is unbalanced.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormSentencesUnbalancedText2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formSentences.xml")), true);
      
    // Select paragraph text
    moveCaretRelativeTo("graph01", 0);
    moveCaretRelativeTo("graph01", "graph01".length() + 10, true);
    
    // Execute 'Capitalize Sentences' action
    invokeCommonAction(Tags.CAPITALIZE_SENTENCES, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>section title</title>\n" + 
        "        <para>paragraph text used to test <emphasis role=\"bold\">exm-20398</emphasis>. second\n" + 
        "            sentence inline.</para>\n" + 
        "        <para>new pa<emphasis role=\"bold\">ra</emphasis>graph01</para>\n" + 
        "        <para>New line</para>\n" + 
        "        <para>paragraph2 text. </para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "            <listitem>\n" + 
        "                <para>first test.</para>\n" + 
        "            </listitem>\n" + 
        "            <listitem>\n" + 
        "                <para>second test</para>\n" + 
        "            </listitem>\n" + 
        "            <listitem>\n" + 
        "                <para>third test</para>\n" + 
        "            </listitem>\n" + 
        "            <listitem>\n" + 
        "                <para>Fourth test</para>\n" + 
        "            </listitem>\n" + 
        "        </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals("graph01New line", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'Form Sentences' operation when selection is highly unbalanced.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormSentencesUnbalancedText3() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formSentences.xml")), true);
      
    // Select paragraph text
    moveCaretRelativeTo("list", 0);
    moveCaretRelativeTo("list", "list".length() + 14, true);
    
    // Execute 'Capitalize Sentences' action
    invokeCommonAction(Tags.CAPITALIZE_SENTENCES, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>section title</title>\n" + 
        "        <para>paragraph text used to test <emphasis role=\"bold\">exm-20398</emphasis>. second\n" + 
        "            sentence inline.</para>\n" + 
        "        <para>new pa<emphasis role=\"bold\">ra</emphasis>graph01</para>\n" + 
        "        <para>new line</para>\n" + 
        "        <para>paragraph2 text. </para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>First test.</para>\n" + 
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
        "</article>\n", 
        true);
    assertEquals("list:First test", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'Form Sentences' operation when all document text is selected.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormSentencesAllTextSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/formSentences.xml")), true);
      
    // Select paragraph text
    moveCaretRelativeTo("test article", 0);
    moveCaretRelativeTo("Fourth test", "Fourth test".length(), true);
    
    // Execute 'Capitalize Sentences' action
    invokeCommonAction(Tags.CAPITALIZE_SENTENCES, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section title</title>\n" + 
        "        <para>Paragraph text used to test <emphasis role=\"bold\">exm-20398</emphasis>. Second\n" + 
        "            sentence inline.</para>\n" + 
        "        <para>New pa<emphasis role=\"bold\">ra</emphasis>graph01</para>\n" + 
        "        <para>New line</para>\n" + 
        "        <para>Paragraph2 text. </para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>First test.</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>Second test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>Third test</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>Fourth test</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals(
        "Test articleSection titleParagraph text used to test exm-20398. Second sentence inline."
        + "New paragraph01New lineParagraph2 text. Test list:First test.Second testThird testFourth test",
        vViewport.getSelectedText());
  }

  /**
   * <p><b>Description:</b> Test for 'Form Sentences' operation when selection contains only text,
   *                        without markup.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testFormSentencesSimpleText2() throws Exception {
    documentTypeIsRequired = false;
    open(URLUtil.correct(new File("test/EXM-20398/formSentences2.xml")), true);
      
    // Select paragraph text
    moveCaretRelativeTo("prop 1", 0);
    moveCaretRelativeTo("prop 2.", "prop 2.".length(), true);
    
    // Execute 'Capitalize Sentences' action
    invokeCommonAction(Tags.CAPITALIZE_SENTENCES, Tags.SOURCE);
    
    // Assert content after perform action
    assertEquals("Prop 1. Prop 2.", vViewport.getSelectedText());
  }
}