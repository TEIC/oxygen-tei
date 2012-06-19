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
package ro.sync.ecss.extensions.tei;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.exml.workspace.api.node.customizer.BasicRenderingInformation;
import ro.sync.exml.workspace.api.node.customizer.NodeRendererCustomizerContext;
import ro.sync.exml.workspace.api.node.customizer.XMLNodeRendererCustomizer;

/**
 * Class used to customize the way an TEI node is rendered in the UI. 
 * A node represents an entry from Author outline, Author bread crumb,
 * Text page outline, content completion proposals window or Elements view.
 * 
 * @author alin_balasa
 * @author alina_iordache
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class TEINodeRendererCustomizer extends XMLNodeRendererCustomizer {
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(TEINodeRendererCustomizer.class.getName());
  /**
   * Mapping between name and icon path
   */
  private static final Map<String, String> nameToIconPath = new HashMap<String, String>();

  // Initialize mapping
  static {

    // TEI root
    nameToIconPath.put("TEI", getImageURL("/images/node-customizer/ElementTEI16.png"));
    
    String headImg = getImageURL("/images/node-customizer/ElementHead16.png");

    // TEI header
    nameToIconPath.put("teiHeader", headImg);

    // head
    nameToIconPath.put("head", headImg);

    // title
    nameToIconPath.put("title", getImageURL("/images/node-customizer/ElementTitle16.png"));

    // paragraph
    nameToIconPath.put("p", getImageURL("/images/node-customizer/ElementPara16.png"));

    // list item
    nameToIconPath.put("item", getImageURL("/images/node-customizer/ElementLi16.png"));

    // div
    nameToIconPath.put("div", getImageURL("/images/node-customizer/ElementDiv16.png"));

    // body
    nameToIconPath.put("body", getImageURL("/images/node-customizer/ElementBody16.png"));

    // table
    nameToIconPath.put("table", getImageURL("/images/node-customizer/ElementTable16.png"));

    // table cell
    nameToIconPath.put("cell", getImageURL("/images/node-customizer/ElementTd16.png"));

    // italic
    nameToIconPath.put("emph", getImageURL("/images/node-customizer/ElementItalic16.png"));

    // image
    nameToIconPath.put("figure", getImageURL("/images/node-customizer/ElementImage16.png"));

    String linkIconPath = getImageURL("/images/node-customizer/ElementLink16.png");

    // link
    nameToIconPath.put("link", linkIconPath);

    // ref
    nameToIconPath.put("ref", linkIconPath);

    // ptr
    nameToIconPath.put("ptr", linkIconPath);
    
    // Some images that depend both on element name and some attribute value
    // The key is like: elementName @ attributeValue
    
    // ordered lists
    nameToIconPath.put("list@ordered", getImageURL("/images/node-customizer/ElementOl16.png"));

    // unordered lists
    nameToIconPath.put("list@unordered", getImageURL("/images/node-customizer/ElementUl16.png"));

    // bold
    nameToIconPath.put("hi@bold", getImageURL("/images/node-customizer/ElementBold16.png"));

    // italic
    nameToIconPath.put("hi@italic", getImageURL("/images/node-customizer/ElementItalic16.png"));

    // underline
    nameToIconPath.put("hi@underline", getImageURL("/images/node-customizer/ElementUnderline16.png"));

    // table header
    nameToIconPath.put("row@label", getImageURL("/images/node-customizer/ElementTHead16.png"));

    // table row
    nameToIconPath.put("row", getImageURL("/images/node-customizer/ElementTr16.png"));
  }

  /**
   * Searches the class-path for the image with the given path 
   * and if found it returns the string representation of its URL, otherwise it returns <code>null</code>.  
   * 
   * @param path The image path to search for.
   * 
   * @return The string representation of the image URL or <code>null</code> if the image is not found.
   */
  private static String getImageURL(String path) {
    URL imageURL = TEINodeRendererCustomizer.class.getResource(path);
    if (imageURL != null) {
      return imageURL.toExternalForm();
    } else {
      logger.error(TEINodeRendererCustomizer.class.getName() + " - Image not found: " + path);
      return null;
    }
  }

  @Override
  public BasicRenderingInformation getRenderingInformation(NodeRendererCustomizerContext context) {
    BasicRenderingInformation renderingInfo = new BasicRenderingInformation();
    String nodeName = context.getNodeName();
    if (nodeName != null) {
      String imageKey = nodeName;
      if ("list".equals(nodeName)) { //list
        String attrValue = context.getAttributeValue("type");
        if ("ordered".equals(attrValue)) {
          // ordered list
          imageKey += "@ordered";
        } else {
          // unordered list
          imageKey += "@unordered";
        }
      } else if ("hi".equals(nodeName)) { // highlight
        String attrValue = context.getAttributeValue("rend");
        if ("bold".equals(attrValue)) { 
          // bold
          imageKey += "@bold";
        } else if ("italic".equals(attrValue) || "it".equals(attrValue)) {
          // italic
          imageKey += "@italic";
        } else if ("ul".equals(attrValue) || "underline".equals(attrValue)) {
          // underline
          imageKey += "@underline";
        }
      } else if ("row".equals(nodeName)) { // table row
        String attrValue = context.getAttributeValue("role");
        if ("label".equals(attrValue)) {
          // header
          imageKey += "@label";
        }
      }
      // Get icon path from map
      String iconPath = nameToIconPath.get(imageKey);
      renderingInfo.setIconPath(iconPath);
    }
    return renderingInfo;
  }

  @Override
  public String getDescription() {
    return "TEI Node Renderer Customizer";
  }
}