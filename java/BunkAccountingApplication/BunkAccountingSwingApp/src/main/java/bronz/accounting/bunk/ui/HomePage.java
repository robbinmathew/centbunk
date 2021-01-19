package bronz.accounting.bunk.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.ui.panel.AdminPanel;
import bronz.accounting.bunk.ui.panel.BasePublicPanel;
import bronz.accounting.bunk.ui.panel.DailyStatementPanel;
import bronz.accounting.bunk.ui.panel.ReportViewerPanel;
import bronz.accounting.bunk.ui.panel.ReportsPanel;
import bronz.accounting.bunk.ui.product.panel.StocksSummaryPanel;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.accounting.bunk.ui.util.UiUtil.ImageType;

/**
 * Home Page.
 */
public class HomePage extends JFrame implements ActionListener
{
	/** Serial Number.*/
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger( HomePage.class );
    private static final String TITLE =
    		"Bunk Accounting Application v4-Alpha-SVN-" +
    				AppConfig.CODE_REVISION.getStringValue();
    public static final Rectangle PUBLIC_PANEL_SIZE;
    private static final Rectangle FRAME_SIZE;
    public static int SCREEN_HIEGHT = 730;
    public static int SCREEN_WIDTH = 1024;
    
    static
    {
        final GraphicsEnvironment desktopEnvironment =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        FRAME_SIZE = desktopEnvironment.getMaximumWindowBounds();
        final Toolkit desktopKit = Toolkit.getDefaultToolkit();
        SCREEN_WIDTH = desktopKit.getScreenSize().width;
        SCREEN_HIEGHT = desktopKit.getScreenSize().height - 30;
        final int xOffset = 1;
        final int yOffset = 103;
        PUBLIC_PANEL_SIZE = new Rectangle( xOffset, yOffset,
                SCREEN_WIDTH - xOffset, SCREEN_HIEGHT - yOffset );
    }

	private static final ImageIcon BACKGROUND_IMAGE =
			UiUtil.getImage( ImageType.TOP_NAV_HOME_PAGE );
	
	private final BunkAppInitializer bunkAppInitializer;
	private BasePublicPanel publicPanel;
	private JPanel homePagePanel;
	private JLabel dateLabel;
	private JMenuBar menuBar;
	
	private JLabel loggedInLabel;
	private JButton stocksButton;
	private JButton enterDailySalesButton;
	private JButton reportsButton;
	private JButton adminButton;
	private int todayInteger;
	
	/**
	 * Title Panel.
	 */
	private class HomePagePanel extends JPanel
	{
		/**
		 * Serial Number.
		 */
		private static final long serialVersionUID = 1L;

		public void paintComponent( final Graphics g )
		{
			super.paintComponent( g );
			if (!BunkAppInitializer.TEST_ENV.equals(
					AppConfig.IS_TEST_ENV.getNullableStringValue()))
			{
				g.drawImage( BACKGROUND_IMAGE.getImage(), 0, 0, this );
			}
		}
	}
	
	private static final HomePage HOME_PAGE_SINGLETON = new HomePage();
    public static HomePage getInstance()
    {
        return HOME_PAGE_SINGLETON;
    }
	
	/**
	 * Default constructor.
	 */
	private HomePage()
	{
		super( TITLE );
		LOG.info("Launching " + TITLE );
		this.bunkAppInitializer = BunkAppInitializer.getInstance();
		
		try
		{
			this.homePagePanel = new HomePagePanel();
			this.menuBar = new JMenuBar();
			this.stocksButton = new JButton( "STOCKS" );
			this.enterDailySalesButton = new JButton( "DAILY STATEMENT" );
			this.reportsButton = new JButton( "REPORTS" );
			this.adminButton = new JButton( "ADMIN" );
			this.dateLabel = new JLabel();
			this.loggedInLabel = new JLabel();
			AppConfig.FIRST_DAY_PROP_NAME.setValue(	Integer.toString(
					this.bunkAppInitializer.getBunkManager().getFirstDate() ) );
			initialize();
			
			configurePrivatePanel();
			//To set the icon on the title bar
			this.setIconImage(UiUtil.getImage(ImageType.BRONZ_LOGO).getImage());
		}
		catch ( BunkMgmtException bunkMgmtException)
		{
			UiUtil.alertFatalError(this, bunkMgmtException);
		}
		
		// Frame settings
		this.setLayout( null );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setBounds( FRAME_SIZE );
		this.homePagePanel.setBounds( FRAME_SIZE );
		this.setPreferredSize(new Dimension(FRAME_SIZE.width, FRAME_SIZE.height));
		this.setVisible( true );
		this.setResizable( false );
		this.getContentPane().add( this.homePagePanel );
		
		this.repaint();
		this.validate();
	}
	
	public void initialize() throws BunkMgmtException
	{
		this.todayInteger = this.bunkAppInitializer.getBunkManager().getTodayDate();
        LOG.info( "DATE : " + this.todayInteger );
        
        setDefaultPublicPanel();
		this.dateLabel.setText( UiUtil.getDateString( "DATE", this.todayInteger) );
		this.repaint();
		this.validate();
	}
	
	/**
	 * Sets the public panel with the given Panel.
	 *
	 * @param publicPanel The panel to be set.
	 */
	public void setPublicPanel( final BasePublicPanel publicPanel )
	{
		if ( null != this.publicPanel )
		{
			UiUtil.removeAllActionListeners( this.publicPanel );
			this.homePagePanel.remove( this.publicPanel );
		}
		this.publicPanel = publicPanel;
		this.publicPanel.setBounds( PUBLIC_PANEL_SIZE );
		this.homePagePanel.add( this.publicPanel );
		this.setTitle( TITLE + " - " + this.publicPanel.getTitleSuffix() );
		this.publicPanel.validate();
		this.validate();
		this.repaint();
		this.publicPanel.postRenderTasks();
		this.validate();
		this.repaint();
	}
	
	public BasePublicPanel getPublicPanel()
    {
        return this.publicPanel;
    }
	
	
	/**
	 * Configures the settings required for private panel.
	 */
	private void configurePrivatePanel()
	{
		this.homePagePanel.setLayout( null );
		this.homePagePanel.setBounds( 0, 0, SCREEN_WIDTH, 103 );
		
		this.loggedInLabel.setText( "LOGGED AS : MANAGER" );
		
		this.menuBar.setBounds( 2, 72, SCREEN_WIDTH - 10, 25 );
		this.menuBar.setOpaque(false);
		this.menuBar.setLayout(null);
		this.getContentPane().add( this.menuBar );
		
		addTabComponents( this.dateLabel, this.stocksButton, 
				this.enterDailySalesButton, this.reportsButton,
		        this.adminButton );
	}
	
	private void addTabComponents( final JComponent... components )
	{
	    int xOffset = 5;
	    for (JComponent comp : components)
	    {
	        comp.setBounds( xOffset, 0, 150, 23 );
	        this.menuBar.add( comp );
            if ( comp instanceof JButton  )
            {
                ((JButton) comp).addActionListener( this );
            }
	        xOffset = xOffset + 155;
	    }
	}
	
	public BunkManager getBunkManager() throws BunkMgmtException
	{
	    return this.bunkAppInitializer.getBunkManager();
	}
	
	public Integer getTodayInteger()
    {
        return this.todayInteger;
    }
	
	public void setDefaultPublicPanel() throws BunkMgmtException
    {
        setPublicPanel( new ReportViewerPanel( this, null,
                this.bunkAppInitializer.getReportsCreator().createMonthlyCashSummaryStatement(
                		this.todayInteger, this.todayInteger ), null) );
    }
	
	/**
	 * Implements actionPerformed method of ActionListener interface.
	 */
	public void actionPerformed( ActionEvent actionEvent )
	{
		BasePublicPanel publicPanel = null;
		try
		{
			if (actionEvent.getSource() == this.stocksButton )
			{
			    this.setTitle( TITLE + " - Stocks" );
				publicPanel = new StocksSummaryPanel( this );
			}
			else if (actionEvent.getSource() == this.enterDailySalesButton)
			{
				publicPanel = new DailyStatementPanel( this );
			}
			else if (actionEvent.getSource() == this.adminButton)
			{
				publicPanel = new AdminPanel( this );
			}
			else if(actionEvent.getSource() == this.reportsButton)
			{
				publicPanel = new ReportsPanel( this, false );
			}
            setPublicPanel( publicPanel );
		}
		catch ( final Exception exception )
		{
			UiUtil.alertUnexpectedError( this, exception );
		}
	}
}
