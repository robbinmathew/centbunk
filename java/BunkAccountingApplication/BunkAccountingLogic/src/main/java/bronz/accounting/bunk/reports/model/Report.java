package bronz.accounting.bunk.reports.model;

import bronz.accounting.bunk.reports.exception.ReportException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.ImageIO;

public class Report {
	private static final int PNG_IMG_PAGE_WIDTH = 955;
	private static final int PNG_IMG_PAGE_HEIGHT = 1350;
    private String title;
    private JasperPrint reportPrint;
    
    public Report() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( final String title ) {
        this.title = title;
    }

    public JasperPrint getReportPrint() {
        return reportPrint;
    }

    public void setReportPrint( final JasperPrint reportPrint ) {
        this.reportPrint = reportPrint;
    }
    
    public void exportToFile( final ReportFormat format, final String outputFileName )
            throws ReportException
    {
        final JRExporter exporter;
        try
        {
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
	        exporter.setParameter(JRExporterParameter.JASPER_PRINT, this.reportPrint);
	        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
			exporter.exportReport();
		}
        catch (JRException e)
		{
			throw new ReportException(e);
		}
    }

	public void writeAsPDF( final OutputStream stream )
		throws ReportException
	{
		try	{
			final JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, this.reportPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, stream);
			exporter.exportReport();
		} catch (JRException e)	{
			throw new ReportException(e);
		}
	}
    
    public String getHtmlText() throws ReportException
    {
    	try
    	{
	        final JRExporter exporter = new JRHtmlExporter();
	        final StringBuffer htmlTextBuffer = new StringBuffer();
	        exporter.setParameter(JRHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
	        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
	        exporter.setParameter(JRExporterParameter.JASPER_PRINT, this.reportPrint);
	        exporter.setParameter(JRHtmlExporterParameter.OUTPUT_STRING_BUFFER, htmlTextBuffer);
	        exporter.exportReport();
	        return htmlTextBuffer.toString();
	    }
	    catch (JRException e)
		{
			throw new ReportException(e);
		}
    }

	public void printAsImage(final OutputStream stream )
		throws ReportException{
		try	{
			final int pageSize = reportPrint.getPages().size();
			final BufferedImage result = new BufferedImage(
				PNG_IMG_PAGE_WIDTH, PNG_IMG_PAGE_HEIGHT * pageSize, BufferedImage.TYPE_INT_RGB);
			int y = 0;
			final Graphics g = result.getGraphics();
			for (int i = 0;i<pageSize; i++) {
				BufferedImage rendered_image = (BufferedImage)JasperPrintManager.printPageToImage(reportPrint, i, 1.6f);
				g.drawImage(rendered_image, 0, y, null);
				y += rendered_image.getHeight();
			}
			ImageIO.write(result, "png", stream);
		} catch (Exception e)	{
			throw new ReportException(e);
		}
	}

}