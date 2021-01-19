package bronz.utilities.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bronz.utilities.general.GeneralUtil;
import bronz.utilities.general.ReflectionUtil;
import bronz.utilities.general.UtilException;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.custom.ValidatableComponent;

public class AbstractDataTable<T> extends JTable implements ValidatableComponent
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(
            AbstractDataTable.class );
    
    protected final Class<T> dataModelClass;
    protected final DataTable dataTable;
    protected final Map<Integer, DataTableFieldWrapper> colMetaData;
        
    public AbstractDataTable( final Class<T> dataModelClass )
    {
        super();
        try
        {
            this.dataModelClass = dataModelClass;
            this.dataTable = GeneralUtil.getDataTable( dataModelClass );
            this.colMetaData = GeneralUtil.getDataTableFields(
                    this.dataModelClass );
            this.setModel( new AbstractDataTableModel<T>( this,
                    new LinkedList<T>(), this.dataModelClass, this.dataTable,
                    this.colMetaData, this.dataTable.minNoOfRows() ) );
            putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            validateTableFields();
            //this.setcDefaultRenderer( Object.class, new AbstractDataTableCellRenderer(
            //        this.getModel() ));
        }
        catch ( Exception exception )
        {
            throw new UtilException( exception );
        }
    }
    
    public AbstractDataTable( final Class<T> dataModelClass, final List<T> data)
    {
        super();
        try
        {
            this.dataModelClass = dataModelClass;
            this.dataTable = GeneralUtil.getDataTable( dataModelClass );
            this.colMetaData = GeneralUtil.getDataTableFields(
                    this.dataModelClass );
            final int noOfRows;
            if ( this.dataTable.fixNoOfRows() )
            {
                noOfRows = data.size();
            }
            else
            {
                if (data == null)
                {
                    noOfRows = this.dataTable.minNoOfRows();
                }
                else
                {
                    if(this.dataTable.minNoOfRows() > data.size() )
                    {
                        noOfRows = this.dataTable.minNoOfRows();
                    }
                    else
                    {
                        noOfRows = data.size() + 1;
                    }
                }
                
            }
            
            this.setModel( new AbstractDataTableModel<T>( this, data,
                    this.dataModelClass, this.dataTable, this.colMetaData, noOfRows ) );
            putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            validateTableFields();
        }
        catch ( Exception exception )
        {
            throw new UtilException( exception );
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<T> getData()
    {
        return ((AbstractDataTableModel<T>) getModel()).getData() ;
    }
    
    public void setBounds( final int x, final int y, final int width,
            final int height)
    {
        super.setBounds( x, y, width, height );
        final int portion = width / 100;
        int widthSum = 0;
        for( DataTableFieldWrapper field : this.colMetaData.values() )
        {
            final int colNum = field.getColNum();
            final TableColumn column = this.getColumnModel().getColumn( colNum);
            column.setPreferredWidth( field.getPreferredWidth() * portion );
            widthSum = widthSum + field.getPreferredWidth();
        }
        if ( widthSum != 100 )
        {
            LOG.error( "Sum of column width is expected to be 100, now its " +
                    widthSum );
        }
    }
    
    public Component prepareRenderer( final TableCellRenderer renderer,
    		final int rowIndex, final int colIndex)
    {
    	final Component c = super.prepareRenderer(renderer, rowIndex, colIndex);
    	
    	if (isCellSelected(rowIndex, colIndex))
        {
        	c.setBackground(new Color(0xd1, 0xe0, 0xff));
        }
    	else
    	{
    		if (rowIndex % 2 != 0)
            {
            	c.setBackground(new Color(0xf0, 0xf5, 0xff));
            }
        	else
            {
            	c.setBackground(getBackground());
            }
    	}
        
        return c;
	}


    public String getValidationErrors()
    {
        final String errorValidationMeth =
                this.dataTable.errorValidationMethod();
        String valErrors = null;
        if ( ValidationUtil.isNotBlank( errorValidationMeth ) )
        {
            final StringBuilder buffer = new StringBuilder();
            for ( T value : getData() )
            {
                appendMessage( buffer, ReflectionUtil.invokeMethod( value,
                        errorValidationMeth ) );
            }
            valErrors = buffer.toString();
        }
        return valErrors;
    }
    
    private static void appendMessage( final StringBuilder buffer,
            final Object message )
    {
        if ( buffer.length() > 0 && ValidationUtil.isNotBlank( message.toString() ))
        {
            buffer.append( "\n" );
        }
        buffer.append( message );
    }

    public String getValidationWarnings()
    {
        final String warningValidationMeth =
            this.dataTable.warningValidationMethod();
        String valErrors = null;
        if ( ValidationUtil.isNotBlank( warningValidationMeth ) )
        {
            final StringBuilder buffer = new StringBuilder();
            for ( T value : getData() )
            {
                appendMessage( buffer, ReflectionUtil.invokeMethod( value,
                        warningValidationMeth ) );
            }
            valErrors = buffer.toString();
        }
        return valErrors;
    }
    
    @SuppressWarnings("unchecked")
	public void fireTableDataChanged()
    {
    	((AbstractDataTableModel<T>) this.dataModel).fireTableDataChanged();
    }
    
    private void validateTableFields()
    {
    	for (DataTableFieldWrapper wrapper : this.colMetaData.values())
    	{
    		if (wrapper.getDataTableField().isEditable() &&
    				wrapper.getDataTableField().columnName().contains("<html>")	&&
    				ValidationUtil.isNullOrEmpty(wrapper.getDataTableField().displayName()))
    		{
    			LOG.error("Warning in class " + this.dataModelClass.getName() +
    					":Error messages may contain '" + wrapper.getDataTableField().columnName() +
    					"'. Provide 'displayName' to avoid this.");
    		}
    	}
    }
}
