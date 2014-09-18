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
package ro.sync.ecss.extensions.commons.table.properties;

import java.util.List;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper;

/**
 * Table helper for 'Table Properties' dialog.
 * 
 * @author adriana_sbircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public interface TablePropertiesHelper extends TablePropertiesConstants {
  /**
   * Checks if the given node represents the table element.
   * 
   * @param node  The node to be checked.
   * 
   * @return <code>true</code> if the given node is the table element.
   */
  boolean isTable(AuthorElement node);
  /**
   * Checks if the given node represents a table group element.
   * 
   * @param node  The node to be checked.
   * 
   * @return <code>true</code> if the given node is the table group element.
   */
  boolean isTableGroup(AuthorElement node);
  /**
   * Checks if the given node represents a table body element.
   * 
   * @param node  The node to be checked.
   * 
   * @return <code>true</code> if the given node is a table body element.
   */
  boolean isTableBody(AuthorElement node);
  /**
   * Checks if the given node represents a table head element.
   * 
   * @param node  The node to be checked.
   * 
   * @return <code>true</code> if the given node is a table head element.
   */
  boolean isTableHead(AuthorElement node);
  /**
   * Checks if the given node represents a table foot element.
   * 
   * @param node  The node to be checked.
   * 
   * @return <code>true</code> if the given node is a table foot element.
   */
  boolean isTableFoot(AuthorElement node);
  /**
   * Checks if the given node represents a table row element.
   * 
   * @param node  The node to be checked.
   * 
   * @return <code>true</code> if the given node is a table row element.
   */
  boolean isTableRow(AuthorElement node);
  /**
   * Checks if the given node represents a table cell element.
   * 
   * @param node  The node to be checked.
   * 
   * @return <code>true</code> if the given node is a table cell element.
   */
  boolean isTableCell(AuthorElement node);
  /**
   * Checks if the given node represents a table colspec element.
   * 
   * @param node  The node to be checked.
   * 
   * @return <code>true</code> if the given node is a table colspec element.
   */
  boolean isTableColspec(AuthorElement node);
  /**
   * Search for an ancestor {@link AuthorNode} with the specified type. 
   * 
   * @param node The starting node.
   * @param type The type of the ancestor.
   * @return     The ancestor node of the given <code>node</code> or the <code>node</code> 
   * itself if the type matches.
   */
  AuthorElement getElementAncestor(AuthorNode node, int type);
  /**
   * Test if an {@link AuthorNode} is an element and it has one of the following types:
   * {@link AuthorTableHelper#TYPE_CELL}, {@link AuthorTableHelper#TYPE_ROW} or 
   * {@link AuthorTableHelper#TYPE_TABLE}.
   * 
   * @param node  The node to be checked.
   * @param type  The type to search for.
   * @return      <code>true</code> if the <code>node</code> is an element with the specified type.
   */
  boolean isNodeOfType(AuthorElement node, int type);
  /**
   * <code>true</code> if the current table allows footer element.
   * 
   * @return <code>true</code> if the table allows footer.
   */
  boolean allowsFooter();
  /**
   * Obtain a list of children with the given type.
   * 
   * @param node The parent node.
   * @param type The type of the children.
   * @param children The list with collected children. Empty when the function is called.
   */
  void getChildElements(AuthorElement node, int type, List<AuthorElement> children);
  
  /**
   * Obtain the first row child of the parent which has the given type. The type could be one of
   * TYPE_HEADED, TYPE_BODY, TYPE_FOOTER.
   * 
   * @param currentRow  The current row element.
   * @param childType   The type of the child that is needed.
   * @param parentType  The type for the parent which will contain the returned row element.
   * 
   * @return The first row from the parent or null if a parent with the given type is not found or 
   * if it does not contain any rows.
   */
  AuthorElement getFirstChildOfTypeFromParentWithType(AuthorElement currentRow, int childType, int parentType);
  /**
   * Obtain the type of the given node. Type can be one of {@link TablePropertiesConstants#TYPE_TABLE},
   * {@link TablePropertiesConstants#TYPE_GROUP}, {@link TablePropertiesConstants#TYPE_HEADER},
   * {@link TablePropertiesConstants#TYPE_BODY}, {@link TablePropertiesConstants#TYPE_FOOTER},
   * {@link TablePropertiesConstants#TYPE_ROW}, {@link TablePropertiesConstants#TYPE_CELL},
   * {@link TablePropertiesConstants#TYPE_COLSPEC}.
   * 
   * @param node The node to compute type for.
   * 
   * @return The type of the given node or -1 if the node is not a table node.
   */
  int getElementType(AuthorElement node);
  /**
   * Obtain the element name.
   * 
   * @param elementType The type of the element.
   * 
   * @return the element tag.
   */
  String getElementTag(int elementType);
  /**
   * 
   * @param elementType The element type.
   * 
   * @return The element name.
   */
  String getElementName(int elementType);
}
