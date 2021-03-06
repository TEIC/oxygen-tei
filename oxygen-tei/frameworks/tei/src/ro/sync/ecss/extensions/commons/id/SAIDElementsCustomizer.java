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
package ro.sync.ecss.extensions.commons.id;

import java.awt.Component;
import java.awt.Frame;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;

/**
 * Customize the list of elements for auto ID generation.
 * It is used on standalone implementation.
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class SAIDElementsCustomizer {
  
  /**
   * Ask the user to customize the ID elements.
   * 
   * @param authorAccess        Access to author functionality.
   * @param autoIDElementsInfo  Information about for what elements should IDs be generated.
   * @param listMessage         The label used on the dialog before the list
   *
   * @return The initial list of elements for which to generate IDs.
   */
  public GenerateIDElementsInfo customizeIDElements(
      AuthorAccess authorAccess, GenerateIDElementsInfo autoIDElementsInfo, String listMessage) {
    return customizeIDElements(authorAccess, autoIDElementsInfo, listMessage, null);
  }
  
  /**
   * Ask the user to customize the ID elements.
   * 
   * @param authorAccess        Access to author functionality.
   * @param autoIDElementsInfo  Information about for what elements should IDs be generated.
   * @param listMessage         The label used on the dialog before the list
   * @param helpPageID          The ID of the help page which will be opened when users invoke help in the dialog.
   *
   * @return The initial list of elements for which to generate IDs.
   */
  public GenerateIDElementsInfo customizeIDElements(
      AuthorAccess authorAccess, GenerateIDElementsInfo autoIDElementsInfo, String listMessage, final String helpPageID) {

    SAIDElementsCustomizerDialog idCustomizeDialog =
      new SAIDElementsCustomizerDialog(
          (Frame) authorAccess.getWorkspaceAccess().getParentFrame(),
          listMessage,
          authorAccess.getAuthorResourceBundle()){
      /**
       * @see ro.sync.exml.workspace.api.standalone.ui.OKCancelDialog#getHelpPageID()
       */
      public String getHelpPageID() {
        return helpPageID;
      }
    };

    idCustomizeDialog.setLocationRelativeTo((Component) authorAccess.getWorkspaceAccess().getParentFrame());
    return idCustomizeDialog.showDialog(autoIDElementsInfo);
  }
}