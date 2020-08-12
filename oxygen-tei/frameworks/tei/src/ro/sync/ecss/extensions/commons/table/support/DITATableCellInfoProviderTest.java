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
package ro.sync.ecss.extensions.commons.table.support;

import java.util.List;

import ro.sync.ecss.component.AuthorViewport;
import ro.sync.ecss.css.StyleSheet;
import ro.sync.ecss.dom.AuthorDocumentImpl;
import ro.sync.ecss.dom.AuthorElementImpl;
import ro.sync.ecss.dom.AuthorSentinelNode;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.WidthRepresentation;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.layout.DumpConfiguration;
import ro.sync.ecss.layout.LayoutUtils;
import ro.sync.ecss.layout.RootBox;
import ro.sync.ecss.layout.table.AuthorTableResizeDialogPresenter;
import ro.sync.ecss.layout.table.AuthorTableResizingHandler;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.editor.xmleditor.pageauthor.AuthorEditorPage;

/**
 * 
 * Test for DITA tables cell information provider.
 *
 * @author radu_coravu
 */
public class DITATableCellInfoProviderTest extends EditorAuthorExtensionTestBase  {

  /**
   * Constructor.
   */
  public DITATableCellInfoProviderTest() {
    super(false, true);
  }
  
  /**
   * Test the col width 
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testColWidth() throws Exception {
    String xml = 
      "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
      "<topic id=\"topic-1\">\n" + 
      "  <title>Topic title</title>\n" + 
      "  <body>\n" + 
      "    <p>Topic paragraph</p>\n" + 
      "      <simpletable relcolwidth=\"1* 3*\">\n" + 
      "          <sthead>\n" + 
      "              <stentry>Type style</stentry>\n" + 
      "              <stentry>Elements used</stentry>\n" + 
      "          </sthead>\n" + 
      "          <strow>\n" + 
      "              <stentry>Bold</stentry>\n" + 
      "              <stentry>b</stentry>\n" + 
      "          </strow>\n" + 
      "      </simpletable>\n" + 
      "  </body>\n" + 
      "</topic>";

    initEditor(xml);
    AuthorDocumentImpl document = ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
    AuthorElement table = (AuthorElement) ((AuthorSentinelNode) ((AuthorSentinelNode)document.getChild("topic")).getChild("body")).getChild("simpletable");
    AuthorSentinelNode row = (AuthorSentinelNode) (((AuthorSentinelNode) table).getChild("strow"));
    DITATableCellInfoProvider tableSupport = new DITATableCellInfoProvider();
    tableSupport.init(table);
    
    // Col 0
    List<WidthRepresentation> colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(0), 0, 1);
    assertEquals(1, colWidth.size());
    
    WidthRepresentation col = colWidth.get(0);
    int fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(1.0f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
    // Col 1
    colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(1), 1, 1);
    assertEquals(1, colWidth.size());
    
    col = colWidth.get(0);
    fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(3.0f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
  }
  
  /**
   * Test for the layout of the simpletable in DITA.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testDITASimpleTableLayout2() throws Exception { 
    String xml = "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
    		"<topic id=\"topic-1\">\n" + 
    		"  <body>\n" + 
    		"      <simpletable relcolwidth=\"1*\">\n" + 
    		"          <sthead>\n" + 
    		"              <stentry>Elements used</stentry>\n" +
    		"          </sthead>\n" + 
    		"      </simpletable>\n" + 
    		"  </body>\n" + 
    		"</topic>";
    
    initEditor(xml, true, true);
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
    		"<RootBox>[X:0 Y:0 W:400 H:42 ]\n" + 
    		"BlockElementBox: <#document>[X:0 Y:10 W:400 H:22 ]\n" + 
    		"  BlockElementBox: <topic>[X:0 Y:0 W:400 H:22 ]\n" + 
    		"    BlockElementBox: <body>[X:7 Y:0 W:393 H:22 ]\n" + 
    		"      TableBox: <simpletable>[X:5 Y:0 W:383 H:22 ]\n" + 
    		"        TableCellBox: <stentry>[X:1 Y:1 W:381 H:20 ]\n" + 
    		"          ParagraphBox[X:1 Y:1 W:91 H:18 ]\n" + 
    		"            LineBox: <stentry>[X:0 Y:0 W:91 H:18 ]\n" + 
    		"              DocumentTextBox: 'Elements used'[X:0 Y:0 W:91 H:18 ](Length:13, StartRel:1)\n" + 
    		"", dump.toString());
  }
  
  /**
   * Test for the layout of the simpletable in DITA.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testDITASimpleTableNotWrapped() throws Exception { 
    String xml = "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "  <body>\n" + 
        "      <simpletable>\n" + 
        "          <sthead>\n" + 
        "              <stentry>Elements used</stentry>\n" + 
        "          </sthead>\n" + 
        "      </simpletable>\n" + 
        "  </body>\n" + 
        "</topic>";
    
    initEditor(xml, true, true);
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
        "<RootBox>[X:0 Y:0 W:400 H:42 ]\n" + 
        "BlockElementBox: <#document>[X:0 Y:10 W:400 H:22 ]\n" + 
        "  BlockElementBox: <topic>[X:0 Y:0 W:400 H:22 ]\n" + 
        "    BlockElementBox: <body>[X:7 Y:0 W:393 H:22 ]\n" + 
        "      TableBox: <simpletable>[X:5 Y:0 W:95 H:22 ]\n" + 
        "        TableCellBox: <stentry>[X:1 Y:1 W:93 H:20 ]\n" + 
        "          ParagraphBox[X:1 Y:1 W:91 H:18 ]\n" + 
        "            LineBox: <stentry>[X:0 Y:0 W:91 H:18 ]\n" + 
        "              DocumentTextBox: 'Elements used'[X:0 Y:0 W:91 H:18 ](Length:13, StartRel:1)\n" + 
        "", dump.toString());
  }
  
  /**
   * Test 
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testDITASimpleTableLayout1() throws Exception { 
    String xml = "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "  <body>\n" + 
        " <simpletable relcolwidth=\"1* 3* 2*\">\n" + 
        "          <sthead>\n" + 
        "              <stentry>Col 1</stentry>\n" + 
        "              <stentry>Col 2</stentry>\n" + 
        "              <stentry>Col 3</stentry>\n" + 
        "          </sthead>\n" + 
        "      </simpletable>" + 
        "  </body>\n" + 
        "</topic>";
    
    initEditor(xml, true, true);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();

    StringBuilder dump = new StringBuilder();
    RootBox rootBox = vViewport.getRootBox();
    
    
    // Don't print minimum and maximum width 
    DumpConfiguration dumpConfiguration = new DumpConfiguration(true);
    dumpConfiguration.setReportMaximumWidth(false);
    dumpConfiguration.setReportMinimumWidth(false);
    dumpConfiguration.setReportOffsets(false);    
    
    rootBox.dump(dump, dumpConfiguration , vViewport.createLayoutContext());
    assertEquals( 
    		"<RootBox>[X:0 Y:0 W:400 H:42 ]\n" + 
    		"BlockElementBox: <#document>[X:0 Y:10 W:400 H:22 ]\n" + 
    		"  BlockElementBox: <topic>[X:0 Y:0 W:400 H:22 ]\n" + 
    		"    BlockElementBox: <body>[X:7 Y:0 W:393 H:22 ]\n" + 
    		"      TableBox: <simpletable>[X:5 Y:0 W:383 H:22 ]\n" + 
    		"        TableCellBox: <stentry>[X:1 Y:1 W:64 H:20 ]\n" + 
    		"          ParagraphBox[X:1 Y:1 W:35 H:18 ]\n" + 
    		"            LineBox: <stentry>[X:0 Y:0 W:35 H:18 ]\n" + 
    		"              DocumentTextBox: 'Col 1'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
    		"        TableCellBox: <stentry>[X:65 Y:1 W:190 H:20 ]\n" + 
    		"          ParagraphBox[X:1 Y:1 W:35 H:18 ]\n" + 
    		"            LineBox: <stentry>[X:0 Y:0 W:35 H:18 ]\n" + 
    		"              DocumentTextBox: 'Col 2'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
    		"        TableCellBox: <stentry>[X:255 Y:1 W:127 H:20 ]\n" + 
    		"          ParagraphBox[X:1 Y:1 W:35 H:18 ]\n" + 
    		"            LineBox: <stentry>[X:0 Y:0 W:35 H:18 ]\n" + 
    		"              DocumentTextBox: 'Col 3'[X:0 Y:0 W:35 H:18 ](Length:5, StartRel:1)\n" + 
    		"", dump.toString());
  }
  
  private int userChoice;
  private boolean addCWAsked;
  private boolean onlyProportional;
  
  /**
   * Test the resizing of columns with user interaction.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTableColsWithUserInteraction_simpletable() throws Exception {
    String xml =
      "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
      "<topic id=\"topic-1\">\n" + 
      "  <title>Topic title</title>\n" + 
      "  <body>\n" + 
      "    <p>Topic paragraph</p>\n" + 
      "      <simpletable>\n" + 
      "          <sthead>\n" + 
      "              <stentry>Type style</stentry>\n" + 
      "              <stentry>Elements used</stentry>\n" + 
      "          </sthead>\n" + 
      "          <strow>\n" + 
      "              <stentry>Bold</stentry>\n" + 
      "              <stentry>b</stentry>\n" + 
      "          </strow>\n" + 
      "      </simpletable>\n" + 
      "  </body>\n" + 
      "</topic>";
    
    initEditor(xml, true, true);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {

      @Override
      protected int shouldAddColWidthSpecifications(boolean proportional) {
        onlyProportional = proportional;
        addCWAsked = true;
        return userChoice;
      }
    });

    userChoice = AuthorTableResizeDialogPresenter.ADD_PROPORTIONAL_COLUMN_WIDTHS;
    int x = sendMousePressed(vViewport, 2, false);
    
    assertTrue("Only proportional column widths specification can be added in " +
    		"DITA tables", onlyProportional);

    assertTrue(addCWAsked);
    assertEquals( 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "  <title>Topic title</title>\n" + 
        "  <body>\n" + 
        "    <p>Topic paragraph</p>\n" + 
        "      <simpletable relcolwidth=\"1.0* 1.3*\">\n" + 
        "            <sthead>\n" + 
        "                <stentry>Type style</stentry>\n" + 
        "                <stentry>Elements used</stentry>\n" + 
        "            </sthead>\n" + 
        "            <strow>\n" + 
        "                <stentry>Bold</stentry>\n" + 
        "                <stentry>b</stentry>\n" + 
        "            </strow>\n" + 
        "        </simpletable>\n" + 
        "  </body>\n" + 
        "</topic>", 
        serializeDocumentViewport(vViewport, true));
    
    x = sendMousePressed(vViewport, 2, true);
    sendMouseReleased(vViewport, x - 100);
    
    assertEquals( 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>Topic paragraph</p>\n" + 
        "        <simpletable relcolwidth=\"1.0* 1.3*\">\n" + 
        "            <sthead>\n" + 
        "                <stentry>Type style</stentry>\n" + 
        "                <stentry>Elements used</stentry>\n" + 
        "            </sthead>\n" + 
        "            <strow>\n" + 
        "                <stentry>Bold</stentry>\n" + 
        "                <stentry>b</stentry>\n" + 
        "            </strow>\n" + 
        "        </simpletable>\n" + 
        "    </body>\n" + 
        "</topic>\n",  
        serializeDocumentViewport(vViewport, false));
    
    x = sendMousePressed(vViewport, 2, false);
    sendMouseReleased(vViewport, x + 100);
    flushAWTBetter();
    
    assertEquals( 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
        "<topic id=\"topic-1\">\n" + 
        "    <title>Topic title</title>\n" + 
        "    <body>\n" + 
        "        <p>Topic paragraph</p>\n" + 
        "        <simpletable relcolwidth=\"2.38* 1.0*\">\n" + 
        "            <sthead>\n" + 
        "                <stentry>Type style</stentry>\n" + 
        "                <stentry>Elements used</stentry>\n" + 
        "            </sthead>\n" + 
        "            <strow>\n" + 
        "                <stentry>Bold</stentry>\n" + 
        "                <stentry>b</stentry>\n" + 
        "            </strow>\n" + 
        "        </simpletable>\n" + 
        "    </body>\n" + 
        "</topic>\n", 
        serializeDocumentViewport(vViewport, false));
  }

  /**
   * Test the resizing of columns with user interaction.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTableColsWithUserInteraction_properties() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<!DOCTYPE reference PUBLIC \"-//OASIS//DTD DITA Reference//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/reference.dtd\">\n" + 
      "<reference id=\"referenceId\">\n" + 
      "    <title>Reference title</title>\n" + 
      "    <shortdesc>Short reference description.</shortdesc>\n" + 
      "    <refbody>\n" + 
      "        <properties>\n" + 
      "            <property>\n" + 
      "                <proptype>Prop name</proptype>\n" + 
      "                <propvalue>Prop value</propvalue>\n" + 
      "            </property>\n" + 
      "        </properties>\n" + 
      "    </refbody>\n" + 
      "</reference>";
    
    initEditor(xml, true, true);
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {
  
      @Override
      protected int shouldAddColWidthSpecifications(boolean proportional) {
        onlyProportional = proportional;
        addCWAsked = true;
        return userChoice;
      }
    });
  
    userChoice = AuthorTableResizeDialogPresenter.ADD_PROPORTIONAL_COLUMN_WIDTHS;
    sendMousePressed(vViewport, 2, false);
    assertTrue("Only proportional column widths specification can be added in " +
    		"DITA tables", onlyProportional);
  
    assertTrue(addCWAsked);
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE reference PUBLIC \"-//OASIS//DTD DITA Reference//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/reference.dtd\">\n" + 
        "<reference id=\"referenceId\">\n" + 
        "    <title>Reference title</title>\n" + 
        "    <shortdesc>Short reference description.</shortdesc>\n" + 
        "    <refbody>\n" + 
        "        <properties relcolwidth=\"1.0* 1.09*\">\n" + 
        "            <property>\n" + 
        "                <proptype>Prop name</proptype>\n" + 
        "                <propvalue>Prop value</propvalue>\n" + 
        "            </property>\n" + 
        "        </properties>\n" + 
        "    </refbody>\n" + 
        "</reference>",
        serializeDocumentViewport(vViewport, true));
  }
  
  /**
   * Test the resizing of columns with user interaction.
   * 
   * @author radu_coravu
   * @throws Exception
   */
  public void testResizeTableColsWithUserInteraction_choicetable() throws Exception {
    String xml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/task.dtd\">\n" + 
      "<task id=\"taskId\">\n" + 
      "    <title>Task title</title>\n" + 
      "    <taskbody>\n" + 
      "        <steps>\n" + 
      "            <step>\n" + 
      "                <cmd>Then this</cmd>\n" + 
      "                <choicetable>\n" + 
      "                    <chhead>\n" + 
      "                        <choptionhd>Do something</choptionhd>\n" + 
      "                        <chdeschd>Or Else this</chdeschd>\n" + 
      "                    </chhead>\n" + 
      "                    <chrow><choption>Do this</choption>\n" + 
      "                        <chdesc>and this will happen</chdesc></chrow>\n" + 
      "                </choicetable>\n" + 
      "            </step>\n" + 
      "        </steps>\n" + 
      "    </taskbody>\n" + 
      "</task>\n" + 
      "";
    
    initEditor(xml);
    
    AuthorViewport vViewport = ((AuthorEditorPage)editor.getEditorPage(EditorPageConstants.PAGE_AUTHOR)).getViewport();
    
    vViewport.setTableResizingHandlerForTCs(new AuthorTableResizingHandler() {
      @Override
      protected int shouldAddColWidthSpecifications(boolean proportional) {
        onlyProportional = proportional;
        addCWAsked = true;
        return userChoice;
      }
    });
  
    flushAWTBetter();
    userChoice = AuthorTableResizeDialogPresenter.ADD_PROPORTIONAL_COLUMN_WIDTHS;
    sendMousePressed(vViewport, 2, false);
    
    assertTrue("Only proportional column widths specification can be added in " +
        "DITA tables", onlyProportional);
  
    assertTrue(addCWAsked);
    assertEquals( 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE task PUBLIC \"-//OASIS//DTD DITA Task//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/task.dtd\">\n" + 
        "<task id=\"taskId\">\n" + 
        "    <title>Task title</title>\n" + 
        "    <taskbody>\n" + 
        "        <steps>\n" + 
        "            <step>\n" + 
        "                <cmd>Then this</cmd>\n" + 
        "                <choicetable relcolwidth=\"1.0* 1.63*\">\n" + 
        "                    <chhead>\n" + 
        "                        <choptionhd>Do something</choptionhd>\n" + 
        "                        <chdeschd>Or Else this</chdeschd>\n" + 
        "                    </chhead>\n" + 
        "                    <chrow>\n" + 
        "                        <choption>Do this</choption>\n" + 
        "                        <chdesc>and this will happen</chdesc>\n" + 
        "                    </chrow>\n" + 
        "                </choicetable>\n" + 
        "            </step>\n" + 
        "        </steps>\n" + 
        "    </taskbody>\n" + 
        "</task>\n" + 
        "",
        serializeDocumentViewport(vViewport, true));
  }

  /**
   * <p><b>Description:</b> We have a simple table specialization.</p>
   * <p><b>Bug ID:</b> EXM-29165</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testColWidthSimpleTableSpecializationEXM_29165() throws Exception {
    String xml = 
      "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
      "<topic id=\"topic-1\">\n" + 
      "  <title>Topic title</title>\n" + 
      "  <body>\n" + 
      "    <p>Topic paragraph</p>\n" + 
      "      <gogutable relcolwidth=\"1* 3*\" class='- topic/gogutable topic/simpletable '>\n" + 
      "          <goguhead class='- topic/goguhead topic/sthead '>\n" + 
      "              <goguentry class='- topic/goguentry topic/stentry '>Type style</goguentry>\n" + 
      "              <goguentry class='- topic/goguentry topic/stentry '>Elements used</goguentry>\n" + 
      "          </goguhead>\n" + 
      "          <gogurow class='- topic/gogurow topic/strow '>\n" + 
      "              <goguentry class='- topic/goguentry topic/stentry '>Bold</goguentry>\n" + 
      "              <goguentry class='- topic/goguentry topic/stentry '>b</goguentry>\n" + 
      "          </gogurow>\n" + 
      "      </gogutable>\n" + 
      "  </body>\n" + 
      "</topic>";
  
    initEditor(xml);
    AuthorDocumentImpl document = ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
    AuthorElement table = (AuthorElement) ((AuthorSentinelNode) ((AuthorSentinelNode)document.getChild("topic")).getChild("body")).getChild("gogutable");
    AuthorSentinelNode row = (AuthorSentinelNode) (((AuthorSentinelNode) table).getChild("gogurow"));
    DITATableCellInfoProvider tableSupport = new DITATableCellInfoProvider();
    tableSupport.init(table);
    
    assertTrue("Should recognize specialization", tableSupport.isTableAndColumnsResizable("goguentry"));
    
    // Col 0
    List<WidthRepresentation> colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(0), 0, 1);
    assertEquals(1, colWidth.size());
    
    WidthRepresentation col = colWidth.get(0);
    int fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(1.0f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
    // Col 1
    colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(1), 1, 1);
    assertEquals(1, colWidth.size());
    
    col = colWidth.get(0);
    fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(3.0f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
  }

  /**
   * <p><b>Description:</b> We have a CALS table specialization.</p>
   * <p><b>Bug ID:</b> EXM-29165</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testColWidthCALSTableSpecializationEXM_29165() throws Exception {
    String xml = 
      "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
      "<topic id=\"topic-1\">\n" + 
      "  <title>Topic title</title>\n" + 
      "  <body>\n" + 
      "    <p>Topic paragraph</p>\n" + 
      "      <gogutable class='- topic/gogutable topic/table '>\n" + 
      "                <title>test</title>\n" + 
      "                <gogutgroup cols=\"2\" class='- topic/gogutgroup topic/tgroup '>\n" + 
      "                    <colspec colname=\"c1\" colnum=\"1\" colwidth=\"1*\"/>\n" + 
      "                    <colspec colname=\"c2\" colnum=\"2\" colwidth=\"3*\"/>\n" + 
      "                    <goguthead class='- topic/goguthead topic/thead '>\n" + 
      "                        <gogurow class='- topic/gogurow topic/row '>\n" + 
      "                            <goguentry class='- topic/goguentry topic/entry '>1</goguentry>\n" + 
      "                            <goguentry class='- topic/goguentry topic/entry '>2</goguentry>\n" + 
      "                        </gogurow>\n" + 
      "                    </goguthead>\n" + 
      "                    <tbody>\n" + 
      "                        <gogurow class='- topic/gogurow topic/row '>\n" + 
      "                            <goguentry class='- topic/goguentry topic/entry '>3</goguentry>\n" + 
      "                            <goguentry class='- topic/goguentry topic/entry '>4</goguentry>\n" + 
      "                        </gogurow>\n" + 
      "                    </tbody>\n" + 
      "                </gogutgroup>\n" + 
      "            </gogutable>\n" + 
      "  </body>\n" + 
      "</topic>";
  
    initEditor(xml);
    AuthorDocumentImpl document = ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
    AuthorElement table = (AuthorElement) ((AuthorSentinelNode)((AuthorSentinelNode) ((AuthorSentinelNode)document.getChild("topic")).getChild("body")).getChild("gogutable")).getChild("gogutgroup");
    AuthorSentinelNode row = (AuthorSentinelNode) ((AuthorSentinelNode)(((AuthorSentinelNode) table).getChild("tbody"))).getChild("gogurow");
    DITATableCellInfoProvider tableSupport = new DITATableCellInfoProvider();
    tableSupport.init(table);
    
    assertTrue("Should recognize specialization", tableSupport.isTableAndColumnsResizable("goguentry"));
    
    // Col 0
    List<WidthRepresentation> colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(0), 0, 1);
    assertEquals(1, colWidth.size());
    
    WidthRepresentation col = colWidth.get(0);
    int fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(1.0f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
    // Col 1
    colWidth = tableSupport.getCellWidth((AuthorElementImpl) row.getContentNodes().get(1), 1, 1);
    assertEquals(1, colWidth.size());
    
    col = colWidth.get(0);
    fixedWidthInPx = LayoutUtils.convertToPx(col.getFixedWidth(), col.getFixedWidthUnit(), 0, StyleSheet.DOTS_PER_INCH);
    assertEquals(3.0f, col.getRelativeWidth());
    assertEquals(0, fixedWidthInPx);
    assertFalse(col.isRelativeToParent());
    
  }

  /**
   * <p><b>Description:</b> No CALS provider for image map.</p>
   * <p><b>Bug ID:</b> EXM-32789</p>
   *
   * @author radu_coravu
   *
   * @throws Exception
   */
  public void testNoCALSProviderForImageMapEXM_32789() throws Exception {
    String xml = 
      "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd\">\n" + 
      "<topic id=\"introduction\">\n" + 
      "    <title>Introduction</title>\n" + 
      "    <body>\n" + 
      "        \n" + 
      "        <imagemap>\n" + 
      "            <image href=\"../images/imagemapworld.jpg\">\n" + 
      "                <alt>Map of the world showing 5 areas</alt>\n" + 
      "            </image>\n" + 
      "            <area><shape>rect</shape><coords>2,0,53,59</coords>\n" + 
      "                <xref href=\"index.dita\">Section 1</xref>\n" + 
      "            </area>\n" + 
      "        </imagemap>\n" + 
      "    </body>\n" + 
      "</topic>\n" + 
      "";
  
    initEditor(xml);
    AuthorDocumentImpl document = ((AuthorEditorPage)editor.getCurrentPage()).getViewport().getController().getAuthorDocument();
    AuthorElement table = (AuthorElement) ((AuthorSentinelNode) ((AuthorSentinelNode)document.getChild("topic")).getChild("body")).getChild("imagemap");
    DITATableCellInfoProvider tableSupport = new DITATableCellInfoProvider();
    tableSupport.init(table);
    
    assertFalse(tableSupport.isTableAndColumnsResizable("shape"));
  }
}