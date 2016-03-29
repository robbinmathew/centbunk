package bronz.utilities.hibernate;

public class GenericDaoException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    GenericDaoException( final Throwable throwable )
    {
        super( throwable );
    }
    
    GenericDaoException( final String message )
    {
        super( message );
    }
}
