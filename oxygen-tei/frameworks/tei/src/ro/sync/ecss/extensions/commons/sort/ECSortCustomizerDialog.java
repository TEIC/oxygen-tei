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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;




import ro.sync.annotations.obfuscate.SkipLevel;
import ro.sync.annotations.obfuscate.SkipObfuscate;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.ui.EclipseHelpUtils;

/**
 *  Eclipse implementation of the customizer used to select the criterion information used when sorting.
 */


public class ECSortCustomizerDialog extends TrayDialog implements SortCustomizer, KeysController {
  /**
   * Radio button used to enable the sorting of all the children from a parent element.
   */
  private Button sortAllElementsRadio;
  /**
   * Radio button used to enable the sorting of only the selected elements.
   */
  private Button sortSelectedElementsRadio;
  /**
   * The composite for the sorting criteria.
   */
  private Composite criterionSection;
  /**
   * Criteria information given to the dialog.
   */
  private List<CriterionInformation> criteriaInformation;
  /**
   * <code>true</code> when elements selected in the document can be sorted.
   */
  private boolean hasSelectedSortableElements;
  /**
   * <code>true</code> when all the elements from the parent of the sort operation cannot be sorted.
   */
  private boolean cannotSortAllElements;
  /**
   * The composite for the first sorting criterion.
   */
  private CriterionComposite firstCriterionComposite;
  /**
   * The composite for the second sorting criterion.
   */
  private CriterionComposite secondCriterionComposite;
  /**
   * The composite for the third sorting criterion.
   */
  private CriterionComposite thirdCriterionComposite;
  /**
   * Criteria information containing the information chosen by the user.
   */
  private ArrayList<CriterionInformation> info;
  /**
   * <code>true</code> if the selected entries should be sorted, and all elements 
   * cannot be sorted.
   */
  private boolean onlySelectedEntries;
  /**
   * Author resource bundle.
   */
  private final AuthorResourceBundle authorResourceBundle;
  /**
   * The name of the "selected elements" radio combo.
   */
  private final String selectedElemensString;
  /**
   * The name of the all elements radio combo.
   */
  private final String allElementsString;
  /**
   * Help page ID.
   */
  private String helpPageID;
  
  /**
   * Constructor.
   * 
   * @param parentFrame The parent shell.
   * @param authorResourceBundle The author resource bundle.
   * @param selectedElemensString The name of the "selected elements" radio combo.
   * @param allElementsString The name of the "all elements" radio combo.
   */
  public ECSortCustomizerDialog(Shell parentFrame, AuthorResourceBundle authorResourceBundle, 
      String selectedElemensString, String allElementsString) {
    this(parentFrame, authorResourceBundle, selectedElemensString, allElementsString, null);
  }

  /**
   * Constructor.
   * 
   * @param parentFrame The parent shell.
   * @param authorResourceBundle The author resource bundle.
   * @param selectedElemensString The name of the "selected elements" radio combo.
   * @param allElementsString The name of the "all elements" radio combo.
   * @param helpPageID Help page ID
   */
  public ECSortCustomizerDialog(Shell parentFrame, AuthorResourceBundle authorResourceBundle, 
      String selectedElemensString, String allElementsString, String helpPageID) {
    super(parentFrame);
    this.selectedElemensString = selectedElemensString;
    this.allElementsString = allElementsString;
    this.helpPageID = helpPageID;
    int style = SWT.DIALOG_TRIM;
    style |= SWT.RESIZE;
    style |= SWT.APPLICATION_MODAL;
    setShellStyle(style);
    this.authorResourceBundle = authorResourceBundle;
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    // Main composite
    Composite mainComposite = (Composite) super.createDialogArea(parent);
    GridLayout layout = new GridLayout(4, false);
    layout.marginHeight = 10;
    layout.marginWidth = 10;
    mainComposite.setLayout(layout);
    
    // Scope section
    Composite scopeSection = createSection(mainComposite, authorResourceBundle.getMessage(ExtensionTags.RANGE));
    GridData data = new GridData(SWT.FILL, SWT.NONE, true, false, 4, 1);
    scopeSection.setLayoutData(data);
    // Create the radios
    sortSelectedElementsRadio = new Button(mainComposite, SWT.RADIO | SWT.LEFT);
    sortSelectedElementsRadio.setText(selectedElemensString);
    data = new GridData(SWT.LEFT, SWT.NONE, true, false, 2, 1);
    data.horizontalIndent = 5;
    sortSelectedElementsRadio.setLayoutData(data);
    
    sortAllElementsRadio = new Button(mainComposite, SWT.RADIO | SWT.LEFT);
    sortAllElementsRadio.setText(allElementsString);
    data = new GridData(SWT.LEFT, SWT.NONE, true, false, 2, 1);
    sortSelectedElementsRadio.setLayoutData(data);
    
    // Criterion section
    criterionSection = createSection(mainComposite, authorResourceBundle.getMessage(ExtensionTags.CRITERIA));
    data = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
    criterionSection.setLayoutData(data);
    
    // Compute the number of given criteria
    int numberOfCriteria = criteriaInformation.size();
    
    if (hasSelectedSortableElements) {
      sortSelectedElementsRadio.setEnabled(true);
      sortSelectedElementsRadio.setSelection(true);
    } else {
      sortAllElementsRadio.setSelection(true);
      sortAllElementsRadio.setEnabled(true);
      // Disable the "Selected elements" radio 
      sortSelectedElementsRadio.setEnabled(false);
    }
    
    // Disable the "All" radio
    if (cannotSortAllElements) {
      sortAllElementsRadio.setEnabled(false);
      sortAllElementsRadio.setSelection(false);
    }
    
    
    // Determine the selected element
    CriterionInformation firstSel = criteriaInformation.size() > 0 ? criteriaInformation.get(0) : null;
    for (int i = 0; i < criteriaInformation.size(); i++) {
      CriterionInformation criterionInformation = criteriaInformation.get(i);
      if (criterionInformation.isInitiallySelected()) {
        firstSel = criterionInformation;
        break;
      }
    }
    
    // First criterion composite
    firstCriterionComposite = new CriterionComposite(
        mainComposite,
        authorResourceBundle,
        criteriaInformation, 
        firstSel,
        true,
        this,
        criteriaInformation);
    
    if (numberOfCriteria > 1) {
      // Should be checked
      firstCriterionComposite.enableSortcriterion();
    }
    
    // The second criteria composite
    CriterionInformation secondSel = null;
    if (numberOfCriteria >= 2) {
      
      List<CriterionInformation> secondCriteria = new ArrayList<CriterionInformation>();
      boolean computeSel = true;
      for (int i = 0; i < criteriaInformation.size(); i++) {
        CriterionInformation criterionInformation = criteriaInformation.get(i);
        if (criterionInformation != firstSel) {
          secondCriteria.add(criterionInformation);
          if (criterionInformation.isInitiallySelected() && computeSel) {
            secondSel = criterionInformation;
            computeSel = false;
          }
        }
      }
      if (secondSel == null && secondCriteria.size() > 0) {
        secondSel = secondCriteria.get(0);
      }
      
      secondCriterionComposite = new CriterionComposite(
          mainComposite,
          authorResourceBundle,
          secondCriteria, 
          secondSel,
          false,
          this,
          criteriaInformation); 
    }
    
    // The third criteria composite
    if (numberOfCriteria >= 3) {
      List<CriterionInformation> thirdCriteria = new ArrayList<CriterionInformation>();
      CriterionInformation thirdSel = null;
      boolean computeSel = true;
      for (int i = 0; i < criteriaInformation.size(); i++) {
        CriterionInformation criterionInformation = criteriaInformation.get(i);
        if (criterionInformation != firstSel && criterionInformation != secondSel) {
          thirdCriteria.add(criterionInformation);
          if (criterionInformation.isInitiallySelected() && computeSel) {
            thirdSel = criterionInformation;
            computeSel = false;
          }
        }
      }
      
      if (thirdSel == null && thirdCriteria.size() > 0) {
        thirdSel = thirdCriteria.get(0);
      }
      
      thirdCriterionComposite = new CriterionComposite(
          mainComposite,
          authorResourceBundle,
          thirdCriteria,
          thirdSel,
          false,
          this,
          criteriaInformation);
    }
    
    return mainComposite;
  }
  
  /**
   * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
   */
  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    EclipseHelpUtils.installHelp(newShell, helpPageID);
    newShell.setText(authorResourceBundle.getMessage(ExtensionTags.SORT));
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortCustomizer#getSortInformation(java.util.List, boolean, boolean)
   */
  @Override
  public SortCriteriaInformation getSortInformation(
      List<CriterionInformation> criteriaInformation,
      boolean hasSelectedSortableElements, 
      boolean cannotSortAllElements) {
    
    this.criteriaInformation = criteriaInformation;
    this.hasSelectedSortableElements = hasSelectedSortableElements;
    this.cannotSortAllElements = cannotSortAllElements;
    
    SortCriteriaInformation sortInformation = null;
    
    if (open() == OK) {
      // Return the chosen information
      sortInformation = new SortCriteriaInformation(info.toArray(new CriterionInformation[0]), onlySelectedEntries);
    }
    
    return sortInformation;
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed() {
    // Obtain the chosen information
    info = new ArrayList<CriterionInformation>();
    
    onlySelectedEntries = sortSelectedElementsRadio != null && sortSelectedElementsRadio.getSelection();
    
    if (firstCriterionComposite.getInformation() != null) {
      info.add(firstCriterionComposite.getInformation());
    }
    
    if (secondCriterionComposite != null && secondCriterionComposite.getInformation() != null) {
      info.add(secondCriterionComposite.getInformation());
    }
    
    if (thirdCriterionComposite != null && thirdCriterionComposite.getInformation() != null) {
      info.add(thirdCriterionComposite.getInformation());
    }
    
    super.okPressed();
  }
  
  private Composite createSection(Composite parent, String text) {
    Composite composite = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    composite.setLayout(layout);

    Label label = new Label(composite, SWT.LEFT | SWT.WRAP);
    label.setText(text);
    FontData fontData = label.getFont().getFontData()[0];
    fontData.setStyle(SWT.BOLD);
    final Font font = new Font(Display.getDefault(), fontData);
    label.setFont(font);
    label.addDisposeListener(new DisposeListener() {
      @Override
      public void widgetDisposed(DisposeEvent e) {
        font.dispose();
      }
    });

    label = new Label(composite, SWT.CENTER | SWT.SEPARATOR | SWT.HORIZONTAL);
    GridData data = new GridData(SWT.FILL, SWT.NONE, true, false);
    data.horizontalSpan = 1;
    data.verticalIndent = 8;
    label.setLayoutData(data);
    
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    return composite;
  }
  
  /**
   * @see org.eclipse.jface.dialogs.Dialog#isResizable()
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.KeysController#selectionChanged(java.lang.String, java.lang.String)
   */
  @Override
  public void selectionChanged(String newSelection, String oldSelection) {
    // If only one criteria panel, there is no need to update nothing
    if (criteriaInformation.size() > 1 && newSelection != null) {
      if (firstCriterionComposite != null 
          && firstCriterionComposite.getKeyCombo() != null
          && firstCriterionComposite.getKeyCombo().getItem(
              firstCriterionComposite.getKeyCombo().getSelectionIndex()).equals(newSelection) 
          && secondCriterionComposite != null) {
        
        // The first criterion panel changed its selection, update the other criterion panels
        String secondSel = secondCriterionComposite.getKeyCombo().getItem(
            secondCriterionComposite.getKeyCombo().getSelectionIndex());
        
        // Remove all the items, so there won't be duplicates
        secondCriterionComposite.getKeyCombo().removeAll();

        List<String> items = new ArrayList<String>();
        int selectionIndex = -1;
        // Add the other item to the second criterion
        for (int i = 0; i < criteriaInformation.size(); i++) {
          String displayName = criteriaInformation.get(i).getDisplayName();
          if (!newSelection.equals(displayName)) {
            items.add(displayName);
            if (displayName.equals(secondSel) && selectionIndex == -1) {
              // Found the index of the currently selected item
              selectionIndex = items.size() - 1;
            }
          }
        }
        secondCriterionComposite.getKeyCombo().setItems(items.toArray(new String[0]));

        // Select the last selected item if it already exists in the combo values,
        // otherwise select the first item in the combo
        if (selectionIndex != -1) {
          secondCriterionComposite.getKeyCombo().select(selectionIndex);
        } else if (secondCriterionComposite.getKeyCombo().getItemCount() > 0) {
          secondCriterionComposite.getKeyCombo().select(0);
        }

        if (thirdCriterionComposite != null && secondCriterionComposite != null) {
          
          String thirdSel = thirdCriterionComposite.getKeyCombo().getItem(
              thirdCriterionComposite.getKeyCombo().getSelectionIndex());
          
          // Remove all the items, so there won't be duplicates
          thirdCriterionComposite.getKeyCombo().removeAll();
          
          // Add the other item to the second criterion
          String selectedItem = secondCriterionComposite.getKeyCombo().getItem(
              secondCriterionComposite.getKeyCombo().getSelectionIndex());
          items.clear();
          selectionIndex = -1;
          for (int i = 0; i < criteriaInformation.size(); i++) {
            String displayName = criteriaInformation.get(i).getDisplayName();
            if (!newSelection.equals(displayName) 
                && !displayName.equals(selectedItem)) {
              items.add(displayName);
              if (displayName.equals(thirdSel) && selectionIndex == -1) {
                selectionIndex = items.size() - 1;
              }
            }
          }

          thirdCriterionComposite.getKeyCombo().setItems(items.toArray(new String[0]));

          // Select the last selected item if it already exists in the combo values,
          // otherwise select the first item in the combo
          if (selectionIndex != -1) {
            thirdCriterionComposite.getKeyCombo().select(selectionIndex);
          } else if (thirdCriterionComposite.getKeyCombo().getItemCount() > 0) {
            thirdCriterionComposite.getKeyCombo().select(0);
          }
        }
      } else if (secondCriterionComposite != null 
          && secondCriterionComposite.getKeyCombo().getItem(
              secondCriterionComposite.getKeyCombo().getSelectionIndex()).equals(newSelection)
          && thirdCriterionComposite != null) {

        // The first criterion panel changed its selection, update the other criterion panels
        String thirdSel = thirdCriterionComposite.getKeyCombo().getItem(thirdCriterionComposite.getKeyCombo().getSelectionIndex());
        thirdCriterionComposite.getKeyCombo().removeAll();
        List<String> items = new ArrayList<String>();
        int selectionIndex = -1;
        // Add the other item to the second criterion
        String selectedItem = firstCriterionComposite.getKeyCombo() != null ? 
            firstCriterionComposite.getKeyCombo().getItem(
                firstCriterionComposite.getKeyCombo().getSelectionIndex()) 
            : "";
        for (int i = 0; i < criteriaInformation.size(); i++) {
          String displayName = criteriaInformation.get(i).getDisplayName();
          if (!newSelection.equals(displayName) 
              && !displayName.equals(selectedItem)) {
            items.add(displayName);
            if (displayName.equals(thirdSel) && selectionIndex == -1) {
              selectionIndex = items.size() - 1;
            }
          }
        }

        thirdCriterionComposite.getKeyCombo().setItems(items.toArray(new String[0]));

        // Select the last selected item if it already exists in the combo values,
        // otherwise select the first item in the combo 
        if (selectionIndex != -1) {
          thirdCriterionComposite.getKeyCombo().select(selectionIndex);
        } else if (thirdCriterionComposite.getKeyCombo().getItemCount() > 0) {
          thirdCriterionComposite.getKeyCombo().select(0);
        }
      }
    }
  }
}