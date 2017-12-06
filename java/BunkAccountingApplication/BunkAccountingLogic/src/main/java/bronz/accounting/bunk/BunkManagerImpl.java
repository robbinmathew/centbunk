package bronz.accounting.bunk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bronz.accounting.bunk.model.QueryResults;
import bronz.accounting.bunk.model.SavedDailyStatement;
import bronz.accounting.bunk.model.dao.SavedStatementDao;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import bronz.accounting.bunk.framework.dao.DBUtil;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.framework.exceptions.BunkValidationException;
import bronz.accounting.bunk.model.ClosingStatement;
import bronz.accounting.bunk.model.StockReceipt;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyResult;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.party.model.Settlement;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.ProdRateChange;
import bronz.accounting.bunk.products.model.Product;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.products.model.ReceiptSummary;
import bronz.accounting.bunk.products.model.StockVariation;
import bronz.accounting.bunk.tankandmeter.dao.TankAndMeterDao;
import bronz.accounting.bunk.tankandmeter.model.MeterClosingReading;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.utilities.general.DateUtil;

public class BunkManagerImpl implements BunkManager {
    private static final Logger LOG = LogManager.getLogger(
            BunkManagerImpl.class );
    private int todayDate;
    
    private final PartyDao partyDao;
    private final TankAndMeterDao tankAndMeterDao;
    private final ProductDao productDao;
    private final DBUtil dbUtil;
    private final SavedStatementDao savedStatementDao;
    
    BunkManagerImpl(final PartyDao partyDao, final ProductDao productDao,
                    final TankAndMeterDao tankAndMeterDao, final DBUtil dbUtil,
                    final SavedStatementDao savedStatementDao)
    {
        super();
        this.partyDao = partyDao;
        this.dbUtil = dbUtil;
        this.productDao = productDao;
        this.tankAndMeterDao = tankAndMeterDao;
        this.savedStatementDao =  savedStatementDao;
    }
    
    public int getNextDate() throws BunkMgmtException
    {
    	this.todayDate = this.dbUtil.getNextDate();
        return this.todayDate;
    }
    
    public int getFirstDate() throws BunkMgmtException
    {
        return this.dbUtil.getFirstDay();
    }
    
    public int getTodayDate() throws BunkMgmtException
    {
        return this.todayDate;
    }

    public void savePartyDetails(final List<Party> partyToBeUpdated,
                                 final List<PartyTransaction> partyTransToBeUpdated) throws BunkMgmtException
    {
        this.partyDao.saveParties( partyToBeUpdated );
        this.partyDao.savePartyTransactions(partyTransToBeUpdated);
    }

    public void specialUpdatePartyTrans(PartyTransaction newPartyTransaction, Integer prevSlNo, BigDecimal amtDiff) throws BunkMgmtException {
        this.partyDao.specialUpdatePartyTrans(newPartyTransaction, prevSlNo, amtDiff);
    }

    public Map<Integer, Party> getAllParties() throws BunkMgmtException
    {
        final Map<Integer, Party> partyMap = new HashMap<Integer, Party>();
        for ( Party party : partyDao.getAllParties() )
        {
            partyMap.put( party.getPartyId(), party);
        }
        return partyMap;
    }

    public List<PartyResult> getParties(String type) throws BunkMgmtException {
        List<PartyClosingBalance> partyClosingBalances = getPartyList(type);
        Map<Integer, Party> parties = getAllParties();
        List<PartyResult> results = new ArrayList<PartyResult>();
        for (PartyClosingBalance closingBalance : partyClosingBalances) {
            results.add(new PartyResult(parties.get(closingBalance.getId()), closingBalance));
        }
        return results;
    }
    
    public List<PartyTransaction> getPendingChequesAtOffice() throws BunkMgmtException
    {
        return this.partyDao.getPendingChequeAtOffice(getTodayDate());
    }
    
    public List<PartyClosingBalance> getPartyList(final String type)
            throws BunkMgmtException
    {
        return this.partyDao.getPartyList(type, getTodayDate());
    }
    
    public List<EmployeeMonthlyStatus> getEmployeesStatus()
    		throws BunkMgmtException
    {
    	return this.partyDao.getEmployeesStatus(getTodayDate());
    }
    
    public PartyClosingBalance getParty(final Integer id)
        throws BunkMgmtException
    {
        return this.partyDao.getParty(id, getTodayDate());
    }
    
    public List<ProductClosingBalance> getProductList(final String type)
            throws BunkMgmtException
    {
        return this.productDao.getProductList(type, getTodayDate());
    }
    
    public List<ProductClosingBalance> getAvailableProductList(final String type)
        throws BunkMgmtException
    {
        return this.productDao.getAvailableProductList(type, getTodayDate());
    }
    
    public List<Product> getAllProduct() throws BunkMgmtException
    {
        return this.productDao.getAllProducts();
    }

    public void saveProdDetails(final List<Product> prodToBeUpdated,
                                final List<ProductTransaction> prodTransToBeUpdated) throws BunkMgmtException
    {
        this.productDao.saveProducts( prodToBeUpdated );
        this.productDao.saveProductTransactions(prodTransToBeUpdated);
    }
    
    public List<MeterClosingReading> getMeterList() throws BunkMgmtException
    {
        return this.tankAndMeterDao.getAllMeterClosingByDate( getTodayDate() );
    }
    
    public List<MeterClosingReading> getActiveMeterList() throws BunkMgmtException
    {
        return this.tankAndMeterDao.getActiveMeterClosings();
    }

    public void saveStockReceipt(final StockReceipt stockReceipt) throws BunkMgmtException
    {
    	final String completeInvNumber = stockReceipt.getReceiptSummary().getInvoiceNumber();
    	final ReceiptSummary duplicateReceipt =
    			this.productDao.getReceiptSummary( completeInvNumber );
    	
    	if ( null != duplicateReceipt) {
    		//Trim the first two characters
    		final String invNum = completeInvNumber.substring(2);
    		throw new BunkValidationException("Duplicate invoice number!!! " +
    				"Stock receipt with invoice number '" + invNum +
    				"' is already saved on " + duplicateReceipt.getDateText() );
    	}
        this.productDao.saveProducts( stockReceipt.getUpdatedProducts() );
        this.productDao.saveStockReceipt(stockReceipt.getReceiptSummary());
        this.productDao.saveProductTransactions(stockReceipt.getProductTransactions());
        this.tankAndMeterDao.saveTankTransactions(stockReceipt.getTankTransactions());
        this.partyDao.savePartyTransactions(stockReceipt.getPartyTransactions());
    }
    
    public List<StockReceipt> getStockReciepts(final int date) throws BunkMgmtException
    {
        LOG.info("Read Stock reciepts on " + date);
        final List<StockReceipt> receipts = new LinkedList<StockReceipt>();
        
        final List<ReceiptSummary> receiptSummaries = this.productDao.getReceiptSummaries( date );
        for ( final ReceiptSummary summary : receiptSummaries )
        {
            final StockReceipt receipt = new StockReceipt();
            receipt.setReceiptSummary( summary );
            receipt.getPartyTransactions().addAll( this.partyDao.getTransByDate(
                    date, date, null, summary.getInvoiceNumber() + "%" ));
            receipt.getProductTransactions().addAll( this.productDao.getAllProductTransByDetail(
                    summary.getInvoiceNumber() ) );
            receipt.getTankTransactions().addAll( this.tankAndMeterDao.getAllTankTransByDetail(
                    summary.getInvoiceNumber() ) );
            receipts.add( receipt );
        }
        
        return receipts;
    }

    public void saveAndCloseStatement(final ClosingStatement statement) throws BunkMgmtException
    {
        LOG.info("Saving closing statement for date:" + statement.getSettlement().getSettlementDate() +
                 '(' + statement.getSettlement().getDate() + ')');
        this.tankAndMeterDao.saveMeterTransactions(statement.getMeterTransactions());
        this.productDao.saveProductTransactions(statement.getProductTransactions());
        this.tankAndMeterDao.saveTankTransactions(statement.getTankTransactions());
        this.partyDao.savePartyTransactions(statement.getPartyTransactions());
        this.partyDao.saveSettlement(statement.getSettlement());
    }

    public void savePartyTrans(final List<PartyTransaction> transactions) throws BunkMgmtException
    {
        LOG.info("Saving party transactions");
        this.partyDao.savePartyTransactions(transactions);
    }
    
    public ClosingStatement getClosingStatement(final int date) throws BunkMgmtException
    {
        LOG.info( "Get closing statement for date:" + date );
        final ClosingStatement statement = new ClosingStatement();
        //statement.setSettlement( this.partyDao.getSettlement( date ) );
        statement.getPartyTransactions().addAll( this.partyDao.getTransByDate( date, date, null, null ) );
        //statement.getProductTransactions().addAll( this.productDao.getAllProdTransByDate( date, date ) );
        return statement;
    }

    public SavedDailyStatement getSavedDailyStatement(final int date) throws BunkMgmtException
    {
        return savedStatementDao.getSavedDailyStatement(date);
    }

    public void deleteSavedDailyStatement(final int date) throws BunkMgmtException
    {
        savedStatementDao.deleteSavedDailyStatement(date);
    }

    public void saveSavedDailyStatement(SavedDailyStatement savedDailyStatement) throws BunkMgmtException
    {
        savedStatementDao.saveOrUpdateSavedDailyStatement(savedDailyStatement);
    }
    
    public List<PartyTransaction> getPartyTransactionHistory(final int partyId, final int startDate, final int endDate,
                                                             final String transTypeFilter, final String detailFilter) throws BunkMgmtException
    {
        LOG.info("Get the trans history for party:" + partyId);
        return this.partyDao.getTransForPartyByDate( partyId, startDate, endDate, transTypeFilter, detailFilter );
    }
    
    public List<ProductClosingBalance> getProductList(final int date)
            throws BunkMgmtException
    {
        return this.productDao.getProdClosingByDate(date);
    }
    
    public List<TankClosingStock> getTankList(final int date)
            throws BunkMgmtException
    {
        return this.tankAndMeterDao.getTankClosingByDate(date);
    }
    
    public List<ProdRateChange> getRateChangesHistory() throws BunkMgmtException
    {
        return this.productDao.getProdRateChanges(getTodayDate());
    }

    public void saveRateChange(final ProdRateChange prodRateChange, final ProductTransaction transaction)
            throws BunkMgmtException
    {
        this.productDao.saveProductTransactions(Arrays.asList(transaction));
        this.productDao.saveProdRateChange(prodRateChange);
    }

    public void clearTransactions(final int date) throws BunkMgmtException
    {
        this.dbUtil.clearTrans(date);
    }
    
    public void executeDbBackup() throws BunkMgmtException
    {
    	if ("ON".equals(AppConfig.ENABLE_AUTO_DB_BACKUP.getStringValue())) {
    		LOG.info("Backing up the database....");
    		try
            {
                this.dbUtil.executeDbDump( "AFTER_DAILY_" + DateUtil.getDateString( getTodayDate() ));
                LOG.info("DB backup successful....");
            }
            catch ( final Exception exception )
            {
            	//Ground it..
            	LOG.error( "Failed to clear the transactions", exception );
            }
		}
    	else
    	{
    		LOG.debug("Ignoring backing up the database since the auto backup is not enabled.");
    	}
    }
    
    @Deprecated
    /*public void paySalary(final int lastDayOfMonth) throws BunkMgmtException
    {
        try
        {
            final String monthText = DateUtil.getDateYearMonthString( lastDayOfMonth );
            final String payingMonthSalaryMsg = "Paying salary for : " + monthText;
            LOG.info( payingMonthSalaryMsg );
            final List<PartyClosingBalance> employees = partyDao.getPartyList( PartyDao.EMPLOYEES, lastDayOfMonth );
            final EntityTransactionBuilder transBuilder = new EntityTransactionBuilder( this, lastDayOfMonth );
            final Integer expensePartyId = AppConfig.EXPENSES_PARTY_ID.getIntValue();
            BigDecimal totalSalary = BigDecimal.ZERO; 
            for (PartyClosingBalance employee : employees )
            {
                if (BigDecimal.ZERO.compareTo( employee.getBalance() ) < 0  && expensePartyId != employee.getId())
                {
                    totalSalary = totalSalary.add( employee.getBalance() );
                    transBuilder.getPartyTransBuilder().addTrans( employee.getId(), employee.getBalance(), payingMonthSalaryMsg, "DEBIT_S" );
                }
            }
            
            final PartyTransaction totalSalTrans = transBuilder.getPartyTransBuilder().addTrans( expensePartyId,
                    totalSalary, "Total salary paid on month " + monthText, "CREDIT_S" );
            transBuilder.getPartyTransBuilder().addTrans( expensePartyId, totalSalTrans.getBalance(),
                    "Total expense for month " + monthText, "DEBIT_S" );
            
            this.txnManager.start();
            this.partyDao.savePartyTransactions( transBuilder.getPartyTransBuilder().getTransactions() );
            this.txnManager.commitAndClose();
        }
        catch ( final Exception exception )
        {
            this.txnManager.rollbackAndClose();
            throw new BunkMgmtException( "Failed to pay the salary", exception );
        }
    }
    
    //Pay rent should be handled with LOAD PAYMENT AND DAILY STATEMENT
    @Deprecated
    public void creditRentToCompanyAccount(final int date) throws BunkMgmtException
    {
        try
        {
            LOG.info( "Adding the monthly recovery amount" );
            final String monthText = DateUtil.getDateYearMonthString( date );
            final EntityTransactionBuilder transBuilder = new EntityTransactionBuilder( this, date );
            final Integer companyPartyId = AppConfig.COMPANY_PARTY_ID.getIntValue();
            final Integer expensePartyId = AppConfig.EXPENSES_PARTY_ID.getIntValue();
            transBuilder.getPartyTransBuilder().addSwapTrans(companyPartyId, expensePartyId,
            		new BigDecimal( AppConfig.MONTHLY_COMPANY_RENT.getValue( String.class ) ),
            		"HPCL:Rent for the month : " + monthText, "HPCL:Rent for the month : " + monthText,
            		PartyTransaction.DEBIT_SYSTEM, PartyTransaction.CREDIT_SYSTEM);
            this.txnManager.start();
            this.partyDao.savePartyTransactions( transBuilder.getPartyTransBuilder().getTransactions() );
            this.txnManager.commitAndClose();
        }
        catch ( final Exception exception )
        {
            this.txnManager.rollbackAndClose();
            throw new BunkMgmtException( "Failed to credit rent amount to company account.", exception );
        }
    }*/
    
    public Settlement getSettlement(final int date) throws BunkMgmtException
    {
        try
        {
            return this.partyDao.getSettlement( date );
        }
        catch ( final Exception exception )
        {
            throw new BunkMgmtException( "Failed to get the settlement", exception );
        }
    }
    
    public List<StockVariation> getStockVariation(final int date)
			throws BunkMgmtException
	{
		return this.productDao.getStockVariation(date);
	}

    public QueryResults getResult(final String savedQueryName, final Map<String, Object> params) throws BunkMgmtException {
        return savedStatementDao.getResult(savedQueryName, params);
    }

}
