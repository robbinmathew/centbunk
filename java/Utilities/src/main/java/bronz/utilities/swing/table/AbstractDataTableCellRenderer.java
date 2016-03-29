package bronz.utilities.swing.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class AbstractDataTableCellRenderer extends DefaultTableCellRenderer
{
    private static final long serialVersionUID = 1L;
    
    private final TableModel tableModel;
    AbstractDataTableCellRenderer( final TableModel tableModel )
    {
        super();
        this.tableModel = tableModel;
    }
    public Component getTableCellRendererComponent( final JTable table,
            final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column)
    {
        final Component component = super.getTableCellRendererComponent( table,
                value, isSelected, hasFocus, row, column);
        if ( this.tableModel.isCellEditable( row, column ) )
        {
            //component.setFont(/* special font*/);
            // you may want to address isSelected here too
            //component.setForeground(/*special foreground color*/);
            component.setBackground( Color.LIGHT_GRAY );
        }
        return component;
    }
}
