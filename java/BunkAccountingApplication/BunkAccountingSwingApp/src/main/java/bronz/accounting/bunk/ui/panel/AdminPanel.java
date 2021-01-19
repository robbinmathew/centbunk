package bronz.accounting.bunk.ui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.utilities.general.DateUtil;
import bronz.utilities.general.ValidationUtil;

public class AdminPanel extends BasePublicPanel
        implements ActionListener
{
    /** Serial Number.*/
    private static final long serialVersionUID = 1L;
    private static final Random RANDOM_GENERATOR = new Random();
    private static final Logger LOG = LogManager.getLogger(
    		AdminPanel.class );

    private final JRadioButton byDateRadioButton;
    private final ButtonGroup buttonGroup;
    private final CalendarButton startDate;
    private final JButton deleteButton;
    
    /**
     * Parameterized constructor.
     *
     * @param parentFrame The parent frame.
     * @param todayInteger The int equivalent of today.
     * @param panelSize The screen size.
     *
     * @throws BunkMgmtException If it fails to create panel. 
     */
    public AdminPanel( final HomePage parentFrame ) throws BunkMgmtException
    {
        super( parentFrame, "Admin" );
        this.byDateRadioButton = new JRadioButton( "DELETE BY DATE" );
        this.buttonGroup = new ButtonGroup();
        this.deleteButton = new JButton( "DELETE TRANSACTIONS ON AND AFTER SELECTED DATE" );
        this.startDate = new CalendarButton( 200 , 130, this.parentFrame,
        		this.todayInteger, "DATE", false );
        
        this.uiBuilder.addElement( UiElement.JLABEL, "", 50, 200, 150, 20,
        		"DB:" + AppConfig.DB_SCHEMA_NAME_PROP_NAME.getStringValue(), UiUtil.LABEL_REGULAR_FONT );
        
        /*this.uiBuilder.addElement( UiElement.JBUTTON, TOGGLE_DROP_DOWN_BUTTON,
        		200, 200, 300, 20, "ENABLE AUTO FILL IN TABLES" );*/
        
        this.add( this.byDateRadioButton );
        this.add( this.startDate );
        this.add( this.deleteButton );
        
        this.byDateRadioButton.setBounds(50, 130, 150, 20);
		this.deleteButton.setBounds(50, 160, 400, 20);
        
        this.byDateRadioButton.setContentAreaFilled( false );
        this.buttonGroup.add( this.byDateRadioButton );
        UiUtil.addActionListeners( this );
        this.byDateRadioButton.doClick();
        this.setLayout( null );
        this.setVisible( true );
    }
    
    @Override
    public String getTitleSuffix()
    {
        return "Admin";
    }
    
    public void actionPerformed( final ActionEvent actionEvent)
    {
        if( this.deleteButton.equals( actionEvent.getSource()))
        {
            final int startDate = DateUtil.getIntegerEquivalent(
                this.startDate.getSelectedDate() );
            boolean validationSuccess;
            if( this.byDateRadioButton.isSelected() &&
                    startDate > AppConfig.FIRST_DAY_PROP_NAME.getValue( Integer.class ))
            {
                validationSuccess = true;
            }
            else
            {
                validationSuccess = false;
                throw new UnsupportedOperationException( "No support" );
            }
            this.validate();
            this.repaint();
            
            if( validationSuccess )
            {
                try
                {
                    final String randomKey = String.valueOf( RANDOM_GENERATOR.nextInt( 100000 ) );
                    final Object response = JOptionPane.showInputDialog(
                            this, "This will erase all transactions on and after " +
                            DateUtil.getDateStringWithDay( startDate ) + ".\n THIS DATA WILL NOT BE RECOVERABLE!!\n\n" +
                            		"Enter '" + randomKey + "' below to continue..", "STOP!!!!", JOptionPane.PLAIN_MESSAGE,
                            		null, null, "" );
                    if ( randomKey.equals( response ) )
                    {
                        final Object reason = JOptionPane.showInputDialog(
                                this, "Please enter the reason for this deletion..", "STOP!!!!", JOptionPane.PLAIN_MESSAGE,
                                        null, null, "" );
                        if (reason == null || ValidationUtil.isNullOrEmpty(reason.toString()))
                        {
                        	UiUtil.alertValidationError( this, "Please provide a reason.." );
                        }
                        else
                        {
                        	LOG.info("Deletion reason:" + reason);
                            this.bunkManager.clearTransactions( startDate );
                            
                            UiUtil.confirmUserChoiceWithCustomOptions(this, "All data deleted..!! \n The application will close automatically now. Please start the application again.", "AGREE", "AGREE");
                            LOG.info("DATA_DELETION:Closing the application..");
                            this.parentFrame.dispose();
                            
    	                    final Timer failSafeAppCloseTimer = new Timer(10, new ActionListener(){
    	                        	public void actionPerformed(ActionEvent e){
    	                        		LOG.error("Failed to close the application with <frame>.dispose() " +
    	                        				"method.. Executing System.exit(0)");
    	                        		System.exit(0);
    	                        	}
    							});
    	                    failSafeAppCloseTimer.setInitialDelay(5000);
    	                    failSafeAppCloseTimer.setRepeats(false); 
    	                    failSafeAppCloseTimer.start();
                        }
                    }
                    else
                    {
                        UiUtil.alertValidationError( this, "Wrong value entered. Please try again" );
                    }
                }
                catch ( Exception baseLogicException)
                {
                    UiUtil.alertUnexpectedError( this, baseLogicException );
                }
            }
        }
        /*else if( getComp( TOGGLE_DROP_DOWN_BUTTON, JButton.class ).equals( actionEvent.getSource()))
        {
            AppConfig.AUTO_SUGGEST_DROP_DOWN_ENABLED.setValue( "TRUE" );
        }*/
        this.validate();
        this.repaint();
    }
}
