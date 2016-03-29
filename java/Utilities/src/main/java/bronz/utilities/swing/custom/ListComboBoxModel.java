package bronz.utilities.swing.custom;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import bronz.utilities.general.ValidationUtil;

public class ListComboBoxModel extends DefaultComboBoxModel
{
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("rawtypes")
    public ListComboBoxModel( final List itemList )
    {
    	ValidationUtil.checkIfNull( itemList, "item list" );
        for ( Object item : itemList )
        {
            this.addElement( item );
        }
    }
}
