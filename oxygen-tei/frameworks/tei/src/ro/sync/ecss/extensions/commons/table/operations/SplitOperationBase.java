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
package ro.sync.ecss.extensions.commons.table.operations;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;

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
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.ContentInterval;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.access.AuthorTableAccess;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;
import ro.sync.exml.workspace.api.Platform;

/**
 * Operation for splitting the selected table cell (or the cell at caret
 * when there is no selection), if it spans over multiple rows or columns
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(false)
@WebappRestSafe
public abstract class SplitOperationBase extends AbstractTableOperation {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(SplitOperationBase.class.getName());
  /**
   * Constructor.
   * 
   * @param tableHelper Table helper with methods specific to a document type.
   */
  public SplitOperationBase(AuthorTableHelper tableHelper) {
    super(tableHelper);
  }
  
  /**
   *  Split the selected table cell (or the cell at caret when there is no selection), if it spans over multiple rows or columns
   *  
   * @see ro.sync.ecss.extensions.commons.table.operations.AbstractTableOperation#doOperationInternal(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  protected void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    try {
      AuthorElement cell = null;
      
      if(authorAccess.getEditorAccess().hasSelection()) {
        // Determine the fully selected cell
        AuthorNode fullySelectedNode = authorAccess.getEditorAccess().getFullySelectedNode();
        if (fullySelectedNode != null) {
          cell = getElementAncestor(fullySelectedNode, AuthorTableHelper.TYPE_CELL);
        }
      }
      
      if (cell == null) {
        // Determine the cell at caret
        int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
        AuthorNode nodeAtCaret = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
        cell = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_CELL);
      }
      
      if (cell != null) {
        // Check row spans and col spans
        AuthorTableAccess tableAccess = authorAccess.getTableAccess();
        int[] tableRowSpanIndices = tableAccess.getTableRowSpanIndices(cell);
        int[] tableColSpanIndices = tableAccess.getTableColSpanIndices(cell);

        // Rows
        int startRow = tableRowSpanIndices[0];
        int endRow = tableRowSpanIndices[1];
        // Columns
        int startColumn = tableColSpanIndices[0];
        int endColumn = tableColSpanIndices[1];
        
        int initialRowSpan = endRow - startRow + 1;
        int initialColSpan = endColumn - startColumn + 1;
        
        AuthorDocumentController controller = authorAccess.getDocumentController();
        boolean hasInitialSpan = initialRowSpan > 1 || initialColSpan > 1;
        int rowSpan = hasInitialSpan ? initialRowSpan : 20;
        int colSpan = hasInitialSpan ? initialColSpan : 20;

        int[] result = null;
        int[] imposedSplitInfo = getSplitInfoFromArguments(args);
        if (imposedSplitInfo != null) {
          // Check if the rows count and columns count are imposed from arguments
          result = new int[] {imposedSplitInfo[0], imposedSplitInfo[1]};
        } else if ((rowSpan == 2 && colSpan == 1) || (rowSpan == 1 && colSpan == 2)) {
          result = new int[] {colSpan, rowSpan};
        } else {
          // Determine the platform
          Platform platform = authorAccess.getWorkspaceAccess().getPlatform();
          // Show the dialog
          if (Platform.STANDALONE == platform) {
            SATableSplitCustomizerDialog saSplitDialog = new SATableSplitCustomizerDialog(
                (Frame) authorAccess.getWorkspaceAccess().getParentFrame(), 
                authorAccess.getAuthorResourceBundle(), colSpan, rowSpan) {
              @Override
              public String getHelpPageID() {
                return SplitOperationBase.this.getHelpPageID();
              }
            };
            result = saSplitDialog.getSplitInformation();
          } else if (Platform.ECLIPSE == platform) {
            //Eclipse table customization
            ECTableSplitCustomizerDialog ecTablePropertiesCustomizer = new ECTableSplitCustomizerDialog(
                authorAccess.getWorkspaceAccess().getParentFrame(), 
                authorAccess.getAuthorResourceBundle(), colSpan, rowSpan, getHelpPageID());
            result = ecTablePropertiesCustomizer.getSplitInformation();
          }
        }

        // Get the number of cells to insert
        if (result != null) {
          // Get the table parent
          AuthorElement tableElem = getElementAncestor(cell, AuthorTableHelper.TYPE_TABLE);
          int nrOfColumnsForSplit = result[0];
          int nrOfRowsForSplit = result[1];

          if (nrOfRowsForSplit > 1 || nrOfColumnsForSplit > 1) {
            if (hasInitialSpan) {
              splitWithInitialSpan(authorAccess, cell, tableElem, initialRowSpan, 
                  initialColSpan, controller, nrOfColumnsForSplit, nrOfRowsForSplit);
            } else {
              splitNoInitialSpan(authorAccess, cell, tableElem, controller, 
                  nrOfColumnsForSplit - 1, nrOfRowsForSplit - 1);
            }
          }
          
          // Determine the intervals for selection
          List<ContentInterval> toSelect = new ArrayList<ContentInterval>();
          
          int lastRowSpan = 0;
          int lastColSpan = 0;
          for (int i = 0; i < nrOfRowsForSplit; i++) {
            int startIntervalOffset = -1;
            int endIntervalOffset = -1;
            lastColSpan = 0;
            int currentRowSpan = -1; 
            for (int j = 0; j < nrOfColumnsForSplit; j++) {
              AuthorElement cellToSelect = tableAccess.getTableCellAt(
                  startRow + lastRowSpan + i, startColumn + lastColSpan + j, tableElem);
              
              if (currentRowSpan == -1) {
                int[] rowSpanIndices = tableAccess.getTableRowSpanIndices(cell);
                // Rows
                int startCurrentRow = rowSpanIndices[0];
                int endCurrentRow = rowSpanIndices[1];
                currentRowSpan = endCurrentRow - startCurrentRow;
              }
              
              int[] colSpanIndices = tableAccess.getTableColSpanIndices(cell);
              // Columns
              int startCurrentColumn = colSpanIndices[0];
              int endCurrentColumn = colSpanIndices[1];
              int initialCurrentColSpan = endCurrentColumn - startCurrentColumn;
              
              lastColSpan += initialCurrentColSpan;
              
              if (i == 0 && j == 0) {
                // Set caret at start
                authorAccess.getEditorAccess().setCaretPosition(cellToSelect.getStartOffset());
              } 
              
              if (j == 0) {
                // Determine the start interval offset
                startIntervalOffset = cellToSelect.getStartOffset();
              }
              // Determine the end interval offset
              endIntervalOffset = cellToSelect.getEndOffset() + 1;
            }
            
            lastRowSpan += currentRowSpan;
            
            if (startIntervalOffset >= 0 && endIntervalOffset >= 0) {
              toSelect.add(new ContentInterval(startIntervalOffset, endIntervalOffset));
            }
          }
          
          // Select all the cells that were affected by the split operation
          authorAccess.getEditorAccess().getAuthorSelectionModel().addSelectionIntervals(toSelect, false);
        }
      } else {
        AuthorOperationException ex = new AuthorOperationException("The caret must be inside a table cell.");
        ex.setOperationRejectedOnPurpose(true);
        throw ex;
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException(
          "The operation cannot be performed due to: " + e.getMessage(), e);
    }
  }

  /**
   * Get the split information imposed from arguments.
   * 
   * @param args The map of arguments.  
   * @return An array containing the split row count and split column count values.
   */
  private static int[] getSplitInfoFromArguments(ArgumentsMap args) {
    int[] imposedSplitInfo = null;
    Object spltInfo = args.getArgumentValue("split_info");
    if (spltInfo instanceof String) {
      String splitInfoString = (String) spltInfo;
      String[] split = splitInfoString.split(",");
      if (split.length == 2) {
        String rows = split[0].trim();
        String cols = split[1].trim();
        try {
          imposedSplitInfo = new int[2];
          imposedSplitInfo[0] = NumberParserUtil.valueOfInteger(rows);
          imposedSplitInfo[1] = NumberParserUtil.valueOfInteger(cols);
        } catch (NumberFormatException e) {
          logger.error(e);
        }
      }
    }
    return imposedSplitInfo;
  }

  /**
   * Split the cell when it has initial column or row span.
   * 
   * @param authorAccess The author access.
   * @param cell The table cell to span.
   * @param tableElement The parent table element
   * @param controller The Author document controller
   * @param nrOfColumnsForSplit The split columns number
   * @param nrOfRowsForSplit The split rows number
   */
  private void splitNoInitialSpan(AuthorAccess authorAccess, AuthorElement cell,
      AuthorElement tableElement, AuthorDocumentController controller, int nrOfColumnsForSplit, 
      int nrOfRowsForSplit) throws BadLocationException, AuthorOperationException {
    String xPathExpression = authorAccess.getDocumentController().getXPathExpression(cell.getStartOffset());
    int insertOffset = cell.getEndOffset();

    // Set of initial cells from the same column with the split one
    // These cells will be joined with the consecutive ones, added during the split 
    Set<AuthorElement> columnCellsToJoin = null;
    // Set of initial cells from the same row with the split one
    // These cells will be joined with the consecutive ones, added during the split
    Set<AuthorElement> rowCellsToJoin = null;

    AuthorTableAccess tableAccess = authorAccess.getTableAccess();
    int[] tableCellIndex = tableAccess.getTableCellIndex(cell);
    int rowIndex = tableCellIndex[0];
    int colIndex = tableCellIndex[1];

    // Determine the cells from the same row, that must be joined
    if (nrOfRowsForSplit >= 1) {
      rowCellsToJoin = new HashSet<AuthorElement>();

      int numberOfColumns = tableAccess.getTableNumberOfColumns(tableElement);
      for (int i = 0; i < numberOfColumns; i++) {
        if (i != colIndex) {
          AuthorElement tableCell = tableAccess.getTableCellAt(rowIndex, i, tableElement);
          if (tableCell != null) {
            rowCellsToJoin.add(tableCell);
          }
        }
      }
    }

    // Determine the cells from the same column, that must be joined
    if (nrOfColumnsForSplit >= 1) {
      columnCellsToJoin = new HashSet<AuthorElement>();
      int numberOfRows = tableAccess.getTableRowCount(tableElement);
      for (int i = 0; i < numberOfRows; i++) {
        if (i != rowIndex) {
          AuthorElement tableCell = tableAccess.getTableCellAt(i, colIndex, tableElement);
          if (tableCell != null) {
            int[] tableColSpanIndices = tableAccess.getTableColSpanIndices(tableCell);
            if (tableColSpanIndices[1] == colIndex) {
              columnCellsToJoin.add(tableCell);
            }
          }
        }
      }
    }

    // Insert rows
    getInsertRowOperation().insertRows(authorAccess, xPathExpression, cell.getNamespace(), 
        cell, tableElement, nrOfRowsForSplit, AuthorConstants.POSITION_AFTER);
    // Insert columns
    getInsertColumnOperation().insertColumns(authorAccess, tableElement, 
        cell.getNamespace(), AuthorConstants.POSITION_AFTER, 
        insertOffset, nrOfColumnsForSplit);

    // Join 
    if (columnCellsToJoin != null || rowCellsToJoin != null) {
      try {
        JoinOperationBase joinOperation = getJoinOperation();
        if (columnCellsToJoin != null) {

          // Join the column cells
          for (AuthorElement elem : columnCellsToJoin) {
            int[] cellIndex = tableAccess.getTableCellIndex(elem);
            int[] tableRowSpanIndices = tableAccess.getTableRowSpanIndices(elem);
            int[] tableColSpanIndices = tableAccess.getTableColSpanIndices(elem);

            int startRow = cellIndex[0];
            int startCol = cellIndex[1];
            int endRow = tableRowSpanIndices[1];
            int endCol = startCol + nrOfColumnsForSplit + 
                (tableColSpanIndices[1] - tableColSpanIndices[0]);

            Set<AuthorElement> toJoin = new HashSet<AuthorElement>();
            for (int i = startRow; i <= endRow; i++) {
              for (int j = startCol; j <= endCol; j++) {
                AuthorElement nextCell = tableAccess.getTableCellAt(i, j, tableElement);
                if (nextCell != null) {
                  toJoin.add(nextCell);
                }
              }
            }

            if (toJoin.size() > 1) {
              // Join
              joinOperation.joinCells(authorAccess, tableElement, new ArrayList<AuthorElement>(toJoin));
            }
          }
        }

        // Join the row cells
        if (rowCellsToJoin != null) {
          for (AuthorElement elem : rowCellsToJoin) {
            int[] cellIndex = tableAccess.getTableCellIndex(elem);
            int[] tableColSpanIndices = tableAccess.getTableColSpanIndices(elem);

            int startRow = cellIndex[0];
            int startCol = cellIndex[1];
            int endRow = startRow + nrOfRowsForSplit;
            int endCol = tableColSpanIndices[1];

            Set<AuthorElement> toJoin = new HashSet<AuthorElement>();
            for (int i = startRow; i <= endRow; i++) {
              for (int j = startCol; j <= endCol; j++) {
                AuthorElement nextCell = tableAccess.getTableCellAt(i, j, tableElement);
                if (nextCell != null) {
                  toJoin.add(nextCell);
                }
              }
            }

            if (toJoin.size() > 1) {
              // Join
              joinOperation.joinCells(authorAccess, tableElement, new ArrayList<AuthorElement>(toJoin));
            }
          }
        }

        // EXM-38238 Copy the attributes from the source cell to the cells inserted after split 
        int attributesCount = cell.getAttributesCount();
        if (attributesCount > 0) {
          String[] skippedColumnAttributes = getIgnoredAttributesForColumnSplit();
          String[] skippedRowAttributes = getIgnoredAttributesForRowSplit();
          List< String> skippedAttributesList = null;
          if (skippedColumnAttributes != null || skippedRowAttributes != null) {
            // Collect attributes that must be skipped
            skippedAttributesList = new ArrayList<String>();
            if (skippedColumnAttributes != null) {
              skippedAttributesList.addAll(Arrays.asList(skippedColumnAttributes));
            }
            if (skippedRowAttributes != null) {
              skippedAttributesList.addAll(Arrays.asList(skippedRowAttributes));
            }
          }
          Map<String, String> attrsToBeAdded = new LinkedHashMap<String, String>();
          for (int i = 0; i < attributesCount; i++) {
            String attrName = cell.getAttributeAtIndex(i);
            AttrValue attrValue = cell.getAttribute(attrName);
            if (attrValue.isSpecified() && 
                (skippedAttributesList == null || !skippedAttributesList.contains(attrName))) {
              attrsToBeAdded.put(attrName, attrValue.getValue());
            }
          }
          if (attrsToBeAdded.size() > 0) {
            for (int i = rowIndex; i <= rowIndex + nrOfRowsForSplit; i++) {
              for (int j = colIndex; j <= colIndex + nrOfColumnsForSplit; j++) {
                if (i != rowIndex || j != colIndex) {
                  // Cell resulted from split
                  AuthorElement cellFromSplit = tableAccess.getTableCellAt(i, j, tableElement);
                  // Set attributes 
                  if (cellFromSplit != null) {
                    Set<String> keySet = attrsToBeAdded.keySet();
                    for (String key : keySet) {
                      controller.setAttribute(key, new AttrValue(attrsToBeAdded.get(key)), cellFromSplit);
                    }
                  }
                }
              }
            }
          }
        }
      } catch (AuthorOperationException e) {
        AuthorOperationException splitEx = new AuthorOperationException("The split operation cannot be completed.", e);
        logger.error(e, e);
        splitEx.setOperationRejectedOnPurpose(e.isOperationRejectedOnPurpose());
        throw splitEx;
      }
    }
  }
  
  /**
   * Get the insert row operation to be used when splitting cells that have no 
   * initial span.
   */
  protected abstract InsertRowOperationBase getInsertRowOperation();
  
  /**
   * Get the insert column operation to be used when splitting cells that have no 
   * initial span.
   */
  protected abstract InsertColumnOperationBase getInsertColumnOperation();
  
  /**
   * Get the join operation to be used when splitting cells that have no 
   * initial span.
   */
  protected abstract JoinOperationBase getJoinOperation();

  /**
   * Split the cell when it has initial column or row span.
   * 
   * @param authorAccess The author access.
   * @param cell The table cell to span.
   * @param tableElem The parent table element
   * @param initialRowSpan Initial row span
   * @param initialColSpan Initial column span
   * @param controller The Author document controller
   * @param nrOfColumnsForSplit The split columns number
   * @param nrOfRowsForSplit The split rows number
   */
  private void splitWithInitialSpan(AuthorAccess authorAccess, AuthorElement cell, AuthorElement tableElem,
      int initialRowSpan, int initialColSpan, AuthorDocumentController controller,
      int nrOfColumnsForSplit, int nrOfRowsForSplit)
      throws BadLocationException, AuthorOperationException {
    int currentRowSpan = 0;
    // Split cells
    AuthorElement firstSplitCellOnRow = cell;
    for (int i = 1; i <= nrOfRowsForSplit && firstSplitCellOnRow != null; i++) {
      int[] location = authorAccess.getTableAccess().getTableCellIndex(firstSplitCellOnRow);
      // i = 1 is current cell, we must not insert other cell
      if (i > 1) {
        int insertionOffset = findCellInsertionOffset(
            authorAccess, 
            tableElem, 
            location[0] + currentRowSpan, 
            location[1]);

        if (insertionOffset != -1) {
          // Insert cell
          // Create a fragment containing an exact copy of the cell
          AuthorDocumentFragment contentFragment  = createEmptyCell(authorAccess, firstSplitCellOnRow, 
              getIgnoredAttributesForRowSplit());
          // Insert the copy at the determined offset
          controller.insertFragment(insertionOffset, contentFragment);
          AuthorNode nodeAtCaret = authorAccess.getDocumentController().getNodeAtOffset(insertionOffset + 1);
          firstSplitCellOnRow = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_CELL);
        } else {
          AuthorOperationException ex = new AuthorOperationException("Could not determine the location where the split will occur.");
          ex.setOperationRejectedOnPurpose(true);
          throw ex;
        }
      }
      currentRowSpan = determineCurrentSpan(currentRowSpan, nrOfRowsForSplit, initialRowSpan, i);
      // Update row span 
      tableHelper.updateTableRowSpan(authorAccess, firstSplitCellOnRow, currentRowSpan);

      // Split this cell horizontally
      int currentColSpan = 0;
      AuthorElement currentSplitCellOnColumn = firstSplitCellOnRow;
      for (int j = 1; j <= nrOfColumnsForSplit && currentSplitCellOnColumn != null; j++) {
        if (j > 1) {
          // Create the cell
          int insertOffset = currentSplitCellOnColumn.getEndOffset() + 1;
          // Create a fragment for the empty cell to be inserted.
          AuthorDocumentFragment emptyCellFragment = createEmptyCell(authorAccess, currentSplitCellOnColumn, 
              getIgnoredAttributesForColumnSplit());
          // Insert the copy at the determined offset
          controller.insertFragment(insertOffset, emptyCellFragment);
          AuthorNode nodeAtCaret = authorAccess.getDocumentController().getNodeAtOffset(insertOffset + 1);
          currentSplitCellOnColumn = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_CELL);
        }

        currentColSpan = determineCurrentSpan(currentColSpan, nrOfColumnsForSplit, initialColSpan, j);
        // Update row span 
        updateColSpan(authorAccess, tableHelper.getTableCellSpanProvider(tableElem), 
            currentSplitCellOnColumn, currentColSpan);
      }
    }
  }

  /**
   * Determine the span for the new cell.
   * 
   * @param currentSpan     The last computed span.
   * @param countForSplit   The new number of cells.
   * @param initialSpan     The initial span of the the cell which will be split.
   * @param i               The current step.
   * 
   * @return The new span which will be set to a cell inserted in the document.
   */
  private static int determineCurrentSpan(int currentSpan, int countForSplit, int initialSpan, int i) {
    if (i == countForSplit) {
      currentSpan = initialSpan - (currentSpan * (countForSplit - 1)); 
    } else {
      currentSpan = Math.round((float)initialSpan/countForSplit);
      if (initialSpan - (currentSpan * (countForSplit - 1)) <= 0) {
        currentSpan = initialSpan/countForSplit;
      }
    }
    return currentSpan;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Split the selected table cell (or the cell at caret when there is no selection), "
        + "if it spans over multiple rows or columns";
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SplitOperationBase#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[] {
        new ArgumentDescriptor(
            "split_info", 
            ArgumentDescriptor.TYPE_JAVA_OBJECT, 
            "",
            "${ask('', generic, '2,2')}")
    };
  }
  
  /**
   * @return The attributes which should be skipped, when creating a copy of the split cell.
   */
  protected abstract String[] getIgnoredAttributesForRowSplit();
  
  /**
   * @return The attributes which should be skipped when creating a copy of 
   * the split cell.
   */
  protected abstract String[] getIgnoredAttributesForColumnSplit();
  
  /**
   * Update the column span of the <code>cell</code>. 
   * 
   * @param authorAccess Access to author functionality. 
   * @param tableSupport  The table cell span support.
   * @param cell  The table cell to update the column span for.
   * @param colSpan The number of columns that this cell spans over 
   * @throws AuthorOperationException When the column span cannot be decreased.
   */
  private void updateColSpan(
      AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableSupport, 
      AuthorElement cell, 
      int colSpan) throws AuthorOperationException {
    int[] cellColSpanIndices = authorAccess.getTableAccess().getTableColSpanIndices(cell);

    // Start column of the cell, 1 based.
    int startColumn = cellColSpanIndices[0] + 1;
    // End column of the cell, 1 based.
    int endColumn = startColumn + colSpan - 1;
    tableHelper.updateTableColSpan(authorAccess, tableSupport, cell, startColumn, endColumn);
  }
  
  /**
   * Get the ID of the help page which will be called by the end user.
   * @return the ID of the help page which will be called by the end user or <code>null</code>.
   */
  protected String getHelpPageID(){
    return null;
  }
}