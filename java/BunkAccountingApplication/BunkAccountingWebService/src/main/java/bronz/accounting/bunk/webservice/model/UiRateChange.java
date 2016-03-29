package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 2/24/16.
 */
public class UiRateChange {
    private int productId;
    private BigDecimal newUnitPrice;
    private BigDecimal totalCashDiff;
    private BigDecimal oldPrice;
    private BigDecimal currentStock;
    private BigDecimal margin;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public BigDecimal getNewUnitPrice() {
        return newUnitPrice;
    }

    public void setNewUnitPrice(BigDecimal newUnitPrice) {
        this.newUnitPrice = newUnitPrice;
    }

    public BigDecimal getTotalCashDiff() {
        return totalCashDiff;
    }

    public void setTotalCashDiff(BigDecimal totalCashDiff) {
        this.totalCashDiff = totalCashDiff;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock) {
        this.currentStock = currentStock;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }
}
