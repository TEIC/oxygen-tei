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

import java.util.ArrayList;
import java.util.List;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorReviewController;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlight;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlight.PersistentHighlightType;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * A path that describes the location of an element in the DOM tree.
 * 
 * @author cristi_talau
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
final class ElementLocationPath {

  /**
   * An element location path component.
   * 
   * @author cristi_talau
   */
  public static interface ElementLocationPathComponent {
    /**
     * @return An XPath representation of the path component.
     */
    public abstract String toXPath();
  }
  
  /**
   * Path component that identifies the parent node.
   * 
   * @author cristi_talau
   */
  public static class Parent implements ElementLocationPathComponent {

    /**
     * @see ro.sync.ecss.extensions.commons.operations.ElementLocationPath.ElementLocationPathComponent#toXPath()
     */
    @Override
    public String toXPath() {
      return "/..";
    }
    
  }
    
  /**
   * Path component that identifies the root of the DOM node.
   * 
   * @author cristi_talau
   */
  public static class Root implements ElementLocationPathComponent {
    /**
     * @see ro.sync.ecss.extensions.commons.operations.ElementLocationPath.ElementLocationPathComponent#toXPath()
     */
    @Override
    public String toXPath() {
      return "/*";
    }
  }

  /**
   * Path component that identifies one of the children of the current element.
   * 
   * @author cristi_talau
   */
  public static class Child implements ElementLocationPathComponent {
    /**
     * The index of the child.
     */
    private final int index;
    
    /**
     * Constructor.
     * 
     * @param index The index of the child. 
     */
    public Child(int index) {
      this.index = index;
    }
    
    /**
     * @return Returns the index.
     */
    public int getIndex() {
      return index;
    }

    /**
     * @see ro.sync.ecss.extensions.commons.operations.ElementLocationPath.ElementLocationPathComponent#toXPath()
     */
    @Override
    public String toXPath() {
      return "/*[" + index + "]";
    }
  }
  
  /**
   * The path the represents the location.
   */
  private final List<ElementLocationPathComponent> path = new ArrayList<>();
  
  
  /**
   * Constructor.
   */
  private ElementLocationPath() {
    // Use getCurrentElementLocation() to create an object.
  }
  
  /**
   * @return Returns the path.
   */
  public List<ElementLocationPathComponent> getPath() {
    return path;
  }
  
  /**
   * @return The XPath representation of the element location.
   */
  public String toXPath() {
    StringBuilder sb = new StringBuilder();
    for (ElementLocationPathComponent elementLocationComponent : path) {
      sb.append(elementLocationComponent.toXPath());
    }
    return sb.toString();
  }
  
  /**
   * Adds a path component to the front of the path.
   * 
   * @param comp The component to add.
   * 
   * @return this, for chaining.
   */
  private ElementLocationPath addFront(ElementLocationPathComponent comp) {
    path.add(0, comp);
    return this;
  }
  
  /**
   * Returns the location of the current element relative to the source element.
   * 
   * @param authorReviewController The author review controller, used to identify deleted nodes.
   * @param currentElement The current element at caret.
   * @param sourceElement The element to transform.
   * 
   * @return The current element location relative to the source element.
   */
  public static ElementLocationPath getCurrentElementLocation(AuthorReviewController authorReviewController, AuthorElement currentElement,
      AuthorElement sourceElement) {
    ElementLocationPath currentElementLocation = new ElementLocationPath();
    AuthorNode tmp = currentElement;
    if (tmp.isDescendentOf(sourceElement)) {
      while (tmp != sourceElement) {
        AuthorElement parent = ((AuthorElement)tmp.getParent());
        List<AuthorNode> contentNodes = parent.getContentNodes();
        int index = 1;
        for (int i = 0; i < contentNodes.size(); i++) {
          AuthorNode child = contentNodes.get(i);
          if(child == tmp){
            //Xpath indices are 1-based
            break;
          } else {
            boolean ignoreThisNode = false;
            if (child.getType() != AuthorNode.NODE_TYPE_ELEMENT) {
              // EXM-48837: Process only elements when creating the current element location. 
              ignoreThisNode = true;
            } else {
              //EXM-33943 Ignore fully deleted sibling nodes.
              AuthorPersistentHighlight[] intersectingHighlights = authorReviewController.getChangeHighlights(child.getStartOffset(), child.getEndOffset());
              if(intersectingHighlights != null){
                for (int j = 0; j < intersectingHighlights.length; j++) {
                  if(intersectingHighlights[j].getType() == PersistentHighlightType.CHANGE_DELETE){
                    //Find delete marker which engulfs element
                    if(intersectingHighlights[j].getStartOffset() <= child.getStartOffset() 
                        && child.getEndOffset() <= intersectingHighlights[j].getEndOffset()){
                      ignoreThisNode = true;
                      break;
                    }
                  }
                }
              }
            }
            if(! ignoreThisNode){
              //Increment counter
              index ++;
            } else {
              //Ignore it
            }
          }
        }
        currentElementLocation.addFront(new ElementLocationPath.Child(index));
        tmp = parent;
      }
      currentElementLocation.addFront(new ElementLocationPath.Child(1));
    } else if (tmp.equals(sourceElement)) {
      currentElementLocation = new ElementLocationPath().addFront(new ElementLocationPath.Root());
    } else {
      currentElementLocation = new ElementLocationPath().addFront(new ElementLocationPath.Parent());
    }
    return currentElementLocation;
  }
}
