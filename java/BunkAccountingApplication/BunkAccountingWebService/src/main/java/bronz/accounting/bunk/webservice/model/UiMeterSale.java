package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 2/13/16.
 */
public class UiMeterSale {
    private int meterId;
    private BigDecimal totalSale;
    private BigDecimal closingReading;
    private BigDecimal testSale;

    public int getMeterId() {
        return meterId;
    }

    public void setMeterId(int meterId) {
        this.meterId = meterId;
    }

    public BigDecimal getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(BigDecimal totalSale) {
        this.totalSale = totalSale;
    }

    public BigDecimal getClosingReading() {
        return closingReading;
    }

    public void setClosingReading(BigDecimal closingReading) {
        this.closingReading = closingReading;
    }

    public BigDecimal getTestSale() {
        return testSale;
    }

    public void setTestSale(BigDecimal testSale) {
        this.testSale = testSale;
    }
}
