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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.CursorType;
import ro.sync.ecss.extensions.api.access.AuthorUtilAccess;
import ro.sync.ecss.extensions.api.editor.AbstractInplaceEditor;
import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.editor.EditingEvent;
import ro.sync.ecss.extensions.api.editor.InplaceEditorArgumentKeys;
import ro.sync.ecss.extensions.api.editor.InplaceRenderer;
import ro.sync.ecss.extensions.api.editor.RendererLayoutInfo;
import ro.sync.exml.view.graphics.Dimension;
import ro.sync.exml.view.graphics.Font;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;

/**
 * Simple URL Chooser in-place editor.
 * 
 * @author costi
 * @author adriana
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class SimpleURLChooserEditor extends AbstractInplaceEditor implements InplaceRenderer {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(SimpleURLChooserEditor.class.getName());
  
  /**
   * <code>true</code> if the platform is Eclipse.
   */
  private static final Boolean IS_ECLIPSE =
      Boolean.valueOf(System.getProperty("com.oxygenxml.is.eclipse.plugin"));

  /**
   * <code>true</code> if the platform is Windows.
   */
  private static final boolean IS_WIN32 = System.getProperty("os.name").toUpperCase().startsWith("WIN");

  /**
   * The vertical gap of the panel layout.
   */
  private final static int VGAP = 0;
  
  /**
   * The horizontal gap of the panel layout.
   */
  private final static int HGAP = 5;

  /**
   * Undo manager property.
   */
  private static final String UNDO_MANAGER_PROPERTY = "undo-manager-property";
  
  /**
   * Browse URL button.
   */
  private final JButton browseBtn;
  
  /**
   * URL chooser panel.
   */
  private final JPanel urlChooserPanel;
  
  /**
   * URL text field.
   */
  private JTextField urlTextField;
  /**
   * <code>true</code> if we are during browse.
   */
  private boolean isBrowsing = false;

  /**
   * Default foreground color of the text field.
   */
  private final Color defaultForeground;

  /**
   * Author Util.
   */
  private AuthorUtilAccess utilAccess;

  /**
   * The default font.
   */
  private final java.awt.Font defaultFont;

  /**
   * Constructor.
   */
  public SimpleURLChooserEditor() {
    urlChooserPanel = new JPanel(new BorderLayout(HGAP, VGAP));
    URL imageURL = SimpleURLChooserEditor.class.getResource("/images/Open16.gif");
    browseBtn = new JButton();
    if (imageURL != null) {
      browseBtn.setIcon((new ImageIcon(imageURL)));
    }
    if (IS_WIN32) {
      // WE ARE ON WINDOWS
      browseBtn.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 5));
    }
    urlTextField = createTextField();
    defaultForeground = urlTextField.getForeground();
    
    urlTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
        fireEditingOccured();
      }
      @Override
      public void insertUpdate(DocumentEvent e) {
        fireEditingOccured();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
        fireEditingOccured();
      }
    });
    
    urlChooserPanel.add(urlTextField, BorderLayout.CENTER);
    urlChooserPanel.add(browseBtn,BorderLayout.EAST);
    urlChooserPanel.setOpaque(false);
    
    urlTextField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          // ESC must cancel the edit.
          e.consume();
          cancelEditing();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          // An ENTER commits the value.
          e.consume();
          stopEditing(true);
        }
      }
    });
    
    browseBtn.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          // ESC must cancel the edit.
          e.consume();
          cancelEditing();
        }
      }
    });
    
    FocusListener focusListener = new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if (e.getOppositeComponent() != browseBtn
            && e.getOppositeComponent() != urlTextField
            && !isBrowsing) {
          // The focus is outside the components of this editor.
          fireEditingStopped(new EditingEvent(urlTextField.getText(), e.getOppositeComponent() == null));
        }
      }
    };
    
    browseBtn.addFocusListener(focusListener);
    urlTextField.addFocusListener(focusListener);
    
    Insets originalInsets = urlTextField.getMargin();
    Insets imposedInsets = null;
    if(originalInsets != null) {
      imposedInsets = (Insets) originalInsets.clone();
    } else {
      //EXM-27442 - Maybe Nimbus LF, set some margins.
      imposedInsets = new Insets(1, 1, 1, 1);
    }
    if (IS_WIN32 && IS_ECLIPSE) {
      // On Eclipse the text field text should not flicker
      imposedInsets.top = -1;
      imposedInsets.left += 3;
    }
    
    urlTextField.setMargin(imposedInsets);
    
    defaultFont = urlTextField.getFont();
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "A sample implementation that provides a browse button associated with a text field.";
  }

  ///////////////////////////// RENDERER METHODS //////////////////////
  
  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getRendererComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext)
   */
  @Override
  public Object getRendererComponent(AuthorInplaceContext context) {
    // The renderer will be reused so we must make sure it's properly initialized.
    prepareComponents(context, false);
    return urlChooserPanel;
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getRenderingInfo(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext)
   */
  @Override
  public RendererLayoutInfo getRenderingInfo(AuthorInplaceContext context) {
    // The renderer will be reused so we must make sure it's properly initialized.
    prepareComponents(context, false);
    
    return computeRenderingInfo(context);
  }

  /**
   * Compute the dimension of the editor.
   * 
   * @param context The current context.
   * 
   * @return Layout information.
   */
  private RendererLayoutInfo computeRenderingInfo(AuthorInplaceContext context) {
    final java.awt.Dimension preferredSize = urlTextField.getPreferredSize();

    // Get width
    int width = urlTextField.getPreferredSize().width;
   
    Integer columns = (Integer) context.getArguments().get(InplaceEditorArgumentKeys.PROPERTY_COLUMNS);
    if (columns != null && columns > 0) {
      FontMetrics fontMetrics = urlTextField.getFontMetrics(urlTextField.getFont());
      width = columns * fontMetrics.charWidth('w');
    }
    // Add width for button and gap
    width += HGAP + browseBtn.getPreferredSize().getWidth();
    
    // Get height correction
    int correction = 0;
    if (IS_ECLIPSE) {
      // When using the renderer for Eclipse, MAC OS with just an icon,
      // the SWING button is smaller than the SWT and when imposing the size to 
      // the SWT one the SWT button looks bad.
      correction = 5;
    }
    
    return new RendererLayoutInfo(
        urlTextField.getBaseline(preferredSize.width, preferredSize.height),
        new Dimension(width, preferredSize.height + correction));
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getTooltipText(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext, int, int)
   */
  @Override
  public String getTooltipText(AuthorInplaceContext context, int x, int y) {
    // The renderer will be reused so we must make sure it's properly initialized.
    prepareComponents(context, false);
    
    return "Browse URL";
  }

  ///////////////////////////// EDITOR METHODS //////////////////////
  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#getEditorComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext, ro.sync.exml.view.graphics.Rectangle, ro.sync.exml.view.graphics.Point)
   */
  @Override
  public Object getEditorComponent(final AuthorInplaceContext context, Rectangle allocation, Point mouseLocation) {
    prepareComponents(context, true);
    
    // Get the editor location to make the selected URL relative.
    final AuthorAccess authorAccess = context.getAuthorAccess();
    final URL editorLocation = authorAccess.getEditorAccess().getEditorLocation();
    
    browseBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        isBrowsing = true;
        try {
          URL chooseURL = context.getAuthorAccess().getWorkspaceAccess().chooseURL("Choose URL", null, null);
          if (chooseURL != null) {
            // Make the chosen URL relative to the current file
            String relativeURL = context.getAuthorAccess().getUtilAccess().makeRelative(editorLocation, chooseURL);
            // EXM-25327 Do not insert the username and password in the document
            try {
              final URL clearedURL = utilAccess.removeUserCredentials(new URL(relativeURL));
              urlTextField.setText(clearedURL.toExternalForm());
            } catch (MalformedURLException e1) {
              urlTextField.setText(relativeURL);
            }
            
            // EXM-25433 Commit to document.
            fireCommitValue(new EditingEvent((String) getValue()));
          }
        } finally {
          isBrowsing = false;
        }
      }
    });
    
    return urlChooserPanel;
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#getScrollRectangle()
   */
  @Override
  public Rectangle getScrollRectangle() {
    Rectangle rectangle = null;
    try {
      java.awt.Rectangle modelToView = urlTextField.modelToView(urlTextField.getCaretPosition());
      rectangle = new Rectangle(modelToView.x, modelToView.y, modelToView.width, modelToView.height);
    } catch (BadLocationException e) {
      // Shouldn't happen.
      logger.error(e, e);
    }
    
    return rectangle;
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#requestFocus()
   */
  @Override
  public void requestFocus() {
    urlTextField.requestFocus();
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#getValue()
   */
  @Override
  public Object getValue() {
    String text = urlTextField.getText();
    try {
      URL clearedURL = utilAccess.removeUserCredentials(new URL(text));
      return clearedURL.toExternalForm();
    } catch (MalformedURLException e) {
      return text;
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#stopEditing()
   */
  @Override
  public void stopEditing() {
    stopEditing(false);
  }
  
  private void stopEditing(boolean onEnter) {
    String text = urlTextField.getText();
    try {
      URL clearedURL = utilAccess.removeUserCredentials(new URL(text));
      text = clearedURL.toExternalForm();
    } catch (MalformedURLException e) {
    }
    
    if (onEnter) {
      fireNextEditLocationRequested();
    } else {
      fireEditingStopped(new EditingEvent(text));
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#cancelEditing()
   */
  @Override
  public void cancelEditing() {
    fireEditingCanceled();
  }

  /**
   * Prepare UI components.
   * 
   * @param context The current context.
   */
  private void prepareComponents(AuthorInplaceContext context, boolean forEditing) {
    utilAccess = context.getAuthorAccess().getUtilAccess();
    String text = (String) context.getArguments().get(InplaceEditorArgumentKeys.INITIAL_VALUE);
    ro.sync.exml.view.graphics.Color color =
        (ro.sync.exml.view.graphics.Color) context.getArguments().get(InplaceEditorArgumentKeys.PROPERTY_COLOR);
    if (color != null) {
      urlTextField.setForeground(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
    } else {
      urlTextField.setForeground(defaultForeground);
    }
    
    if (!forEditing && text == null) {
      urlTextField.setForeground(Color.GRAY);
      text = (String) context.getArguments().get(InplaceEditorArgumentKeys.DEFAULT_VALUE);
    }
    
    if (text == null) {
      text = "";
    }
    
    // EXM-25327 Do not insert the username and password in the document 
    try {
      URL clearedURL = utilAccess.removeUserCredentials(new URL(text));
      urlTextField.setText(clearedURL.toExternalForm());
    } catch (MalformedURLException e) {
      urlTextField.setText(text);
    }

    // We don't want an UNDO to reset the initial text.
    UndoManager undoManager = (UndoManager) urlTextField.getDocument().getProperty(UNDO_MANAGER_PROPERTY);
    if (undoManager != null) {
      undoManager.discardAllEdits();
    }
    
    Font font = (Font) context.getArguments().get(InplaceEditorArgumentKeys.FONT);
    if (font != null) {
      java.awt.Font currentFont = new java.awt.Font(font.getName(), font.getStyle(), font.getSize());
      urlTextField.setFont(currentFont);
      browseBtn.setFont(currentFont);
    } else {
      urlTextField.setFont(defaultFont);
      browseBtn.setFont(defaultFont);
    }
    
    InplaceEditorUtil.relayout(urlTextField, context);
    
    Point relMousePos = context.getRelativeMouseLocation();
    boolean rollover = false;
    if (relMousePos != null) {
      RendererLayoutInfo renderInfo = computeRenderingInfo(context);
      urlChooserPanel.setSize(renderInfo.getSize().width, renderInfo.getSize().height);
      // Unless we do the layout we can't determine the component under the mouse. 
      urlChooserPanel.doLayout();

      Component componentAt = urlChooserPanel.getComponentAt(relMousePos.x, relMousePos.y);
      rollover = componentAt == browseBtn;
    }
    
    browseBtn.getModel().setRollover(rollover);
  }
  
  /**
   * Creates the text field used to display the selected URL and installs the
   * UNDO support on it.
   * 
   * @return The text field used to display the selected URL.
   */
  protected JTextField createTextField() {
    JTextField textField = new JTextField();
    
    final UndoManager undoManager = new UndoManager();
    Document doc = textField.getDocument();
    doc.putProperty(UNDO_MANAGER_PROPERTY, undoManager);

    doc.addUndoableEditListener(new UndoableEditListener() {
        @Override
        public void undoableEditHappened(UndoableEditEvent evt) {
            undoManager.addEdit(evt.getEdit());
        }
    });

    textField.getActionMap().put("Undo",
        new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException e) {
                }
            }
       });

    int modifier =
        System.getProperty("os.name").toUpperCase().startsWith("MAC OS") ? KeyEvent.META_DOWN_MASK
                                                                         : KeyEvent.CTRL_DOWN_MASK;
    KeyStroke undoKS = KeyStroke.getKeyStroke(KeyEvent.VK_Z, modifier, false);
    KeyStroke redoKS = KeyStroke.getKeyStroke(KeyEvent.VK_Y, modifier, false);
    textField.getInputMap().put(undoKS, "Undo");

    textField.getActionMap().put("Redo",
        new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                } catch (CannotRedoException e) {
                }
            }
        });

    textField.getInputMap().put(redoKS, "Redo");
    
    return textField;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getCursorType(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext, int, int)
   */
  @Override
  public CursorType getCursorType(AuthorInplaceContext context, int x, int y) {
    return CursorType.CURSOR_NORMAL;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceRenderer#getCursorType(int, int)
   */
  @Override
  public CursorType getCursorType(int x, int y) {
    return null;
  }
}