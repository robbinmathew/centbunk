package bronz.accounting.bunk.scanner;

import java.math.BigDecimal;

public class PriceChangeScanModel {
    private int productId;
    private BigDecimal newPrice;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }
}
