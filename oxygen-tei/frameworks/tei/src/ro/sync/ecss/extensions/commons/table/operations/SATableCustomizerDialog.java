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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;

/**
 * Dialog used to customize the insertion of a table (number of rows, columns, table caption).
 * It is used on standalone implementation.  
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class SATableCustomizerDialog extends OKCancelDialog implements TableCustomizerConstants {
  
  /**
   * Default number of columns for properties table.
   */
  private static final int DEFAULT_NO_OF_COLS_FOR_PROPERTIES_TABLE = 3;

  /**
   * If selected the user can specify the table title. 
   */
  protected JCheckBox titleCheckbox;
  
  /**
   * Text field for specify the table title.
   */
  protected JTextField titleTextField;
  
  /**
   * Used to specify the number of rows.
   */
  protected JSpinner rowsSpinner;
  
  /**
   * Used to specify the number of columns.
   */
  protected JSpinner columnsSpinner;
  
  /**
   * Used to specify how the column widths are generated. 
   * The column widths values can be fixed or proportional.
   */
  protected JComboBox colWidthsCombobox;
  
  /**
   * If selected an empty table header will be generated.
   */
  protected JCheckBox headerCheckbox;
  
  /**
   * If selected an empty table footer will be generated.
   */
  protected JCheckBox footerCheckbox;
  
  /**
   * Combo used to chose the table frame type.
   */
  protected JComboBox frameCombo;
  
  /**
   * Combo used to chose the table row separator value.
   */
  protected JComboBox rowsepCombo;
  
  /**
   * Combo used to chose the table column separator value.
   */
  protected JComboBox colsepCombo;
  
  /**
   * Combo used to chose the table align value.
   */
  protected JComboBox alignCombo;
  
  /**
   * <code>true</code> if the table that is customized by this dialog has a footer.
   */
  private final boolean hasFooter;
  
  /**
   * <code>true</code> if the table customized by this dialog has a frame attribute.
   */
  private final boolean hasFrameAttribute;
  
  /**
   * <code>true</code> if the table customized by this dialog has a row separator attribute.
   */
  private final boolean hasRowsepAttribute;
  
  /**
   * <code>true</code> if the table customized by this dialog has a column separator attribute.
   */
  private final boolean hasColsepAttribute;
  
  /**
   * <code>true</code> if the table customized by this dialog has an align attribute.
   */
  private final boolean hasAlignAttribute;
  
  /**
   * If <code>true</code> the table model chooser will be shown.
   * The table model can be CALS or HTML.
   */
  private boolean showModelChooser;
  
  /**
   * Radio button used to choose CALS table model.
   */
  protected JRadioButton calsModelRadio;
  /**
   * Either simple or HTML.
   */
  protected JRadioButton simpleOrHtmlModelRadio;
  /**
   * Properties model.
   */
  protected JRadioButton propertiesModelRadio;

  /**
   * Author resource bundle.
   */
  protected final AuthorResourceBundle authorResourceBundle;

  /**
   * The predefined number of columns.
   */
  private final int predefinedColumnsCount;

  /**
   * The predefined number of rows.
   */
  private final int predefinedRowsCount;

  /**
   * <code>true</code> if the model is for choice table.
   */
  private final boolean choiceTableModel;
  
  /**
   * <code>true</code> if the model is for CALS table.
   */
  private boolean isCalsTable;
  
  /**
   * <code>true</code> if the table type is simple, not HTML.
   */
  private final boolean isSimpleTableNotHtml;

  /**
   * <code>true</code> if the model is for properties table.
   */
  private boolean isPropertiesTable;

  /**
   * The model for the properties table column spinner.
   */
  private final SpinnerModel propertiesTableColSpinnerModel = new SpinnerNumberModel(
      TableInfo.DEFAULT_COLUMNS_COUNT_PROPERTIES_TABLE,
      TableInfo.MIN_COLUMNS_COUNT_PROPERTIES_TABLE,
      TableInfo.MAX_COLUMNS_COUNT_PROPERTIES_TABLE,
      1);
  /**
   * The default table column spinner model.
   */
  private final SpinnerModel defaultColSpinnerModel = new SpinnerNumberModel(
      TableInfo.DEFAULT_COLUMNS_COUNT, 
      TableInfo.MIN_COLUMNS_COUNT,
      TableInfo.MAX_COLUMNS_COUNT,
      1);

  /**
   * <code>true</code> if the model is for simple or HTML table, not CALS, nor properties.
   */
  private boolean isSimpleOrHtmlTable;

  /**
   * Constructor.
   * 
   * @param parentFrame           The parent {@link JFrame} of the dialog.
   * @param hasFooter             <code>true</code> if this table has a footer.
   * @param hasFrameAttribute     <code>true</code> if this table has a frame attribute.
   * @param showModelChooser      <code>true</code> to show the dialog panel for choosing the table
   *                              model, one of CALS or HTML.  
   * @param authorResourceBundle  Author resource bundle.
   * @param predefinedRowsCount The predefined number of rows.
   * @param predefinedColumnsCount The predefined number of columns.
   */
  public SATableCustomizerDialog(
      Frame parentFrame,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount, 
      int predefinedColumnsCount) {
    this(parentFrame, hasFooter, hasFrameAttribute, showModelChooser, false, false, false, false,
        false, authorResourceBundle, predefinedRowsCount, predefinedColumnsCount);
  }
  
  /**
   * Constructor.
   * 
   * @param parentFrame             The parent {@link JFrame} of the dialog.  
   * @param hasFooter               <code>true</code> if this table has a footer.
   * @param hasFrameAttribute       <code>true</code> if the table has a frame attribute.
   * @param showModelChooser        <code>true</code> to show the dialog panel for choosing the table
   *                                    model, one of CALS or HTML.
   * @param showSimpleModel         <code>true</code> to use the simple table model radio button instead of the HTML model.
   * @param innerCallsTable         <code>true</code> if this is an inner CALLS table.
   * @param hasRowsepAttribute      <code>true</code> if the table has a row separator attribute.
   *                                    Flag used to add a corresponding combo box in the dialog.
   * @param hasColsepAttribute      <code>true</code> if the table has a column separator attribute.
   *                                    Flag used to add a corresponding combo box in the dialog.
   * @param hasAlignAttribute       <code>true</code> if the table has an align attribute.
   *                                    Flag used to add a corresponding combo box in the dialog.
   * @param authorResourceBundle    Author resource bundle.
   * @param predefinedRowsCount     The predefined number of rows.
   * @param predefinedColumnsCount  The predefined number of columns.
   */
  public SATableCustomizerDialog(
      Frame parentFrame,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModel,
      boolean innerCallsTable,
      boolean hasRowsepAttribute,
      boolean hasColsepAttribute,
      boolean hasAlignAttribute,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount,
      int predefinedColumnsCount) { 
    this(parentFrame, hasFooter, hasFrameAttribute, showModelChooser, showSimpleModel, false,
         innerCallsTable, hasRowsepAttribute, hasColsepAttribute, hasAlignAttribute, 
         authorResourceBundle, predefinedRowsCount, predefinedColumnsCount);
  }
  
  /**
   * Constructor.
   * 
   * @param parentFrame             The parent {@link JFrame} of the dialog.  
   * @param hasFooter               <code>true</code> if this table has a footer.
   * @param hasFrameAttribute       <code>true</code> if the table has a frame attribute.
   * @param showModelChooser        <code>true</code> to show the dialog panel for choosing the table
   *                                    model, one of CALS or HTML.
   * @param showSimpleModel         <code>true</code> to use the simple table model radio button instead of the HTML model.
   * @param choiceTableModel        <code>true</code> to use the choice table model. 
   * @param innerCallsTable         <code>true</code> if this is an inner CALLS table.
   * @param hasRowsepAttribute      <code>true</code> if the table has a row separator attribute.
   *                                    Flag used to add a corresponding combo box in the dialog.
   * @param hasColsepAttribute      <code>true</code> if the table has a column separator attribute.
   *                                    Flag used to add a corresponding combo box in the dialog.
   * @param hasAlignAttribute       <code>true</code> if the table has an align attribute.
   *                                    Flag used to add a corresponding combo box in the dialog.
   * @param authorResourceBundle    Author resource bundle.
   * @param predefinedRowsCount     The predefined number of rows.
   * @param predefinedColumnsCount  The predefined number of columns.
   */
  public SATableCustomizerDialog(
      Frame parentFrame,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModel,
      boolean choiceTableModel,
      boolean innerCallsTable,
      boolean hasRowsepAttribute,
      boolean hasColsepAttribute,
      boolean hasAlignAttribute,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount,
      int predefinedColumnsCount) {
    this(parentFrame, hasFooter, hasFrameAttribute, showModelChooser, showSimpleModel, choiceTableModel, 
        true, innerCallsTable, hasRowsepAttribute, hasColsepAttribute, hasAlignAttribute, authorResourceBundle, 
        predefinedRowsCount, predefinedColumnsCount);
  }
  
  /**
   * Constructor.
   * 
   * @param parentFrame             The parent {@link JFrame} of the dialog.  
   * @param hasFooter               <code>true</code> if this table has a footer.
   * @param hasFrameAttribute       <code>true</code> if the table has a frame attribute.
   * @param showModelChooser        <code>true</code> to show the dialog panel for choosing the table
   *                                    model, one of CALS or HTML.
   * @param showSimpleModel         <code>true</code> to use the simple table model radio button instead of the HTML model.
   * @param choiceTableModel        <code>true</code> to show the dialog for choice table.
   * @param isCalsTable             <code>true</code> if the table model is CALS.
   * @param innerCallsTable         <code>true</code> if this is an inner CALLS table.
   * @param hasRowsepAttribute      <code>true</code> if the table has a row separator attribute.
   *                                    Flag used to add a corresponding combo box in the dialog.
   * @param hasColsepAttribute      <code>true</code> if the table has a column separator attribute.
   *                                    Flag used to add a corresponding combo box in the dialog.
   * @param hasAlignAttribute       <code>true</code> if the table has an align attribute.
   *                                    Flag used to add a corresponding combo box in the dialog.
   * @param authorResourceBundle    Author resource bundle.
   * @param predefinedRowsCount     The predefined number of rows.
   * @param predefinedColumnsCount  The predefined number of columns.
   */
  public SATableCustomizerDialog(
      Frame parentFrame,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModel,
      boolean choiceTableModel,
      boolean isCalsTable,
      boolean innerCallsTable,
      boolean hasRowsepAttribute,
      boolean hasColsepAttribute,
      boolean hasAlignAttribute,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount,
      int predefinedColumnsCount) {
    this(parentFrame, hasFooter, hasFrameAttribute, showModelChooser, showSimpleModel, choiceTableModel,
        isCalsTable, false, false, false, innerCallsTable, hasRowsepAttribute, hasColsepAttribute, hasAlignAttribute,
        authorResourceBundle, predefinedRowsCount, predefinedColumnsCount);
  }
  /**
   * Constructor.
   * 
   * @param parentFrame               The parent {@link JFrame} of the dialog.  
   * @param hasFooter                 <code>true</code> if this table has a footer.
   * @param hasFrameAttribute         <code>true</code> if the table has a frame attribute.
   * @param showModelChooser          <code>true</code> to show the dialog panel for choosing the table
   *                                      model, one of CALS or HTML.
   * @param showSimpleModel           <code>true</code> to use the simple table model radio instead of the HTML model.
   * @param choiceTableModel          <code>true</code> to show the dialog for choice table.
   * @param isCalsTable               <code>true</code> if the table model is CALS.
   * @param isSimpleOrHtmlTable       <code>true</code> if the model is for simple or HTML table, not CALS or properties.
   * @param isPropertiesTableAccepted <code>true</code> of a properties table is accepted.
   * @param isPropertiesTable         <code>true</code> if the current table has a properties table model.
   * @param innerCallsTable           <code>true</code> if this is an inner CALLS table.
   * @param hasRowsepAttribute        <code>true</code> if the table has a row separator attribute.
   *                                      Flag used to add a corresponding combo box in the dialog.
   * @param hasColsepAttribute        <code>true</code> if the table has a column separator attribute.
   *                                      Flag used to add a corresponding combo box in the dialog.
   * @param hasAlignAttribute         <code>true</code> if the table has an align attribute. 
   *                                      Flag used to add a corresponding combo box in the dialog.
   * @param authorResourceBundle      Author resource bundle.
   * @param predefinedRowsCount       The predefined number of rows.
   * @param predefinedColumnsCount    The predefined number of columns.
   */
  public SATableCustomizerDialog(
      Frame parentFrame,
      boolean hasFooter,
      boolean hasFrameAttribute,
      boolean showModelChooser,
      boolean showSimpleModel,
      boolean choiceTableModel,
      boolean isCalsTable,
      boolean isSimpleOrHtmlTable,
      boolean isPropertiesTableAccepted,
      boolean isPropertiesTable,
      boolean innerCallsTable,
      boolean hasRowsepAttribute,
      boolean hasColsepAttribute,
      boolean hasAlignAttribute,
      AuthorResourceBundle authorResourceBundle,
      int predefinedRowsCount,
      int predefinedColumnsCount) { 
    super(
        parentFrame,
        authorResourceBundle.getMessage(choiceTableModel ? ExtensionTags.INSERT_CHOICE_TABLE
            : ExtensionTags.INSERT_TABLE), true);
    this.hasFooter = hasFooter;
    this.hasFrameAttribute = hasFrameAttribute;
    this.showModelChooser = showModelChooser;
    this.isSimpleTableNotHtml = showSimpleModel;
    this.choiceTableModel = choiceTableModel;
    this.isCalsTable = isCalsTable;
    this.isSimpleOrHtmlTable = isSimpleOrHtmlTable;
    this.isPropertiesTable = isPropertiesTable;
    this.hasRowsepAttribute = hasRowsepAttribute;
    this.hasColsepAttribute = hasColsepAttribute;
    this.hasAlignAttribute = hasAlignAttribute;
    this.authorResourceBundle = authorResourceBundle;
    this.predefinedRowsCount = predefinedRowsCount;
    this.predefinedColumnsCount = predefinedColumnsCount;

    // EXM-25921 Insert dialog, so the text of the ok button should be 'Insert'.
    setOkButtonText(authorResourceBundle.getMessage(ExtensionTags.INSERT));
    JPanel mainPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gridBagConstr = new GridBagConstraints();
    gridBagConstr.gridx = 0;
    gridBagConstr.gridy = 0;
    gridBagConstr.fill = GridBagConstraints.BOTH;
    gridBagConstr.weightx = 1;
    gridBagConstr.anchor = GridBagConstraints.WEST;
    gridBagConstr.gridwidth = 2;
    gridBagConstr.insets = new Insets(0, 0, 0, 0);

    // Model chooser panel.
    JPanel modelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
    modelPanel.setBorder(
        BorderFactory.createTitledBorder(authorResourceBundle.getMessage(ExtensionTags.MODEL)));

    ButtonGroup buttonGroup = new ButtonGroup();
    // Radio button for choosing CALS table model
    calsModelRadio = new JRadioButton("CALS");
    calsModelRadio.setName("CALS model");
    calsModelRadio.addItemListener(new ItemListener() {
      /**
       * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
       */
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          addValuesToFrameCombo(TableInfo.TABLE_MODEL_CALS);
          // Update column widths combo
          updateColumnsWidthsCombo(getColumnWidthsSpecifications(TableInfo.TABLE_MODEL_CALS));
          colWidthsCombobox.setEnabled(true);
          updateTitleState(true);
          updateElementsState(true);
          updateAlignState(true);
        }
      }
    });
    modelPanel.add(calsModelRadio);
    buttonGroup.add(calsModelRadio);

    if (!choiceTableModel) {
      if (showSimpleModel) {
        // Radio button for choosing the simple table model
        simpleOrHtmlModelRadio = new JRadioButton(authorResourceBundle.getMessage(ExtensionTags.SIMPLE));
        simpleOrHtmlModelRadio.setName("Simple table model");
        simpleOrHtmlModelRadio.addItemListener(new ItemListener() {
          /**
           * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
           */
          @Override
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              // Update column widths combo
              updateColumnsWidthsCombo(getColumnWidthsSpecifications(TableInfo.TABLE_MODEL_DITA_SIMPLE));
              addValuesToFrameCombo(TableInfo.TABLE_MODEL_DITA_SIMPLE);
              colWidthsCombobox.setEnabled(true);
              updateTitleState(false);
              updateElementsState(false);
              updateAlignState(false);
            }
          }
        });
        modelPanel.add(simpleOrHtmlModelRadio);
        buttonGroup.add(simpleOrHtmlModelRadio);
      } else {
        // Radio button for choosing HTML table model
        simpleOrHtmlModelRadio = new JRadioButton("HTML");
        simpleOrHtmlModelRadio.setName("HTML model");
        simpleOrHtmlModelRadio.addItemListener(new ItemListener() {
          /**
           * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
           */
          @Override
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              // Update column widths combo
              updateColumnsWidthsCombo(getColumnWidthsSpecifications(TableInfo.TABLE_MODEL_HTML));
              addValuesToFrameCombo(TableInfo.TABLE_MODEL_HTML);
              updateElementsState(false);
              updateAlignState(true);
            }
          }
        });
        modelPanel.add(simpleOrHtmlModelRadio);
        buttonGroup.add(simpleOrHtmlModelRadio);
      }
      
      if (isPropertiesTableAccepted) {
        // Radio button for choosing properties table model
        propertiesModelRadio = new JRadioButton(authorResourceBundle.getMessage(ExtensionTags.PROPERTIES));
        propertiesModelRadio.setName("Properties");
        propertiesModelRadio.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              addValuesToFrameCombo(TableInfo.TABLE_MODEL_DITA_SIMPLE);
              colWidthsCombobox.setEnabled(false);
              updateTitleState(false);
              updateElementsState(false);
              updateAlignState(false);
              Integer cols = (Integer) columnsSpinner.getValue();
              columnsSpinner.setModel(propertiesTableColSpinnerModel);
              if(cols < 2 || cols > 3) {
                // Force to 3.
                cols = 3;
              }
              columnsSpinner.setValue(cols);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
              Object value = columnsSpinner.getValue();
              columnsSpinner.setModel(defaultColSpinnerModel);
              // Keep the value
              columnsSpinner.setValue(value);
            }
          }
        });
        modelPanel.add(propertiesModelRadio);
        buttonGroup.add(propertiesModelRadio);
      }
    } else {
      // Choice table model
      this.showModelChooser = false;
      modelPanel.setVisible(false);
    }

    int tableModelType = innerCallsTable ? TableInfo.TABLE_MODEL_CALS : TableInfo.TABLE_MODEL_CUSTOM;
    if (showModelChooser) {
      // Model chooser panel must be visible
      mainPanel.add(modelPanel, gridBagConstr);      
      tableModelType = TableInfo.TABLE_MODEL_CALS;
    }

    if(! innerCallsTable && !choiceTableModel) {
      // Title check box
      titleCheckbox = createTitleCheckbox();    
      titleCheckbox.addItemListener(new ItemListener() {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
          titleTextField.setEditable(titleCheckbox.isSelected());
        }
      });

      titleCheckbox.setBorder(BorderFactory.createEmptyBorder());
      titleCheckbox.getAccessibleContext().setAccessibleName("Create a title element for the table");
      gridBagConstr.gridy ++;
      gridBagConstr.gridx = 0;
      gridBagConstr.weightx = 0;
      gridBagConstr.gridwidth = 1;
      gridBagConstr.insets = new Insets(7, 0, 0, 0);
      gridBagConstr.fill = GridBagConstraints.NONE;
      mainPanel.add(titleCheckbox, gridBagConstr);

      // Title text field
      titleTextField = new JTextField();
      titleTextField.setName("Title text field");
      titleTextField.getAccessibleContext().setAccessibleName("Table Title");
      gridBagConstr.gridx ++;
      gridBagConstr.weightx = 1;
      gridBagConstr.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstr.insets = new Insets(7, 5, 0, 0);
      mainPanel.add(titleTextField, gridBagConstr);
    }

    JPanel sizePanel = new JPanel(new GridLayout(1, 2));
    sizePanel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(authorResourceBundle.getMessage(ExtensionTags.TABLE_SIZE)),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)));

    gridBagConstr.gridy ++;
    gridBagConstr.gridx = 0;
    gridBagConstr.weightx = 1;
    gridBagConstr.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstr.gridwidth = 2;
    gridBagConstr.insets = new Insets(7, 0, 0, 0);
    mainPanel.add(sizePanel, gridBagConstr);

    JPanel rowsPanel = new JPanel(new GridBagLayout());
    sizePanel.add(rowsPanel);

    // 'Rows' label
    JLabel rowsLabel = new JLabel(authorResourceBundle.getMessage(ExtensionTags.ROWS) + ":");
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.weightx = 0;
    c.weighty = 0;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 0, 0, 0);
    rowsPanel.add(rowsLabel, c);

    // Number of rows text field
    rowsSpinner = new JSpinner();
    rowsLabel.setLabelFor(rowsSpinner);
    rowsSpinner.setName("Rows spinner");
    rowsSpinner.setModel(new SpinnerNumberModel(2, TableInfo.MIN_ROWS_COUNT, 1000, 1));
    if (predefinedColumnsCount >= 0) {
      rowsSpinner.setValue(predefinedRowsCount);
    } else {
      rowsSpinner.setValue(TableInfo.DEFAULT_ROWS_COUNT);
    }
    c.gridx++;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets.left = 5;
    // Leave some space to the right of the component. The same will be to the left of the right column component.
    c.insets.right = 8;
    rowsPanel.add(rowsSpinner, c);

    // 'Columns' label
    JPanel columnsPanel = new JPanel(new GridBagLayout());
    sizePanel.add(columnsPanel);

    JLabel columnsLabel = new JLabel(authorResourceBundle.getMessage(ExtensionTags.COLUMNS) + ":");
    c = new GridBagConstraints();
    c.gridx= 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.weightx = 0;
    c.weighty = 0;
    c.fill = GridBagConstraints.NONE;
    // Leave some space to the left, the same as in the right side of the component from the left column.
    c.insets = new Insets(0, 8, 0, 0);
    columnsPanel.add(columnsLabel, c);

    // Number of columns text field
    columnsSpinner = new JSpinner();
    columnsLabel.setLabelFor(columnsSpinner);
    columnsSpinner.setName("Columns spinner");
    columnsSpinner.setModel(defaultColSpinnerModel);
    if (predefinedColumnsCount >= 0) {
      columnsSpinner.setValue(predefinedColumnsCount);
    } else {
      columnsSpinner.setValue(TableInfo.DEFAULT_COLUMNS_COUNT);
    }
    c.gridx++;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets.left = 5;
    columnsPanel.add(columnsSpinner, c);

    if (choiceTableModel) {
      columnsSpinner.setEnabled(false);
      columnsSpinner.setValue(TableInfo.DEFAULT_COLUMNS_COUNT_CHOICE_TABLE);
    }

    JPanel headerFooterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    gridBagConstr.gridx = 0;
    gridBagConstr.gridy ++;
    gridBagConstr.gridwidth = 2;
    gridBagConstr.weightx = 1;
    gridBagConstr.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstr.insets = new Insets(7, 0, 0, 0);
    mainPanel.add(headerFooterPanel, gridBagConstr);

    // 'Header' check box
    headerCheckbox = new JCheckBox(authorResourceBundle.getMessage(ExtensionTags.GENERATE_TABLE_HEADER));
    headerCheckbox.setName("Header checkbox");
    headerCheckbox.setBorder(BorderFactory.createEmptyBorder());
    headerFooterPanel.add(headerCheckbox);

    if (hasFooter) {
      headerFooterPanel.add(Box.createHorizontalStrut(16));
      // 'Footer' check box
      footerCheckbox = new JCheckBox(authorResourceBundle.getMessage(ExtensionTags.GENERATE_TABLE_FOOTER));
      footerCheckbox.setName("Footer checkbox");
      footerCheckbox.setBorder(BorderFactory.createEmptyBorder());
      headerFooterPanel.add(footerCheckbox);
    }

    // Column widths
    ColumnWidthsType[] columnsWidths = getColumnWidthsSpecifications(tableModelType);
    if (columnsWidths != null) {
      // Column widths label
      JLabel colWidthsLabel = new JLabel(authorResourceBundle.getMessage(ExtensionTags.COLUMN_WIDTHS) + ":");
      gridBagConstr.gridx = 0;
      gridBagConstr.gridy ++;
      gridBagConstr.gridwidth = 1;
      gridBagConstr.weightx = 0;
      gridBagConstr.fill = GridBagConstraints.NONE;
      gridBagConstr.insets = new Insets(7, 0, 0, 0);
      mainPanel.add(colWidthsLabel, gridBagConstr);

      // Column widths combo box
      colWidthsCombobox = new JComboBox();
      colWidthsLabel.setLabelFor(colWidthsCombobox);
      colWidthsCombobox.setName("Column Widths combo");
      final ListCellRenderer initRenderer = colWidthsCombobox.getRenderer();
      colWidthsCombobox.setRenderer(new ListCellRenderer() {
        /**
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
          // Render column widths
          String render = null;
          if (ColumnWidthsType.DYNAMIC_COL_WIDTHS == value) {
            render = COLS_DYNAMIC; 
          } else if (ColumnWidthsType.PROPORTIONAL_COL_WIDTHS == value) {
            render = COLS_PROPORTIONAL; 
          } if (ColumnWidthsType.FIXED_COL_WIDTHS == value) {
            render = COLS_FIXED; 
          }
          return initRenderer.getListCellRendererComponent(list, render, index, isSelected, cellHasFocus);
        }
      });
      // Update column widths combo
      updateColumnsWidthsCombo(columnsWidths);

      gridBagConstr.gridx ++;
      gridBagConstr.weightx = 1;
      gridBagConstr.insets = new Insets(7, 5, 0, 0);
      gridBagConstr.fill = GridBagConstraints.HORIZONTAL;
      // Add column widths combo
      mainPanel.add(colWidthsCombobox, gridBagConstr);
    }


    if (hasFrameAttribute) {
      // 'Frame' label
      JLabel frameLabel = new JLabel(authorResourceBundle.getMessage(ExtensionTags.FRAME) + ":");
      gridBagConstr.gridx = 0;
      gridBagConstr.gridy ++;
      gridBagConstr.gridwidth = 1;
      gridBagConstr.weightx = 0;
      gridBagConstr.insets = new Insets(7, 0, 0, 0);
      gridBagConstr.fill = GridBagConstraints.NONE;
      mainPanel.add(frameLabel, gridBagConstr);

      // Frame combo box
      frameCombo = new JComboBox();
      frameLabel.setLabelFor(frameCombo);
      frameCombo.setName("Frame combo");
      addValuesToFrameCombo(tableModelType);

      gridBagConstr.gridx ++;
      gridBagConstr.weightx = 1;
      gridBagConstr.insets.left = 5;
      gridBagConstr.fill = GridBagConstraints.HORIZONTAL;
      mainPanel.add(frameCombo, gridBagConstr);
    }

    if (hasRowsepAttribute) {
      // 'Rowsep' label
      JLabel rowsepLabel = new JLabel(authorResourceBundle.getMessage(ExtensionTags.ROW_SEPARATOR) + ":");
      gridBagConstr.gridx = 0;
      gridBagConstr.gridy ++;
      gridBagConstr.gridwidth = 1;
      gridBagConstr.weightx = 0;
      gridBagConstr.insets = new Insets(7, 0, 0, 0);
      gridBagConstr.fill = GridBagConstraints.NONE;
      mainPanel.add(rowsepLabel, gridBagConstr);

      // Rowsep combo box
      rowsepCombo = new JComboBox();
      rowsepLabel.setLabelFor(rowsepCombo);
      rowsepCombo.setName("Rowsep combo");
      addValuesToRowsepCombo(tableModelType);

      gridBagConstr.gridx ++;
      gridBagConstr.weightx = 1;
      gridBagConstr.insets.left = 5;
      gridBagConstr.fill = GridBagConstraints.HORIZONTAL;
      mainPanel.add(rowsepCombo, gridBagConstr);
    }

    if (hasColsepAttribute) {
      // 'Colsep' label
      JLabel colsepLabel = new JLabel(authorResourceBundle.getMessage(ExtensionTags.COLUMN_SEPARATOR) + ":");
      gridBagConstr.gridx = 0;
      gridBagConstr.gridy ++;
      gridBagConstr.gridwidth = 1;
      gridBagConstr.weightx = 0;
      gridBagConstr.insets = new Insets(7, 0, 0, 0);
      gridBagConstr.fill = GridBagConstraints.NONE;
      mainPanel.add(colsepLabel, gridBagConstr);

      // Column separator combo box
      colsepCombo = new JComboBox();
      colsepLabel.setLabelFor(colsepCombo);
      colsepCombo.setName("Colsep combo");
      addValuesToColsepCombo(tableModelType);

      gridBagConstr.gridx ++;
      gridBagConstr.weightx = 1;
      gridBagConstr.insets.left = 5;
      gridBagConstr.fill = GridBagConstraints.HORIZONTAL;
      mainPanel.add(colsepCombo, gridBagConstr);
    }

    if (hasAlignAttribute) {
      // 'Align' label
      JLabel alignLabel = new JLabel(authorResourceBundle.getMessage(ExtensionTags.ALIGNMENT) + ":");
      gridBagConstr.gridx = 0;
      gridBagConstr.gridy ++;
      gridBagConstr.gridwidth = 1;
      gridBagConstr.weightx = 0;
      gridBagConstr.insets = new Insets(7, 0, 0, 0);
      gridBagConstr.fill = GridBagConstraints.NONE;
      mainPanel.add(alignLabel, gridBagConstr);

      // Align combo box
      alignCombo = new JComboBox();
      alignLabel.setLabelFor(alignCombo);
      alignCombo.setName("Align combo");
      addValuesToAlignCombo(tableModelType);

      gridBagConstr.gridx ++;
      gridBagConstr.weightx = 1;
      gridBagConstr.insets.left = 5;
      gridBagConstr.fill = GridBagConstraints.HORIZONTAL;
      mainPanel.add(alignCombo, gridBagConstr);
    }

    //Add the main panel - leave some extra space to the bottom
    mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
    getContentPane().add(mainPanel, BorderLayout.CENTER);

    pack();
    setSize(340, getSize().height);
    setResizable(false);
  }
  
  /**
   * @param columnsWidthsSpecifications The column widths specifications.
   */
  protected void updateColumnsWidthsCombo(ColumnWidthsType[] columnsWidthsSpecifications) {
    if (colWidthsCombobox != null) {
      Object previousSel = colWidthsCombobox.getSelectedItem();
      colWidthsCombobox.removeAllItems();
      int selIndex = -1;
      for (int i = 0; i < columnsWidthsSpecifications.length; i++) {
        // Add combo item
        colWidthsCombobox.addItem(columnsWidthsSpecifications[i]);
        if(columnsWidthsSpecifications[i].equals(previousSel)) {
          selIndex = i;
        }
      }
      if(selIndex != -1) {
        colWidthsCombobox.setSelectedIndex(selIndex);
      }
    }
  }

  /**
   * Update the enabled state of the title section.
   * 
   * @param enabled <code>true</code> if the title is enabled.
   */
  private void updateTitleState(boolean enabled) {
    if(titleCheckbox != null) {
      titleCheckbox.setEnabled(enabled);
      // EXM-35014 If the title checkbox is disabled, also uncheck it
      titleCheckbox.setSelected(enabled);
      titleTextField.setEditable(enabled && titleCheckbox.isSelected());
    }
  }
  
  /**
   * Update the state for row separator and column separator attributes combos.
   * 
   * @param enabled <code>true</code> if the combos will be enabled.
   */
  private void updateElementsState(boolean enabled) {
    if(rowsepCombo != null) {
      rowsepCombo.setEnabled(enabled);
    }
    
    if(colsepCombo != null) {
      colsepCombo.setEnabled(enabled);
    }
  }
  
  /**
   * Update the enabled state of the align combo.
   * 
   * @param enabled <code>true</code> if the align combo is enabled.
   */
  private void updateAlignState(boolean enabled) {
    if(alignCombo != null) {
      alignCombo.setEnabled(enabled);
    }
  }

  /**
   * Compute the possible values for the <code>frame</code> attribute.
   * 
   * @param tableModelType The table model type. 
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return Returns the possible values for the <code>frame</code> attribute. 
   */
  protected abstract String[] getFrameValues(int tableModelType);
  
  /**
   * Get the default frame value.
   * 
   *  @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return The default frame value.
   */
  protected abstract String getDefaultFrameValue(int tableModelType);
  
  /**
   * Compute the possible values for the <code>rowsep</code> attribute.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return Returns the possible values for the <code>rowsep</code> attribute. 
   */
  protected abstract String[] getRowsepValues(int tableModelType);
  
  /**
   * Get the default rowsep value.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return The default row separator value.
   */
  protected abstract String getDefaultRowsepValue(int tableModelType);
  
  /**
   * Compute the possible values for the <code>colsep</code> attribute.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * @return Returns the possible values for the <code>colsep</code> attribute. 
   */
  protected abstract String[] getColsepValues(int tableModelType);
  
  /**
   * Compute the possible values for the <code>align</code> attribute.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return Returns the possible values for the <code>align</code> attribute. 
   */
  protected abstract String[] getAlignValues(int tableModelType);
  
  /**
   * Get the default column separator value.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return The default column separator value.
   */
  protected abstract String getDefaultColsepValue(int tableModelType);
  
  /**
   * Get the default alignment value.
   * 
   * @param tableModelType The table model type.
   * One of the constants: 
   * {@link TableInfo#TABLE_MODEL_CALS}, {@link TableInfo#TABLE_MODEL_CUSTOM},
   * {@link TableInfo#TABLE_MODEL_DITA_SIMPLE}, {@link TableInfo#TABLE_MODEL_HTML}.
   * 
   * @return The default align value.
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
  protected abstract ColumnWidthsType[] getColumnWidthsSpecifications(int tableModelType);

  /**
   * Creates the title checkbox with an implementation specific name.
   * 
   * @return The title checkbox customized according to implementation.
   */
  protected abstract JCheckBox createTitleCheckbox();

  /**
   * Add the possible values to the frame combo.
   * 
   * @param tableModelType The table model type.
   */
  private void addValuesToFrameCombo(int tableModelType) {
    Object sel = frameCombo.getSelectedItem();
    String[] frameValues = getFrameValues(tableModelType);
    frameCombo.removeAllItems();
    int selIndex = -1;
    for (int i = 0; i < frameValues.length; i++) {
      frameCombo.addItem(frameValues[i]);
      if(frameValues[i] != null && frameValues[i].equals(sel)) {
        selIndex = i;
      }
    }
    if(selIndex != -1) {
      frameCombo.setSelectedIndex(selIndex);
    } else {
      frameCombo.setSelectedItem(getDefaultFrameValue(tableModelType));
    }
  }
  
  /**
   * Add the possible values to the row separator combo.
   * 
   * @param tableModelType The table model type.
   */
  private void addValuesToRowsepCombo(int tableModelType) {
    String[] values = getRowsepValues(tableModelType);
    rowsepCombo.removeAllItems();
    for (int i = 0; i < values.length; i++) {
      rowsepCombo.addItem(values[i]);
    }

    rowsepCombo.setSelectedItem(getDefaultRowsepValue(tableModelType));
  }
  
  /**
   * Add the possible values to the column separator combo.
   * 
   * @param tableModelType The table model type.
   */
  private void addValuesToColsepCombo(int tableModelType) {
    String[] values = getColsepValues(tableModelType);
    colsepCombo.removeAllItems();
    for (int i = 0; i < values.length; i++) {
      colsepCombo.addItem(values[i]);
    }

    colsepCombo.setSelectedItem(getDefaultColsepValue(tableModelType));
  }

  /**
   * Add the possible values to the align combo.
   * 
   * @param tableModelType The table model type.
   */
  private void addValuesToAlignCombo(int tableModelType) {
    String[] values = getAlignValues(tableModelType);
    alignCombo.removeAllItems();
    for (int i = 0; i < values.length; i++) {
      alignCombo.addItem(values[i]);
    }

    alignCombo.setSelectedItem(getDefaultAlignValue(tableModelType));
  }
  /**
   * Show the dialog to customize the table attributes.
   * @param previousTableInfo Table info to be used for initializing the controls.
   * 
   * @return The {@link TableInfo} object with informations about the table 
   * to be inserted. 
   * If <code>null</code> then the user canceled the table insertion.
   */
  public TableInfo showDialog(TableInfo previousTableInfo) {
    initialize(previousTableInfo);
    
    super.setVisible(true);
    
    TableInfo tableInfo = null;
    if(getResult() == RESULT_OK) {
      String title = null;
      if(titleCheckbox != null && titleCheckbox.isSelected()) {
        title = titleTextField.getText();
      }
      int rowsNumber = ((Integer)rowsSpinner.getValue()).intValue();
      int columnsNumber = ((Integer)columnsSpinner.getValue()).intValue();
      // Compute the value of the table model
      int tableModelType = getTableModelType();
      tableInfo = 
        new TableInfo(
            title, 
            rowsNumber, 
            columnsNumber, 
            headerCheckbox.isSelected(), 
            hasFooter? footerCheckbox.isSelected() : false, 
            // EXM-23110 If the user chose "<unspecified>" value for Frame 
            // attribute, don't insert any frame attribute into table element
            hasFrameAttribute
              ? (!UNSPECIFIED.equals(frameCombo.getSelectedItem())
                    ? (String) frameCombo.getSelectedItem()
                    : null)
              : null,
            tableModelType, 
            colWidthsCombobox != null ? (ColumnWidthsType) colWidthsCombobox.getSelectedItem() : null, 
            // EXM-29536 Add row separator and column separator  
            hasRowsepAttribute
            ? (!UNSPECIFIED.equals(rowsepCombo.getSelectedItem())
                  ? (String) rowsepCombo.getSelectedItem()
                  : null)
            : null, 
            hasColsepAttribute
            ? (!UNSPECIFIED.equals(colsepCombo.getSelectedItem())
                ? (String) colsepCombo.getSelectedItem()
                : null)
              : null,
            hasAlignAttribute
              ? (!UNSPECIFIED.equals(alignCombo.getSelectedItem())
                  ? (String) alignCombo.getSelectedItem()
                  : null)
                : null);
    } else {
      // Cancel was pressed
    }

    return tableInfo;
  }

  /**
   * Obtain the table model type.
   * 
   * @return The table model type.
   */
  protected int getTableModelType() {
    int tableModelType = TableInfo.TABLE_MODEL_CUSTOM;
    if(showModelChooser) {
      if (calsModelRadio.isSelected()) {
        tableModelType = TableInfo.TABLE_MODEL_CALS;
      } else {
        if (propertiesModelRadio != null && propertiesModelRadio.isSelected()) {
          tableModelType = TableInfo.TABLE_MODEL_DITA_PROPERTIES;
        } else {
          // Simple or HTML model selected
          if (isSimpleTableNotHtml) {
            tableModelType = TableInfo.TABLE_MODEL_DITA_SIMPLE;
          } else {
            tableModelType = TableInfo.TABLE_MODEL_HTML;
          }
        }
      }
    } else if (choiceTableModel) {
      tableModelType = TableInfo.TABLE_MODEL_DITA_CHOICE;
    }
    return tableModelType;
  }

  /**
   * Initialize the controls.
   * 
   * @param previousTableInfo If <code>null</code> defaults will be used. Otherwise, the controls
   * will be initialized with values from this info.
   */
  private void initialize(TableInfo previousTableInfo) {
    // Reset components to default values
    if(titleCheckbox != null) {
      titleTextField.setEditable(true);
      titleTextField.setText("");
    }
    
    if (choiceTableModel) {
      updateColumnsWidthsCombo(getColumnWidthsSpecifications(TableInfo.TABLE_MODEL_DITA_CHOICE));
      addValuesToFrameCombo(TableInfo.TABLE_MODEL_DITA_CHOICE);
    }
    
    if (previousTableInfo != null) {
      if (showModelChooser) {
        if (isCalsTable 
            || previousTableInfo.getTableModel() == TableInfo.TABLE_MODEL_CALS 
                && !isPropertiesTable 
                && !isSimpleOrHtmlTable) {
          calsModelRadio.setSelected(true);
        } else if (isSimpleOrHtmlTable 
            || previousTableInfo.getTableModel() != TableInfo.TABLE_MODEL_CALS
                && previousTableInfo.getTableModel() != TableInfo.TABLE_MODEL_DITA_PROPERTIES
                && !isCalsTable 
                && !isPropertiesTable) {
          simpleOrHtmlModelRadio.setSelected(true);
        } else if (propertiesModelRadio != null 
            && (isPropertiesTable 
                || previousTableInfo.getTableModel() == TableInfo.TABLE_MODEL_DITA_PROPERTIES 
                    && !isCalsTable
                    && !isSimpleOrHtmlTable)) {
          propertiesModelRadio.setSelected(true);
        } else {
          // This may happen when the previous model was "properties", 
          // but in the meantime we moved to a document that doesn't accept
          // a properties table. Select CALS by default.
          calsModelRadio.setSelected(true);
        }
      }
      
      // Title check box. It's important to set the "Title" check box selection
      // after selecting the model radio button, otherwise the listeners added
      // to the model radio buttons will mess up with the check box.
      if(titleCheckbox != null) {
        if (previousTableInfo.getTitle() != null) {
          titleCheckbox.setSelected(true);
        } else {
          titleCheckbox.setSelected(false);
          titleTextField.setEditable(false);
        }
      }

      if (predefinedColumnsCount < 0 || predefinedRowsCount < 0) {
        // Set the default number of rows and columns
        rowsSpinner.setValue(previousTableInfo.getRowsNumber());
        int colsNumber = previousTableInfo.getColumnsNumber();
        if (!choiceTableModel) {
          if (propertiesModelRadio != null 
              && (isPropertiesTable 
                  || previousTableInfo.getTableModel() == TableInfo.TABLE_MODEL_DITA_PROPERTIES 
                      && !isCalsTable
                      && !isSimpleOrHtmlTable)) {
            // Properties table.
            if(colsNumber < 2 || colsNumber > 3) {
              // Force to 3.
              colsNumber = 3;
            }
          }
          columnsSpinner.setValue(colsNumber);
        }
      }

      // Header and footer
      headerCheckbox.setSelected(previousTableInfo.isGenerateHeader());
      if (hasFooter) {
        footerCheckbox.setSelected(previousTableInfo.isGenerateFooter());
      }
      
      if (colWidthsCombobox != null) {
        colWidthsCombobox.setSelectedItem(previousTableInfo.getColumnsWidthsType());
      }
      
      if (frameCombo != null) {
        if (previousTableInfo.getFrame() != null) {
          frameCombo.setSelectedItem(previousTableInfo.getFrame());
        } else {
          // EXM-23110 The user previously chose default value for frame attribute.
          frameCombo.setSelectedItem(UNSPECIFIED);
        }
      }
      // Rowsep
      if (rowsepCombo != null) {
        if (previousTableInfo.getRowsep() != null) {
          rowsepCombo.setSelectedItem(previousTableInfo.getRowsep());
        } else {
          rowsepCombo.setSelectedItem(UNSPECIFIED);
        }
      }
      
      // Colsep
      if (colsepCombo != null) {
        if (previousTableInfo.getColsep() != null) {
          colsepCombo.setSelectedItem(previousTableInfo.getColsep());
        } else {
          colsepCombo.setSelectedItem(UNSPECIFIED);
        }
      }
      
      // Alignment
      if (alignCombo != null) {
        if (previousTableInfo.getAlign() != null) {
          alignCombo.setSelectedItem(previousTableInfo.getAlign());
        } else {
          alignCombo.setSelectedItem(UNSPECIFIED);
        }
      }
    } else {
      if(titleCheckbox != null) {
        titleCheckbox.setSelected(true);
      }

      if (showModelChooser) {
        if (isCalsTable) {
          calsModelRadio.setSelected(true);
        } else if (isSimpleOrHtmlTable) {
          simpleOrHtmlModelRadio.setSelected(true);
        } else if (isPropertiesTable) {
          propertiesModelRadio.setSelected(true);
        } else {
          // No default table model. Select CALS by default.
          calsModelRadio.setSelected(true);
        }
      }
      
      if (predefinedColumnsCount < 0 || predefinedRowsCount < 0) {
        // Set the default number of rows and columns
        rowsSpinner.setValue(TableInfo.DEFAULT_ROWS_COUNT);
        if (isPropertiesTable) {
          columnsSpinner.setValue(DEFAULT_NO_OF_COLS_FOR_PROPERTIES_TABLE);
        } else {
          columnsSpinner.setValue(TableInfo.DEFAULT_COLUMNS_COUNT);
        }
      }

      // Header and footer
      headerCheckbox.setSelected(true);
      if (hasFooter) {
        footerCheckbox.setSelected(false);
      }
    }
    if(titleCheckbox != null) {
      // Request focus in title field
      titleTextField.requestFocus();
    }
  }
  
  /**
   * @return Returns the propertiesModelRadio.
   */
  public JRadioButton getPropertiesModelRadio() {
    return propertiesModelRadio;
  }

  /**
   * @return Returns the columnsSpinner.
   */
  public JSpinner getColumnsSpinner() {
    return columnsSpinner;
  }
  
  /**
   * @return Returns the headerCheckbox.
   */
  public JCheckBox getHeaderCheckbox() {
    return headerCheckbox;
  }
  
  /**
   * @return Returns the colWidthsCombobox.
   */
  public JComboBox getColWidthsCombobox() {
    return colWidthsCombobox;
  }
  
  /**
   * @return Returns the frameCombo.
   */
  public JComboBox getFrameCombo() {
    return frameCombo;
  }
  
  /**
   * @return Returns the colsepCombo.
   */
  public JComboBox getColsepCombo() {
    return colsepCombo;
  }
  
  /**
   * @return Returns the rowsepCombo.
   */
  public JComboBox getRowsepCombo() {
    return rowsepCombo;
  }
  
  /**
   * @return Returns the alignCombo.
   */
  public JComboBox getAlignCombo() {
    return alignCombo;
  }
  
  /**
   * @return Returns the titleCheckbox.
   */
  public JCheckBox getTitleCheckbox() {
    return titleCheckbox;
  }
  
  /**
   * @return Returns the titleTextField.
   */
  public JTextField getTitleTextField() {
    return titleTextField;
  }
  
  /**
   * @return Returns the rowsSpinner.
   */
  public JSpinner getRowsSpinner() {
    return rowsSpinner;
  }
  
  /**
   * @return Returns the calsModelRadio.
   */
  public JRadioButton getCalsModelRadio() {
    return calsModelRadio;
  }
  
  /**
   * @return Returns the simpleOrHtmlModelRadio.
   */
  public JRadioButton getSimpleOrHtmlModelRadio() {
    return simpleOrHtmlModelRadio;
  }
  
  /**
   * @return Returns the footer Checkbox.
   */
  public JCheckBox getFooterCheckbox() {
    return footerCheckbox;
  }
}