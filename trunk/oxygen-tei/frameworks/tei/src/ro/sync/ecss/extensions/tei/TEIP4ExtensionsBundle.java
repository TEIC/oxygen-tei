/**
 * Copyright 2011 Syncro Soft SRL, Romania. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:

 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 * 
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Syncro Soft SRL ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Syncro Soft SRL OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Syncro Soft SRL.
 */
package ro.sync.ecss.extensions.tei;

import ro.sync.ecss.extensions.api.AuthorExtensionStateListener;
import ro.sync.ecss.extensions.api.AuthorExternalObjectInsertionHandler;
import ro.sync.ecss.extensions.api.UniqueAttributesRecognizer;
import ro.sync.ecss.extensions.api.content.ClipboardFragmentProcessor;
import ro.sync.ecss.extensions.tei.id.TEIP4UniqueAttributesRecognizer;

/**
 * The TEI P4 framework extensions bundle.
 */
public class TEIP4ExtensionsBundle extends TEIExtensionsBundleBase {
  
  /**
   * The unique attributes recognizer
   */
  private TEIP4UniqueAttributesRecognizer uniqueAttributesRecognizer;

  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#createAuthorExtensionStateListener()
   */
  @Override
  public AuthorExtensionStateListener createAuthorExtensionStateListener() {
    uniqueAttributesRecognizer = new TEIP4UniqueAttributesRecognizer();
    return uniqueAttributesRecognizer;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  public String getDescription() {
    return "TEI P4 extensions bundle implementation";
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#getDocumentTypeID()
   */
  @Override
  public String getDocumentTypeID() {
    return "TEI.P4.document.type";
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#getUniqueAttributesIdentifier()
   */
  @Override
  public UniqueAttributesRecognizer getUniqueAttributesIdentifier() {
    return uniqueAttributesRecognizer;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#getClipboardFragmentProcessor()
   */
  @Override
  public ClipboardFragmentProcessor getClipboardFragmentProcessor() {
    return uniqueAttributesRecognizer;
  }

  /**
   * @see ro.sync.ecss.extensions.tei.TEIExtensionsBundleBase#getDocumentNamespace()
   */
  @Override
  protected String getDocumentNamespace() {
    return "";
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#createExternalObjectInsertionHandler()
   */
  @Override
  public AuthorExternalObjectInsertionHandler createExternalObjectInsertionHandler() {
    return new TEIP4ExternalObjectInsertionHandler();
  }
}