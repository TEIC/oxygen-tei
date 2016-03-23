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
package ro.sync.ecss.extensions.commons.table.operations.cals;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.AuthorTableCellSpanProvider;
import ro.sync.ecss.extensions.api.WebappCompatible;
import ro.sync.ecss.extensions.api.WidthRepresentation;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.table.operations.TableColumnSpecificationInformation;
import ro.sync.ecss.extensions.commons.table.operations.AuthorTableHelper;
import ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase;
import ro.sync.ecss.extensions.commons.table.operations.InsertTableCellsContentConstants;
import ro.sync.ecss.extensions.commons.table.support.CALSColSpec;
import ro.sync.ecss.extensions.commons.table.support.CALSTableCellInfoProvider;

/**
 * Operation used to insert one or more CALS table columns.
 */

@WebappCompatible(false)
public class InsertColumnOperation extends InsertColumnOperationBase implements 
  CALSConstants, InsertTableCellsContentConstants {
  
  /**
   * Operation arguments.
   */
  private ArgumentDescriptor[] arguments;
  
  /**
   * The fragment that must be introduced in the table cells
   */
  protected String cellContent;
  
  /**
   * Constructor.
   */
  public InsertColumnOperation() {
    this(new CALSDocumentTypeHelper());
  }
  
  /**
   * Constructor.
   * 
   * @param tableHelper The table helper
   */
  public InsertColumnOperation(AuthorTableHelper tableHelper) {
    super(tableHelper);
    ArgumentDescriptor[] superArgs = super.getArguments();
    if (superArgs != null) {
      this.arguments = new ArgumentDescriptor[superArgs.length + 1];
      // Add descriptors from super
      for (int i = 0; i < superArgs.length; i++) {
        this.arguments[i] = superArgs[i];
      }
      // Add cell fragment argument
      this.arguments[superArgs.length] = CELL_FRAGMENT_ARGUMENT;
    } else {
      this.arguments = CELL_FRAGMENT_ARGUMENT_IN_ARRAY;
    }
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase#doOperationInternal(ro.sync.ecss.extensions.api.AuthorAccess, ro.sync.ecss.extensions.api.ArgumentsMap)
   */
  @Override
  protected void doOperationInternal(AuthorAccess authorAccess, ArgumentsMap args)
  throws IllegalArgumentException, AuthorOperationException {
    Object cellFragmentObj =  args.getArgumentValue(CELL_FRAGMENT_ARGUMENT_NAME);
    if (cellFragmentObj instanceof String) {
      cellContent = (String) cellFragmentObj;
    } 
    if ("".equals(cellContent)) {
      cellContent = null;
    }
    super.doOperationInternal(authorAccess, args);
  }

  /**
   * Overwrite the base implementation.
   * For CALS tables the column specifications must be updated. 
   * 
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase#updateColumnCellsSpan(AuthorAccess, AuthorTableCellSpanProvider, AuthorElement, int, TableColumnSpecificationInformation, String, int)
   */
  @Override
  protected void updateColumnCellsSpan(AuthorAccess authorAccess,
      AuthorTableCellSpanProvider tableSupport, AuthorElement tgroup, int newColumnIndex,
      TableColumnSpecificationInformation columnSpecification,
      String namespace, int noOfColumnsToBeInserted) throws AuthorOperationException {
    
    // Find the name of the column specification relative to which the insertion will be performed. 
    Set<CALSColSpec> colSpecs = ((CALSTableCellInfoProvider) tableSupport).getColSpecs();
    
    int previousColspecIndex = -1;
    // If true insert the new 'colspec' before the determined relative 'colspec'
    boolean insertBefore = false;
    
    //Look at how the colspecs were previously defined.
    int numberOfCalNameSpec = 0;
    int numberOfCalNumSpec = 0;
    for (Iterator iterator = colSpecs.iterator(); iterator.hasNext();) {
      CALSColSpec colSpec = (CALSColSpec) iterator.next();
      if(colSpec.getColumnName() != null) {
        numberOfCalNameSpec ++;
      }
      if(colSpec.isColNumberSpecified()) {
        numberOfCalNumSpec ++;
      }
    }
    //Choose to specify a colname and colnum in the spanspec analysing the existing colspecs
    boolean specifyColNum = colSpecs.size() == 0 || numberOfCalNumSpec > 0;
    boolean specifyColName = colSpecs.size() == 0 || numberOfCalNameSpec > 0;
    
    for (Iterator iterator = colSpecs.iterator(); iterator.hasNext();) {
      CALSColSpec colSpec = (CALSColSpec) iterator.next();
      previousColspecIndex = colSpec.getIndexInDocument();
      if(colSpec.getColumnNumber() >= newColumnIndex + 1) {
        insertBefore = true;
        break;
      }
    }
    // Determine the index in document where the new 'colspec' will be inserted
    int insertOffset = -1;
    if(previousColspecIndex != -1) {
      // Increase the 'colnum' attributes which are <= than the inserted column
      List<AuthorNode> contentNodes = tgroup.getContentNodes();
      // Increase the 'colnum' of the column specification that are after the inserted column  
      for (Iterator<AuthorNode> iterator = contentNodes.iterator(); iterator.hasNext();) {
        AuthorNode colspecNodeCandidate = iterator.next();
        if(isElement(colspecNodeCandidate, ELEMENT_NAME_COLSPEC)) {
          AttrValue colSpecNumber= ((AuthorElement)colspecNodeCandidate).getAttribute(ATTRIBUTE_NAME_COLNUM);
          if(colSpecNumber != null) {
            try {
              int colSpecNr = Integer.parseInt(colSpecNumber.getValue());
              if (colSpecNr >= newColumnIndex + 1) {
                // If the col spec num is <= inserted column, increase it.
              	// EXM-31671: Do this for each column to be inserted.
                authorAccess.getDocumentController().setAttribute(
                    ATTRIBUTE_NAME_COLNUM,
                    new AttrValue("" + (colSpecNr + noOfColumnsToBeInserted)),
                    (AuthorElement) colspecNodeCandidate);
              }
            } catch (NumberFormatException e) {
              // Nothing to do
            }
          } else {
            //No colspec number specified, will auto-increase
          }
        }
      }

      // Look for the 'colSpec' which has a specific 'colSpecName'.
      int i = 0;
      for (Iterator<AuthorNode> iterator = contentNodes.iterator(); iterator.hasNext();) {
        AuthorNode node = iterator.next();
        if(isElement(node, ELEMENT_NAME_COLSPEC)) {
          if(i == previousColspecIndex) {
            // We found the 'colSpec', insert before or after it depending on the flag
            insertOffset = (insertBefore ? node.getStartOffset() : node.getEndOffset() + 1);
            break;
          }
        }  
        i++;
      }
    } else {
      // There are no 'colspec's defined
      if(newColumnIndex == 0) {
        // Insert a column specification at the start of 'tgroup'
        insertOffset = tgroup.getStartOffset() + 1;
      }
    }    

    //EXM-31671: create a set of column names in which we can add the names of each column after insertion.
    //This set is used for testing the uniqueness of a generated column name (if it is contained by the set,
    //then it's not unique and another one will be generated).
    Set<String> uniqueColumnNames = new HashSet<String>();
    for (CALSColSpec colSpec : colSpecs) {
      uniqueColumnNames.add(colSpec.getColumnName());
    }
    
    //EXM-31671: generate name, number, width for the colspecs corresponding to all the inserted columns
    StringBuilder newColSpecFragment = new StringBuilder();
    for (int i = 0; i < noOfColumnsToBeInserted; i++) {
      if (insertOffset != -1) {
        
        newColSpecFragment.append("<").append(ELEMENT_NAME_COLSPEC);
        if (namespace != null) {
          newColSpecFragment.append(" xmlns=\"").append(namespace).append("\"");
        }
        
        
        String newColSpecName = null;
        if(specifyColName) {
          if (columnSpecification instanceof CALSTableColumnSpecificationInformation) {
            // We could impose the column name from the initial column configuration
            newColSpecName = ((CALSTableColumnSpecificationInformation)columnSpecification).getColumnName();
          }
          if (newColSpecName == null) {
            //There was no imposed column name
            newColSpecName = getUniqueColSpecName(uniqueColumnNames, newColumnIndex + i + 1);
            //Add the new unique name to the set
            uniqueColumnNames.add(newColSpecName);
          }
          
          //Set also a column name to the colspec
          newColSpecFragment.append(" ").append(ATTRIBUTE_NAME_COLNAME);
          newColSpecFragment.append("=\"").append(newColSpecName).append("\"");
        }
        if(specifyColNum) {
          //Set also a column number to the colspec
          newColSpecFragment.append(" ").append(ATTRIBUTE_NAME_COLNUM);
          newColSpecFragment.append("=\"").append(newColumnIndex + i + 1).append("\"");
        }
        // EXM-23813 Set a default column width to the colspec (1*)
        String colWidth = getDefaultColWidthValue();
        if (columnSpecification != null) {
          WidthRepresentation colWidthRepresentation = columnSpecification.getWidthRepresentation();
          if (colWidthRepresentation != null) {
            // The colWidth is imposed from the column specification
            colWidth = colWidthRepresentation.getWidthRepresentation();
          }
        }
        if(colWidth != null) {
          newColSpecFragment.append(" ").append(ATTRIBUTE_NAME_COLWIDTH);
          newColSpecFragment.append("=\"").append(colWidth).append("\"");
        }
        
        newColSpecFragment.append("/>");
        
        uniqueColumnNames.add(newColSpecName);
        
        
      } else {
        throw new AuthorOperationException(
        "Could not compute the index of the column to be inserted.");
      }
    }
    // Insert the fragment with the new 'colspec'
    authorAccess.getDocumentController().insertXMLFragment(newColSpecFragment.toString(), insertOffset);
  }

  /**
   * Get the default col width value. Can be overwritten by an implementor.
   * 
   * @return The default col width value.
   */
  protected String getDefaultColWidthValue() {
    return "1*";
  }

  /**
   * Determine a unique column specification name for the column specification to be inserted.
   * 
   * @param colSpecNames  The set of column specification names.
   * @param colSpecIndex  The index of the column specification, 1 based.
   */
  private String getUniqueColSpecName(Set<String> colSpecNames, int colSpecIndex) {
    String uniqueColSpecName = "newCol" + colSpecIndex;
    // The number of iteration for find a unique col spec name 
    boolean isUnique = false;
    // Generate a new name until a unique one has been found
    while(!isUnique) {
      isUnique = true;
      for (Iterator<String> iterator = colSpecNames.iterator(); iterator.hasNext();) {
        String colSpec = iterator.next();
        if(uniqueColSpecName.equals(colSpec)) {
          isUnique = false;
          colSpecIndex ++;
          uniqueColSpecName = "newCol" + colSpecIndex;
          break;
        }
      }
    }
    return uniqueColSpecName;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase#getCellElementName(ro.sync.ecss.extensions.api.node.AuthorElement, int)
   */
  @Override
  protected String getCellElementName(AuthorElement row, int newColumnIndex) {
    return ELEMENT_NAME_ENTRY;
  }
  
  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase#getDefaultContentForEmptyCells()
   */
  @Override
  protected String getDefaultContentForEmptyCells() {
    return cellContent;
  }

  /**
   * @see ro.sync.ecss.extensions.commons.table.operations.InsertColumnOperationBase#getArguments()
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return arguments;
  }
}