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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.table.properties.EditedTablePropertiesInfo.TAB_TYPE;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;
import ro.sync.exml.workspace.api.util.ColorThemeUtilities;
import ro.sync.util.Resource;

/**
 * Dialog that allows the user to edit the table properties.
 * 
 * @author adriana_sbircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class SATablePropertiesCustomizerDialog extends OKCancelDialog {
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(SATablePropertiesCustomizerDialog.class
      .getName());
  
  /**
   * Panel containing all the properties for a given element.
   * 
   * @author adriana_sbircea
   */
  class SAPropertiesPanel extends JPanel implements PropertySelectionController {
    /**
     * The property panels.
     */
    final private List<SAPropertyPanel> propertyPanels = new ArrayList<SAPropertyPanel>();

    /**
     * List of previews.
     */
    final private Map<String, JLabel> previews = new HashMap<String, JLabel>();

    /**
     * Constructor.
     * 
     * The tab name is obtained from the current key, using {@link AuthorResourceBundle}. 
     * @param properties    The list with table properties. 
     * @param contextInfo   The context information. It will be presented to the user like a label.
     */
    public SAPropertiesPanel(List<TableProperty> properties, String contextInfo) {
      GridBagLayout mgr = new GridBagLayout();
      setLayout(mgr);

      Map<String, List<TableProperty>> groups = new LinkedHashMap<String, List<TableProperty>>();
      // Group all the properties
      for (int i = 0; i < properties.size(); i++) {
        TableProperty property = properties.get(i);
        String currentGroup = property.getParentGroup();
        List<TableProperty> list = groups.get(currentGroup);
        if (list == null) {
          list = new ArrayList<TableProperty>();
        }
        list.add(property);
        groups.put(currentGroup, list);
        // Create the preview
        previews.put(currentGroup, new JLabel());
      }

      GridBagConstraints constr = new GridBagConstraints();
      constr.gridx = 0;
      constr.gridy = 0;
      constr.anchor = GridBagConstraints.WEST;
      constr.fill = GridBagConstraints.HORIZONTAL;
      constr.weightx = 1;
      constr.insets = new Insets(3, 4, 2, 4);

      Set<String> groupSet = groups.keySet();
      boolean isFirstElement = true;
      for (Iterator iterator = groupSet.iterator(); iterator.hasNext();) {
        // Found a group
        String key = (String) iterator.next();
        List<TableProperty> props = groups.get(key);
        // Add the current group
        addGroup(constr, isFirstElement, key, props);
        if (isFirstElement) {
          isFirstElement = false;
        }
      }

      // Add the context information
      if (contextInfo != null) {
        constr.gridx = 0;
        constr.gridy ++;
        constr.anchor = GridBagConstraints.WEST;
        constr.fill = GridBagConstraints.NONE;
        constr.insets.top += 7;
        constr.insets.left += 10;
        constr.weightx = 0;
        constr.weighty = 0;
        constr.gridwidth = 2;
        // We have information to show
        add(new JLabel(contextInfo), constr);
      }

      // Some adjustments for the layout
      constr.gridx = 0;
      constr.gridy ++;
      constr.fill = GridBagConstraints.BOTH;
      constr.weightx = 1;
      constr.weighty = 1;
      constr.gridwidth = 2;
      add(new JLabel(""), constr);

      if (colorThemeUtilities == null || !colorThemeUtilities.getColorTheme().isDarkTheme()) {
        setOpaque(false);
      }
    }

    /**
     * Add a group for the given properties which will have the given title.
     * 
     * @param constr          The {@link GridBagConstraints} objects which will 
     *                        be used to determine where the group will be added 
     *                        inside the current panel.
     * @param isFirstElement  <code>true</code> if the group will be the first 
     *                        element of the panel.
     * @param keyTitle        The key group to obtain the title.
     * @param properties      The properties that will be contained by the current group.
     */
    private void addGroup(GridBagConstraints constr, boolean isFirstElement, String keyTitle,
        List<TableProperty> properties) {
      JPanel groupPanel = new JPanel(new GridBagLayout());
      int maxColNr = 1;
      if (properties.size() == 1 && properties.get(0).getGuiType() == GuiElements.COMBOBOX) {
        // We do not show the titled border for one property shown using a combobox, 
        // but we have to align the components with the other, so create a titled 
        // border to obtain its insets.
        TitledBorder titledBorder = BorderFactory.createTitledBorder("");
        TitledBorder titledBorderWithTitle = BorderFactory.createTitledBorder(authorResourceBundle.getMessage(keyTitle));
        Insets borderInsets = titledBorder.getBorderInsets(groupPanel);
        // Set an empty border, with the insets from the titled border
        groupPanel.setBorder(BorderFactory.createEmptyBorder(
            borderInsets.top, 
            titledBorderWithTitle.getBorderInsets(groupPanel).left, 
            borderInsets.bottom, 
            borderInsets.right));
      } else {
        groupPanel.setBorder(BorderFactory.createTitledBorder(authorResourceBundle.getMessage(keyTitle)));
      }
      // No need to see the panel's background
      if (colorThemeUtilities == null || !colorThemeUtilities.getColorTheme().isDarkTheme()) {
        groupPanel.setOpaque(false);
      }
      // Constraints for the newly created panel
      GridBagConstraints constr1 = new GridBagConstraints();
      constr1.gridx = 0;
      constr1.anchor = GridBagConstraints.WEST;
      constr1.insets.top += 2;
      // Add every property inside this panel
      for (int i = 0; i < properties.size(); i++) {
        if (properties.get(i).getGuiType() == GuiElements.COMBOBOX) {
          maxColNr = 2;
        } else if (1 > maxColNr) {
          maxColNr = 1;
        }
        SAPropertyPanel p = new SAPropertyPanel(groupPanel, constr1, properties.get(i), authorResourceBundle, this, 0, i == 0);
        propertyPanels.add(p);
      }

      // Some layout adjustments
      constr1.fill = GridBagConstraints.BOTH;
      constr1.weightx = 1.0;
      constr1.weighty = 1.0;
      constr.gridx = 0;
      constr1.gridwidth = maxColNr;
      groupPanel.add(new JLabel(""), constr1);
      // Some adjustments
      if (isFirstElement) {
        // First element has always a bigger top inset 
        constr.insets.top = 5;
      } else {
        constr.insets.top = 0;
      }

      constr.anchor = GridBagConstraints.WEST;
      constr.gridx = 0;
      constr.gridwidth = 1;
      constr.fill = GridBagConstraints.BOTH;
      constr.weightx = 1.0;
      constr.weighty = 0.0;
      // Add the group
      add(groupPanel, constr);

      // Add preview panel
      JPanel previewPanel = new JPanel(new GridBagLayout());
      previewPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder(authorResourceBundle.getMessage(ExtensionTags.PREVIEW)), 
          BorderFactory.createEmptyBorder(0, 0, 6, 0)));
      if (colorThemeUtilities == null || !colorThemeUtilities.getColorTheme().isDarkTheme()) {
        previewPanel.setOpaque(false);
      }
      GridBagConstraints previewConstr = new GridBagConstraints();
      previewConstr.gridx = 0;
      previewConstr.gridy = 0;
      previewConstr.fill = GridBagConstraints.NONE;
      previewConstr.anchor = GridBagConstraints.CENTER;

      // Icon
      previews.get(keyTitle).setFocusable(false);
      previews.get(keyTitle).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      previewPanel.add(previews.get(keyTitle), previewConstr);
      previewPanel.setPreferredSize(new Dimension(150, previewPanel.getPreferredSize().height));
      previewPanel.setMinimumSize(new Dimension(150, previewPanel.getMinimumSize().height));
      constr.gridx ++;
      constr.fill = GridBagConstraints.BOTH;
      constr.weightx = 0;
      constr.weighty = 0;
      add(previewPanel, constr);
      // Next group will be added on the next line
      constr.gridy ++;
    }

    /**
     * @see ro.sync.ecss.extensions.commons.table.properties.PropertySelectionController#selectionChanged(ro.sync.ecss.extensions.commons.table.properties.TableProperty, java.lang.String)
     */
    @Override
    public void selectionChanged(TableProperty property, String newValue) throws AuthorOperationException {
      StringBuilder iconRelativePath = new StringBuilder();
      String group = property.getParentGroup();
      if (TablePropertiesConstants.COLSEP.equalsIgnoreCase(property.getAttributeName()) || TablePropertiesConstants.ROWSEP.equalsIgnoreCase(property.getAttributeName())) {
        // Check all the properties until the rowsep with the same parent group is found
        String colsepVal = TablePropertiesConstants.ATTR_NOT_SET;
        String rowsepVal = TablePropertiesConstants.ATTR_NOT_SET;
        // Compute which property is modified
        boolean checkRowsep = true;
        if (TablePropertiesConstants.COLSEP.equalsIgnoreCase(property.getAttributeName())) {
          colsepVal = newValue;
        } else {
          rowsepVal = newValue;
          checkRowsep = false;
        }

        // Check all properties from the same group
        for (int i = 0; i < propertyPanels.size(); i++) {
          SAPropertyPanel tableProperty = propertyPanels.get(i);
          if (checkRowsep && TablePropertiesConstants.ROWSEP.equalsIgnoreCase(tableProperty.getTableProperty().getAttributeName())) {
            rowsepVal = tableProperty.getCurrentlySelectedValue();
          } else if (!checkRowsep && TablePropertiesConstants.COLSEP.equalsIgnoreCase(tableProperty.getTableProperty().getAttributeName())) {
            colsepVal = tableProperty.getCurrentlySelectedValue();
          }
        }

        // Obtain the correct icon by combining the properties selected values
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
        // Obtain the icon
        String iconPath = null;
        iconPath = property.getIcons().get(newValue);
        if (iconPath != null) {
          iconRelativePath.append(iconPath);
        } else {
          // if no icon is found, set the empty icon
          iconRelativePath.append(TablePropertiesConstants.EMPTY_ICON);
        }
      }

      // Obtain the preview for the group and set the icon with the computed style
      JLabel iconLabel = previews.get(group);
      if (iconLabel != null) {
        // Set the icon
        BufferedImage bufferedImage = null;
        URL imageURL = Resource.getResource(iconRelativePath.toString());
        try {
          bufferedImage = (BufferedImage) colorThemeUtilities.getImageInverter().loadImage(imageURL);
        } catch (IOException e) {
          throw new AuthorOperationException(e.getMessage());
        }
        if (colorThemeUtilities.getColorTheme().isHighContrastTheme() &&
            !colorThemeUtilities.getColorTheme().isHighContrastWhiteTheme()) {
          // Always invert image colors on black high contrast themes
          try {
            bufferedImage = (BufferedImage) colorThemeUtilities.getImageInverter().invertImage(bufferedImage);
          } catch (IOException e) {
            logger.error(e, e);
          }
        }
        ImageIcon imageIcon = new ImageIcon(bufferedImage);
        iconLabel.setIcon(imageIcon);
        iconLabel.repaint();
      }
    }
    
   
    /**
     * Obtain a list with all modified properties for the current panel.
     * 
     * @return a {@link List} with all modified properties from the current panel.
     */
    public List<TableProperty> getModifiedProperties() {
      // Obtain only the modified properties
      List<TableProperty> properties = new ArrayList<TableProperty>();
      for (int i = 0; i < propertyPanels.size(); i++) {
        TableProperty modifiedProperty = propertyPanels.get(i).getModifiedProperty();
        if (modifiedProperty != null) {
          properties.add(modifiedProperty);
        }
      }

      return properties;
    }
  }
  
  /**
   * The tabbed pane for the table properties.
   */
  private JTabbedPane tabbedPane;
  /**
   * The author resource bundle.
   */
  private AuthorResourceBundle authorResourceBundle;
  
  /**
   * The color theme utilities.
   */
  private ColorThemeUtilities colorThemeUtilities;
  
  /**
   * Constructor.
   * 
   * @param parentFrame           The parent frame of the dialog.
   * @param authorResourceBundle  The author resource bundle.It is used for translations.
   * @param colorThemeUtilities            The color theme.
   */
  public SATablePropertiesCustomizerDialog(Frame parentFrame, AuthorResourceBundle authorResourceBundle, ColorThemeUtilities colorThemeUtilities) {
    super(parentFrame, authorResourceBundle.getMessage(ExtensionTags.TABLE_PROPERTIES), true);
    this.authorResourceBundle = authorResourceBundle;
    this.colorThemeUtilities = colorThemeUtilities;
  }

  /**
   * Obtain the table information.
   * @param info The information used to customize the "Table Properties dialog"
   * 
   * @return The table information if at least one of the properties was modified 
   * or <code>null</code> if none of the properties was modified or if the dialog was cancelled.
   */
  public EditedTablePropertiesInfo getTablePropertiesInformation(EditedTablePropertiesInfo info) {
    EditedTablePropertiesInfo tableInfo = null;

    // Create the dialog content
    getContentPane().setLayout(new GridBagLayout());
    tabbedPane = new JTabbedPane();
    List<TabInfo> categories = info.getCategories();
    TAB_TYPE selectedTab = info.getSelectedTab();
    int selectedTabIndex = 0;
    for (int i = 0; i < categories.size(); i++) {
      TabInfo tabInfo = categories.get(i);
      SAPropertiesPanel panel = new SAPropertiesPanel(tabInfo.getProperties(), tabInfo.getContextInfo());
      tabbedPane.addTab(authorResourceBundle.getMessage(tabInfo.getTabKey()), panel);
      // Obtain the selected tab
      if (tabInfo.getTabKey().equals(ExtensionTags.TABLE) && selectedTab == TAB_TYPE.TABLE_TAB) {
       selectedTabIndex = i; 
      } else if ((tabInfo.getTabKey().equals(ExtensionTags.ROW) || tabInfo.getTabKey().equals(ExtensionTags.ROWS))
          && selectedTab == TAB_TYPE.ROW_TAB) {
        selectedTabIndex = i; 
       } else if ((tabInfo.getTabKey().equals(ExtensionTags.COLUMN) || tabInfo.getTabKey().equals(ExtensionTags.COLUMNS)) 
           && selectedTab == TAB_TYPE.COLUMN_TAB) {
         selectedTabIndex = i;
       } else if ((tabInfo.getTabKey().equals(ExtensionTags.CELL) || tabInfo.getTabKey().equals(ExtensionTags.CELLS)) 
           && selectedTab == TAB_TYPE.CELL_TAB) {
         selectedTabIndex = i; 
       }
    }

    // Select the index
    if (categories.size() > 0) {
      tabbedPane.setSelectedIndex(selectedTabIndex);
    }
    
    // Add the tabbed pane to the dialog
    GridBagConstraints constr = new GridBagConstraints();
    constr.gridx = 0;
    constr.gridy = 0;
    constr.fill = GridBagConstraints.BOTH;
    constr.weightx = 1;
    constr.weighty = 1;
    constr.insets = new Insets(0, 0, 10, 0);
    constr.gridwidth = 1;
    getContentPane().add(tabbedPane, constr);

    setResizable(true);
    pack();
    setMinimumSize(new Dimension(getSize().width, getSize().height));
    setVisible(true);

    if (getResult() == RESULT_OK) {
      // Ok was pressed, so compute the modified properties
      List<TabInfo> modifications = new ArrayList<TabInfo>();
      for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
        SAPropertiesPanel panel = (SAPropertiesPanel) tabbedPane.getComponentAt(i);
        TabInfo tabInfo = categories.get(i);
        if (!panel.getModifiedProperties().isEmpty()) {
          modifications.add(new TabInfo(tabInfo.getTabKey(), panel.getModifiedProperties(), tabInfo.getNodes()));
        }
      }

      // There is at least one property modified, so create the table information
      if (!modifications.isEmpty()) {
        tableInfo = new EditedTablePropertiesInfo(modifications);
      }
    }

    return tableInfo;
  }
}
