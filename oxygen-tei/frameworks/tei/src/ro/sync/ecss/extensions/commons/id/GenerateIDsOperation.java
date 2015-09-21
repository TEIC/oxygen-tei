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
package ro.sync.ecss.extensions.commons.id;

import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.UniqueAttributesRecognizer;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Operation used to auto generate IDs for the elements included in the selected fragment.
 */

public abstract class GenerateIDsOperation implements AuthorOperation {
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
  throws IllegalArgumentException, AuthorOperationException {
    UniqueAttributesRecognizer attrsAssigner = getUniqueAttributesRecognizer();
    if(attrsAssigner != null) {
      attrsAssigner.activated(authorAccess);
      
      if(authorAccess.getEditorAccess().hasSelection()) {
        List<ContentInterval> selectionIntervals = authorAccess.getEditorAccess().getAuthorSelectionModel().getSelectionIntervals();
        // EXM-29244 Generate IDs on multiple selection
        for (ContentInterval contentInterval : selectionIntervals) {
          int startSel = contentInterval.getStartOffset();
          // Inclusive end
          int endSel = contentInterval.getEndOffset();
          
          try {
            //But maybe a node is completely engulfed in the selection
            AuthorNode nodeAtEndSel = authorAccess.getDocumentController().getNodeAtOffset(endSel - 1);
            if(startSel == nodeAtEndSel.getStartOffset() && nodeAtEndSel.getEndOffset() == endSel - 1) {
              //EXM-19319 This is the case, force ID generation on the totally engulfed node.
              attrsAssigner.assignUniqueIDs(nodeAtEndSel.getStartOffset(), nodeAtEndSel.getStartOffset(), true);
            } else {
              int[] balancedSelection = authorAccess.getEditorAccess().getBalancedSelection(startSel, endSel);
              int start = balancedSelection[0];
              int end = balancedSelection[1];
              Segment seg = new Segment();
              // Get all the selection chars
              authorAccess.getDocumentController().getChars(start, end - start, seg);

              // Iterate the current segment chars and assign ID-s to all the first-level nodes
              for (int i = 0; i < seg.count; i++) {
                // Current char
                char chr = seg.array[seg.offset + i];
                boolean isSentinel = chr == 0;
                if (isSentinel) {
                  // The offset in document corresponding to the current char
                  int offsetInDoc = start + i;
                  AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(offsetInDoc + 1);
                  if (nodeAtOffset.getStartOffset() == offsetInDoc) {
                    // Skip this node
                    i += nodeAtOffset.getEndOffset() - nodeAtOffset.getStartOffset();
                    if (nodeAtOffset.getEndOffset() <= end - 1) {
                      // Assign an ID if this node is fully selected
                      attrsAssigner.assignUniqueIDs(offsetInDoc, offsetInDoc, true);
                    }
                  }
                }
              }
            }
            attrsAssigner.assignUniqueIDs(startSel, endSel - 1, false);
          } catch (BadLocationException e) {
            // Ignore
          }
        }
      } else {
        try {
          // No selection, consider the parent node to generate for.
          AuthorNode nodeAtCaret = authorAccess.getDocumentController().getNodeAtOffset(
              authorAccess.getEditorAccess().getCaretOffset());
          int startSel = nodeAtCaret.getStartOffset();
          int endSel = nodeAtCaret.getStartOffset();
          attrsAssigner.assignUniqueIDs(startSel, endSel, ! authorAccess.getEditorAccess().hasSelection());
        } catch (BadLocationException e) {
          // Ignore
        }
      }
    }
  }

  /**
   * @return The unique attributes handler 
   */
  protected abstract UniqueAttributesRecognizer getUniqueAttributesRecognizer();

  /**
   * No Arguments
   * 
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
    return "Generate unique IDs on the selected content";
  }
}
