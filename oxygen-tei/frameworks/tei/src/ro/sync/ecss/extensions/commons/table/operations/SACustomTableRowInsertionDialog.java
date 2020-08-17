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
package ro.sync.ecss.extensions.commons.table.operations;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;

/**
 * Dialog displayed when trying to customize row insertion (using "Insert Rows...").
 * It is used on standalone implementation. 
 * 
 * @author sorin_carbunaru
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class SACustomTableRowInsertionDialog extends OKCancelDialog {
  
  /**
   * Number of rows to be inserted.
   */
  private JSpinner rowsSpinner;
  
  /**
   * "Above" radio button. If selected, the row(s) will be inserted above the current location.
   */
  private JRadioButton aboveRadioButton;
  
  /**
   * "Below" radio button. If selected, the row(s) will be inserted below the current location.
   */
  private JRadioButton belowRadioButton;
  
  /**
   * Constructor.
   * @param parentFrame the parent frame
   * @param resourceBundle The resource bundle.
   */
  public SACustomTableRowInsertionDialog(JFrame parentFrame, AuthorResourceBundle resourceBundle) {
    super(parentFrame, resourceBundle.getMessage(ExtensionTags.INSERT_ROWS), true);
    setOkButtonText(resourceBundle.getMessage(ExtensionTags.INSERT));
    
    // main panel
    JPanel mainPanel = new JPanel(new GridBagLayout());
    
    // no. of rows label
    JLabel numberOfRowsLabel = new JLabel(resourceBundle.getMessage(ExtensionTags.NUMBER_OF_ROWS) + ":");
    GridBagConstraints gridBagConstr = new GridBagConstraints();
    gridBagConstr.gridx = 0;
    gridBagConstr.gridy = 0;
    mainPanel.add(numberOfRowsLabel, gridBagConstr);
    
    // spinner for providing the number of rows to be inserted
    rowsSpinner = new JSpinner();
    rowsSpinner.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    rowsSpinner.setPreferredSize(new Dimension(110, (int) rowsSpinner.getPreferredSize().getHeight()));
    gridBagConstr = new GridBagConstraints();
    gridBagConstr.gridx = 1;
    gridBagConstr.gridy = 0;
    mainPanel.add(rowsSpinner, gridBagConstr);
    
    // position label
    JLabel positionLabel = new JLabel(resourceBundle.getMessage(ExtensionTags.POSITION) + ":");
    gridBagConstr = new GridBagConstraints();
    gridBagConstr.gridx = 0;
    gridBagConstr.gridy = 1;
    gridBagConstr.anchor = GridBagConstraints.WEST;
    mainPanel.add(positionLabel, gridBagConstr);
    
    // "above" radio button
    aboveRadioButton = new JRadioButton(resourceBundle.getMessage(ExtensionTags.ABOVE));
    aboveRadioButton.setSelected(false);
    
    // "below" radio button
    belowRadioButton = new JRadioButton(resourceBundle.getMessage(ExtensionTags.BELOW));
    belowRadioButton.setSelected(true);
    
    // group the radio buttons
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(aboveRadioButton);
    buttonGroup.add(belowRadioButton);
    
    // add the radio buttons to the position panel
    JPanel positionPanel = new JPanel();
    positionPanel.add(aboveRadioButton);
    positionPanel.add(belowRadioButton);
    
    // add the position panel to the main one
    gridBagConstr = new GridBagConstraints();
    gridBagConstr.gridx = 1;
    gridBagConstr.gridy = 1;
    mainPanel.add(positionPanel, gridBagConstr);
    
    //Add the main panel
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    
    pack();
    setResizable(false);
    
  }
  
  /**
   * Show the dialog to customize the row insertion.
   * @param previousTableRowsInfo the previous row information
   * @return The {@link TableRowsInfo} object with informations about the row(s) 
   * to be inserted. 
   * If <code>null</code>, then the user canceled the custom row insertion.
   * 
   */
  public TableRowsInfo showDialog(TableRowsInfo previousTableRowsInfo) {
    initialize(previousTableRowsInfo);
    //Presents dialog to user.
    super.setVisible(true);
    
    TableRowsInfo tableRowInfo = null;
    // get info from user
    if(getResult() == RESULT_OK) {
      int rowsNumber = ((Integer)rowsSpinner.getValue()).intValue();
      boolean insertBelow = belowRadioButton.isSelected();
      tableRowInfo = new TableRowsInfo(rowsNumber, insertBelow);
    } else {
      // Cancel was pressed
    }
    return tableRowInfo;
  }
  
  /**
   * Initialize the controls.
   * 
   * @param previousTableRowsInfo If <code>null</code>, defaults will be used. Otherwise, the controls
   * will be initialized with values from this info.
   */
  private void initialize(TableRowsInfo previousTableRowsInfo) {
    if(previousTableRowsInfo == null) {
      previousTableRowsInfo = new TableRowsInfo();
    }
    // use available info when initializing the dialog
    rowsSpinner.setValue(previousTableRowsInfo.getRowsNumber());

    aboveRadioButton.setSelected(!previousTableRowsInfo.isInsertBelow());
    belowRadioButton.setSelected(previousTableRowsInfo.isInsertBelow());
  }

}
