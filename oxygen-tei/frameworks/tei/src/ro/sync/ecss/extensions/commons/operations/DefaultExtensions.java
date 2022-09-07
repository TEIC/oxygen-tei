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

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.commons.table.spansupport.TEITableCellSpanProvider;
import ro.sync.ecss.extensions.commons.table.support.CALSTableCellInfoProvider;
import ro.sync.ecss.extensions.commons.table.support.CALSandHTMLTableCellInfoProvider;
import ro.sync.ecss.extensions.commons.table.support.HTMLTableCellInfoProvider;

/**
 * Interface containing all the default operation distributed with Oxygen.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public interface DefaultExtensions {
  /**
   * The array with default operations.
   */
  static final Class[] DEFAULT_OPERATIONS = new Class[] {
      //Insert fragment.
      InsertFragmentOperation.class,
      DeleteElementOperation.class,
      //Insert or replace fragment.
      InsertOrReplaceFragmentOperation.class,
      //Insert or replace text.
      InsertOrReplaceTextOperation.class,
      //Surround with fragment.
      SurroundWithFragmentOperation.class,
      //Surround with text.
      SurroundWithTextOperation.class ,
      // Change attribute value.
      ChangeAttributeOperation.class, 
      // Unwrap tags
      UnwrapTagsOperation.class, 
      // Toggle surround with element
      ToggleSurroundWithElementOperation.class,
      // Toggle comments
      ToggleCommentOperation.class,
      // Insert XInclude element
      InsertXIncludeOperation.class,
      // Insert equation
      InsertEquationOperation.class,
      // Show element documentation
      ShowElementDocumentationOperation.class,
      // XSLT operation
      XSLTOperation.class,
      // XQuery operation
      XQueryOperation.class,
      XQueryUpdateOperation.class,
      OpenInSystemAppOperation.class,
      //Run transformation scenario operation.
      ExecuteTransformationScenariosOperation.class,
      // Run validation scenario operation.
      ExecuteValidationScenariosOperation.class,
      // Set a pseudo-class 
      SetPseudoClassOperation.class,
      // Removes a pseudo-class 
      RemovePseudoClassOperation.class,
      // Toggles a pseudo-class
      TogglePseudoClassOperation.class,
      //Change more than one pseudo classes.
      ChangePseudoClassesOperation.class,
      // Execute a sequence of actions operation
      ExecuteMultipleActionsOperation.class,
      // Execute a sequence of webapp-compatible actions operation
      ExecuteMultipleWebappCompatibleActionsOperation.class,
      // Move element operation.
      MoveElementOperation.class,
      // Move element operation.
      DeleteElementsOperation.class,
      // Renames elements.
      RenameElementOperation.class,
      // JS operation
      JSOperation.class,
      //EXM-34226: Added an operation that changes the read-only status of the document.
      SetReadOnlyStatusOperation.class,
      // Replaces the content of an XML document.
      ReplaceContentOperation.class,
      // EXM-35810: promote demote list items on Web Author
      PromoteDemoteItemOperation.class,
      // EXM-29044: Execute command line operations
      ExecuteCommandLineOperation.class,
      // EXM-36078: Move caret operation
      MoveCaretOperation.class,
      // WA-668: Webapp-only operation to mark a document as saved.
      WebappMarkAsSavedOperation.class,
      // Reloads the content operation
      ReloadContentOperation.class,
      // EXM-31097
      ChangeAttributesOperation.class,
      //WA-3185 Replace element content
      ReplaceElementContentOperation.class,
      // EXM-50545 - run a transformation scenario with custom params
      ExecuteCustomizableTransformationScenarioOperation.class,
      // EXM-50584 - stop transformation
      StopCurrentTransformationScenarioOperation.class
  };
  
  
  /**
   * The array with default classes used for table support.
   */
  static final Class[] DEFAULT_TABLE_SUPPORT = new Class[] {
      //DB and DITA table span support.
      CALSTableCellInfoProvider.class,
      //HTML table support.
      HTMLTableCellInfoProvider.class,
      //CALS and HTML table support.
      CALSandHTMLTableCellInfoProvider.class,
      //TEI table support.
      TEITableCellSpanProvider.class };
}