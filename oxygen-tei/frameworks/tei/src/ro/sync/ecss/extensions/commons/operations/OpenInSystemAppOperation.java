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
package ro.sync.ecss.extensions.commons.operations;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.text.BadLocationException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Node;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.basic.util.URLUtil;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.XPathVersion;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Detects the application that is associated with the given file in the OS
 * and uses it to open the file. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(true)
public class OpenInSystemAppOperation implements AuthorOperation {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(OpenInSystemAppOperation.class.getName());

  /**
   * An XPath expression that when run returns the path of the resource that must
   * be opened.
   */
  static final String ARGUMENT_RESOURCE_PATH = "resourcePath";
  /**
   * <code>true</code> if the value of the XPATH represents an unparsed entity name.
   */
  private static final String ARGUMENT_UNPARSED_ENTITY = "isUnparsedEntity";
  
  private static final String ARGUMENT_MEDIA_TYPE = "mediaType";

  /** Media Type video */
  public static final String MEDIA_TYPE_VIDEO = "video";
  /** Media Type audio */
  public static final String MEDIA_TYPE_AUDIO = "audio";
  /** Media Type Medial */
  public static final String MEDIA_TYPE_MEDIA = "media";
  /** Media Type image */
  public static final String MEDIA_TYPE_IMAGE = "image";
  /** Media Type html */
  public static final String MEDIA_TYPE_HTML = "html";
  /** Media Type PDF */
  public static final String MEDIA_TYPE_PDF = "pdf";

  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Constructor.
   */
  public OpenInSystemAppOperation() {
    arguments = new ArgumentDescriptor[3];
    // Argument defining the XML fragment that will be inserted.
    ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_RESOURCE_PATH,
        ArgumentDescriptor.TYPE_XPATH_EXPRESSION,
        "An XPath expression that gives the path of the resource that must be opened.");
    arguments[0] = argumentDescriptor;
    
    // Some vocabularies like DocBook have attributes who's values are unparsed entity names.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_UNPARSED_ENTITY,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "True if the value returned by the resourcePath argument represents the name of an unparsed entity.",
        new String[] {
            AuthorConstants.ARG_VALUE_TRUE,
            AuthorConstants.ARG_VALUE_FALSE,
        },
        AuthorConstants.ARG_VALUE_FALSE);
    arguments[1] = argumentDescriptor;
    
    // Pass the MIME type for the file to open in system application.
    argumentDescriptor = new ArgumentDescriptor(
        ARGUMENT_MEDIA_TYPE,
        ArgumentDescriptor.TYPE_CONSTANT_LIST,
        "The media type of the file to be opened. ",
        new String[] {
            MEDIA_TYPE_VIDEO, 
            MEDIA_TYPE_AUDIO,
            MEDIA_TYPE_MEDIA,
            MEDIA_TYPE_IMAGE,
            MEDIA_TYPE_HTML,
            MEDIA_TYPE_PDF},
        MEDIA_TYPE_HTML);
    arguments[2] = argumentDescriptor;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Open in default system application";
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws AuthorOperationException {
    Object resourcePathXPath = args.getArgumentValue(ARGUMENT_RESOURCE_PATH);

    if (resourcePathXPath != null && ((String) resourcePathXPath).trim().length() > 0) {
      // EXM-33238 Expand editor variables.
      resourcePathXPath = authorAccess.getUtilAccess().expandEditorVariables(
          (String) resourcePathXPath, 
          authorAccess.getEditorAccess().getEditorLocation());
      // Execute the XPath that gives the file to open.
      Object[] results =
          authorAccess.getDocumentController().evaluateXPath((String) resourcePathXPath, null, false, true, true, false, 
        		  //EXM-27096 Use XPath 3.1
        		  XPathVersion.XPATH_3_0);
      
      if (results != null && results.length > 0) {
        // We have a resource to open.
        String toOpenVal = null;
        if (results[0] instanceof String) {
          // String result.
          toOpenVal = (String) results[0];
        } else if (results[0] instanceof Node) {
          // A node.
          toOpenVal = ((Node) results[0]).getNodeValue();
        }
        
        if (logger.isDebugEnabled()) {
          logger.debug("Relative location " + toOpenVal);
        }
        
        if (toOpenVal != null) {
          open(toOpenVal, authorAccess, args);
        }
      } else {
        throw new AuthorOperationException("The resource path XPath must evaluate to a string or node: " + resourcePathXPath);
      }
    }
  }

  /**
   * Open the given resource.
   * 
   * @param toOpenVal The resource to open.
   * @param authorAccess The author access.
   * @param args The map of arguments.
   * 
   * @throws AuthorOperationException
   */
  private static void open(String toOpenVal, AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
    URL toOpen = null;
    // We need a context node for resolving an unparsed entity.
    int caretOffset = authorAccess.getEditorAccess().getCaretOffset();
    AuthorNode contextNode = null;
    if (caretOffset > 0) {
      //Get node at caret...
      try {
        contextNode = authorAccess.getDocumentController().getNodeAtOffset(caretOffset);
      } catch (BadLocationException e) {
        logger.error(e, e);
      }
    }
    
    if (contextNode == null) {
      // Shouldn't happen. Just a fallback.
      contextNode = authorAccess.getDocumentController().getAuthorDocumentNode();
    }
    Object unparsedEntity = args.getArgumentValue(ARGUMENT_UNPARSED_ENTITY);
    if (AuthorConstants.ARG_VALUE_TRUE.equals(unparsedEntity)) {
      //Unparsed entity.
      String systemID = authorAccess.getDocumentController().getUnparsedEntityUri(contextNode, toOpenVal);
      if (systemID != null) {
        try {
          toOpen = new URL(systemID);
        } catch (MalformedURLException e) {
          logger.error(e, e);
        }
      }
    } else {
      // A simple resource.
      toOpen = authorAccess.getXMLUtilAccess().resolvePathThroughCatalogs(
          contextNode != null ? contextNode.getXMLBaseURL() : authorAccess.getEditorAccess().getEditorLocation(), 
          toOpenVal, 
          true, 
          true);
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("Absolute location to open: " + URLUtil.filterPasswords(String.valueOf(toOpen)));
    }
    
    String mediaType = (String)args.getArgumentValue(ARGUMENT_MEDIA_TYPE);
    if (toOpen != null) {
      File localFile = authorAccess.getUtilAccess().locateFile(toOpen);
      if (localFile != null && !localFile.exists()) {
        // A local file that doesn't exists.
        throw new AuthorOperationException("Resource does not exists: " + toOpen);
      } else {
        //Use media argument...
        authorAccess.getWorkspaceAccess().openInExternalApplication(toOpen, true, mediaType);
      }
    } else {
      String toOpenString = toOpenVal;
      if (AuthorConstants.ARG_VALUE_TRUE.equals(unparsedEntity)) {
        //Unparsed entity.
        String systemID = authorAccess.getDocumentController().getUnparsedEntityUri(contextNode, toOpenVal);
        if (systemID != null) {
          toOpenString = systemID;
        }
      }
      
      authorAccess.getWorkspaceAccess().openInExternalApplication(toOpenString, true, mediaType);
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }
}
