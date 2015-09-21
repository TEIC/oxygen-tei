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
package ro.sync.ecss.extensions.commons.table.properties;

import java.util.List;





/**
 * @author adriana_sbircea
 *
 */

public class EditedTablePropertiesInfo {
  
  /**
   * Enumeration that contains the elements for every tab type.
   * 
   * @author adriana_sbircea
   */
  public enum TAB_TYPE {
    /**
     * The table tab.
     */
    TABLE_TAB,
    /**
     * The rows tab.
     */
    ROW_TAB,
    /**
     * The columns tab.
     */
    COLUMN_TAB,
    /**
     * The cells tab.
     */
    CELL_TAB
  }
  
  /**
   * The list of attributes that will be edited. The keyTab represents the category 
   * name and the properties represent a list with {@link TableProperty} elements for 
   * the given category. The category represents the tab name in the "Table Properties" 
   * dialog or the element name/alias for which the properties are edited.
   */
  private List<TabInfo> categories = null;
  
  /**
   * the tab that should be selected when the dialog is shown.
   */
  private TAB_TYPE selectedTab = TAB_TYPE.TABLE_TAB;
  
  /**
   * Constructor.
   * This constructor will consider that table tab should be selected when the 
   * "Table Properties" dialog is shown.
   * 
   * @param categories The properties that will be edited in the table properties 
   * for the given element. The element will be also the tab name in the dialog. 
   */
  public EditedTablePropertiesInfo(List<TabInfo> categories) {
    this(categories, TAB_TYPE.TABLE_TAB);
  }
  
  /**
   * Constructor.
   * 
   * @param categories The properties that will be edited in the table properties 
   * for the given element. The element will be also the tab name in the dialog.
   * @param selectedTab The tab that is selected when the dialog is shown.
   */
  public EditedTablePropertiesInfo(List<TabInfo> categories, TAB_TYPE selectedTab) {
   this.categories = categories; 
   this.selectedTab = selectedTab;
  }
  
  /**
   * @return Returns the table properties mapped to the element name/alias.
   */
  public List<TabInfo> getCategories() {
    return categories;
  }
  
  /**
   * Obtain the tab that is selected when the dialog is shown.
   * 
   * @return The tab that is selected when the dialog is shown.
   */
  public TAB_TYPE getSelectedTab() {
    return selectedTab;
  }
}
