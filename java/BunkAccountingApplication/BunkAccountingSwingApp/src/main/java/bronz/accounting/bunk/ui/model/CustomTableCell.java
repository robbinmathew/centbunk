package bronz.accounting.bunk.ui.model;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

public class CustomTableCell extends DefaultCellEditor
{
	/**
	 * .
	 */
	private static final long serialVersionUID = 1L;

	public CustomTableCell()
	{
		super( new JTextField() );
		this.clickCountToStart = 1;
	}
}
