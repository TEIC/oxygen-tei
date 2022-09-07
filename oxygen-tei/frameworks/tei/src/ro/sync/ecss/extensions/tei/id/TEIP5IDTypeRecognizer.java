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
package ro.sync.ecss.extensions.tei.id;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.contentcompletion.xml.Context;
import ro.sync.contentcompletion.xml.ContextElement;
import ro.sync.ecss.extensions.api.link.CannotRecognizeIDException;
import ro.sync.ecss.extensions.api.link.DefaultIDTypeIdentifier;
import ro.sync.ecss.extensions.api.link.IDTypeIdentifier;
import ro.sync.ecss.extensions.api.link.IDTypeRecognizer;

/**
 * Implementation of ID declarations and references recognizer for TEI P5 framework.
 * 
 * In this framework the IDs are declared in attributes with name 'id'. The references are recognized
 * in attributes ptr/@target or ref/@target, see http://www.tei-c.org/release/doc/tei-p5-doc/en/html/ref-ptr.html. 
 * 
 * @author radu_pisoi
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class TEIP5IDTypeRecognizer extends IDTypeRecognizer {
  
  /**
   * @see ro.sync.ecss.extensions.api.link.IDTypeRecognizer#detectIDType(java.lang.String, ro.sync.contentcompletion.xml.Context, java.lang.String, java.lang.String, java.lang.String, int)
   */
  @Override
  public List<IDTypeIdentifier> detectIDType(String systemID, Context context, String attrName,
      String attrNs, String attributeValue, int offset) throws CannotRecognizeIDException {
    List<IDTypeIdentifier> idTypeIdentifiers = new ArrayList<IDTypeIdentifier>();
    
    if(attributeValue != null && !attributeValue.trim().isEmpty()) {
      if("id".equals(attrName)) {
        // xml:id attribute
        DefaultIDTypeIdentifier idTypeIdentifier = new DefaultIDTypeIdentifier(attributeValue.trim(), true);
        idTypeIdentifiers.add(idTypeIdentifier);
      } else if("target".equals(attrName)) {
        // 'target' attribute
        Stack<ContextElement> elementStack = context.getElementStack();
        if(!elementStack.isEmpty()) {
          // For ptr/@target or ref/@target the ID references are recognized if the attribute value
          // has the pattern #id1 #id2
          String idValue = null;

          StringTokenizer stringTokenizer = new StringTokenizer(attributeValue, " ", true);
          int idx = 0;
          while(stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();

            if(offset < idx) {
              break;
            }

            if(offset <= idx + nextToken.length()) {
              // Current token include the offset
              if(!" ".equals(nextToken)) {
                idValue = nextToken;
              }
              break;
            }

            idx += nextToken.length();
          }


          if (idValue != null && !"".equals(idValue.trim()) && idValue.startsWith("#")) {
            idValue = idValue.substring(1);
            if (idValue.trim().length() > 0) {
              idTypeIdentifiers.add(new DefaultIDTypeIdentifier(idValue, false));
            }
          }
        }
      }
    }
    
    return idTypeIdentifiers.isEmpty() ? null : idTypeIdentifiers;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.link.IDTypeRecognizer#locateIDType(java.lang.String, ro.sync.contentcompletion.xml.Context, java.lang.String, java.lang.String, java.lang.String, ro.sync.ecss.extensions.api.link.IDTypeIdentifier, short)
   */
  @Override
  public int[] locateIDType(String systemID, Context context, String attrName, String attrNs,
      String attributeValue, IDTypeIdentifier idIdentifier, short mode) {
    
    int[] idLocation = null;
    
    if ((mode  & MODE_LOCATE_DECLARATIONS) != 0) {
      // xml:id declaration
      if("id".equals(attrName)) {
        idLocation = new int[] {0, attributeValue.length()};
      }
    }
    
    if ((mode  & MODE_LOCATE_REFERENCES) != 0) {
      if("target".equals(attrName)) {
        Stack<ContextElement> elementStack = context.getElementStack();
        if(!elementStack.isEmpty()) {
          String idValue = idIdentifier.getValue();
          String textToFind = "#" + idValue;
          int indexOf = attributeValue.indexOf(textToFind);
          while (indexOf >= 0) {

            if(indexOf + textToFind.length() == attributeValue.length() || 
                attributeValue.charAt(indexOf + textToFind.length()) == ' ') {
              idLocation = new int[] { indexOf + 1, indexOf + 1 + idIdentifier.getValue().length() };
            }

            if (idLocation != null) {
              break;
            } else {
              indexOf = attributeValue.indexOf(textToFind, indexOf + textToFind.length());
            }
          }   
        }
      }
    }
    return idLocation;
  }

  /**
   * @see ro.sync.ecss.extensions.api.link.IDTypeRecognizer#isDefaultIDTypeRecognitionAvailable()
   */
  @Override
  public boolean isDefaultIDTypeRecognitionAvailable() {
    return false;
  }

  /**
   * @see ro.sync.ecss.extensions.api.link.IDTypeRecognizer#isIDTypeRecognitionAvailable()
   */
  @Override
  public boolean isIDTypeRecognitionAvailable() {
    return true;
  }
}
