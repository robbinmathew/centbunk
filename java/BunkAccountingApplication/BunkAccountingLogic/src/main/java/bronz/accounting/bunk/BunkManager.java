package bronz.accounting.bunk;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.model.*;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyResult;
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

    //Edit transactions
    @RequiresTransaction(failureExceptionText = "Failed to edit partyTransactions")
    void specialUpdatePartyTrans(final PartyTransaction newPartyTransaction, final Integer prevSlNo, BigDecimal amtDiff) throws BunkMgmtException;

    Map<Integer, Party> getAllParties() throws BunkMgmtException;

    List<PartyResult> getParties(String type) throws BunkMgmtException;

    List<PartyTransaction> getPendingChequesAtOffice() throws BunkMgmtException;

    List<PartyClosingBalance> getPartyList(String type) throws BunkMgmtException;

    List<EmployeeMonthlyStatus> getEmployeesStatus() throws BunkMgmtException;

    PartyClosingBalance getParty(Integer id) throws BunkMgmtException;

    List<ProductClosingBalance> getProductList(String type) throws BunkMgmtException;

    List<ProductClosingBalance> getAvailableProductList(String type) throws BunkMgmtException;

    List<Product> getAllProduct() throws BunkMgmtException;

    @Deprecated //Use saveStockReceipt instead
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

    @RequiresTransaction(failureExceptionText = "Failed to delete saved stmt")
    void deleteSavedDailyStatement(int date) throws BunkMgmtException;

    @RequiresTransaction(failureExceptionText = "Failed to save saved stmt")
    void saveSavedDailyStatement(SavedDailyStatement savedDailyStatement) throws BunkMgmtException;

    @RequiresTransaction(failureExceptionText = "Failed to save scraped data")
    String saveScannedData(String data, ScanType type, String source) throws BunkMgmtException;

    List<ScannedDetail> getScannedData(int startDate, int endDate, ScanType type) throws BunkMgmtException;

    List<PartyTransaction> getPartyTransactionHistory(final int partyId, final int startDate, final int endDate,
                                                      final String transTypeFilter, final String detailFilter) throws BunkMgmtException;

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

    QueryResults getResult(final String savedQueryName, final Map<String, Object> params) throws BunkMgmtException;
}
