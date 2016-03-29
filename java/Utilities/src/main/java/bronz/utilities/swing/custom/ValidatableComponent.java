package bronz.utilities.swing.custom;

public interface ValidatableComponent
{
	/**
	 * Checks if the value of the component is valid.
	 *
	 * @return The validation errors if any.Else null;
	 */
	String getValidationErrors();
	
	/**
     * Checks if the value of the component is valid.
     *
     * @return The validation errors if any.Else null;
     */
    String getValidationWarnings();
	
}
