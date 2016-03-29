package bronz.accounting.bunk.ui.product.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=true, uniqueIdCol=true )
public class FuelSalesProdDetailBean
{
    @DataTableField( columnName="PRODUCT", preferedWidth=28, allowNull=false, columnNum=0, isEditable=false)
    private ProductClosingBalance product;
    //@DataTableField( columnName="RATE", preferedWidth=10, allowNull=false, columnNum=1, isEditable=false)
    private BigDecimal rate = new CustomDecimal( 0 );
    @DataTableField( columnName="TOTAL", preferedWidth=10, allowNull=false, columnNum=1, isEditable=false)
    private BigDecimal totalSaleAmt = new CustomDecimal( 0 );
    @DataTableField( columnName="TOTAL CASH", preferedWidth=14, allowNull=false, columnNum=2, isEditable=false)
    private BigDecimal totalSaleCash = new CustomDecimal( 0 );
    @DataTableField( columnName="SALE", preferedWidth=10, allowNull=false, columnNum=3, isEditable=false)
    private BigDecimal saleAmt = new CustomDecimal( 0 );
    @DataTableField( columnName="SALE CASH", preferedWidth=14, allowNull=false, columnNum=4, isEditable=false)
    private BigDecimal saleCash = new CustomDecimal( 0 );
    @DataTableField( columnName="TEST", preferedWidth=10, allowNull=false, columnNum=5, isEditable=false)
    private BigDecimal testAmt = new CustomDecimal( 0 );
    @DataTableField( columnName="TEST CASH", preferedWidth=14, allowNull=false, columnNum=6, isEditable=false)
    private BigDecimal testCash = new CustomDecimal( 0 );
    
    private final List<TankSaleDetailBean> tankList;
    
    public FuelSalesProdDetailBean( final ProductClosingBalance product,
            final List<TankSaleDetailBean> tankList )
    {
        super();
        this.tankList = tankList;
        this.setProduct( product );
    }

    public ProductClosingBalance getProduct()
    {
        return product;
    }

    public void setProduct( final ProductClosingBalance product )
    {
        this.product = product;
        if ( null != product )
        {
            this.rate = product.getUnitSellingPrice().setScale( 2, RoundingMode.HALF_UP );
        }
    }

    /**
     * @return the rate
     */
    public BigDecimal getRate()
    {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate( BigDecimal rate )
    {
        this.rate = rate.setScale( 2, RoundingMode.HALF_UP );
    }

    /**
     * @return the saleAmt
     */
    public BigDecimal getSaleAmt()
    {
        return saleAmt;
    }

    /**
     * @param saleAmt the saleAmt to set
     */
    public void setSaleAmt( BigDecimal saleAmt )
    {
        this.saleAmt = saleAmt.setScale( 3, RoundingMode.HALF_UP );
    }

    /**
     * @return the saleCash
     */
    public BigDecimal getSaleCash()
    {
        return saleCash;
    }

    /**
     * @param saleCash the saleCash to set
     */
    public void setSaleCash( BigDecimal saleCash )
    {
        this.saleCash = saleCash.setScale( 2, RoundingMode.HALF_UP );
    }

    /**
     * @return the testAmt
     */
    public BigDecimal getTestAmt()
    {
        return testAmt;
    }

    /**
     * @param testAmt the testAmt to set
     */
    public void setTestAmt( BigDecimal testAmt )
    {
        this.testAmt = testAmt.setScale( 3, RoundingMode.HALF_UP );
    }

    /**
     * @return the testCash
     */
    public BigDecimal getTestCash()
    {
        return testCash;
    }

    /**
     * @param testCash the testCash to set
     */
    public void setTestCash( BigDecimal testCash )
    {
        this.testCash = testCash.setScale( 2, RoundingMode.HALF_UP );
    }

    /**
     * @return the tankList
     */
    public List<TankSaleDetailBean> getTankList()
    {
        return tankList;
    }

    /**
     * @return the totalSaleAmt
     */
    public BigDecimal getTotalSaleAmt()
    {
        if ( null != this.tankList )
        {
            BigDecimal sale = new BigDecimal( 0 );
            BigDecimal test = new BigDecimal( 0 );
            BigDecimal total = new BigDecimal( 0 );
            for ( TankSaleDetailBean bean : this.tankList )
            {
                sale = sale.add( bean.getSaleAmt() );
                test = test.add( bean.getTest() );
                total = total.add( bean.getTotalSale() );
            }
            this.saleAmt = sale.setScale( 3, RoundingMode.HALF_UP );
            this.saleCash = sale.multiply( this.rate ).setScale( 2, RoundingMode.HALF_UP );
            this.testAmt = test.setScale( 3, RoundingMode.HALF_UP );
            this.testCash = test.multiply( this.rate ).setScale( 2, RoundingMode.HALF_UP );
            this.totalSaleAmt = total.setScale( 3, RoundingMode.HALF_UP );
            this.totalSaleCash = total.multiply( this.rate ).setScale( 2, RoundingMode.HALF_UP );
        }
        return totalSaleAmt.setScale( 3, RoundingMode.HALF_UP );
    }

    /**
     * @param totalSaleAmt the totalSaleAmt to set
     */
    public void setTotalSaleAmt( BigDecimal totalSaleAmt )
    {
        this.totalSaleAmt = totalSaleAmt.setScale( 3, RoundingMode.HALF_UP );
    }

    /**
     * @return the totalSaleCash
     */
    public BigDecimal getTotalSaleCash()
    {
        return totalSaleCash;
    }

    /**
     * @param totalSaleCash the totalSaleCash to set
     */
    public void setTotalSaleCash( BigDecimal totalSaleCash )
    {
        this.totalSaleCash = totalSaleCash.setScale( 2, RoundingMode.HALF_UP );
    }

}
