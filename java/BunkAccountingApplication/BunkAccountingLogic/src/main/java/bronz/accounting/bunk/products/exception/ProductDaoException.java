package bronz.accounting.bunk.products.exception;

public class ProductDaoException extends ProductException
{
    private static final long serialVersionUID = 1L;

    public ProductDaoException()
    {
        super();
    }
    
    public ProductDaoException( final String message )
    {
        super( message );
    }
    
    public ProductDaoException( final Throwable cause )
    {
        super( cause );
    }

    public ProductDaoException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
