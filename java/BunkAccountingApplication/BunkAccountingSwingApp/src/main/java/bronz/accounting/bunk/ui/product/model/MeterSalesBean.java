package bronz.accounting.bunk.ui.product.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import bronz.accounting.bunk.tankandmeter.model.MeterClosingReading;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=true, uniqueIdCol=true )
public class MeterSalesBean
{
    @DataTableField( columnName="METER", preferedWidth=15, allowNull=false, columnNum=0, isEditable=false)
    private MeterClosingReading meter;
    @DataTableField( columnName="OPENING READING", preferedWidth=20, allowNull=false, columnNum=1, isEditable=false)
    private BigDecimal openingReading = new CustomDecimal( 0 );
    @DataTableField( columnName="CLOSING READING", preferedWidth=20, allowNull=false, columnNum=2, isEditable=true)
    private BigDecimal closingReading = new CustomDecimal( 0 );
    @DataTableField( columnName="TOTAL SALES", preferedWidth=15, allowNull=false, columnNum=3, isEditable=false)
    private BigDecimal totalSales = new CustomDecimal( 0 );
    @DataTableField( columnName="TEST IN LITRES", preferedWidth=15, allowNull=false, columnNum=4, isEditable=true)
    private BigDecimal testLitres = new CustomDecimal( 0 );
    @DataTableField( columnName="ACTUAL SALE", preferedWidth=15, allowNull=false, columnNum=5, isEditable=false)
    private BigDecimal sale = new CustomDecimal( 0 );
    
    public MeterSalesBean( final MeterClosingReading meter)
    {
        super();
        setMeter( meter );
    }

    /**
     * @return the meter
     */
    public MeterClosingReading getMeter()
    {
        return meter;
    }

    /**
     * @param meter the meter to set
     */
    public void setMeter( MeterClosingReading meter )
    {
        this.meter = meter;
        if ( null != meter )
        {
            this.openingReading = meter.getFinalReading().setScale( 2, RoundingMode.HALF_UP );
            this.closingReading = meter.getFinalReading().setScale( 2, RoundingMode.HALF_UP );
            this.testLitres = new CustomDecimal( 0 );
            this.sale = new CustomDecimal( 0 );
        }
    }

    /**
     * @return the totalSales
     */
    public BigDecimal getTotalSales()
    {
        return totalSales;
    }

    /**
     * @param totalSales the totalSales to set
     */
    public void setTotalSales( BigDecimal totalSales )
    {
        this.totalSales = totalSales;
    }

    /**
     * @return the openingReading
     */
    public BigDecimal getOpeningReading()
    {
        return BunkUtil.setAsMeterReading( openingReading );
    }

    /**
     * @param openingReading the openingReading to set
     */
    public void setOpeningReading( BigDecimal openingReading )
    {
        this.openingReading = openingReading.setScale( 2, RoundingMode.HALF_UP );
    }

    /**
     * @return the closingReading
     */
    public BigDecimal getClosingReading()
    {
        return BunkUtil.setAsMeterReading( closingReading );
    }

    /**
     * @param closingReading the closingReading to set
     */
    public void setClosingReading( BigDecimal closingReading )
    {
        if ( closingReading.floatValue() > this.openingReading.floatValue() )
        {
            this.closingReading = closingReading.setScale( 2, RoundingMode.HALF_UP );
            this.totalSales = BunkUtil.setAsMeterReading( closingReading.setScale(
                    2, RoundingMode.HALF_UP ).subtract( openingReading.setScale( 2, RoundingMode.HALF_UP ) ) );
            setTestLitres( getTestLitres() );
        }
        else
        {
            this.closingReading = meter.getFinalReading();
            this.testLitres = new CustomDecimal( 0 );
            this.totalSales = new CustomDecimal( 0 );
            this.sale = new CustomDecimal( 0 );
        }
        
        
    }

    /**
     * @return the testLitres
     */
    public BigDecimal getTestLitres()
    {
        return testLitres;
    }

    /**
     * @param testLitres the testLitres to set
     */
    public void setTestLitres( BigDecimal testLitres )
    {
        if ( this.closingReading.subtract( this.openingReading ).floatValue() >= testLitres.floatValue() )
        {
            this.testLitres = testLitres;
        }
        else
        {
            this.testLitres = new CustomDecimal( 0 );
        }
        this.sale = BunkUtil.setAsMeterReading( this.totalSales.subtract( this.testLitres ));
        
    }

    /**
     * @return the sale
     */
    public BigDecimal getSale()
    {
        return sale;
    }

    /**
     * @param sale the sale to set
     */
    public void setSale( BigDecimal sale )
    {
        this.sale = sale;
    }

}
