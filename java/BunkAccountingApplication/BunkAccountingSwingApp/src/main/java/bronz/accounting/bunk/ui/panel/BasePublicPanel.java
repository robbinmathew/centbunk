package bronz.accounting.bunk.ui.panel;

import javax.swing.JLabel;


import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.util.UiUtil;

public abstract class BasePublicPanel extends BasePanel
{
	/** Serial Number.*/
    private static final long serialVersionUID = 1L;
    protected final HomePage parentFrame;
    protected final int todayInteger;
    protected JLabel titleLabel;
    protected final BunkManager bunkManager;
    protected final long launchTime;
    
    public BasePublicPanel( final HomePage parentFrame, final String pageTitle )
            throws BunkMgmtException
    {
        super( HomePage.PUBLIC_PANEL_SIZE );
        this.parentFrame = parentFrame;
        this.todayInteger = parentFrame.getTodayInteger();
        this.titleLabel = new JLabel( pageTitle );
        this.titleLabel.setBounds( 10, 1, panelSize.width - 210, 35);
        this.titleLabel.setFont( UiUtil.SUB_TITLE_BOLD_FONT );
        //this.setBorder(BorderFactory.createTitledBorder(pageTitle));
        this.add( this.titleLabel );
        this.launchTime = System.currentTimeMillis();
        this.bunkManager = this.parentFrame.getBunkManager();
        this.uiBuilder.setParentWindow(this.parentFrame);
    }
    
    public abstract String getTitleSuffix();
    
    public void postRenderTasks(){ }
}
