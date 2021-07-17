package bronz.accounting.bunk.webservice;

import bronz.accounting.bunk.model.*;
import bronz.accounting.bunk.scanner.PriceChangeScanModel;
import bronz.accounting.bunk.scanner.ProdReceiptScanModel;
import bronz.accounting.bunk.webservice.model.*;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyResult;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.party.model.Settlement;
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
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.DateUtil;

/**
 * Created by pmathew on 1/9/16.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
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
    @Path("logout")
    @Produces(MediaType.TEXT_PLAIN)
    public String logout(@Context HttpServletRequest req) {
        HttpSession session= req.getSession(true);
        session.invalidate();
        return "logged out";
    }

    @GET
    @Path("info")
    public Map getInfo(@Context SecurityContext sc) throws BunkMgmtException
    {
        final int todayDate = this.bunkManager.getNextDate();
        final Calendar calendar = DateUtil.getCalendarEquivalent(todayDate);
        final Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("firstDate", this.bunkManager.getFirstDate());
        infoMap.put("firstDateText", DateUtil.getSimpleDateString(this.bunkManager.getFirstDate()));
        infoMap.put("todayDate", todayDate);
        infoMap.put("todayDateText", DateUtil.getSimpleDateString(calendar));
        infoMap.put("todayDateDayText", DateUtil.getDateStringWithDay(calendar));
        infoMap.put("username", sc.getUserPrincipal().getName());
        infoMap.put("isAdmin", InMemoryLoginService.getInstance().getLoginService().getUsers().get(sc.getUserPrincipal().getName()).isUserInRole("admin", null));

        for(AppConfig config : AppConfig.values()) {
            if(!config.isSecure()) {
                infoMap.put(config.name(), config.getValue(Object.class));
            }
        }
        return infoMap;
    }

    @POST
    @Path("savePartyDetails")
    public void savePartyDetails(final UiUpdateParties data) throws BunkMgmtException {
        final int todayDate = bunkManager.getTodayDate();
        Map<Integer, Party> currentPartyMap = bunkManager.getAllParties();

        int nextEmpId = PartyDao.MIN_EMP_ID;
        int nextBankId = PartyDao.MIN_BANK_ID;
        int nextPartyId = PartyDao.MIN_PARTY_ID;
        for (Party party : currentPartyMap.values())
        {
            if (PartyDao.MIN_EMP_ID <= party.getPartyId() && PartyDao.MIN_BANK_ID > party.getPartyId() &&
                party.getPartyId()> nextEmpId )
            {
                nextEmpId = party.getPartyId();
            }
            if (PartyDao.MIN_BANK_ID <= party.getPartyId() && PartyDao.MIN_PARTY_ID > party.getPartyId() &&
                party.getPartyId()> nextBankId )
            {
                nextBankId = party.getPartyId();
            }
            if (PartyDao.MIN_PARTY_ID <= party.getPartyId() && party.getPartyId()> nextPartyId )
            {
                nextPartyId = party.getPartyId();
            }
        }
        final List<Party> partyToBeUpdated = new ArrayList<Party>();
        final List<PartyTransaction> partyTransToBeUpdated = new ArrayList<PartyTransaction>();

        for ( Party uiParty : data.getEmployees() ) {
            Party currentPartyRecord = currentPartyMap.get(uiParty.getPartyId());
            if (currentPartyRecord == null) {
                uiParty.setPartyId(++nextEmpId);
                final PartyTransaction transaction = new PartyTransaction();
                transaction.setPartyId( uiParty.getPartyId() );
                transaction.setAmount( CustomDecimal.ZERO );
                transaction.setBalance( CustomDecimal.ZERO );
                transaction.setDate( todayDate );
                transaction.setTransactionDetail( "New party" );
                transaction.setTransactionType( "CREDIT_S" );
                partyTransToBeUpdated.add(transaction);

                partyToBeUpdated.add(uiParty);
            } else if (!currentPartyRecord.equals(uiParty)){
                partyToBeUpdated.add(uiParty);
            }
        }

        for ( Party uiParty : data.getParties() ) {
            Party currentPartyRecord = currentPartyMap.get(uiParty.getPartyId());
            if (currentPartyRecord == null) {
                uiParty.setPartyId(++nextPartyId);
                final PartyTransaction transaction = new PartyTransaction();
                transaction.setPartyId( uiParty.getPartyId() );
                transaction.setAmount( CustomDecimal.ZERO );
                transaction.setBalance( CustomDecimal.ZERO );
                transaction.setDate( todayDate );
                transaction.setTransactionDetail( "New party" );
                transaction.setTransactionType( "CREDIT_S" );
                partyTransToBeUpdated.add(transaction);

                partyToBeUpdated.add(uiParty);
            } else if (!currentPartyRecord.equals(uiParty)){
                partyToBeUpdated.add(uiParty);
            }
        }
        if (!partyToBeUpdated.isEmpty() || !partyTransToBeUpdated.isEmpty()) {
            bunkManager.savePartyDetails(partyToBeUpdated, partyTransToBeUpdated);
            BunkAppInitializer.refreshPartyNameCache();
        }

    }

    @POST
    @Path("saveScanData")
    @Produces(MediaType.TEXT_PLAIN)
    public String saveScanData(@QueryParam("type") String type, final String data) throws BunkMgmtException {
        return this.bunkManager.saveScannedData(data, ScanType.getByType(type), "web");
    }


    @GET
    @Path("scanData")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<ScanType, String> scanDataFromHpcl(@QueryParam("types") String types, final @QueryParam("fromDays") int fromDays) throws BunkMgmtException {
        Set<ScanType> scanTypes = new HashSet<>();
        for (String type : types.split(",")) {
            scanTypes.add(ScanType.getByType(type));
        }
        return this.bunkManager.scanDataFromHpcl(fromDays, scanTypes);
    }

    @GET
    @Path("getScanData")
    @Produces(MediaType.APPLICATION_JSON)
    public List<? extends Object> getScanData(@QueryParam("type") String type, final @QueryParam("start") int start, final @QueryParam("end") int end) throws BunkMgmtException {
        List<ScannedDetail> items = this.bunkManager.getScannedData( start, end, type);
        /*List<Object> results =  new ArrayList<>();
        for (ScannedDetail scannedDetail : items) {
            if ()
        }
*/
        return items;
    }

    @POST
    @Path("saveSpecialPartyTransactions")
    public void saveSpecialPartyTransactions(final UiSpecialPartyTransaction data) throws BunkMgmtException {
        if (data.getLastTransaction() == null || data.getUpdateMode() == null) {
            throw new BunkMgmtException("Incomplete request");
        }

        final PartyTransaction updatePartyTrans = new PartyTransaction();
        updatePartyTrans.setPartyId(data.getLastTransaction().getPartyId());
        BigDecimal amtDiff = null;
        if ("ADD".equals(data.getUpdateMode())) {
            updatePartyTrans.setTransactionDetail(data.getTransactionDetail());
            updatePartyTrans.setTransactionType(data.getTransactionType());
            updatePartyTrans.setAmount(data.getAmount());
            //Set date
            if (data.getDate() != null) {
                updatePartyTrans.setDate(data.getDate());
            } else {
                updatePartyTrans.setDate(DateUtil.getDateFromSimpleDateString(data.getDateText()));
            }

            //Set balance.
            BigDecimal balance = data.getLastTransaction().getBalance();
            if ( PartyTransaction.CREDIT_TRANS_TYPES.contains( data.getTransactionType() ) ) {
                balance = truncate( balance.add( data.getAmount() ) );
                amtDiff = data.getAmount();
            } else {
                balance = truncate(balance.subtract( data.getAmount() ));
                amtDiff = data.getAmount().negate();
            }
            updatePartyTrans.setBalance(balance);
        } else {
            updatePartyTrans.setSlNo(data.getLastTransaction().getSlNo());
            updatePartyTrans.setTransactionType(data.getLastTransaction().getTransactionType());
            updatePartyTrans.setDate(data.getLastTransaction().getDate());
            updatePartyTrans.setTransactionDetail(data.getLastTransaction().getTransactionDetail());
            updatePartyTrans.setAmount(data.getAmount());

            //Update balance
            amtDiff = truncate(data.getAmount().subtract(data.getLastTransaction().getAmount()));
            updatePartyTrans.setBalance(truncate(data.getLastTransaction().getBalance().add(amtDiff)));
        }
        this.bunkManager.specialUpdatePartyTrans(updatePartyTrans, data.getLastTransaction().getSlNo(), amtDiff);
    }

    private BigDecimal truncate( final BigDecimal decimal )
    {
        return decimal.setScale( 2, RoundingMode.HALF_UP );
    }

    @GET
    @Path("parties")
    public List<PartyResult> getParties(final @QueryParam("type") String type) throws BunkMgmtException
    {
        return this.bunkManager.getParties(type);
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
        stockReceipt.getTankTransactions().addAll(transBuilder.getTankTransBuilder().getTransactions());

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
        BunkAppInitializer.refreshProductNameCache();
    }

    @GET
    @Path("stockReciepts")
    public List<StockReceipt> getStockReciepts( final @QueryParam("date") int date ) throws BunkMgmtException
    {
        return this.bunkManager.getStockReciepts(date);
    }

    @POST
    @Path("saveDailyStatement")
    @Produces(MediaType.TEXT_PLAIN)
    public String saveAndCloseStatement(@Context SecurityContext sc, final UiDailyStatement uiDailyStatement) throws BunkMgmtException
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

            for ( UiTankSale tankSale : uiDailyStatement.getTankSales() ) {
                transBuilder.getTankTransBuilder().addTrans( tankSale.getTankId(),
                        BunkUtil.setAsProdVolume(tankSale.getSale().add(tankSale.getTestSale())), "", TankTransaction.SALE );
                if (tankSale.getTestSale().compareTo(CustomDecimal.ZERO ) > 0) {
                    transBuilder.getTankTransBuilder().addTrans( tankSale.getTankId(),
                        tankSale.getTestSale(), "", TankTransaction.TEST );
                }

                //Consider TANK STOCK DIFF only if dipstock is provided.
                if (tankSale.getDipStock().compareTo( CustomDecimal.ZERO ) > 0) {
                    transBuilder.getTankTransBuilder().addTrans( tankSale.getTankId(),
                                                                 tankSale.getDiffToday(), "", TankTransaction.DIFF );
                }
            }
            for (UiFuelSale bean : uiDailyStatement.getFuelSales()) {
                transBuilder.getProdTransBuilder().addTrans( bean.getProductId(),
                                                             bean.getActualSale(), "", "SALE" );

            }

            //Handle variations. Override the product stock with the actual
            final Map<Integer, BigDecimal> stockVariations = new HashMap<Integer, BigDecimal>();
            if (DateUtil.isLastDayOfMonth(todayInteger)) {
                final Map<Integer, BigDecimal> tankStockTotal = new HashMap<Integer, BigDecimal>();
                final Map<Integer, BigDecimal> monthlyVariation = new HashMap<Integer, BigDecimal>();
                //After adding all other tank and prod transactions, compare the balance and consider it as the difference
                for ( UiTankSale tankSale : uiDailyStatement.getTankSales() ) {
                    if (tankStockTotal.get(tankSale.getProductId()) == null) {
                        tankStockTotal.put(tankSale.getProductId(), BigDecimal.ZERO);
                    }
                    final BigDecimal totalStockPerProduct = BunkUtil.setAsProdVolume( tankStockTotal.get(
                            tankSale.getProductId()).add(transBuilder.getTankTransBuilder().getBalanceForEntity(tankSale.getTankId())));
                    tankStockTotal.put(tankSale.getProductId(), totalStockPerProduct);

                    //Sum up the monthly variation just for comparison
                    if (monthlyVariation.get(tankSale.getProductId()) == null) {
                        monthlyVariation.put(tankSale.getProductId(), BigDecimal.ZERO);
                    }
                    final BigDecimal totalMonthlyVariation = BunkUtil.setAsProdVolume( monthlyVariation.get(
                            tankSale.getProductId()).add(tankSale.getDiffThisMonth()));
                    monthlyVariation.put(tankSale.getProductId(), totalMonthlyVariation);
                }
                for ( Map.Entry<Integer, BigDecimal> entry : tankStockTotal.entrySet() ) {
                    BigDecimal difference = BunkUtil.setAsProdVolume(entry.getValue().subtract(transBuilder.getProdTransBuilder().getBalanceForEntity(entry.getKey())));
                    stockVariations.put(entry.getKey(), difference);
                    transBuilder.getProdTransBuilder().addTrans( entry.getKey(), difference,
                            "Stock diff id=" + entry.getKey() + " monthly_variation=" + monthlyVariation.get(entry.getKey()), ProductTransaction.DIFF );
                }
            }
            for (UiFuelSale bean : uiDailyStatement.getFuelSales()) {
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
            settlement.setCreatedBy(sc.getUserPrincipal().getName());
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
            StringBuilder responseStringBuilder = new StringBuilder();
            int nextday = this.bunkManager.getNextDate();
            try {
                //Apply rate changes for the next day;
                List<ScannedDetail> priceChanges = bunkManager.getScannedData(nextday, nextday, ScanType.PRICE_CHANGE_TYPE.getType());
                if (priceChanges != null && priceChanges.size() > 0) {
                    List<ProductClosingBalance> records =this.bunkManager.getAvailableProductList("PRIMARY_PRODUCTS");
                    Map<Integer, ProductClosingBalance> prodsMap = new HashMap<>();
                    for (ProductClosingBalance productClosingBalance : records) {
                        prodsMap.put(productClosingBalance.getProductId(), productClosingBalance);
                    }

                    StringBuilder priceChangeInfo = new StringBuilder();

                    List<UiRateChange> list = new ArrayList<>();
                    priceChangeInfo.append("<br/> Auto price change::");

                    for (ScannedDetail priceChange : priceChanges ) {

                        PriceChangeScanModel priceChangeScanModel = JSON_SERIALIZER.readValue(priceChange.getContents(), PriceChangeScanModel.class);

                        if (priceChangeScanModel == null || priceChangeScanModel.getNewPrice().intValue() <= 0) {
                            throw new IllegalStateException("Invalid priceChangeScanModel:" + priceChange.getContents());
                        }

                        ProductClosingBalance productClosingBalance = prodsMap.get(priceChangeScanModel.getProductId());
                        if (productClosingBalance == null) {
                            throw new IllegalStateException("Invalid product:" + priceChangeScanModel.getProductId());
                        }
                        BigDecimal rateChange = BunkUtil.setAsPrice(priceChangeScanModel.getNewPrice().subtract(productClosingBalance.getUnitSellingPrice()));
                        if (rateChange.compareTo( CustomDecimal.ZERO ) >= 0 ) {
                            priceChangeInfo.append("<br/>").append("Prod:").append(productClosingBalance.getProductName()).append(" Old price:").append(productClosingBalance.getUnitSellingPrice()).append(" New price:").append(priceChangeScanModel.getNewPrice());

                            BigDecimal totalCashDiff = BunkUtil.setAsPrice(rateChange).multiply(productClosingBalance.getClosingStock());

                            UiRateChange uiRateChange = new UiRateChange();
                            uiRateChange.setProductId(productClosingBalance.getProductId());
                            uiRateChange.setOldPrice(productClosingBalance.getUnitSellingPrice());
                            uiRateChange.setCurrentStock(productClosingBalance.getClosingStock());
                            uiRateChange.setNewUnitPrice(priceChangeScanModel.getNewPrice());
                            uiRateChange.setTotalCashDiff(totalCashDiff);
                            uiRateChange.setMargin(BunkUtil.setAsPrice(productClosingBalance.getMargin().add(rateChange)));
                            list.add(uiRateChange);
                        }
                    }
                    if (list.size()>0) {
                        saveRateChangeinternal(list);
                        responseStringBuilder.append(priceChangeInfo);
                    }

                }
                List<ScannedDetail> fuelProdReceipts = bunkManager.getScannedData(nextday, nextday, ScanType.PROD_RECEIPT.getType() + StockReceipt.FUEL_RECEIPT_TYPE);
                if (fuelProdReceipts != null && fuelProdReceipts.size() > 0) {

                    responseStringBuilder.append("<br/> Auto fuel receipt added::");
                    List<ProductClosingBalance> records =this.bunkManager.getAvailableProductList("PRIMARY_PRODUCTS");
                    Map<Integer, ProductClosingBalance> prodsMap = new HashMap<>();
                    for (ProductClosingBalance productClosingBalance : records) {
                        prodsMap.put(productClosingBalance.getProductId(), productClosingBalance);
                    }

                    List<TankClosingStock> tankList =this.bunkManager.getTankList(nextday);
                    Map<Integer, Integer> prodIdToTankIdMap = new HashMap<>();
                    for (TankClosingStock tankClosingStock : tankList) {
                        prodIdToTankIdMap.put(tankClosingStock.getProductId(), tankClosingStock.getTankId());
                    }

                    //Group by invoice
                    Map<String, UiStockReceipt> receipts = new TreeMap<>(); //Using tree map to order the receipts by the invoice number

                    for (ScannedDetail scannedDetail : fuelProdReceipts) {
                        ProdReceiptScanModel prodReceiptScanModel = JSON_SERIALIZER.readValue(scannedDetail.getContents(), ProdReceiptScanModel.class);
                        UiStockReceipt uiStockReceipt = receipts.get(prodReceiptScanModel.getInvoiceNum());
                        if (uiStockReceipt == null) {
                            uiStockReceipt = new UiStockReceipt();
                            uiStockReceipt.setTankReceipts(new ArrayList<>());
                            uiStockReceipt.setFuelReceipts(new ArrayList<>());
                            receipts.put(prodReceiptScanModel.getInvoiceNum(), uiStockReceipt);
                            uiStockReceipt.setInvoiceNo(prodReceiptScanModel.getInvoiceNum());
                            uiStockReceipt.setType(StockReceipt.FUEL_RECEIPT_TYPE);
                            uiStockReceipt.setInvoiceAmt(CustomDecimal.ZERO);
                            uiStockReceipt.setTotalLoad(CustomDecimal.ZERO);

                        }
                        Integer prodId = null;
                        if ( "HSD".equalsIgnoreCase(prodReceiptScanModel.getProductNameText())) {
                            prodId = 2;
                        } else if ( "MS".equalsIgnoreCase(prodReceiptScanModel.getProductNameText())) {
                            prodId = 1;
                        } else if ( "POWER".equalsIgnoreCase(prodReceiptScanModel.getProductNameText())) {
                            prodId = 3;
                        }

                        if (prodId == null || !prodIdToTankIdMap.containsKey(prodId)) {
                            throw new IllegalStateException("Unknown product : " + prodReceiptScanModel.getProductNameText());
                        }


                        UiTankReceipt uiTankReceipt = new UiTankReceipt();
                        uiTankReceipt.setReceiptAmt(prodReceiptScanModel.getQuantity());

                        uiTankReceipt.setTankId(prodIdToTankIdMap.get(prodId));
                        uiStockReceipt.getTankReceipts().add(uiTankReceipt);


                        UiFuelReceipt uiFuelReceipt = new UiFuelReceipt();
                        uiFuelReceipt.setCostOnInv(prodReceiptScanModel.getTotalPrice());
                        uiFuelReceipt.setUnitSellingPrice(prodsMap.get(prodId).getUnitSellingPrice());
                        uiFuelReceipt.setReceiptAmt(prodReceiptScanModel.getQuantity());

                        //Compute Margin
                        BigDecimal margin = BunkUtil.setAsPrice(prodsMap.get(prodId).getUnitSellingPrice().subtract(prodReceiptScanModel.getTotalPrice().divide(prodReceiptScanModel.getQuantity())));
                        uiFuelReceipt.setMargin(margin);
                        uiFuelReceipt.setProductId(prodId);
                        uiFuelReceipt.setProductName(prodsMap.get(prodId).getProductName());
                        uiStockReceipt.getFuelReceipts().add(uiFuelReceipt);

                        uiStockReceipt.setTotalLoad(uiStockReceipt.getTotalLoad().add(prodReceiptScanModel.getQuantity()));
                        uiStockReceipt.setInvoiceAmt(uiStockReceipt.getInvoiceAmt().add(prodReceiptScanModel.getTotalPrice()));
                    }

                    for (Map.Entry<String, UiStockReceipt> entry : receipts.entrySet()) {
                        saveStockReceipt(entry.getValue());
                        responseStringBuilder.append("<br/> InvNum:").append(entry.getKey());

                        for ( UiFuelReceipt uiFuelReceipt : entry.getValue().getFuelReceipts()) {
                            responseStringBuilder.append("<br/>&emsp; ProdName:").append(prodsMap.get(uiFuelReceipt.getProductId()).getProductName()).append(" Quantity:") .append(uiFuelReceipt.getReceiptAmt());
                        }
                    }
                }
                EmailService.notify("Auto updates:" + DateUtil.getDateStringWithDay(DateUtil.getCalendarEquivalent(nextday)), "Applied changes:" + responseStringBuilder.toString());
            } catch (Exception e) {
                //Consider these as warnings.
                EmailService.notifyError(e, "Failed to auto apply rate changes. Applied changes:" + responseStringBuilder.toString(), "Auto updates:" + DateUtil.getDateStringWithDay(DateUtil.getCalendarEquivalent(nextday)));
                LOG.error("Failed to apply automatic updates." + DateUtil.getDateStringWithDay(DateUtil.getCalendarEquivalent(nextday)) + " Applied changes:" + responseStringBuilder.toString(), e);
            }

            return responseStringBuilder.toString();
        } else {
            //TODO:: Validate if the statement was submitted. Avoid updated
            try {
                final SavedDailyStatement savedDailyStatement = new SavedDailyStatement();
                savedDailyStatement.setDate(uiDailyStatement.getDate());
                savedDailyStatement.setContents(JSON_SERIALIZER.writeValueAsBytes(uiDailyStatement));
                this.bunkManager.saveSavedDailyStatement(savedDailyStatement);
            } catch (Exception e) {
                LOG.error("Failed to save stmt", e);
            }
            return "";
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
    @Path("partyTransactions")
    public List<PartyTransaction> getPartyTransactionHistory( final @QueryParam("id") int partyId, @Context UriInfo uriInfo, @QueryParam("typeFilter") String transTypeFilter, @QueryParam("detailFilter") final String detailFilter) throws BunkMgmtException
    {
        return this.bunkManager.getPartyTransactionHistory(partyId, getDate(uriInfo, "date"), getDate(uriInfo, "toDate"), transTypeFilter, detailFilter);
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

    @POST
    @Path("saveRateChange")
    public void saveRateChange( final List<UiRateChange> list ) throws BunkMgmtException {
        try {
            String data = JSON_SERIALIZER.writeValueAsString(list);
            LOG.info("Manual rate change::" + data);
            EmailService.notify("Manual rate change",data);
        } catch (Exception e) {
            LOG.error(e);
        }
        saveRateChangeinternal(list);
    }

    private void saveRateChangeinternal(final List<UiRateChange> list)  throws BunkMgmtException{
        final int todayInteger = bunkManager.getTodayDate();
        for (UiRateChange rateChange : list) {
            final ProdRateChange prodRateChange = new ProdRateChange();
            prodRateChange.setComments("TOTAL CASH DIFF IS:" + rateChange.getTotalCashDiff().toPlainString()); //Do not change this message text. Its used in preparing reports
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
    @Path("result/{queryName}")
    public QueryResults getFuelsSalesSummary(final @PathParam("queryName") String queryName,
        @Context UriInfo uriInfo)
        throws BunkMgmtException {
        final MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder();
        for(String key : queryParams.keySet()) {
            builder.put(key.toUpperCase(),queryParams.getFirst(key));
        }
        return this.bunkManager.getResult(queryName, builder.build());
    }

    @GET
    @Path("report")
    public Response getReport(@Context UriInfo uriInfo, @QueryParam("type") String type, @DefaultValue("pdf") @QueryParam("format") final String format)
        throws BunkMgmtException {
        final Report report;
        if ( "DAILY_STMT".equals(type) ) {
            report = this.reportsCreator.createClosingStatement( getDate(uriInfo, "date") );
        } else if ( "STOCK_STATUS".equals(type) ) {
            report = this.reportsCreator.createStockStatusReport(getDate(uriInfo, "date") );
        } else if ( "CREDIT_STATUS".equals(type) ) {
            report = this.reportsCreator.createCreditStatusReport(getDate(uriInfo, "date") );
        } else if ( "CASH_SUMMARY".equals(type) ) {
            report = this.reportsCreator.createCashSummaryStatement(getDate(uriInfo, "date") );
        } else if ( "MONTHLY_SUMMARY".equals(type) ) {
            report = this.reportsCreator.createMonthlyCashSummaryStatement(
                getDate(uriInfo, "date"), getDate(uriInfo, "toDate"));
        } else if ( "MONTHLY_SAL_STMT".equals(type) ) {
            report = this.reportsCreator.createMonthlySalarySummaryStatement(
                getDate(uriInfo, "date"), getDate(uriInfo, "toDate"));
        } else if ( "PARTY_COMPLETE_STMT".equals(type) ) {
            report = this.reportsCreator.createPartyStatement(
                getField(uriInfo, "id"), getDate(uriInfo, "date"), getDate(uriInfo, "toDate"));
        } else if ( "PARTY_CREDIT_STMT".equals(type) ) {
            report = this.reportsCreator.createCreditAlonePartyStatement(
                getField(uriInfo, "id"), getDate(uriInfo, "date"), getDate(uriInfo, "toDate"));
        } else if ( "STOCK_RECEIPTS".equals(type) ) {
            report = this.reportsCreator.createReceiptSummaryReport(getDate(uriInfo, "date"), getDate(uriInfo, "toDate"));
        } else if ( "PROD_SALES".equals(type) ) {
            report = this.reportsCreator.createProdSalesStatement(
                getField(uriInfo, "id"), getDate(uriInfo, "date"), getDate(uriInfo, "toDate"));
        } else if ( "DSR_STMT".equals(type) ) {
            report = this.reportsCreator.createDSR(
                getField(uriInfo, "id"), getDate(uriInfo, "date"), getDate(uriInfo, "toDate"));
        } else if ( "TANK_SALES".equals(type) ) {
            report = this.reportsCreator.createTankSalesStatement(
                getField(uriInfo, "id"), getDate(uriInfo, "date"), getDate(uriInfo, "toDate"));
        } else if ( "PROD_SALES_STMT".equals(type) ) {
            report = this.reportsCreator.createProdSalesStatement( getDate(uriInfo, "date"), getDate(uriInfo, "toDate"));
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

    private int getDate(UriInfo uriInfo, String fieldName) {
        int date;
        if(StringUtils.isNotEmpty(uriInfo.getQueryParameters().getFirst(fieldName + "Text"))) {
            date = DateUtil.getDateFromSimpleDateString(uriInfo.getQueryParameters().getFirst(fieldName + "Text"));
        } else if (StringUtils.isNotEmpty(uriInfo.getQueryParameters().getFirst(fieldName))) {
            date = Integer.parseInt(uriInfo.getQueryParameters().getFirst(fieldName));
        } else {
            throw new IllegalArgumentException("Unable to prepare report without value for field=" + fieldName);
        }
        return date;
    }

    private int getField(UriInfo uriInfo, String fieldName) {
        int date = 0;
        if(StringUtils.isNotEmpty(uriInfo.getQueryParameters().getFirst(fieldName))) {
            date = Integer.parseInt(uriInfo.getQueryParameters().getFirst(fieldName));
        } else {
            throw new IllegalArgumentException("Unable to prepare report without value for field=" + fieldName);
        }
        return date;
    }


}
