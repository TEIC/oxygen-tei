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
package ro.sync.ecss.extensions.commons.table.operations.cals;

import java.util.List;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.AuthorParentNode;
import ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper;
import ro.sync.ecss.extensions.commons.table.support.CALSColSpanSpec;
import ro.sync.ecss.extensions.commons.table.support.CALSColSpec;
import ro.sync.ecss.extensions.commons.table.support.CALSTableCellInfoProvider;

/**
 * Implementation of the document type helper for CALS table model(DocBook and DITA).
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class CALSDocumentTypeHelper extends AbstractDocumentTypeHelper implements CALSConstants {
  /**
   * The list of row element names for CALS Table.
   */
  private static final String[] ROW_ELEMENT_NAMES = new String[] { ELEMENT_NAME_ROW };
  /**
   * The list of cell element names for CALS Table.
   */
  private static final String[] CELL_ELEMENT_NAMES = new String[] { ELEMENT_NAME_ENTRY };
  /**
   * The list of table element names for CALS Table.
   */
  private static final String[] TABLE_ELEMENT_NAMES = 
    new String[] { ELEMENT_NAME_TGROUP, ELEMENT_NAME_INFORMALTABLE };
  
  /**
   * @see ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper#getTableCellElementNames()
   */
  @Override
  public String[] getTableCellElementNames() {
    return CELL_ELEMENT_NAMES;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper#getTableRowElementNames()
   */
  @Override
  public String[] getTableRowElementNames() {
    return ROW_ELEMENT_NAMES;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper#getTableElementLocalName()
   */
  @Override
  public String[] getTableElementLocalName() {
    return TABLE_ELEMENT_NAMES;
  }


  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#checkTableColSpanIsDefined(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider, ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public void checkTableColSpanIsDefined(
      AuthorAccess authorAccess, 
      AuthorTableCellSpanProvider tableSpanSupport, 
      AuthorElement cellElement) throws AuthorOperationException {
    CALSColSpanSpec cellSpanSpec = 
      ((CALSTableCellInfoProvider)tableSpanSupport).getCellSpanSpec(authorAccess, cellElement);
    if(cellSpanSpec == null) {
      AuthorOperationException ex = new AuthorOperationException(
          "Cannot compute the horizontal span specifications for the involved cells " +
          "because there are missing 'colspec' elements.");
      ex.setOperationRejectedOnPurpose(true);
      throw ex;
    }
  }
  
  /**
   * Get the column specification defined for a table column. 
   * <p> I.E. for DocBook the column specification is defined by the 'colspec' element. 
   * If it is missing then the column specification is not defined.
   * 
   * @param tableSpanSupport The table span support.
   * @param colIndex The index of the column. The index of column is 1 based.
   * @return The column specification if it could be obtained.
   * @throws AuthorOperationException When the column span is not defined for the table cell. 
   * 
   */
  private static CALSColSpec getTableColSpec(AuthorTableCellSpanProvider tableSpanSupport, int colIndex)
      throws AuthorOperationException {
    CALSColSpec colSpec = 
      ((CALSTableCellInfoProvider)tableSpanSupport).getColSpec(colIndex);
    
    if(colSpec == null) {
      throw new AuthorOperationException(
          "There is no column specification for column with index: " + colIndex);
    }
    return colSpec;
  }

  /**
   * Update the span information of the specified cell element. 
   * The <code>namest</code> and <code>nameend</code> attributes will be set 
   * according to the <code>startCol</code> and <code>endCol</code> supplied values.    
   * If the <code>spanname</code> attribute is set, then it will be removed.
   * If the <code>colname</code> attribute is set, then it will be removed.    
   * 
   * @throws AuthorOperationException If the supplied values for start span column 
   * and end span column do not correspond to existing columns specifications. 
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#updateTableColSpan(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider, ro.sync.ecss.extensions.api.node.AuthorElement, int, int)
   */
  @Override
  public void updateTableColSpan(
      AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableSupport,
      AuthorElement cellElem,
      int startCol,
      int endCol) throws AuthorOperationException {

    // Find the column specifications for start and end indices
    CALSColSpec startColSpec = getTableColSpec(tableSupport, startCol);
    CALSColSpec endColSpec = getTableColSpec(tableSupport, endCol);    

    // Update the 'namest' attribute if necessary
    AuthorDocumentController controller = authorAccess.getDocumentController();

    // If column span is one remove the 'namest' and 'nameend' attributes.
    if(startColSpec.getColumnNumber() == endColSpec.getColumnNumber()) {
      controller.removeAttribute(ATTRIBUTE_NAME_NAMEST, cellElem);
      controller.removeAttribute(ATTRIBUTE_NAME_NAMEEND, cellElem);
    } else {
      // Set 'namest' attribute.
      controller.setAttribute(
          ATTRIBUTE_NAME_NAMEST,
          new AttrValue(startColSpec.getColumnName()),
          cellElem);        
      // Set 'nameend' attribute.
      controller.setAttribute(
          ATTRIBUTE_NAME_NAMEEND,
          new AttrValue(endColSpec.getColumnName()),
          cellElem);        
    }

    // Remove 'spanname' attribute.
    authorAccess.getDocumentController().removeAttribute(
        ATTRIBUTE_NAME_SPANNAME,
        cellElem);
    
    // EXM-40431 Remove 'colname' attribute.
    authorAccess.getDocumentController().removeAttribute(
        ATTRIBUTE_NAME_COLNAME,
        cellElem);
  }

  /**
   * Creates an <code>AuthorTableCellSpanProvider</code> corresponding to the table element.
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#getTableCellSpanProvider(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public AuthorTableCellSpanProvider getTableCellSpanProvider(AuthorElement tgroupElement) {
    CALSTableCellInfoProvider tableSpanSupport = 
      new CALSTableCellInfoProvider();
    tableSpanSupport.init(tgroupElement);    
    return tableSpanSupport;
  }

  /**
   * Update the <code>morerows</code> attribute value for the given cell element.
   * If the supplied value for the row span is less than or equal to 1 then the 
   * attribute will be removed. 
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#updateTableRowSpan(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public void updateTableRowSpan(AuthorAccess authorAccess, AuthorElement cellElem, int rowSpan) {
    if (rowSpan > 1) {
      authorAccess.getDocumentController().setAttribute(
          ATTRIBUTE_NAME_MOREROWS, 
          new AttrValue(String.valueOf(rowSpan - 1)) , 
          cellElem);
    } else {
      authorAccess.getDocumentController().removeAttribute(
          ATTRIBUTE_NAME_MOREROWS, 
          cellElem);
    }
  }

  /**
   * Update the <code>cols</code> attribute value of the table <code>tgroup</code> 
   * element. 
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#updateTableColumnNumber(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public void updateTableColumnNumber(
      AuthorAccess authorAccess, 
      AuthorElement tableElement, 
      int colsNumber) {
    // Change the 'cols' attribute of the 'tgroup' element
    authorAccess.getDocumentController().setAttribute(
        ATTRIBUTE_NAME_COLS, 
        new AttrValue(String.valueOf(colsNumber)), 
        tableElement);
  }

  /**
   * Not needed for CALS Tables.
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#updateTableRowNumber(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public void updateTableRowNumber(AuthorAccess authorAccess, AuthorElement tableElement,
      int rowsNumber) {
    // Nothing to do here.
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#getIgnoredRowAttributes()
   */
  @Override
  public String[] getIgnoredRowAttributes() {
    return new String[] {
        ATTRIBUTE_NAME_MOREROWS,
    };
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#getIgnoredCellIDAttributes()
   */
  @Override
  public String[] getIgnoredCellIDAttributes() {
	    return new String[] {
	            ATTRIBUTE_NAME_XML_ID, 
	            ATTRIBUTE_NAME_ID,
	        };
  }

  /**
   * Get a list of allowed cell attributes to copy when creating a new row.
   * @return a list of allowed cell attributes to copy when creating a new row.
   */
  @Override
  public String[] getAllowedCellAttributesToCopy() {
	  //Return the list from the CALS table specification
      return new String[] {
              "namest", 
              "nameend",
              "rowsep",
              "colsep",
              "colname",
              "spanname",
              "align",
              "valign",
              "charoff",
              "rotate",
          };
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#getIgnoredColumnAttributes()
   */
  @Override
  public String[] getIgnoredColumnAttributes() {
    return new String[] {
        ATTRIBUTE_NAME_NAMEST,
        ATTRIBUTE_NAME_NAMEEND,
    };
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.cals.CALSDocumentTypeHelper#getTableElementForDeletion(ro.sync.ecss.extensions.api.node.AuthorNode)
   */
  @Override
  public AuthorNode getTableElementForDeletion(AuthorNode element) {
    AuthorNode tableElement = null;
    if (isActuallyTableAndNotTgroup(element)) {
      tableElement = element;
    } else {
      while (element.getParent() != null) {
        if (isActuallyTableAndNotTgroup(element.getParent())) {
          tableElement = element.getParent();
          break;
        }
      }
    }
    
    return tableElement;
  }
  
  /**
   * Check if the given node is a CALS table (not a tgroup, but actually a table element).
   * 
   * @param node the node for which we perform the check.
   * 
   * @return <code>true</code> if the given node is a table element.
   */
  protected boolean isActuallyTableAndNotTgroup(AuthorNode node) {
    if(node instanceof AuthorElement) {
      AuthorElement element = (AuthorElement) node;
      if (ELEMENT_NAME_TABLE.equals(element.getLocalName())
          || ELEMENT_NAME_INFORMALTABLE.equals(element.getLocalName())) {
        return true;
      } 
    }
    return false;
  }
  
  /**
   * Limits the value of the "morerows" attribute from the given rows
   * fragments according to the number of rows. Each fragment has inside it a single table row. 
   * For example if we have 3 rows and 
   * the first row contains a cell with 'morerows=5', we'll set 'morerows=2' on the cell. 
   * 
   * @param rowFragments The fragments of rows to be limited.
   */
  public void limitRowSpan(AuthorDocumentFragment[] rowFragments) {
    int nuOfRows = rowFragments.length;
    for (int i = 0; i < nuOfRows; i++) {
      AuthorDocumentFragment rowFragment = rowFragments[i];
      int maxRowSpanValue = nuOfRows - (i + 1);
      limitRowSpanInRowDocumentFragment(rowFragment, maxRowSpanValue);
   }
  }
  
  /**
   *  Limits the value of the "morerows" attribute from the given row fragment containg a single table row.
   *  
   * @param fragment         Fragment that contains the row to be limited.
   * @param maxRowSpanValue  The maximum value of the 'morerows' attribute.
   */
  private AuthorDocumentFragment limitRowSpanInRowDocumentFragment(
      AuthorDocumentFragment fragment, int maxRowSpanValue){
    List<AuthorNode> contentNodes = fragment.getContentNodes();
    if(contentNodes != null && !contentNodes.isEmpty()) {
      AuthorNode rowNode = contentNodes.get(0);
      if(isTableRow(rowNode)) {
        limitRowSpanInRowCells((AuthorParentNode)rowNode, maxRowSpanValue);
      }
    }
    return fragment;
  }

  /**
   *  Limits the value of the "morerows" attribute from the given row node.
   *  
   * @param rowNode          The row node to be limited.
   * @param maxRowSpanValue  The maximum value of the 'morerows' attribute.
   */
  private static void limitRowSpanInRowCells(AuthorParentNode rowNode, int maxRowSpanValue) {
    List<AuthorNode> cells = rowNode.getContentNodes();
    CALSTableCellInfoProvider provider = new CALSTableCellInfoProvider();
    for (AuthorNode cell : cells) {
      if(cell instanceof AuthorElement) {
        AuthorElement cellElement = (AuthorElement)cell;
        Integer rowSpan = provider.getRowSpan(cellElement);
        if(rowSpan != null) {
          if(rowSpan > maxRowSpanValue) {
            if(maxRowSpanValue > 0) {
              cellElement.setAttribute(
                  ATTRIBUTE_NAME_MOREROWS, new AttrValue(String.valueOf(maxRowSpanValue)));
            } else {
              cellElement.removeAttribute(ATTRIBUTE_NAME_MOREROWS);
            }
          }
        }
      }
    }
  }
}