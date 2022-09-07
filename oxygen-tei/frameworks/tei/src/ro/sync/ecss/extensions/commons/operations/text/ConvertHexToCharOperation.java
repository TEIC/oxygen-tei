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


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.basic.util.NumberFormatException;
import ro.sync.basic.util.NumberParserUtil;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;

/**
 * Operation for converting a hexadecimal sequence of digits from the left of the caret 
 * to the equivalent Unicode character.
 * Note that the longest valid hexadecimal sequence will be converted and the length of that sequence 
 * is less or equal than 4, excluding the hexadecimal prefix such as <b>'0x'</b> or <b>'0X'</b> 
 * which could precede that sequence or not.
 * It also works on selected text consisting of valid hexadecimal characters (max 6, including prefix).
 * 
 * @author teodor_timplaru
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public abstract class ConvertHexToCharOperation implements AuthorOperation {
  /**
   * Logger for logging. 
   */
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ConvertHexToCharOperation.class.getName());
  
  /**
   * Max number of hexadecimal digits to convert to a single Unicode character.
   */
  private static final int MAX_NUMBER_OF_HEX_DIGITS_TO_CONVERT = 4;
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    // Flag indicating whether the selected sequence of presumed hex characters is valid or not
    boolean validSelection = true;
    // Caret position adjustment in case of a sequence preceded by hexadecimal prefix 
    int startOffsetAdjustment = 0;
    
    // If selection is present, save the selected text and check its validity as a hex sequence
    AuthorDocumentController controller = authorAccess.getDocumentController();
    int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
    int selStart = authorAccess.getEditorAccess().getSelectionStart();
    int selEnd = authorAccess.getEditorAccess().getSelectionEnd();
    int textStartOffset = -1;
    int textEndOffset = -1;
    String hexToConvert = null;

    if (selEnd < selStart) {
      int tmp = selStart;
      selStart = selEnd;
      selEnd = tmp;
    }
    if (selEnd > selStart) {
      TextContentIterator textContentIterator = controller.getTextContentIterator(selStart, selEnd - 1);
      if (textContentIterator.hasNext()) {
        TextContext tc = textContentIterator.next();
        // There should be a single piece of text, of the required length 
        hexToConvert = tc.getText().toString();
        textStartOffset = tc.getTextStartOffset();
        textEndOffset = tc.getTextEndOffset();
      }
      if (hexToConvert != null) {
        // Quickly inspect the selection; it should consist of useful continuous text only
        if ((textStartOffset != -1 && selStart != textStartOffset) 
            || (textEndOffset != -1 && selEnd != textEndOffset)) {
          validSelection = false;
        }
        if (validSelection) {
          // Check if the selected sequence is a valid hexadecimal one
          if (hexToConvert.length() > MAX_NUMBER_OF_HEX_DIGITS_TO_CONVERT + 2) {
            validSelection = false;
          } else {
            for (int i = 0; i < hexToConvert.length(); i++) {
              char ch = Character.toLowerCase(hexToConvert.charAt(i));
              if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch == 'x')) {
                continue;
              } else {
                validSelection = false;
                break;
              }
            }
            if (validSelection) {
              if (hexToConvert.toLowerCase().startsWith("0x")) {
                hexToConvert = hexToConvert.substring(2);
                startOffsetAdjustment = 2;
              }
              if (hexToConvert.toLowerCase().contains("x")) {
                validSelection = false;
              }
            }
          }
        }
      } else {
        validSelection = false;
      }
    } else {
      validSelection = false;
    }
    
    // If the selection is made from right to left, adjust the caret position
    if (hexToConvert != null && validSelection && caretOffset == selStart) {
      caretOffset += selEnd - selStart;
    }
    
    // When no selection, the sequence of hex digits should be no longer than 
    // MAX_NUMBER_OF_HEX_DIGITS_TO_CONVERT + 2; 2 is the length of the prefix if present ('0x' | '0X') 
    // The hex characters to be converted are stored in the below String
    StringBuilder hexDigits = new StringBuilder(MAX_NUMBER_OF_HEX_DIGITS_TO_CONVERT + 2);
    if (hexToConvert == null) {
      int currPos = caretOffset;
      while (currPos > 0 && hexDigits.length() < MAX_NUMBER_OF_HEX_DIGITS_TO_CONVERT + 2) {
        TextContentIterator textContentIterator = controller.getTextContentIterator(currPos - 1, currPos - 1);
        if (textContentIterator.hasNext()) {
          TextContext tc = textContentIterator.next();
          // There should be a single piece of text, of the required length 
          // Otherwise, it means there is some mark-up and no continuous text - nothing to do in this case
          String str = tc.getText().toString();
          char ch = Character.toLowerCase(str.charAt(0));
          // Check if the current char is an hexadecimal one and then store it
          if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch == 'x')) {
            hexDigits.append(ch);
            --currPos;
          } else {
            break;
          }
        } else {
          break;
        }
      }
    }
    
    // Try to complete the operation
    int len = hexToConvert != null && validSelection ? hexToConvert.length() : hexDigits.length();

    if (len > 1) {
      // The 'if' below corresponds to the case of no selection
      if (hexToConvert == null) {
        hexToConvert = hexDigits.reverse().toString();
        // Adjust the start offset if a hexadecimal prefix encountered in order to remove that prefix
        // Take into account that 'x' could be part of the hex sequence only when is preceded by '0'
        int idx = hexToConvert.lastIndexOf("0x");
        if (idx != -1) {
          hexToConvert = hexToConvert.substring(idx + 2);
          startOffsetAdjustment = 2;
        } else {
          int length = hexToConvert.length();
          if (length > MAX_NUMBER_OF_HEX_DIGITS_TO_CONVERT) {
            hexToConvert = hexToConvert.substring(length - MAX_NUMBER_OF_HEX_DIGITS_TO_CONVERT);
          }
        }
        idx = hexToConvert.lastIndexOf('x');
        if (idx != -1) {
          hexToConvert = hexToConvert.substring(idx + 1);
          startOffsetAdjustment = 0;
        }
      }      
      
      // Explicitly selected or not, the final sequence to convert is well determined in this point
      len = hexToConvert.length();
      if (len > 1 && len <= MAX_NUMBER_OF_HEX_DIGITS_TO_CONVERT) {
        try {
          int value = NumberParserUtil.parseInt(hexToConvert, 16);
          // Replace the hex sequence of text with the equivalent character
          int startOffset = caretOffset - len - startOffsetAdjustment;
          controller.delete(startOffset, caretOffset - 1);
          controller.insertText(startOffset, "" + (char) value);
        } catch (NumberFormatException ex) {
          authorAccess.getWorkspaceAccess().showErrorMessage(getErrorMessage());
        }
      } else {
        authorAccess.getWorkspaceAccess().showErrorMessage(getErrorMessage());
      }
    } else {
      authorAccess.getWorkspaceAccess().showErrorMessage(getErrorMessage());
    }
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
    return "Converts a hexadecimal sequence of characters to a single Unicode character";
  }

  /**
   * Gets the error message displayed when operation fails.
   * 
   * @return The error message displayed when operation fails. 
   */
  protected abstract String getErrorMessage();
}