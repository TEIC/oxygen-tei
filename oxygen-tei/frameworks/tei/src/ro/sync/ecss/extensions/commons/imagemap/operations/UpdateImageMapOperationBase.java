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
package ro.sync.ecss.extensions.commons.imagemap.operations;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Objects;
import java.util.Optional;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
/**
 * Updates an image map with shape information from an SVG.
 * 
 * @since 25.0
 * <br>
 * <br>
 * *********************************
 * <br>
 * EXPERIMENTAL - Subject to change
 * <br>
 * ********************************
 * <br>
 * <p>Please note that this API is not marked as final and it can change in one of the next versions of the application. If you have suggestions,
 * comments about it, please let us know.</p>
 */
@API(type=APIType.EXTENDABLE, src=SourceType.PUBLIC)
@WebappCompatible
public abstract class UpdateImageMapOperationBase implements AuthorOperation {
  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(UpdateImageMapOperationBase.class.getName());
  
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
      throws AuthorOperationException {
    String svgText = (String) args.getArgumentValue(ARGUMENT_SHAPES);
    
    AuthorDocumentController controller = authorAccess.getDocumentController();
    AuthorEditorAccess editorAccess = authorAccess.getEditorAccess();
    int caretOffset = editorAccess.getCaretOffset();
    
    AuthorElement currentImageMap = getNodeToReplace(controller, editorAccess, caretOffset);
    if (currentImageMap != null) {
      List<? extends NewShapeDescriptor> newShapesList = getNewShapesList(svgText); controller.beginCompoundEdit();
      try {
        mergeImageMaps(controller, currentImageMap, newShapesList);
      
      } catch (Exception e) { throw new AuthorOperationException("Cannot obtain the image map from internal model!", e);
      } finally {
        controller.endCompoundEdit(); }
    } else {
        throw new AuthorOperationException("Could not identify Image Map!");
    }
  }
  
  /**
   * Return the image map that contains the current element.
   * 
   * @param currentElement The current element.
   * @return The image map element.
   */
  protected abstract AuthorElement getImageMapElement(AuthorElement currentElement);

  
  /**
   * Return the list of new shapes descriptors.
   * 
   * @param svgText The SVG text.
   * @return The list.
   * 
   * @throws AuthorOperationException If the conversion fails.
   */
  protected abstract List<? extends NewShapeDescriptor> getNewShapesList(String svgText) 
      throws AuthorOperationException;
  
  /**
   * Return the list of existing shapes starting from the existing Image Map.
   * @param existingImageMap The existing Image Map.
   * @return The array of elements that correspond to shapes.
   */
  protected abstract AuthorElement[] getExistingShapesList(AuthorElement existingImageMap);
  
  /**
   * Merge image maps.
   * 
   * @param controller       The document controller.
   * @param currentImageMap  The original map element.
   * @param newShapeElements The list of new shapes.
   * @throws AuthorOperationException
   */
  private void mergeImageMaps(AuthorDocumentController controller,
      AuthorElement currentImageMap, List<? extends NewShapeDescriptor> newShapeElements) throws AuthorOperationException {
          // Get the shapes from the existing image map.

    Map<Integer, AuthorElement> shapeElements = getShapesMap(getExistingShapesList(currentImageMap));

    ///////////
    // STEP 1. Create the list of new shapes to be inserted in the old image map element.
    ///////////

    // Create the list of new shapes to be inserted.
    List<String> newShapes = newShapeElements.stream()
      .map(newShapeElement -> getXmlForNewShape(controller, shapeElements, newShapeElement))
      .filter(Objects::nonNull)
      .collect(toList());
          
          ///////////
    // Step 2. Remove the existing shapes.
    ///////////

    // Save the insertion point for the new shapes.
    int insertionPoint = currentImageMap.getEndOffset();
          for (AuthorElement shapeElement : shapeElements.values()) {
            // Keep the lowest index.
      insertionPoint = Math.min(insertionPoint, shapeElement.getStartOffset()); // Delete the old node.
      controller.deleteNode(shapeElement);
          } ///////////
    // Step 3. Add the new shapes.
    ///////////
    // Reverse the list.
    Collections.reverse(newShapes);
    // Insert the new shapes.
    for (String xmlShape : newShapes) {
            controller.insertXMLFragment(xmlShape, insertionPoint);
          }
        } /**
   * Returns the XML serialization for the new shape. 
   * 
   * It tries to inherit some XML attributes (for example the links) from the current document.
   *
   * @param controller The document controller.
   * @param shapeElements The shape elements in the current document.
   * @param newShapeElement The new shape elements.
   * @return The XML serialization for the new shape or null.
   */ private String getXmlForNewShape(AuthorDocumentController controller,
      Map<Integer, AuthorElement> shapeElements, NewShapeDescriptor newShapeElement) {
          String newShapeXml; Optional<Integer> originalLayer = newShapeElement.getOriginalLayer();
    if (originalLayer.isPresent()) {
      try {
        AuthorElement originalShape = shapeElements.get(originalLayer.get());
        newShapeElement.mergeIntoOriginalShape(controller, originalShape);
        
        newShapeXml = controller.serializeFragmentToXML(controller.createDocumentFragment(originalShape, true));
      } catch (BadLocationException e) {
        logger.warn("Unable to merge shapes.", e); newShapeXml = newShapeElement.serializeToXml().orElse(null);
      }
    } else {
      newShapeXml = newShapeElement.serializeToXml().orElse(null);
    }
  return newShapeXml;
  }
  
  /**
   * Get the shapes map.
   * 
   * @param imageMapElements The image map elements.
   * @return  The list of shape elements.
   */
  private static Map<Integer, AuthorElement> getShapesMap(AuthorElement[] imageMapElements) {
    Map<Integer,
      AuthorElement> map = new HashMap<>(); 
          for (int i = 0; i < imageMapElements.length; i++) {
      map.put(i, imageMapElements[i]);

  }
    return map;
  }
  
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
    AuthorElement currentElement = (AuthorElement) editorAccess.getFullySelectedNode();
    if (currentElement == null) {
      try {
        currentElement = (AuthorElement) controller.getNodeAtOffset(caretOffset);
      } catch (BadLocationException e) {
        throw new AuthorOperationException(e.getMessage(), e);
      }
    }
    
    return getImageMapElement(currentElement);
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }
}