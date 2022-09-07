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
package ro.sync.ecss.extensions.commons.table.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.text.BadLocationException;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorOperationStoppedByUserException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.commons.AbstractDocumentTypeHelper;
import ro.sync.exml.workspace.api.Platform;

/**
 * Abstract class for operation used to insert a table row. 
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public abstract class InsertRowOperationBase extends AbstractTableOperation {
  
  /**
   * The insert location argument name. The argument defines the location where the operation will be executed as an XPath expression.
   * The value is <code>insertLocation</code>
   */
  private static final String XPATH_LOCATION_ARGUMENT = "insertLocation";
  
  /**
   *  The insert position argument name. The argument defines the relative position to the node obtained 
   *  from the XPath location where the row(s) will be inserted.
   *  The value is <code>insertPosition</code>
   */
  private static final String RELATIVE_POSITION_ARGUMENT = "insertPosition";

  /**
   * The name of the argument specifying if the custom row insertion has been requested or not.
   *  The value is <code>customRowInsertion</code>
   */
  private static final String CUSTOM_ROW_INSERTION_ARGUMENT  = "customRowInsertion";

  /**
   * Argument descriptor for the argument that specifies whether a custom insertion should be used.
   */
  protected static final ArgumentDescriptor CUSTOM_INSERTION_ARGUMENT_DESCRIPTOR = 
      new ArgumentDescriptor(CUSTOM_ROW_INSERTION_ARGUMENT, ArgumentDescriptor.TYPE_CONSTANT_LIST,
      "A boolean specifying if the custom row insertion has been requested or not. "
          + "A custom insertion allows the user to choose the number of rows to be inserted "
          + "and the position of insertion (above or below the current row).",
      new String[] {"true", "false"}, "false");
  
  /**
   * The arguments of the operation.
   */
  private ArgumentDescriptor[] arguments = null;
  
  /**
   * Get the array of arguments used for this operation.
   * The first argument defines the location where the operation will be executed
   * as an xpath expression, the second one defines the relative position to the 
   * node obtained from the XPath location, the third is the namespace argument
   * descriptor and the forth specifies if the user desires the insertion of multiple rows or not.
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
    ArgumentDescriptor[] args = new ArgumentDescriptor[4];
    
    // Argument defining the location where the operation will be executed as an xpath expression
    ArgumentDescriptor argumentDescriptor = 
      new ArgumentDescriptor(
          XPATH_LOCATION_ARGUMENT, 
          ArgumentDescriptor.TYPE_XPATH_EXPRESSION, 
          "An XPath expression indicating the insert location for the new table row.\n" +
          "Note: If it is not defined then the insert location will be at the caret.");
    args[0] = argumentDescriptor;
    
    // Argument defining the relative position to the node obtained from the XPath location
    argumentDescriptor = 
      new ArgumentDescriptor(
          RELATIVE_POSITION_ARGUMENT, 
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
              AuthorConstants.POSITION_AFTER              
          }, 
          AuthorConstants.POSITION_INSIDE_FIRST);
    args[1] = argumentDescriptor;
    
    args[2] = NAMESPACE_ARGUMENT_DESCRIPTOR;
    
    // argument specifying if the user desires to customize the row insertion (via "Insert rows...")
    argumentDescriptor = CUSTOM_INSERTION_ARGUMENT_DESCRIPTOR;
    args[3] = argumentDescriptor;
    
    return args;
  }
  
  /**
   * Constructor.
   * 
   * @param documentTypeHelper Author Document type helper, has methods specific to a document type.
   */
  public InsertRowOperationBase(AuthorTableHelper documentTypeHelper) {
    super(documentTypeHelper, true);
    
    arguments = getOperationArguments();
  }
    
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.AbstractTableOperation#doOperationInternal(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  protected void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args)
  throws AuthorOperationException {
    try {
      // The node at caret position
      AuthorNode nodeAtCaret =
        authorAccess.getDocumentController().getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset());
      
      // Table element
      AuthorElement tableElement = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_TABLE);

      if (tableElement != null) {

        // the number of rows to be inserted
        int noOfRowsToBeInserted = 0;
          
        // the relative position to the current location
        String relativePosition = "";
        
        TableRowsInfo tableRowInfo = null;
        // Custom row insertion has been requested
        if(AuthorConstants.ARG_VALUE_TRUE.equals(args.getArgumentValue(CUSTOM_ROW_INSERTION_ARGUMENT))) {
          Platform platform = authorAccess.getWorkspaceAccess().getPlatform();
          if (Platform.STANDALONE == platform) {
            // SWING
            tableRowInfo = SATableRowInsertionCustomizerInvoker.getInstance()
                .customizeTableRowInsertion(authorAccess);
          } else if (Platform.ECLIPSE == platform) {
            // SWT
            tableRowInfo = ECTableRowInsertionCustomizerInvoker.getInstance()
                .customizeTableRowInsertion(authorAccess);
          }
          // Get info from user
          if (tableRowInfo != null ) {
            noOfRowsToBeInserted = tableRowInfo.getRowsNumber();
            if(tableRowInfo.isInsertBelow()) {
              relativePosition = AuthorConstants.POSITION_AFTER;
            } else {
              relativePosition = AuthorConstants.POSITION_BEFORE;
            }
          } else {
            // User canceled the operation.
            throw new AuthorOperationStoppedByUserException("Cancelled by user");
          }
        } else { 
          // default row insertion
          noOfRowsToBeInserted = 1;
          relativePosition = args.getArgumentValue(RELATIVE_POSITION_ARGUMENT).toString();
        }
        
        insertRows(authorAccess, (String)args.getArgumentValue(XPATH_LOCATION_ARGUMENT), 
            (String) args.getArgumentValue(NAMESPACE_ARGUMENT), nodeAtCaret, tableElement, 
            noOfRowsToBeInserted, relativePosition);
      }
    } catch (BadLocationException e) {
      throw new AuthorOperationException(
          "The operation cannot be performed due to: " + e.getMessage(), e);    
    }    
  }

  /**
   * Insert rows.
   * 
   * @param authorAccess The author access.
   * @param xPathLocation The xPath location.
   * @param namespace The rows namespace.
   * @param nodeAtCaret The node at caret
   * @param tableElement The parent table element.
   * @param noOfRowsToBeInserted Number of rows to be inserted.
   * @param relativePosition One of {@link AuthorConstants#POSITION_AFTER} or
   * {@link AuthorConstants#POSITION_BEFORE} constants.  
   * @throws BadLocationException
   * @throws AuthorOperationException
   */
  public void insertRows(AuthorAccess authorAccess, String xPathLocation, String namespace,
      AuthorNode nodeAtCaret, AuthorElement tableElement, int noOfRowsToBeInserted,
      String relativePosition) throws BadLocationException, AuthorOperationException {
    // Current/Reference row element
    AuthorElement referenceRowElement = null;
    AuthorDocumentController documentController = authorAccess.getDocumentController();
    

    referenceRowElement = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_ROW);
    if(referenceRowElement == null) {
      //We want to insert before.
      AuthorElement table = getElementAncestor(nodeAtCaret, AuthorTableHelper.TYPE_TABLE);
      if(table != null) {
        //Probably the caret is between table rows.
        if(AuthorConstants.POSITION_BEFORE.equals(relativePosition)) {
          AuthorNode fullySelectedNode = authorAccess.getEditorAccess().getFullySelectedNode();
          // EXM-36837: when selecting an entire row, "Insert Above" should insert above... d'ohhh
          if (fullySelectedNode != null && tableHelper.isTableRow(fullySelectedNode)) {
            referenceRowElement = (AuthorElement) fullySelectedNode;
            xPathLocation = documentController.getXPathExpression(referenceRowElement.getStartOffset() + 1);
          } else {
            //We want to insert before. The reference row will be the one from below the caret.
            AuthorNode nodeBelowCurrentOffset = documentController.getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset() + 1);
            referenceRowElement = getElementAncestor(nodeBelowCurrentOffset, AuthorTableHelper.TYPE_ROW);
          }
        } else if(AuthorConstants.POSITION_AFTER.equals(relativePosition)) {
          //We want to insert after. The reference row will be the one from above the caret.
          AuthorNode nodeAboveCurrentOffset = documentController.getNodeAtOffset(authorAccess.getEditorAccess().getCaretOffset() - 1);
          referenceRowElement = getElementAncestor(nodeAboveCurrentOffset, AuthorTableHelper.TYPE_ROW);
        }
      }
    }

    // XML code to be inserted
    StringBuilder xmlFragmentBuilder = new StringBuilder();
    // Build the XML fragment for as many rows as needed
    for (int i = 0; i < noOfRowsToBeInserted; i++) {
      xmlFragmentBuilder.append(getRowXMLFragment(
          authorAccess, tableElement, referenceRowElement, useCurrentRowTemplateOnInsert(),
          namespace, AuthorConstants.POSITION_BEFORE.equals(relativePosition)));
    }

    String xmlFragment = xmlFragmentBuilder.toString();
    if (!xmlFragment.isEmpty()) {
      // Insert row fragment
      documentController.insertXMLFragmentSchemaAware(
          xmlFragment, xPathLocation, relativePosition);

      if (referenceRowElement != null) {
         AuthorElement startUpdateRowSpansRow = referenceRowElement; 
        if(AuthorConstants.POSITION_BEFORE.equals(relativePosition)){
          //The reference row is below the row which gets inserted, only update rows which are before it
          AuthorNode prevRow =
              documentController.getNodeAtOffset(referenceRowElement.getStartOffset() - 1);
          if (prevRow.getType() == AuthorNode.NODE_TYPE_ELEMENT && tableHelper.isTableRow(prevRow)) {
            startUpdateRowSpansRow = (AuthorElement) prevRow;
          } else {
            startUpdateRowSpansRow = null;
          }
        }
        if(startUpdateRowSpansRow != null) {
          // Increment row spans for cells spanning over multiple rows
          incrementRowSpans(tableElement, startUpdateRowSpansRow, authorAccess, 1, noOfRowsToBeInserted, relativePosition);
        }
      } 
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
   * @param initialNumberOfColumns The initial number of columns. 
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
      int newCellColumnIndex, 
      int initialNumberOfColumns) throws BadLocationException {
    StringBuilder newRowStructure = null;
    if (newCellFragment != null && newCellColumnIndex <= initialNumberOfColumns) {

      // Determine the row name
      String rowName = getRowElementName(tableElement);
      if (rowName != null) {
        // Start row fragment 
        newRowStructure = new StringBuilder("<").append(rowName);
        if (namespace != null) {
          int prefixDelimiter = rowName.indexOf(':');
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
        for (int i = 0; i < initialNumberOfColumns; i++) {
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
        
        if (newCellColumnIndex == initialNumberOfColumns) {
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
   * @param preferReferencedRow <code>true</code> to prefer the referenced row.
   * @param namespace           The namespace of the table row.
   * @param before <code>true</code> to insert before the reference row.
   *
   * @return The XML fragment to be inserted.
   *
   * @throws BadLocationException  
   */
  private String getRowXMLFragment(
      AuthorAccess authorAccess, 
      AuthorElement tableElement, 
      AuthorElement referenceRowElement,
      boolean preferReferencedRow,
      String namespace, boolean before) throws BadLocationException {
    StringBuilder newRowStructure = null;
    String defaultRowElementName = getRowElementName(tableElement);
    // Determine the row name
    String rowName = defaultRowElementName;
    if(defaultRowElementName == null){
      //EXM-38582 fallback to reference row element
      if(referenceRowElement != null){
        rowName = referenceRowElement.getName();
      }
    } else if (preferReferencedRow){
      rowName = referenceRowElement != null ? referenceRowElement.getName() : defaultRowElementName;
    } 
    // Determine the namespace
    namespace = (preferReferencedRow && referenceRowElement != null) ? referenceRowElement.getNamespace() : namespace;
    if (rowName != null) {
      // Start row fragment 
      newRowStructure = new StringBuilder("<").append(rowName);
      if (namespace != null) {
        int prefixDelimiter = rowName.indexOf(':');
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
      
      boolean useReferenceRowElement = preferReferencedRow;
      if(! useReferenceRowElement){
        if(getCellElementName(tableElement, 0) == null){
       	//EXM-38582 We will need to use the reference row element...
          useReferenceRowElement = true;
        }
      }
      
      if (referenceRowElement != null && useReferenceRowElement) {
        AuthorTableCellSpanProvider spanProvider = tableHelper.getTableCellSpanProvider(tableElement);

        // Copy referenced row structure
        List<AuthorNode> contentNodes = referenceRowElement.getContentNodes();
        String[] ignoredAttributes = mergeArrays(tableHelper.getIgnoredRowAttributes(), tableHelper.getIgnoredCellIDAttributes());
        List<String> allIgnoredAttrs = new ArrayList<String>();
        allIgnoredAttrs.addAll(Arrays.asList(ignoredAttributes));
        String[] allowedAttributes = tableHelper instanceof AbstractDocumentTypeHelper ?
            ((AbstractDocumentTypeHelper)tableHelper).getAllowedCellAttributesToCopy() : null;
        for (AuthorNode cellNode : contentNodes) {
          // Create the new cells fragments 
          if (cellNode.getType() == AuthorNode.NODE_TYPE_ELEMENT) {
            // Copy only cells without row span
            Integer rowSpan = spanProvider.getRowSpan((AuthorElement) cellNode);
            if (rowSpan == null || rowSpan < 2 
                //When we insert before the reference row, the vertical spans on the cells for the row
                //below are of no interest to us.
                || before) {
              String cellXMLFragment = createCellXMLFragment((AuthorElement) cellNode, 
                  ignoredAttributes, allowedAttributes, getDefaultContentForEmptyCells());
              
              // Add cell fragment to row content
              newRowStructure.append(cellXMLFragment);
            }
          }
        }
      } else {
        // Try to create a row with maximum number of cells
        int colsNumber = authorAccess.getTableAccess().getTableNumberOfColumns(tableElement);
        if (colsNumber > 0) {
          for (int i = 0; i < colsNumber; i++) {
            createCell(tableElement, newRowStructure, i);
          }
        } else if (colsNumber <= 0) {
          // If the table is empty, add one cell.
          createCell(tableElement, newRowStructure, 0);
        }
      }

      // End row fragment
      newRowStructure.append("</").append(rowName).append(">");
    }
    
    return newRowStructure == null ? null : newRowStructure.toString();
  }

  /**
   * Create a table cell.
   * 
   * @param tableElement        The table element into which to add the cell.
   * @param newRowStructure     The row which will contain the cell.
   * @param i                   The column index of the cell. Used to determine the name of the cell element.
   */
  private void createCell(AuthorElement tableElement, StringBuilder newRowStructure, int i) {
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
  
  /**
   * Merges the two arrays in one.
   * 
   * @param array1 The first array.
   * @param array2 The second array.
   * @return The newly created array.
   */
  private static String[] mergeArrays(String[] array1, String[] array2) {
    String[] toRet;
    if(array1 == null) {
      toRet = array2;
    } else if(array2 == null) {
      toRet = array1;
    } else {
      String[] result = new String[array1.length + array2.length];
      System.arraycopy(array1, 0, result, 0, array1.length);
      System.arraycopy(array2, 0, result, array1.length, array2.length);
      toRet = result;
    }
    return toRet;
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
      String[] allowedAttributes, 
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
          if(allowedAttributes != null){
        	 //EXM-35307  We need to check against list of allowed attributes.
            skip = true;
            for (String allowedAttribute : allowedAttributes) {
              if (attributeName.equals(allowedAttribute)) {
                // This attribute must be used.
                skip = false;
                break;
              }
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
   * Increment all row spans that affect the inserted row, starting from reference row.
   * 
   * @param tableElement            The table element.
   * @param referenceRowElement     The reference row element.
   * @param authorAccess            The author access.
   * @param minRowSpan              The minimum row span that must be updated.
   * @param numberOfInsertedRows    The number of inserted rows.
   *
   * @throws BadLocationException
   */
  private void incrementRowSpans(
      AuthorElement tableElement, 
      AuthorElement referenceRowElement,
      AuthorAccess authorAccess, 
      int minRowSpan,
      int numberOfInsertedRows,
      String relativePosition) throws BadLocationException {

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
          if (rowSpan != null && rowSpan > 1 
              && (rowSpan >= minRowSpan 
                  //EXM-31665
                  || (AuthorConstants.POSITION_BEFORE.equals(relativePosition) && rowSpan + numberOfInsertedRows > minRowSpan)) ) {
            tableHelper.updateTableRowSpan(authorAccess, cellElement, rowSpan + numberOfInsertedRows);
          }
        }
      }
    }
    
    // The update for the upper cells stops when a row without rowspan is found 
    if (tableNumberOfColumns != currentRowSize) {
      // Determine the previous row 
      AuthorNode prevRow =
        authorAccess.getDocumentController().getNodeAtOffset(referenceRowElement.getStartOffset() - 1);
      minRowSpan++;
      if (prevRow.getType() == AuthorNode.NODE_TYPE_ELEMENT && tableHelper.isTableRow(prevRow)) {
        // Increment row spans of the previous row
        incrementRowSpans(tableElement, (AuthorElement) prevRow, authorAccess, minRowSpan, numberOfInsertedRows, relativePosition);
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
  
  /**
   * Removes the argument descriptor for custom insertion from an arguments list.
   *  
   * @param superArguments The input arguments list.
   * 
   * @return The filtered arguments list.
   */
  protected static ArgumentDescriptor[] removeCustomInsertionDescriptor(ArgumentDescriptor[] superArguments) {
    List<ArgumentDescriptor> arguments = new ArrayList<ArgumentDescriptor>(superArguments.length);
    Collections.addAll(arguments, superArguments);
    arguments.remove(CUSTOM_INSERTION_ARGUMENT_DESCRIPTOR);
    return arguments.toArray(new ArgumentDescriptor[0]);
  }
}