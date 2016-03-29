package bronz.utilities.swing.table;


import java.util.List;

import javax.swing.table.AbstractTableModel;

import bronz.utilities.general.ReflectionUtil;
import bronz.utilities.general.ValidationUtil;

public class GenericReadOnlyTableModel<T> extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private final List<String> columnNames;
	private final List<String> fieldPropNames;
	private final List<T> records;
	
	public GenericReadOnlyTableModel( final List<T> records,
	        final List<String> columnNames, final List<String> fieldPropNames )
	{
		ValidationUtil.checkIfNull( records, "Records" );
		ValidationUtil.checkIfNull( columnNames, "Column Names" );
		ValidationUtil.checkIfNull( fieldPropNames, "fieldPropNames" );
	    if ( columnNames.size() != fieldPropNames.size() )
	    {
	        throw new IllegalStateException( "Column count and no of fields" +
	        		" mismatch" );
	    }
	    this.records = records;
	    this.columnNames = columnNames;
	    this.fieldPropNames = fieldPropNames;
	}
    
	public int getColumnCount()
	{
		return this.columnNames.size();
	}

	public int getRowCount()
	{
		return records.size();
    }

	public String getColumnName( final int col )
	{
		return this.columnNames.get( col );
	}

	public Object getValueAt( final int row, final int col )
	{
	    final T value = this.records.get( row );
		return ReflectionUtil.getFieldValue( value,
		        this.fieldPropNames.get( col ) );
	}

        
	public boolean isCellEditable( final int row, final int col )
	{
	    return false;
	}
}
