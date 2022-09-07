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
package ro.sync.ecss.extensions.commons.table.operations.xhtml;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper;
import ro.sync.ecss.extensions.commons.table.support.HTMLTableCellInfoProvider;

/**
 * Implementation of the document type helper for XHTML.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class XHTMLDocumentTypeHelper extends AbstractDocumentTypeHelper implements XHTMLConstants {
  
  /**
   * Row element names.
   */
  private static final String[] ROW_ELEMENT_NAMES = new String[] { ELEMENT_NAME_TR };
  
  /**
   * Cell element names.
   */
  private static final String[] CELL_ELEMENT_NAMES = new String[] { ELEMENT_NAME_TD, ELEMENT_NAME_TH };
  
  /**
   * Table element names.
   */
  public static final String[] TABLE_ELEMENT_NAMES = new String[] { ELEMENT_NAME_TABLE, ELEMENT_NAME_INFORMALTABLE };
  
  /**
   * For XHTML, the column span is always defined.
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#checkTableColSpanIsDefined(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider, ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public void checkTableColSpanIsDefined(
      AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableSpanSupport,
      AuthorElement cellElement) throws AuthorOperationException {
    // Always is defined
  }

  /**
   * @see ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper#getTableCellElementNames()
   */
  @Override
  public String[] getTableCellElementNames() {
    return CELL_ELEMENT_NAMES;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper#getTableElementLocalName()
   */
  @Override
  public String[] getTableElementLocalName() {
    return TABLE_ELEMENT_NAMES;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper#getTableRowElementNames()
   */
  @Override
  public String[] getTableRowElementNames() {
    return ROW_ELEMENT_NAMES;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#getTableCellSpanProvider(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public AuthorTableCellSpanProvider getTableCellSpanProvider(AuthorElement tableElement) {
    HTMLTableCellInfoProvider tableSpanSupport = new HTMLTableCellInfoProvider();
    tableSpanSupport.init(tableElement);
    return tableSpanSupport;
  }
  
  /**
   * Update the 'colspan' attribute.
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#updateTableColSpan(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider, ro.sync.ecss.extensions.api.node.AuthorElement, int, int)
   */
  @Override
  public void updateTableColSpan(
      AuthorAccess authorAccess, 
      AuthorTableCellSpanProvider tableSupport, 
      AuthorElement cellElement, 
      int startCol, 
      int endCol) {
    int colSpan = endCol - startCol + 1;
    if (colSpan > 1) {
      authorAccess.getDocumentController().setAttribute(
          ATTRIBUTE_NAME_COLSPAN, 
          new AttrValue(String.valueOf(colSpan)) , 
          cellElement);
    } else {
      authorAccess.getDocumentController().removeAttribute(
          ATTRIBUTE_NAME_COLSPAN, 
          cellElement);
    }
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#updateTableRowSpan(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public void updateTableRowSpan(
      AuthorAccess authorAccess, 
      AuthorElement cellElement, 
      int rowSpan) {
    if (rowSpan > 1) {
      authorAccess.getDocumentController().setAttribute(
          ATTRIBUTE_NAME_ROWSPAN, 
          new AttrValue(String.valueOf(rowSpan)) , 
          cellElement);
    } else {
      authorAccess.getDocumentController().removeAttribute(
          ATTRIBUTE_NAME_ROWSPAN, 
          cellElement);
    }
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#updateTableColumnNumber(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public void updateTableColumnNumber(AuthorAccess authorAccess, AuthorElement tableElement, int colNum) {
    // Not implemented
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#updateTableRowNumber(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public void updateTableRowNumber(AuthorAccess authorAccess, AuthorElement tableElement,
      int rowsNumber) {
    // Nothing to do here.
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#getIgnoredColumnAttributes()
   */
  @Override
  public String[] getIgnoredColumnAttributes() {
    return new String[] {
        ATTRIBUTE_NAME_ROWSPAN,
        ATTRIBUTE_NAME_COLSPAN,
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
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#getIgnoredRowAttributes()
   */
  @Override
  public String[] getIgnoredRowAttributes() {
    return new String[] {
        ATTRIBUTE_NAME_ROWSPAN,
    };
  }
}