package bronz.accounting.bunk.products.model;

import java.math.BigDecimal;

import bronz.accounting.bunk.util.EntityNameCache;
import bronz.utilities.general.DateUtil;

public class ProdRateChange
{
    private int slNo;
    private int prodId;
    private int date;
    private BigDecimal oldPrice;
    private BigDecimal stock;
    private String comments;
    
    public int getSlNo()
    {
        return slNo;
    }
    public void setSlNo( int slNo )
    {
        this.slNo = slNo;
    }
    public int getProdId()
    {
        return prodId;
    }
    
    public String getProdName()
    {
        return EntityNameCache.getProductName( prodId );
    }
    
    public void setProdId( int prodId )
    {
        this.prodId = prodId;
    }
    public int getDate()
    {
        return date;
    }
    public void setDate( int date )
    {
        this.date = date;
    }
    
    public String getDateText()
    {
        return DateUtil.getDateString( date );
    }
    public BigDecimal getOldPrice()
    {
        return oldPrice;
    }
    public void setOldPrice( BigDecimal oldPrice )
    {
        this.oldPrice = oldPrice;
    }
    public BigDecimal getStock()
    {
        return stock;
    }
    public void setStock( BigDecimal stock )
    {
        this.stock = stock;
    }
    public String getComments()
    {
        return comments;
    }
    public void setComments( String comments )
    {
        this.comments = comments;
    }
    
}
