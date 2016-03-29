package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 2/21/16.
 */
public class UiOfficeCash {
    private int id;
    private BigDecimal toOffice;
    private BigDecimal paidToBank;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getToOffice() {
        return toOffice;
    }

    public void setToOffice(BigDecimal toOffice) {
        this.toOffice = toOffice;
    }

    public BigDecimal getPaidToBank() {
        return paidToBank;
    }

    public void setPaidToBank(BigDecimal paidToBank) {
        this.paidToBank = paidToBank;
    }
}
