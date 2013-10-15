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
package ro.sync.ecss.extensions.commons.operations.text.test;

import java.io.File;

import ro.sync.ecss.conditions.ProfileAppliedConditionSetPO;
import ro.sync.ecss.conditions.ProfileConditionsSetInfoPO;
import ro.sync.ecss.extensions.EditorAuthorExtensionTestBase;
import ro.sync.ecss.extensions.commons.operations.text.CountWordsOperation;
import ro.sync.exml.Tags;
import ro.sync.exml.options.OptionTags;
import ro.sync.exml.options.Options;
import ro.sync.options.PersistentObject;
import ro.sync.options.SerializableLinkedHashMap;
import ro.sync.util.URLUtil;

/**
 * Test for the operation {@link CountWordsOperation}.
 * 
 * @author Costi
 */
public class CountWordsOperationTest extends EditorAuthorExtensionTestBase {
  /**
   * <p><b>Description:</b> Test for 'Count words' operation.</p>
   * <p><b>Bug ID:</b> EXM-13458</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testCountWordsTest() throws Exception {
    //Apply a condition set to obtain the filtered list:
    // Create Condition Set
    ProfileConditionsSetInfoPO conditionSet = new ProfileConditionsSetInfoPO();
    conditionSet.setDocumentTypePattern("DITA*");
    conditionSet.setConditionSetName("audience");
    SerializableLinkedHashMap<String, String[]> map = new SerializableLinkedHashMap<String, String[]>();
    map.put("audience", new String[] {"expert"});
    conditionSet.setConditions(map);
    
    // Save Set
    Options.getInstance().setObjectArrayProperty(
        OptionTags.PROFILING_CONDITIONS_SET_LIST, new PersistentObject[] { conditionSet });
    ProfileAppliedConditionSetPO po =
      new ProfileAppliedConditionSetPO("DITA*", conditionSet.getConditionSetName());
    Options.getInstance().setObjectArrayProperty(
        OptionTags.APPLIED_CONDITION_SETS, new PersistentObject[] { po });
    
    open(URLUtil.correct(new File("test/EXM-13458/test.xml")));
    
    // Execute 'To count words action' action
    invokeCommonAction(Tags.COUNT_WORDS, Tags.SOURCE);
    
    assertEquals(
        "Regular content:\n" + 
        "     Words: 6\n" + 
        "     Characters (no spaces): 33\n" + 
        "\n" + 
        "Filtered content:\n" + 
        "     Words: 3\n" + 
        "     Characters (no spaces): 27\n" + 
        "\n" + 
        "Read-only content:\n" + 
        "     Words: 1\n" + 
        "     Characters (no spaces): 4\n" + 
        "\n" + 
        "Total:\n" + 
        "     Words: 10\n" + 
        "     Characters (no spaces): 64",
    		lastInfoMessage);
  }
  
  /**
   * <p><b>Description:</b> Test for 'Count words' operation.</p>
   * <p><b>Bug ID:</b> EXM-13458</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testCountWordsTest2() throws Exception {
    //Apply a condition set to obtain the filtered list:
    // Create Condition Set
    ProfileConditionsSetInfoPO conditionSet = new ProfileConditionsSetInfoPO();
    conditionSet.setDocumentTypePattern("DITA*");
    conditionSet.setConditionSetName("audience");
    SerializableLinkedHashMap<String, String[]> map = new SerializableLinkedHashMap<String, String[]>();
    map.put("audience", new String[] {"expert"});
    conditionSet.setConditions(map);
    
    // Save Set
    Options.getInstance().setObjectArrayProperty(
        OptionTags.PROFILING_CONDITIONS_SET_LIST, new PersistentObject[] {conditionSet});
    ProfileAppliedConditionSetPO po =
      new ProfileAppliedConditionSetPO("DITA*", conditionSet.getConditionSetName());
    Options.getInstance().setObjectArrayProperty(
        OptionTags.APPLIED_CONDITION_SETS, new PersistentObject[] { po });
    
    open(URLUtil.correct(new File("test/EXM-13458/test.xml")));
    
    // Make selection
    moveCaretRelativeTo("word4", 0);
    moveCaretRelativeTo("Filtered1", "Filtered1".length(), true);
    
    // Execute 'To count words action' action
    invokeCommonAction(Tags.COUNT_WORDS, Tags.SOURCE);
    
    assertEquals(
        "Regular content:\n" + 
        "     Words: 2\n" + 
        "     Characters (no spaces): 9\n" + 
        "\n" + 
        "Filtered content:\n" + 
        "     Words: 1\n" + 
        "     Characters (no spaces): 9\n" + 
        "\n" + 
        "Read-only content:\n" + 
        "     Words: 1\n" + 
        "     Characters (no spaces): 4\n" + 
        "\n" + 
        "Total:\n" + 
        "     Words: 4\n" + 
        "     Characters (no spaces): 22",
    		lastInfoMessage);
  }
  
  /**
   * <p><b>Description:</b> Test for 'Count words' operation.</p>
   * <p><b>Bug ID:</b> EXM-13458</p>
   *
   * @author Costi
   *
   * @throws Exception When it fails.
   */
  public void testCountWordsTest3() throws Exception {
    //Apply a condition set to obtain the filtered list:
    // Create Condition Set
    ProfileConditionsSetInfoPO conditionSet = new ProfileConditionsSetInfoPO();
    conditionSet.setDocumentTypePattern("DITA*");
    conditionSet.setConditionSetName("audience");
    SerializableLinkedHashMap<String, String[]> map = new SerializableLinkedHashMap<String, String[]>();
    map.put("audience", new String[] {"expert"});
    conditionSet.setConditions(map);
    
    // Save Set
    Options.getInstance().setObjectArrayProperty(
        OptionTags.PROFILING_CONDITIONS_SET_LIST, new PersistentObject[] {conditionSet});
    ProfileAppliedConditionSetPO po =
      new ProfileAppliedConditionSetPO("DITA*", conditionSet.getConditionSetName());
    Options.getInstance().setObjectArrayProperty(
        OptionTags.APPLIED_CONDITION_SETS, new PersistentObject[] { po });
    
    open(URLUtil.correct(new File("test/EXM-13458/test.xml")));
    
    // Make selection
    moveCaretRelativeTo("word3", 3);
    moveCaretRelativeTo("word4", 3, true);
    
    // Execute 'To count words action' action
    invokeCommonAction(Tags.COUNT_WORDS, Tags.SOURCE);
    
    assertEquals(
        "Words: 2\n" + 
        "Characters (no spaces): 5",
    		lastInfoMessage);
  }
}