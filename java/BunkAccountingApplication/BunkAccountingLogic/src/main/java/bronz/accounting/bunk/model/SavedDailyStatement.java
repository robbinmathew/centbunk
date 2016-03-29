package bronz.accounting.bunk.model;

import bronz.utilities.general.Pair;
import bronz.utilities.general.Triple;

import java.math.BigDecimal;
import java.util.*;

/**
 * Bean to hold the saved daily statement.
 */
public class SavedDailyStatement {
    private int date;
    private String message;
    private Map<Integer,Pair<BigDecimal, BigDecimal>> officeCashMap;//Party ID, to office, to bank
    private List<Triple<Integer,BigDecimal, String>> creditedChequeDetails;//Party ID, Cheque Amount, credit detail.
    private Map<Integer,Pair<BigDecimal, BigDecimal>> meterMap;//Meter ID, closing reading, test
    private Map<Integer, BigDecimal> tankStockMap;//Tank ID, closing dip reading.
    private Map<Integer,Pair<BigDecimal, BigDecimal>> productSalesMap;//Product ID, sale amount, discount
    private Map<Integer,Triple<Pair<String,BigDecimal>,Pair<String,BigDecimal>, Pair<String,BigDecimal>>> partyTransactionsMap;
    // Party Id, CHQ<message, amount>, DEBIT<message, amount>, CREDIT<message, amount>
    private Map<Integer,Pair<Pair<String,BigDecimal>,Pair<String,BigDecimal>>> employeeTransactionsMap;
    //Emp Id, sal message, sal amount, incentive message, incentive amount.

    public SavedDailyStatement(){
        officeCashMap = new HashMap<Integer, Pair<BigDecimal, BigDecimal>>();
        meterMap = new HashMap<Integer, Pair<BigDecimal, BigDecimal>>();
        tankStockMap = new HashMap<Integer, BigDecimal>();
        productSalesMap = new LinkedHashMap<Integer, Pair<BigDecimal, BigDecimal>>();
        partyTransactionsMap = new LinkedHashMap<Integer, Triple<Pair<String, BigDecimal>, Pair<String, BigDecimal>, Pair<String, BigDecimal>>>();
        employeeTransactionsMap = new LinkedHashMap<Integer, Pair<Pair<String, BigDecimal>, Pair<String, BigDecimal>>>();
        creditedChequeDetails = new ArrayList<Triple<Integer, BigDecimal, String>>();
    }

    public void addOfficeTransaction(Integer offPartyId, BigDecimal toOffice, BigDecimal toBank){
        this.officeCashMap.put(offPartyId, new Pair<BigDecimal, BigDecimal>(toOffice, toBank));
    }

    public void addMeterTransaction(Integer meterId, BigDecimal closingReading, BigDecimal testLiters){
        this.meterMap.put(meterId, new Pair<BigDecimal, BigDecimal>(closingReading, testLiters));
    }

    public void addTankTransaction(Integer tankId, BigDecimal closingDip){
        this.tankStockMap.put(tankId, closingDip);
    }

    public void addProductSaleTransaction(Integer prodId, BigDecimal saleAmount, BigDecimal discount){
        this.productSalesMap.put(prodId, new Pair<BigDecimal, BigDecimal>(saleAmount, discount));
    }

    public void addEmployeeTransaction(Integer empId, String salMessage, BigDecimal salAmount,
                                       String incentiveMessage, BigDecimal incentiveAmount){
        this.employeeTransactionsMap.put(empId, new Pair<Pair<String, BigDecimal>, Pair<String, BigDecimal>>(
                new Pair<String, BigDecimal>(salMessage, salAmount),
                new Pair<String, BigDecimal>(incentiveMessage, incentiveAmount)));
    }

    public void addPartyTransaction(Integer partyId, String chqMessage, BigDecimal chqAmount,
                                    String debitMessage, BigDecimal debitAmount, String creditMessage, BigDecimal creditAmount){
        this.partyTransactionsMap.put(partyId, new Triple<Pair<String, BigDecimal>, Pair<String, BigDecimal>, Pair<String, BigDecimal>>(
                new Pair<String, BigDecimal>(chqMessage, chqAmount),
                new Pair<String, BigDecimal>(debitMessage, debitAmount),
                new Pair<String, BigDecimal>(creditMessage, creditAmount)));
    }

    public void addChequeCreditedDetail(Integer partyId, BigDecimal amount, String detail){
        this.creditedChequeDetails.add(new Triple<Integer, BigDecimal, String>(partyId,amount, detail));
    }

    public Map<Integer, Pair<Pair<String, BigDecimal>, Pair<String, BigDecimal>>> getEmployeeTransactionsMap() {
        return employeeTransactionsMap;
    }

    public void setEmployeeTransactionsMap(Map<Integer, Pair<Pair<String, BigDecimal>, Pair<String, BigDecimal>>> employeeTransactionsMap) {
        this.employeeTransactionsMap = employeeTransactionsMap;
    }

    public Map<Integer, Pair<BigDecimal, BigDecimal>> getMeterMap() {
        return meterMap;
    }

    public void setMeterMap(Map<Integer, Pair<BigDecimal, BigDecimal>> meterMap) {
        this.meterMap = meterMap;
    }

    public Map<Integer, Pair<BigDecimal, BigDecimal>> getOfficeCashMap() {
        return officeCashMap;
    }

    public void setOfficeCashMap(Map<Integer, Pair<BigDecimal, BigDecimal>> officeCashMap) {
        this.officeCashMap = officeCashMap;
    }

    public Map<Integer, Triple<Pair<String, BigDecimal>, Pair<String, BigDecimal>, Pair<String, BigDecimal>>> getPartyTransactionsMap() {
        return partyTransactionsMap;
    }

    public void setPartyTransactionsMap(Map<Integer, Triple<Pair<String, BigDecimal>, Pair<String, BigDecimal>, Pair<String, BigDecimal>>> partyTransactionsMap) {
        this.partyTransactionsMap = partyTransactionsMap;
    }

    public Map<Integer, Pair<BigDecimal, BigDecimal>> getProductSalesMap() {
        return productSalesMap;
    }

    public void setProductSalesMap(Map<Integer, Pair<BigDecimal, BigDecimal>> productSalesMap) {
        this.productSalesMap = productSalesMap;
    }

    public List<Triple<Integer, BigDecimal, String>> getCreditedChequeDetails() {
        return creditedChequeDetails;
    }

    public void setCreditedChequeDetails(List<Triple<Integer, BigDecimal, String>> creditedChequeDetails) {
        this.creditedChequeDetails = creditedChequeDetails;
    }

    public Map<Integer, BigDecimal> getTankStockMap() {
        return tankStockMap;
    }

    public void setTankStockMap(Map<Integer, BigDecimal> tankStockMap) {
        this.tankStockMap = tankStockMap;
    }

    public void setDate(int date){
        this.date = date;
    }

    public int getDate(){
        return date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
