/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2015 Syncro Soft SRL, Romania.  All rights
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

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;




import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.ui.EclipseHelpUtils;

/**
 * Dialog that allows the user to choose the information necessary for the Split operation.
 * 
 * @author adriana_sbircea
 */

public class ECTableSplitCustomizerDialog extends TrayDialog {

  /**
   * The author resource bundle.It is used for translations.
   */
  private AuthorResourceBundle authorResourceBundle;
  /**
   * Row number chooser.
   */
  private Spinner rowsSpinner;
  /**
   * Column number chooser.
   */
  private Spinner columnsSpinner;
  /**
   * The number of columns that the user has chosen in the dialog.
   */
  private int chosenColumns = 0;
  /**
   * The number of rows that the user has chosen in the dialog.
   */
  private int chosenRows = 0;
  /**
   * The maximum number of columns in which the current cell can be split.
   */
  private int maxColumns;
  /**
   * The maximum number of rows in which the current cell can be split.
   */
  private int maxRows;
  /**
   * The help page ID
   */
  private String helpPageID;

  /**
   * Constructor.
   * 
   * @param parentFrame           The parent frame of the dialog.
   * @param authorResourceBundle  The author resource bundle.It is used for translations.
   * @param maxColumns            The maximum number of columns in which the current cell can be split.
   * @param maxRows               The maximum number of rows in which the current cell can be split.
   * @param helpPageID            The help page ID.
   */
  public ECTableSplitCustomizerDialog(Object parentFrame, 
      AuthorResourceBundle authorResourceBundle, int maxColumns, int maxRows, String helpPageID) {
    super((Shell)parentFrame);
    this.authorResourceBundle = authorResourceBundle;
    this.maxColumns = maxColumns;
    this.maxRows = maxRows;
    this.helpPageID = helpPageID;
    
    int style = SWT.DIALOG_TRIM;
    style |= SWT.RESIZE;
    style |= SWT.APPLICATION_MODAL;
    setShellStyle(style);
  }
  
  /**
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    EclipseHelpUtils.installHelp(newShell, helpPageID);
    newShell.setText(authorResourceBundle.getMessage(ExtensionTags.SPLIT_CELLS));
  }
  
  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite mainComposite = (Composite) super.createDialogArea(parent);
    // Set the layout
    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 10;
    layout.marginWidth = 10;
    mainComposite.setLayout(layout);
    
    // Column
    Label label = new Label(mainComposite, SWT.LEFT);
    label.setText(authorResourceBundle.getMessage(ExtensionTags.NUMBER_OF_COLUMNS) + ":");
    
    columnsSpinner = new Spinner(mainComposite, SWT.BORDER);
    columnsSpinner.setMinimum(1);
    columnsSpinner.setMaximum(maxColumns);
    chosenColumns = maxColumns > 1 ? 2 : 1;
    columnsSpinner.setSelection(chosenColumns);
    columnsSpinner.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        chosenColumns = columnsSpinner.getSelection();
      }
    });
    GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
    columnsSpinner.setLayoutData(data);
    
    // Row
    Label label2 = new Label(mainComposite, SWT.LEFT);
    label2.setText(authorResourceBundle.getMessage(ExtensionTags.NUMBER_OF_ROWS) + ":");
    rowsSpinner = new Spinner(mainComposite, SWT.BORDER);
    rowsSpinner.setMinimum(1);
    rowsSpinner.setMaximum(maxRows);
    chosenRows = 1;
    rowsSpinner.setSelection(chosenRows);
    rowsSpinner.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        chosenRows = rowsSpinner.getSelection();
      }
    });
    data = new GridData(SWT.FILL, SWT.NONE, true, false);
    rowsSpinner.setLayoutData(data);
    
    return mainComposite;
  }
  
  /**
   * Obtain the number of cells on split. (horizontally and vertically)
   * 
   * @return The first element contains the number of columns and the second 
   *        element contains the number of rows.
   */
  public int[] getSplitInformation() {
    int[] result = null;
    if (open() == OK) {
      result = new int[2];
      result[0] = chosenColumns;
      result[1] = chosenRows;
    }

    return result;
  }
}