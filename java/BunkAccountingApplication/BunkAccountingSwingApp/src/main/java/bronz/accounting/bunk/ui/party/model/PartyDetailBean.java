package bronz.accounting.bunk.ui.party.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;
import bronz.utilities.swing.util.SwingUtil;

@DataTable( fixNoOfRows=false, uniqueIdCol=false, minNoOfRows=30, editableColumnListMethod="getEditableColList", errorValidationMethod="errorValidation" )
public class PartyDetailBean
{
	public static final String DEBIT_CHQ = "DEBIT CHQ";
	public static final String CREDIT = "CREDIT";
	public static final String DEBIT = "DEBIT";
	public static final List<String> ROWS = Arrays.asList(DEBIT_CHQ, DEBIT, CREDIT);
	
	
    private static Map<Integer,PartyDetailBean> PARTY_BALANCE = new LinkedHashMap<Integer, PartyDetailBean>();
    
    @DataTableField( columnName="PARTY", preferedWidth=25, allowNull=false, columnNum=0)
    private PartyClosingBalance party;
    @DataTableField( columnName="DETAIL", preferedWidth=35, allowNull=false, columnNum=1)
    private Map<String, String> details = new HashMap<String, String>();
    @DataTableField( columnName="DEBIT BY CHQ", preferedWidth=10, allowNull=false, columnNum=2)
    private BigDecimal debitByCheque = new CustomDecimal( 0 );
    @DataTableField( columnName="CREDIT", preferedWidth=10, allowNull=false, columnNum=4)
    private BigDecimal credit = new CustomDecimal( 0 );
    @DataTableField( columnName="DEBIT", preferedWidth=10, allowNull=false, columnNum=3)
    private BigDecimal debit = new CustomDecimal( 0 );
    @DataTableField( columnName="BALANCE", preferedWidth=10, allowNull=false, columnNum=5, isEditable=false)
    private String balance = "";
    
    private List<Integer> editableColList = null;
    private boolean isUpdated = true;
    
    public static void CLEAR_BALANCES()
    {
        PARTY_BALANCE = new LinkedHashMap<Integer, PartyDetailBean>();
    }
    
    public static void CLEAR_AND_ADD_ALL(final List<PartyDetailBean> beans)
    {
        PARTY_BALANCE.clear();
        for (PartyDetailBean bean : beans)
        {
            PARTY_BALANCE.put( PARTY_BALANCE.size(), bean );
        }
    }
    
    public PartyDetailBean()
    {
        super();
        PARTY_BALANCE.put( PARTY_BALANCE.size(), this );
    }

    /**
     * @return the party
     */
    public PartyClosingBalance getParty()
    {
        return party;
    }

    /**
     * @param party the party to set
     */
    public void setParty( PartyClosingBalance party )
    {
        this.party = party;
        updateAllBalance();
    }

    /**
     * @return the credit
     */
    public BigDecimal getCredit()
    {
        return credit;
    }

    /**
     * @param credit the credit to set
     */
    public void setCredit( BigDecimal credit )
    {
        this.credit = credit;
        updateAllBalance();
    }

    /**
     * @return the debit
     */
    public BigDecimal getDebit()
    {
        return debit;
    }

    /**
     * @param debit the debit to set
     */
    public void setDebit( BigDecimal debit )
    {
        this.debit = debit;
        updateAllBalance();
    }

    /**
     * @return the balance
     */
    public String getBalance()
    {
        return this.balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance( String balance )
    {
        this.balance = balance;
    }

    public Map<String, String> getDetails()
    {
		return details;
	}

	public void setDetails(Map<String, String> details)
	{
		this.details = details;
	}

    public String getDebitByChqDetail()
    {
        return this.details.get(DEBIT_CHQ);
    }
    
    public String getDebitDetail()
    {
        return this.details.get(DEBIT);
    }
    
    public String getCreditDetail()
    {
        return this.details.get(CREDIT);
    }
    
    public void setDebitDetail(final String detail)
    {
    	this.details.put(DEBIT, detail);
    }
    
    public void setCreditDetail(final String detail)
    {
        this.details.put(CREDIT, detail);
    }

    private synchronized void updateAllBalance()
    {
        if ( null != party )
        {
            final Map<Integer, BigDecimal> balances = new HashMap<Integer, BigDecimal>();
            for( PartyDetailBean bean : PARTY_BALANCE.values() )
            {
                if ( bean.party != null )
                {
                	if (bean.isUpdated)
                	{
	                    if ( null == balances.get( bean.party.getId()))
	                    {
	                        balances.put( bean.party.getId(), bean.party.getBalance() );
	                    }
	                    balances.put( bean.party.getId(), balances.get( bean.party.getId()).add( bean.credit ).subtract(
	                            bean.debit ).subtract( bean.debitByCheque ) );
	                    bean.balance = BunkUtil.setAsPrice(balances.get( bean.party.getId())).toPlainString();
                	}
                	else
                	{
                		bean.balance = "NA";
                	}
                }
            }
        }
    }
    
    /**
     * @return the editableColList
     */
    public List<Integer> getEditableColList()
    {
        return editableColList;
    }


    /**
     * @param editableColList the editableColList to set
     */
    public void setEditableColList( List<Integer> editableColList )
    {
        this.editableColList = editableColList;
    }

    public boolean isUpdated()
    {
        return isUpdated;
    }

    public void setUpdated( boolean isUpdated )
    {
        this.isUpdated = isUpdated;
    }

    public BigDecimal getDebitByCheque()
    {
        return debitByCheque;
    }

    public void setDebitByCheque( BigDecimal debitByCheque )
    {
        this.debitByCheque = debitByCheque;
        updateAllBalance();
    }
    
    public String errorValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if ( this.party != null &&
        		this.debitByCheque.compareTo(BigDecimal.ZERO) > 0 &&
        		ValidationUtil.isNullOrEmpty(getDebitByChqDetail()) )
        {
        	errorMsg.append( "--Enter chq no and bank for the cheque received for " + this.party.getName() );
        }
        if ( this.party != null &&
        		this.party.getId() == AppConfig.EXPENSES_PARTY_ID.getIntValue() )
        {
        	if ( this.debit.compareTo(BigDecimal.ZERO) > 0 &&
            		ValidationUtil.isNullOrEmpty(getDebitDetail()) )
        	{
        		SwingUtil.appendMessage(errorMsg, "--The detail for the debit amount '" + BunkUtil.setAsPrice(debit).toPlainString() + "' received for " + this.party.getName() );
        	}
        	if ( this.credit.compareTo(BigDecimal.ZERO) > 0 &&
            		ValidationUtil.isNullOrEmpty(getCreditDetail()) )
        	{
        		SwingUtil.appendMessage(errorMsg, "--The detail for the credit amount '" + BunkUtil.setAsPrice(credit).toPlainString() + "' paid for " + this.party.getName() );
        	}
        }
        return errorMsg.toString();
    }
}
