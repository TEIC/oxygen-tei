/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2011 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.operations.text;


import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.css.Styles;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Count words either in the whole document or in the selection.
 * 
 * @author Costi Vetezi
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class CountWordsOperation implements AuthorOperation {
  /**
   * Logger for logging. 
   */
  private static final Logger logger = Logger.getLogger(CountWordsOperation.class.getName());
  
  /**
   * Used to compute the number of words in the text.
   */
  private static final int WORD_STATE = 0;
  
  /**
   * Used to compute the number of words in the text.
   */
  private static final int WHITESPACE_STATE = 1;
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
  throws IllegalArgumentException, AuthorOperationException {
    
    // Scope of the entire document
    int startOffset = 0;
    int endOffset = authorAccess.getDocumentController().getAuthorDocumentNode().getEndOffset();
    
    // Selected text scope
    if (authorAccess.getEditorAccess().hasSelection()) {
      startOffset = authorAccess.getEditorAccess().getSelectionStart();
      endOffset = authorAccess.getEditorAccess().getSelectionEnd() - 1;
    }
    
    TextContentIterator contentIterator =
      authorAccess.getDocumentController().getTextContentIterator(startOffset, endOffset);
    
    int wordCount = 0;
    int charCount = 0;
    int wordCountInReadOnly = 0;
    int charCountInReadOnly = 0;
    int wordCountInFilteredCondProfiling = 0;
    int charCountInFilteredCondProfiling = 0;
    
    // Check if it the first iteration
    boolean firstIteration = true;
    
    while (contentIterator.hasNext()) {

      TextContext textContext = contentIterator.next();
      if(textContext.inVisibleContent()){
        int currentEditableState = textContext.getEditableState();
        if (currentEditableState != TextContext.NOT_EDITABLE_IN_DELETE_CHANGE_TRACKING) {
          // We are either in readonly reference or in a editable context
          CharSequence textContent = textContext.getText();
          int textContentLength = textContent.length();
          int currentTextCharCount = 0;
          int currentTextWordCount = 0;
          boolean startsWithCharacter = false;

          int currentWordState = WHITESPACE_STATE;
          for (int i = 0; i < textContentLength; i++) { 
            char ch = textContent.charAt(i);
            if (!Character.isWhitespace(ch)) {
              if (currentWordState == WHITESPACE_STATE) {
                // We are in a whitespace state and we receive a non whitespace character
                // We are switching to word state.
                currentWordState = WORD_STATE;
                currentTextWordCount++;
              } else {
                // We are in a word state and we receive a non whitespace character
                // The state is preserved.
              }

              currentTextCharCount++;
              if (i == 0) {
                // The text starts with a non whitespace character
                startsWithCharacter = true;
              }
            } else {
              // We found a whitespace and we update the current state
              if (currentWordState == WORD_STATE) {
                currentWordState = WHITESPACE_STATE;
              } else {
                // The whitespace state is preserved.
              }
            }
          }
          // We must check if the text of the current node is part of a larger word
          if (startsWithCharacter) {
            if (!firstIteration) {
              if (!isWordStart(authorAccess, textContext.getTextStartOffset())) {
                currentTextWordCount--;
              }
            }
          }
          if (currentEditableState == TextContext.EDITABLE) {
            charCount += currentTextCharCount;
            wordCount += currentTextWordCount;
          } else if (currentEditableState == TextContext.EDITABLE_IN_FILTERED_CONDITIONAL_PROFILING) {
            // Filtered conditional profiling
            charCountInFilteredCondProfiling += currentTextCharCount;
            wordCountInFilteredCondProfiling += currentTextWordCount;
          } else {
            // Read-only reference
            charCountInReadOnly += currentTextCharCount;
            wordCountInReadOnly += currentTextWordCount;
          }

        }

        // This is no longer first iteration
        firstIteration = false;
      }
    }
    
    // Display the statistics
    StringBuilder message = new StringBuilder();

    if (wordCountInReadOnly == 0 && wordCountInFilteredCondProfiling == 0) {
      // We display only word and character count
      message.append("Words: ");
      message.append(wordCount);
      message.append("\n");
      message.append("Characters (no spaces): ");
      message.append(charCount);
    } else {
      String tabString = "     ";
      // Display regular content
      message.append("Regular content:");
      message.append("\n");
      message.append(tabString);
      message.append("Words: ");
      message.append(wordCount);
      message.append("\n");
      message.append(tabString);
      message.append("Characters (no spaces): ");
      message.append(charCount);
      if (wordCountInFilteredCondProfiling > 0) {
        // We have filtered content
        message.append("\n\n");
        message.append("Filtered content:");
        message.append("\n");
        message.append(tabString);
        message.append("Words: ");
        message.append(wordCountInFilteredCondProfiling);
        message.append("\n");
        message.append(tabString);
        message.append("Characters (no spaces): ");
        message.append(charCountInFilteredCondProfiling);
      }
      if (wordCountInReadOnly > 0) {
        // We have read-only content
        message.append("\n\n");
        message.append("Read-only content:");
        message.append("\n");
        message.append(tabString);
        message.append("Words: ");
        message.append(wordCountInReadOnly);
        message.append("\n");
        message.append(tabString);
        message.append("Characters (no spaces): ");
        message.append(charCountInReadOnly);
      }
      // We display overall statistics
      message.append("\n\n");
      message.append("Total:");
      message.append("\n");
      message.append(tabString);
      message.append("Words: ");
      message.append(wordCountInReadOnly + wordCountInFilteredCondProfiling + wordCount);
      message.append("\n");
      message.append(tabString);
      message.append("Characters (no spaces): ");
      message.append(charCountInReadOnly + charCountInFilteredCondProfiling + charCount);
    }
    
    authorAccess.getWorkspaceAccess().showInformationMessage(message.toString());
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Count words either in the whole document or in the selection.";
  }
  
  /**
   * Checks if a word starts at the specified offset.
   * 
   * @param authorAccess  The Author access.
   * @param contentOffset The offset in content.
   * 
   * @return <code>true</code> if a word starts at the specified offset.
   *
   * @throws BadLocationException
   * @throws AuthorOperationException
   */
  private boolean isWordStart(AuthorAccess authorAccess, int contentOffset) {
    AuthorDocumentController documentController = authorAccess.getDocumentController();

    boolean toRet = false;

    try {
      Segment seg = new Segment();
      contentOffset --;
      while (contentOffset > 1) {
        // Read one char
        documentController.getChars(contentOffset, 1, seg);
        char ch = seg.array[seg.offset];      
        if (ch == '\0') {
          // Sentinel character
          OffsetInformation contentInformationAtOffset =
            documentController.getContentInformationAtOffset(contentOffset);

          AuthorNode nodeAtOffset = contentInformationAtOffset.getNodeForMarkerOffset();
          Styles styles = authorAccess.getEditorAccess().getStyles(nodeAtOffset);
          if (!styles.isInline()) {
            // Block sentinel is a sentence delimiter
            toRet = true;
            break;
          }
        } else if (Character.isWhitespace(ch)) {
          // Whitespace found
          toRet = true;
          break;
        } else {
          // This is part of a word
          toRet = false;
          break;
        }
        contentOffset --;
      }
    } catch (BadLocationException ex) {
      logger.error(ex, ex);
    }

    return toRet;
  }
}