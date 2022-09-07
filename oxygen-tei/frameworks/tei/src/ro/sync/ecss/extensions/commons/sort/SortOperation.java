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
package ro.sync.ecss.extensions.commons.sort;

import java.awt.Frame;
import java.text.Collator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.text.BadLocationException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.eclipse.swt.widgets.Shell;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.Platform;

/**
 * Sort operations base class.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class SortOperation implements AuthorOperation {
  /**
   * Id of the sorting support page.
   */
  private static final String SORTING_SUPPORT_PAGE_ID = "sorting-support";

  /**
   * Holds an {@link AuthorDocumentFragment} and the associated key values used when sorting the fragments.
   */
  private static class SortableFragment {
    /**
     * The document fragment to be sorted.
     */
    private AuthorDocumentFragment documentFragment;
    /**
     * The key values used to sort the fragments.
     */
    private String[] keyValues;
  }
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(SortOperation.class.getName());
  
  /**
   * String used in the default name for the sorting criterion.
   */
  protected static final String COLUMN = "Column";
  
  /**
   * The Author access.
   */
  protected AuthorAccess authorAccess;
  
  /**
   * Information about the sort keys, sort type and order.
   */
  private SortCriteriaInformation sortInformation = null;
  /**
   * The name of the "selected elements" radio combo.
   */
  private final String selElementsString;
  /**
   * The name of the "all elements" radio combo.
   */
  private final String allElementsString;
  
  /**
   * Constructor.
   * 
   * @param selElementsString The name of the "selected elements" radio combo.
   * @param allElementsString The name of the "all elements" radio combo.
   */
  public SortOperation(String selElementsString, String allElementsString) {
    this.selElementsString = selElementsString;
    this.allElementsString = allElementsString;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Sort operation";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    this.authorAccess = authorAccess;
    try {
      // Find the parent node whose children should be sorted.
      int offset = authorAccess.getEditorAccess().getCaretOffset();
      // If there is a selection use its start.
      List<ContentInterval> selectionIntervals = authorAccess.getEditorAccess().getAuthorSelectionModel().getSelectionIntervals();
      if (selectionIntervals != null && !selectionIntervals.isEmpty()) {
        offset = selectionIntervals.get(0).getStartOffset();
      }
      AuthorElement parent = getSortParent(offset, authorAccess);
      if (parent != null) {
        List<CriterionInformation> sortCriteria = getSortCriteria(parent);
        if (!sortCriteria.isEmpty()) {
          int[] selectedNonIgnoredChildrenInterval = getSelectedNonIgnoredChildrenInterval(parent);
          sortInformation = getSortInformation(authorAccess, parent, sortCriteria, selectedNonIgnoredChildrenInterval);
          
          if (sortInformation != null && sortInformation.criteriaInfo != null && sortInformation.criteriaInfo.length > 0) {
            Locale locale = Locale.getDefault();
            String langVal = SortUtil.detectXMLLangFrom(parent);
            if (langVal != null) {
              locale = createLocale(langVal);
            }
            
            final AuthorOperationException[] error = new AuthorOperationException[1];
            Set<SortableFragment> sortableNodesFragments = new TreeSet<SortableFragment>(createNodesComparator(locale, error));

            int i = 0;
            int indexInSortableNodes = 0;
            // Obtain the sortable and the ignored nodes.
            List<AuthorNode> contentNodes = parent.getContentNodes();
            List<AuthorDocumentFragment> ignoredNodesFragments = new ArrayList<AuthorDocumentFragment>();
            List ignoredNodesIndices = new ArrayList<Integer>();
            for (AuthorNode child : contentNodes) {
              try {
                if (isIgnored(child)
                    // Only the selected nodes are sorted but the current child is outside the selection, than it can be ignored.
                    || (sortInformation.onlySelectedElements 
                        && selectedNonIgnoredChildrenInterval != null 
                        && (selectedNonIgnoredChildrenInterval[0] > indexInSortableNodes
                            || selectedNonIgnoredChildrenInterval[1] < indexInSortableNodes))) {
                  ignoredNodesFragments.add(authorAccess.getDocumentController().createDocumentFragment(child, true));
                  ignoredNodesIndices.add(i);
                } else {
                  SortableFragment sortableFragment = new SortableFragment();
                  sortableFragment.documentFragment = authorAccess.getDocumentController().createDocumentFragment(child, true);
                  sortableFragment.keyValues = getSortKeysValues(child, sortInformation);
                  sortableNodesFragments.add(sortableFragment);
                  if (error[0] != null) {
                    throw error[0];
                  }
                }
              } catch (BadLocationException e) {
                logger.error(e, e);
                throw new AuthorOperationException(e.getMessage(), e);
              }

              if (!isIgnored(child)) {
                indexInSortableNodes ++;
              }

              i ++;
            }

            AuthorDocumentFragment[] newFragments = new AuthorDocumentFragment[sortableNodesFragments.size() + ignoredNodesFragments.size()];
            int[] insertOffsets = new int[newFragments.length];

            // Add first the ignored nodes.
            Iterator<AuthorDocumentFragment> fragmentsIt = ignoredNodesFragments.iterator();
            for (Iterator iterator = ignoredNodesIndices.iterator(); iterator.hasNext() && fragmentsIt.hasNext();) {
              int index = (Integer) iterator.next();
              newFragments[index] = fragmentsIt.next();
            }

            // Add the sorted nodes.
            insertOffsets[0] = parent.getStartOffset() + 1;
            Iterator<SortableFragment> it = sortableNodesFragments.iterator();
            for (int j = 0; j < newFragments.length; j++) {
              if (newFragments[j] == null && it.hasNext()) {
                AuthorDocumentFragment documentFragment = it.next().documentFragment;
                newFragments[j] = documentFragment;
              }

              if (j > 0) {
                insertOffsets[j] = parent.getStartOffset() + 1;
              }
            }

            // EXM-47758 - multiple delete, that will fire only structure change and one layout change.
            // it's also good for undo/redo operations.
            authorAccess.getDocumentController().multipleDelete(
                parent, 
                new int[] {parent.getStartOffset() + 1}, 
                new int[] {parent.getEndOffset()});
            
            authorAccess.getDocumentController().insertMultipleFragments(parent, newFragments, insertOffsets);
            // Restore the caret position
            authorAccess.getEditorAccess().setCaretPosition(offset);
          }
        }
      }
    } catch (AuthorOperationException e) {
      authorAccess.getWorkspaceAccess().showErrorMessage("The sort operation couldn't be performed.\n" + e.getMessage(), e);
    }
  }

  /**
   * Get the sort information.
   * 
   * @param authorAccess  The author access.
   * @param parent        The parent node of all the nodes which will be sorted.
   * @param sortCriteria  The list of sort criteria
   * @param selectedNonIgnoredChildrenInterval The interval of sortable nodes indices covered by selection.
   * 
   * @return The sort information.
   * 
   * @throws AuthorOperationException When given node and intervals are not sortable.
   */
  private SortCriteriaInformation getSortInformation(AuthorAccess authorAccess, AuthorElement parent,
      List<CriterionInformation> sortCriteria, int[] selectedNonIgnoredChildrenInterval)
      throws AuthorOperationException {
    
    canBeSorted(parent, selectedNonIgnoredChildrenInterval);
    SortCriteriaInformation sortInfo = null;
     
    // Check if all elements can be sorted.
    boolean cannotSortAllElements = false;
    try {
      canBeSorted(parent, new int[] {0, getNonIgnoredChildren(parent).size() - 1});
    } catch (AuthorOperationException e) { //NOSONAR java:S1166 We set a flag in this case
      cannotSortAllElements = true;
    }
    
    // Get the sort criterion from the customizer.
    Platform platform = authorAccess.getWorkspaceAccess().getPlatform();
    if (Platform.STANDALONE == platform) {
      SASortCustomizerDialog saSortCustomizerDialog = new SASortCustomizerDialog((Frame) authorAccess.getWorkspaceAccess().getParentFrame(), 
          authorAccess.getAuthorResourceBundle(),
          authorAccess.getAuthorResourceBundle().getMessage(selElementsString), 
          authorAccess.getAuthorResourceBundle().getMessage(allElementsString)){
        @Override
        public String getHelpPageID() {
          return SortOperation.this.getHelpPageID();
        }
      };

      sortInfo = saSortCustomizerDialog.getSortInformation(
          sortCriteria, 
          selectedNonIgnoredChildrenInterval != null, 
          cannotSortAllElements);
    } else if (Platform.ECLIPSE == platform) {
      sortInfo = new ECSortCustomizerDialog((Shell) authorAccess.getWorkspaceAccess().getParentFrame(), 
          authorAccess.getAuthorResourceBundle(), authorAccess.getAuthorResourceBundle().getMessage(selElementsString), 
          authorAccess.getAuthorResourceBundle().getMessage(allElementsString), getHelpPageID()).
          getSortInformation(
              sortCriteria, 
              selectedNonIgnoredChildrenInterval != null, 
              cannotSortAllElements);
    }
    return sortInfo;
  }

  /**
   * Create nodes fragments comparator.
   * 
   * @param locale The locale to be use.
   * @param error The errors array.
   * 
   * @return The nodes fragments comparator.
   */
  private Comparator<SortableFragment> createNodesComparator(final Locale locale, final AuthorOperationException[] error) {
    return new Comparator<SortableFragment>() {
      @Override
      public int compare(SortableFragment s1, SortableFragment s2) {
        for (int j = 0; s1.keyValues != null && s2.keyValues != null && j < s1.keyValues.length; j++) {
          if (s1.keyValues[j] == null && s2.keyValues[j] != null) {
            return CriterionInformation.ORDER.ASCENDING.getName().equals(sortInformation.criteriaInfo[j].getOrder()) ? 1 : -1;
          } else if (s2.keyValues[j] == null) {
            return CriterionInformation.ORDER.ASCENDING.getName().equals(sortInformation.criteriaInfo[j].getOrder()) ? -1 : 1;
          } else if (s1.keyValues[j].trim().equals(s2.keyValues[j].trim())) {
            continue;
          } else {
            int compareTo = 0;
            s1.keyValues[j] = s1.keyValues[j].trim();
            s2.keyValues[j] = s2.keyValues[j].trim();
            if (CriterionInformation.TYPE.NUMERIC.getName().equals(sortInformation.criteriaInfo[j].getType())) {
              Double val1 = SortUtil.parseNumber(error, s1.keyValues[j]);
              
              if (error[0] == null) {
                Double val2 = SortUtil.parseNumber(error, s2.keyValues[j]);
                if (error[0] == null) {
                  compareTo = CriterionInformation.ORDER.ASCENDING.getName().equals(
                      sortInformation.criteriaInfo[j].getOrder()) ? val1.compareTo(val2) : val2.compareTo(val1);
                }
              }
            } else if (CriterionInformation.TYPE.DATE.getName().equals(sortInformation.criteriaInfo[j].getType())) {
              // Try with some standard formats.
              int[] styles = {
                  DateFormat.DEFAULT,
                  DateFormat.SHORT,
                  DateFormat.MEDIUM,
                  DateFormat.LONG,
                  DateFormat.FULL };
              
              Date val1 = null;
              Date val2 = null;
              for (int k = 0; k < styles.length && (val1 == null || val2 == null); k++) {
                int currentStyle = styles[k];
                Locale defaultLocale = Locale.getDefault();
                DateFormat  dateTimeFormatter = DateFormat.getDateTimeInstance(currentStyle, currentStyle, defaultLocale);
                DateFormat  dateFormatter = DateFormat.getDateInstance(currentStyle, defaultLocale);
                DateFormat  timeFormatter = DateFormat.getTimeInstance(currentStyle, defaultLocale);
                
                if (val1 == null) {
                  val1 = SortUtil.parseDate(error, s1.keyValues[j], dateTimeFormatter, dateFormatter, timeFormatter);
                }

                if (val2 == null) {
                  val2 = SortUtil.parseDate(error, s2.keyValues[j], dateTimeFormatter, dateFormatter, timeFormatter);
                }
              }

              // Maybe val1 and val 2 are XSD:DATE or XSD:DATETIME
              if (val1 == null) {
                val1 = SortUtil.parseXsdDatetime(error, s1.keyValues[j]);
              }
              
              if (val2 == null) {
                val2 = SortUtil.parseXsdDatetime(error, s2.keyValues[j]);
              }
              
              if (val1 != null && val2 != null) {
                compareTo = CriterionInformation.ORDER.ASCENDING.getName().equals(sortInformation.criteriaInfo[j].getOrder())
                    ? val1.compareTo(val2) : val2.compareTo(val1);
                error[0] = null;
              }
            } else {
              Collator collator = Collator.getInstance(locale);
              collator.setStrength(Collator.TERTIARY);
              collator.setDecomposition(Collator.FULL_DECOMPOSITION);
              
              int result = collator.compare(s1.keyValues[j], s2.keyValues[j]);
              compareTo = CriterionInformation.ORDER.ASCENDING.getName().equals(sortInformation.criteriaInfo[j].getOrder()) 
                  ? result : - result;
            }
            if (error[0] != null) {
              break;
            }
            if (compareTo != 0) {
              return compareTo;
            }
          }
        }
        return 1;
      }

      
    };
  }

  /**
   * Given the value obtained from the document for the 'xml:lang' attribute try to obtain a corresponding Locale.
   * 
   * @param langVal the string representing the language.
   * @return A Locale for this language.
   */
  private static Locale createLocale(String langVal) {
    int dashIdx = langVal.indexOf('-');
    String lang = langVal;
    String country = "";
    if (dashIdx > 0) {
      lang = langVal.substring(0, dashIdx);
      // Try to obtain an ISO language id.
      String[] isoLanguages = Locale.getISOLanguages();
      for (int i = 0; i < isoLanguages.length; i++) {
        if (isoLanguages[i].equalsIgnoreCase(lang)) {
          lang = isoLanguages[i];
        }
      }
      
      country = langVal.substring(dashIdx + 1);
      
      // Try to obtain an ISO country id.
      String[] isoCountries = Locale.getISOCountries();
      for (int i = 0; i < isoCountries.length; i++) {
        if (isoCountries[i].equalsIgnoreCase(lang)) {
          country = isoCountries[i];
        }
      }
    }
    return new Locale(lang, country);
  }
  
  /**
   * Check if the parent element selected children can be sorted.
   * For example a table row containing a cell with rowspan cannot be sorted and stops the operation.
   * 
   * @param parent The parent of the elements which will be sorted.
   * @param selectedNonIgnoredChildrenInterval The interval of selected children indices.
   * 
   * @throws AuthorOperationException When the given node is not sortable. 
   * For example a table row containing a cell with multiple rowspan stops the operation.
   */
  public abstract void canBeSorted(AuthorElement parent, int[] selectedNonIgnoredChildrenInterval) throws AuthorOperationException;

  /**
   * Return the interval of sortable nodes indices covered by selection.
   * 
   * @param parent The parent node for the sortable nodes.
   * @return An interval of sortable nodes indices that can be sorted.
   * Typically it returns a non-null interval when the selected sortable nodes from parent are part of a continuous sequence.
   * If the selection must be ignored or the sequence of selected nodes is discontinuous it returns <code>null</code>.
   */
  public int[] getSelectedNonIgnoredChildrenInterval(AuthorElement parent) {
    int[] selectedChildrenInterval = null;
    if (authorAccess.getEditorAccess().hasSelection()) {
      // Maybe we need to force the operation to sort all the elements, not only
      // the selected ones.
      if (forceSortAll()) {
        return null;
      }
      
      // Obtain the selection
      final List<ContentInterval> selectionIntervals = authorAccess.getEditorAccess().getAuthorSelectionModel().getSelectionIntervals();
      int selStart = 0;
      int selEnd = -1;
      List<AuthorNode> nonIgnoredChildren = getNonIgnoredChildren(parent);
      // Check the selection
      boolean validSel = true;
      for (int i = 0; i < selectionIntervals.size(); i++) {
        ContentInterval selectionInterval = selectionIntervals.get(i);
        int[] selection = getSelectionElementsIndices(nonIgnoredChildren, selectionInterval.getStartOffset(), selectionInterval.getEndOffset());
        // First element, retain the start and the end
        if (i == 0) {
          selStart = selection[0];
          selEnd = selection[1];
        } else if (selection[0] - 1 == selEnd) {
          // Still continuous selection
          selEnd = selection[1]; 
        } else {
          // Invalid selection
          validSel = false;
          // Do not go further, it is unnecessary
          break;
        }
      }
      
      // The current selection is valid
      if (validSel) {
        selectedChildrenInterval = new int[] {selStart, selEnd};
      }
    }
    
    return selectedChildrenInterval;
  }
  
  /**
   * @return <code>true</code> if the sort operation should not use the selected
   * element and should always sort all elements. 
   */
  protected boolean forceSortAll() {
    return false;
  }

  /**
   * Given a selection interval it returns an interval of sortable children indices covered by selection.
   * 
   * @param nonIgnoredChildren The non ignored sortable children.
   * @param selStart Selection start.
   * @param selEnd Selection end.
   * @return an interval of sortable nodes indices that are intersected by selection.
   */
  private static int[] getSelectionElementsIndices(List<AuthorNode> nonIgnoredChildren, int selStart, int selEnd) {
    int startSelChildIndex = -1;
    int endSelChildIndex = -1;
    
    int size = nonIgnoredChildren.size();
    for (int i = 0; i < size; i++) {
      AuthorNode authorNode = nonIgnoredChildren.get(i);
      if ((authorNode.getStartOffset() <= selStart && authorNode.getEndOffset() >= selStart)
          || (startSelChildIndex == -1 && selStart <= authorNode.getStartOffset() && selEnd > authorNode.getStartOffset())) {
        startSelChildIndex = i;
      }
      
      if ((authorNode.getStartOffset() < selEnd && authorNode.getEndOffset() >= selEnd)
          || (selStart <= authorNode.getEndOffset() && selEnd > authorNode.getEndOffset())) {
        endSelChildIndex = i;
      }
    }
    
    return new int[] {startSelChildIndex, endSelChildIndex};
  }
  
  /**
   * Returns a list of non ignored children.
   *  
   * @param parent The parent node.
   * @return A list of non ignored children.
   */
  protected List<AuthorNode> getNonIgnoredChildren(AuthorElement parent) {
    List<AuthorNode> nonIgnoredChildren = new ArrayList<AuthorNode>();
    
    List<AuthorNode> children = parent.getContentNodes();
    for (int i = 0; i < children.size(); i++) {
      AuthorNode child = children.get(i);
      if (!isIgnored(child)) {
        nonIgnoredChildren.add(child);
      }
    }
    
    return nonIgnoredChildren;
  }

  /**
   * Obtain the parent node of all the nodes which will be sorted.
   * 
   * @param offset The offset where the operation was invoked.
   * @param authorAccess The {@link AuthorAccess}.
   * 
   * @return The parent node of the nodes which will be sorted.
   * @throws AuthorOperationException When the offset is negative or greater than the content length.
   */
  public abstract AuthorElement getSortParent(int offset, AuthorAccess authorAccess) throws AuthorOperationException;
  
  /**
   * Checks if a given node is ignored when sorting.
   * 
   * @param node The node to be checked.
   * 
   * @return <code>true</code> if the given node is ignored when sorting.
   */
  public abstract boolean isIgnored(AuthorNode node);
  
  /**
   * Obtain the values of the keys that can be used for sorting.
   * 
   * @param node      The element which will be sorted.
   * @param sortInfo  The sort information corresponding to the user choice.
   * 
   * @return an array containing the values of the keys which can be used for sorting.
   * @throws AuthorOperationException  If the text content cannot be obtained.
   */
  public abstract String[] getSortKeysValues(AuthorNode node, SortCriteriaInformation sortInfo) throws AuthorOperationException;
  
  /**
   * Obtain the sort criterion.
   * 
   * @param parent  The parent node of the nodes which will be sorted.
   * 
   * @return A {@link SortCriteriaInformation} containing the {@link CriterionInformation} objects.
   * @throws AuthorOperationException 
   */
  public abstract List<CriterionInformation> getSortCriteria(AuthorElement parent) throws AuthorOperationException;
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return null;
  }
  
  /**
   * Given a node obtain the content to be used during the sort operation.
   * In general this text does not contain the deleted changes and the leading or trailing spaces.
   * 
   * @param node The node to get the value for.
   * @return The test to be considered as sort key value.
   */
  protected String getTextContentToSort(AuthorNode node) {
    TextContentIterator contentIterator = authorAccess.getDocumentController().getTextContentIterator(node.getStartOffset(), node.getEndOffset());
    StringBuilder val = new StringBuilder();
    while (contentIterator.hasNext()) {
      TextContext textContext = contentIterator.next();
      if (textContext.getEditableState() != TextContext.NOT_EDITABLE_IN_DELETE_CHANGE_TRACKING) {
        val.append(textContext.getText());
      }
    }
    return val.toString().trim();
  }
  
  /**
   * Get the ID of the help page which will be called by the end user.
   * @return the ID of the help page which will be called by the end user or <code>null</code>.
   */
  protected String getHelpPageID(){
    return SORTING_SUPPORT_PAGE_ID;
  }
}