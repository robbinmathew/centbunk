package bronz.utilities.custom;

import java.math.BigDecimal;

public class CustomDecimal extends BigDecimal
{
	private static final long serialVersionUID = 1L;
	public static final BigDecimal ZERO = new CustomDecimal( "0" );
	
    public CustomDecimal( final Object value )
    {
        super( value.toString());
    }
    
    public CustomDecimal( final String value )
	{
		super( value.trim() );
	}
	public CustomDecimal( final BigDecimal value )
	{
		super( value.toString().trim() );
	}
	public String toString()
	{
		return this.toPlainString();
	}
}
