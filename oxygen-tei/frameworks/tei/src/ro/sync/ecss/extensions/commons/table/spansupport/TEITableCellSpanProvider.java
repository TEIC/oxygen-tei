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
package ro.sync.ecss.extensions.commons.table.spansupport;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.basic.util.NumberFormatException;
import ro.sync.basic.util.NumberParserUtil;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;

/**
 * Provides cell spanning information about TEI tables.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class TEITableCellSpanProvider implements AuthorTableCellSpanProvider {
  
  /**
   * Compute the number of columns the cell spans across by looking at the 'cols' attribute.
   *  
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#getColSpan(AuthorElement)
   */
  @Override
  public Integer getColSpan(AuthorElement cellElement) {
    Integer colspan = null;
    AttrValue attrValue = cellElement.getAttribute("cols");
    if (attrValue != null) {
      try {
        int value = NumberParserUtil.parseInt(attrValue.getValue());
        colspan = Integer.valueOf(Math.max(value, 1));
      } catch(NumberFormatException nfe) {
        // Not a number.
      }
    }
    return colspan;
  }

  /**
   * Compute the number of rows the cell spans across by looking at the 'rows' attribute.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#getRowSpan(AuthorElement)
   */
  @Override
  public Integer getRowSpan(AuthorElement cellElement) {
    Integer rowspan = null;
    AttrValue attrValue = cellElement.getAttribute("rows");
    if (attrValue != null) {
      try {
        int value = NumberParserUtil.parseInt(attrValue.getValue());
        rowspan = Integer.valueOf(Math.max(value, 1));     
      } catch(NumberFormatException nfe) {
        // Not a number.
      }
    }
    return rowspan;
  }

  /**
   * Nothing to do. Cell spanning information in a TEI table is given through the
   * attributes of the cell element.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#init(AuthorElement)
   */
  @Override
  public void init(AuthorElement tableElement) {
    // Nothing to do.
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Provides cell spanning information about a TEI table.";
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider#hasColumnSpecifications(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean hasColumnSpecifications(AuthorElement tableElement) {
    return true;
  }
}