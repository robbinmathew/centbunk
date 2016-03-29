package bronz.accounting.bunk.reports;


import junit.framework.TestCase;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.reports.model.ReportFormat;
import bronz.utilities.general.GeneralUtil;

public class ReportGeneratorJRImplTest extends TestCase
{
    private ReportsGenerator reportsGenerator;
    private int date;
    public void setUp()
    {
        try
        {
            this.date = 734654;
            GeneralUtil.readPropsToSystemProps( AppConfig.PROPERTIES_FILE_FOLDER +
            		AppConfig.APP_PROP_FILE_NAME );
            this.reportsGenerator = BunkAppInitializer.getInstance().getReportsGenerator();
        }
        catch( final Exception exception )
        {
            exception.printStackTrace();
            fail( "Failed to create setup for test" );
        }
    }

    public void testGenerateSettlementPDFReport() throws Exception
    {
        final String reportName = this.reportsGenerator.generateClosingStatement( ReportFormat.PDF, date );
        System.out.println(reportName);
        Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler " + reportName );
    }
}
