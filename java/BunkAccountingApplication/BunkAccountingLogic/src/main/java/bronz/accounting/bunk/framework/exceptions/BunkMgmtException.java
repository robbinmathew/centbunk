package bronz.accounting.bunk.framework.exceptions;

public class BunkMgmtException extends Exception
{
    private static final long serialVersionUID = 1L;

    public BunkMgmtException()
    {
        super();
    }
    
    public BunkMgmtException( final String message )
    {
        super( message );
    }
    
    public BunkMgmtException( final Throwable cause )
    {
        super( cause );
    }

    public BunkMgmtException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
