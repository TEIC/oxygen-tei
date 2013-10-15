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
package ro.sync.ecss.extensions.commons.table.operations;

import java.util.Iterator;
import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Base class for table operations. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class AbstractTableOperation implements AuthorOperation {
  
  /**
   * Table helper, has methods specific to each document type.
   */
  protected AuthorTableHelper tableHelper;

  /**
   * Constructor.
   * 
   * @param authorTableHelper Table helper, has methods specific to each document type.
   */
  public AbstractTableOperation(AuthorTableHelper authorTableHelper) {
    this.tableHelper = authorTableHelper;
  }

  /**
   * Search for an ancestor {@link AuthorNode} with the specified type. 
   * 
   * @param node The starting node.
   * @param type The type of the ancestor.
   * @return     The ancestor node of the given <code>node</code> or the <code>node</code> 
   * itself if the type matches.
   */
  protected AuthorElement getElementAncestor(AuthorNode node, int type) {
    AuthorElement parentCell = null;
    
    while (node != null && node instanceof AuthorElement) {
      if (isTableElement(node, type)) {
        parentCell = (AuthorElement) node;
        break;
      }
      node = node.getParent();
    }
    
    return parentCell;
  }
  
  /**
   * Test if a given {@link AuthorNode} is an element and has the a specific local name.
   * 
   * @param node          The {@link AuthorNode} to be checked.
   * @param elemLocalName The local name of the element.
   * @return              <code>true</code> if the given {@link AuthorNode} is an 
   * element and its local name matches the given string.
   */
  protected boolean isElement(AuthorNode node, String elemLocalName) {
    return node instanceof AuthorElement && 
        elemLocalName.equals(((AuthorElement)node).getLocalName());    
  }
  
  /**
   * Test if an {@link AuthorNode} is an element and it has one of the following types:
   * {@link AuthorTableHelper#TYPE_CELL}, {@link AuthorTableHelper#TYPE_ROW} or 
   * {@link AuthorTableHelper#TYPE_TABLE}.
   * 
   * @param node  The node to be checked.
   * @param type  The type to search for.
   * @return      <code>true</code> if the <code>node</code> is an element with the specified type.
   */
  protected boolean isTableElement(AuthorNode node, int type) {
    boolean toReturn = false;
    switch (type) {
      case AuthorTableHelper.TYPE_CELL:
        toReturn = tableHelper.isTableCell(node);
        break;
      case AuthorTableHelper.TYPE_ROW:
        toReturn = tableHelper.isTableRow(node);
        break;
      case AuthorTableHelper.TYPE_TABLE:
        toReturn = tableHelper.isTable(node);
        break;
    }
    return toReturn;
  }

  /**
   * Find the offset in the document where a new entry (table cell) should be inserted 
   * for the given table row and column. 
   * 
   * @param authorAccess The author access.
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility 
   * @param tableElement The element rendered as a table. Its 'display' CSS property
   * is set to 'table'. 
   * @param row The table row where the insertion will occur, 0 based.
   * @param column The column where the insertion will occur, 0 based.
   * @return The offset where the new entry should be inserted.
   */
  protected int findCellInsertionOffset(
      AuthorAccess authorAccess, 
      AuthorElement tableElement, 
      int row, 
      int column) {
    
    int insertionOffset = -1;
    // Find the insert location
    // At the start of row.
    AuthorElement insertRow = authorAccess.getTableAccess().getTableRow(row, tableElement);
    
    if (insertRow != null) {
      if (column == 0) {
        // Cell is on the first column
        insertionOffset = insertRow.getStartOffset() + 1;
      } else {
        // Find the last cell on the row that is located before the given column
        AuthorElement previousCell = null;
        for (Iterator iterator = insertRow.getContentNodes().iterator(); iterator.hasNext();) {
          AuthorNode authorNode = (AuthorNode) iterator.next();
          if (tableHelper.isTableCell(authorNode)) {
            int[] cellIndex = authorAccess.getTableAccess().getTableCellIndex((AuthorElement) authorNode);
            if (cellIndex != null) {
              if (cellIndex[1] < column) {
                previousCell = (AuthorElement) authorNode;
              } else {
                break;
              }
            }
          }
        }

        if (previousCell != null) {
          // A cell was found before the column
          insertionOffset = previousCell.getEndOffset() + 1;
        } else {
          // No cell was found, use the start of row
          insertionOffset = insertRow.getStartOffset() + 1;
        }
      } 
    }
    return insertionOffset;
  }
  
  /**
   * Create an {@link AuthorDocumentFragment} representing an empty cell by duplicating 
   * the given cell without its content and skipping the specified attributes.
   * 
   * @param authorAccess The author access.
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility 
   * @param cell  The reference cell.
   * @param skippedAttributes The attributes which should not be copied.
   * @return The document fragment representing the empty cell created starting 
   * from the original cell.
   * @throws BadLocationException When the fragment cannot be created.
   */
  protected AuthorDocumentFragment createEmptyCell(
      AuthorAccess authorAccess, 
      AuthorElement cell,
      String[] skippedAttributes) throws BadLocationException {
    AuthorDocumentFragment newCellFragment = null;

    AuthorDocumentController controller = authorAccess.getDocumentController();
    // Create an empty fragment
    newCellFragment = controller.createDocumentFragment(cell, false);

    List fragNodes = newCellFragment.getContentNodes();
    // Remove attributes
    if (fragNodes != null && fragNodes.size() > 0) {
      AuthorNode node = (AuthorNode) fragNodes.get(0);
      if (node.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
        AuthorElement clonedElement = (AuthorElement) node;
        if (skippedAttributes != null) {
          for (int i = 0; i < skippedAttributes.length; i++) {
            String attrName = skippedAttributes[i];
            clonedElement.removeAttribute(attrName);
          }
        }
      }
    }

    return newCellFragment;
  }
}