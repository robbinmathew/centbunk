package bronz.accounting.bunk.scanner;

import bronz.accounting.bunk.model.ScrapedDetail;
import bronz.utilities.general.DateUtil;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LastScanStatus {
    public static class ScanStatus {
        private Date lastFailedDate;
        private Date lastSuccessDate;
        private Map<String, String> lastRunDetail;
        private String type;

        public Date getLastFailedDate() {
            return lastFailedDate;
        }

        public void setLastFailedDate(Date lastFailedDate) {
            this.lastFailedDate = lastFailedDate;
        }

        public Date getLastSuccessDate() {
            return lastSuccessDate;
        }

        public void setLastSuccessDate(Date lastSuccessDate) {
            this.lastSuccessDate = lastSuccessDate;
        }

        public Map<String, String> getLastRunDetail() {
            return lastRunDetail;
        }

        public void setLastRunDetail(Map<String, String> lastRunDetail) {
            this.lastRunDetail = lastRunDetail;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @JsonIgnore
        public String getRunDetail(String key) {
            if (lastRunDetail == null) {
                return null;
            }
            return lastRunDetail.get(key);
        }

        @JsonIgnore
        public void setRunDetail(String key, String value) {
            if (lastRunDetail == null) {
                lastRunDetail = new HashMap<String, String>();
            }
            lastRunDetail.put(key, value);
        }

        @JsonIgnore
        public void clearRunDetails() {
            lastRunDetail = null;
        }
    }
    private Map<String, ScanStatus> lastScanStatusMap = new HashMap<String, ScanStatus>();

    public Map<String, ScanStatus> getLastScanStatusMap() {
        return lastScanStatusMap;
    }

    public ScanStatus getLastScanStatusMap(String type) {
        if (!lastScanStatusMap.containsKey(type)) {
            lastScanStatusMap.put(type, new ScanStatus());
        }
        return lastScanStatusMap.get(type);
    }

    public void setLastScanStatusMap(Map<String, ScanStatus> lastScanStatusMap) {
        this.lastScanStatusMap = lastScanStatusMap;
    }

    @JsonIgnore
    public boolean shouldRun(int indianDate, String type) {
        return this.lastScanStatusMap == null ||
                this.lastScanStatusMap.get(type) == null ||
                indianDate > DateUtil.getIndianDate(this.lastScanStatusMap.get(type).getLastSuccessDate());
    }
}


