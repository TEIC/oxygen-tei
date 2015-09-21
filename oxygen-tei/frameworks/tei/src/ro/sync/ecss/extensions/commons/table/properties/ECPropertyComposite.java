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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;




import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;

/**
 * The composite used to edit a table property.
 * 
 * @author adriana_sbircea
 */

public class ECPropertyComposite {

  /**
   * The combo for properties that are presented using a combobox.
   */
  private ComboViewer propertyValuesCombo = null;
  /**
   * The list the radio buttons.
   */
  List<Button> radioButtons = null;
  /**
   * The table property that is edited using the current composite.
   */
  private TableProperty tableProperty;
  /**
   * The current selected value of the table property.
   */
  private String currentlySelectedValue;
  /**
   * Property controller.
   */
  private PropertySelectionController controller;

  /**
   * Font label provider class.
   * 
   * @author adriana_sbircea
   */
  private class FontLabelProvider extends LabelProvider implements IFontProvider {
    /**
     * Italic font.
     */
    private Font italicFont = null;
    /**
     * Normal font.
     */
    private Font normalFont = null;

    /**
     * Create fonts.
     */
    {
      FontData fd = propertyValuesCombo.getControl().getFont().getFontData()[0];
      fd.setStyle(SWT.ITALIC);
      italicFont = new Font(Display.getDefault(), fd);
      fd.setStyle(SWT.NORMAL);
      normalFont = new Font(Display.getDefault(), fd);
    }
    /**
     * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
     */
    @Override
    public Font getFont(Object element) {
      // For "preserve" and "not set" values use italic font
      if (TablePropertiesConstants.PRESERVE.equals(getText(element)) 
          || TablePropertiesConstants.ATTR_NOT_SET.equals(getText(element))) {
        return italicFont;
      } else {
        return normalFont;
      }
    }

    /**
     * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
      // Dispose font
      italicFont.dispose();
      normalFont.dispose();
      super.dispose();
    }
  }

  /**
   * Constructor.
   * 
   * @param parent                The parent composite.
   * @param tableProperty         The table property that is edited using the current composite.
   * @param authorResourceBundle  The author resource bundle. It is used for translation. 
   * @param controller            The property controller.
   * @param firstChild            <code>true</code> if the current property is the first child in the given parent.
   */
  public ECPropertyComposite(
      final Composite parent, 
      TableProperty tableProperty,
      AuthorResourceBundle authorResourceBundle,
      final PropertySelectionController controller,
      boolean firstChild) {
    this.tableProperty = tableProperty;
    this.controller = controller;
    currentlySelectedValue = tableProperty.getCurrentValue();
    String propertyName = tableProperty.getAttributeName();
    String propertyRenderString = tableProperty.getAttributeRenderString();
    List<String> values = tableProperty.getValues();
    String propertyLabelName = propertyRenderString != null ? authorResourceBundle.getMessage(propertyRenderString) : propertyName;
    GuiElements guiElement = tableProperty.getGuiType();
    if (GuiElements.COMBOBOX == guiElement) {
      // Create the label 
      propertyLabelName += ":";
      Label label = new Label(parent, SWT.LEFT);
      label.setText(propertyLabelName);

      // Combo
      propertyValuesCombo = new ComboViewer(parent);
      propertyValuesCombo.setContentProvider(new IStructuredContentProvider() {
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        
        @Override
        public void dispose() {
        }
        
        @Override
        public Object[] getElements(Object inputElement) {
          return (Object[]) inputElement;
        }
      });
      
      propertyValuesCombo.setLabelProvider(new FontLabelProvider());
      
      // Set the combo input
      propertyValuesCombo.setInput(values.toArray(new String[0]));
      
      GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      gd.grabExcessHorizontalSpace = true;
      propertyValuesCombo.getCombo().setLayoutData(gd);
      // Selection listener
      propertyValuesCombo.getCombo().addSelectionListener(new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          comboSelectionChanged();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
          comboSelectionChanged();
        }
      });

      if (tableProperty.getCurrentValue() != null) {
        // Select the current property value
        int index = values.indexOf(tableProperty.getCurrentValue());
        propertyValuesCombo.getCombo().select(index);
        // Fire selection changed, so the preview to be updated
        comboSelectionChanged();
      }
      
      propertyValuesCombo.getCombo().setEnabled(this.tableProperty.isActive());
    } else if (GuiElements.RADIO_BUTTONS == guiElement) {
      // Radios
      radioButtons = new ArrayList<Button>();
      // Create a radio button for every value
      for (int i = 0; i < values.size(); i++) {
        final Button button = new Button(parent, SWT.RADIO | SWT.LEFT);
        String currentValue = values.get(i);
        button.setText(currentValue);
        // Set italic font for not set and preserve values
        if (TablePropertiesConstants.ATTR_NOT_SET.equals(currentValue) 
            || TablePropertiesConstants.PRESERVE.equals(currentValue)) {
          FontData fd = button.getFont().getFontData()[0];
          // Italic style 
          fd.setStyle(SWT.ITALIC);
          final Font italicFont = new Font(parent.getDisplay(), fd);
          button.setFont(italicFont);
          // Dispose the font
          button.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
              // Dispose the font
              italicFont.dispose();
            }
          });
        }
        
        // Add selection listener
        button.addSelectionListener(new SelectionListener() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            // Selection changed
            radioSelectionChanged(button);
          }
          
          @Override
          public void widgetDefaultSelected(SelectionEvent e) {
            // Selection changed
            radioSelectionChanged(button);
          }
        });
        
        // Select the button if its text corresponds to the current value for the property
        if (this.tableProperty.getCurrentValue() != null && currentValue.equals(this.tableProperty.getCurrentValue())) {
          button.setSelection(true);
          // Fire selection changed, so the preview to be updated
          radioSelectionChanged(button);
        } else {
          button.setSelection(false);
        }
        button.setEnabled(this.tableProperty.isActive());
        radioButtons.add(button);
      }
    }
  }

  /**
   * Get the new table property. If the value of the given property is not modified,
   * a <code>null</code> will be return.
   *  
   * @return The modified property or <code>null</code> if the property value was not changed.
   */
  public TableProperty getModifiedProperty() {
    TableProperty modifiedProperty = null;
    String currentSelectedValue = tableProperty.getCurrentValue();
    if (tableProperty.getGuiType() == GuiElements.COMBOBOX) {
      // Combobox
      int selIndex = propertyValuesCombo.getCombo().getSelectionIndex();
      currentSelectedValue = propertyValuesCombo.getCombo().getItem(selIndex);
    } else if (tableProperty.getGuiType() == GuiElements.RADIO_BUTTONS) {
      // Radio buttons
      for (int i = 0; i < radioButtons.size(); i++) {
        Button radioButton = radioButtons.get(i);
        boolean isSelected = radioButton.getSelection();
        if (isSelected) {
          currentSelectedValue = radioButton.getText();
          break;
        }
      }
    }

    // Check if the property was really modified
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
   * The current edited table property.
   * 
   * @return Returns the corresponding table property.
   */
  public TableProperty getTableProperty() {
    return tableProperty;
  }

  /**
   * Obtain the current selected value for this property.
   * 
   * @return The current selected value of the table property.
   */
  public String getCurrentlySelectedValue() {
    return currentlySelectedValue;
  }
  
  /**
   * The selection changed so fire selection changed for property controller. It is used for the 
   * combo.
   */
  private void comboSelectionChanged() {
    currentlySelectedValue = propertyValuesCombo.getCombo().getItem(propertyValuesCombo.getCombo().getSelectionIndex());
    if (currentlySelectedValue != null) {
      try {
        ECPropertyComposite.this.controller.selectionChanged(
            ECPropertyComposite.this.tableProperty, currentlySelectedValue);
      } catch (AuthorOperationException e) {
        // Do nothing
      }
    }
  }
  
  /**
   * Method that handles the selection changed event for the radio group.
   */
  private void radioSelectionChanged(Button currentRadioButton) {
    currentlySelectedValue = currentRadioButton.getText();
    try {
      controller.selectionChanged(ECPropertyComposite.this.tableProperty, currentlySelectedValue);
    } catch (AuthorOperationException e1) {
      // Do nothing
    }
  }
}
