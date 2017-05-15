package bronz.accounting.bunk.reports;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.framework.dao.DBUtil;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.reports.exception.ReportException;
import bronz.accounting.bunk.reports.model.Report;
import bronz.accounting.bunk.reports.util.ReportGeneratorHelper;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.utilities.general.DateUtil;

public class JRReportsCreatorImpl implements JRReportsCreator
{
	private static final Logger LOG = LogManager.getLogger( JRReportsCreatorImpl.class );
    private final DBUtil dbUtil;
    
    public JRReportsCreatorImpl( final DBUtil dbUtil )
    {
        this.dbUtil = dbUtil;
    }
    
    public Report createPartyStatement( final PartyClosingBalance party, final int startDate, final int endDate )
            throws ReportException
    {
        return createPartyStatement( party, startDate, endDate, "PARTY_ALL_TRANSACTIONS_STMT" );
    }
    
    public Report createCreditAlonePartyStatement( final PartyClosingBalance party, final int startDate, final int endDate )
        throws ReportException
    {
        return createPartyStatement( party, startDate, endDate, "PARTY_CREDIT_TRANSACTIONS_STMT" );
    }
    
    public Report createProdSalesStatement( final ProductClosingBalance prod, final int startDate, final int endDate )
		throws ReportException
	{
    	final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TEMPLATE_WITH_TITLE");
        	report.setTitle( String.format( "Transaction statement for %1$S in period %2$S - %3$S",
        			prod.getProductName(), DateUtil.getDateString( startDate ), DateUtil.getDateString( endDate ) ) );
            connection = this.dbUtil.getConnection();
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("reportName", report.getTitle());
            subReportParameterMap.put("prodId", prod.getProductId());
            subReportParameterMap.put("startDate", startDate);
            subReportParameterMap.put("endDate", endDate);
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/PROD_TRANSACTIONS_STMT") );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
	}
    
    public Report createDSR( final ProductClosingBalance prod, final int startDate, final int endDate )
    		throws ReportException
    	{
        	final Report report = new Report();
            Connection connection = null;
            InputStream reportTemplateInputStream = null;
            try
            {
            	reportTemplateInputStream = getCompiledReportAsStream(
            			"bronz/accounting/bunk/report/GENERIC_TEMPLATE_WITH_TITLE");
            	report.setTitle( String.format( "Daily-Sales-Report for %1$S in period %2$S - %3$S",
            			prod.getProductName(), DateUtil.getDateString( startDate ), DateUtil.getDateString( endDate ) ) );
                connection = this.dbUtil.getConnection();
                final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
                subReportParameterMap.put("reportName", report.getTitle());
                subReportParameterMap.put("prodId", prod.getProductId());
                subReportParameterMap.put("startDate", startDate);
                subReportParameterMap.put("endDate", endDate);
                
                final Map<String, Object> parameterMap = new HashMap<String, Object>();
                parameterMap.put("reportName", report.getTitle());
                parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
            			"bronz/accounting/bunk/report/subreport/DSR_STMT") );
                parameterMap.put("subReportConnection", connection );
                parameterMap.put("subReportParameterMap", subReportParameterMap );
                
                final JRDataSource dataSource=new JREmptyDataSource();
                report.setReportPrint( JasperFillManager.fillReport(
                		reportTemplateInputStream, parameterMap, dataSource) );
            }
            catch (Exception e)
            {
                throw new ReportException("Failed to generate report", e);
            }
            finally
            {
                this.dbUtil.closeConnection( connection );
                closeInputStream(reportTemplateInputStream);
            }
            return report;
    	}
    
    public Report createTankSalesStatement( final TankClosingStock tank, final int startDate, final int endDate )
	throws ReportException
	{
    	final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TEMPLATE_WITH_TITLE");
        	report.setTitle( String.format( "Transaction statement for %1$S in period %2$S - %3$S",
        			tank.getTankName(), DateUtil.getDateString( startDate ), DateUtil.getDateString( endDate ) ) );
            connection = this.dbUtil.getConnection();
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("reportName", report.getTitle());
            subReportParameterMap.put("tankId", tank.getTankId());
            subReportParameterMap.put("startDate", startDate);
            subReportParameterMap.put("endDate", endDate);
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/TANK_TRANSACTIONS_STMT") );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
	}
    
    public Report createCreditStatusReport( final int date ) throws ReportException
    {
    	final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TEMPLATE_WITH_TITLE");
        	report.setTitle( String.format( "Credit summary on %1$S", DateUtil.getDateString( date ) ) );
            connection = this.dbUtil.getConnection();
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("date", date);
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/CREDIT_SUMMARY") );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
    }
    
    public Report createStockStatusReport( final int date ) throws ReportException
    {
    	final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TEMPLATE_WITH_TITLE");
        	report.setTitle( String.format( "Stock summary on %1$S", DateUtil.getDateString( date ) ) );
            connection = this.dbUtil.getConnection();
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("date", date);
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/STOCK_SUMMARY") );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
    }
    
    private Report createPartyStatement( final PartyClosingBalance party, final int startDate, final int endDate,
            final String subReportFileName ) throws ReportException
    {
    	final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TEMPLATE_WITH_TITLE");
        	report.setTitle( String.format( "Credit statement for %1$S in period %2$S - %3$S",
                    party.getName(), DateUtil.getDateString( startDate ), DateUtil.getDateString( endDate ) ) );
            connection = this.dbUtil.getConnection();
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("reportName", report.getTitle());
            subReportParameterMap.put("partyId", party.getId());
            subReportParameterMap.put("startDate", startDate);
            subReportParameterMap.put("endDate", endDate);
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/" + subReportFileName) );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
    }

    public Report createClosingStatement( int date ) throws ReportException
    {
        final Report report = new Report();
        Connection connection = null;
        try
        {
            compileSubReport( "bronz/accounting/bunk/report/subreport/DAILY_STMT_PROD_SALES.jrxml",
                    "DAILY_STMT_PROD_SALES.jasper" );
            compileSubReport( "bronz/accounting/bunk/report/subreport/DAILY_STMT_METER_CLOSING.jrxml",
                    "DAILY_STMT_METER_CLOSING.jasper" );
            compileSubReport( "bronz/accounting/bunk/report/subreport/DAILY_STMT_PARTY_TRANSACTIONS.jrxml",
                    "DAILY_STMT_PARTY_TRANSACTIONS.jasper" );
            compileSubReport( "bronz/accounting/bunk/report/subreport/DAILY_STMT_CASH_IN_OFF.jrxml",
                    "DAILY_STMT_CASH_IN_OFF.jasper" );
            
            report.setTitle( String.format( "Closing statement for %1$S",
                    DateUtil.getDateString( date ) ));
            
            connection = this.dbUtil.getConnection();
            final JasperReport jasperReport = getReportTemplate(
                    "bronz/accounting/bunk/report/DAILY_STATEMENT_REPORT.jrxml" );
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("date", date );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportFolder", ReportGeneratorHelper.getCompiledSubReportFolder() );
            parameterMap.put("cashToOfficePartyId", AppConfig.OFFICE_CASH_PARTY_ID.getValue( Integer.class ) );
            parameterMap.put("chqToOfficePartyId", AppConfig.OFFICE_CHEQUE_PARTY_ID.getValue( Integer.class ) );
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport( jasperReport, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
        }
        return report;
    }
    
    public Report createCashSummaryStatement( final int date ) throws ReportException
    {
        final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TEMPLATE_WITH_TITLE");
            report.setTitle( String.format( "ACCOUNTS SUMMARY ON %1$S", DateUtil.getDateString( date ) ));
            connection = this.dbUtil.getConnection();
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("date", date);
            subReportParameterMap.put("cashSummaryTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/CASH_SUMMARY") );
            subReportParameterMap.put("creditSummaryTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/CREDIT_SUMMARY") );
            subReportParameterMap.put("stockSummaryTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/STOCK_SUMMARY") );
            subReportParameterMap.put("bankSummaryTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/BANK_SUMMARY") );
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/DAILY_SUMMARY") );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            //parameterMap.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
    }
    
    public Report createMonthlyCashSummaryStatement( final int date, final int todayDate )
    		throws ReportException
    {
        final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TEMPLATE_WITH_TITLE");
        	
        	final int lastDayOfTheMonth = DateUtil.getLastDateOfGivenMonth(date);
        	if (lastDayOfTheMonth > todayDate)
        	{
                report.setTitle( String.format( "MONTHLY SUMMARY FOR THE CURRENT MONTH. (TILL %1$S)", DateUtil.getDateString( date ) ));
        	}
        	else if (lastDayOfTheMonth == todayDate)
        	{
                report.setTitle( String.format( "MONTHLY SUMMARY FOR THE CURRENT MONTH. (TILL %1$S-OPENING)", DateUtil.getDateString( date ) ));
        	}
        	else
        	{
                report.setTitle( String.format( "MONTHLY SUMMARY FOR %1$S", DateUtil.getDateYearMonthString( date ) ));
        	}
        	

            connection = this.dbUtil.getConnection();
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("date", date);
            subReportParameterMap.put("cashSummaryTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/CASH_SUMMARY") );
            subReportParameterMap.put("creditSummaryTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/PARTY_MONTHLY_SUMMARY") );
            subReportParameterMap.put("stockSummaryTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/PROD_SALES_MONTHLY_STMT") );
            subReportParameterMap.put("bankSummaryTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/BANK_MONTHLY_SUMMARY") );
            subReportParameterMap.put("salSummaryTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/SALARY_MONTHLY_SUMMARY") );
            subReportParameterMap.put("partyDetailsTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/PARTY_ALL_TRX_MONTHLY_STMT") );
            subReportParameterMap.put("specialPartyDetailsTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/SPECIAL_PARTY_MONTHLY_SUMMARY") );
            subReportParameterMap.put("rateChangeDetailsTemplateUrl", getCompiledReportAsString(
            "bronz/accounting/bunk/report/subreport/PROD_RATE_CHANGE_MONTHLY_STMT") );
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/MONTHLY_SUMMARY") );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            //parameterMap.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
    }
    
    public Report createMonthlySalarySummaryStatement( final int date, final int todayDate )
    		throws ReportException
    {
        final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TITLELESS_TEMPLATE");
        	
        	final int lastDayOfTheMonth = DateUtil.getLastDateOfGivenMonth(date);
        	if (lastDayOfTheMonth >= todayDate)
        	{
                report.setTitle( String.format( "SALARY SUMMARY FOR THE CURRENT MONTH. (TILL %1$S)", DateUtil.getDateString( date ) ));
        	}
        	else
        	{
                report.setTitle( String.format( "SALARY SUMMARY FOR %1$S", DateUtil.getDateYearMonthString( date ) ));
        	}
        	

            connection = this.dbUtil.getConnection();
            
            
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("date", date);
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/SALARY_MONTHLY_SUMMARY") );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
    }
    
    public Report createReceiptSummaryReport( final int startDate, final int endDate ) throws ReportException
    {
        final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TITLELESS_TEMPLATE");
        	if (startDate == endDate)
        	{
        		report.setTitle( String.format( "STOCK RECEIPTS ON %1$S", DateUtil.getDateString( startDate )));
        	}
        	else
        	{
        		report.setTitle( String.format( "STOCK RECEIPTS BETWEEN %1$S AND %2$S",
                		DateUtil.getDateString( startDate ), DateUtil.getDateString( endDate ) ));
        	}
            
            connection = this.dbUtil.getConnection();
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("startDate", startDate);
            subReportParameterMap.put("endDate", endDate);
            subReportParameterMap.put("receiptDetailsTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/RECEIPT_DETAILS") );
            subReportParameterMap.put("prodReceiptDetailsTemplateUrl", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/RECEIPT_DETAILS_PROD") );
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/RECEIPT_SUMMARY") );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            //parameterMap.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
    }
    
    public Report createProdSalesStatement( final int startDate, final int endDate ) throws ReportException
    {
        final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/GENERIC_TITLELESS_TEMPLATE");
        	report.setTitle( String.format( "PRODUCT SALES STATEMENT (%1$S - %2$S)",
                		DateUtil.getDateString( startDate ), DateUtil.getDateString( endDate ) ));
            
            connection = this.dbUtil.getConnection();
            final Map<String, Object> subReportParameterMap = new HashMap<String, Object>();
            subReportParameterMap.put("startDate", startDate);
            subReportParameterMap.put("endDate", endDate);
            
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", report.getTitle());
            parameterMap.put("subReportTemplateURL", getCompiledReportAsString(
        			"bronz/accounting/bunk/report/subreport/PROD_SALES_STMT") );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportParameterMap", subReportParameterMap );
            
            final JRDataSource dataSource=new JREmptyDataSource();
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, dataSource) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
    }
    
    public Report createStockVariationStatement( final int date ) throws ReportException
    {
        final Report report = new Report();
        Connection connection = null;
        InputStream reportTemplateInputStream = null;
        try
        {
        	reportTemplateInputStream = getCompiledReportAsStream(
        			"bronz/accounting/bunk/report/subreport/PRIMARY_PROD_STOCK_VARIATION");
        	report.setTitle( String.format( "STOCK VARIATION THIS MONTH (%1$S)",
                		DateUtil.getDateYearMonthString( date ) ));
            
            connection = this.dbUtil.getConnection();
            final int firstDayOfMonth = DateUtil.getFirstDateOfGivenMonth(date);
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("monthStart", firstDayOfMonth);
            parameterMap.put("monthLast", date);
            parameterMap.put("title", report.getTitle());
            parameterMap.put("prevMonthStart", DateUtil.getFirstDateOfGivenMonth(firstDayOfMonth - 1));
            parameterMap.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            
            report.setReportPrint( JasperFillManager.fillReport(
            		reportTemplateInputStream, parameterMap, connection) );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
            closeInputStream(reportTemplateInputStream);
        }
        return report;
    }
    
    private void closeInputStream(final InputStream inputStream) {
    	if (inputStream != null)
        {
        	try {
        		inputStream.close();
			} catch (IOException e) {
				LOG.error("Failed to close the stream. This error may not be serious", e);
			}
        }
    }
    
    private JasperReport getReportTemplate( final String templateFileName ) throws JRException
    {
        final InputStream reportStream = JRReportsCreatorImpl.class.getClassLoader().getResourceAsStream(
                templateFileName );
        return JasperCompileManager.compileReport( reportStream );
    }
    
    private void compileSubReport( final String sourceFile, final String destFile) throws Exception
    {
        final InputStream reportStream = JRReportsCreatorImpl.class.getClassLoader().getResourceAsStream(
                sourceFile );
        final String destFileString = ReportGeneratorHelper.getCompiledSubReportFolder() + destFile;
        new File( destFileString ).createNewFile();
        final OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( destFileString ) );
        JasperCompileManager.compileReportToStream( reportStream, outputStream );
        
    }
    
    private InputStream getCompiledReportAsStream( final String sourceFile) throws Exception
    {
    	final String fileOnClassPath = sourceFile + ".jasper";
    	final URL compiledReportURLOnClasspath =
    		JRReportsCreatorImpl.class.getClassLoader().getResource(
    				fileOnClassPath);
    	if (compiledReportURLOnClasspath != null )
    	{
    		LOG.debug("Compiled template on classpath Url:" +
    				fileOnClassPath);
    		return JRReportsCreatorImpl.class.getClassLoader().getResourceAsStream(
    				fileOnClassPath );
    	}
    	
        final InputStream reportStream = JRReportsCreatorImpl.class.getClassLoader().getResourceAsStream(
                sourceFile + ".jrxml" );
        final String destFileString = ReportGeneratorHelper.getCompiledSubReportFolder() + sourceFile.substring(sourceFile.lastIndexOf("/") + 1) + ".jasper";
        LOG.debug("Compiling the template to:" + destFileString);
        new File( destFileString ).createNewFile();
        final OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( destFileString ) );
        JasperCompileManager.compileReportToStream( reportStream, outputStream );
        return new FileInputStream(destFileString);
    }
    
    private String getCompiledReportAsString( final String sourceFile) throws Exception
    {
    	final String fileOnClassPath = sourceFile + ".jasper";
    	final URL compiledReportURLOnClasspath =
    		JRReportsCreatorImpl.class.getClassLoader().getResource(
    				fileOnClassPath);
    	if (compiledReportURLOnClasspath != null )
    	{
    		LOG.debug("Compiled template on classpath Url:" +
    				fileOnClassPath);
    		return fileOnClassPath;
    	}
    	final InputStream reportStream = JRReportsCreatorImpl.class.getClassLoader().getResourceAsStream(
                sourceFile + ".jrxml" );
        final String destFileString = ReportGeneratorHelper.getCompiledSubReportFolder() + sourceFile.substring(sourceFile.lastIndexOf("/") + 1) + ".jasper";
        LOG.debug("Compiling the template to:" + destFileString);
        new File( destFileString ).createNewFile();
        final OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( destFileString ) );
        JasperCompileManager.compileReportToStream( reportStream, outputStream );
        return destFileString;
    }
}
