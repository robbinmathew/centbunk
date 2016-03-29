package bronz.accounting.bunk.products.exception;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;

public class ProductException extends BunkMgmtException
{
    private static final long serialVersionUID = 1L;

    public ProductException()
    {
        super();
    }
    
    public ProductException( final String message )
    {
        super( message );
    }
    
    public ProductException( final Throwable cause )
    {
        super( cause );
    }

    public ProductException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
