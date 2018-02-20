/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2012 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.imagemap.operations;

import java.io.StringReader;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

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
import ro.sync.ecss.extensions.api.access.AuthorEditorAccess;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.imagemap.Areas2SVGUtil;
import ro.sync.ecss.imagemap.SupportedFrameworks;

/**
 * Updates an image map with shape information from an SVG.
 * 
 * @author cristi_talau
 * @author mircea
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public abstract class UpdateImageMapOperationBase implements AuthorOperation {
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Updates an image map with shape information from an SVG";
  }

  /**
   * An SVG with the shapes to be used to update the Image Map at caret.
   */
  public static final String ARGUMENT_SHAPES = "shapes";

  /**
   * The arguments descriptor.
   */
  protected static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
    new ArgumentDescriptor(
      ARGUMENT_SHAPES, 
      ArgumentDescriptor.TYPE_STRING,
      "An SVG with the shapes to be used to update the Image Map at caret.\n" + 
      "In order to match shapes from the SVG with shapes in the document, this operation needs " + 
      "the \"data-original-layer\" attribute of the SVG element to match the layer of the shape in the " + 
      "document.")
    };

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
      throws IllegalArgumentException, AuthorOperationException {
    String svgText = (String) args.getArgumentValue(ARGUMENT_SHAPES);
    
    AuthorDocumentController controller = authorAccess.getDocumentController();
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    int caretOffset = editorAccess.getCaretOffset();
    
    AuthorElement currentImageMap = getNodeToReplace(controller, editorAccess, caretOffset);
    if (currentImageMap != null) {
      String imageMapXml = Areas2SVGUtil.fromSVG(svgText, getSupportedFramework());
      
      if (imageMapXml != null && !imageMapXml.trim().isEmpty()) {
        try {
          Document newMapDOM = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
              new InputSource(new StringReader(imageMapXml)));
          
          controller.beginCompoundEdit();
          try {
            mergeImageMaps(controller, currentImageMap, newMapDOM);
          } finally {
            controller.endCompoundEdit();
          }
        } catch (Exception e) {
          throw new AuthorOperationException("Cannot obtain the image map from internal model!", e);
        }
      } else {
        throw new AuthorOperationException("Cannot obtain the image map from internal model!");
      }
    } else {
      throw new AuthorOperationException("Could not identify Image Map!");
    }
  }
  
  /**
   * Merge image maps.
   * 
   * @param controller      The document controller.
   * @param currentImageMap The original map element.
   * @param newMapDOM       The new map fragment.
   * @throws AuthorOperationException
   */
  protected abstract void mergeImageMaps(AuthorDocumentController controller,
      AuthorElement currentImageMap, Document newMapDOM) 
          throws AuthorOperationException;

  /**
   * Get the supported framework.
   * 
   * @return  The supported framework.
   */
  protected abstract SupportedFrameworks getSupportedFramework();

  /**
   * Returns the node to be replaced.
   * 
   * @param controller    The document controller.
   * @param editorAccess  The editor access.
   * @param caretOffset   The caret offset.
   * 
   * @return The node to replace with the new image map
   * 
   * @throws AuthorOperationException
   */
  private AuthorElement getNodeToReplace(AuthorDocumentController controller,
      AuthorEditorAccess editorAccess, int caretOffset) throws AuthorOperationException {
    AuthorElement imageElement = (AuthorElement) editorAccess.getFullySelectedNode();
    if (imageElement == null) {
      try {
        imageElement = (AuthorElement) controller.getNodeAtOffset(caretOffset);
      } catch (BadLocationException e) {
        throw new AuthorOperationException(e.getMessage(), e);
      }
    }
    
    return (AuthorElement) (imageElement == null ? null : imageElement.getParentElement());
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }
}