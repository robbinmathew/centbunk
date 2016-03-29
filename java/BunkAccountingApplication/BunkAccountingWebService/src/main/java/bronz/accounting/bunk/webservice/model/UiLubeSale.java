package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 2/13/16.
 */
public class UiLubeSale {
    private int productId;
    private BigDecimal totalSale;
    private BigDecimal discountPerUnit;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public BigDecimal getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(BigDecimal totalSale) {
        this.totalSale = totalSale;
    }

    public BigDecimal getDiscountPerUnit() {
        return discountPerUnit;
    }

    public void setDiscountPerUnit(BigDecimal discountPerUnit) {
        this.discountPerUnit = discountPerUnit;
    }
}
