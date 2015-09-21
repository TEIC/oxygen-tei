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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;




import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.table.properties.EditedTablePropertiesInfo.TAB_TYPE;
import ro.sync.exml.workspace.api.util.ColorThemeUtilities;

/**
 * Dialog that allows the user to edit the table properties. 
 * 
 * @author adriana_sbircea
 */

public class ECTablePropertiesCustomizerDialog extends Dialog {

  /**
   * The author resource bundle.
   */
  private AuthorResourceBundle authorResourceBundle;
  /**
   * Properties to edit.
   */
  private EditedTablePropertiesInfo editedTablePropertiesInfo;
  /**
   * The tab pane that holds all the properties.
   */
  private TabFolder tabFolder;
  /**
   * The table information which contains all the modifications.
   */
  private EditedTablePropertiesInfo tableInfo = null;
  /**
   * The color theme.
   */
  private ColorThemeUtilities colorThemeUtilities;

  /**
   * Constructor.
   * 
   * @param parentFrame           The parent frame. The dialog will be created over the parent.
   * @param authorResourceBundle  The author resource bundle. It is used to translate different 
   *                              information used inside the dialog.
   * @param colorThemeUtilities   The color theme utilities.
   */
  public ECTablePropertiesCustomizerDialog(Shell parentFrame,
      AuthorResourceBundle authorResourceBundle, ColorThemeUtilities colorThemeUtilities) {
    super(parentFrame);
    this.authorResourceBundle = authorResourceBundle;
    this.colorThemeUtilities = colorThemeUtilities;
    
    int style = SWT.DIALOG_TRIM;
    style |= SWT.RESIZE;
    style |= SWT.APPLICATION_MODAL;
    setShellStyle(style);
  }
  
  /**
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(authorResourceBundle.getMessage(ExtensionTags.TABLE_PROPERTIES));
  }
  
  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite mainComposite = (Composite) super.createDialogArea(parent);
    // Set the layout
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 10;
    layout.marginWidth = 10;
    mainComposite.setLayout(layout);
    // Create the tabbed pane
    tabFolder = new TabFolder(mainComposite, SWT.HORIZONTAL);
    tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    // Obtain the categories
    List<TabInfo> categories = editedTablePropertiesInfo.getCategories();
    TAB_TYPE selectedTab = editedTablePropertiesInfo.getSelectedTab();
    int tabToSelect = 0;
    
    for (int i = 0; i < categories.size(); i++) {
      TabInfo tabInfo = categories.get(i);
      // Create a tab item for every category
      TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
      // Create the composite that will hold the properties for current category
      ECPropertiesComposite ecPropertiesComposite = new ECPropertiesComposite(
          tabFolder,
          tabInfo.getProperties(), 
          tabInfo.getContextInfo(),
          authorResourceBundle,
          colorThemeUtilities);

      tabItem.setControl(ecPropertiesComposite);
      tabItem.setText(authorResourceBundle.getMessage(tabInfo.getTabKey()));
      
      // Compute the selected tab index
      if (tabInfo.getTabKey().equals(ExtensionTags.TABLE) && selectedTab == EditedTablePropertiesInfo.TAB_TYPE.TABLE_TAB) {
        tabToSelect = i; 
      } else if ((tabInfo.getTabKey().equals(ExtensionTags.ROW) || tabInfo.getTabKey().equals(ExtensionTags.ROWS)) 
          && selectedTab == EditedTablePropertiesInfo.TAB_TYPE.ROW_TAB) {
        tabToSelect = i; 
      } else if ((tabInfo.getTabKey().equals(ExtensionTags.COLUMN) || tabInfo.getTabKey().equals(ExtensionTags.COLUMNS)) 
          && selectedTab == EditedTablePropertiesInfo.TAB_TYPE.COLUMN_TAB) {
        tabToSelect = i; 
      } else if ((tabInfo.getTabKey().equals(ExtensionTags.CELL) || tabInfo.getTabKey().equals(ExtensionTags.CELLS))
          && selectedTab == EditedTablePropertiesInfo.TAB_TYPE.CELL_TAB) {
        tabToSelect = i; 
      }
    }
    // Select a tab
    tabFolder.setSelection(tabToSelect);
    return mainComposite;
  }

  
  /**
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed() {
    // Ok was pressed, so collect only the properties that are modified.
    List<TabInfo> categories = editedTablePropertiesInfo.getCategories();
    List<TabInfo> modifications = new ArrayList<TabInfo>();
    for (int i = 0; i < tabFolder.getItemCount(); i++) {
      TabItem item = tabFolder.getItem(i);
      ECPropertiesComposite panel = (ECPropertiesComposite) item.getControl();
      TabInfo tabInfo = categories.get(i);
      if (!panel.getModifiedProperties().isEmpty()) {
        modifications.add(new TabInfo(tabInfo.getTabKey(), panel.getModifiedProperties(), tabInfo.getNodes()));
      }
    }
    // Create the table information object which will contain only the modified properties
    if (!modifications.isEmpty()) {
      tableInfo = new EditedTablePropertiesInfo(modifications);
    }
  
    super.okPressed();
  }
  /**
   * Obtain the table information.
   * 
   * @param editedTablePropertiesInfo The information used to customize the "Table Properties dialog"
   * 
   * @return The table information if at least one of the properties was modified 
   * or <code>null</code> if none of the properties was modified or if the dialog was cancelled.
   */
  public EditedTablePropertiesInfo getTablePropertiesInformation(EditedTablePropertiesInfo editedTablePropertiesInfo) {
    this.editedTablePropertiesInfo = editedTablePropertiesInfo;
    EditedTablePropertiesInfo tableInformation = null;
    if (open() == OK) {
      tableInformation = tableInfo;
    }

    return tableInformation;
  }
}
