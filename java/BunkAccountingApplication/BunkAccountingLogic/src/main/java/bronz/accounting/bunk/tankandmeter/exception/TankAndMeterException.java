package bronz.accounting.bunk.tankandmeter.exception;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;

public class TankAndMeterException extends BunkMgmtException
{
    private static final long serialVersionUID = 1L;

    public TankAndMeterException()
    {
        super();
    }
    
    public TankAndMeterException( final String message )
    {
        super( message );
    }
    
    public TankAndMeterException( final Throwable cause )
    {
        super( cause );
    }

    public TankAndMeterException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
