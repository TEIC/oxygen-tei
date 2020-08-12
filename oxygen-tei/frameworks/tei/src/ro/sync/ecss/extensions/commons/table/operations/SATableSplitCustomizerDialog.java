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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog;

/**
 * Dialog that allows the user to choose the information necessary for the Split operation.
 * 
 * @author adriana_sbircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class SATableSplitCustomizerDialog extends OKCancelDialog {
  /**
   * Columns number chooser.
   */
  private JSpinner colsSpinner;
  /**
   * Rows number chooser.
   */
  private JSpinner rowsSpinner;

  /**
   * Constructor.
   * 
   * @param parentFrame           The parent frame of the dialog.
   * @param authorResourceBundle  The author resource bundle.It is used for translations.
   * @param maxColumns            The maximum number of columns in which the current cell can be split.
   * @param maxRows               The maximum number of rows in which the current cell can be split.
   */
  public SATableSplitCustomizerDialog(Frame parentFrame, AuthorResourceBundle authorResourceBundle, int maxColumns, int maxRows) {
    super(parentFrame, authorResourceBundle.getMessage(ExtensionTags.SPLIT_CELLS), true);
    
    getContentPane().setLayout(new GridBagLayout());
    // Column
    GridBagConstraints constr = new GridBagConstraints();
    constr.gridx = 0;
    constr.gridy = 0;
    constr.gridwidth = 1;
    constr.gridheight = 1;
    constr.weightx = 0;
    constr.weighty = 0;
    constr.fill = GridBagConstraints.NONE;
    constr.anchor = GridBagConstraints.WEST;
    constr.insets = new Insets(0, 0, 0, 0);
    getContentPane().add(new JLabel(authorResourceBundle.getMessage(ExtensionTags.NUMBER_OF_COLUMNS) + ":"), constr);
    
    constr.gridx ++;
    constr.weightx = 1;
    constr.fill = GridBagConstraints.HORIZONTAL;
    constr.insets.left = 5;
    
    // Column Spinner
    colsSpinner = new JSpinner();
    colsSpinner.setName("Columns spinner");
    colsSpinner.setModel(new SpinnerNumberModel(maxColumns > 1 ? 2 : 1, 1, maxColumns, 1));
    JComponent columnsEditor = colsSpinner.getEditor();
    if (columnsEditor instanceof JSpinner.DefaultEditor) {
      JTextField textField = ((JSpinner.DefaultEditor)columnsEditor).getTextField();
      // On ENTER, do ok in the dialog, not in tghe spinner
      textField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          int keyCode = e.getKeyCode();
          if (keyCode == KeyEvent.VK_ENTER) {
            doOK();
          }
        }
      });
    }
    getContentPane().add(colsSpinner, constr);
    
    // Row
    constr.gridx = 0;
    constr.gridy ++;
    constr.weightx = 0;
    constr.fill = GridBagConstraints.NONE;
    constr.insets = new Insets(7, 0, 0, 0);
    getContentPane().add(new JLabel(authorResourceBundle.getMessage(ExtensionTags.NUMBER_OF_ROWS) + ":"), constr);
    
    constr.gridx ++;
    constr.weightx = 1;
    constr.fill = GridBagConstraints.HORIZONTAL;
    constr.insets.left = 5;
    
    // Rows spinner
    rowsSpinner = new JSpinner();
    rowsSpinner.setName("Rows spinner");
    rowsSpinner.setModel(new SpinnerNumberModel(1, 1, maxRows, 1));
    // On ENTER, do ok in the dialog, not in tghe spinner
    JComponent rowsEditor = rowsSpinner.getEditor();
    if (rowsEditor instanceof JSpinner.DefaultEditor) {
      JTextField textField = ((JSpinner.DefaultEditor)rowsEditor).getTextField();
      textField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          int keyCode = e.getKeyCode();
          if (keyCode == KeyEvent.VK_ENTER) {
            doOK();
            
          }
        }
      });
    }
    getContentPane().add(rowsSpinner, constr);
    
    constr.gridx = 0;
    constr.gridy ++;
    constr.gridwidth = 2;
    constr.weightx = 1;
    constr.weighty = 1;
    constr.fill = GridBagConstraints.BOTH;
    constr.insets = new Insets(0, 0, 0,0);
    getContentPane().add(new JPanel(), constr);
    
    setResizable(true);
    pack();
    setMinimumSize(new Dimension(getSize().width, getSize().height));
  }
  
  /**
   * Obtain the number of cells on split. (horizontally and vertically)
   * 
   * @return The first element contains the number of columns and the second 
   *        element contains the number of rows.
   */
  public int[] getSplitInformation() {
    setVisible(true);

    int[] result = null;
    if (getResult() == RESULT_OK) {
      result = new int[2];
      // columns
      result[0] = ((Integer)colsSpinner.getValue()).intValue();
      // rows
      result[1] = ((Integer)rowsSpinner.getValue()).intValue();
    }

    return result;
  }
}