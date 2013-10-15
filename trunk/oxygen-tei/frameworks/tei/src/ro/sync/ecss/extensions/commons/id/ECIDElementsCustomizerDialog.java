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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;

/**
 * Dialog used to customize DITA elements which have auto ID generation. 
 *  It will be also used when the IDs are generated manually.
 * It is used on standalone implementation.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class ECIDElementsCustomizerDialog extends Dialog {
  
  /**
   * The list of elements or class values for which to generate IDs
   */
  private List listOfElements;
  
  /**
   * Auto assign element IDs
   */
  private Button autoAssignElementIDs;
  
  /**
   * Filter IDs on Copy
   */
  private Button filterIDsOnCopy;
  
  /**
   * The pattern used for id generation.
   */
  private Text idGenerationPatternField;

  /**
   * The add button.
   */
  private Button addButton;

  /**
   * The edit button.
   */
  private Button editButton;

  /**
   * The remove button.
   */
  private Button removeButton;

  /**
   * The list message
   */
  private final String listMessage;

  /**
   * Information about auto ID generation
   */
  private GenerateIDElementsInfo autoIDElementsInfo;

  /**
   * Author resource bundle.
   */
  private final AuthorResourceBundle authorResourceBundle;

  
  /**
   * Constructor.
   * 
   * @param parentShell           The parent shell for the dialog. 
   * @param listMessage           The message label used on the list.
   * @param authorResourceBundle  The author resource bundle.
   */
  public ECIDElementsCustomizerDialog(
      Shell parentShell, String listMessage, AuthorResourceBundle authorResourceBundle) {
    super(parentShell);
    this.listMessage = listMessage;
    this.authorResourceBundle = authorResourceBundle;
  }
  
  /**
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  @Override
  protected void configureShell(Shell newShell) {
    newShell.setText(authorResourceBundle.getMessage(ExtensionTags.ID_OPTIONS));
    super.configureShell(newShell);
  }
  
  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new GridLayout(2, false));

    Label label = new Label(composite, SWT.LEFT);
    GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
    data.horizontalSpan = 1;
    data.horizontalIndent = 5;
    data.verticalIndent = 10;
    label.setLayoutData(data);
    
    label.setText(authorResourceBundle.getMessage(ExtensionTags.ID_PATTERN));
    
    idGenerationPatternField = new Text(composite, SWT.SINGLE | SWT.BORDER);
    data = new GridData(SWT.FILL, SWT.NONE, true, false);
    data.horizontalSpan = 1;
    data.verticalIndent = 10;
    data.horizontalIndent = 5;
    idGenerationPatternField.setLayoutData(data);
    
    // List composite
    Composite listGroup = new Composite(composite, SWT.SINGLE);
    listGroup.setLayout(new GridLayout(3, true));
    data = new GridData(SWT.FILL, SWT.NONE, true, false);
    data.horizontalSpan = 3;
    listGroup.setLayoutData(data);
    
    Label label2 = new Label(listGroup, SWT.LEFT);
    data = new GridData(SWT.FILL, SWT.NONE, true, false);
    data.horizontalSpan = 3;
    label2.setLayoutData(data);
    label2.setText(listMessage + ":");
    
    listOfElements = new List(listGroup, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
    data = new GridData(SWT.FILL, SWT.NONE, true, false);
    data.horizontalSpan = 3;
    data.widthHint = 250;
    data.heightHint = 200;
    listOfElements.setLayoutData(data);
    
    // The 'Add' button 
    addButton = new Button(listGroup, SWT.PUSH);
    addButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        addNewElement();
      }
    });
    
    addButton.setText(authorResourceBundle.getMessage(ExtensionTags.ADD));
    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    data.horizontalSpan = 1;
    addButton.setLayoutData(data);
    
    // The 'Edit' button
    editButton = new Button(listGroup, SWT.PUSH);
    editButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editElement();
      }
    });
    
    editButton.setText(authorResourceBundle.getMessage(ExtensionTags.EDIT));
    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    data.horizontalSpan = 1;
    editButton.setLayoutData(data);
    
    // The 'Remove' button
    removeButton = new Button(listGroup, SWT.PUSH);
    removeButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        removeElement();
      }
    });
    
    removeButton.setText(authorResourceBundle.getMessage(ExtensionTags.REMOVE));
    data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    data.horizontalSpan = 1;
    removeButton.setLayoutData(data);
    
    listOfElements.addSelectionListener(new SelectionAdapter(){
      @Override
      public void widgetSelected(SelectionEvent e) {
        updateButtonState();
      }
    });
    
    // Auto assign checkbox
    autoAssignElementIDs = new Button(composite, SWT.CHECK);
    autoAssignElementIDs.setText(authorResourceBundle.getMessage(ExtensionTags.AUTOGENERATE_IDS_FOR_ELEMENTS));
    data = new GridData(SWT.FILL, SWT.NONE, true, false);
    data.horizontalSpan = 2;
    data.horizontalIndent = 5;
    autoAssignElementIDs.setLayoutData(data);
    
    // Remove IDs on copy
    filterIDsOnCopy = new Button(composite, SWT.CHECK);
    filterIDsOnCopy.setText(authorResourceBundle.getMessage(ExtensionTags.REMOVE_IDS_ON_COPY_IN_SAME_DOC));
    data = new GridData(SWT.FILL, SWT.NONE, true, false);
    data.horizontalSpan = 2;
    data.horizontalIndent = 5;
    filterIDsOnCopy.setLayoutData(data);
    
    // Update the checkbox state from the info
    if (autoAssignElementIDs != null) {
      autoAssignElementIDs.setSelection(autoIDElementsInfo.isAutoGenerateIDs());
      
      String idGenerationPattern = autoIDElementsInfo.getIdGenerationPattern();
      idGenerationPatternField.setText(
           idGenerationPattern != null ? idGenerationPattern : "");
      
      // Set the data in the list model.
      String[] elementsWithIDGeneration = autoIDElementsInfo.getElementsWithIDGeneration();
      listOfElements.setItems(
          elementsWithIDGeneration != null ? elementsWithIDGeneration : new String[0]);
      
      filterIDsOnCopy.setSelection(autoIDElementsInfo.isFilterIDsOnCopy());
      
      idGenerationPatternField.setToolTipText(autoIDElementsInfo.getPatternTooltip());
    }
    updateButtonState();
    
    return composite;
  }
  
  /**
   * Update the buttons state on selection change.
   */
  private void updateButtonState() {
    int selectedIndex = listOfElements.getSelectionIndex();
    boolean selected = selectedIndex != -1;
    
    editButton.setEnabled(selected);
    removeButton.setEnabled(selected);
  }

  /**
   * Add a new element.
   */
  private void addNewElement() {
    InputDialog inputDialog = new InputDialog(
        getShell(), authorResourceBundle.getMessage(ExtensionTags.ADD), listMessage + ": ", "", null);
    int open = inputDialog.open();
    if(open == InputDialog.OK) {
      String value = inputDialog.getValue();
      if (value != null) {
        listOfElements.add(value);
        // Select last element.
        listOfElements.setSelection(listOfElements.getItemCount() - 1);
      }
    }
  }
  
  /**
   * Edit the selected element.
   */
  private void editElement() {
    int selectedIndex = listOfElements.getSelectionIndex();
    if (selectedIndex != -1) {
      InputDialog inputDialog = new InputDialog(
          getShell(), authorResourceBundle.getMessage(ExtensionTags.EDIT), listMessage + ": ",
          listOfElements.getItem(selectedIndex), null);
      int open = inputDialog.open();
      if(open == InputDialog.OK) {
        String value = inputDialog.getValue();
        if (value != null) {
          listOfElements.setItem(selectedIndex, value);
        }
      }
    }
  }
  
  /**
   * Remove the selected element.
   */
  private void removeElement() {
    int selectedIndex = listOfElements.getSelectionIndex();
    if (selectedIndex != -1) {
      listOfElements.remove(selectedIndex);
      int size = listOfElements.getItemCount();
      if (size > 0) {
        if (selectedIndex < size) {
          listOfElements.setSelection(selectedIndex);
        } else {
          listOfElements.setSelection(size - 1);
        }
      }
    }
  }
  
  
  /**
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed() {
    autoIDElementsInfo = 
      new GenerateIDElementsInfo(
        autoAssignElementIDs.getSelection(),
        idGenerationPatternField.getText(),
        listOfElements.getItems(),
        filterIDsOnCopy.getSelection());
    super.okPressed();
  }

  /**
   * @param info The initial information
   * @return The new information or null if canceled.
   */
  public GenerateIDElementsInfo showDialog(GenerateIDElementsInfo info) {
    this.autoIDElementsInfo = info;
    if (open() == OK) {
      // OK  pressed.
      return this.autoIDElementsInfo;
    }
    return null;
  }
}