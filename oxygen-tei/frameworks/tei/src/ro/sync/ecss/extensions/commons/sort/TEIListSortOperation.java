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
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.ExtensionTags;

/**
 * The implementation for TEI list sort operation.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(false)
public class TEIListSortOperation extends SortOperation {

  /**
   * Constructor.
   */
  public TEIListSortOperation() {
    super(ExtensionTags.SELECTED_ITEMS, ExtensionTags.ALL_ITEMS);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#getSortParent(int, ro.sync.ecss.extensions.api.AuthorAccess)
   */
  @Override
  public AuthorElement getSortParent(int offset, AuthorAccess authorAccess)
      throws AuthorOperationException {
    
    // Check if the selected node is a list.
    if (authorAccess.getEditorAccess().hasSelection()) {
      AuthorNode selectedNode = authorAccess.getEditorAccess().getFullySelectedNode();
      if (selectedNode != null && selectedNode.getType() == AuthorElement.NODE_TYPE_ELEMENT && 
          "list".equals(((AuthorElement) selectedNode).getLocalName())) {
        return (AuthorElement) selectedNode;
      }
    }
    
    try {
      // Check if the node at caret is a list or a list descendant.
      AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(offset);
      AuthorNode parentNode = nodeAtOffset;
      while (parentNode != null && parentNode.getType() != AuthorNode.NODE_TYPE_DOCUMENT) {
        if (parentNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
          AuthorElement parentElement = (AuthorElement) parentNode;
          if ("list".equals(parentElement.getLocalName())) {
            return parentElement;
          }
        } 
        
        parentNode = parentNode.getParent();
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
    // Ignore comments and PIs.
    return (node.getType() == AuthorNode.NODE_TYPE_COMMENT || node.getType() == AuthorNode.NODE_TYPE_PI);
  }

  /**
   * Check if an element is an item from the list.
   * 
   * @param node The node to check.
   * @throws AuthorOperationException
   */
  private void checkValidForSorting(AuthorNode node) throws AuthorOperationException {
    // Only sort lists containing 'item' elements.
    if(!(node instanceof AuthorElement) || !"item".equals(((AuthorElement) node).getLocalName())) {
      throw new AuthorOperationException("The 'Sort' operation is unavailable for lists containing elements which are not 'item'.");
    }
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#canBeSorted(ro.sync.ecss.extensions.api.node.AuthorElement, int[])
   */
  @Override
  public void canBeSorted(AuthorElement parent, int[] selectedNonIgnoredChildrenInterval)
      throws AuthorOperationException {
    List<AuthorNode> nonIgnoredChildren = getNonIgnoredChildren(parent);
    if (selectedNonIgnoredChildrenInterval == null) {
      selectedNonIgnoredChildrenInterval = new int[] {0, nonIgnoredChildren.size() - 1};
    }
    
    // Check the list children to be sortable.
    for (int i = selectedNonIgnoredChildrenInterval[0]; i <= selectedNonIgnoredChildrenInterval[1]; i ++) {
      if (i >= 0 && i < nonIgnoredChildren.size()) {
        checkValidForSorting(nonIgnoredChildren.get(i));
      } else {
        break;
      }
    }
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#getSortKeysValues(ro.sync.ecss.extensions.api.node.AuthorNode, ro.sync.ecss.extensions.commons.sort.SortCriteriaInformation)
   */
  @Override
  public String[] getSortKeysValues(AuthorNode node, SortCriteriaInformation sortInfo)
      throws AuthorOperationException {

    CriterionInformation[] criterionInfo = sortInfo.criteriaInfo;
    String[] values = null;
    if (node instanceof AuthorElement && criterionInfo.length > 0) {
      values = new String[1];
      AuthorElement authorElement = (AuthorElement)node;
      // The sort key values is the text value of the 'item' element.
      if("item".equals(authorElement.getLocalName())) {
        values[0] = getTextContentToSort(node);
      }
    }

    return values;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#getSortCriteria(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public List<CriterionInformation> getSortCriteria(AuthorElement parent)
      throws AuthorOperationException {
    List<CriterionInformation> criteria = new ArrayList<CriterionInformation>();
    int nrChildren = getNonIgnoredChildren(parent).size();
    // For non empty lists only one criterion is available.
    if (nrChildren > 0) {
      criteria.add(new CriterionInformation(0, authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.LIST_ITEM)));
    }
    return criteria;
  }
}