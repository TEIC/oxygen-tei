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
package ro.sync.ecss.extensions.commons.table.spansupport;

import java.io.StringReader;

import junit.framework.TestCase;
import ro.sync.ecss.dom.AuthorDocumentImpl;
import ro.sync.ecss.dom.AuthorElementImpl;
import ro.sync.ecss.dom.AuthorSentinelNode;
import ro.sync.ecss.dom.builder.AuthorDocumentFactory;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;

/**
 * Test for the cell spanning in a tei table.
 * @author mircea
 */
public class TEITableCellSpanProviderTest extends TestCase {

  /**
   * Test we obtain the correct cell spanning for a tei table.
   * 
   * @author mircea
   * @throws Exception
   */
  public void testEXM_9678() throws Exception {
    String xml = 
      "<table>\r\n" + 
      " <row>\r\n" + 
      "   <cell cols=\"2\">11</cell>\r\n" + 
      "   <cell rows=\"2\">12</cell>\r\n" + 
      " </row>\r\n" + 
      " <row>\r\n" + 
      "   <cell cols=\"3\">2</cell>\r\n" + 
      " </row>\r\n" + 
      "</table>";
    AuthorDocumentImpl document = AuthorDocumentFactory.createFromTests(new StringReader(xml), "fake.xml", null, true, null);
    AuthorElementImpl table = (AuthorElementImpl) document.getRootElement();
    
    AuthorTableCellSpanProvider tableSupport = new TEITableCellSpanProvider();
    
    tableSupport.init(table);
    
    AuthorSentinelNode row = (AuthorSentinelNode) table.getContentNodes().get(0);
    
    // Test the col span.
    assertEquals(2, tableSupport.getColSpan((AuthorElementImpl) row.getContentNodes().get(0)).intValue());
    assertNull(tableSupport.getColSpan((AuthorElementImpl) row.getContentNodes().get(1)));
  
    // Test the row span.
    assertNull(tableSupport.getRowSpan((AuthorElementImpl) row.getContentNodes().get(0)));
    assertEquals(2, tableSupport.getRowSpan((AuthorElementImpl) row.getContentNodes().get(1)).intValue());
    
    row = (AuthorSentinelNode) table.getContentNodes().get(1);
    
    // Test the col span.
    assertEquals(3, tableSupport.getColSpan((AuthorElementImpl) row.getContentNodes().get(0)).intValue());
  
    // Test the row span.
    assertNull(tableSupport.getRowSpan((AuthorElementImpl) row.getContentNodes().get(0)));
  }

}