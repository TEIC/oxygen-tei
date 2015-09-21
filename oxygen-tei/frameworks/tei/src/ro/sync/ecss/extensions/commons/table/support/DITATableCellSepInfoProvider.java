/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2012 Syncro Soft SRL, Romania.  All rights
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




import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;

/**
 * A DITA cell separators provider. The same as a CALS one, but also knows about the simple table.
 * 
 * @author dan
 */

public class DITATableCellSepInfoProvider extends CALSTableCellInfoProvider{

  /**
   * The default in DITA is not to present the separators.
   */
  public DITATableCellSepInfoProvider() {
    super(false);
  }
  
  /**
   * Special case for the topic/simpletable. Always return <code>true</code> for them.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSepProvider#getColSep(ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public boolean getColSep(AuthorElement cellElement, int columnIndex) {
    boolean colsep = false;
    if (containsClass(cellElement, " topic/stentry ") || containsClass(cellElement, " map/relcell ") || containsClass(cellElement, " map/relcolspec ")) {
      colsep = true;
    } else {
      colsep = super.getColSep(cellElement, columnIndex);
    }
    return colsep;
  }

  /**
   * Special case for the topic/simpletable. Always return <code>true</code> for them.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorTableCellSepProvider#getRowSep(ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public boolean getRowSep(AuthorElement cellElement, int columnIndex) {
    boolean rowsep = false;
    if (containsClass(cellElement, " topic/stentry ")|| containsClass(cellElement, " map/relcell ") || containsClass(cellElement, " map/relcolspec ")) {
      rowsep = true;
    } else {
      rowsep = super.getRowSep(cellElement, columnIndex);
    }
    return rowsep;
  }

  /**
   * Checks if the current node has a <code>class</code> attribute containing the specified substring.
   * 
   * @param element The element
   * @param substring The substring to search for.
   * @return <code>true</code> if the class attribute contains the specified substring. 
   */
  private boolean containsClass(AuthorElement element, String substring) {
    boolean ret = false;
    AttrValue attribute = element.getAttribute("class");
    if (attribute != null) {
      String classAttr = attribute.getValue();
      if (classAttr !=null && classAttr.contains(substring)) {
        ret = true;
      }
    }
    return ret;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.support.CALSTableCellInfoProvider#isTableElement(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  protected boolean isTableElement(AuthorElement element) {
    return super.isTableElement(element) || containsClass(element, " topic/table ") || containsClass(element, " map/reltable ");
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.support.CALSTableCellInfoProvider#isTgroupElement(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  protected boolean isTgroupElement(AuthorElement element) {
    return super.isTgroupElement(element) || containsClass(element, " topic/tgroup ");
  }
}
