package bronz.accounting.bunk.ui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.reports.JRReportsCreator;
import bronz.accounting.bunk.reports.model.Report;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.utilities.general.DateUtil;
import bronz.utilities.swing.custom.ListComboBoxModel;

public class ReportsPanel extends BasePublicPanel
        implements ActionListener
{
	/** Serial Number.*/
    private static final long serialVersionUID = 1L;
    private static final int MULTIDATE_PROP_INDEX = 0;
    private static final int REQUIRES_PARTY_DROP_DOWN_PROP_INDEX = 1;
    private static final int REQUIRES_PROD_DROP_DOWN_PROP_INDEX = 2;
    private static final int REQUIRES_TANK_DROP_DOWN_PROP_INDEX = 3;
    private static final int PRIM_PRODS_ONLY_DROP_DOWN_PROP_INDEX = 4;
    
    
    private static final String DAILY_STMT = "DAILY STATEMENT";
    private static final String DSR_STMT = "DAILY SALES REPORT FOR PRODUCTS";
    private static final String CASH_SUMMARY = "CASH SUMMARY";
    private static final String MONTHLY_SUMMARY = "MONTHLY SUMMARY";
    private static final String MONTHLY_SAL_STMT = "MONTHLY SALARY STATEMENT";
    private static final String CREDIT_STATUS = "CREDIT_STATUS";
    private static final String PARTY_CREDIT_STMT = "PARTY STATEMENT(CREDIT ALONE)";
    private static final String PARTY_COMPLETE_STMT = "PARTY STATEMENT(COMPLETE)";
    private static final String STOCK_RECEIPTS = "STOCK RECEIPTS";
    private static final String PROD_SALES = "PRODUCT SALES";
    private static final String TANK_SALES = "TANK SALES";
    private static final String PROD_SALES_STMT = "PRODUCT SALES STATEMENT";
    
    private static final Map<String, List<Boolean>> REPORT_TYPES_MAP = new LinkedHashMap<String, List<Boolean>>();
    
    static
    {
        //REPORT_TYPES_MAP.put( CASH_SUMMARY, Arrays.asList( false, false, false, false, false ) );
        REPORT_TYPES_MAP.put( MONTHLY_SUMMARY, Arrays.asList( false, false, false, false, false ) );
        REPORT_TYPES_MAP.put( MONTHLY_SAL_STMT, Arrays.asList( false, false, false, false, false ) );
        REPORT_TYPES_MAP.put( CREDIT_STATUS, Arrays.asList( false, false, false, false, false ) );
        REPORT_TYPES_MAP.put( DAILY_STMT, Arrays.asList( false, false, false, false, false ) );
        REPORT_TYPES_MAP.put( PARTY_CREDIT_STMT, Arrays.asList( true, true, false, false, false ) );
        REPORT_TYPES_MAP.put( PARTY_COMPLETE_STMT, Arrays.asList( true, true, false, false, false ) );
        REPORT_TYPES_MAP.put( STOCK_RECEIPTS, Arrays.asList( true, false, false, false, false ) );
        REPORT_TYPES_MAP.put( PROD_SALES, Arrays.asList( true, false, true, false, false ) );
        REPORT_TYPES_MAP.put( DSR_STMT, Arrays.asList( true, false, true, false, true ) );
        REPORT_TYPES_MAP.put( TANK_SALES, Arrays.asList( true, false, false, true, false ) );
        REPORT_TYPES_MAP.put( PROD_SALES_STMT, Arrays.asList( true, false, false, false, false ) );
    }
    
    private final JRReportsCreator reportsCreator;
    
    private final JComboBox partyIdComboBox;
    private final JComboBox prodIdComboBox;
    private final JComboBox tankIdComboBox;
    private final ComboBoxModel allProdsModel;
    private final ComboBoxModel primaryProdsModel;
    
    private final CalendarButton startDate;
    private final CalendarButton endDate;
    private final JButton generateButton;
    private final JDialog dialog;
    
    public ReportsPanel( final HomePage parentFrame, final boolean launchAsPopup )
    	throws BunkMgmtException
    {
        super( parentFrame, "Reports" );
        this.reportsCreator = BunkAppInitializer.getInstance().getReportsCreator();
        addReportTypeRadioButtons();
                
        this.generateButton = new JButton( "VIEW REPORT" );
        this.startDate = new CalendarButton( 200 , 475, this.parentFrame, this.todayInteger - 1, "START DATE", false );
        this.endDate = new CalendarButton( 200 , 500, this.parentFrame,	this.todayInteger, "END DATE", false );
        this.partyIdComboBox = new JComboBox( new ListComboBoxModel( this.bunkManager.getPartyList( PartyDao.ALL )));
        this.allProdsModel = new ListComboBoxModel( this.bunkManager.getProductList( ProductDao.ALL));
        this.primaryProdsModel = new ListComboBoxModel( this.bunkManager.getProductList( ProductDao.FUEL_PRODUCTS));
        this.prodIdComboBox = new JComboBox( this.allProdsModel );
        this.tankIdComboBox = new JComboBox( new ListComboBoxModel( this.bunkManager.getTankList(this.todayInteger)));
        
        this.tankIdComboBox.setBounds(150, 400, 350, 20);
        this.prodIdComboBox.setBounds(150, 425, 350, 20);
        this.partyIdComboBox.setBounds(150, 450, 350, 20);
        this.generateButton.setBounds(200, 525, 250, 20);
        
        this.add( this.startDate );
        this.add( this.endDate );
        this.add( this.generateButton );
        this.add( this.prodIdComboBox );
        this.add( this.tankIdComboBox );
        this.add( this.partyIdComboBox );
        UiUtil.addActionListeners( this );
        getComp( PARTY_CREDIT_STMT, JRadioButton.class ).doClick();

        this.setLayout( null );
        this.setVisible( true );
        
        if ( launchAsPopup)
        {
        	this.dialog = new JDialog( parentFrame, "Reports", true);
        	this.dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        	
        	this.setBounds( 0, 0, HomePage.PUBLIC_PANEL_SIZE.width,
            		HomePage.PUBLIC_PANEL_SIZE.height );
        	this.dialog.setBounds( 0, 50, HomePage.PUBLIC_PANEL_SIZE.width,
            		HomePage.PUBLIC_PANEL_SIZE.height );
        	this.dialog.add( this );
        	this.dialog.setVisible( true );
        	this.dialog.setResizable( true );
        	this.dialog.setLayout( null );
        }
        else
        {
        	this.dialog = null;
        }
    }
    
    @Override
    public String getTitleSuffix()
    {
        return "Reports";
    }
    
    private void addReportTypeRadioButtons()
    {
        final ButtonGroup buttonGroup = new ButtonGroup();
        int y = 40;
        //Add report types
        for (String reportType : REPORT_TYPES_MAP.keySet())
        {
            final JRadioButton radio = new JRadioButton( reportType );
            buttonGroup.add( radio );
            radio.setContentAreaFilled( false );
            this.uiBuilder.addElement( radio, reportType, 50, y, 250, 20);
            y = y + 30;
        }
    }
    
    public void actionPerformed( final ActionEvent actionEvent)
    {
        if( this.generateButton.equals( actionEvent.getSource()) )
        {
            final int startDate = DateUtil.getIntegerEquivalent(
                this.startDate.getSelectedDate() );
            final int endDate = DateUtil.getIntegerEquivalent(
                this.endDate.getSelectedDate() );
            boolean validationSuccess = true;
            for (Map.Entry<String, List<Boolean>> entry : REPORT_TYPES_MAP.entrySet())
            {
                if( getComp( entry.getKey(), JRadioButton.class ).isSelected() )
                {
                    if (startDate > this.todayInteger)
                    {
                        UiUtil.alertValidationError( this, "The report for the selected date is not available." +
                        		" Please selected yesterday or any previous date." );
                        validationSuccess = false;
                    }
                    final int firstDay = AppConfig.FIRST_DAY_PROP_NAME.getValue( Integer.class );
                    if (startDate < firstDay)
                    {
                        UiUtil.alertValidationError( this, "The report for the selected date is not available." +
                                " The details are available only from " + DateUtil.getDateString( firstDay + 1 ) +
                                ".Please select date accordingly." );
                        validationSuccess = false;
                    }
                    if (entry.getValue().get( MULTIDATE_PROP_INDEX ))
                    {
                        if (startDate >= endDate)
                        {
                            UiUtil.alertValidationError( this, "Please select a start date before end date." );
                            validationSuccess = false;
                        }
                    }
                    break;
                }
            }
            if( validationSuccess )
            {
                try
                {
                    final Report report;
                    if ( getComp( DAILY_STMT, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createClosingStatement( startDate );
                    }
                    else if ( getComp( CREDIT_STATUS, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createCreditStatusReport( startDate );
                    }
                    /*else if ( getComp( CASH_SUMMARY, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createCashSummaryStatement( startDate );
                    }*/
                    else if ( getComp( MONTHLY_SUMMARY, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createMonthlyCashSummaryStatement(
                        		startDate, this.todayInteger );
                    }
                    else if ( getComp( MONTHLY_SAL_STMT, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createMonthlySalarySummaryStatement(
                        		startDate, this.todayInteger );
                    }
                    else if ( getComp( PARTY_COMPLETE_STMT, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createPartyStatement(
                            ((PartyClosingBalance) this.partyIdComboBox.getSelectedItem()).getId(), startDate, endDate );
                    }
                    else if ( getComp( PARTY_CREDIT_STMT, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createCreditAlonePartyStatement(
                            ((PartyClosingBalance) this.partyIdComboBox.getSelectedItem()).getId(), startDate, endDate );
                    }
                    else if ( getComp( STOCK_RECEIPTS, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createReceiptSummaryReport(startDate, endDate);
                    }
                    else if ( getComp( PROD_SALES, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createProdSalesStatement(
                            ((ProductClosingBalance) this.prodIdComboBox.getSelectedItem()).getProductId(), startDate, endDate);
                    }
                    else if ( getComp( DSR_STMT, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createDSR(
                            ((ProductClosingBalance) this.prodIdComboBox.getSelectedItem()).getProductId(), startDate, endDate);
                    }
                    else if ( getComp( TANK_SALES, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createTankSalesStatement(
                            ((TankClosingStock) this.tankIdComboBox.getSelectedItem()).getTankId(), startDate, endDate);
                    }
                    else if ( getComp( PROD_SALES_STMT, JRadioButton.class ).isSelected() )
                    {
                        report = this.reportsCreator.createProdSalesStatement( startDate, endDate);
                    }
                    else
                    {
                        throw new UnsupportedOperationException("This report is not supported");
                    }
                    if (this.dialog == null)
                    {
                    	this.parentFrame.setPublicPanel(
                    			new ReportViewerPanel( this.parentFrame, this,
                    					report, null ));
                    }
                    else
                    {
                    	this.dialog.remove(this);
                    	this.dialog.validate();
                    	this.dialog.repaint();
                    	final JPanel panel = new ReportViewerPanel(
                    			this.parentFrame, this, report, this.dialog );
                    	panel.setBounds( 0, 0, HomePage.PUBLIC_PANEL_SIZE.width,
                        		HomePage.PUBLIC_PANEL_SIZE.height );
                    	this.dialog.add(panel);
                    	this.dialog.validate();
                    	this.dialog.repaint();
                    }
                }
                catch ( Exception baseLogicException)
                {
                    UiUtil.alertUnexpectedError( this, baseLogicException );
                }
            }
        }
        else
        {
            for (Map.Entry<String, List<Boolean>> entry : REPORT_TYPES_MAP.entrySet())
            {
                if( getComp( entry.getKey(), JRadioButton.class ).equals( actionEvent.getSource() ) )
                {
                    if (entry.getValue().get( MULTIDATE_PROP_INDEX ))
                    {
                        setAsMultiDayReport();
                    }
                    else
                    {
                        setAsSingleDayReport();
                    }
                    this.partyIdComboBox.setEnabled( entry.getValue().get( REQUIRES_PARTY_DROP_DOWN_PROP_INDEX ) );
                    this.prodIdComboBox.setEnabled( entry.getValue().get( REQUIRES_PROD_DROP_DOWN_PROP_INDEX ) );
                    this.tankIdComboBox.setEnabled( entry.getValue().get( REQUIRES_TANK_DROP_DOWN_PROP_INDEX ) );
                    if(entry.getValue().get( REQUIRES_PROD_DROP_DOWN_PROP_INDEX ))
                    {
                    	if(entry.getValue().get( PRIM_PRODS_ONLY_DROP_DOWN_PROP_INDEX ))
                        {
                        	this.prodIdComboBox.setModel(this.primaryProdsModel);
                        }
                    	else
                    	{
                    		this.prodIdComboBox.setModel(this.allProdsModel);
                    	}
                    }
                    break;
                }
            }
        }
        this.validate();
        this.repaint();
    }
    
    private void setAsSingleDayReport()
    {
        this.remove( this.endDate );
        this.startDate.setFieldName( "DATE" );
        
    }
    
    private void setAsMultiDayReport()
    {
        this.add( this.endDate );
        this.startDate.setFieldName( "START DATE" );
        
    }
}
