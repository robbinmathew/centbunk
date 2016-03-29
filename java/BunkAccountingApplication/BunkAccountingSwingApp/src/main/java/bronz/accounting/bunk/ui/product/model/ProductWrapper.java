package bronz.accounting.bunk.ui.product.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.table.DataTable;
import bronz.utilities.swing.table.DataTableField;

@DataTable( fixNoOfRows=false, minNoOfRows=5, errorValidationMethod="errorValidation",editableColumnListMethod="getEditableColList" )
public class ProductWrapper
{
    @DataTableField( columnName="PRODUCT NAME", preferedWidth=50, allowNull=false, columnNum=0)
    private String prodName;
    @DataTableField( columnName="RATE", preferedWidth=15, allowNull=false, columnNum=1)
    private BigDecimal rate = new CustomDecimal( 0 );
    @DataTableField( columnName="MARGIN", preferedWidth=15, allowNull=false, columnNum=2)
    private BigDecimal margin = new CustomDecimal( 0 );
    @DataTableField( columnName="STOCK", preferedWidth=20, allowNull=false, columnNum=3, isEditable=false)
    private BigDecimal stock = new CustomDecimal( 0 );
    
    private List<Integer> editableColList = null;
    private ProductClosingBalance product;
    
    public ProductWrapper( final ProductClosingBalance product )
    {
        super();
        this.setProduct( product );
    }
    
    public ProductWrapper()
    {
        super();
        this.setProduct( null );
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
            this.prodName = product.getProductName();
            this.rate = product.getUnitSellingPrice();
            this.stock = product.getClosingStock();
            this.margin = product.getMargin();
            if (this.stock.compareTo( CustomDecimal.ZERO ) > 0 )
            {
                this.editableColList = Arrays.asList( 0 );
            }
            else
            {
                this.editableColList = null;
            }
        }
        else
        {
            this.prodName = "";
            this.rate = CustomDecimal.ZERO;
            this.stock = CustomDecimal.ZERO;
            this.margin = CustomDecimal.ZERO;
            this.editableColList = null;
        }
    }
    
    public boolean isProdUpdated()
    {
        if (null != this.product && !this.product.getProductName().equals( this.prodName )) {
            return true;
        }
        return false;
    }
    
    public boolean isRateUpdated()
    {
        if (null != this.product && (!this.product.getMargin().equals( this.margin ) ||
                !this.product.getUnitSellingPrice().equals( this.rate ))) {
            return true;
        }
        return false;
    }

    
    public String getProdName()
    {
        return prodName;
    }

    public void setProdName( String prodName )
    {
        this.prodName = prodName.toUpperCase().trim();
    }

    public BigDecimal getRate()
    {
        return rate;
    }

    public void setRate( BigDecimal rate )
    {
        this.rate = rate;
        setMargin( this.margin );
    }

    public BigDecimal getMargin()
    {
        return margin;
    }

    public void setMargin( BigDecimal margin )
    {
        if (this.rate.compareTo( margin ) > 0 || CustomDecimal.ZERO.compareTo( margin ) < 0 )
        {
            this.margin = margin;
        }
        else
        {
            this.margin = CustomDecimal.ZERO;
        }
    }

    public BigDecimal getStock()
    {
        return stock;
    }

    public void setStock( BigDecimal stock )
    {
        this.stock = stock;
    }

    public String errorValidation()
    {
        final StringBuilder errorMsg = new StringBuilder();
        if (ValidationUtil.isNullOrEmpty( this.prodName ) )
        {
            errorMsg.append( "-- Please specify a product name." );
        }
        else
        {
            if (CustomDecimal.ZERO.compareTo( this.rate ) > 0)
            {
                errorMsg.append( "-- The rate cannot be zero or less than zero for " + this.prodName );
            }
        }
        return errorMsg.toString();
    }
    
    /**
     * @return the editableColList
     */
    public List<Integer> getEditableColList()
    {
        return editableColList;
    }
}
