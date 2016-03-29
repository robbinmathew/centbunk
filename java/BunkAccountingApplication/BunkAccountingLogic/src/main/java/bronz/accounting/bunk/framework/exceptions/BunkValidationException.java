package bronz.accounting.bunk.framework.exceptions;

public class BunkValidationException extends BunkMgmtException
{
	/**
	 * Version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an exception.
	 */
	public BunkValidationException()
	{
		super();
	}
	
	/**
	 * Constructs an exception with a message.
	 *
	 * @param message The exception message.
	 */
	public BunkValidationException( final String message )
	{
		super( message );
	}
	
	/**
	 * Constructs an exception with a message.
	 *
	 * @param cause The exception cause.
	 */
	public BunkValidationException( final Throwable cause )
	{
		super( cause );
	}
	
	/**
	 * Constructs an exception with a message and cause.
	 *
	 * @param message The exception message.
	 * @param cause The exception cause.
	 */
	public BunkValidationException( final String message, final Throwable cause )
	{
		super( message, cause );
	}
}
