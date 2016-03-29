package bronz.accounting.bunk.products.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import bronz.accounting.bunk.util.BunkUtil;

public class StockVariation
{
	private int productId;
	private String prodName;
	private BigDecimal thisMonthSale;
	private BigDecimal thisMonthVariation;
	private BigDecimal thisMonthVarAmt;
	private BigDecimal prevMonthVariation;
	private BigDecimal prevMonthVarPercetage;
	
	public int getProductId()
	{
		return productId;
	}
	public void setProductId(int productId)
	{
		this.productId = productId;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName)
	{
		this.prodName = prodName;
	}
	public BigDecimal getThisMonthSale()
	{
		return BunkUtil.setAsProdVolume(overrideNullWithZero(thisMonthSale));
	}
	public void setThisMonthSale(BigDecimal thisMonthSale)
	{
		this.thisMonthSale = thisMonthSale;
	}
	public BigDecimal getThisMonthVariation()
	{
		return BunkUtil.setAsProdVolume(overrideNullWithZero(thisMonthVariation));
	}
	public void setThisMonthVariation(BigDecimal thisMonthVariation)
	{
		this.thisMonthVariation = thisMonthVariation;
	}
	public String getThisMonthVarPercetage()
	{
		if (BigDecimal.ZERO.compareTo(overrideNullWithZero(this.thisMonthSale)) >= 0)
		{
			return "NA";
		}
		else
		{
			return BunkUtil.setAsPrice(overrideNullWithZero(this.thisMonthVariation).divide(
				overrideNullWithZero(this.thisMonthSale), 4, RoundingMode.HALF_UP).multiply(
						new BigDecimal(100))).toPlainString();
		}
	}
	public BigDecimal getThisMonthVarAmt()
	{
		return overrideNullWithZero(thisMonthVarAmt);
	}
	public void setThisMonthVarAmt(BigDecimal thisMonthVarAmt)
	{
		this.thisMonthVarAmt = thisMonthVarAmt;
	}
	public BigDecimal getPrevMonthVariation()
	{
		return BunkUtil.setAsProdVolume(overrideNullWithZero(prevMonthVariation));
	}
	public void setPrevMonthVariation(BigDecimal prevMonthVariation)
	{
		this.prevMonthVariation = prevMonthVariation;
	}
	public BigDecimal getPrevMonthVarPercetage()
	{
		return BunkUtil.setAsPrice(overrideNullWithZero(prevMonthVarPercetage));
	}
	public void setPrevMonthVarPercetage(BigDecimal prevMonthVarPercetage)
	{
		this.prevMonthVarPercetage = prevMonthVarPercetage;
	}
	
	private BigDecimal overrideNullWithZero(final BigDecimal value)
	{
		if (value == null)
		{
			return BigDecimal.ZERO;
		}
		else
		{
			return value;
		}
	}

}
