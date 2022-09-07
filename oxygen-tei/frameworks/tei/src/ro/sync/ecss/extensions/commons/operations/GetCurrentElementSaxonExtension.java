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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.SequenceType;
import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.commons.operations.ElementLocationPath.ElementLocationPathComponent;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;

/**
 * Returns the current element for an XSLT operation.
 * 
 * @author cristi_talau
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class GetCurrentElementSaxonExtension extends ExtensionFunctionDefinition {

  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(GetCurrentElementSaxonExtension.class.getName());

  /**
   * The location of the current element.
   */
  private final ElementLocationPath elementLocation;

  /**
   * The cached element - computed only the first time the function is invoked.
   */
  private NodeInfo cachedElement = null;

  /**
   * Constructor.
   * 
   * @param currentElementLocation the location of the element defined as a simple XPath.
   */
  public GetCurrentElementSaxonExtension(ElementLocationPath currentElementLocation) {
    this.elementLocation = currentElementLocation;
  }
  
  /**
   * Implementation of the extension function.
   */
  private final class ExtensionFunctionCallImpl extends ExtensionFunctionCall {
    @Override
    public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
      if (cachedElement == null) {
        Item contextItem = context.getContextItem();
        if (contextItem instanceof NodeInfo) {
          contextItem = ((NodeInfo) contextItem).getRoot();
          cachedElement = getNodeByLocation(contextItem, elementLocation);
        }
      }
      return cachedElement != null ? cachedElement : EmptySequence.getInstance();
    }
    
    /**
     * Gets the node located by the given XPath.
     * @param contextItem The context item.
     * @return The node located by the XPath.
     */
    private NodeInfo getNodeByLocation(Item contextItem, ElementLocationPath elementLocation) {
      NodeInfo crtNode;
      crtNode = ((NodeInfo)contextItem).getRoot();
      List<ElementLocationPathComponent> parts = elementLocation.getPath();
      for (ElementLocationPathComponent part : parts) {
        if (logger.isDebugEnabled()) {
          logger.debug("XPath part: " + part);
        }
        crtNode = nextNode(crtNode, part);
      }
      return crtNode;
    }
    
    /**
     * Compute the next node to traverse based on the XPath component.
     * 
     * @param node The current node.
     * @param xpathComp The XPath component: either '..' or '*' or '*[nnn]'.
     * 
     * @return The next node.
     */
    private NodeInfo nextNode(NodeInfo node, ElementLocationPathComponent comp) {
      NodeInfo nextNode;
      if (comp instanceof ElementLocationPath.Root) {
        nextNode = this.getNthChild(node.getRoot(), 1);
      } else if (comp instanceof ElementLocationPath.Parent) {
        nextNode = node.getParent();
      } else if (comp instanceof ElementLocationPath.Child) {
        ElementLocationPath.Child childSelector = (ElementLocationPath.Child) comp; 
        nextNode = this.getNthChild(node, childSelector.getIndex());
      } else {
        throw new IllegalArgumentException("XPath component not supported: " + comp.toXPath());
      }
      return nextNode;
    }
    
    /**
     * Return the n-th child of the given node.
     * 
     * @param node The node.
     * @param n The index of the child.
     * @return The child.
     */
    private NodeInfo getNthChild(NodeInfo node, int n) {
      AxisIterator iter = node.iterateAxis(AxisInfo.CHILD, NodeKindTest.ELEMENT);
      NodeInfo child = null;
      for (int i = 0; i < n; i++) {
        child = iter.next();
      }
      return child;
    }
  }

  /**
   * @see net.sf.saxon.lib.ExtensionFunctionDefinition#getFunctionQName()
   */
  @Override
  public StructuredQName getFunctionQName() {
    return new StructuredQName(XMLUtilAccess.EXTENSION_PREFIX, XMLUtilAccess.EXTENSION_NS, "current-element");
  }

  /**
   * @see net.sf.saxon.lib.ExtensionFunctionDefinition#getArgumentTypes()
   */
  @Override
  public SequenceType[] getArgumentTypes() {
    return new SequenceType[0];
  }

  /**
   * @see net.sf.saxon.lib.ExtensionFunctionDefinition#getResultType(net.sf.saxon.value.SequenceType[])
   */
  @Override
  public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
    return SequenceType.OPTIONAL_NODE;
  }

  /**
   * @see net.sf.saxon.lib.ExtensionFunctionDefinition#makeCallExpression()
   */
  @Override
  public ExtensionFunctionCall makeCallExpression() {
    return new ExtensionFunctionCallImpl();
  }

}
