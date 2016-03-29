package bronz.accounting.bunk.party.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bronz.utilities.general.DateUtil;

/**
 * Business model for Party transaction.
 */
public class PartyTransaction
{
    public static final String CHEQUE_DEBIT = "DEBIT_CHQ";
    public static final String CREDIT = "CREDIT";
    public static final String DEBIT = "DEBIT";
    public static final String DEBIT_SYSTEM = "DEBIT_S";
    public static final String CREDIT_SYSTEM = "CREDIT_S";
    public static final String CREDIT_SALARY = "CREDIT_PAY";
    public static final String CREDIT_INCENTIVE = "CREDIT_INC";
    public static final String CHEQUE_CREDIT_TRANS_DETAIL_PREFIX = "CHQ:CRED:FOR:SLNO:";
    
    public static final List<String> CREDIT_TRANS_TYPES = Arrays.asList(
    		CREDIT, CREDIT_SYSTEM, CREDIT_INCENTIVE, CREDIT_SALARY );
    
	private Integer slNo;
	private int partyId;
	private String transactionDetail;
	private String transactionType;
	private BigDecimal amount;
	private int date;
	private BigDecimal balance;

    /**
	 * Default constructor.
	 */
	public PartyTransaction()
	{
		super();
	}

	/**
	 * Gets amount.
	 *
	 * @return amount
	 */
	public BigDecimal getAmount()
	{
		return amount;
	}

	/**
	 * Sets amount.
	 *
	 * @param amount the amount to set
	 */
	public void setAmount( BigDecimal amount)
	{
		this.amount = amount;
	}

	/**
	 * Gets balance.
	 *
	 * @return balance
	 */
	public BigDecimal getBalance()
	{
		return balance;
	}

	/**
	 * Sets balance.
	 *
	 * @param balance the balance to set
	 */
	public void setBalance( BigDecimal balance)
	{
		this.balance = balance;
	}

    /**
     * Gets date.
     *
     * @return date
     */
    public int getDate()
    {
        return date;
    }

	/**
	 * Sets date.
	 *
	 * @param date the date to set
	 */
	public void setDate( int date)
	{
		this.date = date;
	}

	/**
	 * Gets partyId.
	 *
	 * @return partyId
	 */
	public int getPartyId()
	{
		return partyId;
	}

	/**
	 * Sets partyId.
	 *
	 * @param partyId the partyId to set
	 */
	public void setPartyId( final int partyId)
	{
		this.partyId = partyId;
	}

	/**
	 * Gets transactionDetail.
	 *
	 * @return transactionDetail
	 */
	public String getTransactionDetail()
	{
		return transactionDetail;
	}

	/**
	 * Sets transactionDetail.
	 *
	 * @param transactionDetail the transactionDetail to set
	 */
	public void setTransactionDetail( String transactionDetail)
	{
		this.transactionDetail = transactionDetail;
	}

	/**
	 * Gets transactionType.
	 *
	 * @return transactionType
	 */
	public String getTransactionType()
	{
		return transactionType;
	}

	/**
	 * Sets transactionType.
	 *
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType( String transactionType)
	{
		this.transactionType = transactionType;
	}
	
	/**
     * Gets date text.
     *
     * @return date
     */
    public String getDateText()
    {
        return DateUtil.getDateString( this.date );
    }
    
    public String getCreditText()
    {
        return this.transactionType.startsWith( CREDIT )? this.amount.toPlainString() : "";
    }
    
    public String getDebitText()
    {
        return !this.transactionType.startsWith( CREDIT )? this.amount.toPlainString() : "";
    }
	
    public String toString()
	{
		return String.format( "\nID:%1$-5d PARTY:%2$-4d AMT:%3$9.2f DATE:%4$-7d" +
				"BAL:%5$9.2f TYPE:%6$-6s DETAIL:%7$s", slNo, partyId, amount, date,
				balance, transactionType, transactionDetail );
	}

	/**
	 * Gets slno.
	 *
	 * @return slno
	 */
	public Integer getSlNo()
	{
		return slNo;
	}

	/**
	 * Sets slno.
	 *
	 * @param slNo the slno to set
	 */
	public void setSlNo( final Integer slNo)
	{
		this.slNo = slNo;
	}
}
