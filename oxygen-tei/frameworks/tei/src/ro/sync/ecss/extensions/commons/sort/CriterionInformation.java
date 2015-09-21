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
package ro.sync.ecss.extensions.commons.sort;




import ro.sync.ecss.extensions.commons.ExtensionTags;

/**
 * Holds information about a single sorting criterion.
 */

public class CriterionInformation {
  /**
   * Type enumeration.
   */
  public enum TYPE {
    /**
     * Alfanumeric sorting type.
     */
    TEXT(ExtensionTags.TEXT),
    /**
     * Date sorting type.
     */
    DATE(ExtensionTags.DATE),
    /**
     * Numeric sorting type.
     */
    NUMERIC(ExtensionTags.NUMERIC);

    /**
     * The name.
     */
    String name = "";
    
    /**
     * Constructor. 
     */
    private TYPE(String name) {
      this.name = name;
    }
    
    /**
     * Return the name.
     * 
     * @return Returns the name.
     */
    public String getName() {
      return name;
    }
  }
  
  /**
   * Order enumeration.
   */
  public enum ORDER {
    /**
     * Ascending sorting order.
     */
    ASCENDING(ExtensionTags.ASCENDING),
    /**
     * Descending sorting order.
     */
    DESCENDING(ExtensionTags.DESCENDING);

    /**
     * The name.
     */
    String name = "";
    
    /**
     * Constructor. 
     */
    private ORDER(String name) {
      this.name = name;
    }
    
    /**
     * Return the name.
     * 
     * @return Returns the name.
     */
    public String getName() {
      return name;
    }
  }
  
  /**
   * The index in its parent of the element that corresponds to a key.
   * For example the index of a table column inside the row element.
   */
  private int keyIndex;
  
  /**
   * The sorting type. One of {@link #TYPE_TEXT}, {@link #TYPE_DATE} or {@link #TYPE_NUMERIC}.  
   */
  private String type;
  
  /**
   * The sorting order. One of {@link #ORDER_ASCENDING} or {@link #ORDER_DESCENDING}.
   */
  private String order;
  
  /**
   * The key display name.
   * For a table column this can be the text from the corresponding table head cell.
   */
  private String displayName;
  
  /**
   * <code>true</code> if this criterion is enabled initially.
   */
  private boolean isInitiallyEnabled;
  
  /**
   * Constructor.
   * 
   * @param keyIndex The index in its parent of the element that corresponds to the sorting key.
   * @param type The sorting type. One of {@link TYPE#TEXT}, {@link TYPE#NUMERIC} or {@link TYPE#DATE}.
   * @param order The sorting order. One of {@link ORDER#ASCENDING} or {@link ORDER#DESCENDING}.
   * @param displayName The key display name. For a table column it can be the text from the corresponding table header cell.
   */
  public CriterionInformation(int keyIndex, String type, String order, String displayName) {
    this(keyIndex, type, order, displayName, false);
  }
  
  /**
   * Constructor.
   * 
   * @param keyIndex The index in its parent of the element that corresponds to the sorting key.
   * @param type The sorting type. One of {@link TYPE#TEXT}, {@link TYPE#NUMERIC} or {@link TYPE#DATE}.
   * @param order The sorting order. One of {@link ORDER#ASCENDING} or {@link ORDER#DESCENDING}.
   * @param displayName The key display name. For a table column it can be the text from the corresponding table header cell.
   * @param isInitiallyEnabled <code>true</code> if this criterion should be initially enabled.
   */
  public CriterionInformation(int keyIndex, String type, String order, String displayName, boolean isInitiallyEnabled) {
    this.keyIndex = keyIndex;
    this.type = type;
    this.order = order;
    this.displayName = displayName;
    this.isInitiallyEnabled = isInitiallyEnabled;
  }
  
  /**
   * Constructor.
   *  
   * @param keyIndex The index in its parent of the element that corresponds to the sorting key.
   * @param displayName The key display name. For a table column it can be the text from the corresponding table header cell.
   */
  public CriterionInformation(int keyIndex, String displayName) {
    this(keyIndex, null, null, displayName);
  }

  /**
   * @return Returns the display name of the criterion key.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * @return Returns the key index for the sorting criterion. This represents the index in its parent of the element associated with the key.
   * For a table this represents the index of the cell in its parent row.
   */
  public int getKeyIndex() {
    return keyIndex;
  }

  /**
   * @return Returns the sorting type. 
   * One of {@link TYPE#TEXT}, {@link TYPE#NUMERIC} or {@link TYPE#DATE}
   */
  public String getType() {
    return type;
  }

  /**
   * @return Returns the sorting order.
   * One of {@link ORDER#ASCENDING} or {@link ORDER#DESCENDING}.
   */
  public String getOrder() {
    return order;
  }

  /**
   * @return <code>true</code> if the criterion is initially enabled.
   */
  public boolean isInitiallySelected() {
    return isInitiallyEnabled;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + getKeyIndex() + ", " + getType() + ", " + getOrder() + "]";
  }
}