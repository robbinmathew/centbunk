package bronz.accounting.bunk.webservice.model;

import bronz.accounting.bunk.model.ClosingStatement;
import bronz.accounting.bunk.model.SavedDailyStatement;
import bronz.accounting.bunk.party.model.Settlement;

/**
 * Created by pmathew on 1/19/16.
 */
public class DailyStatementInfo {
    private Settlement settlement;
    private SavedDailyStatement savedDailyStatement;
    private ClosingStatement closingStatement;

    public Settlement getSettlement() {
        return settlement;
    }

    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }

    public SavedDailyStatement getSavedDailyStatement() {
        return savedDailyStatement;
    }

    public void setSavedDailyStatement(SavedDailyStatement savedDailyStatement) {
        this.savedDailyStatement = savedDailyStatement;
    }

    public ClosingStatement getClosingStatement() {
        return closingStatement;
    }

    public void setClosingStatement(ClosingStatement closingStatement) {
        this.closingStatement = closingStatement;
    }
}
