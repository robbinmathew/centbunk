package bronz.accounting.bunk.ui.panel;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.party.model.PartyWrapper;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.ui.util.EntityConverter;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.swing.custom.ListCellEditor;
import bronz.utilities.swing.table.AbstractDataTable;
import bronz.utilities.swing.util.SwingUtil;
import bronz.utilities.swing.util.UIValidationException;

public class EditPartiesPanel extends BasePanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final int PANEL_WIDTH = HomePage.SCREEN_WIDTH - 100;
	private static final int PANEL_HEIGHT = HomePage.SCREEN_HIEGHT - 100;
	private static final String SUBMIT_BUTTON = "SUBMIT_BUTTON";
	
	private final BunkManager bunkManager;
	private final List<Party> parties;
    private final JDialog dialog;
    private final ActionListener actionListener;
    private final AbstractDataTable<PartyWrapper> employeesTable;
    private final AbstractDataTable<PartyWrapper> bankTable;
    private final AbstractDataTable<PartyWrapper> partiesTable;
    
    
	public EditPartiesPanel( final HomePage frame, final ActionListener actionListener) throws BunkMgmtException
	{
        super( 50, 50, PANEL_WIDTH, PANEL_HEIGHT );
        this.actionListener = actionListener;
        this.bunkManager = frame.getBunkManager();
        this.parties = bunkManager.getAllParties();
        final List<PartyWrapper> employees = new ArrayList<PartyWrapper>();
        final List<PartyWrapper> banks = new ArrayList<PartyWrapper>();
        final List<PartyWrapper> creditParties = new ArrayList<PartyWrapper>();
        EntityConverter.convertToPartyWrapper( this.parties, this.bunkManager.getPartyList( PartyDao.ALL ),
                employees, banks, creditParties );
        this.uiBuilder.addElement( UiElement.JBUTTON, SUBMIT_BUTTON, PANEL_WIDTH-160, PANEL_HEIGHT - 90, 100, 30, "SUBMIT" );
        this.uiBuilder.addElement( UiElement.JLABEL, "", 10, 10, PANEL_WIDTH, 20, "Add/Edit parties", UiUtil.SUB_TITLE_BOLD_FONT );
        this.uiBuilder.addElement( UiElement.JLABEL, "", 15, 40, 300, 20, "EMPLOYEES", UiUtil.LABEL_BOLD_FONT );
        this.employeesTable = new AbstractDataTable<PartyWrapper>( PartyWrapper.class, employees );
        final JScrollPane employeesScrollPane = new JScrollPane( employeesTable );
        final int employeePanelHieght = 140;
        employeesScrollPane.setBounds( 15, 60, PANEL_WIDTH - 40, employeePanelHieght );
        this.add( employeesScrollPane );
        final int bankTableY = employeePanelHieght + 65;
        final int bankPanelHieght = 100;
        this.uiBuilder.addElement( UiElement.JLABEL, "", 15, bankTableY, 250, 20, "BANKS", UiUtil.LABEL_BOLD_FONT );
        this.bankTable = new AbstractDataTable<PartyWrapper>( PartyWrapper.class, banks );
        final JScrollPane bankScrollPane = new JScrollPane( bankTable );
        bankScrollPane.setBounds( 15, bankTableY + 20, PANEL_WIDTH - 40, bankPanelHieght );
        this.add( bankScrollPane );
        
        final int partyTableY = bankTableY + bankPanelHieght + 25;
        this.uiBuilder.addElement( UiElement.JLABEL, "", 15, partyTableY, 250, 20, "PARTIES", UiUtil.LABEL_BOLD_FONT );
        this.partiesTable = new AbstractDataTable<PartyWrapper>( PartyWrapper.class, creditParties );
        final JScrollPane partiesScrollPane = new JScrollPane( partiesTable );
        partiesScrollPane.setBounds( 15, partyTableY + 20, PANEL_WIDTH - 40, (PANEL_HEIGHT - partyTableY -125) );
        this.add( partiesScrollPane );
        this.uiBuilder.addElement( UiElement.JLABEL, "", 15, PANEL_HEIGHT - 100, PANEL_WIDTH - 160, 40,
        		"<html><b>Note:Parties/Employees/Banks with outstanding balance cannot be deactivated.<br/>" +
        		"Employees with outstanding balance can be deactivated after the current month closing.</b></html>",
        		UiUtil.LABEL_REGULAR_FONT );
        
        
        this.partiesTable.getColumnModel().getColumn( 4 ).setCellEditor( new ListCellEditor( Arrays.asList( "ACTIVE", "INACTIVE" ) ) );
        this.employeesTable.getColumnModel().getColumn( 4 ).setCellEditor( new ListCellEditor( Arrays.asList( "ACTIVE", "INACTIVE" ) ) );
        this.bankTable.getColumnModel().getColumn( 4 ).setCellEditor( new ListCellEditor( Arrays.asList( "ACTIVE", "INACTIVE" ) ) );
        
        UiUtil.addActionListeners( this );
        this.dialog = new JDialog( frame, "Add/Edit parties", true );
        this.dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        this.dialog.setBounds( 50, 50, PANEL_WIDTH, PANEL_HEIGHT );
        this.dialog.add( this );
        this.dialog.setVisible( true );
        this.dialog.setResizable( true );
        this.dialog.setLayout( null );
        
        this.validate();
        this.repaint();
	}
	
	protected void processPanelRequest() throws BunkMgmtException
    {
       int nextEmpId = PartyDao.MIN_EMP_ID;
       int nextBankId = PartyDao.MIN_BANK_ID;
       int nextPartyId = PartyDao.MIN_PARTY_ID;
       for (Party party : this.parties)
       {
           if (PartyDao.MIN_EMP_ID <= party.getPartyId() && PartyDao.MIN_BANK_ID > party.getPartyId() &&
                   party.getPartyId()> nextEmpId )
           {
               nextEmpId = party.getPartyId();
           }
           if (PartyDao.MIN_BANK_ID <= party.getPartyId() && PartyDao.MIN_PARTY_ID > party.getPartyId() &&
                   party.getPartyId()> nextBankId )
           {
               nextBankId = party.getPartyId();
           }
           if (PartyDao.MIN_PARTY_ID <= party.getPartyId() && party.getPartyId()> nextPartyId )
           {
               nextPartyId = party.getPartyId();
           }
       }
       final List<Party> partyToBeUpdated = new ArrayList<Party>();
       final List<PartyTransaction> partyTransToBeUpdated = new ArrayList<PartyTransaction>();
       
       for ( PartyWrapper partyWrapper : this.employeesTable.getData() )
       {
           if (partyWrapper.getParty() == null)
           {
               addParty( partyWrapper, ++nextEmpId, partyToBeUpdated, partyTransToBeUpdated );
           }
           else if (partyWrapper.isUpdated())
           {
               updateParty( partyWrapper, partyToBeUpdated, partyTransToBeUpdated );
           }
       }
       for ( PartyWrapper partyWrapper : this.bankTable.getData() )
       {
           if (partyWrapper.getParty() == null)
           {
               addParty( partyWrapper, ++nextBankId, partyToBeUpdated, partyTransToBeUpdated );
           }
           else if (partyWrapper.isUpdated())
           {
               updateParty( partyWrapper, partyToBeUpdated, partyTransToBeUpdated );
           }
       }
       for ( PartyWrapper partyWrapper : this.partiesTable.getData() )
       {
           if (partyWrapper.getParty() == null)
           {
               addParty( partyWrapper, ++nextPartyId, partyToBeUpdated, partyTransToBeUpdated );
           }
           else if (partyWrapper.isUpdated())
           {
               updateParty( partyWrapper, partyToBeUpdated, partyTransToBeUpdated );
           }
       }
       this.bunkManager.savePartyDetails( partyToBeUpdated, partyTransToBeUpdated );
    }
	
	protected void validatePanel() throws UIValidationException
    {
	    cancelTableCellEditing( this.bankTable, this.employeesTable, this.partiesTable );
	    SwingUtil.validatePanelComponents( this );
    }
	
	public void actionPerformed( final ActionEvent actionEvent )
	{
		if ( actionEvent.getSource().equals( this.getComp( SUBMIT_BUTTON, JButton.class ) ) )
		{
		    if (submitPanel())
		    {
		        UiUtil.alertOperationComplete( this, "Parties saved successfully");
		        BunkAppInitializer.refreshPartyNameCache();
	            if (null != this.actionListener)
	            {
	                this.actionListener.actionPerformed( actionEvent );
	            }
	            this.dialog.dispose();
		    }
		}
		this.validate();
		this.repaint();
	}
	
	private void addParty(final PartyWrapper partyWrapper, final int nextId, final List<Party> partyToBeUpdated,
	        final List<PartyTransaction> partyTransToBeUpdated ) throws BunkMgmtException
	{
	    final Party party = new Party();
	    party.setPartyId( nextId );
	    party.setPartyDetail( partyWrapper.getDetail() );
	    party.setPartyName( partyWrapper.getPartyName() );
	    party.setPartyPhone( partyWrapper.getPhone() );
	    party.setPartyStatus( partyWrapper.getStatus() );
	    partyToBeUpdated.add( party );
	    
	    final PartyTransaction transaction = new PartyTransaction();
        transaction.setPartyId( nextId );
        transaction.setAmount( CustomDecimal.ZERO );
        transaction.setBalance( CustomDecimal.ZERO );
        transaction.setDate( this.bunkManager.getTodayDate() );
        transaction.setTransactionDetail( "New party" );
        transaction.setTransactionType( "CREDIT_S" );
        partyTransToBeUpdated.add( transaction );
	}
	
	private void updateParty(final PartyWrapper partyWrapper, final List<Party> partyToBeUpdated,
            final List<PartyTransaction> partyTransToBeUpdated ) throws BunkMgmtException
    {
        final Party party = partyWrapper.getParty();
        party.setPartyDetail( partyWrapper.getDetail() );
        party.setPartyName( partyWrapper.getPartyName() );
        party.setPartyPhone( partyWrapper.getPhone() );
        party.setPartyStatus( partyWrapper.getStatus() );
        partyToBeUpdated.add( party );
    }
}
