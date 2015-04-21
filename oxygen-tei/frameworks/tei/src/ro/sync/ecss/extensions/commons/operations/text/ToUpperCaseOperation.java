package ro.sync.ecss.extensions.commons.operations.text;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.WebappCompatible;

/**
 * Provides an operation to convert the text from a selection into upper case text.
 *
 * @author Costi Vetezi
 */
@API(type=APIType.INTERNAL, src=SourceType.PUBLIC)
@WebappCompatible
public class ToUpperCaseOperation extends SelectedTextOperation {
  /**
   * Processes the selected text and makes it uppercase.
   */
  @Override
  protected String processText(String text) {
    return text.toUpperCase();
  }

  /**
   * No operation arguments necessary.
   */
  @Override
  public ArgumentDescriptor[] getArguments() {
    return null;
  }

  /**
   * @see ro.sync.ecss.extensions.api.Extension#getDescription()
   */
  @Override
  public String getDescription() {
    return "Replaces the selection with uppercase letters.";
  }
}