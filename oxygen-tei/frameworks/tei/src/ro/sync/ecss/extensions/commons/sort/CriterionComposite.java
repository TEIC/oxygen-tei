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

import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;




import ro.sync.annotations.obfuscate.SkipLevel;
import ro.sync.annotations.obfuscate.SkipObfuscate;
import ro.sync.ecss.extensions.api.AuthorResourceBundle;
import ro.sync.ecss.extensions.commons.ExtensionTags;

/**
 * This class will add to the given parent container a checkbox to enable the criterion, a combobox to select the key,
 * a type combobox and order combobox. It will return the user choice as a {@link CriterionInformation} object.
 */


public class CriterionComposite {
  /**
   * <code>true</code> if the checkbox for enabling the criterion should be added.
   */
  private boolean addCheckbox;
  /**
   * The checkbox to enable the criterion.
   */
  private Button keyCheckbox;
  /**
   * The combobox to select the key.
   */
  private Combo keyCombo;
  /**
   * The sorting method combo. It's one of the: {@link CriterionInformation#TYPE_TEXT},
   * {@link CriterionInformation#TYPE_NUMERIC}, {@link CriterionInformation#TYPE_DATE}.
   */
  private ComboViewer typeCombo;
  /**
   * The sorting order combo. It's one of the: {@link CriterionInformation#ORDER_ASCENDING},
   * {@link CriterionInformation#ORDER_DESCENDING}.
   */
  private ComboViewer orderCombo;
  /**
   * The keys controller. It is notified when the keys combobox changes its selection.
   */
  private final KeysController keysController;
  /**
   *  All the available criteria.
   */
  private final List<CriterionInformation> allCriteria;

  /**
   * Constructor.
   * 
   * @param parent        The parent {@link Composite}. 
   * @param authorResourceBundle The Author resource bundle
   * @param criterionInformation The list of available criterion which will be added to the keys combobox.
   * @param selectedItem  The item which will be selected in the keys combobox.
   * @param isFirstCriterion <code>true</code> if the titles for every component of the current composite
   *                   should be displayed.
   * @param keysController The keys combo controller.
   * @param allCriteria All criteria information, not only the 
   *                          criteria information shown by the current criterion composite.
   */
  public CriterionComposite(
      Composite parent, 
      final AuthorResourceBundle authorResourceBundle,
      List<CriterionInformation> criterionInformation,
      CriterionInformation selectedItem, 
      boolean isFirstCriterion,
      KeysController keysController,
      List<CriterionInformation> allCriteria) {
    this.keysController = keysController;
    this.allCriteria = allCriteria;
    this.addCheckbox = allCriteria.size() > 1;

    // Add titles labels
    if (addCheckbox) {
      new Label(parent, SWT.LEFT).setText("");
    }
    Label label = new Label(parent, SWT.LEFT);
    label.setText(authorResourceBundle.getMessage(isFirstCriterion ? ExtensionTags.SORT_BY : ExtensionTags.AND_THEN_BY) + ":");
    GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);

    gd = new GridData(SWT.FILL, SWT.NONE, true, false);
    gd.horizontalSpan = 3;
    label.setLayoutData(gd);  

    // Create the key checkbox
    if (addCheckbox) {
      keyCheckbox = new Button(parent, SWT.CHECK | SWT.LEFT);
      keyCheckbox.setText("");
      keyCheckbox.addSelectionListener(new SelectionListener() {
        
        @Override
        public void widgetSelected(SelectionEvent e) {
          changeControlsState();
        }
        
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
          changeControlsState();
        }
      });
    }
    
    // Key combo
    keyCombo = new Combo(parent, SWT.READ_ONLY);
    gd = new GridData(SWT.FILL, SWT.NONE, true, false);
    gd.horizontalSpan = addCheckbox ? 1 : 2;
    keyCombo.setLayoutData(gd);
    keyCombo.setToolTipText(authorResourceBundle.getMessage(ExtensionTags.SELECT_KEY_COLUMN_TOOLTIP));

    keyCombo.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        selectionChanged();
      }

      private void selectionChanged() {
        String currentSel = keyCombo.getItem(keyCombo.getSelectionIndex());
        if (currentSel != null) {
          CriterionComposite.this.keysController.selectionChanged(currentSel, null);
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        selectionChanged();
      }
    });

    // Select the item
    String[] criterionInfo = new String[criterionInformation.size()];
    for (int i = 0; i < criterionInformation.size(); i++) {
      criterionInfo[i] = criterionInformation.get(i).getDisplayName();
    }

    // Set items
    keyCombo.setItems(criterionInfo);

    // Select the item
    if (selectedItem != null) {
      int index = 0;
      for (int i = 0; i < keyCombo.getItemCount(); i++) {
        if (keyCombo.getItem(i).equals(selectedItem.getDisplayName())) {
          index = i;
          break;
        }
      }
      keyCombo.select(index);
    } else {
      // Select the first item
      keyCombo.select(0);
    }

    // Type combo
    typeCombo = new ComboViewer(parent, SWT.READ_ONLY);
    typeCombo.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        return authorResourceBundle.getMessage((String) element);
      }
    });
    typeCombo.getCombo().setToolTipText(authorResourceBundle.getMessage(ExtensionTags.SELECT_TYPE_COMBO_TOOLTIP));
    typeCombo.getCombo().setItems(new String[] {CriterionInformation.TYPE.TEXT.getName(), CriterionInformation.TYPE.NUMERIC.getName(),
        CriterionInformation.TYPE.DATE.getName()});
    typeCombo.getCombo().select(0);
    gd = new GridData(SWT.FILL, SWT.NONE, true, false);
    gd.horizontalSpan = 1;
    typeCombo.getCombo().setLayoutData(gd);
    
    // Order combo
    orderCombo = new ComboViewer(parent, SWT.READ_ONLY);
    orderCombo.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        return authorResourceBundle.getMessage((String) element);
      }
    });
    
    orderCombo.getCombo().setToolTipText(authorResourceBundle.getMessage(ExtensionTags.SELECT_ORDER_COMBO_TOOLTIP));
    orderCombo.getCombo().setItems(new String[] {CriterionInformation.ORDER.ASCENDING.getName(), CriterionInformation.ORDER.DESCENDING.getName()});
    orderCombo.getCombo().select(0);
    gd = new GridData(SWT.FILL, SWT.NONE, true, false);
    gd.horizontalSpan = 1;
    orderCombo.getCombo().setLayoutData(gd);
    
    changeControlsState();
  }
  
  /**
   * Obtain access to the keys combobox.
   * 
   * @return Returns the keys combobox.
   */
  public Combo getKeyCombo() {
    return keyCombo;
  }

  /**
   * Returns the user input as a {@link CriterionInformation} object.
   * 
   * @return The criterion information selected by the user in the current component.
   */
  public CriterionInformation getInformation() {
    CriterionInformation info = null;
    
    if (!addCheckbox || keyCheckbox.getSelection()) {
      int index = 0;
      if (keyCombo != null) {
        for (int i = 0; i < allCriteria.size(); i++) {
          if (allCriteria.get(i).getDisplayName().equals(keyCombo.getItem(keyCombo.getSelectionIndex()))) {
            index = i;
            break;
          }
        }
      }
      info = new CriterionInformation(
          index, 
          typeCombo.getCombo().getItem(typeCombo.getCombo().getSelectionIndex()),
          orderCombo.getCombo().getItem(orderCombo.getCombo().getSelectionIndex()),
          keyCombo != null ? keyCombo.getItem(keyCombo.getSelectionIndex()) : "Column 1");
    }
    
    return info;
  }
 
  /**
   * Selects the checbox associated with the criterion panel, which means that the criterion information from it will be taken
   * into account when sorting.
   */
  public void enableSortcriterion() {
    keyCheckbox.setSelection(true);
    changeControlsState();
  }
  
  /**
   * Change the state of the criterion combos.
   */
  private void changeControlsState() {
    keyCombo.setEnabled(!addCheckbox || keyCheckbox.getSelection());
    typeCombo.getCombo().setEnabled(!addCheckbox || keyCheckbox.getSelection());
    orderCombo.getCombo().setEnabled(!addCheckbox || keyCheckbox.getSelection());
    
    
  }
}