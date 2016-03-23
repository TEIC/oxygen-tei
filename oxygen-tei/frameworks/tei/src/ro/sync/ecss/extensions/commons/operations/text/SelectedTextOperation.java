package ro.sync.ecss.extensions.commons.operations.text;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.apache.log4j.Logger;




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;

/**
 * Provides upper case and lower case operations over a selected text.
 * 
 * @author Costi Vetezi
 */

public abstract class SelectedTextOperation implements ro.sync.ecss.extensions.api.AuthorOperation {
  /** 
    * Logger for logging.
    */
  private static final Logger logger = Logger.getLogger(SelectedTextOperation.class.getName());
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  public ArgumentDescriptor[] getArguments() {
    return null;
  }

	/**
	 * Process the selected text and make it lower case or upper case.
	 * 
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
	 */
	public void doOperation(AuthorAccess authorAccess, ArgumentsMap arguments)
	throws IllegalArgumentException, AuthorOperationException {
		
		AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
		// Enters here if a text selection was made
		if (authorEditorAccess.hasSelection()) {
			// Get he author document controller in order to get the selected document fragment
			AuthorDocumentController documentController = authorAccess.getDocumentController();
			int selectionStartOffset = authorEditorAccess.getSelectionStart();
			int selectionEndOffset = authorEditorAccess.getSelectionEnd();
			try {
			  Position selEndPosition = documentController.createPositionInContent(selectionEndOffset);
			  processContentRange(documentController, selectionStartOffset, selectionEndOffset - 1);
				// Restore selection
				authorEditorAccess.select(selectionStartOffset, selEndPosition.getOffset()); 
			} catch (BadLocationException e) {
			  logger.error(e, e);
			}
		}
	}

	/**
	 * Process the entire range between start selection and end selection.
	 * 
	 * @param controller Document controller.
	 * @param selStart   The start of the selection.
	 * @param selEnd     The end of the selection.
	 *
	 * @throws AuthorOperationException
	 * @throws BadLocationException
	 */
	private void processContentRange(AuthorDocumentController controller, int selStart, int selEnd)
	throws AuthorOperationException, BadLocationException {

	  // Create an iterator which iterates through text nodes
	  TextContentIterator iterator = controller.getTextContentIterator(selStart, selEnd);
	  try {
	    controller.beginCompoundEdit();
	    while (iterator.hasNext()) {
	      // Get the current text from the iterator and convert it to upper case letters
	      TextContext next = iterator.next();
	      if (next.inVisibleContent() && (next.getEditableState() == TextContext.EDITABLE
	          || next.getEditableState() == TextContext.EDITABLE_IN_FILTERED_CONDITIONAL_PROFILING)) {
	        String content = next.getText().toString();
	        content = processText(content);
	        next.replaceText(content);
	      }
	    }
	  } finally {
	    controller.endCompoundEdit();
	  }
	}
	
	/**
	 * Process text.
	 *
	 * @param text The text to be processed.
	 *
	 * @return The text after process.
	 */
	protected abstract String processText(String text);
}