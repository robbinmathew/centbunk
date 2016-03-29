package bronz.utilities.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import bronz.utilities.swing.table.AbstractDataTable;

@SuppressWarnings("unchecked")
public class TableWithinTableCell  extends AbstractCellEditor
	implements TableCellRenderer, TableCellEditor
{
	private static final long serialVersionUID = 1L;
	private static final ThreadLocal<CellTable> LOCAL = new ThreadLocal<CellTable>();
	
	private final List<String> colNames;
	private final CellTable cellTableRenderer;
	private final int colOneWidth;
	public TableWithinTableCell( final List<String> colNames, final int colOneWidth )
	{
		this.colNames = colNames;
		this.colOneWidth = colOneWidth;
		this.cellTableRenderer = new CellTable(colNames, this.colOneWidth);
	}
	
	public Object getCellEditorValue() {
		final CellTable editor = LOCAL.get();
		if (editor != null)
		{
			return editor.getDetails();
		}
		else
		{
			return null;
		}
	}
	
	private CellTable getDefaultEditor(final Object value, final JTable parentTable)
	{
		final CellTable cellTableEditor = new CellTable(this.colNames, this.colOneWidth,
				(Map<String, Object>) value, parentTable);
		cellTableEditor.addFocusListener(new FocusAdapter()
			{
				public void focusLost(final FocusEvent e)
				{
					if (e.getComponent() == cellTableEditor)
					{
						final CellEditor editor = cellTableEditor.getCellEditor();
						if (editor != null && editor instanceof DefaultCellEditor )
						{
							final DefaultCellEditor cellEditor = (DefaultCellEditor) editor;
							
							if (e.getOppositeComponent() != cellEditor.getComponent())
							{
								cellEditor.stopCellEditing();
								fireEditingStopped();
							}
						}
					}
				}
			});
		return cellTableEditor;
	}
	
	public Component getTableCellRendererComponent(
            final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int rowIndex, final int colIndex)
	{
		this.cellTableRenderer.setDetails(value);
		return prepareComp(table, isSelected, rowIndex, this.cellTableRenderer);
	}
	
	public Component getTableCellEditorComponent(final JTable table,
			final Object value, final boolean isSelected, final int rowIndex,
			final int colIndex) {
		LOCAL.set(getDefaultEditor(value, table));
		return prepareComp(table, isSelected, rowIndex, LOCAL.get());
	}
	
	private CellTable prepareComp(final JTable parentTable,
			final boolean isSelected, final int rowIndex, final CellTable cellTable)
	{
		if (isSelected)
        {
			cellTable.setBackground(new Color(0xd1, 0xe0, 0xff));
        }
    	else
    	{
    		if (rowIndex % 2 != 0)
            {
    			cellTable.setBackground(new Color(0xf0, 0xf5, 0xff));
            }
        	else
            {
        		cellTable.setBackground(parentTable.getBackground());
            }
    	}
		return cellTable;
	}
}

@SuppressWarnings("unchecked")
class CellTable extends JTable
{
	private static final long serialVersionUID = 1L;

	private Map<String, Object> details;
	private final List<String> colNames;
	private final JTable parentTable;
	private final int colOneWidth;
	CellTable(final List<String> colNames, final int colOneWidth, 
			final Map<String, Object> details, final JTable parentTable)
	{
		super(colNames.size(), 2);
		this.details = details;
		this.colNames = colNames;
		this.parentTable = parentTable;
		this.colOneWidth = colOneWidth;
		this.setOpaque(true);
		this.setRowHeight(15);
		for (int i =0; i < colNames.size(); i++)
		{
			this.setValueAt(colNames.get(i), i, 0);
		}
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}
	
	CellTable(final List<String> colNames, final int colOneWidth)
	{
		this(colNames, colOneWidth, null, null);
	}
	
	public void setBounds(final int x, final int y, final int width,
			final int height)
	{
		super.setBounds(x, y, width, height);
		final Float portion = width / 100f;
        getColumnModel().getColumn( 0 ).setPreferredWidth( this.colOneWidth * portion.intValue() );
        getColumnModel().getColumn( 1 ).setPreferredWidth( (100 - this.colOneWidth) * portion.intValue() );
	}
	@SuppressWarnings("rawtypes")
	public void setValueAt(Object value, final int rowIndex,
			final int colIndex)
	{
		if (value != null && value instanceof String )
		{
			value = value.toString().toUpperCase();
		}
		if (colIndex == 1 && this.details != null)
		{
			final String key = this.colNames.get(rowIndex);
			if (key != null)
			{
				this.details.put(key, value);
			}
		}
		super.setValueAt(value, rowIndex, colIndex);
		if (null != this.parentTable &&  this.parentTable instanceof AbstractDataTable )
		{
			((AbstractDataTable) this.parentTable).fireTableDataChanged();
		}
	}
	
	public Object getValueAt( final int rowIndex, final int colIndex)
	{
		if (colIndex == 1)
		{
			if (rowIndex < this.colNames.size() && this.details != null)
			{
				final String key = this.colNames.get(rowIndex);
				if (key != null)
				{
					if (!this.details.containsKey(key))
					{
						this.details.put(key, "");
					}
					return this.details.get(key);
				}
				return null;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return super.getValueAt(rowIndex, colIndex);
		}
	}
	
	public boolean isCellEditable(final int rowIndex, final int colIndex)
	{
		if (colIndex == 1 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Map<String, Object> getDetails()
	{
		return this.details;
	}
	
	public void setDetails(final Object value)
	{
		this.details = (Map<String, Object>) value;
	}
	
}
