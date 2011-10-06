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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog;

/**
 * The dialog used to customize a TEI table. It is used on Eclipse platform implementation.
 */
public class ECTEITableCustomizerDialog extends ECTableCustomizerDialog {
  
  /**
   * Constructor.
   * 
   * @param parentShell The parent shell for the dialog.
   */
  public ECTEITableCustomizerDialog(Shell parentShell) {
    super(parentShell, false, false, false);
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getColumnWidthsSpecifications(int)
   */
  @Override
  protected List<ColumnWidthsType> getColumnWidthsSpecifications(int tableModel) {
    return null;
  }

  /**
   * In TEI we don't have a frame attribute.
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getFrameValues(int)
   */
  @Override
  protected String[] getFrameValues(int tableModel) {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#createTitleCheckbox(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Button createTitleCheckbox(Composite parent) {
    Button titleCheckBox = new Button(parent, SWT.CHECK | SWT.LEFT);
    titleCheckBox.setText("Head");
    titleCheckBox.setToolTipText("The title for the table");
    return titleCheckBox;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.ECTableCustomizerDialog#getDefaultFrameValue(int)
   */
  @Override
  protected String getDefaultFrameValue(int tableModel) {
    return null;
  }
}