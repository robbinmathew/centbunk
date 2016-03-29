package bronz.utilities.swing.table;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bronz.utilities.general.ValidationUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DataTableFieldWrapper
{
    private final List NUMBER_CLASSES = Arrays.asList( Integer.class, BigDecimal.class );
    private final DataTableField dataTableField;
    private final String fieldName;
    private final Class fieldType;
    
    public DataTableFieldWrapper( final DataTableField dataTableField,
            final String fieldName, final Class fieldType )
    {
        this.dataTableField = dataTableField;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public DataTableField getDataTableField()
    {
        return this.dataTableField;
    }

    public String getFieldName()
    {
        return this.fieldName;
    }

    public Class getFieldType()
    {
        return this.fieldType;
    }
    
    public int getColNum()
    {
        return this.dataTableField.columnNum();
    }
    
    public int getPreferredWidth()
    {
        return this.dataTableField.preferedWidth();
    }
    
    public boolean isNumber()
    {
        return NUMBER_CLASSES.contains( this.fieldType );
    }
    
    public String getDisplayableName()
    {
    	return ValidationUtil.isNotBlank(this.dataTableField.displayName())?
    			this.dataTableField.displayName(): this.dataTableField.columnName();
    }
}
