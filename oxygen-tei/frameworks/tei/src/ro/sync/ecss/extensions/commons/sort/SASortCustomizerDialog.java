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
package ro.sync.ecss.extensions.commons.sort;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;






import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;

/**
 * Standalone implementation of the customizer used to select the criterion information used when sorting.
 */


public class SASortCustomizerDialog extends OKCancelDialog implements SortCustomizer, KeysController {

  /**
   * Radio button used to enable the sorting of only the selected elements.
   */
  private JRadioButton sortSelectedElementsRadio;
  
  /**
   * Radio button used to enable the sorting of all the children from a parent element.
   */
  private JRadioButton sortAllElementsRadio;
  
  /**
   * The panel for the first sorting criterion.
   */
  private CriterionPanel firstCriterion;
  
  /**
   * The panel for the second sorting criterion.
   */
  private CriterionPanel secondCriterion;
  
  /**
   * The panel for the third sorting criterion.
   */
  private CriterionPanel thirdCriterion;
  
  /**
   * The available number of criteria.
   */
  private int numberOfCriteria;

  private final AuthorResourceBundle authorResourceBundle;
  /**
   * The all criteria information shown in the dialog.
   */
  private List<CriterionInformation> criteriaInformation;
  /**
   * The name of the "selected elements" radio combo.
   */
  private final String selElems;
  /**
   * The name of the "all elements" radio combo.
   */
  private final String allElems;
  
  /**
   * Constructor.
   * 
   * @param parentFrame The parent frame of the dialog.
   * @param authorResourceBundle The Author resource bundle. 
   * @param selElems The name of the "selected elements" radio combo.
   * @param allElems The name of the "all elements" radio combo.
   */
  public SASortCustomizerDialog(Frame parentFrame, AuthorResourceBundle authorResourceBundle, String selElems, String allElems) {
    super(parentFrame, authorResourceBundle.getMessage(ExtensionTags.SORT), true);
    this.authorResourceBundle = authorResourceBundle;
    this.selElems = selElems;
    this.allElems = allElems;
  }

  /**
   * Add titled section in the dialog.
   * 
   * @param constr The constraints.
   * @param title The section title.
   */
  private void addSection(GridBagConstraints constr, String title) {
    JPanel sepPanel = new JPanel(new GridBagLayout());

    // Add the section title
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.insets = new Insets(0, 0, 0, 5);
    JLabel titleLabel = new JLabel(title);
    sepPanel.add(titleLabel, c);

    // Add the horizontal separator for the section.
    JSeparator sep = new JSeparator();
    c.gridx ++;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 1;
    c.insets = new Insets(0, 0, 0, 0);
    sepPanel.add(sep, c);
    getContentPane().add(sepPanel, constr);
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortCustomizer#getSortInformation(java.util.List, boolean, boolean)
   */
  @Override
  public SortCriteriaInformation getSortInformation(
      List<CriterionInformation> criteriaInformation, 
      boolean sortSelectedElements, 
      boolean cannotSortAllElements) {
    this.criteriaInformation = criteriaInformation;
    numberOfCriteria = criteriaInformation.size();
    
    getContentPane().setLayout(new GridBagLayout());
    
    // Add the sort range section.
    GridBagConstraints constr = new GridBagConstraints();
    constr.gridx = 0;
    constr.gridy = 0;
    constr.gridwidth = numberOfCriteria > 1 ? 4 : 3;
    constr.fill = GridBagConstraints.HORIZONTAL;
    constr.anchor = GridBagConstraints.NORTHWEST;
    constr.insets = new Insets(0, 0, 0, 0);
    addSection(constr, authorResourceBundle.getMessage(ExtensionTags.RANGE));

    JPanel radioButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    ButtonGroup radioGroup = new ButtonGroup();

    // Sort only the selected elements radio.
    sortSelectedElementsRadio = new JRadioButton(selElems);
    radioGroup.add(sortSelectedElementsRadio);
    radioButtonsPanel.add(sortSelectedElementsRadio);
    sortSelectedElementsRadio.setSelected(true);

    // Sort all elements radio.
    sortAllElementsRadio = new JRadioButton(allElems);
    radioGroup.add(sortAllElementsRadio);
    radioButtonsPanel.add(sortAllElementsRadio);

    constr.gridy ++;
    getContentPane().add(radioButtonsPanel, constr);

    constr.gridy ++;
    // Change the radio buttons states according to the available range options.
    if (sortSelectedElements) {
      sortSelectedElementsRadio.setEnabled(true);
      sortSelectedElementsRadio.setSelected(true);
    } else {
      sortAllElementsRadio.setSelected(true);
      sortAllElementsRadio.setEnabled(true);
      sortSelectedElementsRadio.setEnabled(false);
    }
    
    // If all elements cannot be sorted disable the corresponding radio.
    if (cannotSortAllElements) {
      sortAllElementsRadio.setEnabled(false);
      sortAllElementsRadio.setSelected(false);
    }

    // Add the sort criteria section.
    addSection(constr, authorResourceBundle.getMessage(ExtensionTags.CRITERIA));
    
    constr.gridx = 0;
    constr.gridy ++;
    constr.fill = GridBagConstraints.NONE;
    constr.weightx = 0;
    constr.gridwidth = 1;
    constr.insets = new Insets(7, 0, 5, 7);
    if (numberOfCriteria > 1) {
      // Empty label for nice layout.
      getContentPane().add(new JLabel(""), constr);
      constr.gridx ++;
    } 

    // Add first criterion configuration combo boxes.
    constr.fill = GridBagConstraints.HORIZONTAL;
    constr.weightx = 1;
    constr.gridwidth = 3;
    getContentPane().add(new JLabel(authorResourceBundle.getMessage(ExtensionTags.SORT_BY) + ":"), constr);

    // Determine the initially selected element for the first criterion.
    CriterionInformation firstSel = criteriaInformation.size() > 0 ? criteriaInformation.get(0) : null;
    for (int i = 0; i < criteriaInformation.size(); i++) {
      CriterionInformation criterionInformation = criteriaInformation.get(i);
      if (criterionInformation.isInitiallySelected()) {
        firstSel = criterionInformation;
        break;
      }
    }
    
    // Create the panel for the first criterion.
    firstCriterion = new CriterionPanel(
        getContentPane(),
        constr,
        criteriaInformation,
        firstSel,
        authorResourceBundle,
        this,
        criteriaInformation);
    
    if (numberOfCriteria > 1) {
      firstCriterion.enableSortcriterion();
    }
    
    // Create the second criterion section with the configuration combo boxes, if available.
    CriterionInformation secondSel = null;
    if (numberOfCriteria >= 2) {
      // Empty lable for a nice layout.
      constr.gridx = 0;
      constr.gridy ++;
      constr.fill = GridBagConstraints.NONE;
      constr.weightx = 0;
      constr.insets.bottom = 5;
      getContentPane().add(new JLabel(""), constr);
      
      constr.gridx ++;
      constr.fill = GridBagConstraints.HORIZONTAL;
      constr.weightx = 1;
      constr.gridwidth = 3;
      getContentPane().add(new JLabel(authorResourceBundle.getMessage(ExtensionTags.AND_THEN_BY) + ":"), constr);
      List<CriterionInformation> secondCriteria = new ArrayList<CriterionInformation>();
      boolean computeSel = true;
      for (int i = 0; i < criteriaInformation.size(); i++) {
        CriterionInformation criterionInformation = criteriaInformation.get(i);
        // Remove from the second criterion available keys the selected key for the first criterion.
        if (criterionInformation != firstSel) {
          secondCriteria.add(criterionInformation);
          // Determine the initial selection for second criterion.
          if (criterionInformation.isInitiallySelected() && computeSel) {
            secondSel = criterionInformation;
            computeSel = false;
          }
        }
      }
      if (secondSel == null && secondCriteria.size() > 0) {
        secondSel = secondCriteria.get(0);
      }
      
      // Create the criterion panel.
      secondCriterion = new CriterionPanel(
          getContentPane(),
          constr, 
          secondCriteria,
          secondSel,
          authorResourceBundle,
          this,
          criteriaInformation);
    }
    
    
    // Check if the third criterion section should be added.
    if (numberOfCriteria >= 3) {
      // Empty lable for a nice layout.
      constr.gridx = 0;
      constr.gridy ++;
      constr.fill = GridBagConstraints.NONE;
      constr.weightx = 0;
      getContentPane().add(new JLabel(""), constr);
      
      constr.gridx ++;
      constr.fill = GridBagConstraints.HORIZONTAL;
      constr.weightx = 1;
      constr.gridwidth = 3;
      constr.insets.bottom = 5;
      getContentPane().add(new JLabel(authorResourceBundle.getMessage(ExtensionTags.AND_THEN_BY) + ":"), constr);
      
      List<CriterionInformation> thirdCriteria = new ArrayList<CriterionInformation>();
      CriterionInformation thirdSel = null;
      boolean computeSel = true;
      for (int i = 0; i < criteriaInformation.size(); i++) {
        CriterionInformation criterionInformation = criteriaInformation.get(i);
        // Remove the keys selected for the first and second criteria from the available ones.
        if (criterionInformation != firstSel && criterionInformation != secondSel) {
          thirdCriteria.add(criterionInformation);
          // Determine the initial selection for the third criterion.
          if (criterionInformation.isInitiallySelected() && computeSel) {
            thirdSel = criterionInformation;
            computeSel = false;
          }
        }
      }
      
      if (thirdSel == null && thirdCriteria.size() > 0) {
        thirdSel = thirdCriteria.get(0);
      }
      
      // Create the criterion panel.
      thirdCriterion = new CriterionPanel(
          getContentPane(),
          constr,
          thirdCriteria,
          thirdSel,
          authorResourceBundle,
          this,
          criteriaInformation);
    }
    
    // Some adjustments for the layout of the dialog.
    constr.gridx = 0;
    constr.gridy ++;
    constr.fill = GridBagConstraints.BOTH;
    constr.weightx = 1;
    constr.weighty = 1;
    constr.gridwidth = numberOfCriteria > 1 ? 4 : 3;
    getContentPane().add(new JPanel(), constr);
    setResizable(true);
    pack();
    setMinimumSize(new Dimension(350, getSize().height));
    setSize(450, getSize().height);
    
    SortCriteriaInformation sortInformation = null;
    setVisible(true);
    
    if (getResult() == RESULT_OK) {
      List<CriterionInformation> info = new ArrayList<CriterionInformation>();
      // Maybe we have only one entry
      if (firstCriterion != null && firstCriterion.getInformation() != null) {
        // Add the first criterion information.
        info.add(firstCriterion.getInformation());
      } else if (criteriaInformation.size() == 1) {
        info.add(new CriterionInformation(0, "Column 1"));
      }
      
      // Add the second criterion descriptor.
      if (secondCriterion != null && secondCriterion.getInformation() != null) {
        info.add(secondCriterion.getInformation());
      }
      
      // Add the third criterion descriptor.
      if (thirdCriterion != null && thirdCriterion.getInformation() != null) {
        info.add(thirdCriterion.getInformation());
      }
      
      // Create the descriptor for sort operation configuration.
      sortInformation = new SortCriteriaInformation(info.toArray(new CriterionInformation[0]), 
          sortSelectedElementsRadio != null && sortSelectedElementsRadio.isSelected());
    }
    
    return sortInformation;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.KeysController#selectionChanged(java.lang.String, java.lang.String)
   */
  @Override
  public void selectionChanged(String newSelection, String oldSelection) {
    if (numberOfCriteria > 1 && newSelection != null) {
      if (firstCriterion != null 
          && firstCriterion.getKeyCombo() != null 
          && firstCriterion.getKeyCombo().getSelectedItem().equals(newSelection) 
          && secondCriterion != null) {
        // The first criterion panel changed its selection, update the other criterion panels
        String secondSel = (String) secondCriterion.getKeyCombo().getSelectedItem();
        secondCriterion.getKeyCombo().removeAllItems();

        // Add the other item to the second criterion
        for (int i = 0; i < criteriaInformation.size(); i++) {
          String displayName = criteriaInformation.get(i).getDisplayName();
          if (!newSelection.equals(displayName)) {
            secondCriterion.getKeyCombo().addItem(displayName);
          }
        }

        secondCriterion.getKeyCombo().setSelectedItem(secondSel);

        if (thirdCriterion != null && secondCriterion != null) {
          String thirdSel = (String) thirdCriterion.getKeyCombo().getSelectedItem();
          thirdCriterion.getKeyCombo().removeAllItems();
          // Add the other item to the second criterion
          String selectedItem = (String) secondCriterion.getKeyCombo().getSelectedItem();
          for (int i = 0; i < criteriaInformation.size(); i++) {
            String displayName = criteriaInformation.get(i).getDisplayName();
            
            // Check the display name of the criterion with the new combo selection
            if (!newSelection.equals(displayName) 
                && !displayName.equals(selectedItem)) {
              thirdCriterion.getKeyCombo().addItem(criteriaInformation.get(i).getDisplayName());
            }
          }

          thirdCriterion.getKeyCombo().setSelectedItem(thirdSel);
        }
      } else if (secondCriterion != null 
          && secondCriterion.getKeyCombo().getSelectedItem().equals(newSelection) 
          && thirdCriterion != null) {

        // The first criterion panel changed its selection, update the other criterion panels
        String thirdSel = (String) thirdCriterion.getKeyCombo().getSelectedItem();
        thirdCriterion.getKeyCombo().removeAllItems();


        // Add the other item to the second criterion
        String selectedItem = firstCriterion.getKeyCombo() != null ? 
            (String) firstCriterion.getKeyCombo().getSelectedItem() : "";
        for (int i = 0; i < criteriaInformation.size(); i++) {
          String displayName = criteriaInformation.get(i).getDisplayName();
          
          // Check the criterion display name with the new combo selection.
          if (!newSelection.equals(displayName) 
              && !displayName.equals(selectedItem)) {
            thirdCriterion.getKeyCombo().addItem(displayName);
          }
        }
        thirdCriterion.getKeyCombo().setSelectedItem(thirdSel);
      }
    }
  }
}