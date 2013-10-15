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
package ro.sync.ecss.extensions.commons.table.operations;

import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * Abstract class for operation used to insert a table row. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class InsertRowOperationBase extends AbstractTableOperation {
  
  /**
   * The insert location argument.
   * The value is <code>insertLocation</code>
   */
  private static final String ARGUMENT_XPATH_LOCATION = "insertLocation";
  
  /**
   * The insert position argument.
   *  The value is <code>insertPosition</code>
   */
  private static final String ARGUMENT_RELATIVE_LOCATION = "insertPosition";

  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Get the array of arguments used for this operation.
   * The first argument define the location where the operation will be executed
   * as an xpath expression, the second one define the relative position to the 
   * node obtained from the XPath location and the third is the namespace argument
   * descriptor.
   * For the second argument included in the returned arguments descriptor array,
   * the allowed values are:
   * <code>
   * {@link AuthorConstants#POSITION_BEFORE}, 
   * {@link AuthorConstants#POSITION_AFTER}, 
   * {@link AuthorConstants#POSITION_INSIDE_FIRST}
   * {@link AuthorConstants#POSITION_INSIDE_LAST}
   * </code> 
   * 
   * @return The array with the arguments of the operation.
   */
  protected ArgumentDescriptor[] getOperationArguments() {
    ArgumentDescriptor[] args = new ArgumentDescriptor[3];
    
    // Argument defining the location where the operation will be executed as an xpath expression
    ArgumentDescriptor argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_XPATH_LOCATION, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating the insert location for the new table row.\n" +
          "Note: If it is not defined then the insert location will be at the caret.");
    args[0] = argumentDescriptor;
    
    // Argument defining the relative position to the node obtained from the XPath location
    argumentDescriptor = 
      new ArgumentDescriptor(
          ARGUMENT_RELATIVE_LOCATION, 
          ArgumentDescriptor.TYPE_CONSTANT_LIST,
          "The insert position relative to the node determined by the XPath expression.\n" +
          "Can be: " + 
          AuthorConstants.POSITION_BEFORE + ", " +
          AuthorConstants.POSITION_INSIDE_FIRST + ", " +
          AuthorConstants.POSITION_INSIDE_LAST + " or " +
          AuthorConstants.POSITION_AFTER + ".\n" +
          "Note: If the XPath expression is not defined this argument is ignored.",
          new String[] {
              AuthorConstants.POSITION_BEFORE,
              AuthorConstants.POSITION_INSIDE_FIRST,
              AuthorConstants.POSITION_INSIDE_LAST,
              AuthorConstants.POSITION_AFTER,
              
          }, 
          AuthorConstants.POSITION_INSIDE_FIRST);
    args[1] = argumentDescriptor;
    
    args[2] = NAMESPACE_ARGUMENT_DESCRIPTOR;
    return args;
  }
  
  /**
   * Constructor.
   * 
   * @param documentTypeHelper Author Document type helper, has methods specific to a document type.
   */
  public InsertRowOperationBase(AuthorTableHelper documentTypeHelper) {
    super(documentTypeHelper);
    
    arguments = getOperationArguments();
  }
    
  /**
   * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  public void doOperation(AuthorAccess authorAccess, ArgumentsMap args)
  throws IllegalArgumentException, AuthorOperationException {
    try {
      Object namespaceObj =  args.getArgumentValue(NAMESPACE_ARGUMENT);
      
      // The node at caret position
      AuthorNode nodeAtCaret =
        authorAccess.getDocumentController().getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset());
      
      // Table element
      AuthorElement tableElement = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_TABLE);

      if (tableElement != null) {
        // Current row element
        AuthorElement referenceRowElement =
          useCurrentRowTemplateOnInsert() ? getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_ROW)
                                          : null;

        String xmlFragment =
          getRowXMLFragment(authorAccess, tableElement, referenceRowElement, (String) namespaceObj);

        if (xmlFragment != null) {
          // Insert row fragment
          authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
              xmlFragment, 
              (String)args.getArgumentValue(ARGUMENT_XPATH_LOCATION), 
              (String)args.getArgumentValue(ARGUMENT_RELATIVE_LOCATION));
          
          if (referenceRowElement != null) {
            // Increment row spans
            incrementRowSpans(tableElement, referenceRowElement, authorAccess, 1);
          }
        }
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException(
          "The operation cannot be performed due to: " + e.getMessage(), e);    
    }    
  }
  
  /**
   * Creates the XML fragment representing a new table row to be inserted.
   * 
   * @param authorAccess        The author access.
   * @param tableElement        The table element.
   * @param namespace           The namespace of the table row.
   * @param newCellFragment     The row will contain an additional cell added at the given 
   * column index.
   * @param newCellColumnIndex  The column index of the additional cell 
   *
   * @return The XML fragment to be inserted.
   *
   * @throws BadLocationException  
   */
  public String getRowXMLFragment(
      AuthorAccess authorAccess, 
      AuthorElement tableElement, 
      String namespace, 
      String newCellFragment, 
      int newCellColumnIndex) throws BadLocationException {
    int colsNumber = authorAccess.getTableAccess().getTableNumberOfColumns(tableElement);
    StringBuilder newRowStructure = null;
    if (newCellFragment != null && newCellColumnIndex <= colsNumber) {

      // Determine the row name
      String rowName = getRowElementName(tableElement);
      if (rowName != null) {
        // Start row fragment 
        newRowStructure = new StringBuilder("<").append(rowName);
        if (namespace != null) {
          int prefixDelimiter = rowName.indexOf(":");
          String prefix = null;
          if (prefixDelimiter > 0) {
            prefix = rowName.substring(0, prefixDelimiter);
          }
          newRowStructure.append(" xmlns");
          if (prefix != null) {
            // The reference row has a prefix 
            newRowStructure.append(":").append(prefix);
          } 
          newRowStructure.append("=\"").append(namespace).append("\"");

        }
        newRowStructure.append(">");


        // Create a row with maximum number of cells
        for (int i = 0; i < colsNumber; i++) {
          String cellName = getCellElementName(tableElement, i);
          
          if (i == newCellColumnIndex) {
            newRowStructure.append(newCellFragment);
          }
          
          if (cellName != null) {
            String defaultContentForEmptyCells = getDefaultContentForEmptyCells();
            if (defaultContentForEmptyCells != null) {
              newRowStructure.append("<").append(cellName).append(">");
              newRowStructure.append(defaultContentForEmptyCells);
              newRowStructure.append("</").append(cellName).append(">");
            } else {
              newRowStructure.append("<").append(cellName).append("/>");
            }
          }
        }
        
        if (newCellColumnIndex == colsNumber) {
          newRowStructure.append(newCellFragment);
        }
        
        // End row fragment
        newRowStructure.append("</").append(rowName).append(">");
      }
    }
    return newRowStructure == null ? null : newRowStructure.toString();
  }

  /**
   * Creates the XML fragment representing a new table row to be inserted.
   * 
   * @param authorAccess        The author access.
   * @param referenceRowElement The reference row element (from the caret position). 
   * @param tableElement        The table element.
   * @param namespace           The namespace of the table row.
   *
   * @return The XML fragment to be inserted.
   *
   * @throws BadLocationException  
   */
  public String getRowXMLFragment(
      AuthorAccess authorAccess, 
      AuthorElement tableElement, 
      AuthorElement referenceRowElement,
      String namespace) throws BadLocationException {
    StringBuilder newRowStructure = null;
    // Determine the row name
    String rowName =
      referenceRowElement != null ? referenceRowElement.getName() : getRowElementName(tableElement);
    // Determine the namespace
    namespace = referenceRowElement != null ? referenceRowElement.getNamespace() : namespace;
    if (rowName != null) {
      // Start row fragment 
      newRowStructure = new StringBuilder("<").append(rowName);
      if (namespace != null) {
        int prefixDelimiter = rowName.indexOf(":");
        String prefix = null;
        if (prefixDelimiter > 0) {
          prefix = rowName.substring(0, prefixDelimiter);
        }
        newRowStructure.append(" xmlns");
        if (prefix != null) {
          // The reference row has a prefix 
          newRowStructure.append(":").append(prefix);
        } 
        newRowStructure.append("=\"").append(namespace).append("\"");
        
      }
      newRowStructure.append(">");
      if (referenceRowElement != null) {
        AuthorTableCellSpanProvider spanProvider = tableHelper.getTableCellSpanProvider(tableElement);

        // Copy referenced row structure
        List<AuthorNode> contentNodes = referenceRowElement.getContentNodes();
        String[] ignoredAttributes = tableHelper.getIgnoredRowAttributes();
        for (AuthorNode cellNode : contentNodes) {
          // Create the new cells fragments 
          if (cellNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
            // Copy only cells without row span
            Integer rowSpan = spanProvider.getRowSpan((AuthorElement) cellNode);
            if (rowSpan == null || rowSpan < 2) {
              String cellXMLFragment = createCellXMLFragment((AuthorElement) cellNode, 
                  ignoredAttributes, getDefaultContentForEmptyCells());
              
              // Add cell fragment to row content
              newRowStructure.append(cellXMLFragment);
            }
          }
        }
      } else {
        // Create a row with maximum number of cells
        int colsNumber = authorAccess.getTableAccess().getTableNumberOfColumns(tableElement);
        for (int i = 0; i < colsNumber; i++) {
          String cellName = getCellElementName(tableElement, i);
          if (cellName != null) {
            String defaultContentForEmptyCells = getDefaultContentForEmptyCells();
            if (defaultContentForEmptyCells != null) {
              newRowStructure.append("<").append(cellName).append(">");
              newRowStructure.append(defaultContentForEmptyCells);
              newRowStructure.append("</").append(cellName).append(">");
            } else {
              newRowStructure.append("<").append(cellName).append("/>");
            }
          }
        }
      }

      // End row fragment
      newRowStructure.append("</").append(rowName).append(">");
    }
    
    return newRowStructure == null ? null : newRowStructure.toString();
  }
  
  /**
   * Create a cell XML fragment by copying the element and attributes
   * from a given cell element.
   *  
   * @param cell The cell to copy the element name and attributes from.
   * @param skippedAttributes List of skipped attributes names.
   * @param cellContent The cell content.
   * @return The cell fragment.
   * @throws BadLocationException
   */
  protected String createCellXMLFragment(
      AuthorElement cell,
      String[] skippedAttributes, 
      String cellContent) throws BadLocationException {
    StringBuilder cellXMLFragment = new StringBuilder(); 
    
    if (cell != null) {
      cellXMLFragment.append("<").append(cell.getName());
      int attributesCount = cell.getAttributesCount();
      for (int i = 0; i < attributesCount; i++) {
        String attributeName = cell.getAttributeAtIndex(i);
        AttrValue attribute = cell.getAttribute(attributeName);
        // Add only specified attributes
        if (attribute.isSpecified()) {
          boolean skip = false;
          for (String skippedAttribute : skippedAttributes) {
            if (attributeName.equals(skippedAttribute)) {
              // This attribute must be skipped
              skip = true;
              break;
            }
          }
          if (!skip) {
            // Add attribute
            cellXMLFragment.append(" ").append(attributeName).
            append("=\"").append(attribute.getRawValue()).append("\"");
          }
        }
      }

      if (cellContent != null && cellContent.length() > 0) {
        cellXMLFragment.append(">");
        cellXMLFragment.append(cellContent);
        cellXMLFragment.append("</").append(cell.getName()).append(">");
      } else {
        cellXMLFragment.append("/>");
      }
      
    }
    
    return cellXMLFragment.toString();
  }

  /**
   * Increment all row spans that affects the inserted row, starting from reference row.
   * 
   * @param tableElement        The table element.
   * @param referenceRowElement The reference row element.
   * @param authorAccess        The author access.
   * @param minRowSpan          The minimum row span that must be updated.
   * @param spanProvider        The table cell span provider.
   *
   * @throws BadLocationException
   */
  private void incrementRowSpans(
      AuthorElement tableElement, 
      AuthorElement referenceRowElement,
      AuthorAccess authorAccess, 
      int minRowSpan) throws BadLocationException {

    // Determine the total number of table columns
    int tableNumberOfColumns = authorAccess.getTableAccess().getTableNumberOfColumns(tableElement);
    AuthorTableCellSpanProvider spanProvider = null;
    // Increment the row span in the current row
    List<AuthorNode> cells = referenceRowElement.getContentNodes();
    int currentRowSize = cells.size();
    for (AuthorNode cellNode : cells) {
      if (cellNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
        if (tableHelper.isTableCell(cellNode)) {
          AuthorElement cellElement = (AuthorElement) cellNode;
          if (spanProvider == null) {
            spanProvider = tableHelper.getTableCellSpanProvider(tableElement);
          }
          Integer rowSpan = spanProvider.getRowSpan(cellElement);
          // Check if the current cell has a row span that affects the inserted row
          if (rowSpan != null && rowSpan > 1 && rowSpan >= minRowSpan) {
            tableHelper.updateTableRowSpan(authorAccess, cellElement, rowSpan + 1);
          }
        }
      }
    }
    
    // The update for the upper cells stops when a row without rowspans is found 
    if (tableNumberOfColumns != currentRowSize) {
      // Determine the previous row 
      AuthorNode prevRow =
        authorAccess.getDocumentController().getNodeAtOffset(referenceRowElement.getStartOffset() - 1);
      minRowSpan++;
      if (prevRow.getType() == AuthorNode.NODE_TYPE_ELEMENT && tableHelper.isTableRow(prevRow)) {
        // Increment row spans of the previous row
        incrementRowSpans(tableElement, (AuthorElement) prevRow, authorAccess, minRowSpan);
      }
    }
  }

  /**
   * The operation will display a dialog for choose table attributes.
   * 
   * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }
  
  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Insert a table row.";
  }

  /**
   * Get the name of the element that represents a cell.
   * @param tableElement The table element
   * @param columnIndex The column index.
   * @return The name of the element that represent a cell in the table.
   */
  protected abstract String getCellElementName(AuthorElement tableElement, int columnIndex);

  /**
   * Get the name of the element that represents a row.
   * 
   * @param tableElement The table parent element.
   * @return The name of the element that represent a row in the table.
   */
  protected abstract String getRowElementName(AuthorElement tableElement);
  
  /**
   * @return <code>true</code> if the current row template should be used to create 
   * the new row that must be inserted.
   * Default: <code>false</code>
   */
  protected boolean useCurrentRowTemplateOnInsert() {
    return false;
  }
  
  /**
   * Get the default content that must be introduced in empty cells.
   * 
   * @return The default content that must be introduced in empty cells.
   * Default: <code>null</code>.
   * 
   * @since 14.1
   */
  protected String getDefaultContentForEmptyCells() {
    return null;
  }
}