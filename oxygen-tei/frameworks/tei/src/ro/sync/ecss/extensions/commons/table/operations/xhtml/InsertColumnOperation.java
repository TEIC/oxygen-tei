/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2009 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.table.operations.xhtml;

import java.util.List;




import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.table.operations.TableColumnSpecificationInformation;
import ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper;
import ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase;

/**
 * Operation used to insert one or more XHTML table columns.
 */

@WebappCompatible(false)
public class InsertColumnOperation extends InsertColumnOperationBase implements XHTMLConstants {
  
  /**
   * Colspec element name
   */
  private static final String ELEMENT_NAME_COLSPEC = "col";
  
  /**
   * thead element name
   */
  private static final String ELEMENT_NAME_THEAD = "thead";

  /**
   * tbody element name
   */
  private static final String ELEMENT_NAME_TBODY = "tbody";
  
  /**
   * colgroup element name
   */
  private static final String ELEMENT_NAME_COLGROUP = "colgroup";

  /**
   * Constructor.
   */
  public InsertColumnOperation() {
    this(new XHTMLDocumentTypeHelper());
  }

  /**
   * Constructor.
   * 
   * @param documentTypeHelper Document type helper, has methods specific to a document type.
   */
  protected InsertColumnOperation(AuthorTableHelper documentTypeHelper) {
    super(documentTypeHelper);
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase#getCellElementName(ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  protected String getCellElementName(AuthorElement rowElement, int newColumnIndex) {
    String elemName = ELEMENT_NAME_TD;
    String name = getLocalName(rowElement.getParent().getName());
    if (ELEMENT_NAME_THEAD.equals(name)) {
      elemName = ELEMENT_NAME_TH;
    }
    return elemName;
  }
  
  /**
   * Get the local name from an qualified element or attribute name.
   * 
   * @param qName The name in a qualified form.
   * @return The local name, or <code>null</code> if the argument is <code>null</code>.
   */
  private String getLocalName(String qName) {
    String local = qName;
    if (qName != null) {
      int idx = qName.lastIndexOf(':');
      if (idx != -1) {
        local = qName.substring(idx + 1);
      }
    }
    return local;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase#updateColumnCellsSpan(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider, ro.sync.ecss.extensions.api.node.AuthorElement, int, ro.sync.ecss.extensions.api.table.operations.TableColumnSpecificationInformation, java.lang.String, int)
   */
  @Override
  protected void updateColumnCellsSpan(AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableSupport, AuthorElement tableElem, int newColumnIndex,
      TableColumnSpecificationInformation columnSpecification, String namespace, int noOfColumnsToBeInserted)
      throws AuthorOperationException {
    //Call super
    super.updateColumnCellsSpan(authorAccess, tableSupport, tableElem, newColumnIndex,
        columnSpecification, namespace, noOfColumnsToBeInserted);
    //EXM-31675 Now try to insert a colspec in the proper place.
    int referenceInsertionOffset = tableElem.getEndOffset();
    List<AuthorNode> tableChildren = tableElem.getContentNodes();
    int noOfEncounteredCols = 0;
    boolean foundColspecs = false;
    if(tableChildren != null){
      loop: for (int i = 0; i < tableChildren.size(); i++) {
        AuthorNode child = tableChildren.get(i);
        if(child.getType() == AuthorNode.NODE_TYPE_ELEMENT){
          //Element
          AuthorElement elem = (AuthorElement) child;
          String localName = elem.getLocalName();
          if(ELEMENT_NAME_THEAD.equals(localName) 
              || ELEMENT_NAME_TBODY.equals(localName)){
            //Did not encounter any col.
            referenceInsertionOffset = elem.getStartOffset();
            break;
          } else if (ELEMENT_NAME_COLSPEC.equals(localName)){
            foundColspecs = true;
            if(noOfEncounteredCols == newColumnIndex){
              referenceInsertionOffset = elem.getStartOffset();
              break loop;
            }
            noOfEncounteredCols ++;
          } else if (ELEMENT_NAME_COLGROUP.equals(localName)){
            foundColspecs = true;
            //We have to iterate inside the colgroup.
            List<AuthorNode> colgroupChildren = elem.getContentNodes();
            if(colgroupChildren != null){
              for (int j = 0; j < colgroupChildren.size(); j++) {
                AuthorNode cgCH = colgroupChildren.get(j);
                if(cgCH.getType() == AuthorNode.NODE_TYPE_ELEMENT){
                  AuthorElement cgElem = (AuthorElement) cgCH;
                  if (ELEMENT_NAME_COLSPEC.equals(cgElem.getLocalName())){
                    if(noOfEncounteredCols == newColumnIndex){
                      referenceInsertionOffset = cgElem.getStartOffset();
                      break loop;
                    }
                    noOfEncounteredCols ++;
                  }
                }
              }
            }
            if(noOfEncounteredCols == newColumnIndex){
              referenceInsertionOffset = elem.getStartOffset();
              break loop;
            }
            noOfEncounteredCols ++;
          }
        }
      }
    }
    if(referenceInsertionOffset != -1 
    		//Maybe user does not want colspecs on the table.
    		&& foundColspecs){
      StringBuilder newColSpecFragment = new StringBuilder();
      //EXM-31671: Insert a new empty <col/> element for each column.
      for (int i = 0; i < noOfColumnsToBeInserted; i++) {
        newColSpecFragment.append("<").append(ELEMENT_NAME_COLSPEC);
        if (namespace != null) {
          newColSpecFragment.append(" xmlns=\"").append(namespace).append("\"");
        }
        newColSpecFragment.append("/>");
      }
      authorAccess.getDocumentController().insertXMLFragment(newColSpecFragment.toString(), referenceInsertionOffset);
    }
  }
}