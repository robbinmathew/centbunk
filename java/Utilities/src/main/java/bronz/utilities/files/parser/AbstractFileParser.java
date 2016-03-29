package bronz.utilities.files.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import bronz.utilities.general.ValidationUtil;

@SuppressWarnings("rawtypes")
public abstract class AbstractFileParser<T, U> implements FileParser<T, U> 
{
    protected String filePath;
	protected Class clazz;
    protected String keyField;
    protected int noOfProperties;
    protected Map<String, FieldMetadata> propertiesMetadata =
        new HashMap<String, FieldMetadata>();
    
    public AbstractFileParser( final Properties properties )
    {
    	ValidationUtil.checkIfNull( properties, "Properties for parsing" );
        this.initializeProperties(
                properties.getProperty( FILE_PATH_PROP_NAME ),
                properties.getProperty( VALUE_CLASSNAME_PROP_NAME ),
                properties.getProperty( KEY_FIELD_PROP_NAME ),
                properties.getProperty( FIELD_NAMES_PROP_NAME, EMPTY_STRING ),
                properties.getProperty( FIELD_TYPE_PROP_NAME, EMPTY_STRING ),
                properties.getProperty( DEFAULT_VALUES_PROP_NAME, EMPTY_STRING ));
    }
    
    public AbstractFileParser( final String filePath, final String className,
            final String keyField, final String fieldNames,
            final String fieldTypes, final String fieldDefaultValues )
    {
        this.initializeProperties( filePath, className, keyField, fieldNames,
                fieldTypes, fieldDefaultValues );
    }

    public abstract List<T> getParsedObjectList();

    public abstract Map<U, T> getParsedObjectMap();
    
    private void initializeProperties( final String filePath,
            final String className, final String keyField,
            final String fieldNames, final String fieldTypes,
            final String defaultValues )
    {
        // TODO Make the validations
        this.filePath = filePath;
        try
        {
            this.clazz = Class.forName( className );
        }
        catch( ClassNotFoundException exception )
        {
            throw new ParserException( exception );
        }
        this.keyField = keyField;
        final String[] fieldNamesArray = fieldNames.split( SEPARATOR );
        final String[] fieldTypesArray = fieldTypes.split( SEPARATOR );
        final String[] defaultValuesArray = defaultValues.split( SEPARATOR );
        this.noOfProperties = fieldNamesArray.length;
        
        for ( int i=0; i < this.noOfProperties; i++ )
        {
            final FieldMetadata metadata = new FieldMetadata(
                    fieldNamesArray[ i ], fieldTypesArray[ i ],
                    defaultValuesArray[ i ] );
            this.propertiesMetadata.put( metadata.getFieldName(), metadata );
        }
    }
}
