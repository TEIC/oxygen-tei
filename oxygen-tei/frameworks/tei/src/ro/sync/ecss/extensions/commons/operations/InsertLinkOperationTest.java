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
package ro.sync.ecss.extensions.commons.operations;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import ro.sync.ecss.dita.reference.ReferenceInfo;
import ro.sync.ecss.docbook.DocbookAccess;
import ro.sync.ecss.docbook.DocbookAccessCustomizer;
import ro.sync.ecss.docbook.olink.OLinkInfo;
import ro.sync.ecss.extensions.AuthorExtensionActionCore;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.dita.link.InsertLinkOperation;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.insert.crossReference.InsertLocalIDDialog;
import ro.sync.exml.status.MessageListener;
import ro.sync.exml.status.OxygenAppender;
import ro.sync.ui.InputUrlPanel;
import ro.sync.ui.UiUtil;
import ro.sync.ui.application.ApplicationTree;
import ro.sync.util.URLUtil;

/**
 * Test cases for 'Insert link' operations for Docbook framework.
 * 
 * @author adriana_sbircea
 * @author radu_coravu
 *
 */
public class InsertLinkOperationTest extends EditorAuthorExtensionTestBase {
  /**
   * Variable used to check if the ID of a link is properly computed,
   * depending on whether the source and target files are different, or it
   * is one the same.
   */
  String id = "";
  
  /**
   * <p><b>Description:</b> if the source and the target files are actually the same file, 
   * the xlink:href attribute should contain only the target's ID. Test for the "link" element.</p>
   * <p><b>Bug ID:</b> EXM-33945</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testLocalLink_XlinkHrefContainsOnlyTheID() throws Exception {
    open(URLUtil.correct(new File("test/EXM-33209/hrefPasteXInclude.xml")), true);
    
    moveCaretRelativeTo("1st", 0);
    
    final AuthorAccess authorAccess = ((AuthorEditorPage)editor.getCurrentPage()).getAuthorAccess();
    final InsertLocalIDDialog dialog = InsertLocalIDDialog.getInstance();
    
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        // isXref = false => we call the method for "link"
        id = dialog.showLocalLinkDialog(authorAccess, editor.getEditorLocation(), false, true);
      }
    });
    flushAWTBetter();
    
    dialog.selectXlinkHref();
    flushAWTBetter();
    
    dialog.doOk();
    flushAWTBetter();
    assertEquals("#first", id);
    
  }
  
  /**
   * <p><b>Description:</b> if the source and the target files are actually the same file, 
   * the xlink:href attribute should contain only the target's ID. Test for the "xref" element.</p>
   * <p><b>Bug ID:</b> EXM-33945</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testXref_XlinkHrefContainsOnlyTheID() throws Exception {
    open(URLUtil.correct(new File("test/EXM-33209/hrefPasteXInclude.xml")), true);
    
    moveCaretRelativeTo("1st", 0);
    
    final AuthorAccess authorAccess = ((AuthorEditorPage)editor.getCurrentPage()).getAuthorAccess();
    final InsertLocalIDDialog dialog = InsertLocalIDDialog.getInstance();
    
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        // isXref = true
        id = dialog.showLocalLinkDialog(authorAccess, editor.getEditorLocation(), true, true);
      }
    });
    flushAWTBetter();
    
    dialog.selectXlinkHref();
    flushAWTBetter();
    
    dialog.doOk();
    flushAWTBetter();
    assertEquals("#first", id);
    
  }
  
  /**
   * <p><b>Description:</b> if the source and the target files are different files, 
   * the xlink:href attribute should contain also the target's ID. Test for the "xref" element.</p>
   * <p><b>Bug ID:</b> EXM-33945</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testXref_XlinkHrefContainsAlsoTheRelativePath() throws Exception {
    open(URLUtil.correct(new File("test/EXM-33209/whereToPaste.xml")), true);
    
    moveCaretRelativeTo("Text", 0);
    
    final AuthorAccess authorAccess = ((AuthorEditorPage)editor.getCurrentPage()).getAuthorAccess();
    final InsertLocalIDDialog dialog = InsertLocalIDDialog.getInstance();
    
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        // isXref = true
        id = dialog.showLocalLinkDialog(authorAccess, editor.getEditorLocation(), true, true);
      }
    });
    flushAWTBetter();
    
    InputUrlPanel urlChooser = dialog.getUrlChooser();
    urlChooser.setSelectedURL(URLUtil.correct(new File("test/EXM-33209/hrefPasteXInclude.xml")).toExternalForm());
    flushAWTBetter();
    
    dialog.selectXlinkHref();
    flushAWTBetter();
    
    dialog.doOk();
    flushAWTBetter();
    assertEquals("hrefPasteXInclude.xml#first", id);
    
  }
  
  /**
   * <p><b>Description:</b> if the source and the target files are different files, 
   * the xlink:href attribute should contain also the target's ID. Test for the "link" element.</p>
   * <p><b>Bug ID:</b> EXM-33945</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testLocalLink_XlinkHrefContainsAlsoTheRelativePath() throws Exception {
    open(URLUtil.correct(new File("test/EXM-33209/whereToPaste.xml")), true);
    
    moveCaretRelativeTo("Text", 0);
    
    final AuthorAccess authorAccess = ((AuthorEditorPage)editor.getCurrentPage()).getAuthorAccess();
    final InsertLocalIDDialog dialog = InsertLocalIDDialog.getInstance();
    
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        // isXref = true
        id = dialog.showLocalLinkDialog(authorAccess, editor.getEditorLocation(), false, true);
      }
    });
    flushAWTBetter();
    
    InputUrlPanel urlChooser = dialog.getUrlChooser();
    urlChooser.setSelectedURL(URLUtil.correct(new File("test/EXM-33209/hrefPasteXInclude.xml")).toExternalForm());
    flushAWTBetter();
    
    dialog.selectXlinkHref();
    flushAWTBetter();
    
    dialog.doOk();
    flushAWTBetter();
    assertEquals("hrefPasteXInclude.xml#first", id);
    
  }

  /**
   * <p><b>Description:</b> Insert link (external) test on Docbook 5.</p>
   * <p><b>Bug ID:</b> EXM-22236</p>
   *
   * @author adriana_sbircea
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testInsertExternalLinkOnDocbook5Test() throws Exception {
    open(URLUtil.correct(new File("test/EXM-22236/test.xml")), true);
    
    moveCaretRelativeTo("Text", 0);
    
    // Apply the action
    final Action insertLinkExternalAction = getDinamicToolbarAction("Web Link (link)...");
    UiUtil.invokeLater(new Runnable() {
      @Override
      public void run() {
        invokeAction(insertLinkExternalAction);
      }
    });
    waitForWindowToShow("Web Link (link)");
    
    JDialog inputURLDialog = findDialog("Web Link (link)");
    assertNotNull(inputURLDialog);
    JComboBox urlComboBox = findComponent(inputURLDialog, JComboBox.class, 0);
    assertNotNull(urlComboBox);
    urlComboBox.setSelectedItem(URLUtil.correct(new File("test/EXM-22236/test.xml")).toString());
    JButton okBtn = findComponent(inputURLDialog, JButton.class, 1);
    assertNotNull(okBtn);
    okBtn.doClick();
    flushAWTBetter();
    
    assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
    		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
    		"<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
    		"    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
    		"    <info>\n" + 
    		"        <title>Article Template Title</title>\n" + 
    		"        <author>\n" + 
    		"            <orgname>Organization Name</orgname>\n" + 
    		"            <address>\n" + 
    		"                <city>City</city>\n" + 
    		"                <street>Street</street>\n" + 
    		"                <postcode>000000</postcode>\n" + 
    		"                <country>Country</country>\n" + 
    		"            </address>\n" + 
    		"            <email>user@example.com</email>\n" + 
    		"        </author>\n" + 
    		"    </info>\n" + 
    		"    <sect1>\n" + 
    		"        <title>Section1 Title</title>\n" + 
    		"        <subtitle>Section1 Subtitle</subtitle>\n" + 
    		"        <para><link xlink:href=\"test.xml\"/>Text</para>\n" + 
    		"    </sect1>\n" + 
    		"</article>\n" + 
    		"");
  
  }

  /**
     * <p><b>Description:</b> Insert link (local) test on Docbook 5.</p>
     * <p><b>Bug ID:</b> EXM-22236</p>
     *
     * @author adriana_sbircea
     * @author radu_coravu
     *
     * @throws Exception
     */
    public void testInsertLocalLinkOnDocbook5Test() throws Exception {
      DocbookAccess.setDocbookAccessCustomizer(new DocbookAccessCustomizer() {
        
        @Override
        public OLinkInfo editOLink(AuthorAccess authorAccess, String targetDoc, String targetPtr) {
          return null;
        }
        
        @Override
        public OLinkInfo chooseOLink(AuthorAccess authorAccess) {
          return null;
        }
        
        @Override
        public String chooseLocalLink(AuthorAccess authorAccess, boolean isXref, boolean isDB5) {
          return "some_id";
        }
      });
      
      open(URLUtil.correct(new File("test/EXM-22236/test.xml")), true);
      
      moveCaretRelativeTo("Text", 0);
      
      // Apply the action
      final Action insertLinkLocalAction = getDinamicToolbarAction("Cross reference (link)...");
      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          invokeAction(insertLinkLocalAction);
        }
      });
      flushAWTBetter();
      
      assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
      		"<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
      		"    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
      		"    <info>\n" + 
      		"        <title>Article Template Title</title>\n" + 
      		"        <author>\n" + 
      		"            <orgname>Organization Name</orgname>\n" + 
      		"            <address>\n" + 
      		"                <city>City</city>\n" + 
      		"                <street>Street</street>\n" + 
      		"                <postcode>000000</postcode>\n" + 
      		"                <country>Country</country>\n" + 
      		"            </address>\n" + 
      		"            <email>user@example.com</email>\n" + 
      		"        </author>\n" + 
      		"    </info>\n" + 
      		"    <sect1>\n" + 
      		"        <title>Section1 Title</title>\n" + 
      		"        <subtitle>Section1 Subtitle</subtitle>\n" + 
      		"        <para><link linkend=\"some_id\"/>Text</para>\n" + 
      		"    </sect1>\n" + 
      		"</article>\n" + 
      		"");
    }
    
    /**
     * <p><b>Description:</b> Insert XRef test on Docbook 5.</p>
     * <p><b>Bug ID:</b> EXM-22236</p>
     *
     * @author adriana_sbircea
     * @author radu_coravu
     *
     * @throws Exception
     */
    public void testInsertXrefOnDocbook5Test() throws Exception {
      DocbookAccess.setDocbookAccessCustomizer(new DocbookAccessCustomizer() {
        
        @Override
        public OLinkInfo editOLink(AuthorAccess authorAccess, String targetDoc, String targetPtr) {
          return null;
        }
        
        @Override
        public OLinkInfo chooseOLink(AuthorAccess authorAccess) {
          return null;
        }
        
        @Override
        public String chooseLocalLink(AuthorAccess authorAccess, boolean isXref, boolean isDB5) {
          return "some_id";
        }
      });
      
      open(URLUtil.correct(new File("test/EXM-22236/test.xml")), true);
      
      moveCaretRelativeTo("Text", 0);
      
      // Apply the action
      final Action insertLinkLocalAction = getDinamicToolbarAction("Cross reference (xref)...");
      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          invokeAction(insertLinkLocalAction);
        }
      });
      flushAWTBetter();
      
      assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
      		"<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
      		"    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
      		"    <info>\n" + 
      		"        <title>Article Template Title</title>\n" + 
      		"        <author>\n" + 
      		"            <orgname>Organization Name</orgname>\n" + 
      		"            <address>\n" + 
      		"                <city>City</city>\n" + 
      		"                <street>Street</street>\n" + 
      		"                <postcode>000000</postcode>\n" + 
      		"                <country>Country</country>\n" + 
      		"            </address>\n" + 
      		"            <email>user@example.com</email>\n" + 
      		"        </author>\n" + 
      		"    </info>\n" + 
      		"    <sect1>\n" + 
      		"        <title>Section1 Title</title>\n" + 
      		"        <subtitle>Section1 Subtitle</subtitle>\n" + 
      		"        <para><xref linkend=\"some_id\"/>Text</para>\n" + 
      		"    </sect1>\n" + 
      		"</article>\n" + 
      		"");
    }

    /**
     * <p><b>Description:</b> Insert link test on Docbook 4.</p>
     * <p><b>Bug ID:</b> EXM-22236</p>
     *
     * @author adriana_sbircea
     * @author radu_coravu
     *
     * @throws Exception
     */
    public void testInsertLinkOnDocbook4Test() throws Exception {
      DocbookAccess.setDocbookAccessCustomizer(new DocbookAccessCustomizer() {
        
        @Override
        public OLinkInfo editOLink(AuthorAccess authorAccess, String targetDoc, String targetPtr) {
          return null;
        }
        
        @Override
        public OLinkInfo chooseOLink(AuthorAccess authorAccess) {
          return null;
        }
        
        @Override
        public String chooseLocalLink(AuthorAccess authorAccess, boolean isXref, boolean isDB5) {
          return "some_id";
        }
      });
      
      open(URLUtil.correct(new File("test/EXM-22236/testDocbook4.xml")), true);
      
      moveCaretRelativeTo("Text", 0);
      
      // Apply the action
      final Action insertLinkLocalAction = getDinamicToolbarAction("Cross reference (link)...");
      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          invokeAction(insertLinkLocalAction);
        }
      });
      flushAWTBetter();
      
      assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      		"<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
      		"                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
      		"<article>\n" + 
      		"    <title>Article Title</title>\n" + 
      		"    <sect1>\n" + 
      		"        <title>Section1 Title</title>\n" + 
      		"        <para><link linkend=\"some_id\"/>Text</para>\n" + 
      		"    </sect1>\n" + 
      		"</article>");
    }

    /**
     * <p><b>Description:</b> Insert XRef test on Docbook 4.</p>
     * <p><b>Bug ID:</b> EXM-22236</p>
     *
     * @author adriana_sbircea
     * @author radu_coravu
     *
     * @throws Exception
     */
    public void testInsertXrefOnDocbook4Test() throws Exception {
      DocbookAccess.setDocbookAccessCustomizer(new DocbookAccessCustomizer() {
        
        @Override
        public OLinkInfo editOLink(AuthorAccess authorAccess, String targetDoc, String targetPtr) {
          return null;
        }
        
        @Override
        public OLinkInfo chooseOLink(AuthorAccess authorAccess) {
          return null;
        }
        
        @Override
        public String chooseLocalLink(AuthorAccess authorAccess, boolean isXref, boolean isDB5) {
          return "some_id";
        }
      });
      
      open(URLUtil.correct(new File("test/EXM-22236/testDocbook4.xml")), true);
      
      moveCaretRelativeTo("Text", 0);
      
      // Apply the action
      final Action insertLinkLocalAction = getDinamicToolbarAction("Cross reference (xref)...");
      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          invokeAction(insertLinkLocalAction);
        }
      });
      flushAWTBetter();
      
      assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      		"<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
      		"                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
      		"<article>\n" + 
      		"    <title>Article Title</title>\n" + 
      		"    <sect1>\n" + 
      		"        <title>Section1 Title</title>\n" + 
      		"        <para><xref linkend=\"some_id\"/>Text</para>\n" + 
      		"    </sect1>\n" + 
      		"</article>");
    }

    /**
     * <p><b>Description:</b> Insert link (external) test on Docbook 5 with selection.</p>
     * <p><b>Bug ID:</b> EXM-22236</p>
     *
     * @author adriana_sbircea
     * @author radu_coravu
     *
     * @throws Exception
     */
    public void testInsertExternalLinkOnDocbook5WithSelectionTest() throws Exception {
      open(URLUtil.correct(new File("test/EXM-22236/test.xml")), true);
      
      moveCaretRelativeTo("Text", 0);
      moveCaretRelativeTo("Text", "Text".length(), true);
      
      // Apply the action
      final Action insertLinkExternalAction = getDinamicToolbarAction("Web Link (link)...");
      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          invokeAction(insertLinkExternalAction);
        }
      });
      waitForWindowToShow("Web Link (link)");
      
      JDialog inputURLDialog = findDialog("Web Link (link)");
      assertNotNull(inputURLDialog);
      JComboBox urlComboBox = findComponent(inputURLDialog, JComboBox.class, 0);
      assertNotNull(urlComboBox);
      urlComboBox.setSelectedItem(URLUtil.correct(new File("test/EXM-22236/test.xml")).toString());
      JButton okBtn = findComponent(inputURLDialog, JButton.class, 1);
      assertNotNull(okBtn);
      okBtn.doClick();
      flushAWTBetter();
      
      assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
      		"<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
      		"    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
      		"    <info>\n" + 
      		"        <title>Article Template Title</title>\n" + 
      		"        <author>\n" + 
      		"            <orgname>Organization Name</orgname>\n" + 
      		"            <address>\n" + 
      		"                <city>City</city>\n" + 
      		"                <street>Street</street>\n" + 
      		"                <postcode>000000</postcode>\n" + 
      		"                <country>Country</country>\n" + 
      		"            </address>\n" + 
      		"            <email>user@example.com</email>\n" + 
      		"        </author>\n" + 
      		"    </info>\n" + 
      		"    <sect1>\n" + 
      		"        <title>Section1 Title</title>\n" + 
      		"        <subtitle>Section1 Subtitle</subtitle>\n" + 
      		"        <para><link xlink:href=\"test.xml\">Text</link></para>\n" + 
      		"    </sect1>\n" + 
      		"</article>\n" + 
      		"");
    }

    /**
     * <p><b>Description:</b> Insert link (local) test on Docbook 5 with selection.</p>
     * <p><b>Bug ID:</b> EXM-22236</p>
     *
     * @author adriana_sbircea
     * @author radu_coravu
     *
     * @throws Exception
     */
    public void testInsertLocalLinkOnDocbook5WithSelectionTest() throws Exception {
      DocbookAccess.setDocbookAccessCustomizer(new DocbookAccessCustomizer() {
        
        @Override
        public OLinkInfo editOLink(AuthorAccess authorAccess, String targetDoc, String targetPtr) {
          return null;
        }
        
        @Override
        public OLinkInfo chooseOLink(AuthorAccess authorAccess) {
          return null;
        }
        
        @Override
        public String chooseLocalLink(AuthorAccess authorAccess, boolean isXref, boolean isDB5) {
          return "some_id";
        }
      });
      
      open(URLUtil.correct(new File("test/EXM-22236/test.xml")), true);
      
      moveCaretRelativeTo("Text", 0);
      moveCaretRelativeTo("Text", "Text".length(), true);
      
      // Apply the action
      final Action insertLinkLocalAction = getDinamicToolbarAction("Cross reference (link)...");
      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          invokeAction(insertLinkLocalAction);
        }
      });
      flushAWTBetter();
      
      assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
      		"<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
      		"    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
      		"    <info>\n" + 
      		"        <title>Article Template Title</title>\n" + 
      		"        <author>\n" + 
      		"            <orgname>Organization Name</orgname>\n" + 
      		"            <address>\n" + 
      		"                <city>City</city>\n" + 
      		"                <street>Street</street>\n" + 
      		"                <postcode>000000</postcode>\n" + 
      		"                <country>Country</country>\n" + 
      		"            </address>\n" + 
      		"            <email>user@example.com</email>\n" + 
      		"        </author>\n" + 
      		"    </info>\n" + 
      		"    <sect1>\n" + 
      		"        <title>Section1 Title</title>\n" + 
      		"        <subtitle>Section1 Subtitle</subtitle>\n" + 
      		"        <para><link linkend=\"some_id\">Text</link></para>\n" + 
      		"    </sect1>\n" + 
      		"</article>\n" + 
      		"");
    }

    /**
     * <p><b>Description:</b> Insert XRef test on Docbook 5 with selection.</p>
     * <p><b>Bug ID:</b> EXM-22236</p>
     *
     * @author adriana_sbircea
     * @author radu_coravu
     *
     * @throws Exception
     */
    public void testInsertXrefOnDocbook5WithSelectionTest() throws Exception {
      DocbookAccess.setDocbookAccessCustomizer(new DocbookAccessCustomizer() {
        
        @Override
        public OLinkInfo editOLink(AuthorAccess authorAccess, String targetDoc, String targetPtr) {
          return null;
        }
        
        @Override
        public OLinkInfo chooseOLink(AuthorAccess authorAccess) {
          return null;
        }
        
        @Override
        public String chooseLocalLink(AuthorAccess authorAccess, boolean isXref, boolean isDB5) {
          return "some_id";
        }
      });
      
      open(URLUtil.correct(new File("test/EXM-22236/test.xml")), true);
      
      moveCaretRelativeTo("Text", 0);
      moveCaretRelativeTo("Text", "Text".length(), true);
      
      // Apply the action
      final Action insertLinkLocalAction = getDinamicToolbarAction("Cross reference (xref)...");
      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          invokeAction(insertLinkLocalAction);
        }
      });
      flushAWTBetter();
      
      assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
      		"<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
      		"    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
      		"    <info>\n" + 
      		"        <title>Article Template Title</title>\n" + 
      		"        <author>\n" + 
      		"            <orgname>Organization Name</orgname>\n" + 
      		"            <address>\n" + 
      		"                <city>City</city>\n" + 
      		"                <street>Street</street>\n" + 
      		"                <postcode>000000</postcode>\n" + 
      		"                <country>Country</country>\n" + 
      		"            </address>\n" + 
      		"            <email>user@example.com</email>\n" + 
      		"        </author>\n" + 
      		"    </info>\n" + 
      		"    <sect1>\n" + 
      		"        <title>Section1 Title</title>\n" + 
      		"        <subtitle>Section1 Subtitle</subtitle>\n" + 
      		"        <para><xref linkend=\"some_id\"/></para>\n" + 
      		"    </sect1>\n" + 
      		"</article>\n");
    }

    /**
     * <p><b>Description:</b> Insert link (local) test on Docbook 5 with an ID that contains an xlink.</p>
     * <p><b>Bug ID:</b> EXM-22236</p>
     *
     * @author radu_coravu
     *
     * @throws Exception
     */
    public void testInsertLocalLinkWithXLinkOnDocbook5Test() throws Exception {
      final StringBuilder mess = new StringBuilder();
      OxygenAppender appender = new OxygenAppender(Level.ALL, true);
      
      //Catch all logger errors to fail.
      appender.addMessageListener(new MessageListener() {
        @Override
        public void appendMessage(String message, Level level) {
          System.err.println(message);
          mess.append(message);
        }
      });
      LogManager.getRootLogger().addAppender(appender);
      
      open(URLUtil.correct(new File("test/EXM-22236/test.xml")), true);
      
      moveCaretRelativeTo("Text", 0);
      
      // Apply the action
      final Action insertLinkLocalAction = getDinamicToolbarAction("Cross reference (link)...");
      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          invokeAction(insertLinkLocalAction);
        }
      });
      flushAWTBetter();
      
      JDialog insertLocalLinkDialog = findDialog("Cross reference");
      JTextField urlChooserTextField = findComponent(insertLocalLinkDialog, JTextField.class, 0);
      URL fileURL = URLUtil.correct(new File("test/EXM-22236/xlinkDB5.xml"));
      urlChooserTextField.setText(fileURL.toString());
      flushAWTBetter();
      
      ApplicationTree findComponent = findComponent(insertLocalLinkDialog, ApplicationTree.class, 0);
      assertNotNull(findComponent);
      findComponent.requestFocus();
      sendKey(findComponent, KeyEvent.VK_KP_DOWN);
      flushAWTBetter();
      
      JButton okButton = findButton(insertLocalLinkDialog, "OK");
      assertNotNull(okButton);
      okButton.doClick();
      flushAWTBetter();
      
      String console = mess.toString();
      
      assertFalse(console.contains("The prefix \"xlink\" for attribute \"xlink:href\" " +
      		"associated with an element type \"link\" is not bound."));
      
      assertDocumentContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
      		"<?xml-model href=\"http://www.oasis-open.org/docbook/xml/5.0/rng/docbook.rng\" type=\"application/xml\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n" + 
      		"<article xmlns=\"http://docbook.org/ns/docbook\"\n" + 
      		"    xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\">\n" + 
      		"    <info>\n" + 
      		"        <title>Article Template Title</title>\n" + 
      		"        <author>\n" + 
      		"            <orgname>Organization Name</orgname>\n" + 
      		"            <address>\n" + 
      		"                <city>City</city>\n" + 
      		"                <street>Street</street>\n" + 
      		"                <postcode>000000</postcode>\n" + 
      		"                <country>Country</country>\n" + 
      		"            </address>\n" + 
      		"            <email>user@example.com</email>\n" + 
      		"        </author>\n" + 
      		"    </info>\n" + 
      		"    <sect1>\n" + 
      		"        <title>Section1 Title</title>\n" + 
      		"        <subtitle>Section1 Subtitle</subtitle>\n" + 
      		"        <para><link linkend=\"para_mpp_xqq_1h\"/>Text</para>\n" + 
      		"    </sect1>\n" + 
      		"</article>\n" + 
      		"");
    }
    
    /**
     * <p><b>Description:</b> Test insert link operation with preferred element.
     * The preferred element name is a valid specializations. Use this name.</p>
     * <p><b>Bug ID:</b> EXM-36672</p>
     *
     * @author sorin_carbunaru
     *
     * @throws Exception
     */
    public void testInsertXrefWithPreferredElementName() throws Exception {
      open(URLUtil.correct(new File("test/EXM-36672/testWithRellinks.xml")), true);

      final InsertLinkOperation op = new InsertLinkOperation();

      AuthorExtensionActionCore authorExtensionActionCore = new AuthorExtensionActionCore(
          vViewport.getAuthorAccess(), null, null, null, vViewport.getAPISelectionModel(),
          vViewport.getController());
      authorExtensionActionCore.addListener(vViewport);

      ditaRefInfo = new ReferenceInfo("myFile.dita", "");

      Map userValues = new HashMap();
      userValues.put("href type", "dita topic");
      userValues.put("preferred element name", "linkMy");    

      final ArgumentsMap operationArguments =
          authorExtensionActionCore.getOperationArguments(userValues, op);

      String text = vViewport.getController().getAuthorDocument().getText();
      vViewport.moveTo(text.indexOf("CBA") + "CBA".length() + 3);
      flushAWTBetter();
      sleep(500);

      final boolean[] exceptionThrown = new boolean[1];
      exceptionThrown[0] = false;

      UiUtil.invokeSynchronously(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), operationArguments);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
            exceptionThrown[0] = true;
          } catch (NullPointerException e) {
            e.printStackTrace();
            exceptionThrown[0] = true;
          } 
        }
      });

      // Wait for the operation to finish.
      flushAWTBetter();
      Thread.sleep(500);

      assertFalse(exceptionThrown[0]);

      String serializeDocument = serializeDocumentViewport(vViewport, false);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
              "<?xml-model href=\"../EXM-36747/org.dita-ng.doctypes/rng/technicalContent/rng/topic.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
              "<topic id=\"abc\">\n" + 
              "    <title>ABC</title>\n" + 
              "    <body>\n" + 
              "        <p>CBA</p>\n" + 
              "    </body>\n" + 
              "    <related-links>\n" + 
              "        <linkMy href=\"myFile.dita\"/>\n" + 
              "    </related-links>\n" + 
              "</topic>\n" + 
              "", serializeDocument);
    }

    /**
     * <p><b>Description:</b> Test insert link operation with preferred element.
     * The preferred element name does not correspond to a specialization.
     * Fallback to "link".</p>
     * <p><b>Bug ID:</b> EXM-36672</p>
     *
     * @author sorin_carbunaru
     *
     * @throws Exception
     */
    public void testInsertXrefWithPreferredElementName_2() throws Exception {
      open(URLUtil.correct(new File("test/EXM-36672/testWithRellinks.xml")), true);

      final InsertLinkOperation op = new InsertLinkOperation();

      AuthorExtensionActionCore authorExtensionActionCore = new AuthorExtensionActionCore(
          vViewport.getAuthorAccess(), null, null, null, vViewport.getAPISelectionModel(),
          vViewport.getController());
      authorExtensionActionCore.addListener(vViewport);

      Map userValues = new HashMap();
      userValues.put("format", "xml");
      userValues.put("href type", "non dita resource");
      userValues.put("preferred element name", "linkMy0");    

      final ArgumentsMap operationArguments =
          authorExtensionActionCore.getOperationArguments(userValues, op);

      String text = vViewport.getController().getAuthorDocument().getText();
      vViewport.moveTo(text.indexOf("CBA") + "CBA".length() + 3);
      flushAWTBetter();
      sleep(500);

      final boolean[] exceptionThrown = new boolean[1];
      exceptionThrown[0] = false;

      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), operationArguments);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
            exceptionThrown[0] = true;
          } catch (NullPointerException e) {
            e.printStackTrace();
            exceptionThrown[0] = true;
          } 
        }
      });
      flushAWTBetter();
      Thread.sleep(300);

      JDialog refDialog = findDialog("File Reference");
      assertNotNull(refDialog);

      Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      sendString(focusOwner, "myFile.ext");
      flushAWTBetter();

      sendKey(refDialog.getRootPane(), KeyEvent.VK_ENTER);

      // Wait for the operation to finish.
      flushAWTBetter();
      Thread.sleep(500);

      assertFalse(exceptionThrown[0]);

      String serializeDocument = serializeDocumentViewport(vViewport, false);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
              "<?xml-model href=\"../EXM-36747/org.dita-ng.doctypes/rng/technicalContent/rng/topic.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
              "<topic id=\"abc\">\n" + 
              "    <title>ABC</title>\n" + 
              "    <body>\n" + 
              "        <p>CBA</p>\n" + 
              "    </body>\n" + 
              "    <related-links>\n" + 
              "        <link href=\"myFile.ext\" format=\"ext\"/>\n" + 
              "    </related-links>\n" + 
              "</topic>\n" + 
              "", serializeDocument);
    }
    
    /**
     * <p><b>Description:</b> Test insert link operation with preferred element.
     * No preferred element name. Fallback to "link".</p>
     * <p><b>Bug ID:</b> EXM-36672</p>
     *
     * @author sorin_carbunaru
     *
     * @throws Exception
     */
    public void testInsertXrefWithPreferredElementName_3() throws Exception {
      open(URLUtil.correct(new File("test/EXM-36672/testWithRellinks.xml")), true);

      final InsertLinkOperation op = new InsertLinkOperation();

      AuthorExtensionActionCore authorExtensionActionCore = new AuthorExtensionActionCore(
          vViewport.getAuthorAccess(), null, null, null, vViewport.getAPISelectionModel(),
          vViewport.getController());
      authorExtensionActionCore.addListener(vViewport);

      Map userValues = new HashMap();
      userValues.put("format", "html");
      userValues.put("scope", "external");
      userValues.put("href type", "web page");

      final ArgumentsMap operationArguments =
          authorExtensionActionCore.getOperationArguments(userValues, op);

      String text = vViewport.getController().getAuthorDocument().getText();
      vViewport.moveTo(text.indexOf("CBA") + "CBA".length() + 3);
      flushAWTBetter();
      sleep(500);

      final boolean[] exceptionThrown = new boolean[1];
      exceptionThrown[0] = false;

      UiUtil.invokeLater(new Runnable() {
        @Override
        public void run() {
          try {
            op.doOperation(vViewport.getAuthorAccess(), operationArguments);
          } catch (AuthorOperationException e) {
            e.printStackTrace();
            exceptionThrown[0] = true;
          } catch (NullPointerException e) {
            e.printStackTrace();
            exceptionThrown[0] = true;
          } 
        }
      });
      flushAWTBetter();
      Thread.sleep(300);

      JDialog refDialog = findDialog("Web Link");
      assertNotNull(refDialog);

      Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      sendString(focusOwner, "www.abc.gg");
      flushAWTBetter();

      sendKey(refDialog.getRootPane(), KeyEvent.VK_ENTER);

      // Wait for the operation to finish.
      flushAWTBetter();
      Thread.sleep(500);

      assertFalse(exceptionThrown[0]);

      String serializeDocument = serializeDocumentViewport(vViewport, false);
      assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<?xml-model href=\"../EXM-36747/org.dita-ng.doctypes/rng/technicalContent/rng/topic.rng\" type=\"application/xml\" schematypens=\"http://relaxng.org/ns/structure/1.0\"?>\n" + 
          "<topic id=\"abc\">\n" + 
          "    <title>ABC</title>\n" + 
          "    <body>\n" + 
          "        <p>CBA</p>\n" + 
          "    </body>\n" + 
          "    <related-links>\n" + 
          "        <link href=\"http://www.abc.gg\" format=\"html\" scope=\"external\"/>\n" + 
          "    </related-links>\n" + 
          "</topic>\n" + 
          "", serializeDocument);
    }
}
