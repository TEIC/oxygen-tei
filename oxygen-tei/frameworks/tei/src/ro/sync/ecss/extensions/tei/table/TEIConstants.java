/**
 * Copyright 2011 Syncro Soft SRL, Romania. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:

 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 * 
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Syncro Soft SRL ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Syncro Soft SRL OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Syncro Soft SRL.
 */
package ro.sync.ecss.extensions.tei.table;

/**
 * Interface containing the names of the elements and attributes used in TEI.
 */
public interface TEIConstants {
  /**
   * The name of the element that defines a table cell.
   * The value is <code>cell</code>.
   */
  String ELEMENT_NAME_CELL = "cell";
  
  /**
   * The name of the element that defines a table row.
   * The value is <code>row</code>.
   */
  String ELEMENT_NAME_ROW = "row";
  
  /**
   * The name of the element that defines the main content of a table.
   * The value is <code>table</code>.
   */
  String ELEMENT_NAME_TABLE = "table";
  
  /**
   * The name of the 'id' attribute.
   * 
   */
  String ATTRIBUTE_NAME_ID = "id";
  
  /**
   * The 'xml:id' attribute.
   * 
   */
  String ATTRIBUTE_NAME_XML_ID = "xml:id";
  
  /**
   * The name of the 'cols' attribute. 
   * For the 'cell' element the attribute specifies the number of occupied columns in the table. 
   */
  String ATTRIBUTE_NAME_COLS = "cols";
  
  /**
   * The name of the 'rows' attribute. 
   * For the 'cell' element the attribute specifies the number of occupied rows in the table. 
   */
  String ATTRIBUTE_NAME_ROWS = "rows";
}