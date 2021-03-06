package bronz.accounting.bunk.model.dao;

import java.util.List;
import java.util.Map;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.model.QueryResults;
import bronz.accounting.bunk.model.SavedDailyStatement;
import bronz.accounting.bunk.model.ScannedDetail;

/**
 * Created with IntelliJ IDEA.
 * User: pmathew
 * Date: 10/13/13
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SavedStatementDao {
    String DAILY_STATEMENT_TYPE="DAILY_STATEMENT";
    String SCANNER_TYPE="SCANNER_STATEMENT";
    SavedDailyStatement getSavedDailyStatement(int date) throws BunkMgmtException;
    void saveOrUpdateSavedDailyStatement(SavedDailyStatement savedDailyStatement) throws BunkMgmtException;
    void deleteSavedDailyStatement(int date) throws BunkMgmtException;

    List<ScannedDetail> getScrapedDetails(int startDate, int endDate, String type) throws BunkMgmtException;
    int saveScrapedDetail(ScannedDetail scannedDetail) throws BunkMgmtException;

    QueryResults getResult(final String savedQueryName, final Map<String, Object> params) throws BunkMgmtException;

}
