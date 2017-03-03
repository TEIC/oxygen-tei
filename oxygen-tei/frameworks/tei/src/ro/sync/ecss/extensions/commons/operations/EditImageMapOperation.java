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
package ro.sync.ecss.extensions.commons.operations;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerFactory;

import net.sf.saxon.TransformerFactoryImpl;
import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.NamespaceContext;
import ro.sync.ecss.extensions.commons.imagemap.EditImageMapCore;
import ro.sync.ecss.imagemap.ImageMapAccess;
import ro.sync.ecss.imagemap.ImageMapNotSuportedException;
import ro.sync.ecss.imagemap.ImageMapUtil;
import ro.sync.ecss.imagemap.SupportedFrameworks;

/**
 * Operation used to edit an ImageMap in some documents.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(false)
public abstract class EditImageMapOperation implements AuthorOperation {
  /**
   * The image map core operations.
   */
  private EditImageMapCore imageMapCore;

  /**
   * Operation's constructor.
   * 
   * @param imageMapCore  The image map core utilities.
   */
  public EditImageMapOperation(EditImageMapCore imageMapCore) {
    this.imageMapCore = imageMapCore;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public final void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    // Get the old transformer property.
    String oldProp = System.getProperty(TransformerFactory.class.getName());
    // Set a property that supports the "http://javax.xml.XMLConstants/feature/secure-processing" feature. 
    System.setProperty(TransformerFactory.class.getName(), TransformerFactoryImpl.class.getName());
    
    processArgumentsMap(args);
    AuthorDocumentController documentController = authorAccess.getDocumentController();
    try {
      // Get the offsets for the fragments constituting the image map.
      AuthorNode[] nodes = imageMapCore.getNodesOfInterest(authorAccess, null, true);
      
      if (nodes != null) {
        // Create the fragments.
        AuthorDocumentFragment[] fragments = new AuthorDocumentFragment[nodes.length];
        // Serialize them.
        String[] asXML = new String[nodes.length];
        for (int i = 0; i < asXML.length; i++) {
          fragments[i] = documentController.createDocumentFragment(nodes[i], true);
          asXML[i] = documentController.serializeFragmentToXML(fragments[i]);
        }
        
        // Get the supported framework.
        SupportedFrameworks framework = imageMapCore.getSupportedFramework(nodes[0].getNamespace());
        // Get the namespace context.
        NamespaceContext nsContext = nodes[0].getNamespaceContext();
        // Build the URI 2 proxies mapping.
        Map<String, String> uri2ProxyMappings = new HashMap<String, String>();
        String[] nss = nsContext.getNamespaces();
        for (int i = 0; i < nss.length; i++) {
          uri2ProxyMappings.put(nss[i], nsContext.getPrefixForNamespace(nss[i]));
        }
        
        String emptyNS4EmptyPrefix = uri2ProxyMappings.get("");
        if (emptyNS4EmptyPrefix != null && emptyNS4EmptyPrefix.trim().length() == 0) {
          uri2ProxyMappings.remove(emptyNS4EmptyPrefix);
        }
        
        // Do the edit.
        String[] result = ImageMapAccess.getInstance().editMap(
            authorAccess,
            framework, 
            authorAccess.getEditorAccess().getEditorLocation(), 
            uri2ProxyMappings,
            ImageMapUtil.getFontOfNodeSize(authorAccess, nodes[0]),
            asXML);
        
        // If some result, put it back into the document.
        if (result != null) {
          documentController.beginCompoundEdit();
          try {
            for (int i = 0; i < nodes.length; i++) {
              int startOffset = nodes[i].getStartOffset();
              documentController.delete(startOffset, nodes[i].getEndOffset());
              AuthorDocumentFragment toInsertFrag = 
                  documentController.createNewDocumentFragmentInContext(
                      result[i],
                      startOffset);
              documentController.insertFragment(startOffset, toInsertFrag);
            }
          } finally {
            documentController.endCompoundEdit();
          }
        } else {
          documentController.cancelCompoundEdit();
        }
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException(e.getMessage(), e);
    } catch (MalformedURLException e) {
      throw new AuthorOperationException(e.getMessage(), e);
    } catch (JAXBException e) {
      StringBuilder message = new StringBuilder();
      String originalMessage = e.getMessage();
      if (originalMessage.startsWith("unexpected element")) {
        // Explain a little more why we don't allow this operation.
        message.append("The image map source contains unsupported elements that would be removed by this operation. Details: ");
      }
      
      // Capitalize the original message.
      if (originalMessage.length() > 1) {
        message.append(Character.toUpperCase(originalMessage.charAt(0))).append(originalMessage, 1, originalMessage.length());
      }
      throw new AuthorOperationException(message.toString(), e);
    } catch (ImageMapNotSuportedException e) {
      documentController.cancelCompoundEdit();
      authorAccess.getWorkspaceAccess().showErrorMessage(e.getMessage());
    } finally {
      // Set the old property back.
      if (oldProp == null) {
        System.clearProperty(TransformerFactory.class.getName());
      } else {
        System.setProperty(TransformerFactory.class.getName(), oldProp);
      }
    }
  }
  
  /**
   * Process the arguments map.
   * 
   * @param args  The map with arguments for this operation.
   */
  protected void processArgumentsMap(ArgumentsMap args) {
    // Do nothing by default.
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    // No arguments.
    return null;
  }
}