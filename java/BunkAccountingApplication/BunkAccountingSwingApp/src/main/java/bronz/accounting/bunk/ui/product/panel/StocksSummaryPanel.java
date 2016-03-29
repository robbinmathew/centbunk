package bronz.accounting.bunk.ui.product.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sf.jasperreports.swing.JRViewer;

import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.reports.model.Report;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.panel.BasePublicPanel;
import bronz.accounting.bunk.ui.panel.CashPaymentPanel;
import bronz.accounting.bunk.ui.panel.ChangeRatePanel;
import bronz.accounting.bunk.ui.panel.EditProductsPanel;
import bronz.accounting.bunk.ui.panel.SimpleJRViewer;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.ui.util.UiUtil;

public class StocksSummaryPanel extends BasePublicPanel
        implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private static final String SUB_TITLE = "Stocks Summary";
    
    private static final String STOCK_RECEIPT_BUTTON = "STOCK_RECEIPT";
    private static final String PAYMENT = "<html><center><font size=-0>PAY HPCL<br/>(FOR LOAD/RENT)</font></center></html>";
    private static final String CHANGE_RATE = "CHANGE RATE";
    private static final String ADD_EDIT_BUTTON = "ADD_EDIT_BUTTON";
    
    public StocksSummaryPanel( final HomePage parentFrame )
        throws BunkMgmtException
    {
        super( parentFrame, SUB_TITLE );
        this.uiBuilder.addSubOptionFields(
                UiElement.JBUTTON, STOCK_RECEIPT_BUTTON, "STOCK RECEIPTS", 30,
                UiElement.JBUTTON, PAYMENT, PAYMENT, 30,
                UiElement.JBUTTON, CHANGE_RATE, "CHANGE RATE", 20,
                UiElement.JBUTTON, ADD_EDIT_BUTTON, "ADD/EDIT PRODUCTS", 20);
        final Report report = BunkAppInitializer.getInstance().getReportsCreator().createStockStatusReport(
        		this.todayInteger );
        
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
        UiUtil.addActionListeners( this );
    }
    
    @Override
    public String getTitleSuffix()
    {
        return "Stocks Summary";
    }
    
    public void actionPerformed( ActionEvent actionEvent )
    {
        try
        {
            if ( actionEvent.getSource() == this.componentMap.get(
                    STOCK_RECEIPT_BUTTON ) )
            {
                this.parentFrame.setPublicPanel( new StockRecepSummaryPanel(
                    this.parentFrame, this.bunkManager,
                    this.todayInteger, this.panelSize ) );
                
            }
            else if ( actionEvent.getSource() == this.componentMap.get( CHANGE_RATE ) )
            {
                this.parentFrame.setPublicPanel( new ChangeRatePanel( this.parentFrame ) );
            }
            else if ( actionEvent.getSource() == this.componentMap.get( ADD_EDIT_BUTTON ) )
            {
                new EditProductsPanel( this.parentFrame, new ActionListener()
                {
                    
                    public void actionPerformed( ActionEvent e )
                    {
                        try
                        {
                            parentFrame.setPublicPanel( new StocksSummaryPanel( parentFrame ) );
                        }
                        catch( Exception exception )
                        {
                            UiUtil.alertUnexpectedError( parentFrame, exception );
                        }
                    }
                });
            }
            else if ( actionEvent.getSource() == this.componentMap.get( PAYMENT ) )
            {
                new CashPaymentPanel( this.todayInteger, CashPaymentPanel.FROM_SUMMARY_PANEL_MODE );
            }


        }
        catch ( final Exception exception )
        {
            UiUtil.alertUnexpectedError( this, exception );
        }
    }
}
