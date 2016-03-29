package bronz.accounting.bunk.ui.product.panel;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sf.jasperreports.swing.JRViewer;

import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.model.StockReceipt;
import bronz.accounting.bunk.reports.model.Report;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.panel.BasePublicPanel;
import bronz.accounting.bunk.ui.panel.CalendarButton;
import bronz.accounting.bunk.ui.panel.SimpleJRViewer;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.utilities.general.DateUtil;

public class StockRecepSummaryPanel extends BasePublicPanel
        implements ActionListener
{
	/** Serial Number.*/
    private static final long serialVersionUID = 1L;
    private static final String SUB_TITLE = "Stocks receipts summary";
    
    private static final String CAL_BUTTON = "CAL_BUTTON";
    private static final String GO_BUTTON = "GO_BUTTON";
    private final int selectedDate;
    
    public StockRecepSummaryPanel( final HomePage parentFrame,
            final BunkManager bunkManager, final int selectedDate,
            final Rectangle panelSize ) throws BunkMgmtException
    {
        super( parentFrame, SUB_TITLE );
        this.selectedDate = selectedDate;
        this.uiBuilder.addSubOptionFields(
                UiElement.JBUTTON, StockReceipt.FUEL_RECEIPT_TYPE, "ADD TANKER RECEIPT", 50,
                UiElement.JBUTTON, StockReceipt.OIL_RECEIPT_TYPE, "ADD LUBES RECEIPT", 25,
                UiElement.JBUTTON, StockReceipt.BWATER_RECEIPT_TYPE, "ADD BATTERY WATER RECEIPT", 25 );
        
        populateDetailsPane();
        UiUtil.addActionListeners( this, false );
        this.validate();
        this.repaint();
    }
    
    @Override
    public String getTitleSuffix()
    {
        return "Stocks Receipt Summary";
    }
    
    
    private void populateDetailsPane() throws BunkMgmtException
    {
    	final Report report = BunkAppInitializer.getInstance().getReportsCreator().createReceiptSummaryReport(
        		this.selectedDate, this.selectedDate );
    	
    	final int noOfPages = report.getReportPrint().getPages().size();
	    final Object[] reportPages = new Object[noOfPages*3];
	    
	    for (int i = 0; i < report.getReportPrint().getPages().size(); i++)
	    {
	    	final JRViewer jrViewer = new SimpleJRViewer( report.getReportPrint(), i );
	    	reportPages[(i*3)]= "PAGE"+i;
	    	reportPages[(i*3)+1]= jrViewer;
	    	reportPages[(i*3)+2]= 250;
	    }
	    this.uiBuilder.addDetailPanelsWithScroll(reportPages);
    	
        this.uiBuilder.addElement( UiElement.CALENDAR_BUTTON, CAL_BUTTON, panelSize.width - 370, 10, this.selectedDate, "DATE", this.parentFrame );
        this.uiBuilder.addElement( UiElement.JBUTTON, GO_BUTTON, panelSize.width - 110, 10, 65, 20, "GO" );
    }
    
    public void actionPerformed( ActionEvent actionEvent )
    {
        try
        {
            if ( actionEvent.getSource() == this.componentMap.get(
            		StockReceipt.FUEL_RECEIPT_TYPE ) )
            {
                this.parentFrame.setPublicPanel( new StockReceptionPanel(
                        this.parentFrame, StockReceipt.FUEL_RECEIPT_TYPE ) );
            }
            else if ( actionEvent.getSource() == this.componentMap.get(
            		StockReceipt.OIL_RECEIPT_TYPE ) )
            {
                this.parentFrame.setPublicPanel( new StockReceptionPanel(
                        this.parentFrame, StockReceipt.OIL_RECEIPT_TYPE ) );
            }
            else if ( actionEvent.getSource() == this.componentMap.get(
            		StockReceipt.BWATER_RECEIPT_TYPE ) )
            {
                this.parentFrame.setPublicPanel( new StockReceptionPanel(
                        this.parentFrame, StockReceipt.BWATER_RECEIPT_TYPE ) );
            }
            else if ( actionEvent.getSource() == this.componentMap.get( GO_BUTTON ) )
            {
            	final int selectedDate = DateUtil.getIntegerEquivalent(
            			this.getComp( CAL_BUTTON, CalendarButton.class).getSelectedDate());
            	this.parentFrame.setPublicPanel( new StockRecepSummaryPanel(
                        this.parentFrame, this.bunkManager, selectedDate, this.panelSize ) );
            }
            else
            {
                throw new UnsupportedOperationException( "No supported" );
            }
        }
        catch ( final Exception exception )
        {
            UiUtil.alertUnexpectedError( this, exception );
        }
    }
}
