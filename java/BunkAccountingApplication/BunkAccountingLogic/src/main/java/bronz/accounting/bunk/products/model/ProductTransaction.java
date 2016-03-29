package bronz.accounting.bunk.products.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Business model for product transaction.
 */
public class ProductTransaction
{
	public static final String RECEIPT = "RECEIPT";
	public static final String DIFF = "DIFF";
	
	public static final List<String> CREDIT_TRANS_TYPES = Arrays.asList( RECEIPT, DIFF );
	
	private int productId;
    private Integer slNo;
	private String transactionType;
	private int date;
	private BigDecimal quantity;
	private BigDecimal balance;
	private BigDecimal unitPrice;
	private String detail;
	private BigDecimal margin;
	
    /**
	 * Default constructor.
	 */
	public ProductTransaction()
	{
		super();
	}

	public String getDetail()
    {
        return detail;
    }

    public void setDetail( String detail )
    {
        this.detail = detail;
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
	public void setBalance( final BigDecimal balance)
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
	public void setDate( final int date)
	{
		this.date = date;
	}

	/**
	 * Gets productId.
	 *
	 * @return productId
	 */
	public int getProductId()
	{
		return productId;
	}

	/**
	 * Sets productId.
	 *
	 * @param productId the productId to set
	 */
	public void setProductId( final int productId)
	{
		this.productId = productId;
	}

	/**
	 * Gets quantity.
	 *
	 * @return quantity
	 */
	public BigDecimal getQuantity()
	{
		return quantity;
	}

	/**
	 * Sets quantity.
	 *
	 * @param quantity the quantity to set
	 */
	public void setQuantity( final BigDecimal quantity)
	{
		this.quantity = quantity;
	}

	/**
	 * Gets totalPrice.
	 *
	 * @return totalPrice
	 */
	public BigDecimal getUnitPrice()
	{
		return unitPrice;
	}

	/**
	 * Sets totalPrice.
	 *
	 * @param totalPrice the totalPrice to set
	 */
	public void setUnitPrice( final BigDecimal totalPrice)
	{
		this.unitPrice = totalPrice;
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
	public void setTransactionType( final String transactionType)
	{
		this.transactionType = transactionType;
	}
	
	public String toString()
    {
        return String.format( "\nID:%1$-5d PROD:%2$-3d QUANTITY:%3$9.3f " +
        		"DATE:%4$-7d BAL:%5$9.2f TYPE:%6$-15s PRICE:%7$7.2f",
        		slNo, productId, quantity, date, balance, transactionType,
        		unitPrice );
    }

    /**
     * Gets slNo.
     *
     * @return slNo
     */
    public Integer getSlNo()
    {
        return slNo;
    }

    /**
     * Sets slNo.
     *
     * @param slNo the slNo to set
     */
    public void setSlNo( final Integer slNo)
    {
        this.slNo = slNo;
    }

    public BigDecimal getMargin()
    {
        return margin;
    }

    public void setMargin( BigDecimal margin )
    {
        this.margin = margin;
    }
}
