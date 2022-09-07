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

import java.awt.Frame;

import javax.swing.JCheckBox;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog;

/**
 * Dialog used to customize XHTML table creation.
 * It is used on standalone implementation.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class SAXHTMLTableCustomizerDialog extends SATableCustomizerDialog {
  
  /**
   * Constructor.
   * 
   * @param parentFrame The parent frame.
   * @param authorResourceBundle The author resource bundle.
   * @param predefinedColumnsCount The predefined number of columns.
   * @param predefinedRowsCount The predefined number of rows.
   */
  public SAXHTMLTableCustomizerDialog(Frame parentFrame, AuthorResourceBundle authorResourceBundle, int predefinedRowsCount, int predefinedColumnsCount) {
    super(parentFrame, true, true, false, true, false, false, false, true, 
        authorResourceBundle, predefinedRowsCount, predefinedColumnsCount);
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#getColumnWidthsSpecifications(int)
   */
  @Override
  protected ColumnWidthsType[] getColumnWidthsSpecifications(int tableModelType) {
    return HTML_WIDTHS_SPECIFICATIONS;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#getFrameValues(int)
   */
  @Override
  protected String[] getFrameValues(int tableModelType) {
    return XHTMLTableCustomizerConstants.TABLE_FRAME_VALUES;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#createTitleCheckbox()
   */
  @Override
  protected JCheckBox createTitleCheckbox() {
    JCheckBox titleCheckBox = new JCheckBox(authorResourceBundle.getMessage(ExtensionTags.CAPTION) + ":");
    titleCheckBox.setName("Title checkbox");
    return titleCheckBox;
  }
  
  /**
   * Test the UI.
   * 
   * @param args Not used.
   */
  public static void main(String[] args) {
    new SAXHTMLTableCustomizerDialog(null, null, 0, 0).showDialog(null);
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#getDefaultFrameValue(int)
   */
  @Override
  protected String getDefaultFrameValue(int tableModelType) {
    return UNSPECIFIED;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#getRowsepValues(int)
   */
  @Override
  protected String[] getRowsepValues(int tableModelType) {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#getDefaultRowsepValue(int)
   */
  @Override
  protected String getDefaultRowsepValue(int tableModelType) {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#getColsepValues(int)
   */
  @Override
  protected String[] getColsepValues(int tableModelType) {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#getDefaultColsepValue(int)
   */
  @Override
  protected String getDefaultColsepValue(int tableModelType) {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#getAlignValues(int)
   */
  @Override
  protected String[] getAlignValues(int tableModelType) {
    return XHTMLTableCustomizerConstants.ALIGN_VALUES;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.SATableCustomizerDialog#getDefaultAlignValue(int)
   */
  @Override
  protected String getDefaultAlignValue(int tableModelType) {
    return UNSPECIFIED;
  }
  
  /**
   * @see ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog#getHelpPageID()
   */
  @Override
  public String getHelpPageID() {
    return "add-table-xhtml";
  }
}