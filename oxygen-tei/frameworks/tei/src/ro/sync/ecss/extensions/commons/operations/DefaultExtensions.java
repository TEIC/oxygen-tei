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
  Class[] DEFAULT_OPERATIONS = new Class[] {
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
      OpenInSystemAppOperation.class,
      //Run transformation scenario operation.
      ExecuteTransformationScenariosOperation.class,
      // Set a pseudo-class 
      SetPseudoClassOperation.class,
      // Removes a pseudo-class 
      RemovePseudoClassOperation.class,
      // Toggles a pseudo-class
      TogglePseudoClassOperation.class,
      // Execute a sequence of actions operation
      ExecuteMultipleActionsOperation.class
  };
  
  /**
   * The array with default classes used for table support.
   */
  Class[] DEFAULT_TABLE_SUPPORT = new Class[] {
      //DB and DITA table span support.
      CALSTableCellInfoProvider.class,
      //HTML table support.
      HTMLTableCellInfoProvider.class,
      //CALS and HTML table support.
      CALSandHTMLTableCellInfoProvider.class,
      //TEI table support.
      TEITableCellSpanProvider.class };
}