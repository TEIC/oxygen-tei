/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2022 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper;
import ro.sync.ecss.extensions.commons.table.operations.cals.CALSConstants;

/**
 * Abstract implementation of the document type helper.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class AbstractDocumentTypeHelper implements AuthorTableHelper {
  
  /**
   * Test if a given node is an element and has the a specific local name.
   * 
   * @param node          The {@link AuthorNode} to be checked.
   * @param elemLocalName The local name of the element.
   * @return <code>true</code> if the given {@link AuthorNode} is an element and 
   * its local name matches the given string.
   */
  protected boolean isElement(AuthorNode node, String elemLocalName) {
    return node instanceof AuthorElement && 
        elemLocalName.equals(((AuthorElement)node).getLocalName());    
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#isTableCell(ro.sync.ecss.extensions.api.node.AuthorNode)
   */
  @Override
  public boolean isTableCell(AuthorNode node) {
    String[] rowElemNames = getTableCellElementNames();
    for (int i = 0; i < rowElemNames.length; i++) {
      if (isElement(node, rowElemNames[i])) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#isTable(ro.sync.ecss.extensions.api.node.AuthorNode)
   */
  @Override
  public boolean isTable(AuthorNode node) {
    String[] tableElemNames = getTableElementLocalName();
    for (int i = 0; i < tableElemNames.length; i++) {
      if (isElement(node, tableElemNames[i])) {
        return true;
      }
    }
    return false;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#isTableRow(ro.sync.ecss.extensions.api.node.AuthorNode)
   */
  @Override
  public boolean isTableRow(AuthorNode node) {
    String[] rowElemNames = getTableRowElementNames();
    for (int i = 0; i < rowElemNames.length; i++) {
      if (isElement(node, rowElemNames[i])) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper#getTableElementForDeletion(ro.sync.ecss.extensions.api.node.AuthorNode)
   */
  @Override
  public AuthorNode getTableElementForDeletion(AuthorNode element) {
    AuthorNode tableElement = null;
    if (isTable(element)) {
      tableElement = element;
    } else {
      while (element.getParent() != null) {
        AuthorNode parentElement = element.getParent();
        if (isTable(parentElement)) {
          tableElement = parentElement;
          break;
        }
      }
    }
    
    return tableElement;
  }
  
  /////////////////////////
  // Abstract methods
  
  /**
   * Returns the possible local names of the elements that represents a table cell.
   *  
   * @return The local names of the elements that represents a table cell.
   * Not <code>null</code>.
   */
  protected abstract String[] getTableCellElementNames();

  /**
   * Return the possible local names of the elements that represent a table row.
   *  
   * @return The local names of the elements that represent a table row.
   */
  protected abstract String[] getTableRowElementNames();
  
  /**
   * Returns the possible local names of the elements that represents a table.
   *  
   * @return The local names of the elements that represents a table. 
   */
  protected abstract String[] getTableElementLocalName();
  
  /**
   * Get a list of allowed cell attributes to copy when creating a new row based on an older one.
   * @return a list of allowed cell attributes to copy when creating a new row.
   * If it returns <code>null</code>, the list of ignored attributes will be used by default.
   */
  public String[] getAllowedCellAttributesToCopy() {
      return null;
  }
  
  /**
   * Check if this node references another node which should replace it entirely.
   * This is used in the tables to replace conreffed table rows entirely
   * 
   * @param node The node
   * @return <code>true</code> if this node references another node which should replace it entirely.
   */
  public boolean isContentReference(AuthorNode node) {
    return false;
  }
  
  /**
   * Check if a node is a colspec node.
   * @param node The node.
   * @return <code>true</code> if a node is a colspec node.
   */
  @Override
  public boolean isColspec(AuthorNode node){
    return node instanceof AuthorElement && 
        CALSConstants.ELEMENT_NAME_COLSPEC.equals(((AuthorElement)node).getLocalName());    
  }
}