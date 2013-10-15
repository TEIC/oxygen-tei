/*
 *  The Syncro Soft SRL License
 *
 *  Copyright (c) 1998-2007 Syncro Soft SRL, Romania.  All rights
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.util.editorvars.EditorVariables;

/**
 * Information about the list of elements for which to generate auto ID + if the auto ID generation is activated
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
public class GenerateIDElementsInfo {
  /**
   * The key from options
   */
  public static final String GENERATE_ID_ELEMENTS_KEY = "generate.id.for.elements";
  
  /**
   * The key from options
   */
  public static final String GENERATE_ID_ELEMENTS_ACTIVE_KEY = "auto.generate.id.elements.active";
  
  /**
   * The key from options
   */
  public static final String GENERATE_ID_PATTERN_KEY = "generate.ids.pattern";
  /**
   * The key from options
   */
  public static final String FILTER_IDS_ON_COPY_KEY = "filter.ids.on.copy";
  
  /**
   * Local name pattern macro.
   */
  public static final String LOCAL_NAME_PATTERN_MACRO = "${localName}";
  
  /**
   * Description for the local name pattern macro.
   */
  public static final String LOCAL_NAME_PATTERN_DESCRIPTION = "The local name of the element.";
  
  /**
   * Description for the uuid pattern macro.
   */
  public static final String UUID_PATTERN_DESCRIPTION = "Universally Unique Identifier";
  
  /**
   * Description for the id pattern macro.
   */
  public static final String ID_PATTERN_DESCRIPTION = "Compact Unique Identifier";
  
  /**
   * The default id generation pattern.
   */
  public static final String DEFAULT_ID_GENERATION_PATTERN = 
    LOCAL_NAME_PATTERN_MACRO + "_" + EditorVariables.ID;
  
  /**
   * The default pattern tooltip.
   */
  public static String PATTERN_TOOLTIP = 
    EditorVariables.ID + " - " + GenerateIDElementsInfo.ID_PATTERN_DESCRIPTION + "; \n" + 
    EditorVariables.UUID + " - " + GenerateIDElementsInfo.UUID_PATTERN_DESCRIPTION + "; \n " +
    GenerateIDElementsInfo.LOCAL_NAME_PATTERN_MACRO + " - " + GenerateIDElementsInfo.LOCAL_NAME_PATTERN_DESCRIPTION;
  
  /**
   * True to auto generate IDs
   */
  private boolean autoGenerateIds;
  
  /**
   * ID generation pattern.
   */
  private String idGenerationPattern;
  
  /**
   * List of elements with auto ID generation
   */
  private String[] elementsWithIDGeneration;

  /**
   * Filter IDs when copying content in the Author page.
   */
  private boolean removeIDsOnCopy;
  
  /**
   * The pattern tooltip
   */
  private String patternTooltip = PATTERN_TOOLTIP;
  
  /**
   * Constructor.
   * 
   * @param authorAccess The author access
   * @param defaultOptions The default options.
   */
  public GenerateIDElementsInfo(AuthorAccess authorAccess, GenerateIDElementsInfo defaultOptions) {
    //Read the Auto ID Elements Info from options.
    this(isAutoGenerateIDs(authorAccess, defaultOptions),
        getIDGenerationPattern(authorAccess, defaultOptions),
        getIDGenerationElements(authorAccess, defaultOptions),
        isFilterIDs(authorAccess, defaultOptions));
    
    setPatternTooltip(defaultOptions.getPatternTooltip());
  }

  /**
   * @param authorAccess Author access
   * @param defaultOptions Default options
   * @return the array of elements for ID generation
   */
  private static String[] getIDGenerationElements(AuthorAccess authorAccess,
      GenerateIDElementsInfo defaultOptions) {
    return splitStrings(authorAccess.getOptionsStorage().getOption(
    GENERATE_ID_ELEMENTS_KEY, defaultOptions.getElementsAsOptionsString()));
  }

  /**
   * @param authorAccess The author access
   * @return The ID generation pattern
   */
  private static String getIDGenerationPattern(AuthorAccess authorAccess, GenerateIDElementsInfo defaultOptions) {
    return authorAccess.getOptionsStorage().getOption(
        GENERATE_ID_PATTERN_KEY, defaultOptions.getIdGenerationPattern());
  }

  /**
   * @param authorAccess The author access
   * @param defaultOptions The default options
   * @return true if auto generate IDs
   */
  private static boolean isAutoGenerateIDs(AuthorAccess authorAccess,
      GenerateIDElementsInfo defaultOptions) {
    Boolean autoGenerate = Boolean.valueOf(authorAccess.getOptionsStorage().getOption(
        GENERATE_ID_ELEMENTS_ACTIVE_KEY, Boolean.toString(defaultOptions.isAutoGenerateIDs())));
    return autoGenerate.booleanValue();
  }
  
  /**
   * Checks if filter IDs activated.
   * 
   * @param authorAccess    The Author access.
   * @param defaultOptions  The default options.
   *
   * @return <code>true</code> if filter IDs.
   */
  private static boolean isFilterIDs(
      AuthorAccess authorAccess, GenerateIDElementsInfo defaultOptions) {
    return Boolean.valueOf(
        authorAccess.getOptionsStorage().getOption(
            FILTER_IDS_ON_COPY_KEY, Boolean.toString(defaultOptions.isFilterIDsOnCopy())));
  }
  
  /**
   * @param optionsString String read from options with comma separated values
   * @return null if string is empty or null or an array of strings
   */
  private static String[] splitStrings(String optionsString) {
    if(optionsString == null || optionsString.trim().length() == 0) {
      //Nothing here.
      return null;
    } else {
      return optionsString.split(",");
    }
  }
  
  /**
   * Constructor.
   * 
   * @param autoGenerateIds           <code>true</code> to auto generate IDs.
   * @param idGenerationPattern       The pattern for id generation.
   * @param elementsWithIDGeneration  List of elements for which to generate IDs.
   */
  public GenerateIDElementsInfo(
      boolean autoGenerateIds, String idGenerationPattern, String[] elementsWithIDGeneration) {
    this(autoGenerateIds, idGenerationPattern, elementsWithIDGeneration, true);
  }

  /**
   * Constructor.
   * 
   * @param autoGenerateIds           <code>true</code> to auto generate IDs.
   * @param idGenerationPattern       The pattern for id generation.
   * @param elementsWithIDGeneration  List of elements for which to generate IDs.
   * @param filterIDsOnCopy           Filter IDs when copying content in the same file.
   */
  public GenerateIDElementsInfo(
      boolean autoGenerateIds,
      String idGenerationPattern,
      String[] elementsWithIDGeneration,
      boolean filterIDsOnCopy) {
    this.autoGenerateIds = autoGenerateIds;
    this.idGenerationPattern = idGenerationPattern;
    this.elementsWithIDGeneration = elementsWithIDGeneration;
    this.removeIDsOnCopy = filterIDsOnCopy;
  }
  
  /**
   * @return <code>true</code> if auto generates IDs for elements.
   */
  public boolean isAutoGenerateIDs() {
    return autoGenerateIds;
  }
  
  /**
   * @return Returns <code>true</code> to filter IDs when copying content in the Author page.
   */
  public boolean isFilterIDsOnCopy() {
    return removeIDsOnCopy;
  }
  
  /**
   * @return Returns the pattern for id generation.
   */
  public String getIdGenerationPattern() {
    return idGenerationPattern;
  }
  
  /**
   * @return Returns the elements for which to generate IDs.
   */
  public String[] getElementsWithIDGeneration() {
    return elementsWithIDGeneration;
  }
  
  /**
   * Save to persistent options
   * @param authorAccess The author access
   */
  public void saveToOptions(AuthorAccess authorAccess) {
    //Store the auto ID elements info to options.
    //Save back as comma separated.
    StringBuffer toSave = new StringBuffer();
    toSave.append(getElementsAsOptionsString());
    authorAccess.getOptionsStorage().setOption(
        GENERATE_ID_ELEMENTS_KEY, toSave.toString());
    
    authorAccess.getOptionsStorage().setOption(
        GENERATE_ID_PATTERN_KEY, idGenerationPattern);
    
    authorAccess.getOptionsStorage().setOption(
        GENERATE_ID_ELEMENTS_ACTIVE_KEY, Boolean.toString(autoGenerateIds));
    
    //Filter IDs on copy
    authorAccess.getOptionsStorage().setOption(
        FILTER_IDS_ON_COPY_KEY, Boolean.toString(removeIDsOnCopy));
  }
  
  private String getElementsAsOptionsString() {
    StringBuffer toSave = new StringBuffer();
    for (int i = 0; i < elementsWithIDGeneration.length; i++) { 
      toSave.append(elementsWithIDGeneration[i]).append(",");
    }
    return toSave.toString();
  }
  
  /**
   * Generate an ID from a pattern for the specified element.
   * 
   * @param idGenerationPattern The pattern.
   * @param elementLocalName The element local name
   * @return The generated ID.
   */
  public static String generateID(String idGenerationPattern,  String elementLocalName) {
    // Process the pattern, look for macros
    if (idGenerationPattern.indexOf(GenerateIDElementsInfo.LOCAL_NAME_PATTERN_MACRO) != -1) {
      // Found a local name macro
      idGenerationPattern =
        replaceAll(
            idGenerationPattern,
            GenerateIDElementsInfo.LOCAL_NAME_PATTERN_MACRO,
            elementLocalName);
    }
    //Expand id and uuid editor variables (and much more).
    idGenerationPattern = EditorVariables.expandEditorVariables(idGenerationPattern, null);
    return idGenerationPattern;
  }
  
  /**
   * Replace all occurrences of 'match' with 'toReplaceWith'
   * @param original The original string.
   * @param match The match string (not a regular expression)
   * @param toReplaceWith String to replace with
   * @return The replaced string.
   */
  private static String replaceAll(String original, String match, String toReplaceWith) {
    try {
      return Pattern.compile(match, Pattern.LITERAL).matcher(original).replaceAll(
          Matcher.quoteReplacement(toReplaceWith));
    }catch(Throwable t) {
      return original;
    }
  }
  
  /**
   * Set auto generate IDs.
   * 
   * @param autoGenerateIds <code>true</code> to auto generate IDs.
   */
  public void setAutoGenerateIds(boolean autoGenerateIds) {
    this.autoGenerateIds = autoGenerateIds;
  }
  
  /**
   * Set a list of elements with ID generation
   * 
   * @param elementsWithIDGeneration a list of elements with ID generation
   */
  public void setElementsWithIDGeneration(String[] elementsWithIDGeneration) {
    this.elementsWithIDGeneration = elementsWithIDGeneration;
  }
  
  /**
   * Set the flag which controls whether the IDs will be removed on copy.
   * 
   * @param removeIDsOnCopy The filterIDsOnCopy to set.
   */
  public void setRemoveIDsOnCopy(boolean removeIDsOnCopy) {
    this.removeIDsOnCopy = removeIDsOnCopy;
  }
  
  /**
   * Set the ID generation pattern.
   * 
   * @param idGenerationPattern The idGeneration pattern.
   */
  public void setIdGenerationPattern(String idGenerationPattern) {
    this.idGenerationPattern = idGenerationPattern;
  }
  
  /**
   * Get the pattern tooltip. Can be overwritten to provide another tooltip.
   *  
   * @return the pattern tooltip. 
   */
  public String getPatternTooltip() {
    return patternTooltip;
  }
  
  /**
   * Set the pattern tooltip which will be shown in the configuration dialog.
   * 
   * @param patternTooltip the pattern tooltip which will be shown in the configuration dialog.
   */
  public void setPatternTooltip(String patternTooltip) {
    this.patternTooltip = patternTooltip;
  }
}
