package bronz.accounting.bunk.party.model;

import java.math.BigDecimal;

/**
 * Created by pmathew on 5/12/17.
 */
public class PartyResult extends Party {

    private Integer slNo;
    private int date;
    private BigDecimal balance;

    /**
     * Default constructor.
     */
    public PartyResult()
    {
        super();
    }

    public PartyResult(final Party party, final PartyClosingBalance partyClosingBalance) {
        super(party);
        this.slNo = partyClosingBalance.getSlNo();
        this.date = partyClosingBalance.getDate();
        this.balance = partyClosingBalance.getBalance();
    }

    public Integer getSlNo() {
        return slNo;
    }

    public void setSlNo(Integer slNo) {
        this.slNo = slNo;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
