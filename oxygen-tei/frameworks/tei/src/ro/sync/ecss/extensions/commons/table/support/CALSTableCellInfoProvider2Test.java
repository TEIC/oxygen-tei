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

import ro.sync.ecss.component.AuthorViewport;
import ro.sync.ecss.css.IStyleSheet;
import ro.sync.ecss.dom.AuthorSentinelNode;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.layout.DumpConfiguration;
import ro.sync.ecss.layout.ParagraphBox;
import ro.sync.ecss.layout.RootBox;
import ro.sync.ecss.layout.TableCellBox;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.css.CSSResource;
import ro.sync.io.IOUtil;
import ro.sync.util.PlatformDetector;
import ro.sync.util.URLUtil;

/**
 * Test that the cell spanning is computed correctly.
 * @author iulian_velea
 */
public class CALSTableCellInfoProvider2Test extends EditorAuthorExtensionTestBase {
  
  /**
   * Constructor.
   */
  public CALSTableCellInfoProvider2Test() {
    super(false, true);
    // Activate screen-shots on fail
    activateScreenshotsOnTestFail();
  }
  
  /**
   * @see ro.sync.ecss.extensions.EditorAuthorExtensionTestBase#getFixedCSSsToUse()
   */
  @Override
  protected CSSResource[] getFixedCSSsToUse() {
    return fixedCSSsToUse;
  }
  
  /**
   * <p><b>Description:</b> If we have valign set on a <thead> it should be used.</p>
   * <p><b>Bug ID:</b> EXM-30149</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testObeyValignInTheadEXM_30149() throws Exception {
    try {
      //Add a new selector to the Docbook CSS
      File backup = new File("frameworks/docbook/css/docbook_CUSTOM.css");
      backup.delete();
      backup.deleteOnExit();
      String content = IOUtil.readFile(new File("frameworks/docbook/css/docbook.css"));
      content += IOUtil.readFile(new File("frameworks/docbook/css/hide_colspec.css"));
      content += "\nentry{\n" + 
          "    foldable:true;\n" + 
          "}";
      FileWriter fw = new FileWriter(backup);
      fw.write(content);
      fw.close();

      CSSResource res = new CSSResource(URLUtil.correct(backup).toString(),  IStyleSheet.SOURCE_DOCUMENT_TYPE);
      fixedCSSsToUse = new CSSResource[] {res};

      open(URLUtil.correct(new File("test/authorExtensions/EXM-26379-2-valign.xml")), true);
      
      moveCaretRelativeTo("BOTTOM", 1);

      AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
      
      TableCellBox box = (TableCellBox) vViewport.getNearestLayoutBox((AuthorSentinelNode) vViewport.getController().getNodeAtOffset(vViewport.getCaretOffset()));
      ParagraphBox pb = (ParagraphBox) box.getChildren()[0];
      assertTrue("The paragraph should be aligned to bottom", pb.getY() > 15);
    } finally {
      fixedCSSsToUse = null;
    }
  }

  /**
   * <p><b>Description:</b> Edit content in a centered paragraph. The edited para should remain centered.</p>
   * <p><b>Bug ID:</b> EXM-26379</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testColumnMinWidthForcesColspecAndModifyWidthEXM_26379_3() throws Exception {
    try {
      //Add a new selector to the Docbook CSS
      File backup = new File("frameworks/dita/css_classed/dita_CUSTOM.css");
      backup.delete();
      backup.deleteOnExit();
      String content = IOUtil.readFile(new File("frameworks/dita/css_classed/hide_colspec.css"));
      content += "\np{\n" + 
          "    text-align:center;\n" + 
          "}";
      FileWriter fw = new FileWriter(backup);
      fw.write(content);
      fw.close();

      CSSResource res = new CSSResource(URLUtil.correct(backup).toString(),  IStyleSheet.SOURCE_DOCUMENT_TYPE);
      fixedCSSsToUse = new CSSResource[] {res};  

      open(URLUtil.correct(new File("test/authorExtensions/EXM-26379-3.xml")), true);

      //Enlarge the cell by adding chars which cannot be split on multiple lines.
      moveCaretRelativeTo("text", "text".length() + 2);
      sendString("abcdef");

      AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
      RootBox rootBox = vViewport.getRootBox();
      StringBuilder dump = new StringBuilder();

      // Don't print minimum and maximum width 
      DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
      dumpConfiguration.setReportMaximumWidth(false);
      dumpConfiguration.setReportMinimumWidth(false);
      dumpConfiguration.setReportOffsets(false);    

      rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());

      //There is a cell called "aaaaa"
      //After the modification in a cell on another row, the cell left to it (N1,N2,M2) 
      //has more space and will span on one row (it spanned on two rows before this).
      //So the cell "aaaaa" will also layout because it has vertical alignment
      if(PlatformDetector.isWin32()){
        assertEquals( 
            "Please run this test on Linux!", 
                dump.toString());
      } else {
        assertEquals( 
            "<RootBox>[X:0 Y:0 W:400 H:204 ]\n" + 
            "BlockElementBox: <#document>[X:0 Y:8 W:400 H:183 ]\n" + 
            "  BlockElementBox: <topic>[X:0 Y:0 W:400 H:183 ]\n" + 
            "    BlockElementBox: <title>[X:7 Y:0 W:393 H:19 ]\n" + 
            "      ParagraphBox[X:0 Y:0 W:7 H:18 ]\n" + 
            "        LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
            "          DocumentTextBox: 'A'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
            "    BlockElementBox: <body>[X:7 Y:32 W:393 H:151 ]\n" + 
            "      BlockElementBox: <table>[X:4 Y:0 W:385 H:151 ]\n" + 
            "        BlockElementBox: <title>[X:14 Y:2 W:369 H:18 ]\n" + 
            "          ParagraphBox[X:14 Y:0 W:7 H:18 ]\n" + 
            "            LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
            "              DocumentTextBox: 'T'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
            "        TableBox: <tgroup>[X:14 Y:24 W:369 H:125 ]\n" + 
            "          TableCellBox: <entry>[X:1 Y:1 W:184 H:22 ]\n" + 
            "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
            "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
            "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "          TableCellBox: <entry>[X:185 Y:1 W:183 H:22 ]\n" + 
            "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
            "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
            "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "          TableCellBox: <entry>[X:1 Y:23 W:184 H:57 ]\n" + 
            "            BlockElementBox: <p>[X:2 Y:7 W:180 H:18 ]\n" + 
            "              ParagraphBox[X:0 Y:0 W:104 H:18 ]\n" + 
            "                LineBox: <p>[X:76 Y:0 W:28 H:18 ]\n" + 
            "                  DocumentTextBox: 'text'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
            "            BlockElementBox: <p>[X:2 Y:32 W:179 H:18 ]\n" + 
            "              ParagraphBox[X:0 Y:0 W:110 H:18 ]\n" + 
            "                LineBox: <p>[X:68 Y:0 W:42 H:18 ]\n" + 
            "                  DocumentTextBox: 'abcdef'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
            "          TableCellBox: <entry>[X:185 Y:23 W:183 H:57 ]\n" + 
            "            ParagraphBox[X:2 Y:7 W:18 H:18 ]\n" + 
            "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
            "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "          TableCellBox: <entry>[X:1 Y:80 W:184 H:22 ]\n" + 
            "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
            "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
            "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "          TableCellBox: <entry>[X:185 Y:80 W:183 H:22 ]\n" + 
            "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
            "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
            "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "          TableCellBox: <entry>[X:1 Y:102 W:184 H:22 ]\n" + 
            "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
            "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
            "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "          TableCellBox: <entry>[X:185 Y:102 W:183 H:22 ]\n" + 
            "            ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
            "              LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
            "                EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "", 
                dump.toString());
      }
    } finally {
      fixedCSSsToUse = null;
    }
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
  public void testColumnMinWidthForcesColspecAndModifyWidthEXM_26379_2() throws Exception {
    try {
      //Add a new selector to the Docbook CSS
      File backup = new File("frameworks/docbook/css/docbook_CUSTOM.css");
      backup.delete();
      backup.deleteOnExit();
      String content = IOUtil.readFile(new File("frameworks/docbook/css/docbook.css"));
      content += IOUtil.readFile(new File("frameworks/docbook/css/hide_colspec.css"));
      content += "\nentry{\n" + 
          "    foldable:true;\n" + 
          "}";
      FileWriter fw = new FileWriter(backup);
      fw.write(content);
      fw.close();
  
      CSSResource res = new CSSResource(URLUtil.correct(backup).toString(),  IStyleSheet.SOURCE_DOCUMENT_TYPE);
      fixedCSSsToUse = new CSSResource[] {res};
  
      open(URLUtil.correct(new File("test/authorExtensions/EXM-26379-noValign.xml")), true);
  
      //Enlarge the cell by adding chars which cannot be split on multiple lines.
      moveCaretRelativeTo("M6,L6", 0);
      sendString("defhi");
  
      AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
      RootBox rootBox = vViewport.getRootBox();
      StringBuilder dump = new StringBuilder();
  
      // Don't print minimum and maximum width 
      DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
      dumpConfiguration.setReportMaximumWidth(false);
      dumpConfiguration.setReportMinimumWidth(false);
      dumpConfiguration.setReportOffsets(false);    
  
      rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
  
      //There is a cell called "aaaaa"
      //After the modification in a cell on another row, the cell left to it (N1,N2,M2) 
      //has more space and will span on one row (it spanned on two rows before this).
      //So the cell "aaaaa" will also layout because it has vertical alignment
      if(PlatformDetector.isWin32()){
        assertEquals( 
            "Please run this test on Linux!", 
                dump.toString());
      } else {
        assertEquals( 
            "<RootBox>[X:0 Y:0 W:447 H:207 ]\n" + 
            "BlockElementBox: <#document>[X:0 Y:8 W:447 H:186 ]\n" + 
            "  BlockElementBox: <article>[X:0 Y:0 W:447 H:186 ]\n" + 
            "    BlockElementBox: <sect1>[X:0 Y:0 W:447 H:186 ]\n" + 
            "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
            "        ParagraphBox[X:0 Y:0 W:84 H:18 ]\n" + 
            "          LineBox: <title>[X:0 Y:0 W:84 H:18 ]\n" + 
            "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
            "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
            "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
            "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
            "            DocumentTextBox: 'S'[X:77 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
            "      BlockElementBox: <para>[X:12 Y:31 W:435 H:155 ]\n" + 
            "        BlockElementBox: <table>[X:4 Y:0 W:427 H:155 ]\n" + 
            "          BlockElementBox: <title>[X:16 Y:7 W:360 H:18 ]\n" + 
            "            ParagraphBox[X:0 Y:0 W:7 H:18 ]\n" + 
            "              LineBox: <title>[X:0 Y:0 W:7 H:18 ]\n" + 
            "                DocumentTextBox: 'A'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
            "          TableBox: <tgroup>[X:16 Y:29 W:407 H:122 ]\n" + 
            "            TableCellBox: <entry>[X:1 Y:1 W:108 H:40 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:60 H:18 ]\n" + 
            "                LineBox: <entry>[X:32 Y:0 W:28 H:18 ]\n" + 
            "                  DocumentTextBox: 'NAME'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
            "            TableCellBox: <entry>[X:109 Y:1 W:72 H:40 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:45 H:36 ]\n" + 
            "                LineBox: <entry>[X:10 Y:0 W:35 H:18 ]\n" + 
            "                  DocumentTextBox: 'WCSP '[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
            "                LineBox: <entry>[X:14 Y:18 W:28 H:18 ]\n" + 
            "                  DocumentTextBox: 'BALL'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:6)\n" + 
            "            TableCellBox: <entry>[X:181 Y:1 W:51 H:40 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:31 H:36 ]\n" + 
            "                LineBox: <entry>[X:3 Y:0 W:28 H:18 ]\n" + 
            "                  DocumentTextBox: 'QFN '[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
            "                LineBox: <entry>[X:3 Y:18 W:28 H:18 ]\n" + 
            "                  DocumentTextBox: 'BALL'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:5)\n" + 
            "            TableCellBox: <entry>[X:232 Y:1 W:37 H:40 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:21 H:18 ]\n" + 
            "                LineBox: <entry>[X:0 Y:0 W:21 H:18 ]\n" + 
            "                  DocumentTextBox: 'I/O'[X:0 Y:0 W:21 H:18 ](Length:3, StartRel:1)\n" + 
            "            TableCellBox: <entry>[X:269 Y:1 W:75 H:40 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:18 H:18 ]\n" + 
            "                LineBox: <entry>[X:0 Y:0 W:18 H:18 ]\n" + 
            "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "            TableCellBox: <entry>[X:344 Y:1 W:62 H:40 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:32 H:18 ]\n" + 
            "                LineBox: <entry>[X:14 Y:0 W:18 H:18 ]\n" + 
            "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "            TableCellBox: <entry>[X:1 Y:41 W:108 H:22 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:56 H:18 ]\n" + 
            "                LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
            "                  DocumentTextBox: 'SMPS1_IN'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
            "            TableCellBox: <entry>[X:109 Y:41 W:72 H:22 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:56 H:18 ]\n" + 
            "                LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
            "                  DocumentTextBox: 'N1,N2,M2'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
            "            TableCellBox: <entry>[X:181 Y:41 W:51 H:22 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:35 H:18 ]\n" + 
            "                LineBox: <entry>[X:0 Y:0 W:35 H:18 ]\n" + 
            "                  DocumentTextBox: 'aaaaa'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
            "            TableCellBox: <entry>[X:232 Y:41 W:37 H:22 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:14 H:18 ]\n" + 
            "                LineBox: <entry>[X:7 Y:0 W:7 H:18 ]\n" + 
            "                  DocumentTextBox: 'I'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
            "            TableCellBox: <entry>[X:269 Y:41 W:75 H:22 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:38 H:18 ]\n" + 
            "                LineBox: <entry>[X:20 Y:0 W:18 H:18 ]\n" + 
            "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "            TableCellBox: <entry>[X:344 Y:41 W:62 H:22 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:32 H:18 ]\n" + 
            "                LineBox: <entry>[X:14 Y:0 W:18 H:18 ]\n" + 
            "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "            TableCellBox: <entry>[X:1 Y:63 W:108 H:58 ]\n" + 
            "              ParagraphBox[X:14 Y:20 W:56 H:18 ]\n" + 
            "                LineBox: <entry>[X:0 Y:0 W:56 H:18 ]\n" + 
            "                  DocumentTextBox: 'SMPS2_SW'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
            "            TableCellBox: <entry>[X:109 Y:63 W:72 H:58 ]\n" + 
            "              ParagraphBox[X:14 Y:2 W:56 H:54 ]\n" + 
            "                LineBox: <entry>[X:17 Y:0 W:21 H:18 ]\n" + 
            "                  DocumentTextBox: 'N6,'[X:0 Y:0 W:21 H:18 ](Length:3, StartRel:1)\n" + 
            "                LineBox: <entry>[X:0 Y:18 W:56 H:18 ]\n" + 
            "                  DocumentTextBox: 'defhiM6,'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:4)\n" + 
            "                LineBox: <entry>[X:21 Y:36 W:14 H:18 ]\n" + 
            "                  DocumentTextBox: 'L6'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:12)\n" + 
            "            TableCellBox: <entry>[X:181 Y:63 W:51 H:58 ]\n" + 
            "              ParagraphBox[X:14 Y:20 W:28 H:18 ]\n" + 
            "                LineBox: <entry>[X:7 Y:0 W:21 H:18 ]\n" + 
            "                  DocumentTextBox: 'A37'[X:0 Y:0 W:21 H:18 ](Length:3, StartRel:1)\n" + 
            "            TableCellBox: <entry>[X:232 Y:63 W:37 H:58 ]\n" + 
            "              ParagraphBox[X:14 Y:20 W:14 H:18 ]\n" + 
            "                LineBox: <entry>[X:7 Y:0 W:7 H:18 ]\n" + 
            "                  DocumentTextBox: 'O'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
            "            TableCellBox: <entry>[X:269 Y:63 W:75 H:58 ]\n" + 
            "              ParagraphBox[X:14 Y:20 W:38 H:18 ]\n" + 
            "                LineBox: <entry>[X:20 Y:0 W:18 H:18 ]\n" + 
            "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "            TableCellBox: <entry>[X:344 Y:63 W:62 H:58 ]\n" + 
            "              ParagraphBox[X:14 Y:20 W:32 H:18 ]\n" + 
            "                LineBox: <entry>[X:14 Y:0 W:18 H:18 ]\n" + 
            "                  EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
            "", 
                dump.toString());
      }
    } finally {
      fixedCSSsToUse = null;
    }
  }

  private static CSSResource[] fixedCSSsToUse = null;
}