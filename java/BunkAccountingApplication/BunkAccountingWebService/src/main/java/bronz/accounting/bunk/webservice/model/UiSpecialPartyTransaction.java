package bronz.accounting.bunk.webservice.model;

import bronz.accounting.bunk.party.model.PartyTransaction;

import java.math.BigDecimal;

public class UiSpecialPartyTransaction {
    private PartyTransaction transaction;
    private Integer prevSlNoForInsert;
    private BigDecimal amtDiffForTransUpdate;

    public PartyTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(PartyTransaction transaction) {
        this.transaction = transaction;
    }

    public Integer getPrevSlNoForInsert() {
        return prevSlNoForInsert;
    }

    public void setPrevSlNoForInsert(Integer prevSlNoForInsert) {
        this.prevSlNoForInsert = prevSlNoForInsert;
    }

    public BigDecimal getAmtDiffForTransUpdate() {
        return amtDiffForTransUpdate;
    }

    public void setAmtDiffForTransUpdate(BigDecimal amtDiffForTransUpdate) {
        this.amtDiffForTransUpdate = amtDiffForTransUpdate;
    }
}
