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
package ro.sync.ecss.extensions.commons.table.properties;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;




import ro.sync.annotations.obfuscate.SkipLevel;
import ro.sync.annotations.obfuscate.SkipObfuscate;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;

/**
 * This class will add to the given parent container a label with the property 
 * render string and a combobox containing all the possible values for the given 
 * property. It will return the user choice as a {@link TableProperty} object.
 * 
 * @author adriana_sbircea
 */


public class SAPropertyPanel {

  /**
   * The {@link TableProperty} object associated with the current panel.
   */
  private TableProperty tableProperty;
  /**
   * The component which will present the possible values for the given {@link TableProperty} object.
   */
  private final List<JComponent> valuesComponent = new ArrayList<JComponent>();
  /**
   * The current selected value of the table property.
   */
  private String currentlySelectedValue;

  /**
   * Constructor.
   * 
   * @param parentContainer       The component that will contain the current property.
   * @param constr                The {@link GridBagConstraints} object.
   * @param tableProperty         The table property that will be shown by the current panel.
   * @param authorResourceBundle  The {@link AuthorResourceBundle} object, which allow to i18n the property render string.
   * @param controller            The controller used to update the dialog when a value is changed.
   * @param firstChildTopInset    The top inset for the first child in parent.
   * @param firstChild          <code>true</code> if the current panel is the first child of the given parent container.
   */
  public SAPropertyPanel(JPanel parentContainer, 
      GridBagConstraints constr, 
      TableProperty tableProperty, 
      AuthorResourceBundle authorResourceBundle,
      final PropertySelectionController controller,
      int firstChildTopInset,
      boolean firstChild) {
    this.tableProperty = tableProperty;
    currentlySelectedValue = tableProperty.getCurrentValue();
    String propertyName = tableProperty.getAttributeName();
    String propertyRenderString = tableProperty.getAttributeRenderString();
    GuiElements guiElement = tableProperty.getGuiType();
    List<String> values = tableProperty.getValues();
    constr.gridx = 0;
    // Add insets
    constr.insets = new Insets(1, 5, 1, 1);
    if (firstChild) {
      constr.gridy = 0;
    } else {
      constr.gridy ++;
    }
    constr.gridwidth = 1;
    String propertyLabelName = propertyRenderString != null ? authorResourceBundle.getMessage(propertyRenderString) : propertyName;
    if (GuiElements.COMBOBOX == guiElement) {
      // Create the label and the combo for the given property
      constr.fill = GridBagConstraints.NONE;
      constr.anchor = GridBagConstraints.WEST;
      constr.weightx = 0;
      // Create the label 
      propertyLabelName += ":";
      JLabel nameLabel = new JLabel(propertyLabelName);
      parentContainer.add(nameLabel, constr);
      // Combo
      constr.gridx ++;
      constr.fill = GridBagConstraints.HORIZONTAL;
      constr.weightx = 1;
      JComboBox comboBox = new JComboBox(values.toArray(new String[0])) {
        /**
         * @see javax.swing.JComboBox#setSelectedItem(java.lang.Object)
         */
        @Override
        public void setSelectedItem(Object anObject) {
          String oldSel = (String) dataModel.getSelectedItem();
          super.setSelectedItem(anObject);
          // If the old and the new selection are different, a selectedItemChanged()
          // will be performed in super.setSelectedItem()
          if (oldSel.equals(anObject)) {
            selectedItemChanged();
          }
        }
      };
      
      final ListCellRenderer defaultComboRenderer = comboBox.getRenderer();
      // Add the renderer
      comboBox.setRenderer(new ListCellRenderer() {
        /**
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
          JLabel listCellRendererComponent = 
              (JLabel) defaultComboRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
          // For "<not set>" and "preserve" values set italic font
          if (TablePropertiesConstants.PRESERVE.equals(value) || TablePropertiesConstants.ATTR_NOT_SET.equals(value)) {
            Font font = listCellRendererComponent.getFont();
            // Create the italic font 
            Font derivedFont = font.deriveFont(Font.ITALIC);
            listCellRendererComponent.setFont(derivedFont);
          }
          
          return listCellRendererComponent;
        }
      });
      // Add the item listener
      comboBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            // Selected changed, handle it 
            currentlySelectedValue = (String) e.getItem();
            try {
              controller.selectionChanged(SAPropertyPanel.this.tableProperty, currentlySelectedValue);
            } catch (AuthorOperationException e1) {
              // Do nothing
            }
          }
        }
      });
      
      // Select the current value of the property
      if (tableProperty.getCurrentValue() != null) {
        comboBox.setSelectedItem(tableProperty.getCurrentValue());
      }
      
      // Set enable/disable 
      comboBox.setEnabled(this.tableProperty.isActive());
      // Add the combo to the given parent
      parentContainer.add(comboBox, constr);
      constr.gridy ++;
      
      valuesComponent.add(comboBox);
    } else if (GuiElements.RADIO_BUTTONS == guiElement) {
      // Radios
      constr.gridwidth = 2;
      constr.fill = GridBagConstraints.HORIZONTAL;
      constr.weightx = 1;
      // Add a radio button for each value 
      final ButtonGroup valuesGroup = new ButtonGroup();
      
      for (int i = 0; i < values.size(); i++) {
        // For every value, add a radio button
        if (i == 0) {
          constr.insets.top += 4;
        } else {
          constr.insets.top = 3;
        }
        JRadioButton radioButton = new JRadioButton(values.get(i));
        radioButton.setOpaque(false);
        valuesComponent.add(radioButton);
        radioButton.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              // handle the selection changed event
              JRadioButton selRadioButton = (JRadioButton) e.getItem();
              // Obtain the current value
              currentlySelectedValue = selRadioButton.getText();
              try {
                controller.selectionChanged(SAPropertyPanel.this.tableProperty, currentlySelectedValue);
              } catch (AuthorOperationException e1) {
                // Do nothing
              }
            }
          }
        });
        
        // Select the radio which contains the current property value
        if (this.tableProperty.getCurrentValue() != null && values.get(i).equals(this.tableProperty.getCurrentValue())) {
          radioButton.setSelected(true);
        }
        radioButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        valuesGroup.add(radioButton);
        // For "<not set>" and "preserve" values set italic font
        if (TablePropertiesConstants.PRESERVE.equals(values.get(i)) 
            || TablePropertiesConstants.ATTR_NOT_SET.equals(values.get(i))) {
          Font font = radioButton.getFont();
          Font derivedFont = font.deriveFont(Font.ITALIC);
          radioButton.setFont(derivedFont);
        }
        radioButton.setEnabled(this.tableProperty.isActive());
        parentContainer.add(radioButton, constr);
        constr.gridy ++;
      }
    }
  }
  
  /**
   * Get the new table property. If the value of the given property is not modified,
   * a <code>null</code> object will be return.
   *  
   * @return The modified property or <code>null</code> if the property value was not changed.
   */
  public TableProperty getModifiedProperty() {
    TableProperty modifiedProperty = null;
    String currentSelectedValue = tableProperty.getCurrentValue();
    if (tableProperty.getGuiType() == GuiElements.COMBOBOX) {
      // Combobox
      currentSelectedValue = (String) ((JComboBox)valuesComponent.get(0)).getSelectedItem();
    } else if (tableProperty.getGuiType() == GuiElements.RADIO_BUTTONS) {
      // Radio buttons
      for (int i = 0; i < valuesComponent.size(); i++) {
        // Obtain the selected radio
        JRadioButton radioButton = (JRadioButton)valuesComponent.get(i);
        boolean isSelected = radioButton.isSelected();
        if (isSelected) {
          currentSelectedValue = radioButton.getText();
          break;
        }
      }
    }
    
    // Check if the property was modified
    if (tableProperty.getCurrentValue() == null
        || tableProperty.getCurrentValue() != null 
        && !tableProperty.getCurrentValue().equals(currentSelectedValue)) {
      modifiedProperty = new TableProperty(
          tableProperty.getAttributeName(), 
          tableProperty.getAttributeRenderString(), 
          tableProperty.getValues(), 
          currentSelectedValue,
          tableProperty.isAttribute());
      modifiedProperty.setOldValue(tableProperty.getCurrentValue());
    }
    
    return modifiedProperty;
  }
  
  /**
   * Obtain the currently selected value;
   * 
   * @return The currently selected value for this property.
   */
  public String getCurrentlySelectedValue() {
    return currentlySelectedValue;
  }

  /**
   * @return Returns the table property.
   */
  public TableProperty getTableProperty() {
    return tableProperty;
  }
}
