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
package ro.sync.ecss.extensions.commons.id;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandler;
import ro.sync.ecss.extensions.api.UniqueAttributesRecognizer;
import ro.sync.ecss.extensions.api.content.ClipboardFragmentInformation;
import ro.sync.ecss.extensions.api.content.ClipboardFragmentProcessor;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.AuthorParentNode;

/**
 * Default unique attributes recognizer
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class DefaultUniqueAttributesRecognizer implements UniqueAttributesRecognizer, ClipboardFragmentProcessor {
  
  /**
   * The ID attribute qname
   */
  protected String idAttrQname = "id";
  
  /**
   * The author access
   */
  protected AuthorAccess authorAccess;
  /**
   * The default options
   */
  private GenerateIDElementsInfo defaultOptions = new GenerateIDElementsInfo(false, GenerateIDElementsInfo.DEFAULT_ID_GENERATION_PATTERN, new String[0]);
  
  /** 
   * Logger for logging. 
   */
  private static Logger logger = Logger.getLogger(DefaultUniqueAttributesRecognizer.class.getName());
  
  /**
   * Information about what attribute to set on what element 
   */
  private class AttributeSetInfo{
    /**
     * Element on which to set the attribute 
     */
    private AuthorElement element; 
    
    /**
     * Attribute QName to set
     */
    private String attrQName;
    
    /**
     * The attribute value to set.
     */
    private String attrValue;
  }
  
  /**
   * Default constructor. 
   */
  public DefaultUniqueAttributesRecognizer() {
    //
  }
  
  /**
   * Constructor. 
   * @param idAttrQname The ID attribute qname
   */
  public DefaultUniqueAttributesRecognizer(String idAttrQname) {
    this.idAttrQname = idAttrQname;
  }

  /**
   * @see ro.sync.ecss.extensions.api.UniqueAttributesRecognizer#copyAttributeOnSplit(java.lang.String, ro.sync.ecss.extensions.api.node.AuthorElement)
   */
  @Override
  public boolean copyAttributeOnSplit(String attrQName, AuthorElement element) {
    return ! idAttrQname.equals(attrQName);
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorExtensionStateListener#activated(ro.sync.ecss.extensions.api.AuthorAccess)
   */
  @Override
  public void activated(final AuthorAccess authorAccess) {
    this.authorAccess = authorAccess;
    //Load default options.
    defaultOptions = GenerateIDElementsInfo.loadDefaultsFromConfiguration(authorAccess, getDefaultOptionsXMLResourceName());
    if(defaultOptions.getAttrQname() != null){
      idAttrQname = defaultOptions.getAttrQname();
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorExtensionStateListener#deactivated(ro.sync.ecss.extensions.api.AuthorAccess)
   */
  @Override
  public void deactivated(AuthorAccess authorAccess) {
    this.authorAccess = null;
  }

  /**
   * @return The default generation options
   */
  protected GenerateIDElementsInfo getDefaultOptions(){
    return defaultOptions;
  }
  
  /**
   * Get the name of the XML resource from which to load the default options. 
   * 
   * @return the name of the XML resource from which to load the default options.
   */
  protected String getDefaultOptionsXMLResourceName() {
    return null;
  }

  /**
   * @return true if auto generation is active and we have elements for which to generate.
   */
  @Override
  public boolean isAutoIDGenerationActive() {
    GenerateIDElementsInfo generateIDElementsInfo = getGenerateIDElementsInfo();
    return generateIDElementsInfo != null 
    && generateIDElementsInfo.isAutoGenerateIDs()
    && generateIDElementsInfo.getElementsWithIDGeneration() != null;
  }
  
  /**
   * @param element The current element.
   * @param elemsWithAutoGeneration The array of elements for which generation is activated
   * @param forceGeneration Force ID generation if there is no selection.
   * 
   * @return The name of the attribute for which to generate the ID or null (default behavior).
   */
  protected String getGenerateIDAttributeQName(AuthorElement element, String[] elemsWithAutoGeneration, boolean forceGeneration) {
    String idAttrName = null;
    if(forceGeneration) {
      idAttrName = idAttrQname; 
    } else {
      for (int i = 0; i < elemsWithAutoGeneration.length; i++) {
        String pattern = elemsWithAutoGeneration[i];
        boolean match = false;
        if(element.getLocalName().equals(pattern)) {
          //Local name match
          match = true;
        }

        if(match) {
          //Fill in the ID attribute
          idAttrName = idAttrQname;
          break;
        }
      }
    }
    return idAttrName;
  }

  /**
   * Generate an unique ID for an element
   * @param idGenerationPattern The pattern for id generation.
   * @param element The element
   * @return The unique ID
   */
  protected String generateUniqueIDFor(String idGenerationPattern, AuthorElement element) {
    URL edLocation = element.getXMLBaseURL();
    return GenerateIDElementsInfo.generateID(idGenerationPattern, element.getLocalName(), edLocation != null ? edLocation.toString() : null);
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.UniqueAttributesRecognizer#assignUniqueIDs(int, int, boolean)
   */
  @Override
  public void assignUniqueIDs(int startOffset, int endOffset, boolean forceGeneration) {
    if(authorAccess != null) {
      GenerateIDElementsInfo currentElemsInfo = getGenerateIDElementsInfo();
      if(forceGeneration || (currentElemsInfo != null 
          && currentElemsInfo.getElementsWithIDGeneration() != null 
          && currentElemsInfo.getElementsWithIDGeneration().length > 0)) {
        try {
          AuthorNode commonParent = 
            authorAccess.getDocumentController().getCommonParentNode(authorAccess.getDocumentController().getAuthorDocumentNode(), startOffset, endOffset);
          authorAccess.getDocumentController().beginCompoundEdit();
          try {
            List<AttributeSetInfo> toGenerate = new ArrayList<AttributeSetInfo>();
            generateUniqueIDs(authorAccess, commonParent, startOffset, endOffset,
                currentElemsInfo.getIdGenerationPattern(),
                currentElemsInfo.getElementsWithIDGeneration(), forceGeneration, toGenerate);
            if(toGenerate.size() == 1){
              //Set it the usual way
              AttributeSetInfo info = toGenerate.get(0);
              authorAccess.getDocumentController().setAttribute(info.attrQName, new AttrValue(info.attrValue), info.element);
            } else {
              //Set them all at once.
              int[] elemOffsets = new int[toGenerate.size()];
              AuthorNode[] nodes = new AuthorNode[toGenerate.size()];
              List<Map<String, AttrValue>> attrs = new ArrayList<Map<String,AttrValue>>(toGenerate.size());
              for (int i = 0; i < elemOffsets.length; i++) {
                AttributeSetInfo attributeSetInfo = toGenerate.get(i);
                nodes[i] = attributeSetInfo.element;
                elemOffsets[i] = attributeSetInfo.element.getStartOffset() + 1;
                Map<String, AttrValue> attrsToGenerate = new HashMap<String, AttrValue>();
                attrsToGenerate.put(attributeSetInfo.attrQName, new AttrValue(attributeSetInfo.attrValue));
                attrs.add(attrsToGenerate);
              }
              //Find common ancestor.
              AuthorNode commonAncestor = authorAccess.getDocumentController().getCommonAncestor(nodes);
              if (commonAncestor == null) {
                commonAncestor = authorAccess.getDocumentController().getAuthorDocumentNode();
              }
              
              // Set multiple attributes in one shot.
              if (!attrs.isEmpty()) {
                authorAccess.getDocumentController().setMultipleDistinctAttributes(commonAncestor.getStartOffset() + 1, elemOffsets, attrs);
              }
            }
          } finally {
            authorAccess.getDocumentController().endCompoundEdit();
          }
        } catch (BadLocationException e) {
          logger.warn(e, e);
        }
      }
    }
  }

  /**
   * Generate IDs for all elements in a specified range.
   * 
   * @param currentNode The current node.
   * @param startSel The start offset limit
   * @param endSel The end offset limit.
   * @param idGenerationPattern The pattern for id generation .
   * @param elementsToGenerateFor The elements for which IDs must be generated.
   */
  private void generateUniqueIDs(
      AuthorAccess authorAccess, AuthorNode currentNode, int startSel, int endSel,
      String idGenerationPattern, String[] elementsToGenerateFor, boolean forceGeneration, List<AttributeSetInfo> attrsToGenerate) {
    if(authorAccess.getDocumentController().isEditable(currentNode)) {
      int startOffset = currentNode.getStartOffset();
      int endOffset = currentNode.getEndOffset();

      boolean nodeContainsSelection = 
        //Node is entirely contained in the selection
        (startSel <= startOffset && endOffset <= endSel)
        //The selection start intersects the node
        || (startSel <= startOffset && startOffset <= endSel)
        //The selection end intersects the end
        || (startSel <= endOffset && endOffset <= endSel);

      boolean nodeIntersectsSelection = (startOffset <= startSel && startSel <= endOffset)
      || (startOffset <= endSel && endSel <= endOffset)
      || (startSel <= startOffset && startOffset <= endSel) 
      || (startSel <= endOffset && endOffset <= endSel);
      //Check intersection with the range.
      if (nodeIntersectsSelection) {
        if(nodeContainsSelection) {
          // The node intersects the range
          if(currentNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
            AuthorElement element = (AuthorElement) currentNode;
            String generateIdAttrQName = getGenerateIDAttributeQName(element, elementsToGenerateFor, forceGeneration);
            if(generateIdAttrQName != null) {
              AttrValue attr = element.getAttribute(generateIdAttrQName);
              if(
                  //Attribute is not present
                  attr == null 
                  //This may happen from content completion when required attributes are added.
                  || "".equals(attr.getValue())
                  //Unspecified attribute, generate.
                  || ! attr.isSpecified()) {
                //If the ID is not yet set...
                AttributeSetInfo info = new AttributeSetInfo();
                info.attrQName = generateIdAttrQName;
                info.attrValue = generateUniqueIDFor(idGenerationPattern, element);
                info.element = element;
                attrsToGenerate.add(info);
              }
            }
          }
        }

        //Recurse into children
        if(currentNode instanceof AuthorParentNode) {
          AuthorParentNode sentinelNode = (AuthorParentNode) currentNode;
          List<AuthorNode> contentNodes = sentinelNode.getContentNodes();
          for (int i = 0; i < contentNodes.size(); i++) {
            generateUniqueIDs(
                authorAccess, contentNodes.get(i), startSel, endSel, idGenerationPattern, elementsToGenerateFor, forceGeneration, attrsToGenerate);
          }
        }
      }
    }
  }
  
  /**
   * @return Returns the autoGenerateElementsInfo.
   */
  public GenerateIDElementsInfo getGenerateIDElementsInfo() {
    if(authorAccess != null) {
      return new GenerateIDElementsInfo(authorAccess, getDefaultOptions());
    } else {
      return null;
    }
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.content.ClipboardFragmentProcessor#process(ro.sync.ecss.extensions.api.content.ClipboardFragmentInformation)
   */
  @Override
  public void process(ClipboardFragmentInformation fragmentInformation) {
    if(authorAccess == null) {
      //EXM-20948 partial fix for NPE
      logger.warn("NULL Author Access, should not happen", new Exception());
      return;
    }
    GenerateIDElementsInfo currentElemsInfo = getGenerateIDElementsInfo();
    if(currentElemsInfo != null && ! currentElemsInfo.isFilterIDsOnCopy()) {
      //No filtering will be done.
      return;
    }
    //Remove unique IDs when pasting in other documents or when copy/paste in the same document
    boolean removeUniqueIDs = false;
    if(fragmentInformation.getFragmentOriginalLocation() != null
        && ! fragmentInformation.getFragmentOriginalLocation().equals(authorAccess.getEditorAccess().getEditorLocation().toString())) {
      if(preserveIDsWhenPastingBetweenResources()){
        //EXM-21408 Paste from another document. Preserve the IDs
        removeUniqueIDs = false;
      } else {
        removeUniqueIDs = true;
      }
    } else {
      int purposeID = fragmentInformation.getPurposeID();
      if(purposeID == AuthorSchemaAwareEditingHandler.CREATE_FRAGMENT_PURPOSE_COPY 
          || purposeID == AuthorSchemaAwareEditingHandler.CREATE_FRAGMENT_PURPOSE_DND_COPY) {
        removeUniqueIDs = true;
      }
    }
    if(removeUniqueIDs) {
      //Remove unique IDs
      filterIDAttributes(fragmentInformation.getFragment().getContentNodes());
    } 
  }
  
  /**
   * Check if we should preserve IDs when pasting between resources.
   * 
   * @return <code>true</code> if we should preserve IDs when pasting between resources.
   * By default the base method returns <code>true</code>.
   */
  protected boolean preserveIDsWhenPastingBetweenResources() {
    return true;
  }

  /**
   * Filter all ID attributes from the fragment.
   * 
   * @param contentNodes The nodes.
   */
  private void filterIDAttributes(List<AuthorNode> contentNodes) {
    for (int i = 0; i < contentNodes.size(); i++) {
      AuthorNode node = contentNodes.get(i);
      if(node.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
        //Remove the ID attribute.
        ((AuthorElement)node).removeAttribute(idAttrQname);
      }
      if(node instanceof AuthorParentNode) {
        filterIDAttributes(((AuthorParentNode)node).getContentNodes());
      }
    }
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Default unique attributes generation";
  }
}
