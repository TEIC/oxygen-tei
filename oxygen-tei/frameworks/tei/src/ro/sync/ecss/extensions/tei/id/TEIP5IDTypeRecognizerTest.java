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
package ro.sync.ecss.extensions.tei.id;

import java.io.File;
import java.util.List;

import javax.swing.text.Highlighter.Highlight;

import ro.sync.contentcompletion.editor.NonCCEditor;
import ro.sync.document.SyntaxDocumentBase;
import ro.sync.exml.editor.AbstractEditor;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.editor.MarkOccurencesSupportBase;
import ro.sync.exml.editor.xsleditor.pagetext.MarkOccurrencesSupportTestBase;
import ro.sync.util.URLUtil;


/**
 * @author radu_pisoi
 *
 */
public class TEIP5IDTypeRecognizerTest extends MarkOccurrencesSupportTestBase {
  /**
   * <p><b>Description:</b> Test for mark ID/IDREF occurrences in a TEI P5</p>
   * <p><b>Bug ID:</b> EXM-27785</p>
   *
   * @author radu_pisoi
   * @throws Exception
   */
  public void testMarkOccurrencesTEI_P5() throws Exception {
    AbstractEditor editor = open(
        URLUtil.correct(new File("test/EXM-27785/testIDREFS_TEI-P5.xml")), EditorPageConstants.PAGE_TEXT);
    MarkOccurencesSupportBase.MARK_OCCURRENCES_DELAY = 0;
    
    // Set caret before 'hierarchyAndReferences'
    textPageMoveCaretRelativeTo("Markup", 1);
    flushAWTBetter();
    Thread.sleep(500);
    
    // Assert the highlights
    NonCCEditor activeEditor = (NonCCEditor)editor.getTextPage().getCCEditor().getActiveComponent();
    SyntaxDocumentBase document = (SyntaxDocumentBase) activeEditor.getDocument();
    List<Highlight> highlights = getMarkOccurrencesHighlights(activeEditor);
    assertNotNull(highlights);
    assertEquals(0, highlights.size());
    
    // Set caret inside 'harris.anderson'
    textPageMoveCaretRelativeTo("#P1", 1);
    flushAWTBetter();
    Thread.sleep(500);
    
    // Assert the highlights
    highlights = getMarkOccurrencesHighlights(activeEditor);
    assertNotNull(highlights);
    assertEquals(4, highlights.size());
    
    Highlight highlight = highlights.get(0);
    int startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(13, startLine);
    int length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("P1", document.getText(highlight.getStartOffset(), length));

    highlight = highlights.get(1);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(15, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("P1", document.getText(highlight.getStartOffset(), length));    

    highlight = highlights.get(2);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(19, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("P1", document.getText(highlight.getStartOffset(), length));
    
    highlight = highlights.get(3);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(26, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("P1", document.getText(highlight.getStartOffset(), length));    
    
    
    // Set caret inside 'helen.jackson'
    textPageMoveCaretRelativeTo("P2", 1);
    flushAWTBetter();
    Thread.sleep(500);
    
    // Assert the highlights
    highlights = getMarkOccurrencesHighlights(activeEditor);
    assertNotNull(highlights);
    assertEquals(2, highlights.size());
    
    highlight = highlights.get(0);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(13, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("P2", document.getText(highlight.getStartOffset(), length));
    
    highlight = highlights.get(1);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(22, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("P2", document.getText(highlight.getStartOffset(), length));  
  }

  /**
   * <p><b>Description:</b> Test for mark ID/IDREF occurrences in a TEI P5</p>
   * <p><b>Bug ID:</b> EXM-27785</p>
   *
   * @author radu_pisoi
   * @throws Exception
   */
  public void testMarkOccurrencesTEI_P5_1() throws Exception {
    AbstractEditor editor = open(
        URLUtil.correct(new File("test/EXM-27785/testIDREFS_TEI-P5_1.xml")), EditorPageConstants.PAGE_TEXT);
    MarkOccurencesSupportBase.MARK_OCCURRENCES_DELAY = 0;
    
    // Set caret before 'hierarchyAndReferences'
    textPageMoveCaretRelativeTo("PREFACE", 1);
    flushAWTBetter();
    Thread.sleep(500);
    
    // Assert the highlights
    NonCCEditor activeEditor = (NonCCEditor)editor.getTextPage().getCCEditor().getActiveComponent();
    SyntaxDocumentBase document = (SyntaxDocumentBase) activeEditor.getDocument();
    List<Highlight> highlights = getMarkOccurrencesHighlights(activeEditor);
    assertNotNull(highlights);
    assertEquals(0, highlights.size());
    
    // Set caret inside first target
    textPageMoveCaretRelativeTo("#dd", 1);
    flushAWTBetter();
    Thread.sleep(500);
    
    // Assert the highlights
    highlights = getMarkOccurrencesHighlights(activeEditor);
    assertNotNull(highlights);
    assertEquals(2, highlights.size());
    
    Highlight highlight = highlights.get(0);
    int startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(8, startLine);
    int length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("dd", document.getText(highlight.getStartOffset(), length));
  
    highlight = highlights.get(1);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(10, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("dd", document.getText(highlight.getStartOffset(), length));    
  
    // Set caret inside the second target
    textPageMoveCaretRelativeTo("#aadd", 1);
    flushAWTBetter();
    Thread.sleep(500);
    
    // Assert the highlights
    highlights = getMarkOccurrencesHighlights(activeEditor);
    assertNotNull(highlights);
    assertEquals(1, highlights.size());
    
    highlight = highlights.get(0);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(9, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("aadd", document.getText(highlight.getStartOffset(), length));
    
    // Set caret inside the third target
    textPageMoveCaretRelativeTo("#dddd", 1);
    flushAWTBetter();
    Thread.sleep(500);
    
    // Assert the highlights
    highlights = getMarkOccurrencesHighlights(activeEditor);
    assertNotNull(highlights);
    assertEquals(1, highlights.size());
    
    highlight = highlights.get(0);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(10, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("dddd", document.getText(highlight.getStartOffset(), length));
    
    // Set caret inside the third target
    textPageMoveCaretRelativeTo("#dddd #dd", 7);
    flushAWTBetter();
    Thread.sleep(500);
    
    // Assert the highlights
    highlights = getMarkOccurrencesHighlights(activeEditor);
    assertNotNull(highlights);
    assertEquals(2, highlights.size());
    
    highlight = highlights.get(0);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(8, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("dd", document.getText(highlight.getStartOffset(), length));
  
    highlight = highlights.get(1);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(10, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("dd", document.getText(highlight.getStartOffset(), length));
    
    // Set caret inside the third target
    textPageMoveCaretRelativeTo("#aaaa", 2);
    flushAWTBetter();
    Thread.sleep(500);
    
    // Assert the highlights
    highlights = getMarkOccurrencesHighlights(activeEditor);
    assertNotNull(highlights);
    assertEquals(1, highlights.size());
    
    highlight = highlights.get(0);
    startLine = document.getLineOfOffset(highlight.getStartOffset());
    assertEquals(10, startLine);
    length = highlight.getEndOffset() - highlight.getStartOffset();
    assertEquals("aaaa", document.getText(highlight.getStartOffset(), length));
  }
}