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
package ro.sync.ecss.extensions.commons.sort.test;

import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JDialog;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.ue.AuthorDocumentControllerImpl;
import ro.sync.exml.editor.EditorPage;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.options.OptionConstants;
import ro.sync.exml.options.OptionTags;
import ro.sync.exml.options.Options;
import ro.sync.util.URLUtil;

/**
 * Test class for Docbook table sort operation.
 */
public class XHTMLSortTableOperationTest extends EditorAuthorExtensionTestBase {

  /**
   * <p><b>Description:</b> Test the Sort operation for HTML tables.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testHTMLSortTable() throws Exception {
    String originalContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html>\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>A</th>\n" + 
    		"                    <th>B</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Jul 15, 2009 2:59:52 PM</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>22/05/2001</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Wednesday, July 10, 2013 2:59:52 PM EEST</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>A</th>\n" + 
    		"                    <th>C</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>a</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td rowspan=\"2\">tn</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>";
    open(URLUtil.correct(new File("test/EXM-12505/testSortHTMLTable.xhtml")), true);

    // Move the caret after title
    moveCaretRelativeTo("C", 0);
    
    flushAWTBetter();

    invokeActionForID("sort");
    Thread.sleep(500);

    assertEquals("The sort operation couldn't be performed.\n" + 
    		"The 'Sort' operation is unavailable for tables with multiple rowspan cells.", lastErrorMessage);
    verifyDocument(originalContent, true);
    
    // Move the caret after title
    moveCaretRelativeTo("A", 0);

    flushAWTBetter();

    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog("Sort");
    JComboBox typeCombo = findComponent(dialog, JComboBox.class, 1);
    typeCombo.setSelectedIndex(2);
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html>\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>A</th>\n" + 
    		"                    <th>B</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Wednesday, July 10, 2013 2:59:52 PM EEST</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Jul 15, 2009 2:59:52 PM</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>22/05/2001</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>A</th>\n" + 
    		"                    <th>C</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>a</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td rowspan=\"2\">tn</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>", true);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation for HTML tables with change tracking on.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testHTMLSortTableWithChangeTrackingOn() throws Exception {
    // Save modified options state
    int initialSaveTrackChangesStateOpt = Options.getInstance().getIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE);
    // Set custom options
    Options.getInstance().setIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE, OptionConstants.TRACK_CHANGES_INITIAL_STATE_ALWAYS_ON);
    
    try {
    open(URLUtil.correct(new File("test/EXM-12505/testSortHTMLTable.xhtml")), true);
    final AuthorDocumentControllerImpl ctrl = vViewport.getController();
    ctrl.getAuthorMarksManager().setCurrentAuthorName("userName");
    ctrl.getAuthorMarksManager().setFixedTimeStampForTCs("1234567890");
  
    moveCaretRelativeTo("A", 0);
  
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog("Sort");
    JComboBox typeCombo = findComponent(dialog, JComboBox.class, 1);
    typeCombo.setSelectedIndex(2);
    sleep(2000);
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html>\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>A</th>\n" + 
    		"                    <th>B</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <?oxy_insert_start author=\"userName\" timestamp=\"1234567890\"?>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Wednesday, July 10, 2013 2:59:52 PM EEST</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Jul 15, 2009 2:59:52 PM</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>22/05/2001</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <?oxy_insert_end?>\n" + 
    		"                <?oxy_delete author=\"userName\" timestamp=\"1234567890\" content=\"&lt;tr&gt;&lt;td&gt;Jul 15, 2009 2:59:52 PM&lt;/td&gt;&lt;td/&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;22/05/2001&lt;/td&gt;&lt;td/&gt;&lt;/tr&gt;&lt;tr&gt;&lt;td&gt;Wednesday, July 10, 2013 2:59:52 PM EEST&lt;/td&gt;&lt;td/&gt;&lt;/tr&gt;\"?>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>A</th>\n" + 
    		"                    <th>C</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>a</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td rowspan=\"2\">tn</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>", true);
    } finally {
      Options.getInstance().setIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE, initialSaveTrackChangesStateOpt);
    }
  }

  /**
   * <p><b>Description:</b> Test the Sort operation for HTML tables containing change tracking.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testHTMLSortTableWithChangeTrackingModification() throws Exception {
    // Save modified options state
    int initialSaveTrackChangesStateOpt = Options.getInstance().getIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE);
    // Set custom options
    Options.getInstance().setIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE, OptionConstants.TRACK_CHANGES_INITIAL_STATE_ALWAYS_OFF);
    
    try {
    open(URLUtil.correct(new File("test/EXM-12505/testSortHTMLTableWithChangeTracking.xhtml")), true);
  
    moveCaretRelativeTo("A", 0);
  
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog("Sort");
    JComboBox typeCombo = findComponent(dialog, JComboBox.class, 1);
    typeCombo.setSelectedIndex(2);
    sleep(2000);
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html>\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>A</th>\n" + 
    		"                    <th>B</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Wednesday, July 10, 2013 2:59:52 PM EEST</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Jul 15, 2009 2:59:52 PM</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td><?oxy_insert_start author=\"adriana_sbircea\" timestamp=\"20130712T125949+0300\"?>22/05/2001<?oxy_insert_end?></td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>C</th>\n" + 
    		"                    <th>D</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>a</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td><?oxy_delete author=\"adriana_sbircea\" timestamp=\"20130712T143157+0300\" content=\"c\"?>0</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td><?oxy_delete author=\"adriana_sbircea\" timestamp=\"20130712T143157+0300\" content=\"b\"?>1</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>\n" + 
    		"<?oxy_options track_changes=\"on\"?>", true);
    
    
    moveCaretRelativeTo("C", 0);
    
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog("Sort");
    typeCombo = findComponent(dialog, JComboBox.class, 1);
    typeCombo.setSelectedIndex(0);
    sleep(2000);
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();
    
    sleep(3000);
    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html>\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>A</th>\n" + 
    		"                    <th>B</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Wednesday, July 10, 2013 2:59:52 PM EEST</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>Jul 15, 2009 2:59:52 PM</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td><?oxy_insert_start author=\"adriana_sbircea\" timestamp=\"20130712T125949+0300\"?>22/05/2001<?oxy_insert_end?></td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <col width=\"50%\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>C</th>\n" + 
    		"                    <th>D</th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>a</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td><?oxy_delete author=\"adriana_sbircea\" timestamp=\"20130712T143157+0300\" content=\"b\"?>1</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td><?oxy_delete author=\"adriana_sbircea\" timestamp=\"20130712T143157+0300\" content=\"c\"?>0</td>\n" + 
    		"                    <td></td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>\n" + 
    		"<?oxy_options track_changes=\"on\"?>", true);
    
    } finally {
      Options.getInstance().setIntegerProperty(OptionTags.TRACK_CHANGES_INITIAL_STATE, initialSaveTrackChangesStateOpt);
    }
  }
  
  /**
   * <p><b>Description:</b> Sort closest parent</p>
   * <p><b>Bug ID:</b> EXM-27906</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void testSortClosestParentXHTML_EXM_27906() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27906/sortClosestParentXHTML.xhtml")), true);

    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    // Sort the inner table
    moveCaretRelativeTo("inner table1", 0);
    
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>\n" + 
    		"                        <ul>\n" + 
    		"                            <li> head list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul>\n" + 
    		"                    </th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tfoot>\n" + 
    		"                <tr>\n" + 
    		"                    <td><ul>\n" + 
    		"                            <li>foot list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul></td>\n" + 
    		"                </tr>\n" + 
    		"            </tfoot>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td><p>top table1</p><ol>\n" + 
    		"                            <li> item1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ol></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table2</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table3</td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>\n" + 
    		"", true);
    
    // Sort the inner list
    moveCaretRelativeTo("item1", 0);
    
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>\n" + 
    		"                        <ul>\n" + 
    		"                            <li> head list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul>\n" + 
    		"                    </th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tfoot>\n" + 
    		"                <tr>\n" + 
    		"                    <td><ul>\n" + 
    		"                            <li>foot list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul></td>\n" + 
    		"                </tr>\n" + 
    		"            </tfoot>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td><p>top table1</p><ol>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li> item1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ol></td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table2</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table3</td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>\n" + 
    		"", true);
    
    // Sort the big table
    moveCaretRelativeTo("top table1", 0);
    
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>\n" + 
    		"                        <ul>\n" + 
    		"                            <li> head list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul>\n" + 
    		"                    </th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tfoot>\n" + 
    		"                <tr>\n" + 
    		"                    <td><ul>\n" + 
    		"                            <li>foot list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul></td>\n" + 
    		"                </tr>\n" + 
    		"            </tfoot>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table3</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table2</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>\n" + 
    		"                        <p>top table1</p>\n" + 
    		"                        <ol>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li> item1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ol>\n" + 
    		"                    </td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>\n" + 
    		"", true);
    
    // Sort the table from header
    moveCaretRelativeTo("head table1", 0);
    
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>\n" + 
    		"                        <ul>\n" + 
    		"                            <li> head list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul>\n" + 
    		"                    </th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tfoot>\n" + 
    		"                <tr>\n" + 
    		"                    <td><ul>\n" + 
    		"                            <li>foot list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul></td>\n" + 
    		"                </tr>\n" + 
    		"            </tfoot>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table3</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table2</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>\n" + 
    		"                        <p>top table1</p>\n" + 
    		"                        <ol>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li> item1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ol>\n" + 
    		"                    </td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>\n" + 
    		"", true);
    
    // Sort the list from header
    moveCaretRelativeTo("head list1", 0);
    
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>\n" + 
    		"                        <ul>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li> head list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ul>\n" + 
    		"                    </th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tfoot>\n" + 
    		"                <tr>\n" + 
    		"                    <td><ul>\n" + 
    		"                            <li>foot list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul></td>\n" + 
    		"                </tr>\n" + 
    		"            </tfoot>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table3</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table2</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>\n" + 
    		"                        <p>top table1</p>\n" + 
    		"                        <ol>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li> item1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ol>\n" + 
    		"                    </td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>\n" + 
    		"", true);
    
    // Sort the table from footer
    moveCaretRelativeTo("foot table1", 0);
    
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>\n" + 
    		"                        <ul>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li> head list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ul>\n" + 
    		"                    </th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tfoot>\n" + 
    		"                <tr>\n" + 
    		"                    <td><ul>\n" + 
    		"                            <li>foot list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list3</p>\n" + 
    		"                            </li>\n" + 
    		"                        </ul></td>\n" + 
    		"                </tr>\n" + 
    		"            </tfoot>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table3</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table2</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>\n" + 
    		"                        <p>top table1</p>\n" + 
    		"                        <ol>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li> item1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ol>\n" + 
    		"                    </td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>\n" + 
    		"", true);
    
    // Sort the list from footer.
    moveCaretRelativeTo("foot list1", 0);
    
    flushAWTBetter();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");

    verifyDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" + 
    		"                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" + 
    		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
    		"    <head>\n" + 
    		"        <title></title>\n" + 
    		"    </head>\n" + 
    		"    <body>\n" + 
    		"        <table frame=\"void\">\n" + 
    		"            <caption></caption>\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <col width=\"1.0*\" />\n" + 
    		"            <thead>\n" + 
    		"                <tr>\n" + 
    		"                    <th>\n" + 
    		"                        <ul>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>head list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li> head list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>head table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ul>\n" + 
    		"                    </th>\n" + 
    		"                </tr>\n" + 
    		"            </thead>\n" + 
    		"            <tfoot>\n" + 
    		"                <tr>\n" + 
    		"                    <td><ul>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>foot list2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>foot list1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>foot table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ul></td>\n" + 
    		"                </tr>\n" + 
    		"            </tfoot>\n" + 
    		"            <tbody>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table3</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>top table2</td>\n" + 
    		"                </tr>\n" + 
    		"                <tr>\n" + 
    		"                    <td>\n" + 
    		"                        <p>top table1</p>\n" + 
    		"                        <ol>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item3</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li>\n" + 
    		"                                <p>item2</p>\n" + 
    		"                            </li>\n" + 
    		"                            <li> item1<table frame=\"void\">\n" + 
    		"                                    <caption></caption>\n" + 
    		"                                    <col width=\"1.0*\" />\n" + 
    		"                                    <thead>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <th></th>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </thead>\n" + 
    		"                                    <tbody>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table3</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table2</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                        <tr>\n" + 
    		"                                            <td>inner table1</td>\n" + 
    		"                                        </tr>\n" + 
    		"                                    </tbody>\n" + 
    		"                                </table>\n" + 
    		"                            </li>\n" + 
    		"                        </ol>\n" + 
    		"                    </td>\n" + 
    		"                </tr>\n" + 
    		"            </tbody>\n" + 
    		"        </table>\n" + 
    		"    </body>\n" + 
    		"</html>\n" + 
    		"", true);
  }
}