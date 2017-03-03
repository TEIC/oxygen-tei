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

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorActionEventHandler;
import ro.sync.ecss.extensions.api.AuthorImageDecorator;
import ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandler;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.EditPropertiesHandler;
import ro.sync.ecss.extensions.api.ExtensionsBundle;
import ro.sync.ecss.extensions.api.TEIAuthorActionEventHandler;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.table.spansupport.TEITableCellSpanProvider;
import ro.sync.exml.workspace.api.node.customizer.XMLNodeRendererCustomizer;

/**
 * The TEI framework extensions bundle.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class TEIExtensionsBundleBase extends ExtensionsBundle {
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(TEIExtensionsBundleBase.class.getName());
  
  /**
   * The TEI schema aware editing handler.
   */
  private TEISchemaAwareEditingHandler teiSchemaAwareEditingHandler;
  
  /**
   * Handles special actions. 
   */
  private AuthorActionEventHandler handler;
  
  /**
   * Image decorator for TEI.
   */
  private TEIAuthorImageDecorator decorator;

  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#createAuthorTableCellSpanProvider()
   */
  @Override
  public AuthorTableCellSpanProvider createAuthorTableCellSpanProvider() {
    return new TEITableCellSpanProvider();
  }
  
  /**
   * @return The document namespace.
   */
  protected abstract String getDocumentNamespace();
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#getAuthorSchemaAwareEditingHandler()
   */
  @Override
  public AuthorSchemaAwareEditingHandler getAuthorSchemaAwareEditingHandler() {
    if (teiSchemaAwareEditingHandler == null) {
      teiSchemaAwareEditingHandler = new TEISchemaAwareEditingHandler(getDocumentNamespace());
    }
    return teiSchemaAwareEditingHandler;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#createXMLNodeCustomizer()
   */
  @Override
  public XMLNodeRendererCustomizer createXMLNodeCustomizer() {
    return new TEINodeRendererCustomizer();
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#getAuthorActionEventHandler()
   */
  @Override
  public AuthorActionEventHandler getAuthorActionEventHandler() {
    if (handler == null) {
      handler = new TEIAuthorActionEventHandler();
    }
    
    return handler;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#getAuthorImageDecorator()
   */
  @Override
  public AuthorImageDecorator getAuthorImageDecorator() {
    if (decorator == null) {
      decorator = new TEIAuthorImageDecorator();
    }
    
    return decorator;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.ExtensionsBundle#createEditPropertiesHandler()
   */
  @Override
  public EditPropertiesHandler createEditPropertiesHandler() {
    return new EditPropertiesHandler() {
      
      @Override
      public String getDescription() {
        return "Handles imagemap editing";
      }
      
      @Override
      public void editProperties(AuthorNode authorNode, AuthorAccess authorAccess) {
        try {
          new EditImageMapOperation().doOperation(authorAccess, null);
        } catch (Exception e) {
          logger.error(e, e);
        }
      }
      
      @Override
      public boolean canEditProperties(AuthorNode authorNode) {
        boolean isHandled = false;
        if (authorNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
          AuthorElement element = (AuthorElement) authorNode;
          if (element.getLocalName().equals("surface")) {
            isHandled = true;
          } else if (element.getLocalName().equals("graphic")) {
            AuthorElement parent = (AuthorElement) element.getParentElement();
            if (parent != null && parent.getLocalName().equals("surface")) {
              isHandled = true;
            }
          }
        }
        return isHandled;
      }
    };
  }
}