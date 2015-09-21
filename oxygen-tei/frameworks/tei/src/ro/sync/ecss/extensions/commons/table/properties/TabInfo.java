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

import javax.swing.text.Position;




import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;

/**
 * Information associated with a tab from the 'Table Properties' dialog. 
 * 
 * @author adriana_sbircea
 */

public class TabInfo {
  /**
   * The tab key name. If no translation for the tab, then it represents the name
   * of the tab.
   */
  private String tabKey = null;
  /**
   * The list with the properties which will be presented in the current tab. 
   */
  private List<TableProperty> properties = null;
  /**
   * The nodes whose properties will be edited.
   */
  private List<AuthorElement> nodes = null;
  /**
   * The list with fragments which will be inserted in the document after a property that affects
   * table structure is modified.
   */
  private List<AuthorDocumentFragment> fragmentsToInsert = null;
  /**
   * The insets where the fragment will be inserted. 
   */
  private Position[] insertOffsets = null;
  /**
   * A string that specifies the context of the properties. For example for "Row(s)"
   * tab, if one row is edited, it will be "The current row is edited".
   */
  private String contextInfo = null;

  /**
   * Constructor.
   * 
   * @param key         The tab key name. If no translation for the tab, then it represents the name
   * of the tab.
   * @param properties  The list with the properties which will be presented in the current tab.
   * @param nodes       The nodes whose properties will be edited.
   */
  public TabInfo(String key, List<TableProperty> properties, List<AuthorElement> nodes) {
    this(key, properties, nodes, null, null);
  }
  
  /**
   * Constructor.
   * 
   * @param key         The tab key name. If no translation for the tab, then it represents the name
   * of the tab.
   * @param properties  The list with the properties which will be presented in the current tab.
   * @param nodes       The nodes whose properties will be edited.
   * @param fragmentsToInsert The list of {@link AuthorDocumentFragment}s to be inserted.
   * @param offsets     The offsets where the new fragments will be inserted.
   */
  public TabInfo(
      String key, 
      List<TableProperty> properties, 
      List<AuthorElement> nodes, 
      List<AuthorDocumentFragment> fragmentsToInsert, 
      Position[] offsets) {
    this(key, properties, nodes, fragmentsToInsert, offsets, null);
  }
  
  /**
   * Constructor.
   * 
   * @param key                 The tab key name. If no translation for the tab, 
   *                            then it represents the name of the tab.
   * @param properties          The list with the properties which will be presented in the current tab.
   * @param nodes               The nodes whose properties will be edited.
   * @param fragmentsToInsert   The fragments to be inserted.
   * @param offsets             The offsets where the new fragments will be inserted.
   * @param contextInfo         The context information of the current tab. If no context information, then it will be <code>null</code>.
   */
  public TabInfo(
      String key, 
      List<TableProperty> properties, 
      List<AuthorElement> nodes, 
      List<AuthorDocumentFragment> fragmentsToInsert, 
      Position[] offsets, 
      String contextInfo) {
    this.tabKey = key;
    this.properties = properties;
    this.nodes = nodes;
    this.fragmentsToInsert = fragmentsToInsert;
    this.insertOffsets = offsets;
    this.contextInfo = contextInfo;
  }

  /**
   * Return the tab key name. If no translation for the tab, then it represents the name
   * of the tab.
   * @return Returns the tab key.
   */
  public String getTabKey() {
    return tabKey;
  }

  /**
   * Set the tab key name. If no translation for the tab, then it represents the name
   * of the tab.
   * @param tabKey The new tab Key.
   */
  public void setTabKey(String tabKey) {
    this.tabKey = tabKey;
  }

  /**
   * Obtain the list with the properties which will be presented in the current tab. 
   * 
   * @return Returns the  the list with the properties which will be presented in the current tab.
   */
  public List<TableProperty> getProperties() {
    return properties;
  }

  /**
   * Set the list with the properties which will be presented in the current tab.
   *  
   * @param properties The new properties to set.
   */
  public void setProperties(List<TableProperty> properties) {
    this.properties = properties;
  }

  /**
   * The nodes whose properties will be edited.
   * 
   * @return Returns the nodes whose properties will be edited..
   */
  public List<AuthorElement> getNodes() {
    return nodes;
  }

  /**
   * Set the nodes whose properties will be edited.
   * 
   * @param nodes The new list of nodes to set.
   */
  public void setNodes(List<AuthorElement> nodes) {
    this.nodes = nodes;
  }

  /**
   * Get the fragments which will be inserted in the document.
   * 
   * @return Returns the fragments which will be inserted in the document.
   */
  public List<AuthorDocumentFragment> getFragmentsToInsert() {
    return fragmentsToInsert;
  }

  /**
   * Set the fragments which will be inserted in the document.
   * 
   * @param fragmentsToInsert The fragments which will be inserted in the document.
   */
  public void setFragmentsToInsert(List<AuthorDocumentFragment> fragmentsToInsert) {
    this.fragmentsToInsert = fragmentsToInsert;
  }

  /**
   * Get the position where the fragments will be inserted.
   * 
   * @return Returns the position where the fragments will be inserted.
   */
  public Position[] getInsertOffsets() {
    return insertOffsets;
  }

  /**
   * Sets the position where the fragments will be inserted.
   * @param positions The position where the fragments will be inserted.
   */
  public void setInsertOffsets(Position[] positions) {
    this.insertOffsets = positions;
  }
  
  /**
   * Obtain the context information of the current tab.
   * 
   * @return Returns the context information.
   */
  public String getContextInfo() {
    return contextInfo;
  }

  /**
   * Set the context information.
   * 
   * @param contextInfo The context information to set.
   */
  public void setContextInfo(String contextInfo) {
    this.contextInfo = contextInfo;
  }
}