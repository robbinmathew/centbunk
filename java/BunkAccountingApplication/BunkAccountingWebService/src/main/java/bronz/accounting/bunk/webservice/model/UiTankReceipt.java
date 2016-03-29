package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 1/14/16.
 */
public class UiTankReceipt {
    private Integer tankId;
    private BigDecimal receiptAmt;

    public Integer getTankId() {
        return tankId;
    }

    public void setTankId(Integer tankId) {
        this.tankId = tankId;
    }

    public BigDecimal getReceiptAmt() {
        return receiptAmt;
    }

    public void setReceiptAmt(BigDecimal receiptAmt) {
        this.receiptAmt = receiptAmt;
    }
}
