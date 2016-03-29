package bronz.utilities.swing.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import bronz.utilities.general.GeneralUtil;
import bronz.utilities.general.ReflectionUtil;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.util.SwingUtil;

public class AbstractDataTableModel<T> extends AbstractTableModel
{
    private static final long serialVersionUID = -6755542631918927448L;
    protected final List<T> data;
    protected final Class<T> dataModelClass;
    protected int rowCount;
    protected final int colCount;
    protected final JTable table;
    private String[] columnNames;
    protected final Map<Integer, DataTableFieldWrapper> colMetaData;
    protected final DataTable dataTable;
    protected String idColName = "";
    @SuppressWarnings("rawtypes")
    protected final List ids = new ArrayList();    
    
    AbstractDataTableModel( final JTable table, final List<T> data,
            final Class<T> dataModelClass, final DataTable dataTable,
            final Map<Integer, DataTableFieldWrapper> colMetaData,
            final int minNoOfRows )
    {
        super();
        this.table = table;
        this.dataModelClass = dataModelClass;
        this.colMetaData = colMetaData;
        this.colCount = this.colMetaData.size();
        this.columnNames = new String[ this.colCount ];
        this.dataTable = dataTable;
        for ( DataTableFieldWrapper metadata : this.colMetaData.values() )
        {
            this.columnNames[ metadata.getDataTableField().columnNum() ] =
                metadata.getDataTableField().columnName();
            if ( metadata.getColNum() == dataTable.idCol() )
            {
                this.idColName = metadata.getDisplayableName();
            }
        }
        this.data = data;
        this.rowCount = minNoOfRows;
    }
    
    public List<T> getData()
    {
        return this.data;
    }
    
    public String getColumnName( final int col )
    {
        return this.columnNames[ col ];
    }
    

    public int getColumnCount()
    {
        return this.colCount;
    }

    public int getRowCount()
    {
        return this.rowCount;
    }

    public Object getValueAt( int rowIndex, int columnIndex )
    {
        Object value = null;
        try
        {
            final T rowModel = getRow( rowIndex, false );
            if ( null != rowModel )
            {
                final DataTableFieldWrapper columnMetaData =
                    this.colMetaData.get( columnIndex );
                value = ReflectionUtil.getFieldValue( rowModel,
                        columnMetaData.getFieldName() );
            }
        }
        catch ( Exception exception )
        {
            throw new IllegalStateException( exception );
        }
        return value;
    }
    
    @SuppressWarnings("unchecked")
    public void setValueAt( final Object value, final int rowIndex,
            final int columnIndex )
    {
        final DataTableFieldWrapper columnMetaData =
            this.colMetaData.get( columnIndex );
        final Object oldValue = getValueAt( rowIndex, columnIndex );
        if ( null != oldValue )
        {
            this.ids.remove( oldValue );
        }
        
        if ( validateValue( value, columnMetaData, columnIndex ) )
        {
            final T rowModel = getRow( rowIndex, true );
            if ( this.dataTable.idCol() == columnIndex )
            {
                this.ids.add( value );
            }
            if ( null != rowModel )
            {
            	ReflectionUtil.setFieldValue( rowModel,
                        columnMetaData.getFieldName(),
                        columnMetaData.getFieldType(), value );
                if ( this.data.size() <= rowIndex )
                {
                    this.data.add( rowModel );
                }
                fireTableRowsUpdated( rowIndex, rowIndex );
            }
            if ( !this.dataTable.fixNoOfRows() && rowIndex == this.rowCount - 1)
            {
                this.rowCount++;
                fireTableRowsInserted( this.rowCount - 1 , this.rowCount );
            }
        }
    }
    
    private boolean validateValue( final Object value,
            final DataTableFieldWrapper metadata, final int columnIndex )
    {
        boolean valid = true;
        if ( this.dataTable.uniqueIdCol() &&
                this.dataTable.idCol() == columnIndex &&
                this.ids.contains( value ) )
        {
            valid = false;
            SwingUtil.notifyInfoMsg( this.table, "The table already contains " +
            		"the mentioned " + this.idColName  + " value." );
        }
        if ( null == value && !metadata.getDataTableField().allowNull() )
        {
            valid = false;
        }
        else if ( metadata.isNumber() && !ValidationUtil.isValidNumber( value ) )
        {
            valid = false;
            SwingUtil.notifyInfoMsg( this.table, "Please specify a valid number for" +
                    " field " + metadata.getDisplayableName() );
        }
        else if ( "".equals( value ) &&
                !metadata.getDataTableField().allowEmpty() )
        {
            valid = false;
            SwingUtil.notifyInfoMsg( this.table, "Please specify a value for" +
                    " field " + metadata.getDisplayableName() );
        }
        return valid;
    }
    
    private T getRow( final int rowIndex, final boolean createIfNull )
    {
        T rowInstance = null;
        if ( rowIndex < this.data.size() )
        {
            rowInstance = this.data.get( rowIndex );
        }
        else
        {
            if ( createIfNull )
            {
                rowInstance = GeneralUtil.createInstance( this.dataModelClass );
            }
            //Fill default values
            /*for ( DataTableFieldWrapper metadata : this.colMetaData.values() )
            {
                GeneralUtil.setFieldValue( rowInstance,
                        metadata.getFieldName(),
                        metadata.getFieldType(),
                        metadata.getDataTableField().defaultValue() );
            }*/
            
        }
        return rowInstance;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public boolean isCellEditable( int rowIndex, int columnIndex )
    {
        boolean isEditable = this.colMetaData.get(
                columnIndex ).getDataTableField().isEditable();
        
        //Override if the bean says if the cell is non editable.
        if ( null != this.dataTable.editableColumnListMethod() &&
                !"".equals( this.dataTable.editableColumnListMethod() ) && null != getRow( rowIndex, true ) )
        {
            final T rowModel = getRow( rowIndex, true );
            final List<Integer> editableColList = (List<Integer>) ReflectionUtil.invokeMethod( rowModel,
                    this.dataTable.editableColumnListMethod() );
            if ( null != editableColList )
            {
                isEditable = editableColList.contains( columnIndex );
            }
        }
        
        if ( isEditable )
        {
            if ( rowIndex > this.data.size() )
            {
                isEditable = false;
            }
            else if ( null == getRow( rowIndex, false ) && !(columnIndex == 0 )  )
            {
                isEditable = false;
                SwingUtil.notifyInfoMsg( this.table, "Select " +
                        this.idColName + " for this row" );
            }
        }
        return isEditable;
    }
    
    public void fireTableDataChanged()
    {
    	super.fireTableDataChanged();
    }
}
