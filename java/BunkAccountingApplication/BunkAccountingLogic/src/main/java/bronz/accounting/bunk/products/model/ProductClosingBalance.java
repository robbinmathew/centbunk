package bronz.accounting.bunk.products.model;

import java.math.BigDecimal;

import bronz.accounting.bunk.AppConfig;
import bronz.utilities.general.DateUtil;

public class ProductClosingBalance
{
    private int slNo;
    private int productId;
    private int date;
    private String productName;
    private BigDecimal unitSellingPrice;
    private BigDecimal closingStock;
    private Integer lastLoadOn;
    private BigDecimal margin;
    
    public int getSlNo()
    {
        return slNo;
    }
    
    public void setSlNo( final int slNo )
    {
        this.slNo = slNo;
    }
    
    public int getProductId()
    {
        return productId;
    }
    
    public void setProductId( final int productId )
    {
        this.productId = productId;
    }
    
    public String getProductName()
    {
        return productName;
    }
    
    public void setProductName( final String productName )
    {
        this.productName = productName;
    }

    public BigDecimal getUnitSellingPrice()
    {
        return unitSellingPrice;
    }

    public void setUnitSellingPrice( final BigDecimal unitSellingPrice )
    {
        this.unitSellingPrice = unitSellingPrice;
    }

    public BigDecimal getClosingStock()
    {
        return closingStock;
    }

    public void setClosingStock( final BigDecimal closingStock )
    {
        this.closingStock = closingStock;
    }

    public int getDate()
    {
        return date;
    }

    public void setDate( final Integer date )
    {
        this.date = date;
    }
    
    public Integer getLastLoadOn()
    {
        return lastLoadOn;
    }
    
    public String getLastLoadOnText()
    {
        final StringBuffer text = new StringBuffer();
        
        if ( null == lastLoadOn )
        {
            text.append( "BEFORE " );
            text.append( DateUtil.getDateString(
            		AppConfig.FIRST_DAY_PROP_NAME.getValue(Integer.class ) ) );
        }
        else
        {
            text.append( DateUtil.getDateString( lastLoadOn ) );
        }
        return text.toString();
    }

    public void setLastLoadOn( final Integer lastLoadOn )
    {
        this.lastLoadOn = lastLoadOn;
    }
    
    public String toString()
    {
        return this.productName + " | R:" + this.unitSellingPrice.toPlainString() +
                " | S:" + this.closingStock.toPlainString();
        /*return String.format( "\nD:%1$-5d PROD_ID:%2$-3d NAME:%3$-25s " +
                "DATE:%4$-7d BAL:%5$9.2f PRICE:%6$7.2f LAST_LOAD_ON:%7$s", slNo,
                productId, productName, date, closingStock, unitSellingPrice,
                getLastLoadOnText() );*/
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
