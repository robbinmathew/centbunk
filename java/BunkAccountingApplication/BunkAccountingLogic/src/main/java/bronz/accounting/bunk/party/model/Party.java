package bronz.accounting.bunk.party.model;

/**
 * Business model for party table.
 */
public class Party
{
	private Integer partyId;
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

	public Party(final Party party)	{
		this.partyId = party.partyId;
		this.partyName = party.partyName;
		this.partyDetail = party.partyDetail;
		this.partyPhone = party.partyPhone;
		this.partyStatus = party.partyStatus;
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
	public Integer getPartyId()
	{
		return partyId;
	}

	/**
	 * Sets partyId.
	 *
	 * @param partyId the partyId to set
	 */
	public void setPartyId( final Integer partyId)
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
	 * Overrides toString method.
	 *
	 * @return The String.
	 */
	public String toString()
	{
		return this.partyName;
	}

	public boolean equals(Party party) {
		if (party.partyId.equals(this.partyId) &&
			party.partyName.equals(this.partyName) &&
			party.partyDetail.equals(this.partyDetail) &&
			party.partyPhone.equals(this.partyPhone) &&
			party.partyStatus.equals(this.partyStatus)) {
			return true;
		} else {
			return false;
		}
	}
}
