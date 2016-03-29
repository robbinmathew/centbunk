package bronz.utilities.swing.table;

import java.awt.Component;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

import bronz.utilities.swing.custom.AutoCompleteDocument;
import bronz.utilities.swing.custom.ListComboBoxModel;

public class ComboBoxTableCellEditor extends AbstractCellEditor implements ActionListener, TableCellEditor, Serializable
{
    private static final long serialVersionUID = 1L;
    private final JComboBox comboBox;
    
    @SuppressWarnings("rawtypes")
	public ComboBoxTableCellEditor(final List items)
    {
        this.comboBox = new JComboBox( new ListComboBoxModel( items ) );
        AutoCompleteDocument.enable( this.comboBox );
        this.comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        // hitting enter in the combo box should stop cellediting (see below)
        this.comboBox.addActionListener(this);
        // remove the editor's border - the cell itself already has one
        ((JComponent) comboBox.getEditor().getEditorComponent()).setBorder(null);
    }
    
    private void setValue(Object value)
    {
        comboBox.setSelectedItem(value);
    }
    
    // Implementing ActionListener
    public void actionPerformed(final ActionEvent e)
    {
        // Selecting an item results in an actioncommand "comboBoxChanged".
        // We should ignore these ones.
        
        // Hitting enter results in an actioncommand "comboBoxEdited"
        if(e.getActionCommand().equals("comboBoxEdited"))
        {
            stopCellEditing();
        }
        System.out.println( "COMBO EVENT********* " + e.getActionCommand() );
    }
    
    // Implementing CellEditor
    public Object getCellEditorValue()
    {
        return comboBox.getSelectedItem();
    }
    
    public boolean stopCellEditing()
    {
        if (comboBox.isEditable()) {
            // Notify the combo box that editing has stopped (e.g. User pressed F2)
            comboBox.actionPerformed(new ActionEvent(this, 0, ""));
        }
        if( comboBox.getSelectedItem() == null || "".equals( comboBox.getSelectedItem() ) )
        {
            fireEditingCanceled();
        }
        else
        {
            fireEditingStopped();
        }
        return true;
    }
    
    // Implementing TableCellEditor
    public Component getTableCellEditorComponent( final JTable table, final Object value, final boolean isSelected,
            final int row, final int column) {
        setValue(value);
        table.addKeyListener(((AutoCompleteDocument) ((JTextComponent) comboBox.getEditor().getEditorComponent() ).getDocument()).getKeyListener());
        return comboBox;
    }
    
    // Implementing TreeCellEditor
//    public java.awt.Component getTreeCellEditorComponent(javax.swing.JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
//        String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, false);
//        setValue(stringValue);
//        return comboBox;
//    }    
}
