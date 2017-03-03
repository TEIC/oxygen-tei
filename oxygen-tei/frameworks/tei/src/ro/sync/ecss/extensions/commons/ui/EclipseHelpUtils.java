/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2015 Syncro Soft SRL, Romania.  All rights
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
package ro.sync.ecss.extensions.commons.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IContextComputer;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;

/**
 * Eclipse help utilities.
 * 
 * @author radu_coravu
 */
@API(type=APIType.INTERNAL, src=SourceType.PRIVATE)
public class EclipseHelpUtils {
  /**
   * Logger for logging. 
   */
  private static final Logger logger = Logger.getLogger(EclipseHelpUtils.class.getName());
  
  /**
   * Install the help in a new shell based on a provided help page ID.
   * @param newShell The new shell.
   * @param helpPageID The help page ID.
   */
  public static void installHelp(final Shell newShell, final String helpPageID){
    if (helpPageID != null) {
      try{
        final String[] pluginID = new String[1];
        pluginID[0] = (String) Class.forName("com.oxygenxml.editor.EditorPlugin").getMethod("getPluginID", new Class[0]).invoke(
            null, new Object[0]);
        if(pluginID[0] != null){
          try {
            //EXM-18582 Set a context computer so that the help page ID can be computed in the dialog dynamically
            //before the help is invoked
            WorkbenchHelpSystem whs = (WorkbenchHelpSystem) PlatformUI.getWorkbench().getHelpSystem();
            whs.setHelp(
                newShell,
                new IContextComputer() {
                  public Object[] getLocalContexts(HelpEvent event) {
                    return null;
                  }
                  public Object[] computeContexts(HelpEvent event) {
                    return new String[] {pluginID[0] + "." + helpPageID};
                  }
                }
                );
          } catch(Throwable t) {
            logger.warn(t, t);
            //Fallback
            PlatformUI.getWorkbench().getHelpSystem().setHelp(
                newShell,
                pluginID[0] + "." + helpPageID);
          }
        }
      } catch(Throwable t) {
        logger.warn(t, t);
      }
    }
  }
}
