package bronz.accounting.bunk.webservice;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.model.ClosingStatement;
import bronz.accounting.bunk.model.SavedDailyStatement;
import bronz.accounting.bunk.model.StockReceipt;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.party.model.Settlement;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.ProdRateChange;
import bronz.accounting.bunk.products.model.Product;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.products.model.ProductType;
import bronz.accounting.bunk.products.model.ReceiptSummary;
import bronz.accounting.bunk.products.model.StockVariation;
import bronz.accounting.bunk.reports.JRReportsCreator;
import bronz.accounting.bunk.reports.exception.ReportException;
import bronz.accounting.bunk.reports.model.Report;
import bronz.accounting.bunk.tankandmeter.model.MeterClosingReading;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.tankandmeter.model.TankTransaction;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.accounting.bunk.util.EntityNameCache;
import bronz.accounting.bunk.util.EntityTransactionBuilder;
import bronz.accounting.bunk.webservice.model.DailyStatementInfo;
import bronz.accounting.bunk.webservice.model.UiDailyStatement;
import bronz.accounting.bunk.webservice.model.UiEmployeeTransaction;
import bronz.accounting.bunk.webservice.model.UiFuelReceipt;
import bronz.accounting.bunk.webservice.model.UiFuelSale;
import bronz.accounting.bunk.webservice.model.UiLubeSale;
import bronz.accounting.bunk.webservice.model.UiMeterSale;
import bronz.accounting.bunk.webservice.model.UiOfficeCash;
import bronz.accounting.bunk.webservice.model.UiPartyTransaction;
import bronz.accounting.bunk.webservice.model.UiRateChange;
import bronz.accounting.bunk.webservice.model.UiStockReceipt;
import bronz.accounting.bunk.webservice.model.UiTankReceipt;
import bronz.accounting.bunk.webservice.model.UiTankSale;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.DateUtil;
import bronz.utilities.general.Pair;

/**
 * Created by pmathew on 1/9/16.
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BunkAccountingWebService {
    private static final Logger LOG = LogManager.getLogger(
        BunkAccountingWebService.class);
    private static final ObjectMapper JSON_SERIALIZER = new ObjectMapper();

    private final BunkManager bunkManager;
    private final JRReportsCreator reportsCreator;

    public BunkAccountingWebService() throws BunkMgmtException {
        this.bunkManager = BunkAppInitializer.getInstance().getBunkManager();
        this.reportsCreator = BunkAppInitializer.getInstance().getReportsCreator();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "working";
    }

    @GET
    @Path("info")
    public Map getInfo() throws BunkMgmtException
    {
        final int todayDate = this.bunkManager.getNextDate();
        final Calendar calendar = DateUtil.getCalendarEquivalent(todayDate);
        final Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("firstDate", this.bunkManager.getFirstDate());
        infoMap.put("firstDateText", DateUtil.getSimpleDateString(this.bunkManager.getFirstDate()));
        infoMap.put("todayDate", todayDate);
        infoMap.put("todayDateText", DateUtil.getSimpleDateString(calendar));
        infoMap.put("todayDateDayText", DateUtil.getDateStringWithDay(calendar));
        for(AppConfig config : AppConfig.values()) {
            if(!config.isSecure()) {
                infoMap.put(config.name(), config.getValue(Object.class));
            }
        }
        return infoMap;
    }

    @POST
    @Path("savePartyDetails")
    public void savePartyDetails(final Pair<List<Party>,List<PartyTransaction>> data) throws BunkMgmtException
    {
        bunkManager.savePartyDetails(data.getFirst(), data.getSecond());
    }

    @GET
    @Path("parties")
    public List<Party> getAllParties() throws BunkMgmtException
    {
        return this.bunkManager.getAllParties();
    }

    @GET
    @Path("pendingChequesAtOffice")
    public List<PartyTransaction> getPendingChequesAtOffice() throws BunkMgmtException
    {
        return this.bunkManager.getPendingChequesAtOffice();
    }

    @GET
    @Path("partiesClosingBalance")
    public List<PartyClosingBalance> getPartyList( final @QueryParam("type") String type )
        throws BunkMgmtException
    {
        return this.bunkManager.getPartyList(type);
    }

    @GET
    @Path("officeClosingBalance")
    public List<PartyClosingBalance> getOfficeCashList()
        throws BunkMgmtException
    {
        final List<PartyClosingBalance> partyClosingBalances = new ArrayList<PartyClosingBalance>();
        partyClosingBalances.add(bunkManager.getParty(AppConfig.OFFICE_CASH_PARTY_ID.getValue( Integer.class )));
        partyClosingBalances.add(bunkManager.getParty(AppConfig.OFFICE_CHEQUE_PARTY_ID.getValue(Integer.class)));
        return partyClosingBalances;
    }

    @GET
    @Path("employeesStatus")
    public List<EmployeeMonthlyStatus> getEmployeesStatus()
        throws BunkMgmtException
    {
        return this.bunkManager.getEmployeesStatus();
    }

    @GET
    @Path("productsClosingBalance")
    public List<ProductClosingBalance> getProductList( final @QueryParam("type") String type )
        throws BunkMgmtException
    {
        return this.bunkManager.getProductList(type);
    }

    @GET
    @Path("getAvailableProductList")
    public List<ProductClosingBalance> getAvailableProductList( final @QueryParam("type") String type )
        throws BunkMgmtException
    {
        return this.bunkManager.getAvailableProductList(type);
    }

    @GET
    @Path("products")
    public List<Product> getAllProduct() throws BunkMgmtException
    {
        return this.bunkManager.getAllProduct();
    }

    @GET
    @Path("meterClosingReading")
    public List<MeterClosingReading> getMeterList() throws BunkMgmtException
    {
        return this.bunkManager.getMeterList();
    }

    @GET
    @Path("activeMeterClosingReading")
    public List<MeterClosingReading> getActiveMeterList() throws BunkMgmtException
    {
        return this.bunkManager.getActiveMeterList();
    }

    @POST
    @Path("stockReceipt")
    public void saveStockReceipt( final UiStockReceipt uiStockReceipt ) throws BunkMgmtException
    {
        final int todayDate = bunkManager.getTodayDate();
        final EntityTransactionBuilder transBuilder = new EntityTransactionBuilder(bunkManager, todayDate);
        final StockReceipt stockReceipt = new StockReceipt();
        final String typePrefix;
        final Integer partyId;
        final String partyTransType;
        final String partyTransMessage;
        final StringBuffer stockReceiptComment = new StringBuffer("Total load amount:")
            .append(uiStockReceipt.getTotalLoad()).append(" (items/liters)");
        final ProductType productType;

        if(StockReceipt.FUEL_RECEIPT_TYPE.equals(uiStockReceipt.getType())) {
            typePrefix = "RF";
            partyId = AppConfig.COMPANY_PARTY_ID.getValue( Integer.class );
            partyTransType = "DEBIT_S";
            partyTransMessage = ":PROD COST";
            productType = ProductType.FUEL_PRODUCTS;
        } else if(StockReceipt.OIL_RECEIPT_TYPE.equals(uiStockReceipt.getType())) {
            typePrefix = "RO";
            partyId = AppConfig.COMPANY_PARTY_ID.getValue( Integer.class );
            partyTransType = "DEBIT_S";
            partyTransMessage = ":PROD COST";
            productType = ProductType.LUBE_PRODUCTS;
        } else {
            //StockReceipt.BWATER_RECEIPT_TYPE
            typePrefix = "RB";
            partyId = AppConfig.EXPENSES_PARTY_ID.getIntValue();
            partyTransType = "CREDIT";
            partyTransMessage = ":BATTERY WATER COST";
            productType = ProductType.ADDITIONAL_PRODUCTS;
        }

        final String transactionType = typePrefix + uiStockReceipt.getInvoiceNo();

        //Add tank transactions
        for(UiTankReceipt uiTankReceipt : uiStockReceipt.getTankReceipts()) {
            if ( CustomDecimal.ZERO.compareTo( uiTankReceipt.getReceiptAmt()) < 0 )
            {
                transBuilder.getTankTransBuilder().addTrans( uiTankReceipt.getTankId(), uiTankReceipt.getReceiptAmt(),
                                                             transactionType, "FILL" );
            }
        }
        stockReceipt.getTankTransactions().addAll(transBuilder.getTankTransBuilder().getTransactions() );

        //Add fuels transactions
        for ( UiFuelReceipt fuelReceipt : uiStockReceipt.getFuelReceipts() )
        {
            if ( CustomDecimal.ZERO.compareTo( fuelReceipt.getReceiptAmt()) < 0 ) {
                if (fuelReceipt.getProductId() == null || fuelReceipt.getProductId() == 0) {
                    //New product
                    transBuilder.getProdTransBuilder().addNewProd(fuelReceipt.getProductName(), fuelReceipt.getUnitSellingPrice(),
                        fuelReceipt.getMargin(), fuelReceipt.getReceiptAmt(), transactionType, "RECEIPT", productType);
                } else {
                    //Existing product
                    transBuilder.getProdTransBuilder().addTransWithMargin(fuelReceipt.getProductId(),
                        fuelReceipt.getReceiptAmt(),
                        fuelReceipt.getMargin(), "RECEIPT",
                        transactionType);
                }
            }
        }

        stockReceipt.getUpdatedProducts().addAll(transBuilder.getProdTransBuilder().getProdToBeUpdated());
        stockReceipt.getProductTransactions().addAll(transBuilder.getProdTransBuilder().getTransactions());

        final BigDecimal invoiceAmt = uiStockReceipt.getInvoiceAmt();
        transBuilder.getPartyTransBuilder().addTrans( partyId, invoiceAmt, transactionType + partyTransMessage, partyTransType );
        stockReceipt.getPartyTransactions().addAll(transBuilder.getPartyTransBuilder().getTransactions());
        final ReceiptSummary receiptSummary = new ReceiptSummary();
        receiptSummary.setComments(stockReceiptComment.toString());
        receiptSummary.setDate(todayDate);
        receiptSummary.setInvoiceNumber(transactionType);
        receiptSummary.setTotalAmt(BunkUtil.setAsPrice(invoiceAmt));
        receiptSummary.setType(uiStockReceipt.getType());
        stockReceipt.setReceiptSummary(receiptSummary);

        this.bunkManager.saveStockReceipt(stockReceipt);
    }

    @GET
    @Path("stockReciepts")
    public List<StockReceipt> getStockReciepts( final @QueryParam("date") int date ) throws BunkMgmtException
    {
        return this.bunkManager.getStockReciepts(date);
    }

    @POST
    @Path("saveDailyStatement")
    public void saveAndCloseStatement(final UiDailyStatement uiDailyStatement) throws BunkMgmtException
    {
        final int todayInteger = bunkManager.getTodayDate();
        if("SUBMIT".equals(uiDailyStatement.getType())) {
            final EntityTransactionBuilder transBuilder = new EntityTransactionBuilder( bunkManager, todayInteger);
            final ClosingStatement statement = new ClosingStatement();
            final Settlement settlement = new Settlement();
            BigDecimal totalDiscount = CustomDecimal.ZERO;

            for ( UiMeterSale meterSale : uiDailyStatement.getMeterSales() ) {
                transBuilder.getMeterTransactionBuilder().addTrans( meterSale.getMeterId(),
                    meterSale.getTotalSale(), "Test sale:" + meterSale.getTestSale(), "SALE" );
            }
            final Map<Integer, BigDecimal> stockVariations = new HashMap<Integer, BigDecimal>();
            for ( UiTankSale tankSale : uiDailyStatement.getTankSales() ) {
                transBuilder.getTankTransBuilder().addTrans( tankSale.getTankId(),
                                                             tankSale.getSale(), "", TankTransaction.SALE );
                if (tankSale.getTestSale().compareTo(CustomDecimal.ZERO ) > 0) {
                    transBuilder.getTankTransBuilder().addTrans( tankSale.getTankId(),
                        tankSale.getTestSale(), "", TankTransaction.TEST );
                }

                //Consider TANK STOCK DIFF only if dipstock is provided.
                if (tankSale.getDipStock().compareTo( CustomDecimal.ZERO ) > 0) {
                    transBuilder.getTankTransBuilder().addTrans( tankSale.getTankId(),
                                                                 tankSale.getDiffToday(), "", TankTransaction.DIFF );
                }
                //Override the product stock with the actual
                if (DateUtil.isLastDayOfMonth(todayInteger)) {
                    transBuilder.getProdTransBuilder().addTrans( tankSale.getProductId(), tankSale.getDiffThisMonth(),
                        "Stock diff for tank id " + tankSale.getTankId(), ProductTransaction.DIFF );
                    if (stockVariations.get(tankSale.getProductId()) == null) {
                        stockVariations.put(tankSale.getProductId(), BigDecimal.ZERO);
                    }
                    stockVariations.put(tankSale.getProductId(), stockVariations.get(
                        tankSale.getProductId()).add(tankSale.getDiffThisMonth()));
                }
            }
            for (UiFuelSale bean : uiDailyStatement.getFuelSales()) {
                transBuilder.getProdTransBuilder().addTrans( bean.getProductId(),
                                                             bean.getActualSale(), "", "SALE" );
                final BigDecimal stockVariation = stockVariations.get(bean.getProductId());
                if (null != stockVariation) {
                    transBuilder.getPartyTransBuilder().addTrans( AppConfig.EXPENSES_PARTY_ID.getIntValue(),
                          BunkUtil.setAsPrice(stockVariation.multiply(bean.getUnitSellingPrice())),
                          EntityNameCache.getProductName(bean.getProductId()) + ":Stock variation this month(Litres):" +
                          BunkUtil.setAsProdVolume(stockVariation), "DEBIT_S" );
                }
            }
            for (UiLubeSale bean : uiDailyStatement.getLubesSales()) {
                if (bean.getTotalSale().compareTo( CustomDecimal.ZERO ) > 0) {
                    if (bean.getDiscountPerUnit().compareTo( CustomDecimal.ZERO ) > 0) {
                        totalDiscount = totalDiscount.add( bean.getDiscountPerUnit().multiply( bean.getTotalSale() ) );
                    }
                    transBuilder.getProdTransBuilder().addTransWithDiscount( bean.getProductId(),
                         bean.getTotalSale(), bean.getDiscountPerUnit(), "SALE" );
                }
            }
            for (UiPartyTransaction bean : uiDailyStatement.getPartyTrans()) {
                if (bean.getCreditAmt().compareTo( CustomDecimal.ZERO ) > 0) {
                    transBuilder.getPartyTransBuilder().addTrans( bean.getPartyId(),
                          bean.getCreditAmt(), bean.getCreditDetail(), PartyTransaction.CREDIT );
                }
                if (bean.getDebitAmt().compareTo( CustomDecimal.ZERO ) > 0) {
                    if(bean.getIsChequeDebit()) {
                        transBuilder.getPartyTransBuilder().addTrans( bean.getPartyId(),
                              bean.getDebitAmt(), "BY CHQ:" + bean.getDebitDetail(), PartyTransaction.CHEQUE_DEBIT );
                    } else {
                        transBuilder.getPartyTransBuilder().addTrans( bean.getPartyId(),
                              bean.getDebitAmt(), bean.getDebitDetail(), PartyTransaction.DEBIT );
                    }
                }
            }

            for (UiEmployeeTransaction bean : uiDailyStatement.getEmpTrans())
            {
                if (bean.getSalaryAmt().compareTo( CustomDecimal.ZERO ) > 0) {
                    transBuilder.getPartyTransBuilder().addTrans( bean.getPartyId(),
                          bean.getSalaryAmt(), "SALARY:" + bean.getSalaryDetail(), PartyTransaction.CREDIT_SALARY );
                }
                if (bean.getIncentiveAmt().compareTo( CustomDecimal.ZERO ) > 0) {
                    transBuilder.getPartyTransBuilder().addTrans( bean.getPartyId(),
                          bean.getIncentiveAmt(), "INCENTIVE:" +bean.getIncentiveDetail(), PartyTransaction.CREDIT_INCENTIVE );
                }
            }

            for ( UiOfficeCash bean : uiDailyStatement.getOfficeCash() )
            {
                transBuilder.getPartyTransBuilder().addTrans( bean.getId(), bean.getToOffice(), "To office",
                                                              "CREDIT_S" );
                if ( AppConfig.OFFICE_CHEQUE_PARTY_ID.getIntValue() != bean.getId() &&
                     bean.getPaidToBank().compareTo( CustomDecimal.ZERO ) > 0) {
                    transBuilder.getPartyTransBuilder().addSwapTrans( bean.getId(), AppConfig.DEFAULT_BANK_PARTY_ID.getIntValue(),
                          bean.getPaidToBank(), "Cash paid to bank", "Cash deposit:" + EntityNameCache.getPartyName(bean.getId()), "DEBIT_S", "CREDIT_S" );
                } else if ( AppConfig.OFFICE_CHEQUE_PARTY_ID.getIntValue() == bean.getId() &&
                            bean.getPaidToBank().compareTo( CustomDecimal.ZERO ) > 0) {
                    transBuilder.getPartyTransBuilder().addSwapTrans( AppConfig.OFFICE_CHEQUE_PARTY_ID.getIntValue(),
                        AppConfig.DEFAULT_BANK_PARTY_ID.getIntValue(),
                        bean.getPaidToBank(), "",
                        "Cheque credited:" + EntityNameCache.getPartyName(bean.getId()), "DEBIT_S", "CREDIT_S" );
                    /*for(ChequeTransWrapper chqTrans: bean.getCreditedCheques()){
                        transBuilder.getPartyTransBuilder().addSwapTrans( this.offChequePartyId, defaultBankPartyId,
                                                                          chqTrans.getAmount(), PartyTransaction.CHEQUE_CREDIT_TRANS_DETAIL_PREFIX +
                                                                                                chqTrans.getPartyTransaction().getSlNo() + ":"  + chqTrans.getCreditDetail(),
                                                                          "Cheque credited:" + chqTrans.getPartyName(), "DEBIT_S", "CREDIT_S" );
                    }*/
                }
            }

            if (CustomDecimal.ZERO.compareTo( totalDiscount ) < 0) {
                transBuilder.getPartyTransBuilder().addTrans( AppConfig.EXPENSES_PARTY_ID.getIntValue(), totalDiscount,
                                                              "Discount to lubes and battery water", "CREDIT" );
            }

            /*if (salBeansForMonthlyClosing != null)
            {
                //Monthly closing
                final String monthText = DateUtil.getDateYearMonthString( this.todayInteger );
                LOG.info( "Adding salary transactions.." );

                final Map<Integer, BigDecimal> employeeIdToAdvSalMap = new HashMap<Integer, BigDecimal>();
                BigDecimal totalSalaries = BigDecimal.ZERO;
                BigDecimal totalAdvSalaries = BigDecimal.ZERO;
                BigDecimal totalIncentives = BigDecimal.ZERO;
                for (EmpSalBean salBean : salBeansForMonthlyClosing )
                {
                    //Add negative amount for advance salary
                    if (BigDecimal.ZERO.compareTo( salBean.getSalAdvance() ) < 0)
                    {
                        employeeIdToAdvSalMap.put(salBean.getId(), salBean.getSalAdvance());
                        totalAdvSalaries = totalAdvSalaries.add( salBean.getSalAdvance() );
                        transBuilder.getPartyTransBuilder().addTrans( salBean.getId(), salBean.getSalAdvance().negate(),
                                                                      "Advance salary recieved in month " + monthText, PartyTransaction.CREDIT_SALARY );
                    }
                    //Add amount for salary
                    if (BigDecimal.ZERO.compareTo( salBean.getSalThisMonth() ) < 0)
                    {
                        totalSalaries = totalSalaries.add( salBean.getSalThisMonth() );
                        transBuilder.getPartyTransBuilder().addTrans( salBean.getId(), salBean.getSalThisMonth(),
                                                                      "Salary recieved in month " + monthText, PartyTransaction.DEBIT_SYSTEM );
                    }
                    //Add incentives
                    if (BigDecimal.ZERO.compareTo( salBean.getIncentives() ) < 0)
                    {
                        totalIncentives = totalIncentives.add( salBean.getIncentives() );
                        transBuilder.getPartyTransBuilder().addTrans( salBean.getId(), salBean.getIncentives(),
                                                                      "Incentives recieved in month " + monthText, PartyTransaction.DEBIT_SYSTEM );
                    }
                }
                // Add the ADVANCE SALARY paid this month to expense to balance the settlement.
                if (BigDecimal.ZERO.compareTo( totalAdvSalaries ) < 0)
                {
                    transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
                                                                  totalAdvSalaries, "Advance salaries paid this month " + monthText, PartyTransaction.CREDIT );
                }
                // Add the total salary and incentives to expenses
                transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
                                                              totalSalaries, "Total salary paid on month " + monthText, PartyTransaction.CREDIT_SYSTEM );
                transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
                                                              totalIncentives, "Total incentives paid on month " + monthText, PartyTransaction.CREDIT_SYSTEM );

                // Close the expenses for this month.
                transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
                                                              transBuilder.getPartyTransBuilder().getBalanceForEntity(this.expensePartyId),
                                                              "Total expenses for month " + monthText, PartyTransaction.DEBIT_SYSTEM );

                if (BigDecimal.ZERO.compareTo( totalAdvSalaries ) < 0)
                {
                    // Add the ADVANCE SALARY paid this month to expense to balance the settlement.
                    transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
                                                                  totalAdvSalaries, "Advance salaries paid this month " + monthText, PartyTransaction.CREDIT );
                }

                // #### FOR NEXT DAY

                for (Map.Entry<Integer, BigDecimal> entry : employeeIdToAdvSalMap.entrySet() )
                {
                    final PartyTransaction trans = transBuilder.getPartyTransBuilder().addTrans(
                        entry.getKey(), entry.getValue(), "Adv salary collected the previous month",
                        PartyTransaction.CREDIT_SALARY );
                    //Increment the date so that the transaction comes on the next day.
                    trans.setDate(this.todayInteger + 1);
                }
                if (BigDecimal.ZERO.compareTo( totalAdvSalaries ) < 0)
                {
                    // Deduct the ADVANCE SALARY paid prev month from expense to balance the settlement.
                    final PartyTransaction trans = transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
                                                                                                 totalAdvSalaries, "Advance salaries paid previous month " + monthText, PartyTransaction.DEBIT );
                    //Increment the date so that the transaction comes on the next day.
                    trans.setDate(this.todayInteger + 1);
                }
            }
            */

            settlement.setClosingBal( uiDailyStatement.getClosingBalance() );
            settlement.setComments( "" );
            settlement.setCreatedBy( "admin" );
            settlement.setCreatedDate( new Date() );
            settlement.setSettlementDate( DateUtil.getCalendarEquivalent( uiDailyStatement.getDate() ).getTime() );
            settlement.setDate( uiDailyStatement.getDate() );
            settlement.setSettlementType( "DAILY_CLOSING" );
            statement.getMeterTransactions().addAll( transBuilder.getMeterTransactionBuilder().getTransactions() );
            statement.getPartyTransactions().addAll( transBuilder.getPartyTransBuilder().getTransactions() );
            statement.getProductTransactions().addAll( transBuilder.getProdTransBuilder().getTransactions() );
            statement.getTankTransactions().addAll( transBuilder.getTankTransBuilder().getTransactions() );
            statement.setSettlement( settlement );
            this.bunkManager.saveAndCloseStatement(statement);
        } else {
            try {
                final SavedDailyStatement savedDailyStatement = new SavedDailyStatement();
                savedDailyStatement.setDate(uiDailyStatement.getDate());
                savedDailyStatement.setContents(JSON_SERIALIZER.writeValueAsBytes(uiDailyStatement));
                this.bunkManager.saveSavedDailyStatement(savedDailyStatement);
            } catch (Exception e) {
                LOG.error("Failed to save stmt", e);
            }
        }
    }

    @GET
    @Path("closingStatement")
    public ClosingStatement getClosingStatement( final @QueryParam("date") int date ) throws BunkMgmtException {
        return this.bunkManager.getClosingStatement(date);
    }

    @GET
    @Path("savedDailyStatement")
    public UiDailyStatement getSavedDailyStatement( final @QueryParam("date") int date ) throws BunkMgmtException
    {
        return getSavedUiDailyStmt(date);
    }

    private UiDailyStatement getSavedUiDailyStmt(final int date) throws BunkMgmtException {
        try {
            final SavedDailyStatement savedStmt = this.bunkManager.getSavedDailyStatement(date);
            if(savedStmt != null && savedStmt.getContents() != null && savedStmt.getContents().length >0) {
                return JSON_SERIALIZER.readValue(savedStmt.getContents(), UiDailyStatement.class);
            }
        } catch (Exception e) {
            LOG.error("Failed to read saved daily statement", e);
        }
        return null;
    }

    @DELETE
    @Path("deleteSavedDailyStatement")
    public void deleteSavedDailyStatement( final  @QueryParam("date") int date ) throws BunkMgmtException
    {
        this.bunkManager.deleteSavedDailyStatement(date);
    }

    @GET
    @Path("getPartyTransactionHistory")
    public List<PartyTransaction> getPartyTransactionHistory( final @QueryParam("id") int partyId ) throws BunkMgmtException
    {
        return this.bunkManager.getPartyTransactionHistory(partyId);
    }

    @GET
    @Path("getProductListByDate")
    public List<ProductClosingBalance> getProductList( final @QueryParam("date") int date )
        throws BunkMgmtException
    {
        return this.bunkManager.getProductList(date);
    }

    @GET
    @Path("tankClosingStock")
    public List<TankClosingStock> getTankList( final @QueryParam("date") int date )
        throws BunkMgmtException
    {
        return this.bunkManager.getTankList(date);
    }

    @GET
    @Path("getRateChangesHistory")
    public List<ProdRateChange> getRateChangesHistory() throws BunkMgmtException
    {
        return this.bunkManager.getRateChangesHistory();
    }

    @POST
    @Path("saveRateChange")
    public void saveRateChange( final List<UiRateChange> list ) throws BunkMgmtException {
        final int todayInteger = bunkManager.getTodayDate();
        for (UiRateChange rateChange : list) {
            final ProdRateChange prodRateChange = new ProdRateChange();
            prodRateChange.setComments("TOTAL CASH DIFF IS:" + rateChange.getTotalCashDiff().toPlainString());
            prodRateChange.setDate(todayInteger);
            prodRateChange.setOldPrice(rateChange.getOldPrice());
            prodRateChange.setProdId(rateChange.getProductId());
            prodRateChange.setStock(rateChange.getCurrentStock());

            final ProductTransaction transaction = new ProductTransaction();
            transaction.setBalance(rateChange.getCurrentStock());
            transaction.setUnitPrice(rateChange.getNewUnitPrice());
            transaction.setDate(todayInteger);
            transaction.setDetail(
                "Rate changed from " + rateChange.getOldPrice() + " to " + rateChange.getNewUnitPrice()
                    .toPlainString());
            transaction.setProductId(rateChange.getProductId());
            transaction.setQuantity(CustomDecimal.ZERO);
            transaction.setTransactionType(ProductTransaction.RECEIPT);
            transaction.setMargin(rateChange.getMargin());
            this.bunkManager.saveRateChange(prodRateChange, transaction);
        }
    }

    @GET
    @Path("getSettlement")
    public Settlement getSettlement(final @QueryParam("date") int date) throws BunkMgmtException
    {
        return this.bunkManager.getSettlement(date);
    }

    @GET
    @Path("dailyStatement")
    public DailyStatementInfo getDailyStatementInfo(final @QueryParam("date") int date) throws BunkMgmtException
    {
        final DailyStatementInfo dailyStatementInfo = new DailyStatementInfo();
        dailyStatementInfo.setClosingStatement(bunkManager.getClosingStatement(date));
        dailyStatementInfo.setSavedDailyStatement(getSavedUiDailyStmt(date));
        dailyStatementInfo.setSettlement(bunkManager.getSettlement(date-1));

        return dailyStatementInfo;
    }

    @GET
    @Path("getStockVariation")
    public List<StockVariation> getStockVariation(final @QueryParam("date") int date)
        throws BunkMgmtException
    {
        return this.bunkManager.getStockVariation(date);
    }

    @DELETE
    @Path("clearDsr")
    public void clearDsr(final @QueryParam("date") int date) throws BunkMgmtException {
        this.bunkManager.clearTransactions(date);
    }


    @GET
    @Path("fuelsSalesSummary")
    public List getFuelsSalesSummary(final @QueryParam("start") int start, final @QueryParam("end") int end)
        throws BunkMgmtException {
        return this.bunkManager.getFuelsSaleSummary(start, end);
    }

    @GET
    @Path("report")
    public Response getReport(@Context UriInfo uriInfo, @QueryParam("type") String type, @DefaultValue("pdf") @QueryParam("format") final String format)
        throws BunkMgmtException {
        final Report report;
        if ( "DAILY_STMT".equals(type) ) {
            int date;
            if(StringUtils.isNotEmpty(uriInfo.getQueryParameters().getFirst("dateText"))) {
                date = DateUtil.getDateFromSimpleDateString(uriInfo.getQueryParameters().getFirst("dateText"));
            } else {
                date = Integer.parseInt(uriInfo.getQueryParameters().getFirst("date"));
            }
            report = this.reportsCreator.createClosingStatement( date );
        } else {
            throw new UnsupportedOperationException("This report is not supported");
        }

        final StreamingOutput stream = new StreamingOutput() {
            public void write(OutputStream os) throws IOException, WebApplicationException {

                //Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                try {
                    if("image".equals(format)) {
                        report.printAsImage(os);
                    } else {
                        report.writeAsPDF(os);
                    }
                } catch (ReportException e) {
                    throw new IOException(e);
                }
                //writer.flush();
            }
        };

        Response.ResponseBuilder responseBuilder = Response.ok(stream);
        if("image".equals(format)) {
            responseBuilder.header("Content-Type", "image/png");
        } else {
            responseBuilder.header("Content-Type", "application/pdf");
        }

        return responseBuilder.build();
    }


}
