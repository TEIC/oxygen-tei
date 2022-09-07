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

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;

/**
 * Contains the names of the elements and attributes used in CALS table model
 * (e.g. DocBook or DITA tables).
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public interface CALSConstants {
  /**
   * The name of the element that defines a table.
   * The value is <code>'table'</code>.
   */
  String ELEMENT_NAME_TABLE = "table";
  
  /**
   * The name of the element that defines a CALS table cell.
   * The value is <code>'entry'</code>.
   */
  String ELEMENT_NAME_ENTRY = "entry";
  
  /**
   * The name of the element that defines a CALS table row.
   * The value is <code>'row'</code>.
   */
  String ELEMENT_NAME_ROW = "row";
  
  /**
   * The name of the element that defines a column specification.
   * The value is <code>'colspec'</code>.
   */
  String ELEMENT_NAME_COLSPEC = "colspec";
  
  /**
   * The name of the element that defines an informaltable.
   * The value is <code>'informaltable'</code>.
   */
  String ELEMENT_NAME_INFORMALTABLE = "informaltable";
  
  /**
   * The name of the element that defines the main content of a table, or part of a table.
   * The value is <code>'tgroup'</code>.
   */
  String ELEMENT_NAME_TGROUP = "tgroup";
  
  /**
   * The name of the element that defines column span information.
   * The value is <code>'spanspec'</code>.
   */
  String ELEMENT_NAME_SPANSPEC = "spanspec";
  
  /**
   * The name of the attribute that specifies the start column name
   * in a column span specification element.
   * The value is <code>'namest'</code>.
   */
  String ATTRIBUTE_NAME_NAMEST = "namest";
  
  /**
   * The name of the attribute that specifies the end column name
   * in a column span specification element.
   * The value is <code>'nameend'</code>. 
   */
  String ATTRIBUTE_NAME_NAMEEND = "nameend";
  
  /**
   * The name of the attribute that defines the column name.
   * The value is <code>'colname'</code>.
   */
  String ATTRIBUTE_NAME_COLNAME = "colname";
  
  /**
   * The name of the attribute that identifies a span specification for a table cell.
   * The value is <code>'spanname'</code>.
   */
  String ATTRIBUTE_NAME_SPANNAME = "spanname";
  
  /**
   * The name of the attribute that identifies a column separator 
   * specification for a table cell.
   * 
   * The value is <code>'colsep'</code>.
   */
  String ATTRIBUTE_NAME_COLSEP = "colsep";  

  /**
   * The name of the attribute that identifies a row separator 
   * specification for a table cell.
   * 
   * The value is <code>'rowsep'</code>.
   */
  String ATTRIBUTE_NAME_ROWSEP = "rowsep";
  
  /**
   * The name of the <code>'colspec'</code> element attribute that specifies 
   * the column number.
   * The value is <code>'colnum'</code>. 
   */
  String ATTRIBUTE_NAME_COLNUM = "colnum";
  
  /**
   * The name of the <code>'colspec'</code> element attribute that specifies 
   * the width of the column.
   * The value is <code>'colwidth'</code>.
   */
  String ATTRIBUTE_NAME_COLWIDTH = "colwidth";
  
  /**
   * The name of the width attribute for the <code>'table'</code> element.
   * The value is <code>'width'</code>.
   */
  String ATTRIBUTE_NAME_TABLE_WIDTH = "width";
  
  /**
   * The name of the <code>'morerows'</code> attribute.
   * Specifies on how many additional rows the cell spans.
   * The value is <code>'morerows'</code>.
   */
  String ATTRIBUTE_NAME_MOREROWS = "morerows";
  
  /**
   * The <code>'tgroup'</code> element attribute that specifies the number of columns 
   * in the table.
   * The value is <code>'cols'</code>. 
   */
  String ATTRIBUTE_NAME_COLS = "cols";
  
  /**
   * The name of the id attribute.
   * The value is <code>'id'</code>.
   */
  String ATTRIBUTE_NAME_ID = "id";
  
  /**
   * The xml:id attribute.
   * The value is <code>'xml:id'</code>.
   */
  String ATTRIBUTE_NAME_XML_ID = "xml:id";
  
  /**
   * The name of the <code>'colspec'</code> element attribute that specifies 
   * the alignament of the column.
   * The value is <code>'align'</code>.
   */
  String ATTRIBUTE_NAME_ALIGN = "align";
}