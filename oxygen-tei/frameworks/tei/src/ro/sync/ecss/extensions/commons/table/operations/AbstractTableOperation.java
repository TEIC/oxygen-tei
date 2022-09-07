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
package ro.sync.ecss.extensions.commons.table.operations;

import java.util.Iterator;

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil;
import ro.sync.exml.workspace.api.Platform;

/**
 * Base class for table operations. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class AbstractTableOperation implements AuthorOperation {
  
  /**
   * Parameter name for change tracking behavior.
   */
  static final String CHANGE_TRACKING_BEHAVIOR = "changeTrackingBehavior";

  /**
   * Operation is not performed when change tracking is activated.
   */
  static final String CHANGE_TRACKING_BEHAVIOR_BLOCK = "Block";
  
  /**
   * Operation is performed when change tracking is activated. For complex
   * table operations, the resulting table layout will be broken.
   */
  static final String CHANGE_TRACKING_BEHAVIOR_ALLOW = "Allow";
  
  /**
   * Operation is performed with change tracking disabled.
   */
  static final String CHANGE_TRACKING_BEHAVIOR_ALLOW_WITHOUT = "Allow with change tracking disabled";

  /**
   * Let the editor decide which strategy to use, maybe after asking the end user.
   */
  static final String CHANGE_TRACKING_BEHAVIOR_AUTO = "Auto";

  /**
   * Argument descriptor for change tracking behavior.
   */
  public static final ArgumentDescriptor CHANGE_TRACKING_BEHAVIOR_ARGUMENT = new ArgumentDescriptor(
      CHANGE_TRACKING_BEHAVIOR, 
      ArgumentDescriptor.TYPE_CONSTANT_LIST,
      "Configures the operation behavior when change tracking is activated. Can be one of:\n" + 
      " - Block: The operation is not performed when change tracking is activated.\n" + 
      " - Allow: The operation is performed when change tracking is activated. If complex\n" + 
      "     table operations are performed, the resulting table layout may become broken.\n" + 
      " - Allow with change tracking disabled: The operation is performed with change tracking disabled.\n" + 
      " - Auto: Let the application decide which strategy to use, possibly by asking the end user.",
      new String[] {
          CHANGE_TRACKING_BEHAVIOR_ALLOW, 
          CHANGE_TRACKING_BEHAVIOR_ALLOW_WITHOUT,
          CHANGE_TRACKING_BEHAVIOR_BLOCK,
          CHANGE_TRACKING_BEHAVIOR_AUTO}, 
      CHANGE_TRACKING_BEHAVIOR_AUTO);

  /**
   * The name of the table info argument.
   */
  public static final String TABLE_INFO_ARGUMENT_NAME = "table_info";
  
  /**
   * Argument descriptor for a table info argument.
   */
  public static final ArgumentDescriptor TABLE_INFO_ARGUMENT_DESCRIPTOR = new ArgumentDescriptor(
      TABLE_INFO_ARGUMENT_NAME, 
      ArgumentDescriptor.TYPE_JAVA_OBJECT, 
      "Details of the table that will be inserted in the document. "
      + "The value of the argument should be a Java Map<String, Object> with "
      + "keys corresponding to TableInfo fields."
      + "If a null value is passed in, the table customized dialog will be shown.");
 
  /**
   * Table helper, has methods specific to each document type.
   */
  protected AuthorTableHelper tableHelper;

  /**
   * <code>true</code> if the operation result should always be marked as a tracked change if track changes is enabled.
   */
  private boolean supportsChangeTracking = false;
   /**
   * Constructor.
   * 
   * @param authorTableHelper Table helper, has methods specific to each document type.
   */
  public AbstractTableOperation(AuthorTableHelper authorTableHelper) {
    this(authorTableHelper, false);
  }
  
  /**
  * Constructor.
  * 
  * @param authorTableHelper Table helper, has methods specific to each document type.
   * @param markAsChange <code>true</code> if the operation result is marked as a change.
  */
  public AbstractTableOperation(AuthorTableHelper authorTableHelper, boolean markAsChange) {
    this.tableHelper = authorTableHelper;
    this.supportsChangeTracking = markAsChange;
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
    
    while (node instanceof AuthorElement) {
      if (isTableElement(node, type)) {
        parentCell = (AuthorElement) node;
        break;
      }
      node = node.getParent();
    }
    
    if(parentCell != null && parentCell.getParent() != null && parentCell.getParent().getType() == AuthorNode.NODE_TYPE_REFERENCE){
      AuthorNode parentOfRef = parentCell.getParent().getParent();
      if(tableHelper instanceof AbstractDocumentTypeHelper && ((AbstractDocumentTypeHelper)tableHelper).isContentReference(parentOfRef)){
        //EXM-31584 Go up...
        parentCell = getElementAncestor(parentCell.getParent(), type);
      }
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
        AuthorElement previousCell = findPreviousCellInRow(authorAccess, column, insertRow);

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
   * Find the last cell on the row that is located before the given column
   * @param authorAccess The author access.
   * @param column The column for which to find the previous cell.
   * @param row The row in which to find the previous cell.
   * @return The previous cell or <code>null</code>.
   */
  private AuthorElement findPreviousCellInRow(AuthorAccess authorAccess, int column, AuthorElement row) {
    AuthorElement previousCell = null;
    for (Iterator iterator = row.getContentNodes().iterator(); iterator.hasNext();) {
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
    return previousCell;
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

    CommonsOperationsUtil.removeUnwantedAttributes(skippedAttributes, newCellFragment, controller);

    return newCellFragment;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public final void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    if (!supportsChangeTracking && authorAccess.getReviewController().isTrackingChanges()) {
      String behavior = getChangeTrackingBehavior(authorAccess, args);
      if (CHANGE_TRACKING_BEHAVIOR_ALLOW.equals(behavior)) {
        doOperationInternal(authorAccess, args);
      } else if (CHANGE_TRACKING_BEHAVIOR_ALLOW_WITHOUT.equals(behavior)) {
        doOperationWithoutChangeTracking(authorAccess, args);
      } else if (CHANGE_TRACKING_BEHAVIOR_BLOCK.equals(behavior)) {
        authorAccess.getWorkspaceAccess().showErrorMessage(
            authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.TABLE_OPERATION_WHEN_TC_ERROR_MESSAGE));
      }
    } else {
      doOperationInternal(authorAccess, args);
    }
  }

  /**
   * Get the configured behavior when change tracking is enabled.
   * 
   * @param authorAccess The author access object.
   * @param args The operation arguments.
   * 
   * @return The change tracking behavior.
   */
  private static String getChangeTrackingBehavior(AuthorAccess authorAccess, ArgumentsMap args) {
    String behavior = (String) args.getArgumentValue(CHANGE_TRACKING_BEHAVIOR);
    if (behavior == null) {
      behavior = CHANGE_TRACKING_BEHAVIOR_AUTO;
    }
    if (CHANGE_TRACKING_BEHAVIOR_AUTO.equals(behavior)) {
      if (authorAccess.getWorkspaceAccess().getPlatform() == Platform.WEBAPP) {
        behavior = CHANGE_TRACKING_BEHAVIOR_BLOCK;
      } else {
        int response =  authorAccess.getWorkspaceAccess().showConfirmDialog(
            authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.TRACK_CHANGES), 
            authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.ACTION_NOT_MARKED_AS_CHANGE), 
            new String[] {
                "OK",
                authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.CANCEL) },
            new int[] { 1, 0 });
        behavior = response == 1 ? 
            CHANGE_TRACKING_BEHAVIOR_ALLOW_WITHOUT : CHANGE_TRACKING_BEHAVIOR_BLOCK;
      }
    }
    return behavior;
  }

  private void doOperationWithoutChangeTracking(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    // Turn off the track changes
    authorAccess.getReviewController().toggleTrackChanges();
    try {
      doOperationInternal(authorAccess, args);
    } finally {
      // Restore track changes state
      if (!authorAccess.getReviewController().isTrackingChanges()) {
        authorAccess.getReviewController().toggleTrackChanges();
      }
    }
  }
  
  /**
   * Perform the actual operation.
   *  
   * @param authorAccess The author access.
   * Provides access to specific informations and actions for 
   * editor, document, workspace, tables, change tracking, utility a.s.o.
   * @param args The map of arguments. <strong>All the arguments defined by method 
   * {@link #getArguments()} must be present in the map of arguments.</strong>
   * @throws IllegalArgumentException Thrown when one or more arguments are illegal.
   * @throws AuthorOperationException Thrown when the operation fails.
   */
  protected abstract void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args) 
    throws AuthorOperationException;
}