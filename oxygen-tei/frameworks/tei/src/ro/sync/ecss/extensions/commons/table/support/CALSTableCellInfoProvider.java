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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.text.BadLocationException;




import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSepProvider;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.AuthorTableColumnWidthProviderBase;
import ro.sync.ecss.extensions.api.WidthRepresentation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.operations.cals.CALSConstants;

/**
 * Provides informations about the cell spanning and column width for Docbook CALS tables. 
 */

public class CALSTableCellInfoProvider extends AuthorTableColumnWidthProviderBase implements AuthorTableCellSpanProvider, CALSConstants, AuthorTableCellSepProvider {
  /**
   * The default width representation.
   * PUBLIC BECAUSE IT WAS USED IN OLDER VERSIONS AS API.
   */
  public static final WidthRepresentation DEFAULT_WIDTH_REPRESENTATION = new WidthRepresentation(0, null, 1, false) {
    @Override
    public boolean isSpecified() {
      return false;
    }
  }; 
  /**
   * CALS Docbook table cell name.
   */
  private static final String CALS_DOCBOOK_CELL_NAME = "entry";
  
  /**
   * The map between the <code>CALSColSpec</code> containing information about 
   * the columns specification for this table and the corresponding author element. 
   */
  private Map<CALSColSpec, AuthorElement> colspecInfosMap = new TreeMap<CALSColSpec, AuthorElement>(new ColspecComparator());
  
  /**
   * The list with the <code>CALSColSpanSpec</code> containing 
   * information about the columns span specification for this table. 
   */
  protected List<CALSColSpanSpec> spanspecInfos = new ArrayList<CALSColSpanSpec>();

  /**
   * The author element associated width the CALS table.
   */
  private AuthorElement tableElement;
  
  /**
   * The prefix for generated colspec names
   */
  private static final String COLSPEC_NAME_PREFIX = "c";
  
  /**
   * The default visibility for the row and column separators. For DITA they 
   * are hidden, for Docbook are visible. 
   */
  private boolean colsepAndRowSepAreVisibleByDefault = false;
  
  /**
   * Compare two table column specifications by index.
   */
  private static class ColspecComparator implements Comparator {
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Object obj1, Object obj2) {
      int compRes = -1;
      if (obj1 instanceof CALSColSpec && 
          obj2 instanceof CALSColSpec) {
        CALSColSpec colSpec1 = (CALSColSpec) obj1;
        CALSColSpec colSpec2 = (CALSColSpec) obj2;
        compRes = colSpec1.getColumnNumber() - colSpec2.getColumnNumber();
      }
      return compRes;
    }    
  }
  
  /**
   * Constructor.
   * 
   * @param colsepAndRowSepAreVisibleByDefault The default visibility for the rowsep and 
   * colsep. (i.e. if no <code>colsep</code> or <code>rowsep</code> attributes are present 
   * in the table).
   */
  public CALSTableCellInfoProvider(boolean colsepAndRowSepAreVisibleByDefault) {
    this.colsepAndRowSepAreVisibleByDefault  = colsepAndRowSepAreVisibleByDefault;    
  }

  /**
   * Constructor. The default visibility for the rowsep and 
   * colsep. (i.e. if no <code>colsep</code> or <code>rowsep</code> attributes are present 
   * in the table) is hidden.
   */
  public CALSTableCellInfoProvider() {
    this(false);    
  }

  /**
   * Compute the number of columns the cell spans across by looking 
   * at the 'spanspec' attribute. In case the 'spanspec' attribute is missing 
   * then the column span is defined by the 'namest' and 'nameend' attribute.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#getColSpan(AuthorElement)
   */
  @Override
  public Integer getColSpan(AuthorElement cellElem) {
    Integer colspan = null;
    AttrValue attrValue = cellElem.getAttribute(ATTRIBUTE_NAME_SPANNAME);
    if (attrValue != null) {
      // The col span is specified through a spanspec.
      CALSColSpanSpec spanSpec = getSpanSpec(attrValue.getValue());
      if(spanSpec != null) {
        colspan = getColSpan(spanSpec.getStartColumnName(), spanSpec.getEndColumnName());          
      }
    } else {
      AttrValue namestValue = cellElem.getAttribute(ATTRIBUTE_NAME_NAMEST);
      AttrValue nameendValue = cellElem.getAttribute(ATTRIBUTE_NAME_NAMEEND);
      if (namestValue != null && nameendValue != null
          && namestValue.getValue() != null && nameendValue.getValue() != null) {
        // The colspan is specified by the name of the 2 columns.
        colspan = getColSpan(namestValue.getValue(), nameendValue.getValue());
      }
    }
    return colspan;
  }
  
  /**
   * Compute the column span number for given column start and end names.
   * 
   * @param namest The start span column name.
   * @param nameend The end span column name.
   * 
   * @return The column span numbver or <code>null</code> if it cannot be computed.
   */
  private Integer getColSpan(String namest, String nameend) {
    Integer colspan = null;
    int startIndex = -1;
    int endIndex = -1; 
    Set<CALSColSpec> colspecs = colspecInfosMap.keySet();
    for (Iterator iterator = colspecs.iterator(); iterator.hasNext();) {
      CALSColSpec colspec = (CALSColSpec) iterator.next();
      if (namest.equals(colspec.getColumnName())) {
        startIndex = colspec.getColumnNumber();
      } 
      if (nameend.equals(colspec.getColumnName())) {
        endIndex = colspec.getColumnNumber();
      }
    }
    if (startIndex != -1 && endIndex != -1) {
      colspan = Integer.valueOf(Math.abs(endIndex - startIndex)  + 1);
    }
    return colspan;
  }

  /**
   * Compute the number of rows the cells span across by looking at the <code>morerows</code> attribute.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#getRowSpan(AuthorElement)
   */
  @Override
  public Integer getRowSpan(AuthorElement cellElement) {
    Integer span = null;
    AttrValue val = cellElement.getAttribute("morerows"); 
    String cs = val != null ? val.getValue() : null;
    if (cs != null) {
      try {
        int intVal = Integer.parseInt(cs);
        if (intVal >= 1) {
          span = Integer.valueOf(intVal + 1);
        }
      } catch (NumberFormatException ex) {
        // Not a number.
      }
    }      

    return span;
  }
  
  /**
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#init(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public void init(AuthorElement tableElement) {
    this.tableElement = tableElement;
    colspecInfosMap.clear();
    spanspecInfos.clear();
    int colspecIndex = 0;
    List<AuthorNode> children = tableElement.getContentNodes();
    int colSpecNumber = 0;
    for (Iterator<AuthorNode> iterator = children.iterator(); iterator.hasNext();) {
      AuthorNode node = iterator.next();
      if (node instanceof AuthorElement) { 
        AuthorElement child = (AuthorElement) node;
        if (ELEMENT_NAME_COLSPEC.equals(child.getLocalName())) {
          boolean colNumberSpecified = false;
          // "colnum" attribute
          AttrValue attrValue = child.getAttribute(ATTRIBUTE_NAME_COLNUM);
          int currentColIndex = -1;
          if (attrValue != null) {
            try {
              currentColIndex = Integer.parseInt(attrValue.getValue());
            } catch (NumberFormatException nfe) {
              // Not a number.
            }
          }

          if (currentColIndex != -1) {
            colSpecNumber = currentColIndex;
            colNumberSpecified = true;
          } else { 
            colSpecNumber++;
          }

          // "colname" attribute
          attrValue = child.getAttribute(ATTRIBUTE_NAME_COLNAME);
          String colspecName = null;
          if (attrValue != null) {
            colspecName = attrValue.getValue();
          }

          // "colwidth" attribute
          attrValue = child.getAttribute(ATTRIBUTE_NAME_COLWIDTH);
          String colWidth = null;
          if (attrValue != null) {
            colWidth = attrValue.getValue();
          }
          
          //Align attribute
          attrValue = child.getAttribute(ATTRIBUTE_NAME_ALIGN);
          String textAlign = null;
          if (attrValue != null) {
            textAlign = attrValue.getValue();
          }

          // "colsep" attribute
          attrValue = child.getAttribute(ATTRIBUTE_NAME_COLSEP);
          Boolean colsep = null;
          if (attrValue != null) {
            colsep = "1".equals(attrValue.getValue());
          }

          // "rowsep" attribute
          attrValue = child.getAttribute(ATTRIBUTE_NAME_ROWSEP);
          Boolean rowsep = null;
          if (attrValue != null) {
            rowsep = "1".equals(attrValue.getValue());
          }

          CALSColSpec cs = new CALSColSpec(colspecIndex, colSpecNumber, colNumberSpecified, colspecName, colWidth, colsep, rowsep);
          cs.setAlign(textAlign);
          colspecInfosMap.put(cs, child);
          colspecIndex ++;
        } else if (ELEMENT_NAME_SPANSPEC.equals(child.getLocalName())) {
          String spanName = null;
          String namest = null;
          String nameend = null;
          AttrValue attrValue = child.getAttribute(ATTRIBUTE_NAME_SPANNAME);
          if (attrValue != null) {
            spanName = attrValue.getValue();
          }
          attrValue = child.getAttribute(ATTRIBUTE_NAME_NAMEST);
          if (attrValue != null) {
            namest = attrValue.getValue();
          }
          attrValue = child.getAttribute(ATTRIBUTE_NAME_NAMEEND);
          if (attrValue != null) {
            nameend = attrValue.getValue();
          }
          if (spanName != null && namest != null && nameend != null) {
            spanspecInfos.add(new CALSColSpanSpec(spanName, namest, nameend));
          }
        }
      }
    }
  } 

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Provides information about the cells for a DocBook or DITA table.";
  }
  
  /**
   * Find a column span specification by name.
   * 
   * @param spanSpecName The name of the column span specification.
   * @return  The column span specification or <code>null</code> 
   * if no column span specification defined for the given name.
   */
  private CALSColSpanSpec getSpanSpec(String spanSpecName) {
    CALSColSpanSpec spanSpec = null;
    for (Iterator<CALSColSpanSpec> iterator = spanspecInfos.iterator(); iterator.hasNext();) {
      CALSColSpanSpec currentSpanSpec = iterator.next();
      if (spanSpecName.equals(currentSpanSpec.getSpanName())) {
        spanSpec = currentSpanSpec;          
        break;
      }
    }
    return spanSpec;
  }
  
  /**
   * Find the column span specification for a table cell.
   * 
   * If 'spanname' attribute is present the corresponding span specification will be returned.
   * Otherwise a new span specification will be returned looking at the name of columns
   * spanned by the cell.
   *  
   * @param authorAccess The author access. 
   * @param cellElement The table cell element.
   * @return The cell span specification. <code>Null</code> when column specifications are 
   * not defined.
   */
  public CALSColSpanSpec getCellSpanSpec(
      AuthorAccess authorAccess, 
      AuthorElement cellElement) {
    CALSColSpanSpec spanSpec = null;

    // Test if 'spanname' attribute is present
    AttrValue spanSpecAttr = 
      cellElement.getAttribute(ATTRIBUTE_NAME_SPANNAME);
    if (spanSpecAttr != null) {
      spanSpec = getSpanSpec(spanSpecAttr.getValue());
    } else {
      // Test if 'namest' and 'nameend' attribute are present 
      AttrValue startColAttr = 
        cellElement.getAttribute(ATTRIBUTE_NAME_NAMEST);
      AttrValue endColAttr = 
        cellElement.getAttribute(ATTRIBUTE_NAME_NAMEEND);

      if (startColAttr != null && endColAttr != null) {
        spanSpec = new CALSColSpanSpec(null, startColAttr.getValue(), endColAttr.getValue());
      } else {
        CALSColSpec colSpec = getColumnSpec(authorAccess, cellElement);
        if (colSpec != null) {
          spanSpec = 
            new CALSColSpanSpec(
                null, 
                colSpec.getColumnName(), 
                colSpec.getColumnName());
        }
      }
    }
    return spanSpec;
  }
  
  /**
   * Find the column specification for a table cell.
   * 
   * @param authorAccess The author access.
   * @param cellElement The table cell element.
   * @return The column specification or <code>null</code> if no column specification was defined 
   * for the given cell.
   */
  CALSColSpec getColumnSpec(AuthorAccess authorAccess, AuthorElement cellElement) {
    CALSColSpec colSpec = null;

    // First, test if 'colname' attribute is present
    AttrValue attrValue = cellElement.getAttribute(ATTRIBUTE_NAME_COLNAME);
    if (attrValue != null) {
      colSpec = getColSpec(attrValue.getValue());
    } else {
      // Compute the index of cell
      int[] cellIndex = authorAccess.getTableAccess().getTableCellIndex(cellElement);
      if (cellIndex != null) {
        colSpec = getColSpec(cellIndex[1] + 1);
      }
    }
    return colSpec;
  }
  
  
  /**
   * Find the column specification for a table cell, either by the column name specified in the 
   * element attributes, or the column index, as fallback.
   * 
   * @param cellElement The table cell element. 
   * @param columnIndex The index of the column. (used only when there is no colname on the element.)
   * @return The column specification or <code>null</code> if no column specification was defined 
   * for the given cell.
   */
  private CALSColSpec getColumnSpec(AuthorElement cellElement, int columnIndex) {
    CALSColSpec colSpec = null;
    // First, test if 'colname' attribute is present
    AttrValue attrValue = cellElement.getAttribute(ATTRIBUTE_NAME_COLNAME);
    if (attrValue != null) {
      colSpec = getColSpec(attrValue.getValue());
    } else {
      colSpec = getColSpec(columnIndex);
    }
    return colSpec;
  }

  /**
   * Find a column specification by name.
   * 
   * @param colSpecName The name of column specification.
   * @return The column specification or <code>null</code> if no column specification is defined for 
   * the given colspec name.
   */
  private CALSColSpec getColSpec(String colSpecName) {
    CALSColSpec colSpec = null;
    Set<CALSColSpec> colspecs = colspecInfosMap.keySet();
    for (Iterator<CALSColSpec> iterator = colspecs.iterator(); iterator.hasNext();) {
      CALSColSpec currentColSpec = iterator.next();
      if (colSpecName != null && colSpecName.equals(currentColSpec.getColumnName())) {
        colSpec = currentColSpec;          
        break;
      }
    }
    return colSpec;
  }

  /**
   * Find the column specification for the given column number.
   * 
   * @param columnNumber The column number, one based.
   * @return The column specification or <code>null</code> if no column specification is defined for 
   * the given column number. 1 based.
   */
  public CALSColSpec getColSpec(int columnNumber) {
    CALSColSpec colSpec = null;
    Set<CALSColSpec> colspecs = colspecInfosMap.keySet();
    for (Iterator<CALSColSpec> iterator = colspecs.iterator(); iterator.hasNext();) {
      CALSColSpec currentColSpec = iterator.next();
      if (columnNumber == currentColSpec.getColumnNumber()) {
        colSpec = currentColSpec;          
        break;
      }
    }
    return colSpec;
  }
  
  /**
   * Find a column specification element.
   * 
   * @param colspec The column specification.
   * @return The column specification element or <code>null</code> if no column 
   * corresponds to the given specification.
   */
  public AuthorElement getColSpecElement(CALSColSpec colspec) {
    return colspecInfosMap.get(colspec);
  }
  
  /**
   * Returns the column specification set corresponding to the CALS table. 
   * The list is ordered ascending by the column specification index ('colnum' attribute).
   * 
   * @return The column specifications set.
   */
  public Set<CALSColSpec> getColSpecs() {   
    return colspecInfosMap.keySet();
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#hasColumnSpecifications(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean hasColumnSpecifications(AuthorElement tableElement) {
    return !colspecInfosMap.isEmpty();
  }
  
  /**
   * The list with the width representations for the given cell is obtained by 
   * computing the column span and then determining the {@link WidthRepresentation}
   * for each column the cell spans across.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#getCellWidth(ro.sync.ecss.extensions.api.node.AuthorElement, int, int)
   */
  @Override
  public List<WidthRepresentation> getCellWidth(AuthorElement cellElement, int colNumberStart, int colSpan) {
    // Init the list of widths to return 
    List<WidthRepresentation> toReturn = null;
    AttrValue attrValue = cellElement.getAttribute(ATTRIBUTE_NAME_SPANNAME);
    String startColumnName = null;
    String endColumnName = null;

    if (attrValue != null) {
      // The col span is specified through a spanspec.
      CALSColSpanSpec spanSpec = getSpanSpec(attrValue.getValue());
      if(spanSpec != null) {
        startColumnName = spanSpec.getStartColumnName();
        endColumnName = spanSpec.getEndColumnName();
      }
    } else {
      AttrValue namestValue = cellElement.getAttribute(ATTRIBUTE_NAME_NAMEST);
      AttrValue nameendValue = cellElement.getAttribute(ATTRIBUTE_NAME_NAMEEND);
      if (namestValue != null && nameendValue != null) {
        // The colspan is specified by the name of the 2 columns.
        startColumnName = namestValue.getValue();
        endColumnName = nameendValue.getValue();
      }
    }
    if (startColumnName == null && endColumnName == null) {
      // First, test if 'colname' attribute is present
      attrValue = cellElement.getAttribute(ATTRIBUTE_NAME_COLNAME);
      if (attrValue != null) {
        startColumnName = endColumnName = attrValue.getValue();
      }
    }
    
    // If the start-column and end-column were determined then the combined
    // width will be determined
    if (startColumnName != null && endColumnName != null) {
      boolean start = false;
      Set<CALSColSpec> colspecs = colspecInfosMap.keySet();
      for (Iterator iterator = colspecs.iterator(); iterator.hasNext();) {
        CALSColSpec colspec = (CALSColSpec) iterator.next();
        if (startColumnName.equals(colspec.getColumnName())) {
          start = true;
        } 
        if(start) {
          WidthRepresentation colWidth = colspec.getColWidth();
          colWidth = (colWidth == null) ? DEFAULT_WIDTH_REPRESENTATION : colWidth;
          
          if(colspec.getAlign() != null && colWidth == DEFAULT_WIDTH_REPRESENTATION) {
            //Pass the text align, do not alter the constant.
            colWidth = new WidthRepresentation(0, null, 1, false) {
              /**
               * @see ro.sync.ecss.extensions.api.WidthRepresentation#isSpecified()
               */
              @Override
              public boolean isSpecified() {
                return false;
              }
            };
            colWidth.setAlign(colspec.getAlign());
          }
          
          if (toReturn == null) {
            toReturn = new ArrayList<WidthRepresentation>();
          }
          toReturn.add(colWidth);
        }
        if (endColumnName.equals(colspec.getColumnName())) {
          break;
        }
      }
    } else if (colNumberStart >= 0){
      // We have to find the colspec that has colnum equivalent with the given 
      // colNumberStart
      int columnNumber = colNumberStart + 1;
      Set<CALSColSpec> colspecs = colspecInfosMap.keySet();
      for (Iterator iterator = colspecs.iterator(); iterator.hasNext();) {
        CALSColSpec colspec = (CALSColSpec) iterator.next();
        if (colspec.getColumnNumber() == columnNumber) {
          WidthRepresentation colWidth = colspec.getColWidth();
          colWidth = (colWidth == null) ? DEFAULT_WIDTH_REPRESENTATION : colWidth;
          
          if(colspec.getAlign() != null && colWidth == DEFAULT_WIDTH_REPRESENTATION) {
            //Pass the text align, do not alter the constant.
            colWidth = new WidthRepresentation(0, null, 1, false) {
              /**
               * @see ro.sync.ecss.extensions.api.WidthRepresentation#isSpecified()
               */
              @Override
              public boolean isSpecified() {
                return false;
              }
            };
            colWidth.setAlign(colspec.getAlign());
          }
          
          if (toReturn == null) {
            toReturn = new ArrayList<WidthRepresentation>();
          }
          toReturn.add(colWidth);
        }
      }
    }

    return toReturn;
  }
  
  /**
   * Updates the columns width specifications in the source document by
   * setting the <code>colwidth</code> attribute value of the
   * <code>colspec</code> elements and by adding new <code>colspec</code>
   * elements if needed.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#commitColumnWidthModifications(AuthorDocumentController, ro.sync.ecss.extensions.api.WidthRepresentation[], java.lang.String)
   */
  @Override
  public void commitColumnWidthModifications(AuthorDocumentController authorDocumentController,
      WidthRepresentation[] colWidths, String tableCellsTagName) throws AuthorOperationException {
    if (isTableCell(tableCellsTagName)) {
      if (colWidths != null && authorDocumentController != null && tableElement != null) {
        int currentOffset = tableElement.getStartOffset() + 1;
        try {
          for (int i = 0; i < colWidths.length; i++) {
            WidthRepresentation colWidth = colWidths[i];
            int colNumber = i + 1;
            CALSColSpec colSpec = getColSpec(colNumber);
            AuthorElement currentElem = null;
            if (colSpec == null) {
              // Create a new colspec for the given cell element
              String colname = getUniqueColumnName(colNumber); 
              colSpec = new CALSColSpec(i, colNumber, true, colname, colWidth);
              String xmlFragment = colSpec.createXMLFragment(tableElement.getNamespace());
              authorDocumentController.insertXMLFragment(xmlFragment, currentOffset);
              currentElem = (AuthorElement) authorDocumentController.getNodeAtOffset(currentOffset + 1);
              colspecInfosMap.put(colSpec, currentElem);
            } else {
              colSpec.setColWidth(colWidth);
              String strRepresentation = colWidth.getWidthRepresentation();
              AttrValue val = null;
              if (strRepresentation != null) {
                val = new AttrValue(colWidth.getWidthRepresentation());
              }
              currentElem = colspecInfosMap.get(colSpec);
              authorDocumentController.setAttribute(
                  ATTRIBUTE_NAME_COLWIDTH, 
                  val, currentElem);
            }
            currentOffset = currentElem.getEndOffset() + 1;
          }
        } catch (AuthorOperationException e) {
          throw e;
        } catch (BadLocationException e) {
          throw new AuthorOperationException(e.getMessage(), e);
        }
      }
    }
  }

  /**
   * Check if the name of an element is a table cell.
   * 
   * @param tableCellsTagName  The name of an element.
   * @return <code>true</code> if the name of an element is a table cell.
   */
  protected boolean isTableCell(String tableCellsTagName) {
    return CALS_DOCBOOK_CELL_NAME.equals(tableCellsTagName);
  }

  /**
   * Get an unique name for column with number <code>colNumber</code>
   * 
   * @param colNumber The column number
   * @return A name for column.
   */
  private String getUniqueColumnName(int colNumber) {
    // EXM-14589 Avoid conflicts on change colname
    Set<CALSColSpec> colSpecs = getColSpecs();
    List<String> columnNames = new ArrayList<String>();
    for (CALSColSpec colSpec : colSpecs) {
      // Current column name
      String columnName = colSpec.getColumnName();
      if (columnName != null) {
        // Retain the column name
        columnNames.add(columnName);
      }
    }
    StringBuilder columnName = new StringBuilder();
    columnName.append(COLSPEC_NAME_PREFIX).append(colNumber);
    boolean isUnique = false;
    while (!isUnique) {
      // Check if the column name is already in use
      if (columnNames.contains(columnName.toString())) {
        // Not unique, try another
        columnName.append(colNumber);
      } else {
        // We have found an unique name for this column
        isUnique = true;
      }
    }
    return columnName.toString();
  }

  /**
   * The element associated with the CALS table is the <code>tgroup</code> element.
   * This method is used to find the <code>table</code> element 
   * (parent of the <code>tgroup</code> element)
   * for getting and setting the "width" attribute.
   *    
   * @return The table associated author element.
   */
  private AuthorElement getTableElement() {
    AuthorElement tblElem = tableElement;
    if (tblElem != null) {
      // Find the element named "table" if any.
      while(true) {
        if (ELEMENT_NAME_TABLE.equals(tblElem.getName())) {
          break;
        } else {
          AuthorNode parent = tblElem.getParent();
          if (parent != null && parent instanceof AuthorElement) {
            tblElem = (AuthorElement) parent;
          } else {
            tblElem = null;
            break;
          }
        }
      }
    }
    
    return tblElem;
  }

  /**
   * Sets the <code>width</code> attribute value of the <code>table</code> element.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#commitTableWidthModification(AuthorDocumentController, int, java.lang.String)
   */
  @Override
  public void commitTableWidthModification(AuthorDocumentController authorDocumentController, int newTableWidth, String tableCellsTagName) throws AuthorOperationException {
    if (isTableCell(tableCellsTagName)) {
      AuthorElement tblElem = getTableElement();
      if (newTableWidth > 0 && authorDocumentController != null) {
        if (tblElem != null) {
          String newWidth = String.valueOf(newTableWidth);

          authorDocumentController.setAttribute(
              ATTRIBUTE_NAME_TABLE_WIDTH, 
              new AttrValue(newWidth),
              tblElem);
        } else {
          throw new AuthorOperationException("Cannot find the element representing the table.");
        }
      }
    }
  }
  
  /**
   * Returns the {@link WidthRepresentation} obtained by analyzing the
   * <code>width</code> attribute value of the <code>table</code> element.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#getTableWidth(java.lang.String)
   */
  @Override
  public WidthRepresentation getTableWidth(String tableCellsTagName) {
    WidthRepresentation toReturn = null;
    if (isTableCell(tableCellsTagName)) {
      toReturn = getTableWidth();
    }
    return toReturn;
  }
  
  /**
   * The DocBook CALS tables do not accept width specification.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isTableAcceptingWidth(java.lang.String)
   */
  @Override
  public boolean isTableAcceptingWidth(String tableCellsTagName) {
    return false;
  }
  
  /**
   * Create a table width representation.
   * 
   * @return The width representation of the parent element named "table".
   */
  private WidthRepresentation getTableWidth() {
    WidthRepresentation toReturn = null;
    AuthorElement tblElem = getTableElement();
    if (tblElem != null) {
      AttrValue widthAttr = tblElem.getAttribute(ATTRIBUTE_NAME_TABLE_WIDTH);
      if (widthAttr != null) {
         String width = widthAttr.getValue();
         if (width != null) {
           toReturn = new WidthRepresentation(width, true);
         }
      }
    }
    return toReturn;
  }
  
  /**
   * It returns <code>true</code> only if the given table cells tag name is
   * equal to <code>'entry'</code>.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isTableAndColumnsResizable(java.lang.String)
   */
  @Override
  public boolean isTableAndColumnsResizable(String tableCellsTagName) {
    return isTableCell(tableCellsTagName);
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isAcceptingFixedColumnWidths(java.lang.String)
   */
  @Override
  public boolean isAcceptingFixedColumnWidths(String tableCellsTagName) {
    return isTableCell(tableCellsTagName);
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isAcceptingPercentageColumnWidths(java.lang.String)
   */
  @Override
  public boolean isAcceptingPercentageColumnWidths(String tableCellsTagName) {
    return false;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isAcceptingProportionalColumnWidths(java.lang.String)
   */
  @Override
  public boolean isAcceptingProportionalColumnWidths(String tableCellsTagName) {
    return isTableCell(tableCellsTagName);
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProviderBase#getAllColspecWidthRepresentations()
   */
  @Override
  public List<WidthRepresentation> getAllColspecWidthRepresentations() {
    List<WidthRepresentation> toReturn = new ArrayList<WidthRepresentation>();
    Iterator<CALSColSpec> iter = colspecInfosMap.keySet().iterator();
    while(iter.hasNext()) {
      CALSColSpec cs = iter.next();
      WidthRepresentation cw = cs.getColWidth();
      if(cw != null) {
        toReturn.add(cw);
      } else {
        //1*
        toReturn.add(new WidthRepresentation(0, null, 1f, false));
      }
    }
    
    //See https://www.oasis-open.org/specs/a502.htm
    //Maybe we have more columns that colspecs.
    AuthorElement tblElem = getTableElement();
    if (tblElem != null) {
      AttrValue colNumAttrVal = tblElem.getAttribute(ATTRIBUTE_NAME_COLNUM);
      if (colNumAttrVal != null && colNumAttrVal.getValue() != null) {
        try {
          int colNum = Integer.parseInt(colNumAttrVal.getValue());
          int delta = colNum - toReturn.size();
          //Add them with 1* proportional size
          if(delta > 0) {
            for (int i = 0; i < delta; i++) {
              toReturn.add(new WidthRepresentation(0, null, 1f, false));
            }
          }
        } catch (NumberFormatException e) {
          // Nothing to do
        }
      }
    }
    return toReturn;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSepProvider#getColSep(ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public boolean getColSep(AuthorElement cellElem, int columnIndex) {
    
    Boolean desc[] = getColSepOrRowSepFromAttributes(cellElem, true);
    Boolean colsep = desc[0];
    Boolean colsepFromTable = desc[1];
    
    if (colsep == null || colsepFromTable) {
      // Not found in the attributes or given by the table element.
      // The table element is weaker than the column specification.
      CALSColSpec colspec = getColumnSpec(cellElem, columnIndex);
      if (colspec != null && colspec.getColSep() != null) {
          colsep = colspec.getColSep();
      }
    }

    if (colsep == null) {
      // Docbook and DITA defaults differs.
      colsep = colsepAndRowSepAreVisibleByDefault;
    }
    
    return colsep;  
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSepProvider#getRowSep(ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public boolean getRowSep(AuthorElement cellElem, int columnIndex) {
    Boolean desc[] = getColSepOrRowSepFromAttributes(cellElem, false);
    Boolean rowsep = desc[0];
    Boolean rowsepFromTable = desc[1];
        
    if (rowsep == null || rowsepFromTable) {
      // Not found in the attributes or given by the table element.
      // The table element is weaker than the column specification.
      CALSColSpec colspec = getColumnSpec(cellElem, columnIndex);
      if (colspec != null && colspec.getRowSep() != null) {
          rowsep = colspec.getRowSep();
      }
    }
    
    if (rowsep == null) {
      // Docbook and DITA defaults differs.
      rowsep = colsepAndRowSepAreVisibleByDefault;
    }
    
    return rowsep;  
  }
  
  /**
   * Scans the hierarchy up to the table element, and finds the closest 
   * definition for the rowsep or colsep.
   * 
   * @param cellElem The cell element.
   * @param needingColSep <code>true</code> if the <code>colsep</code> is needed, 
   * <code>false</code> for the <code>rowsep</code>.
   * @return an array with two elements, on the first the <code>colsep</code> or <code>rowsep </code>
   * and on the second position <code>true</code> if it was collected from 
   * the table structure: the CALS <code>table</code> or <code>tgroup</code> element. The second 
   * position is never <code>null</code>.
   */
  private Boolean[] getColSepOrRowSepFromAttributes(AuthorElement cellElem, boolean needingColSep) {
    Boolean separator = null;
    boolean fromTable = false;
    
    // Try from cell or its parents, up to the table.
    AuthorNode current = cellElem;
    while (current instanceof AuthorElement) {
      AuthorElement element = (AuthorElement) current;
      
      boolean isTableElement = isTableElement(element);
      boolean isTgroupElement = isTgroupElement(element);
      
      AttrValue attr;     
      if (needingColSep) {
        // Colsep
        attr = element.getAttribute(ATTRIBUTE_NAME_COLSEP);
      } else {
        // Rowsep
        attr = element.getAttribute(ATTRIBUTE_NAME_ROWSEP);
      }
      
      if(attr != null) {
        separator = "1".equals(attr.getValue());
        fromTable = isTableElement || isTgroupElement;
        break;
      }
      
      if (isTableElement) {
        // Analyzed all parents up to the table, including the parent of 
        // the element with table layout. It may happen that the 'table' 
        // to be a block, while the 'tgroup' is table. We are interested also in
        // the attributes of the parent. 
        
        // Stop now.
        break;
      } else {
        current = current.getParent();
      }      
    }
    return new Boolean[] {separator, fromTable};
  }

  /**
   * Check if this element is a <code>table</code> element.
   * 
   * @param element The analyzed element.
   * @return <code>true</code> if this element is a CALS <code>table</code> element.
   */
  protected boolean isTableElement(AuthorElement element) {
    return element == tableElement.getParent() ||       
        "table".equals(element.getLocalName());
  }
  
  /**
   * Check if this element is a <code>tgroup</code> element.
   * 
   * @param element The analyzed element.
   * @return <code>true</code> if this element is a CALS <code>tgroup</code> element.
   */
  protected boolean isTgroupElement(AuthorElement element) {
    return "tgroup".equals(element.getLocalName());
  }
}