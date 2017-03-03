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
package ro.sync.ecss.extensions.tei;


import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandler;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.ExtensionTags;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil.ConversionElementHelper;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil.SelectedFragmentInfo;
import ro.sync.ecss.extensions.commons.operations.InsertListOperation;

/**
 * Operation used to convert a selection to an ordered/itemized list for TEI.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible(true)
public class TEIInsertListOperation extends InsertListOperation {
  /**
   * Ordered list parameter.
   */
  static final String ORDERED_LIST = "orderedlist";
  /**
   * Itemized list parameter.
   */
  static final String ITEMIZED_LIST = "itemizedlist";
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(TEIInsertListOperation.class.getName());
  
  /**
   * The arguments array.
   */
  private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
    SCHEMA_AWARE_ARGUMENT_DESCRIPTOR,
    // List type argument
    new ArgumentDescriptor(LIST_TYPE_ARGUMENT, 
        ArgumentDescriptor.TYPE_CONSTANT_LIST, 
        "Controls the type of list to be inserted. Accepted values are: "
        + ORDERED_LIST + ", " + ITEMIZED_LIST + ". Default value is " + ORDERED_LIST + ".", 
        new String[] {
          ORDERED_LIST, 
          ITEMIZED_LIST
        }, 
        ORDERED_LIST)
  };

  @Override
  protected StringBuilder getListXMLFragment(String listType, int numberOfListItems, AuthorAccess authorAccess) {
    // Check if ordered or unordered list.
    boolean orderedList = listType.equals(ORDERED_LIST);
    // Create the list XML fragment.
    StringBuilder listXMLFragment = new StringBuilder();

    listXMLFragment.append("<list");
    String namespace = getNamespace();
    if (namespace != null) {
      listXMLFragment.append(" xmlns=\"").append(namespace).append("\"");
    }
    // List element.
    if (orderedList) {
      // Ordered list
      listXMLFragment.append(" type=\"ordered\"");
    } else {
      // Itemized list
      listXMLFragment.append(" type=\"bulleted\"");
    }
    listXMLFragment.append(">");
    
    // Append list items
    for (int i = 0; i < numberOfListItems; i++) {
      listXMLFragment.append("<item/>");
    }

    listXMLFragment.append("</list>");
    
    return listXMLFragment;
  }
  
  
  /**
   * @see ro.sync.ecss.extensions.commons.operations.InsertListOperation#getXMLFragment(ro.sync.ecss.extensions.api.AuthorAccess, java.lang.String, java.lang.String)
   */
  @Override
  protected String getXMLFragment(AuthorAccess authorAccess, String listType, String parentListType) {
    // Check if ordered or unordered list.
    boolean orderedList = listType.equals(ORDERED_LIST);
    // Create the list XML fragment.
    StringBuilder listXMLFragment = new StringBuilder();
    
    String namespace = getNamespace();
    if (ORDERED_LIST.equals(parentListType) || ITEMIZED_LIST.equals(parentListType)) {
      listXMLFragment.append("<item");
      if (namespace != null) {
        listXMLFragment.append(" xmlns=\"").append(namespace).append("\"");
      }
      listXMLFragment.append(">");
    }

    listXMLFragment.append("<list");
    if (namespace != null) {
      listXMLFragment.append(" xmlns=\"").append(namespace).append("\"");
    }
    // List element.
    if (orderedList) {
      // Ordered list
      listXMLFragment.append(" type=\"ordered\"");
    } else {
      // Itemized list
      listXMLFragment.append(" type=\"bulleted\"");
    }
    listXMLFragment.append("><item/></list>");
    
    if (ORDERED_LIST.equals(parentListType) || ITEMIZED_LIST.equals(parentListType)) {
      listXMLFragment.append("</item>");
    }
    
    return listXMLFragment.toString();
  
  }

  /**
   * @see ro.sync.ecss.extensions.commons.operations.InsertListOperation#getNamespace()
   */
  @Override
  protected String getNamespace() {
    return "http://www.tei-c.org/ns/1.0";
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return ARGUMENTS;
  }

 /**
  * @see ro.sync.ecss.extensions.commons.operations.InsertListOperation#insertContent(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.node.AuthorNode, java.util.List)
  */
  @Override
  protected void insertContent(AuthorAccess authorAccess, AuthorNode listNode, 
      List<SelectedFragmentInfo> selectedFragmentsInfos) {
    if (listNode instanceof AuthorElement) {
      List<AuthorNode> contentNodes = ((AuthorElement) listNode).getContentNodes();
      for (int i = 0; i < contentNodes.size(); i++) {
        AuthorNode authorNode = contentNodes.get(i);
        if (authorNode instanceof AuthorElement) {
          String localName = ((AuthorElement) authorNode).getLocalName();
          if ("item".equals(localName) && i < selectedFragmentsInfos.size()) {
            try {
              String xmlFragment = authorAccess.getDocumentController().serializeFragmentToXML(
                  selectedFragmentsInfos.get(i).getSelectedFragment());
              Map<String, String> attributes = selectedFragmentsInfos.get(i).getAttributes();
              if (attributes != null) {
                Set<String> names = attributes.keySet();
                for (String attrName : names) {
                  if (!"xml:id".equals(attrName)) {
                    ((AuthorElement) authorNode).setAttribute(attrName, new AttrValue(attributes.get(attrName)));
                  }
                }
              }
              authorAccess.getDocumentController().insertXMLFragmentSchemaAware(xmlFragment.toString(), 
                  authorNode.getStartOffset() + 1, AuthorSchemaAwareEditingHandler.ACTION_ID_INSERT_FRAGMENT, false);
            } catch (BadLocationException e) {
              logger.error(e, e);
            } catch (AuthorOperationException e) {
              logger.error(e, e);
            }
          }
        }
      }
    }
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Convert the selected paragraph(s) to an ordered/itemized list.";
  }

  /**
   * @see ro.sync.ecss.extensions.commons.operations.InsertListOperation#getConversionElementsChecker()
   */
  @Override
  protected ConversionElementHelper getConversionElementsChecker() {
    return new ConversionElementHelper() {
      /**
       * @see ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil.ConversionElementHelper#blockContentMustBeConverted(ro.sync.ecss.extensions.api.node.AuthorNode, ro.sync.ecss.extensions.api.AuthorAccess)
       */
      @Override
      public boolean blockContentMustBeConverted(AuthorNode node, AuthorAccess authorAccess) throws AuthorOperationException {
        boolean canBeConverted = false;
        if (node instanceof AuthorElement) {
          AuthorElement element = (AuthorElement) node;
          String name = element.getLocalName();
          if (name != null) {
            if ("p".equals(name) || "list".equals(name) || "item".equals(name)) {
              canBeConverted = true;
            }
          }
        }
        
        if (!canBeConverted) {
          throw new AuthorOperationException(
              authorAccess.getAuthorResourceBundle().getMessage(ExtensionTags.LIST_CONVERT_EXCEPTION));
          }
        
        return canBeConverted;
      }
    };
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.operations.InsertListOperation#getParentListType(ro.sync.ecss.extensions.api.node.AuthorNode)
   */
  @Override
  protected String getParentListType(AuthorNode nodeAtOffset) {
    while (nodeAtOffset != null) {
      if (nodeAtOffset instanceof AuthorElement) {
        String localName = ((AuthorElement) nodeAtOffset).getLocalName();
        if ("item".equals(localName)) {
            return null;
        } else if ("list".equals(localName)) {
          AttrValue type = ((AuthorElement) nodeAtOffset).getAttribute("type");
          if ("bulleted".equals(type)) {
            return ITEMIZED_LIST;
          } else if ("ordered".equals(type)) {
            return ORDERED_LIST;
          }
        }
      }
      nodeAtOffset = nodeAtOffset.getParent();
    }
    return null;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.operations.InsertListOperation#isEmptyListElement(ro.sync.ecss.extensions.api.node.AuthorNode)
   */
  @Override
  protected boolean isEmptyListElement(AuthorNode node) {
    boolean toRet = false;
    if (node instanceof AuthorElement) {
      String localName = ((AuthorElement) node).getLocalName();
      String parentLocalName = ((AuthorElement) node).getParentElement().getLocalName();
      // Check the element
      toRet = ("list".equals(localName) || "item".equals(localName) || 
          "list".equals(parentLocalName) || "item".equals(parentLocalName))
          && node.getStartOffset() + 1 == node.getEndOffset();

    }
    return toRet;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.operations.InsertListOperation#getListTypeDescription(java.lang.String)
   */
  @Override
  protected String getListTypeDescription(String listType) {
    String toRet = "";

    if (ORDERED_LIST.equals(listType)) {
      toRet = "ordered";
    } else if (ITEMIZED_LIST.equals(listType)) {
      toRet = "itemized";
    }
    return toRet;
  
  }
}