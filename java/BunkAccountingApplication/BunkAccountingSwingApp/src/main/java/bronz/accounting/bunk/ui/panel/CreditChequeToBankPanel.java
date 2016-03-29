package bronz.accounting.bunk.ui.panel;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.party.model.ChequeTransWrapper;
import bronz.accounting.bunk.ui.party.model.OfficeCashDetailBean;
import bronz.accounting.bunk.ui.util.EntityConverter;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.accounting.bunk.util.EntityTransactionBuilder;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.swing.custom.ListCellEditor;
import bronz.utilities.swing.table.AbstractDataTable;
import bronz.utilities.swing.util.SwingUtil;
import bronz.utilities.swing.util.UIValidationException;

public class CreditChequeToBankPanel extends BasePanel implements ActionListener, TableModelListener
{
	private static final long serialVersionUID = 1L;
	private static final int PANEL_WIDTH = HomePage.SCREEN_WIDTH - 100;
	private static final int PANEL_HEIGHT = HomePage.SCREEN_HIEGHT - 200;
	private static final String SUBMIT_BUTTON = "SUBMIT_BUTTON";
	private static final String TOTAL_CREDITTED_TODAY = "TOTAL_CREDITTED_TODAY";
	private static final String TOTAL_CREDITTED_TODAY_PREFIX = "Total amt credited today:";
	private final Integer defaultBankPartyId = AppConfig.DEFAULT_BANK_PARTY_ID.getValue( Integer.class );
	
	private final BunkManager bunkManager;
    private final JDialog dialog;
    private final AbstractDataTable<ChequeTransWrapper> pendingChequesTable;
    private final OfficeCashDetailBean officeChqBean;
    private final HomePage frame;
    
	public CreditChequeToBankPanel( final HomePage frame, final OfficeCashDetailBean officeChqBean, final List<PartyTransaction> pendingCheques) throws BunkMgmtException
	{
        super( 100, 50, PANEL_WIDTH, PANEL_HEIGHT );
        this.frame = frame;
        this.officeChqBean = officeChqBean;
        this.bunkManager = frame.getBunkManager();
        this.uiBuilder.addElement( UiElement.JBUTTON, SUBMIT_BUTTON, PANEL_WIDTH/2-50, PANEL_HEIGHT - 80, 100, 20, "SUBMIT" );
        this.uiBuilder.addElement( UiElement.JLABEL, "", 10, 10, PANEL_WIDTH, 25, "Pending cheques at office", UiUtil.SUB_TITLE_BOLD_FONT );
        this.uiBuilder.addElement( UiElement.JLABEL, "", 30, 50, PANEL_WIDTH, 20, "Total outstanding amt:" + officeChqBean.getOpening().toPlainString(), UiUtil.LABEL_BOLD_FONT );
        this.uiBuilder.addElement( UiElement.JLABEL, "", 30, PANEL_HEIGHT - 120, PANEL_WIDTH - 50, 30,
        		"<html>-Cheques recieved till yesterday can be creditted today.<br/>" +
        		"<b>-The cheques recieved today can be credited only tomorrow</b></html>",
                UiUtil.LABEL_REGULAR_FONT );
        this.uiBuilder.addElement( UiElement.JLABEL, TOTAL_CREDITTED_TODAY, 30, 70, PANEL_WIDTH, 20, TOTAL_CREDITTED_TODAY_PREFIX + "0.0", UiUtil.LABEL_BOLD_FONT );
        this.pendingChequesTable = new AbstractDataTable<ChequeTransWrapper>( ChequeTransWrapper.class,
                EntityConverter.convertToChequeTrans( pendingCheques ) );
        final JScrollPane pendingChqsScrollPane = new JScrollPane( pendingChequesTable );
        pendingChqsScrollPane.setBounds( 15, 100, PANEL_WIDTH - 40, PANEL_HEIGHT - 230 );
        this.add( pendingChqsScrollPane );
        this.pendingChequesTable.getColumnModel().getColumn( 4 ).setCellEditor( new ListCellEditor( Arrays.asList(
                ChequeTransWrapper.STILL_PENDING, ChequeTransWrapper.CREDITED_TODAY ) ) );
        this.pendingChequesTable.getModel().addTableModelListener( this );
        UiUtil.addActionListeners( this );
        
        this.dialog = new JDialog( frame, "Credit pending cheques", true );
        this.dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        this.dialog.setBounds( 50, 100, PANEL_WIDTH, PANEL_HEIGHT );
        this.dialog.add( this );
        this.dialog.setVisible( true );
        this.dialog.setResizable( true );
        this.dialog.setLayout( null );
        
        this.validate();
        this.repaint();
	}
	
	protected void processPanelRequest() throws BunkMgmtException
    {
        List<ChequeTransWrapper> creditedCheques = new ArrayList<ChequeTransWrapper>();
	    for ( ChequeTransWrapper chqTrans : this.pendingChequesTable.getData() )
        {
            if (ChequeTransWrapper.CREDITED_TODAY.equals( chqTrans.getSelectedForCredit()))
            {
                creditedCheques.add(chqTrans);
            }
        }

        this.officeChqBean.setCreditedCheques(creditedCheques);
    }
	
	protected void validatePanel() throws UIValidationException
    {
	    cancelTableCellEditing( this.pendingChequesTable );
	    BigDecimal recievedToday = CustomDecimal.ZERO;
        for ( ChequeTransWrapper chqTrans : this.pendingChequesTable.getData() )
        {
            if (ChequeTransWrapper.CREDITED_TODAY.equals( chqTrans.getSelectedForCredit())) {
                recievedToday = recievedToday.add( chqTrans.getAmount() );
            }
        }
        if (CustomDecimal.ZERO.compareTo( recievedToday ) >= 0)
        {
            throw new UIValidationException( "Nothing to update. No cheques selected for credit." );
        }
	    
	    SwingUtil.validatePanelComponents( this );
    }
	
	/**
     * Implements the method of TableChangedListener interface.
     */
    public void tableChanged( final TableModelEvent tableModelEvent)
    {
        BigDecimal recievedToday = CustomDecimal.ZERO;
        for ( ChequeTransWrapper partyWrapper : this.pendingChequesTable.getData() )
        {
            if (ChequeTransWrapper.CREDITED_TODAY.equals( partyWrapper.getSelectedForCredit())) {
                recievedToday = recievedToday.add( partyWrapper.getAmount() );
            }
        }
        this.getComp( TOTAL_CREDITTED_TODAY, JLabel.class ).setText( TOTAL_CREDITTED_TODAY_PREFIX +
        		BunkUtil.setAsPrice( recievedToday).toPlainString());
        this.validate();
        this.repaint();
    }
	
	public void actionPerformed( final ActionEvent actionEvent )
	{
		if ( actionEvent.getSource().equals( this.getComp( SUBMIT_BUTTON, JButton.class ) ) )
		{
		    if (submitPanel())
		    {
		        UiUtil.alertOperationComplete( this, "Cheque credits will be saved with the Daily statement.");
		        this.frame.validate();
		        this.frame.repaint();
	            this.dialog.dispose();
		    }
		}
		this.validate();
		this.repaint();
	}
}
