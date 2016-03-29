package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 2/13/16.
 */
public class UiTankSale {
    private int tankId;
    private int productId;
    private BigDecimal dipStock;
    private BigDecimal testSale;
    private BigDecimal sale;
    private BigDecimal diffToday;
    private BigDecimal diffThisMonth;

    public int getTankId() {
        return tankId;
    }

    public void setTankId(int tankId) {
        this.tankId = tankId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public BigDecimal getDipStock() {
        return dipStock;
    }

    public void setDipStock(BigDecimal dipStock) {
        this.dipStock = dipStock;
    }

    public BigDecimal getTestSale() {
        return testSale;
    }

    public void setTestSale(BigDecimal testSale) {
        this.testSale = testSale;
    }

    public BigDecimal getSale() {
        return sale;
    }

    public void setSale(BigDecimal sale) {
        this.sale = sale;
    }

    public BigDecimal getDiffToday() {
        return diffToday;
    }

    public void setDiffToday(BigDecimal diffToday) {
        this.diffToday = diffToday;
    }

    public BigDecimal getDiffThisMonth() {
        return diffThisMonth;
    }

    public void setDiffThisMonth(BigDecimal diffThisMonth) {
        this.diffThisMonth = diffThisMonth;
    }
}
