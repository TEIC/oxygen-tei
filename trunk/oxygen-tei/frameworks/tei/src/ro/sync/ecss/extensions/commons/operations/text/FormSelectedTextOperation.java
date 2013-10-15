package ro.sync.ecss.extensions.commons.operations.text;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.apache.log4j.Logger;

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
  private static final Logger logger = Logger.getLogger(CapitalizeSentencesOperation.class.getName());
  
	/**
	 * An array with word delimiters
	 */
	private static final char[] WORD_DELIMITERS_CHARS = new char[] { '.', ' ', ';', ':', '"', '\'', ',' };

	/**
	 * The author access
	 */
  private AuthorAccess authorAccess;

  /**
   * The selection start offset
   */
  private int selectionStartOffset;

	/**
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
	 */
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
	public void doOperation(AuthorAccess authorAccess, ArgumentsMap arguments) throws AuthorOperationException {
		this.authorAccess = authorAccess;
    AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
		// Enters here if a text selection was made
		if (authorEditorAccess.hasSelection()) {
			// Get the author document controller in order to get the selected
			// document fragment
			AuthorDocumentController documentController = authorAccess.getDocumentController();
			selectionStartOffset = authorEditorAccess.getSelectionStart();
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
	      if (next.getEditableState() == TextContext.EDITABLE
	          || next.getEditableState() == TextContext.EDITABLE_IN_FILTERED_CONDITIONAL_PROFILING) {
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