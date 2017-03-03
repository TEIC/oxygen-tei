/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2015 Syncro Soft SRL, Romania.  All rights
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

import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorExternalObjectInsertionHandler;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.ReferenceType;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResultInsertConstants;

/**
 * Dropped URLs handler
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class TEIP5ExternalObjectInsertionHandler extends AuthorExternalObjectInsertionHandler{
  
  /**
   * Logger for logging. 
   */
  private static Logger logger = Logger.getLogger(TEIP5ExternalObjectInsertionHandler.class.getName());

  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorExternalObjectInsertionHandler#insertURLs(ro.sync.ecss.extensions.api.AuthorAccess, java.util.List, java.util.List, int)
   */
  @Override
  public void insertURLs(AuthorAccess authorAccess, List<URL> urls, List<ReferenceType> types,
      int source) throws AuthorOperationException {
    if(! urls.isEmpty()) {
      URL base = getBaseURLAtCaretPosition(authorAccess);
      for (int i = 0; i < urls.size(); i++) {
        URL url = urls.get(i);
        ReferenceType type = types.get(i);
        String relativeLocation = authorAccess.getUtilAccess().makeRelative(base, url);
        relativeLocation = authorAccess.getXMLUtilAccess().escapeAttributeValue(relativeLocation);
        SchemaAwareHandlerResult result = null;
        int cp = authorAccess.getEditorAccess().getCaretOffset();
        AuthorElement elementAtOffset = null; 
        try {
          AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(cp);
          if(nodeAtOffset.getType() == AuthorNode.NODE_TYPE_ELEMENT){
            elementAtOffset = (AuthorElement) nodeAtOffset;
          }
        } catch (BadLocationException e) {
          logger.error(e, e);
        }
        if(isImageReference(authorAccess, type, url)) {
          if(elementAtOffset != null && "graphic".equals(elementAtOffset.getLocalName())){
            //This is already an image, set the attribute to it.
            authorAccess.getDocumentController().setAttribute("url", new AttrValue(relativeLocation), elementAtOffset);
          } else{
            //We have to make an image reference to it.
            // Insert the graphic
            result = authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
                "<graphic url=\"" + relativeLocation + "\" xmlns=\"http://www.tei-c.org/ns/1.0\"/>" ,
                cp, true);
          }
        } else {
          //Probably add an xref to it.
          result = authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
              "<ptr target=\"" + relativeLocation + "\" xmlns=\"http://www.tei-c.org/ns/1.0\"/>" ,
              cp, true);
        }
        if(result != null && i < urls.size() - 1) {
          //Move after the inserted element to insert the next one.
          Integer off = (Integer) result.getResult(SchemaAwareHandlerResultInsertConstants.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET);
          if(off != null) {
            authorAccess.getEditorAccess().setCaretPosition(off.intValue() + 2);
          }
        }
      }
    }
  }
  
  /**
   * Checks if the URL refers to an image.
   * 
   * @param authorAccess The author access.
   * @param type The reference type.
   * @param url The URL.
   * 
   * @return <code>true</code> if the URL is an image reference.
   */
  private boolean isImageReference(AuthorAccess authorAccess, ReferenceType type, URL url) {
    return type == ReferenceType.IMAGE_REFERENCE || 
        type == null && authorAccess.getUtilAccess().isSupportedImageURL(url);
  }
  
  /**
   * @throws AuthorOperationException 
   * @see ro.sync.ecss.extensions.api.AuthorExternalObjectInsertionHandler#insertURLs(ro.sync.ecss.extensions.api.AuthorAccess, java.util.List, int)
   */
  @Override
  public void insertURLs(AuthorAccess authorAccess, List<URL> urls, int source) throws AuthorOperationException {
    List<ReferenceType> types = Collections.<ReferenceType>nCopies(urls.size(), null);
    insertURLs(authorAccess, urls, types, source);
  }
  

  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorExternalObjectInsertionHandler#getImporterStylesheetFileName(ro.sync.ecss.extensions.api.AuthorAccess)
   */
  @Override
  protected String getImporterStylesheetFileName(AuthorAccess authorAccess) {
    return "xhtml2tei5Driver.xsl";
  }
}
