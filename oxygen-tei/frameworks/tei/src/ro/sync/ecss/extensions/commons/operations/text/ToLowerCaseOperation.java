package ro.sync.ecss.extensions.commons.operations.text;




import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.WebappCompatible;

/**
 * Provides an operation to convert the text from a selection into lower case text.
 * 
 * @author Costi Vetezi
 */

@WebappCompatible
public class ToLowerCaseOperation extends SelectedTextOperation {
  /**
   * Processes text and makes it lower cased.
   */
	@Override
  protected String processText(String text) {
		return text.toLowerCase();
	}

	/**
	 * No arguments necessary.
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
		return "Replaces the selection with lowercase letters.";
	}
}