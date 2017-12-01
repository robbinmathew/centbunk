package bronz.accounting.bunk.framework.dao;

import com.google.common.collect.ImmutableMap;

import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.hibernate.type.StringType;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pmathew on 8/30/16.
 */
public enum SavedQuery {
    FUEL_REPORT("fuelsSalesSummary",
        ImmutableMap.<String, Type>builder()
            .put("DATE_TEXT", new StringType())
            .put("P_SALE", new FloatType())
            .put("D_SALE", new FloatType())
            .put("P_TEST", new FloatType())
            .put("D_TEST", new FloatType())
            .put("P_CL_STOCK", new FloatType())
            .put("D_CL_STOCK", new FloatType()).build(), null, null),
    RATE_CHANGE_HISTORY("rateChangeHistory",
        ImmutableMap.<String, Type>builder()
            .put("DATE", new IntegerType())
            .put("DATE_TEXT", new StringType())
            .put("PRODUCT_NAME", new StringType())
            .put("PRODUCT_ID", new FloatType())
            .put("STOCK", new FloatType())
            .put("PRICE_CHANGE", new FloatType())
            .put("TOTAL", new FloatType()).build(), null, null),
    FUEL_SALE_V2("fuelsSalesSummaryV2",
        //TRX.DATE DATE, DATE_FORMAT(FROM_DAYS(TRX.DATE), '%d %b') AS DATE_TEXT, TRX.ID, TRX.T VALUE, CONCAT(CONVERT(CAST(CONVERT(PT.PRODUCT_NAME USING latin1) AS BINARY) using utf8), " ", TRX.DETAIL) DETAIL
        ImmutableMap.<String, Type>builder()
            .put("DATE", new IntegerType())
            .put("DATE_TEXT", new StringType())
            .put("DETAIL", new StringType())
            .put("VALUE", new FloatType()).build(),
        Arrays.asList("DATE", "DATE_TEXT"),
        ImmutableMap.<String, String>builder()
            .put("DETAIL", "VALUE").build()),
    FUEL_TEST("fuelsTestSummary",
        ImmutableMap.<String, Type>builder()
            .put("DATE", new IntegerType())
            .put("DATE_TEXT", new StringType())
            .put("DETAIL", new StringType())
            .put("VALUE", new FloatType()).build(),
        Arrays.asList("DATE", "DATE_TEXT"),
        ImmutableMap.<String, String>builder()
            .put("DETAIL", "VALUE").build()),
    PARTY_BALANCE_SUMMARY("partyBalanceSummary",
            ImmutableMap.<String, Type>builder()
                    .put("DATE", new IntegerType())
                    .put("DATE_TEXT", new StringType())
                    .put("NAME", new StringType())
                    .put("BALANCE", new FloatType()).build(),
            Arrays.asList("DATE", "DATE_TEXT"),
            ImmutableMap.<String, String>builder()
                    .put("NAME", "BALANCE").build(), 1);


    private final String savedQueryName;
    private final Map<String, ? extends Type> typeMap;
    private final List<String> groupByFields;
    private final Map<String, String> pivotFields;
    private final Set<String> ignoreFields;
    private final int missingValueHandling;

    private SavedQuery(final String savedQueryName,
                       final Map<String, ? extends Type> typeMap, final List<String> groupByFields,
                       final Map<String, String> pivotFields) {
        this(savedQueryName, typeMap, groupByFields, pivotFields, 0);
    }

    private SavedQuery(final String savedQueryName,
        final Map<String, ? extends Type> typeMap, final List<String> groupByFields,
        final Map<String, String> pivotFields, final int missingValueHandling) {
        this.savedQueryName = savedQueryName;
        this.typeMap = typeMap;
        this.groupByFields = groupByFields;
        this.pivotFields = pivotFields;
        this.ignoreFields = new HashSet<String>();
        if(pivotFields !=null) {
            this.ignoreFields.addAll(pivotFields.values());
        }
        this.missingValueHandling = missingValueHandling;
    }

    public static SavedQuery findQuery(String name) {
        for (SavedQuery query : EnumSet.allOf(SavedQuery.class)) {
            if(query.savedQueryName.equals(name)) {
                return query;
            }
        }
        return null;
    }


    public List<String> getGroupByFields() {
        return groupByFields;
    }

    public Map<String, String> getPivotFields() {
        return pivotFields;
    }

    public Set<String> getIgnoreFields() {
        return ignoreFields;
    }

    public String getSavedQueryName() {
        return savedQueryName;
    }

    public Map<String, ? extends Type> getTypeMap() {
        return typeMap;
    }

    public int getMissingValueHandling() {
        return missingValueHandling;
    }
}
