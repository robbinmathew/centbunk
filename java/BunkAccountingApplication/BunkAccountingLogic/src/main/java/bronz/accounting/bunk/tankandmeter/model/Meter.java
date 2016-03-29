package bronz.accounting.bunk.tankandmeter.model;

import java.math.BigDecimal;

/**
 * Business model for meter table.
 */
public class Meter
{
	private int meterId;
	private String meterName;
	private int tankId;
	private BigDecimal maxReading;
	private String status;
	
	
	/**
	 * Default constructor.
	 */
	public Meter()
	{
	}


	public int getTankId()
    {
        return tankId;
    }


    public void setTankId( int tankId )
    {
        this.tankId = tankId;
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
	 * Gets meterName.
	 *
	 * @return meterName
	 */
	public String getMeterName()
	{
		return this.meterName;
	}


	/**
	 * Sets meterName.
	 *
	 * @param meterName the meterName to set
	 */
	public void setMeterName( final String meterName)
	{
		this.meterName = meterName;
	}


	/**
	 * Gets maxReading.
	 *
	 * @return maxReading
	 */
	public BigDecimal getMaxReading()
	{
		return this.maxReading;
	}


	/**
	 * Sets maxReading.
	 *
	 * @param maxReading the meterReading to set
	 */
	public void setMaxReading( final BigDecimal maxReading)
	{
		this.maxReading = maxReading;
	}


	/**
	 * Gets status.
	 *
	 * @return status
	 */
	public String getStatus()
	{
		return this.status;
	}


	/**
	 * Sets status.
	 *
	 * @param status the status to set
	 */
	public void setStatus( final String status)
	{
		this.status = status;
	}

	/**
	 * Overrides the toString() method.
	 */
	public String toString()
	{
		return this.meterName;
	}
	
}
