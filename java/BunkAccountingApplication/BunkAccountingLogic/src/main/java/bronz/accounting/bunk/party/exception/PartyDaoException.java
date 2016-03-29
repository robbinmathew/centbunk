package bronz.accounting.bunk.party.exception;

public class PartyDaoException extends PartyException
{
    private static final long serialVersionUID = 1L;

    public PartyDaoException()
    {
        super();
    }
    
    public PartyDaoException( final String message )
    {
        super( message );
    }
    
    public PartyDaoException( final Throwable cause )
    {
        super( cause );
    }

    public PartyDaoException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
