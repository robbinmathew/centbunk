package bronz.accounting.bunk.products.model;

import java.math.BigDecimal;

import bronz.utilities.general.DateUtil;

public class ReceiptSummary
{
    private int slNo;
    private String invoiceNumber;
    private int date;
    private String type;
    private String comments;
    private BigDecimal pendingAmt;
    private BigDecimal totalAmt;


    public ReceiptSummary()
    {
        super();
        this.pendingAmt = BigDecimal.ZERO;
    }
    
    public String getInvoiceNumber()
    {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber( final String invoiceNumber )
    {
        this.invoiceNumber = invoiceNumber;
    }
    
    public int getDate()
    {
        return date;
    }
    
    public String getDateText()
    {
        return DateUtil.getDateStringWithDay( date );
    }

    public void setDate( final int date )
    {
        this.date = date;
    }

    public int getSlNo()
    {
        return slNo;
    }

    public void setSlNo( int slNo )
    {
        this.slNo = slNo;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments( String comments )
    {
        this.comments = comments;
    }

    @Deprecated
    public BigDecimal getPendingAmt()
    {
        return pendingAmt;
    }

    @Deprecated
    public void setPendingAmt( BigDecimal pendingAmt )
    {
        this.pendingAmt = pendingAmt;
    }
    
    public BigDecimal getTotalAmt()
    {
        return totalAmt;
    }

    public void setTotalAmt( BigDecimal totalAmt )
    {
        this.totalAmt = totalAmt;
    }
}
