package bronz.accounting.bunk.ui.party.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.panel.CreditChequeToBankPanel;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.DateUtil;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=true, warningValidationMethod="warningValidation", editableColumnListMethod="getEditableColList" )
public class OfficeCashDetailBean
{
    @DataTableField( columnName="TYPE", preferedWidth=20, allowNull=false, columnNum=0, isEditable=false)
    private PartyClosingBalance party;
    @DataTableField( columnName="OPENING", preferedWidth=20, allowNull=false, columnNum=1, isEditable=false)
    private BigDecimal opening = new CustomDecimal( 0 );
    @DataTableField( columnName="TO OFF TODAY", preferedWidth=20, allowNull=false, columnNum=2, isEditable=true)
    private BigDecimal amountToday = new CustomDecimal( 0 );
    @DataTableField( columnName="PAID TO BANK TODAY", preferedWidth=20, allowNull=false, columnNum=3, isEditable=true)
    private BigDecimal paidToBank = new CustomDecimal( 0 );
    @DataTableField( columnName="BALANCE", preferedWidth=20, allowNull=false, columnNum=4, isEditable=false)
    private BigDecimal balance = new CustomDecimal( 0 );
    
    private final int todayDate;
    private final List<PartyDetailBean> partySaleBeans;
    private final List<PartyTransaction> pendingCheques;
    private List<ChequeTransWrapper> creditedCheques;

    public OfficeCashDetailBean( final PartyClosingBalance party, final int todayDate,
            final List<PartyDetailBean> partySaleBeans, final List<PartyTransaction> pendingCheques )
    {
        super();
        this.todayDate = todayDate;
        this.partySaleBeans = partySaleBeans;
        this.pendingCheques = pendingCheques;
        setParty( party );
    }

    public PartyClosingBalance getParty()
    {
        return party;
    }

    public void setParty( PartyClosingBalance party )
    {
        this.party = party;
        if (null != party )
        {
            this.opening = party.getBalance();
            this.balance = party.getBalance();
            this.amountToday = CustomDecimal.ZERO;
            this.paidToBank = CustomDecimal.ZERO;
        }
    }

    public BigDecimal getOpening()
    {
        return opening;
    }

    public void setOpening( BigDecimal opening )
    {
        this.opening = opening;
    }

    public BigDecimal getAmountToday()
    {
        if ( AppConfig.OFFICE_CHEQUE_PARTY_ID.getValue( Integer.class ) ==
            this.party.getId() && null != this.partySaleBeans )
        {
            BigDecimal amount = CustomDecimal.ZERO;
            for( PartyDetailBean partyDetailBean : this.partySaleBeans )
            {
                if (CustomDecimal.ZERO.compareTo( partyDetailBean.getDebitByCheque() ) < 0 )
                {
                    amount = amount.add( partyDetailBean.getDebitByCheque() );
                }
            }
            amountToday = amount.setScale( 2, RoundingMode.HALF_UP );
            updateBalance();
        }
        return amountToday;
    }

    public void setAmountToday( BigDecimal amountToday )
    {
        if (CustomDecimal.ZERO.compareTo( amountToday )<=0)
        {
            this.amountToday = amountToday.setScale( 2, RoundingMode.HALF_UP );
            updateBalance();
        }
    }

    public BigDecimal getPaidToBank()
    {
        return paidToBank;
    }

    public void setPaidToBank( BigDecimal paidToBank )
    {
        if (CustomDecimal.ZERO.compareTo( amountToday )<=0 && paidToBank.compareTo( this.amountToday.add( opening ) ) <= 0)
        {
            this.paidToBank = paidToBank.setScale( 2, RoundingMode.HALF_UP );
        }
        else
        {
            this.paidToBank = CustomDecimal.ZERO;
        }
        updateBalance();
    }

    public BigDecimal getBalance()
    {
        return balance;
    }

    public void setBalance( BigDecimal balance )
    {
        this.balance = balance;
    }
    public void updateBalance()
    {
        this.balance = this.opening.add( amountToday ).subtract( this.paidToBank ).setScale( 2, RoundingMode.HALF_UP );
    }
    
    public String warningValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if ( AppConfig.OFFICE_CASH_PARTY_ID.getValue( Integer.class ) ==
            this.party.getId())
        {
            if ((paidToBank.compareTo( CustomDecimal.ZERO ) <= 0) && DateUtil.isWorkingDay( this.todayDate ))
            {
                errorMsg.append( "--No bank deposits found a on a non SUNDAY day." );
            }
            else if ((paidToBank.compareTo( CustomDecimal.ZERO ) > 0) && !DateUtil.isWorkingDay( this.todayDate ))
            {
                errorMsg.append( "--Bank deposits found a on a SUNDAY!!" );
            }
        }
        else if ( AppConfig.OFFICE_CHEQUE_PARTY_ID.getValue( Integer.class ) ==
            this.party.getId())
        {
            if ((paidToBank.compareTo( CustomDecimal.ZERO ) <= 0) && (opening.compareTo( CustomDecimal.ZERO ) > 0) &&
                    DateUtil.isWorkingDay( this.todayDate ))
            {
                errorMsg.append( "--Cheques yet to be presented to bank." );
            }
            else if ((paidToBank.compareTo( CustomDecimal.ZERO ) > 0) && !DateUtil.isWorkingDay( this.todayDate ))
            {
                errorMsg.append( "--Cheques presented to bank on a SUNDAY!!" );
            }
        }
        return errorMsg.toString();
    }
    
    /**
     * @return the editableColList
     */
    public List<Integer> getEditableColList()
    {
        if ( AppConfig.OFFICE_CHEQUE_PARTY_ID.getIntValue() == this.party.getId())
        {
            try
            {
                new CreditChequeToBankPanel( HomePage.getInstance(), this, this.pendingCheques);
            }
            catch( BunkMgmtException exception )
            {
                UiUtil.alertUnexpectedError( HomePage.getInstance(), exception );
            }
            return new ArrayList<Integer>();
        }
        else
        {
            return null;
        }
    }

    public List<ChequeTransWrapper> getCreditedCheques() {
        return creditedCheques;
    }

    public List<PartyTransaction> getPendingCheques() {
        return pendingCheques;
    }

    public void setCreditedCheques(List<ChequeTransWrapper> creditedCheques) {
        this.creditedCheques = creditedCheques;
        BigDecimal recievedToday = CustomDecimal.ZERO;
        for ( ChequeTransWrapper chqTrans : this.creditedCheques )
        {
            recievedToday = recievedToday.add( chqTrans.getAmount() );
        }
        this.setPaidToBank( recievedToday );
    }
}
