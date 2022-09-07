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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.ui.EclipseHelpUtils;

/**
 * Dialog used to customize the insertion of a generic table (number of rows, columns, table caption).
 * It is used on Eclipse platform implementation.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class ECTableCustomizerDialog extends TrayDialog implements TableCustomizerConstants{
  
  /**
   * The id of the help page
   */
  private static final String HELP_PAGE_ID = "adding-tables-author";

  /**
   * If create a title the user can specify the table title.
   */
  private String title;

  /**
   * Set a title to the table.
   */
  private boolean createTitle;

  /**
   * For specify the number of rows.
   */
  private int rows;

  /**
   * Specify the number of columns.
   */
  private int columns;
  /**
   * If <code>true</code> an empty table header will be generated.
   */
  private boolean createHeader;
  /**
   * If <code>true</code> an empty table footer will be generated.
   */
  private boolean createFooter;

  /**
   * True to make CALS table.
   */
  private boolean makeCalsTable;
  
  /**
   * True to make properties table.
   */
  private boolean makePropertiesTable = false;
  
  /**
   * True to make simple or HTML table.
   */
  private boolean makeSimpleOrHtmlTable = false;
  
  /**
   * The selected frame.
   */
  private String selectedFrame;
  
  /**
   * The selected row separator value.
   */
  private String selectedRowsep;
  
  /**
   * The selected column separator value.
   */
  private String selectedColsep;
  
  /**
   * The selected align value.
   */
  private String selectedAlign;
  
  //Customization parameters.
  /**
   * The table that is customized by this dialog has a footer.
   */
  private final boolean hasFooter;
  
  /**
   * The table customized by this dialog has a frame attribute.
   */
  private final boolean hasFrameAttribute;
  
  /**
   * The table can be of any one of the types: CALS or HTML.
   */
  private final boolean showModelChooser;

  /**
   * The title text field.
   */
  private Text titleTextField;

  /**
   * Used to specify how the column widths are generated. 
   * The column widths values can be fixed or proportional.
   */
  private ComboViewer colWidthsCombobox;

  /**
   * Frame values combo.
   */
  private ComboViewer framesCombo;
  
  /**
   * Row separator values combo.
   */
  private ComboViewer rowsepCombo;
  
  /**
   * Column separator values combo.
   */
  private ComboViewer colsepCombo;
  
  /**
   * Align values combo.
   */
  private ComboViewer alignCombo;

  /**
   * The table can be have a simple model.
   */
  private final boolean simpleTableModel;

  /**
   * The title checkbox.
   */
  private Button titleCheckbox;

  /**
   * The selected column widths type
   */
  protected ColumnWidthsType selectedColWidthsType;
  /**
   * CALS table model button.
   */
  private Button calsModelRadio;
  /**
   * Simple or HTML table model.
   */
  private Button simpleOrHtmlModelRadio;
  /**
   * Properties model.
   */
  private Button propertiesModelRadio;
  /**
   * Row number chooser.
   */
  private Spinner rowsSpinner;
  /**
   * Column number chooser.
   */
  private Spinner columnsSpinner;
  /**
   * Button to specify if a header exists.
   */
  private Button headerCheckbox;
  /**
   * Button to specify if a footer exists.
   */
  private Button footerCheckbox;

  private TableInfo tableInfo;

  /**
   * <code>true</code> to allow editing the title of the table
   */
  private final boolean innerCalsTable;

  /**
   * Author resource bundle.
   */
  protected final AuthorResourceBundle authorResourceBundle;

  /**
   * Number of predefined rows
   */
  private final int predefinedRowsCount;

  /**
   * Number of predefined columns
   */
  private final int predefinedColumnsCount;

  /**
   * <code>true</code> to show choice table dialog wizard.
   */
  private final boolean showChoiceTable;
  /**
   * <code>true</code> if the table allows row separators.
   */
  private boolean hasRowSep;
  /**
   * <code>true</code> if the table allows column separators.
   */
  private boolean hasColsep;
  /**
   * <code>true</code> if the table allows align attribute.
   */
  private boolean hasAlign;
  /**
   * <code>true</code> if the model is for CALS table.
   */
  private boolean isCalsTable;
  /**
   * <code>true</code> of a properties table is accepted.
   */
  private boolean isPropertiesTableAccepted;
  /**
   * <code>true</code> if the current table has a properties table model.
   */
  private boolean isPropertiesTableModel;
  /**
   * <code>true</code> if the table model is simple or HTML, not CALS, nor properties.
   */
  private boolean isSimpleOrHtmlTable;

  /**
   * Constructor.
   * 
   * @param authorAccess           The Author access.
   * @param parentShell            The parent shell for the dialog.
   * @param hasFooter              <code>true</code> if this table supports a footer.
   * @param hasFrameAttribute      <code>true</code> if the table has a frame attribute.
   * @param showModelChooser       <code>true</code> to show the dialog panel for choosing the table model,
   *                                   one of CALS or HTML.
   * @param authorResourceBundle   The author resource bundle.
   * @param predefinedRowsCount    The predefined number of rows.
   * @param predefinedColumnsCount The predefined number of columns.
   */
  public ECTableCustomizerDialog(
      AuthorAccess authorAccess,
      Shell parentShell,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount, 
      int predefinedColumnsCount) {
    this(authorAccess, parentShell, hasFooter, hasFrameAttribute, showModelChooser, false, 
        false, false, false, false, authorResourceBundle, predefinedRowsCount, predefinedColumnsCount);
  }

  /**
   * Constructor.
   * 
   * @param authorAccess           The Author access.
   * @param parentShell            The parent shell for the dialog.
   * @param hasFooter              <code>true</code> if this table supports a footer.
   * @param hasFrameAttribute      <code>true</code> if the table has a frame attribute.
   * @param showModelChooser       <code>true</code> to show the dialog panel for choosing the table model, 
   *                                   one of CALS or HTML.
   * @param showSimpleModelRadio   <code>true</code> to show the simple model radio in the model chooser.
   * @param innerCalsTable         <code>true</code> if this is an inner calls table.
   * @param hasRowsep              <code>true</code> if the table has rowsep attribute. 
   *                                   Flag used to add a corresponding combo box in the dialog.
   * @param hasColsep              <code>true</code> if the table has colsep attribute.
   *                                   Flag used to add a corresponding combo box in the dialog.
   * @param hasAlign               <code>true</code> if the table has align attribute.
   *                                   Flag used to add a corresponding combo box in the dialog.
   * @param authorResourceBundle   The author resource bundle.
   * @param predefinedRowsCount    The predefined number of rows.
   * @param predefinedColumnsCount The predefined number of columns.
   */
  public ECTableCustomizerDialog(
      AuthorAccess authorAccess,
      Shell parentShell,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModelRadio,
      boolean innerCalsTable,
      boolean hasRowsep,
      boolean hasColsep,
      boolean hasAlign,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount, 
      int predefinedColumnsCount) {
    this(
        authorAccess,
        parentShell, 
        hasFooter, 
        hasFrameAttribute, 
        showModelChooser, 
        showSimpleModelRadio, 
        false, 
        innerCalsTable, 
        hasRowsep,
        hasColsep,
        hasAlign,
        authorResourceBundle, 
        predefinedRowsCount, 
        predefinedColumnsCount);
  }
  
  
  /**
   * Constructor for TrangDialog.
   * 
   * @param authorAccess           The Author access.
   * @param parentShell            The parent shell for the dialog.
   * @param hasFooter              <code>true</code> if this table supports a footer.
   * @param hasFrameAttribute      <code>true</code> if the table has a frame attribute.
   * @param showModelChooser       <code>true</code> to show the dialog panel for choosing the table model, 
   *                                   one of CALS or HTML.
   * @param showSimpleModelRadio   <code>true</code> to show the simple model radio in the model chooser.
   * @param showChoiceTableDialog  <code>true</code> to show the dialog for choice table.
   * @param innerCalsTable         <code>true</code> if this is an inner calls table.
   * @param hasRowsep              <code>true</code> if the table has rowsep attribute.
   *                                   Flag used to add a corresponding combo box in the dialog.
   * @param hasColsep              <code>true</code> if the table has colsep attribute.
   *                                   Flag used to add a corresponding combo box in the dialog.
   * @param hasAlign               <code>true</code> if the table has align attribute.
   *                                   Flag used to add a corresponding combo box in the dialog.
   * @param authorResourceBundle   The author resource bundle.
   * @param predefinedRowsCount    The predefined number of rows.
   * @param predefinedColumnsCount The predefined number of columns.
   */
  public ECTableCustomizerDialog(
      AuthorAccess authorAccess,
      Shell parentShell,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModelRadio,
      boolean showChoiceTableDialog,
      boolean innerCalsTable,
      boolean hasRowsep,
      boolean hasColsep,
      boolean hasAlign,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount, 
      int predefinedColumnsCount) {
    this(authorAccess, parentShell, hasFooter, hasFrameAttribute, showModelChooser, showSimpleModelRadio, showChoiceTableDialog, 
        true, innerCalsTable, hasRowsep, hasColsep, hasAlign, authorResourceBundle, predefinedRowsCount, predefinedColumnsCount);
  }
  /**
   * Constructor.
   * 
   * @param authorAccess           The Author access.
   * @param parentShell            The parent shell for the dialog.
   * @param hasFooter              <code>true</code> if this table supports a footer.
   * @param hasFrameAttribute      <code>true</code> if the table has a frame attribute.
   * @param showModelChooser       <code>true</code> to show the dialog panel for choosing the table model, 
   *                                   one of CALS or HTML.
   * @param showSimpleModelRadio   <code>true</code> to show the simple model radio in the model chooser.
   * @param showChoiceTableDialog  <code>true</code> to show the dialog for choice table.
   * @param isCalsTable            <code>true</code> if the table model is CALS.
   * @param innerCalsTable         <code>true</code> if this is an inner calls table.
   * @param hasRowsep              <code>true</code> if the table has rowsep attribute.
   *                                   Flag used to add a corresponding combo box in the dialog.
   * @param hasColsep              <code>true</code> if the table has colsep attribute.
   *                                   Flag used to add a corresponding combo box in the dialog.
   * @param hasAlign               <code>true</code> if the table has align attribute.
   *                                   Flag used to add a corresponding combo box in the dialog.
   * @param authorResourceBundle   The author resource bundle.
   * @param predefinedRowsCount    The predefined number of rows.
   * @param predefinedColumnsCount The predefined number of columns.
   */
  public ECTableCustomizerDialog(
      AuthorAccess authorAccess,
      Shell parentShell,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModelRadio,
      boolean showChoiceTableDialog,
      boolean isCalsTable,
      boolean innerCalsTable,
      boolean hasRowsep,
      boolean hasColsep,
      boolean hasAlign,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount, 
      int predefinedColumnsCount) {
    this(authorAccess, parentShell, hasFooter, hasFrameAttribute, showModelChooser, showSimpleModelRadio, showChoiceTableDialog,
        isCalsTable, innerCalsTable, false, false, false, hasRowsep, hasColsep,
        hasAlign, authorResourceBundle, predefinedRowsCount, predefinedColumnsCount);
  }
  
  /**
   * Constructor.
   * 
   * @param authorAccess                The Author access.
   * @param parentShell                 The parent shell for the dialog.
   * @param hasFooter                   <code>true</code> if this table supports a footer.
   * @param hasFrameAttribute           <code>true</code> if the table has a frame attribute.
   * @param showModelChooser            <code>true</code> to show the dialog panel for choosing the table model, 
   *                                         one of CALS or HTML.
   * @param showSimpleModelRadio        <code>true</code> to show the simple model radio in the model chooser.
   * @param showChoiceTableDialog       <code>true</code> to show the dialog for choice table.
   * @param isCalsTable                 <code>true</code> if the table model is CALS.
   * @param isSimpleOrHtmlTable         <code>true</code> if the table model is simple or HTML.
   * @param innerCalsTable              <code>true</code> if this is an inner calls table.
   * @param isPropertiesTableAccepted   <code>true</code> of a properties table is accepted.
   * @param isPropertiesTableModel      <code>true</code> if the current table has a properties table model.
   * @param hasRowsep                   <code>true</code> if the table has rowsep attribute.
   *                                        Flag used to add a corresponding combo box in the dialog.
   * @param hasColsep                   <code>true</code> if the table has colsep attribute.
   *                                        Flag used to add a corresponding combo box in the dialog.
   * @param hasAlign                    <code>true</code> if the table has align attribute.
   *                                        Flag used to add a corresponding combo box in the dialog.
   * @param authorResourceBundle        The author resource bundle.
   * @param predefinedRowsCount         The predefined number of rows.
   * @param predefinedColumnsCount      The predefined number of columns.
   */
  public ECTableCustomizerDialog(
      AuthorAccess authorAccess,
      Shell parentShell,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModelRadio,
      boolean showChoiceTableDialog,
      boolean isCalsTable,
      boolean isSimpleOrHtmlTable,
      boolean innerCalsTable,
      boolean isPropertiesTableAccepted,
      boolean isPropertiesTableModel,
      boolean hasRowsep,
      boolean hasColsep,
      boolean hasAlign,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount, 
      int predefinedColumnsCount) {
    super(parentShell);
    this.hasFooter = hasFooter;
    this.hasFrameAttribute = hasFrameAttribute;
    this.showModelChooser = showModelChooser;
    this.simpleTableModel = showSimpleModelRadio;
    this.showChoiceTable = showChoiceTableDialog;
    this.isCalsTable = isCalsTable;
    this.isSimpleOrHtmlTable = isSimpleOrHtmlTable;
    this.innerCalsTable = innerCalsTable;
    this.isPropertiesTableAccepted = isPropertiesTableAccepted;
    this.isPropertiesTableModel = isPropertiesTableModel;
    this.authorResourceBundle = authorResourceBundle;
    this.predefinedRowsCount = predefinedRowsCount;
    this.predefinedColumnsCount = predefinedColumnsCount;
    this.hasRowSep = hasRowsep;
    this.hasColsep = hasColsep;
    this.hasAlign = hasAlign;
  }

  /**
   * Configure Shell. Set a title to it.
   * 
   * @param newShell The new shell.
   * @see org.eclipse.jface.dialogs.Dialog#configureShell(org.eclipse.swt.widgets.Shell)
   */
  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    EclipseHelpUtils.installHelp(newShell, HELP_PAGE_ID);
    newShell.setText(
        authorResourceBundle.getMessage(
            showChoiceTable ? ExtensionTags.INSERT_CHOICE_TABLE : ExtensionTags.INSERT_TABLE));
  }

  /**
   * Create Dialog area.
   * 
   * @param parent The parent composite.
   * @return The dialog control.
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new GridLayout(2, false));
    
    int tableModel;
    if (showModelChooser) {     
      tableModel = TableInfo.TABLE_MODEL_CALS;
      //Allow the user to choose between HTML and CALS
      Group modelChooser = new Group(composite, SWT.SINGLE);
      modelChooser.setText(authorResourceBundle.getMessage(ExtensionTags.MODEL));
      modelChooser.setLayout(new GridLayout(3, true));
      GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
      data.horizontalSpan = 2;
      modelChooser.setLayoutData(data);
      
      // Radio button for choosing CALS table model
      calsModelRadio = new Button(modelChooser, SWT.RADIO | SWT.LEFT);
      calsModelRadio.setText("CALS");
      calsModelRadio.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          makeCalsTable = calsModelRadio.getSelection();
          makeSimpleOrHtmlTable = simpleOrHtmlModelRadio.getSelection();
          if (propertiesModelRadio != null) {
            makePropertiesTable = propertiesModelRadio.getSelection();
          }
          // Set column widths input
          tableModelChanged(TableInfo.TABLE_MODEL_CALS);
          
          // Set limits and keep value
          columnsSpinner.setMinimum(TableInfo.MIN_COLUMNS_COUNT);
          columnsSpinner.setMaximum(TableInfo.MAX_COLUMNS_COUNT);
        }
      });

      if (!showChoiceTable) {
        simpleOrHtmlModelRadio = new Button(modelChooser, SWT.RADIO | SWT.LEFT);
        if (!isPropertiesTableAccepted) {
          GridData gridData = new GridData();
          gridData.horizontalSpan = 2;
          simpleOrHtmlModelRadio.setLayoutData(gridData);
        }
        
        if (simpleTableModel) {
          // Radio button for choosing simple table model
          simpleOrHtmlModelRadio.setText(authorResourceBundle.getMessage(ExtensionTags.SIMPLE));
          simpleOrHtmlModelRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              makeCalsTable = calsModelRadio.getSelection();
              makeSimpleOrHtmlTable = simpleOrHtmlModelRadio.getSelection();
              if (propertiesModelRadio != null) {
                makePropertiesTable = propertiesModelRadio.getSelection();
              }
              // Set column widths input
              tableModelChanged(TableInfo.TABLE_MODEL_DITA_SIMPLE);
              
              // Set limits and keep value
              columnsSpinner.setMinimum(TableInfo.MIN_COLUMNS_COUNT);
              columnsSpinner.setMaximum(TableInfo.MAX_COLUMNS_COUNT);
            }
          });

          //Set some default values.
          updateRadioButtonsSelection();
          if(makeCalsTable) {
            tableModel = TableInfo.TABLE_MODEL_CALS;
          } else {
            tableModel = makePropertiesTable ? TableInfo.TABLE_MODEL_DITA_PROPERTIES : TableInfo.TABLE_MODEL_DITA_SIMPLE;
          }
          tableModelChanged(tableModel);
        } else {
          // Radio button for choosing HTML table model
          simpleOrHtmlModelRadio.setText("HTML");
          simpleOrHtmlModelRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              makeCalsTable = calsModelRadio.getSelection();
              makeSimpleOrHtmlTable = simpleOrHtmlModelRadio.getSelection();
              if (propertiesModelRadio != null) {
                makePropertiesTable = propertiesModelRadio.getSelection();
              }
              // Set column widths inputs
              tableModelChanged(TableInfo.TABLE_MODEL_HTML);
              
              // Set limits and keep value
              columnsSpinner.setMinimum(TableInfo.MIN_COLUMNS_COUNT);
              columnsSpinner.setMaximum(TableInfo.MAX_COLUMNS_COUNT);
            }
          });

          //Set some default values.
          updateRadioButtonsSelection();
          if (makeCalsTable) {
            tableModel = TableInfo.TABLE_MODEL_CALS;
          } else {
            tableModel = makePropertiesTable ? TableInfo.TABLE_MODEL_DITA_PROPERTIES : TableInfo.TABLE_MODEL_HTML;
          }
          tableModelChanged(tableModel);
        }

        if (isPropertiesTableAccepted) {
          propertiesModelRadio = new Button(modelChooser, SWT.RADIO | SWT.LEFT);
          propertiesModelRadio.setText(authorResourceBundle.getMessage(ExtensionTags.PROPERTIES));
          propertiesModelRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              makeCalsTable = calsModelRadio.getSelection();
              makeSimpleOrHtmlTable = simpleOrHtmlModelRadio.getSelection();
              if (propertiesModelRadio != null) {
                makePropertiesTable = propertiesModelRadio.getSelection();
              }
              tableModelChanged(TableInfo.TABLE_MODEL_DITA_PROPERTIES);
              
              // Set new limits and value for the columns spinner
              columnsSpinner.setMinimum(TableInfo.MIN_COLUMNS_COUNT_PROPERTIES_TABLE);
              columnsSpinner.setMaximum(TableInfo.MAX_COLUMNS_COUNT_PROPERTIES_TABLE);
              if (columns < 2 || columns > 3) {
                // Force to 3
                columns = 3;
              }
              columnsSpinner.setSelection(columns);
            }
          });
          
          updateRadioButtonsSelection();
          if (makeCalsTable) {
            tableModel = TableInfo.TABLE_MODEL_CALS;
          } else {
            tableModel = makePropertiesTable ? TableInfo.TABLE_MODEL_DITA_PROPERTIES : TableInfo.TABLE_MODEL_DITA_SIMPLE;
          }
          tableModelChanged(tableModel);
        }
      }
    } else {
      tableModel = TableInfo.TABLE_MODEL_DITA_CHOICE;
    }

    if(!innerCalsTable && !showChoiceTable) {
      // Title check box.
      titleCheckbox = createTitleCheckbox(composite);
      titleCheckbox.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          titleTextField.setEditable(titleCheckbox.getSelection());
          createTitle = titleCheckbox.getSelection();
          if (titleCheckbox.getSelection()) {
            // Request focus in text field
            titleTextField.setFocus();
          }
        }
      });
      //Set some default values.
      createTitle = true;
      titleCheckbox.setSelection(createTitle);

      // Title text field
      titleTextField = new Text(composite, SWT.SINGLE | SWT.BORDER);
      titleTextField.addModifyListener(new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
          title = titleTextField.getText();
        }
      });
      GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
      titleTextField.setLayoutData(data);
    }
    //Set some default values.
    title = "";

    GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
    //Give the number of rows and cols for the new table.
    Group sizeGroup = new Group(composite, SWT.SINGLE);
    sizeGroup.setText(authorResourceBundle.getMessage(ExtensionTags.TABLE_SIZE));
    sizeGroup.setLayout(new GridLayout(4, false));
    data.horizontalSpan = 2;
    sizeGroup.setLayoutData(data);

    // 'Rows' label.
    Label label = new Label(sizeGroup, SWT.LEFT);
    label.setText(authorResourceBundle.getMessage(ExtensionTags.ROWS));
    rowsSpinner = new Spinner(sizeGroup, SWT.BORDER);
    rowsSpinner.setMinimum(TableInfo.MIN_ROWS_COUNT);
    rowsSpinner.setMaximum(1000);
    rowsSpinner.setSelection(3);
    rowsSpinner.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        rows = rowsSpinner.getSelection();
      }
    });
    rowsSpinner.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    //Set some default values.
    rows = TableInfo.DEFAULT_ROWS_COUNT;

    // 'Columns' label
    label = new Label(sizeGroup, SWT.LEFT);
    label.setText(authorResourceBundle.getMessage(ExtensionTags.COLUMNS));
    columnsSpinner = new Spinner(sizeGroup, SWT.BORDER);
    columnsSpinner.setMinimum(isPropertiesTableModel ? TableInfo.MIN_COLUMNS_COUNT_PROPERTIES_TABLE : TableInfo.MIN_COLUMNS_COUNT);
    columnsSpinner.setMaximum(isPropertiesTableModel ? TableInfo.MAX_COLUMNS_COUNT_PROPERTIES_TABLE : TableInfo.MAX_COLUMNS_COUNT);
    columnsSpinner.setSelection(isPropertiesTableModel ? TableInfo.DEFAULT_COLUMNS_COUNT_PROPERTIES_TABLE : TableInfo.DEFAULT_COLUMNS_COUNT);
    columnsSpinner.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        columns = columnsSpinner.getSelection();
      }
    });
    columnsSpinner.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    //Set some default values.
    columns = isPropertiesTableModel ? TableInfo.DEFAULT_COLUMNS_COUNT_PROPERTIES_TABLE : TableInfo.DEFAULT_COLUMNS_COUNT;

    if (showChoiceTable) {
      columnsSpinner.setEnabled(false);
    }

    //Allow header and/or footer generation.
    Composite headerAndFooterComposite = new Composite(composite, SWT.NONE);
    GridLayout layout = new GridLayout(2, true);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    headerAndFooterComposite.setLayout(layout);
    data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.horizontalSpan = 2;
    headerAndFooterComposite.setLayoutData(data);

    // 'Header' check box
    headerCheckbox = new Button(headerAndFooterComposite, SWT.CHECK | SWT.LEFT);
    headerCheckbox.setText(authorResourceBundle.getMessage(ExtensionTags.GENERATE_TABLE_HEADER));
    headerCheckbox.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        createHeader = headerCheckbox.getSelection();
      }
    });
    //Set some default values.
    headerCheckbox.setSelection(true);
    createHeader = true;

    if (hasFooter) {
      // 'Footer' check box
      footerCheckbox = new Button(headerAndFooterComposite, SWT.CHECK | SWT.LEFT);
      footerCheckbox.setText(authorResourceBundle.getMessage(ExtensionTags.GENERATE_TABLE_FOOTER));
      footerCheckbox.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          createFooter = footerCheckbox.getSelection();
        }
      });
      //Set some default values.
      footerCheckbox.setSelection(false);
      createFooter = false;
    }
    
    final List<ColumnWidthsType> columnsWidths = getColumnWidthsSpecifications(tableModel);
    if (columnsWidths != null) {
      // Cols label
      Label colsLabel = new Label(composite, SWT.LEFT);
      colsLabel.setText(authorResourceBundle.getMessage(ExtensionTags.COLUMN_WIDTHS) + ": ");
      
      // Cols combo box
      colWidthsCombobox = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      colWidthsCombobox.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      ILabelProvider labelProvider = new LabelProvider() {
        /**
         * Get descr for element.
         */
        @Override
        public String getText(Object element) {
          String render = String.valueOf(element);
          if (element instanceof ColumnWidthsType) {
            if (ColumnWidthsType.DYNAMIC_COL_WIDTHS == element) {
              render = COLS_DYNAMIC; 
            } else if (ColumnWidthsType.PROPORTIONAL_COL_WIDTHS == element) {
              render = COLS_PROPORTIONAL; 
            } else if (ColumnWidthsType.FIXED_COL_WIDTHS == element) {
              render = COLS_FIXED; 
            }
          }
          return render;
        }
      };
      // Cols label provider
      colWidthsCombobox.setLabelProvider(labelProvider);
      colWidthsCombobox.addSelectionChangedListener(new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
          selectedColWidthsType = ((ColumnWidthsType)((StructuredSelection)event.getSelection()).getFirstElement());
        }
      });
      // Cols content provider
      colWidthsCombobox.setContentProvider(new ListContentProvider()); 
      setColWidthsComboInput(columnsWidths);
    }

    if (hasFrameAttribute) {
      // 'Frame' label.
      Label frameLabel = new Label(composite, SWT.LEFT);
      frameLabel.setText(authorResourceBundle.getMessage(ExtensionTags.FRAME) + ": ");

      // Frame combo box
      framesCombo = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      framesCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      framesCombo.addSelectionChangedListener(new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
          selectedFrame = (String) ((StructuredSelection)event.getSelection()).getFirstElement();
        }
      });
      framesCombo.setContentProvider(new ListContentProvider()); 
      setFrameComboInput(getFrameValues(getTableModel()));
    }
    
    // EXM-29536 Add rowsep and colsep 
    if (hasRowSep) {
      // 'Row sep' label.
      Label rowsepLabel = new Label(composite, SWT.LEFT);
      rowsepLabel.setText(authorResourceBundle.getMessage(ExtensionTags.ROW_SEPARATOR) + ": ");

      // Row sep combo box
      rowsepCombo = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      rowsepCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      rowsepCombo.addSelectionChangedListener(new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
          selectedRowsep = (String) ((StructuredSelection)event.getSelection()).getFirstElement();
        }
      });
      rowsepCombo.setContentProvider(new ListContentProvider()); 
      setRowsepComboInput(getRowsepValues(getTableModel()));
    }
    
    // EXM-29536 Add rowsep and colsep attribute when inserting a table 
    if (hasColsep) {
      // 'Colsep' label.
      Label colsepLabel = new Label(composite, SWT.LEFT);
      colsepLabel.setText(authorResourceBundle.getMessage(ExtensionTags.COLUMN_SEPARATOR) + ": ");

      // Col sep combo box
      colsepCombo = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      colsepCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      colsepCombo.addSelectionChangedListener(new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
          selectedColsep = (String) ((StructuredSelection)event.getSelection()).getFirstElement();
        }
      });
      colsepCombo.setContentProvider(new ListContentProvider()); 
      setColsepComboInput(getColsepValues(getTableModel()));
    }
    
    // EXM-29536 Add align attribute when inserting a table 
    if (hasAlign) {
      // 'Align' label.
      Label alignLabel = new Label(composite, SWT.LEFT);
      alignLabel.setText(authorResourceBundle.getMessage(ExtensionTags.ALIGNMENT) + ": ");

      // Align combo box
      alignCombo = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
      alignCombo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      alignCombo.addSelectionChangedListener(new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
          selectedAlign = (String) ((StructuredSelection)event.getSelection()).getFirstElement();
        }
      });
      alignCombo.setContentProvider(new ListContentProvider()); 
      setAlignComboInput(getAlignValues(getTableModel()));
    }
    
    initialize();
    return composite;
  }

  /**
   * Update the selection state of the radio buttons.
   */
  private void updateRadioButtonsSelection() {
    makeCalsTable = (isCalsTable && innerCalsTable)
        // This happens when invoking the toolbar Insert Table action,
        // which doesn't have a default model.
        || (!isCalsTable && !isSimpleOrHtmlTable && !isPropertiesTableModel);
    calsModelRadio.setSelection(makeCalsTable);
    simpleOrHtmlModelRadio.setSelection(isSimpleOrHtmlTable);
    if (propertiesModelRadio != null) {
      propertiesModelRadio.setSelection(isPropertiesTableModel);
    }
  }
  
  /**
   * @param frames List of possible frames values
   */
  private void setFrameComboInput(String[] frames) {
    if (framesCombo != null) {
      Object lastSelected = null;
      if(!framesCombo.getSelection().isEmpty()) {
        lastSelected = ((IStructuredSelection)framesCombo.getSelection()).getFirstElement();
      }
      List<String> framesList = Arrays.asList(frames);
      framesCombo.setInput(framesList);
      int indexOfSel = framesList.indexOf(lastSelected);
      if(indexOfSel != -1) {
        framesCombo.setSelection(new StructuredSelection(framesList.get(indexOfSel)), true);
      } else {
        framesCombo.setSelection(new StructuredSelection(getDefaultFrameValue(getTableModel())), true);
      }
    }
  }
  
  /**
   * Set the row separator values.
   * 
   * @param values List of possible rowsep values.
   */
  private void setRowsepComboInput(String[] values) {
    if (rowsepCombo != null) {
      rowsepCombo.setInput(Arrays.asList(values));
      rowsepCombo.setSelection(new StructuredSelection(getDefaultRowsepValue(getTableModel())), true);
    }
  }
  
  /**
   * Set the column separator values.
   * 
   * @param values List of possible colsep values.
   */
  private void setColsepComboInput(String[] values) {
    if (colsepCombo != null) {
      colsepCombo.setInput(Arrays.asList(values));
      colsepCombo.setSelection(new StructuredSelection(getDefaultColsepValue(getTableModel())), true);
    }
  }
  
  /**
   * Set the align values.
   * 
   * @param values List of possible align values.
   */
  private void setAlignComboInput(String[] values) {
    if (alignCombo != null) {
      alignCombo.setInput(Arrays.asList(values));
      alignCombo.setSelection(new StructuredSelection(getDefaultAlignValue(getTableModel())), true);
    }
  
  }

  /**
   * @param columnsWidths List of possible column widths
   */
  private void setColWidthsComboInput(List<ColumnWidthsType> columnsWidths) {
    Object lastSelected = null;
    if (colWidthsCombobox != null && columnsWidths != null) {
      if(!colWidthsCombobox.getSelection().isEmpty()) {
        lastSelected = ((IStructuredSelection)colWidthsCombobox.getSelection()).getFirstElement();
      }
      colWidthsCombobox.setInput(columnsWidths);
      int indexOfSel = columnsWidths.indexOf(lastSelected);
      if (indexOfSel == -1) {
        indexOfSel = 0;
      }
      if(indexOfSel != -1) {
        colWidthsCombobox.setSelection(new StructuredSelection(columnsWidths.get(indexOfSel)), true);
      }
    }
  }

  /**
   * Compute the possible values for <code>'frame'</code> attribute.
   * 
   * @param tableModelType The table model type. 
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * @return Returns the possible values for <code>'frame'</code> attribute. 
   */
  protected abstract String[] getFrameValues(int tableModelType);
  
  /**
   * Compute the possible values for <code>'rowsep'</code> attribute.
   * 
   * @param tableModelType The table model type. 
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * @return Returns the possible values for <code>'rowsep'</code> attribute. 
   */
  protected abstract String[] getRowsepValues(int tableModelType);
  
  /**
   * Compute the possible values for <code>'colsep'</code> attribute.
   * 
   * @param tableModelType The table model. 
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * @return Returns the possible values for <code>'colsep'</code> attribute. 
   */
  protected abstract String[] getColsepValues(int tableModelType);
  
  /**
   * Compute the possible values for <code>'align'</code> attribute.
   * 
   * @param tableModelType The table model type. 
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * @return Returns the possible values for <code>'align'</code> attribute. 
   */
  protected abstract String[] getAlignValues(int tableModelType);
  
  /**
   * Get the default frame value.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return The default frame value
   */
  protected abstract String getDefaultFrameValue(int tableModelType);
  
  /**
   * Get the default row separator value.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return The default row separator value
   */
  protected abstract String getDefaultRowsepValue(int tableModelType);
  
  /**
   * Get the default column separator value.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return The default column separator value
   */
  protected abstract String getDefaultColsepValue(int tableModelType);
  
  /**
   * Get the default align value.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return The default align value
   */
  protected abstract String getDefaultAlignValue(int tableModelType);
  
  /**
   * Compute the possible values for the column widths specifications.
   * 
   * @param tableModelType The table model type. 
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * @return Returns the possible values for the column widths modifications. 
   */
  protected abstract List<ColumnWidthsType> getColumnWidthsSpecifications(int tableModelType);

  /**
   * Create a checkbox with an implementation specific title.
   * 
   * @param parent The parent {@link Composite}.
   * @return The title checkbox customized according to implementation.
   */
  protected abstract Button createTitleCheckbox(Composite parent);

  /**
   * Show the dialog to customize the table attributes.
   * @param tableInfo 
   * 
   * @return The information about the table to be inserted, 
   * or <code>null</code> if the user canceled the table insertion.
   */
  public TableInfo showDialog(TableInfo tableInfo) {
    this.tableInfo = tableInfo;
    if (OK == open()) {
      int rowsNumber = rows;
      int columnsNumber = columns;
      // Compute the value of the table model
      int tableModel = getTableModel();
      return 
      new TableInfo( //NOSONAR java:S1067 exception here
          createTitle ? title : null, 
          rowsNumber, 
          columnsNumber, 
          createHeader, 
          hasFooter && createFooter, 
          // EXM-23110 If the user chose the "<unspecified>" value for frame attribute, don't insert any frame attribute
          hasFrameAttribute && !UNSPECIFIED.equals(selectedFrame) ? selectedFrame : null,
          tableModel, 
          selectedColWidthsType,
          hasRowSep && !UNSPECIFIED.equals(selectedRowsep) ? selectedRowsep : null,
          hasColsep && !UNSPECIFIED.equals(selectedColsep) ? selectedColsep : null,
          hasAlign && !UNSPECIFIED.equals(selectedAlign) ? selectedAlign : null);
    } else {
      // Cancel was pressed
    }
    return null;
  }
  
  /**
   * Initialize controls.
   */
  private void initialize() {
    if(titleTextField != null) {
      // Reset components to default values
      titleTextField.setEditable(true);
      title = "";
      titleTextField.setText(title);
    }
    
    if (showChoiceTable) {
      tableModelChanged(TableInfo.TABLE_MODEL_DITA_CHOICE);
    }
    
    if (tableInfo != null) {
      if (showModelChooser) {
        if (isCalsTable 
            || (tableInfo.getTableModel() == TableInfo.TABLE_MODEL_CALS 
                && !isPropertiesTableModel 
                && !isSimpleOrHtmlTable)) {
          makeCalsTable = true; 
          makeSimpleOrHtmlTable = false;
          makePropertiesTable = false;
        } else if (isSimpleOrHtmlTable 
                  || (tableInfo.getTableModel() != TableInfo.TABLE_MODEL_CALS
                      && tableInfo.getTableModel() != TableInfo.TABLE_MODEL_DITA_PROPERTIES
                      && !isCalsTable 
                      && !isPropertiesTableModel)) {
          makeCalsTable = false; 
          makeSimpleOrHtmlTable = true;
          makePropertiesTable = false;
        } else if ( propertiesModelRadio != null 
                    && (isPropertiesTableModel 
                        || (tableInfo.getTableModel() == TableInfo.TABLE_MODEL_DITA_PROPERTIES 
                            && !isCalsTable
                            && !isSimpleOrHtmlTable))) {
          makeCalsTable = false; 
          makeSimpleOrHtmlTable = false;
          makePropertiesTable = true;
        } else { //NOSONAR java:S1871: This is the default. It's easier to understand in this way
          // This may happen when the previous model was "properties", 
          // but in the meantime we moved to a document that doesn't accept
          // a properties table. Select CALS by default.
          makeCalsTable = true;
          makeSimpleOrHtmlTable = false;
          makePropertiesTable = false;
        }
        
        calsModelRadio.setSelection(makeCalsTable);
        simpleOrHtmlModelRadio.setSelection(makeSimpleOrHtmlTable);
        if (propertiesModelRadio != null) {
          propertiesModelRadio.setSelection(makePropertiesTable);
        }
        
        int tableModel;
        if (makeCalsTable) {
          tableModel = TableInfo.TABLE_MODEL_CALS;
        } else if (makePropertiesTable) {
          tableModel = TableInfo.TABLE_MODEL_DITA_PROPERTIES;
        } else if (simpleTableModel){
          tableModel = TableInfo.TABLE_MODEL_DITA_SIMPLE;
        } else {
          tableModel = TableInfo.TABLE_MODEL_HTML;
        }
        tableModelChanged(tableModel);
      }
      
      // Title check box and text field. It's important to set the "Title" check box selection
      // after selecting the model radio button, otherwise the listeners added
      // to the model radio buttons will mess up with the check box.
      if(titleTextField != null) {
        if (tableInfo.getTitle() != null) {
          createTitle = true;
          titleCheckbox.setSelection(createTitle);
          // Request focus in title field
          titleTextField.setFocus();
        } else {
          createTitle = false;
          titleCheckbox.setSelection(createTitle);
        }
      }

      if (predefinedRowsCount < 0 || predefinedColumnsCount < 0) {
        // Set the default number of rows and columns
        rows = tableInfo.getRowsNumber();
        rowsSpinner.setSelection(rows);
        if (!showChoiceTable) {
          columns = tableInfo.getColumnsNumber();
          if (makePropertiesTable) {
            columnsSpinner.setMinimum(TableInfo.MIN_COLUMNS_COUNT_PROPERTIES_TABLE);
            columnsSpinner.setMaximum(TableInfo.MAX_COLUMNS_COUNT_PROPERTIES_TABLE);
            if (columns < 2 || columns > 3) {
              // Force to 3
              columns = 3;
            }
          } else {
            columnsSpinner.setMinimum(TableInfo.MIN_COLUMNS_COUNT);
            columnsSpinner.setMaximum(TableInfo.MAX_COLUMNS_COUNT);
          }
          columnsSpinner.setSelection(columns);
        } 
      } else {
        rowsSpinner.setSelection(predefinedRowsCount);
        columnsSpinner.setSelection(predefinedColumnsCount);
      }

      // Header and footer
      createHeader = tableInfo.isGenerateHeader();
      headerCheckbox.setSelection(createHeader);
      if (hasFooter) {
        createFooter = tableInfo.isGenerateFooter();
        footerCheckbox.setSelection(createFooter);
      }
      
      if (colWidthsCombobox != null && tableInfo.getColumnsWidthsType() != null) {
        colWidthsCombobox.setSelection(new StructuredSelection(tableInfo.getColumnsWidthsType()));
      }
      
      if (framesCombo != null) {
        if (tableInfo.getFrame() != null) {
          selectedFrame = tableInfo.getFrame();
          framesCombo.setSelection(new StructuredSelection(selectedFrame));
        } else {
          // EXM-23110 If the table has no frame attribute, then the frame combo selection should be "<unspecified>"
          selectedFrame = UNSPECIFIED;
          framesCombo.setSelection(new StructuredSelection(selectedFrame));
        }
      }
      
      if (rowsepCombo != null) {
        if (tableInfo.getRowsep() != null) {
          selectedRowsep = tableInfo.getRowsep();
          rowsepCombo.setSelection(new StructuredSelection(selectedRowsep));
        } else {
          // EXM-23110 If the table has no row separator attribute, then the frame combo selection should be "<unspecified>"
          selectedRowsep = UNSPECIFIED;
          rowsepCombo.setSelection(new StructuredSelection(selectedRowsep));
        }
      
      }
      
      // Colsep value
      if (colsepCombo != null) {
        if (tableInfo.getColsep() != null) {
          selectedColsep = tableInfo.getColsep();
          colsepCombo.setSelection(new StructuredSelection(selectedColsep));
        } else {
          selectedColsep = UNSPECIFIED;
          colsepCombo.setSelection(new StructuredSelection(selectedColsep));
        }
      }
      
      // Align value
      if (alignCombo != null) {
        if (tableInfo.getAlign() != null) {
          selectedAlign = tableInfo.getAlign();
          alignCombo.setSelection(new StructuredSelection(selectedAlign));
        } else {
          selectedAlign = UNSPECIFIED;
          alignCombo.setSelection(new StructuredSelection(selectedAlign));
        }
      }
    } else {
      if(titleTextField != null) {
        titleCheckbox.setSelection(true);
        // Request focus in text field
        titleTextField.setFocus();
      }

      if (showModelChooser) {
        makeCalsTable = isCalsTable || (!isSimpleOrHtmlTable && !isPropertiesTableModel);
        makeSimpleOrHtmlTable = isSimpleOrHtmlTable;
        makePropertiesTable = isPropertiesTableModel;
        
        calsModelRadio.setSelection(makeCalsTable);
        simpleOrHtmlModelRadio.setSelection(makeSimpleOrHtmlTable);
        if (propertiesModelRadio != null) {
          propertiesModelRadio.setSelection(makePropertiesTable);
        }
        
        if (isPropertiesTableModel) {
          tableModelChanged(TableInfo.TABLE_MODEL_DITA_PROPERTIES);
        } else if (simpleTableModel) {
          tableModelChanged(makeCalsTable ? TableInfo.TABLE_MODEL_CALS : TableInfo.TABLE_MODEL_DITA_SIMPLE);
        } else {
          tableModelChanged(makeCalsTable ? TableInfo.TABLE_MODEL_CALS : TableInfo.TABLE_MODEL_HTML);
        }
      }
      
      if (predefinedRowsCount < 0 || predefinedColumnsCount < 0) {
        // Set the default number of rows and columns
        rows = TableInfo.DEFAULT_ROWS_COUNT;
        rowsSpinner.setSelection(rows);
        columns = isPropertiesTableModel ? TableInfo.DEFAULT_COLUMNS_COUNT_PROPERTIES_TABLE : TableInfo.DEFAULT_COLUMNS_COUNT;
        columnsSpinner.setSelection(columns);
      } else {
        rowsSpinner.setSelection(predefinedRowsCount);
        columnsSpinner.setSelection(predefinedColumnsCount);
      }

      // Header and footer
      createHeader = true;
      headerCheckbox.setSelection(createHeader);
      if (hasFooter) {
        createFooter = false;
        footerCheckbox.setSelection(createFooter);
      }
    }
    
    if(titleTextField != null) {
      titleTextField.setEditable(titleCheckbox.isEnabled() && titleCheckbox.getSelection());
    }
  }

  /**
   * @return The table model type depending on the state of the dialog.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   */
  private int getTableModel() {
    int tableModelType = TableInfo.TABLE_MODEL_CUSTOM;
    if (showModelChooser) {
      if (makeCalsTable) {
        tableModelType = TableInfo.TABLE_MODEL_CALS;
      } else {
        if (makePropertiesTable) {
          tableModelType = TableInfo.TABLE_MODEL_DITA_PROPERTIES;
        } else {
          if (simpleTableModel) {
            tableModelType = TableInfo.TABLE_MODEL_DITA_SIMPLE;
          } else {
            tableModelType = TableInfo.TABLE_MODEL_HTML;
          }
        }
      }
    } else if (showChoiceTable) {
      tableModelType = TableInfo.TABLE_MODEL_DITA_CHOICE;
    }
    
    return tableModelType;
  }
  
  /**
   * Update the state of the title section.
   * 
   * @param enabled <code>true</code> if the title section is enabled.
   */
  private void updateTitleState(boolean enabled) {
    if(titleTextField != null) {
      titleCheckbox.setEnabled(enabled);
      // EXM-35014 If the title checkbox is disabled, also uncheck it
      titleCheckbox.setSelection(enabled);
      titleTextField.setEditable(enabled && titleCheckbox.getSelection());
    }
  }
  
  /**
   * Update the state of the separators combos.
   * 
   * @param enabled <code>true</code> if the separators should be enabled.
   */
  private void updateSeparatorsState(boolean enabled) {
    // Update title state
    if (rowsepCombo != null) {
      rowsepCombo.getCombo().setEnabled(enabled);
    }

    if (colsepCombo != null) {
      colsepCombo.getCombo().setEnabled(enabled);
    }
  }
  
  /**
   * Update the state of the align combo.
   * 
   * @param enabled <code>true</code> if the align combo is enabled.
   */
  private void updateAlignState(boolean enabled) {
    if (alignCombo != null) {
      alignCombo.getCombo().setEnabled(enabled);
    }
  }
  
  /**
   * Update the state of the column widths combo.
   * 
   * @param enabled <code>true</code> if the combo should be enabled.
   */
  private void updateColWidthsCombo(boolean enabled) {
    if (colWidthsCombobox != null) {
      colWidthsCombobox.getCombo().setEnabled(enabled);
    }
  }
  
  /**
   * Update controls for the given selected table model.
   */
  private void tableModelChanged(int model) {
    updateColWidthsCombo(model != TableInfo.TABLE_MODEL_DITA_PROPERTIES);
    if (colWidthsCombobox != null && colWidthsCombobox.getCombo().isEnabled()) {
      setColWidthsComboInput(getColumnWidthsSpecifications(model));
    }
    setFrameComboInput(getFrameValues(model));
    // TABLE_MODEL_DITA_SIMPLE and TABLE_MODEL_DITA_CHOICE do not support title
    updateTitleState(
        model != TableInfo.TABLE_MODEL_DITA_SIMPLE && model != TableInfo.TABLE_MODEL_DITA_CHOICE
        && model != TableInfo.TABLE_MODEL_DITA_PROPERTIES);
    // Update the separators
    updateSeparatorsState(model == TableInfo.TABLE_MODEL_CALS);
    // Update the align combo
    updateAlignState(model == TableInfo.TABLE_MODEL_CALS || model == TableInfo.TABLE_MODEL_HTML);
  }
  
  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    getButton(OK).setText(authorResourceBundle.getMessage(ExtensionTags.INSERT));
  }
  
  /**
   * Get the ID of the help page which will be called by the end user.
   * @return the ID of the help page which will be called by the end user or <code>null</code>.
   */
  protected String getHelpPageID(){
    return HELP_PAGE_ID;
  }
}