package bronz.accounting.bunk.party.exception;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;

public class PartyException extends BunkMgmtException
{
    private static final long serialVersionUID = 1L;

    public PartyException()
    {
        super();
    }
    
    public PartyException( final String message )
    {
        super( message );
    }
    
    public PartyException( final Throwable cause )
    {
        super( cause );
    }

    public PartyException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
