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
package ro.sync.ecss.extensions.commons.sort;

import java.util.List;
import java.util.TreeSet;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.ExtensionTags;

/**
 * Base table sort operation. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class TableSortOperation extends SortOperation {
  /**
   * Constructor.
   */
  public TableSortOperation() {
    super(ExtensionTags.SELECTED_ROWS, ExtensionTags.ALL_ROWS);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#canBeSorted(ro.sync.ecss.extensions.api.node.AuthorElement, int[])
   */
  @Override
  public void canBeSorted(AuthorElement parent, int[] selectedNonIgnoredChildrenInterval)
      throws AuthorOperationException {
    List<AuthorNode> nonIgnoredChildren = getNonIgnoredChildren(parent);
    if (selectedNonIgnoredChildrenInterval == null || selectedNonIgnoredChildrenInterval[1] < 0) {
      selectedNonIgnoredChildrenInterval = new int[] {0, nonIgnoredChildren.size() - 1};
    }
    
    // Determine the rows containing cells with multiple rowspan.
    TreeSet<Integer> notSortableRows = new TreeSet<Integer>();
    for (int i = 0; i <= selectedNonIgnoredChildrenInterval[1]; i ++) {
      AuthorNode node = nonIgnoredChildren.get(i);
      if (node.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
        List<AuthorNode> children = getNonIgnoredChildren((AuthorElement) node);
        int size = children.size();
        // Check every child
        for (int k = 0; k < size; k++) {
          AuthorNode child = children.get(k);
          if (child.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
            int[] tableRowSpanIndices = authorAccess.getTableAccess().getTableRowSpanIndices((AuthorElement) child);
            // If the table has row spans, the operation cannot be performed, so throw an exception
            if (tableRowSpanIndices != null && tableRowSpanIndices[1] - tableRowSpanIndices[0] > 0) {
              for (int j = tableRowSpanIndices[0]; j <= tableRowSpanIndices[1]; j ++) {
                notSortableRows.add(j);
              }
            }
          }
        }
      }      
    }

    // Get the row index where the table rows start. This means dismissing the header and footer rows.
    int tBodyRowIndex = getRowIndexForTableBody(parent);
    for (int i = selectedNonIgnoredChildrenInterval[0]; i <= selectedNonIgnoredChildrenInterval[1]; i ++) {
      // Iterate the selected rows and check if they contain cells with multiple row spans.
      if (i >= 0 && i < nonIgnoredChildren.size()) {
        if (notSortableRows.contains(i + tBodyRowIndex)) {
          throw new AuthorOperationException("The 'Sort' operation is unavailable for tables with multiple rowspan cells.");
        }
      } else {
        break;
      }
    }
  }
 
  /**
   * Returns the visual row index of the actual table body if the table has separate head, foot element and table group elements.
   */
  protected abstract int getRowIndexForTableBody(AuthorNode table);
  
  /**
   * @see ro.sync.ecss.extensions.commons.sort.SortOperation#getHelpPageID()
   */
  protected String getHelpPageID() {
    return "sort-entire-table";
  }
}