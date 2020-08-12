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
package ro.sync.ecss.extensions.commons.table.support;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import ro.sync.document.CompoundEditUndoManager;
import ro.sync.ecss.AuthorErrorHandler;
import ro.sync.ecss.component.AuthorViewport;
import ro.sync.ecss.css.IStyleSheet;
import ro.sync.ecss.css.StyleSheet;
import ro.sync.ecss.dom.AuthorDocumentImpl;
import ro.sync.ecss.dom.AuthorElementImpl;
import ro.sync.ecss.dom.AuthorSentinelNode;
import ro.sync.ecss.dom.builder.AuthorDocumentFactory;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.WidthRepresentation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.layout.CaretInfo;
import ro.sync.ecss.layout.DumpConfiguration;
import ro.sync.ecss.layout.LayoutUtils;
import ro.sync.ecss.layout.RootBox;
import ro.sync.ecss.layout.TableBox;
import ro.sync.ecss.layout.TableCellBox;
import ro.sync.ecss.layout.table.AuthorTableResizeDialogPresenter;
import ro.sync.ecss.layout.table.AuthorTableResizingHandler;
import ro.sync.ecss.ue.AuthorDocumentControllerImpl;
import ro.sync.exml.IDEAccess;
import ro.sync.exml.SAIDEAccess;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.exml.workspace.api.editor.page.author.css.CSSResource;
import ro.sync.ui.UiUtil;
import ro.sync.util.PlatformDetector;
import ro.sync.util.URLUtil;

/**
 * Test that the cell spanning is computed correctly.
 * @author radu_coravu
 */
public class CALSTableCellInfoProviderTest extends EditorAuthorExtensionTestBase {
  
  /**
   * Constructor.
   */
  public CALSTableCellInfoProviderTest() {
    super(false, true);
    // Activate screen-shots on fail
    activateScreenshotsOnTestFail();
  }
  
  /**
   * <p><b>Description:</b> Inner HTML table in CALS table.</p>
   * <p><b>Bug ID:</b> EXM-24409</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testInnerHTMLTableInCalsTableEXM_24409() throws Exception {
    open(URLUtil.correct(new File("test/authorExtensions/EXM-24409.xml")), true);
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    
    
    //The inserted HTML table should not make the CALS table very large.
    if(PlatformDetector.isWin32()){
      assertEquals( 
          "Please run this test on Linux!", 
              dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:418 H:238 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:8 W:418 H:217 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:418 H:217 ]\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:0 W:418 H:217 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:84 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:84 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'S'[X:77 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "      BlockElementBox: <para>[X:12 Y:31 W:406 H:186 ]\n" + 
          "        BlockElementBox: <table>[X:4 Y:0 W:398 H:186 ]\n" + 
          "          BlockElementBox: <title>[X:14 Y:2 W:364 H:18 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:7 H:18 ]\n" + 
          "              LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
          "                DocumentTextBox: 'T'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "          TableBox: <tgroup>[X:14 Y:24 W:382 H:160 ]\n" + 
          "            TableCellBox: <entry>[X:1 Y:1 W:60 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:7 H:18 ]\n" + 
          "                  DocumentTextBox: 'a'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:61 Y:1 W:320 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:7 H:18 ]\n" + 
          "                  DocumentTextBox: 'b'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:1 Y:23 W:60 H:136 ]\n" + 
          "              TableBox: <table>[X:6 Y:13 W:48 H:110 ]\n" + 
          "                BlockElementBox: <caption>[X:0 Y:0 W:48 H:18 ]\n" + 
          "                  ParagraphBox[X:0 Y:0 W:14 H:18 ]\n" + 
          "                    LineBox: <caption>[X:0 Y:0 W:14 H:18 ]\n" + 
          "                      DocumentTextBox: 'T2'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "                TableCellBox: <th>[X:2 Y:20 W:22 H:22 ]\n" + 
          "                  ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                    LineBox: <th>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                      EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "                TableCellBox: <th>[X:24 Y:20 W:22 H:22 ]\n" + 
          "                  ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                    LineBox: <th>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                      EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "                TableCellBox: <td>[X:2 Y:42 W:22 H:22 ]\n" + 
          "                  ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                    LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                      EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "                TableCellBox: <td>[X:24 Y:42 W:22 H:22 ]\n" + 
          "                  ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                    LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                      EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "                TableCellBox: <td>[X:2 Y:64 W:22 H:22 ]\n" + 
          "                  ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                    LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                      EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "                TableCellBox: <td>[X:24 Y:64 W:22 H:22 ]\n" + 
          "                  ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                    LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                      EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "                TableCellBox: <td>[X:2 Y:86 W:22 H:22 ]\n" + 
          "                  ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                    LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                      EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "                TableCellBox: <td>[X:24 Y:86 W:22 H:22 ]\n" + 
          "                  ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                    LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                      EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:61 Y:23 W:320 H:136 ]\n" + 
          "              ParagraphBox[X:2 Y:13 W:7 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:7 H:18 ]\n" + 
          "                  DocumentTextBox: 'd'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * <p><b>Description:</b> Test that the rowsep and colspec are correctly rendered when there is a 
   * border-spacing set in css.</p>
   * <p><b>Bug ID:</b> EXM-30827</p>
   *
   * @author mihaela
   * @author adriana_sbircea
   *
   * @throws Exception
   */
  public void testRowsepColspecTableEXM_30827() throws Exception {
    open(URLUtil.correct(new File("test/EXM-30827.xml")), true);
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    
    
    //The inserted HTML table should not make the CALS table very large.
    if(PlatformDetector.isWin32()){
      assertEquals( 
          "Please run this test on Linux!", 
              dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:264 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:8 W:400 H:246 ]\n" + 
          "  BlockElementBox: <task>[X:0 Y:0 W:400 H:246 ]\n" + 
          "    BlockElementBox: <title>[X:7 Y:0 W:393 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:39 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:39 H:18 ]\n" + 
          "          EmptyInlineElemTextBox: 'title'[X:0 Y:0 W:39 H:18 ]\n" + 
          "    BlockElementBox: <shortdesc>[X:7 Y:27 W:393 H:18 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:133 H:18 ]\n" + 
          "        LineBox: <shortdesc>[X:0 Y:0 W:133 H:18 ]\n" + 
          "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:133 H:18 ]\n" + 
          "            StaticTextBox: 'Short Description: '[X:0 Y:0 W:133 H:18 ]\n" + 
          "    BlockElementBox: <taskbody>[X:7 Y:52 W:393 H:194 ]\n" + 
          "      BlockElementBox: <context>[X:0 Y:0 W:393 H:43 ]\n" + 
          "        BlockPseudoElementBox: before[X:12 Y:0 W:381 H:18 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:63 H:18 ]\n" + 
          "            LineBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
          "              StaticTextBox: 'Context: '[X:0 Y:0 W:63 H:18 ]\n" + 
          "        BlockElementBox: <p>[X:12 Y:25 W:381 H:18 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:11 H:18 ]\n" + 
          "            LineBox: <p>[X:0 Y:0 W:11 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: 'p'[X:0 Y:0 W:11 H:18 ]\n" + 
          "      BlockElementBox: <steps>[X:14 Y:50 W:379 H:144 ]\n" + 
          "        BlockElementBox: <step>[X:14 Y:0 W:365 H:144 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:42 H:18 ]\n" + 
          "            LineBox: <step>[X:0 Y:0 W:42 H:18 ]\n" + 
          "              InlineStaticContentForElementBox: <before>[X:0 Y:0 W:42 H:18 ]\n" + 
          "                StaticTextBox: 'Step '[X:0 Y:0 W:35 H:18 ]\n" + 
          "                StaticTextBox: '1'[X:35 Y:0 W:7 H:18 ]\n" + 
          "          BlockElementBox: <cmd>[X:0 Y:18 W:365 H:18 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:25 H:18 ]\n" + 
          "              LineBox: <cmd>[X:0 Y:0 W:25 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: 'cmd'[X:0 Y:0 W:25 H:18 ]\n" + 
          "          TableBox: <choicetable>[X:5 Y:46 W:355 H:98 ]\n" + 
          "            TableCellBox: <choptionhd>[X:1 Y:1 W:177 H:24 ]\n" + 
          "              ParagraphBox[X:3 Y:3 W:63 H:18 ]\n" + 
          "                LineBox: <choptionhd>[X:0 Y:0 W:63 H:18 ]\n" + 
          "                  DocumentTextBox: 'dfdsfdsfd'[X:0 Y:0 W:63 H:18 ](Length:9, StartRel:1)\n" + 
          "            TableCellBox: <chdeschd>[X:178 Y:1 W:176 H:24 ]\n" + 
          "              ParagraphBox[X:3 Y:3 W:63 H:18 ]\n" + 
          "                LineBox: <chdeschd>[X:0 Y:0 W:63 H:18 ]\n" + 
          "                  DocumentTextBox: 'dsfdsfdsf'[X:0 Y:0 W:63 H:18 ](Length:9, StartRel:1)\n" + 
          "            TableCellBox: <choption>[X:1 Y:25 W:177 H:24 ]\n" + 
          "              ParagraphBox[X:3 Y:3 W:119 H:18 ]\n" + 
          "                LineBox: <choption>[X:0 Y:0 W:119 H:18 ]\n" + 
          "                  DocumentTextBox: 'dsfdsfdsfdsfdsfds'[X:0 Y:0 W:119 H:18 ](Length:17, StartRel:1)\n" + 
          "            TableCellBox: <chdesc>[X:178 Y:25 W:176 H:24 ]\n" + 
          "              ParagraphBox[X:3 Y:3 W:98 H:18 ]\n" + 
          "                LineBox: <chdesc>[X:0 Y:0 W:98 H:18 ]\n" + 
          "                  DocumentTextBox: 'dsfdsfdsdsfdsf'[X:0 Y:0 W:98 H:18 ](Length:14, StartRel:1)\n" + 
          "            TableCellBox: <choption>[X:1 Y:49 W:177 H:24 ]\n" + 
          "              ParagraphBox[X:3 Y:3 W:77 H:18 ]\n" + 
          "                LineBox: <choption>[X:0 Y:0 W:77 H:18 ]\n" + 
          "                  DocumentTextBox: 'dfdsfsdfdsf'[X:0 Y:0 W:77 H:18 ](Length:11, StartRel:1)\n" + 
          "            TableCellBox: <chdesc>[X:178 Y:49 W:176 H:24 ]\n" + 
          "              ParagraphBox[X:3 Y:3 W:18 H:18 ]\n" + 
          "                LineBox: <chdesc>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <choption>[X:1 Y:73 W:177 H:24 ]\n" + 
          "              ParagraphBox[X:3 Y:3 W:18 H:18 ]\n" + 
          "                LineBox: <choption>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <chdesc>[X:178 Y:73 W:176 H:24 ]\n" + 
          "              ParagraphBox[X:3 Y:3 W:18 H:18 ]\n" + 
          "                LineBox: <chdesc>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testPara() throws Exception {
    String xml =
      "<?xml-stylesheet type=\"text/css\" href=\"" + new File(".").toURI().toString() + "frameworks/docbook/css/hide_colspec.css\"?>\n" +
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "        <table>\n" + 
      "            <tgroup cols=\"1\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
      "                <colspec colnum=\"5\" colname=\"c1\" colwidth=\"1*\"/>\n" + 
      "                <tbody>\n" + 
      "                    <row>\n" + 
      "                        <entry colname=\"c1\"><para>b2</para></entry>\n" + 
      "                    </row>\n" + 
      "                </tbody>\n" + 
      "            </tgroup>\n" + 
      "        </table>\n" + 
      "    </sect1>\n" + 
      "</article>";
    

    initEditor(xml);
    flushAWTBetter();
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    
    if(PlatformDetector.isWin32()){
      assertEquals( 
          "Please run this test on Linux!", 
              dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:68 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:400 H:42 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:400 H:42 ]\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:0 W:400 H:42 ]\n" + 
          "      BlockElementBox: <table>[X:16 Y:0 W:380 H:42 ]\n" + 
          "        TableBox: <tgroup>[X:16 Y:4 W:360 H:34 ]\n" + 
          "          TableCellBox: <entry>[X:1 Y:1 W:358 H:32 ]\n" + 
          "            BlockElementBox: <para>[X:2 Y:7 W:354 H:18 ]\n" + 
          "              ParagraphBox[X:0 Y:0 W:14 H:18 ]\n" + 
          "                LineBox: <para>[X:0 Y:0 W:14 H:18 ]\n" + 
          "                  DocumentTextBox: 'b2'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testNestedTableWithColspecs() throws Exception { 
    String xmlDoc = 
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <table frame=\"none\">\n" + 
      "        <tgroup cols=\"2\">\n" + 
      "            <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1*\"/>\n" + 
      "            <colspec colname=\"c2\" colnum=\"2\" colwidth=\"2*\"/>\n" + 
      "            <thead>\n" + 
      "                <row>\n" + 
      "                    <entry colname=\"c1\">Header 1</entry>\n" + 
      "                    <entry colname=\"c2\">Header 2</entry>\n" + 
      "                </row>\n" + 
      "            </thead>\n" + 
      "            <tbody>\n" + 
      "                <row>\n" + 
      "                    <entry colname=\"c1\"><informaltable frame=\"none\">\n" + 
      "                            <tgroup cols=\"2\">\n" + 
      "                                <colspec colname=\"c1\" colnum=\"1\"/>\n" + 
      "                                <colspec colname=\"c2\" colnum=\"2\"/>\n" + 
      "                                <tbody>\n" + 
      "                                    <row>\n" + 
      "                                        <entry/>\n" + 
      "                                        <entry/>\n" + 
      "                                    </row>\n" + 
      "                                </tbody>\n" + 
      "                            </tgroup>\n" + 
      "                        </informaltable></entry>\n" + 
      "                    <entry colname=\"c2\"/>\n" + 
      "                </row>\n" + 
      "                <row>\n" + 
      "                    <entry colname=\"c1\"/>\n" + 
      "                    <entry colname=\"c2\"/>\n" + 
      "                </row>\n" + 
      "                <row>\n" + 
      "                    <entry colname=\"c1\"/>\n" + 
      "                    <entry colname=\"c2\"/>\n" + 
      "                </row>\n" + 
      "            </tbody>\n" + 
      "        </tgroup>\n" + 
      "    </table>\n" + 
      "</article>";
    initEditor(xmlDoc);
    Thread.sleep(300);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();

    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    Thread.sleep(300);
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    String expected = "Please run this test on Linux!";
    if (PlatformDetector.isLinux()) {
      expected = "<RootBox>[X:0 Y:0 W:410 H:210 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:410 H:184 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:410 H:184 ]\n" + 
          "    BlockElementBox: <table>[X:4 Y:0 W:402 H:184 ]\n" + 
          "      TableBox: <tgroup>[X:14 Y:2 W:386 H:180 ]\n" + 
          "        BlockPseudoElementBox: before[X:0 Y:0 W:386 H:30 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "            LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "              StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], enableInReadOnlyContext=true, fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "        TableCellBox: <entry>[X:0 Y:30 W:136 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "            LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "              DocumentTextBox: 'Header 1'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "        TableCellBox: <entry>[X:136 Y:30 W:250 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "            LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "              DocumentTextBox: 'Header 2'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "        TableCellBox: <entry>[X:0 Y:52 W:136 H:84 ]\n" + 
          "          BlockElementBox: <informaltable>[X:6 Y:13 W:124 H:58 ]\n" + 
          "            TableBox: <tgroup>[X:2 Y:2 W:120 H:54 ]\n" + 
          "              BlockPseudoElementBox: before[X:0 Y:0 W:120 H:30 ]\n" + 
          "                ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "                  LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "                    StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], enableInReadOnlyContext=true, fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "              TableCellBox: <entry>[X:1 Y:31 W:59 H:22 ]\n" + 
          "                ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                  LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                    EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "              TableCellBox: <entry>[X:60 Y:31 W:59 H:22 ]\n" + 
          "                ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                  LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                    EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "        TableCellBox: <entry>[X:136 Y:52 W:250 H:84 ]\n" + 
          "          ParagraphBox[X:2 Y:19 W:18 H:18 ]\n" + 
          "            LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "        TableCellBox: <entry>[X:0 Y:136 W:136 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "            LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "        TableCellBox: <entry>[X:136 Y:136 W:250 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "            LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "        TableCellBox: <entry>[X:0 Y:158 W:136 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "            LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "        TableCellBox: <entry>[X:136 Y:158 W:250 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "            LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "";
    }
    assertEquals( 
        expected, 
        dump.toString());
  }
  
  /**
   * Test a table with different colwidths.
   * @author radu_coravu
   * @throws Exception
   */
  public void testLayout() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "        <table width=\"256\">\n" + 
      "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
      "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"1*\"/>\n" + 
      "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"200\"/>\n" + 
      "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"2*\"/>\n" + 
      "                <thead>\n" + 
      "                    <row>\n" + 
      "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
      "                        <entry colname=\"c3\">a3</entry>\n" + 
      "                    </row>\n" + 
      "                </thead>\n" + 
      "                <tbody>\n" + 
      "                    <row>\n" + 
      "                        <entry colname=\"c1\">b1</entry>\n" + 
      "                        <entry colname=\"c2\">b2</entry>\n" + 
      "                        <entry colname=\"c3\">b3</entry>\n" + 
      "                    </row>\n" + 
      "                    <row>\n" + 
      "                        <entry colname=\"c1\">c1</entry>\n" + 
      "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\"\n" + 
      "                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
      "                    </row>\n" + 
      "                </tbody>\n" + 
      "            </tgroup>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    flushAWTBetter();
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    if(PlatformDetector.isWin32()){
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:132 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:400 H:106 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:400 H:106 ]\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:0 W:400 H:106 ]\n" + 
          "      BlockElementBox: <table>[X:16 Y:0 W:380 H:106 ]\n" + 
          "        TableBox: <tgroup>[X:16 Y:4 W:256 H:98 ]\n" + 
          "          BlockPseudoElementBox: before[X:0 Y:0 W:256 H:30 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "              LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "                StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], enableInReadOnlyContext=true, fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "          TableCellBox: <entry>[X:1 Y:31 W:218 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:159 H:18 ]\n" + 
          "              LineBox: <entry>[X:54 Y:0 W:105 H:18 ]\n" + 
          "                DocumentTextBox: 'Horizontal Span'[X:0 Y:0 W:105 H:18 ](Length:15, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:219 Y:31 W:36 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:14 H:18 ]\n" + 
          "                DocumentTextBox: 'a3'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:1 Y:53 W:18 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:14 H:18 ]\n" + 
          "                DocumentTextBox: 'b1'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:19 Y:53 W:200 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:14 H:18 ]\n" + 
          "                DocumentTextBox: 'b2'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:219 Y:53 W:36 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:14 H:18 ]\n" + 
          "                DocumentTextBox: 'b3'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:1 Y:75 W:18 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:14 H:18 ]\n" + 
          "                DocumentTextBox: 'c1'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:19 Y:75 W:236 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:198 H:18 ]\n" + 
          "              LineBox: <entry>[X:33 Y:0 W:165 H:18 ]\n" + 
          "                DocumentTextBox: 'Spans '[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "                InlineElementBox: <emphasis>[X:42 Y:0 W:46 H:18 ]\n" + 
          "                  [shape][X:0 Y:1 W:9 H:13 ]\n" + 
          "                  DocumentTextBox: 'Both'[X:9 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
          "                  [shape][X:37 Y:1 W:9 H:13 ]\n" + 
          "                DocumentTextBox: ' directions'[X:88 Y:0 W:77 H:18 ](Length:11, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * @author radu_coravu
   * @throws Exception
   */
  public void testEXM_9678() throws Exception {
    String xml = 
      "    <table><title>Horizontal span</title>\n" + 
      "        <tgroup cols=\"4\" >\n" + 
      "            <colspec colnum=\"1\" colname=\"col1\" colwidth=\"1*\"/>\n" + 
      "            <colspec colnum=\"2\" colname=\"col2\" colwidth=\"2*\"/>\n" + 
      "            <colspec colnum=\"3\" colname=\"col3\" colwidth=\"1.5*\"/>\n" + 
      "            <colspec colnum=\"4\" colname=\"col4\" colwidth=\"1*\"/>\n" +
      "            <colspec colnum=\"5\" colname=\"col5\" colwidth=\"1*\"/>\n" +
      "<spanspec spanname=\"span1\" namest=\"col4\" nameend=\"col5\"/>\n" + 
      "            <thead>\n" + 
      "                <row>\n" + 
      "                    <entry morerows='1'>First column</entry>\n" + 
      "                    <entry namest=\"col2\" nameend=\"col3\" align=\"center\">Span columns 2 and 3</entry>\n" + 
      "                    <entry spanname='span1'>Fourth column</entry>\n" + 
      "                </row>\n" + 
      "            </thead>\n" + 
      "        </tgroup>\n" + 
      "    </table>";
    
    
    AuthorDocumentImpl document = AuthorDocumentFactory.createFromTests(new StringReader(xml), "fake.xml", null, true, null);
    AuthorElementImpl tGroup = (AuthorElementImpl) document.getRootElement().getContentNodes().get(1);
    assertEquals("tgroup", tGroup.getName());
    
    AuthorTableCellSpanProvider tableSupport = new CALSTableCellInfoProvider();
    
    tableSupport.init(tGroup);
    
    AuthorSentinelNode row = (AuthorSentinelNode) ((AuthorSentinelNode) tGroup.getChild("thead")).getChild("row");
    
    // Test the col span.
    assertNull(tableSupport.getColSpan((AuthorElementImpl) row.getContentNodes().get(0)));
    assertEquals(2, tableSupport.getColSpan((AuthorElementImpl) row.getContentNodes().get(1)).intValue());
    assertEquals(2, tableSupport.getColSpan((AuthorElementImpl) row.getContentNodes().get(2)).intValue());

    // Test the row span.
    assertEquals(2, tableSupport.getRowSpan((AuthorElementImpl) row.getContentNodes().get(0)).intValue());
    assertNull(tableSupport.getRowSpan((AuthorElementImpl) row.getContentNodes().get(1)));
    assertNull(tableSupport.getRowSpan((AuthorElementImpl) row.getContentNodes().get(2)));
  }
  
  /**
   * Test the col width 
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testColWidth() throws Exception {
    String xml = 
      "<?xml-stylesheet type=\"text/css\" href=\"frameworks/docbook/css/hide_colspec.css\"?>" + 
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" +      
      "<article>\n" + 
      "    <table>" +
      "        <title>title</title>" +  
      "        <tgroup cols=\"4\" >\n" + 
      "            <colspec colnum=\"1\" colname=\"col1\" colwidth=\"1*\"/>\n" + 
      "            <colspec colnum=\"2\" colname=\"col2\" colwidth=\"2*\"/>\n" + 
      "            <colspec colnum=\"3\" colname=\"col3\" colwidth=\"1.5* + 15px\"/>\n" + 
      "            <colspec colnum=\"4\" colname=\"col4\" colwidth=\"1*\"/>\n" +
      "            <colspec colnum=\"5\" colname=\"col5\" colwidth=\"1*\"/>\n" +
      "            <spanspec spanname=\"span1\" namest=\"col4\" nameend=\"col5\"/>\n" + 
      "            <thead>\n" + 
      "                <row>\n" + 
      "                    <entry morerows='1' colname=\"col1\">column</entry>\n" + 
      "                    <entry namest=\"col2\" nameend=\"col3\" align=\"center\">Span columns 2 and 3</entry>\n" + 
      "                    <entry spanname='span1'>Fourth column</entry>\n" + 
      "                </row>\n" + 
      "            </thead>\n" + 
      "        </tgroup>\n" + 
      "    </table>" +
      "</article>" ; 
    
    initEditor(xml);

    Thread.sleep(3000);
    
    AuthorDocumentImpl document = 
      ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
    
    AuthorElement tGroup = (AuthorElement) getCellRelativeTo(
        document, "title", "title".length() + 2);
    assertEquals("tgroup", tGroup.getLocalName());
    
    AuthorSentinelNode row = (AuthorSentinelNode) ((AuthorSentinelNode) (((AuthorSentinelNode) tGroup).getChild("thead"))).getChild("row");
    
    CALSTableCellInfoProvider tableSupport = new CALSTableCellInfoProvider();
    
    tableSupport.init(tGroup);
    
    List<WidthRepresentation> colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(0), 0, 0);
    assertEquals(1, colWidth.size());
    
    WidthRepresentation col = colWidth.get(0);
    int fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(1f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
    
    colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(1), 0, 0);
    assertEquals(2, colWidth.size());
    
    col = colWidth.get(0);
    fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(2f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
    col = colWidth.get(1);
    fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(1.5f, col.getRelativeWidth());
    assertEquals(15, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    flushAWTBetter();
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    if(PlatformDetector.isWin32()) {
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:83 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:400 H:57 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:400 H:57 ]\n" + 
          "    BlockElementBox: <table>[X:4 Y:0 W:392 H:57 ]\n" + 
          "      BlockElementBox: <title>[X:16 Y:7 W:372 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:35 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:35 H:18 ]\n" + 
          "            DocumentTextBox: 'title'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "      TableBox: <tgroup>[X:16 Y:29 W:372 H:24 ]\n" + 
          "        TableCellBox: <entry>[X:1 Y:1 W:55 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:42 H:18 ]\n" + 
          "            LineBox: <entry>[X:0 Y:0 W:42 H:18 ]\n" + 
          "              DocumentTextBox: 'column'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "        TableCellBox: <entry>[X:56 Y:1 W:207 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:171 H:18 ]\n" + 
          "            LineBox: <entry>[X:31 Y:0 W:140 H:18 ]\n" + 
          "              DocumentTextBox: 'Span columns 2 and 3'[X:0 Y:0 W:140 H:18 ](Length:20, StartRel:1)\n" + 
          "        TableCellBox: <entry>[X:263 Y:1 W:108 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:91 H:18 ]\n" + 
          "            LineBox: <entry>[X:0 Y:0 W:91 H:18 ]\n" + 
          "              DocumentTextBox: 'Fourth column'[X:0 Y:0 W:91 H:18 ](Length:13, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * Test for the method EXM-14001.
   * @author radu_coravu
   * @throws Exception
   */
  public void testGetColumnSpecWithoutNames() throws Exception {
    String xml = 
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <table>\n" + 
      "        <title>title</title>" + 
      "        <tgroup>\n" + 
      "            <colspec colwidth=\"100px\"/>\n" + 
      "            <colspec colnum=\"2\"  colwidth=\"200px\"/>\n" + 
      "            <colspec colnum=\"3\" colwidth=\"300px\"/>\n" + 
      "            <colspec colwidth=\"400px\"/>\n" + 
      "            <spanspec namest=\"col4\" nameend=\"col5\" spanname=\"s1\"/>\n" + 
      "            <tbody>\n" + 
      "                <row>\n" + 
      "                    <entry>test</entry>\n" + 
      "                    <entry>test</entry>\n" + 
      "                    <entry>test</entry>\n" + 
      "                    <entry>test</entry>\n" + 
      "                </row>\n" + 
      "            </tbody>\n" + 
      "        </tgroup>\n" + 
      "    </table>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorDocumentImpl document = 
      ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
    
    AuthorElement tgroup = (AuthorElement) getCellRelativeTo(
        document, "title", "title".length() + 2);
    assertEquals("tgroup", tgroup.getLocalName());
    
    CALSTableCellInfoProvider tableSupport = new CALSTableCellInfoProvider();
    tableSupport.init(tgroup);
    
    CALSColSpec colSpec = tableSupport.getColSpec(1);
    assertNull(colSpec.getColumnName());
    assertEquals(1, colSpec.getColumnNumber());
    assertEquals("0.0* + 100.0px", colSpec.getColWidth().toString());
    
    colSpec = tableSupport.getColSpec(2);
    assertNull(colSpec.getColumnName());
    assertEquals(2, colSpec.getColumnNumber());
    assertEquals("0.0* + 200.0px", colSpec.getColWidth().toString());
    
    colSpec = tableSupport.getColSpec(3);
    assertNull(colSpec.getColumnName());
    assertEquals(3, colSpec.getColumnNumber());
    assertEquals("0.0* + 300.0px", colSpec.getColWidth().toString());
    
    colSpec = tableSupport.getColSpec(4);
    assertNull(colSpec.getColumnName());
    assertEquals(4, colSpec.getColumnNumber());
    assertEquals("0.0* + 400.0px", colSpec.getColWidth().toString());
  }
  
  /**
   * Test for the method getColumnSpec.
   * @author radu_coravu
   * @throws Exception
   */
  public void testGetColumnSpec() throws Exception {
    String xml = 
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <table>\n" + 
      "        <title>title</title>" + 
      "<tgroup cols=\"5\">\n" + 
      "            <colspec colname=\"col1\"/>\n" + 
      "            <colspec colname=\"col2\"/>\n" + 
      "            <colspec colname=\"col3\"/>\n" + 
      "            <colspec colname=\"col4\"/>\n" + 
      "            <colspec colname=\"col5\"/>\n" + 
      "            <colspec colname=\"col6\"/>\n" + 
      "            <colspec colname=\"col7\"/>\n" + 
      "            <spanspec namest=\"col4\" nameend=\"col5\" spanname=\"s1\"/>\n" + 
      "            <tbody>\n" + 
      "                <row>\n" + 
      "                    <entry namest=\"col1\" nameend=\"col3\">c11.3</entry>\n" + 
      "                    <entry spanname=\"s1\">c12.2</entry>\n" + 
      "                    <entry>c13.1</entry>\n" + 
      "                    <entry colname=\"col7\">c14.1</entry>\n" + 
      "                </row>\n" + 
      "                <row>\n" + 
      "                    <entry colname=\"invalidColName\">c21</entry>\n" + 
      "                    <entry spanname=\"invalidSpanName\">c22</entry>\n" + 
      "                    <entry namest=\"invalidNamest\" nameend=\"invalidNameend\">c23</entry>\n" + 
      "                    <entry>c24</entry>\n" + 
      "                    <entry>c25</entry>\n" + 
      "                    <entry>c26</entry>\n" + 
      "                    <entry>c27</entry>\n" + 
      "                    <entry>c28</entry>\n" + 
      "                </row>\n" + 
      "            </tbody>\n" + 
      "        </tgroup>\n" + 
      "    </table>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorDocumentImpl document = 
      ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
    AuthorAccess authorAccess = ((AuthorEditorPage)editor.getCurrentPage()).getAuthorAccess();
    
    AuthorElement tgroup = (AuthorElement) getCellRelativeTo(
        document, "title", "title".length() + 2);
    assertEquals("tgroup", tgroup.getLocalName());
    
    CALSTableCellInfoProvider tableSupport = new CALSTableCellInfoProvider();
    tableSupport.init(tgroup);
    
    flushAWTBetter();
    
    // Cell 'c11.3'
    AuthorElement cell = (AuthorElement) getCellRelativeTo(
        document, "c11.3", 0);
    CALSColSpec colSpec = tableSupport.getColumnSpec(authorAccess, cell);
    assertEquals("col1", colSpec.getColumnName());
    assertEquals(1, colSpec.getColumnNumber());
    
    // Cell 'c12.2'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c12.2", 0);
    colSpec = tableSupport.getColumnSpec(authorAccess, cell);
    assertEquals("col4", colSpec.getColumnName());
    assertEquals(4, colSpec.getColumnNumber());
  
    // Cell 'c13.1'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c13.1", 0);
    colSpec = tableSupport.getColumnSpec(authorAccess, cell);
    assertEquals("col6", colSpec.getColumnName());
    assertEquals(6, colSpec.getColumnNumber());
  
    // Cell 'c14.1'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c14.1", 0);
    colSpec = tableSupport.getColumnSpec(authorAccess, cell);
    assertEquals("col7", colSpec.getColumnName());
    assertEquals(7, colSpec.getColumnNumber());
  
    // Cell 'c27'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c27", 0);
    colSpec = tableSupport.getColumnSpec(authorAccess, cell);
    assertEquals("col7", colSpec.getColumnName());
    assertEquals(7, colSpec.getColumnNumber());
  
    // Cell 'c28'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c28", 0);
    colSpec = tableSupport.getColumnSpec(authorAccess, cell);
    assertNull(colSpec);
  }


  /**
   * Test for the method getCellSpanSpec.
   * @author radu_coravu
   * @throws Exception
   */
  public void testGetCellSpanSpec() throws Exception {
    String xml = 
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.5/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <table>\n" + 
      "        <title>title</title>" + 
      "<tgroup cols=\"5\">\n" + 
      "            <colspec colname=\"col1\"/>\n" + 
      "            <colspec colname=\"col2\"/>\n" + 
      "            <colspec colname=\"col3\"/>\n" + 
      "            <colspec colname=\"col4\"/>\n" + 
      "            <colspec colname=\"col5\"/>\n" + 
      "            <colspec colname=\"col6\"/>\n" + 
      "            <colspec colname=\"col7\"/>\n" + 
      "            <spanspec namest=\"col4\" nameend=\"col5\" spanname=\"s1\"/>\n" + 
      "            <tbody>\n" + 
      "                <row>\n" + 
      "                    <entry namest=\"col1\" nameend=\"col3\">c11.3</entry>\n" + 
      "                    <entry spanname=\"s1\">c12.2</entry>\n" + 
      "                    <entry>c13.1</entry>\n" + 
      "                    <entry colname=\"col7\">c14.1</entry>\n" + 
      "                </row>\n" + 
      "                <row>\n" + 
      "                    <entry colname=\"invalidColName\">c21</entry>\n" + 
      "                    <entry spanname=\"invalidSpanName\">c22</entry>\n" + 
      "                    <entry namest=\"invalidNamest\" nameend=\"invalidNameend\">c23</entry>\n" + 
      "                    <entry>c24</entry>\n" + 
      "                    <entry>c25</entry>\n" + 
      "                    <entry>c26</entry>\n" + 
      "                    <entry>c27</entry>\n" + 
      "                    <entry>c28</entry>\n" + 
      "                </row>\n" + 
      "            </tbody>\n" + 
      "        </tgroup>\n" + 
      "    </table>\n" + 
      "</article>";
    
    initEditor(xml);
    
    AuthorDocumentImpl document = 
      ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
    AuthorAccess authorAccess = ((AuthorEditorPage)editor.getCurrentPage()).getAuthorAccess();
    
    
    AuthorElement tgroup = (AuthorElement) getCellRelativeTo(
        document, "c11.3", -3);
    assertEquals("tgroup", tgroup.getLocalName());
    
    CALSTableCellInfoProvider tableSupport = new CALSTableCellInfoProvider();
    tableSupport.init(tgroup);
    
    
    // Cell 'c11.3'
    AuthorElement cell = (AuthorElement) getCellRelativeTo(
        document, "c11.3", 0);
    CALSColSpanSpec spanSpec = tableSupport.getCellSpanSpec(authorAccess, cell);
    assertEquals(null, spanSpec.getSpanName());
    assertEquals("col1", spanSpec.getStartColumnName());
    assertEquals("col3", spanSpec.getEndColumnName());
    
    // Cell 'c12.2'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c12.2", 0);
    spanSpec = tableSupport.getCellSpanSpec(authorAccess, cell);
    assertEquals("s1", spanSpec.getSpanName());
    assertEquals("col4", spanSpec.getStartColumnName());
    assertEquals("col5", spanSpec.getEndColumnName());

    // Cell 'c13.1'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c13.1", 0);
    spanSpec = tableSupport.getCellSpanSpec(authorAccess, cell);
    assertEquals(null, spanSpec.getSpanName());
    assertEquals("col6", spanSpec.getStartColumnName());
    assertEquals("col6", spanSpec.getEndColumnName());

    // Cell 'c14.1'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c14.1", 0);
    spanSpec = tableSupport.getCellSpanSpec(authorAccess, cell);
    assertEquals(null, spanSpec.getSpanName());
    assertEquals("col7", spanSpec.getStartColumnName());
    assertEquals("col7", spanSpec.getEndColumnName());

    // Cell 'c27'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c27", 0);
    spanSpec = tableSupport.getCellSpanSpec(authorAccess, cell);
    assertEquals(null, spanSpec.getSpanName());
    assertEquals("col7", spanSpec.getStartColumnName());
    assertEquals("col7", spanSpec.getEndColumnName());

    // Cell 'c28'
    cell = (AuthorElement) getCellRelativeTo(
        document, "c28", 0);
    spanSpec = tableSupport.getCellSpanSpec(authorAccess, cell);
    assertNull(spanSpec);
  }
  
  /**
   * Test the colwidth when the colname is not set (the colnum must be used).  
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testColWidthForColNumber() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <title>Article Title</title>\n" + 
      "    <sect1>\n" + 
      "        <title>Section1 Title</title>\n" + 
      "        <para>Text<table frame=\"none\">\n" + 
      "                <title/>\n" + 
      "                <tgroup cols=\"2\">\n" + 
      "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"100px\"/>\n" + 
      "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"800px\"/>\n" + 
      "                    <thead>\n" + 
      "                        <row>\n" + 
      "                            <entry>Header 1</entry>\n" + 
      "                            <entry>Header 2</entry>\n" + 
      "                        </row>\n" + 
      "                    </thead>\n" + 
      "                    <tbody/>\n" + 
      "                </tgroup>\n" + 
      "            </table></para>\n" + 
      "    </sect1>\n" + 
      "</article>\n" + 
      "";
    

    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    flushAWTBetter();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    
    if(PlatformDetector.isWin32()) {
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:936 H:185 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:9 W:936 H:163 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:936 H:163 ]\n" + 
          "    BlockElementBox: <title>[X:0 Y:0 W:400 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:154 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:154 H:18 ]\n" + 
          "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
          "            StaticTextBox: 'Article: '[X:0 Y:0 W:63 H:18 ]\n" + 
          "          DocumentTextBox: 'Article Title'[X:63 Y:0 W:91 H:18 ](Length:13, StartRel:1)\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:28 W:936 H:135 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:175 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:175 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'Section1 Title'[X:77 Y:0 W:98 H:18 ](Length:14, StartRel:1)\n" + 
          "      BlockElementBox: <para>[X:12 Y:26 W:924 H:109 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:28 H:18 ]\n" + 
          "          LineBox: <para>[X:0 Y:0 W:28 H:18 ]\n" + 
          "            DocumentTextBox: 'Text'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
          "        BlockElementBox: <table>[X:4 Y:31 W:916 H:78 ]\n" + 
          "          BlockElementBox: <title>[X:14 Y:2 W:364 H:18 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:39 H:18 ]\n" + 
          "              LineBox: <title>[X:0 Y:0 W:39 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: 'title'[X:0 Y:0 W:39 H:18 ]\n" + 
          "          TableBox: <tgroup>[X:14 Y:24 W:900 H:52 ]\n" + 
          "            BlockPseudoElementBox: before[X:0 Y:0 W:900 H:30 ]\n" + 
          "              ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "                LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "                  StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], enableInReadOnlyContext=true, fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:30 W:100 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                  DocumentTextBox: 'Header 1'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:100 Y:30 W:800 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                  DocumentTextBox: 'Header 2'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }

  private AuthorSentinelNode getCellRelativeTo(AuthorDocumentImpl document, String str, int relative) 
    throws Exception {
    AuthorDocumentControllerImpl documentController = 
      new AuthorDocumentControllerImpl(new AuthorErrorHandler(), 50, true);
    
    documentController.setDocument(document);
    String contentStr = document.getContent().getString(0, document.getContent().getLength()).replace('\0', 'X');
    return documentController.getSentinelNodeAt(
        contentStr.indexOf(str) + relative);
  }
  
  /**
   * Test the table resizing feature.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTableCols() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "        <table>\n" + 
      "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
      "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"50pt\"/>\n" + 
      "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"100pt\"/>\n" + 
      "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"50pt\"/>\n" + 
      "                <thead>\n" + 
      "                    <row>\n" + 
      "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
      "                        <entry colname=\"c3\">a3</entry>\n" + 
      "                    </row>\n" + 
      "                </thead>\n" + 
      "                <tbody>\n" + 
      "                    <row>\n" + 
      "                        <entry colname=\"c1\">b1</entry>\n" + 
      "                        <entry colname=\"c2\">b2</entry>\n" + 
      "                        <entry colname=\"c3\">b3</entry>\n" + 
      "                    </row>\n" + 
      "                    <row>\n" + 
      "                        <entry colname=\"c1\">c1</entry>\n" + 
      "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\"\n" + 
      "                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
      "                    </row>\n" + 
      "                </tbody>\n" + 
      "            </tgroup>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    flushAWTBetter();
    int x = sendMousePressed(vViewport, 2, false);
    flushAWTBetter();
    sendMouseReleased(vViewport, x - 100);
    flushAWTBetter();
    Thread.sleep(300);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table>\n" + 
        "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"50pt\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"21.6pt\"/>\n" + 
        "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"50pt\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
        "                        <entry colname=\"c3\">a3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">b1</entry>\n" + 
        "                        <entry colname=\"c2\">b2</entry>\n" + 
        "                        <entry colname=\"c3\">b3</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">c1</entry>\n" + 
        "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\">Spans\n" + 
        "                                <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>    </sect1>\n" + 
        "</article>", 
        serializeDocumentViewport(vViewport, true));
    
    
    x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 100);
    flushAWTBetter();
    Thread.sleep(300);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table>\n" + 
        "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"50pt\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"14.4pt\"/>\n" + 
        "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"50pt\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
        "                        <entry colname=\"c3\">a3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">b1</entry>\n" + 
        "                        <entry colname=\"c2\">b2</entry>\n" + 
        "                        <entry colname=\"c3\">b3</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">c1</entry>\n" + 
        "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\">Spans\n" + 
        "                                <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>    </sect1>\n" + 
        "</article>",
        serializeDocumentViewport(vViewport, true));
    
    x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x + 300);
    flushAWTBetter();
    Thread.sleep(300);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table>\n" + 
        "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"50pt\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"256pt\"/>\n" + 
        "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"50pt\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
        "                        <entry colname=\"c3\">a3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">b1</entry>\n" + 
        "                        <entry colname=\"c2\">b2</entry>\n" + 
        "                        <entry colname=\"c3\">b3</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">c1</entry>\n" + 
        "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\">Spans\n" + 
        "                                <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 300);
    flushAWTBetter();
    Thread.sleep(300);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table>\n" + 
        "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"50pt\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"17.6pt\"/>\n" + 
        "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"50pt\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
        "                        <entry colname=\"c3\">a3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">b1</entry>\n" + 
        "                        <entry colname=\"c2\">b2</entry>\n" + 
        "                        <entry colname=\"c3\">b3</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">c1</entry>\n" + 
        "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\">Spans\n" + 
        "                                <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * <p><b>Description:</b> When resizing a table the colspecs are created with default names. 
   * We test that the generated names are unique for a table</p>
   * <p><b>Bug ID:</b> EXM-14589</p>
   *
   * @author mihaela
   * @author alina_iordache
   *
   * @throws Exception
   */
  public void testAResizeTableCols_UniqueColspecNames() throws Exception {
    // Initial xml
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "        <table>\n" + 
      "            <tgroup cols=\"11\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
      "                <colspec colnum=\"2\" colname=\"c1\" colwidth=\"100.0pt\"/>\n" +
      "                <colspec colnum=\"3\" colname=\"c4\" colwidth=\"100.0pt\"/>\n" +
      "                <thead>\n" + 
      "                    <row>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                    </row>\n" + 
      "                </thead>\n" + 
      "                <tbody>\n" + 
      "                    <row>\n" +
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                        <entry>Horizontal Span</entry>\n" + 
      "                    </row>\n" + 
      "                </tbody>\n" + 
      "            </tgroup>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    flushAWTBetter();
    // Resize first column
    int x = sendMousePressed(vViewport, 1, true);
    sendMouseReleased(vViewport, x + 100);
    
    flushAWTBetter();
    // The colspecs should be generated 
    assertEquals( 
        "The colspec generated names should be unique!", 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table>\n" + 
        "            <tgroup cols=\"11\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colnum=\"1\" colname=\"c11\" colwidth=\"100pt\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"c1\" colwidth=\"64.8pt\"/>\n" + 
        "                <colspec colnum=\"3\" colname=\"c4\" colwidth=\"100pt\"/>\n" + 
        "                <colspec colnum=\"4\" colname=\"c44\" colwidth=\"64.8pt\"/>\n" + 
        "                <colspec colnum=\"5\" colname=\"c5\" colwidth=\"64.8pt\"/>\n" + 
        "                <colspec colnum=\"6\" colname=\"c6\" colwidth=\"64.8pt\"/>\n" + 
        "                <colspec colnum=\"7\" colname=\"c7\" colwidth=\"64.8pt\"/>\n" + 
        "                <colspec colnum=\"8\" colname=\"c8\" colwidth=\"64.8pt\"/>\n" + 
        "                <colspec colnum=\"9\" colname=\"c9\" colwidth=\"64.8pt\"/>\n" + 
        "                <colspec colnum=\"10\" colname=\"c10\" colwidth=\"64.8pt\"/>\n" + 
        "                <colspec colnum=\"11\" colname=\"c1111\" colwidth=\"64.8pt\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                        <entry>Horizontal Span</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>    </sect1>\n" + 
        "</article>", 
        serializeDocumentViewport(vViewport, true));
  }
  
  private int userChoice;
  private boolean addCWAsked;
  private boolean onlyProportional;
  /**
   * Test the resizing of the table columns when using relative widths.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeColumnWidths2() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "        <table>\n" + 
      "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
      "                <thead>\n" + 
      "                    <row>\n" + 
      "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
      "                        <entry colname=\"c3\">a3</entry>\n" + 
      "                    </row>\n" + 
      "                </thead>\n" + 
      "                <tbody>\n" + 
      "                    <row>\n" + 
      "                        <entry colname=\"c1\">b1</entry>\n" + 
      "                        <entry colname=\"c2\">b2</entry>\n" + 
      "                        <entry colname=\"c3\">b3</entry>\n" + 
      "                    </row>\n" + 
      "                    <row>\n" + 
      "                        <entry colname=\"c1\">c1</entry>\n" + 
      "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\"\n" + 
      "                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
      "                    </row>\n" + 
      "                </tbody>\n" + 
      "            </tgroup>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {
      @Override
      protected int shouldAddColWidthSpecifications(boolean onlyProportional) {
        addCWAsked = true;
        return userChoice;
      }
    });
    
    addCWAsked = false;
    userChoice = AuthorTableResizeDialogPresenter.ADD_PROPORTIONAL_COLUMN_WIDTHS;
    
    int x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 100);
    
    assertTrue(addCWAsked);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table>\n" + 
        "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"6.11*\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"9.5*\"/>\n" + 
        "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
        "                        <entry colname=\"c3\">a3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">b1</entry>\n" + 
        "                        <entry colname=\"c2\">b2</entry>\n" + 
        "                        <entry colname=\"c3\">b3</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">c1</entry>\n" + 
        "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\"\n" + 
        "                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>    </sect1>\n" + 
        "</article>", 
        serializeDocumentViewport(vViewport, true));
    
    // Try to resize the table by shrinking and enlarging the last table column.
    // Should not be permitted.
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 100);
    
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x + 50);
    
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table>\n" + 
        "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"6.11*\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"9.5*\"/>\n" + 
        "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
        "                        <entry colname=\"c3\">a3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">b1</entry>\n" + 
        "                        <entry colname=\"c2\">b2</entry>\n" + 
        "                        <entry colname=\"c3\">b3</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">c1</entry>\n" + 
        "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\">Spans\n" + 
        "                                <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", 
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 3, true);
    sendMouseReleased(vViewport, x + 150);
    flushAWTBetter();
    
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table>\n" + 
        "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"12.99*\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"2.64*\"/>\n" + 
        "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"1*\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
        "                        <entry colname=\"c3\">a3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">b1</entry>\n" + 
        "                        <entry colname=\"c2\">b2</entry>\n" + 
        "                        <entry colname=\"c3\">b3</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">c1</entry>\n" + 
        "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\">Spans\n" + 
        "                                <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", 
        serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * TC for the layout of the table without colwidths.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testLayoutTableWithoutColWidth() throws Exception {
    String xml =
      "<?xml-stylesheet type=\"text/css\" href=\"" + new File(".").toURI().toString() + "frameworks/docbook/css/hide_colspec.css\"?>\n" +
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "       <table frame=\"none\" width=\"300\">\n" + 
      "                <title/>\n" + 
      "                <tgroup cols=\"2\">\n" + 
      "                    <colspec colnum=\"1\" colname=\"c1\"/>\n" + 
      "                    <colspec colnum=\"2\" colname=\"c2\"/>\n" + 
      "                    <thead>\n" + 
      "                        <row>\n" + 
      "                            <entry>Header 1</entry>\n" + 
      "                            <entry>Header 2</entry>\n" + 
      "                        </row>\n" + 
      "                    </thead>\n" + 
      "                    <tbody>\n" + 
      "                        <row>\n" + 
      "                            <entry/>\n" + 
      "                            <entry/>\n" + 
      "                        </row>\n" + 
      "                        <row>\n" + 
      "                            <entry/>\n" + 
      "                            <entry/>\n" + 
      "                        </row>\n" + 
      "                        <row>\n" + 
      "                            <entry/>\n" + 
      "                            <entry/>\n" + 
      "                        </row>\n" + 
      "                    </tbody>\n" + 
      "                </tgroup>\n" + 
      "            </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    flushAWTBetter();
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    if(PlatformDetector.isWin32()) {
      assertEquals("Please run this test on Windows!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:140 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:400 H:114 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:400 H:114 ]\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:0 W:400 H:114 ]\n" + 
          "      BlockElementBox: <table>[X:16 Y:0 W:380 H:114 ]\n" + 
          "        BlockElementBox: <title>[X:14 Y:2 W:364 H:18 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:39 H:18 ]\n" + 
          "            LineBox: <title>[X:0 Y:0 W:39 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: 'title'[X:0 Y:0 W:39 H:18 ]\n" + 
          "        TableBox: <tgroup>[X:14 Y:24 W:300 H:88 ]\n" + 
          "          TableCellBox: <entry>[X:0 Y:0 W:150 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                DocumentTextBox: 'Header 1'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:150 Y:0 W:150 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                DocumentTextBox: 'Header 2'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:0 Y:22 W:150 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "          TableCellBox: <entry>[X:150 Y:22 W:150 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "          TableCellBox: <entry>[X:0 Y:44 W:150 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "          TableCellBox: <entry>[X:150 Y:44 W:150 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "          TableCellBox: <entry>[X:0 Y:66 W:150 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "          TableCellBox: <entry>[X:150 Y:66 W:150 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * TC for the layout of the table when colwidth is smaller than column minimum width.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testLayoutTableEXM_14011() throws Exception {
    String xml =
      "<?xml-stylesheet type=\"text/css\" href=\"" + new File(".").toURI().toString() + "frameworks/docbook/css/hide_colspec.css\"?>\n" +
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "       <table>\n" + 
      "            <tgroup cols=\"2\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
      "                <colspec colnum=\"1\" colname=\"c4\" colwidth=\"28\"/>\n" + 
      "                <colspec colnum=\"2\" colname=\"c5\" colwidth=\"50\"/>\n" + 
      "                <tbody>\n" + 
      "                    <row>\n" + 
      "                        <entry>b4</entry>\n" + 
      "                        <entry morerows=\"1\" valign=\"middle\"><para>\n" + 
      "                                <emphasis role=\"bold\">Vertical</emphasis> Span </para></entry>\n" + 
      "                    </row>\n" + 
      "                    <row>\n" + 
      "                        <entry>c4</entry>\n" + 
      "                    </row>\n" + 
      "                </tbody>\n" + 
      "            </tgroup>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    flushAWTBetter();
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    
    if(PlatformDetector.isWin32()) {
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:104 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:400 H:78 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:400 H:78 ]\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:0 W:400 H:78 ]\n" + 
          "      BlockElementBox: <table>[X:16 Y:0 W:380 H:78 ]\n" + 
          "        TableBox: <tgroup>[X:16 Y:4 W:90 H:70 ]\n" + 
          "          TableCellBox: <entry>[X:1 Y:1 W:28 H:34 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:14 H:18 ]\n" + 
          "                DocumentTextBox: 'b4'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:29 Y:1 W:60 H:68 ]\n" + 
          "            BlockElementBox: <para>[X:2 Y:7 W:56 H:54 ]\n" + 
          "              ParagraphBox[X:0 Y:0 W:56 H:54 ]\n" + 
          "                LineBox: <para>[X:0 Y:0 W:16 H:18 ]\n" + 
          "                  DocumentTextBox: ' '[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "                  InlineElementBox: <emphasis>[X:7 Y:0 W:9 H:18 ]\n" + 
          "                    [shape][X:0 Y:1 W:9 H:13 ]\n" + 
          "                LineBox: <para>[X:0 Y:18 W:56 H:18 ]\n" + 
          "                  InlineElementBox: <emphasis>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                    DocumentTextBox: 'Vertical'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "                LineBox: <para>[X:0 Y:36 W:51 H:18 ]\n" + 
          "                  InlineElementBox: <emphasis>[X:0 Y:0 W:9 H:18 ]\n" + 
          "                    [shape][X:0 Y:1 W:9 H:13 ]\n" + 
          "                  DocumentTextBox: ' Span '[X:9 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:1 Y:35 W:28 H:34 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:14 H:18 ]\n" + 
          "                DocumentTextBox: 'c4'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * Test the layout of a table with fixed table width and fixed column widths.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testTableLayoutFixedTWAndCW() throws Exception {
    
    // We need a test with the colspecs visible.
    String xml =
      "<?xml-stylesheet type=\"text/css\" href=\"" + new File(".").toURI().toString() + "frameworks/docbook/css/show_colspec.css\"?>\n" + 
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "<table frame=\"none\" width=\"415\">\n" + 
      "                <title/>\n" + 
      "                <tgroup cols=\"2\">\n" + 
      "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"61.07pt\"/>\n" + 
      "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"151.07pt\"/>\n" + 
      "                    <thead>\n" + 
      "                        <row>\n" + 
      "                            <entry>Header 1</entry>\n" + 
      "                            <entry>Header 2</entry>\n" + 
      "                        </row>\n" + 
      "                    </thead>\n" + 
      "                    <tbody>\n" + 
      "                        <row>\n" + 
      "                            <entry/>\n" + 
      "                            <entry/>\n" + 
      "                        </row>\n" + 
      "                    </tbody>\n" + 
      "                </tgroup>\n" + 
      "            </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    flushAWTBetter();
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    
    if(PlatformDetector.isWin32()){
      assertEquals(
      		"Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:694 H:158 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:694 H:132 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:694 H:132 ]\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:0 W:694 H:132 ]\n" + 
          "      BlockElementBox: <table>[X:16 Y:0 W:674 H:132 ]\n" + 
          "        BlockElementBox: <title>[X:14 Y:2 W:364 H:18 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:39 H:18 ]\n" + 
          "            LineBox: <title>[X:0 Y:0 W:39 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: 'title'[X:0 Y:0 W:39 H:18 ]\n" + 
          "        TableBox: <tgroup>[X:14 Y:24 W:658 H:106 ]\n" + 
          "          BlockElementBox: <colspec>[X:0 Y:0 W:658 H:31 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:658 H:31 ]\n" + 
          "              LineBox: <colspec>[X:0 Y:0 W:658 H:31 ]\n" + 
          "                InlineStaticContentForElementBox: <after>[X:0 Y:0 W:658 H:31 ]\n" + 
          "                  StaticTextBox: '  name '[X:0 Y:4 W:49 H:18 ]\n" + 
          "                  StaticEditBox: '{columns=7, edit=@colname, edit_qualified=colname, type=text}'[X:49 Y:3 W:65 H:21 ]value:'c1'\n" + 
          "                  StaticTextBox: '  number '[X:114 Y:4 W:63 H:18 ]\n" + 
          "                  StaticEditBox: '{columns=7, edit=@colnum, edit_qualified=colnum, type=text}'[X:177 Y:3 W:65 H:21 ]value:'1'\n" + 
          "                  StaticTextBox: '  width '[X:242 Y:4 W:56 H:18 ]\n" + 
          "                  StaticEditBox: '{columns=7, edit=@colwidth, edit_qualified=colwidth, type=text}'[X:298 Y:3 W:65 H:21 ]value:'61.07pt'\n" + 
          "                  StaticTextBox: '  align '[X:363 Y:4 W:56 H:18 ]\n" + 
          "                  StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:419 Y:0 W:92 H:26 ]\n" + 
          "                  StaticTextBox: '  '[X:511 Y:4 W:14 H:18 ]\n" + 
          "                  StaticEditBox: '{edit=@colsep, edit_qualified=colsep, fontInherit=true, labels=colsep, type=check, values=1}'[X:525 Y:2 W:64 H:24 ] | |colsep\n" + 
          "                  StaticEditBox: '{edit=@rowsep, edit_qualified=rowsep, fontInherit=true, labels=rowsep, type=check, values=1}'[X:589 Y:2 W:69 H:24 ] | |rowsep\n" + 
          "          BlockElementBox: <colspec>[X:0 Y:31 W:658 H:31 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:658 H:31 ]\n" + 
          "              LineBox: <colspec>[X:0 Y:0 W:658 H:31 ]\n" + 
          "                InlineStaticContentForElementBox: <after>[X:0 Y:0 W:658 H:31 ]\n" + 
          "                  StaticTextBox: '  name '[X:0 Y:4 W:49 H:18 ]\n" + 
          "                  StaticEditBox: '{columns=7, edit=@colname, edit_qualified=colname, type=text}'[X:49 Y:3 W:65 H:21 ]value:'c2'\n" + 
          "                  StaticTextBox: '  number '[X:114 Y:4 W:63 H:18 ]\n" + 
          "                  StaticEditBox: '{columns=7, edit=@colnum, edit_qualified=colnum, type=text}'[X:177 Y:3 W:65 H:21 ]value:'2'\n" + 
          "                  StaticTextBox: '  width '[X:242 Y:4 W:56 H:18 ]\n" + 
          "                  StaticEditBox: '{columns=7, edit=@colwidth, edit_qualified=colwidth, type=text}'[X:298 Y:3 W:65 H:21 ]value:'151.07pt'\n" + 
          "                  StaticTextBox: '  align '[X:363 Y:4 W:56 H:18 ]\n" + 
          "                  StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:419 Y:0 W:92 H:26 ]\n" + 
          "                  StaticTextBox: '  '[X:511 Y:4 W:14 H:18 ]\n" + 
          "                  StaticEditBox: '{edit=@colsep, edit_qualified=colsep, fontInherit=true, labels=colsep, type=check, values=1}'[X:525 Y:2 W:64 H:24 ] | |colsep\n" + 
          "                  StaticEditBox: '{edit=@rowsep, edit_qualified=rowsep, fontInherit=true, labels=rowsep, type=check, values=1}'[X:589 Y:2 W:69 H:24 ] | |rowsep\n" + 
          "          TableCellBox: <entry>[X:0 Y:62 W:77 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                DocumentTextBox: 'Header 1'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:77 Y:62 W:190 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                DocumentTextBox: 'Header 2'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:0 Y:84 W:77 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "          TableCellBox: <entry>[X:77 Y:84 W:190 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * Test for EXM-14311. The content of the table cell should be wrapped after the first word.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testEXM_14311() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "<table frame=\"void\">\n" + 
      "            <caption/>\n" + 
      "            <col width=\"1in\"/>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td>wwww wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>wwww</td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    Thread.sleep(300);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    if(PlatformDetector.isWin32()) {
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:524 H:141 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:524 H:115 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:524 H:115 ]\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:0 W:524 H:115 ]\n" + 
          "      TableBox: <table>[X:16 Y:0 W:504 H:115 ]\n" + 
          "        BlockElementBox: <caption>[X:0 Y:0 W:365 H:18 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:53 H:18 ]\n" + 
          "            LineBox: <caption>[X:0 Y:0 W:53 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: 'caption'[X:0 Y:0 W:53 H:18 ]\n" + 
          "        BlockElementBox: <col>[X:0 Y:18 W:504 H:31 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
          "            LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
          "              InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
          "                StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:'1'\n" + 
          "                StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'1in'\n" + 
          "                StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
          "        TableCellBox: <td>[X:2 Y:51 W:361 H:40 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:357 H:36 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'wwww '[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "            LineBox: <td>[X:0 Y:18 W:357 H:18 ]\n" + 
          "              DocumentTextBox: 'wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww'[X:0 Y:0 W:357 H:18 ](Length:51, StartRel:6)\n" + 
          "        TableCellBox: <td>[X:2 Y:91 W:361 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:28 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:28 H:18 ]\n" + 
          "              DocumentTextBox: 'wwww'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
          "",
              dump.toString());
    }
  }

  /**
   * Test the add column and table width dialog.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testAddWidthsDialog() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "        <table>\n" + 
      "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
      "                <thead>\n" + 
      "                    <row>\n" + 
      "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
      "                        <entry colname=\"c3\">a3</entry>\n" + 
      "                    </row>\n" + 
      "                </thead>\n" + 
      "                <tbody>\n" + 
      "                    <row>\n" + 
      "                        <entry colname=\"c1\">b1</entry>\n" + 
      "                        <entry colname=\"c2\">b2</entry>\n" + 
      "                        <entry colname=\"c3\">b3</entry>\n" + 
      "                    </row>\n" + 
      "                    <row>\n" + 
      "                        <entry colname=\"c1\">c1</entry>\n" + 
      "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\"\n" + 
      "                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
      "                    </row>\n" + 
      "                </tbody>\n" + 
      "            </tgroup>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    System.out.println(xml);
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {
      @Override
      protected int shouldAddColWidthSpecifications(boolean proportional) {
        onlyProportional = proportional;
        return userChoice;
      }
    });
    
    userChoice = AuthorTableResizeDialogPresenter.ADD_FIXED_COLUMN_WIDTHS;
    int x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 100);
    
    // Only column widths specifications (fixed or proportional). 
    assertFalse("Only column widths specifications (fixed or proportional) can be added",
        onlyProportional);
    
    Thread.sleep(300);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table>\n" + 
        "            <tgroup cols=\"3\" align=\"left\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colnum=\"1\" colname=\"c1\" colwidth=\"87.2pt\"/>\n" + 
        "                <colspec colnum=\"2\" colname=\"c2\" colwidth=\"135.2pt\"/>\n" + 
        "                <colspec colnum=\"3\" colname=\"c3\" colwidth=\"14.4pt\"/>\n" + 
        "                <thead>\n" + 
        "                    <row>\n" + 
        "                        <entry namest=\"c1\" nameend=\"c2\" align=\"center\">Horizontal Span</entry>\n" + 
        "                        <entry colname=\"c3\">a3</entry>\n" + 
        "                    </row>\n" + 
        "                </thead>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">b1</entry>\n" + 
        "                        <entry colname=\"c2\">b2</entry>\n" + 
        "                        <entry colname=\"c3\">b3</entry>\n" + 
        "                    </row>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"c1\">c1</entry>\n" + 
        "                        <entry namest=\"c2\" nameend=\"c3\" align=\"center\" valign=\"bottom\"\n" + 
        "                            >Spans <emphasis role=\"bold\">Both</emphasis> directions</entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "        </table>    </sect1>\n" + 
        "</article>",
        serializeDocumentViewport(vViewport, true));
  }

  /**
   * EXM-16220  Layout problem after resizing CALS table.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeColumnWidthsEXM_16220() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<?xml-stylesheet type=\"text/css\" href=\"" + new File(".").toURI().toString() + "frameworks/docbook/css/hide_colspec.css\"?>\n" +
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <title>Article Title</title>\n" + 
      "    <sect1>\n" + 
      "        <title>Section1 Title</title>\n" + 
      "        <para>Text<table frame=\"none\">\n" + 
      "            <title/>\n" + 
      "            <tgroup cols=\"2\">\n" + 
      "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"50pt\"/>\n" + 
      "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"150.0pt\"/>\n" + 
      "                <thead>\n" + 
      "                    <row>\n" + 
      "                        <entry>Header 1</entry>\n" + 
      "                        <entry>Header 2</entry>\n" + 
      "                    </row>\n" + 
      "                </thead>\n" + 
      "                <tbody>\n" + 
      "                    <row>\n" + 
      "                        <entry/>\n" + 
      "                        <entry/>\n" + 
      "                    </row>\n" + 
      "                    <row>\n" + 
      "                        <entry/>\n" + 
      "                        <entry/>\n" + 
      "                    </row>\n" + 
      "                    <row>\n" + 
      "                        <entry/>\n" + 
      "                        <entry/>\n" + 
      "                    </row>\n" + 
      "                </tbody>\n" + 
      "            </tgroup>\n" + 
      "        </table></para>\n" + 
      "    </sect1>\n" + 
      "</article> ";
    
    initEditor(xml);
    flushAWTBetter();
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    int x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 200);
    flushAWTBetter();
    
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text<table frame=\"none\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"50pt\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"42.4pt\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Header 1</entry>\n" + 
        "                            <entry>Header 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "        </table></para>\n" + 
        "    </sect1>\n" + 
        "</article> ", 
        serializeDocumentViewport(vViewport, true).replaceAll("<\\?(.*?)\\?>\n", ""));

   
    
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    if(PlatformDetector.isWin32()) {
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:239 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:9 W:400 H:217 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:400 H:217 ]\n" + 
          "    BlockElementBox: <title>[X:0 Y:0 W:400 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:154 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:154 H:18 ]\n" + 
          "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
          "            StaticTextBox: 'Article: '[X:0 Y:0 W:63 H:18 ]\n" + 
          "          DocumentTextBox: 'Article Title'[X:63 Y:0 W:91 H:18 ](Length:13, StartRel:1)\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:28 W:400 H:189 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:175 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:175 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'Section1 Title'[X:77 Y:0 W:98 H:18 ](Length:14, StartRel:1)\n" + 
          "      BlockElementBox: <para>[X:12 Y:26 W:388 H:163 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:28 H:18 ]\n" + 
          "          LineBox: <para>[X:0 Y:0 W:28 H:18 ]\n" + 
          "            DocumentTextBox: 'Text'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
          "        BlockElementBox: <table>[X:4 Y:31 W:380 H:132 ]\n" + 
          "          BlockElementBox: <title>[X:14 Y:2 W:364 H:18 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:39 H:18 ]\n" + 
          "              LineBox: <title>[X:0 Y:0 W:39 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: 'title'[X:0 Y:0 W:39 H:18 ]\n" + 
          "          TableBox: <tgroup>[X:14 Y:24 W:116 H:106 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:0 W:63 H:40 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                  DocumentTextBox: 'Header 1'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:63 Y:0 W:53 H:40 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:49 H:36 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:49 H:18 ]\n" + 
          "                  DocumentTextBox: 'Header '[X:0 Y:0 W:49 H:18 ](Length:7, StartRel:1)\n" + 
          "                LineBox: <entry>[X:0 Y:18 W:7 H:18 ]\n" + 
          "                  DocumentTextBox: '2'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:8)\n" + 
          "            TableCellBox: <entry>[X:0 Y:40 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:63 Y:40 W:53 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:62 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:63 Y:62 W:53 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:84 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:63 Y:84 W:53 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "", 
              dump.toString());
    }
   
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x + 50);
    flushAWTBetter();
    
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text<table frame=\"none\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"50pt\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"80.8pt\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Header 1</entry>\n" + 
        "                            <entry>Header 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "        </table></para>\n" + 
        "    </sect1>\n" + 
        "</article> ", 
        serializeDocumentViewport(vViewport, true).replaceAll("<\\?(.*?)\\?>\n", ""));

    
    rootBox = vViewport.getRootBox();
    dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    if(PlatformDetector.isWin32()) {
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:221 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:9 W:400 H:199 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:400 H:199 ]\n" + 
          "    BlockElementBox: <title>[X:0 Y:0 W:400 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:154 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:154 H:18 ]\n" + 
          "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
          "            StaticTextBox: 'Article: '[X:0 Y:0 W:63 H:18 ]\n" + 
          "          DocumentTextBox: 'Article Title'[X:63 Y:0 W:91 H:18 ](Length:13, StartRel:1)\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:28 W:400 H:171 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:175 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:175 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'Section1 Title'[X:77 Y:0 W:98 H:18 ](Length:14, StartRel:1)\n" + 
          "      BlockElementBox: <para>[X:12 Y:26 W:388 H:145 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:28 H:18 ]\n" + 
          "          LineBox: <para>[X:0 Y:0 W:28 H:18 ]\n" + 
          "            DocumentTextBox: 'Text'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
          "        BlockElementBox: <table>[X:4 Y:31 W:380 H:114 ]\n" + 
          "          BlockElementBox: <title>[X:14 Y:2 W:364 H:18 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:39 H:18 ]\n" + 
          "              LineBox: <title>[X:0 Y:0 W:39 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: 'title'[X:0 Y:0 W:39 H:18 ]\n" + 
          "          TableBox: <tgroup>[X:14 Y:24 W:164 H:88 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:0 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                  DocumentTextBox: 'Header 1'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:63 Y:0 W:101 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                  DocumentTextBox: 'Header 2'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:0 Y:22 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:63 Y:22 W:101 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:44 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:63 Y:44 W:101 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:66 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:63 Y:66 W:101 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "", 
              dump.toString());
    }
    
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 50);
    flushAWTBetter();
    
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text<table frame=\"none\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"50pt\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"42.4pt\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Header 1</entry>\n" + 
        "                            <entry>Header 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "        </table></para>\n" + 
        "    </sect1>\n" + 
        "</article> ", 
        serializeDocumentViewport(vViewport, true).replaceAll("<\\?(.*?)\\?>\n", ""));

    
    rootBox = vViewport.getRootBox();
    dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    if(PlatformDetector.isWin32()) {
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:239 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:9 W:400 H:217 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:400 H:217 ]\n" + 
          "    BlockElementBox: <title>[X:0 Y:0 W:400 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:154 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:154 H:18 ]\n" + 
          "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
          "            StaticTextBox: 'Article: '[X:0 Y:0 W:63 H:18 ]\n" + 
          "          DocumentTextBox: 'Article Title'[X:63 Y:0 W:91 H:18 ](Length:13, StartRel:1)\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:28 W:400 H:189 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:175 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:175 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'Section1 Title'[X:77 Y:0 W:98 H:18 ](Length:14, StartRel:1)\n" + 
          "      BlockElementBox: <para>[X:12 Y:26 W:388 H:163 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:28 H:18 ]\n" + 
          "          LineBox: <para>[X:0 Y:0 W:28 H:18 ]\n" + 
          "            DocumentTextBox: 'Text'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
          "        BlockElementBox: <table>[X:4 Y:31 W:380 H:132 ]\n" + 
          "          BlockElementBox: <title>[X:14 Y:2 W:364 H:18 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:39 H:18 ]\n" + 
          "              LineBox: <title>[X:0 Y:0 W:39 H:18 ]\n" + 
          "                EmptyInlineElemTextBox: 'title'[X:0 Y:0 W:39 H:18 ]\n" + 
          "          TableBox: <tgroup>[X:14 Y:24 W:116 H:106 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:0 W:63 H:40 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
          "                  DocumentTextBox: 'Header 1'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:63 Y:0 W:53 H:40 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:49 H:36 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:49 H:18 ]\n" + 
          "                  DocumentTextBox: 'Header '[X:0 Y:0 W:49 H:18 ](Length:7, StartRel:1)\n" + 
          "                LineBox: <entry>[X:0 Y:18 W:7 H:18 ]\n" + 
          "                  DocumentTextBox: '2'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:8)\n" + 
          "            TableCellBox: <entry>[X:0 Y:40 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:63 Y:40 W:53 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:62 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:63 Y:62 W:53 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:0 Y:84 W:63 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "            TableCellBox: <entry>[X:63 Y:84 W:53 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
          "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "", 
              dump.toString());
    }
  }

  /**
   * EXM-17445 Layout problem after resizing CALS table (scroll to caret after resize)
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeColumnWidthsEXM_17445() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <title>Article Title</title>\n" + 
      "    <sect1>\n" + 
      "        <title>Section1 Title</title>\n" + 
      "        <para>Text<table frame=\"none\">\n" + 
      "            <title/>\n" + 
      "            <tgroup cols=\"2\">\n" + 
      "                <colspec colname=\"c1\" colnum=\"1\" colwidth=\"50pt\"/>\n" + 
      "                <colspec colname=\"c2\" colnum=\"2\" colwidth=\"150.0pt\"/>\n" + 
      "                <thead>\n" + 
      "                    <row>\n" + 
      "                        <entry>Header 1</entry>\n" + 
      "                        <entry>Header 2</entry>\n" + 
      "                    </row>\n" + 
      "                </thead>\n" + 
      "                <tbody>\n" + 
      "                    <row>\n" + 
      "                        <entry/>\n" + 
      "                        <entry/>\n" + 
      "                    </row>\n" + 
      "                    <row>\n" + 
      "                        <entry/>\n" + 
      "                        <entry/>\n" + 
      "                    </row>\n" + 
      "                    <row>\n" + 
      "                        <entry/>\n" + 
      "                        <entry/>\n" + 
      "                    </row>\n" + 
      "                </tbody>\n" + 
      "            </tgroup>\n" + 
      "        </table></para>\n" + 
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "        <para>Text</para>\n" +
      "    </sect1>\n" + 
      "</article> ";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    vViewport.getHost().scrollRectToVisible(new Rectangle(0, 0, 1, 1));
    Thread.sleep(200);
    flushAWTBetter();
    int x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 200);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>Text<table frame=\"none\">\n" + 
        "            <title/>\n" + 
        "            <tgroup cols=\"2\">\n" + 
        "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"50pt\"/>\n" + 
        "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"42.4pt\"/>\n" + 
        "                    <thead>\n" + 
        "                        <row>\n" + 
        "                            <entry>Header 1</entry>\n" + 
        "                            <entry>Header 2</entry>\n" + 
        "                        </row>\n" + 
        "                    </thead>\n" + 
        "                    <tbody>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                        <row>\n" + 
        "                            <entry/>\n" + 
        "                            <entry/>\n" + 
        "                        </row>\n" + 
        "                    </tbody>\n" + 
        "                </tgroup>\n" + 
        "        </table></para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "        <para>Text</para>\n" + 
        "    </sect1>\n" + 
        "</article> ", 
        serializeDocumentViewport(vViewport, true));
    Thread.sleep(1000);
    Rectangle actualVisibleBounds = vViewport.getHost().getViewportBounds();
    CaretInfo caretShape = vViewport.getCaretShape();
    assertFalse(actualVisibleBounds.contains(caretShape.getBounds()));
  }

  /**
   * <p><b>Description:</b> Resize a column where the content is larger than the specified colspec</p>
   * <p><b>Bug ID:</b> EXM-19360</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testResizeColumnWidthEXM_19360() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<?xml-stylesheet type=\"text/css\" href=\"" + new File(".").toURI().toString() + "frameworks/dita/css_classed/hide_colspec.css\"?>\n" + 
      "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
      "<topic id=\"topic-1\">\n" + 
      "  <title>Topic title</title>\n" + 
      "  <body>\n" + 
      "    <table>\n" + 
      "      <tgroup cols=\"3\" colsep=\"1\" rowsep=\"1\">\n" + 
      "        <colspec colname=\"1\" colnum=\"1\" colwidth=\"2em\"/>\n" + 
      "        <colspec colname=\"2\" colnum=\"2\" colwidth=\"2em\"/>\n" + 
      "        <colspec colname=\"3\" colnum=\"3\" colwidth=\"*\"/>\n" + 
      "        <tbody>\n" + 
      "          <row>\n" + 
      "            <entry colname=\"1\">\n" + 
      "              <p>WWWW</p>\n" + 
      "            </entry>\n" + 
      "            <entry colname=\"2\"> t </entry>\n" + 
      "            <entry colname=\"3\"> txt </entry>\n" + 
      "          </row>\n" + 
      "        </tbody>\n" + 
      "      </tgroup>\n" + 
      "    </table>\n" + 
      "  </body>\n" + 
      "</topic>\n" + 
      "\n" + 
      "";
    
    initEditor(xml);
    flushAWTBetter();
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    flushAWTBetter();
    
    DumpConfiguration dumpConfig = new DumpConfiguration(false);
    dumpConfig.setReportWidth(true);
    StringBuilder string = new StringBuilder();
    vViewport.getRootBox().dump(string, dumpConfig, vViewport.createLayoutContext());
    
    flushAWTBetter();
    
    int x = sendMousePressed(vViewport, 1, true);
    flushAWTBetter();
    sendMouseReleased(vViewport, x + 4);
    flushAWTBetter();
    
    assertEquals( 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "  <title>Topic title</title>\n" + 
        "  <body>\n" + 
        "    <table>\n" + 
        "      <tgroup cols=\"3\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colname=\"1\" colnum=\"1\" colwidth=\"2.43em\"/>\n" + 
        "                <colspec colname=\"2\" colnum=\"2\" colwidth=\"1.86em\"/>\n" + 
        "                <colspec colname=\"3\" colnum=\"3\" colwidth=\"1*\"/>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"1\">\n" + 
        "                            <p>WWWW</p>\n" + 
        "                        </entry>\n" + 
        "                        <entry colname=\"2\"> t </entry>\n" + 
        "                        <entry colname=\"3\"> txt </entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "    </table>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "\n" + 
        "", 
        serializeDocumentViewport(vViewport, true).replaceAll("<\\?(.*?)\\?>\n", ""));
  }

  /**
   * <p><b>Description:</b> Resize a column where the content is larger than the specified colspec</p>
   * <p><b>Bug ID:</b> EXM-19360</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testResizeColumnWidthEXM_19360_2() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
      "<topic id=\"topic-1\">\n" + 
      "  <title>Topic title</title>\n" + 
      "  <body>\n" + 
      "    <table>\n" + 
      "      <tgroup cols=\"3\" colsep=\"1\" rowsep=\"1\">\n" + 
      "        <colspec colname=\"1\" colnum=\"1\" colwidth=\"2em\"/>\n" + 
      "        <colspec colname=\"2\" colnum=\"2\" colwidth=\"2em\"/>\n" + 
      "        <colspec colname=\"3\" colnum=\"3\" colwidth=\"*\"/>\n" + 
      "        <tbody>\n" + 
      "          <row>\n" + 
      "            <entry colname=\"1\">\n" + 
      "              <p>WWWW</p>\n" + 
      "            </entry>\n" + 
      "            <entry colname=\"2\"> t </entry>\n" + 
      "            <entry colname=\"3\"> txt </entry>\n" + 
      "          </row>\n" + 
      "        </tbody>\n" + 
      "      </tgroup>\n" + 
      "    </table>\n" + 
      "  </body>\n" + 
      "</topic>\n" + 
      "\n" + 
      "";
    
    initEditor(xml);
    flushAWTBetter();
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    flushAWTBetter();
    
    int x = sendMousePressed(vViewport, 2, true);
    flushAWTBetter();
    sendMouseReleased(vViewport, x + 50);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "  <title>Topic title</title>\n" + 
        "  <body>\n" + 
        "    <table>\n" + 
        "      <tgroup cols=\"3\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colname=\"1\" colnum=\"1\" colwidth=\"2.29em\"/>\n" + 
        "                <colspec colname=\"2\" colnum=\"2\" colwidth=\"5.43em\"/>\n" + 
        "                <colspec colname=\"3\" colnum=\"3\" colwidth=\"1*\"/>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"1\">\n" + 
        "                            <p>WWWW</p>\n" + 
        "                        </entry>\n" + 
        "                        <entry colname=\"2\"> t </entry>\n" + 
        "                        <entry colname=\"3\"> txt </entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "    </table>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "\n" + 
        "", 
        serializeDocumentViewport(vViewport, true));
  }
  
  /**
   * <p><b>Description:</b> Resizing a column from a table with dynamic column
   * widths will ask the user about the widths type (fixed or proportional)</p>
   * <p><b>Bug ID:</b> EXM-30747</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testResizeColumnWidthEXM_30747() throws Exception {
    final boolean[] askResult = new boolean[2];
    final int[] askReturn = new int[1];
    askReturn[0] = AuthorTableResizeDialogPresenter.ADD_PROPORTIONAL_COLUMN_WIDTHS;
    
    IDEAccess.setInstance(new SAIDEAccess() {
      /**
       * @see ro.sync.exml.SimpleSAIDEAccess#askUserToAddTableWidthAndColWidths(boolean)
       */
      @Override
      public int askUserToAddTableWidthAndColWidths(boolean onlyProportional) {
        askResult[0] = true;
        askResult[1] = onlyProportional;
        return askReturn[0];
      }
    });
    
    String initialXml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
      "<topic id=\"topic-1\">\n" + 
      "  <title>Topic title</title>\n" + 
      "  <body>\n" + 
      "    <table>\n" + 
      "      <tgroup cols=\"3\" colsep=\"1\" rowsep=\"1\">\n" + 
      "        <colspec colname=\"1\" colnum=\"1\"/>\n" + 
      "        <colspec colname=\"2\" colnum=\"2\"/>\n" + 
      "        <colspec colname=\"3\" colnum=\"3\"/>\n" + 
      "        <tbody>\n" + 
      "          <row>\n" + 
      "            <entry colname=\"1\">\n" + 
      "              <p>WWWW</p>\n" + 
      "            </entry>\n" + 
      "            <entry colname=\"2\"> t </entry>\n" + 
      "            <entry colname=\"3\"> txt </entry>\n" + 
      "          </row>\n" + 
      "        </tbody>\n" + 
      "      </tgroup>\n" + 
      "    </table>\n" + 
      "  </body>\n" + 
      "</topic>\n" + 
      "\n" + 
      "";
    
    // Use the old DITA CSS.
    initEditor(initialXml, true, true);
    flushAWTBetter();
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    flushAWTBetter();
    
    int x = sendMousePressed(vViewport, 2, true);
    flushAWTBetter();
    sendMouseReleased(vViewport, x + 50);
    flushAWTBetter();
    
    assertTrue(askResult[0]);
    assertFalse(askResult[1]);
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "  <title>Topic title</title>\n" + 
        "  <body>\n" + 
        "    <table>\n" + 
        "      <tgroup cols=\"3\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colname=\"1\" colnum=\"1\" colwidth=\"1*\"/>\n" + 
        "                <colspec colname=\"2\" colnum=\"2\" colwidth=\"1*\"/>\n" + 
        "                <colspec colname=\"3\" colnum=\"3\" colwidth=\"1*\"/>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"1\">\n" + 
        "                            <p>WWWW</p>\n" + 
        "                        </entry>\n" + 
        "                        <entry colname=\"2\"> t </entry>\n" + 
        "                        <entry colname=\"3\"> txt </entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "    </table>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "\n" + 
        "", 
        serializeDocumentViewport(vViewport, true));
    
    final CompoundEditUndoManager undoManager = vViewport.getController().getUndoManager();
    
    // Undo
    UiUtil.invokeSynchronously(new Runnable() {
      
      @Override
      public void run() {
        undoManager.undo();
      }
    });
    flushAWTBetter();
    
    // Check document
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "  <title>Topic title</title>\n" + 
        "  <body>\n" + 
        "    <table>\n" + 
        "      <tgroup cols=\"3\" colsep=\"1\" rowsep=\"1\">\n" + 
        "        <colspec colname=\"1\" colnum=\"1\"/>\n" + 
        "        <colspec colname=\"2\" colnum=\"2\"/>\n" + 
        "        <colspec colname=\"3\" colnum=\"3\"/>\n" + 
        "        <tbody>\n" + 
        "          <row>\n" + 
        "            <entry colname=\"1\">\n" + 
        "              <p>WWWW</p>\n" + 
        "            </entry>\n" + 
        "            <entry colname=\"2\"> t </entry>\n" + 
        "            <entry colname=\"3\"> txt </entry>\n" + 
        "          </row>\n" + 
        "        </tbody>\n" + 
        "      </tgroup>\n" + 
        "    </table>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "\n" + 
        "", serializeDocumentViewport(vViewport, true));
    
    //Add fixed column widths
    askResult[0] = false;
    askReturn[0] = AuthorTableResizeDialogPresenter.ADD_FIXED_COLUMN_WIDTHS;
    
    x = sendMousePressed(vViewport, 2, true);
    flushAWTBetter();
    sendMouseReleased(vViewport, x + 50);
    flushAWTBetter();
    
    assertTrue(askResult[0]);
    assertFalse(askResult[1]);
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "  <title>Topic title</title>\n" + 
        "  <body>\n" + 
        "    <table>\n" + 
        "      <tgroup cols=\"3\" colsep=\"1\" rowsep=\"1\">\n" + 
        "                <colspec colname=\"1\" colnum=\"1\" colwidth=\"96.8pt\"/>\n" + 
        "                <colspec colname=\"2\" colnum=\"2\" colwidth=\"96.8pt\"/>\n" + 
        "                <colspec colname=\"3\" colnum=\"3\" colwidth=\"96.8pt\"/>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"1\">\n" + 
        "                            <p>WWWW</p>\n" + 
        "                        </entry>\n" + 
        "                        <entry colname=\"2\"> t </entry>\n" + 
        "                        <entry colname=\"3\"> txt </entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "    </table>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "\n", 
        serializeDocumentViewport(vViewport, true));
    
  }

  /**
   * <p><b>Description:</b> One of the cells has a large min width which forces the width of the first column.
   * Despite of this, the table width should not increase much.</p>
   * <p><b>Bug ID:</b> EXM-26379</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testColumnMinWidthForcesColspecWidthEXM_26379() throws Exception {
    open(URLUtil.correct(new File("test/authorExtensions/EXM-26379.xml")), true);
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    
    // One of the cells has a large min width which forces the width of the first column.
    //Despite of this, the table width should not increase much.
    if(PlatformDetector.isWin32()){
      assertEquals( 
          "Please run this test on Linux!", 
              dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:480 H:154 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:8 W:480 H:133 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:480 H:133 ]\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:0 W:480 H:133 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:84 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:84 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'S'[X:77 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "      BlockElementBox: <para>[X:12 Y:31 W:468 H:102 ]\n" + 
          "        BlockElementBox: <table>[X:4 Y:0 W:460 H:102 ]\n" + 
          "          BlockElementBox: <title>[X:14 Y:2 W:364 H:18 ]\n" + 
          "            ParagraphBox[X:0 Y:0 W:7 H:18 ]\n" + 
          "              LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
          "                DocumentTextBox: 'T'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "          TableBox: <tgroup>[X:14 Y:24 W:444 H:76 ]\n" + 
          "            BlockPseudoElementBox: before[X:0 Y:0 W:444 H:30 ]\n" + 
          "              ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "                LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "                  StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], enableInReadOnlyContext=true, fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "            TableCellBox: <entry>[X:1 Y:31 W:102 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:98 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:98 H:18 ]\n" + 
          "                  DocumentTextBox: 'abcdefghijklmn'[X:0 Y:0 W:98 H:18 ](Length:14, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:103 Y:31 W:340 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:7 H:18 ]\n" + 
          "                  DocumentTextBox: 'b'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:1 Y:53 W:102 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:7 H:18 ]\n" + 
          "                  DocumentTextBox: 'c'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "            TableCellBox: <entry>[X:103 Y:53 W:340 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "                LineBox: <entry>[X:0 Y:0 W:7 H:18 ]\n" + 
          "                  DocumentTextBox: 'd'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#getFixedCSSsToUse()
   */
  @Override
  protected CSSResource[] getFixedCSSsToUse() {
    return fixedCSSsToUse;
  }
  
  private CSSResource[] fixedCSSsToUse = null; 

  /**
   * <p><b>Description:</b> Obey alignment of colspecs in cells content.</p>
   * <p><b>Bug ID:</b> EXM-9928</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testObeyColspecAlignsInCellContentEXM_9928() throws Exception {
    open(URLUtil.correct(new File("test/authorExtensions/EXM-26379-4.xml")), true);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    final RootBox rootBox = vViewport.getRootBox();
    
    // Don't print minimum and maximum width 
    final DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    final StringBuilder dump = new StringBuilder();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
      }
    });
    
    //The first column should be right aligned and the second should be justified.
    if(PlatformDetector.isWin32()){
      assertEquals( 
          "Please run this test on Linux!", 
              dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:135 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:8 W:400 H:114 ]\n" + 
          "  BlockElementBox: <topic>[X:0 Y:0 W:400 H:114 ]\n" + 
          "    BlockElementBox: <title>[X:7 Y:0 W:393 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:7 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
          "          DocumentTextBox: 'A'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "    BlockElementBox: <body>[X:7 Y:32 W:393 H:82 ]\n" + 
          "      BlockElementBox: <table>[X:4 Y:0 W:385 H:82 ]\n" + 
          "        BlockElementBox: <title>[X:14 Y:2 W:369 H:18 ]\n" + 
          "          ParagraphBox[X:14 Y:0 W:7 H:18 ]\n" + 
          "            LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: 'T'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableBox: <tgroup>[X:14 Y:24 W:369 H:56 ]\n" + 
          "          TableCellBox: <entry>[X:1 Y:1 W:184 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:180 H:18 ]\n" + 
          "              LineBox: <entry>[X:145 Y:0 W:35 H:18 ]\n" + 
          "                DocumentTextBox: 'text1'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:185 Y:1 W:183 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:107 H:18 ]\n" + 
          "              LineBox: <entry>[X:72 Y:0 W:35 H:18 ]\n" + 
          "                DocumentTextBox: 'text2'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:1 Y:23 W:184 H:32 ]\n" + 
          "            BlockElementBox: <p>[X:2 Y:7 W:180 H:18 ]\n" + 
          "              ParagraphBox[X:0 Y:0 W:180 H:18 ]\n" + 
          "                LineBox: <p>[X:145 Y:0 W:35 H:18 ]\n" + 
          "                  DocumentTextBox: 'text3'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:185 Y:23 W:183 H:32 ]\n" + 
          "            ParagraphBox[X:2 Y:7 W:107 H:18 ]\n" + 
          "              LineBox: <entry>[X:72 Y:0 W:35 H:18 ]\n" + 
          "                DocumentTextBox: 'text4'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
    
    //Edit in one of the cells, the alignment should be preserved.
    moveCaretRelativeTo("text4", "text4".length());
    sendString("a");
    
    
    //Dump again
    dump.setLength(0);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
      }
    });

    //The alignment should be preserved.
    if(PlatformDetector.isWin32()){
      assertEquals( 
          "Please run this test on Linux!", 
              dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:135 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:8 W:400 H:114 ]\n" + 
          "  BlockElementBox: <topic>[X:0 Y:0 W:400 H:114 ]\n" + 
          "    BlockElementBox: <title>[X:7 Y:0 W:393 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:7 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
          "          DocumentTextBox: 'A'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "    BlockElementBox: <body>[X:7 Y:32 W:393 H:82 ]\n" + 
          "      BlockElementBox: <table>[X:4 Y:0 W:385 H:82 ]\n" + 
          "        BlockElementBox: <title>[X:14 Y:2 W:369 H:18 ]\n" + 
          "          ParagraphBox[X:14 Y:0 W:7 H:18 ]\n" + 
          "            LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: 'T'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableBox: <tgroup>[X:14 Y:24 W:369 H:56 ]\n" + 
          "          TableCellBox: <entry>[X:1 Y:1 W:184 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:180 H:18 ]\n" + 
          "              LineBox: <entry>[X:145 Y:0 W:35 H:18 ]\n" + 
          "                DocumentTextBox: 'text1'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:185 Y:1 W:183 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:107 H:18 ]\n" + 
          "              LineBox: <entry>[X:72 Y:0 W:35 H:18 ]\n" + 
          "                DocumentTextBox: 'text2'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:1 Y:23 W:184 H:32 ]\n" + 
          "            BlockElementBox: <p>[X:2 Y:7 W:180 H:18 ]\n" + 
          "              ParagraphBox[X:0 Y:0 W:180 H:18 ]\n" + 
          "                LineBox: <p>[X:145 Y:0 W:35 H:18 ]\n" + 
          "                  DocumentTextBox: 'text3'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:185 Y:23 W:183 H:32 ]\n" + 
          "            ParagraphBox[X:2 Y:7 W:110 H:18 ]\n" + 
          "              LineBox: <entry>[X:68 Y:0 W:42 H:18 ]\n" + 
          "                DocumentTextBox: 'text4a'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
    
    moveCaretRelativeTo("text4", "text4".length());
    final AuthorElement cellText4 = (AuthorElement) vViewport.getController().getSentinelNodeAt(vViewport.getCaretOffset());
    //Set imposed alignment to one of the cells
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          vViewport.getController().setAttribute("align", new AttrValue("right"), cellText4);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    
    //Dump again
    dump.setLength(0);
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
      }
    });
  
    //The 4-th cell should now be right aligned.
    if(PlatformDetector.isWin32()){
      assertEquals( 
          "Please run this test on Linux!", 
              dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:400 H:135 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:8 W:400 H:114 ]\n" + 
          "  BlockElementBox: <topic>[X:0 Y:0 W:400 H:114 ]\n" + 
          "    BlockElementBox: <title>[X:7 Y:0 W:393 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:7 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
          "          DocumentTextBox: 'A'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "    BlockElementBox: <body>[X:7 Y:32 W:393 H:82 ]\n" + 
          "      BlockElementBox: <table>[X:4 Y:0 W:385 H:82 ]\n" + 
          "        BlockElementBox: <title>[X:14 Y:2 W:369 H:18 ]\n" + 
          "          ParagraphBox[X:14 Y:0 W:7 H:18 ]\n" + 
          "            LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: 'T'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableBox: <tgroup>[X:14 Y:24 W:369 H:56 ]\n" + 
          "          TableCellBox: <entry>[X:1 Y:1 W:184 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:180 H:18 ]\n" + 
          "              LineBox: <entry>[X:145 Y:0 W:35 H:18 ]\n" + 
          "                DocumentTextBox: 'text1'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:185 Y:1 W:183 H:22 ]\n" + 
          "            ParagraphBox[X:2 Y:2 W:107 H:18 ]\n" + 
          "              LineBox: <entry>[X:72 Y:0 W:35 H:18 ]\n" + 
          "                DocumentTextBox: 'text2'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:1 Y:23 W:184 H:32 ]\n" + 
          "            BlockElementBox: <p>[X:2 Y:7 W:180 H:18 ]\n" + 
          "              ParagraphBox[X:0 Y:0 W:180 H:18 ]\n" + 
          "                LineBox: <p>[X:145 Y:0 W:35 H:18 ]\n" + 
          "                  DocumentTextBox: 'text3'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "          TableCellBox: <entry>[X:185 Y:23 W:183 H:32 ]\n" + 
          "            ParagraphBox[X:2 Y:7 W:179 H:18 ]\n" + 
          "              LineBox: <entry>[X:137 Y:0 W:42 H:18 ]\n" + 
          "                DocumentTextBox: 'text4a'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  

  /**
   * <p><b>Description:</b> Resizing a column in an right-to-left (RTL) table. Dragging from the left 
   * side of a cell should increase that cells width, because the left side is the end side of the cell.</p>
   * <p><b>Bug ID:</b> EXM-27146</p>
   *
   * @author dan
   *
   * @throws Exception
   */
  public void testResizeColumnRTL() throws Exception {
    String xml =
      "<?xml version='1.0' encoding='UTF-8'?>\n" + 
      "<!DOCTYPE topic PUBLIC '-//OASIS//DTD DITA Topic//EN' 'http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd'>\n" + 
      "<topic id='topic-1'>\n" + 
      "  <title>Topic title</title>\n" + 
      "  <body>\n" + 
      "    <table dir='rtl'>\n" + 
      "      <tgroup cols='3' colsep='1' rowsep='1'>\n" + 
      "        <colspec colname='1' colnum='1' colwidth='100px'/>\n" + 
      "        <colspec colname='2' colnum='2' colwidth='100px'/>\n" + 
      "        <colspec colname='3' colnum='3' colwidth='100px'/>\n" + 
      "        <tbody>\n" + 
      "          <row>\n" + 
      "            <entry colname='1'> alpha </entry>\n" + 
      "            <entry colname='2'> beta </entry>\n" + 
      "            <entry colname='3'> gamma </entry>\n" + 
      "          </row>\n" + 
      "        </tbody>\n" + 
      "      </tgroup>\n" + 
      "    </table>\n" + 
      "  </body>\n" + 
      "</topic>\n" + 
      "\n" + 
      "";
    
    initEditor(xml);
    flushAWTBetter();
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    flushAWTBetter();
    
    DumpConfiguration dumpConfig = new DumpConfiguration(false);
    dumpConfig.setReportWidth(true);
    StringBuilder string = new StringBuilder();
    vViewport.getRootBox().dump(string, dumpConfig, vViewport.createLayoutContext());
    flushAWTBetter();
    

    // The cells are arranged like:
    // "gamma", "beta", "alpha"
    // We'll drag the line between the "beta" and "alpha" and move it to the left.
    // This means, in the RTL context, that we enlarge the first column.
    
    int x = sendMousePressed(vViewport, 1, false);
    flushAWTBetter();
    sendMouseReleased(vViewport, x - 20);
    flushAWTBetter();
    

    assertEquals( 
        "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<!DOCTYPE topic PUBLIC '-//OASIS//DTD DITA Topic//EN' 'http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd'>\n" + 
        "<topic id='topic-1'>\n" + 
        "  <title>Topic title</title>\n" + 
        "  <body>\n" + 
        "    <table dir='rtl'>\n" + 
        "      <tgroup cols=\"3\" colsep=\"1\" rowsep=\"1\">\n" + 
        // Is not quite 20px as we moved, there are some 'corrections' in the sendMousePressed method.       
        "                <colspec colname=\"1\" colnum=\"1\" colwidth=\"118px\"/>\n" + 
        "                <colspec colname=\"2\" colnum=\"2\" colwidth=\"100px\"/>\n" + 
        "                <colspec colname=\"3\" colnum=\"3\" colwidth=\"100px\"/>\n" + 
        "                <tbody>\n" + 
        "                    <row>\n" + 
        "                        <entry colname=\"1\"> alpha </entry>\n" + 
        "                        <entry colname=\"2\"> beta </entry>\n" + 
        "                        <entry colname=\"3\"> gamma </entry>\n" + 
        "                    </row>\n" + 
        "                </tbody>\n" + 
        "            </tgroup>\n" + 
        "    </table>\n" + 
        "  </body>\n" + 
        "</topic>\n" + 
        "\n" + 
        "", 
        serializeDocumentViewport(vViewport, true));
  }
  
  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#getAdditionalCSSsToUse()
   */
  @Override
  protected CSSResource[] getAdditionalCSSsToUse() {
    String content = "body{\n" + 
        "   margin:0px !important;\n" + 
        "}";
    File tmp;
    try {
      tmp = File.createTempFile("test", ".css");
      tmp.deleteOnExit();
      FileWriter fw = new FileWriter(tmp);
      fw.write(content);
      fw.close();
      
      CSSResource res = new CSSResource(URLUtil.correct(tmp).toString(),  IStyleSheet.SOURCE_DOCUMENT_TYPE);
      return new CSSResource[]{res};
    } catch (IOException e) {
      e.printStackTrace();
    }
    return super.getAdditionalCSSsToUse();
  }
  

  /**
   * <p><b>Description:</b> Tests how the column separators are taken into account when 
   * defined on cells, rows, or in the colspecs.</p>
   * <p><b>Bug ID:</b> EXM-18190</p>
   *
   * @author dan
   *
   * @throws Exception
   */
  public void testColSep() throws Exception {
    String xml = 
      "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
      "<topic id=\"topic-1\">\n" + 
      "  <title>Topic title</title>\n" + 
      "  <body>\n" + 
      "    <p>Topic paragraph</p>\n" +
      ///////////////////////////////////////////////
      "    <table colsep='0' rowsep='0'>\n" + 
      "    <title>Attributes on rows and cells.</title>\n" + 
      "      <tgroup cols=\"3\">\n" + 
      "        <tbody>\n" + 
      "            <row>" +
      // Completely specified.
      "               <entry rowsep='1' colsep='1'>C1</entry>" +     
      "               <entry>C2</entry>" +
      "               <entry>C3</entry>" +
      "             </row>\n" + 
      "            <row colsep='1'>" +
      // Inherit from row.
      "               <entry>C4</entry>" +
      "               <entry>C5</entry>" +
      "               <entry>C6</entry>" +
      "            </row>\n" + 
      "            <row rowsep='1'>" +
      // Inherit from row.
      "               <entry>C7</entry>" +
      "               <entry>C8</entry>" +
      "               <entry colsep='1'>C9</entry>" +
      "            </row>\n" + 
      "        </tbody>\n" + 
      "      </tgroup>\n" + 
      "    </table>" + 
      //////////////////////////////////////////////
      "    <table colsep='1' rowsep='1'>\n" + 
      "    <title>Attributes on table.</title>\n" + 
      "      <tgroup cols=\"3\" >\n" + 
      "        <tbody>\n" + 
      "            <row>" +
      // Specified in the table.
      "               <entry>C1</entry>" +     
      "             </row>\n" + 
      "        </tbody>\n" + 
      "      </tgroup>\n" + 
      "    </table>" + 
      //////////////////////////////////////////////
      "    <table colsep='0' rowsep='0'>\n" + 
      "    <title>Attributes on colspecs.</title>\n" +
      "      <colspec colname='c1' colnum='1' colsep='1'/>\n" + 
      "      <colspec colname='c2' colnum='2' rowsep='1'/>\n" + 
      "      <tgroup cols=\"3\" >\n" + 
      "        <tbody>\n" + 
      "            <row>" +
      // Specified in the colspec.
      "               <entry>C1</entry>" +     
      "               <entry>C2</entry>" +     
      "               <entry>C3</entry>" +     
      "            </row>\n" + 
      "            <row>" +
      // Specified in the colspec.
      "               <entry>C4</entry>" +     
      "               <entry>C5</entry>" +     
      "               <entry>C6</entry>" +     
      "            </row>\n" + 
      "            <row>" +
      // Overwritten by an attribute.
      "               <entry colsep='0'>C7</entry>" +     
      "               <entry>C8</entry>" +     
      "               <entry>C9</entry>" +     
      "            </row>\n" + 
      "        </tbody>\n" + 
      "      </tgroup>\n" + 
      "    </table>" + 
      "    <simpletable>\n" + 
      "      <strow>\n" + 
      "         <stentry/>\n" + 
      "         <stentry/>\n" + 
      "      </strow>\n" + 
      "    </simpletable>\n" + 
      //////////////////////////////////////////////
      // The same behavior for the attributes set on tgroup or table elements.
      // Should be less important than the attributes set on cells or on colspecs.
      "    <table >\n" + 
      "    <title>Attributes on colspecs.</title>\n" +
      "      <colspec colname='c1' colnum='1' colsep='1'/>\n" + 
      "      <colspec colname='c2' colnum='2' rowsep='1'/>\n" + 
      "      <tgroup cols=\"3\" colsep='0' rowsep='0'>\n" + 
      "        <tbody>\n" + 
      "            <row>" +
      // Specified in the colspec.
      "               <entry>C1</entry>" +     
      "               <entry>C2</entry>" +     
      "               <entry>C3</entry>" +     
      "            </row>\n" + 
      "            <row>" +
      // Specified in the colspec.
      "               <entry>C4</entry>" +     
      "               <entry>C5</entry>" +     
      "               <entry>C6</entry>" +     
      "            </row>\n" + 
      "            <row>" +
      // Overwritten by an attribute.
      "               <entry colsep='0'>C7</entry>" +     
      "               <entry>C8</entry>" +     
      "               <entry>C9</entry>" +     
      "            </row>\n" + 
      "        </tbody>\n" + 
      "      </tgroup>\n" + 
      "    </table>" + 
      "  </body>\n" + 
      "</topic>";
    initEditor(xml);    
    flushAWTBetter();
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();


    /////////////////////////////////TABLE 1///////////////////////////////////
    TableBox tableBox = (TableBox) findBox(TableBox.class, 1, vViewport.getRootBox());
    CALSTableCellInfoProvider tableSupport =  new CALSTableCellInfoProvider();

    // The TableBox is associated to the "tgroup".
    tableSupport.init((AuthorElement) tableBox.getElement().getParent());
    // Row 1.
    // C1.
    TableCellBox cell = (TableCellBox) findBox(TableCellBox.class, 1, tableBox);
    boolean colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    boolean rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 1);
    assertTrue("Colsep attribute is present on the cell.", colsep);
    assertTrue("Rowsep attribute is present on the cell.", rowsep);

    // C2.
    cell = (TableCellBox) findBox(TableCellBox.class, 2, tableBox);
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 2);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 2);
    assertFalse("Colsep attribute is not present on the cell.", colsep);
    assertFalse("Rowsep attribute is not present on the cell.", rowsep);

    // C3.
    cell = (TableCellBox) findBox(TableCellBox.class, 3, tableBox);
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 3);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 3);
    assertFalse("Colsep attribute is not present on the cell.", colsep);
    assertFalse("Rowsep attribute is not present on the cell.", rowsep);


    // Row 2
    // C4.
    cell = (TableCellBox) findBox(TableCellBox.class, 4, tableBox);
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 1);
    assertTrue("Colsep attribute is present on the cell.", colsep);
    assertFalse("Rowsep attribute is not present on the cell.", rowsep);

    // C5.
    cell = (TableCellBox) findBox(TableCellBox.class, 5, tableBox);
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 2);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 2);
    assertTrue("Colsep attribute is present on the cell.", colsep);
    assertFalse("Rowsep attribute is not present on the cell.", rowsep);

    // C6.
    cell = (TableCellBox) findBox(TableCellBox.class, 6, tableBox);
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 3);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 3);
    assertTrue("Colsep attribute is present on the cell.", colsep);
    assertFalse("Rowsep attribute is not present on the cell.", rowsep);


    // Row 3
    // C7.
    cell = (TableCellBox) findBox(TableCellBox.class, 7, tableBox);
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 1);
    assertFalse("Colsep attribute is not present on the cell.", colsep);
    assertTrue("Rowsep attribute is present on the cell.", rowsep);

    // C8.
    cell = (TableCellBox) findBox(TableCellBox.class, 8, tableBox);
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 2);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 2);
    assertFalse("Colsep attribute is not present on the cell.", colsep);
    assertTrue("Rowsep attribute is present on the cell.", rowsep);

    // C9.
    cell = (TableCellBox) findBox(TableCellBox.class, 9, tableBox);
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 3);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 3);
    assertTrue("Colsep attribute is present on the cell.", colsep);
    assertTrue("Rowsep attribute is present on the cell.", rowsep);
    
    assertFalse("Because is in the last row, even if the rowsep attribute is true, " +
    		"it should not paint the separator.", tableBox.shouldPaintRowSep(tableSupport, cell, 3, 3));
    assertFalse("Because is in the last column, even if the colsep attribute is true, " +
        "it should not paint the separator.", tableBox.shouldPaintColSep(tableSupport, cell, 3, 3));
    
    /////////////////////////////////TABLE 2///////////////////////////////////
    tableBox = (TableBox) findBox(TableBox.class, 2, vViewport.getRootBox());
    tableSupport = new CALSTableCellInfoProvider();
    // The TableBox is associated to the "tgroup".
    tableSupport.init((AuthorElement) tableBox.getElement().getParent());

    // Row 1.
    // C1.
    cell = (TableCellBox) findBox(TableCellBox.class, 1, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 1);
    assertTrue("Colsep attribute is present on the table.", colsep);
    assertTrue("Rowsep attribute is present on the table.", rowsep);
    

    /////////////////////////////////TABLE 3///////////////////////////////////
    tableBox = (TableBox) findBox(TableBox.class, 3, vViewport.getRootBox());
    tableSupport = new CALSTableCellInfoProvider();
    // The TableBox is associated to the "tgroup".
    tableSupport.init((AuthorElement) tableBox.getElement().getParent());
    // Row 1.
    // C1.
    cell = (TableCellBox) findBox(TableCellBox.class, 1, tableBox);

    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 1);
    assertTrue("Colsep attribute is in the colspan.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);
    
    

    // C2.
    cell = (TableCellBox) findBox(TableCellBox.class, 2, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 2);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 2);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertTrue("Rowsep attribute is in the colspan.", rowsep);

    // C3.
    cell = (TableCellBox) findBox(TableCellBox.class, 3, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 3);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 3);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);

    // Row 2.
    // C4.
    cell = (TableCellBox) findBox(TableCellBox.class, 4, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    assertTrue("Colsep attribute is in the colspan.", colsep);

    assertTrue("Colsep attribute is in the colspan.", tableBox.shouldPaintColSep(tableSupport, cell, 1, 0));
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);

    // C5.
    cell = (TableCellBox) findBox(TableCellBox.class, 5, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 2);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 2);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertFalse("Colsep attribute is in the colspan.", tableBox.shouldPaintColSep(tableSupport, cell, 1, 0));
    assertTrue("Rowsep attribute is in the colspan.", rowsep);

    // C6.
    cell = (TableCellBox) findBox(TableCellBox.class, 6, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 3);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 3);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);


    // Row 3.
    // C7.
    cell = (TableCellBox) findBox(TableCellBox.class, 7, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 1);
    assertFalse("Colsep attribute set on the cell is more important than the one set on the colspec.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);

    // C8.
    cell = (TableCellBox) findBox(TableCellBox.class, 8, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 2);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 2);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertTrue("Rowsep attribute is in the colspan.", rowsep);

    // C9.
    cell = (TableCellBox) findBox(TableCellBox.class, 9, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 3);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 3);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);
    
    /////////////////////////////////SIMPELTABLE 1///////////////////////////////////
    tableBox = (TableBox) findBox(TableBox.class, 4, vViewport.getRootBox());
    DITATableCellSepInfoProvider dtSupport =  new DITATableCellSepInfoProvider();

    dtSupport.init((AuthorElement) tableBox.getElement().getParent());
    cell = (TableCellBox) findBox(TableCellBox.class, 1, tableBox);
    colsep = dtSupport.getColSep((AuthorElement) cell.getElement(), 1);
    rowsep = dtSupport.getRowSep((AuthorElement) cell.getElement(), 1);
    assertTrue("For simple table colsep should be always true.", colsep);
    assertTrue("For simple table rowsep should be always true.", rowsep);    
    

    /////////////////////////////////TABLE 4///////////////////////////////////
    tableBox = (TableBox) findBox(TableBox.class, 5, vViewport.getRootBox());
    tableSupport = new CALSTableCellInfoProvider();
    // The TableBox is associated to the "tgroup".
    tableSupport.init((AuthorElement) tableBox.getElement().getParent());
    // Row 1.
    // C1.
    cell = (TableCellBox) findBox(TableCellBox.class, 1, tableBox);

    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 1);
    assertTrue("Colsep attribute is in the colspan.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);
    
    

    // C2.
    cell = (TableCellBox) findBox(TableCellBox.class, 2, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 2);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 2);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertTrue("Rowsep attribute is in the colspan.", rowsep);

    // C3.
    cell = (TableCellBox) findBox(TableCellBox.class, 3, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 3);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 3);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);

    // Row 2.
    // C4.
    cell = (TableCellBox) findBox(TableCellBox.class, 4, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    assertTrue("Colsep attribute is in the colspan.", colsep);

    assertTrue("Colsep attribute is in the colspan.", tableBox.shouldPaintColSep(tableSupport, cell, 1, 0));
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);

    // C5.
    cell = (TableCellBox) findBox(TableCellBox.class, 5, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 2);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 2);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertFalse("Colsep attribute is in the colspan.", tableBox.shouldPaintColSep(tableSupport, cell, 1, 0));
    assertTrue("Rowsep attribute is in the colspan.", rowsep);

    // C6.
    cell = (TableCellBox) findBox(TableCellBox.class, 6, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 3);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 3);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);


    // Row 3.
    // C7.
    cell = (TableCellBox) findBox(TableCellBox.class, 7, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 1);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 1);
    assertFalse("Colsep attribute set on the cell is more important than the one set on the colspec.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);

    // C8.
    cell = (TableCellBox) findBox(TableCellBox.class, 8, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 2);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 2);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertTrue("Rowsep attribute is in the colspan.", rowsep);

    // C9.
    cell = (TableCellBox) findBox(TableCellBox.class, 9, tableBox);
    
    colsep = tableSupport.getColSep((AuthorElement) cell.getElement(), 3);
    rowsep = tableSupport.getRowSep((AuthorElement) cell.getElement(), 3);
    assertFalse("Colsep attribute is not in the colspan.", colsep);
    assertFalse("Rowsep attribute is not in the colspan.", rowsep);
    
  }
}