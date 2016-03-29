package bronz.accounting.bunk.tankandmeter.model;

import java.math.BigDecimal;

public class TankClosingStock
{
    private int slNo;
    private Integer tankId;
    private String tankName;
    private String productType;
    private int productId;
    private BigDecimal closingStock;
    private BigDecimal diffThisMonth;
    private int date;
    
    public int getSlNo()
    {
        return slNo;
    }
    
    public void setSlNo( final int slNo )
    {
        this.slNo = slNo;
    }
    
    public BigDecimal getClosingStock()
    {
        return closingStock;
    }

    public void setClosingStock( final BigDecimal closingStock )
    {
        this.closingStock = closingStock;
    }

    public BigDecimal getDiffThisMonth()
    {
		return diffThisMonth;
	}

	public void setDiffThisMonth(BigDecimal diffThisMonth)
	{
		this.diffThisMonth = diffThisMonth;
	}

	public int getDate()
    {
        return this.date;
    }

    public void setDate( final Integer date )
    {
        this.date = date;
    }

    public Integer getTankId()
    {
        return this.tankId;
    }

    public void setTankId( final Integer tankId )
    {
        this.tankId = tankId;
    }

    public String getTankName()
    {
        return this.tankName;
    }

    public void setTankName( final String tankName )
    {
        this.tankName = tankName;
    }

    public String getProductType()
    {
        return this.productType;
    }

    public void setProductType( final String productType )
    {
        this.productType = productType;
    }

    public void setDate( final int date )
    {
        this.date = date;
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
    public void setProductId( final int productId )
    {
        this.productId = productId;
    }

    public String toDetailedString()
    {
        return String.format( "\nID:%1$-5d NAME:%2$-25s PROD_TYPE:%3$-10s " +
        		"DATE:%4$-7d STOCK:%5$9.2f", slNo, tankName, productType,
                date, closingStock);
    }
    
    public String toString()
    {
        return this.tankName + '(' + this.productType + ')';
    }
}
