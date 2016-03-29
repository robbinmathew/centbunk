package bronz.accounting.bunk.party.model;

import java.math.BigDecimal;
import java.util.Date;

public class Settlement
{
    private int date;
    private BigDecimal closingBal;
    private String comments;
    private String settlementType;
    private Date settlementDate;
    private String createdBy;
    private Date createdDate;
    /**
     * @return the date
     */
    public int getDate()
    {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate( int date )
    {
        this.date = date;
    }
    /**
     * @return the closingBal
     */
    public BigDecimal getClosingBal()
    {
        return closingBal;
    }
    /**
     * @param closingBal the closingBal to set
     */
    public void setClosingBal( BigDecimal closingBal )
    {
        this.closingBal = closingBal;
    }
    /**
     * @return the comments
     */
    public String getComments()
    {
        return comments;
    }
    /**
     * @param comments the comments to set
     */
    public void setComments( String comments )
    {
        this.comments = comments;
    }
    /**
     * @return the settlementType
     */
    public String getSettlementType()
    {
        return settlementType;
    }
    /**
     * @param settlementType the settlementType to set
     */
    public void setSettlementType( String settlementType )
    {
        this.settlementType = settlementType;
    }
    /**
     * @return the settlementDate
     */
    public Date getSettlementDate()
    {
        return settlementDate;
    }
    /**
     * @param settlementDate the settlementDate to set
     */
    public void setSettlementDate( Date settlementDate )
    {
        this.settlementDate = settlementDate;
    }
    /**
     * @return the createdBy
     */
    public String getCreatedBy()
    {
        return createdBy;
    }
    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }
    /**
     * @return the createdDate
     */
    public Date getCreatedDate()
    {
        return createdDate;
    }
    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate( Date createdDate )
    {
        this.createdDate = createdDate;
    }
    
    

}
