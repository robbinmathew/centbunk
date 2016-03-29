package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 2/13/16.
 */
public class UiEmployeeTransaction {
    private int partyId;
    private BigDecimal salaryAmt;
    private BigDecimal incentiveAmt;
    private String salaryDetail;
    private String incentiveDetail;

    public int getPartyId() {
        return partyId;
    }

    public void setPartyId(int partyId) {
        this.partyId = partyId;
    }

    public BigDecimal getSalaryAmt() {
        return salaryAmt;
    }

    public void setSalaryAmt(BigDecimal salaryAmt) {
        this.salaryAmt = salaryAmt;
    }

    public BigDecimal getIncentiveAmt() {
        return incentiveAmt;
    }

    public void setIncentiveAmt(BigDecimal incentiveAmt) {
        this.incentiveAmt = incentiveAmt;
    }

    public String getSalaryDetail() {
        return salaryDetail;
    }

    public void setSalaryDetail(String salaryDetail) {
        this.salaryDetail = salaryDetail;
    }

    public String getIncentiveDetail() {
        return incentiveDetail;
    }

    public void setIncentiveDetail(String incentiveDetail) {
        this.incentiveDetail = incentiveDetail;
    }
}
