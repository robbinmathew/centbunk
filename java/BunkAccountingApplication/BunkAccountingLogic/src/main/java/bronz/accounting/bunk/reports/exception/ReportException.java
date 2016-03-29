package bronz.accounting.bunk.reports.exception;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;

public class ReportException extends BunkMgmtException
{
    private static final long serialVersionUID = 1L;

    public ReportException()
    {
        super();
    }
    
    public ReportException( final String message )
    {
        super( message );
    }
    
    public ReportException( final Throwable cause )
    {
        super( cause );
    }

    public ReportException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
