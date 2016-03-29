package bronz.accounting.bunk.ui.product.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=false, uniqueIdCol=true, errorValidationMethod="errorValidation" )
public class LubeSalesProdDetailBean
{
    @DataTableField( columnName="PRODUCT", preferedWidth=35, allowNull=false, columnNum=0)
    private ProductClosingBalance product;
    @DataTableField( columnName="MARGIN", preferedWidth=10, allowNull=false, columnNum=1, isEditable=false)
    private BigDecimal rate = new CustomDecimal( 0 );
    @DataTableField( columnName="SALE", preferedWidth=10, allowNull=false, columnNum=2)
    private BigDecimal saleAmount = new CustomDecimal( 0 );
    @DataTableField( columnName="DISCOUNT PER UNIT", preferedWidth=15, allowNull=false, columnNum=3)
    private BigDecimal discountPerUnit = new CustomDecimal( 0 );
    @DataTableField( columnName="TOTAL CASH", preferedWidth=15, allowNull=false, columnNum=4, isEditable=false)
    private BigDecimal totalSaleCash = new CustomDecimal( 0 );
    @DataTableField( columnName="STOCK AFTER SALE", preferedWidth=15, allowNull=false, columnNum=5, isEditable=false)
    private BigDecimal stockAfterSale = new CustomDecimal( 0 );
    
    public LubeSalesProdDetailBean( final ProductClosingBalance product)
    {
        super();
        setProduct( product );
    }
    
    public LubeSalesProdDetailBean()
    {
        super();
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
            this.rate = product.getMargin();
            this.stockAfterSale = product.getClosingStock();
            this.setSaleAmount( this.saleAmount );
            this.setDiscountPerUnit( this.discountPerUnit );
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
        this.rate = rate;
    }

    /**
     * @return the discountPerUnit
     */
    public BigDecimal getDiscountPerUnit()
    {
        return discountPerUnit;
    }

    /**
     * @param discountPerUnit the discountPerUnit to set
     */
    public void setDiscountPerUnit( BigDecimal discountPerUnit )
    {
        if ( this.product != null && this.product.getUnitSellingPrice().compareTo( discountPerUnit) >=0 )
        {
            this.discountPerUnit = discountPerUnit;
            setTotalSaleCash(this.product.getUnitSellingPrice().subtract( this.discountPerUnit ).multiply( this.saleAmount ));
        }
        else
        {
            this.discountPerUnit = new CustomDecimal( 0 );
        }
    }

    /**
     * @return the totalSaleCash
     */
    public BigDecimal getTotalSaleCash()
    {
        setDiscountPerUnit( discountPerUnit );
        return totalSaleCash;
    }

    /**
     * @param totalSaleCash the totalSaleCash to set
     */
    public void setTotalSaleCash( BigDecimal totalSaleCash )
    {
        this.totalSaleCash = totalSaleCash.setScale( 2, RoundingMode.HALF_UP );
    }

    /**
     * @return the stockAfterSale
     */
    public BigDecimal getStockAfterSale()
    {
        return stockAfterSale;
    }

    /**
     * @param stockAfterSale the stockAfterSale to set
     */
    public void setStockAfterSale( BigDecimal stockAfterSale )
    {
        this.stockAfterSale = stockAfterSale;
    }

    /**
     * @return the saleAmount
     */
    public BigDecimal getSaleAmount()
    {
        return saleAmount;
    }

    /**
     * @param saleAmount the saleAmount to set
     */
    public void setSaleAmount( BigDecimal saleAmount )
    {
        if ( saleAmount.floatValue() >= 0 )
        {
            if ( null != product )
            {
                if (this.product.getProductId() == 5 )
                {
                    this.saleAmount = saleAmount.setScale( 3, RoundingMode.HALF_UP );
                }
                else
                {
                    this.saleAmount = saleAmount.setScale( 0, RoundingMode.HALF_UP );
                }
                this.stockAfterSale = this.product.getClosingStock().subtract( this.saleAmount );
            }
            setDiscountPerUnit( this.discountPerUnit );
        }
    }
    
    public String errorValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if ( null != this.stockAfterSale && 0 < this.saleAmount.intValue() )
        {
            if ( this.stockAfterSale.floatValue() < 0)
            {
                errorMsg.append( "The stock after sale for product " );
                errorMsg.append( product.toString() );
                errorMsg.append( " is less than zero. Check if you entered all stock receipts." );
            }
        }
        return errorMsg.toString();
    }
}
