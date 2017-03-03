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
package ro.sync.ecss.extensions.commons.table.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.AuthorTableColumnWidthProviderBase;
import ro.sync.ecss.extensions.api.WidthRepresentation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.support.errorscanner.CALSAndHTMLTableLayoutProblem;


/**
 * Provides information about the column width for DITA tables. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class DITATableCellInfoProvider extends AuthorTableColumnWidthProviderBase implements AuthorTableCellSpanProvider {

  /**
   * The DITA simpletable class attribute value. 
   */
  private static final String SIMPLETABLE_CLASS_VALUE = " topic/simpletable ";
  
  /**
   * The cell of the simpletable class attribute value.
   */
  private static final String SIMPLETABLE_CELL_CLASS_VALUE = " topic/stentry ";
  
  /**
   * The row of the simpletable class attribute value.
   */
  private static final String SIMPLETABLE_ROW_CLASS_VALUE = " topic/strow ";
  
  /**
   * The head of the simpletable class attribute value.
   */
  private static final String SIMPLETABLE_HEAD_CLASS_VALUE = " topic/sthead ";
  
  /**
   * The attribute name class.
   * The value is <code>class</code>
   */
  private static final String ATTRIBUTE_NAME_CLASS = "class";
  
  /**
   * The relcolwidth attribute of the simpletable.
   * The value is <code>relcolwidth</code>
   */
  private static final String ATTRIBUTE_NAME_RELCOLWIDTH = "relcolwidth";
  
  /**
   * The CALS table cell information provider used if the table is not 
   * a simpletable.
   */
  private CALSTableCellInfoProvider calsProvider;
  
  /**
   * A list containing the specified widths for the table columns if the table 
   * is a DITA simpletable. 
   */
  private List<WidthRepresentation> columnWidths = new ArrayList<WidthRepresentation>();
  
  /**
   * The tag name for the table cells (eg: stentry, choption, chdesc, propvalue, proptype, propdesc etc.)
   */
  private Set<String> simpleTableCellTagNames = new HashSet<String>();
  
  /**
   * The table element.
   */
  private AuthorElement tableElement;
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#init(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public void init(AuthorElement tableElement) {
    this.tableElement = tableElement;
    calsProvider = null;
    AttrValue classAttrVal = tableElement.getAttribute(ATTRIBUTE_NAME_CLASS);
    if(classAttrVal != null && classAttrVal.getRawValue() != null && classAttrVal.getRawValue().contains(" ut-d/imagemap ")){
      //EXM-32789 Inside an image map, no cals provider.
    } else if (classAttrVal != null 
        && classAttrVal.getRawValue() != null 
        && classAttrVal.getRawValue().contains(SIMPLETABLE_CLASS_VALUE)) {
      //Detect simple table cell tag names.
      simpleTableCellTagNames = detectSimpleTableCellTagNames(tableElement);
      
      // Init the table column widths list
      AttrValue relColWidthAttr = tableElement.getAttribute(ATTRIBUTE_NAME_RELCOLWIDTH);
      if (relColWidthAttr != null) {
        String value = relColWidthAttr.getValue().trim();
        if (value.length() > 0) {
          String[] split = value.split(" ");
          for (String token : split) { 
            float relativeWidth = 0f;
            if (token.length() > 0) {
              if (token.endsWith("*")) {
                String proportion = token.substring(0, token.length() - 1);
                try {
                  // Find the relative width value.
                  if (proportion.length() == 0) {
                    // '*' it means '1*'
                    relativeWidth = 1;
                  } else {
                    relativeWidth = Float.parseFloat(proportion);
                  }
                } catch (NumberFormatException e) {
                  // Nothing to do, the relative width will remain 0.
                }
              } else //Maybe it ends with an unit of measure...
                if(token.endsWith("pt") || token.endsWith("px") || token.endsWith("in")){
                  // Not a number.
                  if (errorsListener != null) {
                    errorsListener.add(tableElement, tableElement, CALSAndHTMLTableLayoutProblem.COLUMN_WIDTH_NO_MEASURING_UNITS_VALUE_INCORRECT, token);
                  }
                }
              // Create the corresponding colwidth.
              WidthRepresentation colWidth = new WidthRepresentation(0f, null, relativeWidth, false);
              // Add the colwidth to the column widths list.
              columnWidths.add(colWidth);
            }
          }
        }
      }
    } else {
      // The table is not a simpletable so init the CALS table support.
      //But be flexible with the cell name.
      calsProvider = new DITACALSTableCellInfoProvider();
      calsProvider.init(tableElement);
    }
  }

  /**
   * Determine the list of cell tag names.
   * 
   * @param tableElement The table element.
   */
  private static Set<String> detectSimpleTableCellTagNames(AuthorElement tableElement) {
    Set<String> cellTagNames = new  HashSet<String>();
    // Determine the table cell tag name.
    List<AuthorNode> tableChildren = tableElement.getContentNodes();
    for (Iterator<AuthorNode> iterator = tableChildren.iterator(); iterator.hasNext();) {
      AuthorNode tableChild = iterator.next();
      if (tableChild.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
        AttrValue classAttrVal = ((AuthorElement) tableChild).getAttribute(ATTRIBUTE_NAME_CLASS);
        if (classAttrVal != null 
            && classAttrVal.getRawValue() != null 
            && (classAttrVal.getRawValue().contains(SIMPLETABLE_ROW_CLASS_VALUE)
                || classAttrVal.getRawValue().contains(SIMPLETABLE_HEAD_CLASS_VALUE))) {
          List<AuthorNode> rowChildren = ((AuthorElement) tableChild).getContentNodes();
          for (Iterator<AuthorNode> iterator2 = rowChildren.iterator(); iterator2.hasNext();) {
            AuthorNode rowChild = iterator2.next();
            if (rowChild.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
              classAttrVal = ((AuthorElement) rowChild).getAttribute(ATTRIBUTE_NAME_CLASS);
              if (classAttrVal != null 
                  && classAttrVal.getRawValue() != null 
                  && classAttrVal.getRawValue().contains(SIMPLETABLE_CELL_CLASS_VALUE)) {
                cellTagNames.add(((AuthorElement) rowChild).getLocalName());
              }
            }
          }
        }
      }
    }
    return cellTagNames;
  }
  
  /**
   * Returns true if the provided cell tag name is one of the 
   * determined cell tag names for the current table.
   * 
   * @param cellTagName The cell tag name to be tested.
   * @return <code>true</code> if the given tag name is a cell 
   * tag name for the current table.
   */
  private boolean isSimpleTableCell(String cellTagName) {
    return simpleTableCellTagNames.contains(cellTagName);
  }
  
  /**
   * Returns true if the provided cell tag name belongs to a reltable.
   * 
   * @param cellTagName The cell tag name to be tested.
   * @return <code>true</code> if the given tag name is a cell 
   * tag name for the current table.
   */
  private boolean isRelTableCell(String cellTagName) {
    return "relcell".equals(cellTagName) || "relcolspec".equals(cellTagName);
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#getColSpan(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public Integer getColSpan(AuthorElement cellElement) {
    Integer toReturn = null;
    if (calsProvider != null) {
      toReturn = calsProvider.getColSpan(cellElement);
    } 
    return toReturn;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#getRowSpan(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public Integer getRowSpan(AuthorElement cellElement) {
    Integer toReturn = null;
    if (calsProvider != null) {
      toReturn = calsProvider.getRowSpan(cellElement);
    } 
    return toReturn;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#hasColumnSpecifications(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean hasColumnSpecifications(AuthorElement tableElement) {
    boolean toReturn = true;
    if (calsProvider != null) {
      toReturn = calsProvider.hasColumnSpecifications(tableElement);
    } 
    return toReturn;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    String toReturn = null;
    if (calsProvider != null) {
      toReturn = calsProvider.getDescription();
    } else {
      toReturn = "Provides information about cells in DITA simple tables";
    }
    return toReturn;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#getCellWidth(ro.sync.ecss.extensions.api.node.AuthorElement, int, int)
   */
  @Override
  public List<WidthRepresentation> getCellWidth(AuthorElement cellElement, int colNumberStart, int colSpan) {
    List<WidthRepresentation> toReturn = null;
    if (calsProvider != null) {
      toReturn = calsProvider.getCellWidth(cellElement, colNumberStart, colSpan);
    } else {
      // The table is a DITA simpletable.
      if (colNumberStart < columnWidths.size()) {
        toReturn = new ArrayList<WidthRepresentation>();
        toReturn.add(columnWidths.get(colNumberStart));
      }
    }
    return toReturn;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#commitColumnWidthModifications(AuthorDocumentController, ro.sync.ecss.extensions.api.WidthRepresentation[], java.lang.String)
   */
  @Override
  public void commitColumnWidthModifications(AuthorDocumentController authorDocumentController,
      WidthRepresentation[] colWidths, String tableCellsTagName) throws AuthorOperationException {
    if (isSimpleTableCell(tableCellsTagName)) {
      columnWidths.clear();
      StringBuilder newWidth = new StringBuilder();
      for (int i = 0; i < colWidths.length; i++) {
        columnWidths.add(colWidths[i]);
        newWidth.append(colWidths[i].getRelativeWidth()).append("*"); 
        if (i != colWidths.length - 1) {
          newWidth.append(" ");
        }
      }
      authorDocumentController.setAttribute(
          ATTRIBUTE_NAME_RELCOLWIDTH, 
          newWidth.length() > 0 ? new AttrValue(newWidth.toString()) : null,
              tableElement);
    } else {
      calsProvider.commitColumnWidthModifications(authorDocumentController, colWidths, tableCellsTagName);
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#commitTableWidthModification(AuthorDocumentController, int, java.lang.String)
   */
  @Override
  public void commitTableWidthModification(AuthorDocumentController authorDocumentController, int newTableWidth, String tableCellsTagName) throws AuthorOperationException {
    // Delegate to CALS provider, DITA simpletables don't accept width attribute.
    if (calsProvider != null) {
      calsProvider.commitTableWidthModification(
          authorDocumentController, newTableWidth, tableCellsTagName);
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#getTableWidth(java.lang.String)
   */
  @Override
  public WidthRepresentation getTableWidth(String tableCellsTagName) {
    // Delegate to CALS provider, DITA simpletables don't accept width attribute.
    if (calsProvider != null) {
      return calsProvider.getTableWidth(tableCellsTagName);
    }
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isTableAcceptingWidth(java.lang.String)
   */
  @Override
  public boolean isTableAcceptingWidth(String tableCellsTagName) {
    return false;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isTableAndColumnsResizable(java.lang.String)
   */
  @Override
  public boolean isTableAndColumnsResizable(String tableCellsTagName) {
    boolean toReturn = false;
    if(isSimpleTableCell(tableCellsTagName)){
      toReturn = true;
    } else if(isRelTableCell(tableCellsTagName)){
      toReturn = false;
    } else if (calsProvider != null) {
      toReturn = calsProvider.isTableAndColumnsResizable(tableCellsTagName);
    }
    return toReturn;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isAcceptingFixedColumnWidths(java.lang.String)
   */
  @Override
  public boolean isAcceptingFixedColumnWidths(String tableCellsTagName) {
    boolean toReturn = false;
    if(isSimpleTableCell(tableCellsTagName)){
      toReturn = false;
    } else if(isRelTableCell(tableCellsTagName)){
      toReturn = false;
    } else if (calsProvider != null) {
      toReturn = calsProvider.isAcceptingFixedColumnWidths(tableCellsTagName);
    } 
    return toReturn;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isAcceptingPercentageColumnWidths(java.lang.String)
   */
  @Override
  public boolean isAcceptingPercentageColumnWidths(String tableCellsTagName) {
    boolean toReturn = false;
    if(isSimpleTableCell(tableCellsTagName)){
      toReturn = false;
    } else if(isRelTableCell(tableCellsTagName)){
      toReturn = false;
    } else  if (calsProvider != null) {
      toReturn = calsProvider.isAcceptingPercentageColumnWidths(tableCellsTagName);
    } 
    return toReturn;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isAcceptingProportionalColumnWidths(java.lang.String)
   */
  @Override
  public boolean isAcceptingProportionalColumnWidths(String tableCellsTagName) {
    boolean toReturn = false;
    if(isSimpleTableCell(tableCellsTagName)){
      toReturn = true;
    } else if(isRelTableCell(tableCellsTagName)){
      toReturn = false;
    } else if (calsProvider != null) {
      toReturn = calsProvider.isAcceptingProportionalColumnWidths(tableCellsTagName);
    }
    return toReturn;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProviderBase#getAllColspecWidthRepresentations()
   */
  @Override
  public List<WidthRepresentation> getAllColspecWidthRepresentations() {
    List<WidthRepresentation> toReturn = null;
    if (calsProvider != null) {
      toReturn = calsProvider.getAllColspecWidthRepresentations();
    } else {
      toReturn = columnWidths;
    }
    return toReturn;
  }
}