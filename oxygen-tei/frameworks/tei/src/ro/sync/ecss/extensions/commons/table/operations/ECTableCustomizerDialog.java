/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2009 Syncro Soft SRL, Romania.  All rights
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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.xml.XmlUtil;

/**
 * Dialog used to customize the insertion of a generic table (number of rows, columns, table caption).
 * It is used on Eclipse platform implementation.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class ECTableCustomizerDialog extends Dialog implements TableCustomizerConstants{
  
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
  private Button otherModelRadio;
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
   * Constructor for TrangDialog.
   * 
   * @param parentShell           The parent shell for the dialog.
   * @param hasFooter             <code>true</code> if this table supports a footer.
   * @param hasFrameAttribute     <code>true</code> if the table has a frame attribute.
   * @param showModelChooser      <code>true</code> to show the dialog panel for choosing the table model,
   *                              one of CALS or HTML.
   * @param authorResourceBundle  The author resource bundle.
   * @param predefinedRowsCount The predefined number of rows.
   * @param predefinedColumnsCount The predefined number of columns.
   */
  public ECTableCustomizerDialog(
      Shell parentShell,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount, 
      int predefinedColumnsCount) {
    this(parentShell, hasFooter, hasFrameAttribute, showModelChooser, false, 
        false, false, false, false, authorResourceBundle, predefinedRowsCount, predefinedColumnsCount);
  }

  /**
   * Constructor for TrangDialog.
   * 
   * @param parentShell           The parent shell for the dialog.
   * @param hasFooter             <code>true</code> if this table supports a footer.
   * @param hasFrameAttribute     <code>true</code> if the table has a frame attribute.
   * @param showModelChooser      <code>true</code> to show the dialog panel for choosing the table model, 
   *                              one of CALS or HTML.
   * @param showSimpleModel       <code>true</code> to show the simple model radio in the model chooser.
   * @param innerCalsTable        <code>true</code> if this is an inner calls table.
   * @param hasRowsep             <code>true</code> if the table has rowsep attribute.
   * @param hasColsep             <code>true</code> if the table has colsep attribute.
   * @param hasAlign              <code>true</code> if the table has align attribute.
   * @param authorResourceBundle  The author resource bundle.
   * @param predefinedRowsCount The predefined number of rows.
   * @param predefinedColumnsCount The predefined number of columns.
   */
  public ECTableCustomizerDialog(
      Shell parentShell,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModel,
      boolean innerCalsTable,
      boolean hasRowsep,
      boolean hasColsep,
      boolean hasAlign,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount, 
      int predefinedColumnsCount) {
    this(parentShell, 
        hasFooter, 
        hasFrameAttribute, 
        showModelChooser, 
        showSimpleModel, 
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
   * @param parentShell           The parent shell for the dialog.
   * @param hasFooter             <code>true</code> if this table supports a footer.
   * @param hasFrameAttribute     <code>true</code> if the table has a frame attribute.
   * @param showModelChooser      <code>true</code> to show the dialog panel for choosing the table model, 
   *                              one of CALS or HTML.
   * @param showSimpleModel       <code>true</code> to show the simple model radio in the model chooser.
   * @param showChoiceTable       <code>true</code> to show the dialog for choice table.
   * @param innerCalsTable        <code>true</code> if this is an inner calls table.
   * @param hasRowsep             <code>true</code> if the table has rowsep attribute.
   * @param hasColsep             <code>true</code> if the table has colsep attribute.
   * @param hasAlign              <code>true</code> if the table has align attribute.
   * @param authorResourceBundle  The author resource bundle.
   * @param predefinedRowsCount The predefined number of rows.
   * @param predefinedColumnsCount The predefined number of columns.
   */
  public ECTableCustomizerDialog(
      Shell parentShell,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModel,
      boolean showChoiceTable,
      boolean innerCalsTable,
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
    this.simpleTableModel = showSimpleModel;
    this.showChoiceTable = showChoiceTable;
    this.innerCalsTable = innerCalsTable;
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
    newShell.setText(
        authorResourceBundle.getMessage(
            showChoiceTable ? ExtensionTags.INSERT_CHOICE_TABLE : ExtensionTags.INSERT_TABLE));
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
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new GridLayout(2, false));
    
    int tableModel = innerCalsTable ? TableInfo.TABLE_MODEL_CALS : TableInfo.TABLE_MODEL_CUSTOM;
    if (showModelChooser) {     
      tableModel = TableInfo.TABLE_MODEL_CALS;
      //Allow the user to choose between HTML and CALS
      Group modelChooser = new Group(composite, SWT.SINGLE);
      modelChooser.setText(authorResourceBundle.getMessage(ExtensionTags.MODEL));
      modelChooser.setLayout(new GridLayout(2, true));
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
          // Set column widths input
          tableModelChanged(TableInfo.TABLE_MODEL_CALS);
        }
      });

      if (!showChoiceTable) {
        if (simpleTableModel) {
          // Radio button for choosing simple table model
          otherModelRadio = new Button(modelChooser, SWT.RADIO | SWT.LEFT);
          otherModelRadio.setText(authorResourceBundle.getMessage(ExtensionTags.SIMPLE));
          otherModelRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              makeCalsTable = calsModelRadio.getSelection();
              // Set column widths input
              tableModelChanged(TableInfo.TABLE_MODEL_DITA_SIMPLE);
            }
          });

          //Set some default values.
          makeCalsTable = true;
          calsModelRadio.setSelection(makeCalsTable);
          otherModelRadio.setSelection(! makeCalsTable);
        } else {
          // Radio button for choosing HTML table model
          otherModelRadio = new Button(modelChooser, SWT.RADIO | SWT.LEFT);
          otherModelRadio.setText("HTML");
          otherModelRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
              makeCalsTable = calsModelRadio.getSelection();
              // Set column widths inputs
              tableModelChanged(TableInfo.TABLE_MODEL_HTML);
            }
          });

          //Set some default values.
          makeCalsTable = true;
          calsModelRadio.setSelection(makeCalsTable);
          otherModelRadio.setSelection(! makeCalsTable);
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
    if (predefinedRowsCount <= 0 || predefinedColumnsCount <= 0) {
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
      rowsSpinner.setMinimum(0);
      rowsSpinner.setMaximum(100);
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
      columnsSpinner.setMinimum(0);
      columnsSpinner.setMaximum(100);
      columnsSpinner.setSelection(2);
      columnsSpinner.addModifyListener(new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
          columns = columnsSpinner.getSelection();
        }
      });
      columnsSpinner.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      //Set some default values.
      columns = TableInfo.DEFAULT_COLUMNS_COUNT;
      
      if (showChoiceTable) {
        columnsSpinner.setEnabled(false);
      }
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
            } if (ColumnWidthsType.FIXED_COL_WIDTHS == element) {
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
   * @param frames List of possible frames values
   */
  private void setFrameComboInput(String[] frames) {
    if (framesCombo != null) {
      framesCombo.setInput(Arrays.asList(frames));
      framesCombo.setSelection(new StructuredSelection(getDefaultFrameValue(getTableModel())), true);
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
    if (colWidthsCombobox != null && columnsWidths != null) {
      colWidthsCombobox.setInput(columnsWidths);
      colWidthsCombobox.setSelection(new StructuredSelection(columnsWidths.get(0)), true);
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
      int rowsNumber = predefinedRowsCount;
      int columnsNumber = predefinedColumnsCount;
      if (predefinedColumnsCount <=0 || predefinedRowsCount <= 0) {
        rowsNumber = rows;
        columnsNumber = columns;
      }
      // Compute the value of the table model
      int tableModel = getTableModel();
      // EXM-11910 Escape the table title.
      title = XmlUtil.escape(title);
      return 
      new TableInfo(
          createTitle ? title : null, 
          rowsNumber, 
          columnsNumber, 
          createHeader, 
          hasFooter? createFooter : false, 
          // EXM-23110 If the user chose the "<unspecified>" value for frame attribute, don't insert any frame attribute
          hasFrameAttribute ? (!UNSPECIFIED.equals(selectedFrame) ? selectedFrame : null) : null,
          tableModel, 
          selectedColWidthsType,
          hasRowSep ? (!UNSPECIFIED.equals(selectedRowsep) ? selectedRowsep : null) : null,
          hasColsep ? (!UNSPECIFIED.equals(selectedColsep) ? selectedColsep : null) : null,
          hasAlign ? (!UNSPECIFIED.equals(selectedAlign) ? selectedAlign : null) : null);
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
      if(titleTextField != null) {
        if (tableInfo.getTitle() != null) {
          title = tableInfo.getTitle();
          titleTextField.setText(title);
          createTitle = true;
          titleCheckbox.setSelection(createTitle);
          // Request focus in title field
          titleTextField.setFocus();
        } else {
          createTitle = false;
          titleCheckbox.setSelection(createTitle);
        }
      }
      
      if (showModelChooser) {
        makeCalsTable = tableInfo.getTableModel() == TableInfo.TABLE_MODEL_CALS;
        calsModelRadio.setSelection(makeCalsTable);
        otherModelRadio.setSelection(! makeCalsTable);
        tableModelChanged(tableInfo.getTableModel());
      }

      if (predefinedRowsCount <= 0 || predefinedColumnsCount <= 0) {
        // Set the default number of rows and columns
        rows = tableInfo.getRowsNumber();
        rowsSpinner.setSelection(rows);
        if (!showChoiceTable) {
          columns = tableInfo.getColumnsNumber();
          columnsSpinner.setSelection(columns);
        } 
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
        makeCalsTable = true;
        calsModelRadio.setSelection(makeCalsTable);
      }
      
      if (predefinedRowsCount <= 0 || predefinedColumnsCount <= 0) {
        // Set the default number of rows and columns
        rows = 3;
        rowsSpinner.setSelection(Integer.valueOf(rows));
        columns = 2;
        columnsSpinner.setSelection(Integer.valueOf(columns));
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
        if (simpleTableModel) {
          tableModelType = TableInfo.TABLE_MODEL_DITA_SIMPLE;
        } else {
          tableModelType = TableInfo.TABLE_MODEL_HTML;
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
   * Update controls for the given selected table model.
   */
  private void tableModelChanged(int model) {
    setColWidthsComboInput(getColumnWidthsSpecifications(model));
    setFrameComboInput(getFrameValues(model));
    // TABLE_MODEL_DITA_SIMPLE and TABLE_MODEL_DITA_CHOICE do not support title
    updateTitleState(
        model != TableInfo.TABLE_MODEL_DITA_SIMPLE && model != TableInfo.TABLE_MODEL_DITA_CHOICE);
    // Update the separators
    updateSeparatorsState(model == TableInfo.TABLE_MODEL_CALS);
    // Update the align combo
    updateAlignState(model == TableInfo.TABLE_MODEL_CALS || model == TableInfo.TABLE_MODEL_HTML);
  }
}