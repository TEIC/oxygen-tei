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
package ro.sync.ecss.extensions.commons.sort;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorElementBaseInterface;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Sort operation for simple tables
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
abstract public class SimpleTableSortOperation extends TableSortOperation {

  /**
   * Get the table element whose rows will be sorted.
   * 
   * @throws AuthorOperationException 
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#getSortParent(int, ro.sync.ecss.extensions.api.AuthorAccess)
   */
  @Override
  public AuthorElement getSortParent(int offset, AuthorAccess authorAccess) throws AuthorOperationException {
    if (authorAccess.getEditorAccess().hasSelection()) {
      AuthorNode selectedNode = authorAccess.getEditorAccess().getFullySelectedNode();
      if (selectedNode != null && selectedNode.getType() == AuthorNode.NODE_TYPE_ELEMENT 
          && isTableElement((AuthorElement) selectedNode)) {
        return (AuthorElement) selectedNode;
      }
    } 

    try {
      AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(offset);
      AuthorNode parentElement = nodeAtOffset;
      while (parentElement.getType() != AuthorNode.NODE_TYPE_DOCUMENT) {
        if (parentElement.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
          if (isTableElement((AuthorElement) parentElement)) {
            // Found a table element
            return (AuthorElement) parentElement;
          }
        }
        // Next parent 
        parentElement = parentElement.getParent();
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException(e.getMessage(), e);
    }

    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#isIgnored(ro.sync.ecss.extensions.api.node.AuthorNode)
   */
  @Override
  public boolean isIgnored(AuthorNode node) {
    boolean isStHeadElem = false;
    if (node.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
      // Head element is ignored
      isStHeadElem = isHeadElement((AuthorElement) node);
    }
    
    return (node.getType() == AuthorNode.NODE_TYPE_COMMENT || node.getType() == AuthorNode.NODE_TYPE_PI || isStHeadElem);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#getSortKeysValues(AuthorNode, SortCriteriaInformation)
   */
  @Override
  public String[] getSortKeysValues(AuthorNode node, SortCriteriaInformation sortInfo) throws AuthorOperationException {
    CriterionInformation[] criterionInfo = sortInfo.criteriaInfo;
    String[] values = null;
    if (node instanceof AuthorElement && criterionInfo.length > 0) {
      values = new String[criterionInfo.length];
      // Row element ?
      AuthorElement authorParentNode = (AuthorElement)node;
      if (isRowElement(authorParentNode)) {
        List<AuthorNode> contentNodes = getNonIgnoredChildren(authorParentNode);
        int size = contentNodes.size();
        for (int i = 0; i < criterionInfo.length; i++) {
          CriterionInformation criterionInformation = criterionInfo[i];
          int keyIndex = criterionInformation.getKeyIndex();
          AuthorNode cellNode = null;
          for (int j = 0; j < size; j++) {
            AuthorNode authorNode = contentNodes.get(j);
            if (authorNode instanceof AuthorElement) {
              int[] tableColSpanIndices = authorAccess.getTableAccess().getTableColSpanIndices((AuthorElement) authorNode);
              // Maybe we have cols span
              if (tableColSpanIndices != null) {
                if (tableColSpanIndices[0] <= keyIndex && tableColSpanIndices[1] >= keyIndex) {
                  // Found the cell node
                  cellNode = authorNode;
                  break;
                } 
              }
            }
          }

          if (cellNode != null) {
            // Found a cell node, get the text content 
            values[i] = getTextContentToSort(cellNode);
          }
        }
      }
    }
    
    return values;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#getSortCriteria(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public List<CriterionInformation> getSortCriteria(AuthorElement parent) throws AuthorOperationException {
    List<CriterionInformation> criteria = new ArrayList<CriterionInformation>();
    
    // Parent is tbody element
    // Determine max number of entries for tbody
    List<AuthorNode> children = getNonIgnoredChildren(parent);
    int maxNrOfChildren = 0;
    AuthorElement maxRowElem = null;
    for (int i = 0; i < children.size(); i++) {
      // Row 
      AuthorNode child = children.get(i);
      if (child.getType() == AuthorNode.NODE_TYPE_ELEMENT && isRowElement((AuthorElement) child)) {
        int size = getNonIgnoredChildren((AuthorElement)child).size();
        if (size > maxNrOfChildren) {
          // Found a max, retain it
          maxRowElem = ((AuthorElement)child);
          maxNrOfChildren = size; 
        }
      }
    }
    
    // Determine the thead/sthead element
    AuthorElementBaseInterface tGroupElementElement = null;
    if (isTableElement(parent)) {
      tGroupElementElement = parent;
    }
    if (tGroupElementElement != null) {
      List<AuthorNode> contentNodes = tGroupElementElement.getContentNodes();
      AuthorElement theadElement = null;
      for (int i = 0; i < contentNodes.size(); i++) {
        // Get the children
        AuthorNode child = contentNodes.get(i);
        if (child.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
          AuthorElement authorElement = (AuthorElement)child;
          if (isHeadElement(authorElement)) {
            // sthead element
            theadElement = authorElement;
            break;
          }
        }
      }
      
      // Obtain the first thead row containing the max number of entries
      AuthorElement theadRow = null;
      if (theadElement != null) {
        if (isTableElement(parent)) {
          if (theadElement.getContentNodes().size() == maxNrOfChildren) {
            // Found a thead row
            theadRow = theadElement;
          }
        }
      }
      
      if (theadRow != null && maxRowElem != null) {
        List<AuthorNode> theadRowChildren = getNonIgnoredChildren(theadRow);
        for (int i = 0; i < maxNrOfChildren; i++) {
          AuthorNode authorNode = theadRowChildren.get(i);
          if (authorNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
            try {
                AuthorElement theadEntry = (AuthorElement)authorNode;
                String textContent = theadEntry.getTextContent();
                // Create the criterion information
                criteria.add(new CriterionInformation(i,
                    CriterionInformation.TYPE.TEXT.getName(), 
                    CriterionInformation.ORDER.ASCENDING.getName(),
                		(textContent.length() > 0 ? textContent : (COLUMN + " " + (i + 1))), 
                        TableSortUtil.isEntirelySelected(authorAccess, theadEntry) || isCaretInColumn(authorAccess, i)));
            } catch (BadLocationException e) {
              throw new AuthorOperationException(e.getMessage(), e);
            }
          }
        }
      } else if (maxRowElem != null) {
        List<AuthorNode> maxRowChildren = getNonIgnoredChildren(maxRowElem);
        // We do not have a thead element
        for (int i = 0; i < maxRowChildren.size(); i++) {
          if (maxRowChildren.get(i).getType() == AuthorNode.NODE_TYPE_ELEMENT) {
            criteria.add(new CriterionInformation(i,
                CriterionInformation.TYPE.TEXT.getName(), 
                CriterionInformation.ORDER.ASCENDING.getName(),
            		COLUMN + " " + (i + 1),
            		TableSortUtil.isEntirelySelected(authorAccess, (AuthorElement) maxRowChildren.get(i)) || isCaretInColumn(authorAccess, i)));
          }
        }
      }
    }
    
    return criteria;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#forceSortAll()
   */
  @Override
  protected boolean forceSortAll() {
    // All the table should be sorted because all the rows are selected
    // So there is no need to obtain the selected rows
    return TableSortUtil.isColumnOrTableSelection(authorAccess);
  }

  /**
   * Checks if the caret is in a cell which is in the given column.
   * 
   * @param authorAccess The author access.
   * @param columnNumber  The number of the column in which to check.
   * 
   * @return <code>true</code> if the given column has a cell which contains the caret. 
   */
  public boolean isCaretInColumn(AuthorAccess authorAccess, int columnNumber) {
    if (columnNumber < 0) {
      return false;
    }
    
    boolean isCaretInColumn = false;
    
    int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
    try {
      AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
      if (nodeAtOffset != null && nodeAtOffset.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
        // Check if it is a cell
        if (nodeAtOffset.getParent().getType() == AuthorNode.NODE_TYPE_ELEMENT && isRowElement((AuthorElement) nodeAtOffset.getParent())) {
          List<AuthorNode> nonIgnoredChildren = getNonIgnoredChildren(((AuthorElement) nodeAtOffset.getParent()));
          int[] tableColSpanIndices = authorAccess.getTableAccess().getTableColSpanIndices((AuthorElement) nodeAtOffset);
          // Maybe be have col spans
          if (tableColSpanIndices != null) {
            if (tableColSpanIndices[0] <= columnNumber && tableColSpanIndices[1] >= columnNumber) {
              isCaretInColumn = true;
            } 
          } else if (columnNumber < nonIgnoredChildren.size()) {
            isCaretInColumn = nonIgnoredChildren.get(columnNumber) == nodeAtOffset;
          }
        }
      }
    } catch (BadLocationException e) {
      // Do nothing 
    }
    
    return isCaretInColumn;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.sort.TableSortOperation#getRowIndexForTableBody(ro.sync.ecss.extensions.api.node.AuthorNode)
   */
  @Override
  protected int getRowIndexForTableBody(AuthorNode table) {
    return 0;
  }
  
  /**
   * Return <code>true</code> if the given node is the table element.
   * 
   * @param node The node to be checked.
   * @return <code>true</code> if the given node is the table element.
   */
  abstract public boolean isTableElement(AuthorElement node);
  
  /**
   * Returns <code>true</code> if the given node is the table header element.
   * 
   * @param node The node to be checked.
   * @return <code>true</code> if the given node is the table header.
   */
  abstract public boolean isHeadElement(AuthorElement node);
  
  /**
   * Returns <code>true</code> if the given node is a table row.
   * 
   * @param node The node to be checked.
   * @return <code>true</code> when the given node is a table row element.
   */
  abstract public boolean isRowElement(AuthorElement node);
}