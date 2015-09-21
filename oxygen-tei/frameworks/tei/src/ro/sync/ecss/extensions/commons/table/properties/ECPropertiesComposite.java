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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;




import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.exml.workspace.api.util.ColorThemeUtilities;
import ro.sync.util.Resource;

/**
 * Composite corresponding to a tab information. It contains all the properties 
 * that will be modified, for a type of elements.
 * 
 * @author adriana_sbircea
 */

public class ECPropertiesComposite extends Composite implements PropertySelectionController {
  
  /**
   * Logger for logging.
   */
  private static Logger logger = Logger.getLogger(ECPropertiesComposite.class.getName());
  /**
   * Class for preview group. Contains a label which will present the preview image.
   * 
   * @author adriana_sbircea
   */
  class PreviewGroup extends Group {
    /**
     * Label that presents the preview image.
     */
    Label previewLabel = null;
    
    /**
     * Constructor.
     * 
     * @param parent  The parent composite.
     * @param style   The group style.
     * @param colorThemeUtilities Color theme utilities
     */
    public PreviewGroup (Composite parent, int style, ColorThemeUtilities colorThemeUtilities) {
      super(parent, style);
      
      setText(authorResourceBundle.getMessage(ExtensionTags.PREVIEW));
      // Set the layout
      GridLayout previewLayout = new GridLayout();
      previewLayout.numColumns = 1;
      setLayout(previewLayout);
      // Create the label that will contain the preview image
      previewLabel = new Label(this, SWT.CENTER);
      // Set the layout
      previewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
      // As default, set the empty icons
      URL imageURL = Resource.getResource(TablePropertiesConstants.EMPTY_ICON);
      Image imageIcon = null;
      boolean fallback = true;
      if (colorThemeUtilities.getColorTheme().isHighContrastTheme() &&
          !colorThemeUtilities.getColorTheme().isHighContrastWhiteTheme()) {
        try {
          Object loadedImage = colorThemeUtilities.getImageInverter().loadImage(imageURL);
          ImageDescriptor currentImageDescriptor = (ImageDescriptor) colorThemeUtilities.getImageInverter().invertImage(loadedImage);
          imageIcon = currentImageDescriptor.createImage();
          fallback = false;
        } catch (IOException e) {
          logger.error(e, e);
        }
      } 
      if(fallback) {
        ImageDescriptor currentImageDescriptor = ImageDescriptor.createFromURL(
            imageURL);
        imageIcon = currentImageDescriptor.createImage();
      }
        
      // Add it to the cache
      images.put(imageURL, imageIcon);
      previewLabel.setImage(imageIcon);
    }
    
    /**
     * Obtain the preview label.
     * 
     * @return Returns the preview label.
     */
    public Label getPreviewLabel() {
      return previewLabel;
    }
    
    /**
     * Sets the new preview image.
     * 
     * @param image The new preview image that will be shown for the current group.
     */
    public void setPreviewImage(Image image) {
      previewLabel.setImage(image);
    }
    
    /**
     * @see org.eclipse.swt.widgets.Group#checkSubclass()
     */
    @Override
    protected void checkSubclass() {
      // Sub-classes are allowed.
    }
  }
  
  /**
   * The property panels.
   */
  private final List<ECPropertyComposite> propertyPanels = new ArrayList<ECPropertyComposite>();
  /**
   * List of previews.
   */
  final private Map<String, PreviewGroup> previewsList = new HashMap<String, PreviewGroup>();
  /**
   * List of properties groups.
   */
  final private Map<String, Composite> groupsList = new HashMap<String, Composite>();
  /**
   * The preview images.
   */
  final private Map<URL, Image> images = new HashMap<URL, Image>();
  /**
   * The author resource bundle.
   */
  final private AuthorResourceBundle authorResourceBundle;
  /**
   * The color theme utilities of Oxygen.
   */
  private ColorThemeUtilities colorThemeUtilities;
  
  /**
   * Constructor.
   * 
   * @param parent                The tab folder.
   * @param properties            The list with properties that will be presented inside 
   *                              the current composite.
   * @param contextInfo           The context information. It contains information about what
   *                              is edited inside the current composite.
   * @param authorResourceBundle  The author resource bundle.
   * @param colorThemeUtilities            The color theme utilities. 
   */
  public ECPropertiesComposite(
      TabFolder parent, 
      List<TableProperty> properties, 
      String contextInfo, 
      AuthorResourceBundle authorResourceBundle,
      ColorThemeUtilities colorThemeUtilities) {
    super(parent, SWT.NONE);
    
    this.authorResourceBundle = authorResourceBundle;
    this.colorThemeUtilities = colorThemeUtilities;
    
    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginBottom = 8;
    setLayout(gridLayout);
    // All the children should inherit the background
    setBackgroundMode(SWT.INHERIT_FORCE);
    // Set the background color
    if (!colorThemeUtilities.getColorTheme().isHighContrastTheme()) {
      setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    }
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd.grabExcessHorizontalSpace = true;
    gd.widthHint = 340;
    setLayoutData(gd);
    
    // Group all the properties
    Map<String, List<TableProperty>> groups = new LinkedHashMap<String, List<TableProperty>>();
    for (int i = 0; i < properties.size(); i++) {
      TableProperty property = properties.get(i);
      String currentGroup = property.getParentGroup();
      List<TableProperty> list = groups.get(currentGroup);
      if (list == null) {
        list = new ArrayList<TableProperty>();
      }
      list.add(property);
      groups.put(currentGroup, list);
    }
    
    Set<String> keySet = groups.keySet();
    // Create the GUI elements which will hold the groups and the previews
    for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
      String string = (String) iterator.next();
      // If only a combobox inside the group, do no add a group with title and border,
      // just a composite 
      if (groups.get(string).size() == 1 
          && groups.get(string).get(0).getGuiType() == GuiElements.COMBOBOX) {
        groupsList.put(string, new Composite(this, SWT.NONE));
      } else {
        groupsList.put(string, new Group(this, SWT.SHADOW_ETCHED_IN));
      }
      // Create the preview group
      previewsList.put(string, new PreviewGroup (this, SWT.SHADOW_ETCHED_IN, colorThemeUtilities));
    }
    
    Set<String> groupSet = groups.keySet();
    for (Iterator iterator = groupSet.iterator(); iterator.hasNext();) {
      // Found a group
      String key = (String) iterator.next();
      List<TableProperty> props = groups.get(key);
      // Populate the group with the properties and set the corresponding preview image
      addGroup(key, props);
    }
    
    // If any context information was given, preset it
    if (contextInfo != null) {
      Label contextInfoLabel = new Label(this, SWT.LEFT);
      gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      // Align it with the groups
      gd.horizontalIndent = 8;
      gd.verticalIndent = 8;
      contextInfoLabel.setLayoutData(gd);
      // Set the context text
      contextInfoLabel.setText(contextInfo);
    }
  }
  
  /**
   * Obtain a list with all modified properties for the current panel.
   * 
   * @return a {@link List} with all modified properties from the current panel.
   */
  public List<TableProperty> getModifiedProperties() {
    // Obtain all the properties that are modified in the dialog
    List<TableProperty> properties = new ArrayList<TableProperty>();
    for (int i = 0; i < propertyPanels.size(); i++) {
      TableProperty modifiedProperty = propertyPanels.get(i).getModifiedProperty();
      if (modifiedProperty != null) {
        properties.add(modifiedProperty);
      }
    }
    
    return properties;
  }
  
  /**
   * Add a group for the given properties which will have the given title.
   * 
   * @param keyTitle        The key group to obtain the title.
   * @param properties      The properties that will be contained by the current group.
   */
  private void addGroup(String keyTitle,
      List<TableProperty> properties) {
    
    Composite group = groupsList.get(keyTitle);
    if (group instanceof Group) {
      ((Group)group).setText(authorResourceBundle.getMessage(keyTitle));
    }
    
    GridLayout layout = new GridLayout();
    group.setLayout(layout);
    GridData gridLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
    if (!(group instanceof Group)) {
      // Align the label with the other groups
      gridLayoutData.horizontalIndent = 3;
    } 
    
    group.setLayoutData(gridLayoutData);
    // Determine how many column should contain the group
    int columnsNumber = 1;
    // Add every property inside this composite
    for (int i = 0; i < properties.size(); i++) {
      if (properties.get(i).getGuiType() == GuiElements.COMBOBOX) {
        // If combo, it will be two column
        columnsNumber = 2;
      }
      
      layout.numColumns = columnsNumber;
      ECPropertyComposite p = new ECPropertyComposite(group, properties.get(i), authorResourceBundle, this, i == 0);
      propertyPanels.add(p);
    }
    
    group.pack(true);
    
    // Preview
    gridLayoutData = new GridData(SWT.NONE, SWT.NONE, false, false);
    gridLayoutData.widthHint = 150;
    gridLayoutData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
    gridLayoutData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
    previewsList.get(keyTitle).setLayoutData(gridLayoutData);
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.PropertySelectionController#selectionChanged(ro.sync.ecss.extensions.commons.table.properties.TableProperty, java.lang.String)
   */
  @Override
  public void selectionChanged(TableProperty property, String newValue)
      throws AuthorOperationException {
    StringBuilder iconRelativePath = new StringBuilder();
    String group = property.getParentGroup();
    if (TablePropertiesConstants.COLSEP.equalsIgnoreCase(property.getAttributeName()) || TablePropertiesConstants.ROWSEP.equalsIgnoreCase(property.getAttributeName())) {
      // Check all the properties until the rowsep with the same parent group is found
      String colsepVal = TablePropertiesConstants.ATTR_NOT_SET;
      String rowsepVal = TablePropertiesConstants.ATTR_NOT_SET;
      boolean checkRowsep = true;
      if (TablePropertiesConstants.COLSEP.equalsIgnoreCase(property.getAttributeName())) {
        colsepVal = newValue;
      } else {
        rowsepVal = newValue;
        checkRowsep = false;
      }
      // Check the other property from the group so the preview to be updated 
      // according to the values of all properties from the group
      for (int i = 0; i < propertyPanels.size(); i++) {
        ECPropertyComposite tableProperty = propertyPanels.get(i);
        if (checkRowsep && TablePropertiesConstants.ROWSEP.equalsIgnoreCase(tableProperty.getTableProperty().getAttributeName())) {
          rowsepVal = tableProperty.getCurrentlySelectedValue();
        } else if (!checkRowsep && TablePropertiesConstants.COLSEP.equalsIgnoreCase(tableProperty.getTableProperty().getAttributeName())) {
          colsepVal = tableProperty.getCurrentlySelectedValue();
        }
      }
      
      // Compute the icon path
      if ("1".equals(colsepVal) && "1".equals(rowsepVal)) {
        iconRelativePath.append(TablePropertiesConstants.ICON_COL_ROW_SEP);
      } else if ("1".equals(colsepVal)) {
        iconRelativePath.append(TablePropertiesConstants.ICON_COLSEP);
      } else if ("1".equals(rowsepVal)) {
        iconRelativePath.append(TablePropertiesConstants.ICON_ROWSEP);
      } else {
        iconRelativePath.append(TablePropertiesConstants.EMPTY_ICON);
      }
    } else {
      // Get the icon path for the current property
      String iconPath = null;
      iconPath = property.getIcons().get(newValue);
      if (iconPath != null) {
        iconRelativePath.append(iconPath);
      } else {
        iconRelativePath.append(TablePropertiesConstants.EMPTY_ICON);
      }
    }
    
    // Obtain the preview for the group and set the icon with the computed style
    PreviewGroup previewGroup = previewsList.get(group);
    if (previewGroup != null) {
      // Get the image
      URL imageURL = Resource.getResource(iconRelativePath.toString());
      Image imageIcon = images.get(imageURL);
      if (imageIcon == null) {
        boolean fallback = true;
        if (colorThemeUtilities.getColorTheme().isHighContrastTheme() &&
            !colorThemeUtilities.getColorTheme().isHighContrastWhiteTheme()) {
          try {
            ImageDescriptor loadedImage = (ImageDescriptor) colorThemeUtilities.getImageInverter().loadImage(imageURL);
            loadedImage = (ImageDescriptor) colorThemeUtilities.getImageInverter().invertImage(loadedImage);
            imageIcon = loadedImage.createImage();
            fallback = false;
          } catch (IOException e) {
            //Problem here
            logger.error(e, e);
          }
        } 
        if(fallback) {
          //Load the image normally
          ImageDescriptor currentImageDescriptor = ImageDescriptor.createFromURL(imageURL);
          imageIcon = currentImageDescriptor.createImage();
        }
        images.put(imageURL, imageIcon);
      }
      // Set the image to the preview
      previewGroup.setPreviewImage(imageIcon);
      previewGroup.redraw();
    }
  }
  
  /**
   * @see org.eclipse.swt.widgets.Widget#dispose()
   */
  @Override
  public void dispose() {
    // Dispose the used images
    for (int i = 0; i < images.size(); i++) {
      images.get(i).dispose();
    }

    super.dispose();
  }
}