package bronz.accounting.bunk.framework.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import bronz.accounting.bunk.framework.exceptions.BunkValidationException;
import bronz.accounting.bunk.model.QueryResults;
import bronz.accounting.bunk.model.SavedDailyStatement;
import bronz.accounting.bunk.model.ScrapedDetail;
import bronz.accounting.bunk.model.dao.SavedStatementDao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.FloatType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.exception.PartyDaoException;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.party.model.Settlement;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.exception.ProductDaoException;
import bronz.accounting.bunk.products.model.ProdRateChange;
import bronz.accounting.bunk.products.model.Product;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.products.model.ReceiptSummary;
import bronz.accounting.bunk.products.model.StockVariation;
import bronz.accounting.bunk.tankandmeter.dao.TankAndMeterDao;
import bronz.accounting.bunk.tankandmeter.exception.TankAndMeterDaoException;
import bronz.accounting.bunk.tankandmeter.model.MeterClosingReading;
import bronz.accounting.bunk.tankandmeter.model.MeterTransaction;
import bronz.accounting.bunk.tankandmeter.model.Tank;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.tankandmeter.model.TankStock;
import bronz.accounting.bunk.tankandmeter.model.TankTransaction;
import bronz.utilities.general.CommandExecuter;
import bronz.utilities.general.DateUtil;
import bronz.utilities.general.UtilException;
import bronz.utilities.hibernate.GenericHibernateDao;

import javax.sql.rowset.serial.SerialBlob;

@SuppressWarnings("unchecked")
public class BunkAppDaoHibernateImpl extends GenericHibernateDao
        implements PartyDao, ProductDao, TankAndMeterDao, TxnManager, DBUtil, SavedStatementDao
{
    private static final ObjectMapper JSON_SERIALIZER = new ObjectMapper();

    private static final Map<String, String> TABLE_TO_DATE_FIELD_MAP = new HashMap<String, String>();
    private static final Logger LOG = LogManager.getLogger( BunkAppDaoHibernateImpl.class );
    private static final SimpleDateFormat CURRENT_TIME_DATE_FORMAT =
    		new SimpleDateFormat("EEE_dd_MM_yyyy_HHmmssSSS");
    static
    {
        TABLE_TO_DATE_FIELD_MAP.put( "pbms_closing_readings", "date" );
        TABLE_TO_DATE_FIELD_MAP.put( "pbms_settlements", "date" );
        TABLE_TO_DATE_FIELD_MAP.put( "pbms_party_transactions", "date" );
        TABLE_TO_DATE_FIELD_MAP.put( "pbms_product_meter_change", "date" );
        TABLE_TO_DATE_FIELD_MAP.put( "pbms_product_transactions", "date" );
        TABLE_TO_DATE_FIELD_MAP.put( "pbms_stock_receipts", "date" );
        TABLE_TO_DATE_FIELD_MAP.put( "pbms_tank_transactions", "date" );
        TABLE_TO_DATE_FIELD_MAP.put( "pbms_tank_dip", "date" );
        
    }
    public BunkAppDaoHibernateImpl( final String configFile,
            final Map<String, String> propertiesToOverride )
    {
        super( configFile, propertiesToOverride );
    }
    
    public List<Party> getAllParties() throws PartyDaoException
    {
        try {
            return getSession().createCriteria( Party.class ).list();
        } finally {
            closeSession();
        }
    }
    
    public void saveParties(final List<Party> parties) throws PartyDaoException
    {
        for (Party party : parties)
        {
            getSession().saveOrUpdate( party );
        }
    }
    
    public List<ProdRateChange> getProdRateChanges( final int date ) throws ProductDaoException
    {
        return getByQueryWithLimit( ProdRateChange.class, "from ProdRateChange where date <=? order by date desc, prodId", 25, date );
    }
    
    public void saveProdRateChange( final ProdRateChange prodRateChange )
    {
        getSession().saveOrUpdate( prodRateChange );
    }

    public PartyClosingBalance getParty( final Integer id, final Integer date ) throws PartyDaoException
    {
        final SQLQuery query = getSavedSQLQuery("findPartyClosingByID");
        final List<PartyClosingBalance> list = query.addEntity( PartyClosingBalance.class ).setParameter(
                "ID", id ).setParameter( "TODAY", date ).list();
        final PartyClosingBalance party;
        if ( null != list && list.size() > 0 )
        {
            party = list.get( 0 );
        }
        else
        {
            party = null;
        }
        return party;
    }
    
    public List<EmployeeMonthlyStatus> getEmployeesStatus(final Integer date ) throws PartyDaoException
    {
        final SQLQuery query = getSavedSQLQuery("findEmployeesMonthlyStatus");
        return query.addEntity( EmployeeMonthlyStatus.class ).setParameter( "TODAY", date ).list();
    }
    
    public List<PartyClosingBalance> getPartyList( final String partyType, final Integer date )
            throws PartyDaoException
    {
    	if(ALL_ACTIVE_NON_EMPLOYEE_PARTIES.equals( partyType ))
        {
    		final SQLQuery query = getSavedSQLQuery("findActiveNonEmployeePartiesClosing");
            return query.addEntity( PartyClosingBalance.class ).setParameter( "TODAY", date ).list();
        }
    	else
    	{
	        final int idLowerLimit;
	        final int idHigherLimit;
	        String status = "ACTIVE%";
	        if( EMPLOYEES.equals( partyType ) )
	        {
	            idLowerLimit = 1;
	            idHigherLimit = 250;
	        }
	        else if( BANKS.equals( partyType ) )
	        {
	            idLowerLimit = 250;
	                idHigherLimit = 300;
	        }
	        else if( PARTIES.equals( partyType ) )
	        {
	            idLowerLimit = 300;
	            idHigherLimit = 10000;
	        }
	        else
	        {
	            idLowerLimit = 1;
	            idHigherLimit = 10000;
	        }
	        
	        if (ALL_ACTIVE.equals( partyType ))
	        {
	            status = "ACTIVE%";
	        }
	        else if (PartyDao.ALL.equals( partyType ))
	        {
	            status = "%";
	        }
	        final SQLQuery query = getSavedSQLQuery("findPartiesClosing");
	        
	        return query.addEntity( PartyClosingBalance.class ).setParameter(
	        		"MAX", idHigherLimit ).setParameter(
	                "MIN", idLowerLimit ).setParameter(
	                "TODAY", date ).setParameter(
	                "STATUS", status ).list();
    	}
    }

    public List<PartyTransaction> getTransForPartyByDate( final int partyId, final int startDate, final int endDate,
            final String transTypeFilter, final String detailFilter ) throws PartyDaoException
    {
        return getByQuery( PartyTransaction.class, "from PartyTransaction " +
                "where date >= ? and date <= ? and partyId=? and transactionType like ?  and transactionDetail like ? " +
                "order by date, slNo", startDate, endDate, partyId, overrideNullFilter( transTypeFilter),
                overrideNullFilter(detailFilter ));
    }
    
    public List<PartyTransaction> getTransByDate( final int startDate, final int endDate,
            final String transTypeFilter, final String detailFilter ) throws PartyDaoException
    {
        return getByQuery(PartyTransaction.class, "from PartyTransaction " +
                                                  "where date >= ? and date <= ? and transactionType like ?  and transactionDetail like ? "
                                                  +
                                                  "order by partyId, date, slNo", startDate, endDate,
            overrideNullFilter(transTypeFilter),
            overrideNullFilter(detailFilter));
    }
    
    public List<PartyTransaction> getPendingChequeAtOffice(final int date) throws PartyDaoException
    {
        final SQLQuery query = getSavedSQLQuery("findPendingChequesAtOffice");
        query.addEntity( PartyTransaction.class ).setString(
        		"chqTransType", PartyTransaction.CHEQUE_DEBIT ).setInteger(
        		"date", date).setString(
        		"chqCreditPrefix", PartyTransaction.CHEQUE_CREDIT_TRANS_DETAIL_PREFIX ).setInteger(
        		"officeChequePartyId", AppConfig.OFFICE_CHEQUE_PARTY_ID.getValue( Integer.class ));
        return query.list();
    }
    
    private String overrideNullFilter( final String filter)
    {
        return (null == filter? "%" : filter);
    }
    
    public Settlement getSettlement(final int date) throws PartyDaoException
    {
        Settlement settlement = null;
        final List<Settlement> settlements = getByQuery( Settlement.class, "from Settlement " +
                "where date=?", date );
        if ( settlements != null && !settlements.isEmpty())
        {
            settlement = settlements.get( 0 );
        }
        return settlement;
    }
    
    public void saveSettlement(final Settlement settlement) throws PartyDaoException
    {
        getSession().save( settlement );
    }
    
    public List<Product> getAllProducts() throws ProductDaoException
    {
        return getSession().createCriteria( Product.class ).list();
    }
    
    public ProductClosingBalance getProduct( final int productId, final int date ) throws ProductDaoException
    {
        final SQLQuery query = getSession().createSQLQuery(
                "SELECT PR.PK_PRODUCT_ID AS PRODUCT_ID, TR.PK_SLNO AS SL_NO, " +
                "    PR.PRODUCT_NAME AS NAME, TX.TOTAL_PRICE AS UNIT_PRICE, " +
                "    TR.BALANCE AS CLOSING_STOCK, TR.DATE AS DATE, TX.DATE AS LAST_LOAD, TX.MARGIN AS MARGIN " +
                "FROM ( " +
                "    SELECT MAX(TQ.PK_SLNO) AS MAX_SLNO " +
                "    FROM PBMS_PRODUCT_TRANSACTIONS TQ " +
                "    WHERE TQ.DATE <=:TODAY AND TQ.PRODUCT_ID = :ID GROUP BY TQ.PRODUCT_ID) AS MAX_SL " +
                "INNER JOIN PBMS_PRODUCT_TRANSACTIONS AS TR " +
                "    ON MAX_SL.MAX_SLNO=TR.PK_SLNO " +
                "LEFT JOIN PBMS_PRODUCT_TABLE AS PR " +
                "    ON TR.PRODUCT_ID=PR.PK_PRODUCT_ID " +
                "LEFT JOIN ( " +
                "    SELECT MAX(TP.PK_SLNO) AS LOAD_PK_SLNO, TP.PRODUCT_ID " +
                "    FROM PBMS_PRODUCT_TRANSACTIONS TP " +
                "    WHERE TP.DATE <= :TODAY AND SELL_RECIEVE like 'R%' GROUP BY TP.PRODUCT_ID) AS LAST " +
                "ON LAST.PRODUCT_ID=PR.PK_PRODUCT_ID " +
                "INNER JOIN PBMS_PRODUCT_TRANSACTIONS AS TX " +
                "    ON LAST.LOAD_PK_SLNO=TX.PK_SLNO "
                ).addEntity( ProductClosingBalance.class );
        
        final List<ProductClosingBalance> productList =
            query.setInteger( "TODAY", date ).setInteger( "ID", productId).list();
        final ProductClosingBalance product;
        
        if ( productList == null || productList.size() == 0 )
        {
            product = null;
        }
        else
        {
            product = productList.get( 0 );
        }
        return product;
    }
    
    public List<ProductClosingBalance> getAvailableProductList( final String productType, final int date )
            throws ProductDaoException
    {
        final int idLowerLimit;
        final int idHigherLimit;
        if ( PRIMARY.equals( productType ) )
        {
            idLowerLimit = 0;
            idHigherLimit = 10;
        }
        else if ( SECONDARY.equals( productType ) )
        {   
            idLowerLimit = 5;
            idHigherLimit = 10000;
        }
        else if ( FUEL_PRODUCTS.equals( productType ) )
        {
            idLowerLimit = 0;
            idHigherLimit = 5;
        }
        else if ( OIL_PRODUCTS.equals( productType ) )
        {
            idLowerLimit = MIN_OIL_PROD_ID;
            idHigherLimit = MIN_ADDITIONAL_PROD_ID;
        }
        else if ( LUBE_PRODUCTS.equals( productType ) )
        {
            idLowerLimit = 5;
            idHigherLimit = MIN_ADDITIONAL_PROD_ID;
        }
        else if ( ADDITIONAL_PRODUCTS.equals( productType ) )
        {
            idLowerLimit = MIN_ADDITIONAL_PROD_ID;
            idHigherLimit = 10000;
        }
        else
        {
            idLowerLimit = 1;
            idHigherLimit = 10000;
        }


        final SQLQuery query = getSavedSQLQuery("availableProductList").addEntity( ProductClosingBalance.class);

        return query.setInteger( "TODAY", date ).setInteger( "MIN", idLowerLimit).setInteger(
                "MAX", idHigherLimit  ).list();
        
    }
    
    public List<ProductClosingBalance> getProductList( final String productType, final int date )
        throws ProductDaoException
    {
        final int idLowerLimit;
        final int idHigherLimit;
        if ( PRIMARY.equals( productType ) )
        {
            idLowerLimit = 0;
            idHigherLimit = 10;
        }
        else if ( SECONDARY.equals( productType ) )
        {   
            idLowerLimit = 5;
            idHigherLimit = 10000;
        }
        else if ( FUEL_PRODUCTS.equals( productType ) )
        {
            idLowerLimit = 0;
            idHigherLimit = 5;
        }
        else if ( OIL_PRODUCTS.equals( productType ) )
        {
            idLowerLimit = MIN_OIL_PROD_ID;
            idHigherLimit = MIN_ADDITIONAL_PROD_ID;
        }
        else if ( LUBE_PRODUCTS.equals( productType ) )
        {
            idLowerLimit = 5;
            idHigherLimit = MIN_ADDITIONAL_PROD_ID;
        }
        else if ( ADDITIONAL_PRODUCTS.equals( productType ) )
        {
            idLowerLimit = MIN_ADDITIONAL_PROD_ID;
            idHigherLimit = 10000;
        }
        else
        {
            idLowerLimit = 1;
            idHigherLimit = 10000;
        }
        final SQLQuery query = getSession().createSQLQuery(
                "SELECT PR.PK_PRODUCT_ID AS PRODUCT_ID, TR.PK_SLNO AS SL_NO, " +
                "    PR.PRODUCT_NAME AS NAME, TX.TOTAL_PRICE AS UNIT_PRICE, " +
                "    TR.BALANCE AS CLOSING_STOCK, TR.DATE AS DATE, TX.DATE AS LAST_LOAD, TX.MARGIN AS MARGIN " +
                "FROM ( " +
                "    SELECT MAX(TQ.PK_SLNO) AS MAX_SLNO " +
                "    FROM PBMS_PRODUCT_TRANSACTIONS TQ " +
                "    WHERE TQ.DATE <=:TODAY AND TQ.PRODUCT_ID >= :MIN AND TQ.PRODUCT_ID < :MAX GROUP BY TQ.PRODUCT_ID) AS MAX_SL " +
                "INNER JOIN PBMS_PRODUCT_TRANSACTIONS AS TR " +
                "    ON MAX_SL.MAX_SLNO=TR.PK_SLNO " +
                "LEFT JOIN PBMS_PRODUCT_TABLE AS PR " +
                "    ON TR.PRODUCT_ID=PR.PK_PRODUCT_ID " +
                "LEFT JOIN ( " +
                "    SELECT MAX(TP.PK_SLNO) AS LOAD_PK_SLNO, TP.PRODUCT_ID " +
                "    FROM PBMS_PRODUCT_TRANSACTIONS TP " +
                "    WHERE TP.DATE <= :TODAY AND SELL_RECIEVE like 'R%' GROUP BY TP.PRODUCT_ID) AS LAST " +
                "ON LAST.PRODUCT_ID=PR.PK_PRODUCT_ID " +
                "INNER JOIN PBMS_PRODUCT_TRANSACTIONS AS TX " +
                "    ON LAST.LOAD_PK_SLNO=TX.PK_SLNO " +
                "ORDER BY PR.PRODUCT_NAME"
                ).addEntity( ProductClosingBalance.class );
        
        return query.setInteger( "TODAY", date ).setInteger( "MIN", idLowerLimit).setInteger(
                "MAX", idHigherLimit  ).list();
    }
    
    
    
    public List<ReceiptSummary> getReceiptSummaries( final int date ) throws ProductDaoException
    {
        return getByQuery(ReceiptSummary.class, "from ReceiptSummary" +
                                                " where date = ? order by invoiceNumber", date);
    }
    
    public ReceiptSummary getReceiptSummary( final String invoiceNumber )
    		throws ProductDaoException
    {
    	final List<ReceiptSummary> receipts = getByQuery( ReceiptSummary.class,
    			"from ReceiptSummary where invoiceNumber = ?", invoiceNumber );
    	if (receipts == null || receipts.isEmpty())
    	{
    		return null;
    	}
    	else
    	{
    		return receipts.get(0);
    	}
    }
    
    public List<ReceiptSummary> getReceiptSummaries( final int startDate, final int endDate )
            throws ProductDaoException
    {
        return getByQuery( ReceiptSummary.class, "from ReceiptSummary" +
                " where date >= ? and date <= ? order by invoiceNumber", startDate, endDate );
    }
    
    public void savePartyTransactions( final List<PartyTransaction> transactions ) throws PartyDaoException
    {
        final Session session = getSession();
        for ( final PartyTransaction partyTransaction : transactions )
        {
            session.saveOrUpdate( partyTransaction );
        }
    }

    public void specialUpdatePartyTrans(PartyTransaction newPartyTransaction, Integer prevSlNo, BigDecimal amtDiff) throws PartyDaoException {
        final Session session = getSession();
        Integer startSlnoForBalanceUpdate = null;
        if ( newPartyTransaction.getSlNo() == null) {
            //Add new transaction at the end
            Integer newSlno = (Integer) session.save( newPartyTransaction );
            session.flush();

            if (prevSlNo != null) {
                //Push all transactions to make space for the new transaction.
                final Query updateSlnoQuery = session.createSQLQuery( "update pbms_party_transactions set PK_SLNO=PK_SLNO+1 where PK_SLNO>? ORDER BY PK_SLNO desc");
                fillParameters(updateSlnoQuery, prevSlNo);
                updateSlnoQuery.executeUpdate();
                session.flush();

                //Update the sl no. Note that the above query updated the SLNO of the new row by one.
                final Query reassignSlnoQuery = session.createQuery( "UPDATE PartyTransaction" +
                        " set slNo=? where slNo = ?");
                fillParameters(reassignSlnoQuery, prevSlNo + 1, newSlno +1);
                reassignSlnoQuery.executeUpdate();
                session.flush();

                String updateOperation = PartyTransaction.CREDIT_TRANS_TYPES.contains(newPartyTransaction.getTransactionType()) ? "+" : "-";

                startSlnoForBalanceUpdate = prevSlNo + 1;
            }
        } else {
            //Update the transaction.
            session.update( newPartyTransaction );
            session.flush();
            startSlnoForBalanceUpdate = newPartyTransaction.getSlNo();
        }

        if (startSlnoForBalanceUpdate != null) {
            //Update the balances for the rest of the transactions of this party.
            final Query queryObject = session.createQuery( "UPDATE PartyTransaction" +
                    " set balance=balance+? where slNo > ? and partyId = ?");
            fillParameters(queryObject, amtDiff, startSlnoForBalanceUpdate, newPartyTransaction.getPartyId());
            queryObject.executeUpdate();
            session.flush();
        }
    }

    public void saveProductTransactions( final List<ProductTransaction> transactions ) throws ProductDaoException
    {
        final Session session = getSession();
        for ( final ProductTransaction productTransaction : transactions )
        {
            session.saveOrUpdate( productTransaction );
        }
    }
    
    public void saveProducts( final List<Product> products ) throws ProductDaoException
    {
        final Session session = getSession();
        for ( final Product product : products )
        {
            session.saveOrUpdate( product );
        }
    }
    
    public void saveMeterTransactions( final List<MeterTransaction> meterTransactions ) throws TankAndMeterDaoException
    {
        final Session session = getSession();
        for ( final MeterTransaction meterTransaction : meterTransactions )
        {
            session.saveOrUpdate( meterTransaction );
        }
    }

    public void saveStockReceipt( final ReceiptSummary receiptSummary ) throws ProductDaoException
    {
        getSession().saveOrUpdate( receiptSummary );
    }

    public List<ProductTransaction> getAllProdTransByDate( final int startDate,
            final int endDate )
    {
        return getByQuery( ProductTransaction.class, "from ProductTransaction" +
                " where date >= ? and date <= ? order by productId, date, slNo", startDate,
                endDate );
    }

    public List<ProductTransaction> getAllProdTransByDateAndType(
            final int startDate, final int endDate,
            final String transactionType )
    {
        return getByQuery( ProductTransaction.class, "from ProductTransaction" +
                " where date >= ? and date <= ? and transactionType = ? " +
                "order by productId, date, slNo", startDate, endDate, transactionType );
    }

    public List<ProductTransaction> getAllProductTransByDetail(
            final String transactionType )
    {
        return getByQuery(ProductTransaction.class, "from ProductTransaction" +
                                                    " where detail like ? order by productId, date, slNo",
            transactionType + '%');
    }
    
    public List<ProductClosingBalance> getProdClosingByDate( final int date )
    {
        final SQLQuery query = getSession().createSQLQuery(
                "SELECT PR.PK_PRODUCT_ID AS PRODUCT_ID, TR.PK_SLNO AS SL_NO, " +
                "    PR.PRODUCT_NAME AS NAME, TX.TOTAL_PRICE AS UNIT_PRICE, " +
                "    TR.BALANCE AS CLOSING_STOCK, TR.DATE AS DATE, TX.DATE AS LAST_LOAD, TX.MARGIN AS MARGIN " +
                "FROM ( " +
                "    SELECT MAX(TQ.PK_SLNO) AS MAX_SLNO " +
                "    FROM PBMS_PRODUCT_TRANSACTIONS TQ " +
                "    WHERE TQ.DATE <=:TODAY GROUP BY TQ.PRODUCT_ID) AS MAX_SL " +
                "INNER JOIN PBMS_PRODUCT_TRANSACTIONS AS TR " +
                "    ON MAX_SL.MAX_SLNO=TR.PK_SLNO " +
                "LEFT JOIN PBMS_PRODUCT_TABLE AS PR " +
                "    ON TR.PRODUCT_ID=PR.PK_PRODUCT_ID " +
                "LEFT JOIN ( " +
                "    SELECT MAX(TP.PK_SLNO) AS LOAD_PK_SLNO, TP.PRODUCT_ID " +
                "    FROM PBMS_PRODUCT_TRANSACTIONS TP " +
                "    WHERE TP.DATE <= :TODAY AND SELL_RECIEVE like 'R%' GROUP BY TP.PRODUCT_ID) AS LAST " +
                "ON LAST.PRODUCT_ID=PR.PK_PRODUCT_ID " +
                "INNER JOIN PBMS_PRODUCT_TRANSACTIONS AS TX " +
                "    ON LAST.LOAD_PK_SLNO=TX.PK_SLNO " +
                "ORDER BY PR.PRODUCT_NAME"
                ).addEntity( ProductClosingBalance.class );
        
        return query.setInteger("TODAY", date).list();
    }
    
    public List<Tank> getAllTanks() throws TankAndMeterDaoException
    {
        return getSession().createCriteria(Tank.class).list();
    }

    public List<TankClosingStock> getTankClosingByDate( int date )
            throws TankAndMeterDaoException
    {
		final Query query = getSavedSQLQuery("findTankClosingStock").addEntity(
						TankClosingStock.class).setInteger("TODAY", date);
		return query.list();
    }
    
    public void saveTankStock( List<TankStock> tankStockList )
            throws TankAndMeterDaoException
    {
        final Session session = getSession();
        for (TankStock stock : tankStockList )
        {
            session.save( stock );
        }
    }
    
    public List<TankTransaction> getAllTankTransByDetail( final String type )
            throws TankAndMeterDaoException
    {
        return getByQuery( TankTransaction.class, "from TankTransaction" +
                " where detail like ? order by tankId, date, slNo", type +'%' ); 
    }
    
    public void saveTankTransactions( final List<TankTransaction> tankTransactions )
        throws TankAndMeterDaoException
    {
        final Session session = getSession();
        for ( final TankTransaction transaction : tankTransactions )
        {
            session.save(transaction);
        }
    }

    public void clearTrans( final int date ) throws BunkMgmtException
    {
    	LOG.info("Deleting all records on and after " + date);
        for ( Map.Entry<String, String> entry : TABLE_TO_DATE_FIELD_MAP.entrySet() )
        {
            executeSQL("DELETE FROM " + entry.getKey() + " WHERE " + entry.getValue() + " >= ?", date);
        }
    }
    
    public void executeDbDump(final String backupType) throws BunkMgmtException
    {
    	try
    	{
    		final String fileName = String.format("%1$s%2$s_%3$s_%4$s.sql",
 	     		 AppConfig.AUTO_DB_BACKUP_FOLDER.getStringValue(),
 	     		 AppConfig.DB_SCHEMA_NAME_PROP_NAME.getStringValue(),
 	     		 backupType, CURRENT_TIME_DATE_FORMAT.format(new Date()) );
    		
    		LOG.info("DB Backup file name:" + fileName);
    		
	        final String command = String.format("\"%1$sbin\\mysqldump\" \"-u%2$s\" \"-p%3$s\" \"-r%5$s\" \"%4$s\"",
	     		   AppConfig.MYSQL_INSTALLATION_HOME_DIR.getStringValue(),
	     		   AppConfig.DB_USERNAME_PROP_NAME.getStringValue(),
	     		   AppConfig.DB_PASSWORD_PROP_NAME.getStringValue(),
	     		   AppConfig.DB_SCHEMA_NAME_PROP_NAME.getStringValue(),
	     		   fileName);
	        
	        CommandExecuter.executeWinCmd( "DB_BACKUP",command, false);
	        
    	}
    	catch (UtilException exception)
    	{
    		throw new BunkMgmtException("Failed to take DB backup", exception);
    	}
    }
    
    public List<MeterClosingReading> getAllMeterClosingByDate( final int date ) throws TankAndMeterDaoException
    {
        final SQLQuery query = getSession().createSQLQuery(
                "SELECT RE.PK_SLNO AS PK_SLNO, RE.PK_METER_ID AS METER_ID, ME.TANK_ID AS TANK_ID, " +
                "TA.PRODUCT_ID AS PROD_ID, ME.METER_NAME AS METER_NAME, " +
                "RE.FINAL_READING AS FINAL_READING, RE.DATE AS DATE FROM " +
                "(SELECT MAX(PK_SLNO) AS SLNO FROM PBMS_CLOSING_READINGS WHERE DATE <= :TODAY GROUP BY PK_METER_ID) AS MAX_SL " +
                "INNER JOIN PBMS_CLOSING_READINGS RE ON RE.PK_SLNO = MAX_SL.SLNO " +
                "LEFT JOIN PBMS_METER_TABLE ME ON RE.PK_METER_ID = ME.PK_METER_ID " +
                "LEFT JOIN PBMS_TANK_TABLE TA ON ME.TANK_ID = TA.PK_TANK_ID " +
                "ORDER BY ME.METER_NAME"
                ).addEntity( MeterClosingReading.class );
        
        return query.setInteger( "TODAY", date ).list();
    }
    
    public List<MeterClosingReading> getActiveMeterClosings() throws TankAndMeterDaoException
    {
        final SQLQuery query = getSession().createSQLQuery(
                "SELECT RE.PK_SLNO AS PK_SLNO, RE.PK_METER_ID AS METER_ID, ME.TANK_ID AS TANK_ID, " +
                "TA.PRODUCT_ID AS PROD_ID, ME.METER_NAME AS METER_NAME, " +
                "RE.FINAL_READING AS FINAL_READING, RE.DATE AS DATE FROM " +
                "(SELECT MAX(PK_SLNO) AS SLNO FROM PBMS_CLOSING_READINGS GROUP BY PK_METER_ID) AS MAX_SL " +
                "INNER JOIN PBMS_CLOSING_READINGS RE ON RE.PK_SLNO = MAX_SL.SLNO " +
                "INNER JOIN PBMS_METER_TABLE ME ON RE.PK_METER_ID = ME.PK_METER_ID AND METER_STATUS LIKE 'ACTIVE%'" +
                "LEFT JOIN PBMS_TANK_TABLE TA ON ME.TANK_ID = TA.PK_TANK_ID " +
                "ORDER BY ME.METER_NAME"
                ).addEntity( MeterClosingReading.class );
        
        return query.list();
    }
    
    
	public int getNextDate() throws BunkMgmtException
	{
		int date = 0;
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try
		{
			connection = getSession().connection();
			statement = connection.createStatement();
			final String query = "SELECT ((MAX(DATE))+1) AS NEXT FROM" +
							" pbms_settlements";
			resultSet = statement.executeQuery( query );
			while ( resultSet.next() )
			{
				date = resultSet.getInt( "NEXT" );
			}
		}
		catch ( SQLException sqlException)
		{
			throw new BunkMgmtException( "Failed to get next date :" +
					sqlException.toString(), sqlException );
		}
		finally
		{
			closeStatement(statement);
			closeResultSet(resultSet);
		}
		return date;
	}
    
    public int getFirstDay() throws BunkMgmtException
    {
        int date = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = getSession().connection();
            statement = connection.createStatement();
            final String query = "SELECT (MIN(DATE)) AS NEXT FROM" +
                            " pbms_settlements";
            resultSet = statement.executeQuery( query );
            while ( resultSet.next() )
            {
                date = resultSet.getInt( "NEXT" );
            }
        }
        catch ( SQLException sqlException)
        {
            throw new BunkMgmtException( "Failed to get next date :" +
                    sqlException.toString(), sqlException );
        }
        finally
        {
            closeStatement(statement );
            closeResultSet(resultSet);
        }
        return date;
    }

	public List<StockVariation> getStockVariation(final int date)
			throws ProductDaoException
	{
		try
		{
			final int firstDayOfMonth = DateUtil.getFirstDateOfGivenMonth(date);
			final Query query = getSavedSQLQuery("findStockVariation").addEntity(
					StockVariation.class).setInteger("monthLast", date).setInteger(
							"monthStart", firstDayOfMonth).setInteger("prevMonthStart",
									DateUtil.getFirstDateOfGivenMonth(firstDayOfMonth - 1));
			return query.list();
		}
		finally
        {
			closeSession();
        }
	}

    public SavedDailyStatement getSavedDailyStatement(int date) throws BunkMgmtException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try{
            connection = getSession().connection();
            ps = connection.prepareStatement("SELECT content FROM PBMS_SAVED_STATEMENT where date=? AND type=?");
            int n = 1;
            ps.setInt(n++, date);
            ps.setString(n++, SavedStatementDao.DAILY_STATEMENT_TYPE);
            resultSet = ps.executeQuery();
            if(resultSet.next()){
                SerialBlob serialBlob = new SerialBlob(resultSet.getBlob("content"));
                final SavedDailyStatement savedDailyStatement = new SavedDailyStatement();
                savedDailyStatement.setContents(serialBlob.getBytes(1, (int) serialBlob.length()));
                return savedDailyStatement;
            }
        }
        catch ( Exception exception)
        {
            throw new BunkMgmtException( "Failed to read saved daily statement", exception );
        }
        finally {
            closeStatement(ps);
            closeResultSet(resultSet);
        }
        return null;
    }

    public void deleteSavedDailyStatement(int date) throws BunkMgmtException {
        Connection connection = null;
        PreparedStatement ps = null;
        try{
            connection = getSession().connection();
            ps = connection.prepareStatement("DELETE FROM PBMS_SAVED_STATEMENT WHERE date=?");
            int n = 1;
            ps.setInt(n++, date);
            ps.executeUpdate();
        }
        catch ( Exception exception)
        {
            throw new BunkMgmtException( "Failed to delete saved daily statement", exception );
        }
        finally {
            closeStatement(ps);
        }
    }

    public void saveOrUpdateSavedDailyStatement(SavedDailyStatement savedDailyStatement) throws BunkMgmtException {
        Connection connection = null;
        PreparedStatement ps = null;
        try{
            connection = getSession().connection();
            ps = connection.prepareStatement("INSERT INTO PBMS_SAVED_STATEMENT (date, content, type, lastSavedDate, " +
                    "createdDate) VALUES (?,?,?,NOW(),NOW()) ON DUPLICATE KEY UPDATE content=?, lastSavedDate=NOW()");
            SerialBlob serialBlob = new SerialBlob(savedDailyStatement.getContents());
            int n = 1;
            //INSERT
            ps.setInt(n++, savedDailyStatement.getDate());
            ps.setBlob(n++, serialBlob);
            ps.setString(n++, SavedStatementDao.DAILY_STATEMENT_TYPE);

            //UPDATE
            ps.setBlob(n++, serialBlob);
            ps.executeUpdate();
        }
        catch ( Exception exception)
        {
            throw new BunkMgmtException( "Failed to save saved daily statement", exception );
        }
        finally {
            closeStatement(ps);
        }
    }

    public List<ScrapedDetail> getScrapedDetails(int date) throws BunkMgmtException {

        List<ScrapedDetail> results = new ArrayList<ScrapedDetail>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try{
            connection = getSession().connection();
            ps = connection.prepareStatement("SELECT * FROM PBMS_SCRAPED_DETAILS where date=?");
            int n = 1;
            ps.setInt(n++, date);
            resultSet = ps.executeQuery();
            if(resultSet.next()){
                SerialBlob serialBlob = new SerialBlob(resultSet.getBlob("content"));
                final ScrapedDetail scrapedDetail = new ScrapedDetail();
                scrapedDetail.setContents(serialBlob.getBytes(1, (int) serialBlob.length()));
                scrapedDetail.setComments(resultSet.getString("comments"));
                scrapedDetail.setType(resultSet.getString("type"));
                scrapedDetail.setPkSlNo(resultSet.getInt("pkSlNo"));
                scrapedDetail.setDate(resultSet.getInt("date"));
                results.add(scrapedDetail);
            }
        }
        catch ( Exception exception)
        {
            throw new BunkMgmtException( "Failed to read scraped details", exception );
        }
        finally {
            closeStatement(ps);
            closeResultSet(resultSet);
        }
        return results;
    }


    public void saveScrapedDetail(ScrapedDetail scrapedDetail) throws BunkMgmtException {
        Connection connection = null;
        PreparedStatement ps = null;
        try{
            connection = getSession().connection();
            ps = connection.prepareStatement("INSERT INTO PBMS_SCRAPED_DETAILS (pkSlNo, date, content, comments, type, " +
                    "createdDate) VALUES (NULL,?,?,?,?,NOW())");
            SerialBlob serialBlob = new SerialBlob(scrapedDetail.getContents());
            int n = 1;
            //INSERT
            ps.setInt(n++, scrapedDetail.getDate());
            ps.setBlob(n++, serialBlob);
            ps.setString(n++, scrapedDetail.getComments());
            ps.setString(n++, scrapedDetail.getType());
            ps.executeUpdate();
        }
        catch ( Exception exception)
        {
            throw new BunkMgmtException( "Failed to save scrapped info", exception );
        }
        finally {
            closeStatement(ps);
        }
    }

    public QueryResults getResult(final String savedQueryName, final Map<String, Object> params) throws BunkMgmtException{
        final SavedQuery savedQuery = SavedQuery.findQuery(savedQueryName);
        if(savedQuery == null){
            throw new BunkValidationException("Unknown query:" + savedQueryName);
        }
        final QueryResults queryResults = new QueryResults();
        final SQLQuery query = getSavedSQLQuery(savedQuery.getSavedQueryName());
        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        for(Map.Entry<String, ? extends Type> type: savedQuery.getTypeMap().entrySet()) {
            query.addScalar(type.getKey(), type.getValue());
        }
        for(String param: query.getNamedParameters()) {
            query.setParameter(param, params.get(param));
        }

        final List rawResults = query.list();
        final Map<String, String> pivotFields = savedQuery.getPivotFields();
        if(pivotFields == null || pivotFields.isEmpty()) {
            for(Map.Entry<String, ? extends Type> type: savedQuery.getTypeMap().entrySet()) {
                queryResults.getFields().add(type.getKey());
            }
            queryResults.setResults(rawResults);
        } else {
            final Map<String, Map> pivotMap = new HashMap();
            queryResults.getGroupByFields().addAll(savedQuery.getGroupByFields());
            for (Map<String, Object> rawRecord : (List<Map<String, Object>>)rawResults) {
                //Prepare key
                StringBuilder keyBuilder = new StringBuilder();
                for(int i =0; i < savedQuery.getGroupByFields().size();i++) {
                    if(i>0){
                        keyBuilder.append("_"); //Separator
                    }
                    keyBuilder.append(rawRecord.get(savedQuery.getGroupByFields().get(i)));
                }

                String key = keyBuilder.toString();

                Map pivotRecord = pivotMap.get(key);
                if(pivotRecord == null) {
                   pivotRecord = new HashMap();
                    pivotMap.put(key, pivotRecord);
                }

                for (Map.Entry<String, Object> entry : rawRecord.entrySet()) {
                    if (entry.getValue() != null && !savedQuery.getIgnoreFields().contains(entry.getKey())) {
                        if(savedQuery.getPivotFields().containsKey(entry.getKey())) {
                            pivotRecord.put(entry.getValue(), rawRecord.get(savedQuery.getPivotFields().get(
                                entry.getKey())));
                            String fieldName = entry.getValue().toString();
                            if(!savedQuery.getGroupByFields().contains(fieldName)) {
                                queryResults.getFields().add(fieldName);
                            }

                        } else {
                            pivotRecord.put(entry.getKey(), entry.getValue());

                            String fieldName = entry.getKey();
                            if(!savedQuery.getGroupByFields().contains(fieldName)) {
                                queryResults.getFields().add(fieldName);
                            }
                        }

                    }
                }
            }
            Set<String> fields = queryResults.getFields();
            for (Map pivotRecord : pivotMap.values()) {
                for (String field: fields) {
                    if (!pivotRecord.containsKey(field)) {
                        pivotRecord.put(field, 0); //default to zero for missing fields
                    }
                }

            }
            queryResults.setResults(pivotMap.values());
        }
        return queryResults;
    }
}
