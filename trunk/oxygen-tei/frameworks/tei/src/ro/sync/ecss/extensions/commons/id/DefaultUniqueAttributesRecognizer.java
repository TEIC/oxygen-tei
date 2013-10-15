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

import java.util.List;

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
public abstract class DefaultUniqueAttributesRecognizer implements UniqueAttributesRecognizer, ClipboardFragmentProcessor {
  
  /**
   * The ID attribute qname
   */
  protected final String idAttrQname;
  
  /**
   * The author access
   */
  protected AuthorAccess authorAccess;

  /** 
   * Logger for logging. 
   */
  private static Logger logger = Logger.getLogger(DefaultUniqueAttributesRecognizer.class.getName());
  
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
  public boolean copyAttributeOnSplit(String attrQName, AuthorElement element) {
    return ! idAttrQname.equals(attrQName);
  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorExtensionStateListener#activated(ro.sync.ecss.extensions.api.AuthorAccess)
   */
  public void activated(final AuthorAccess authorAccess) {
//    logger.info("Author extension activated+++++++++++++++");
//    logger.info("Editor location " + authorAccess.getDocumentController().getAuthorDocumentNode().getSystemID());
    this.authorAccess = authorAccess;

    
    
////----------EXM-17413 Uncomment below to have the editor's title set as the editor's tab text.   
//    this.authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//    authorAccess.getDocumentController().addAuthorListener(new AuthorListener() {
//      public void documentChanged(AuthorDocument oldDocument, AuthorDocument newDocument) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void doctypeChanged() {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void contentInserted(DocumentContentInsertedEvent e) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void contentDeleted(DocumentContentDeletedEvent e) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void beforeDoctypeChange() {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void beforeContentInsert(DocumentContentInsertedEvent e) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void beforeContentDelete(DocumentContentDeletedEvent e) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void beforeAuthorNodeStructureChange(AuthorNode authorNode) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void beforeAuthorNodeNameChange(AuthorNode authorNode) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void beforeAttributeChange(AttributeChangedEvent e) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void authorNodeStructureChanged(AuthorNode node) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void authorNodeNameChanged(AuthorNode node) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//      public void attributeChanged(AttributeChangedEvent e) {
//        authorAccess.getEditorAccess().setEditorTabText(getTitle(authorAccess));
//      }
//    });
///---------------------------------
  }
  
//EXM-17413
//  private String getTitle(AuthorAccess authorAccess) {
//    try {
//      Object[] eval = authorAccess.getDocumentController().evaluateXPath("//title[1]/text()", false, false, false);
//      if(eval != null & eval.length > 0) {
//        return ((Text)eval[0]).getNodeValue();
//      }
//    } catch (AuthorOperationException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }

  /**
   * @see ro.sync.ecss.extensions.api.AuthorExtensionStateListener#deactivated(ro.sync.ecss.extensions.api.AuthorAccess)
   */
  public void deactivated(AuthorAccess authorAccess) {
//    logger.info("Author extension deactivated-------------------");
//    logger.info("Editor location " + authorAccess.getDocumentController().getAuthorDocumentNode().getSystemID());
    this.authorAccess = null;
  }

  /**
   * @return The default generation options
   */
  protected abstract GenerateIDElementsInfo getDefaultOptions();
  
  /**
   * @return true if auto generation is active and we have elements for which to generate.
   */
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
    return GenerateIDElementsInfo.generateID(idGenerationPattern, element.getLocalName());
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.UniqueAttributesRecognizer#assignUniqueIDs(int, int, boolean)
   */
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
            generateUniqueIDs(authorAccess, commonParent, startOffset, endOffset,
                currentElemsInfo.getIdGenerationPattern(),
                currentElemsInfo.getElementsWithIDGeneration(), forceGeneration);
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
      String idGenerationPattern, String[] elementsToGenerateFor, boolean forceGeneration) {
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
                authorAccess.getDocumentController().setAttribute(
                    generateIdAttrQName, new AttrValue(generateUniqueIDFor(idGenerationPattern, element)), element);
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
                authorAccess, contentNodes.get(i), startSel, endSel, idGenerationPattern, elementsToGenerateFor, forceGeneration);
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
      //EXM-21408 Paste from another document. Preserve the IDs
      removeUniqueIDs = false;
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
}
