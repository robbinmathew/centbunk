package bronz.accounting.bunk.reports;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.framework.dao.DBUtil;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.reports.exception.ReportException;
import bronz.accounting.bunk.reports.model.ReportFormat;
import bronz.accounting.bunk.reports.util.ReportGeneratorHelper;
import bronz.utilities.general.DateUtil;

public class ReportsGeneratorJRImpl implements ReportsGenerator
{
    private final PartyDao partyDao;
    private final DBUtil dbUtil;
    
    public ReportsGeneratorJRImpl( final PartyDao partyDao, final DBUtil dbUtil )
    {
        this.partyDao = partyDao;
        this.dbUtil = dbUtil;
    }
    
    
    public String generatePartyStatement( final ReportFormat format, PartyClosingBalance party,
            int startDate, int endDate ) throws ReportException
    {
        String fileName = null;
        try
        {
            final JasperReport jasperReport = getReportTemplate(
                    "bronz/accounting/bunk/report/PBMS_PARTY_STATEMENT.jrxml" );
            final HashMap<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", String.format( "Credit statement for %1$S in period %2$S - %3$S",
                    party.getName(), DateUtil.getDateString( startDate ), DateUtil.getDateString( endDate ) ));
            parameterMap.put("one", 1);
            parameterMap.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            final List<PartyTransaction> transactions = this.partyDao.getTransForPartyByDate( party.getId(),
                    startDate, endDate, "CREDIT", null );
            final JRDataSource dataSource;
            if (null != transactions && !transactions.isEmpty())
            {
                dataSource = new JRBeanCollectionDataSource(transactions);
            }
            else
            {
                dataSource = new JREmptyDataSource();
            }
            final JasperPrint print = JasperFillManager.fillReport( jasperReport, parameterMap, dataSource);
            fileName = ReportGeneratorHelper.getFileName( "REPORT_" + party.getName(), startDate, format.getExtension() );
            exportToFile( format, print, fileName );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        return fileName;
    }
    
    public String generateClosingStatement( final ReportFormat format, int date ) throws ReportException
    {
        return generateClosingStatement( format, date,
        		AppConfig.DEFAULT_REPORT_PATH_PROP_NAME.getValue( String.class ));
    }
    
    public String generateClosingStatement( final ReportFormat format, int date, final String reportFolder ) throws ReportException
    {
        String fileName = null;
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
            
            connection = this.dbUtil.getConnection();
            final JasperReport jasperReport = getReportTemplate(
                    "bronz/accounting/bunk/report/DAILY_STATEMENT_REPORT.jrxml" );
            final Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("reportName", String.format( "Closing statement for %1$S",
                    DateUtil.getDateString( date ) ));
            parameterMap.put("date", date );
            parameterMap.put("subReportConnection", connection );
            parameterMap.put("subReportFolder", ReportGeneratorHelper.getCompiledSubReportFolder() );
            parameterMap.put("cashToOfficePartyId", AppConfig.OFFICE_CASH_PARTY_ID.getValue( Integer.class ) );
            parameterMap.put("chqToOfficePartyId", AppConfig.OFFICE_CHEQUE_PARTY_ID.getValue( Integer.class ) );
            
            //parameterMap.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            final JRDataSource dataSource=new JREmptyDataSource();
            final JasperPrint print = JasperFillManager.fillReport( jasperReport, parameterMap, dataSource);
            fileName = ReportGeneratorHelper.getFileName( "DAILY_STATEMENT", date, format.getExtension(), reportFolder );
            exportToFile( format, print, fileName );
        }
        catch (Exception e)
        {
            throw new ReportException("Failed to generate report", e);
        }
        finally
        {
            this.dbUtil.closeConnection( connection );
        }
        return fileName;
    }
    
    private JasperReport getReportTemplate( final String templateFileName ) throws JRException
    {
        final InputStream reportStream = ReportsGeneratorJRImpl.class.getClassLoader().getResourceAsStream(
                templateFileName );
        return JasperCompileManager.compileReport( reportStream );
    }
    
    private void compileSubReport( final String sourceFile, final String destFile) throws Exception
    {
        final InputStream reportStream = ReportsGeneratorJRImpl.class.getClassLoader().getResourceAsStream(
                sourceFile );
        final String destFileString = ReportGeneratorHelper.getCompiledSubReportFolder() + destFile;
        new File( destFileString ).createNewFile();
        final OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( destFileString ) );
        JasperCompileManager.compileReportToStream( reportStream, outputStream );
        
    }
    
    private void exportToFile( final ReportFormat format, final JasperPrint print, final String outputFileName )
        throws JRException, ReportException
    {
        final JRExporter exporter;
        switch(format)
        {
            case PDF:
                exporter = new JRPdfExporter();
                break;
            case EXCEL:
                exporter = new JRXlsExporter();
                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
                exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, true);
                exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, false);
                break;
            case HTML:
                exporter = new JRHtmlExporter();
                exporter.setParameter(JRHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
                break;
                default:
                    throw new ReportException("The format is not supportted for this report.");
        }
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
        exporter.exportReport();
    }
}
