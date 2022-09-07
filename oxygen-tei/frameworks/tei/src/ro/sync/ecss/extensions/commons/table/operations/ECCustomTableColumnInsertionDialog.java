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

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;

/**
 * Dialog displayed when trying to insert multiple columns (using "Insert Columns...").
 * For Eclipse plugin.
 * 
 * @author sorin_carbunaru
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class ECCustomTableColumnInsertionDialog extends TrayDialog {

  /**
   * Table columns info.
   */
  private TableColumnsInfo tableColumnsInfo;

  /**
   * Number of columns to be inserted.
   */
  private int columns;

  /**
   * <code>true</code> if inserting columns(s) after the current location. 
   */
  private boolean insertAfter;

  /**
   * Spinner providing the number of columns to be inserted.
   */
  private Spinner columnsSpinner;

  /**
   * "Before" radio button.
   */
  private Button beforeRadioButton;

  /**
   * "After" radio button.
   */
  private Button afterRadioButton;
  
  /**
   * The author resource bundle
   */
  private AuthorResourceBundle authorResourceBundle;
  
  /**
   * Constructor.
   * @param parentShell the parent shell.
   * @param authorResourceBundle the author resource bundle.
   */
  protected ECCustomTableColumnInsertionDialog(Shell parentShell, AuthorResourceBundle authorResourceBundle) {
    super(parentShell);
    this.authorResourceBundle = authorResourceBundle;
  }
  
  /**
   * Configure Shell. Set a title to it.
   * 
   * @param newShell The new shell.
   * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
   */
  @Override
  protected void configureShell(Shell newShell) {
    newShell.setText( authorResourceBundle.getMessage(ExtensionTags.INSERT_COLUMNS));
    super.configureShell(newShell);
  }
  
  /**
   * Create Dialog area.
   * 
   * @param parent The parent composite.
   * @return The dialog control.
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    // main panel
    Composite mainComposite = new Composite(parent, SWT.NONE);
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = SWT.FILL;
    mainComposite.setLayoutData(gridData);
    mainComposite.setLayout(new GridLayout(2, false));

    // no. of columns label
    Label numberOfColumnsLabel = new Label(mainComposite, SWT.NONE);
    numberOfColumnsLabel.setText(authorResourceBundle.getMessage(ExtensionTags.NUMBER_OF_COLUMNS) + ":");
    
    // spinner for providing the number of columns to be inserted
    columnsSpinner = new Spinner(mainComposite, SWT.BORDER);
    columnsSpinner.setValues(1, 1, Integer.MAX_VALUE, 0, 1, 1);
    gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    columnsSpinner.setLayoutData(gridData);
    columnsSpinner.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        columns = columnsSpinner.getSelection();
      }
    });
    columns = new TableColumnsInfo().getColumnsNumber();
    
    // position label
    Label positionLabel = new Label(mainComposite, SWT.NONE);
    positionLabel.setText(authorResourceBundle.getMessage(ExtensionTags.POSITION) + ":");

    // panel for selecting the position of the columns to be inserted
    Composite positionComposite = new Composite(mainComposite, SWT.NONE);
    positionComposite.setLayout(new RowLayout());
    
    // "before" radio button
    beforeRadioButton = new Button(positionComposite, SWT.RADIO);
    beforeRadioButton.setText(authorResourceBundle.getMessage(ExtensionTags.BEFORE));
    beforeRadioButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent arg0) {
        insertAfter = false;
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent arg0) {
        insertAfter = false;
      }
    });
    
    // "below" radio button
    afterRadioButton = new Button(positionComposite, SWT.RADIO);
    afterRadioButton.setText(authorResourceBundle.getMessage(ExtensionTags.AFTER));
    afterRadioButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent arg0) {
        insertAfter = true; 
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent arg0) {
        insertAfter = true;          
      }
    });

    initialize();

    return mainComposite;
  }
  
  /**
   * Show the dialog to customize the column(s) insertion.
   * @param previousTableColumnsInfo the previous columns information. 
   * 
   * @return The information about the column(s) to be inserted, 
   * or <code>null</code> if the user canceled the insertion.
   */
  public TableColumnsInfo showDialog(TableColumnsInfo previousTableColumnsInfo) {
    this.tableColumnsInfo = previousTableColumnsInfo;
    if (OK == open()) {
      return new TableColumnsInfo(columns, insertAfter);
    } else {
      // Cancel was pressed
    }
    return null;
  }
  
  /**
   * Initialize dialog. 
   */
  private void initialize() {
    // initialize using the previous columns information, if possible
    if (tableColumnsInfo != null) {
      columns = tableColumnsInfo.getColumnsNumber();
      columnsSpinner.setSelection(columns);
      insertAfter = tableColumnsInfo.isInsertAfter();
      beforeRadioButton.setSelection(!insertAfter);
      afterRadioButton.setSelection(insertAfter);
    } else { 
      TableColumnsInfo defaultInfo = new TableColumnsInfo();
      columns = defaultInfo.getColumnsNumber();
      columnsSpinner.setSelection(columns);
      insertAfter = defaultInfo.isInsertAfter();
      beforeRadioButton.setSelection(!insertAfter);
      afterRadioButton.setSelection(insertAfter);
    }
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    getButton(OK).setText(authorResourceBundle.getMessage(ExtensionTags.INSERT));
  }
  

}
