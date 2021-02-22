package bronz.accounting.bunk.scanner;

import java.math.BigDecimal;

public class ProdReceiptScanModel {
    //            scannedDetail.setContents(String.format("{\"productNameText\":\"%s\", \"invoiceNum\":\"%s\", \"quantity\":\"%s\", \"totalPrice\":\"%s\"}",
    private String productNameText;
    private String invoiceNum;
    private BigDecimal quantity;
    private BigDecimal totalPrice;


    public String getProductNameText() {
        return productNameText;
    }

    public void setProductNameText(String productNameText) {
        this.productNameText = productNameText;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
