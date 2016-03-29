package bronz.accounting.bunk.ui.panel;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import net.sf.jasperreports.swing.JRViewer;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.reports.model.Report;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;

public class ReportViewerPanel extends BasePublicPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final String BACK_BUTTON = "BACK_BUTTON";
	
	private final BasePublicPanel previousPublicPanel;
	private final Report report;
	private final JDialog dialog;
	
	public ReportViewerPanel( final HomePage parentFrame, final BasePublicPanel previousPublicPanel,
	        final Report report, final JDialog dialog ) throws BunkMgmtException
	{
	    super( parentFrame, report.getTitle() );
	    this.dialog = dialog;
	    this.previousPublicPanel = previousPublicPanel;
	    this.report = report;
	    
	    final int noOfPages = report.getReportPrint().getPages().size();
	    final Object[] reportPages = new Object[noOfPages*3];
	    
	    for (int i = 0; i < report.getReportPrint().getPages().size(); i++)
	    {
	    	final JRViewer jrViewer = new SimpleJRViewer( report.getReportPrint(), i );
	    	reportPages[(i*3)]= "PAGE"+i;
	    	reportPages[(i*3)+1]= jrViewer;
	    	reportPages[(i*3)+2]= 250;
	    }
	    this.uiBuilder.addDetailPanelsWithScrollWithoutSubOptions(reportPages);
	    
	    
	    if ( null != this.previousPublicPanel )
	    {
	        this.uiBuilder.addElement( UiElement.JBUTTON, BACK_BUTTON, panelSize.width - 200, 10, 150, 25, "Back" );
	    }
	    UiUtil.addActionListeners( this );
	}
	
	@Override
    public String getTitleSuffix()
    {
        return "View report - " + this.report.getTitle();
    }

    public void actionPerformed( ActionEvent actionEvent )
    {
        if( actionEvent.getSource().equals( this.componentMap.get( BACK_BUTTON )))
        {
            if (null == this.dialog)
            {
            	UiUtil.addActionListeners( this.previousPublicPanel );
            	this.parentFrame.setPublicPanel( this.previousPublicPanel );
            }
            else
            {
            	this.dialog.remove(this);
            	this.dialog.validate();
            	this.dialog.repaint();
            	this.dialog.add(this.previousPublicPanel);
            	this.previousPublicPanel.validate();
            	this.previousPublicPanel.repaint();
            	this.dialog.validate();
            	this.dialog.repaint();
            }
        }
    }
}
