package bronz.accounting.bunk.tankandmeter.model;

import java.math.BigDecimal;

public class Tank
{
    private Integer tankId;
    private String tankName;
    private Integer productType;
    private BigDecimal maxCapacity;
    private String status;
    
    public Tank()
    {
        super();
    }

    public Integer getTankId()
    {
        return tankId;
    }

    public void setTankId( final Integer tankId )
    {
        this.tankId = tankId;
    }

    public String getTankName()
    {
        return tankName;
    }

    public void setTankName( final String tankName )
    {
        this.tankName = tankName;
    }

    public Integer getProductType()
    {
        return productType;
    }

    public void setProductType( final Integer productType )
    {
        this.productType = productType;
    }

    public BigDecimal getMaxCapacity()
    {
        return maxCapacity;
    }

    public void setMaxCapacity( final BigDecimal maxCapacity )
    {
        this.maxCapacity = maxCapacity;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( final String status )
    {
        this.status = status;
    }
    
    

}
