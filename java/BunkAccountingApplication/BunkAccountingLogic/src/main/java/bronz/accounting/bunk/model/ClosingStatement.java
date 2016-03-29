package bronz.accounting.bunk.model;

import java.util.ArrayList;
import java.util.List;

import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.party.model.Settlement;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.tankandmeter.model.MeterTransaction;
import bronz.accounting.bunk.tankandmeter.model.TankStock;
import bronz.accounting.bunk.tankandmeter.model.TankTransaction;

public class ClosingStatement
{
    private Settlement settlement;
    private List<ProductTransaction> productTransactions;
    private List<TankTransaction> tankTransactions;
    private List<PartyTransaction> partyTransactions;
    private List<MeterTransaction> meterTransactions;
    private List<TankStock> tankDipStockTransactions;
    
    public ClosingStatement()
    {
        super();
        this.productTransactions = new ArrayList<ProductTransaction>();
        this.partyTransactions = new ArrayList<PartyTransaction>();
        this.tankTransactions = new ArrayList<TankTransaction>();
        this.meterTransactions = new ArrayList<MeterTransaction>();
        this.tankDipStockTransactions = new ArrayList<TankStock>();
    }
    
    public List<ProductTransaction> getProductTransactions()
    {
        return productTransactions;
    }
    
    public List<TankStock> getTankDipStockTransactions()
    {
        return tankDipStockTransactions;
    }

    public List<TankTransaction> getTankTransactions()
    {
        return tankTransactions;
    }

    public List<PartyTransaction> getPartyTransactions()
    {
        return partyTransactions;
    }

    public Settlement getSettlement()
    {
        return settlement;
    }

    public void setSettlement( Settlement settlement )
    {
        this.settlement = settlement;
    }

    public List<MeterTransaction> getMeterTransactions()
    {
        return meterTransactions;
    }

    public void setProductTransactions(
        List<ProductTransaction> productTransactions) {
        this.productTransactions = productTransactions;
    }

    public void setTankTransactions(
        List<TankTransaction> tankTransactions) {
        this.tankTransactions = tankTransactions;
    }

    public void setPartyTransactions(List<PartyTransaction> partyTransactions) {
        this.partyTransactions = partyTransactions;
    }

    public void setMeterTransactions(
        List<MeterTransaction> meterTransactions) {
        this.meterTransactions = meterTransactions;
    }

    public void setTankDipStockTransactions(
        List<TankStock> tankDipStockTransactions) {
        this.tankDipStockTransactions = tankDipStockTransactions;
    }
}
