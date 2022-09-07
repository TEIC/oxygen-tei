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

/**
 * This interface contains the name of the elements and attributes used in XHTML.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public interface XHTMLConstants {
  
  /**
   * The name of element that defines a table cell.
   * The value is <code>td</code>.
   */
  String ELEMENT_NAME_TD = "td";
  
  /**
   * The name of element that defines a table row.
   * The value is <code>tr</code>.
   */
  String ELEMENT_NAME_TR = "tr";
  
  /**
   * The name of element that defines a table header row.
   * The value is <code>th</code>.
   */
  String ELEMENT_NAME_TH = "th";
  
  /**
   * The name of element that defines an XHTML table.
   * The value is <code>table</code>.
   */
  String ELEMENT_NAME_TABLE = "table";
  
  /**
   * The name of element that defines an XHTML table for DocBook model.
   * The value is <code>informaltable</code>.
   */
  String ELEMENT_NAME_INFORMALTABLE = "informaltable";
  
  /**
   * The name of the attribute that specifies the column span of a table cell.
   * The value is <code>colspan</code>.
   */
  String ATTRIBUTE_NAME_COLSPAN = "colspan";
  
  /**
   * The name of the ID attribute.
   * The value is <code>id</code>.
   */
  String ATTRIBUTE_NAME_ID = "id";
  
  /**
   * The xml:id attribute.
   * The value is <code>xml:id</code>.
   */
  String ATTRIBUTE_NAME_XML_ID = "xml:id";
  
  /**
   * The name of the attribute that specifies the row span of a table cell.
   * The value is <code>rowspan</code>.
   */
  String ATTRIBUTE_NAME_ROWSPAN = "rowspan";
  
  /**
   * The name of the table header element.
   * The value is <code>thead</code>.
   */
  String ELEMENT_NAME_THEAD = "thead";
}