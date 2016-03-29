package bronz.accounting.bunk.tankandmeter.model;

import java.math.BigDecimal;

/**
 * Business model for Meter closing reading.
 */
public class MeterClosingReading
{
    private int slNo;
	private int meterId;
	private int tankId;
	private int productId;
	private int date;
	private String meterName;
	private BigDecimal finalReading;
	
	/**
	 * Default constructor.
	 */
	public MeterClosingReading()
	{
	}

	/**
     * @return the tankId
     */
    public int getTankId()
    {
        return tankId;
    }

    /**
     * @param tankId the tankId to set
     */
    public void setTankId( int tankId )
    {
        this.tankId = tankId;
    }

    /**
     * @return the productId
     */
    public int getProductId()
    {
        return productId;
    }

    /**
     * @param productId the productId to set
     */
    public void setProductId( int productId )
    {
        this.productId = productId;
    }

    /**
     * @return the meterName
     */
    public String getMeterName()
    {
        return meterName;
    }

    /**
     * @param meterName the meterName to set
     */
    public void setMeterName( String meterName )
    {
        this.meterName = meterName;
    }

    public BigDecimal getFinalReading()
    {
        return finalReading;
    }

    public void setFinalReading( BigDecimal finalReading )
    {
        this.finalReading = finalReading;
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
	
	public String toString()
	{
	    return this.meterName;
	}

}
