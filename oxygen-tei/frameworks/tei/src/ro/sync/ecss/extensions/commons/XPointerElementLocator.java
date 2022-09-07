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
package ro.sync.ecss.extensions.commons;

import java.util.Stack;
import java.util.StringTokenizer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.basic.util.NumberFormatException;
import ro.sync.basic.util.NumberParserUtil;
import ro.sync.ecss.extensions.api.link.Attr;
import ro.sync.ecss.extensions.api.link.ElementLocator;
import ro.sync.ecss.extensions.api.link.ElementLocatorException;
import ro.sync.ecss.extensions.api.link.IDTypeVerifier;

/**
 * Element locator for links that have the one of the following patterns:
 * <ul>
 *   <li>element(elementID) - locate the element with the same id</li>
 *   <li>element(/1/2/5) - A child sequence appearing alone identifies an element by means 
 *   of stepwise navigation, which is directed by a sequence of integers separated by slashes (/); 
 *   each integer n locates the nth child element of the previously located element. </li>
 *   <li>element(elementID/3/4) - A child sequence appearing after an 
 *   NCName identifies an element by means of stepwise navigation, 
 *   starting from the element located by the given name.</li>
 * </ul>
 *   
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class XPointerElementLocator extends ElementLocator {
  
  /** 
   * Logger for logging. 
   */
  private static final Logger logger = LoggerFactory.getLogger(XPointerElementLocator.class.getName());
  
  /**
   * Verifies if a given attribute has the ID type.
   */
  private IDTypeVerifier idVerifier;
  
  /**
   * XPointer path, the path to locate the linked element.
   */
  private String[] xpointerPath;

  /**
   * The stack with indexes in parent of the current iterated elements.
   */
  private Stack currentElementIndexStack = new Stack();

  /**
   * The number of elements in XPointer path.
   */
  private int xpointerPathDepth;
  
  /**
   * If <code>true</code> then the XPointer path starts with an element ID.
   */
  private boolean startWithElementID = false;
  
  /**
   * The depth of the current element in document, incremented in startElement. 
   */
  private int startElementDepth = 0;
  
  /**
   * Depth in document in the last endElement event. 
   */
  private int endElementDepth = 0;
  
  /**
   * The index in parent of the previous iterated element. Set in endElement().
   */
  private int lastIndexInParent;

  /**
   * Constructor.
   * 
   * @param idVerifier Verifies if an given attribute has the type ID. 
   * @param link       The link that gives the element position.    
   * @throws ElementLocatorException  When the link format is not supported.
   **/
  public XPointerElementLocator(IDTypeVerifier idVerifier, String link) throws ElementLocatorException {
    super(link);
    this.idVerifier = idVerifier;

    link = link.substring("element(".length(), link.length() - 1);
    
    if (link != null && link.length() > 0 ) {

      StringTokenizer stringTokenizer = new StringTokenizer(link, "/", false);
      xpointerPath = new String[stringTokenizer.countTokens()];
      int i = 0;
      while (stringTokenizer.hasMoreTokens()) {
        xpointerPath[i] = stringTokenizer.nextToken();
        boolean invalidFormat = false;

        // Empty XPointer component is not supported
        if(xpointerPath[i].length() == 0){
          invalidFormat = true;
        }

        if(i > 0){
          try {
            NumberParserUtil.parseInt(xpointerPath[i]);
          } catch (NumberFormatException e) {
            invalidFormat = true;
          }
        }

        if(invalidFormat){
          throw new ElementLocatorException(
              "Only the element() scheme is supported when locating XPointer links. " +
              "Supported formats: element(elementID), element(/1/2/3), element(elemID/2/3/4).");
        }
        i++;
      }

      if(Character.isDigit(xpointerPath[0].charAt(0))){
        // This is the case when XPointer have the following pattern /1/5/7
        xpointerPathDepth = xpointerPath.length;
      } else {
        // This is the case when XPointer starts with an element ID
        xpointerPathDepth = -1;
        startWithElementID  = true;
      }
    } else {
      // The "element() Scheme Syntax" should not be permitted. 
      // Inform user about this. 
      String errorMessage = "Syntax Error.\nThe XPointer element() scheme with no arguments is not permitted.";
      throw new ElementLocatorException(errorMessage);
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.link.ElementLocator#endElement(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(String uri, String localName, String name) {
    endElementDepth = startElementDepth;
    startElementDepth --;
    lastIndexInParent = ((Integer)currentElementIndexStack.pop()).intValue();
  }

  /**
   * @see ro.sync.ecss.extensions.api.link.ElementLocator#startElement(java.lang.String, java.lang.String, java.lang.String, ro.sync.ecss.extensions.api.link.Attr[])
   */
  @Override
  public boolean startElement(String uri, String localName, String name, Attr[] atts) {
    boolean linkLocated = false;
    // Increase current element document depth
    startElementDepth  ++;
    
    if (endElementDepth != startElementDepth) {
      // The current element is the first child of the parent
      currentElementIndexStack.push(Integer.valueOf(1));
    } else {
      // Another element in the parent element
      currentElementIndexStack.push(Integer.valueOf(lastIndexInParent + 1));
    }
    
    if (startWithElementID) {
      // This the case when XPointer path starts with an element ID.
      String xpointerElement = xpointerPath[0];
      for (int i = 0; i < atts.length; i++) {
        if(xpointerElement.equals(atts[i].getValue())){
          if(idVerifier.hasIDType(
              localName, uri, atts[i].getQName(), atts[i].getNamespace())){
            xpointerPathDepth = startElementDepth + xpointerPath.length - 1;
            break;
          }            
        }
      }
    }
        
    if(xpointerPathDepth == startElementDepth){
      
      // check if XPointer path matches with the current element path
      linkLocated = true;
      try {        
        int xpointerIdx = xpointerPath.length - 1;
        int stackIdx = currentElementIndexStack.size() - 1;
        int stopIdx = startWithElementID ? 1 : 0;
        while (xpointerIdx >= stopIdx && stackIdx >= 0) {
          int xpointerIndex = NumberParserUtil.parseInt(xpointerPath[xpointerIdx]);
          int currentElementIndex = ((Integer)currentElementIndexStack.get(stackIdx)).intValue();
          if(xpointerIndex != currentElementIndex) {
            linkLocated = false;
            break;
          }
          
          xpointerIdx--;
          stackIdx--;
        }

      } catch (NumberFormatException e) {
        logger.warn(e,e);
      }
    }
    return linkLocated;
  }
}