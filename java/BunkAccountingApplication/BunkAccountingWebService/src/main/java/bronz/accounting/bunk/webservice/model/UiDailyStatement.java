package bronz.accounting.bunk.webservice.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pmathew on 2/13/16.
 */
public class UiDailyStatement {
    private int date;
    private String type;
    private BigDecimal closingBalance;

    private List<UiOfficeCash> officeCash = new ArrayList<UiOfficeCash>();
    private List<UiMeterSale> meterSales = new ArrayList<UiMeterSale>();
    private List<UiTankSale> tankSales = new ArrayList<UiTankSale>();
    private List<UiFuelSale> fuelSales = new ArrayList<UiFuelSale>();
    private List<UiLubeSale> lubesSales = new ArrayList<UiLubeSale>();
    private List<UiPartyTransaction> partyTrans = new ArrayList<UiPartyTransaction>();
    private List<UiEmployeeTransaction> empTrans = new ArrayList<UiEmployeeTransaction>();

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public List<UiOfficeCash> getOfficeCash() {
        return officeCash;
    }

    public void setOfficeCash(List<UiOfficeCash> officeCash) {
        this.officeCash = officeCash;
    }

    public List<UiMeterSale> getMeterSales() {
        return meterSales;
    }

    public void setMeterSales(List<UiMeterSale> meterSales) {
        this.meterSales = meterSales;
    }

    public List<UiTankSale> getTankSales() {
        return tankSales;
    }

    public void setTankSales(List<UiTankSale> tankSales) {
        this.tankSales = tankSales;
    }

    public List<UiFuelSale> getFuelSales() {
        return fuelSales;
    }

    public void setFuelSales(List<UiFuelSale> fuelSales) {
        this.fuelSales = fuelSales;
    }

    public List<UiLubeSale> getLubesSales() {
        return lubesSales;
    }

    public void setLubesSales(List<UiLubeSale> lubesSales) {
        this.lubesSales = lubesSales;
    }

    public List<UiPartyTransaction> getPartyTrans() {
        return partyTrans;
    }

    public void setPartyTrans(List<UiPartyTransaction> partyTrans) {
        this.partyTrans = partyTrans;
    }

    public List<UiEmployeeTransaction> getEmpTrans() {
        return empTrans;
    }

    public void setEmpTrans(List<UiEmployeeTransaction> empTrans) {
        this.empTrans = empTrans;
    }
}
