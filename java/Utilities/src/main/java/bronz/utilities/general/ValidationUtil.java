package bronz.utilities.general;

import java.math.BigDecimal;
import java.util.Collection;

public class ValidationUtil
{
	public static boolean isNotBlank( final String string )
    {
        boolean notBlank = false;
        if ( null != string && !"".equals( string ) )
        {
            notBlank = true;
        }
        return notBlank;
    }
	
	/**
     * Checks if the given property is null.
     * 
     * @param property The property to be checked
     * @param propertyName The name of the property.
     */
    public static void checkIfNull( final Object property,
            final String propertyName )
    {
        if (null == property)
        {
            throw new IllegalArgumentException( propertyName + " is null" );
        }
    }
    
    /**
     * Checks if the given property is null or empty.
     * 
     * @param property The property to be checked
     * @param propertyName The name of the property.
     */
    public static void checkIfNullOrEmpty( final String property,
            final String propertyName )
    {
        if ( isNullOrEmpty( property ) )
        {
            throw new IllegalArgumentException( propertyName +
                    " is null/empty" );
        }
    }

    /**
     * Checks if the given value is null or empty.
     * 
     * @param value The value to be checked.
     * 
     * @return True if the value is null and empty.
     */
    public static boolean isNullOrEmpty( final String value )
    {
        boolean isNull = false;
        if (null == value || "".equals( value ))
        {
            isNull = true;
        }
        return isNull;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isNullOrEmpty( final Collection value )
    {
        boolean isNull = false;
        if (null == value || value.isEmpty())
        {
            isNull = true;
        }
        return isNull;
    }
    
    /**
     * Checks if the given object is a valid number.
     *
     * @param value The value to be checked.
     *
     * @return True if the value is valid number.
     */
    public static boolean isValidNumber ( final Object value )
    {
        boolean isValidNumber = true;
        try
        {
            new BigDecimal( value.toString() );
        }
        catch ( Exception exception)
        {
            isValidNumber = false; 
        }
        return isValidNumber;
    }
    
    /**
     * Checks if the given object is a valid positive number.
     *
     * @param value The value to be checked.
     *
     * @return True if the value is valid number.
     */
    public static boolean isValidPositiveNumber ( final Object value )
    {
        boolean isValidNumber = false;
        try
        {
            final BigDecimal number = new BigDecimal( value.toString() );
            if( number.intValue() > 0 )
            {
                isValidNumber = true;
            }
        }
        catch ( Exception exception)
        {
            isValidNumber = false; 
        }
        return isValidNumber;
    }
}
