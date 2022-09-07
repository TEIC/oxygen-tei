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
package ro.sync.ecss.extensions.commons.sort;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.annotations.obfuscate.SkipLevel;
import ro.sync.annotations.obfuscate.SkipObfuscate;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;


/**
 * This class will add to the given parent container a checkbox to enable the criterion, a combobox to select the key,
 * a type combobox and order combobox. It will return the user choice as a {@link CriterionInformation} object.
 */
@API(type=APIType.NOT_EXTENDABLE, src=SourceType.PRIVATE)
@SkipObfuscate(classes = SkipLevel.NOT_SPECIFIED, fields = SkipLevel.NOT_SPECIFIED, methods = SkipLevel.PUBLIC)
public class CriterionPanel {
  /**
   * The checkbox to enable the criterion.
   */
  private JCheckBox keyCheckbox;
  
  /**
   * The combobox to select the key.
   */
  private JComboBox keyCombo;
  
  /**
   * The sorting method combo. It's one of the: {@link CriterionInformation#TYPE_TEXT},
   * {@link CriterionInformation#TYPE_NUMERIC}, {@link CriterionInformation#TYPE_DATE}.
   */
  private JComboBox typeCombo;
  
  /**
   * The sorting order combo. It's one of the: {@link CriterionInformation#ORDER_ASCENDING},
   * {@link CriterionInformation#ORDER_DESCENDING}.
   */
  private JComboBox orderCombo;
  
  /**
   * <code>true</code> if the checkbox for enabling the criterion should be added.
   */
  private boolean addCheckbox;
  
  /**
   * All the available criteria.
   */
  private final List<CriterionInformation> allCriteria;

  /**
   * Constructor.
   * 
   * @param parent        The parent {@link Container}. 
   * @param constr        The {@link GridBagConstraints} object.
   * @param criterionInformation The list of available criterion which will be added to the keys combobox.
   * @param selectedItem  The item which will be selected in the keys combobox.
   * @param authorResourceBundle The reosurce bundle for i18n.
   * @param keysController The keys controller.
   * @param allCriteria All criteria information, not only the 
   *                          criteria information shown by the current criterion composite.
   */
  public CriterionPanel(
      Container parent,
      GridBagConstraints constr,
      List<CriterionInformation> criterionInformation,
      CriterionInformation selectedItem,
      final AuthorResourceBundle authorResourceBundle, 
      final KeysController keysController,
      List<CriterionInformation> allCriteria) {
    this.allCriteria = allCriteria;
    this.addCheckbox = allCriteria.size() > 1;

    constr.gridx = 0;
    constr.gridy ++;
    constr.fill = GridBagConstraints.NONE;
    constr.weightx = 0;
    constr.gridwidth = 1;
    constr.insets = new Insets(0, 0, 7, 7);

    if (addCheckbox) {
      keyCheckbox = new JCheckBox();
      parent.add(keyCheckbox, constr);
      constr.gridx ++;

      keyCheckbox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          changeControlsState();
        }
      });
    }
    
    constr.fill = GridBagConstraints.HORIZONTAL;
    constr.weightx = 0.33;
    keyCombo = new JComboBox();
    keyCombo.setToolTipText(authorResourceBundle.getMessage(ExtensionTags.SELECT_KEY_COLUMN_TOOLTIP));
    for (int i = 0; i < criterionInformation.size(); i++) {
      CriterionInformation criterionInfo = criterionInformation.get(i);
      keyCombo.addItem(criterionInfo.getDisplayName());
    }

    if (keyCombo.getPreferredSize().width < 75) {
      keyCombo.setPreferredSize(new Dimension(75, keyCombo.getPreferredSize().height));
      keyCombo.setMinimumSize(new Dimension(75, keyCombo.getPreferredSize().height));
    } else if (keyCombo.getPreferredSize().width > 150) {
      keyCombo.setPreferredSize(new Dimension(150, keyCombo.getPreferredSize().height));
      keyCombo.setMaximumSize(new Dimension(150, keyCombo.getPreferredSize().height));
    }


    keyCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String currentSel = (String)keyCombo.getSelectedItem();
        if (currentSel != null) {
          keysController.selectionChanged(currentSel, null);
        }
      }
    });

    // Select the item
    if (selectedItem != null) {
      keyCombo.setSelectedItem(selectedItem.getDisplayName());
    } else {
      // Select the first item
      keyCombo.setSelectedItem(criterionInformation.get(0).getDisplayName());
    }

    parent.add(keyCombo, constr);
    constr.gridx ++;
    typeCombo = new JComboBox();
    typeCombo.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, authorResourceBundle.getMessage((String) value), index, isSelected, cellHasFocus);
      }
    });
    typeCombo.setToolTipText(authorResourceBundle.getMessage(ExtensionTags.SELECT_TYPE_COMBO_TOOLTIP));
    typeCombo.addItem(CriterionInformation.TYPE.TEXT.getName());
    typeCombo.addItem(CriterionInformation.TYPE.NUMERIC.getName());
    typeCombo.addItem(CriterionInformation.TYPE.DATE.getName());
    typeCombo.setSelectedIndex(0);

    parent.add(typeCombo, constr);

    orderCombo = new JComboBox();
    orderCombo.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, authorResourceBundle.getMessage((String) value), index, isSelected, cellHasFocus);
      }
    });
    orderCombo.addItem(CriterionInformation.ORDER.ASCENDING.getName());
    orderCombo.addItem(CriterionInformation.ORDER.DESCENDING.getName());
    orderCombo.setToolTipText(authorResourceBundle.getMessage(ExtensionTags.SELECT_ORDER_COMBO_TOOLTIP));
    orderCombo.setSelectedIndex(0);
    constr.gridx ++;
    constr.insets = new Insets(0, 0, 7, 0);
    parent.add(orderCombo, constr);
    
    changeControlsState();
  }
  
  /**
   * Returns the user input as a {@link CriterionInformation} object.
   * 
   * @return The criterion information selected by the user in the current component.
   */
  public CriterionInformation getInformation() {
    CriterionInformation info = null;
    
    if (!addCheckbox || keyCheckbox.isSelected()) {
      int index = 0;
      if (keyCombo != null) {
        for (int i = 0; i < allCriteria.size(); i++) {
          if (allCriteria.get(i).getDisplayName().equals(keyCombo.getSelectedItem())) {
            index = i;
            break;
          }
        }
      }
      info = new CriterionInformation(
          index,
          (String) typeCombo.getSelectedItem(),
          (String) orderCombo.getSelectedItem(),
          keyCombo != null ? (String)keyCombo.getSelectedItem() : "Column 1");
    }
    
    return info;
  }
  
 
  /**
   * Selects the checbox associated with the criterion panel, which means that the criterion information from it will be taken
   * into account when sorting.
   */
  public void enableSortcriterion() {
    keyCheckbox.setSelected(true);
    changeControlsState();
  }

  /**
   * Obtain the combo for the criterion key. 
   * 
   * @return Returns the combo for the criterion key.
   */
  public JComboBox getKeyCombo() {
    return keyCombo;
  }

  /**
   * Change the state of the criterion combos.
   */
  private void changeControlsState() {
    keyCombo.setEnabled(keyCheckbox == null || keyCheckbox.isSelected());
    typeCombo.setEnabled(keyCheckbox == null || keyCheckbox.isSelected());
    orderCombo.setEnabled(keyCheckbox == null || keyCheckbox.isSelected());
  }
}