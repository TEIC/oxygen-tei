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





/**
 * Interface that contains all constants used in the table properties operation.
 * 
 * @author adriana_sbircea
 */

public interface TablePropertiesConstants extends TableHelperConstants {
  /**
   * Name for row type property.
   */
  String ROW_TYPE = "Row type";
  /**
   * Vertical alignment attribute name.
   */
  String VALIGN = "valign";
  /**
   * align attribute name.
   */
  String ALIGN = "align";
  /**
   * Row separator attribute name.
   */
  String ROWSEP = "rowsep";
  /**
   * Column separator attribute name.
   */
  String COLSEP = "colsep";
  /**
   * Frame attribute name.
   */
  String FRAME = "frame";
  /**
   * Value shown when an attribute is not set.
   */
  String ATTR_NOT_SET = "<not set>";
  /**
   * Value used to computed the common value of an attribute for multiple elements. 
   */
  String NOT_COMPUTED = "Not computed";
  /**
   * Empty icon.
   */
  String EMPTY_ICON = "/images/table-properties/EmptyIcon.png";
  /**
   * Icon for row type header.
   */
  String ICON_ROW_TYPE_HEADER = "/images/table-properties/RowTypeHeader.png";
  /**
   * Icon for row type body.
   */
  String ICON_ROW_TYPE_BODY = "/images/table-properties/RowTypeBody.png";
  /**
   * Icon for row type footer.
   */
  String ICON_ROW_TYPE_FOOTER = "/images/table-properties/RowTypeFooter.png";
  /**
   * Icon for horizontal align left.
   */
  String ICON_ALIGN_LEFT = "/images/table-properties/HalignLeft.png";
  /**
   * Icon for horizontal align right.
   */
  String ICON_ALIGN_RIGHT = "/images/table-properties/HalignRight.png";
  /**
   * Icon for horizontal align center.
   */
  String ICON_ALIGN_CENTER = "/images/table-properties/HalignCenter.png";
  /**
   * Icon for horizontal align justify.
   */
  String ICON_ALIGN_JUSTIFY = "/images/table-properties/HalignJustify.png";
  /**
   * Icon for colsep = 1.
   */
  String ICON_COLSEP = "/images/table-properties/ColSep.png";
  /**
   * Icon for rowsep = 1.
   */
  String ICON_ROWSEP = "/images/table-properties/RowSep.png";
  /**
   * Icon for colsep = 1 and rowsep = 1.
   */
  String ICON_COL_ROW_SEP = "/images/table-properties/ColRowSep.png";
  /**
   * Icon for vertical align top.
   */
  String ICON_VALIGN_TOP = "/images/table-properties/ValignTop.png";
  /**
   * Icon for vertical align bottom.
   */
  String ICON_VALIGN_BOTTOM = "/images/table-properties/ValignBottom.png";
  /**
   * Icon for vertical align middle.
   */
  String ICON_VALIGN_MIDDLE = "/images/table-properties/ValignMiddle.png";
  /**
   * Icon for frame all/box/border.
   */
  String ICON_FRAME_ALL = "/images/table-properties/FrameAll.png";
  /**
   * Icon for frame top/above.
   */
  String ICON_FRAME_TOP = "/images/table-properties/FrameTop.png";
  /**
   * Icon for frame topbot/hsides.
   */
  String ICON_FRAME_TOPBOT = "/images/table-properties/FrameTopbot.png";
  /**
   * Icon for frame bottom/bellow.
   */
  String ICON_FRAME_BOTTOM = "/images/table-properties/FrameBottom.png";
  /**
   * Icon for frame sides/vsides.
   */
  String ICON_FRAME_SIDES = "/images/table-properties/FrameSides.png";
  /**
   * Icon for frame lhs.
   */
  String ICON_FRAME_LHS = "/images/table-properties/FrameLhs.png";
  /**
   * Icon for frame rhs.
   */
  String ICON_FRAME_RHS = "/images/table-properties/FrameRhs.png";
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
   * Value for vertical alignment.
   */
  String TOP = "top";
  /**
   * Value for vertical alignment.
   */
  String BOTTOM = "bottom";
  /**
   * Value for vertical alignment.
   */
  String MIDDLE = "middle";
  /**
   * Used only for values that are not in the possible values list and are set explicitly in the document.
   */
  String PRESERVE = "<preserve>";
  /**
   * Value for row type property.
   */
  String ROW_TYPE_BODY = "Body";
  /**
   * Value for row type property.
   */
  String ROW_TYPE_HEADER = "Header";
  /**
   * Value for row type property.
   */
  String ROW_TYPE_FOOTER = "Footer";
  /**
   * Row type property name.
   */
  String ROW_TYPE_PROPERTY = "rowType";
}
