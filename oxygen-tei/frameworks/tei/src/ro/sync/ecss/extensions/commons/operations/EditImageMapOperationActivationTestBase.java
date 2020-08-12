/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2007 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.operations;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import junit.extensions.jfcunit.RobotTestHelper;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.keyboard.JFCKeyStroke;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.options.OptionTags;
import ro.sync.exml.options.Options;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.exml.workspace.api.editor.page.author.DisplayModeConstants;

/**
 * Base class for "Edit image map" activation tests.
 * 
 * @author mircea
 */
public abstract class EditImageMapOperationActivationTestBase extends EditorAuthorExtensionTestBase {
  
  private static class MyRobotTestHelper extends RobotTestHelper {
    /**
     * @throws AWTException
     */
    public MyRobotTestHelper() throws AWTException {
      super();
    }

    @Override
    public void keyPressed(Component ultimate, JFCKeyStroke stroke) {
      super.keyPressed(ultimate, stroke);
    }
    @Override
    public void keyReleased(Component ultimate, JFCKeyStroke stroke) {
      super.keyReleased(ultimate, stroke);
    }
  }
  
  /**
   * Constructor. 
   */
  public EditImageMapOperationActivationTestBase() {
    activateScreenshotsOnTestFail();
  }
  
  /**
   * Robot helper.
   */
  protected MyRobotTestHelper robotAWTHelper;
  
  /**
   * Initial tags display mode.
   */
  private int tagsDisplayMode;
  
  /**
   * Normalize format.
   */
  private boolean normalizeFormat;

  /**
   * Setup.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    try {
      robotAWTHelper = new MyRobotTestHelper();
    } catch (AWTException e) {
      e.printStackTrace();
    }
    normalizeFormat = Options.getInstance().getBooleanProperty(OptionTags.AUTHOR_SCHEMA_AWARE_NORMALIZE_FORMAT);
    Options.getInstance().setBooleanProperty(OptionTags.AUTHOR_SCHEMA_AWARE_NORMALIZE_FORMAT, false);
    tagsDisplayMode = Options.getInstance().getIntegerProperty(OptionTags.TAGS_DISPLAY_MODE);
    Options.getInstance().setIntegerProperty(OptionTags.TAGS_DISPLAY_MODE, DisplayModeConstants.DISPLAY_MODE_NO_TAGS);
  }
  
  /**
   * Tear down.
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Options.getInstance().setBooleanProperty(OptionTags.AUTHOR_SCHEMA_AWARE_NORMALIZE_FORMAT, normalizeFormat);
    Options.getInstance().setIntegerProperty(OptionTags.TAGS_DISPLAY_MODE, tagsDisplayMode);
  }
  
  /**
   * Show the context popup menu and give back a specific menu item.
   * 
   * @param authorPage    The author page.
   * @param toFind        The text to find.
   * @param toFindOffset  The additional offset after the text's length.
   * @param actionName    The name of the action to find.
   * @return  The action menu item, if found.
   * @throws Exception  If something goes wrong.
   */
  protected JMenuItem showPopupAndGiveMenuItem(
      AuthorEditorPage authorPage,
      String toFind,
      int toFindOffset,
      String actionName) throws Exception {
    // Move the caret in the image map.
    moveCaretRelativeTo(toFind, toFind.length() + toFindOffset);
    
    Rectangle caret = authorPage.getViewport().getCaretShape();
    
    // Move the selected text at the beginning of the text.
    robotAWTHelper.enterClickAndLeave(
        new MouseEventData(
            this,
            authorPage.getSwingComponent(),
            1,
            MouseEvent.BUTTON3_MASK,
            true, 
            100,
            new Point(caret.x - 5, caret.y + 5)));
    // Flush AWT.
    flushAWTBetter();
    // Find the menu item.
    return findPopupMenuItem(actionName);
  }
}