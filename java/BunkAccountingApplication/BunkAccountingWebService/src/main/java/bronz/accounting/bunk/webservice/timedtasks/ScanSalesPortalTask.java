package bronz.accounting.bunk.webservice.timedtasks;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.scanner.BunkWebScanner;
import bronz.accounting.bunk.model.SavedDailyStatement;
import bronz.accounting.bunk.scanner.LastScanStatus;
import bronz.utilities.general.DateUtil;

import org.codehaus.jackson.map.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ScanSalesPortalTask extends TimerTask {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOG = LogManager.getLogger(
            ScanSalesPortalTask.class);

    public void run() {
        try {
            Calendar currTime = GregorianCalendar.getInstance();
            int indianDate = DateUtil.getIndianDate(currTime);
            LOG.info("Running ScanSalesPortalTask. Indian Time:" + DateUtil.getIndianDateString(currTime) +
                    " Indian date:" + DateUtil.getIndianDate(currTime) +
                    " UTC date:" + DateUtil.getDateFromSimpleDateString(DateUtil.getSimpleDateString(currTime)));
            final BunkManager bunkManager = BunkAppInitializer.getInstance().getBunkManager();


            BunkWebScanner bunkWebScanner = null;
            try {
                bunkWebScanner = getAndLoginToHpcl(bunkWebScanner);

                //Get last execution status
                SavedDailyStatement specialStmtForScanner = bunkManager.getSavedDailyStatement(0);
                final LastScanStatus lastScanStatus;
                if (specialStmtForScanner == null || specialStmtForScanner.getContents() == null ||
                        specialStmtForScanner.getContents().length <= 0) {
                    lastScanStatus = new LastScanStatus();
                } else {
                    lastScanStatus = OBJECT_MAPPER.readValue(specialStmtForScanner.getContents(), LastScanStatus.class);
                }

                /*if (lastScanStatus.shouldRun(indianDate, ScannedDetail.PRICE_CHANGE_TYPE)) { //Avoid re-run if already read for today
                    LastScanStatus.ScanStatus scanStatus =  lastScanStatus.getLastScanStatusMap(ScannedDetail.PRICE_CHANGE_TYPE);
                    Map<String, String> prices = bunkWebScanner.scanPrice();
                    if (prices.get("DATE") != null && prices.get("DATE").equals(DateUtil.getIndianDateString(currTime))) {
                        ScannedDetail newPriceValues = new ScannedDetail();
                        newPriceValues.setType(ScannedDetail.PRICE_CHANGE_TYPE);
                        newPriceValues.setContents(OBJECT_MAPPER.writeValueAsBytes(prices));
                        newPriceValues.setDate(DateUtil.getDateFromSimpleDateString(prices.get("DATE")));
                        newPriceValues.setComments("Scrapped at " + DateUtil.getIndianDateTimeString(currTime));
                        bunkManager.saveScrapedDetail(newPriceValues);
                        scanStatus.setLastSuccessDate(currTime.getTime());
                    } else {
                        LOG.error("Price scrapping returned with empty date or unmatching date. Parsed date:" + prices.get("DATE"));
                    }
                }*/

                //if (lastScanStatus.shouldRun(indianDate, ScannedDetail.HPCL_TRX)) { //Avoid re-run if already read for today
                //    LastScanStatus.ScanStatus scanStatus =  lastScanStatus.getLastScanStatusMap(ScannedDetail.HPCL_TRX);
                    Map<String, Map<String, Map<String, String>>> results = bunkWebScanner.scanTransactions(currTime);
                    for (Map.Entry<String, Map<String, Map<String, String>>> entry : results.entrySet()) {
                        //Date format = 07/05/18
                        int date = DateUtil.getDateFromSimpleDateString(entry.getKey(), "dd/MM/yyyy");







                    }
                    /*if (prices.get("DATE") != null && prices.get("DATE").equals(DateUtil.getIndianDateString(currTime))) {
                        ScannedDetail newPriceValues = new ScannedDetail();
                        newPriceValues.setType(ScannedDetail.PRICE_CHANGE_TYPE);
                        newPriceValues.setContents(OBJECT_MAPPER.writeValueAsBytes(prices));
                        newPriceValues.setDate(DateUtil.getDateFromSimpleDateString(prices.get("DATE")));
                        newPriceValues.setComments("Scrapped at " + DateUtil.getIndianDateTimeString(currTime));
                        bunkManager.saveScrapedDetail(newPriceValues);
                    } else {
                        LOG.error("Price scrapping returned with empty date or unmatching date. Parsed date:" + prices.get("DATE"));
                    }*/
                 //   scanStatus.setLastSuccessDate(currTime.getTime());
               // }

                if (specialStmtForScanner == null) {
                    specialStmtForScanner = new SavedDailyStatement();
                }
                specialStmtForScanner.setDate(0);
                specialStmtForScanner.setMessage("Saved from ScanSalesPortalTask. IST:" + DateUtil.getIndianDateString(currTime));
                specialStmtForScanner.setContents(OBJECT_MAPPER.writeValueAsBytes(lastScanStatus));
                bunkManager.saveSavedDailyStatement(specialStmtForScanner);
            } finally {
                if (bunkWebScanner != null) {
                    bunkWebScanner.close();
                }
            }
        } catch (Exception e) {
            //EmailService.
            LOG.error("Failed while running the task", e);
        }
    }

    private BunkWebScanner getAndLoginToHpcl(BunkWebScanner bunkWebScanner) {
        if (bunkWebScanner == null) {
            bunkWebScanner = new BunkWebScanner(AppConfig.HPCL_CUSTID.getStringValue(),
                    AppConfig.HPCL_PASS.getStringValue(), AppConfig.HPCL_URL.getStringValue(),
                    AppConfig.CHROME_DRIVER_PATH.getStringValue());
            bunkWebScanner.loginToHpcl();

        }
        return bunkWebScanner; // Use the one already initialized.
    }


}
