package bronz.accounting.bunk.ui.panel;

import javax.swing.JPanel;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.swing.JRViewerPanel;
import net.sf.jasperreports.swing.JRViewerToolbar;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.save.JRHtmlSaveContributor;
import net.sf.jasperreports.view.save.JRPdfSaveContributor;
import net.sf.jasperreports.view.save.JRSingleSheetXlsSaveContributor;

public class SimpleJRViewer extends JRViewer
{
	private static final long serialVersionUID = 1L;
	
	private JRViewerPanel panel;
	private final JRSaveContributor[] jrSaveContributors =
		{ new JRPdfSaveContributor( getLocale(), null ),
            new JRSingleSheetXlsSaveContributor( getLocale(), null ),
            new JRHtmlSaveContributor( getLocale(), null )};

	public SimpleJRViewer(final JasperPrint jrPrint, final int pageIndex)
	{
		super(jrPrint);
		customizeToolBar();
		this.viewerContext.setPageIndex(pageIndex);
	}
	
	private void customizeToolBar()
	{
		this.remove(this.tlbToolBar);
		final JRViewerToolbar customizedToolBar = new JRViewerToolbar(this.viewerContext) {
			private static final long serialVersionUID = 1L;

			protected void initSaveContributors(){
				super.initSaveContributors();
				this.remove(this.btnReload);
				this.remove(this.btnFirst);
				this.remove(this.btnNext);
				this.remove(this.btnLast);
				this.remove(this.btnPrevious);
				this.remove(this.txtGoTo);
				this.remove(this.cmbZoom);
				this.remove(this.btnActualSize);
				
			}
		};
		customizedToolBar.setSaveContributors(jrSaveContributors);
		this.add(customizedToolBar, java.awt.BorderLayout.NORTH);
	}
	
	protected JRViewerPanel createViewerPanel()
	{
		this.panel = new JRViewerPanel(this.viewerContext);
	    return this.panel;
	}
	
	protected JPanel getPanel()
	{
	    return this.panel;
	}
}
