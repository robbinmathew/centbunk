package bronz.accounting.bunk.ui.product.model;

import java.math.BigDecimal;
import java.util.List;

import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;
import bronz.utilities.swing.util.SwingUtil;

@DataTable( fixNoOfRows=true, errorValidationMethod="errorValidation", warningValidationMethod="warningValidation" )
public class TankSaleDetailBean
{
    @DataTableField( columnName="TANK NAME", preferedWidth=17, allowNull=false, columnNum=0, isEditable=false)
    private TankClosingStock tank;
    @DataTableField( columnName="OPENING STOCK", preferedWidth=13, allowNull=false, columnNum=1, isEditable=false)
    private BigDecimal openingStock = new CustomDecimal( 0 );
    @DataTableField( columnName="SALE", preferedWidth=8, allowNull=false, columnNum=2, isEditable=false)
    private BigDecimal saleAmt = new CustomDecimal( 0 );
    @DataTableField( columnName="<html>STOCK <font size=-2>(AFTER SALE)</font></html>", preferedWidth=15, allowNull=false, columnNum=3, isEditable=false)
    private BigDecimal stock = new CustomDecimal( 0 );
    @DataTableField( columnName="TEST", preferedWidth=7, allowNull=false, columnNum=4, isEditable=false)
    private BigDecimal test = new CustomDecimal( 0 );
    @DataTableField( columnName="<html>DIP STOCK <font size=-2>(CLOSING)</font></html>", displayName="CLOSING DIP STOCK", preferedWidth=16, allowNull=false, columnNum=5, isEditable=true)
    private BigDecimal dipStock = new CustomDecimal( 0 );
    @DataTableField( columnName="<html>DIFF <font size=-2>(TODAY)</font></html>", preferedWidth=9, allowNull=false, columnNum=6, isEditable=false)
    private BigDecimal stockDiff = new CustomDecimal( 0 );
    @DataTableField( columnName="<html>DIFF <font size=-2>(THIS MONTH)</font></html>", preferedWidth=15, allowNull=false, columnNum=7, isEditable=false)
    private BigDecimal cumDiffThisMonth = new CustomDecimal( 0 );
    
    private BigDecimal totalSale = new CustomDecimal( 0 );
    
    private final List<MeterSalesBean> meterBeans;
    
    public TankSaleDetailBean( final TankClosingStock tank, final List<MeterSalesBean> meterBeans )
    {
        super();
        this.setTank( tank );
        this.meterBeans = meterBeans;
    }

    public TankClosingStock getTank()
    {
        return this.tank;
    }

    public void setTank( final TankClosingStock tank )
    {
        this.tank = tank;
        if ( null != tank )
        {
            this.openingStock = tank.getClosingStock();
            this.stock = tank.getClosingStock();
            if ( null == tank.getDiffThisMonth() )
            {
            	this.cumDiffThisMonth = BigDecimal.ZERO;
            }
            else
            {
            	this.cumDiffThisMonth = tank.getDiffThisMonth();
            }
            
        }
    }
    
    public Integer getTankId()
    {
        Integer tankId = null;
        if ( this.tank != null )
        {
            tankId = this.tank.getTankId();
        }
        return tankId;
    }
    
    public Integer getProdId()
    {
        Integer prodId = null;
        if ( this.tank != null )
        {
        	prodId = this.tank.getProductId();
        }
        return prodId;
    }

    public BigDecimal getOpeningStock()
    {
        return this.openingStock;
    }

    public void setOpeningStock( final BigDecimal openingStock )
    {
        this.openingStock = openingStock;
    }

    public BigDecimal getSaleAmt()
    {
        //Update the sale value
        if ( null != this.meterBeans )
        {
            BigDecimal sale = new BigDecimal( 0 );
            BigDecimal test = new BigDecimal( 0 );
            BigDecimal total = new BigDecimal( 0 );
            for ( MeterSalesBean bean : this.meterBeans )
            {
                sale = sale.add( bean.getSale() );
                test = test.add( bean.getTestLitres() );
                total = total.add( bean.getTotalSales() );
            }
            this.saleAmt = sale;
            this.totalSale = total;
            this.test = test;
            this.stock = BunkUtil.setAsProdVolume( this.openingStock.subtract( this.saleAmt ) );
        }
        return this.saleAmt;
    }

    public void setSaleAmt( final BigDecimal saleAmt )
    {
        this.saleAmt = saleAmt;
    }

    public BigDecimal getStock()
    {
        return this.stock;
    }

    public void setStock( final BigDecimal stock )
    {
        this.stock = stock;
    }

    /**
     * @return the test
     */
    public BigDecimal getTest()
    {
        return test;
    }

    /**
     * @param test the test to set
     */
    public void setTest( final BigDecimal test )
    {
        this.test = test;
    }

    /**
     * @return the totalSale
     */
    public BigDecimal getTotalSale()
    {
        return totalSale;
    }

    /**
     * @return the dipStock
     */
    public BigDecimal getDipStock()
    {
        return dipStock;
    }

    /**
     * @param dipStock the dipStock to set
     */
    public void setDipStock( BigDecimal dipStock )
    {
        this.dipStock = dipStock;
        setStockDiff( this.dipStock.subtract( this.stock ));
    }

    /**
     * @return the stockDiff
     */
    public BigDecimal getStockDiff()
    {
        return BunkUtil.setAsProdVolume(stockDiff);
    }

    /**
     * @param stockDiff the stockDiff to set
     */
    public void setStockDiff( BigDecimal stockDiff )
    {
        this.stockDiff = stockDiff;
    }
    
    public BigDecimal getCumDiffThisMonth() {
		return BunkUtil.setAsProdVolume(this.cumDiffThisMonth.add(this.stockDiff));
	}

	public void setCumDiffThisMonth(BigDecimal cumDiffThisMonth) {
		this.cumDiffThisMonth = cumDiffThisMonth;
	}

	public String errorValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if ( this.saleAmt.compareTo(BigDecimal.ZERO) > 0 )
        {
            if ( this.stock.compareTo(BigDecimal.ZERO) < 0)
            {
                errorMsg.append( "--The stock after sale for tank " );
                errorMsg.append( tank.toString() );
                errorMsg.append( " is less than zero. Check if you entered all stock receipts." +
                		"\n For products with multiple tanks, verify if the receipts are added to the correct tanks" );
            }
            if ( null == this.dipStock || this.dipStock.compareTo(BigDecimal.ZERO) <= 0)
            {
                SwingUtil.appendMessage( errorMsg, "--Enter the closing dip stock of the tank " + tank.toString() );
            }
        }
        return errorMsg.toString();
    }
    
    public String warningValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if ( null != this.stock && null != this.stockDiff && this.saleAmt.compareTo(BigDecimal.ZERO) > 0 )
        {
        	if ( this.stockDiff.intValue() >= 1000 || this.stockDiff.intValue() <= -1000)
            {
                errorMsg.append( "--The stock diff is huge for tank " );
                errorMsg.append( tank.toString() );
            }
        }
        return errorMsg.toString();
    }
}

