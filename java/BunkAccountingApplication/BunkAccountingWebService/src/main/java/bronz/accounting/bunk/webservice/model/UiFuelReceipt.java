package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 1/14/16.
 */
public class UiFuelReceipt {
    private Integer productId;
    private String productName;
    private BigDecimal receiptAmt;
    private BigDecimal unitSellingPrice;
    private BigDecimal costOnInv;
    private BigDecimal margin;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getReceiptAmt() {
        return receiptAmt;
    }

    public void setReceiptAmt(BigDecimal receiptAmt) {
        this.receiptAmt = receiptAmt;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getUnitSellingPrice() {
        return unitSellingPrice;
    }

    public void setUnitSellingPrice(BigDecimal unitSellingPrice) {
        this.unitSellingPrice = unitSellingPrice;
    }

    public BigDecimal getCostOnInv() {
        return costOnInv;
    }

    public void setCostOnInv(BigDecimal costOnInv) {
        this.costOnInv = costOnInv;
    }
}
