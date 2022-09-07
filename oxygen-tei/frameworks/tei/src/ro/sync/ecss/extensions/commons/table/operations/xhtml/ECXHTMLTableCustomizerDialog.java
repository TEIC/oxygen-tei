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

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog;

/**
 * Dialog used to customize XHTML table creation. It is used on Eclipse platform implementation.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class ECXHTMLTableCustomizerDialog extends ECTableCustomizerDialog {
  
  /**
   * Constructor.
   * 
   * @param authorAccess The Author access.
   * @param parentShell The parent shell for the dialog.
   * @param authorResourceBundle The author resource bundle.
   * @param predefinedRowsCount The predefined number of rows.
   * @param predefinedColumnsCount The predefined number of columns.
   */
  public ECXHTMLTableCustomizerDialog(AuthorAccess authorAccess, Shell parentShell, AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount, int predefinedColumnsCount) {
    super(authorAccess, parentShell, true, true, false, false, false, false, false, false, true, authorResourceBundle, 
        predefinedRowsCount, predefinedColumnsCount);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getFrameValues(int)
   */
  @Override
  protected String[] getFrameValues(int tableModelType) {
    return XHTMLTableCustomizerConstants.TABLE_FRAME_VALUES;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getColumnWidthsSpecifications(int)
   */
  @Override
  protected List<ColumnWidthsType> getColumnWidthsSpecifications(int tableModelType) {
    return Arrays.asList(HTML_WIDTHS_SPECIFICATIONS);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#createTitleCheckbox(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Button createTitleCheckbox(Composite parent) {
    Button titleCheckBox = new Button(parent, SWT.CHECK | SWT.LEFT);
    titleCheckBox.setText(authorResourceBundle.getMessage(ExtensionTags.CAPTION));
    return titleCheckBox;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getDefaultFrameValue(int)
   */
  @Override
  protected String getDefaultFrameValue(int tableModelType) {
    return UNSPECIFIED;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getRowsepValues(int)
   */
  @Override
  protected String[] getRowsepValues(int tableModelType) {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getColsepValues(int)
   */
  @Override
  protected String[] getColsepValues(int tableModelType) {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getDefaultRowsepValue(int)
   */
  @Override
  protected String getDefaultRowsepValue(int tableModelType) {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getDefaultColsepValue(int)
   */
  @Override
  protected String getDefaultColsepValue(int tableModelType) {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getAlignValues(int)
   */
  @Override
  protected String[] getAlignValues(int tableModelType) {
    return XHTMLTableCustomizerConstants.ALIGN_VALUES;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getDefaultAlignValue(int)
   */
  @Override
  protected String getDefaultAlignValue(int tableModelType) {
    return UNSPECIFIED;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getHelpPageID()
   */
  @Override
  public String getHelpPageID() {
    return "add-table-xhtml";
  }
}