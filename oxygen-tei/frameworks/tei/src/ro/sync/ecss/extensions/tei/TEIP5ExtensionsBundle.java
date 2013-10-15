/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2007 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.tei;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorExtensionStateListener;
import ro.sync.ecss.extensions.api.AuthorExternalObjectInsertionHandler;
import ro.sync.ecss.extensions.api.UniqueAttributesRecognizer;
import ro.sync.ecss.extensions.api.content.ClipboardFragmentProcessor;
import ro.sync.ecss.extensions.api.link.IDTypeRecognizer;
import ro.sync.ecss.extensions.api.table.operations.AuthorTableOperationsHandler;
import ro.sync.ecss.extensions.tei.id.TEIP5IDTypeRecognizer;
import ro.sync.ecss.extensions.tei.id.TEIP5UniqueAttributesRecognizer;

/**
 * The TEI P5 framework extensions bundle.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class TEIP5ExtensionsBundle extends TEIExtensionsBundleBase {
  /**
   * Table operations handler
   */
  private TEIAuthorTableOperationsHandler tableOperationsHandler;
  /**
   * Unique attrs recognizer
   */
  private TEIP5UniqueAttributesRecognizer uniqueAttributesRecognizer;

  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#createAuthorExtensionStateListener()
   */
  @Override
  public AuthorExtensionStateListener createAuthorExtensionStateListener() {
    uniqueAttributesRecognizer = new TEIP5UniqueAttributesRecognizer();
    return uniqueAttributesRecognizer;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "TEI P5 extensions bundle implementation";
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#getDocumentTypeID()
   */
  @Override
  public String getDocumentTypeID() {
    return "TEI.P5.document.type";
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
    return "http://www.tei-c.org/ns/1.0";
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#createExternalObjectInsertionHandler()
   */
  @Override
  public AuthorExternalObjectInsertionHandler createExternalObjectInsertionHandler() {
    return new TEIP5ExternalObjectInsertionHandler();
  };
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#getAuthorTableOperationsHandler()
   */
  @Override
  public AuthorTableOperationsHandler getAuthorTableOperationsHandler() {
    if (tableOperationsHandler == null) {
      tableOperationsHandler  = new TEIAuthorTableOperationsHandler(getDocumentNamespace());
    }
    return tableOperationsHandler;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#createIDTypeRecognizer()
   */
  @Override
  public IDTypeRecognizer createIDTypeRecognizer() {
    return new TEIP5IDTypeRecognizer();
  }

}