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

import java.util.List;
import java.util.Map;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;

/**
 * Class representing a table property. It contains the name of the property,
 * possible values, icons for the values, current set value of the property,
 * the group that contains it, the type of GUI elements that will be used to
 * present the property in the "Table properties" dialog. 
 * 
 * @author adriana_sbircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class TableProperty {
  /**
   * Property name. If it is a attribute, it will be the qualified name.
   */
  private String propertyName = "";
  /**
   * The string that will be shown in the table customizer dialog.
   */
  private String propertyRenderString = "";
  /**
   * The values for the current property.
   */
  private List<String> values;
  /**
   * The icons for every value of the property.
   */
  private Map<String, String> icons;
  /**
   * Current value of the property.
   */
  private String currentValue = null;
  /**
   * The last old value of the current property. If the property current value was never modified,
   * the oldValue is null. 
   */
  private String oldValue = null;
  /**
   * <code>true</code> if the current property is an attribute
   */
  private boolean isAttr = false;
  /**
   * <code>true</code> if the combobox corresponding to the current property is enabled,
   * <code>false</code> otherwise.
   */
  private boolean isActive;
  /**
   * The name of the group which will contain the current property.
   */
  private String parentGroup = null;
  /**
   * The type of GUI elements which will be used to present the current property.
   */
  private GuiElements guiType = GuiElements.COMBOBOX;

  /**
   * Constructor.
   * 
   * @param propertyName         The qName of the current attribute.
   * @param propertyRenderString  The string that will be presented in the {@link SATablePropertiesCustomizerDialog}. It
   *                          can be different from the attribute name or it can be even the same. 
   * @param propertyValues        The list with the attribute's possible values.
   * @param currentValue      The current of the attribute. 
   */
  public TableProperty(String propertyName, String propertyRenderString, List<String> propertyValues, String currentValue) {
    this(propertyName, propertyRenderString, propertyValues, currentValue, false);
  }
  
  /**
   * Constructor.
   * 
   * @param propertyName         The qName of the current attribute.
   * @param propertyRenderString  The string that will be presented in the {@link SATablePropertiesCustomizerDialog}. It
   *                          can be different from the attribute name or it can be even the same. 
   * @param propertyValues        The list with the attribute's possible values.
   * @param currentValue      The current of the attribute. 
   * @param isAttribute       <code>true</code> if the current property represents an attribute.
   */
  public TableProperty(String propertyName, String propertyRenderString, List<String> propertyValues, String currentValue, boolean isAttribute) {
    this(propertyName, propertyRenderString, propertyValues, currentValue, isAttribute, true);
  }
  
  /**
   * Constructor.
   * 
   * @param propertyName          The qName of the current attribute.
   * @param propertyRenderString  The string that will be presented in the {@link SATablePropertiesCustomizerDialog}. It
   *                              can be different from the attribute name or it can be even the same. 
   * @param propertyValues        The list with the attribute's possible values.
   * @param currentValue          The current of the attribute. 
   * @param isAttribute           <code>true</code> if the current property represents an attribute.
   * @param isActive              <code>true</code> if the combobox corresponding to the current property is enabled,
   *                              <code>false</code> otherwise.
   */
  public TableProperty(String propertyName, String propertyRenderString, List<String> propertyValues, String currentValue, boolean isAttribute, boolean isActive) {
    this(propertyName, propertyRenderString, propertyValues, currentValue, null, null, null, isAttribute, isActive);
  }
  
  /**
   * Constructor.
   * 
   * @param propertyName          The qName of the current attribute.
   * @param propertyRenderString  The string that will be presented in the {@link SATablePropertiesCustomizerDialog}. It
   *                              can be different from the attribute name or it can be even the same. 
   * @param propertyValues        The list with the attribute's possible values.
   * @param currentValue          The current of the attribute. 
   * @param parentGroup           The group name that will include the current property.
   * @param guiType               The type of GUI element that will be used to represent the values for the current property.
   *                              If is one of {@link GuiElements#COMBOBOX}, {@link GuiElements#RADIO_BUTTONS}.
   *                              The default is {@link GuiElements#COMBOBOX}. If this parameter is set to <code>null</code>, the element that
   *                              will be used is {@link GuiElements#COMBOBOX}.
   * @param icons                 The list of icons. An icon for every value. If empty icon corresponds to a value, the icon will be null
   * @param isAttribute           <code>true</code> if the current property represents an attribute.
   * @param isActive              <code>true</code> if the combobox corresponding to the current property is enabled,
   *                              <code>false</code> otherwise.
   */
  public TableProperty(String propertyName, String propertyRenderString, List<String> propertyValues, String currentValue, String parentGroup, GuiElements guiType, Map<String, String> icons, boolean isAttribute, boolean isActive) {
    this.propertyName = propertyName;
    this.propertyRenderString = propertyRenderString;
    values = propertyValues; 
    this.currentValue = currentValue;
    isAttr = isAttribute;
    this.isActive = isActive;
    this.parentGroup = parentGroup;
    if (guiType != null) {
      this.guiType = guiType;
    }
    
    this.icons = icons;
  }
  
  /**
   * Obtain the property name.
   * 
   * @return Returns the property name.
   */
  public String getAttributeName() {
    return propertyName;
  }
  
  /**
   * Obtain the render string fort the property.
   * 
   * @return the render string fort the property
   */
  public String getAttributeRenderString() {
    return propertyRenderString;
  }
  
  /**
   * Obtain the property possible values.
   * 
   * @return Returns the values.
   */
  public List<String> getValues() {
    return values;
  }
  
  /**
   * Obtain the current value for the attributes.
   * 
   * @return Returns the current value of the attribute.
   */
  public String getCurrentValue() {
    return currentValue;
  }
  
  /**
   * Set a new current value for the property.
   * 
   * @param currentValue The new value to set.
   */
  public void setCurrentValue(String currentValue) {
    // Retain the last value of the property
    if (currentValue != null) {
      oldValue = currentValue;
    }
    this.currentValue = currentValue;
  }
  
  /**
   * Obtain the group that includes the current property.
   * 
   * @return Returns the group name or <code>null</code> if no group contains this property.
   */
  public String getParentGroup() {
    return parentGroup;
  }
  
  /**
   * Sets the group that includes the current property.
   * 
   * @param parentGroup The group that includes the current property.
   */
  public void setParentGroup(String parentGroup) {
    this.parentGroup = parentGroup;
  }
  /**
   * <code>true</code> if the current property represents an attribute.
   * 
   * @return Returns <code>true</code> if the property is an attribute.
   */
  public boolean isAttribute() {
    return isAttr;
  }
  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    boolean equals = false;

    if (obj instanceof TableProperty) {
      TableProperty prop = (TableProperty) obj;

      // Check the possible values
      boolean valuesEquals = false;
      List<String> propertyValues = prop.getValues();
      if (values == null && propertyValues == null) {
        valuesEquals = true;
      } else if (values != null && propertyValues != null) {
        // Check the values
        valuesEquals = values.size() == propertyValues.size() && values.containsAll(propertyValues)
            && propertyValues.containsAll(values);
      }

      // Check the current value
      boolean currentValueEquals =
          currentValue == null && prop.getCurrentValue() == null || 
          currentValue != null && currentValue.equals(prop.getCurrentValue());
      // Check the property name    
      boolean nameEquals = 
          propertyName == null && prop.getAttributeRenderString() == null ||
          propertyName != null && propertyName.equals(prop.getAttributeName());

      // Check the render string
      boolean renderStrEquals = 
          propertyRenderString == null && prop.getAttributeRenderString() == null ||
          propertyRenderString != null && propertyRenderString.equals(prop.getAttributeRenderString());

      equals = 
          nameEquals && 
          valuesEquals && 
          currentValueEquals && 
          renderStrEquals &&
          isAttribute() == prop.isAttribute();
    }

    return equals;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Name: " + propertyName + " render string: " + propertyRenderString +
        " current value: " + currentValue + " is attribute: " + isAttr + " possible values: " + values;
  }
  
  /**
   * Obtain the old set value.
   * 
   * @return Returns the old value.
   */
  public String getOldValue() {
    return oldValue;
  }
  
  /**
   * Set the old value for the property. It should be correlated with setting 
   * a new  value.
   * 
   * @param oldValue The old value to set.
   */
  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }
  
  /**
   * Check if the property can be edited through the properties dialog.
   * 
   * @return <code>true</code> if the combobox corresponding to the current property is enabled,
   * <code>false</code> otherwise.
   */
  public boolean isActive() {
    return isActive;
  }
  
  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  /**
   * Sets the values for the current property.
   * 
   * @param values Values for the current property.
   */
  public void setValues(List<String> values) {
    this.values = values;
  }
  
  /**
   * Set the type of GUI elements which will be used to present the values 
   * for the property.
   * 
   * @param guiType The new type GUI elements which will be used to present the values 
   * for the property.
   */
  public void setGuiType(GuiElements guiType) {
    if (guiType != null) {
      this.guiType = guiType;
    }
  }
  
  /**
   * Obtain the type of GUI elements which will be used to present the values 
   * for the property.
   *  
   * @return Returns the type of GUI elements which will be used to present the values 
   * for the property.
   */
  public GuiElements getGuiType() {
    return guiType;
  }

  /**
   * Obtain the icons for the property values. If the list contains null objects,
   * then an empty icon should be used.
   * 
   * @return Returns the icons for all the possible values. Every values is mapped
   * to an icon path. 
   */
  public Map<String, String> getIcons() {
    return icons;
  }
  
  /**
   * Set the icons for the property possible values.
   * 
   * @param icons The icons to set.
   */
  public void setIcons(Map<String, String> icons) {
    this.icons = icons;
  }
}
