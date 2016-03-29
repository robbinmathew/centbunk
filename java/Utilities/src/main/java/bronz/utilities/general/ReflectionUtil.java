package bronz.utilities.general;

import java.lang.reflect.Method;
import java.math.BigDecimal;

@SuppressWarnings("rawtypes")
public class ReflectionUtil
{
	public static Object getFieldValue( final Object instance,
            final String fieldName )
    {
        try
        {
            final Method getterMethod = instance.getClass().getMethod(
                    getGetterMethodName( fieldName ) );
            return getterMethod.invoke( instance );
        }
        catch ( Exception exception )
        {
            throw new IllegalStateException( exception );
        }
    }
    
    public static void setFieldValue( final Object instance,
            final String fieldName, final Class fieldType,
            final Object fieldValue )
    {
        try
        {
            final Method setterMethod = instance.getClass().getMethod(
                    getSetterMethodName( fieldName ), fieldType );
            if ( setterMethod == null )
            {
                throw new IllegalStateException( "Incompatible value." +
                        " No setter method found for field " + fieldName +
                        " with arg " + fieldValue.getClass().getName() );
            }
            else
            {
                setterMethod.invoke( instance, convertValueIfReq( fieldValue,
                        fieldType ));
            }
        }
        catch ( IllegalStateException exception )
        {
            throw exception;
        }
        catch ( Exception exception )
        {
            throw new IllegalStateException(
                    "Error while setting the value", exception );
        }
    }
    
    public static Object invokeMethod( final Object instance,
            final String methodName )
    {
        Object response = null;
        try
        {
            final Method method = instance.getClass().getMethod( methodName );
            if ( method == null )
            {
                throw new IllegalStateException( "No method with name " + methodName +
                        " available in class " + instance.getClass().getName() );
            }
            else
            {
                response = method.invoke( instance );
            }
        }
        catch ( IllegalStateException exception )
        {
            throw exception;
        }
        catch ( Exception exception )
        {
            throw new IllegalStateException(
                    "Error while setting the value", exception );
        }
        return response;
    }
    
    public static String getGetterMethodName( final String fieldName )
    {
        final StringBuilder methodName = new StringBuilder( "get" );
        methodName.append( Character.toUpperCase( fieldName.charAt( 0 ) ) );
        methodName.append( fieldName.substring( 1 ) );
        return methodName.toString();
    }
    
    public static String getSetterMethodName( final String fieldName )
    {
        final StringBuilder methodName = new StringBuilder( "set" );
        methodName.append( Character.toUpperCase( fieldName.charAt( 0 ) ) );
        methodName.append( fieldName.substring( 1 ) );
        return methodName.toString();
    }
    
    private static Object convertValueIfReq( final Object origValue,
            final Class fieldType )
    {
        Object value = origValue;
        if ( origValue != null && !origValue.getClass().equals( fieldType ) )
        {
            if ( origValue instanceof String &&
                    BigDecimal.class.equals( fieldType ) )
            {
                value = new BigDecimal( origValue.toString() );
            }
            else if ( origValue instanceof String &&
                    Integer.class.equals( fieldType ) )
            {
                value = new Integer( origValue.toString() );
            }
            else if ( fieldType != null && fieldType.isInstance(origValue) )
            {
                value = origValue;
            }
            else
            {
                throw new IllegalStateException( "No handling to convert " +
                        origValue.getClass() + " to " + fieldType );
            }
        }
        return value;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T getValue( final Class<T> clazz,
            final Object object )
    {
        return (T) object;
    }

}
