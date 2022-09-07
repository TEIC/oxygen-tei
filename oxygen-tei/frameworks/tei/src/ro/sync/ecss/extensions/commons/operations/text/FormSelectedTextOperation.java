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
import javax.swing.text.Position;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;

/**
 * The class provides form word and form sentence operations over a selected text.
 * 
 * @author Costi Vetezi
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class FormSelectedTextOperation implements AuthorOperation {
  /** 
    * Logger for logging.
    */
  private static final Logger logger = LoggerFactory.getLogger(FormSelectedTextOperation.class.getName());
  
	/**
	 * An array with word delimiters
	 */
	private static final char[] WORD_DELIMITERS_CHARS = new char[] { '.', ' ', ';', ':', '"', '\'', ',' };

	/**
	 * The author access
	 */
  private AuthorAccess authorAccess;

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
	 */
	@Override
  public ArgumentDescriptor[] getArguments() {
		return null;
	}

	/**
	 * Decides if there is a sentence delimiter before the text node.
	 * 
	 * @param contentOffset The offset where search is started.
	 * @param authorAccess
	 *
	 * @return <code>true</code> if the there is a sentence delimiter before the text node or
	 *         <code>false</code> if a non-delimiter character was found.
	 *
	 * @throws BadLocationException
	 * @throws AuthorOperationException
	 */
	protected abstract boolean isDelimiterBeforeTextNode(AuthorAccess authorAccess, int contentOffset)
	throws BadLocationException, AuthorOperationException;
	
	/**
   * Decides if the character is a sentence delimiter or not.
   * 
   * @param ch The character that must be evaluated.
   *
   * @return <code>true</code> if the character is a sentence delimiter or <code>false</code> otherwise.
   */
  protected boolean isWordDelimiter(char ch) {
    boolean isDelimiter = false;
    for (int i = 0; i < WORD_DELIMITERS_CHARS.length; i++) { 
      if (WORD_DELIMITERS_CHARS[i] == ch) {
        isDelimiter = true;
        break;
      }
    }
    return isDelimiter;
  }

	/**
	 * Form sentences.
	 * 
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
	 */
	@Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap arguments) throws AuthorOperationException {
		this.authorAccess = authorAccess;
    AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
		// Enters here if a text selection was made
		if (authorEditorAccess.hasSelection()) {
			// Get the author document controller in order to get the selected
			// document fragment
			AuthorDocumentController documentController = authorAccess.getDocumentController();
			int selectionStartOffset = authorEditorAccess.getSelectionStart();
			int selectionEndOffset = authorEditorAccess.getSelectionEnd();
			try {
			  Position selEnd = documentController.createPositionInContent(selectionEndOffset);
			  processContentRange(documentController, selectionStartOffset, selectionEndOffset - 1);
				// Keep selection
				authorEditorAccess.select(selectionStartOffset, selEnd.getOffset());
			} catch (BadLocationException e) {
			  logger.error(e, e);
			}
		}
	}
	
	/**
	 * Process the given range.
	 * 
	 * @param documentController The document controller.
	 * @param selStart           The start of the selection.
	 * @param selEnd             The end of the selection.
	 * 
	 * @throws AuthorOperationException 
	 * @throws BadLocationException 
	 */
	private void processContentRange(AuthorDocumentController documentController, int selStart, int selEnd)
	throws BadLocationException, AuthorOperationException {

	  // Create an iterator which iterates through text nodes
	  TextContentIterator iterator = documentController.getTextContentIterator(selStart, selEnd);
	  try {
	    documentController.beginCompoundEdit();
	    // Iterate through text nodes
	    while (iterator.hasNext()) {
	      TextContext next = iterator.next();
	      if (next.inVisibleContent() && (next.getEditableState() == TextContext.EDITABLE
	          || next.getEditableState() == TextContext.EDITABLE_IN_FILTERED_CONDITIONAL_PROFILING)) {
	        //In editable test, we can replace.
	        CharSequence text = next.getText();
	        char[] charArray = text.toString().toCharArray();

	        boolean isDelimiterBefore = isDelimiterBeforeTextNode(authorAccess, next.getTextStartOffset());
	        charArray = processTextContent(charArray, isDelimiterBefore);

	        // Replace the modified string
	        text = String.copyValueOf(charArray);
	        next.replaceText(text);
	      }
	    }
	  } finally {
	    documentController.endCompoundEdit();
	  }
	}
	
  /**
   * Process char array.
   * 
   * @param charArray         The character array that must be processed.
   * @param isDelimiterBefore <code>true</code> if we have a delimiter before the given char array,
   *                          <code>false</code> otherwise.
   */
  protected abstract char[] processTextContent(char[] charArray, boolean isDelimiterBefore);
}