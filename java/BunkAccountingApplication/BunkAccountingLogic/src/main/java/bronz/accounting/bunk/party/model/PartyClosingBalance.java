package bronz.accounting.bunk.party.model;

import java.math.BigDecimal;

/**
 * Business model for Party transaction.
 */
public class PartyClosingBalance
{
	private Integer slNo;
	private int id;
	private String name;
	private int date;
	private BigDecimal balance;

    /**
	 * Default constructor.
	 */
	public PartyClosingBalance()
	{
		super();
	}

    public PartyClosingBalance(final Integer slNo, final int id, final String name,
        final int date, final BigDecimal balance) {
        this.slNo= slNo;
        this.id = id;
        this.name = name;
        this.date = date;
        this.balance = balance;
    }

    public Integer getSlNo()
    {
        return slNo;
    }

    public void setSlNo( Integer slNo )
    {
        this.slNo = slNo;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getDate()
    {
        return date;
    }

    public void setDate( int date )
    {
        this.date = date;
    }

    public BigDecimal getBalance()
    {
        return balance;
    }

    public void setBalance( BigDecimal balance )
    {
        this.balance = balance;
    }
    
    public String toString()
    {
        return String.format( "%1$S | BAL:%2$S", this.name, this.balance.toPlainString() );
    }
}
