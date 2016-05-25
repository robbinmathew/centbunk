package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 2/13/16.
 */
public class UiPartyTransaction {
    private int partyId;
    private BigDecimal debitAmt;
    private BigDecimal creditAmt;
    private boolean isChequeDebit;
    private String debitDetail;
    private String creditDetail;
    private Integer slNo;

    public int getPartyId() {
        return partyId;
    }

    public void setPartyId(int partyId) {
        this.partyId = partyId;
    }

    public BigDecimal getDebitAmt() {
        return debitAmt;
    }

    public void setDebitAmt(BigDecimal debitAmt) {
        this.debitAmt = debitAmt;
    }

    public BigDecimal getCreditAmt() {
        return creditAmt;
    }

    public void setCreditAmt(BigDecimal creditAmt) {
        this.creditAmt = creditAmt;
    }

    public boolean getIsChequeDebit() {
        return isChequeDebit;
    }

    public void setIsChequeDebit(boolean isChequeDebit) {
        this.isChequeDebit = isChequeDebit;
    }

    public String getDebitDetail() {
        return debitDetail;
    }

    public void setDebitDetail(String debitDetail) {
        this.debitDetail = debitDetail;
    }

    public String getCreditDetail() {
        return creditDetail;
    }

    public void setCreditDetail(String creditDetail) {
        this.creditDetail = creditDetail;
    }

    public Integer getSlNo() {
        return slNo;
    }

    public void setSlNo(Integer slNo) {
        this.slNo = slNo;
    }
}
