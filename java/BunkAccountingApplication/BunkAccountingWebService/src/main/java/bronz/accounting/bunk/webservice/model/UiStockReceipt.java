package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pmathew on 1/14/16.
 */
public class UiStockReceipt {
    private String invoiceNo;
    private String type;
    private BigDecimal invoiceAmt;
    private BigDecimal totalLoad;
    private List<UiTankReceipt> tankReceipts = new ArrayList<UiTankReceipt>();
    private List<UiFuelReceipt> fuelReceipts = new ArrayList<UiFuelReceipt>();

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public BigDecimal getInvoiceAmt() {
        return invoiceAmt;
    }

    public void setInvoiceAmt(BigDecimal invoiceAmt) {
        this.invoiceAmt = invoiceAmt;
    }

    public BigDecimal getTotalLoad() {
        return totalLoad;
    }

    public void setTotalLoad(BigDecimal totalLoad) {
        this.totalLoad = totalLoad;
    }

    public List<UiTankReceipt> getTankReceipts() {
        return tankReceipts;
    }

    public void setTankReceipts(List<UiTankReceipt> tankReceipts) {
        this.tankReceipts = tankReceipts;
    }

    public List<UiFuelReceipt> getFuelReceipts() {
        return fuelReceipts;
    }

    public void setFuelReceipts(List<UiFuelReceipt> fuelReceipts) {
        this.fuelReceipts = fuelReceipts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
