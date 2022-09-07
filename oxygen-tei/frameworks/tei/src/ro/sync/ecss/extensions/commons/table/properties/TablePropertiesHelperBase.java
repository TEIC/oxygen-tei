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
package ro.sync.ecss.extensions.commons.table.properties;

import java.util.ArrayList;
import java.util.List;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.commons.table.operations.TableOperationsUtil;

/**
 * Abstract class for table properties helper.
 * 
 * @author adriana_sbircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class TablePropertiesHelperBase implements TablePropertiesHelper {

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isTable(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean isTable(AuthorElement node) {
    return false;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isTableGroup(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean isTableGroup(AuthorElement node) {
    return false;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isTableBody(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean isTableBody(AuthorElement node) {
    return false;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isTableHead(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean isTableHead(AuthorElement node) {
    return false;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isTableFoot(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean isTableFoot(AuthorElement node) {
    return false;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isTableRow(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean isTableRow(AuthorElement node) {
    return false;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isTableCell(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean isTableCell(AuthorElement node) {
    return false;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isTableColspec(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean isTableColspec(AuthorElement node) {
    return false;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isNodeOfType(ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  public boolean isNodeOfType(AuthorElement node, int type) {
    boolean toReturn = false;
    // Check the given type
    switch (type) {
      case TYPE_ROW:
        toReturn = isTableRow(node);
        break;
      case TYPE_BODY:
        toReturn = isTableBody(node);
        break;
      case TYPE_HEADER:
        toReturn = isTableHead(node);
        break;
      case TYPE_FOOTER:
        toReturn = isTableFoot(node);
        break;
      case TYPE_GROUP:
        toReturn = isTableGroup(node);
        break;
      case TYPE_TABLE:
        toReturn = isTable(node);
        break;
      case TYPE_CELL: 
        toReturn = isTableCell(node);
        break;
      case TYPE_COLSPEC: 
        toReturn = isTableColspec(node);
        break;
    }
    
    return toReturn;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#allowsFooter()
   */
  @Override
  public boolean allowsFooter() {
    return false;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#isTableHead(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public AuthorElement getFirstChildOfTypeFromParentWithType(AuthorElement currentRow, int childType, int parentType) {
    AuthorElement firstChild = null;
    // Obtain the parent with the given type 
    AuthorElement parentElement = TableOperationsUtil.getElementAncestor(currentRow, parentType, this);
    if (parentElement != null) {
      List<AuthorElement> children = new ArrayList<AuthorElement>();
      // Obtain the children with the given type
      TableOperationsUtil.getChildElements(parentElement, childType, children, this);

      if (!children.isEmpty()) {
        firstChild = children.get(0);
      }
    }
    return firstChild;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#getElementType(ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public int getElementType(AuthorElement node) {
    int type = -1;
    // Check the type of the given node
    if (isNodeOfType(node, TYPE_TABLE)) {
      type = TYPE_TABLE;
    } else if (isNodeOfType(node, TYPE_GROUP)) {
      type = TYPE_GROUP;
    } else if (isNodeOfType(node, TYPE_HEADER)) {
      type = TYPE_HEADER;
    } else if (isNodeOfType(node, TYPE_FOOTER)) {
      type = TYPE_FOOTER;
    } else if (isNodeOfType(node, TYPE_BODY)) {
      type = TYPE_BODY;
    } else if (isNodeOfType(node, TYPE_COLSPEC)) {
      type = TYPE_COLSPEC;
    } else if (isNodeOfType(node, TYPE_ROW)) {
      type = TYPE_ROW;
    } else if (isNodeOfType(node, TYPE_CELL)) {
      type = TYPE_CELL;
    }
    
    return type;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#getElementTag(int)
   */
  @Override
  public String getElementTag(int elementType) {
    return null;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.TablePropertiesHelper#getElementName(int)
   */
  @Override
  public String getElementName(int elementType) {
    return null;
  }
}
