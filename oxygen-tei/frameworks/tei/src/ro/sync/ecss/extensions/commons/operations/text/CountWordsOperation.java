/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2022 Syncro Soft SRL, Romania.  All rights
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.ExtensionTags;

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
  private static final Logger logger = LoggerFactory.getLogger(CountWordsOperation.class.getName());
  
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
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    
    // Load messages.
    AuthorResourceBundle authorResourceBundle = authorAccess.getAuthorResourceBundle();
    //"Regular content" internationalized string.
    final String REGULAR_CONTENT = authorResourceBundle.getMessage(ExtensionTags.REGULAR_CONTENT);

    //"Filtered content" internationalized string.
    final String FILTERED_CONTENT = authorResourceBundle.getMessage(ExtensionTags.FILTERED_CONTENT);

    //"Read-only content" internationalized string.
    final String READONLY_CONTENT = authorResourceBundle.getMessage(ExtensionTags.READONLY_CONTENT);
    
    //"Words" internationalized string.
    final String WORDS = authorResourceBundle.getMessage(ExtensionTags.WORDS);
    
    //"Characters" internationalized string.
    final String CHARACTERS = authorResourceBundle.getMessage(ExtensionTags.CHARACTERS);

    //"Characters (no spaces)" internationalized string.
    final String CHARACTERS_NO_SPACES = authorResourceBundle.getMessage(ExtensionTags.CHARACTERS_NO_SPACES);
    
    //"Characters" internationalized string.
    final String TOTAL = authorResourceBundle.getMessage(ExtensionTags.TOTAL);

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
    int charactersWithWSCount = 0;
    int wordCountInReadOnly = 0;
    int charCountInReadOnly = 0;
    int charactersWithWSCountInReadOnly = 0;
    int wordCountInFilteredCondProfiling = 0;
    int charCountInFilteredCondProfiling = 0;
    int charactersWithWSCountInFilteredCondProfiling = 0;
    
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
          if (startsWithCharacter 
              && !firstIteration
              && !isWordStart(authorAccess, textContext.getTextStartOffset())) {
            currentTextWordCount--;
          }
          if (currentEditableState == TextContext.EDITABLE) {
            charCount += currentTextCharCount;
            wordCount += currentTextWordCount;
            charactersWithWSCount += textContentLength;
          } else if (currentEditableState == TextContext.EDITABLE_IN_FILTERED_CONDITIONAL_PROFILING) {
            // Filtered conditional profiling
            charCountInFilteredCondProfiling += currentTextCharCount;
            wordCountInFilteredCondProfiling += currentTextWordCount;
            charactersWithWSCountInFilteredCondProfiling += textContentLength;
          } else {
            // Read-only reference
            charCountInReadOnly += currentTextCharCount;
            wordCountInReadOnly += currentTextWordCount;
            charactersWithWSCountInReadOnly += textContentLength;
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
      message.append(WORDS + ": ");
      message.append(wordCount);
      message.append("\n");
      message.append(CHARACTERS + ": ");
      message.append(charactersWithWSCount);
      message.append("\n");
      message.append(CHARACTERS_NO_SPACES + ": ");
      message.append(charCount);
    } else {
      String tabString = "     ";
      // Display regular content
      message.append(REGULAR_CONTENT + ":");
      message.append("\n");
      message.append(tabString);
      message.append(WORDS + ": ");
      message.append(wordCount);
      message.append("\n");
      message.append(tabString);
      message.append(CHARACTERS + ": ");
      message.append(charactersWithWSCount);
      message.append("\n");
      message.append(tabString);
      message.append(CHARACTERS_NO_SPACES + ": ");
      message.append(charCount);
      if (wordCountInFilteredCondProfiling > 0) {
        // We have filtered content
        message.append("\n\n");
        message.append(FILTERED_CONTENT + ":");
        message.append("\n");
        message.append(tabString);
        message.append(WORDS + ": ");
        message.append(wordCountInFilteredCondProfiling);
        message.append("\n");
        message.append(tabString);
        message.append(CHARACTERS + ": ");
        message.append(charactersWithWSCountInFilteredCondProfiling);
        message.append("\n");
        message.append(tabString);
        message.append(CHARACTERS_NO_SPACES + ": ");
        message.append(charCountInFilteredCondProfiling);
      }
      if (wordCountInReadOnly > 0) {
        // We have read-only content
        message.append("\n\n");
        message.append(READONLY_CONTENT + ":");
        message.append("\n");
        message.append(tabString);
        message.append(WORDS + ": ");
        message.append(wordCountInReadOnly);
        message.append("\n");
        message.append(tabString);
        message.append(CHARACTERS + ": ");
        message.append(charactersWithWSCountInReadOnly);
        message.append("\n");
        message.append(tabString);
        message.append(CHARACTERS_NO_SPACES + ": ");
        message.append(charCountInReadOnly);
      }
      // We display overall statistics
      message.append("\n\n");
      message.append(TOTAL + ":");
      message.append("\n");
      message.append(tabString);
      message.append(WORDS + ": ");
      message.append(wordCountInReadOnly + wordCountInFilteredCondProfiling + wordCount);
      message.append("\n");
      message.append(tabString);
      message.append(CHARACTERS + ": ");
      message.append(charactersWithWSCount + charactersWithWSCountInReadOnly + charactersWithWSCountInFilteredCondProfiling);
      message.append("\n");
      message.append(tabString);
      message.append(CHARACTERS_NO_SPACES + ": ");
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
  private static boolean isWordStart(AuthorAccess authorAccess, int contentOffset) {
    AuthorDocumentController documentController = authorAccess.getDocumentController();

    boolean toRet = false;

    try {
      CharSequence contentCharSequence = documentController.getContentCharSequence();
      contentOffset --;
      while (contentOffset > 1) {
        // Read one char
        char ch = contentCharSequence.charAt(contentOffset);
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