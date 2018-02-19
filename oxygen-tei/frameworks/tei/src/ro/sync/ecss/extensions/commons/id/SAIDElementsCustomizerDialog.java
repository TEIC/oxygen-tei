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
package ro.sync.ecss.extensions.commons.id;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;

/**
 * Dialog used to customize DITA elements which have auto ID generation. It will
 * be also used when the IDs are generated manually.
 * 
 * It is used on standalone implementation.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class SAIDElementsCustomizerDialog extends OKCancelDialog {
  
  /**
   * The list model.
   */
  private final DefaultListModel listModel = new DefaultListModel();
  
  /**
   * The list of elements or class values for which to generate IDs
   */
  private final JList listOfElements = new JList(listModel);
  
  /**
   * Auto assign element IDs
   */
  private JCheckBox autoAssignElementIDs;

  /**
   * The panel holding the element list and the buttons.
   */
  private final JPanel listPanel;
  
  /**
   * The add button.
   */
  private final JButton addButton;

  /**
   * The edit button.
   */
  private final JButton editButton;

  /**
   * The remove button.
   */
  private final JButton removeButton;

  /**
   * The list message
   */
  private final String listMessage;

  /**
   * The id generation pattern field. 
   */
  private final JTextField idGenerationPatternField = new JTextField();
  
  /**
   * Filter IDs on copy
   */
  private JCheckBox filterIDsOnCopy;

  /**
   * Author resource bundle.
   */
  private final AuthorResourceBundle authorResourceBundle;
  
  /**
   * Constructor.
   * 
   * @param parentFrame           The parent frame.
   * @param listMessage           The message label used on the list.
   * @param authorResourceBundle  The author resource bundle.
   */
  public SAIDElementsCustomizerDialog(
      Frame parentFrame, String listMessage, AuthorResourceBundle authorResourceBundle) {
    this(parentFrame, listMessage, authorResourceBundle, false);
  }
  
  /**
   * Constructor.
   * 
   * @param parentFrame           The parent frame.
   * @param listMessage           The message label used on the list.
   * @param authorResourceBundle  The author resource bundle.
   * @param isDocBook             <code>true</code> if we are in DocBook.
   */
  public SAIDElementsCustomizerDialog(
      Frame parentFrame, String listMessage, AuthorResourceBundle authorResourceBundle, boolean isDocBook) {
    super(parentFrame, authorResourceBundle.getMessage(ExtensionTags.ID_OPTIONS), true);
    //The message depending on the framework
    this.listMessage = listMessage;
    this.authorResourceBundle = authorResourceBundle;
    JPanel mainPanel = new JPanel(new GridBagLayout());
    
    //Panel holding the auto generate checkbox
    //and the pattern customizer
    GridBagConstraints constr = new GridBagConstraints();
    constr.gridx = 0;
    constr.gridy = 0;
    constr.gridwidth = 1;
    constr.gridheight = 1;
    constr.weightx = 0;
    constr.weighty = 0;
    constr.fill = GridBagConstraints.NONE;
    constr.anchor = GridBagConstraints.WEST;
    constr.insets = new Insets(0, 0, 7, 5);
    //The Pattern customizer
    JLabel patternLabel = new JLabel("ID Pattern:");
    mainPanel.add(patternLabel, constr);
    
    constr.gridx ++;
    constr.weightx = 1;
    constr.fill = GridBagConstraints.HORIZONTAL;
    constr.insets = new Insets(0, 0, 7, 0);
    patternLabel.setLabelFor(idGenerationPatternField);
    mainPanel.add(idGenerationPatternField, constr);
    
    constr.gridx = 0;
    constr.gridy ++;
    constr.gridwidth = 2;
    constr.weightx = 1;
    constr.weighty = 1;
    constr.fill = GridBagConstraints.BOTH;
    constr.insets = new Insets(0, 0, 7, 0);
    
    listPanel = new JPanel(new GridBagLayout());
    mainPanel.add(listPanel, constr);
    
    GridBagConstraints listConstr = new GridBagConstraints();
    listConstr.gridx = 0;
    listConstr.gridy = 0;
    listConstr.gridwidth = 3;
    listConstr.gridheight = 1;
    listConstr.weightx = 1;
    listConstr.weighty = 0;
    listConstr.fill = GridBagConstraints.NONE;
    listConstr.anchor = GridBagConstraints.WEST;
    listConstr.insets = new Insets(0, 0, 5, 0);
    JLabel patternsListLabel = new JLabel(listMessage + ":");
    listPanel.add(patternsListLabel, listConstr);
    
    listConstr.gridx = 0;
    listConstr.gridy ++;
    listConstr.weightx = 1;
    listConstr.weighty = 1;
    listConstr.fill = GridBagConstraints.BOTH;
    listConstr.insets = new Insets(0, 0, 5, 0);
    
    //List of element local names
    JScrollPane scrollPane = new JScrollPane(listOfElements);
    scrollPane.setPreferredSize(new Dimension(300, 150));
    listPanel.add(scrollPane, listConstr);
    patternsListLabel.setLabelFor(listOfElements);
    
    //Add an element name
    addButton = new JButton(
        new AbstractAction(authorResourceBundle.getMessage(ExtensionTags.ADD)) {
          @Override
          public void actionPerformed(ActionEvent e) {
            addNewElement();
          }
        });
    
    listConstr.gridx = 0;
    listConstr.gridy++;
    listConstr.gridwidth = 1;
    listConstr.weightx = 1;
    listConstr.weighty = 0;
    listConstr.fill = GridBagConstraints.NONE;
    listConstr.anchor = GridBagConstraints.EAST;
    listConstr.insets = new Insets(0, 0, 5, 5);
    listPanel.add(addButton, listConstr);

    //Edit an element name
    editButton = new JButton(
        new AbstractAction(authorResourceBundle.getMessage(ExtensionTags.EDIT)) {
          @Override
          public void actionPerformed(ActionEvent e) {
            editElement();
          }
        });
    
    listConstr.gridx++;
    listConstr.weightx = 0;
    listPanel.add(editButton, listConstr);

    //Remove an element name
    removeButton = new JButton(
        new AbstractAction(authorResourceBundle.getMessage(ExtensionTags.REMOVE)) {
          @Override
          public void actionPerformed(ActionEvent e) {
            removeElement();
          }
        });
    listConstr.gridx++;
    listConstr.insets.right = 0;
    listPanel.add(removeButton, listConstr);
    
    constr.gridx = 0;
    constr.gridy ++;
    constr.gridwidth = 2;
    constr.weightx = 1;
    constr.weighty = 0;
    constr.anchor = GridBagConstraints.WEST;
    constr.fill = GridBagConstraints.NONE;
    constr.insets = new Insets(0, 0, 7, 0);
    
    // Create checkboxes
    autoAssignElementIDs =
        new JCheckBox(authorResourceBundle.getMessage(ExtensionTags.AUTOGENERATE_IDS_FOR_ELEMENTS));
    autoAssignElementIDs.setBorder(BorderFactory.createEmptyBorder());
    mainPanel.add(autoAssignElementIDs, constr);
    
    constr.gridy ++;
    
    //Filter IDs on copy
    String message = isDocBook ? authorResourceBundle.getMessage(ExtensionTags.REMOVE_IDS_ON_COPY) 
        : authorResourceBundle.getMessage(ExtensionTags.REMOVE_IDS_ON_COPY_IN_SAME_DOC);
    filterIDsOnCopy = new JCheckBox(message);
    filterIDsOnCopy.setBorder(BorderFactory.createEmptyBorder());
    mainPanel.add(filterIDsOnCopy, constr);
    
    add(mainPanel);
    
    setMinimumSize(new Dimension(400, 400));
    setResizable(true);
    
    listOfElements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listOfElements.addListSelectionListener(
        new ListSelectionListener(){
          @Override
          public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
              //EXM-19245 Perform the action only at final selection, avoid intermediate events to trigger any action
              return;
            }
            updateButtonState();
          }
        });
    
    updateButtonState();
  }

  /**
   * Update the buttons state on selection change.
   */
  private void updateButtonState() {
    int selectedIndex = listOfElements.getSelectedIndex();
    boolean selected = selectedIndex != -1;
    
    editButton.setEnabled(selected);
    removeButton.setEnabled(selected);
  }

  /**
   * Add a new element.
   */
  private void addNewElement() {
    String elem =
      JOptionPane.showInputDialog(
          this,
          listMessage + ": ",
          authorResourceBundle.getMessage(ExtensionTags.ADD),
          JOptionPane.PLAIN_MESSAGE);

    if (elem != null) {
      listModel.addElement(elem);
      // Select last element.
      listOfElements.setSelectedIndex(listModel.getSize() - 1);
    }
  }
  
  /**
   * Edit the selected element.
   */
  private void editElement() {
    int selectedIndex = listOfElements.getSelectedIndex();
    if (selectedIndex != -1) {
      String elem =
        (String) JOptionPane.showInputDialog(
            this,
            listMessage + ": ",
            authorResourceBundle.getMessage(ExtensionTags.EDIT),
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            listModel.get(selectedIndex));
      if (elem != null) {
        listModel.set(selectedIndex, elem);
      }
    }
  }
  
  /**
   * Remove the selected element.
   */
  private void removeElement() {
    int selectedIndex = listOfElements.getSelectedIndex();
    if (selectedIndex != -1) {
      listModel.remove(selectedIndex);
      int size = listModel.getSize();
      if (size > 0) {
        if (selectedIndex < size) {
          listOfElements.setSelectedIndex(selectedIndex);
        } else {
          listOfElements.setSelectedIndex(size - 1);
        }
      }
    }
  }

  /**
   * @param autoIDElementsInfo The initial information
   * @return The new information or null if canceled.
   */
  public GenerateIDElementsInfo showDialog(GenerateIDElementsInfo autoIDElementsInfo) {
    // Select the checkbox
    autoAssignElementIDs.setSelected(autoIDElementsInfo.isAutoGenerateIDs());
    // Set the pattern
    idGenerationPatternField.setText(autoIDElementsInfo.getIdGenerationPattern());
    // Set the data in the list model.
    listModel.clear();
    String[] elements = autoIDElementsInfo.getElementsWithIDGeneration();
    if (elements != null) {
      for (int i = 0; i < elements.length; i++) {
        listModel.addElement(elements[i]);
      }
    }
    
    //Filter IDs
    filterIDsOnCopy.setSelected(autoIDElementsInfo.isFilterIDsOnCopy());
    updateButtonState();
    
    //Tooltip for the pattern field
    idGenerationPatternField.setToolTipText(autoIDElementsInfo.getPatternTooltip());
    
    // Show dialog.
    setVisible(true);
    if(getResult() == RESULT_OK) {
      // OK  pressed.
      String[] elems = new String[listModel.getSize()];
      listModel.copyInto(elems);
      
      return new GenerateIDElementsInfo(
          autoAssignElementIDs.isSelected(),
          idGenerationPatternField.getText(),
          elems,
          filterIDsOnCopy.isSelected());
    }
    return null;
  }
}