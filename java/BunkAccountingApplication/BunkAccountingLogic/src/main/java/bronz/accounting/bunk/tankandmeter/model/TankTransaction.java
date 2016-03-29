package bronz.accounting.bunk.tankandmeter.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class TankTransaction
{
	public static final String FILL = "FILL";
	public static final String FILL_SYSTEM = "FILL_S";
	public static final String TEST = "TEST";
	public static final String SALE = "SALE";
	public static final String DIFF = "DIFF";
	
	public static final List<String> CREDIT_TRANS_TYPES = Arrays.asList( FILL, TEST,
			FILL_SYSTEM, DIFF );
	
    private Integer slNo;
    private Integer tankId;
    private String detail;
    private BigDecimal quantity;
    private BigDecimal balance;
    private BigDecimal density;
    private Integer date;
    private String transType;
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
    public String getDetail()
    {
        return detail;
    }
    public void setDetail( String detail )
    {
        this.detail = detail;
    }
    public BigDecimal getQuantity()
    {
        return quantity;
    }
    public void setQuantity( BigDecimal quantity )
    {
        this.quantity = quantity;
    }
    public BigDecimal getBalance()
    {
        return balance;
    }
    public void setBalance( BigDecimal balance )
    {
        this.balance = balance;
    }
    public BigDecimal getDensity()
    {
        return density;
    }
    public void setDensity( BigDecimal density )
    {
        this.density = density;
    }
    public Integer getDate()
    {
        return date;
    }
    public void setDate( Integer date )
    {
        this.date = date;
    }
    public String getTransType()
    {
        return transType;
    }
    public void setTransType( String transType )
    {
        this.transType = transType;
    }
    

}
    