/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2016 Syncro Soft SRL, Romania.  All rights
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

import java.util.HashMap;
import java.util.Map;

import ro.sync.ecss.extensions.ArgumentsMapImpl;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ui.UiUtil;

/**
 * Test cases for the MoveCaretOperation.
 * 
 * @author sorin_carbunaru
 */
public class MoveCaretOperationTest extends EditorAuthorExtensionTestBase {
  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_1() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);

    final MoveCaretOperation op = new MoveCaretOperation();

    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", ".");
    userValues.put("position", AuthorConstants.POSITION_BEFORE);
    userValues.put("selection", AuthorConstants.SELECT_NONE);

    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);

    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("a1") - 1, editorAccess.getCaretOffset());
  }
  
  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_2() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);

    final MoveCaretOperation op = new MoveCaretOperation();

    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//i");
    userValues.put("position", AuthorConstants.POSITION_AFTER);
    userValues.put("selection", AuthorConstants.SELECT_NONE);

    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);

    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("ce") - 1, editorAccess.getCaretOffset());
  }
  
  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_3() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);

    final MoveCaretOperation op = new MoveCaretOperation();

    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//b");
    userValues.put("position", AuthorConstants.POSITION_INSIDE_AT_THE_END);
    userValues.put("selection", AuthorConstants.SELECT_NONE);

    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);

    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("b2") + "b2".length(), editorAccess.getCaretOffset());
  }
  
  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_4() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);

    final MoveCaretOperation op = new MoveCaretOperation();

    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//topic");
    userValues.put("position", AuthorConstants.POSITION_INSIDE_AT_THE_BEGINNING);
    userValues.put("selection", AuthorConstants.SELECT_NONE);

    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);

    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("TITLE") - 1, editorAccess.getCaretOffset());
  }
  
  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_5() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);

    final MoveCaretOperation op = new MoveCaretOperation();

    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//p");
    // Position before and at the beginning do the same thing when performing a selection,
    // that is they place the caret at the beginning of the selection
    userValues.put("position", AuthorConstants.POSITION_BEFORE);
    userValues.put("selection", AuthorConstants.SELECT_CONTENT);

    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);

    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("a1b1ceb2a2", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("a1"), editorAccess.getCaretOffset());
  }
  
  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_6() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);

    final MoveCaretOperation op = new MoveCaretOperation();

    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//b/b");
    // Position before and at the beginning do the same thing when performing a selection,
    // that is they place the caret at the beginning of the selection
    userValues.put("position", AuthorConstants.POSITION_INSIDE_AT_THE_BEGINNING);
    userValues.put("selection", AuthorConstants.SELECT_CONTENT);

    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);

    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("ce", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("ce"), editorAccess.getCaretOffset());
  }

  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_7() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);
  
    final MoveCaretOperation op = new MoveCaretOperation();
  
    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//b/b");
    // Position after and at the end do the same thing when performing a selection,
    // that is they place the caret at the end of the selection
    userValues.put("position", AuthorConstants.POSITION_AFTER);
    userValues.put("selection", AuthorConstants.SELECT_CONTENT);
  
    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);
  
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("ce", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("ce") + "ce".length(), editorAccess.getCaretOffset());
  }

  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_8() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);
  
    final MoveCaretOperation op = new MoveCaretOperation();
  
    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//b/b");
    // Position after and at the end do the same thing when performing a selection,
    // that is they place the caret at the end of the selection
    userValues.put("position", AuthorConstants.POSITION_INSIDE_AT_THE_END);
    userValues.put("selection", AuthorConstants.SELECT_CONTENT);
  
    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);
  
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("ce", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("ce") + "ce".length(), editorAccess.getCaretOffset());
  }

  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_9() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);
  
    final MoveCaretOperation op = new MoveCaretOperation();
  
    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//i");
    // Position before and at the beginning do the same thing when performing a selection,
    // that is they place the caret at the beginning of the selection
    userValues.put("position", AuthorConstants.POSITION_BEFORE);
    userValues.put("selection", AuthorConstants.SELECT_ELEMENT);
  
    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);
  
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("b1", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("b1") - 1, editorAccess.getCaretOffset());
  }

  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_10() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);
  
    final MoveCaretOperation op = new MoveCaretOperation();
  
    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//i");
    // Position before and at the beginning do the same thing when performing a selection,
    // that is they place the caret at the beginning of the selection
    userValues.put("position", AuthorConstants.POSITION_INSIDE_AT_THE_BEGINNING);
    userValues.put("selection", AuthorConstants.SELECT_ELEMENT);
  
    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);
  
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("b1", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("b1") - 1, editorAccess.getCaretOffset());
  }

  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_11() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);
  
    final MoveCaretOperation op = new MoveCaretOperation();
  
    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//i");
    // Position after and at the end do the same thing when performing a selection,
    // that is they place the caret at the end of the selection
    userValues.put("position", AuthorConstants.POSITION_AFTER);
    userValues.put("selection", AuthorConstants.SELECT_ELEMENT);
  
    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);
  
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("b1", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("b1") + "b1".length() + 1, editorAccess.getCaretOffset());
  }

  /**
   * <p><b>Description:</b> test the behaviour of the MoveCaretOperation.</p>
   * <p><b>Bug ID:</b> EXM-36078</p>
   *
   * @author sorin_carbunaru
   *
   * @throws Exception
   */
  public void testMoveCaretOperation_12() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
        "<topic id=\"care\">\n" + 
        "    <title>TITLE</title>\n" + 
        "    <body>\n" + 
        "        <p>a1<b><i>b1</i><b>ce</b>b2</b>a2</p>\n" + 
        "    </body>\n" + 
        "</topic>\n" + 
        "";
    initEditor(xml);
    moveCaretRelativeTo("a1", 0);
  
    final MoveCaretOperation op = new MoveCaretOperation();
  
    // Operation arguments
    Map userValues = new HashMap();
    userValues.put("xpathLocation", "//i");
    // Position after and at the end do the same thing when performing a selection,
    // that is they place the caret at the end of the selection
    userValues.put("position", AuthorConstants.POSITION_INSIDE_AT_THE_END);
    userValues.put("selection", AuthorConstants.SELECT_ELEMENT);
  
    final ArgumentsMapImpl operationArguments = new ArgumentsMapImpl();
    operationArguments.copyMap(userValues);
    
    final AuthorAccess authorAccess = vViewport.getAuthorAccess();
    UiUtil.invokeSynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          op.doOperation(authorAccess, operationArguments);
        } catch (AuthorOperationException e) {
          e.printStackTrace();
        }
      }
    });
    sleep(500);
  
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    assertEquals("b1", editorAccess.getSelectedText());
    
    assertEquals(getDocumentContent().indexOf("b1") + "b1".length() + 1, editorAccess.getCaretOffset());
  }
}
