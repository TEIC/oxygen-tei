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
import ro.sync.ecss.extensions.commons.operations.text.ToUpperCaseOperation;
import ro.sync.exml.Tags;
import ro.sync.util.URLUtil;

/**
 * Test for the operation {@link ToUpperCaseOperation}.
 * 
 * @author Costi
 */
public class ToUpperCaseOperationTest extends EditorAuthorExtensionTestBase {
  /**
   * <p><b>Description:</b> Test for 'To upper case' operation when selection contains only text,
   *                        without markup.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testToUpperCaseSimpleText() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/toUpperLowerCase.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("Text in", 0);
    moveCaretRelativeTo("Text in", "Text in".length(), true);
    
    // Execute 'To uppercase' action
    invokeCommonAction(Tags.TO_UPPER, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section title</title>\n" + 
        "        <para>TEXT IN <emphasis role=\"italic\">paragraph</emphasis> TO test <emphasis role=\"bold\"\n" + 
        "                >EXM-20398</emphasis>.</para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>first item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>second item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>THIRD ITEM</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>FOURTH ITEM</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals("TEXT IN", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'To upper case' operation when selection is unbalanced.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testToUpperCaseUnbalancedText() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/toUpperLowerCase.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("Text in", 0);
    moveCaretRelativeTo("Text in", "Text in".length() + 6, true);
    
    // Execute 'To uppercase' action
    invokeCommonAction(Tags.TO_UPPER, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section title</title>\n" + 
        "        <para>TEXT IN <emphasis role=\"italic\">PARAgraph</emphasis> TO test <emphasis role=\"bold\"\n" + 
        "                >EXM-20398</emphasis>.</para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>first item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>second item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>THIRD ITEM</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>FOURTH ITEM</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals("TEXT IN PARA", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'To upper case' operation when selection is highly unbalanced.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testToUpperCaseUnbalancedText2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/toUpperLowerCase.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("first item", 0);
    moveCaretRelativeTo("first item", "first item".length() + 10, true);
    
    // Execute 'To uppercase' action
    invokeCommonAction(Tags.TO_UPPER, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section title</title>\n" + 
        "        <para>Text in <emphasis role=\"italic\">paragraph</emphasis> TO test <emphasis role=\"bold\"\n" + 
        "                >EXM-20398</emphasis>.</para>\n" + 
        "        <para>Test list:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>FIRST ITEM</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>SECOND item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>THIRD ITEM</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>FOURTH ITEM</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals("FIRST ITEMSECOND", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'To upper case' operation when all document text is selected.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testToUpperCaseAllTextSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/toUpperLowerCase.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("Test", 0);
    moveCaretRelativeTo("Test", "Test".length() + 137, true);
    
    // Execute 'To uppercase' action
    invokeCommonAction(Tags.TO_UPPER, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>TEST ARTICLE</title>\n" + 
        "    <sect1>\n" + 
        "        <title>SECTION TITLE</title>\n" + 
        "        <para>TEXT IN <emphasis role=\"italic\">PARAGRAPH</emphasis> TO TEST <emphasis role=\"bold\"\n" + 
        "                >EXM-20398</emphasis>.</para>\n" + 
        "        <para>TEST LIST:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>FIRST ITEM</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>SECOND ITEM</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>THIRD ITEM</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>FOURTH ITEM</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals(
        "TEST ARTICLESECTION TITLETEXT IN PARAGRAPH TO TEST EXM-20398.TEST LIST:FIRST ITEMSECOND ITEMTHIRD ITEMFOURTH ITEM",
        vViewport.getSelectedText());
  }
}