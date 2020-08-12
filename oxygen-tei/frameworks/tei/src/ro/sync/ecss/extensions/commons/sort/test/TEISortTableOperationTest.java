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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.exml.editor.EditorPage;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.util.URLUtil;

/**
 * Test class for TEI table sort operation.
 */
public class TEISortTableOperationTest extends EditorAuthorExtensionTestBase {
  /**
   * <p><b>Description:</b> Test the Sort operation for TEI tables.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testTEISortTable() throws Exception {
    String originalContent = "<?xml version=\"1.0\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"   <teiHeader>\n" + 
    		"      <fileDesc>\n" + 
    		"         <titleStmt/>\n" + 
    		"      </fileDesc>\n" + 
    		"   </teiHeader>\n" + 
    		"   <text>\n" + 
    		"      <front>\n" + 
    		"         <div type=\"preface\">\n" + 
    		"            <head>PREFACE</head>\n" + 
    		"            <p>\n" + 
    		"               I have endeavoured in this\n" + 
    		"               <emph>Ghostly</emph>\n" + 
    		"               little book, to raise the\n" + 
    		"               <emph>Ghost</emph>\n" + 
    		"               of an xml:idea, which shall not put my readers out of humour with themselves, with each other, with\n" + 
    		"               the season, or with me. May it haunt their houses pleasantly, and no one wish to lay it.\n" + 
    		"            </p>\n" + 
    		"            <signed>\n" + 
    		"               Their faithful Friend and Servant, C.D.\n" + 
    		"               <date>December, 1843.</date>\n" + 
    		"            </signed>\n" + 
    		"         </div>\n" + 
    		"      </front>\n" + 
    		"      <body>\n" + 
    		"         <div>\n" + 
    		"            <head>Tables</head>\n" + 
    		"            <p>Tables may have cells that span multiple columns and rows.</p>\n" + 
    		"            <table rows=\"5\" cols=\"5\">\n" + 
    		"               <head>\n" + 
    		"                  <hi rend=\"bold\">TEI Span Sample</hi>\n" + 
    		"               </head>\n" + 
    		"               <row role=\"label\">\n" + 
    		"                  <cell cols=\"2\">Spans\n" + 
    		"                     <hi rend=\"bold\">Horizontally</hi>\n" + 
    		"                  </cell>\n" + 
    		"                  <cell>Header 3</cell>\n" + 
    		"                  <cell cols=\"2\">Spans\n" + 
    		"                     <hi rend=\"bold\">Horizontally</hi>\n" + 
    		"                  </cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell rows=\"4\">Spans\n" + 
    		"                     <hi rend=\"bold\">Vertically</hi>\n" + 
    		"                  </cell>\n" + 
    		"                  <cell>a</cell>\n" + 
    		"                  <cell>b</cell>\n" + 
    		"                  <cell>c</cell>\n" + 
    		"                  <cell>d</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>e</cell>\n" + 
    		"                  <cell cols=\"2\" rows=\"2\">Spans\n" + 
    		"                     <hi rend=\"bold\">both</hi>\n" + 
    		"                  </cell>\n" + 
    		"                  <cell>f</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>gaga</cell>\n" + 
    		"                  <cell>g</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>iuiu</cell>\n" + 
    		"                  <cell>j</cell>\n" + 
    		"                  <cell cols=\"2\">Spans\n" + 
    		"                     <hi rend=\"bold\">Horizontally</hi>\n" + 
    		"                  </cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>k</cell>\n" + 
    		"                  <cell>l</cell>\n" + 
    		"                  <cell>m</cell>\n" + 
    		"                  <cell>n</cell>\n" + 
    		"                  <cell>o</cell>\n" + 
    		"               </row>\n" + 
    		"            </table>\n" + 
    		"            <table rows=\"3\" cols=\"2\">\n" + 
    		"               <head/>\n" + 
    		"               <row role=\"label\">\n" + 
    		"                  <cell>A</cell>\n" + 
    		"                  <cell>BAAAAAA</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>424</cell>\n" + 
    		"                  <cell/>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>613</cell>\n" + 
    		"                  <cell/>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>104</cell>\n" + 
    		"                  <cell/>\n" + 
    		"               </row>\n" + 
    		"            </table>\n" + 
    		"         </div>\n" + 
    		"      </body>\n" + 
    		"   </text>\n" + 
    		"</TEI>";
    open(URLUtil.correct(new File("test/EXM-12505/tei.xml")), true);

    // Move the caret after title
    moveCaretRelativeTo("Spans", 0);
    
    flushAWTBetter();

    invokeActionForID("sort");
    Thread.sleep(500);

    assertEquals("The sort operation couldn't be performed.\n" + 
    		"The 'Sort' operation is unavailable for tables with multiple rowspan cells.", lastErrorMessage);

    verifyDocument(originalContent, true);
    
    // Move the caret before "gaga".
    moveCaretRelativeTo("gaga", 0);
    flushAWTBetter();
    
    moveCaretRelativeTo("iuiu", 0, true);
    flushAWTBetter();

    invokeActionForID("sort");
    Thread.sleep(500);

    assertEquals("The sort operation couldn't be performed.\n" + 
        "The 'Sort' operation is unavailable for tables with multiple rowspan cells.", lastErrorMessage);

    verifyDocument(originalContent, true);
    
    // Move the caret after title
    moveCaretRelativeTo("BAAAAAA", 0);

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
    typeCombo.setSelectedIndex(1);
    JComboBox combo = findComponent(dialog, JComboBox.class, 2);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");
    flushAWTBetter();

    verifyDocument("<?xml version=\"1.0\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"   <teiHeader>\n" + 
    		"      <fileDesc>\n" + 
    		"         <titleStmt/>\n" + 
    		"      </fileDesc>\n" + 
    		"   </teiHeader>\n" + 
    		"   <text>\n" + 
    		"      <front>\n" + 
    		"         <div type=\"preface\">\n" + 
    		"            <head>PREFACE</head>\n" + 
    		"            <p>\n" + 
    		"               I have endeavoured in this\n" + 
    		"               <emph>Ghostly</emph>\n" + 
    		"               little book, to raise the\n" + 
    		"               <emph>Ghost</emph>\n" + 
    		"               of an xml:idea, which shall not put my readers out of humour with themselves, with each other, with\n" + 
    		"               the season, or with me. May it haunt their houses pleasantly, and no one wish to lay it.\n" + 
    		"            </p>\n" + 
    		"            <signed>\n" + 
    		"               Their faithful Friend and Servant, C.D.\n" + 
    		"               <date>December, 1843.</date>\n" + 
    		"            </signed>\n" + 
    		"         </div>\n" + 
    		"      </front>\n" + 
    		"      <body>\n" + 
    		"         <div>\n" + 
    		"            <head>Tables</head>\n" + 
    		"            <p>Tables may have cells that span multiple columns and rows.</p>\n" + 
    		"            <table rows=\"5\" cols=\"5\">\n" + 
    		"               <head>\n" + 
    		"                  <hi rend=\"bold\">TEI Span Sample</hi>\n" + 
    		"               </head>\n" + 
    		"               <row role=\"label\">\n" + 
    		"                  <cell cols=\"2\">Spans\n" + 
    		"                     <hi rend=\"bold\">Horizontally</hi>\n" + 
    		"                  </cell>\n" + 
    		"                  <cell>Header 3</cell>\n" + 
    		"                  <cell cols=\"2\">Spans\n" + 
    		"                     <hi rend=\"bold\">Horizontally</hi>\n" + 
    		"                  </cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell rows=\"4\">Spans\n" + 
    		"                     <hi rend=\"bold\">Vertically</hi>\n" + 
    		"                  </cell>\n" + 
    		"                  <cell>a</cell>\n" + 
    		"                  <cell>b</cell>\n" + 
    		"                  <cell>c</cell>\n" + 
    		"                  <cell>d</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>e</cell>\n" + 
    		"                  <cell cols=\"2\" rows=\"2\">Spans\n" + 
    		"                     <hi rend=\"bold\">both</hi>\n" + 
    		"                  </cell>\n" + 
    		"                  <cell>f</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>gaga</cell>\n" + 
    		"                  <cell>g</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>iuiu</cell>\n" + 
    		"                  <cell>j</cell>\n" + 
    		"                  <cell cols=\"2\">Spans\n" + 
    		"                     <hi rend=\"bold\">Horizontally</hi>\n" + 
    		"                  </cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>k</cell>\n" + 
    		"                  <cell>l</cell>\n" + 
    		"                  <cell>m</cell>\n" + 
    		"                  <cell>n</cell>\n" + 
    		"                  <cell>o</cell>\n" + 
    		"               </row>\n" + 
    		"            </table>\n" + 
    		"            <table rows=\"3\" cols=\"2\">\n" + 
    		"                    <head/>\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>A</cell>\n" + 
    		"                        <cell>BAAAAAA</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>613</cell>\n" + 
    		"                        <cell/>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>424</cell>\n" + 
    		"                        <cell/>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>104</cell>\n" + 
    		"                        <cell/>\n" + 
    		"                    </row>\n" + 
    		"                </table>\n" + 
    		"         </div>\n" + 
    		"      </body>\n" + 
    		"   </text>\n" + 
    		"</TEI>", true);
  }

  /**
   * <p><b>Description:</b> Test the Sort operation for TEI lists.</p>
   * <p><b>Bug ID:</b> EXM-12505</p>
   *
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testSortList() throws Exception {
    String originalContent = "<?xml version=\"1.0\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"   <teiHeader>\n" + 
    		"      <fileDesc>\n" + 
    		"         <titleStmt>\n" + 
    		"            <title>A TEI P5 Sample File</title>\n" + 
    		"         </titleStmt>\n" + 
    		"         <publicationStmt>\n" + 
    		"            <p>Published as a TEI sample in &lt;oXygen/></p>\n" + 
    		"         </publicationStmt>\n" + 
    		"         <sourceDesc>\n" + 
    		"            <p>Derived from a TEI exercise by Lou Burnard and Sebastian Rahtz. Further modified by Dan Caprioara</p>\n" + 
    		"         </sourceDesc>\n" + 
    		"      </fileDesc>\n" + 
    		"      <revisionDesc>\n" + 
    		"         <list>\n" + 
    		"            <item>\n" + 
    		"               <date>5 February 2002</date>first attempt</item>\n" + 
    		"            <item>\n" + 
    		"               <date>24 October 2007</date>modified by Dan</item>\n" + 
    		"         </list>\n" + 
    		"      </revisionDesc>\n" + 
    		"   </teiHeader>\n" + 
    		"   <text>\n" + 
    		"      <body>\n" + 
    		"         <div>\n" + 
    		"            <list type=\"gloss\">\n" + 
    		"               <label>Label1</label>\n" + 
    		"               <item>aaaaa</item>\n" + 
    		"               <label>Label2</label>\n" + 
    		"               <item>bbbbb</item>\n" + 
    		"            </list>\n" + 
    		"         </div>\n" + 
    		"      </body>\n" + 
    		"   </text>\n" + 
    		"</TEI>";
    open(URLUtil.correct(new File("test/EXM-12505/teiList.xml")), true);
  
    // Move the caret after title
    moveCaretRelativeTo("aaaaa", 0);
    
    flushAWTBetter();
  
    invokeActionForID("sort");
    Thread.sleep(500);
  
    assertEquals("The sort operation couldn't be performed.\n" + 
    		"The 'Sort' operation is unavailable for lists containing elements which are not 'item'.", lastErrorMessage);
  
    verifyDocument(originalContent, true);
  }
  
  /**
   * <p><b>Description:</b> Sort the closest parent. TEI P4</p>
   * <p><b>Bug ID:</b> EXM-27906</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void testSortClosestParentTEIP4_EXM_27906() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27906/sortClosestParentTEIP4.xml")), true);
    
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
    		"<!DOCTYPE TEI.2 PUBLIC \"-//TEI P4//DTD Main Document Type//EN\" \"http://www.tei-c.org/Guidelines/DTD/tei2.dtd\" [\n" + 
    		"<!ENTITY % TEI.prose 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.linking 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.figures 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.analysis 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.XML 'INCLUDE'>\n" + 
    		"<!ENTITY % ISOlat1 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat1.ent'>\n" + 
    		"%ISOlat1;\n" + 
    		"<!ENTITY % ISOlat2 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat2.ent'>\n" + 
    		"%ISOlat2;\n" + 
    		"<!ENTITY % ISOnum SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-num.ent'>\n" + 
    		"%ISOnum;\n" + 
    		"<!ENTITY % ISOpub SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-pub.ent'>\n" + 
    		"%ISOpub;\n" + 
    		"]>\n" + 
    		"\n" + 
    		"<TEI.2>\n" + 
    		"    <teiHeader>\n" + 
    		"        <fileDesc>\n" + 
    		"            <titleStmt>\n" + 
    		"                <title></title>\n" + 
    		"            </titleStmt>\n" + 
    		"            <publicationStmt>\n" + 
    		"                <p/>\n" + 
    		"            </publicationStmt>\n" + 
    		"            <sourceDesc>\n" + 
    		"                <p/>\n" + 
    		"            </sourceDesc>\n" + 
    		"        </fileDesc>\n" + 
    		"    </teiHeader>\n" + 
    		"    <text>\n" + 
    		"        <body>\n" + 
    		"            <p>\n" + 
    		"                <table rows=\"3\" cols=\"1\">\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>\n" + 
    		"                            <list type=\"ordered\">\n" + 
    		"                                <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row role=\"label\">\n" + 
    		"                                            <cell/>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                                <item>head list2</item>\n" + 
    		"                                <item>head list3</item>\n" + 
    		"                            </list>\n" + 
    		"                        </cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table1<list type=\"bulleted\">\n" + 
    		"                                <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                                <item>item2</item>\n" + 
    		"                                <item>item3</item>\n" + 
    		"                            </list></cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table2</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table3</cell>\n" + 
    		"                    </row>\n" + 
    		"                </table>\n" + 
    		"            </p>\n" + 
    		"        </body>\n" + 
    		"    </text>\n" + 
    		"</TEI.2>", true);
    
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
    		"<!DOCTYPE TEI.2 PUBLIC \"-//TEI P4//DTD Main Document Type//EN\" \"http://www.tei-c.org/Guidelines/DTD/tei2.dtd\" [\n" + 
    		"<!ENTITY % TEI.prose 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.linking 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.figures 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.analysis 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.XML 'INCLUDE'>\n" + 
    		"<!ENTITY % ISOlat1 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat1.ent'>\n" + 
    		"%ISOlat1;\n" + 
    		"<!ENTITY % ISOlat2 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat2.ent'>\n" + 
    		"%ISOlat2;\n" + 
    		"<!ENTITY % ISOnum SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-num.ent'>\n" + 
    		"%ISOnum;\n" + 
    		"<!ENTITY % ISOpub SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-pub.ent'>\n" + 
    		"%ISOpub;\n" + 
    		"]>\n" + 
    		"\n" + 
    		"<TEI.2>\n" + 
    		"    <teiHeader>\n" + 
    		"        <fileDesc>\n" + 
    		"            <titleStmt>\n" + 
    		"                <title></title>\n" + 
    		"            </titleStmt>\n" + 
    		"            <publicationStmt>\n" + 
    		"                <p/>\n" + 
    		"            </publicationStmt>\n" + 
    		"            <sourceDesc>\n" + 
    		"                <p/>\n" + 
    		"            </sourceDesc>\n" + 
    		"        </fileDesc>\n" + 
    		"    </teiHeader>\n" + 
    		"    <text>\n" + 
    		"        <body>\n" + 
    		"            <p>\n" + 
    		"                <table rows=\"3\" cols=\"1\">\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>\n" + 
    		"                            <list type=\"ordered\">\n" + 
    		"                                <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row role=\"label\">\n" + 
    		"                                            <cell/>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                                <item>head list2</item>\n" + 
    		"                                <item>head list3</item>\n" + 
    		"                            </list>\n" + 
    		"                        </cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table1<list type=\"bulleted\">\n" + 
    		"                                <item>item3</item>\n" + 
    		"                                <item>item2</item>\n" + 
    		"                                <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list></cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table2</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table3</cell>\n" + 
    		"                    </row>\n" + 
    		"                </table>\n" + 
    		"            </p>\n" + 
    		"        </body>\n" + 
    		"    </text>\n" + 
    		"</TEI.2>", true);
    
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
    		"<!DOCTYPE TEI.2 PUBLIC \"-//TEI P4//DTD Main Document Type//EN\" \"http://www.tei-c.org/Guidelines/DTD/tei2.dtd\" [\n" + 
    		"<!ENTITY % TEI.prose 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.linking 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.figures 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.analysis 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.XML 'INCLUDE'>\n" + 
    		"<!ENTITY % ISOlat1 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat1.ent'>\n" + 
    		"%ISOlat1;\n" + 
    		"<!ENTITY % ISOlat2 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat2.ent'>\n" + 
    		"%ISOlat2;\n" + 
    		"<!ENTITY % ISOnum SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-num.ent'>\n" + 
    		"%ISOnum;\n" + 
    		"<!ENTITY % ISOpub SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-pub.ent'>\n" + 
    		"%ISOpub;\n" + 
    		"]>\n" + 
    		"\n" + 
    		"<TEI.2>\n" + 
    		"    <teiHeader>\n" + 
    		"        <fileDesc>\n" + 
    		"            <titleStmt>\n" + 
    		"                <title></title>\n" + 
    		"            </titleStmt>\n" + 
    		"            <publicationStmt>\n" + 
    		"                <p/>\n" + 
    		"            </publicationStmt>\n" + 
    		"            <sourceDesc>\n" + 
    		"                <p/>\n" + 
    		"            </sourceDesc>\n" + 
    		"        </fileDesc>\n" + 
    		"    </teiHeader>\n" + 
    		"    <text>\n" + 
    		"        <body>\n" + 
    		"            <p>\n" + 
    		"                <table rows=\"3\" cols=\"1\">\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>\n" + 
    		"                            <list type=\"ordered\">\n" + 
    		"                                <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row role=\"label\">\n" + 
    		"                                            <cell/>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                                <item>head list2</item>\n" + 
    		"                                <item>head list3</item>\n" + 
    		"                            </list>\n" + 
    		"                        </cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table3</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table2</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table1<list type=\"bulleted\">\n" + 
    		"                                <item>item3</item>\n" + 
    		"                                <item>item2</item>\n" + 
    		"                                <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list></cell>\n" + 
    		"                    </row>\n" + 
    		"                </table>\n" + 
    		"            </p>\n" + 
    		"        </body>\n" + 
    		"    </text>\n" + 
    		"</TEI.2>", true);
    
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
    		"<!DOCTYPE TEI.2 PUBLIC \"-//TEI P4//DTD Main Document Type//EN\" \"http://www.tei-c.org/Guidelines/DTD/tei2.dtd\" [\n" + 
    		"<!ENTITY % TEI.prose 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.linking 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.figures 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.analysis 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.XML 'INCLUDE'>\n" + 
    		"<!ENTITY % ISOlat1 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat1.ent'>\n" + 
    		"%ISOlat1;\n" + 
    		"<!ENTITY % ISOlat2 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat2.ent'>\n" + 
    		"%ISOlat2;\n" + 
    		"<!ENTITY % ISOnum SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-num.ent'>\n" + 
    		"%ISOnum;\n" + 
    		"<!ENTITY % ISOpub SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-pub.ent'>\n" + 
    		"%ISOpub;\n" + 
    		"]>\n" + 
    		"\n" + 
    		"<TEI.2>\n" + 
    		"    <teiHeader>\n" + 
    		"        <fileDesc>\n" + 
    		"            <titleStmt>\n" + 
    		"                <title></title>\n" + 
    		"            </titleStmt>\n" + 
    		"            <publicationStmt>\n" + 
    		"                <p/>\n" + 
    		"            </publicationStmt>\n" + 
    		"            <sourceDesc>\n" + 
    		"                <p/>\n" + 
    		"            </sourceDesc>\n" + 
    		"        </fileDesc>\n" + 
    		"    </teiHeader>\n" + 
    		"    <text>\n" + 
    		"        <body>\n" + 
    		"            <p>\n" + 
    		"                <table rows=\"3\" cols=\"1\">\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>\n" + 
    		"                            <list type=\"ordered\">\n" + 
    		"                                <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row role=\"label\">\n" + 
    		"                                            <cell/>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                                <item>head list2</item>\n" + 
    		"                                <item>head list3</item>\n" + 
    		"                            </list>\n" + 
    		"                        </cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table3</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table2</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table1<list type=\"bulleted\">\n" + 
    		"                                <item>item3</item>\n" + 
    		"                                <item>item2</item>\n" + 
    		"                                <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list></cell>\n" + 
    		"                    </row>\n" + 
    		"                </table>\n" + 
    		"            </p>\n" + 
    		"        </body>\n" + 
    		"    </text>\n" + 
    		"</TEI.2>", true);
    
    // Sort he list from header.
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
    		"<!DOCTYPE TEI.2 PUBLIC \"-//TEI P4//DTD Main Document Type//EN\" \"http://www.tei-c.org/Guidelines/DTD/tei2.dtd\" [\n" + 
    		"<!ENTITY % TEI.prose 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.linking 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.figures 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.analysis 'INCLUDE'>\n" + 
    		"<!ENTITY % TEI.XML 'INCLUDE'>\n" + 
    		"<!ENTITY % ISOlat1 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat1.ent'>\n" + 
    		"%ISOlat1;\n" + 
    		"<!ENTITY % ISOlat2 SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-lat2.ent'>\n" + 
    		"%ISOlat2;\n" + 
    		"<!ENTITY % ISOnum SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-num.ent'>\n" + 
    		"%ISOnum;\n" + 
    		"<!ENTITY % ISOpub SYSTEM 'http://www.tei-c.org/Entity_Sets/Unicode/iso-pub.ent'>\n" + 
    		"%ISOpub;\n" + 
    		"]>\n" + 
    		"\n" + 
    		"<TEI.2>\n" + 
    		"    <teiHeader>\n" + 
    		"        <fileDesc>\n" + 
    		"            <titleStmt>\n" + 
    		"                <title></title>\n" + 
    		"            </titleStmt>\n" + 
    		"            <publicationStmt>\n" + 
    		"                <p/>\n" + 
    		"            </publicationStmt>\n" + 
    		"            <sourceDesc>\n" + 
    		"                <p/>\n" + 
    		"            </sourceDesc>\n" + 
    		"        </fileDesc>\n" + 
    		"    </teiHeader>\n" + 
    		"    <text>\n" + 
    		"        <body>\n" + 
    		"            <p>\n" + 
    		"                <table rows=\"3\" cols=\"1\">\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>\n" + 
    		"                            <list type=\"ordered\">\n" + 
    		"                                <item>head list3</item>\n" + 
    		"                                <item>head list2</item>\n" + 
    		"                                <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row role=\"label\">\n" + 
    		"                                            <cell/>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list>\n" + 
    		"                        </cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table3</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table2</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table1<list type=\"bulleted\">\n" + 
    		"                                <item>item3</item>\n" + 
    		"                                <item>item2</item>\n" + 
    		"                                <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list></cell>\n" + 
    		"                    </row>\n" + 
    		"                </table>\n" + 
    		"            </p>\n" + 
    		"        </body>\n" + 
    		"    </text>\n" + 
    		"</TEI.2>", true);
  }

  /**
   * <p><b>Description:</b> Sort the closest parent. TEI P5</p>
   * <p><b>Bug ID:</b> EXM-27906</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void testSortClosestParentTEIP5_EXM_27906() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27906/sortClosestParentTEIP5.xml")), true);
    
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
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"   <teiHeader>\n" + 
    		"      <fileDesc>\n" + 
    		"         <titleStmt>\n" + 
    		"            <title/>\n" + 
    		"         </titleStmt>\n" + 
    		"         <publicationStmt>\n" + 
    		"            <p/>\n" + 
    		"         </publicationStmt>\n" + 
    		"         <sourceDesc>\n" + 
    		"            <p/>\n" + 
    		"         </sourceDesc>\n" + 
    		"      </fileDesc>\n" + 
    		"   </teiHeader>\n" + 
    		"   <text>\n" + 
    		"      <body>\n" + 
    		"         <p><table rows=\"3\" cols=\"1\">\n" + 
    		"               <row role=\"label\">\n" + 
    		"                  <cell>\n" + 
    		"                     <list type=\"ordered\">\n" + 
    		"                        <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                              <row role=\"label\">\n" + 
    		"                                 <cell/>\n" + 
    		"                              </row>\n" + 
    		"                              <row>\n" + 
    		"                                 <cell>head table1</cell>\n" + 
    		"                              </row>\n" + 
    		"                              <row>\n" + 
    		"                                 <cell>head table2</cell>\n" + 
    		"                              </row>\n" + 
    		"                              <row>\n" + 
    		"                                 <cell>head table3</cell>\n" + 
    		"                              </row>\n" + 
    		"                           </table></item>\n" + 
    		"                        <item>head list2</item>\n" + 
    		"                        <item>head list3</item>\n" + 
    		"                     </list>\n" + 
    		"                  </cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>top table1<list type=\"bulleted\">\n" + 
    		"                        <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                        <item>item2</item>\n" + 
    		"                        <item>item3</item>\n" + 
    		"                     </list></cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>top table2</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>top table3</cell>\n" + 
    		"               </row>\n" + 
    		"            </table></p>\n" + 
    		"      </body>\n" + 
    		"   </text>\n" + 
    		"</TEI>\n" + 
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
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"   <teiHeader>\n" + 
    		"      <fileDesc>\n" + 
    		"         <titleStmt>\n" + 
    		"            <title/>\n" + 
    		"         </titleStmt>\n" + 
    		"         <publicationStmt>\n" + 
    		"            <p/>\n" + 
    		"         </publicationStmt>\n" + 
    		"         <sourceDesc>\n" + 
    		"            <p/>\n" + 
    		"         </sourceDesc>\n" + 
    		"      </fileDesc>\n" + 
    		"   </teiHeader>\n" + 
    		"   <text>\n" + 
    		"      <body>\n" + 
    		"         <p><table rows=\"3\" cols=\"1\">\n" + 
    		"               <row role=\"label\">\n" + 
    		"                  <cell>\n" + 
    		"                     <list type=\"ordered\">\n" + 
    		"                        <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                              <row role=\"label\">\n" + 
    		"                                 <cell/>\n" + 
    		"                              </row>\n" + 
    		"                              <row>\n" + 
    		"                                 <cell>head table1</cell>\n" + 
    		"                              </row>\n" + 
    		"                              <row>\n" + 
    		"                                 <cell>head table2</cell>\n" + 
    		"                              </row>\n" + 
    		"                              <row>\n" + 
    		"                                 <cell>head table3</cell>\n" + 
    		"                              </row>\n" + 
    		"                           </table></item>\n" + 
    		"                        <item>head list2</item>\n" + 
    		"                        <item>head list3</item>\n" + 
    		"                     </list>\n" + 
    		"                  </cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>top table1<list type=\"bulleted\">\n" + 
    		"                                <item>item3</item>\n" + 
    		"                                <item>item2</item>\n" + 
    		"                                <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list></cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>top table2</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>top table3</cell>\n" + 
    		"               </row>\n" + 
    		"            </table></p>\n" + 
    		"      </body>\n" + 
    		"   </text>\n" + 
    		"</TEI>\n" + 
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
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"   <teiHeader>\n" + 
    		"      <fileDesc>\n" + 
    		"         <titleStmt>\n" + 
    		"            <title/>\n" + 
    		"         </titleStmt>\n" + 
    		"         <publicationStmt>\n" + 
    		"            <p/>\n" + 
    		"         </publicationStmt>\n" + 
    		"         <sourceDesc>\n" + 
    		"            <p/>\n" + 
    		"         </sourceDesc>\n" + 
    		"      </fileDesc>\n" + 
    		"   </teiHeader>\n" + 
    		"   <text>\n" + 
    		"      <body>\n" + 
    		"         <p><table rows=\"3\" cols=\"1\">\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>\n" + 
    		"                            <list type=\"ordered\">\n" + 
    		"                                <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row role=\"label\">\n" + 
    		"                                            <cell/>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                                <item>head list2</item>\n" + 
    		"                                <item>head list3</item>\n" + 
    		"                            </list>\n" + 
    		"                        </cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table3</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table2</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table1<list type=\"bulleted\">\n" + 
    		"                                <item>item3</item>\n" + 
    		"                                <item>item2</item>\n" + 
    		"                                <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list></cell>\n" + 
    		"                    </row>\n" + 
    		"                </table></p>\n" + 
    		"      </body>\n" + 
    		"   </text>\n" + 
    		"</TEI>\n" + 
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
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"   <teiHeader>\n" + 
    		"      <fileDesc>\n" + 
    		"         <titleStmt>\n" + 
    		"            <title/>\n" + 
    		"         </titleStmt>\n" + 
    		"         <publicationStmt>\n" + 
    		"            <p/>\n" + 
    		"         </publicationStmt>\n" + 
    		"         <sourceDesc>\n" + 
    		"            <p/>\n" + 
    		"         </sourceDesc>\n" + 
    		"      </fileDesc>\n" + 
    		"   </teiHeader>\n" + 
    		"   <text>\n" + 
    		"      <body>\n" + 
    		"         <p><table rows=\"3\" cols=\"1\">\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>\n" + 
    		"                            <list type=\"ordered\">\n" + 
    		"                                <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row role=\"label\">\n" + 
    		"                                            <cell/>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                                <item>head list2</item>\n" + 
    		"                                <item>head list3</item>\n" + 
    		"                            </list>\n" + 
    		"                        </cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table3</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table2</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table1<list type=\"bulleted\">\n" + 
    		"                                <item>item3</item>\n" + 
    		"                                <item>item2</item>\n" + 
    		"                                <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list></cell>\n" + 
    		"                    </row>\n" + 
    		"                </table></p>\n" + 
    		"      </body>\n" + 
    		"   </text>\n" + 
    		"</TEI>\n" + 
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
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<?xml-model href=\"http://www.tei-c.org/release/xml/tei/custom/schema/relaxng/tei_all.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"   <teiHeader>\n" + 
    		"      <fileDesc>\n" + 
    		"         <titleStmt>\n" + 
    		"            <title/>\n" + 
    		"         </titleStmt>\n" + 
    		"         <publicationStmt>\n" + 
    		"            <p/>\n" + 
    		"         </publicationStmt>\n" + 
    		"         <sourceDesc>\n" + 
    		"            <p/>\n" + 
    		"         </sourceDesc>\n" + 
    		"      </fileDesc>\n" + 
    		"   </teiHeader>\n" + 
    		"   <text>\n" + 
    		"      <body>\n" + 
    		"         <p><table rows=\"3\" cols=\"1\">\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>\n" + 
    		"                            <list type=\"ordered\">\n" + 
    		"                                <item>head list3</item>\n" + 
    		"                                <item>head list2</item>\n" + 
    		"                                <item>head list1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row role=\"label\">\n" + 
    		"                                            <cell/>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>head table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list>\n" + 
    		"                        </cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table3</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table2</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>top table1<list type=\"bulleted\">\n" + 
    		"                                <item>item3</item>\n" + 
    		"                                <item>item2</item>\n" + 
    		"                                <item>item1<table rows=\"3\" cols=\"1\">\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table3</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table2</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                        <row>\n" + 
    		"                                            <cell>inner table1</cell>\n" + 
    		"                                        </row>\n" + 
    		"                                    </table></item>\n" + 
    		"                            </list></cell>\n" + 
    		"                    </row>\n" + 
    		"                </table></p>\n" + 
    		"      </body>\n" + 
    		"   </text>\n" + 
    		"</TEI>\n" + 
    		"", true);
  }
  
  /**
   * <p><b>Description:</b> Sort TEI tables with rowspans</p>
   * <p><b>Bug ID:</b> EXM-27890</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void testEXM_27890() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27890/tei2.xml")), true);
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    // Move the caret after title
    moveCaretRelativeTo("k", 0);
    flushAWTBetter();
    
    moveCaret(getCaretPosition() + 1, true);
    flushAWTBetter();

    lastErrorMessage = null;
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    findButtonAndClick(dialog, "OK");

    assertNull(lastErrorMessage);
  }
  
  /**
   * <p><b>Description:</b> Sort TEI tables</p>
   * <p><b>Bug ID:</b> EXM-27890</p>
   *
   * @author iulian_velea
   *
   * @throws Exception
   */
  public void test2EXM_27890() throws Exception {
    open(URLUtil.correct(new File("test/EXM-27890/tei2.xml")), true);
    AuthorEditorPage editorPage = (AuthorEditorPage) editor.getEditorPage(EditorPage.PAGE_AUTHOR);
    
    // Move the caret after title
    moveCaretRelativeTo("11", 0);
    flushAWTBetter();
    
    lastErrorMessage = null;
    new Thread(new Runnable() {
      @Override
      public void run() {
        invokeActionForID("sort");
      }
    }).start();
    sleep(100);
    
    JDialog dialog = findDialog(editorPage.getAuthorAccess().getAuthorResourceBundle().getMessage(ExtensionTags.SORT));
    JComboBox combo = findComponent(dialog, JComboBox.class, 1);
    combo.setSelectedIndex(1);
    
    JCheckBox cb = findComponent(dialog, JCheckBox.class, 1);
    cb.setSelected(true);
    combo = findComponent(dialog, JComboBox.class, 5);
    combo.setSelectedIndex(1);
    findButtonAndClick(dialog, "OK");

    verifyDocument("<?xml version=\"1.0\"?>\n" + 
    		"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n" + 
    		"   <teiHeader>\n" + 
    		"      <fileDesc>\n" + 
    		"         <titleStmt/>\n" + 
    		"      </fileDesc>\n" + 
    		"   </teiHeader>\n" + 
    		"   <text>\n" + 
    		"      <body>\n" + 
    		"         <div>\n" + 
    		"            <head>Tables</head>\n" + 
    		"            <p>Tables may have cells that span multiple columns and rows.</p>\n" + 
    		"            <table rows=\"5\" cols=\"5\">\n" + 
    		"               <head>\n" + 
    		"                  <hi rend=\"bold\">TEI Span Sample</hi>\n" + 
    		"               </head>\n" + 
    		"               <row role=\"label\">\n" + 
    		"                  <cell cols=\"2\">Spans <hi rend=\"bold\">Horizontally</hi>\n" + 
    		"                  </cell>\n" + 
    		"                  <cell>Header 3</cell>\n" + 
    		"                  <cell cols=\"2\">Spans <hi rend=\"bold\">Horizontally</hi>\n" + 
    		"                  </cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell rows=\"4\">Spans <hi rend=\"bold\">Vertically</hi>\n" + 
    		"                  </cell>\n" + 
    		"                  <cell>a</cell>\n" + 
    		"                  <cell>b</cell>\n" + 
    		"                  <cell>c</cell>\n" + 
    		"                  <cell>d</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>e</cell>\n" + 
    		"                  <cell cols=\"2\" rows=\"2\">Spans <hi rend=\"bold\">both</hi>\n" + 
    		"                  </cell>\n" + 
    		"                  <cell>f</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>g</cell>\n" + 
    		"                  <cell>g</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>i</cell>\n" + 
    		"                  <cell>j</cell>\n" + 
    		"                  <cell cols=\"2\">Spans <hi rend=\"bold\">Horizontally</hi>\n" + 
    		"                  </cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>k</cell>\n" + 
    		"                  <cell>l</cell>\n" + 
    		"                  <cell>m</cell>\n" + 
    		"                  <cell>n</cell>\n" + 
    		"                  <cell>o</cell>\n" + 
    		"               </row>\n" + 
    		"               <row>\n" + 
    		"                  <cell>p</cell>\n" + 
    		"                  <cell>q</cell>\n" + 
    		"                  <cell>r</cell>\n" + 
    		"                  <cell>s</cell>\n" + 
    		"                  <cell>t</cell>\n" + 
    		"               </row>\n" + 
    		"            </table>\n" + 
    		"            <table rows=\"3\" cols=\"2\">\n" + 
    		"                    <head/>\n" + 
    		"                    <row role=\"label\">\n" + 
    		"                        <cell>A</cell>\n" + 
    		"                        <cell>BAAAAAA</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>11</cell>\n" + 
    		"                        <cell> cc</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>11</cell>\n" + 
    		"                        <cell>bb</cell>\n" + 
    		"                    </row>\n" + 
    		"                    <row>\n" + 
    		"                        <cell>10424</cell>\n" + 
    		"                        <cell>aa</cell>\n" + 
    		"                    </row>\n" + 
    		"                </table>\n" + 
    		"         </div>\n" + 
    		"      </body>\n" + 
    		"   </text>\n" + 
    		"</TEI>", true);
  }
}