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
package ro.sync.ecss.extensions.commons.table.operations;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;

/**
 * Constants used to choose certain table attributes. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public interface TableCustomizerConstants {
  
  /**
   * Column widths specifications
   */
  public enum ColumnWidthsType {
    /**
     * Proportional column widths.
     */
    PROPORTIONAL_COL_WIDTHS,
    /**
     * Dynamic column widths.
     */
    DYNAMIC_COL_WIDTHS, 
    /**
     * Fixed column widths.
     */
    FIXED_COL_WIDTHS, 
  }
  
  /**
   * Frame all four sides of the table.
   * The value is <code>all</code>.
   */
  public static final String FRAME_ALL = "all";
  
  /**
   * Frame only the bottom of the table.
   * The value is <code>bottom</code>.
   */
  public static final String FRAME_BOTTOM = "bottom";
  
  /**
   * Place no border on the table.
   * The value is <code>none</code>.
   */
  public static final String FRAME_NONE = "none";
  
  /**
   * Do not insert frame attribute.
   * The value is <code><unspecified></code>.
   */
  public static final String UNSPECIFIED = "<unspecified>";
  
  /**
   * Frame the left and right sides of the table.
   * The value is <code>sides</code>.
   */
  public static final String FRAME_SIDES = "sides";
  
  /**
   * Frame the top of the table.
   * The value is <code>top</code>.
   */
  public static final String FRAME_TOP = "top";
  
  /**
   * Frame the top and bottom of the table.
   * The value is <code>topbot</code>.
   */
  public static final String FRAME_TOPBOT = "topbot";
  
  /**
   * Possible value for 'frame' attribute.
   * The value is <code>above</code>.
   */
  public static final String FRAME_ABOVE = "above";
  
  /**
   * Possible value for 'frame' attribute.
   * The value is <code>below</code>.
   */
  public static final String FRAME_BELLOW = "below";
  
  /**
   * Possible value for 'frame' attribute.
   * The value is <code>border</code>.
   */
  public static final String FRAME_BORDER = "border";
  
  /**
   * Possible value for 'frame' attribute.
   * The value is <code>box</code>.
   */
  public static final String FRAME_BOX = "box";
  
  /**
   * Possible value for 'frame' attribute.
   * The value is <code>hsides</code>.
   */
  public static final String FRAME_HSIDES = "hsides";
  
  /**
   * Possible value for 'frame' attribute.
   * The value is <code>vsides</code>.
   */
  public static final String FRAME_VSIDES = "vsides";
  
  /**
   * Possible value for 'frame' attribute.
   * The value is <code>lhs</code>.
   */
  public static final String FRAME_LHS = "lhs";
  
  /**
   * Possible value for 'frame' attribute.
   * The value is <code>rhs</code>.
   */
  public static final String FRAME_RHS = "rhs";
  
  /**
   * Possible value for 'frame' attribute.
   * The value is <code>void</code>.
   */
  public static final String FRAME_VOID = "void";
  /**
   * Value for horizontal alignment.
   */
  String LEFT = "left";
  /**
   * Value for horizontal alignment.
   */
  String RIGHT = "right";
  /**
   * Value for horizontal alignment.
   */
  String CENTER = "center";
  /**
   * Value for horizontal alignment.
   */
  String JUSTIFY = "justify";
  /**
   * Value for horizontal alignment.
   */
  String CHAR = "char";
  /**
   * DITA specific frame value.
   */
  public static final String DITA_CONREF = "-dita-use-conref-target";
  
  /**
   * Dynamic column widths.
   */
  public static final String COLS_DYNAMIC = "dynamic";
  
  /**
   * Proportional column widths.
   */
  public static final String COLS_PROPORTIONAL = "proportional";
  
  /**
   * Fixed column widths.
   */
  public static final String COLS_FIXED = "fixed";
  
  /**
   * The column widths type for Simple tables.
   */
  public static final ColumnWidthsType[] SIMPLE_WIDTHS_SPECIFICATIONS = new ColumnWidthsType[] {
    ColumnWidthsType.PROPORTIONAL_COL_WIDTHS, 
    ColumnWidthsType.DYNAMIC_COL_WIDTHS, 
  };
  
  /**
   * The column widths type for CALS tables.
   */
  public static final ColumnWidthsType[] CALS_WIDTHS_SPECIFICATIONS = new ColumnWidthsType[] {
    ColumnWidthsType.PROPORTIONAL_COL_WIDTHS, 
    ColumnWidthsType.DYNAMIC_COL_WIDTHS, 
    ColumnWidthsType.FIXED_COL_WIDTHS,
  };
  
  /**
   * The column widths type for HTML tables.
   */
  public static final ColumnWidthsType[] HTML_WIDTHS_SPECIFICATIONS = new ColumnWidthsType[] {
    ColumnWidthsType.PROPORTIONAL_COL_WIDTHS, 
    ColumnWidthsType.DYNAMIC_COL_WIDTHS, 
    ColumnWidthsType.FIXED_COL_WIDTHS,
  };
  
  /**
   * Default value for relative col widths.
   */
  public static final String REL_COL_WIDTH_DEFAULT_VALUE = "1*";
  /**
   * Fixed col width default value
   */
  public static final String FIXED_COL_WIDTH_DEFAULT_VALUE = "75pt";
}