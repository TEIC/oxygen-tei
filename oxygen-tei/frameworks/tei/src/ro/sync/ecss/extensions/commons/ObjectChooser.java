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

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.text.BadLocationException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.access.AuthorUtilAccess;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 *  Base class for choosers dialogs.
 *  
 * @author adrian_sorop
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class ObjectChooser {

  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ObjectChooser.class.getName());

  /**
   * All the allowed extensions for an image.
   */
  public static final String[] ALLOWED_IMAGE_EXTENSIONS = new String[] {
      "gif", "jpg", "jpeg", "bmp", "png", "svg", "svgz", "wmf", "mathml", "mml", "cgm", "tif", "tiff", "eps", "ai", "pdf", "psd"};
  
  
  /**
   * Makes the given URL relative to the XML whose access object we are given. 
   * 
   * @param authorAccess The author access of the XML document.
   * @param url The url.
   * 
   * @return The relative URL.
   */
  public static String makeUrlRelative(AuthorAccess authorAccess, String url) {
    String filePath;
    AuthorUtilAccess util = authorAccess.getUtilAccess();
    try {
      URL baseURL = getBaseURL(authorAccess);
      filePath = authorAccess.getXMLUtilAccess().escapeAttributeValue(
          util.makeRelative(
              baseURL, 
              //Also remove user credentials if this is the case.
              util.removeUserCredentials(new URL(url))));
    } catch (MalformedURLException e1) {
      // If there is no protocol, let the path specified by the user
      filePath = url;
    }
    return filePath;
  }

  /**
   * Get the base URL
   * @param authorAccess The author access of the XML document.
   * 
   * @return The base URL
   */
  private static URL getBaseURL(AuthorAccess authorAccess) {
    URL baseURL = authorAccess.getEditorAccess().getEditorLocation();
    int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
    try {
      AuthorNode nodeAtOffset = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
      if(nodeAtOffset != null) {
        //EXM-28217 Prefer to compute relative locations based on the XML base URL.
        baseURL = nodeAtOffset.getXMLBaseURL();
      }
    } catch (BadLocationException e) {
      LOGGER.debug(e.getMessage(), e);
    }
    return baseURL;
  }
}
