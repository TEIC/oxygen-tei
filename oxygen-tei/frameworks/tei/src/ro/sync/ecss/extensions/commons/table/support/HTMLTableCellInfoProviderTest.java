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
import java.util.List;

import ro.sync.ecss.component.AuthorViewport;
import ro.sync.ecss.css.IStyleSheet;
import ro.sync.ecss.css.StyleSheet;
import ro.sync.ecss.dom.AuthorDocumentImpl;
import ro.sync.ecss.dom.AuthorElementImpl;
import ro.sync.ecss.dom.AuthorSentinelNode;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WidthRepresentation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.layout.Box;
import ro.sync.ecss.layout.DumpConfiguration;
import ro.sync.ecss.layout.LayoutUtils;
import ro.sync.ecss.layout.RootBox;
import ro.sync.ecss.layout.TableBox;
import ro.sync.ecss.layout.table.AuthorTableResizeDialogPresenter;
import ro.sync.ecss.layout.table.AuthorTableResizingHandler;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.editor.status.StatusModelListener;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;
import ro.sync.exml.options.OptionTags;
import ro.sync.exml.options.Options;
import ro.sync.exml.workspace.api.editor.page.author.css.CSSResource;
import ro.sync.ui.UiUtil;
import ro.sync.util.PlatformDetector;
import ro.sync.util.URLUtil;

/**
 * Test that the cell spanning is computed correctly.
 * @author radu_coravu
 */
public class HTMLTableCellInfoProviderTest extends EditorAuthorExtensionTestBase {
  
  /**
   * Constructor.
   */
  public HTMLTableCellInfoProviderTest() {
    super(false, true);
  }
  
  /**
   * SET UP
   */
  @Override
  protected void setUp() throws Exception {
    Options.getInstance().setBooleanProperty(OptionTags.EDITOR_FORMAT_INDENT_INLINE_ELEMENTS, false);
    super.setUp();
  }
  
  /**
   * TEAR DOWN
   */
  @Override
  protected void tearDown() throws Exception {
    Options.getInstance().setBooleanProperty(OptionTags.EDITOR_FORMAT_INDENT_INLINE_ELEMENTS, true);
    super.tearDown();
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
   * Test that no exception is thrown when children width is bigger then the remaining content width.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testRemainingContentWidth() throws Exception {
      String xml = 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <head>\n" + 
        "        <title></title>\n" + 
        "    </head>\n" + 
        "    <body>\n" + 
        "       <table>\n" + 
        "          <colgroup>\n" + 
        "            <col width=\"200px\"/>\n" + 
        "            <col width=\"200px\"/>\n" +
        "            <col width=\"1px\"/>\n" +
        "          </colgroup>\n" + 
        "          <tr>\n" + 
        "            <td>1</td>\n" + 
        "            <td>2</td>\n" +
        "            <td>3</td>\n" +
        "          </tr>\n" + 
        "       </table>\n" + 
        "    </body>\n" + 
        "</html>";

      initEditor(xml);
      
      AuthorDocumentImpl document = ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
      AuthorElement table = (AuthorElement) ((AuthorSentinelNode) ((AuthorSentinelNode)document.getChild("html")).getChild("body")).getChild("table");
      AuthorSentinelNode row = (AuthorSentinelNode) (((AuthorSentinelNode) table).getChild("tr"));
      CALSTableCellInfoProvider tableSupport = new CALSTableCellInfoProvider();
      tableSupport.init(table);    
      assertNotNull(row);
   
  }
  
  /**
   * Test the col width 
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testColWidth() throws Exception {
    String xml = 
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "       <table>\n" + 
      "                <colgroup>\n" + 
      "                    <col width=\"20px\"/>\n" + 
      "                </colgroup>\n" + 
      "                <colgroup>\n" + 
      "                    <col width=\"20px\"/>\n" + 
      "                    <col width=\"1*\"/>\n" + 
      "                    <col width=\"2*\"/>\n" + 
      "                </colgroup>\n" + 
      "                <tr>\n" + 
      "                    <td>1</td>\n" + 
      "                    <td>2</td>\n" + 
      "                    <td>3</td>\n" + 
      "                    <td>3</td>\n" + 
      "                </tr>\n" + 
      "            </table>\n" + 
      "    </body>\n" + 
      "</html>";

    initEditor(xml);
    AuthorDocumentImpl document = ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
    AuthorElement table = (AuthorElement) ((AuthorSentinelNode) ((AuthorSentinelNode)document.getChild("html")).getChild("body")).getChild("table");
    AuthorSentinelNode row = (AuthorSentinelNode) (((AuthorSentinelNode) table).getChild("tr"));
    HTMLTableCellInfoProvider tableSupport = new HTMLTableCellInfoProvider();
    tableSupport.init(table);
    
    // Col 0
    List<WidthRepresentation> colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(0), 0, 1);
    assertEquals(colWidth.size(), 1);
    
    WidthRepresentation col = colWidth.get(0);
    int fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(0f, col.getRelativeWidth());
    assertEquals(20, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
    // Col 1
    colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(1), 1, 1);
    assertEquals(colWidth.size(), 1);
    
    col = colWidth.get(0);
    fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(0f, col.getRelativeWidth());
    assertEquals(20, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    // Col 2
    colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(2), 2, 1);
    assertEquals(colWidth.size(), 1);
    
    col = colWidth.get(0);
    fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(1.0f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
    // Col 3
    colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(3), 3, 1);
    assertEquals(colWidth.size(), 1);
    
    col = colWidth.get(0);
    fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(2.0f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
  }
  
  /**
   * Test the table layout when the specified colwidts are mixed: fixed sizes, percentages and proprotional.
   * @author radu_coravu
   * @throws Exception
   */
  public void testLayout2() throws Exception {
    String xml = 
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
      "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "            <table>\n" + 
      "                <colgroup span=\"3\">\n" + 
      "                    <col width=\"200px\" span=\"2\"/>\n" + 
      "                </colgroup>\n" + 
      "                <colgroup>\n" + 
      "                    <col width=\"25%\"/>\n" + 
      "                    <col width=\"2.5*\"/>\n" + 
      "                </colgroup>\n" + 
      "                <colgroup>\n" + 
      "                    <col width=\"1*\"/>\n" + 
      "                    <col width=\"3* + 10\"/>\n" + 
      "                </colgroup>\n" + 
      "                <tr>\n" + 
      "                    <td>1</td>\n" + 
      "                    <td>1</td>\n" + 
      "                    <td>1</td>\n" + 
      "                    <td>1</td>\n" + 
      "                    <td>111</td>\n" + 
      "                    <td>111111111</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td colspan=\"3\">1</td>\n" + 
      "                    <td>1</td>\n" + 
      "                    <td>1</td>\n" + 
      "                    <td>1</td>\n" + 
      "                </tr>\n" + 
      "            </table>" + 
      "    </body>\n" + 
      "</html>";
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    flushAWTBetter();
    Thread.sleep(300);
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    if(PlatformDetector.isWin32()){
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals(
          "<RootBox>[X:0 Y:0 W:594 H:100 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:594 H:74 ]\n" + 
          "  BlockElementBox: <html>[X:0 Y:0 W:594 H:74 ]\n" + 
          "    BlockElementBox: <body>[X:0 Y:0 W:594 H:74 ]\n" + 
          "      TableBox: <table>[X:4 Y:0 W:586 H:74 ]\n" + 
          "        BlockPseudoElementBox: before[X:0 Y:0 W:586 H:28 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "            LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "              StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "        TableCellBox: <td>[X:1 Y:29 W:200 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:201 Y:29 W:200 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:401 Y:29 W:11 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:412 Y:29 W:63 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:475 Y:29 W:25 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:21 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:21 H:18 ]\n" + 
          "              DocumentTextBox: '111'[X:0 Y:0 W:21 H:18 ](Length:3, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:500 Y:29 W:85 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:63 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:63 H:18 ]\n" + 
          "              DocumentTextBox: '111111111'[X:0 Y:0 W:63 H:18 ](Length:9, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:1 Y:51 W:411 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:412 Y:51 W:63 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:475 Y:51 W:25 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:500 Y:51 W:85 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "",
              dump.toString());
    }
  }

  /**
   * Test the table layout with percentage specified widths for cells.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testTableLayout3() throws Exception {
    String xml = 
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
      "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "          <table>\n" + 
      "                <colgroup>\n" + 
      "                    <col width=\"20%\"/>\n" + 
      "                </colgroup>\n" + 
      "                <colgroup>\n" + 
      "                    <col width=\"20%\"/>\n" + 
      "                    <col width=\"25%\"/>\n" + 
      "                    <col width=\"10%\"/>\n" + 
      "                </colgroup>\n" + 
      "                <colgroup>\n" + 
      "                    <col width=\"35%\"/>\n" + 
      "                    <col width=\"35%\"/>\n" + 
      "                </colgroup>\n" + 
      "                <tr>\n" + 
      "                    <td>1</td>\n" + 
      "                    <td>2</td>\n" + 
      "                    <td>3</td>\n" + 
      "                    <td>4</td>\n" + 
      "                    <td>111</td>\n" + 
      "                    <td>111111111</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td colspan=\"3\">1</td>\n" + 
      "                    <td>21</td>\n" + 
      "                    <td>22</td>\n" + 
      "                    <td>23</td>\n" + 
      "                </tr>\n" + 
      "            </table>" + 
      "    </body>\n" + 
      "</html>";

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
    if(PlatformDetector.isWin32()) {
      assertEquals("Please run this test on Linux!", dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:467 H:100 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:467 H:74 ]\n" + 
          "  BlockElementBox: <html>[X:0 Y:0 W:467 H:74 ]\n" + 
          "    BlockElementBox: <body>[X:0 Y:0 W:467 H:74 ]\n" + 
          "      TableBox: <table>[X:4 Y:0 W:459 H:74 ]\n" + 
          "        BlockPseudoElementBox: before[X:0 Y:0 W:459 H:28 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "            LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "              StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "        TableCellBox: <td>[X:1 Y:29 W:78 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:79 Y:29 W:78 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '2'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:157 Y:29 W:98 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '3'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:255 Y:29 W:39 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '4'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:294 Y:29 W:97 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:21 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:21 H:18 ]\n" + 
          "              DocumentTextBox: '111'[X:0 Y:0 W:21 H:18 ](Length:3, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:391 Y:29 W:67 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:63 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:63 H:18 ]\n" + 
          "              DocumentTextBox: '111111111'[X:0 Y:0 W:63 H:18 ](Length:9, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:1 Y:51 W:254 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:255 Y:51 W:39 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:14 H:18 ]\n" + 
          "              DocumentTextBox: '21'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:294 Y:51 W:97 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:14 H:18 ]\n" + 
          "              DocumentTextBox: '22'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:391 Y:51 W:67 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:14 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:14 H:18 ]\n" + 
          "              DocumentTextBox: '23'[X:0 Y:0 W:14 H:18 ](Length:2, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * Test the table layout when the widths are specified in cols 
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testTableLayout() throws Exception {
    String xml = 
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
      "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "       <table>\n" + 
      "                <colgroup>\n" + 
      "                    <col width=\"20px\"/>\n" + 
      "                </colgroup>\n" + 
      "                <colgroup>\n" + 
      "                    <col width=\"20px\"/>\n" + 
      "                    <col width=\"1*\"/>\n" + 
      "                    <col width=\"2*\"/>\n" + 
      "                </colgroup>\n" + 
      "                <tr>\n" + 
      "                    <td>1</td>\n" + 
      "                    <td>2</td>\n" + 
      "                    <td>3</td>\n" + 
      "                    <td>3</td>\n" + 
      "                </tr>\n" + 
      "            </table>\n" + 
      "    </body>\n" + 
      "</html>";

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
          "<RootBox>[X:0 Y:0 W:400 H:78 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:400 H:52 ]\n" + 
          "  BlockElementBox: <html>[X:0 Y:0 W:400 H:52 ]\n" + 
          "    BlockElementBox: <body>[X:0 Y:0 W:400 H:52 ]\n" + 
          "      TableBox: <table>[X:4 Y:0 W:392 H:52 ]\n" + 
          "        BlockPseudoElementBox: before[X:0 Y:0 W:392 H:28 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "            LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "              StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "        TableCellBox: <td>[X:1 Y:29 W:21 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:22 Y:29 W:20 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '2'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:42 Y:29 W:116 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '3'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:158 Y:29 W:233 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:7 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: '3'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  
  /**
   * TC for table layout when a cell spans over two other cells with fixed width.
   * The cell should try to wrap it's content taking into account the sum of the
   * fixed widths.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testWrapCellContent() throws Exception {
    String xml = 
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
      "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "<table frame=\"void\">\n" + 
    		"        <colgroup>\n" + 
    		"            <col width=\"100px\"/>\n" + 
    		"            <col width=\"100px\"/>\n" + 
    		"        </colgroup>\n" + 
    		"        <thead>\n" + 
    		"            <tr>\n" + 
    		"                <th>Header 1</th>\n" + 
    		"                <th>Header 2</th>\n" + 
    		"            </tr>\n" + 
    		"        </thead>\n" + 
    		"        <tbody>\n" + 
    		"            <tr>\n" + 
    		"                <td colspan=\"2\">wwwww wwwww wwwww wwwww wwwww wwwwww wwwwwww</td>\n" + 
    		"            </tr>\n" + 
    		"        </tbody>\n" + 
    		"    </table>" + 
        "    </body>\n" + 
        "</html>";

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
            "<RootBox>[X:0 Y:0 W:400 H:116 ]\n" + 
            "BlockElementBox: <#document>[X:0 Y:13 W:400 H:90 ]\n" + 
            "  BlockElementBox: <html>[X:0 Y:0 W:400 H:90 ]\n" + 
            "    BlockElementBox: <body>[X:0 Y:0 W:400 H:90 ]\n" + 
            "      TableBox: <table>[X:4 Y:0 W:200 H:90 ]\n" + 
            "        BlockPseudoElementBox: before[X:0 Y:0 W:200 H:28 ]\n" + 
            "          ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
            "            LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
            "              StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
            "        TableCellBox: <th>[X:0 Y:28 W:100 H:22 ]\n" + 
            "          ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
            "            LineBox: <th>[X:0 Y:0 W:56 H:18 ]\n" + 
            "              DocumentTextBox: 'Header 1'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
            "        TableCellBox: <th>[X:100 Y:28 W:100 H:22 ]\n" + 
            "          ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
            "            LineBox: <th>[X:0 Y:0 W:56 H:18 ]\n" + 
            "              DocumentTextBox: 'Header 2'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
            "        TableCellBox: <td>[X:0 Y:50 W:200 H:40 ]\n" + 
            "          ParagraphBox[X:2 Y:2 W:168 H:36 ]\n" + 
            "            LineBox: <td>[X:0 Y:0 W:168 H:18 ]\n" + 
            "              DocumentTextBox: 'wwwww wwwww wwwww wwwww '[X:0 Y:0 W:168 H:18 ](Length:24, StartRel:1)\n" + 
            "            LineBox: <td>[X:0 Y:18 W:140 H:18 ]\n" + 
            "              DocumentTextBox: 'wwwww wwwwww wwwwwww'[X:0 Y:0 W:140 H:18 ](Length:20, StartRel:25)\n" + 
            "", 
                dump.toString());
      }
  }
  
  /**
   * Test the resizing of tables.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTable() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "<table frame=\"hsides\" width=\"250\">\n" + 
      "            <caption>Sample HTML Table</caption>\n" +
      "<col width=\"200px\"/>\n" +
      "<col width=\"50px\"/>\n" +
      "            <thead>\n" + 
      "                <tr>\n" + 
      "                    <th>Person Name</th>\n" + 
      "                    <th>Age</th>\n" + 
      "                </tr>\n" + 
      "            </thead>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td>Jane</td>\n" + 
      "                    <td>26</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>Bart </td>\n" + 
      "                    <td>24</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>Alexander</td>\n" + 
      "                    <td>22</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td colspan=\"2\">\n" + 
      "                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
      "                            department</emphasis>\n" + 
      "                    </td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    flushAWTBetter();
    
    int x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 100);
    flushAWTBetter();
    Thread.sleep(300);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table frame=\"hsides\" width=\"250\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <col width=\"102px\"/>\n" + 
        "            <col width=\"148px\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
        "                        science department</emphasis> </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", 
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x + 100);
    
    Thread.sleep(300);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table frame=\"hsides\" width=\"250\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <col width=\"204px\"/>\n" + 
        "            <col width=\"46px\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
        "                        science department</emphasis> </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 70);
    
    Thread.sleep(300);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table frame=\"hsides\" width=\"233\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <col width=\"204px\"/>\n" + 
        "            <col width=\"25px\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
        "                        science department</emphasis> </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * Test the resizing of the table columns when the column specifications are 
   * contained inside a 'colgroup'. 
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testEXM_14511_1() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "<table frame=\"hsides\" width=\"250\">\n" + 
      "            <caption>Sample HTML Table</caption>\n" +
      "<colgroup>" +
      "    <col width=\"200px\"/>\n" +
      "    <col width=\"50px\"/>\n" +
      "</colgroup>" +
      "            <thead>\n" + 
      "                <tr>\n" + 
      "                    <th>Person Name</th>\n" + 
      "                    <th>Age</th>\n" + 
      "                </tr>\n" + 
      "            </thead>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td>Jane</td>\n" + 
      "                    <td>26</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>Bart </td>\n" + 
      "                    <td>24</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>Alexander</td>\n" + 
      "                    <td>22</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td colspan=\"2\">\n" + 
      "                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
      "                            department</emphasis>\n" + 
      "                    </td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
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
        "        <table frame=\"hsides\" width=\"250\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <colgroup>\n" + 
        "                <col width=\"102px\"/>\n" + 
        "                <col width=\"148px\"/>\n" + 
        "            </colgroup>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
        "                        science department</emphasis> </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * <p><b>Description:</b> Test that the column resize mark the document as modified.</p>
   * <p><b>Bug ID:</b> EXM-30605</p>
   *
   * @author mihaela
   *
   * @throws Exception
   */
  public void testResizeTableColumns() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "        <table frame=\"void\" bgcolor=\"\" border=\"1px\" align=\"center\" width=\"353\">\n" + 
      "            <caption>A table with merged cells.</caption>\n" + 
      "            <col width=\"200px\"/>\n" + 
      "            <col width=\"300px\"/>\n" + 
      "            <thead>\n" + 
      "                <tr>\n" + 
      "                    <th>x</th>\n" + 
      "                    <th>y</th>\n" + 
      "                </tr>\n" + 
      "            </thead>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td>bla </td>\n" + 
      "                    <td>bla </td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>bla bla </td>\n" + 
      "                    <td>bla </td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>e</td>\n" + 
      "                    <td>f</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>g</td>\n" + 
      "                    <td>h</td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>\n" + 
      "    </sect1>\n" + 
      "</article>\n" + 
      "";
    initEditor(xml);
    flushAWTBetter();
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    editor.setModifiedStatus(StatusModelListener.UNMODIFIED);
    
    int x = sendMousePressed(vViewport, 3, true);
    sendMouseReleased(vViewport, x - 300);
    sleep(300);
    flushAWTBetter();
    
    
    assertTrue(editor.isModified());
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table frame=\"void\" bgcolor=\"\" border=\"1px\" align=\"center\" width=\"353\">\n" + 
        "            <caption>A table with merged cells.</caption>\n" + 
        "            <col width=\"32px\"/>\n" + 
        "            <col width=\"468px\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>x</th>\n" + 
        "                    <th>y</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>bla </td>\n" + 
        "                    <td>bla </td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>bla bla </td>\n" + 
        "                    <td>bla </td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>e</td>\n" + 
        "                    <td>f</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>g</td>\n" + 
        "                    <td>h</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", 
        serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * TC for resizing table columns.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTableColumns2() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "<table frame=\"void\" bgcolor=\"\" border=\"1px\" align=\"center\" width=\"353\">\n" + 
      "            <caption>A table with merged cells.</caption>\n" + 
      "            <col width=\"1.74*\"/>\n" + 
      "            <col width=\"1.3*\"/>\n" + 
      "            <col width=\"1*\"/>\n" + 
      "            <col width=\"1.06*\"/>\n" + 
      "            <col width=\"0.94*\"/>\n" + 
      "            <thead>\n" + 
      "                <tr>\n" + 
      "                    <th>x</th>\n" + 
      "                    <th>y</th>\n" + 
      "                    <th colspan=\"3\">Spans <b>Horizontally</b>\n" + 
      "                    </th>\n" + 
      "                </tr>\n" + 
      "            </thead>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td rowspan=\"3\">Spans <b>Vertically</b>\n" + 
      "                    </td>\n" + 
      "                    <td colspan=\"4\">b</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td rowspan=\"2\" colspan=\"2\">Spans <b>Both</b>\n" + 
      "                    </td>\n" + 
      "                    <td colspan=\"2\">d</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>e</td>\n" + 
      "                    <td>f</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>g</td>\n" + 
      "                    <td>h</td>\n" + 
      "                    <td colspan=\"3\">k</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>x</td>\n" + 
      "                    <td>y</td>\n" + 
      "                    <td>z</td>\n" + 
      "                    <td>t</td>\n" + 
      "                    <td>u</td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    flushAWTBetter();
    
    int x = sendMousePressed(vViewport, 7, false);
    sendMouseReleased(vViewport, x - 500);
    
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table frame=\"void\" bgcolor=\"\" border=\"1px\" align=\"center\" width=\"353\">\n" + 
        "            <caption>A table with merged cells.</caption>\n" + 
        "            <col width=\"9.16*\"/>\n" + 
        "            <col width=\"6.84*\"/>\n" + 
        "            <col width=\"1*\"/>\n" + 
        "            <col width=\"9.84*\"/>\n" + 
        "            <col width=\"4.95*\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>x</th>\n" + 
        "                    <th>y</th>\n" + 
        "                    <th colspan=\"3\">Spans <b>Horizontally</b> </th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td rowspan=\"3\">Spans <b>Vertically</b> </td>\n" + 
        "                    <td colspan=\"4\">b</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td rowspan=\"2\" colspan=\"2\">Spans <b>Both</b> </td>\n" + 
        "                    <td colspan=\"2\">d</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>e</td>\n" + 
        "                    <td>f</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>g</td>\n" + 
        "                    <td>h</td>\n" + 
        "                    <td colspan=\"3\">k</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>x</td>\n" + 
        "                    <td>y</td>\n" + 
        "                    <td>z</td>\n" + 
        "                    <td>t</td>\n" + 
        "                    <td>u</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * Test for EXM-14529. The cell info provider should recompute the 
   * 'col' section offsets on each commit.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testEXM_14529() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "<table frame=\"void\" bgcolor=\"\" border=\"1px\" align=\"center\" width=\"353\">\n" + 
      "            <caption>A table with merged cells.</caption>\n" + 
      "            <col width=\"1.74*\"/>\n" + 
      "            <col width=\"1.3*\"/>\n" + 
      "            <col width=\"1*\"/>\n" + 
      "            <col width=\"1.06*\"/>\n" + 
      "            <col width=\"0.94*\"/>\n" + 
      "            <thead>\n" + 
      "                <tr>\n" + 
      "                    <th>x</th>\n" + 
      "                    <th>y</th>\n" + 
      "                    <th colspan=\"3\">Spans <b>Horizontally</b>\n" + 
      "                    </th>\n" + 
      "                </tr>\n" + 
      "            </thead>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td rowspan=\"3\">Spans <b>Vertically</b>\n" + 
      "                    </td>\n" + 
      "                    <td colspan=\"4\">b</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td rowspan=\"2\" colspan=\"2\">Spans <b>Both</b>\n" + 
      "                    </td>\n" + 
      "                    <td colspan=\"2\">d</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>e</td>\n" + 
      "                    <td>f</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>g</td>\n" + 
      "                    <td>h</td>\n" + 
      "                    <td colspan=\"3\">k</td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    
    AuthorElement root = vViewport.getController().getAuthorDocument().getRootElement();
    final AuthorElement[] sect1 = root.getElementsByLocalName("sect1");
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          vViewport.getController().insertXMLFragment("<p>para</p>", sect1[0].getStartOffset() + 1);
        } catch (AuthorOperationException e) {
          fail(e.getMessage());
        }
      }
    });
    
    flushAWTBetter();

    int x = sendMousePressed(vViewport, 7, false);
    sendMouseReleased(vViewport, x + 100);
    
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <p>para</p>\n" + 
        "        <table frame=\"void\" bgcolor=\"\" border=\"1px\" align=\"center\" width=\"405\">\n" + 
        "            <caption>A table with merged cells.</caption>\n" + 
        "            <col width=\"9.16*\"/>\n" + 
        "            <col width=\"6.84*\"/>\n" + 
        "            <col width=\"15*\"/>\n" + 
        "            <col width=\"1*\"/>\n" + 
        "            <col width=\"4.95*\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>x</th>\n" + 
        "                    <th>y</th>\n" + 
        "                    <th colspan=\"3\">Spans <b>Horizontally</b> </th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td rowspan=\"3\">Spans <b>Vertically</b> </td>\n" + 
        "                    <td colspan=\"4\">b</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td rowspan=\"2\" colspan=\"2\">Spans <b>Both</b> </td>\n" + 
        "                    <td colspan=\"2\">d</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>e</td>\n" + 
        "                    <td>f</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>g</td>\n" + 
        "                    <td>h</td>\n" + 
        "                    <td colspan=\"3\">k</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));
  }
  
  private int userChoice;
  private boolean addTWAsked;
  private boolean onlyProportional;
  /**
   * Test the resizing of columns with user interaction.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTableColsWithUserInteraction() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "<table frame=\"hsides\">\n" + 
      "            <caption>Sample HTML Table</caption>\n" +
      "            <thead>\n" + 
      "                <tr>\n" + 
      "                    <th>Person Name</th>\n" + 
      "                    <th>Age</th>\n" + 
      "                </tr>\n" + 
      "            </thead>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td>Jane</td>\n" + 
      "                    <td>26</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>Bart </td>\n" + 
      "                    <td>24</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>Alexander</td>\n" + 
      "                    <td>22</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td colspan=\"2\">\n" + 
      "                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
      "                            department</emphasis>\n" + 
      "                    </td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {
      @Override
      protected int shouldAddWidthSpecifications() {
        addTWAsked = true;
        return userChoice;
      }
    });
    
    userChoice = AuthorTableResizeDialogPresenter.ADD_TABLE_AND_COLUMN_WIDTHS;
    int x = sendMousePressed(vViewport, 2, false);
    Thread.sleep(300);
    
    assertTrue(addTWAsked);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "<table frame=\"hsides\" width=\"380\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <col width=\"76%\"/>\n" + 
        "            <col width=\"24%\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
        "                        science department</emphasis> </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>    </sect1>\n" + 
        "</article>",
        serializeDocumentViewport(vViewport, true));
    
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 100);
    Thread.sleep(300);
    
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table frame=\"hsides\" width=\"315\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <col width=\"76%\"/>\n" + 
        "            <col width=\"24%\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
        "                        science department</emphasis> </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * Test the resizing of columns with user interaction.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTableColsWithUserInteraction2() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "<table frame=\"hsides\">\n" + 
      "            <caption>Sample HTML Table</caption>\n" +
      "            <thead>\n" + 
      "                <tr>\n" + 
      "                    <th>Person Name</th>\n" + 
      "                    <th>Age</th>\n" + 
      "                </tr>\n" + 
      "            </thead>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td>Jane</td>\n" + 
      "                    <td>26</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>Bart </td>\n" + 
      "                    <td>24</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td>Alexander</td>\n" + 
      "                    <td>22</td>\n" + 
      "                </tr>\n" + 
      "                <tr>\n" + 
      "                    <td colspan=\"2\">\n" + 
      "                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
      "                            department</emphasis>\n" + 
      "                    </td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {
      @Override
      protected int shouldAddWidthSpecifications() {
        addTWAsked = true;
        return userChoice;
      }
    });
    
    userChoice = AuthorTableResizeDialogPresenter.ADD_PROPORTIONAL_COLUMN_WIDTHS;
    int x = sendMousePressed(vViewport, 2, false);
    flushAWTBetter();
    Thread.sleep(100);
    
    assertTrue(addTWAsked);
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "<table frame=\"hsides\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <col width=\"76%\"/>\n" + 
        "            <col width=\"24%\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\">\n" + 
        "                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
        "                            department</emphasis>\n" + 
        "                    </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>    </sect1>\n" + 
        "</article>", 
        serializeDocumentViewport(vViewport, true));
    
    x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 100);
    flushAWTBetter();
    Thread.sleep(100);
    
    assertEquals(
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table frame=\"hsides\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <col width=\"50%\"/>\n" + 
        "            <col width=\"50%\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
        "                        science department</emphasis> </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));   
    
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 100);
    flushAWTBetter();
    Thread.sleep(100);
    
    assertEquals(
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table frame=\"hsides\" width=\"278\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <col width=\"50%\"/>\n" + 
        "            <col width=\"50%\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
        "                        science department</emphasis> </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n", 
        serializeDocumentViewport(vViewport, false));   
  }
  
  /**
   * Test the layout of the table when table width is specified but
   * no column widths are present.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testLayoutEXM_14026() throws Exception {
      String xml = 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table width=\"100px\">\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td/>\n" + 
        "                    <td/>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"/>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html> ";

      initEditor(xml);
      flushAWTBetter();
      
      AuthorViewport vViewport = ((AuthorEditorPage)editor.getCurrentPage()).getViewport();
 
      RootBox rootBox = vViewport.getRootBox();
      StringBuilder dump = new StringBuilder();
      // Don't print minimum and maximum width 
      DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
      dumpConfiguration.setReportMaximumWidth(false);
      dumpConfiguration.setReportMinimumWidth(false);
      dumpConfiguration.setReportOffsets(false);    
      
      rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
      assertEquals(
          "<RootBox>[X:0 Y:0 W:400 H:72 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:400 H:46 ]\n" + 
          "  BlockElementBox: <html>[X:0 Y:0 W:400 H:46 ]\n" + 
          "    BlockElementBox: <body>[X:0 Y:0 W:400 H:46 ]\n" + 
          "      TableBox: <table>[X:4 Y:0 W:100 H:46 ]\n" + 
          "        TableCellBox: <td>[X:1 Y:1 W:49 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "        TableCellBox: <td>[X:50 Y:1 W:49 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "        TableCellBox: <td>[X:1 Y:23 W:98 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "              EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "", 
          dump.toString());
  }
  
  /**
   * Test the layout of the table when table width is specified but
   * no column widths are present.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testLayoutEXM_14397() throws Exception {
    String xml = 
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">  \n" +
      "    <body>\n" + 
      "        <table frame=\"void\" width=\"100\">\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td><table frame=\"void\">\n" + 
      "                            <tbody>\n" + 
      "                                <tr>\n" + 
      "                                    <td><table frame=\"void\" width=\"150\">\n" + 
      "                                            <thead>\n" + 
      "                                                <tr>\n" + 
      "                                                    <th>Header 2</th>\n" + 
      "                                                </tr>\n" + 
      "                                            </thead>\n" + 
      "                                        </table></td>\n" + 
      "                                </tr>\n" + 
      "                            </tbody>\n" + 
      "                        </table></td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>\n" + 
      "    </body>" + 
      "</html> ";

    initEditor(xml);
    flushAWTBetter();
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getCurrentPage()).getViewport();
    File f = File.createTempFile("test", ".css");
    f.deleteOnExit();
    FileWriter fw = new FileWriter(f);
    fw.write(
              "@import \"" + URLUtil.correct(new File("frameworks/xhtml/css/xhtml.css")) + "\";\n" + 
              "table {\n" + 
              "width: 100px !important;\n" + 
              "border: 20px solid red !important;\n" + 
              "}\n"
              + "body{\n"
              + "margin:0px !important;\n"
              + "}");
    fw.close();

    CSSResource resource = new CSSResource(URLUtil.correct(f).toString(), IStyleSheet.SOURCE_DOCUMENT_TYPE);
    vViewport.refresh(new CSSResource[] {resource});
    flushAWTBetter();
    
    RootBox rootBox = vViewport.getRootBox();
    StringBuilder dump = new StringBuilder();
  
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    

    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    
    assertEquals(
        "<RootBox>[X:0 Y:0 W:400 H:220 ]\n" + 
        "BlockElementBox: <#document>[X:0 Y:13 W:400 H:194 ]\n" + 
        "  BlockElementBox: <html>[X:0 Y:0 W:400 H:194 ]\n" + 
        "    BlockElementBox: <body>[X:0 Y:0 W:400 H:194 ]\n" + 
        "      TableBox: <table>[X:4 Y:0 W:254 H:194 ]\n" + 
        "        TableCellBox: <td>[X:20 Y:20 W:214 H:154 ]\n" + 
        "          TableBox: <table>[X:6 Y:13 W:202 H:128 ]\n" + 
        "            TableCellBox: <td>[X:20 Y:20 W:162 H:88 ]\n" + 
        "              TableBox: <table>[X:6 Y:13 W:150 H:62 ]\n" + 
        "                TableCellBox: <th>[X:20 Y:20 W:110 H:22 ]\n" + 
        "                  ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
        "                    LineBox: <th>[X:0 Y:0 W:56 H:18 ]\n" + 
        "                      DocumentTextBox: 'Header 2'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
        "", 
        dump.toString());
  }
 
 
  /**
   * Test for resize a html table containing only "tr" or "tfoot".
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testEXM_14107() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"300\">\n" + 
      "            <tr>\n" + 
      "                <td>First cell</td>\n" + 
      "                <td>Seccond cell</td>\n" + 
      "            </tr>\n" + 
      "        </table>\n" + 
      "    </body>\n" + 
      "</html>"; 
    
    initEditor(xml);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {
      
      @Override
      protected int shouldAddColWidthSpecifications(boolean proportional) {
        // Verify that only proportional widths specifications will be added.
        onlyProportional = proportional;
        return userChoice;
      }
    });
    
    
    userChoice = AuthorTableResizeDialogPresenter.ADD_PROPORTIONAL_COLUMN_WIDTHS;
    sendMousePressed(vViewport, 2, false);
    
    assertTrue("Only proportional widths speccification can be added", onlyProportional);
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"300\">\n" + 
        "            <col width=\"45%\" />\n" + 
        "            <col width=\"55%\" />\n" + 
        "            <tr>\n" + 
        "                <td>First cell</td>\n" + 
        "                <td>Seccond cell</td>\n" + 
        "            </tr>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));
    
    xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"300\">\n" + 
      "            <tfoot><tr>\n" + 
      "                <td>First cell</td>\n" + 
      "                <td>Seccond cell</td>\n" + 
      "            </tr></tfoot>\n" + 
      "        </table>\n" + 
      "    </body>\n" + 
      "</html>"; 
    
    initEditor(xml);
    vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {
      @Override
      protected int shouldAddColWidthSpecifications(boolean onlyProportional) {
        return userChoice;
      }
    });
    
    sendMousePressed(vViewport, 2, false);
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"300\">\n" + 
        "            <col width=\"45%\" />\n" + 
        "            <col width=\"55%\" />\n" + 
        "            <tfoot>\n" + 
        "                <tr>\n" + 
        "                    <td>First cell</td>\n" + 
        "                    <td>Seccond cell</td>\n" + 
        "                </tr>\n" + 
        "            </tfoot>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n", 
        serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * TC for table resizing when the table width is fixed and the column widhs are also fixed.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testTableResizingFixedTWAndCW() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "        <table width=\"100\">\n" + 
      "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
      "            <col width=\"100px\"/>\n" + 
      "            <col width=\"200px\"/>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td>Jane</td>\n" + 
      "                    <td>26</td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>" + 
      "    </body>\n" + 
      "</html>"; 
    
    initEditor(xml);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();

    int x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 100);
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table width=\"200\">\n" + 
        "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
        "            <col width=\"100px\" />\n" + 
        "            <col width=\"98px\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));
  }

  /**
   * Test the resizing of table when the table has fixed width but 
   * the columns have relative widths.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTableWhenTWisFixedAndCWareRelative() throws Exception {
    String xml =      
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<?xml-stylesheet type=\"text/css\" href=\"" + new File(".").toURI().toString() + "frameworks/xhtml/css/hide_colspec.css\"?>\n" +
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "        <table width=\"250\">\n" + 
      "            <col width=\"1*\"/>\n" + 
      "            <col width=\"2*\"/>\n" + 
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td>Jane</td>\n" + 
      "                    <td>26</td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>\n" + 
      "    </body>\n" + 
      "</html>"; 
    
    initEditor(xml);
    flushAWTBetter();
    Thread.sleep(300);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    // Shrink the whole table the column proportional
    // widths should not change, only the table width.
    int x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 48);
    flushAWTBetter();
    Thread.sleep(300);
    
    assertEquals( 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table width=\"200\">\n" + 
        "            <col width=\"1*\" />\n" + 
        "            <col width=\"2*\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" +
        "    </body>\n" + 
        "</html>", 
        serializeDocumentViewport(vViewport, true).replaceAll("<\\?(.*)\\?>\n", ""));
    
    // Increase the first cell, the column proportional widths should change,
    // also the table width.
    x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x + 200);
    flushAWTBetter();
    Thread.sleep(300);
    
    assertEquals( 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table width=\"288\">\n" + 
        "            <col width=\"15.04*\" />\n" + 
        "            <col width=\"1*\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>", 
        serializeDocumentViewport(vViewport, true).replaceAll("<\\?(.*)\\?>\n", ""));
    
    // Shrink the first cell. Columns proportional width should change but the table width should not.
    x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 71);
    
    assertEquals( 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table width=\"288\">\n" + 
        "            <col width=\"2.29*\" />\n" + 
        "            <col width=\"1*\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>", 
        serializeDocumentViewport(vViewport, true).replaceAll("<\\?(.*)\\?>\n", ""));
    
    // Shrink the whole table. Table width should change, column widths no.
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 48);
    flushAWTBetter();
    
    assertEquals( 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table width=\"238\">\n" + 
        "            <col width=\"2.29*\" />\n" + 
        "            <col width=\"1*\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>", 
        serializeDocumentViewport(vViewport, true).replaceAll("<\\?(.*)\\?>\n", ""));
  }
  
  /**
   * Test for the table layout when should wrap cell content.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testTableLayout4() throws Exception {
    if (!PlatformDetector.isLinux()) {
      String xml = 
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
        "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <head>\n" + 
        "        <title/>\n" + 
        "    </head>\n" + 
        "    <table frame=\"void\" width=\"200\">\n" + 
        "        <caption/>\n" + 
        "        <col width=\"0.52*\"/>\n" + 
        "        <col width=\"0.76*\"/>\n" + 
        "        <col width=\"0.52*\"/>\n" + 
        "        <thead>\n" + 
        "            <tr>\n" + 
        "                <th>Header 1</th>\n" + 
        "                <th>Header 2</th>\n" + 
        "                <th>Header 3</th>\n" + 
        "            </tr>\n" + 
        "        </thead>\n" + 
        "        <tbody>\n" + 
        "            <tr>\n" + 
        "                <td/>\n" + 
        "                <td/>\n" + 
        "                <td/>\n" + 
        "            </tr>\n" + 
        "        </tbody>\n" + 
        "    </table>\n" + 
        "    <body>\n" + 
        "    </body>\n" + 
        "</html>";

      useFixedFontMetrics = false;
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
      assertEquals(
          "<RootBox>[X:0 Y:0 W:400 H:381 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:0 W:400 H:381 ]\n" + 
          "  BlockElementBox: <html>[X:0 Y:0 W:400 H:381 ]\n" + 
          "    BlockElementBox: <head>[X:0 Y:0 W:400 H:82 ]\n" + 
          "      BlockElementBox: <title>[X:17 Y:31 W:366 H:20 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:39 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:39 H:18 ]\n" + 
          "            EmptyInlineElemTextBox: 'title'[X:0 Y:0 W:39 H:18 ]\n" + 
          "    TableBox: <table>[X:4 Y:110 W:200 H:240 ]\n" + 
          "      BlockElementBox: <caption>[X:14 Y:0 W:172 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:53 H:18 ]\n" + 
          "          LineBox: <caption>[X:0 Y:0 W:53 H:18 ]\n" + 
          "            EmptyInlineElemTextBox: 'caption'[X:0 Y:0 W:53 H:18 ]\n" + 
          "      BlockElementBox: <col>[X:3 Y:25 W:194 H:48 ]\n" + 
          "        ParagraphBox[X:2 Y:2 W:156 H:44 ]\n" + 
          "          LineBox: <col>[X:0 Y:0 W:156 H:22 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:2 W:49 H:18 ]\n" + 
          "              StaticTextBox: 'column '[X:0 Y:0 W:49 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <after>[X:49 Y:0 W:107 H:22 ]\n" + 
          "              StaticTextBox: 'span:'[X:0 Y:2 W:35 H:18 ]\n" + 
          "              StaticEditBox: '{edit_qualified=span, edit=@span, columns=7, type=text}'[X:35 Y:0 W:65 H:22 ]value:'1'\n" + 
          "              StaticTextBox: ' '[X:100 Y:2 W:7 H:18 ]\n" + 
          "          LineBox: <col>[X:0 Y:22 W:107 H:22 ]\n" + 
          "            InlineStaticContentForElementBox: <after>[X:0 Y:0 W:107 H:22 ]\n" + 
          "              StaticTextBox: 'width:'[X:0 Y:2 W:42 H:18 ]\n" + 
          "              StaticEditBox: '{edit_qualified=width, edit=@width, columns=7, type=text}'[X:42 Y:0 W:65 H:22 ]value:'0.52*'\n" + 
          "      BlockElementBox: <col>[X:3 Y:76 W:194 H:48 ]\n" + 
          "        ParagraphBox[X:2 Y:2 W:156 H:44 ]\n" + 
          "          LineBox: <col>[X:0 Y:0 W:156 H:22 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:2 W:49 H:18 ]\n" + 
          "              StaticTextBox: 'column '[X:0 Y:0 W:49 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <after>[X:49 Y:0 W:107 H:22 ]\n" + 
          "              StaticTextBox: 'span:'[X:0 Y:2 W:35 H:18 ]\n" + 
          "              StaticEditBox: '{edit_qualified=span, edit=@span, columns=7, type=text}'[X:35 Y:0 W:65 H:22 ]value:'1'\n" + 
          "              StaticTextBox: ' '[X:100 Y:2 W:7 H:18 ]\n" + 
          "          LineBox: <col>[X:0 Y:22 W:107 H:22 ]\n" + 
          "            InlineStaticContentForElementBox: <after>[X:0 Y:0 W:107 H:22 ]\n" + 
          "              StaticTextBox: 'width:'[X:0 Y:2 W:42 H:18 ]\n" + 
          "              StaticEditBox: '{edit_qualified=width, edit=@width, columns=7, type=text}'[X:42 Y:0 W:65 H:22 ]value:'0.76*'\n" + 
          "      BlockElementBox: <col>[X:3 Y:127 W:194 H:48 ]\n" + 
          "        ParagraphBox[X:2 Y:2 W:156 H:44 ]\n" + 
          "          LineBox: <col>[X:0 Y:0 W:156 H:22 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:2 W:49 H:18 ]\n" + 
          "              StaticTextBox: 'column '[X:0 Y:0 W:49 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <after>[X:49 Y:0 W:107 H:22 ]\n" + 
          "              StaticTextBox: 'span:'[X:0 Y:2 W:35 H:18 ]\n" + 
          "              StaticEditBox: '{edit_qualified=span, edit=@span, columns=7 type=text}'[X:35 Y:0 W:65 H:22 ]value:'1'\n" + 
          "              StaticTextBox: ' '[X:100 Y:2 W:7 H:18 ]\n" + 
          "          LineBox: <col>[X:0 Y:22 W:107 H:22 ]\n" + 
          "            InlineStaticContentForElementBox: <after>[X:0 Y:0 W:107 H:22 ]\n" + 
          "              StaticTextBox: 'width:'[X:0 Y:2 W:42 H:18 ]\n" + 
          "              StaticEditBox: '{edit_qualified=width, edit=@width, columns=7, type=text}'[X:42 Y:0 W:65 H:22 ]value:'0.52*'\n" + 
          "      TableCellBox: <th>[X:0 Y:178 W:58 H:40 ]\n" + 
          "        ParagraphBox[X:2 Y:2 W:49 H:36 ]\n" + 
          "          LineBox: <th>[X:0 Y:0 W:49 H:18 ]\n" + 
          "            DocumentTextBox: 'Header '[X:0 Y:0 W:49 H:18 ](Length:7, StartRel:1)\n" + 
          "          LineBox: <th>[X:0 Y:18 W:7 H:18 ]\n" + 
          "            DocumentTextBox: '1'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:8)\n" + 
          "      TableCellBox: <th>[X:58 Y:178 W:85 H:40 ]\n" + 
          "        ParagraphBox[X:2 Y:2 W:56 H:18 ]\n" + 
          "          LineBox: <th>[X:0 Y:0 W:56 H:18 ]\n" + 
          "            DocumentTextBox: 'Header 2'[X:0 Y:0 W:56 H:18 ](Length:8, StartRel:1)\n" + 
          "      TableCellBox: <th>[X:143 Y:178 W:57 H:40 ]\n" + 
          "        ParagraphBox[X:2 Y:2 W:49 H:36 ]\n" + 
          "          LineBox: <th>[X:0 Y:0 W:49 H:18 ]\n" + 
          "            DocumentTextBox: 'Header '[X:0 Y:0 W:49 H:18 ](Length:7, StartRel:1)\n" + 
          "          LineBox: <th>[X:0 Y:18 W:7 H:18 ]\n" + 
          "            DocumentTextBox: '3'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:8)\n" + 
          "      TableCellBox: <td>[X:0 Y:218 W:58 H:22 ]\n" + 
          "        ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "          LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "            EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "      TableCellBox: <td>[X:58 Y:218 W:85 H:22 ]\n" + 
          "        ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "          LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "            EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "      TableCellBox: <td>[X:143 Y:218 W:57 H:22 ]\n" + 
          "        ParagraphBox[X:2 Y:2 W:18 H:18 ]\n" + 
          "          LineBox: <td>[X:0 Y:0 W:18 H:18 ]\n" + 
          "            EmptyInlineElemTextBox: '  '[X:0 Y:0 W:18 H:18 ]\n" + 
          "    BlockElementBox: <body>[X:0 Y:363 W:400 H:18 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:32 H:18 ]\n" + 
          "        LineBox: <body>[X:0 Y:0 W:32 H:18 ]\n" + 
          "          EmptyInlineElemTextBox: 'body'[X:0 Y:0 W:32 H:18 ]\n" + 
          "", 
          dump.toString());
    }
  }
  
  /**
   * Test the layout of child table when the column widths are proportional both for parent and child table.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testLayoutTableInTableWithProportionalWidths() throws Exception {
    String xml = 
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
      "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
      "<body>\n" + 
      "    <table border=\"2px\" align=\"center\" width=\"300\">\n" + 
      "            <col width=\"33%\"/>\n" + 
      "            <col width=\"34%\"/>\n" + 
      "            <col width=\"33%\"/>\n" + 
      "            <thead>\n" + 
      "                <tr align=\"center\" >\n" + 
      "                    <th>col1</th>\n" + 
      "                    <th>col2</th>\n" + 
      "                    <th>col3</th>\n" + 
      "                </tr>\n" + 
      "            </thead>\n" + 
      "            <tbody>\n" + 
      "                <tr align=\"center\">\n" + 
      "                    <td>a</td>\n" + 
      "                    <td><table frame=\"void\" border=\"2\">\n" + 
      "                            <col width=\"0.61*\"/>\n" + 
      "                            <col width=\"1.39*\"/>\n" + 
      "                            <thead>\n" + 
      "                                <tr>\n" + 
      "                                    <th>scol1</th>\n" + 
      "                                    <th>scol2</th>\n" + 
      "                                </tr>\n" + 
      "                            </thead>\n" + 
      "                            <tbody/>\n" + 
      "                        </table></td>\n" + 
      "                    <td>b</td>\n" + 
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>" + 
      "    </body>\n" + 
      "</html>";

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
          "<RootBox>[X:0 Y:0 W:400 H:156 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:13 W:400 H:130 ]\n" + 
          "  BlockElementBox: <html>[X:0 Y:0 W:400 H:130 ]\n" + 
          "    BlockElementBox: <body>[X:0 Y:0 W:400 H:130 ]\n" + 
          "      TableBox: <table>[X:50 Y:0 W:300 H:130 ]\n" + 
          "        BlockPseudoElementBox: before[X:0 Y:0 W:300 H:28 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "            LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "              StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "        TableCellBox: <th>[X:2 Y:30 W:98 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:61 H:18 ]\n" + 
          "            LineBox: <th>[X:33 Y:0 W:28 H:18 ]\n" + 
          "              DocumentTextBox: 'col1'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
          "        TableCellBox: <th>[X:100 Y:30 W:130 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:77 H:18 ]\n" + 
          "            LineBox: <th>[X:49 Y:0 W:28 H:18 ]\n" + 
          "              DocumentTextBox: 'col2'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
          "        TableCellBox: <th>[X:230 Y:30 W:68 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:46 H:18 ]\n" + 
          "            LineBox: <th>[X:18 Y:0 W:28 H:18 ]\n" + 
          "              DocumentTextBox: 'col3'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:2 Y:52 W:98 H:76 ]\n" + 
          "          ParagraphBox[X:2 Y:17 W:50 H:18 ]\n" + 
          "            LineBox: <td>[X:43 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: 'a'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:100 Y:52 W:130 H:76 ]\n" + 
          "          TableBox: <table>[X:6 Y:13 W:118 H:50 ]\n" + 
          "            BlockPseudoElementBox: before[X:0 Y:0 W:118 H:28 ]\n" + 
          "              ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
          "                LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
          "                  StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
          "            TableCellBox: <th>[X:0 Y:28 W:39 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:35 H:18 ]\n" + 
          "                LineBox: <th>[X:0 Y:0 W:35 H:18 ]\n" + 
          "                  DocumentTextBox: 'scol1'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "            TableCellBox: <th>[X:39 Y:28 W:61 H:22 ]\n" + 
          "              ParagraphBox[X:2 Y:2 W:46 H:18 ]\n" + 
          "                LineBox: <th>[X:11 Y:0 W:35 H:18 ]\n" + 
          "                  DocumentTextBox: 'scol2'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:230 Y:52 W:68 H:76 ]\n" + 
          "          ParagraphBox[X:2 Y:17 W:35 H:18 ]\n" + 
          "            LineBox: <td>[X:28 Y:0 W:7 H:18 ]\n" + 
          "              DocumentTextBox: 'b'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * EXM-14101: Test the width of a table and inner table when they have specified a width in the document.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testLayoutInnerTableWithFixedWidth() throws Exception {
    Options.getInstance().setBooleanProperty(OptionTags.AUTHOR_SCHEMA_AWARE_NORMALIZE_FORMAT, false);
    try {
      String xml = 
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" + 
        "                      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
        "<body>\n" + 
        "    <table border=\"2px\" align=\"center\" width=\"450\">\n" + 
        "            <col width=\"33%\"/>\n" + 
        "            <col width=\"34%\"/>\n" + 
        "            <col width=\"33%\"/>\n" + 
        "            <thead>\n" + 
        "                <tr align=\"center\">\n" + 
        "                    <th>col1</th>\n" + 
        "                    <th>col2</th>\n" + 
        "                    <th>col3</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr align=\"center\">\n" + 
        "                    <td>a</td>\n" + 
        "                    <td>\n" + 
        "                        <table frame=\"void\" border=\"2\" width=\"100\">\n" + 
        "                            <col width=\"0.61*\"/>\n" + 
        "                            <col width=\"1.39*\"/>\n" + 
        "                            <thead>\n" + 
        "                                <tr>\n" + 
        "                                    <th>sc1</th>\n" + 
        "                                    <th>sc2</th>\n" + 
        "                                </tr>\n" + 
        "                            </thead>\n" + 
        "                            <tbody/>\n" + 
        "                        </table>\n" + 
        "                    </td>\n" + 
        "                    <td>b</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>" + 
        "    </body>\n" + 
        "</html>";

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
            "<RootBox>[X:0 Y:0 W:450 H:156 ]\n" + 
            "BlockElementBox: <#document>[X:0 Y:13 W:450 H:130 ]\n" + 
            "  BlockElementBox: <html>[X:0 Y:0 W:450 H:130 ]\n" + 
            "    BlockElementBox: <body>[X:0 Y:0 W:450 H:130 ]\n" + 
            "      TableBox: <table>[X:0 Y:0 W:450 H:130 ]\n" + 
            "        BlockPseudoElementBox: before[X:0 Y:0 W:450 H:28 ]\n" + 
            "          ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
            "            LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
            "              StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
            "        TableCellBox: <th>[X:2 Y:30 W:147 H:22 ]\n" + 
            "          ParagraphBox[X:2 Y:2 W:85 H:18 ]\n" + 
            "            LineBox: <th>[X:57 Y:0 W:28 H:18 ]\n" + 
            "              DocumentTextBox: 'col1'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
            "        TableCellBox: <th>[X:149 Y:30 W:152 H:22 ]\n" + 
            "          ParagraphBox[X:2 Y:2 W:88 H:18 ]\n" + 
            "            LineBox: <th>[X:60 Y:0 W:28 H:18 ]\n" + 
            "              DocumentTextBox: 'col2'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
            "        TableCellBox: <th>[X:301 Y:30 W:147 H:22 ]\n" + 
            "          ParagraphBox[X:2 Y:2 W:85 H:18 ]\n" + 
            "            LineBox: <th>[X:57 Y:0 W:28 H:18 ]\n" + 
            "              DocumentTextBox: 'col3'[X:0 Y:0 W:28 H:18 ](Length:4, StartRel:1)\n" + 
            "        TableCellBox: <td>[X:2 Y:52 W:147 H:76 ]\n" + 
            "          ParagraphBox[X:2 Y:17 W:75 H:18 ]\n" + 
            "            LineBox: <td>[X:68 Y:0 W:7 H:18 ]\n" + 
            "              DocumentTextBox: 'a'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
            "        TableCellBox: <td>[X:149 Y:52 W:152 H:76 ]\n" + 
            "          TableBox: <table>[X:6 Y:13 W:118 H:50 ]\n" + 
            "            BlockPseudoElementBox: before[X:0 Y:0 W:118 H:28 ]\n" + 
            "              ParagraphBox[X:0 Y:0 W:118 H:28 ]\n" + 
            "                LineBox: <before>[X:0 Y:0 W:118 H:28 ]\n" + 
            "                  StaticEditBox: '{actionID=table.expand.colspec, color=ro.sync.exml.view.graphics.Color[r=0,g=0,b=128], fontInherit=true, showText=true, transparent=true, type=button}'[X:0 Y:0 W:118 H:28 ]\n" + 
            "            TableCellBox: <th>[X:0 Y:28 W:31 H:22 ]\n" + 
            "              ParagraphBox[X:2 Y:2 W:24 H:18 ]\n" + 
            "                LineBox: <th>[X:3 Y:0 W:21 H:18 ]\n" + 
            "                  DocumentTextBox: 'sc1'[X:0 Y:0 W:21 H:18 ](Length:3, StartRel:1)\n" + 
            "            TableCellBox: <th>[X:31 Y:28 W:69 H:22 ]\n" + 
            "              ParagraphBox[X:2 Y:2 W:43 H:18 ]\n" + 
            "                LineBox: <th>[X:22 Y:0 W:21 H:18 ]\n" + 
            "                  DocumentTextBox: 'sc2'[X:0 Y:0 W:21 H:18 ](Length:3, StartRel:1)\n" + 
            "        TableCellBox: <td>[X:301 Y:52 W:147 H:76 ]\n" + 
            "          ParagraphBox[X:2 Y:17 W:75 H:18 ]\n" + 
            "            LineBox: <td>[X:68 Y:0 W:7 H:18 ]\n" + 
            "              DocumentTextBox: 'b'[X:0 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
            "", 
                dump.toString());
      }
    } finally {
      Options.getInstance().setBooleanProperty(OptionTags.AUTHOR_SCHEMA_AWARE_NORMALIZE_FORMAT, true);
    }
  }

  /**
   * Test the resizing of the table columns when there is only one column 
   * specification. 
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testEXM_14511_2() throws Exception {
    String xml =
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <sect1>\n" + 
      "<table frame=\"hsides\" width=\"250\">\n" + 
      "            <caption>Sample HTML Table</caption>\n" +
      "    <col width=\"50px\"/>\n" +
      "            <thead>\n" + 
      "                <tr>\n" + 
      "                    <th>Person Name</th>\n" + 
      "                </tr>\n" + 
      "            </thead>\n" + 
      "        </table>" + 
      "    </sect1>\n" + 
      "</article>";
    
    initEditor(xml);
    flushAWTBetter();
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    int x = sendMousePressed(vViewport, 1, true);
    flushAWTBetter();
    sendMouseReleased(vViewport, x + 100);
    flushAWTBetter();
    Thread.sleep(300);
    
    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table frame=\"hsides\" width=\"156\">\n" + 
        "            <caption>Sample HTML Table</caption>\n" + 
        "            <col width=\"152px\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", 
        serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * TC for table resizing when there are fixed cell widths. 
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testTableResizingFixedCW() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
      "    <body>\n" + 
      "        <table>\n" + 
      "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
      "            <col width=\"4cm\"/>\n" + 
      "            <col width=\"4em\"/>\n" +
      "            <col width=\"4ex\"/>\n" +
      "            <col width=\"4in\"/>\n" +
      "            <col width=\"40mm\"/>\n" +
      "            <col width=\"40pc\"/>\n" +
      "            <col width=\"40px\"/>\n" +
      "            <col width=\"40pt\"/>\n" +
      "            <tbody>\n" + 
      "                <tr>\n" + 
      "                    <td>cm col</td>\n" + 
      "                    <td>em col</td>\n" +
      "                    <td>ex col</td>\n" +
      "                    <td>in col</td>\n" +
      "                    <td>mm col</td>\n" +
      "                    <td>pc col</td>\n" +
      "                    <td>px col</td>\n" +
      "                    <td>pt col</td>\n" +
      "                </tr>\n" + 
      "            </tbody>\n" + 
      "        </table>\n" + 
      "    </body>\n" + 
      "</html>"; 
    
    initEditor(xml);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    sleep(200);
    flushAWTBetter();

    int x = sendMousePressed(vViewport, 1, true);
    sendMouseReleased(vViewport, x + 100);
    sleep(200);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table>\n" + 
        "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
        "            <col width=\"6.77cm\" />\n" + 
        "            <col width=\"4em\" />\n" + 
        "            <col width=\"4ex\" />\n" + 
        "            <col width=\"4in\" />\n" + 
        "            <col width=\"40mm\" />\n" + 
        "            <col width=\"40pc\" />\n" + 
        "            <col width=\"40px\" />\n" + 
        "            <col width=\"40pt\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>cm col</td>\n" + 
        "                    <td>em col</td>\n" + 
        "                    <td>ex col</td>\n" + 
        "                    <td>in col</td>\n" + 
        "                    <td>mm col</td>\n" + 
        "                    <td>pc col</td>\n" + 
        "                    <td>px col</td>\n" + 
        "                    <td>pt col</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x + 100);
    sleep(200);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table>\n" + 
        "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
        "            <col width=\"6.77cm\" />\n" + 
        "            <col width=\"11em\" />\n" + 
        "            <col width=\"4ex\" />\n" + 
        "            <col width=\"4in\" />\n" + 
        "            <col width=\"40mm\" />\n" + 
        "            <col width=\"40pc\" />\n" + 
        "            <col width=\"40px\" />\n" + 
        "            <col width=\"40pt\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>cm col</td>\n" + 
        "                    <td>em col</td>\n" + 
        "                    <td>ex col</td>\n" + 
        "                    <td>in col</td>\n" + 
        "                    <td>mm col</td>\n" + 
        "                    <td>pc col</td>\n" + 
        "                    <td>px col</td>\n" + 
        "                    <td>pt col</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));

    x = sendMousePressed(vViewport, 3, true);
    sendMouseReleased(vViewport, x + 100);
    sleep(200);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table>\n" + 
        "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
        "            <col width=\"6.77cm\" />\n" + 
        "            <col width=\"11em\" />\n" + 
        "            <col width=\"15.71ex\" />\n" + 
        "            <col width=\"4in\" />\n" + 
        "            <col width=\"40mm\" />\n" + 
        "            <col width=\"40pc\" />\n" + 
        "            <col width=\"40px\" />\n" + 
        "            <col width=\"40pt\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>cm col</td>\n" + 
        "                    <td>em col</td>\n" + 
        "                    <td>ex col</td>\n" + 
        "                    <td>in col</td>\n" + 
        "                    <td>mm col</td>\n" + 
        "                    <td>pc col</td>\n" + 
        "                    <td>px col</td>\n" + 
        "                    <td>pt col</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));

    x = sendMousePressed(vViewport, 4, true);
    sendMouseReleased(vViewport, x + 100);
    sleep(200);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table>\n" + 
        "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
        "            <col width=\"6.77cm\" />\n" + 
        "            <col width=\"11em\" />\n" + 
        "            <col width=\"15.71ex\" />\n" + 
        "            <col width=\"5.09in\" />\n" + 
        "            <col width=\"40mm\" />\n" + 
        "            <col width=\"40pc\" />\n" + 
        "            <col width=\"40px\" />\n" + 
        "            <col width=\"40pt\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>cm col</td>\n" + 
        "                    <td>em col</td>\n" + 
        "                    <td>ex col</td>\n" + 
        "                    <td>in col</td>\n" + 
        "                    <td>mm col</td>\n" + 
        "                    <td>pc col</td>\n" + 
        "                    <td>px col</td>\n" + 
        "                    <td>pt col</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 5, true);
    sendMouseReleased(vViewport, x + 100);
    sleep(200);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table>\n" + 
        "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
        "            <col width=\"6.77cm\" />\n" + 
        "            <col width=\"11em\" />\n" + 
        "            <col width=\"15.71ex\" />\n" + 
        "            <col width=\"5.09in\" />\n" + 
        "            <col width=\"67.73mm\" />\n" + 
        "            <col width=\"40pc\" />\n" + 
        "            <col width=\"40px\" />\n" + 
        "            <col width=\"40pt\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>cm col</td>\n" + 
        "                    <td>em col</td>\n" + 
        "                    <td>ex col</td>\n" + 
        "                    <td>in col</td>\n" + 
        "                    <td>mm col</td>\n" + 
        "                    <td>pc col</td>\n" + 
        "                    <td>px col</td>\n" + 
        "                    <td>pt col</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 6, true);
    sendMouseReleased(vViewport, x + 100);
    sleep(200);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table>\n" + 
        "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
        "            <col width=\"6.77cm\" />\n" + 
        "            <col width=\"11em\" />\n" + 
        "            <col width=\"15.71ex\" />\n" + 
        "            <col width=\"5.09in\" />\n" + 
        "            <col width=\"67.73mm\" />\n" + 
        "            <col width=\"46.53pc\" />\n" + 
        "            <col width=\"40px\" />\n" + 
        "            <col width=\"40pt\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>cm col</td>\n" + 
        "                    <td>em col</td>\n" + 
        "                    <td>ex col</td>\n" + 
        "                    <td>in col</td>\n" + 
        "                    <td>mm col</td>\n" + 
        "                    <td>pc col</td>\n" + 
        "                    <td>px col</td>\n" + 
        "                    <td>pt col</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 7, true);
    sendMouseReleased(vViewport, x + 100);
    sleep(200);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table>\n" + 
        "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
        "            <col width=\"6.77cm\" />\n" + 
        "            <col width=\"11em\" />\n" + 
        "            <col width=\"15.71ex\" />\n" + 
        "            <col width=\"5.09in\" />\n" + 
        "            <col width=\"67.73mm\" />\n" + 
        "            <col width=\"46.53pc\" />\n" + 
        "            <col width=\"138px\" />\n" + 
        "            <col width=\"40pt\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>cm col</td>\n" + 
        "                    <td>em col</td>\n" + 
        "                    <td>ex col</td>\n" + 
        "                    <td>in col</td>\n" + 
        "                    <td>mm col</td>\n" + 
        "                    <td>pc col</td>\n" + 
        "                    <td>px col</td>\n" + 
        "                    <td>pt col</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 8, true);
    sendMouseReleased(vViewport, x + 100);
    sleep(200);
    flushAWTBetter();
    
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
        "    <body>\n" + 
        "        <table width=\"2210\">\n" + 
        "            <caption>Sample HTML Table with fixed width and proportional column widths</caption>\n" + 
        "            <col width=\"6.77cm\" />\n" + 
        "            <col width=\"11em\" />\n" + 
        "            <col width=\"15.71ex\" />\n" + 
        "            <col width=\"5.09in\" />\n" + 
        "            <col width=\"67.73mm\" />\n" + 
        "            <col width=\"46.53pc\" />\n" + 
        "            <col width=\"138px\" />\n" + 
        "            <col width=\"118.4pt\" />\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>cm col</td>\n" + 
        "                    <td>em col</td>\n" + 
        "                    <td>ex col</td>\n" + 
        "                    <td>in col</td>\n" + 
        "                    <td>mm col</td>\n" + 
        "                    <td>pc col</td>\n" + 
        "                    <td>px col</td>\n" + 
        "                    <td>pt col</td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </body>\n" + 
        "</html>\n",
        serializeDocumentViewport(vViewport, false));
  }

  /**
   * Test the resizing of tables.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTableEXM_19128() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
      "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
      "<article>\n" + 
      "    <title>Article Title</title>\n" + 
      "    <sect1>\n" + 
      "        <title>Section1 Title</title>\n" + 
      "        <para>\n" + 
      "            <table frame=\"void\" width=\"100\">\n" + 
      "                <caption>UnbreakableLongCaptionText.</caption>\n" + 
      "                <col width=\"50%\"/>\n" + 
      "                <col width=\"50%\"/>\n" + 
      "                <thead>\n" + 
      "                    <tr>\n" + 
      "                        <th/>\n" + 
      "                        <th/>\n" + 
      "                    </tr>\n" + 
      "                </thead>\n" + 
      "                <tbody>\n" + 
      "                    <tr>\n" + 
      "                        <td/>\n" + 
      "                        <td/>\n" + 
      "                    </tr>\n" + 
      "                </tbody>\n" + 
      "            </table>\n" + 
      "        </para>\n" + 
      "    </sect1>\n" + 
      "</article> ";
    
    initEditor(xml);
    flushAWTBetter();
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    int x = sendMousePressed(vViewport, 2, false);
    flushAWTBetter();
    sendMouseReleased(vViewport, x + 20);
    flushAWTBetter();
    String serialized = serializeDocumentViewport(vViewport, false);
    System.out.println(serialized);
    assertTrue(
        serialized.indexOf(
            " <table frame=\"void\" width=\"100\">\n" + 
        		"                <caption>UnbreakableLongCaptionText.</caption>\n" + 
        		"                <col width=\"73%\"/>\n" + 
        		"                <col width=\"27%\"/>") != -1);
   
   
    // Again, this time with a larger delta.
    initEditor(xml);
    x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x + 50);
    serialized = serializeDocumentViewport(vViewport, true);
    assertTrue( 
        serialized.indexOf(" <table frame=\"void\" width=\"148\">\n" + 
            "                <caption>UnbreakableLongCaptionText.</caption>\n" + 
            "                <col width=\"85%\"/>\n" + 
            "                <col width=\"15%\"/>") != -1);
   
  }

  /**
   * <p><b>Description:</b> Obey alignment of colspecs in cells content.</p>
   * <p><b>Bug ID:</b> EXM-9928</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testObeyColspecAlignsInCellContentEXM_9928() throws Exception {
    open(URLUtil.correct(new File("test/authorExtensions/EXM-26379-5.xml")), true);
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
          "<RootBox>[X:0 Y:0 W:524 H:209 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:9 W:524 H:187 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:524 H:187 ]\n" + 
          "    BlockElementBox: <title>[X:0 Y:0 W:400 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:70 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:70 H:18 ]\n" + 
          "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
          "            StaticTextBox: 'Article: '[X:0 Y:0 W:63 H:18 ]\n" + 
          "          DocumentTextBox: 'T'[X:63 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:28 W:524 H:159 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:84 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:84 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'T'[X:77 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "      TableBox: <table>[X:16 Y:31 W:504 H:128 ]\n" + 
          "        BlockElementBox: <caption>[X:0 Y:0 W:380 H:18 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:42 H:18 ]\n" + 
          "            LineBox: <caption>[X:0 Y:0 W:42 H:18 ]\n" + 
          "              DocumentTextBox: 'dsadsa'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "        BlockElementBox: <col>[X:0 Y:18 W:504 H:31 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
          "            LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
          "              InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
          "                StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:'1'\n" + 
          "                StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'50%'\n" + 
          "                StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
          "        BlockElementBox: <col>[X:0 Y:49 W:504 H:31 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
          "            LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
          "              InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
          "                StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:'1'\n" + 
          "                StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'50%'\n" + 
          "                StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
          "        TableCellBox: <td>[X:2 Y:82 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:184 H:18 ]\n" + 
          "            LineBox: <td>[X:149 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text1'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:190 Y:82 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:109 H:18 ]\n" + 
          "            LineBox: <td>[X:74 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text2'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:2 Y:104 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:184 H:18 ]\n" + 
          "            LineBox: <td>[X:149 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text3'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:190 Y:104 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:109 H:18 ]\n" + 
          "            LineBox: <td>[X:74 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text4'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
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
          "<RootBox>[X:0 Y:0 W:524 H:209 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:9 W:524 H:187 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:524 H:187 ]\n" + 
          "    BlockElementBox: <title>[X:0 Y:0 W:400 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:70 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:70 H:18 ]\n" + 
          "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
          "            StaticTextBox: 'Article: '[X:0 Y:0 W:63 H:18 ]\n" + 
          "          DocumentTextBox: 'T'[X:63 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:28 W:524 H:159 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:84 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:84 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'T'[X:77 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "      TableBox: <table>[X:16 Y:31 W:504 H:128 ]\n" + 
          "        BlockElementBox: <caption>[X:0 Y:0 W:380 H:18 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:42 H:18 ]\n" + 
          "            LineBox: <caption>[X:0 Y:0 W:42 H:18 ]\n" + 
          "              DocumentTextBox: 'dsadsa'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "        BlockElementBox: <col>[X:0 Y:18 W:504 H:31 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
          "            LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
          "              InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
          "                StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:'1'\n" + 
          "                StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'50%'\n" + 
          "                StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
          "        BlockElementBox: <col>[X:0 Y:49 W:504 H:31 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
          "            LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
          "              InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
          "                StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:'1'\n" + 
          "                StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'50%'\n" + 
          "                StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
          "        TableCellBox: <td>[X:2 Y:82 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:184 H:18 ]\n" + 
          "            LineBox: <td>[X:149 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text1'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:190 Y:82 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:109 H:18 ]\n" + 
          "            LineBox: <td>[X:74 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text2'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:2 Y:104 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:184 H:18 ]\n" + 
          "            LineBox: <td>[X:149 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text3'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:190 Y:104 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:113 H:18 ]\n" + 
          "            LineBox: <td>[X:71 Y:0 W:42 H:18 ]\n" + 
          "              DocumentTextBox: 'text4a'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
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
          "<RootBox>[X:0 Y:0 W:524 H:209 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:9 W:524 H:187 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:524 H:187 ]\n" + 
          "    BlockElementBox: <title>[X:0 Y:0 W:400 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:70 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:70 H:18 ]\n" + 
          "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
          "            StaticTextBox: 'Article: '[X:0 Y:0 W:63 H:18 ]\n" + 
          "          DocumentTextBox: 'T'[X:63 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:28 W:524 H:159 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:84 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:84 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'T'[X:77 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "      TableBox: <table>[X:16 Y:31 W:504 H:128 ]\n" + 
          "        BlockElementBox: <caption>[X:0 Y:0 W:380 H:18 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:42 H:18 ]\n" + 
          "            LineBox: <caption>[X:0 Y:0 W:42 H:18 ]\n" + 
          "              DocumentTextBox: 'dsadsa'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "        BlockElementBox: <col>[X:0 Y:18 W:504 H:31 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
          "            LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
          "              InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
          "                StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:'1'\n" + 
          "                StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'50%'\n" + 
          "                StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
          "        BlockElementBox: <col>[X:0 Y:49 W:504 H:31 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
          "            LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
          "              InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
          "                StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:'1'\n" + 
          "                StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'50%'\n" + 
          "                StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
          "        TableCellBox: <td>[X:2 Y:82 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:184 H:18 ]\n" + 
          "            LineBox: <td>[X:149 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text1'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:190 Y:82 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:109 H:18 ]\n" + 
          "            LineBox: <td>[X:74 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text2'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:2 Y:104 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:184 H:18 ]\n" + 
          "            LineBox: <td>[X:149 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text3'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:190 Y:104 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:184 H:18 ]\n" + 
          "            LineBox: <td>[X:142 Y:0 W:42 H:18 ]\n" + 
          "              DocumentTextBox: 'text4a'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }

  /**
   * <p><b>Description:</b> When a table row has @align attribute, it is more powerful than colspec @align.</p>
   * <p><b>Bug ID:</b> EXM-9928</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testRejectColspecAlignsInCellContentWhenTRHasAlign() throws Exception {
    open(URLUtil.correct(new File("test/authorExtensions/EXM-26379-6.xml")), true);
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
    
    //The first row should be left aligned and the second row be right aligned because the align attributes set on the parent elements (tr) and (tgroup) have more importance..
    if(PlatformDetector.isWin32()){
      assertEquals( 
          "Please run this test on Linux", 
              dump.toString());
    } else {
      assertEquals( 
          "<RootBox>[X:0 Y:0 W:524 H:209 ]\n" + 
          "BlockElementBox: <#document>[X:0 Y:9 W:524 H:187 ]\n" + 
          "  BlockElementBox: <article>[X:0 Y:0 W:524 H:187 ]\n" + 
          "    BlockElementBox: <title>[X:0 Y:0 W:400 H:19 ]\n" + 
          "      ParagraphBox[X:0 Y:0 W:70 H:18 ]\n" + 
          "        LineBox: <title>[X:0 Y:0 W:70 H:18 ]\n" + 
          "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
          "            StaticTextBox: 'Article: '[X:0 Y:0 W:63 H:18 ]\n" + 
          "          DocumentTextBox: 'T'[X:63 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "    BlockElementBox: <sect1>[X:0 Y:28 W:524 H:159 ]\n" + 
          "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
          "        ParagraphBox[X:0 Y:0 W:84 H:18 ]\n" + 
          "          LineBox: <title>[X:0 Y:0 W:84 H:18 ]\n" + 
          "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
          "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
          "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
          "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
          "            DocumentTextBox: 'T'[X:77 Y:0 W:7 H:18 ](Length:1, StartRel:1)\n" + 
          "      TableBox: <table>[X:16 Y:31 W:504 H:128 ]\n" + 
          "        BlockElementBox: <caption>[X:0 Y:0 W:380 H:18 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:42 H:18 ]\n" + 
          "            LineBox: <caption>[X:0 Y:0 W:42 H:18 ]\n" + 
          "              DocumentTextBox: 'dsadsa'[X:0 Y:0 W:42 H:18 ](Length:6, StartRel:1)\n" + 
          "        BlockElementBox: <col>[X:0 Y:18 W:504 H:31 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
          "            LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
          "              InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
          "                StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'50%'\n" + 
          "                StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
          "        BlockElementBox: <col>[X:0 Y:49 W:504 H:31 ]\n" + 
          "          ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
          "            LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
          "              InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
          "                StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:''\n" + 
          "                StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'50%'\n" + 
          "                StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
          "                StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
          "        TableCellBox: <td>[X:2 Y:82 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:35 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text1'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:190 Y:82 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:35 H:18 ]\n" + 
          "            LineBox: <td>[X:0 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text2'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:2 Y:104 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:184 H:18 ]\n" + 
          "            LineBox: <td>[X:149 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text3'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "        TableCellBox: <td>[X:190 Y:104 W:188 H:22 ]\n" + 
          "          ParagraphBox[X:2 Y:2 W:184 H:18 ]\n" + 
          "            LineBox: <td>[X:149 Y:0 W:35 H:18 ]\n" + 
          "              DocumentTextBox: 'text4'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
          "", 
              dump.toString());
    }
  }
  
  /**
   * <p><b>Description:</b> Test that the align property from the 'col' specification is applied.p>
   * <p><b>Bug ID:</b> EXM-28845</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testEXM_28845AlignCells() throws Exception {
    String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <title>Article Title</title>\n" + 
        "    <sect1>\n" + 
        "        <title>Section1 Title</title>\n" + 
        "        <para>\n" + 
        "            <table width=\"800\" frame=\"border\" rules=\"all\">\n" + 
        "                <caption>Alignment provider failure.</caption>\n" + 
        "                <col width=\"74%\" align=\"right\" title=\"1\"/>\n" + 
        "                <col width=\"26%\" title=\"2\"/>            \n" + 
        "                <tbody>\n" + 
        "                    <tr>\n" + 
        "                        <td>RIGHT</td>\n" + 
        "                        <td>SHOULD BE LEFT</td>\n" + 
        "                    </tr>\n" + 
        "                    <tr>\n" + 
        "                        <td colspan=\"2\">RIGHT</td>\n" + 
        "                    </tr>\n" + 
        "                </tbody>\n" + 
        "            </table>" + 
        "        </para>\n" + 
        "    </sect1>\n" + 
        "</article> ";
      
      initEditor(xml);
      final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
      Box tableBox = findBox(TableBox.class, 1, vViewport.getRootBox());
      assertNotNull(tableBox);
      assertEquals("table", tableBox.getElement().getName());
      
      HTMLTableCellInfoProvider cellInfoProvider = new HTMLTableCellInfoProvider();
      cellInfoProvider.init((AuthorElement) tableBox.getElement());
      
      List<WidthRepresentation> widthRepresentations = cellInfoProvider.getAllColspecWidthRepresentations();
      assertEquals("right", widthRepresentations.get(0).getAlign());
      assertNull(widthRepresentations.get(1).getAlign());
      
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
      if(PlatformDetector.isWin32()){
        assertEquals( 
            "Please run this test on Linux", 
                dump.toString());
      } else {
        // The second cell from the first row should have text aligned to the left.
        assertEquals( 
            "<RootBox>[X:0 Y:0 W:820 H:209 ]\n" + 
            "BlockElementBox: <#document>[X:0 Y:9 W:820 H:187 ]\n" + 
            "  BlockElementBox: <article>[X:0 Y:0 W:820 H:187 ]\n" + 
            "    BlockElementBox: <title>[X:0 Y:0 W:400 H:19 ]\n" + 
            "      ParagraphBox[X:0 Y:0 W:154 H:18 ]\n" + 
            "        LineBox: <title>[X:0 Y:0 W:154 H:18 ]\n" + 
            "          InlineStaticContentForElementBox: <before>[X:0 Y:0 W:63 H:18 ]\n" + 
            "            StaticTextBox: 'Article: '[X:0 Y:0 W:63 H:18 ]\n" + 
            "          DocumentTextBox: 'Article Title'[X:63 Y:0 W:91 H:18 ](Length:13, StartRel:1)\n" + 
            "    BlockElementBox: <sect1>[X:0 Y:28 W:820 H:159 ]\n" + 
            "      BlockElementBox: <title>[X:12 Y:0 W:388 H:18 ]\n" + 
            "        ParagraphBox[X:0 Y:0 W:175 H:18 ]\n" + 
            "          LineBox: <title>[X:0 Y:0 W:175 H:18 ]\n" + 
            "            InlineStaticContentForElementBox: <before>[X:0 Y:0 W:77 H:18 ]\n" + 
            "              StaticTextBox: 'Section '[X:0 Y:0 W:56 H:18 ]\n" + 
            "              StaticTextBox: '1'[X:56 Y:0 W:7 H:18 ]\n" + 
            "              StaticTextBox: ': '[X:63 Y:0 W:14 H:18 ]\n" + 
            "            DocumentTextBox: 'Section1 Title'[X:77 Y:0 W:98 H:18 ](Length:14, StartRel:1)\n" + 
            "      BlockElementBox: <para>[X:12 Y:31 W:808 H:128 ]\n" + 
            "        TableBox: <table>[X:4 Y:0 W:800 H:128 ]\n" + 
            "          BlockElementBox: <caption>[X:0 Y:0 W:800 H:18 ]\n" + 
            "            ParagraphBox[X:0 Y:0 W:189 H:18 ]\n" + 
            "              LineBox: <caption>[X:0 Y:0 W:189 H:18 ]\n" + 
            "                DocumentTextBox: 'Alignment provider failure.'[X:0 Y:0 W:189 H:18 ](Length:27, StartRel:1)\n" + 
            "          BlockElementBox: <col>[X:0 Y:18 W:800 H:31 ]\n" + 
            "            ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
            "              LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
            "                InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
            "                  StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
            "                  StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:'1'\n" + 
            "                  StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
            "                  StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:'1'\n" + 
            "                  StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
            "                  StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'74%'\n" + 
            "                  StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
            "                  StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
            "          BlockElementBox: <col>[X:0 Y:49 W:800 H:31 ]\n" + 
            "            ParagraphBox[X:0 Y:0 W:504 H:31 ]\n" + 
            "              LineBox: <col>[X:0 Y:0 W:504 H:31 ]\n" + 
            "                InlineStaticContentForElementBox: <after>[X:0 Y:0 W:504 H:31 ]\n" + 
            "                  StaticTextBox: '  title '[X:0 Y:4 W:56 H:18 ]\n" + 
            "                  StaticEditBox: '{columns=7, edit=@title, edit_qualified=title, type=text}'[X:56 Y:3 W:65 H:21 ]value:'2'\n" + 
            "                  StaticTextBox: '  span '[X:121 Y:4 W:49 H:18 ]\n" + 
            "                  StaticEditBox: '{columns=7, edit=@span, edit_qualified=span, type=text}'[X:170 Y:3 W:65 H:21 ]value:'1'\n" + 
            "                  StaticTextBox: '  width '[X:235 Y:4 W:56 H:18 ]\n" + 
            "                  StaticEditBox: '{columns=7, edit=@width, edit_qualified=width, type=text}'[X:291 Y:3 W:65 H:21 ]value:'26%'\n" + 
            "                  StaticTextBox: '  align '[X:356 Y:4 W:56 H:18 ]\n" + 
            "                  StaticEditBox: '{canRemoveValue=true, columns=10, edit=@align, edit_qualified=align, editable=false, type=combo}'[X:412 Y:0 W:92 H:26 ]\n" + 
            "          TableCellBox: <td>[X:2 Y:82 W:589 H:22 ]\n" + 
            "            ParagraphBox[X:2 Y:2 W:585 H:18 ]\n" + 
            "              LineBox: <td>[X:550 Y:0 W:35 H:18 ]\n" + 
            "                DocumentTextBox: 'RIGHT'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
            "          TableCellBox: <td>[X:591 Y:82 W:207 H:22 ]\n" + 
            "            ParagraphBox[X:2 Y:2 W:98 H:18 ]\n" + 
            "              LineBox: <td>[X:0 Y:0 W:98 H:18 ]\n" + 
            "                DocumentTextBox: 'SHOULD BE LEFT'[X:0 Y:0 W:98 H:18 ](Length:14, StartRel:1)\n" + 
            "          TableCellBox: <td>[X:2 Y:104 W:796 H:22 ]\n" + 
            "            ParagraphBox[X:2 Y:2 W:792 H:18 ]\n" + 
            "              LineBox: <td>[X:757 Y:0 W:35 H:18 ]\n" + 
            "                DocumentTextBox: 'RIGHT'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
            "", 
            dump.toString());
      }
  }
  
  /**
   * <p><b>Description:</b> Do not overwrite other col element attributes when resizing.</p>
   * <p><b>Bug ID:</b> EXM-28950</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testEXM_28950DoNotOverwriteOtherAttrsWhenResizing() throws Exception {
    String xml =
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
            "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
            "<article>\n" + 
            "    <sect1>\n" + 
            "<table width=\"350\" frame=\"border\" rules=\"all\">\n" + 
            "            <caption>Sample HTML Table with fixed width.</caption>\n" + 
            "            <colgroup>\n" + 
            "                <col width=\"80%\" align=\"center\"/>\n" + 
            "            </colgroup>\n" + 
            "            <colgroup>\n" + 
            "                <col width=\"20%\" align=\"right\"/>\n" + 
            "            </colgroup>\n" + 
            "            <thead>\n" + 
            "                <tr>\n" + 
            "                    <th>Person Name</th>\n" + 
            "                    <th>Age</th>\n" + 
            "                </tr>\n" + 
            "            </thead>\n" + 
            "            <tbody>\n" + 
            "                <tr>\n" + 
            "                    <td>Jane</td>\n" + 
            "                    <td>26</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Bart </td>\n" + 
            "                    <td>24</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Alexander</td>\n" + 
            "                    <td>22</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td colspan=\"2\">\n" + 
            "                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
            "                            department</emphasis>\n" + 
            "                    </td>\n" + 
            "                </tr>\n" + 
            "            </tbody>\n" + 
            "        </table>" + 
            "    </sect1>\n" + 
            "</article>";

    initEditor(xml);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    Thread.sleep(300);

    int x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 200);

    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
            "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
            "<article>\n" + 
            "    <sect1>\n" + 
            "        <table width=\"350\" frame=\"border\" rules=\"all\">\n" + 
            "            <caption>Sample HTML Table with fixed width.</caption>\n" + 
            "            <colgroup>\n" + 
            "                <col width=\"23%\" align=\"center\"/>\n" + 
            "            </colgroup>\n" + 
            "            <colgroup>\n" + 
            "                <col width=\"77%\" align=\"right\"/>\n" + 
            "            </colgroup>\n" + 
            "            <thead>\n" + 
            "                <tr>\n" + 
            "                    <th>Person Name</th>\n" + 
            "                    <th>Age</th>\n" + 
            "                </tr>\n" + 
            "            </thead>\n" + 
            "            <tbody>\n" + 
            "                <tr>\n" + 
            "                    <td>Jane</td>\n" + 
            "                    <td>26</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Bart </td>\n" + 
            "                    <td>24</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Alexander</td>\n" + 
            "                    <td>22</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
            "                        science department</emphasis> </td>\n" + 
            "                </tr>\n" + 
            "            </tbody>\n" + 
            "        </table>\n" + 
            "    </sect1>\n" + 
            "</article>\n" + 
            "", 
            serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * <p><b>Description:</b> Do not overwrite other col element attributes when resizing.</p>
   * <p><b>Bug ID:</b> EXM-28950</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testEXM_28950DoNotOverwriteOtherAttrsWhenResizing2() throws Exception {
    String xml =
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
            "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
            "<article>\n" + 
            "    <sect1>\n" + 
            "<table width=\"350\" frame=\"border\" rules=\"all\">\n" + 
            "            <caption>Sample HTML Table with fixed width.</caption>\n" + 
            "                <col width=\"80%\" align=\"center\"/>\n" + 
            "                <col width=\"20%\" align=\"right\"/>\n" + 
            "            <thead>\n" + 
            "                <tr>\n" + 
            "                    <th>Person Name</th>\n" + 
            "                    <th>Age</th>\n" + 
            "                </tr>\n" + 
            "            </thead>\n" + 
            "            <tbody>\n" + 
            "                <tr>\n" + 
            "                    <td>Jane</td>\n" + 
            "                    <td>26</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Bart </td>\n" + 
            "                    <td>24</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Alexander</td>\n" + 
            "                    <td>22</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td colspan=\"2\">\n" + 
            "                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
            "                            department</emphasis>\n" + 
            "                    </td>\n" + 
            "                </tr>\n" + 
            "            </tbody>\n" + 
            "        </table>" + 
            "    </sect1>\n" + 
            "</article>";

    initEditor(xml);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    Thread.sleep(300);

    int x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 200);

    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
            "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
            "<article>\n" + 
            "    <sect1>\n" + 
            "        <table width=\"350\" frame=\"border\" rules=\"all\">\n" + 
            "            <caption>Sample HTML Table with fixed width.</caption>\n" + 
            "            <col width=\"23%\" align=\"center\"/>\n" + 
            "            <col width=\"77%\" align=\"right\"/>\n" + 
            "            <thead>\n" + 
            "                <tr>\n" + 
            "                    <th>Person Name</th>\n" + 
            "                    <th>Age</th>\n" + 
            "                </tr>\n" + 
            "            </thead>\n" + 
            "            <tbody>\n" + 
            "                <tr>\n" + 
            "                    <td>Jane</td>\n" + 
            "                    <td>26</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Bart </td>\n" + 
            "                    <td>24</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Alexander</td>\n" + 
            "                    <td>22</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
            "                        science department</emphasis> </td>\n" + 
            "                </tr>\n" + 
            "            </tbody>\n" + 
            "        </table>\n" + 
            "    </sect1>\n" + 
            "</article>\n" + 
            "", 
            serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * <p><b>Description:</b> Do not overwrite other col element attributes when resizing.</p>
   * <p><b>Bug ID:</b> EXM-28950</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testEXM_28950DoNotOverwriteOtherAttrsWhenResizing3() throws Exception {
    String xml =
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
            "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
            "<article>\n" + 
            "    <sect1>\n" + 
            "<table width=\"350\" frame=\"border\" rules=\"all\">\n" + 
            "            <caption>Sample HTML Table with fixed width.</caption>\n" + 
            "                <col width=\"80%\" align=\"center\"/>\n" + 
            "            <thead>\n" + 
            "                <tr>\n" + 
            "                    <th>Person Name</th>\n" + 
            "                    <th>Age</th>\n" + 
            "                </tr>\n" + 
            "            </thead>\n" + 
            "            <tbody>\n" + 
            "                <tr>\n" + 
            "                    <td>Jane</td>\n" + 
            "                    <td>26</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Bart </td>\n" + 
            "                    <td>24</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Alexander</td>\n" + 
            "                    <td>22</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td colspan=\"2\">\n" + 
            "                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
            "                            department</emphasis>\n" + 
            "                    </td>\n" + 
            "                </tr>\n" + 
            "            </tbody>\n" + 
            "        </table>" + 
            "    </sect1>\n" + 
            "</article>";

    initEditor(xml);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    Thread.sleep(300);

    int x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 200);

    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
            "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
            "<article>\n" + 
            "    <sect1>\n" + 
            "        <table width=\"350\" frame=\"border\" rules=\"all\">\n" + 
            "            <caption>Sample HTML Table with fixed width.</caption>\n" + 
            "            <col width=\"35%\" align=\"center\"/>\n" + 
            "            <thead>\n" + 
            "                <tr>\n" + 
            "                    <th>Person Name</th>\n" + 
            "                    <th>Age</th>\n" + 
            "                </tr>\n" + 
            "            </thead>\n" + 
            "            <tbody>\n" + 
            "                <tr>\n" + 
            "                    <td>Jane</td>\n" + 
            "                    <td>26</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Bart </td>\n" + 
            "                    <td>24</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Alexander</td>\n" + 
            "                    <td>22</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
            "                        science department</emphasis> </td>\n" + 
            "                </tr>\n" + 
            "            </tbody>\n" + 
            "        </table>\n" + 
            "    </sect1>\n" + 
            "</article>\n" + 
            "", 
            serializeDocumentViewport(vViewport, false));
  }
  
  /**
   * <p><b>Description:</b> Do not overwrite other col element attributes when resizing.</p>
   * <p><b>Bug ID:</b> EXM-28950</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testEXM_28950DoNotOverwriteOtherAttrsWhenResizing4() throws Exception {
    String xml =
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
            "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
            "<article>\n" + 
            "    <sect1>\n" + 
            "<table width=\"350\" frame=\"border\" rules=\"all\">\n" + 
            "            <caption>Sample HTML Table with fixed width.</caption>\n" + 
            "            <thead>\n" + 
            "                <tr>\n" + 
            "                    <th>Person Name</th>\n" + 
            "                    <th>Age</th>\n" + 
            "                </tr>\n" + 
            "            </thead>\n" + 
            "            <tbody>\n" + 
            "                <tr>\n" + 
            "                    <td>Jane</td>\n" + 
            "                    <td>26</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Bart </td>\n" + 
            "                    <td>24</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td>Alexander</td>\n" + 
            "                    <td>22</td>\n" + 
            "                </tr>\n" + 
            "                <tr>\n" + 
            "                    <td colspan=\"2\">\n" + 
            "                        <emphasis role=\"italic\">They are all students of the computer science\n" + 
            "                            department</emphasis>\n" + 
            "                    </td>\n" + 
            "                </tr>\n" + 
            "            </tbody>\n" + 
            "        </table>" + 
            "    </sect1>\n" + 
            "</article>";

    initEditor(xml);
    final AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {
      @Override
      protected int shouldAddWidthSpecifications() {
        addTWAsked = true;
        return AuthorTableResizeDialogPresenter.ADD_PROPORTIONAL_COLUMN_WIDTHS;
      }
    });
    Thread.sleep(300);

    int x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x - 100);

    assertEquals( 
        "<!DOCTYPE article PUBLIC \"-//OASIS//DTD DocBook XML V4.4//EN\"\n" + 
        "                         \"http://www.docbook.org/xml/4.4/docbookx.dtd\">\n" + 
        "<article>\n" + 
        "    <sect1>\n" + 
        "        <table width=\"350\" frame=\"border\" rules=\"all\">\n" + 
        "            <caption>Sample HTML Table with fixed width.</caption>\n" + 
        "            <col width=\"76%\"/>\n" + 
        "            <col width=\"24%\"/>\n" + 
        "            <thead>\n" + 
        "                <tr>\n" + 
        "                    <th>Person Name</th>\n" + 
        "                    <th>Age</th>\n" + 
        "                </tr>\n" + 
        "            </thead>\n" + 
        "            <tbody>\n" + 
        "                <tr>\n" + 
        "                    <td>Jane</td>\n" + 
        "                    <td>26</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Bart </td>\n" + 
        "                    <td>24</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td>Alexander</td>\n" + 
        "                    <td>22</td>\n" + 
        "                </tr>\n" + 
        "                <tr>\n" + 
        "                    <td colspan=\"2\"> <emphasis role=\"italic\">They are all students of the computer\n" + 
        "                        science department</emphasis> </td>\n" + 
        "                </tr>\n" + 
        "            </tbody>\n" + 
        "        </table>\n" + 
        "    </sect1>\n" + 
        "</article>\n" + 
        "", 
            serializeDocumentViewport(vViewport, false));
  }
}