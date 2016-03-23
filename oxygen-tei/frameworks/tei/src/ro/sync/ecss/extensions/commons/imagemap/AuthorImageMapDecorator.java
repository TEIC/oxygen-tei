/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2016 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.imagemap;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerFactory;

import net.sf.saxon.TransformerFactoryImpl;

import org.apache.log4j.Logger;




import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorImageDecorator;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.NamespaceContext;
import ro.sync.ecss.imagemap.ImageMapAccess;
import ro.sync.ecss.imagemap.ImageMapUtil;
import ro.sync.ecss.imagemap.SupportedFrameworks;
import ro.sync.exml.view.graphics.Graphics;
import ro.sync.exml.view.graphics.Rectangle;

/**
 * Image map decorator base for Author. It paints the areas of the image map over the image.
 * 
 * @author mircea
 * @author alex_jitianu
 */

public abstract class AuthorImageMapDecorator extends AuthorImageDecorator {
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(AuthorImageMapDecorator.class.getName());
  /**
   * The image map core functionality.
   */
  private EditImageMapCore imageMapCore;

  /**
   * Base functionality for the Author Image Map Decorator.
   * 
   * @param imageMapCore  The image map core.
   */
  public AuthorImageMapDecorator(EditImageMapCore imageMapCore) {
    this.imageMapCore = imageMapCore;
  }
  
  /**
   * Check if the node to be painted is part of an image map.
   * 
   * @param node      The current node.
   * @param framework The current framework.
   * 
   * @return  <code>true</code> if the node is part of an image map and we shall paint something over the image.
   */
  protected abstract boolean isNodeOfInterest(AuthorNode node, SupportedFrameworks framework);
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorImageDecorator#paint(ro.sync.exml.view.graphics.Graphics, int, int, int, int, Rectangle, AuthorNode, ro.sync.ecss.extensions.api.AuthorAccess, boolean)
   */
  @Override
  public void paint(Graphics g, int x, int y, int imageWidth, int imageHeight,
      Rectangle originalSize, AuthorNode element, AuthorAccess authorAccess, boolean wasAnnotated) {

    // The scale factor. Usually 1.
    double scaleFactor = 1.0;
    
    if (originalSize != null && originalSize.width != imageWidth) {
      scaleFactor = imageWidth / (double) originalSize.width;
    }
    
    // Get the old transformer property.
    String oldProp = System.getProperty(TransformerFactory.class.getName());
    // Set a property that supports the "http://javax.xml.XMLConstants/feature/secure-processing" feature. 
    System.setProperty(TransformerFactory.class.getName(), TransformerFactoryImpl.class.getName());

    AuthorDocumentController documentController = authorAccess.getDocumentController();
    try {
      // Get the offsets for the fragments constituting the image map.
      AuthorNode[] nodes = imageMapCore.getNodesOfInterest(authorAccess, element, false);

      if (nodes != null) {
        // Get the supported framework.
        SupportedFrameworks framework = imageMapCore.getSupportedFramework(nodes[0].getNamespace());
        
        if (!isNodeOfInterest(element, framework)) {
          return;
        }
        
        // Create the fragments.
        AuthorDocumentFragment[] fragments = new AuthorDocumentFragment[nodes.length];
        // Serialize them.
        String[] asXML = new String[nodes.length];
        for (int i = 0; i < asXML.length; i++) {
          fragments[i] = documentController.createDocumentFragment(nodes[i], true);
          asXML[i] = documentController.serializeFragmentToXML(fragments[i]);
        }

        // Get the namespace context.
        NamespaceContext nsContext = nodes[0].getNamespaceContext();
        // Build the URI 2 proxies mapping.
        Map<String, String> uri2ProxyMappings = new HashMap<String, String>();
        String[] nss = nsContext.getNamespaces();
        for (int i = 0; i < nss.length; i++) {
          uri2ProxyMappings.put(nss[i], nsContext.getPrefixForNamespace(nss[i]));
        }

        int fontOfNodeSize = ImageMapUtil.getFontOfNodeSize(authorAccess, nodes[0]);
        
        ImageMapAccess.getInstance().paintImageMapAreas(
            g,
            x,
            y,
            imageWidth,
            imageHeight,
            scaleFactor,
            authorAccess,
            asXML,
            framework,
            uri2ProxyMappings,
            fontOfNodeSize,
            wasAnnotated);
      }
    } catch (Throwable t) {
      // Neglect...
      if (logger.isDebugEnabled()) {
        logger.debug(t, t);
      }
    } finally {
      // Set the old property back.
      if (oldProp == null) {
        System.clearProperty(TransformerFactory.class.getName());
      } else {
        System.setProperty(TransformerFactory.class.getName(), oldProp);
      }
    }
  }
}