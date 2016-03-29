package bronz.accounting.bunk.ui.model;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import bronz.utilities.general.ReflectionUtil;

public class CheckBoxTableCellRenderer implements TableCellRenderer
{
    private final int column;
    public CheckBoxTableCellRenderer( final int column )
    {
        this.column = column;
    }
    public Component getTableCellRendererComponent( final JTable table,
            final Object value,final boolean isSelected, final boolean hasFocus,
            int row, int column)
    {
        Component component = null;
        if ( column == this.column && value != null )
        {
            component = new JCheckBox( "", ReflectionUtil.getValue( Boolean.class,
            		value ));
        }
        return component;
    }
    
}
