package bronz.accounting.bunk.ui.panel;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.products.model.StockVariation;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.party.model.EmpSalBean;
import bronz.accounting.bunk.ui.party.model.EmployeeTrxDetailBean;
import bronz.accounting.bunk.ui.product.model.TankSaleDetailBean;
import bronz.accounting.bunk.ui.util.EntityConverter;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.utilities.general.DateUtil;
import bronz.utilities.swing.table.AbstractDataTable;
import bronz.utilities.swing.table.GenericReadOnlyTableModel;
import bronz.utilities.swing.util.SwingUtil;
import bronz.utilities.swing.util.UIValidationException;

public class MonthlyClosingPanel extends BasePanel implements ActionListener, TableModelListener
{
	private static final long serialVersionUID = 1L;
	private static final int PANEL_WIDTH = HomePage.SCREEN_WIDTH - 100;
	private static final int PANEL_HEIGHT = HomePage.SCREEN_HIEGHT - 100;
	private static final String VERIFY_STOCK_VARIATIONS = "VERIFY_STOCK_VARIATIONS";
	private static final String SAL_TABLE = "SAL_TABLE";
	private static final String SUBMIT_BUTTON = "SUBMIT_BUTTON";
	
    private final JDialog dialog;
    private final DailyStatementPanel parentPanel;
    private final int date;
    private final List<EmployeeTrxDetailBean> empTrxBeans;
    private final List<EmployeeMonthlyStatus> empSalStatus;

    private AbstractDataTable<EmpSalBean> employeeSalaryTable;
    
	public MonthlyClosingPanel( final HomePage frame,
			final DailyStatementPanel parentPanel,
			final List<StockVariation> stockVariations,
			final List<TankSaleDetailBean> tankSalesBeans,
			final List<EmployeeTrxDetailBean> empTrxBeans) throws BunkMgmtException
	{
        super( 50, 50, PANEL_WIDTH, PANEL_HEIGHT );
        this.date = frame.getTodayInteger();
        this.empTrxBeans = empTrxBeans;
        this.empSalStatus = frame.getBunkManager().getEmployeesStatus();
        final String title = "Monthly closing for month " + DateUtil.getDateYearMonthString(this.date);
        this.parentPanel = parentPanel;
        this.uiBuilder.addElement( UiElement.JLABEL, "", 10, 10, PANEL_WIDTH - 20, 25,
        		title, UiUtil.SUB_TITLE_BOLD_FONT );
        this.uiBuilder.addElement( UiElement.JLABEL, "", 15, 45, PANEL_WIDTH - 30, 40,
        		"<html>Please verify the details given below.<br/>If you find any mistakes, " +
        		"close this window and resubmit after correcting the mistakes.</html>",
        		UiUtil.LABEL_REGULAR_FONT );
        
        this.uiBuilder.addElement( UiElement.JLABEL, "", 15, 90, 300, 25,
        		"Fuel products stock variations.", UiUtil.LABEL_BOLD_FONT );
        final Map<Integer, StockVariation> idToStockMap = new HashMap<Integer, StockVariation>();
        for (StockVariation stock: stockVariations)
        {
        	idToStockMap.put(stock.getProductId(), stock);
        	stock.setThisMonthVariation(BigDecimal.ZERO);
        }
        for (TankSaleDetailBean tankSale : tankSalesBeans)
        {
        	final Integer productId = tankSale.getProdId();
        	if (productId != null)
        	{
        		final StockVariation stock = idToStockMap.get(productId);
        		stock.setThisMonthSale(stock.getThisMonthSale().add(tankSale.getSaleAmt()));
        		stock.setThisMonthVariation(stock.getThisMonthVariation().add(tankSale.getCumDiffThisMonth()));
        	}
        }
        final JTable table = new JTable( new GenericReadOnlyTableModel<StockVariation>(stockVariations,
        		Arrays.asList("PRODUCT", "SALE(THIS MONTH)", "VARIATION(THIS MONTH)",
        				"VARIATION %(THIS MONTH)", "VARIATION(LAST MONTH)", "VARIATION %(LAST MONTH)"),
        		Arrays.asList("prodName", "thisMonthSale", "thisMonthVariation", "thisMonthVarPercetage", "prevMonthVariation", "prevMonthVarPercetage")));
        this.uiBuilder.setPreferedWidthToTables(table, PANEL_WIDTH-30, 9, 13, 21,19,19,19);
        
        this.uiBuilder.addElement( table.getTableHeader(), "", 15, 130, PANEL_WIDTH-30, 15 );
        this.uiBuilder.addElement( table, "", 15, 145, PANEL_WIDTH-30, 35 );
        this.uiBuilder.addElement( UiElement.JBUTTON, VERIFY_STOCK_VARIATIONS, PANEL_WIDTH/2-125, 200, 250, 20, "VERIFY THE STOCK VARIATIONS" );
        
        UiUtil.addActionListeners( this );
        this.dialog = new JDialog( frame, title, true );
        this.dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        this.dialog.setBounds( 50, 50, PANEL_WIDTH, PANEL_HEIGHT );
        this.dialog.add( this );
        this.dialog.setVisible( true );
        this.dialog.setResizable( true );
        this.dialog.setLayout( null );
        
        this.validate();
        this.repaint();
	}
	
	protected void validatePanel() throws UIValidationException
    {
	    SwingUtil.validatePanelComponents( this );
    }
	
	public void actionPerformed( final ActionEvent actionEvent )
	{
		if ( actionEvent.getSource().equals( this.getComp( VERIFY_STOCK_VARIATIONS, JButton.class ) ) )
		{
			this.getComp(VERIFY_STOCK_VARIATIONS, JButton.class).setText("STOCK VARIATIONS VERIFIED");
			this.getComp(VERIFY_STOCK_VARIATIONS, JButton.class).setEnabled(false);
			loadMonthlySalaryDetails();
		}
		else if ( actionEvent.getSource().equals( this.getComp( SUBMIT_BUTTON, JButton.class ) ) )
		{
		    if (submitPanel())
		    {
		    	this.parentPanel.submitStatement(this.employeeSalaryTable.getData());
	            this.dialog.dispose();
		    }
		}
		this.validate();
		this.repaint();
	}
	
	private void loadMonthlySalaryDetails()
	{
		this.uiBuilder.addElement( UiElement.JLABEL, "", 15, 230, 300, 25,
        		"Employee salaries.", UiUtil.LABEL_BOLD_FONT );
		this.employeeSalaryTable = new AbstractDataTable<EmpSalBean>(
				EmpSalBean.class, EntityConverter.convertToEmpSalBeans(
						this.empSalStatus, this.empTrxBeans));
		this.employeeSalaryTable.getModel().addTableModelListener( this );
		final JScrollPane employeeSalaryScrollPane = new JScrollPane( employeeSalaryTable );
		this.uiBuilder.addElement( employeeSalaryScrollPane, SAL_TABLE, 15, 260, PANEL_WIDTH-30, 180 );
		this.uiBuilder.addElement( UiElement.JBUTTON, SUBMIT_BUTTON, PANEL_WIDTH/2-100, 450, 200, 60, "VERIFY SALARIES" );
		getComp(SUBMIT_BUTTON, JButton.class).addActionListener(this);
		computeTotalSalaryForMonth();
	}
	
	private void computeTotalSalaryForMonth()
	{
		if (this.employeeSalaryTable != null)
		{
			BigDecimal totalSal = BigDecimal.ZERO;
			BigDecimal totalInce = BigDecimal.ZERO;
			for (EmpSalBean bean: this.employeeSalaryTable.getData() )
			{
				totalInce = totalInce.add(bean.getIncentives());
				totalSal = totalSal.add(bean.getSalThisMonth());
			}
			getComp(SUBMIT_BUTTON, JButton.class).setText( String.format(
					"<html><center><font size=-1>VERIFY MONTHLY SALARY<br/>" +
					"TOTAL SALARY:<b>%1$S</b><br/>" +
					"TOTAL INCENTIVES:<b>%2$S</b></font></center></html>",
					totalSal.toPlainString(), totalInce.toPlainString()));
		}
	}

	public void tableChanged( final TableModelEvent e)
	{
		computeTotalSalaryForMonth();
		this.validate();
		this.repaint();
	}
}
