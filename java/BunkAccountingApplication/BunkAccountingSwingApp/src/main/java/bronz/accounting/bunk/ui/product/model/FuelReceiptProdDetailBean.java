package bronz.accounting.bunk.ui.product.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=true, uniqueIdCol=true, errorValidationMethod="errorValidation" )
public class FuelReceiptProdDetailBean
{
    @DataTableField( columnName="PRODUCT", preferedWidth=30, allowNull=false, columnNum=0, isEditable=false)
    private ProductClosingBalance product;
    @DataTableField( columnName="COST ON INV", preferedWidth=15, allowNull=false, columnNum=3)
    private BigDecimal totalCost = new CustomDecimal( 0 );
    @DataTableField( columnName="RECEIPT AMT", preferedWidth=20, allowNull=false, columnNum=1, isEditable=false)
    private BigDecimal receiptAmt = new CustomDecimal( 0 );
    @DataTableField( columnName="MARGIN PER UNIT", preferedWidth=15, allowNull=false, columnNum=4, isEditable=false)
    private BigDecimal margin = new CustomDecimal( 0 );
    @DataTableField( columnName="FINAL STOCK", preferedWidth=20, allowNull=false, columnNum=2, isEditable=false)
    private BigDecimal finalStock = new CustomDecimal( 0 );
    
    private BigDecimal ratePerUnit = new BigDecimal( 0 );
    private final List<TankReceiptDetailBean> tankList;
    
    public FuelReceiptProdDetailBean( final ProductClosingBalance product,
            final List<TankReceiptDetailBean> tankList )
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
            this.ratePerUnit = product.getUnitSellingPrice();
            this.finalStock = product.getClosingStock();
            updateMargin();
        }
    }

    public BigDecimal getReceiptAmt()
    {
        //Update the receipt value
        if ( null != this.tankList )
        {
            BigDecimal receipt = new BigDecimal( 0 );
            for ( TankReceiptDetailBean bean : this.tankList )
            {
                receipt = receipt.add( bean.getReceiptAmt() );
            }
            this.receiptAmt = receipt;
            updateMargin();
        }
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
    
    public BigDecimal getFinalStock()
    {
        return finalStock;
    }

    public void setFinalStock( final BigDecimal finalStock )
    {
        this.finalStock = finalStock;
    }

    private void updateMargin()
    {
        if ( ValidationUtil.isValidPositiveNumber( this.receiptAmt ) )
        {
            this.margin = this.ratePerUnit.subtract( this.totalCost.divide(
                    this.receiptAmt, 2, RoundingMode.HALF_UP ) );
            this.finalStock = this.product.getClosingStock().add(
                    this.receiptAmt ).setScale( 3, RoundingMode.HALF_UP );
        }
        else
        {
            this.margin = new BigDecimal( 0 );
            this.finalStock = this.product.getClosingStock();
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
    
    public String errorValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if ( null != this.receiptAmt && 0 < this.receiptAmt.intValue() )
        {
            if ( this.margin.floatValue() > 10 || this.margin.floatValue() <= 0 )
            {
                errorMsg.append( "Total cost of " );
                errorMsg.append( this.product.getProductName() );
                errorMsg.append( " is incorrect. The margin per unit is too high or less than zero" );
            }
        }
        return errorMsg.toString();
    }
}
