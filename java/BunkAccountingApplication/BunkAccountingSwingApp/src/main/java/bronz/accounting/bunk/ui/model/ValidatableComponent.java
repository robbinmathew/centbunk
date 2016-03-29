package bronz.accounting.bunk.ui.model;

public interface ValidatableComponent
{
	/**
	 * Checks if the value of the component is valid.
	 *
	 * @return The validation errors if any.Else null;
	 */
	String isValidValue();
	
}
