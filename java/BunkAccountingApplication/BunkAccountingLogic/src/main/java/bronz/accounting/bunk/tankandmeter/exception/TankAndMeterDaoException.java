package bronz.accounting.bunk.tankandmeter.exception;

public class TankAndMeterDaoException extends TankAndMeterException
{
    private static final long serialVersionUID = 1L;

    public TankAndMeterDaoException()
    {
        super();
    }
    
    public TankAndMeterDaoException( final String message )
    {
        super( message );
    }
    
    public TankAndMeterDaoException( final Throwable cause )
    {
        super( cause );
    }

    public TankAndMeterDaoException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
