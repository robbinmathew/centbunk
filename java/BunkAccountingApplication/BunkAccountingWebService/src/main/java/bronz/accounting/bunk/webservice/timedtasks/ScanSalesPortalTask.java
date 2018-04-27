package bronz.accounting.bunk.webservice.timedtasks;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.TimerTask;

public class ScanSalesPortalTask extends TimerTask {
    private static final Logger LOG = LogManager.getLogger(
            ScanSalesPortalTask.class);

    public void run() {
        LOG.info("Running ScanSalesPortalTask");


    }
}
