package bronz.accounting.bunk.util;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bronz.accounting.bunk.party.model.PartyClosingBalance;

/**
 * Created by pmathew on 7/18/16.
 */
public class EntityTransactionBuilderTest extends TestCase {
    private List<PartyClosingBalance> mockClosingBalance = new ArrayList<PartyClosingBalance>();
    protected void setUp() throws Exception {
        mockClosingBalance.add(new PartyClosingBalance(375, 375, "test_party1", 100, new BigDecimal(777000.00)));
        mockClosingBalance.add(new PartyClosingBalance(250, 250, "test_party2", 100, new BigDecimal(43262668.00)));
    }

    public void testSwapTrans() {
        EntityTransactionBuilder.PartyTransactionBuilder partyTransactionBuilder =
            new EntityTransactionBuilder.PartyTransactionBuilder(mockClosingBalance, 100);

        partyTransactionBuilder.addSwapTrans(375, 250, new BigDecimal(4145602.00), "test", "test", "DEBIT_S", "CREDIT_S");

        System.out.print(partyTransactionBuilder.getTransactions());

    }

}
