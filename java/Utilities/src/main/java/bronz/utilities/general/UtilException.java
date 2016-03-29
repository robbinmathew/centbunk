package bronz.utilities.general;

public class UtilException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public UtilException( final Throwable cause )
    {
        super( cause );
    }
    
    public UtilException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
    
    public UtilException( final String message )
    {
        super( message );
    }

}
