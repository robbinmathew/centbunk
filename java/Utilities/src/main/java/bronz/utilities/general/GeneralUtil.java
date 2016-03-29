package bronz.utilities.general;

import java.io.*;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;
import bronz.utilities.swing.table.DataTableFieldWrapper;

public class GeneralUtil
{
    private static final Logger LOG = Logger.getLogger( GeneralUtil.class );
    private GeneralUtil()
    {
        super();
    }
    
    public static String toString( final String format, final Object... args )
    {
        final StringBuffer message = new StringBuffer( format );
        for ( Object arg : args )
        {
            final int argIndex = message.indexOf( "%%" );
            if ( 0 <= argIndex )
            {
                message.replace( argIndex, argIndex + 2, getStringValue( arg ) );
            }
        }
        return message.toString();
    }
    
    @SuppressWarnings("rawtypes")
    public static String getStringValue( final Object object )
    {
        final String value;
        if ( null == object )
        {
            value = "null";
        }
        else if ( Class.class.isInstance( object ) )
        {
            value = ((Class) object).getCanonicalName();
        }
        else
        {
            value = object.toString();
        }
        return value;
    }
    
    public static String getTimeTaken( final long startTime )
    {
        final StringBuilder time = new StringBuilder();
        final long timeTakenInSecs = (System.currentTimeMillis() - startTime) / 1000;
        time.append( "TIME TAKEN: " );
        time.append( (timeTakenInSecs - (timeTakenInSecs % 60)) / 60 );
        time.append( "min " );
        time.append( timeTakenInSecs % 60 );
        time.append( "sec" );

        return time.toString();
    }

    /**
     * Convert the given list to map with the key as the given fieldName.
     * 
     * @param list The list to be converted.
     * @param fieldName The field name of the key field on the POJO.
     * @param orderByKey True if the map has to be ordered by the key. Else the
     * order in the list will be retained.
     *  
     * @return The Map.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map makeMapFromList( final List list, final String fieldName,
            final boolean orderByKey )
    {
        final Map map;
        if ( orderByKey )
        {
            map = new TreeMap();
        }
        else
        {
            map = new LinkedHashMap();
        }
        
        for ( Object object : list )
        {
            map.put( ReflectionUtil.getFieldValue( object, fieldName ), object );
        }
        return map;
    }

    public static String arrayToString( final Object object, final int width )
    {
        ValidationUtil.checkIfNull( object, "Log array object" );
        final StringBuilder result = new StringBuilder();
        if (object instanceof Object[][])
        {
            final Object[][] array = (Object[][]) object;
            final int arrayLength = array.length;
            for ( int j = 0; j < arrayLength; j++ )
            {
                final Object[] innerArray = array[j];
                result.append( arrayToString( innerArray, width ) );
            }
        }
        else if (object instanceof Object[])
        {
            final Object[] array = (Object[]) object;
            final int arrayLength = array.length;
            for ( int j = 0; j < arrayLength && null != array[j]; j++ )
            {
                final Object value = array[j];
                result.append( String.format( "%1$-" + width + "s", value
                        .toString().trim() ) );
            }
            result.append( "\n" );
        }
        else
        {
            throw new IllegalStateException( "The object is not an array.It is "
                    + object.getClass().getName() );
        }
        return result.toString();
    }
    
    @SuppressWarnings("rawtypes")
    public static int getTotalNumOfRows( final Object object )
    {
        int rows = 0;
        if (null != object && object instanceof Map)
        {
            for ( Object mapValues : ((Map) object).values() )
            {
                rows = rows + getTotalNumOfRows( mapValues );
            }
        }
        else if (null != object && object instanceof List)
        {
            rows = ((List) object).size();
        }
        else
        {
            rows = 1;
        }
        return rows;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue( final Class<T> clazz,
            final Object object )
    {
        return (T) object;
    }
    
    public static <T> Map<Integer, DataTableFieldWrapper> getDataTableFields(
            final Class<T> clazz )
    {
    	ValidationUtil.checkIfNull( clazz, "Class" );
        final Map<Integer, DataTableFieldWrapper> fields =
                new TreeMap<Integer, DataTableFieldWrapper>();
        for ( Field field : clazz.getDeclaredFields() )
        {
            if ( field.isAnnotationPresent( DataTableField.class ) )
            {
                final DataTableField dataTableField = field.getAnnotation(
                        DataTableField.class );
                fields.put( dataTableField.columnNum(),
                        new DataTableFieldWrapper( dataTableField,
                                field.getName(), field.getType() ) );
            }
        }
        return fields;
    }
    
    public static <T> DataTable getDataTable( final Class<T> clazz )
    {
    	ValidationUtil.checkIfNull( clazz, "Class" );
        DataTable value = null;
        if ( clazz.isAnnotationPresent( DataTable.class ) )
        {
            value = clazz.getAnnotation( DataTable.class );
        }
        ValidationUtil.checkIfNull( value, "@DataTable annotation required for class " +
                clazz.getName() );
        return value;
    }
    
    
    
    public static <T> T createInstance( final Class<T> clazz )
    {
        try
        {
            return clazz.newInstance();
        }
        catch ( Exception exception )
        {
            throw new IllegalStateException(
                    "Error while creating instance of " + clazz.getName(),
                    exception );
        }
    }
    
    public static <T extends Object> T getPropValueFromSystemProperties(
            final String propertyName, final Class<T> objectType )
    {
        final Object property = System.getProperty( propertyName );
        ValidationUtil.checkIfNull( property, propertyName );
        return convertObjectToType( property, objectType );
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Object> T getPropValueFromSystemProperties(
            final String propertyName, final Class<T> objectType,
            final T defaultValue )
    {
        final Object property = System.getProperty( propertyName );
        Object value = null;
        if ( null == property )
        {
            value = defaultValue;
        }
        else
        {
            value = convertObjectToType( property, objectType );
        }        
        return (T) value;
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Object> T convertObjectToType(
            final Object originalValue, final Class<T> requiredType )
    {
    	ValidationUtil.checkIfNull( originalValue, "value to be converted" );
        Object value = null;
        try
        {
            if ( Long.class == requiredType )
            {
                value = Long.parseLong( originalValue.toString() );
            }
            else if ( Integer.class == requiredType )
            {
                value = Integer.parseInt( originalValue.toString() );
            }
            else if ( String.class == requiredType )
            {
                value = originalValue.toString();
            }
            else if ( Object.class == requiredType )
            {
                value = originalValue;
            }
            else
            {
                throw new UtilException( "No handling to process values of" +
                        " type " + requiredType );
            }
        }
        catch ( final UtilException exception )
        {
            throw exception;
        }
        catch ( final Exception exception )
        {
            throw new UtilException( "'" + originalValue + "' not of type " +
                    requiredType, exception );
        }
        return (T) value;
    }
    
    public static void readPropsToSystemProps( final String propFileName )
            throws IOException 
    {
        InputStream appPropsStream = null;
        try
        {
            final File propertiesFile = new File( propFileName );
            if(propertiesFile.exists()){
                //Read the properties to the System properties.
                appPropsStream = new FileInputStream( propertiesFile );
                System.getProperties().load( appPropsStream );
            } else {
                throw new FileNotFoundException("The file " + propFileName + " not found!!");
            }

        }
        finally
        {
            if ( null != appPropsStream )
            {
                appPropsStream.close();
            }
        }
    }
    
    public static void readPropsOnClasspathToSystemProps( final String propFileName )
            throws IOException 
    {
        InputStream appPropsStream = null;
        try
        {
            appPropsStream = GeneralUtil.class.getClassLoader().getResourceAsStream(propFileName);
            System.getProperties().load( appPropsStream );
        }
        finally
        {
            if ( null != appPropsStream )
            {
                appPropsStream.close();
            }
        }
    }
    
    public static void redirectStdoutAndErrToLog()
    {
        System.setOut( createLoggingProxy( System.out, false ) );
        System.setErr( createLoggingProxy( System.err, true ) );
        
    }
    
    private static PrintStream createLoggingProxy(
            final PrintStream originalPrintStream, final boolean isError )
    {
        return new PrintStream( originalPrintStream )
            {
                public void print( final String message )
                {
                    originalPrintStream.print( message );
                    if ( isError )
                    {
                        LOG.error( "STDERR:" + message );
                    }
                    else
                    {
                        LOG.info( "STDOUT:" + message );
                    }
                }
            };
    }
    
    public static void analyseJavaEnvironment()
    {
        LOG.info( "JVM heap size:" +
                (Runtime.getRuntime().maxMemory() / 1024) / 1024 );
    }
}
