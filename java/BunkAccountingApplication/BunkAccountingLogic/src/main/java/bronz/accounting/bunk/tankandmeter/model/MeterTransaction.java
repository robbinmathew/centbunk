package bronz.accounting.bunk.tankandmeter.model;

import java.math.BigDecimal;

/**
 * Business model for Meter closing reading.
 */
public class MeterTransaction
{
	public static final String SALE = "SALE";
    private int slNo;
	private int meterId;
	private int date;
	private String detail;
	private BigDecimal finalReading;
	private BigDecimal meterSale;
	
	/**
	 * Default constructor.
	 */
	public MeterTransaction()
	{
	}

	public BigDecimal getFinalReading()
    {
        return finalReading;
    }

    public void setFinalReading( BigDecimal finalReading )
    {
        this.finalReading = finalReading;
    }

    public BigDecimal getMeterSale()
    {
        return meterSale;
    }

    public void setMeterSale( BigDecimal meterSale )
    {
        this.meterSale = meterSale;
    }

    public int getSlNo()
    {
        return slNo;
    }

    public void setSlNo( int slNo )
    {
        this.slNo = slNo;
    }

	/**
	 * Gets date.
	 *
	 * @return date
	 */
	public int getDate()
	{
		return this.date;
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
	 * Gets meterId.
	 *
	 * @return meterId
	 */
	public int getMeterId()
	{
		return this.meterId;
	}

	/**
	 * Sets meterId.
	 *
	 * @param meterId the meterId to set
	 */
	public void setMeterId( final int meterId)
	{
		this.meterId = meterId;
	}

	/**
	 * Gets detail.
	 *
	 * @return detail
	 */
	public String getDetail()
	{
		return this.detail;
	}

	/**
	 * Sets detail.
	 *
	 * @param detail the detail to set
	 */
	public void setDetail( final String detail)
	{
		this.detail = detail;
	}
}
