package bronz.accounting.bunk.ui.panel;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.ui.util.SwingUiBuilder;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.utilities.general.GeneralUtil;
import bronz.utilities.swing.custom.LabeledComponent;
import bronz.utilities.swing.util.UIValidationException;

public class BasePanel extends JPanel
{
	/** Serial Number.*/
    private static final long serialVersionUID = 1L;
    public static final String EMPTY_STRING = "";
    
    protected final SwingUiBuilder uiBuilder;
    protected final Map<String, JComponent> componentMap;
    protected Rectangle panelSize;
    
     
    
    public BasePanel( final Rectangle panelSize )
    {
        this.componentMap = new HashMap<String, JComponent>(){
        	/** Serial Number.*/
            private static final long serialVersionUID = 1L;
            public JComponent get( final Object key )
            {
                JComponent component = super.get( key );
                if ( component != null &&
                        component instanceof LabeledComponent )
                {
                    component =
                        ((LabeledComponent) component).getOriginalComp();
                }
                return component;
            }
        };
        this.setLayout( null );
        this.setOpaque( false );
        this.setVisible( true );
        this.uiBuilder = new SwingUiBuilder( this, panelSize, componentMap );
        setBounds( panelSize );
    }
    
    public BasePanel()
    {
        this.componentMap = new HashMap<String, JComponent>();
        this.setLayout( null );
        this.setOpaque( false );
        this.setVisible( true );
        this.panelSize = getBounds();
        this.uiBuilder = new SwingUiBuilder( this, panelSize, componentMap );
    }
    
    public BasePanel( final int x, final int y, final int width,
            final int hieght )
    {
        this( new Rectangle( x, y, width, hieght) );
    }
    
    public void setBounds( final Rectangle panelSize )
    {
        this.panelSize = panelSize;
        super.setBounds( this.panelSize );
    }
    
    public void setBounds( final int x, final int y, final int width,
            final int hieght )
    {
        this.panelSize = new Rectangle( x, y, width, hieght);
        super.setBounds( x, y, width, hieght );
    }
    
    public <T> T getComp( final String id, final Class<T> type )
    {
        return GeneralUtil.getValue( type, this.componentMap.get( id ) );
    }
    
    public int getTableHeight( final String id )
    {
    	final JTable table = getComp(id, JTable.class);
    	if (null == table)
    	{
    		return 0;
    	}
    	else
    	{
    		return (table.getRowCount() * table.getRowHeight()) + 16;
    	}
    }
    
    public void cancelTableCellEditing( final String... tableIds ) throws UIValidationException
    {
        boolean cancelledEditing = false;
        for ( String tableId : tableIds )
        {
            final JTable table = getComp( tableId, JTable.class );
            if ( table != null && table.isEditing() )
            {
                table.getCellEditor().stopCellEditing();
                cancelledEditing = true;
            }
        }
        if (cancelledEditing)
        {
            throw new UIValidationException( "Tables were being editted,but have been updated now. Please verify the values and try again." );
        }
    }
    
    public void cancelTableCellEditing( final JTable... tables ) throws UIValidationException
    {
        boolean cancelledEditing = false;
        for ( final JTable table : tables )
        {
            if ( table != null && table.isEditing() )
            {
                table.getCellEditor().stopCellEditing();
                cancelledEditing = true;
            }
        }
        if (cancelledEditing)
        {
            throw new UIValidationException( "Tables were being editted,but have been updated now. Please verify the values and try again." );
        }
    }
    
    protected void validatePanel() throws UIValidationException
    {
    }
    
    protected void processPanelRequest() throws BunkMgmtException
    {
    }
    
    protected boolean submitPanel()
    {
        boolean validationSuccess;
        try
        {
            validatePanel();
            validationSuccess = true;
        }
        catch ( UIValidationException uiValidationExcepton)
        {
            validationSuccess = false;
            UiUtil.alertValidationError( this, uiValidationExcepton.getMessage() );
        }
        if( validationSuccess )
        {
            try
            {
                synchronized( BasePanel.class )
                {
                    processPanelRequest();
                    return true;
                }
            }
            catch ( Exception exception)
            {
                UiUtil.alertUnexpectedError( this, exception );
            }
        }
        return false;
    }
    
    
}
