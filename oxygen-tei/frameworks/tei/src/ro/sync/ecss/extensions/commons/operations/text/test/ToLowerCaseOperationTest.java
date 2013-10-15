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
import ro.sync.ecss.extensions.commons.operations.text.ToLowerCaseOperation;
import ro.sync.exml.Tags;
import ro.sync.util.URLUtil;

/**
 * Test for the operation {@link ToLowerCaseOperation}.
 * 
 * @author Costi
 */
public class ToLowerCaseOperationTest extends EditorAuthorExtensionTestBase {
  /**
   * <p><b>Description:</b> Test for 'To lower case' operation when selection contains only text,
   *                        without markup.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testToLowerCaseSimpleText() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/toUpperLowerCase.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("EXM-20398", 0);
    moveCaretRelativeTo("EXM-20398", "EXM-20398".length(), true);
    
    // Execute 'To lowercase' action
    invokeCommonAction(Tags.TO_LOWER, Tags.SOURCE);
    
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
        "                >exm-20398</emphasis>.</para>\n" + 
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
    assertEquals("exm-20398", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'To lower case' operation when selection is unbalanced.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testToLowerCaseUnbalancedText() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/toUpperLowerCase.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("TO test", 0);
    moveCaretRelativeTo("TO test", "TO test".length() + 4, true);
    
    // Execute 'To lowercase' action
    invokeCommonAction(Tags.TO_LOWER, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section title</title>\n" + 
        "        <para>Text in <emphasis role=\"italic\">paragraph</emphasis> to test <emphasis role=\"bold\"\n" + 
        "                >exM-20398</emphasis>.</para>\n" + 
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
    assertEquals("to test ex", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'To lower case' operation when selection is highly unbalanced.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testToLowerCaseUnbalancedText2() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/toUpperLowerCase.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("THIRD", 0);
    moveCaretRelativeTo("THIRD", "THIRD".length() + 12, true);
    
    // Execute 'To lowercase' action
    invokeCommonAction(Tags.TO_LOWER, Tags.SOURCE);
    
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
        "                    <para>first item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>second item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>third item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>fouRTH ITEM</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals("third itemfou", vViewport.getSelectedText());
  }
  
  /**
   * <p><b>Description:</b> Test for 'To lower case' operation when all document text is selected.</p>
   * <p><b>Bug ID:</b> EXM-20398</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testToLowerCaseAllTextSelection() throws Exception {
    open(URLUtil.correct(new File("test/EXM-20398/toUpperLowerCase.xml")));
      
    // Select paragraph text
    moveCaretRelativeTo("Test", 0);
    moveCaretRelativeTo("Test", "Test".length() + 137, true);
    
    // Execute 'To lowercase' action
    invokeCommonAction(Tags.TO_LOWER, Tags.SOURCE);
    
    // Assert content after perform action
    verifyDocument(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>test article</title>\n" + 
        "    <sect1>\n" + 
        "        <title>section title</title>\n" + 
        "        <para>text in <emphasis role=\"italic\">paragraph</emphasis> to test <emphasis role=\"bold\"\n" + 
        "                >exm-20398</emphasis>.</para>\n" + 
        "        <para>test list:<orderedlist>\n" + 
        "                <listitem>\n" + 
        "                    <para>first item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>second item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>third item</para>\n" + 
        "                </listitem>\n" + 
        "                <listitem>\n" + 
        "                    <para>fourth item</para>\n" + 
        "                </listitem>\n" + 
        "            </orderedlist></para>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        true);
    assertEquals(
        "test articlesection titletext in paragraph to test exm-20398.test list:first itemsecond itemthird itemfourth item",
        vViewport.getSelectedText());
  }
}