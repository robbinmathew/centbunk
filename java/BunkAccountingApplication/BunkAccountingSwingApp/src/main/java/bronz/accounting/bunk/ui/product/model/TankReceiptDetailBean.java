package bronz.accounting.bunk.ui.product.model;

import java.math.BigDecimal;

import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=true )
public class TankReceiptDetailBean
{
    @DataTableField( columnName="TANK NAME", preferedWidth=30, allowNull=false, columnNum=0, isEditable=false)
    private TankClosingStock tank;
    @DataTableField( columnName="PRODUCT", preferedWidth=20, allowNull=false, columnNum=1, isEditable=false)
    private String productType;
    @DataTableField( columnName="OPENING STOCK", preferedWidth=15, allowNull=false, columnNum=2, isEditable=false)
    private BigDecimal openingStock = new CustomDecimal( 0 );
    @DataTableField( columnName="RECEIPT AMT", preferedWidth=15, allowNull=false, columnNum=3)
    private BigDecimal receiptAmt = new CustomDecimal( 0 );
    @DataTableField( columnName="STOCK AFTER RECEIPT", preferedWidth=20, allowNull=false, columnNum=4, isEditable=false)
    private BigDecimal stock = new CustomDecimal( 0 );
    
    public TankReceiptDetailBean( final TankClosingStock tank )
    {
        super();
        this.setTank( tank );
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
            this.productType = tank.getProductType();
            this.openingStock = tank.getClosingStock();
            this.stock = tank.getClosingStock();
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

    public String getProductType()
    {
        return this.productType;
    }

    public void setProductType( final String productType )
    {
        this.productType = productType;
    }

    public BigDecimal getOpeningStock()
    {
        return this.openingStock;
    }

    public void setOpeningStock( final BigDecimal openingStock )
    {
        this.openingStock = openingStock;
    }

    public BigDecimal getReceiptAmt()
    {
        return this.receiptAmt;
    }

    public void setReceiptAmt( final BigDecimal receiptAmt )
    {
        this.receiptAmt = receiptAmt;
        if ( ValidationUtil.isValidNumber( receiptAmt ) )
        {
            this.stock = BunkUtil.setAsProdVolume(
                    this.openingStock.add( receiptAmt ) );
        }
    }

    public BigDecimal getStock()
    {
        return this.stock;
    }

    public void setStock( final BigDecimal stock )
    {
        this.stock = stock;
    }
    
}

