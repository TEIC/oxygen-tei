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
import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.css.Styles;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * The class provides an operation for forming sentences over a selection.
 * If the start character of a sentence is lower case, it will be changed to upper case.
 * 
 * @author Costi Vetezi
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class CapitalizeSentencesOperation extends FormSelectedTextOperation {
  /**
   * An array with sentence delimiters.
   */
  private static final char[] SENTENCE_DELIMITER_CHARS = new char[] {'.', '?', '!'};

  /**
   * @see ro.sync.ecss.extensions.commons.operations.text.FormSelectedTextOperation#isDelimiterBeforeTextNode(ro.sync.ecss.extensions.api.AuthorAccess, int)
   */
  @Override
  protected boolean isDelimiterBeforeTextNode(AuthorAccess authorAccess, int contentOffset)
  throws BadLocationException, AuthorOperationException {
    
    AuthorDocumentController documentController = authorAccess.getDocumentController();
    boolean toRet = false;
    
    CharSequence contentCharSequence = documentController.getContentCharSequence();
    contentOffset --;
    while (contentOffset >= 1) {
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
      } else if(isSentenceDelimiter(ch)) {
        // Delimiter found.
        toRet = true;
        break;
      } else {
        // Any other character (non-delimiter).
        // If the character is a word delimiter ignore it and continue
        if (!isWordDelimiter(ch)) {
          break;
        }
      }
      contentOffset --;
    }

    return toRet;
  }

  /**
   * Process char array and upper case first letter of containing sentences.
   */
  @Override
  protected char[] processTextContent(char[] charArray, boolean isDelimiterBefore) {
    // Iterate through characters in current text node
    for (int i = 0; i < charArray.length; i++) {
      char currentChar = charArray[i];      
      if (isSentenceDelimiter(currentChar)) {
        // Current character is a sentence delimiter
        isDelimiterBefore = true;
      } else {
        if (isDelimiterBefore && Character.isLetterOrDigit(currentChar)) {
          charArray[i] = Character.toUpperCase(currentChar);
          isDelimiterBefore = false;
        }
      }
    }
    
    return charArray;
  }
  
  /**
   * Decides if the character is a sentence delimiter or not.
   * 
   * @param ch The character that must be evaluated.
   *
   * @return <code>true</code> if the character is a sentence delimiter or <tt>false</tt> otherwise.
   */
  private static boolean isSentenceDelimiter(char ch) {
    boolean isDelimiter = false;
    for (int i = 0; i < SENTENCE_DELIMITER_CHARS.length; i++) { 
      if (SENTENCE_DELIMITER_CHARS[i] == ch) {
        isDelimiter = true;
        break;
      }
    }
    return isDelimiter;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Capitalize the first letter of each sentence in the current selection.";
  }
}