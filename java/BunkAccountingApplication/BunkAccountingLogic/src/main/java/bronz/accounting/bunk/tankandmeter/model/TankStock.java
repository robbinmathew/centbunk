package bronz.accounting.bunk.tankandmeter.model;

import java.math.BigDecimal;

public class TankStock
{
	public static final String DAY_CLOSING = "DAY_CLOSING";
    private Integer slNo;
    private Integer tankId;
    private BigDecimal dip;
    private String detail;
    private BigDecimal actualStock;
    private int date;
    private String type;
    public Integer getSlNo()
    {
        return slNo;
    }
    public void setSlNo( Integer slNo )
    {
        this.slNo = slNo;
    }
    public Integer getTankId()
    {
        return tankId;
    }
    public void setTankId( Integer tankId )
    {
        this.tankId = tankId;
    }
    public BigDecimal getDip()
    {
        return dip;
    }
    public void setDip( BigDecimal dip )
    {
        this.dip = dip;
    }
    public String getDetail()
    {
        return detail;
    }
    public void setDetail( String detail )
    {
        this.detail = detail;
    }
    public BigDecimal getActualStock()
    {
        return actualStock;
    }
    public void setActualStock( BigDecimal actualStock )
    {
        this.actualStock = actualStock;
    }
    public int getDate()
    {
        return date;
    }
    public void setDate( int date )
    {
        this.date = date;
    }
    public String getType()
    {
        return type;
    }
    public void setType( String type )
    {
        this.type = type;
    }
}