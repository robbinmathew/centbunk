package bronz.accounting.bunk;

import java.util.List;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.model.ClosingStatement;
import bronz.accounting.bunk.model.SavedDailyStatement;
import bronz.accounting.bunk.model.StockReceipt;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.party.model.Settlement;
import bronz.accounting.bunk.products.model.ProdRateChange;
import bronz.accounting.bunk.products.model.Product;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.products.model.StockVariation;
import bronz.accounting.bunk.tankandmeter.model.MeterClosingReading;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.util.RequiresTransaction;

/**
 * Created by pmathew on 2/1/16.
 */
public interface BunkManager {
    int getNextDate() throws BunkMgmtException;

    int getFirstDate() throws BunkMgmtException;

    int getTodayDate() throws BunkMgmtException;

    @RequiresTransaction(failureExceptionText = "Failed to save the stock receipt")
    void savePartyDetails(List<Party> partyToBeUpdated, List<PartyTransaction> partyTransToBeUpdated) throws BunkMgmtException;

    List<Party> getAllParties() throws BunkMgmtException;

    List<PartyTransaction> getPendingChequesAtOffice() throws BunkMgmtException;

    List<PartyClosingBalance> getPartyList(String type) throws BunkMgmtException;

    List<EmployeeMonthlyStatus> getEmployeesStatus() throws BunkMgmtException;

    PartyClosingBalance getParty(Integer id) throws BunkMgmtException;

    List<ProductClosingBalance> getProductList(String type) throws BunkMgmtException;

    List<ProductClosingBalance> getAvailableProductList(String type) throws BunkMgmtException;

    List<Product> getAllProduct() throws BunkMgmtException;

    @RequiresTransaction(failureExceptionText = "Failed to save the stock receipt")
    void saveProdDetails(List<Product> prodToBeUpdated,
                         List<ProductTransaction> prodTransToBeUpdated) throws BunkMgmtException;

    List<MeterClosingReading> getMeterList() throws BunkMgmtException;

    List<MeterClosingReading> getActiveMeterList() throws BunkMgmtException;

    @RequiresTransaction(failureExceptionText = "Failed to save the stock receipt")
    void saveStockReceipt(StockReceipt stockReceipt) throws BunkMgmtException;

    List<StockReceipt> getStockReciepts(int date) throws BunkMgmtException;

    @RequiresTransaction(failureExceptionText = "Failed to save the statement")
    void saveAndCloseStatement(ClosingStatement statement) throws BunkMgmtException;

    @RequiresTransaction(failureExceptionText = "Failed to save the party transactions")
    void savePartyTrans(List<PartyTransaction> transactions) throws BunkMgmtException;

    ClosingStatement getClosingStatement(int date) throws BunkMgmtException;

    SavedDailyStatement getSavedDailyStatement(int date) throws BunkMgmtException;

    void deleteSavedDailyStatement(int date) throws BunkMgmtException;

    void saveSavedDailyStatement(SavedDailyStatement savedDailyStatement) throws BunkMgmtException;

    List<PartyTransaction> getPartyTransactionHistory(int partyId) throws BunkMgmtException;

    List<ProductClosingBalance> getProductList(int date) throws BunkMgmtException;

    List<TankClosingStock> getTankList(int date) throws BunkMgmtException;

    List<ProdRateChange> getRateChangesHistory() throws BunkMgmtException;

    @RequiresTransaction(failureExceptionText = "Failed to save the rate change")
    void saveRateChange(ProdRateChange prodRateChange, ProductTransaction transaction) throws BunkMgmtException;

    @RequiresTransaction(failureExceptionText = "Failed to clear the transactions")
    void clearTransactions(int date) throws BunkMgmtException;

    void executeDbBackup() throws BunkMgmtException;

    Settlement getSettlement(int date) throws BunkMgmtException;

    List<StockVariation> getStockVariation(int date) throws BunkMgmtException;

    List getFuelsSaleSummary(final int start, final int end) throws BunkMgmtException;
}
