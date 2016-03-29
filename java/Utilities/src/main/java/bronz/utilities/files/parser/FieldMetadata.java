package bronz.utilities.files.parser;

class FieldMetadata
{
    private final String fieldName;
    private final String className;
    private final String defaultValue;
    
    public FieldMetadata( final String fieldName, final String className,
            final String defaultValue )
    {
        super();
        this.fieldName = fieldName;
        this.className = className;
        this.defaultValue = defaultValue;
    }

    public String getClassName()
    {
        return className;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public String getFieldName()
    {
        return fieldName;
    }
}
