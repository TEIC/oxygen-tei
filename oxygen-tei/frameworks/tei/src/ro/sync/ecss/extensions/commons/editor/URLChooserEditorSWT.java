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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.access.AuthorUtilAccess;
import ro.sync.ecss.extensions.api.editor.AbstractInplaceEditor;
import ro.sync.ecss.extensions.api.editor.AuthorInplaceContext;
import ro.sync.ecss.extensions.api.editor.EditingEvent;
import ro.sync.ecss.extensions.api.editor.InplaceEditorArgumentKeys;
import ro.sync.exml.view.graphics.Color;
import ro.sync.exml.view.graphics.Font;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;

/**
 * URL Chooser in-place editor on Eclipse.
 * 
 * @author costi
 * @author adriana
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class URLChooserEditorSWT extends AbstractInplaceEditor implements ITextOperationTarget {

  /**
   * URL chooser composite.
   */
  private Composite urlChooserComposite;
  /**
   * Browse URL button.
   */
  private Button browseButton;
  
  /**
   * Text viewer.
   */
  private SourceViewer textViewer;
  
  /**
   * The image on the browse button.
   */
  private Image buttonImage;
  
  /**
   * <code>true</code> if the user is during browsing. 
   */
  private boolean isBrowsing = false;
  /**
   * Author Util.
   */
  private AuthorUtilAccess utilAccess;
  /**
   * The imposed font.
   */
  private org.eclipse.swt.graphics.Font swtFont;
  /**
   * Foreground color.
   */
  private org.eclipse.swt.graphics.Color foregroundColor;
  
  /**
   * Constructor.
   */
  public URLChooserEditorSWT() {
  }
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "A sample implementation that provides a browse button associated with a text field.";
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#getEditorComponent(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext, ro.sync.exml.view.graphics.Rectangle, ro.sync.exml.view.graphics.Point)
   */
  @Override
  public Object getEditorComponent(final AuthorInplaceContext context, Rectangle allocation,
      Point mouseInvocationLocation) {
    prepareComponents(context);
    
    // Get the editor location to make the selected URL relative.
    final AuthorAccess authorAccess = context.getAuthorAccess();
    final URL editorLocation = authorAccess.getEditorAccess().getEditorLocation();
    
    // Add selection listener
    browseButton.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event e) {
        // Assign the selected value to the text viewer.
        isBrowsing = true;
        try {
          URL chooseURL = context.getAuthorAccess().getWorkspaceAccess().chooseURL("Choose URL", null, null);
          if (chooseURL != null) {
            String relativeURL = context.getAuthorAccess().getUtilAccess().makeRelative(editorLocation, chooseURL);
            
            // EXM-25327 Do not insert the username and password in the document
            utilAccess = authorAccess.getUtilAccess();
            try {
              URL clearedURL = utilAccess.removeUserCredentials(new URL(relativeURL));
              textViewer.getTextWidget().setText(clearedURL.toExternalForm());
            } catch (MalformedURLException e1) {
              textViewer.getTextWidget().setText(relativeURL);
            }
            
            // EXM-25433 Commit to document.
            fireCommitValue(new EditingEvent((String) getValue()));
          }
        } finally {
          isBrowsing = false;
        }
      }
    });
    
    // Add traverse listener
    browseButton.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent e) {
        if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
          fireNextEditLocationRequested();
          e.doit = false;
        }
        
      }
    });
    
    textViewer.getTextWidget().addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent e) {
        if (e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
          firePreviousEditLocationRequested();
          e.doit = false;
        }
      }
    });
    
    textViewer.getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {
      @Override
      public void verifyKey(VerifyEvent event) {
        switch (event.keyCode) {
          case SWT.TAB:
            // TAB takes us to the next edit position so we don't want it in the editor.
            event.doit = false;
            break;
          case SWT.CR:
          case SWT.KEYPAD_CR:
          case SWT.LF:
            if ((event.stateMask & SWT.CONTROL) == 0) {
              // Stop editing.
              event.doit = false;
              stopEditing(true);
            }
        }
      }
    });
    
    return urlChooserComposite;
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#getScrollRectangle()
   */
  @Override
  public Rectangle getScrollRectangle() {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#requestFocus()
   */
  @Override
  public void requestFocus() {
    textViewer.getTextWidget().setFocus();
  }

  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#getValue()
   */
  @Override
  public Object getValue() {
    String text = textViewer.getTextWidget().getText();
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
  
  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#commitValue()
   */
  @Override
  public void commitValue() {
    fireCommitValue(new EditingEvent((String) getValue()));    
  }

  /**
   * @param onEnter <code>true</code> if editing stopped was triggered on Enter key pressed.
   */
  private void stopEditing(boolean onEnter) {
    String text = (String) getValue();
    if (onEnter) {
      fireNextEditLocationRequested();
    } else {
      fireEditingStopped(new EditingEvent(text));
    }
    dispose();    
  }
  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#cancelEditing()
   */
  @Override
  public void cancelEditing() {
    fireEditingCanceled();
    dispose();
  }

  /**
   * Initialize components.
   * 
   * @param context The current context.
   */
  private void prepareComponents(AuthorInplaceContext context) {
    dispose();
    // Create the components.
    urlChooserComposite = new Composite((Composite) context.getParentHost(), SWT.NONE);
    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginWidth = gridLayout.marginHeight = 0;
    urlChooserComposite.setLayout(gridLayout);
    textViewer = new SourceViewer(urlChooserComposite, null, SWT.FILL | SWT.SINGLE | SWT.BORDER);
    textViewer.configure(new TextSourceViewerConfiguration(EditorsPlugin.getDefault().getPreferenceStore()));
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd.horizontalIndent = 0;
    gd.verticalIndent = 0;
    textViewer.getTextWidget().setLayoutData(gd);
    browseButton = new Button(urlChooserComposite, SWT.PUSH);
    gd = new GridData(SWT.NONE, SWT.FILL, false, true);
    
    // Add required listeners.
    textViewer.getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {
      @Override
      public void verifyKey(VerifyEvent e) {
        switch (e.keyCode) {          
          case SWT.ESC:
            // On ESC cancel the editing and do not commit the value.
            e.doit = false;
            cancelEditing();
            break;
        }
      }
    });
    
    // Add focus listener
    FocusListener focusListener = new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        Display.getDefault().asyncExec(new Runnable() {
          @Override
          public void run() {
            if (!urlChooserComposite.isDisposed()
                && !browseButton.isFocusControl()
                && !textViewer.getTextWidget().isFocusControl()
                && !isBrowsing) {
              // Just make sure we are in sync with the document.
              fireCommitValue(new EditingEvent((String) getValue()));
            }
          }
        });
      }
      @Override
      public void focusGained(FocusEvent e) {
      }
    };
    textViewer.getTextWidget().addFocusListener(focusListener);
    browseButton.addFocusListener(focusListener);
    
    // Add key listener
    browseButton.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) {
      }
      
      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.keyCode) {          
          case SWT.ESC:
            // On ESC cancel the editing and do not commit the value.
            e.doit = false;
            cancelEditing();
            break;
        }
      }
    });
    
    textViewer.setDocument(new Document(""));
    IDocumentListener docChangedListener = new IDocumentListener() {
      @Override
      public void documentAboutToBeChanged(DocumentEvent event) {
        // Not of interest.
      }
      @Override
      public void documentChanged(DocumentEvent event) {
        fireEditingOccured();
      }    
    };
    textViewer.getDocument().addDocumentListener(docChangedListener);
    
    Font font = (Font) context.getArguments().get(InplaceEditorArgumentKeys.FONT);
    if (font != null) {
      swtFont = new org.eclipse.swt.graphics.Font(
          Display.getDefault(), new FontData(font.getName(), font.getSize(), font.getStyle()));
    }
    
    if (swtFont != null) {
      textViewer.getTextWidget().setFont(swtFont);
    } else {
      textViewer.getTextWidget().setFont(JFaceResources.getDialogFont());
    }
    
    Color color = (Color) context.getArguments().get(InplaceEditorArgumentKeys.PROPERTY_COLOR);
    if (color != null) {
      foregroundColor = new org.eclipse.swt.graphics.Color(
          Display.getDefault(), color.getRed(), color.getGreen(), color.getBlue());
      textViewer.getTextWidget().setForeground(foregroundColor);
    }
    
    // Assign an open icon on the browse button.
    InputStream resourceAsStream = URLChooserEditorSWT.class.getResourceAsStream("/images/Open16.gif");
    ImageData imageResource = null;
    if (resourceAsStream != null) {
      imageResource = new ImageData(resourceAsStream);
      // EXM-32124 - Make sure the stream is closed after using it
      try {
        resourceAsStream.close();
      } catch (IOException e1) {
        //Ignore
      }
    }
    // Create button image
    ImageDescriptor icon = null;
    if (imageResource != null) {
      icon = ImageDescriptor.createFromImageData(imageResource);
    }
    
    buttonImage = null;
    if (icon != null) {
      buttonImage  = icon.createImage();
    }
    
    if (buttonImage != null) {
      browseButton.setImage(buttonImage);
    }
    browseButton.setLayoutData(gd);
    
    setInitialValue(context);
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        // Didn't work without an invoke later.
        if (textViewer.getTextWidget() != null) {
          textViewer.getTextWidget().showSelection();
        }
      }
    });
    
    textViewer.getUndoManager().reset();
  }
  
  /**
   * Sets the initial value inside the chooser.
   * 
   * @param context Current context.
   */
  private void setInitialValue(AuthorInplaceContext context) {
    String text = (String) context.getArguments().get(InplaceEditorArgumentKeys.INITIAL_VALUE);
    if (text == null) {
      text = (String) context.getArguments().get(InplaceEditorArgumentKeys.DEFAULT_VALUE);
    }

    if (text == null) {
      text = "";
    }
    
    // EXM-25327 Do not insert the username and password in the document
    utilAccess = context.getAuthorAccess().getUtilAccess();
    try {
      URL clearedURL = utilAccess.removeUserCredentials(new URL(text));
      text = clearedURL.toExternalForm();
      textViewer.getTextWidget().setText(text);
    } catch (MalformedURLException e1) {
      textViewer.getTextWidget().setText(text);
    }
    
    textViewer.getTextWidget().setCaretOffset(text.length());
  }

  /**
   * Dispose of the editor.
   */
  private void dispose() {
    if (swtFont != null) {
      swtFont.dispose();
    }
    
    if (urlChooserComposite != null) {
      urlChooserComposite.dispose();
    }
    
    if (buttonImage != null) {
      buttonImage.dispose();
    }
    
    if (foregroundColor != null) {
      foregroundColor.dispose();
    }
  }
  
  /**
   * @see org.eclipse.jface.text.ITextOperationTarget#canDoOperation(int)
   */
  @Override
  public boolean canDoOperation(int operation) {
    return textViewer != null ? textViewer.canDoOperation(operation) : false;
  }
  /**
   * @see org.eclipse.jface.text.ITextOperationTarget#doOperation(int)
   */
  @Override
  public void doOperation(int operation) {
    textViewer.doOperation(operation);
  }
  /**
   * @see ro.sync.ecss.extensions.api.editor.InplaceEditor#refresh(ro.sync.ecss.extensions.api.editor.AuthorInplaceContext)
   */
  @Override
  public void refresh(AuthorInplaceContext context) {
    setInitialValue(context);
  }
}
