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
package ro.sync.ecss.extensions.commons;




import ro.sync.ecss.extensions.api.link.Attr;
import ro.sync.ecss.extensions.api.link.ElementLocator;
import ro.sync.ecss.extensions.api.link.ExtensionUtil;
import ro.sync.ecss.extensions.api.link.IDTypeVerifier;

/**
 * Implementation of an ElementLocator that locates elements based on a given 
 * link and checks if the attribute with the type ID matches the provided link.   
 */

public class IDElementLocator extends ElementLocator {

  /**
   * Class used to check if an attribute has ID type.
   */
  protected IDTypeVerifier idVerifier;
  
  /**
   * Constructor.
   * 
   * @param idVerifier Used to check if an attribute has ID type.
   * @param link The link used to identify an element.
   */
  public IDElementLocator(IDTypeVerifier idVerifier, String link) {
    super(link);
    this.idVerifier = idVerifier;
  }

  /**
   * @see ro.sync.ecss.extensions.api.link.ElementLocator#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String name) {
    // Nothing to do.
  }

  /**
   * @see ro.sync.ecss.extensions.api.link.ElementLocator#startElement(java.lang.String, java.lang.String, java.lang.String, ro.sync.ecss.extensions.api.link.Attr[])
   */
  @Override
  public boolean startElement(String uri, String localName, String name, Attr[] atts) {
    boolean elementFound = false;
    for (int i = 0; i < atts.length; i++) {
      if (link.equals(atts[i].getValue())) {
        if("xml:id".equals(atts[i].getQName())) {
          // xml:id attribute
          elementFound = true;          
        } else {
          // Check if attribute has ID type
          String attrLocalName = 
            ExtensionUtil.getLocalName(atts[i].getQName());
          String attrUri = atts[i].getNamespace();
          if (idVerifier.hasIDType(localName, uri, attrLocalName, attrUri)) {
            elementFound = true;
          }
        }
      }
    }
    
    return elementFound;
  }
}