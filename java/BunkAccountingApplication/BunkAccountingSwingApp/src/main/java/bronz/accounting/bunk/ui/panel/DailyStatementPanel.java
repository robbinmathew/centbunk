package bronz.accounting.bunk.ui.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import bronz.accounting.bunk.model.SavedDailyStatement;
import bronz.accounting.bunk.tankandmeter.model.MeterClosingReading;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;
import bronz.accounting.bunk.ui.party.model.*;
import bronz.utilities.general.Pair;
import bronz.utilities.general.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.model.ClosingStatement;
import bronz.accounting.bunk.party.dao.PartyDao;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.party.model.Settlement;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.reports.JRReportsCreator;
import bronz.accounting.bunk.reports.exception.ReportException;
import bronz.accounting.bunk.reports.model.ReportFormat;
import bronz.accounting.bunk.reports.util.ReportGeneratorHelper;
import bronz.accounting.bunk.tankandmeter.model.TankTransaction;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.product.model.FuelSalesProdDetailBean;
import bronz.accounting.bunk.ui.product.model.LubeSalesProdDetailBean;
import bronz.accounting.bunk.ui.product.model.MeterSalesBean;
import bronz.accounting.bunk.ui.product.model.TankSaleDetailBean;
import bronz.accounting.bunk.ui.util.EntityConverter;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.accounting.bunk.util.EntityTransactionBuilder;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.DateUtil;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.custom.ListCellEditor;
import bronz.utilities.swing.custom.ValidatableTextField.ValidationType;
import bronz.utilities.swing.table.AbstractDataTable;
import bronz.utilities.swing.table.TableWithinTableCell;
import bronz.utilities.swing.util.SwingUtil;
import bronz.utilities.swing.util.UIValidationException;

@SuppressWarnings("unchecked")
public class DailyStatementPanel extends BasePublicPanel
		implements ActionListener, TableModelListener, KeyListener
{
	/** Serial Number.*/
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(
            DailyStatementPanel.class );
    private static final String REPORTS_BUTTON = "REPORTS_POPUP_BUTTON";
    private static final String ADD_PARTY_BUTTON = "ADD_PARTY_BUTTON";
    private static final String METER_SALES_TABLE = "METER_DETAILS";
    private static final String OFFICE_CASH_TABLE = "OFFICE_CASH_TABLE";
    private static final String TANK_SALES_TABLE = "TANK_SALES_PANEL";
    private static final String FUEL_PROD_TABLE = "FUEL_PROD_TABLE";
    private static final String LUBE_PROD_TABLE = "LUBE_PROD_TABLE";
    private static final String PARTY_TRANS_TABLE = "PARTY_TRANS_TABLE";
    private static final String EMPLOYEE_TRANS_TABLE = "EMPLOYEE_TRANS_TABLE";
    private static final String SINGLE_SCROLL_PANEL = "SINGLE_SCROLL_PANEL";
    private static final String OP_BALANCE = "OPENING BAL";
    private static final String CL_BALANCE = "CLOSING BAL";
    private static final String SAVE_BUTTON = "SAVE_BUTTON";
    private static final String CLEAR_BUTTON = "CLEAR_BUTTON";
    private static final String SUBMIT_BUTTON = "SUBMIT_BUTTON";
    private final SimpleDateFormat TEXT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final JRReportsCreator reportsCreator;
    private final Integer offCashPartyId = AppConfig.OFFICE_CASH_PARTY_ID.getValue( Integer.class );
    private final Integer offChequePartyId = AppConfig.OFFICE_CHEQUE_PARTY_ID.getValue( Integer.class );
    private final Integer expensePartyId = AppConfig.EXPENSES_PARTY_ID.getValue( Integer.class );
    private final Integer defaultBankPartyId = AppConfig.DEFAULT_BANK_PARTY_ID.getValue( Integer.class );
    private Settlement previousDateSettlement;
    private ClosingStatement savedStatement;
    private List<ProductClosingBalance> secondaryProducts;
    private List<PartyClosingBalance> parties;
    private List<EmployeeMonthlyStatus> employees;
    private SavedDailyStatement savedDailyStatement;
    private String panelStatus = null;
	
	public DailyStatementPanel( final HomePage parentFrame ) throws BunkMgmtException
	{
	    super( parentFrame, "Daily Statement");
	    LOG.debug( "Launching the daily statement panel" );
	    this.reportsCreator = BunkAppInitializer.getInstance().getReportsCreator();
	    
	    initPanel();
	    postLaunchTasks();
		
		UiUtil.addActionListeners( this );
	}
	
	public void postRenderTasks()
	{
		setSingleScrollPanelPreferedHeight();
	}
	
	private void setSingleScrollPanelPreferedHeight()
	{
		int tableHeight = 0;
		tableHeight += getTableHeight(OFFICE_CASH_TABLE);
		tableHeight += getTableHeight(METER_SALES_TABLE);
		tableHeight += getTableHeight(TANK_SALES_TABLE);
		tableHeight += getTableHeight(FUEL_PROD_TABLE);
		tableHeight += getTableHeight(LUBE_PROD_TABLE);
		tableHeight += getTableHeight(PARTY_TRANS_TABLE);
		tableHeight += getTableHeight(EMPLOYEE_TRANS_TABLE);
		
		final JPanel panel = this.getComp(SINGLE_SCROLL_PANEL, JPanel.class);
		panel.setPreferredSize(new Dimension(panel.getWidth(), tableHeight));
	}
	
	private void postLaunchTasks()
	{
        /*if(savedDailyStatement != null){
            int savedTranxCount = 0;
            savedTranxCount = savedTranxCount + savedDailyStatement.getEmployeeTransactionsMap().size();
            savedTranxCount = savedTranxCount + savedDailyStatement.getCreditedChequeDetails().size();
            savedTranxCount = savedTranxCount + savedDailyStatement.getPartyTransactionsMap().size();
            savedTranxCount = savedTranxCount + savedDailyStatement.getProductSalesMap().size();
            savedTranxCount = savedTranxCount + savedDailyStatement.getTankStockMap().size();
            savedTranxCount = savedTranxCount + savedDailyStatement.getMeterMap().size();
            savedTranxCount = savedTranxCount + savedDailyStatement.getOfficeCashMap().size();
            if(savedTranxCount>0){
                UiUtil.confirmUserChoiceWithCustomOptions(this, "Loaded the transactions from the statement saved on :" + savedDailyStatement.getMessage(), "OK", "OK");
            }
        }*/
	    if ( DateUtil.isLastDayOfMonth( this.todayInteger + 2 ) || DateUtil.isLastDayOfMonth( this.todayInteger + 1 ) )
        {
	        SwingUtil.notifyInfoMsg( this, "Last 3 days of this month. Please pay the employee salaries before end of the month." );
        }
	    else if ( DateUtil.isLastDayOfMonth( this.todayInteger ) )
        {
            SwingUtil.notifyInfoMsg( this, "Last day of this month. Please complete paying the employee salaries today." );
        }
	}
	
	@Override
    public String getTitleSuffix()
    {
        return "Daily statement";
    }
	
	private void initPanel() throws BunkMgmtException
	{
	    PartyDetailBean.CLEAR_BALANCES();
	    final String submitButtonText;
	    if (DateUtil.isLastDayOfMonth(this.todayInteger))
	    {
	    	submitButtonText = "<html><center><font size=-0>PROCEED TO<br/>MONTHLY CLOSING</font></center></html>";
	    }
	    else
	    {
	    	submitButtonText = "SUBMIT";
	    }
	    this.uiBuilder.addSubOptionFields(
                UiElement.JTEXTFIELD, OP_BALANCE, ValidationType.DISABLED, 20,
                UiElement.JTEXTFIELD, CL_BALANCE, ValidationType.DISABLED, 20,
                UiElement.JBUTTON, CLEAR_BUTTON, "CLEAR", 15,
                UiElement.JBUTTON, SAVE_BUTTON, "SAVE", 15,
                UiElement.JBUTTON, SUBMIT_BUTTON, submitButtonText, 25 );
	    this.previousDateSettlement = this.bunkManager.getSettlement( todayInteger -1 );
	    this.parties = this.bunkManager.getPartyList( PartyDao.ALL_ACTIVE_NON_EMPLOYEE_PARTIES );
	    this.employees = this.bunkManager.getEmployeesStatus();
	    this.savedStatement = this.bunkManager.getClosingStatement( todayInteger );
        this.savedDailyStatement = this.bunkManager.getSavedDailyStatement(todayInteger);
    	    final List<PartyDetailBean> partySaleBeans = EntityConverter.convertToPartySales(
                this.bunkManager.getPartyList( PartyDao.ALL ),
                this.savedStatement.getPartyTransactions());
        final JTable partyTransTable = new AbstractDataTable<PartyDetailBean>( PartyDetailBean.class,
                partySaleBeans);
        final List<PartyTransaction> pendingCheques = this.bunkManager.getPendingChequesAtOffice();

	    final List<OfficeCashDetailBean> officeCashDetailBeans = EntityConverter.convertToOfficeCashDetailBeans(
	            this.bunkManager.getParty( offCashPartyId ), this.bunkManager.getParty( offChequePartyId ), todayInteger,
	            partySaleBeans, pendingCheques );
	    final List<MeterSalesBean> meterSalesBeans =
	            EntityConverter.convertToMeterSales( this.bunkManager.getActiveMeterList() );
	    final JTable meterSales = new AbstractDataTable<MeterSalesBean>(
	            MeterSalesBean.class, meterSalesBeans );
	    final JTable offCashTable = new AbstractDataTable<OfficeCashDetailBean>(
	            OfficeCashDetailBean.class, officeCashDetailBeans );
	    final List<TankSaleDetailBean> tankSalesBeans = EntityConverter.convertToTankSales(
                this.bunkManager.getTankList( this.todayInteger ), meterSalesBeans );
	    final JTable tankSales = new AbstractDataTable<TankSaleDetailBean>(
	            TankSaleDetailBean.class, tankSalesBeans);
	    final JTable fuelProdSales = new AbstractDataTable<FuelSalesProdDetailBean>(
	            FuelSalesProdDetailBean.class, EntityConverter.convertToFuelSales(
                        this.bunkManager.getProductList( ProductDao.FUEL_PRODUCTS ), tankSalesBeans ) );
	    this.secondaryProducts =
	            this.bunkManager.getAvailableProductList( ProductDao.SECONDARY );
	    
	    final JTable lubeProdSales = new AbstractDataTable<LubeSalesProdDetailBean>(
                LubeSalesProdDetailBean.class);
	    
	    final JTable employeeTrans = new AbstractDataTable<EmployeeTrxDetailBean>(
	    		EmployeeTrxDetailBean.class);
	    
	    this.uiBuilder.addDetailPanelsWithSingleScroll(
	    		SINGLE_SCROLL_PANEL,
	            OFFICE_CASH_TABLE, offCashTable,
	            METER_SALES_TABLE, meterSales,
	            TANK_SALES_TABLE, tankSales,
	            FUEL_PROD_TABLE, fuelProdSales,
	            LUBE_PROD_TABLE, lubeProdSales,
	            PARTY_TRANS_TABLE, partyTransTable,
	            EMPLOYEE_TRANS_TABLE, employeeTrans);
	    this.uiBuilder.addElement( UiElement.JBUTTON, ADD_PARTY_BUTTON, panelSize.width - 200, 10, 150, 25, "Add/Edit parties" );
	    this.uiBuilder.addElement( UiElement.JBUTTON, REPORTS_BUTTON, panelSize.width - 370, 10, 150, 25, "Reports" );
        this.getComp( LUBE_PROD_TABLE, JTable.class ).getColumnModel().getColumn( 0 ).setCellEditor(
                new ListCellEditor( this.secondaryProducts ) );
        
        this.getComp( PARTY_TRANS_TABLE, JTable.class ).getColumnModel().getColumn( 0 ).setCellEditor(
                new ListCellEditor( parties ) );
        this.getComp( EMPLOYEE_TRANS_TABLE, JTable.class ).getColumnModel().getColumn( 0 ).setCellEditor(
                new ListCellEditor( employees ) );
        this.getComp( PARTY_TRANS_TABLE, JTable.class ).getColumnModel().getColumn( 1 ).setCellRenderer(
	    		new TableWithinTableCell(PartyDetailBean.ROWS, 20));
	    this.getComp( PARTY_TRANS_TABLE, JTable.class ).getColumnModel().getColumn( 1 ).setCellEditor(
	    		new TableWithinTableCell(PartyDetailBean.ROWS,20));
	    this.getComp( PARTY_TRANS_TABLE, JTable.class ).setRowHeight(45);
	    
	    this.getComp( EMPLOYEE_TRANS_TABLE, JTable.class ).getColumnModel().getColumn( 1 ).setCellRenderer(
	    		new TableWithinTableCell(EmployeeTrxDetailBean.ROWS, 15));
	    this.getComp( EMPLOYEE_TRANS_TABLE, JTable.class ).getColumnModel().getColumn( 1 ).setCellEditor(
	    		new TableWithinTableCell(EmployeeTrxDetailBean.ROWS,15));
	    this.getComp( EMPLOYEE_TRANS_TABLE, JTable.class ).getColumnModel().getColumn( 4 ).setCellRenderer(
	    		new TableWithinTableCell(EmployeeTrxDetailBean.ROWS, 30));
	    this.getComp( EMPLOYEE_TRANS_TABLE, JTable.class ).setRowHeight(30);
        
	    this.getComp( METER_SALES_TABLE, JTable.class ).getModel().addTableModelListener( this );
	    this.getComp( TANK_SALES_TABLE, JTable.class ).getModel().addTableModelListener( this );
	    this.getComp( FUEL_PROD_TABLE, JTable.class ).getModel().addTableModelListener( this );
	    this.getComp( LUBE_PROD_TABLE, JTable.class ).getModel().addTableModelListener( this );
	    this.getComp( OFFICE_CASH_TABLE, JTable.class ).getModel().addTableModelListener( this );
	    this.getComp( PARTY_TRANS_TABLE, JTable.class ).getModel().addTableModelListener( this );
	    this.getComp( EMPLOYEE_TRANS_TABLE, JTable.class ).getModel().addTableModelListener( this );
	    this.getComp( OP_BALANCE, JTextField.class ).setText( this.previousDateSettlement.getClosingBal().toPlainString() );
	    this.getComp( CL_BALANCE, JTextField.class ).setText( this.previousDateSettlement.getClosingBal().toPlainString() );
        loadSavedData();
	    updateClosingBalance();
	}

    private void loadSavedData() throws BunkMgmtException
    {
       /* if(this.savedDailyStatement != null){
            //Iterate through table and pick values from the map
            AbstractDataTable offCashTable = this.getComp( OFFICE_CASH_TABLE, AbstractDataTable.class );
            if(offCashTable != null && this.savedDailyStatement.getOfficeCashMap() != null ){
                int idCol = 0;
                int firstValCol = 2;
                int secValCol = 3;
                for(int i=0;i<offCashTable.getRowCount();i++){
                    Object rowIdBean = offCashTable.getValueAt(i,idCol);
                    if( rowIdBean != null && rowIdBean instanceof PartyClosingBalance){
                        PartyClosingBalance bean = (PartyClosingBalance)rowIdBean;
                        if(offChequePartyId == bean.getId()){
                            if(!this.savedDailyStatement.getCreditedChequeDetails().isEmpty()){
                                //Office Cheque
                                OfficeCashDetailBean chequeBean = (OfficeCashDetailBean) offCashTable.getData().get(i);
                                List<PartyTransaction> pendingCheques = chequeBean.getPendingCheques(); //Pending cheque transactions in database
                                List<ChequeTransWrapper> creditedCheques = new ArrayList<ChequeTransWrapper>();
                                for(Triple<Integer, BigDecimal, String> savedCheque : this.savedDailyStatement.getCreditedChequeDetails()){
                                    for(PartyTransaction pendingCheque : pendingCheques){
                                        if(pendingCheque.getPartyId() == savedCheque.getFirst() && pendingCheque.getAmount().equals(savedCheque.getSecond())){
                                            ChequeTransWrapper chequeTransWrapper = new ChequeTransWrapper(pendingCheque);
                                            chequeTransWrapper.setCreditDetail(savedCheque.getThird());
                                            chequeTransWrapper.setSelectedForCredit(ChequeTransWrapper.CREDITED_TODAY);
                                            creditedCheques.add(chequeTransWrapper);
                                            break;
                                        }
                                    }
                                }
                                chequeBean.setCreditedCheques(creditedCheques);
                            }

                            // Dont set other values for cheque
                            //Pair<BigDecimal,BigDecimal> values = this.savedDailyStatement.getOfficeCashMap().get(bean.getId());
                            //if(values != null){
                            //    offCashTable.setValueAt(values.getFirst(), i, firstValCol);
                            //    offCashTable.setValueAt(values.getSecond(), i, secValCol);
                            //}
                        } else {
                            Pair<BigDecimal,BigDecimal> values = this.savedDailyStatement.getOfficeCashMap().get(bean.getId());
                            if(values != null){
                                offCashTable.setValueAt(values.getFirst(), i, firstValCol);
                                offCashTable.setValueAt(values.getSecond(), i, secValCol);
                            }
                        }
                    }
                }
            }
            JTable meterTable = this.getComp( METER_SALES_TABLE, JTable.class );
            if(meterTable != null && this.savedDailyStatement.getMeterMap() != null ){
                int idCol = 0;
                int firstValCol = 2;
                int secValCol = 4;
                for(int i=0;i<meterTable.getRowCount();i++){
                    Object rowIdBean = meterTable.getValueAt(i,idCol);
                    if( rowIdBean != null && rowIdBean instanceof MeterClosingReading){
                        Pair<BigDecimal,BigDecimal> values = this.savedDailyStatement.getMeterMap().get(((MeterClosingReading)rowIdBean).getMeterId());
                        if(values != null){
                            meterTable.setValueAt(values.getFirst(), i, firstValCol);
                            meterTable.setValueAt(values.getSecond(), i, secValCol);
                        }
                    }
                }
            }

            JTable tankTable = this.getComp( TANK_SALES_TABLE, JTable.class );
            if(tankTable != null && this.savedDailyStatement.getTankStockMap() != null ){
                int idCol = 0;
                int firstValCol = 5;
                for(int i=0;i<tankTable.getRowCount();i++){
                    Object rowIdBean = tankTable.getValueAt(i,idCol);
                    if( rowIdBean != null && rowIdBean instanceof TankClosingStock){
                        BigDecimal value = this.savedDailyStatement.getTankStockMap().get(((TankClosingStock)rowIdBean).getTankId());
                        if(value != null){
                            tankTable.setValueAt(value, i, firstValCol);
                        }
                    }
                }
            }

            AbstractDataTable prodsTable = this.getComp( LUBE_PROD_TABLE, AbstractDataTable.class );
            if(prodsTable != null && this.savedDailyStatement.getProductSalesMap() != null ){
                for(Map.Entry<Integer,Pair<BigDecimal,BigDecimal>> entry : this.savedDailyStatement.getProductSalesMap().entrySet())
                {
                    int idCol = 0;
                    int firstValCol = 2;
                    int secValCol = 3;

                    int rowNo = prodsTable.getData().size(); // this will be next row
                    for(ProductClosingBalance productClosingBalance : this.secondaryProducts){
                        if(productClosingBalance.getProductId() == entry.getKey()){
                            prodsTable.setValueAt(productClosingBalance,rowNo, idCol);
                            prodsTable.setValueAt(entry.getValue().getFirst(),rowNo, firstValCol);
                            prodsTable.setValueAt(entry.getValue().getSecond(),rowNo, secValCol);
                        }
                    }
                }
            }
            AbstractDataTable partyTable = this.getComp( PARTY_TRANS_TABLE, AbstractDataTable.class );
            if(partyTable != null && this.savedDailyStatement.getPartyTransactionsMap() != null ){
                for(Map.Entry<Integer, Triple<Pair<String, BigDecimal>, Pair<String, BigDecimal>, Pair<String, BigDecimal>>> entry :
                        this.savedDailyStatement.getPartyTransactionsMap().entrySet())
                {
                    int idCol = 0;
                    int firstValCol = 1;
                    int secValCol = 2;
                    int thirdValCol = 3;
                    int forthValCol = 4;

                    int rowNo = partyTable.getData().size(); // this will be next row
                    for(PartyClosingBalance partyClosingBalance : this.parties){
                        if(partyClosingBalance.getId() == entry.getKey()){
                            partyTable.setValueAt(partyClosingBalance,rowNo, idCol);
                            Map<String, String> detailMap = new HashMap<String, String>();
                            detailMap.put(PartyDetailBean.DEBIT_CHQ,entry.getValue().getFirst().getFirst());
                            detailMap.put(PartyDetailBean.DEBIT,entry.getValue().getSecond().getFirst());
                            detailMap.put(PartyDetailBean.CREDIT,entry.getValue().getThird().getFirst());

                            partyTable.setValueAt(detailMap,rowNo, firstValCol);
                            partyTable.setValueAt(entry.getValue().getFirst().getSecond(),rowNo, secValCol);
                            partyTable.setValueAt(entry.getValue().getSecond().getSecond(),rowNo, thirdValCol);
                            partyTable.setValueAt(entry.getValue().getThird().getSecond(),rowNo, forthValCol);
                        }
                    }
                }
            }
            AbstractDataTable empTable = this.getComp( EMPLOYEE_TRANS_TABLE, AbstractDataTable.class );
            if(empTable != null && this.savedDailyStatement.getEmployeeTransactionsMap() != null ){
                for(Map.Entry<Integer, Pair<Pair<String, BigDecimal>, Pair<String, BigDecimal>>> entry :
                        this.savedDailyStatement.getEmployeeTransactionsMap().entrySet())
                {
                    int idCol = 0;
                    int firstValCol = 1;
                    int secValCol = 2;
                    int thirdValCol = 3;

                    int rowNo = empTable.getData().size(); // this will be next row
                    for(EmployeeMonthlyStatus employeeMonthlyStatus : this.employees){
                        if(employeeMonthlyStatus.getId() == entry.getKey()){
                            empTable.setValueAt(employeeMonthlyStatus,rowNo, idCol);
                            Map<String, String> detailMap = new HashMap<String, String>();
                            detailMap.put(EmployeeTrxDetailBean.SALARY_MSG,entry.getValue().getFirst().getFirst());
                            detailMap.put(EmployeeTrxDetailBean.INCE_MSG,entry.getValue().getSecond().getFirst());

                            empTable.setValueAt(detailMap,rowNo, firstValCol);
                            empTable.setValueAt(entry.getValue().getFirst().getSecond(),rowNo, secValCol);
                            empTable.setValueAt(entry.getValue().getSecond().getSecond(),rowNo, thirdValCol);
                        }
                    }
                }
            }
        }*/
    }
	
	private void repopulatePartyEditorAndData() throws BunkMgmtException
    {
	    this.parties = this.bunkManager.getPartyList( PartyDao.ALL_ACTIVE_NON_EMPLOYEE_PARTIES );
        this.getComp( PARTY_TRANS_TABLE, JTable.class ).getColumnModel().getColumn( 0 ).setCellEditor(
                new ListCellEditor( parties ) );
	    final Map<Integer, PartyClosingBalance> partyMap = new HashMap<Integer, PartyClosingBalance>();
        for ( PartyClosingBalance party : this.bunkManager.getPartyList( PartyDao.ALL ) )
        {
            partyMap.put( party.getId(), party );
        }
        final List<PartyDetailBean> updatedPartySaleBeans = new ArrayList<PartyDetailBean>();
        final List<Integer> updatedPartyIds = new ArrayList<Integer>();
        for ( PartyClosingBalance party : parties )
        {
            updatedPartyIds.add( party.getId() );
        }
        
	    for (PartyDetailBean currentBean :
	        (List<PartyDetailBean>) this.getComp( PARTY_TRANS_TABLE, AbstractDataTable.class ).getData() )
	    {
	        final Integer partyId = currentBean.getParty().getId();
	        if (!currentBean.isUpdated() || (currentBean.isUpdated() && updatedPartyIds.contains( partyId )))
	        {
	            currentBean.setParty( partyMap.get( partyId ) );
	            updatedPartySaleBeans.add( currentBean );
	        }
	    }
	    this.getComp( PARTY_TRANS_TABLE, AbstractDataTable.class ).getData().clear();
	    this.getComp( PARTY_TRANS_TABLE, AbstractDataTable.class ).getData().addAll( updatedPartySaleBeans );
	    PartyDetailBean.CLEAR_AND_ADD_ALL( updatedPartySaleBeans );
	    
	    
	    this.employees = this.bunkManager.getEmployeesStatus();
	    final List<EmployeeTrxDetailBean> updatedEmployeeBeans = new ArrayList<EmployeeTrxDetailBean>();
	    final List<Integer> updatedEmpIds = new ArrayList<Integer>();
        for ( EmployeeMonthlyStatus party : employees )
        {
        	updatedEmpIds.add( party.getId() );
        }
	    this.getComp( EMPLOYEE_TRANS_TABLE, JTable.class ).getColumnModel().getColumn( 0 ).setCellEditor(
                new ListCellEditor( employees ) );
	    
	    for (EmployeeTrxDetailBean currentBean :
	        (List<EmployeeTrxDetailBean>) this.getComp( EMPLOYEE_TRANS_TABLE, AbstractDataTable.class ).getData() )
	    {
	        final Integer partyId = currentBean.getParty().getId();
	        if (updatedEmpIds.contains( partyId ))
	        {
	            updatedEmployeeBeans.add( currentBean );
	        }
	    }
	    this.getComp( EMPLOYEE_TRANS_TABLE, AbstractDataTable.class ).getData().clear();
	    this.getComp( EMPLOYEE_TRANS_TABLE, AbstractDataTable.class ).getData().addAll( updatedEmployeeBeans );
	    
	    updateClosingBalance();
	    this.validate();
        this.repaint();
    }
	
	private void validateFields() throws UIValidationException
    {
        final StringBuilder errorMessages = new StringBuilder();
        final StringBuilder warningMessages = new StringBuilder();
        BigDecimal totalSale = CustomDecimal.ZERO;
        final List<MeterSalesBean> meterSaleBeans =  this.getComp(
                METER_SALES_TABLE, AbstractDataTable.class ).getData();
        for ( MeterSalesBean meterSaleBean : meterSaleBeans )
        {
            totalSale = totalSale.add( meterSaleBean.getTotalSales() );
        }
        if ( CustomDecimal.ZERO.compareTo( totalSale ) >= 0)
        {
            SwingUtil.appendMessage( warningMessages, "--No meter sale over meter today." );
        }
        final String closingBalance = this.getComp( CL_BALANCE, JTextField.class ).getText(); 
        if (ValidationUtil.isValidNumber( closingBalance ))
        {
            final BigDecimal closingBal = new BigDecimal( closingBalance );
            if (closingBal.compareTo( new BigDecimal( 3000 ) ) >= 0)
            {
                SwingUtil.appendMessage( warningMessages, "--Closing balance seems to be huge amount." );
            }
            else if (closingBal.compareTo( CustomDecimal.ZERO ) < 0)
            {
                SwingUtil.appendMessage( errorMessages, "--Closing balance is less than zero, verify the 'To office' amount." );
            }
        }
        SwingUtil.validatePanelComponents( this, errorMessages, warningMessages );
    }
	
    
	/**
	 * Implements the method of ActionListener interface.
	 */
	public void actionPerformed( final ActionEvent actionEvent )
	{
		try
        {
		    if( actionEvent.getSource().equals( this.componentMap.get( ADD_PARTY_BUTTON )))
	        {
                new EditPartiesPanel( this.parentFrame, new ActionListener()
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        try
                        {
                            repopulatePartyEditorAndData();
                        }
                        catch( Exception exception )
                        {
                            UiUtil.alertUnexpectedError( parentFrame, exception );
                        }
                    }
                });
	        }
		    else if( actionEvent.getSource().equals( this.componentMap.get( REPORTS_BUTTON )))
	        {
		    	new ReportsPanel( this.parentFrame, true );
	        }
		    else if( this.getComp( SUBMIT_BUTTON, JButton.class ) == actionEvent.getSource() )
			{
                panelStatus = SUBMIT_BUTTON + " Clicked";
			    boolean validationSuccess;
	            try
	            {
	                cancelTableCellEditing( METER_SALES_TABLE, TANK_SALES_TABLE, FUEL_PROD_TABLE,
	                        LUBE_PROD_TABLE, PARTY_TRANS_TABLE );
	                validateFields();
	                validationSuccess = true;
	            }
	            catch ( UIValidationException uiValidationExcepton)
	            {
	                validationSuccess = false;
	                UiUtil.alertValidationError( this.parentFrame,
	                        uiValidationExcepton.getMessage() );
	            }
	            if( validationSuccess )
	            {
                    saveRequest("Details submitted at " + TEXT_DATE_FORMAT.format(new Date()));
	            	if (DateUtil.isLastDayOfMonth(this.todayInteger))
	            	{
	            		new MonthlyClosingPanel( this.parentFrame, this,
	            				this.bunkManager.getStockVariation(this.todayInteger),
	            				this.getComp( TANK_SALES_TABLE, AbstractDataTable.class ).getData(),
	            				this.getComp( EMPLOYEE_TRANS_TABLE, AbstractDataTable.class ).getData());
	            	}
	            	else
	            	{
	            		submitStatement(null);
	            	}
	            }
			}
            else if( this.getComp( SAVE_BUTTON, JButton.class ) == actionEvent.getSource() )
            {
                boolean validationSuccess;
                try
                {
                    cancelTableCellEditing( METER_SALES_TABLE, TANK_SALES_TABLE, FUEL_PROD_TABLE,
                            LUBE_PROD_TABLE, PARTY_TRANS_TABLE );
                    validationSuccess = true;
                }
                catch ( UIValidationException uiValidationExcepton)
                {
                    validationSuccess = false;
                    UiUtil.alertValidationError( this.parentFrame,
                            uiValidationExcepton.getMessage() );
                }
                if( validationSuccess )
                {
                    //Save
                    saveRequest("Saved at " + TEXT_DATE_FORMAT.format(new Date()));
                }
            }
            else if( this.getComp( CLEAR_BUTTON, JButton.class ) == actionEvent.getSource() )
            {
                if ( 0 == SwingUtil.confirmUserChoice( parentFrame, "Warning.!! \n" +
                        "This will clear all the saved transactions/data added to this statement. You need to add all the transactions again. \n" +
                        " \n Are you sure you want to continue??" ))
                {
                    // Delete saved details from DB
                    this.bunkManager.deleteSavedDailyStatement(todayInteger);
                    // Load again.
                    this.parentFrame.setPublicPanel(new DailyStatementPanel(this.parentFrame));
                }


            }
        }
        catch ( Exception exception)
        {
            UiUtil.alertUnexpectedError( parentFrame, exception );
        }
		this.validate();
		this.repaint();
	}
	
	public void submitStatement(final List<EmpSalBean> salBeansForMonthlyClosing)
	{
		try
		{
			synchronized( DailyStatementPanel.class )
            {
                processRequest(salBeansForMonthlyClosing);
                postSubmitTasks();
            }
			UiUtil.alertOperationCompleteWithTime(this.parentFrame , "Statement saved successfully",
					this.launchTime );
        
			BunkAppInitializer.getInstance().reInitialize();
			this.parentFrame.initialize();
			this.parentFrame.setDefaultPublicPanel();
			spawnReportThread();
		}
		catch (Exception e)
		{
			throw new IllegalStateException( "Failed to save the statement", e);
		}
	}
	
	private void spawnReportThread()
	{
		new Thread()
        {
            public void run()
            {
                try
                {
                    final String fileName = ReportGeneratorHelper.getFileName(
                    	"DAILY_STATEMENT", todayInteger, ReportFormat.PDF.getExtension(),
                    	AppConfig.EMAIL_SERVICE_REPORT_FOLDER_PROP_NAME.getStringValue() );
                    reportsCreator.createClosingStatement(todayInteger).exportToFile(
                    		ReportFormat.PDF, fileName);
                    UiUtil.showReportGeneratedAlert(fileName, launchTime, parentFrame );
                }
                catch( ReportException e )
                {
                    // LOG error and escape!! :P
                    LOG.error( "Failed to generate the daily statement report", e );
                }
                
                if(DateUtil.isMondayOrThursday(todayInteger))
                {
	                try
	                {
	                	final String fileName = ReportGeneratorHelper.getFileName(
	                        	"MONTHLY_SUMMARY", todayInteger, ReportFormat.PDF.getExtension(),
	                        	AppConfig.EMAIL_SERVICE_REPORT_FOLDER_PROP_NAME.getStringValue() ); 
	                	reportsCreator.createMonthlyCashSummaryStatement(todayInteger,
	                			(todayInteger + 1)).exportToFile(
	                			ReportFormat.PDF, fileName);
	                }
	                catch( ReportException e )
	                {
	                    // LOG error and escape!! :P
	                    LOG.error( "Failed to generate the monthly summary report", e );
	                }
                }
            }
        }.start();
	}

    private void spawnAutoSaveThread()
    {
        new Thread()
        {
            public void run()
            {
                while (true){
                    if(panelStatus == null){
                        try
                        {
                            Thread.sleep(120000);
                            saveRequest("Auto save at " + TEXT_DATE_FORMAT.format(new Date()));
                        }
                        catch( Exception e )
                        {
                            // LOG error and escape!! :P
                            LOG.error( "Error in ", e );
                        }
                    } else {
                        break;
                    }
                }
            }
        }.start();
    }

    protected void saveRequest(String message) throws BunkMgmtException
    {
        /*final SavedDailyStatement savedDailyStatement = new SavedDailyStatement();
        savedDailyStatement.setDate(todayInteger);
        savedDailyStatement.setMessage(message);

        final List<MeterSalesBean> meterSaleBeans =  this.getComp(
                METER_SALES_TABLE, AbstractDataTable.class ).getData();
        for ( MeterSalesBean meterSaleBean : meterSaleBeans )
        {
            if(meterSaleBean.getClosingReading().compareTo(meterSaleBean.getOpeningReading()) >0){
                savedDailyStatement.addMeterTransaction(meterSaleBean.getMeter().getMeterId(),
                        meterSaleBean.getClosingReading(), meterSaleBean.getTestLitres());
            }
        }
        final List<TankSaleDetailBean> tankSaleBeans =  this.getComp(
                TANK_SALES_TABLE, AbstractDataTable.class ).getData();
        for ( TankSaleDetailBean tankSale : tankSaleBeans )
        {
            //Consider TANK STOCK DIFF only if dipstock is provided.
            if (tankSale.getDipStock().compareTo( CustomDecimal.ZERO ) > 0)
            {
                savedDailyStatement.addTankTransaction(tankSale.getTankId(),
                        tankSale.getDipStock() );
            }
        }
        final List<LubeSalesProdDetailBean> lubeBeans  =
                this.getComp( LUBE_PROD_TABLE, AbstractDataTable.class ).getData();
        for (LubeSalesProdDetailBean bean : lubeBeans)
        {
            savedDailyStatement.addProductSaleTransaction(bean.getProduct().getProductId(),
                    bean.getSaleAmount(), bean.getDiscountPerUnit());

        }
        final List<PartyDetailBean> partyBeans  =
                this.getComp( PARTY_TRANS_TABLE, AbstractDataTable.class ).getData();
        for (PartyDetailBean bean : partyBeans)
        {
            if (bean.isUpdated())
            {
                savedDailyStatement.addPartyTransaction(bean.getParty().getId(),
                        bean.getDebitByChqDetail(), bean.getDebitByCheque(),
                        bean.getDebitDetail(), bean.getDebit(),
                        bean.getCreditDetail(), bean.getCredit());
            }
        }

        final List<EmployeeTrxDetailBean> employeeBeans  =
                this.getComp( EMPLOYEE_TRANS_TABLE, AbstractDataTable.class ).getData();
        for (EmployeeTrxDetailBean bean : employeeBeans)
        {
            savedDailyStatement.addEmployeeTransaction(bean.getParty().getId(),
                    bean.getSalDetails(), bean.getSalary(),
                    bean.getInceDetails(), bean.getIncentive());
        }
        final List<OfficeCashDetailBean> officeCashDetailBeans = this.getComp( OFFICE_CASH_TABLE,
                AbstractDataTable.class ).getData();
        for ( OfficeCashDetailBean bean : officeCashDetailBeans )
        {
            if(bean.getAmountToday().compareTo(BigDecimal.ZERO)>0 ||
                    bean.getPaidToBank().compareTo(BigDecimal.ZERO)>0 ){
                if(null != bean.getCreditedCheques()){
                    for(ChequeTransWrapper chqTrans: bean.getCreditedCheques()){
                        savedDailyStatement.addChequeCreditedDetail(chqTrans.getPartyTransaction().getPartyId(), chqTrans.getAmount(), chqTrans.getCreditDetail());
                    }
                }

                savedDailyStatement.addOfficeTransaction(bean.getParty().getId(),
                        bean.getAmountToday(), bean.getPaidToBank());
            }
        }
        this.bunkManager.saveSavedDailyStatement(savedDailyStatement);*/
    }

	
    protected void processRequest(final List<EmpSalBean> salBeansForMonthlyClosing)
    		throws BunkMgmtException
    {
        final EntityTransactionBuilder transBuilder = new EntityTransactionBuilder( bunkManager, todayInteger );
        final ClosingStatement statement = new ClosingStatement();
        final Settlement settlement = new Settlement();
        BigDecimal totalDiscount = CustomDecimal.ZERO;
        
        final List<MeterSalesBean> meterSaleBeans =  this.getComp(
                METER_SALES_TABLE, AbstractDataTable.class ).getData();
        for ( MeterSalesBean meterSaleBean : meterSaleBeans )
        {
            transBuilder.getMeterTransactionBuilder().addTrans( meterSaleBean.getMeter().getMeterId(),
                    meterSaleBean.getTotalSales(), "Test sale:" + meterSaleBean.getTestLitres(), "SALE" );
        }
        final List<TankSaleDetailBean> tankSaleBeans =  this.getComp(
                TANK_SALES_TABLE, AbstractDataTable.class ).getData();
        final Map<Integer, BigDecimal> stockVariations = new HashMap<Integer, BigDecimal>();
        for ( TankSaleDetailBean tankSale : tankSaleBeans )
        {
            transBuilder.getTankTransBuilder().addTrans( tankSale.getTankId(),
                    tankSale.getTotalSale(), "", TankTransaction.SALE );
            if (tankSale.getTest().compareTo( CustomDecimal.ZERO ) > 0)
            {
                transBuilder.getTankTransBuilder().addTrans( tankSale.getTankId(),
                        tankSale.getTest(), "", TankTransaction.TEST );
            }
            
            //Consider TANK STOCK DIFF only if dipstock is provided.
            if (tankSale.getDipStock().compareTo( CustomDecimal.ZERO ) > 0)
            {
            	transBuilder.getTankTransBuilder().addTrans( tankSale.getTankId(),
                        tankSale.getStockDiff(), "", TankTransaction.DIFF );
            }
            
            
            //Override the product stock with the actual
            if (DateUtil.isLastDayOfMonth(this.todayInteger))
        	{
            	transBuilder.getProdTransBuilder().addTrans( tankSale.getProdId(),
                		tankSale.getCumDiffThisMonth(), "Stock diff for tank " +
                				tankSale.getTank().getTankName(), ProductTransaction.DIFF );
            	if (stockVariations.get(tankSale.getProdId()) == null)
            	{
            		stockVariations.put(tankSale.getProdId(), BigDecimal.ZERO);
            	}
            	stockVariations.put(tankSale.getProdId(), stockVariations.get(
            			tankSale.getProdId()).add(tankSale.getCumDiffThisMonth()));
        	}
        }
        final List<FuelSalesProdDetailBean> fuelBeans  =
            this.getComp( FUEL_PROD_TABLE, AbstractDataTable.class ).getData();
        for (FuelSalesProdDetailBean bean : fuelBeans)
        {
            transBuilder.getProdTransBuilder().addTrans( bean.getProduct().getProductId(),
                        bean.getSaleAmt(), "", "SALE" );
            final BigDecimal stockVariation = stockVariations.get(bean.getProduct().getProductId());
            if (null != stockVariation)
            {
            	transBuilder.getPartyTransBuilder().addTrans( expensePartyId,
            			BunkUtil.setAsPrice(stockVariation.multiply(bean.getRate())),
            			bean.getProduct().getProductName() + ":Stock variation this month(Litres):" +
            			BunkUtil.setAsProdVolume(stockVariation), "DEBIT_S" );
            }
        }
        final List<LubeSalesProdDetailBean> lubeBeans  =
            this.getComp( LUBE_PROD_TABLE, AbstractDataTable.class ).getData();
        for (LubeSalesProdDetailBean bean : lubeBeans)
        {
            if (bean.getSaleAmount().compareTo( CustomDecimal.ZERO ) > 0)
            {
                if (bean.getDiscountPerUnit().compareTo( CustomDecimal.ZERO ) > 0)
                {
                    totalDiscount = totalDiscount.add( bean.getDiscountPerUnit().multiply( bean.getSaleAmount() ) );
                }
                transBuilder.getProdTransBuilder().addTransWithDiscount( bean.getProduct().getProductId(),
                        bean.getSaleAmount(), bean.getDiscountPerUnit(), "SALE" );
            }
        }
        final List<PartyDetailBean> partyBeans  =
            this.getComp( PARTY_TRANS_TABLE, AbstractDataTable.class ).getData();
        for (PartyDetailBean bean : partyBeans)
        {
            if (bean.isUpdated())
            {
                if (bean.getCredit().compareTo( CustomDecimal.ZERO ) > 0)
                {
                    transBuilder.getPartyTransBuilder().addTrans( bean.getParty().getId(),
                            bean.getCredit(), bean.getCreditDetail(), PartyTransaction.CREDIT );
                }
                if (bean.getDebit().compareTo( CustomDecimal.ZERO ) > 0)
                {
                    transBuilder.getPartyTransBuilder().addTrans( bean.getParty().getId(),
                            bean.getDebit(), bean.getDebitDetail(), PartyTransaction.DEBIT );
                }
                if (bean.getDebitByCheque().compareTo( CustomDecimal.ZERO ) > 0)
                {
                    transBuilder.getPartyTransBuilder().addTrans( bean.getParty().getId(),
                            bean.getDebitByCheque(), "BY CHQ:" + bean.getDebitByChqDetail(), PartyTransaction.CHEQUE_DEBIT );
                }
            }
        }
        
        final List<EmployeeTrxDetailBean> employeeBeans  =
                this.getComp( EMPLOYEE_TRANS_TABLE, AbstractDataTable.class ).getData();
        for (EmployeeTrxDetailBean bean : employeeBeans)
        {
            if (bean.getSalary().compareTo( CustomDecimal.ZERO ) > 0)
            {
                transBuilder.getPartyTransBuilder().addTrans( bean.getParty().getId(),
                        bean.getSalary(), "SALARY:" + bean.getSalDetails(), PartyTransaction.CREDIT_SALARY );
            }
            if (bean.getIncentive().compareTo( CustomDecimal.ZERO ) > 0)
            {
                transBuilder.getPartyTransBuilder().addTrans( bean.getParty().getId(),
                        bean.getIncentive(), "INCENTIVE:" +bean.getInceDetails(), PartyTransaction.CREDIT_INCENTIVE );
            }
        }
        final List<OfficeCashDetailBean> officeCashDetailBeans = this.getComp( OFFICE_CASH_TABLE,
                AbstractDataTable.class ).getData();
        for ( OfficeCashDetailBean bean : officeCashDetailBeans )
        {
            transBuilder.getPartyTransBuilder().addTrans( bean.getParty().getId(), bean.getAmountToday(), "To office",
                    "CREDIT_S" );
            if ( this.offChequePartyId != bean.getParty().getId() && bean.getPaidToBank().compareTo( CustomDecimal.ZERO ) > 0)
            {
                transBuilder.getPartyTransBuilder().addSwapTrans( bean.getParty().getId(), defaultBankPartyId,
                        bean.getPaidToBank(), "Cash paid to bank", "Cash deposit:" + bean.getParty().getName(), "DEBIT_S", "CREDIT_S" );
            } else if ( this.offChequePartyId == bean.getParty().getId() && bean.getPaidToBank().compareTo( CustomDecimal.ZERO ) > 0 &&
                    null != bean.getCreditedCheques())
            {
                for(ChequeTransWrapper chqTrans: bean.getCreditedCheques()){
                    transBuilder.getPartyTransBuilder().addSwapTrans( this.offChequePartyId, defaultBankPartyId,
                            chqTrans.getAmount(), PartyTransaction.CHEQUE_CREDIT_TRANS_DETAIL_PREFIX +
                            chqTrans.getPartyTransaction().getSlNo() + ":"  + chqTrans.getCreditDetail(),
                            "Cheque credited:" + chqTrans.getPartyName(), "DEBIT_S", "CREDIT_S" );
                }
            }
        }
        
        if (CustomDecimal.ZERO.compareTo( totalDiscount ) < 0)
        {
            transBuilder.getPartyTransBuilder().addTrans( expensePartyId, totalDiscount,
                    "Discount to lubes and battery water", "CREDIT" );
        }
        
        if (salBeansForMonthlyClosing != null)
        {
        	//Monthly closing
        	final String monthText = DateUtil.getDateYearMonthString( this.todayInteger );
            LOG.info( "Adding salary transactions.." );
            
            final Map<Integer, BigDecimal> employeeIdToAdvSalMap = new HashMap<Integer, BigDecimal>();
            BigDecimal totalSalaries = BigDecimal.ZERO;
            BigDecimal totalAdvSalaries = BigDecimal.ZERO;
            BigDecimal totalIncentives = BigDecimal.ZERO;
            for (EmpSalBean salBean : salBeansForMonthlyClosing )
            {
            	//Add negative amount for advance salary
                if (BigDecimal.ZERO.compareTo( salBean.getSalAdvance() ) < 0)
                {
                	employeeIdToAdvSalMap.put(salBean.getId(), salBean.getSalAdvance());
                	totalAdvSalaries = totalAdvSalaries.add( salBean.getSalAdvance() );
                    transBuilder.getPartyTransBuilder().addTrans( salBean.getId(), salBean.getSalAdvance().negate(),
                    		"Advance salary recieved in month " + monthText, PartyTransaction.CREDIT_SALARY );
                }
                //Add amount for salary
                if (BigDecimal.ZERO.compareTo( salBean.getSalThisMonth() ) < 0)
                {
                	totalSalaries = totalSalaries.add( salBean.getSalThisMonth() );
                    transBuilder.getPartyTransBuilder().addTrans( salBean.getId(), salBean.getSalThisMonth(),
                    		"Salary recieved in month " + monthText, PartyTransaction.DEBIT_SYSTEM );
                }
                //Add incentives
                if (BigDecimal.ZERO.compareTo( salBean.getIncentives() ) < 0)
                {
                	totalIncentives = totalIncentives.add( salBean.getIncentives() );
                    transBuilder.getPartyTransBuilder().addTrans( salBean.getId(), salBean.getIncentives(),
                    		"Incentives recieved in month " + monthText, PartyTransaction.DEBIT_SYSTEM );
                }
            }
            // Add the ADVANCE SALARY paid this month to expense to balance the settlement.
            if (BigDecimal.ZERO.compareTo( totalAdvSalaries ) < 0)
            {
            	transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
            			totalAdvSalaries, "Advance salaries paid this month " + monthText, PartyTransaction.CREDIT );
            }
            // Add the total salary and incentives to expenses
            transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
            		totalSalaries, "Total salary paid on month " + monthText, PartyTransaction.CREDIT_SYSTEM );
            transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
            		totalIncentives, "Total incentives paid on month " + monthText, PartyTransaction.CREDIT_SYSTEM );
            
            // Close the expenses for this month.
            transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
            		transBuilder.getPartyTransBuilder().getBalanceForEntity(this.expensePartyId),
            		"Total expenses for month " + monthText, PartyTransaction.DEBIT_SYSTEM );
            
            if (BigDecimal.ZERO.compareTo( totalAdvSalaries ) < 0)
            {
	            // Add the ADVANCE SALARY paid this month to expense to balance the settlement.
	            transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
	            		totalAdvSalaries, "Advance salaries paid this month " + monthText, PartyTransaction.CREDIT );
            }
            
            // #### FOR NEXT DAY 
                        
            for (Map.Entry<Integer, BigDecimal> entry : employeeIdToAdvSalMap.entrySet() )
            {
            	final PartyTransaction trans = transBuilder.getPartyTransBuilder().addTrans(
            			entry.getKey(), entry.getValue(), "Adv salary collected the previous month",
            			PartyTransaction.CREDIT_SALARY );
            	//Increment the date so that the transaction comes on the next day.
            	trans.setDate(this.todayInteger + 1);
            }
            if (BigDecimal.ZERO.compareTo( totalAdvSalaries ) < 0)
            {
	            // Deduct the ADVANCE SALARY paid prev month from expense to balance the settlement.
	            final PartyTransaction trans = transBuilder.getPartyTransBuilder().addTrans( this.expensePartyId,
	            		totalAdvSalaries, "Advance salaries paid previous month " + monthText, PartyTransaction.DEBIT );
	            //Increment the date so that the transaction comes on the next day.
	            trans.setDate(this.todayInteger + 1);
            }
        }
        
        
        
        settlement.setClosingBal( new BigDecimal( this.getComp( CL_BALANCE, JTextField.class ).getText() ) );
        settlement.setComments( "" );
        settlement.setCreatedBy( "admin" );
        settlement.setCreatedDate( new Date() );
        settlement.setSettlementDate( DateUtil.getCalendarEquivalent( this.todayInteger ).getTime() );
        settlement.setDate( this.todayInteger );
        settlement.setSettlementType( "DAILY_CLOSING" );
        statement.getMeterTransactions().addAll( transBuilder.getMeterTransactionBuilder().getTransactions() );
        statement.getPartyTransactions().addAll( transBuilder.getPartyTransBuilder().getTransactions() );
        statement.getProductTransactions().addAll( transBuilder.getProdTransBuilder().getTransactions() );
        statement.getTankTransactions().addAll( transBuilder.getTankTransBuilder().getTransactions() );
        statement.setSettlement( settlement );
        this.bunkManager.saveAndCloseStatement(statement);
    }
    
    private void postSubmitTasks() throws BunkMgmtException
    {
        /*if ( DateUtil.isFirstDayOfMonth( this.todayInteger ))
        {
            this.bunkManager.creditRentToCompanyAccount( this.todayInteger );
        }*/
        this.bunkManager.executeDbBackup();
    }


	/**
	 * Implements the method of TableChangedListener interface.
	 */
	public void tableChanged( final TableModelEvent tableModelEvent)
	{
		updateClosingBalance();
		if (tableModelEvent.getType() == TableModelEvent.INSERT )
		{
			setSingleScrollPanelPreferedHeight();
		}
        this.validate();
        this.repaint();
	}
	
	private void updateClosingBalance()
	{
	    BigDecimal balance = this.previousDateSettlement.getClosingBal();
	    final List<FuelSalesProdDetailBean> fuelBeans  =
	        this.getComp( FUEL_PROD_TABLE, AbstractDataTable.class ).getData();
	    for (FuelSalesProdDetailBean bean : fuelBeans)
	    {
	        bean.getTotalSaleAmt();
	        balance = balance.add( bean.getSaleCash() );
	    }
	    
	    final List<LubeSalesProdDetailBean> lubeBeans  =
            this.getComp( LUBE_PROD_TABLE, AbstractDataTable.class ).getData();
        for (LubeSalesProdDetailBean bean : lubeBeans)
        {
            balance = balance.add( bean.getTotalSaleCash() );
        }
        final List<PartyDetailBean> partyBeans  =
            this.getComp( PARTY_TRANS_TABLE, AbstractDataTable.class ).getData();
        for (PartyDetailBean bean : partyBeans)
        {
            balance = balance.add( bean.getDebit() );
            balance = balance.subtract( bean.getCredit() );
        }
        
        final List<EmployeeTrxDetailBean> employeeBeans  =
                this.getComp( EMPLOYEE_TRANS_TABLE, AbstractDataTable.class ).getData();
        for (EmployeeTrxDetailBean bean : employeeBeans)
        {
            balance = balance.subtract( bean.getSalary() );
            balance = balance.subtract( bean.getIncentive() );
        }
        
        final List<OfficeCashDetailBean> officeCashDetailBeans = this.getComp( OFFICE_CASH_TABLE,
                AbstractDataTable.class ).getData();
        for ( OfficeCashDetailBean bean : officeCashDetailBeans )
        {
            if (this.offCashPartyId == bean.getParty().getId() )
            {
                balance = balance.subtract( bean.getAmountToday() );
            }
        }
        this.getComp( CL_BALANCE, JTextField.class ).setText( balance.toPlainString() );
	}
	
	public void keyTyped( KeyEvent e )
    {
         //No actions on key type events      
    }

    public void keyPressed( KeyEvent e )
    {
        // No actions on key press events
    }

    public void keyReleased( KeyEvent e )
    {
        updateClosingBalance(); 
    }
}
