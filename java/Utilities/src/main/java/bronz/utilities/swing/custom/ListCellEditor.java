package bronz.utilities.swing.custom;

import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;


public class ListCellEditor extends DefaultCellEditor
{
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("rawtypes")
    public ListCellEditor( final List items )
    {
        super( new JComboBox( new ListComboBoxModel( items ) ) );
    }

}
