/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2016 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.operations;

import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandler;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResultInsertConstants;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil.ConversionElementHelper;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil.SelectedFragmentInfo;

/**
 * Operation used to convert a selection to an ordered/unordered list.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(true)
public abstract class InsertListOperation implements AuthorOperation {
  
  /**
   * Schema aware argument.
   */
  protected static final ArgumentDescriptor SCHEMA_AWARE_ARGUMENT_DESCRIPTOR = 
  new ArgumentDescriptor(
      SCHEMA_AWARE_ARGUMENT, 
      ArgumentDescriptor.TYPE_CONSTANT_LIST,
      "Controls if the insertion is schema aware or not. " +
      "When the schema aware is enabled and the fragments insertion is not allowed, a dialog will be shown, proposing solutions, like:\n" +
      " - insert the fragments inside a new element. The name of the element to wrap the fragments in is computed by analyzing the left or right siblings;\n" + 
      " - split an ancestor of the node at insertion offset and insert the fragments between the resulted elements;\n" +
      " - insert the fragments somewhere in the proximity of the insertion offset (left or right without skipping content);\n" + 
      "Note: if a selection exists, the surround with fragment operation is not schema aware.\n" + 
      "Can be: " 
      + AuthorConstants.ARG_VALUE_TRUE + ", " +
      AuthorConstants.ARG_VALUE_FALSE + ". Default value is " + AuthorConstants.ARG_VALUE_TRUE + ".",
      new String[] {
          AuthorConstants.ARG_VALUE_TRUE,
          AuthorConstants.ARG_VALUE_FALSE,
      }, 
      AuthorConstants.ARG_VALUE_TRUE);
  
  /**
   * Argument that controls the type of the list that will be inserted.
   */
  public static final String LIST_TYPE_ARGUMENT = "listType";

  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(InsertListOperation.class.getName());

  /**
   * The author access.
   */
  protected AuthorAccess authorAccess;

  /**
   * The new list type.
   */
  protected String listType;

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    this.authorAccess = authorAccess;
    Object schemaAwareArgumentValue = args.getArgumentValue(SCHEMA_AWARE_ARGUMENT);
    boolean schemaAware = AuthorConstants.ARG_VALUE_FALSE.equals(schemaAwareArgumentValue) ? false : true;
    listType = (String)args.getArgumentValue(LIST_TYPE_ARGUMENT);
    boolean hasSelection = authorAccess.getEditorAccess().getAuthorSelectionModel().hasSelection();
    if (hasSelection) {
      List<SelectedFragmentInfo> selectedFragmentsInfos = 
          CommonsOperationsUtil.getSelectedFragmentsForConversions(authorAccess, getConversionElementsChecker());
      if (selectedFragmentsInfos != null) {
        List<Position> positions = CommonsOperationsUtil.removeCurrentSelection(authorAccess);
        
        // Check if a list element remains empty and delete it
        try {
          for (int i = 0; i < positions.size(); i++) {
            AuthorNode node = 
                authorAccess.getDocumentController().getNodeAtOffset(positions.get(i).getOffset() + 1);
            while (node != null && 
                node != authorAccess.getDocumentController().getAuthorDocumentNode().getRootElement()) {
              AuthorNode parentNode = node.getParent(); 
              if (isEmptyListElement(node)) {
                // Empty element list, remove it
                authorAccess.getDocumentController().deleteNode(node);
                positions.remove(authorAccess.getDocumentController().createPositionInContent(node.getStartOffset()));
                // Delete only the first list (maybe the user wants to keep the other lists)
              }
              node = parentNode;
            }
          }
        } catch (BadLocationException e1) {
          // Do nothing, no element to delete
        }
        
        // Delete empty fragments
        for (int i = 0; i < selectedFragmentsInfos.size(); i++) {
          if (selectedFragmentsInfos.get(i).getSelectedFragment().isEmpty()) {
            selectedFragmentsInfos.remove(selectedFragmentsInfos.get(i));
          }
        }
        StringBuilder xmlFragment = getListXMLFragment((String)args.getArgumentValue(LIST_TYPE_ARGUMENT),
            selectedFragmentsInfos.size(), authorAccess);

        // Insert an list with empty list items
        SchemaAwareHandlerResult result = authorAccess.getDocumentController().insertXMLFragmentSchemaAware(xmlFragment.toString(), 
            authorAccess.getEditorAccess().getCaretOffset(), AuthorSchemaAwareEditingHandler.ACTION_ID_INSERT_FRAGMENT, true);
        Integer offset = (Integer)result.getResult(SchemaAwareHandlerResult.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET);
        try {
          // Now insert the selected fragments in list items. 
          AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(offset + 1);

          insertContent(authorAccess, nodeAtOffset, selectedFragmentsInfos);
        } catch (BadLocationException e) {
          logger.error(e, e);
        }
        CommonsOperationsUtil.removeEmptyElements(authorAccess, positions);
      } else {
        insertAtCaret(authorAccess, args, schemaAware);
      }

    } else {
      insertAtCaret(authorAccess, args, schemaAware);
    }
  }

  /**
   * Checks if the given node is an empty list element.
   * 
   * @param node The node to check.
   * 
   * @return <code>true</code> if the given node is an empty list element, <code>false</code> otherwise. 
   */
  protected abstract boolean isEmptyListElement(AuthorNode node);

  /**
   * Inserts a new list at the caret offset.
   * 
   * @param authorAccess The author access.
   * @param args Arguments of the operation.
   * @param schemaAware <code>true</code> if the insertion should be schema aware.
   * 
   * @throws AuthorOperationException When the insert cannot be done.
   */
  private void insertAtCaret(AuthorAccess authorAccess, ArgumentsMap args, boolean schemaAware)
      throws AuthorOperationException {
    // No selection
    String listType = (String)args.getArgumentValue(LIST_TYPE_ARGUMENT);
    
    String parentListType = null;
    try {
      AuthorNode nodeAtOffset = 
          authorAccess.getDocumentController().getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset());
      parentListType = getParentListType(nodeAtOffset);
    } catch (BadLocationException e) {
      logger.error(e, e);
    }
    String xmlFragment = getXMLFragment(authorAccess, listType, parentListType);


    if (parentListType != null) {
      // Caret inside list -> insert a new list item with empty list inside

      //The XML may contain an editor template for caret positioning.
      int insertionOffset = authorAccess.getEditorAccess().getCaretOffset();

      if (!schemaAware) {
        // Insert fragment at specfied position.
        authorAccess.getDocumentController().insertXMLFragment(
            xmlFragment, (String)null, AuthorConstants.POSITION_INSIDE_FIRST);                
      } else {
        // Insert fragment schema aware.
        SchemaAwareHandlerResult result =
            authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
                xmlFragment, (String) null, AuthorConstants.POSITION_INSIDE_FIRST);
        //Keep the insertion offset.
        if (result != null) {
          Integer off = (Integer) result.getResult(
              SchemaAwareHandlerResultInsertConstants.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET);
          if (off != null) {
            insertionOffset = off.intValue(); 
          }
        }
      }

      // Position inside the first editable position if requested but do not 
      // exceed the already set caret position.
      try {
        int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
        authorAccess.getEditorAccess().goToNextEditablePosition(insertionOffset, caretOffset);
      } catch (BadLocationException e) {
        logger.error(e, e);
      }
    } else {
      // Insert an empty list
      CommonsOperationsUtil.surroundWithFragment(authorAccess, schemaAware, 
          xmlFragment);
    }
  }

  /**
   * Get the type of the list in which the new list will be inserted. Can be <code>null</code>.
   * 
   * @param node The node at offset.
   * @return the type of the list in which the new list will be inserted. Can be <code>null</code>.
   */
  protected abstract String getParentListType(AuthorNode node);

  /**
   * Get the conversion element checker.
   * 
   * @return The conversion element checker.
   */
  protected abstract ConversionElementHelper getConversionElementsChecker();

  /**
   * Insert content.
   * 
   * @param authorAccess The author access.
   * @param listNode The list node.
   * @param selectedFragmentsInfos The fragments to be inserted.
   */
  protected abstract void insertContent(AuthorAccess authorAccess, AuthorNode listNode, List<SelectedFragmentInfo> selectedFragmentsInfos);

  /**
   * Get namespace.
   * 
   * @return The namespace to be used at insertion.
   */
  protected abstract String getNamespace();

  /**
   * Get XML fragment to be inserted when nothing is selected.
   * 
   * @param authorAccess The author access.
   * @param listType The type of the list to be inserted.
   * @param parentListType The type of the parent list, can be <code>null</code>
   * @return the fragment to be inserted.
   */
  protected abstract String getXMLFragment(AuthorAccess authorAccess, String listType, String parentListType);
  
  /**
   * Get list XML fragment.
   * 
   * @param listType The list type.
   * @param numberOfListItems The number of list items.
   * @param authorAccess The author access.
   * @return The list XML fragment.
   */
  protected abstract StringBuilder getListXMLFragment(String listType, int numberOfListItems, 
      AuthorAccess authorAccess);
  
  /**
   * Obtain the name of every list type.
   * 
   * @param listType The list type.
   * 
   * @return A string representing the name of the given list type.
   */
  protected abstract String getListTypeDescription(String listType);
}