package bronz.accounting.bunk.model;

import java.util.ArrayList;
import java.util.List;

import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.products.model.Product;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.products.model.ReceiptSummary;
import bronz.accounting.bunk.tankandmeter.model.TankTransaction;

public class StockReceipt
{
	public static final String FUEL_RECEIPT_TYPE = "FUEL";
    public static final String OIL_RECEIPT_TYPE = "LUBES";
    public static final String BWATER_RECEIPT_TYPE = "ADD_PRODS";
	
    private ReceiptSummary receiptSummary;
    private final List<Product> updatedProducts;
    private final List<ProductTransaction> productTransactions;
    private final List<TankTransaction> tankTransactions;
    private final List<PartyTransaction> partyTransactions;
    
    public StockReceipt()
    {
        super();
        this.updatedProducts = new ArrayList<Product>();
        this.productTransactions = new ArrayList<ProductTransaction>();
        this.partyTransactions = new ArrayList<PartyTransaction>();
        this.tankTransactions = new ArrayList<TankTransaction>();
    }
    
    public List<ProductTransaction> getProductTransactions()
    {
        return productTransactions;
    }

    public List<TankTransaction> getTankTransactions()
    {
        return tankTransactions;
    }

    public List<PartyTransaction> getPartyTransactions()
    {
        return partyTransactions;
    }

    public ReceiptSummary getReceiptSummary()
    {
        return receiptSummary;
    }

    public void setReceiptSummary( ReceiptSummary receiptSummary )
    {
        this.receiptSummary = receiptSummary;
    }

    public List<Product> getUpdatedProducts() {
        return updatedProducts;
    }

    /*public String toString()
    {
        return String.format( "\nInvoice no:%1$-15s CHQ:%2$-10s DATE:%3$s\n" +
                "PAYMENT BANK:%4$s %5$s\nPRODUCT DETAILS:%6$s\n", this.invoiceNumber,
                this.chequeNo, date, this.partyName, payment.toString(),
                (null == productTransactions)?"":productTransactions.toString() );
    }*/

    
}
