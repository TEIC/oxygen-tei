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
package ro.sync.ecss.extensions.commons.operations;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.component.AuthorSchemaAwareOptions;
import ro.sync.ecss.css.CSS;
import ro.sync.ecss.css.Styles;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandler;
import ro.sync.ecss.extensions.api.AuthorSelectionModel;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.access.EditingSessionContext;
import ro.sync.ecss.extensions.api.content.OffsetInformation;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResultInsertConstants;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil.ConversionElementHelper;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil.SelectedFragmentInfo;
import ro.sync.exml.options.OptionTags;
import ro.sync.exml.options.Options;

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
   * Argument that controls whether the action inserts a new list or converts the element at caret if no selection is made.
   */
  public static final String CONVERT_ELEMENT_AT_CARET_ARGUMENT = "convertElementAtCaret";

  /**
   * A value for the CONVERT_ELEMENT_AT_CARET_ARGUMENT.
   */
  private static final String ARG_VALUE_AUTO = "auto";
  
  /**
   * Schema aware argument.
   */
  protected static final ArgumentDescriptor CONVERT_ELEMENT_AT_CARET_ARGUMENT_DESCRIPTOR = 
  new ArgumentDescriptor(
      CONVERT_ELEMENT_AT_CARET_ARGUMENT, 
      ArgumentDescriptor.TYPE_CONSTANT_LIST,
      "Controls whether a new list is inserted or the element at caret is converted to list, if there is no selection in the document.\n " +
      "Can be: " 
      + AuthorConstants.ARG_VALUE_TRUE + ", " +
      AuthorConstants.ARG_VALUE_FALSE + " or " + ARG_VALUE_AUTO + ".\n"
          + "Default value is " + ARG_VALUE_AUTO + " meaning that the element at caret is converted only if the option to"
              + "show content completion when pressing Enter is disabled.",
      new String[] {
          AuthorConstants.ARG_VALUE_TRUE,
          AuthorConstants.ARG_VALUE_FALSE,
          ARG_VALUE_AUTO,
      }, 
      ARG_VALUE_AUTO);
  
  /**
   * Argument that controls the type of the list that will be inserted.
   */
  public static final String LIST_TYPE_ARGUMENT = "listType";

  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(InsertListOperation.class.getName());

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
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    this.authorAccess = authorAccess;
    Object schemaAwareArgumentValue = args.getArgumentValue(SCHEMA_AWARE_ARGUMENT);
    boolean schemaAware = !AuthorConstants.ARG_VALUE_FALSE.equals(schemaAwareArgumentValue);
    listType = (String)args.getArgumentValue(LIST_TYPE_ARGUMENT);
    String convertElementAtCaretArgValue = (String) args.getArgumentValue(CONVERT_ELEMENT_AT_CARET_ARGUMENT);
    
    List<ContentInterval> intervals = getIntervalsToConvert(convertElementAtCaretArgValue);
    List<SelectedFragmentInfo> fragmentsToConvert = getFragmentsToConvert(authorAccess, intervals);
    if (fragmentsToConvert != null) {
      Optional<AuthorElement> selectedList = getSelectedList(intervals);
      Optional<Map<String, String>> listAttributes = selectedList
          .map(list -> CommonsOperationsUtil.getAttributes(list, true));
      List<Position> positions = CommonsOperationsUtil.removeIntervals(authorAccess, intervals);
      
      // Check if a list element remains empty and delete it
      Optional<Map<String, String>> attributesOfRemovedList = removeEmptyListElements(authorAccess, positions);
      if (!listAttributes.isPresent()) {
        listAttributes = attributesOfRemovedList;
      }
      
      // Delete empty fragments.
      deleteEmptyFragments(fragmentsToConvert);

      StringBuilder xmlFragment = getListXMLFragment(
          (String)args.getArgumentValue(LIST_TYPE_ARGUMENT),
          listAttributes.orElse(Collections.emptyMap()),
          fragmentsToConvert.size(), authorAccess);

      // Insert an list with empty list items
      SchemaAwareHandlerResult result = authorAccess.getDocumentController().insertXMLFragmentSchemaAware(xmlFragment.toString(), 
          authorAccess.getEditorAccess().getCaretOffset(), AuthorSchemaAwareEditingHandler.ACTION_ID_INSERT_FRAGMENT, true);
      Integer offset = (Integer)result.getResult(SchemaAwareHandlerResult.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET);
      
      insertFragmentsInListAtOffset(authorAccess, fragmentsToConvert, offset);
      CommonsOperationsUtil.removeEmptyElements(authorAccess, positions);
    } else {
      insertAtCaret(authorAccess, args, schemaAware);
    }
  }

  /**
   * Remove empty list elements and return the attributes of the first deleted list.
   * 
   * @param authorAccess The author access.
   * @param positions The positions where to look for empty lists.
   * 
   * @return The attributes of the first deleted list.
   */
  private Optional<Map<String, String>> removeEmptyListElements(AuthorAccess authorAccess, List<Position> positions) {
    Optional<Map<String, String>> listAttributes = Optional.empty();
    
    AuthorDocumentController controller = authorAccess.getDocumentController();
    AuthorElement rootElement = controller.getAuthorDocumentNode().getRootElement();
    try {
      for (int i = 0; i < positions.size(); i++) {
        AuthorNode node = controller.getNodeAtOffset(positions.get(i).getOffset() + 1);
        
        while (node != null && node != rootElement) {
          AuthorNode parentNode = node.getParent(); 
          if (isEmptyListElement(node)) {
            if (!listAttributes.isPresent() && isList(node)) {
              Map<String, String> attributes = CommonsOperationsUtil.getAttributes(node, true);
              listAttributes = Optional.of(attributes != null ? attributes : Collections.emptyMap());
            }
            // Empty element list, remove it
            controller.deleteNode(node);
            positions.remove(controller.createPositionInContent(node.getStartOffset()));
          }
          node = parentNode;
        }
        
      }
    } catch (BadLocationException e1) {
      // Do nothing, no element to delete
      logger.debug(e1.getMessage(), e1);
    }
    
    return listAttributes;
  }

  /**
   * Return the first selected list in the given intervals.
   * 
   * @param intervals The intervals.
   * 
   * @return The first selected list.
   */
  private Optional<AuthorElement> getSelectedList(List<ContentInterval> intervals) {
    return intervals.stream()
      .map(this::getSelectedList)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst();
  }

  /**
   * Get the selected list inside the interval.
   * 
   * @param interval The interval.
   * 
   * @return The selected list, or empty.
   */
  private Optional<AuthorElement> getSelectedList(ContentInterval interval) {
    try {
      return authorAccess.getDocumentController().getNodesToSelect(interval.getStartOffset(), interval.getEndOffset())
        .stream()
        .filter(this::isList)
        .map(AuthorElement.class::cast)
        .findFirst();
    } catch (BadLocationException e) { //NOSONAR java:S1166 It's returned an empty optional
      return Optional.empty();
    }
  }
  
  /**
   * Insert the given fragments in the list at the specified offset.
   * 
   * @param authorAccess The author access.
   * @param fragmentsToConvert The fragments to convert.
   * @param offset The offset where the list is located.
   */
  private void insertFragmentsInListAtOffset(AuthorAccess authorAccess, 
      List<SelectedFragmentInfo> fragmentsToConvert, int offset) {
    try {
      // Now insert the selected fragments in list items. 
      AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(offset + 1);

      insertContent(authorAccess, nodeAtOffset, fragmentsToConvert);
    } catch (BadLocationException e) {
      logger.error(e, e);
    }
  }

  /**
   * Delete empty fragments and make sure we leave at least one.
   * @param fragmentsToConvert The fragments to convert.
   */
  private static void deleteEmptyFragments(List<SelectedFragmentInfo> fragmentsToConvert) {
    int remaining = fragmentsToConvert.size();
    Iterator<SelectedFragmentInfo> iter = fragmentsToConvert.iterator();
    while (iter.hasNext()) {
       SelectedFragmentInfo fragmentInfo = iter.next();
       if (fragmentInfo.getSelectedFragment().isEmpty() && remaining > 1) {
         iter.remove();
         remaining --;
       }
    }
  }

  /**
   * Check if we should convert the element at caret.
   * 
   * @param args The arguments.
   * @param isAtStart <code>true</code> if the caret is at the start of the element to be converted.
   * 
   * @return <code>true</code> if we should attempt to convert the element at caret.
   */
  private boolean shoudConvertElementAtCaret(String convertElement, boolean isAtStart) {
    boolean shouldConvert;
    if (AuthorConstants.ARG_VALUE_TRUE.equals(convertElement)) {
      shouldConvert = true;
    } else if (ARG_VALUE_AUTO.equals(convertElement)) {
      if (isAtStart) {
        shouldConvert = true;
      } else {
        AuthorSchemaAwareOptions schemaAwareOptions = (AuthorSchemaAwareOptions) Options.getInstance().getObjectProperty(OptionTags.AUTHOR_EDITING_MODE);
        EditingSessionContext editingContext = authorAccess.getEditorAccess().getEditingContext();
        shouldConvert = !schemaAwareOptions.isShowAvailableCCItemsOnEnter() 
            // In Web Author, there is an editing context attribute set when content completion on enter is disabled.
            || "false".equals(editingContext.getAttribute("ccOnEnter"));
      }
    } else {
      shouldConvert = false;
    }
    return shouldConvert;
  }

  /**
   * Return the fragments to convert.
   * 
   * @param authorAccess The author access.
   * @param intervals The intervals to convert.
   * 
   * @return The fragments to convert.
   * 
   * @throws AuthorOperationException
   */
  private List<SelectedFragmentInfo> getFragmentsToConvert(AuthorAccess authorAccess,
      List<ContentInterval> intervals) throws AuthorOperationException {
    List<SelectedFragmentInfo> fragmentsToConvert = null; 
    if (!intervals.isEmpty()) {
      try {
        ConversionElementHelper elementsChecker = getConversionElementsChecker();
        fragmentsToConvert = CommonsOperationsUtil.getFragmentsForConversions(authorAccess, elementsChecker, intervals);
      } catch (BadLocationException e) {
        logger.debug(e, e);
      } catch (AuthorOperationException e) {
        if (authorAccess.getEditorAccess().hasSelection()) {
          // The user really selected something to be converted - throw the exception. 
          // Otherwise, fallback to insert.
          throw e;
        }
      }
    }
    return fragmentsToConvert;
  }
  
  /**
   * Returns the invervals to convert to list.
   * 
   * @param convertElementAtCaretArgValue "true", "false" or "auto" controlling if the element at caret should be converted when 
   * no selection is found in the document.
   * 
   * @return The intervals to convert. 
   */
  private List<ContentInterval> getIntervalsToConvert(String convertElementAtCaretArgValue) {
    ConversionElementHelper elementsChecker = getConversionElementsChecker();
    
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    AuthorSelectionModel authorSelectionModel = editorAccess.getAuthorSelectionModel();
    List<ContentInterval> intervals = Collections.emptyList();
    if (authorSelectionModel.hasSelection()) {
      intervals = authorSelectionModel.getSelectionIntervals().stream().map(interval -> {
        int[] balanced = editorAccess.getBalancedSelection(interval.getStartOffset(), interval.getEndOffset());
        return new ContentInterval(balanced[0], balanced[1]);
      }).collect(toList());
    } else {
      Optional<AuthorNode> element = getElementAtCaretToConvert(authorAccess, elementsChecker);
      if (element.isPresent()) {
        boolean isAtStart = containsOnlyStartSentinels(authorAccess, 
            element.get().getStartOffset(), editorAccess.getCaretOffset());
        if (shoudConvertElementAtCaret(convertElementAtCaretArgValue, isAtStart)) {
          intervals = Collections.singletonList(new ContentInterval(element.get().getStartOffset(), element.get().getEndOffset() + 1));
        } 
      }
    }
    return intervals;
  }
  
  /**
   * Checks whether the document contains only sentinels between the given offsets.
   * 
   * @param authorAccess The author access.
   * @param startOffset The start offset.
   * @param endOffset The end offset (exclusive).
   * @return <code>true</code> if the document contains only setinels between offsets.
   */
  private static boolean containsOnlyStartSentinels(AuthorAccess authorAccess, int startOffset, int endOffset) {
    boolean onlySentinels = true;
    AuthorDocumentController controller = authorAccess.getDocumentController();
    try {
      for (int offset = startOffset; offset < endOffset; offset++) {
        OffsetInformation offsetInfo = controller.getContentInformationAtOffset(offset);
        if (offsetInfo.getPositionType() != OffsetInformation.ON_START_MARKER) {
          onlySentinels = false;
          break;
        }
      }
    } catch (BadLocationException e) {
      logger.debug(e, e);
      onlySentinels = false;
    }
    return onlySentinels;
  }

  /**
   * Returns the element at caret that is suitable to be converted.
   * 
   * @param authorAccess The author access.
   * @param helper Used to check if the elements from selection can be converted 
   * in other elements (table cells or list entries)
   * 
   * @return The element to convert.
   */
  public Optional<AuthorNode> getElementAtCaretToConvert(AuthorAccess authorAccess, 
      ConversionElementHelper helper) {
    Optional<AuthorNode> elementToConvert = Optional.empty();
    int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
    AuthorDocumentController controller = authorAccess.getDocumentController();
    try {
      AuthorNode candidate = controller.getNodeAtOffset(caretOffset);
      while (candidate != null) {
        Styles styles = authorAccess.getEditorAccess().getStyles(candidate);
        String display = styles.getDisplay();
        // Check if this is an block element (or a list item)
        if (CSS.BLOCK.equals(display) || CSS.LIST_ITEM.equals(display)) {
          boolean canBeConverted = canBeConverted(authorAccess, helper, candidate);
          if (canBeConverted) {
            if (CSS.LIST_ITEM.equals(display)) {
              // Convert the entire list.
              candidate = candidate.getParent();
            }
            elementToConvert = Optional.of(candidate);
            break;
          }
        }
        candidate = candidate.getParent();
      }
    } catch (BadLocationException e) {
      logger.debug(e, e);
    }
    return elementToConvert;
  }

  /**
   * Check whether an element can be converted to list or table.
   * @param authorAccess The author access.
   * @param helper Used to check if the elements from selection can be converted 
   * in other elements (table cells or list entries)
   * @param candidate The element to check.
   * @return <code>true</code> if the element can be converted.
   */
  private static boolean canBeConverted(AuthorAccess authorAccess, ConversionElementHelper helper,
      AuthorNode candidate) {
    boolean canBeConverted = false;
    try {
      helper.blockContentMustBeConverted(candidate, authorAccess); 
      canBeConverted = true;
    } catch (AuthorOperationException e) {
      // Ignore the exception - try again with its parent.
    }
    return canBeConverted;
  }

  /**
   * Checks if the given node is an empty list element or list item.
   * 
   * @param node The node to check.
   * 
   * @return <code>true</code> if the given node is an empty list element, <code>false</code> otherwise. 
   */
  private boolean isEmptyListElement(AuthorNode node) {
    return isListElement(node) && node.getStartOffset() == node.getEndOffset() - 1;
  }

  /**
   * Checks if the given node is a list element or list item.
   * 
   * @param node The element to check.
   * @return <code>true</code> if the node is a list element.
   */
  protected boolean isListElement(AuthorNode node) {
    return false;
  }

  /**
   * Checks if the given node is a list.
   * 
   * @param node The element to check.
   * @return <code>true</code> if the node is a list.
   */
  protected abstract boolean isList(AuthorNode node);
  
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
   * @param listAttributes The attributes to add to list items.
   * @param numberOfListItems The number of list items.
   * @param authorAccess The author access.
   * @return The list XML fragment.
   */
  protected abstract StringBuilder getListXMLFragment(String listType, Map<String, String> listAttributes, int numberOfListItems, 
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