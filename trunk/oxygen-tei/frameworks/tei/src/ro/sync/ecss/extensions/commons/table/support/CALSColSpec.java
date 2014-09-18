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

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.WidthRepresentation;

/**
 * The column specification for a CALS table model 
 * (e.g. DocBook or DITA tables).
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class CALSColSpec {
  
  /**
   * The number of the column, 1 based.
   */
  private int colNumber;
  
  /**
   * The name of the column.
   */
  private String colName;
  
  /**
   * The column width representation. See {@link WidthRepresentation} javadoc
   * for details about the format.
   */
  private WidthRepresentation colWidth;

  /**
   * True if the column number was specified
   */
  private final boolean colNumberSpecified;

  /**
   * The index of the colspec in the document.
   */
  private final int indexInDocument;
  
  /**
   * The text align
   */
  private String align;
  
  /**
   * Tests the presence of the column separator. 
   * 
   * @return <code>true</code> if the separator 
   * should be painted at the right of the cell, <code>false</code> if no separator 
   * is needed, or <code>null</code> if the default specified by the document type 
   * should be applied.
   * For instance in Docbook, the default value is <code>true</code> while in DITA 
   * is <code>false</code>.
   * If the cell is the last in the row, this value is disregarded.
   */
  public Boolean getColSep() {
    return colSep;
  }

  /**
   * Tests the presence of the row separator.
   * 
   * @return <code>true</code> if the separator 
   * should be painted below the cell, <code>false</code> if no separator 
   * is needed, or <code>null</code> if the default specified by the document type 
   * should be applied.
   * For instance in Docbook, the default value is <code>true</code> while in DITA 
   * is <code>false</code>.
   * If the cell is the in the last row, this value is disregarded.
   */
  public Boolean getRowSep() {
    return rowSep;
  }

  /**
   * Flag for the column separator. This can be <code>true</code> if the separator 
   * should be painted at the right of the cell, <code>false</code> if no separator 
   * is needed, or <code>null</code> if the default specified by the document type 
   * should be applied.
   * For instance in Docbook, the default value is <code>true</code> while in DITA 
   * is <code>false</code>.
   * If the cell is the last in the row, this value is disregarded.
   */
  private Boolean colSep;
  
  /**
   * Flag for the row separator. This can be <code>true</code> if the separator 
   * should be painted below the cell, <code>false</code> if no separator 
   * is needed, or <code>null</code> if the default specified by the document type 
   * should be applied.
   * For instance in Docbook, the default value is <code>true</code> while in DITA 
   * is <code>false</code>.
   * If the cell is the in the last row, this value is disregarded.
   */
  private Boolean rowSep;
  
  
  /**
   * Constructor.
   * @param indexInDocument Index in colspec elements list.
   * @param colNumber The number of the column. It is 1 based. 
   * @param colNumberSpecified <code>true</code> if the column number was specified as an attribute
   * @param colName The name of the column.
   * @param colWidth The string representation of the column width 
   * as described in the {@link WidthRepresentation}.
   * @param colSep <code>true</code> if the column separators are needed for that column, 
   *   <code>false</code> if not, <code>null</code> if the framework default should apply. 
   *   For instance Docbook has the colsep on true by default, while DITA on false.
   * @param rowSep <code>true</code> if the row separators are needed for that column, 
   *   <code>false</code> if not, <code>null</code> if the framework default should apply. 
   *   For instance Docbook has the rowsep on true by default, while DITA on false.
   */
  public CALSColSpec(int indexInDocument, int colNumber, boolean colNumberSpecified, String colName, String colWidth, Boolean colSep, Boolean rowSep) {
    this.indexInDocument = indexInDocument;
    this.colNumber = colNumber;
    this.colNumberSpecified = colNumberSpecified;
    this.colName = colName;
    if (colWidth != null) {
      this.colWidth = new WidthRepresentation(colWidth, false);
    }
    this.colSep = colSep;
    this.rowSep = rowSep;
  }
  
  /**
   * Constructor.
   * The rowsep and colsep are set to null, i.e. the document type default.
   * 
   * @param indexInDocument Index in colspec elements list.
   * @param colNumber The number of this column. It is 1 based. 
   * @param colNumberSpecified <code>true</code> if the column number was specified as an attribute
   * @param colName The name of this column.
   * @param colWidth The column width representation.
   */
  public CALSColSpec(int indexInDocument, int colNumber, boolean colNumberSpecified, String colName, WidthRepresentation colWidth) {
    this.indexInDocument = indexInDocument;
    this.colNumber = colNumber;
    this.colNumberSpecified = colNumberSpecified;
    this.colName = colName;
    this.colWidth = colWidth;
  }
  
  /**
   * @return Returns the colNumberSpecified.
   */
  public boolean isColNumberSpecified() {
    return colNumberSpecified;
  }

  /**
   * @return Returns the indexInDocument.
   */
  public int getIndexInDocument() {
    return indexInDocument;
  }
  
  /**
   * @return The column number. It is 1 based.
   */
  public int getColumnNumber() {
    return colNumber;
  }

  /**
   * @return The name of the column.
   */
  public String getColumnName() {
    return colName;
  }
  
  /**
   * @return Returns the column width representation.
   */
  public WidthRepresentation getColWidth() {
    return colWidth;
  }
  
  /**
   * Creates a String representation of the column specification.
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String width = (colWidth != null) ? " width: " + colWidth : "";
    return getColumnName() + " at index: " + getColumnNumber() + width;
  }
  
  /**
   * Creates the XML fragment corresponding to the column specification
   * obtained from the <code>colNumber</code>, <code>colName</code> and 
   * <code>colWidth</code> fields.
   * <br>
   * The general format of the generated fragment is:
   * <br><br>
   * <code>
   * &lt;colspec colnum="integer_value" colname="string_value" colwidth="string_value" xmlns="URI"/>
   * </code>
   * 
   * 
   * @param ns The namespace URI of the table element. It can be <code>null</code>.
   * @return The XML fragment corresponding to the column specification.
   */
  public String createXMLFragment(String ns) {
    StringBuffer fragment = new StringBuffer();
    fragment.append("<colspec");
    fragment.append(colNumber <= 0 ? "" : " colnum=\"" + colNumber + "\"");
    fragment.append(colName == null ? "" : " colname=\"" + colName + "\"");
    if (colWidth != null) {
      String colWidthRepresentation = colWidth.getWidthRepresentation();
      if (colWidthRepresentation != null) {
        fragment.append(" colwidth=\"" + colWidthRepresentation + "\"");
      }
    }
    if (ns != null && ns.length() > 0) {
      fragment.append(" xmlns=\"" + ns + "\"");
    }
    fragment.append("/>");
    return fragment.toString();
  }
  
  /**
   * Set the new {@link WidthRepresentation} corresponding to the column specification.
   * @param colWidth The column width to be set.
   */
  public void setColWidth(WidthRepresentation colWidth) {
    this.colWidth = colWidth;
  }
  
  /**
   * Get the align value specified on the colspec. 
   * 
   * @return Returns the align value.
   */
  public String getAlign() {
    return align;
  }
  
  /**
   * Set the align value specified on the colspec. 
   * 
   * @param align The textAlign to set.
   */
  public void setAlign(String align) {
    this.align = align;
    if(colWidth != null) {
      colWidth.setAlign(align);
    }
  }
}