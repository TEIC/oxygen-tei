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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.commons.table.support.CALSColSpec;
import ro.sync.ecss.extensions.commons.table.support.CALSTableCellInfoProvider;


/**
 * @author adriana_sbircea
 *
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class CALSShowTableProperties extends CALSAndHTMLShowTablePropertiesBase {
  
  /**
   * Constructor.
   * 
   * @param helper The table properties.
   */
  public CALSShowTableProperties(TablePropertiesHelper helper) {
    super(helper);
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.ShowTablePropertiesBaseOperation#getElementsWithModifiedAttributes(ro.sync.ecss.extensions.commons.table.properties.EditedTablePropertiesInfo)
   */
  @Override
  protected List<TabInfo> getElementsWithModifiedAttributes(EditedTablePropertiesInfo tableInfo) {
    List<TabInfo> toModify = new ArrayList<TabInfo>();
    List<TableProperty> attrs;
    List<TabInfo> categories = tableInfo.getCategories();
    
    for (int i = 0; i < categories.size(); i++) {
      attrs = new ArrayList<TableProperty>();
      TabInfo tabInfo = categories.get(i);
      List<AuthorElement> nodes = tabInfo.getNodes();
      List<TableProperty> props = tabInfo.getProperties();
      if (!nodes.isEmpty() 
          && tableHelper.isNodeOfType(nodes.get(0), TablePropertiesConstants.TYPE_TABLE)) {
        // For table nodes check the attribute and create two tab info, one for table element with corresponding attributes,
        // and second for tgroup elements with the corresponding attributes.
        List<TableProperty> tableAttributes = new ArrayList<TableProperty>();
        List<TableProperty> tgroupAttributes = new ArrayList<TableProperty>();
        List<AuthorElement> tableElements = new ArrayList<AuthorElement>();
        List<AuthorElement> tgroupElements = new ArrayList<AuthorElement>();
        for (int j = 0; j < nodes.size(); j++) {
          if (tableHelper.isNodeOfType(nodes.get(j), TablePropertiesConstants.TYPE_GROUP)) {
            // Tgroup element
            tgroupElements.add(nodes.get(j));
          } else {
            // Table element
            tableElements.add(nodes.get(j));
          }
        }
        for (int j = 0; j < props.size(); j++) {
          TableProperty prop = props.get(j);
          if (prop.isAttribute()) {
            // Check for "<not set> and "preserve" values
            if (!TablePropertiesConstants.PRESERVE.equals(prop.getCurrentValue())) {
              if (TablePropertiesConstants.ATTR_NOT_SET.equals(prop.getCurrentValue())) {
                prop.setCurrentValue(null);
              }
              
              // For table properties, the align attribute must be set on tgroup element
              if (TablePropertiesConstants.ALIGN.equals(prop.getAttributeName())) {
                tgroupAttributes.add(prop);
              } else {
                tableAttributes.add(prop);
              }
            }
          }
        }
        
        // Check if there are some attributes which should be really modified
        if (!tgroupAttributes.isEmpty() && !tgroupElements.isEmpty()) {
          toModify.add(new TabInfo(tabInfo.getTabKey(), tgroupAttributes, tgroupElements));
        }

        if (!tableAttributes.isEmpty() && !tableElements.isEmpty()) {
          toModify.add(new TabInfo(tabInfo.getTabKey(), tableAttributes, tableElements));
        }
        
      } else {
        // Not table , so set the attribute on the current element
        for (int j = 0; j < props.size(); j++) {
          TableProperty prop = props.get(j);
          if (prop.isAttribute()) {
            if (!TablePropertiesConstants.PRESERVE.equals(prop.getCurrentValue())) {
              if (TablePropertiesConstants.ATTR_NOT_SET.equals(prop.getCurrentValue())) {
                prop.setCurrentValue(null);
              }
              attrs.add(prop);
            }
          }
        }

        toModify.add(new TabInfo(tabInfo.getTabKey(), attrs, tabInfo.getNodes()));
      }
    }
    
    return toModify;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.CALSAndHTMLShowTablePropertiesBase#getColSpecs(Map)
   */
  @Override
  protected List<AuthorElement> getColSpecs(Map<AuthorElement, Set<Integer>> map) {
    CALSTableCellInfoProvider calsTableCellInfoProvider = new CALSTableCellInfoProvider();
    List<AuthorElement> colspecs = new ArrayList<AuthorElement>();
    Set<AuthorElement> keySet = map.keySet();
    for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
      AuthorElement tgroupElement = (AuthorElement) iterator.next();
      // For every tgroup obtain the colspec because theirs indexes are based on parent tgroup
      calsTableCellInfoProvider.init(tgroupElement);
      Set<Integer> set = map.get(tgroupElement);
      for (Iterator iterator2 = set.iterator(); iterator2.hasNext();) {
        Integer index = (Integer) iterator2.next();
        // Get colspec for index
        CALSColSpec colSpec = calsTableCellInfoProvider.getColSpec(index);
        if (colSpec != null) {
          // Obtain the colspec element
          AuthorElement colSpecElement = calsTableCellInfoProvider.getColSpecElement(colSpec);
          if (colSpecElement != null) {
            colspecs.add(colSpecElement);
          }
        }
      }
    }
    
    return colspecs;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.properties.CALSAndHTMLShowTablePropertiesBase#getCellIndexes(java.util.List)
   */
  @Override
  protected Map<AuthorElement, Set<Integer>> getCellIndexes(List<AuthorElement> cells) {
    Map<AuthorElement, Set<Integer>> indexes = new HashMap<AuthorElement, Set<Integer>>();
    for (int i = 0; i < cells.size(); i++) {
      // For every computed cell, obtain the parent tgroup
      AuthorElement tgroup = tableHelper.getElementAncestor(cells.get(i), TablePropertiesHelper.TYPE_GROUP);
      Set<Integer> set = indexes.get(tgroup);
      if (set == null) {
        set = new HashSet<Integer>();
      }
      // Obtain the column span indices for a cell
      int[] tableColSpanIndices = authorAccess.getTableAccess().getTableColSpanIndices(cells.get(i));
      for (int j = 0; tableColSpanIndices != null && j < tableColSpanIndices.length; j++) {
        // Add all indices
        set.add(tableColSpanIndices[j] + 1);
      }
      
      indexes.put(tgroup, set);
    }
    
    return indexes;
  }
}
