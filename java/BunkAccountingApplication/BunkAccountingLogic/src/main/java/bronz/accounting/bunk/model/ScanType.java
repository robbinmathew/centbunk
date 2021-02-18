package bronz.accounting.bunk.model;

import java.util.HashMap;
import java.util.Map;

public class ScanType {
    private static final Map<String, ScanType> TYPE_NAME_MAP = new HashMap<>();

    public static final ScanType PRICE_CHANGE_TYPE = new ScanType("PRI");
    public static final ScanType BANK_TRANS = new ScanType("BNK_TRN");
    public static final ScanType HPCL_TRANS = new ScanType("HPCL_TRN");
    public static final ScanType PROD_RECEIPT = new ScanType("REC");
    //public static final String HPCL_TRX = "HPCL_TRX";
    //public static final String BANK_TRX = "BANK_TRX";

    private String type;
    ScanType(String type) {
        this.type = type;
        TYPE_NAME_MAP.put(this.type, this);
    }

    public String getType() {
        return type;
    }

    public static ScanType getByType(String type) {
        if (!TYPE_NAME_MAP.containsKey(type)) {
            throw new IllegalStateException("Invalid ScanType:" + type);
        }
        return TYPE_NAME_MAP.get(type);
    }
}
