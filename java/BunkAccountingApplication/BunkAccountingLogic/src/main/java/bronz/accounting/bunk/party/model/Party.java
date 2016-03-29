package bronz.accounting.bunk.party.model;

/**
 * Business model for party table.
 */
public class Party
{
	private int partyId;
	private String partyName;
	private String partyDetail;
	private String partyPhone;
	private String partyStatus;
	
	/**
	 * Default Constructor.
	 */
	public Party()
	{
		super();
	}

	/**
	 * Gets partyDetail.
	 *
	 * @return partyDetail
	 */
	public String getPartyDetail()
	{
		return partyDetail;
	}

	/**
	 * Sets partyDetail.
	 *
	 * @param partyDetail the partyDetail to set
	 */
	public void setPartyDetail( final String partyDetail)
	{
		this.partyDetail = partyDetail;
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
	 * Gets partyName.
	 *
	 * @return partyName
	 */
	public String getPartyName()
	{
		return partyName;
	}

	/**
	 * Sets partyName.
	 *
	 * @param partyName the partyName to set
	 */
	public void setPartyName( final String partyName)
	{
		this.partyName = partyName;
	}

	/**
	 * Gets partyPhone.
	 *
	 * @return partyPhone
	 */
	public String getPartyPhone()
	{
		return partyPhone;
	}

	/**
	 * Sets partyPhone.
	 *
	 * @param partyPhone the partyPhone to set
	 */
	public void setPartyPhone( final String partyPhone)
	{
		this.partyPhone = partyPhone;
	}

	/**
	 * Gets partyStatus.
	 *
	 * @return partyStatus
	 */
	public String getPartyStatus()
	{
		return partyStatus;
	}

	/**
	 * Sets partyStatus.
	 *
	 * @param partyStatus The partyStatus to set
	 */
	public void setPartyStatus( final String partyStatus)
	{
		this.partyStatus = partyStatus;
	}
	
	/**
	 * Gets the description of the party.
	 *
	 * @return String description.
	 */
	public String getPartyDescription()
	{
		return "Party ID:" + this.partyId + " Party Name:" +
				this.partyName;
	}
	
	/**
	 * Overrides toString method.
	 *
	 * @return The String.
	 */
	public String toString()
	{
		return this.partyName;
	}
}
