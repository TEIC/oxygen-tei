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
package ro.sync.ecss.extensions.tei;

import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.contentcompletion.xml.ContextElement;
import ro.sync.contentcompletion.xml.WhatElementsCanGoHereContext;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandlerAdapter;
import ro.sync.ecss.extensions.api.AuthorSchemaManager;
import ro.sync.ecss.extensions.api.InvalidEditException;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResultsImpl;

/**
 * Specific editing support for TEI documents. Handles typing and paste events inside list (a item with a para will be 
 * created and the typing/paste content be put inside it) and tables.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class TEISchemaAwareEditingHandler extends AuthorSchemaAwareEditingHandlerAdapter {
  /** 
   * Logger for logging. 
   */
  private static Logger logger = Logger.getLogger(TEISchemaAwareEditingHandler.class.getName());
  /**
   * For TEI P5 http://www.tei-c.org/ns/1.0, for TEI P4 an empty string.
   */
  protected final String documentNamespace;
  /**
   * TEI ordered list element name.
   */
  private static final String LIST = "list";
  /**
   * TEI paragraph element name.
   */
  private static final String PARAGRAPH = "p";
  /**
   * TEI list item element name.
   */
  private static final String LIST_ITEM = "item";
  /**
   * TEI table element name.
   */
  private static final String TABLE = "table";
  /**
   * TEI table row
   */
  private static final String TABLE_ROW = "row";
  /**
   * TEI table cell
   */
  private static final String TABLE_CELL = "cell";
  /**
   * TEI head element
   */
  private static final String HEAD = "head";
  /**
   * Constructor.
   * 
   * @param documentNamespace The document namespace, for different versions of TEI. 
   */
  public TEISchemaAwareEditingHandler(String documentNamespace) {
    this.documentNamespace = documentNamespace;
  }
  /**
   * @see ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandlerAdapter#handleTyping(int, char, ro.sync.ecss.extensions.api.AuthorAccess)
   */
  @Override
  public boolean handleTyping(int offset, char ch, AuthorAccess authorAccess) throws InvalidEditException {
    boolean handleTyping = false;
    AuthorSchemaManager authorSchemaManager = authorAccess.getDocumentController().getAuthorSchemaManager();
    if (!authorSchemaManager.isLearnSchema() && 
        !authorSchemaManager.hasLoadingErrors() &&
        authorSchemaManager.getAuthorSchemaAwareOptions().isEnableSmartTyping()) {
      try {
        AuthorDocumentFragment characterFragment = 
          authorAccess.getDocumentController().createNewDocumentTextFragment(String.valueOf(ch));    
        handleTyping = handleInsertionEvent(offset, new AuthorDocumentFragment[] {characterFragment}, authorAccess);
      } catch (AuthorOperationException e) {
        throw new InvalidEditException(e.getMessage(), "Invalid typing event: " + e.getMessage(), e, false);
      }
    }
    return handleTyping;    
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandlerAdapter#handleTypingFallback(int, char, ro.sync.ecss.extensions.api.AuthorAccess)
   */
  @Override
  public boolean handleTypingFallback(int offset, char ch, AuthorAccess authorAccess)
      throws InvalidEditException {
    boolean handleEvent = false;
    try {
      AuthorSchemaManager authorSchemaManager = authorAccess.getDocumentController().getAuthorSchemaManager();
      // Empty sect element
      WhatElementsCanGoHereContext context = authorSchemaManager.createWhatElementsCanGoHereContext(offset);

      if (context != null) {
        StringBuilder xmlFragment = new StringBuilder("<");
        xmlFragment.append(TEISchemaAwareEditingHandler.PARAGRAPH);
        if (documentNamespace != null && documentNamespace.length() != 0) {
          xmlFragment.append(" xmlns=\"").append(documentNamespace).append("\"");
        }
        xmlFragment.append("/>");

        boolean canInsertRow = authorSchemaManager.canInsertDocumentFragments(
            new AuthorDocumentFragment[] {authorAccess.getDocumentController().createNewDocumentFragmentInContext(xmlFragment.toString(), offset)},
            context,
            AuthorSchemaManager.VALIDATION_MODE_STRICT_FIRST_CHILD_LAX_OTHERS);

        // Test if a paragraph can be inserted at the given offset.
        if (canInsertRow) {
          authorAccess.getDocumentController().insertXMLFragment(xmlFragment.toString(), offset);

          // Insert text inside the 
          authorAccess.getDocumentController().insertText(offset + 1, String.valueOf(ch));
          lastHandlerResult = 
            new SchemaAwareHandlerResultsImpl(SchemaAwareHandlerResult.TYPE_HANDLE_INSERT_FRAGMENT_OPERATION);
          ((SchemaAwareHandlerResultsImpl)lastHandlerResult).addResult(
              SchemaAwareHandlerResult.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET, 
              offset + 1);
          handleEvent = true;
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug("Fragments cannot be inserted in a para element.");
          }
        }
      }
    } catch (BadLocationException e) {
      throw new InvalidEditException(e.getMessage(), "Invalid typing event: " + e.getMessage(), e, false);
    } catch (AuthorOperationException e) {
      throw new InvalidEditException(e.getMessage(), "Invalid typing event: " + e.getMessage(), e, false);
    }
    return handleEvent;
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandlerAdapter#handlePasteFragment(int, ro.sync.ecss.extensions.api.node.AuthorDocumentFragment[], int, ro.sync.ecss.extensions.api.AuthorAccess)
   */
  @Override
  public boolean handlePasteFragment(int offset, AuthorDocumentFragment[] fragmentsToInsert,
      int actionId, AuthorAccess authorAccess) throws InvalidEditException {
    boolean handleInsertionEvent = false;
    AuthorSchemaManager authorSchemaManager = authorAccess.getDocumentController().getAuthorSchemaManager();
    if (!authorSchemaManager.isLearnSchema() && 
        !authorSchemaManager.hasLoadingErrors() &&
        authorSchemaManager.getAuthorSchemaAwareOptions().isEnableSmartPaste()) {
      handleInsertionEvent = handleInsertionEvent(offset, fragmentsToInsert, authorAccess);
    }
    return handleInsertionEvent;
  }

  /**
   * Handle an insertion event (either typing or paste).
   * 
   * @param offset Offset where the insertion event occurred.
   * @param fragmentsToInsert Fragments that must be inserted at the given offset. 
   * @param authorAccess Author access.
   * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
   * 
   * @throws InvalidEditException The event was rejected because it is invalid.
   */
  private boolean handleInsertionEvent(
      int offset, 
      AuthorDocumentFragment[] fragmentsToInsert, 
      AuthorAccess authorAccess) throws InvalidEditException {
    AuthorSchemaManager authorSchemaManager = authorAccess.getDocumentController().getAuthorSchemaManager();
    boolean handleEvent = false;
    try {
      // The fragment can't just be inserted at the given offset.
      AuthorNode nodeAtInsertionOffset = authorAccess.getDocumentController().getNodeAtOffset(offset);
      if (logger.isDebugEnabled()) {
        logger.debug("nodeAtInsertionOffset " + nodeAtInsertionOffset);
      }
      if(isElementWithNameAndNamespace(nodeAtInsertionOffset, LIST)) {
        // Check if the fragment is allowed as it is.
        boolean canInsertFragments = authorSchemaManager.canInsertDocumentFragments(
            fragmentsToInsert, 
            offset, 
            AuthorSchemaManager.VALIDATION_MODE_STRICT_FIRST_CHILD_LAX_OTHERS);
        if (!canInsertFragments) {
          handleEvent = handleInvalidInsertionEventInLists(
              offset, 
              fragmentsToInsert, 
              authorAccess,
              authorSchemaManager);
        }
      } else if (isElementWithNameAndNamespace(nodeAtInsertionOffset, TABLE)) {
        // Check if the fragment is allowed as it is.
        boolean canInsertFragments = authorSchemaManager.canInsertDocumentFragments(
            fragmentsToInsert, 
            offset, 
            AuthorSchemaManager.VALIDATION_MODE_STRICT_FIRST_CHILD_LAX_OTHERS);
        if (!canInsertFragments) {
          handleEvent = handleInvalidInsertionEventInTable(
              offset, 
              fragmentsToInsert, 
              authorAccess,
              authorSchemaManager);
        }
      } 
    } catch (BadLocationException e) {
      throw new InvalidEditException(e.getMessage(), "Invalid typing event: " + e.getMessage(), e, false);
    } catch (AuthorOperationException e) {
      throw new InvalidEditException(e.getMessage(), "Invalid typing event: " + e.getMessage(), e, false);
    }
    return handleEvent;    
  }

  /**
   * Try to handle invalid insertion events in 'list'. 
   * The solution is to insert the <code>fragmentsToInsert</code> into a 'item' element if is possible.  
   * 
   * @param offset Offset where the insertion event occurred.
   * @param fragmentsToInsert Fragments that must be inserted at the given offset. 
   * @param authorAccess Author access.
   * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
   */
  private boolean handleInvalidInsertionEventInLists(int offset,
      AuthorDocumentFragment[] fragmentsToInsert, AuthorAccess authorAccess,
      AuthorSchemaManager authorSchemaManager) throws BadLocationException, AuthorOperationException {
    boolean handleEvent = false;
    // Typing/paste inside an itemized list or ordered list. We will try to insert a list item with a para inside 
    // and perform the insertion inside the para element.
    WhatElementsCanGoHereContext context = authorSchemaManager.createWhatElementsCanGoHereContext(offset);
    // Derive the context by adding a list item with a para inside.
    pushContextElement(context, LIST_ITEM);
    // Test if fragments can be inserted in 'para' element
    if (authorSchemaManager.canInsertDocumentFragments(
        fragmentsToInsert, 
        context, 
        AuthorSchemaManager.VALIDATION_MODE_STRICT_FIRST_CHILD_LAX_OTHERS)) {
      // Create a listitem/para structure and insert fragments inside the new para
      StringBuilder xmlFragment = new StringBuilder("<").append(LIST_ITEM);
      if (documentNamespace != null && documentNamespace.length() != 0) {
        xmlFragment.append(" xmlns=\"").append(documentNamespace).append("\"");
      }
      xmlFragment.append(">").append("</").append(LIST_ITEM).append(">");
      if (logger.isDebugEnabled()) {
        logger.debug("Insert " + xmlFragment);
      }
      // Insert listitem/para
      authorAccess.getDocumentController().insertXMLFragment(xmlFragment.toString(), offset);

      // Insert fragments
      int insertionOffset = -1;
      AuthorNode newParaNode = authorAccess.getDocumentController().getNodeAtOffset(offset + 1);            
      for (int i = 0; i < fragmentsToInsert.length; i++) { 
        if(insertionOffset == -1) {
          insertionOffset = newParaNode.getEndOffset();
        }
        authorAccess.getDocumentController().insertFragment(newParaNode.getEndOffset(), fragmentsToInsert[i]);
      }
      lastHandlerResult = 
        new SchemaAwareHandlerResultsImpl(SchemaAwareHandlerResult.TYPE_HANDLE_INSERT_FRAGMENT_OPERATION);
      ((SchemaAwareHandlerResultsImpl)lastHandlerResult).addResult(
          SchemaAwareHandlerResult.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET, 
          insertionOffset);

      handleEvent = true;
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("Fragments cannot be inserted in a para element.");
      }
    }
    return handleEvent;
  }

  /**
   * Derive the given context by adding the given element.
   */
  protected void pushContextElement(WhatElementsCanGoHereContext context, String elementName) {
    ContextElement contextElement = new ContextElement();
    contextElement.setQName(elementName);
    contextElement.setNamespace(documentNamespace);
    context.pushContextElement(contextElement, null);
  }

  /**
   * @return <code>true</code> if the given node is an element with the given local name and from the TEI namespace.
   */
  protected boolean isElementWithNameAndNamespace(AuthorNode node, String elementLocalName) {
    boolean result = false;
    if(node.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
      AuthorElement element = (AuthorElement) node;
      result = elementLocalName.equals(element.getLocalName()) && element.getNamespace().equals(documentNamespace);
    }
    return result;
  }

  /**
   * Try to handle invalid insertion events in a TEI 'table'. 
   * A row element will be inserted with a new cell in which the fragments will be inserted.
   * 
   * @param offset Offset where the insertion event occurred.
   * @param fragmentsToInsert Fragments that must be inserted at the given offset. 
   * @param authorAccess Author access.
   * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
   */
  private boolean handleInvalidInsertionEventInTable(
      int offset,
      AuthorDocumentFragment[] fragmentsToInsert, 
      AuthorAccess authorAccess,
      AuthorSchemaManager authorSchemaManager) throws BadLocationException, AuthorOperationException {
    boolean handleEvent = false;
    // Typing/paste inside a tbody, tfoot, thead or html table. We will try to wrap the fragment into a new cell and
    // insert it inside a new row.
    WhatElementsCanGoHereContext context = authorSchemaManager.createWhatElementsCanGoHereContext(offset);
    // Derive the context by adding a new row element with a cell.
    pushContextElement(context, TABLE_ROW);
    pushContextElement(context, TABLE_CELL);
    // Test if fragments can be inserted in the new context.
    if (authorSchemaManager.canInsertDocumentFragments(
        fragmentsToInsert, 
        context, 
        AuthorSchemaManager.VALIDATION_MODE_STRICT_FIRST_CHILD_LAX_OTHERS)) {

      // Insert a new row with a cell.
      StringBuilder xmlFragment = new StringBuilder("<");
      xmlFragment.append(TABLE_ROW);
      if (documentNamespace != null && documentNamespace.length() != 0) {
        xmlFragment.append(" xmlns=\"").append(documentNamespace).append("\"");
      }
      xmlFragment.append("><");
      xmlFragment.append(TABLE_CELL);
      xmlFragment.append("/></");
      xmlFragment.append(TABLE_ROW);
      xmlFragment.append(">");
      if (logger.isDebugEnabled()) {
        logger.debug("Insert " + xmlFragment);
      }
      authorAccess.getDocumentController().insertXMLFragment(xmlFragment.toString(), offset);

      // Get the newly inserted cell.
      AuthorNode newCell = authorAccess.getDocumentController().getNodeAtOffset(offset + 2);            
      int insertionOffset = -1;
      for (int i = 0; i < fragmentsToInsert.length; i++) { 
        if(insertionOffset == -1) {
          insertionOffset = newCell.getEndOffset();
        }
        authorAccess.getDocumentController().insertFragment(newCell.getEndOffset(), fragmentsToInsert[i]);
      }
      lastHandlerResult = 
        new SchemaAwareHandlerResultsImpl(SchemaAwareHandlerResult.TYPE_HANDLE_INSERT_FRAGMENT_OPERATION);
      ((SchemaAwareHandlerResultsImpl)lastHandlerResult).addResult(
          SchemaAwareHandlerResult.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET, 
          insertionOffset);

      handleEvent = true;
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("Fragments cannot be inserted in a para element.");
      }
    }
    return handleEvent;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandlerAdapter#changeElementsToMoveUpDown(java.util.List)
   */
  @Override
  public boolean changeElementsToMoveUpDown(List<AuthorNode> selectedElements) {
    boolean elementsChanged = false;
    AuthorNode authorNode = selectedElements.get(0);
    if (isElementWithNameAndNamespace(authorNode, HEAD)) {
      // If the selected element is a head element take into account its parent when move operation is invoked.
      AuthorNode parent = authorNode.getParent();
      if (parent != null) {
        selectedElements.clear();
        selectedElements.add(parent);
        elementsChanged = true;
      }
    }
    return elementsChanged;
  }
}
