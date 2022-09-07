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

import java.util.ArrayList;
import java.util.List;

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
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.SelectionInterpretationMode;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;

/**
 * Provides upper case and lower case operations over a selected text.
 * 
 * @author Costi Vetezi
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class SelectedTextOperation implements ro.sync.ecss.extensions.api.AuthorOperation {
  /** 
    * Logger for logging.
    */
  private static final Logger logger = LoggerFactory.getLogger(SelectedTextOperation.class.getName());
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return null;
  }

	/**
	 * Process the selected text and make it lower case or upper case.
	 * 
	 * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
	 */
	@Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap arguments) throws AuthorOperationException {
	  AuthorDocumentController documentController = authorAccess.getDocumentController();
		AuthorEditorAccess authorEditorAccess = authorAccess.getEditorAccess();
		documentController.beginCompoundEdit();
		try{
		  // Enters here if a text selection was made
		  if (authorEditorAccess.hasSelection()) {
		    // Get the author document controller in order to get the selected document fragment
		    List<ContentInterval> selectionIntervals = authorEditorAccess.getAuthorSelectionModel().getSelectionIntervals();
		    List<ContentInterval> updatedSelectionIntervals = new ArrayList<ContentInterval>(selectionIntervals.size());

		    //Mark selection before.
		    List<int[]> selectionIntervalsToMark = convertSelectionIntervals(selectionIntervals);
		    int caretOffset = authorEditorAccess.getCaretOffset();
        SelectionInterpretationMode selectionInterpretationMode = authorEditorAccess.getAuthorSelectionModel().getSelectionInterpretationMode();
        documentController.markSelection(
		        selectionIntervalsToMark, 
		        caretOffset, 
		        selectionInterpretationMode, 
		        selectionIntervalsToMark, 
		        caretOffset, 
		        selectionInterpretationMode);

		    int diff = 0;
		    for (int i = 0; i < selectionIntervals.size(); i++) {
		      ContentInterval contentInterval = selectionIntervals.get(i);
		      try {
		        int startOffset = contentInterval.getStartOffset() + diff;
		        int endOffset = contentInterval.getEndOffset() + diff;
		        Position endPosition = documentController.createPositionInContent(endOffset);
		        processContentRange(documentController, startOffset, endOffset  - 1);
		        ContentInterval e = new ContentInterval(startOffset, endPosition.getOffset());
		        updatedSelectionIntervals.add(e);
		        diff += endPosition.getOffset() - endOffset;
		      } catch (BadLocationException e) {
		        logger.error(e, e);
		      }
		    }

		    // Mark selection based on updatedSelectionIntervals
		    documentController.markSelection(
		        convertSelectionIntervals(updatedSelectionIntervals), 
		        authorEditorAccess.getCaretOffset(),
		        authorEditorAccess.getAuthorSelectionModel().getSelectionInterpretationMode(), 
		        null, 
		        -1,
		        null);
		  } 
		} finally {
		  documentController.endCompoundEdit();
    }
	}

	/**
   * Converts a list of ContentIntervals to a list of int arrays representing the selection.
   * 
   * @param intervals The list of ContentIntervals. 
   * 
   * @return The list of int arrays.
   */
  private static List<int[]> convertSelectionIntervals(List<ContentInterval> intervals) {
    List<int[]> toReturn = new ArrayList<int[]>(intervals.size());
    for (ContentInterval contentInterval : intervals) {
      toReturn.add(new int[] {contentInterval.getStartOffset(), contentInterval.getEndOffset()});
    }
    return toReturn;
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
	private void processContentRange(AuthorDocumentController controller, int selStart, int selEnd) {

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