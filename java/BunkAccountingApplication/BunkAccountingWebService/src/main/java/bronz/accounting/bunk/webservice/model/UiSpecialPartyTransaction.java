package bronz.accounting.bunk.webservice.model;

import bronz.accounting.bunk.party.model.PartyTransaction;

import java.math.BigDecimal;
import java.util.List;

public class UiSpecialPartyTransaction {
    private PartyTransaction lastTransaction; //Prev Trans for ADD, Original Trans for update

    private String updateMode; //UPDATE/ADD
    private String dateText;
    private Integer date;
    private String transactionDetail;
    private String transactionType;
    private BigDecimal amount;


    public PartyTransaction getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(PartyTransaction lastTransaction) {
        this.lastTransaction = lastTransaction;
    }

    public String getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public String getTransactionDetail() {
        return transactionDetail;
    }

    public void setTransactionDetail(String transactionDetail) {
        this.transactionDetail = transactionDetail;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }
}
