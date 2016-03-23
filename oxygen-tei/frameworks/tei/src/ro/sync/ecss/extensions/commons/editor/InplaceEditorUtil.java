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
package ro.sync.ecss.extensions.commons.editor;

import java.awt.FontMetrics;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;




import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.editor.InplaceEditorArgumentKeys;
import ro.sync.exml.view.graphics.Dimension;
import ro.sync.exml.workspace.api.Platform;

/**
 * Utility methods for preparing the in-place editors for being displayed.
 */

public class InplaceEditorUtil {
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(InplaceEditorUtil.class.getName());
  
  /**
   * Computes the preferred size for the panel.
   * 
   * @param panel A panel used as an editor. 
   * @param context In-place editing context.
   * 
   * @return The preferred size for the given context. 
   */
  public static Dimension getPreferredSize(JPanel panel, AuthorInplaceContext context) {
    final java.awt.Dimension preferredSize = panel.getPreferredSize();

    int width = preferredSize.width;
    
    // First check the WIDTH property.
    int imposedWidth = context.getPropertyEvaluator().evaluateWidthProperty(
        context.getArguments(), 
        context.getStyles().getFont().getSize());
    if (imposedWidth != -1) {
      width = imposedWidth;
    } else {
      // If no width is imposed, check the columns property.
      Integer columns = (Integer) context.getArguments().get(InplaceEditorArgumentKeys.PROPERTY_COLUMNS);
      if (columns != null && columns > 0) {
        FontMetrics fontMetrics = panel.getFontMetrics(panel.getFont());
        width = columns * fontMetrics.charWidth('w');
      }
    }
    
    return new Dimension(width, preferredSize.height);
  }
  
  /**
   * Computes the preferred size for the given combo box.
   * 
   * @param comboBox A combo box used as an editor. 
   * @param context In-place editing context.
   * 
   * @return The preferred size for the given context. 
   */
  public static Dimension getPreferredSize(JComboBox comboBox, AuthorInplaceContext context) {
    int width = comboBox.getPreferredSize().width;
    
    // First check the WIDTH property.
    int imposedWidth = context.getPropertyEvaluator().evaluateWidthProperty(
        context.getArguments(), 
        context.getStyles().getFont().getSize());
    if (imposedWidth != -1) {
      width = imposedWidth;
    } else {
      // If no width is imposed, check the columns property.
      Integer columns = (Integer) context.getArguments().get(InplaceEditorArgumentKeys.PROPERTY_COLUMNS);
      if (columns != null && columns > 0) {
        FontMetrics fontMetrics = comboBox.getFontMetrics(comboBox.getFont());
        width = columns * fontMetrics.charWidth('w');
      }
    }
    
    return new Dimension(width, comboBox.getPreferredSize().height);
  }

  /**
   * Computes the preferred size for the given text field.
   * 
   * @param textField A text field used as an editor. 
   * @param context In-place editing context.
   * 
   * @return The preferred size for the given context. 
   */
  public static Dimension getPreferredSize(JTextField textField, AuthorInplaceContext context) {
    FontMetrics fontMetrics = textField.getFontMetrics(textField.getFont());
    // EXM-26151 Make the text filed a bit wider when relying on preferred size.
    int width = textField.getPreferredSize().width + fontMetrics.charWidth('w');
    
    // First check the WIDTH property.
    int imposedWidth = context.getPropertyEvaluator().evaluateWidthProperty(
        context.getArguments(), 
        context.getStyles().getFont().getSize());
    if (imposedWidth != -1) {
      width = imposedWidth;
    } else {
      // If no width is imposed, check the columns property.
      Integer columns = (Integer) context.getArguments().get(InplaceEditorArgumentKeys.PROPERTY_COLUMNS);
      if (columns != null && columns > 0) {
        width = columns * fontMetrics.charWidth('w');
      } else if (textField.getText().length() == 0) {
        // A minimum width;
        width = 2 * fontMetrics.charWidth('w');
      }
    }
    
    return new Dimension(width, textField.getPreferredSize().height);
  }
  
  /**
   * Computes the required size for the editor and positions the caret at the 
   * end of the text. The caret offset will also be scrolled to be visible.
   * 
   * @param comboBox Combo box used for editing.
   * @param context In-place editing context.
   */
  public static void relayout(final JComboBox comboBox, AuthorInplaceContext context) {
    Dimension size = InplaceEditorUtil.getPreferredSize(comboBox, context);
    // Unless the combo has a size it will not be able to respond to certain
    // calls like modelToView().
    comboBox.setSize(size.width, size.height);
    comboBox.doLayout();
    
    setCaretAtEnd((JTextField) comboBox.getEditor().getEditorComponent(), context);
  }

  /**
   * Computes the required size for the editor and positions the caret at the 
   * end of the text. The caret offset will also be scrolled to be visible.
   * 
   * @param textField Text field used for editing.
   * @param context In-place editing context.
   */
  public static void relayout(final JTextField textField, AuthorInplaceContext context) {
    Dimension size = InplaceEditorUtil.getPreferredSize(textField, context);
    // Unless the text field has a size it will not be able to respond to certain
    // calls like modelToView().
    textField.setSize(size.width, size.height);
    textField.doLayout();
    
    setCaretAtEnd(textField, context);
  }

  /**
   * Sets the caret at the end of the text from the text field.
   * 
   * @param textField Text field to be scrolled.
   * @param context In-place editing context.
   */
  public static void setCaretAtEnd(final JTextComponent textField, AuthorInplaceContext context) {
    boolean isSA = true;
    if (context.getAuthorAccess() != null 
        && context.getAuthorAccess().getWorkspaceAccess() != null) {
      isSA = Platform.STANDALONE.equals(
          context.getAuthorAccess().getWorkspaceAccess().getPlatform());
    }
    // This renderer is sometimes used from SWT.
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        if(textField != null){
          // If the text if longer than the available width we want the last part to be presented.
          int length = textField.getText().length();
          textField.setCaretPosition(length);
          try {
            final java.awt.Rectangle modelToView = textField.modelToView(length);
            if (modelToView != null) {
              textField.scrollRectToVisible(modelToView);
            }
          } catch (BadLocationException e) {
            if (logger.isDebugEnabled()) {
              logger.debug(e, e);
            }
          }
        }
      }
    };

      if (isSA
          // When the renderer is invoked for Eclipse the current thread will 
          // be the SWT thread. We needed it on the AWT thread.
          || SwingUtilities.isEventDispatchThread()) {
        runnable.run();
      } else {
        // Using invokeSynchronously on Mac, runtime-workbench, causes 
        // the main thread to enter a deadlock with AWT.
        // AWT waits for a cursor in a layout validate.
        //
        SwingUtilities.invokeLater(runnable);
      }

  }
}