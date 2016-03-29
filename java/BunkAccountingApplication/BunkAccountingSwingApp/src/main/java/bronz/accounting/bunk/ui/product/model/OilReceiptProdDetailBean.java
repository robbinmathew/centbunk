package bronz.accounting.bunk.ui.product.model;


import java.math.BigDecimal;
import java.math.RoundingMode;

import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( uniqueIdCol=true, errorValidationMethod="errorValidation" )
public class OilReceiptProdDetailBean
{
    @DataTableField( columnName="PRODUCT", preferedWidth=40, allowNull=false, columnNum=0)
    private ProductClosingBalance product;
    @DataTableField( columnName="RATE", preferedWidth=15, allowNull=false, columnNum=1, isEditable=false)
    private BigDecimal finalRatePerUnit = new BigDecimal( 0 );
    @DataTableField( columnName="RECEIPT AMT", preferedWidth=15, allowNull=false, columnNum=2)
    private BigDecimal receiptAmt = new CustomDecimal( 0 );
    @DataTableField( columnName="FINAL STOCK", preferedWidth=20, allowNull=false, columnNum=3, isEditable=false)
    private BigDecimal finalStock = new CustomDecimal( 0 );
    @DataTableField( columnName="COST ON INV", preferedWidth=15, allowNull=false, columnNum=4)
    private BigDecimal totalCost = new CustomDecimal( 0 );
    @DataTableField( columnName="MARGIN PER UNIT", preferedWidth=15, allowNull=false, columnNum=5, isEditable=false)
    private BigDecimal margin = new CustomDecimal( 0 );
    
    private BigDecimal ratePerUnit = new BigDecimal( 0 );
    
    public OilReceiptProdDetailBean()
    {
        super();
    }

    public ProductClosingBalance getProduct()
    {
        return this.product;
    }

    public void setProduct( final ProductClosingBalance product )
    {
        this.product = product;
        if ( null != product )
        {
            this.ratePerUnit = product.getUnitSellingPrice();
            this.finalRatePerUnit = this.ratePerUnit;
            this.finalStock = product.getClosingStock();
            updateMargin();
        }
    }

    public BigDecimal getReceiptAmt()
    {
        return receiptAmt;
    }

    public void setReceiptAmt( final BigDecimal receiptAmt )
    {
        this.receiptAmt = receiptAmt;
        if ( null != receiptAmt )
        {
            updateMargin();
        }
    }
    
    private void updateMargin()
    {
        if ( ValidationUtil.isValidPositiveNumber( this.receiptAmt ) )
        {
            this.margin = this.finalRatePerUnit.subtract( this.totalCost.divide(
                    this.receiptAmt, 2, RoundingMode.HALF_UP ) );
            this.finalStock = this.product.getClosingStock().add( this.receiptAmt );
        }
    }

    public BigDecimal getTotalCost()
    {
        return this.totalCost;
    }

    public void setTotalCost( final BigDecimal totalCost )
    {
        this.totalCost = totalCost;
        if ( null != totalCost )
        {
            updateMargin();
        }
    }

    public BigDecimal getMargin()
    {
        return this.margin;
    }

    public void setMargin( final BigDecimal margin )
    {
        this.margin = margin;
    }
    
    public BigDecimal getFinalRatePerUnit()
    {
        return finalRatePerUnit;
    }

    public void setFinalRatePerUnit( final BigDecimal finalRatePerUnit )
    {
        this.finalRatePerUnit = BunkUtil.setAsPrice( finalRatePerUnit );
    }

    public String errorValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if ( null != this.receiptAmt && 0 < this.receiptAmt.intValue() )
        {
            if ( this.margin.floatValue() > 1000 || this.margin.floatValue() <= 0 )
            {
                errorMsg.append( "Total cost of " );
                errorMsg.append( this.product.getProductName() );
                errorMsg.append( " is incorrect. The margin per unit is too high or less than zero" );
            }
        }
        return errorMsg.toString();
    }
    
    public boolean isNewRate()
    {
        return this.finalRatePerUnit.equals( this.ratePerUnit );
    }

    public BigDecimal getFinalStock()
    {
        return finalStock;
    }

    public void setFinalStock( final BigDecimal finalStock )
    {
        this.finalStock = finalStock;
    }
}
