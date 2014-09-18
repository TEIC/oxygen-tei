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
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.ExtensionTags;

/**
 * Class for Docbook 'Sort list' operation.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class DocbookListSortOperation extends SortOperation {

  /**
   * Constructor.
   */
  public DocbookListSortOperation() {
    super(ExtensionTags.SELECTED_ITEMS, ExtensionTags.ALL_ITEMS);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#getSortParent(int, ro.sync.ecss.extensions.api.AuthorAccess)
   */
  @Override
  public AuthorElement getSortParent(int offset, AuthorAccess authorAccess)
      throws AuthorOperationException {
    
    // Check if the selected element is a list of some kind.
    if (authorAccess.getEditorAccess().hasSelection()) {
      AuthorNode selectedNode = authorAccess.getEditorAccess().getFullySelectedNode();
      if (selectedNode != null && selectedNode.getType() == AuthorElement.NODE_TYPE_ELEMENT && 
          ("orderedlist".equals(((AuthorElement) selectedNode).getLocalName()) 
          || "itemizedlist".equals(((AuthorElement) selectedNode).getLocalName())
          || "variablelist".equals(((AuthorElement) selectedNode).getLocalName()))) {
        
        return (AuthorElement) selectedNode;
      }
    }
    
    try {
      // Check if the node at offset s a list or a list descendant. 
      AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(offset);
      AuthorNode parentNode = nodeAtOffset;
      while (parentNode != null && parentNode.getType() != AuthorNode.NODE_TYPE_DOCUMENT) {
        if (parentNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
          // Check if ul or ol
          AuthorElement parentElement = (AuthorElement) parentNode;
          if ("orderedlist".equals(parentElement.getLocalName()) 
              || "itemizedlist".equals(parentElement.getLocalName())
              || "variablelist".equals(parentElement.getLocalName())) {
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
    //Ignore comments and PIs.
    return (node.getType() == AuthorNode.NODE_TYPE_COMMENT || node.getType() == AuthorNode.NODE_TYPE_PI);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#canBeSorted(ro.sync.ecss.extensions.api.node.AuthorElement, int[])
   */
  @Override
  public void canBeSorted(AuthorElement parent, int[] selectedNonIgnoredChildrenInterval) throws AuthorOperationException {
    // All the lists are sortable.
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
      // li element ?
      AuthorElement authorElement = (AuthorElement)node;
      if("listitem".equals(authorElement.getLocalName()) 
          || "varlistentry".equals(authorElement.getLocalName())) {
        values[0] = getTextContentToSort(authorElement);
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
    List<AuthorNode> children = getNonIgnoredChildren(parent);
    // For non empty lists only a single criterion is available.
    if (children.size() > 0) {
      criteria.add(new CriterionInformation(0, authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.LIST_ITEM)));
    }
    
    return criteria;
  }
}