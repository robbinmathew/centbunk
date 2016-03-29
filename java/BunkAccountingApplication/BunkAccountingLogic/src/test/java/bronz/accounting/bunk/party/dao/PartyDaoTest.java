package bronz.accounting.bunk.party.dao;


import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.utilities.general.GeneralUtil;
import junit.framework.TestCase;
@SuppressWarnings("unused")
public class PartyDaoTest extends TestCase
{
	private PartyDao partyDao;
    private int date;
    
    public void setUp()
    {
        try
        {
            this.date = 734386;
            GeneralUtil.readPropsToSystemProps(
            		AppConfig.PROPERTIES_FILE_FOLDER +
            		AppConfig.APP_PROP_FILE_NAME );
            this.partyDao = BunkAppInitializer.getInstance().getPartyDao();
        }
        catch( final Exception exception )
        {
            exception.printStackTrace();
            fail( "Failed to create setup for test" );
        }
    }
    
    public void testGetTransactions() throws Exception
    {
       /* final List<PartyTransaction> transactions =
            this.partyDao.getTransForPartyByDate( partyId, startDate, endDate, transTypeFilter, detailFilter )tAllPartyTransByDate( this.date, this.date );
        assertNotNull( transactions );
        assertTrue( transactions.size() > 0 );*/
    }
    

}
