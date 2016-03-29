package bronz.utilities.swing.util;

public class UIValidationException extends Exception
{
	/**
	 * Version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an exception.
	 */
	public UIValidationException()
	{
		super();
	}
	
	/**
	 * Constructs an exception with a message.
	 *
	 * @param message The exception message.
	 */
	public UIValidationException( final String message )
	{
		super( message );
	}
	
	/**
	 * Constructs an exception with a message.
	 *
	 * @param cause The exception cause.
	 */
	public UIValidationException( final Throwable cause )
	{
		super( cause );
	}
	
	/**
	 * Constructs an exception with a message and cause.
	 *
	 * @param message The exception message.
	 * @param cause The exception cause.
	 */
	public UIValidationException( final String message, final Throwable cause )
	{
		super( message, cause );
	}
}
