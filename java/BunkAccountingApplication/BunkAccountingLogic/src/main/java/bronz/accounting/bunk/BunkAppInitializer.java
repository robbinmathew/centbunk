package bronz.accounting.bunk;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import bronz.accounting.bunk.model.dao.SavedStatementDao;

import bronz.accounting.bunk.framework.dao.BunkAppDaoHibernateImpl;
import bronz.accounting.bunk.framework.dao.DBUtil;
import bronz.accounting.bunk.framework.dao.TxnManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.reports.JRReportsCreator;
import bronz.accounting.bunk.reports.JRReportsCreatorImpl;
import bronz.accounting.bunk.reports.ReportsGenerator;
import bronz.accounting.bunk.reports.ReportsGeneratorJRImpl;
import bronz.accounting.bunk.tankandmeter.dao.TankAndMeterDao;
import bronz.accounting.bunk.util.EntityNameCache;
import bronz.accounting.bunk.util.EntityTransBalCorrectionHelper;
import bronz.accounting.bunk.util.TransactionSupportInvocationHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BunkAppInitializer
{
    private static final Logger LOG = LogManager.getLogger(
            BunkAppInitializer.class );
    
    private static Map<String, String> DB_PROPS_TO_BE_OVERRIDEN =
            new HashMap<String, String>();
    
    public static final String TEST_ENV = "TEST_ENV";
    public static final String PROD = "PROD";
    
    private static BunkAppInitializer SINGLETON_INSTANCE;
    private BunkAppDaoHibernateImpl daoHibernateImpl;
    private BunkManager bunkManager;
    
    private BunkAppInitializer()
    {
        this.daoHibernateImpl = new BunkAppDaoHibernateImpl(
                "bronz/accounting/bunk/config/databaseConfig.cfg.xml",
                DB_PROPS_TO_BE_OVERRIDEN );
    }
    
    public static synchronized BunkAppInitializer getInstance()
    {
        if ( null == SINGLETON_INSTANCE )
        {
            LOG.debug( BunkAppInitializer.class.getName() + " instance created");
            try
            {
            	AppConfig.checkIfAllPropertiesAvailable();
                initializeDBPropsToBeOverriden();
                SINGLETON_INSTANCE = new BunkAppInitializer();
                EntityNameCache.readNames( getInstance().getProductDao());
                EntityNameCache.readNames( getInstance().getPartyDao());
                EntityNameCache.readNames( getInstance().getTankAndMeterDao());
            }
            catch ( final Exception exception )
            {
                throw new IllegalStateException( "Failed to initialize " +
                		"the application", exception );
            }
        }
        return SINGLETON_INSTANCE;
    }
    
    public static synchronized void refreshProductNameCache() {
        LOG.info( "Refreshing product name cache");
        try
        {
            EntityNameCache.readNames( getInstance().getProductDao());
        }
        catch ( final Exception exception )
        {
            throw new IllegalStateException( "Failed to refresh product name cache.", exception );
        }
    }
    
    public static synchronized void refreshPartyNameCache() {
        LOG.info( "Refreshing party name cache");
        try
        {
            EntityNameCache.readNames( getInstance().getPartyDao());
        }
        catch ( final Exception exception )
        {
            throw new IllegalStateException( "Failed to refresh party name cache.", exception );
        }
    }
    
    public static synchronized void refreshTankNameCache() {
        LOG.info( "Refreshing tank name cache");
        try
        {
            EntityNameCache.readNames( getInstance().getTankAndMeterDao());
        }
        catch ( final Exception exception )
        {
            throw new IllegalStateException( "Failed to refresh tank name cache.", exception );
        }
    }
    
    public DBUtil getDBUtil()
    {
        return this.daoHibernateImpl;
    }

    public SavedStatementDao getSavedStatementDao()
    {
        return this.daoHibernateImpl;
    }
    
    public PartyDao getPartyDao()
    {
        return this.daoHibernateImpl;
    }
    
    public ProductDao getProductDao()
    {
        return this.daoHibernateImpl;
    }
    
    public TankAndMeterDao getTankAndMeterDao()
    {
        return this.daoHibernateImpl;
    }
    
    public TxnManager getTxnManager()
    {
        return this.daoHibernateImpl;
    }
    
    public ReportsGenerator getReportsGenerator()
    {
        return new ReportsGeneratorJRImpl( getPartyDao(), getDBUtil() );
    }
    
    public EntityTransBalCorrectionHelper getEntityTransBalCorrectionHelper()
    {
        return new EntityTransBalCorrectionHelper( getDBUtil() );
    }
    
    public JRReportsCreator getReportsCreator()
    {
        return new JRReportsCreatorImpl( getDBUtil() );
    }
    
    public BunkManager getBunkManager() throws BunkMgmtException
    {
    	if (this.bunkManager == null){
            final BunkManagerImpl realBunkManager = new BunkManagerImpl( getPartyDao(), getProductDao(),
    				getTankAndMeterDao(), getDBUtil(), getSavedStatementDao() );
            final BunkManager proxied = (BunkManager) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                                                                             realBunkManager.getClass().getInterfaces(),
                                                                             new TransactionSupportInvocationHandler(
                                                                                 realBunkManager, getTxnManager()));
            this.bunkManager = proxied;
    		this.bunkManager.getNextDate();
    	}
        return this.bunkManager;
    }
    
    public void reInitialize() throws BunkMgmtException {
    	getBunkManager().getNextDate();
    }
    
    private static void initializeDBPropsToBeOverriden()
    {
        DB_PROPS_TO_BE_OVERRIDEN.put( "hibernate.connection.url",
                "jdbc:mysql://" + AppConfig.DB_HOST_PROP_NAME.getValue(String.class)
                + "/" +  AppConfig.DB_SCHEMA_NAME_PROP_NAME.getValue(String.class) + "?autoReconnect=true&useSSL=false" );
        DB_PROPS_TO_BE_OVERRIDEN.put( "hibernate.connection.username",
        		AppConfig.DB_USERNAME_PROP_NAME.getValue(String.class) );
        DB_PROPS_TO_BE_OVERRIDEN.put( "hibernate.connection.password",
        		AppConfig.DB_PASSWORD_PROP_NAME.getValue(String.class) );
    }	
}
