package bronz.accounting.bunk.ui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.product.panel.ReceiptDetailPanel;
import bronz.accounting.bunk.ui.product.panel.StockRecepSummaryPanel;
import bronz.accounting.bunk.ui.product.panel.StocksSummaryPanel;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.accounting.bunk.util.EntityTransactionBuilder;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.custom.ListComboBoxModel;
import bronz.utilities.swing.custom.ValidatableTextField.ValidationType;
import bronz.utilities.swing.table.GenericReadOnlyTableModel;
import bronz.utilities.swing.util.SwingUtil;
import bronz.utilities.swing.util.UIValidationException;

public class CashPaymentPanel extends BasePanel implements ActionListener, KeyListener
{
    private static final long serialVersionUID = 1L;
    private static String FORM_PANEL = "FORM PANEL";
    private static String HISTORY_TABLE = "HISTORY TABLE";
    private static final String CLEAR_BUTTON = "CLEAR_BUTTON";
    private static final String BANK_COMBOBOX = "PAID FROM BANK";
    private static final String CURRENT_BAL = "CURRENT BALANCE";
    private static final String CHQ_NO = "CHEQUE NO";
    private static final String TOTAL_AMT = "TOTAL AMT";
    private static final String TRANSFER_CHARGE = "DD/RTGS/NEFT CHARGE";
    private static final String CASH_PAID_TODAY = "AMOUNT PAID";
    private static final String PAY_CASH_BUTTON = "PAY_BUTTON";
    private static final String DETAILS_TEXT = "DETAILS(LOAD/RENT/OTHER)";
    
    public static final int FROM_LOAD_RECIEPT_PANEL_MODE = 1;
    public static final int FROM_SUMMARY_PANEL_MODE = 2;
    
    private final JDialog dialog;
    private final BunkManager bunkManager;
    private final int todayDate;
    private final Integer companyPartyId;
    private final int launchMode;
    
    public CashPaymentPanel( final int todayDate, final int launchMode )
    	throws BunkMgmtException
    {
        super( 0, 0, HomePage.PUBLIC_PANEL_SIZE.width - 200, HomePage.PUBLIC_PANEL_SIZE.height - 100 );
        this.launchMode = launchMode;
        this.todayDate = todayDate;
        this.companyPartyId = AppConfig.COMPANY_PARTY_ID.getValue( Integer.class );
        this.bunkManager = BunkAppInitializer.getInstance().getBunkManager();
        populateDetails();
        UiUtil.addActionListeners( this );
        
        this.dialog =  new JDialog( HomePage.getInstance(),
        		"Make load/rent payment", true);
        dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        dialog.setBounds( 100, 100, HomePage.PUBLIC_PANEL_SIZE.width - 200, HomePage.PUBLIC_PANEL_SIZE.height - 100 );
        dialog.add( this );
        dialog.setVisible( true );
        dialog.setResizable( false );
        dialog.setLayout( null );
    }
    
    private void populateDetails() throws BunkMgmtException
    {
        this.uiBuilder.addElement( UiElement.JLABEL, "", 20, 20, 350, 20, "MAKE PAYMENT TO COMPANY", UiUtil.LABEL_BOLD_FONT );
        final TableModel model = new GenericReadOnlyTableModel<PartyTransaction>(
                this.bunkManager.getPartyTransactionHistory( companyPartyId ),
                Arrays.asList( "DATE", "DETAIL", "DEBIT", "CREDIT", "BALANCE" ),
                Arrays.asList( "dateText", "transactionDetail", "debitText", "creditText", "balance" ) );
        final JTable historyTable = new JTable( model );
        this.uiBuilder.addDetailPanelsWithoutSubOptions(
                FORM_PANEL, new BasePanel(), 50,
                HISTORY_TABLE, historyTable, 50);
        this.uiBuilder.setPreferedWidthToTables( historyTable, historyTable.getWidth(), 15, 55, 10, 10, 10 );
        final PartyClosingBalance companyParty = this.bunkManager.getParty( companyPartyId );
        
        
        this.uiBuilder.addFormFields(FORM_PANEL,
                UiElement.JTEXTFIELD_HORIZONTAL, CURRENT_BAL, ValidationType.DISABLED,
                UiElement.JCOMBOBOX_HORIZONTAL, BANK_COMBOBOX, getBankIdsComboBox(),
                UiElement.JTEXTFIELD_HORIZONTAL, CHQ_NO, ValidationType.DEFAULT,
                UiElement.JCOMBOBOX_HORIZONTAL, DETAILS_TEXT, new ListComboBoxModel(
                		Arrays.asList("<SELECT>", "LOAD", "RENT", "LOAD+RENT", "OTHER") ),
                UiElement.JTEXTFIELD_HORIZONTAL, CASH_PAID_TODAY, ValidationType.NUMERIC_NOT_EMPTY,
                UiElement.JTEXTFIELD_HORIZONTAL, TRANSFER_CHARGE, ValidationType.NUMERIC_NOT_EMPTY,
                UiElement.JTEXTFIELD_HORIZONTAL, TOTAL_AMT, ValidationType.DISABLED,
                UiElement.JBUTTON, PAY_CASH_BUTTON, "SAVE",
                UiElement.JBUTTON, CLEAR_BUTTON, "CLEAR" );
        this.getComp( CASH_PAID_TODAY, JTextField.class ).addKeyListener( this );
        this.getComp( TRANSFER_CHARGE, JTextField.class ).addKeyListener( this );
        
        this.getComp( CURRENT_BAL, JTextField.class ).setText( companyParty.getBalance().toPlainString() );
    }
    
    private DefaultComboBoxModel getBankIdsComboBox() throws BunkMgmtException
    {
        final List<PartyClosingBalance> bankList =
                this.bunkManager.getPartyList( PartyDao.BANKS );
        final DefaultComboBoxModel bankIds =
                new ListComboBoxModel( bankList );
        bankIds.insertElementAt( "<SELECT BANK>" , 0);
        return bankIds;
    }
    
    public void actionPerformed( final ActionEvent actionEvent )
    {
        try
        {
            if ( actionEvent.getSource() == this.componentMap.get( CLEAR_BUTTON ) )
            {
                this.dialog.dispose();
            }
            else if ( actionEvent.getSource() == this.componentMap.get( PAY_CASH_BUTTON ) )
            {
                boolean validationSuccess;
                try
                {
                    validateFields();
                    validationSuccess = true;
                }
                catch ( UIValidationException uiValidationExcepton)
                {
                    validationSuccess = false;
                    UiUtil.alertValidationError( HomePage.getInstance(), uiValidationExcepton.getMessage() );
                }
                if( validationSuccess )
                {
                    try
                    {
                        synchronized( ReceiptDetailPanel.class )
                        {
                            processRequest();
                        }
                        UiUtil.alertOperationComplete( this, "Payment made successfully" );
                        this.dialog.dispose();
                        if (FROM_LOAD_RECIEPT_PANEL_MODE == launchMode){
                        	HomePage.getInstance().setPublicPanel(
                        			new StockRecepSummaryPanel( HomePage.getInstance(),
                                    this.bunkManager, this.todayDate, HomePage.PUBLIC_PANEL_SIZE ) );
                        } else {
                        	HomePage.getInstance().setPublicPanel(
                        			new StocksSummaryPanel( HomePage.getInstance() ) );
                        }
                        
                    }
                    catch ( Exception exception)
                    {
                        UiUtil.alertUnexpectedError( this, exception );
                    }
                }
            }
            this.validate();
            this.repaint();
        }
        catch( Exception exception )
        {
            UiUtil.alertUnexpectedError( this, exception );
        }
    }
    
    private void validateFields() throws UIValidationException
    {
        final StringBuilder errorMessages = new StringBuilder();
        final StringBuilder warningMessages = new StringBuilder();
        final String transferCharge = this.getComp( TRANSFER_CHARGE, JTextField.class ).getText();
        final String cashPaid = this.getComp( CASH_PAID_TODAY, JTextField.class ).getText();
        
        if( ValidationUtil.isValidNumber( cashPaid ) && ValidationUtil.isValidNumber( transferCharge ) )
        {
            final BigDecimal cashPaidAmt = new BigDecimal( cashPaid );
            final BigDecimal transferChargeAmt = new BigDecimal( transferCharge );
            if (cashPaidAmt.compareTo( BigDecimal.ZERO ) <= 0)
            {
                errorMessages.append( "--No cash paid today!!" );
            }
            else
            {
                if (transferChargeAmt.compareTo( CustomDecimal.ZERO ) <= 0 )
                {
                    errorMessages.append( "--Mention the transfer charge for today's payment.\n" );
                }
                if ( 0 == getComp( BANK_COMBOBOX, JComboBox.class ).getSelectedIndex() )
                {
                    SwingUtil.appendMessage(errorMessages, "--Select the bank from which the payment was done.\n" );
                }
                if ( 0 == getComp( DETAILS_TEXT, JComboBox.class ).getSelectedIndex() )
                {
                	SwingUtil.appendMessage(errorMessages, "--Select the payment type.\n" );
                }
                if ( ValidationUtil.isNullOrEmpty( getComp( CHQ_NO, JTextField.class ).getText() ) )
                {
                	SwingUtil.appendMessage(errorMessages, "--Please specify the cheque number or the online transaction id.\n" );
                }
            }
        }
        SwingUtil.validatePanelComponents( this, errorMessages, warningMessages );
    }
    
    private void processRequest() throws BunkMgmtException
    {
        final EntityTransactionBuilder transBuilder = new EntityTransactionBuilder( bunkManager, todayDate );
        final Integer expensePartyId = AppConfig.EXPENSES_PARTY_ID.getValue( Integer.class );
        final BigDecimal cashPaid = new BigDecimal( this.getComp( CASH_PAID_TODAY, JTextField.class ).getText() );
        if ( CustomDecimal.ZERO.compareTo(cashPaid) < 0 )
        {
            final StringBuilder detail = new StringBuilder();
            detail.append( "CASH PAYMENT:" );
            detail.append( "CHQ NO :" );
            detail.append( this.getComp( CHQ_NO, JTextField.class ).getText() );
            detail.append( " DETAIL:" );
            detail.append( this.getComp( DETAILS_TEXT, JComboBox.class ).getSelectedItem() );
            
            
            final PartyClosingBalance bankParty = (PartyClosingBalance)getComp( BANK_COMBOBOX, JComboBox.class ).getSelectedItem();
            final BigDecimal transCharge = new BigDecimal( this.getComp( TRANSFER_CHARGE, JTextField.class ).getText() );
            transBuilder.getPartyTransBuilder().addSwapTrans( bankParty.getId(), companyPartyId, cashPaid,
                        detail.toString(), detail.toString() , "DEBIT_S", "CREDIT_S" );
            if (CustomDecimal.ZERO.compareTo(transCharge) < 0 )
            {
                transBuilder.getPartyTransBuilder().addSwapTrans( bankParty.getId(), expensePartyId, transCharge,
                        detail.toString() + ":TRANSFER CHARGE", detail.toString() + ":TRANSFER CHARGE", "DEBIT_S", "CREDIT_S" );
            }
        }
        this.bunkManager.savePartyTrans( transBuilder.getPartyTransBuilder().getTransactions() );
    }
    
    public void keyTyped( KeyEvent e )
    {
         //No actions on key type events      
    }

    public void keyPressed( KeyEvent e )
    {
        // No actions on key press events
    }

    public void keyReleased( KeyEvent e )
    {
        BigDecimal totalAmt = BigDecimal.ZERO;
        
        final String bankCharge = this.getComp( TRANSFER_CHARGE, JTextField.class ).getText();
        if( ValidationUtil.isValidNumber( bankCharge ) )
        {
            totalAmt = totalAmt.add( new BigDecimal( bankCharge ) );
        }
        final String amountPaid = this.getComp( CASH_PAID_TODAY, JTextField.class ).getText();
        if( ValidationUtil.isValidNumber( amountPaid ) )
        {
            totalAmt = totalAmt.add( new BigDecimal( amountPaid ) );
        }
        this.getComp( TOTAL_AMT, JTextField.class ).setText(
        		BunkUtil.setAsPrice( totalAmt ).toPlainString() );
    }
    
}
