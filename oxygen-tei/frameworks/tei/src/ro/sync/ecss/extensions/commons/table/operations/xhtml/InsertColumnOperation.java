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

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper;
import ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase;

/**
 * Operation used to insert an XHTML table column.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class InsertColumnOperation extends InsertColumnOperationBase implements XHTMLConstants {
  
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
}