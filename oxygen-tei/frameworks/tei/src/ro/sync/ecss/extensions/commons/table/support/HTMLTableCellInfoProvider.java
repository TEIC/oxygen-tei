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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

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

/**
 * Provides information regarding HTML table cell span and column width.
 * Updates the table width modification and the column widths in the document 
 * and in the layout model.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class HTMLTableCellInfoProvider extends AuthorTableColumnWidthProviderBase implements AuthorTableCellSpanProvider {

  /**
   * Colgroup element name.
   * The value is <code>colgroup</code>
   * 
   */
  private static final String ELEMENT_NAME_COLGROUP = "colgroup";
  /**
   * Column element name.
   * The value is <code>col</code>
   */
  private static final String ELEMENT_NAME_COL = "col";
  /**
   * Table header element name.
   * The value is <code>thead</code>
   */
  private static final String ELEMENT_NAME_THEAD = "thead";
  /**
   * Table footer element name.
   * The value is <code>tfoot</code>
   */
  private static final String ELEMENT_NAME_TFOOT = "tfoot";
  /**
   * Table body element name.
   * The value is <code>tbody</code>
   */
  private static final String ELEMENT_NAME_TBODY= "tbody";
  /**
   * Span attribute name.
   * The value is <code>span</code>
   */
  private static final String ATTR_NAME_SPAN = "span";
  /**
   * Width attribute name.
   * The value is <code>width</code>
   */
  private static final String ATTR_NAME_WIDTH = "width";
  /**
   * Align attribute name.
   * The value is <code>align</code>
   */
  private static final String ATTR_NAME_ALIGN = "align";
  
  /**
   * HTML table cell name.
   * The value is <code>td</code>
   */
  private static final String HTML_CELL_NAME = "td";
  
  /**
   * HTML table row name.
   * The value is <code>tr</code>
   */
  private static final String HTML_ROW_NAME = "tr";
  
  /**
   * HTML table header cell name.
   * The value is <code>th</code>
   */
  private static final String HTML_HEADER_CELL_NAME = "th";
  
  /**
  * Logger for logging. 
  */
  private static Logger logger = Logger.getLogger(HTMLTableCellInfoProvider.class.getName());
  
  /**
   * The list with the {@link WidthRepresentation} for the table columns.
   */
  private List<WidthRepresentation> colWidthSpecs = new ArrayList<WidthRepresentation>();
  
  /**
   * The table element.
   */
  private AuthorElement tableElement;
  
  /**
   * Compute the number of columns the cell spans across by looking at the 
   * <code>colspan</code> attribute.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#getColSpan(AuthorElement)
   */
  @Override
  public Integer getColSpan(AuthorElement cellElement) {
    Integer colspan = null;
    AttrValue attrValue = cellElement.getAttribute("colspan");
    if (attrValue != null) {
      try {
        int value = Integer.parseInt(attrValue.getValue());
        colspan = Integer.valueOf(Math.max(value, 1));
      } catch(NumberFormatException nfe) {
        // Not a number.
      }
    }
    return colspan;
  }

  /**
   * Compute the number of rows the cell spans across by looking at the 
   * <code>rowspan</code> attribute.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#getRowSpan(AuthorElement)
   */
  @Override
  public Integer getRowSpan(AuthorElement cellElement) {
    Integer rowspan = null;
    AttrValue attrValue = cellElement.getAttribute("rowspan");
    if (attrValue != null) {
      try {
        int value = Integer.parseInt(attrValue.getValue());
        rowspan = Integer.valueOf(Math.max(value, 1));     
      } catch(NumberFormatException nfe) {
        // Not a number.
      }
    }
    return rowspan;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#init(AuthorElement)
   */
  @Override
  public void init(AuthorElement tableElement) {
    this.tableElement = tableElement;
    AuthorElement[] colGroupChildren = tableElement.getElementsByLocalName(ELEMENT_NAME_COLGROUP);
    if (colGroupChildren != null && colGroupChildren.length > 0) {
      for (int i = 0; i < colGroupChildren.length; i++) { 
        // Verify if the current table child is a 'colgroup' element. 
        AuthorElement child = colGroupChildren[i];
        // Determine the number of columns this colgroup spans over.
        // The colgroup span attribute (default value 1) specifies 
        // the number of columns in the group.
        AttrValue attrValue = child.getAttribute(ATTR_NAME_SPAN);
        int colgroupSpan = 1;
        if (attrValue != null) {
          try {
            colgroupSpan = Integer.parseInt(attrValue.getValue());
          } catch (NumberFormatException e) {
            if (logger.isDebugEnabled()) {
              logger.debug(e, e);
            } 
          }
        }

        // Determine if the colgroup specifies a colwidth for the columns it spans.
        attrValue = child.getAttribute(ATTR_NAME_WIDTH);
        String colgroupWidth = null;
        if (attrValue != null) {
          colgroupWidth = attrValue.getValue();
        }
        
        //Align
        String cgAlignValue = null;
        attrValue = child.getAttribute(ATTR_NAME_ALIGN);
        if (attrValue != null) {
          cgAlignValue = attrValue.getValue();
        }

        List colgroupChildren = child.getContentNodes();
        for (Iterator iterator2 = colgroupChildren.iterator(); iterator2.hasNext();) {
          AuthorNode cgChildNode = (AuthorNode) iterator2.next();
          // Iterate the children 'col' elements of a 'colgroup'.
          if (cgChildNode  instanceof AuthorElement) {
            AuthorElement cgChild = (AuthorElement) cgChildNode;
            if (ELEMENT_NAME_COL.equals(cgChild.getLocalName())) {

              // Invalidate the 'span' attribute value in the parent colgroup.
              colgroupSpan = -1;
              // Determine the 'width' for this col.
              AttrValue colWidthAttribute = cgChild.getAttribute(ATTR_NAME_WIDTH);
              String colWidth = null;
              if (colWidthAttribute != null) {
                colWidth = colWidthAttribute.getValue();
              } else if (colgroupWidth != null){
                // If the current col does not have a width specified use
                // the parent colgroup width.
                colWidth = colgroupWidth;
              }
              
              //Align
              String alignValue = cgAlignValue;
              attrValue = child.getAttribute(ATTR_NAME_ALIGN);
              if (attrValue != null) {
                alignValue = attrValue.getValue();
              }

              AttrValue colSpanAttribute = cgChild.getAttribute(ATTR_NAME_SPAN);
              int colSpan = 1;
              if (colSpanAttribute != null) {
                try {
                  colSpan = Integer.parseInt(colSpanAttribute.getValue());
                } catch (NumberFormatException e) {
                  if (logger.isDebugEnabled()) {
                    logger.debug(e, e);
                  } 
                }     
              }
              // Add ColWidth objects for the columns this 'col' specification spans over.
              for (int j = 0; j < colSpan; j ++) {
                WidthRepresentation widthRepresentation = new WidthRepresentation(colWidth, true);
                widthRepresentation.setAlign(alignValue);
                colWidthSpecs.add(widthRepresentation);
              }
            }
          }
        }

        // If the current colgroup didn't had any 'col' children
        // add ColWidth objects for the columns it spans over.
        if (colgroupSpan > 0) {
          for (int j = 0; j < colgroupSpan; j ++) {
            WidthRepresentation widthRepresentation = new WidthRepresentation(colgroupWidth, true);
            widthRepresentation.setAlign(cgAlignValue);
            colWidthSpecs.add(widthRepresentation);
          }
        }
      }
    } else {
      // Maybe the cols are directly children of the 'table' element.
      AuthorElement[] colChildren = tableElement.getElementsByLocalName(ELEMENT_NAME_COL);
      if (colChildren != null && colChildren.length > 0) {
        for (int i = 0; i < colChildren.length; i++) { 
          AuthorElement colChild = colChildren[i];
          // Determine the 'width' for this col.
          AttrValue colWidthAttribute = colChild.getAttribute(ATTR_NAME_WIDTH);
          String colWidth = null;
          if (colWidthAttribute != null) {
            colWidth = colWidthAttribute.getValue();
          }
          
          //Align
          String textAlignValue = null;
          AttrValue attrValue = colChild.getAttribute(ATTR_NAME_ALIGN);
          if (attrValue != null) {
            textAlignValue = attrValue.getValue();
          }
          
          AttrValue colSpanAttribute = colChild.getAttribute(ATTR_NAME_SPAN);
          int colSpan = 1;
          if (colSpanAttribute != null) {
            try {
              colSpan = Integer.parseInt(colSpanAttribute.getValue());
            } catch (NumberFormatException e) {
              if (logger.isDebugEnabled()) {
                logger.debug(e, e);
              } 
            }     
          }
          // Add ColWidth objects for the columns this 'col' specification spans over.
          for (int j = 0; j < colSpan; j ++) {
            WidthRepresentation widthRepresentation = new WidthRepresentation(colWidth, true);
            widthRepresentation.setAlign(textAlignValue);
            colWidthSpecs.add(widthRepresentation);
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
    return "Provides information about cells in HTML tables";
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#hasColumnSpecifications(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean hasColumnSpecifications(AuthorElement tableElement) {
    return true;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#getCellWidth(ro.sync.ecss.extensions.api.node.AuthorElement, int, int)
   */
  @Override
  public List<WidthRepresentation> getCellWidth(AuthorElement cellElement, int colNumberStart, int colSpan) {
    List<WidthRepresentation> toReturn = null;
    int size = colWidthSpecs.size();
    if (size >= colNumberStart && size >= colNumberStart + colSpan) {
      toReturn = new ArrayList<WidthRepresentation>(colSpan);
      for (int i = colNumberStart; i < colNumberStart + colSpan; i ++) {
        // Add the column widths 
        toReturn.add(colWidthSpecs.get(i));
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
    if (isHTMLTableCellTagName(tableCellsTagName)) {
      // Find the cols start offset and cols end offset 
      AuthorElement[] colGroupChildren = tableElement.getElementsByLocalName(ELEMENT_NAME_COLGROUP);
      // EXM-28950: Marks the fact that the col element are already present in document and only their attributes were modified.
      boolean colsModifiedInDoc = false;
      if (colGroupChildren != null && colGroupChildren.length > 0) {
        colsModifiedInDoc = true;
        int colWidthsIdx = 0;
        for (int i = 0; i < colGroupChildren.length; i++) { 
          // Verify if the current table child is a 'colgroup' element. 
          AuthorElement child = colGroupChildren[i];
          AuthorElement[] colChildren = child.getElementsByLocalName(ELEMENT_NAME_COL);
          for (int j = 0; j < colChildren.length; j++) {
        	  // EXM-28950: Modify the width attribute.
            AuthorElement colChild = colChildren[j];
            authorDocumentController.setAttribute(ATTR_NAME_WIDTH, new AttrValue(colWidths[colWidthsIdx ++].getWidthRepresentation()), colChild);
          }
        }
      } else {
        // Maybe the cols are directly children of the 'table' element.
        AuthorElement[] colChildren = tableElement.getElementsByLocalName(ELEMENT_NAME_COL);
        if (colChildren != null && colChildren.length > 0) {
          colsModifiedInDoc = true;
          for (int i = 0; i < colChildren.length; i++) { 
            AuthorElement colChild = colChildren[i];
            // EXM-28950: Modify the width attribute.
            authorDocumentController.setAttribute(ATTR_NAME_WIDTH, new AttrValue(colWidths[i].getWidthRepresentation()), colChild);
          }
        }
      }

      if (!colsModifiedInDoc && colWidths != null && authorDocumentController != null && tableElement != null) {
        // Fallback creates the XML fragment representing the column specifications. 
        String xmlFragment = createXMLFragment(colWidths);
        int offset = getInsertColsOffset();
        if (offset == -1) {
          throw new AuthorOperationException("No valid offset to insert the columns width specification.");
        }
        authorDocumentController.insertXMLFragment(xmlFragment, offset);
      }
    }
  }
  
  /**
   * @return The insert offset of the new columns specification fragment.
   */
  private int getInsertColsOffset() {
    int toReturn = -1;
    AuthorElement[] thead = tableElement.getElementsByLocalName(ELEMENT_NAME_THEAD);
    if (thead != null && thead.length > 0) {
      // Insert the cols elements before the 'thead' element  
      toReturn = thead[0].getStartOffset();
    } else {
      // No 'thead' element found. Insert the cols elements before the 'tbody' element.
      AuthorElement[] tbody = tableElement.getElementsByLocalName(ELEMENT_NAME_TBODY);
      if (tbody != null && tbody.length > 0) {
        toReturn = tbody[0].getStartOffset();
      } else {
        // No 'tbody' element found. Insert the cols elements before the first 'tr' element.
        AuthorElement[] tr = tableElement.getElementsByLocalName(HTML_ROW_NAME);
        if (tr != null && tr.length > 0) {
          toReturn = tr[0].getStartOffset();
        } else {
          // No 'tr' element found. Insert the cols elements before the 'tfoot' element.
          AuthorElement[] tfoot = tableElement.getElementsByLocalName(ELEMENT_NAME_TFOOT);
          if (tfoot != null && tfoot.length > 0) {
            toReturn = tfoot[0].getStartOffset();
          }   
        }
      }
    }
    return toReturn;
  }
  
  /**
   * Creates the XML fragment representing the column specifications.
   * The fragment will contain a list of <code>col</code> elements, one for each 
   * column specification:
   * <br/>
   * <code>
   * &lt;col with="string_width_specification" xmlns="namespace"/>
   * </code>
   * <br/>
   * The xmlns attributes will be included in the <code>col</code> elements only
   * if the table has an associated namespace. 
   * 
   * @param widthRepresentations The list of {@link WidthRepresentation} specific 
   * for each column in the table 
   * @return The XML fragment as a string.
   */
  private String createXMLFragment(WidthRepresentation[] widthRepresentations) {
    StringBuffer fragment = new StringBuffer();
    String ns = tableElement.getNamespace();
    for (int i = 0; i < widthRepresentations.length; i++) {
      WidthRepresentation width = widthRepresentations[i];
      fragment.append("<col");
      String strRepresentation = width.getWidthRepresentation();
      if (strRepresentation != null) {
        fragment.append(" width=\"" + width.getWidthRepresentation() + "\"");
      }
      if (ns != null && ns.length() > 0) {
        fragment.append(" xmlns=\"" + ns + "\"");
      }
      fragment.append("/>");
    }
    return fragment.toString();
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#commitTableWidthModification(AuthorDocumentController, int, java.lang.String)
   */
  @Override
  public void commitTableWidthModification(AuthorDocumentController authorDocumentController, int newTableWidth, String tableCellsTagName) throws AuthorOperationException {
    if (isHTMLTableCellTagName(tableCellsTagName)) {
      if (newTableWidth > 0 && authorDocumentController != null) {
        if (tableElement != null) {
          String newWidth = String.valueOf(newTableWidth);

          authorDocumentController.setAttribute(
              ATTR_NAME_WIDTH, 
              new AttrValue(newWidth),
              tableElement);
        } else {
          throw new AuthorOperationException("Cannot find the element representing the table.");
        }
      }
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#getTableWidth(java.lang.String)
   */
  @Override
  public WidthRepresentation getTableWidth(String tableCellsTagName) {
    WidthRepresentation toReturn = null;
    if (isHTMLTableCellTagName(tableCellsTagName)) {
      toReturn = getTableWidth();
    }
    return toReturn;
  }
  
  /**
   * Create a table width representation.
   * 
   * @return The width representation of the parent element named "table".
   */
  private WidthRepresentation getTableWidth() {
    WidthRepresentation toReturn = null;
    if (tableElement != null) {
      AttrValue widthAttr = tableElement.getAttribute(ATTR_NAME_WIDTH);
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
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isTableAcceptingWidth(java.lang.String)
   */
  @Override
  public boolean isTableAcceptingWidth(String tableCellsTagName) {
    return isHTMLTableCellTagName(tableCellsTagName);
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isTableAndColumnsResizable(java.lang.String)
   */
  @Override
  public boolean isTableAndColumnsResizable(String tableCellsTagName) {
    return isHTMLTableCellTagName(tableCellsTagName);
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isAcceptingFixedColumnWidths(java.lang.String)
   */
  @Override
  public boolean isAcceptingFixedColumnWidths(String tableCellsTagName) {
    return isHTMLTableCellTagName(tableCellsTagName);
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isAcceptingPercentageColumnWidths(java.lang.String)
   */
  @Override
  public boolean isAcceptingPercentageColumnWidths(String tableCellsTagName) {
    return isHTMLTableCellTagName(tableCellsTagName);
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProvider#isAcceptingProportionalColumnWidths(java.lang.String)
   */
  @Override
  public boolean isAcceptingProportionalColumnWidths(String tableCellsTagName) {
    return isHTMLTableCellTagName(tableCellsTagName);
  }
  
  /**
   * Verify if a given table cell tag name is a HTML cell tag name.
   * 
   * @param tableCellsTagName The table cell tag name to check.
   * @return <code>True</code> if the provided table cell tag name is a HTML cell 
   * name or a HTML header cell name.
   */
  public boolean isHTMLTableCellTagName(String tableCellsTagName) {
    return HTML_CELL_NAME.equals(tableCellsTagName)
    || HTML_HEADER_CELL_NAME.equals(tableCellsTagName);
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableColumnWidthProviderBase#getAllColspecWidthRepresentations()
   */
  @Override
  public List<WidthRepresentation> getAllColspecWidthRepresentations() {
    if(colWidthSpecs.size() > 0) {
      return colWidthSpecs;
    } else {
      return null;
    }
  }
}