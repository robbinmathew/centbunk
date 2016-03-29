package bronz.accounting.bunk.party.model;

import java.math.BigDecimal;

/**
 * Business model for Employee Monthly status.
 */
public class EmployeeMonthlyStatus
{
	private Integer slNo;
	private int id;
	private String name;
	private int date;
	private BigDecimal balance;
	private BigDecimal tolSalThisMonth;
	private BigDecimal tolInceThisMonth;

    /**
	 * Default constructor.
	 */
	public EmployeeMonthlyStatus()
	{
		super();
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
    
    public BigDecimal getTolSalThisMonth()
    {
		return tolSalThisMonth;
	}

	public void setTolSalThisMonth(BigDecimal tolSalThisMonth)
	{
		this.tolSalThisMonth = tolSalThisMonth;
	}

	public BigDecimal getTolInceThisMonth()
	{
		return tolInceThisMonth;
	}

	public void setTolInceThisMonth(BigDecimal tolInceThisMonth)
	{
		this.tolInceThisMonth = tolInceThisMonth;
	}

	public String toString()
    {
        return this.name;
    }
}
