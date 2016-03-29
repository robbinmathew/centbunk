package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 2/13/16.
 */
public class UiFuelSale {
    private int productId;
    private BigDecimal totalSale;
    private BigDecimal testSale;
    private BigDecimal actualSale;
    private BigDecimal unitSellingPrice;

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

    public BigDecimal getTestSale() {
        return testSale;
    }

    public void setTestSale(BigDecimal testSale) {
        this.testSale = testSale;
    }

    public BigDecimal getActualSale() {
        return actualSale;
    }

    public void setActualSale(BigDecimal actualSale) {
        this.actualSale = actualSale;
    }

    public BigDecimal getUnitSellingPrice() {
        return unitSellingPrice;
    }

    public void setUnitSellingPrice(BigDecimal unitSellingPrice) {
        this.unitSellingPrice = unitSellingPrice;
    }
}
