package bronz.accounting.bunk.product.dao;

import java.util.List;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.utilities.general.GeneralUtil;
import junit.framework.TestCase;

public class ProductDaoTest extends TestCase
{
    private ProductDao productDao;
    private int date;
    
    public void setUp()
    {
        try
        {
            this.date = 734386;
            GeneralUtil.readPropsToSystemProps(
            		AppConfig.PROPERTIES_FILE_FOLDER +
            		AppConfig.APP_PROP_FILE_NAME );
            this.productDao = BunkAppInitializer.getInstance().getProductDao();
        }
        catch( final Exception exception )
        {
            exception.printStackTrace();
            fail( "Failed to create setup for test" );
        }
    }
    
    public void testGetTransactions() throws Exception
    {
        final List<ProductTransaction> transactions =
            this.productDao.getAllProdTransByDate( this.date, this.date );
        System.out.println( transactions );
        assertNotNull( transactions );
        assertTrue( transactions.size() > 0 );
    }
    
    public void testGetRecieptTransactions() throws Exception
    {
        final List<ProductTransaction> transactions =
            this.productDao.getAllProdTransByDateAndType( this.date, this.date,
            		ProductTransaction.RECEIPT );
        assertNotNull( transactions );
        assertTrue( transactions.size() > 0 );
    }
    
    public void testClosingStocks() throws Exception
    {
        final List<ProductClosingBalance> closingBals =
            this.productDao.getProdClosingByDate( this.date );
        System.out.println( closingBals );
        assertNotNull( closingBals );
        assertTrue( closingBals.size() > 0 );
    }


}
