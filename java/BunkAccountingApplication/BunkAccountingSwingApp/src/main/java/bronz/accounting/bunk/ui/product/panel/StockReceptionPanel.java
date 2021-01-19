package bronz.accounting.bunk.ui.product.panel;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.model.StockReceipt;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ReceiptSummary;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.panel.BasePanel;
import bronz.accounting.bunk.ui.panel.BasePublicPanel;
import bronz.accounting.bunk.ui.panel.CashPaymentPanel;
import bronz.accounting.bunk.ui.panel.EditProductsPanel;
import bronz.accounting.bunk.ui.product.model.FuelReceiptProdDetailBean;
import bronz.accounting.bunk.ui.product.model.OilReceiptProdDetailBean;
import bronz.accounting.bunk.ui.product.model.TankReceiptDetailBean;
import bronz.accounting.bunk.ui.util.EntityConverter;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.ui.util.BunkUiUtil;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.accounting.bunk.util.EntityTransactionBuilder;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.DateUtil;
import bronz.utilities.swing.custom.ListCellEditor;
import bronz.utilities.swing.custom.ValidatableTextField.ValidationType;
import bronz.utilities.swing.table.AbstractDataTable;
import bronz.utilities.swing.util.SwingUtil;
import bronz.utilities.swing.util.UIValidationException;

@SuppressWarnings("unchecked")
public class StockReceptionPanel extends BasePublicPanel
		implements ActionListener, TableModelListener, KeyListener
{
	/** Serial Number.*/
    private static final long serialVersionUID = 1L;
	
    private static final Logger LOG = LogManager.getLogger(
            StockReceptionPanel.class );
    private static final String SAVE_BUTTON = "SAVE_BUTTON";
    private static final String CLEAR_BUTTON = "CLEAR_BUTTON";
    private static final String ADD_PROD_BUTTON = "ADD_PROD_BUTTON";
    private static final String PRODUCT_DETAILS = "PRODUCTS";
    private static final String BUTTON_DETAILS = "BUTTON_PANEL";
    private static final String TANK_DETAILS = "TANK DETAILS";
    private static final String INV_NO = "INVOICE NO";
    private static final String RECEIPT_NO = "RECEIPT NO";
    private static final String TOTAL_INV_AMT = "TOTAL INVOICE AMT";
    private static final Map<String, String> HEADER_PREFIX_MAP =
        new HashMap<String, String>();
    private static final Map<String, String> TITLE_PREFIX_MAP =
        new HashMap<String, String>();
    static
    {
        HEADER_PREFIX_MAP.put( StockReceipt.BWATER_RECEIPT_TYPE,
                "BATTERY WATER STOCK RECEIPT FOR " );
        HEADER_PREFIX_MAP.put( StockReceipt.FUEL_RECEIPT_TYPE,
                "FUEL STOCK RECEIPT FOR " );
        HEADER_PREFIX_MAP.put( StockReceipt.OIL_RECEIPT_TYPE,
                "LUBES STOCK RECEIPT FOR " );
        TITLE_PREFIX_MAP.put( StockReceipt.BWATER_RECEIPT_TYPE,
                "Battery water receipt" );
        TITLE_PREFIX_MAP.put( StockReceipt.FUEL_RECEIPT_TYPE,
                "Fuel receipt" );
        TITLE_PREFIX_MAP.put( StockReceipt.OIL_RECEIPT_TYPE,
                "Lubes receipt" );
    }
    
    private final String receiptType;

    
    
	public StockReceptionPanel( final HomePage parentFrame,
			final String receiptType ) throws BunkMgmtException
	{
        super( parentFrame, getPageHeader( parentFrame, receiptType ) );
        this.receiptType = receiptType;
        LOG.debug( "Launching the stock reciepts panel" );
		initPanel();
		
		UiUtil.addActionListeners( this );
        LOG.debug( "Launched the stock reciepts panel" );
	}
	
	private static String getPageHeader( final HomePage parentFrame,
            final String receiptType )
	{
	    return HEADER_PREFIX_MAP.get( receiptType ) + DateUtil.getDateString(
                parentFrame.getTodayInteger() );
	}
	
    @Override
    public String getTitleSuffix()
    {
        return TITLE_PREFIX_MAP.get( this.receiptType );
    }
	
	private void initPanel() throws BunkMgmtException
	{
	    if ( StockReceipt.FUEL_RECEIPT_TYPE.equals(
	            this.receiptType ) )
	    {
	        final List<ProductClosingBalance> fuelProducts = this.bunkManager.getProductList(
	                ProductDao.FUEL_PRODUCTS );
	        
	        final List<TankReceiptDetailBean> tankList =
	                EntityConverter.convertToTankReciept(
                    this.bunkManager.getTankList( this.todayInteger ) );
	        
	        final List<FuelReceiptProdDetailBean> prodList =
	            EntityConverter.convertToFuelReciept( fuelProducts, tankList );
	        
	        final JTable tankTable = new AbstractDataTable<TankReceiptDetailBean>(
	                TankReceiptDetailBean.class, tankList );
	        final JTable productsTable = new AbstractDataTable<FuelReceiptProdDetailBean>(
	                FuelReceiptProdDetailBean.class, prodList );
	        
	        this.uiBuilder.addDetailPanelsWithoutSubOptions(
	                TANK_DETAILS, tankTable, 20,
	                PRODUCT_DETAILS, productsTable, 20,
	                BUTTON_DETAILS, new BasePanel(), 60 );
	        this.uiBuilder.addFormFields( BUTTON_DETAILS,
	                UiElement.JTEXTFIELD_HORIZONTAL, INV_NO, ValidationType.NOT_EMPTY,
	                UiElement.JTEXTFIELD_HORIZONTAL, TOTAL_INV_AMT, ValidationType.DISABLED,
	                UiElement.JBUTTON, SAVE_BUTTON, "SAVE",
	                UiElement.JBUTTON, CLEAR_BUTTON, "CLEAR" );
	        
	        this.getComp( TANK_DETAILS, JTable.class ).getColumnModel()
                    .getColumn( 3 ).setCellEditor( new ListCellEditor(
                            BunkUiUtil.getFuelReceiptQuantities() ) );
	        
	        this.getComp( TANK_DETAILS, JTable.class ).getModel()
	                .addTableModelListener( this );
	        this.getComp( PRODUCT_DETAILS, JTable.class ).getModel()
	                .addTableModelListener( this );
	    }
	    else if ( StockReceipt.OIL_RECEIPT_TYPE.equals(
                this.receiptType ) )
        {
            final JTable productsTable = new AbstractDataTable<OilReceiptProdDetailBean>(
                    OilReceiptProdDetailBean.class );
            
            this.uiBuilder.addDetailPanelsWithoutSubOptions(
                    PRODUCT_DETAILS, productsTable, 40,
                    BUTTON_DETAILS, new BasePanel(), 60 );
            this.uiBuilder.addFormFields( BUTTON_DETAILS,
                    UiElement.JTEXTFIELD_HORIZONTAL, INV_NO, ValidationType.NOT_EMPTY,
                    UiElement.JTEXTFIELD_HORIZONTAL, TOTAL_INV_AMT, ValidationType.DISABLED,
                    UiElement.JBUTTON, SAVE_BUTTON, "SAVE",
                    UiElement.JBUTTON, CLEAR_BUTTON, "CLEAR" );
            this.uiBuilder.addElement( UiElement.JBUTTON, ADD_PROD_BUTTON, panelSize.width - 200, 10, 150, 25, "Add/Edit product" );
            
            loadOilProductsEditor();
            this.getComp( PRODUCT_DETAILS, JTable.class ).getModel().addTableModelListener( this );
        }
	    else if ( StockReceipt.BWATER_RECEIPT_TYPE.equals(
                this.receiptType ) )
        {
            final JTable productsTable = new AbstractDataTable<OilReceiptProdDetailBean>(
                    OilReceiptProdDetailBean.class );
            
            this.uiBuilder.addDetailPanelsWithoutSubOptions( PRODUCT_DETAILS, productsTable, 60,
                    BUTTON_DETAILS, new BasePanel(), 40 );
            this.uiBuilder.addElement( UiElement.JBUTTON, ADD_PROD_BUTTON, panelSize.width - 200, 10, 150, 25, "Add/Edit product" );

            loadAdditionalProductsEditor();
            this.getComp( PRODUCT_DETAILS, JTable.class ).getModel()
                    .addTableModelListener( this );
            
            this.uiBuilder.addFormFields( BUTTON_DETAILS,
                    UiElement.JTEXTFIELD_HORIZONTAL, RECEIPT_NO, ValidationType.NOT_EMPTY,
                    UiElement.JTEXTFIELD_HORIZONTAL, TOTAL_INV_AMT, ValidationType.DISABLED,
                    UiElement.JBUTTON, SAVE_BUTTON, "SAVE",
                    UiElement.JBUTTON, CLEAR_BUTTON, "CLEAR" );
        }
	}
	
	private List<ProductClosingBalance> loadOilProductsEditor() throws BunkMgmtException
	{
	    final List<ProductClosingBalance> oilProducts = this.bunkManager.getProductList(
                ProductDao.LUBE_PRODUCTS );
        this.getComp( PRODUCT_DETAILS, JTable.class ).getColumnModel()
                .getColumn( 0 ).setCellEditor( new ListCellEditor(
                        oilProducts ) );
        return oilProducts;
	}
	
	private List<ProductClosingBalance> loadAdditionalProductsEditor() throws BunkMgmtException
    {
	    final List<ProductClosingBalance> oilProducts = this.bunkManager.getProductList(
                ProductDao.ADDITIONAL_PRODUCTS );
        this.getComp( PRODUCT_DETAILS, JTable.class ).getColumnModel()
                .getColumn( 0 ).setCellEditor( new ListCellEditor(
                        oilProducts ) );
        return oilProducts;
    }
	
	private void repopulateProdInstancesForData(final String tableId, final List<ProductClosingBalance> oilProducts)
	{
	    final List<OilReceiptProdDetailBean> beans =  this.getComp( tableId, AbstractDataTable.class ).getData();
	    final Map<Integer, ProductClosingBalance> prodMap = new HashMap<Integer, ProductClosingBalance>();
	    for (ProductClosingBalance product : oilProducts)
	    {
	        prodMap.put( product.getProductId(), product );
	    }
        for ( OilReceiptProdDetailBean bean : beans )
        {
            final ProductClosingBalance product = prodMap.get( bean.getProduct().getProductId() );
            bean.setProduct( product );
        }
	}
	
	/**
	 * Implements the method of ActionListener interface.
	 */
	public void actionPerformed( final ActionEvent actionEvent)
	{
	    try
        {
	        if( actionEvent.getSource().equals( this.componentMap.get( ADD_PROD_BUTTON )))
            {
    	        if ( StockReceipt.OIL_RECEIPT_TYPE.equals( this.receiptType ) )
    	        {
                    new EditProductsPanel( this.parentFrame, new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            try
                            {
                                repopulateProdInstancesForData( PRODUCT_DETAILS, loadOilProductsEditor() );
                            }
                            catch( Exception exception )
                            {
                                UiUtil.alertUnexpectedError( parentFrame, exception );
                            }
                        }
                    });
    	        }
    	        else if ( StockReceipt.BWATER_RECEIPT_TYPE.equals( this.receiptType ) )
    	        {
    	            new EditProductsPanel( this.parentFrame, new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            try
                            {
                                repopulateProdInstancesForData( PRODUCT_DETAILS, loadAdditionalProductsEditor() );
                            }
                            catch( Exception exception )
                            {
                                UiUtil.alertUnexpectedError( parentFrame, exception );
                            }
                        }
                    });
    	        }
            }
            else if( actionEvent.getSource().equals( this.componentMap.get( CLEAR_BUTTON )))
            {
                this.parentFrame.setPublicPanel( new StockReceptionPanel(
                        this.parentFrame, this.receiptType ) );
            }
            else if( actionEvent.getSource().equals( this.componentMap.get( SAVE_BUTTON )))
    		{
    			boolean validationSuccess;
    			try
    			{
    			    cancelTableCellEditing( PRODUCT_DETAILS, TANK_DETAILS );
    			    checkIfEmptyReceiptsOrBankMissing();
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
    				try
    				{
                        synchronized( StockReceptionPanel.class )
                        {
                            processRequest();
                        }
                        UiUtil.alertOperationCompleteWithTime( this, "Stock receipt saved successfully",
                                this.launchTime );
                        this.parentFrame.setPublicPanel( new StockRecepSummaryPanel(
                                this.parentFrame, this.bunkManager,
                                this.todayInteger, this.panelSize ) );
                        if (StockReceipt.FUEL_RECEIPT_TYPE.equals(this.receiptType ) ||
                                StockReceipt.OIL_RECEIPT_TYPE.equals(this.receiptType ))
                        {
                            new CashPaymentPanel( this.todayInteger, CashPaymentPanel.FROM_LOAD_RECIEPT_PANEL_MODE );
                        }
    				}
    				catch ( Exception exception)
    				{
    				    UiUtil.alertUnexpectedError( this, exception );
    				}
    			}
    		}
    		this.validate();
    		this.repaint();
        }
	    catch ( final Exception exception )
	    {
	        UiUtil.alertUnexpectedError( this, exception );
	    }
	}
	
	private void checkIfEmptyReceiptsOrBankMissing() throws UIValidationException
	{
	    final StringBuilder errorMessages = new StringBuilder();
	    final StringBuilder warningMessages = new StringBuilder();
	    BigDecimal totalReceiptQuantity = CustomDecimal.ZERO;
	    if ( StockReceipt.FUEL_RECEIPT_TYPE.equals(
                this.receiptType ) )
        {
            final List<FuelReceiptProdDetailBean> fuelBeans =  this.getComp(
                    PRODUCT_DETAILS, AbstractDataTable.class ).getData();
            for ( FuelReceiptProdDetailBean fuelBean : fuelBeans )
            {
                totalReceiptQuantity = totalReceiptQuantity.add( fuelBean.getReceiptAmt() );
            }
        }
        else if ( StockReceipt.OIL_RECEIPT_TYPE.equals(
                this.receiptType ) )
        {
            final List<OilReceiptProdDetailBean> oilBeans =  this.getComp(
                    PRODUCT_DETAILS, AbstractDataTable.class ).getData();
            for ( OilReceiptProdDetailBean oilBean : oilBeans )
            {
                totalReceiptQuantity = totalReceiptQuantity.add( oilBean.getReceiptAmt() );
            }
        }
        else if ( StockReceipt.BWATER_RECEIPT_TYPE.equals(
                this.receiptType ) )
        {
            final List<OilReceiptProdDetailBean> oilBeans =  this.getComp(
                    PRODUCT_DETAILS, AbstractDataTable.class ).getData();
            for ( OilReceiptProdDetailBean oilBean : oilBeans )
            {
                totalReceiptQuantity = totalReceiptQuantity.add( oilBean.getReceiptAmt() );
            }
        }
	    if ( totalReceiptQuantity.intValue() <= 0 )
        {
            errorMessages.append( "--No product receipts are added yet, please add them\n" );
        }
	    SwingUtil.validatePanelComponents( this, errorMessages, warningMessages );
	}
	
	
	private void updateTotalAmount()
	{
	    if ( StockReceipt.FUEL_RECEIPT_TYPE.equals(
                this.receiptType ) )
        {
    	    final List<FuelReceiptProdDetailBean> fuelBeans =  this.getComp(
                    PRODUCT_DETAILS, AbstractDataTable.class ).getData();
            BigDecimal amount = CustomDecimal.ZERO;
            for ( FuelReceiptProdDetailBean fuelBean : fuelBeans )
            {
                amount = amount.add( fuelBean.getTotalCost() );
            }
            this.getComp( TOTAL_INV_AMT, JTextField.class ).setText(
            		BunkUtil.setAsPrice( amount ).toPlainString() );
        }
	    else if ( StockReceipt.OIL_RECEIPT_TYPE.equals(
	            this.receiptType ) )
	    {
	        final List<OilReceiptProdDetailBean> oilBeans =  this.getComp(
                    PRODUCT_DETAILS, AbstractDataTable.class ).getData();
            BigDecimal amount = CustomDecimal.ZERO;
            for ( OilReceiptProdDetailBean oilBean : oilBeans )
            {
                amount = amount.add( oilBean.getTotalCost() );
            }
            this.getComp( TOTAL_INV_AMT, JTextField.class ).setText(
            		BunkUtil.setAsPrice( amount ).toPlainString() );
	    }
	    else if ( StockReceipt.BWATER_RECEIPT_TYPE.equals(
                this.receiptType ) )
        {
            final List<OilReceiptProdDetailBean> oilBeans =  this.getComp(
                    PRODUCT_DETAILS, AbstractDataTable.class ).getData();
            BigDecimal totalAmount = CustomDecimal.ZERO;
            for ( OilReceiptProdDetailBean oilBean : oilBeans )
            {
                totalAmount = totalAmount.add( oilBean.getTotalCost() );
            }
            this.getComp( TOTAL_INV_AMT, JTextField.class ).setText(
            		BunkUtil.setAsPrice( totalAmount ).toPlainString() );
        }
	    
	}

    public void tableChanged( final TableModelEvent tableModelEvent )
	{
	    final Object source = tableModelEvent.getSource();
	    if ( this.getComp( PRODUCT_DETAILS, JTable.class ).getModel().equals(
	            source ) )
	    {
	        updateTotalAmount();
	    }
		this.validate();
		this.repaint();
	}
	
	/**
	 * Processes the request based on the radio button selection.
	 *
	 * @throws BunkMgmtException If is fails to process the request. 
	 */
	private void processRequest() throws BunkMgmtException
	{
	    final StockReceipt stockReceipt = new StockReceipt();
	    final EntityTransactionBuilder transBuilder = new EntityTransactionBuilder( bunkManager, todayInteger );
	    final Integer expensePartyId = AppConfig.EXPENSES_PARTY_ID.getValue( Integer.class );
	    if ( StockReceipt.FUEL_RECEIPT_TYPE.equals(
                this.receiptType ) )
	    {
	        final String transactionType = "RF" + this.getComp( INV_NO, JTextField.class ).getText();
	        
	        //Add tank transactions
	        final List<TankReceiptDetailBean> tankReceipts = this.getComp(
	                TANK_DETAILS, AbstractDataTable.class ).getData();
	        
	        for ( TankReceiptDetailBean tankReceipt : tankReceipts )
	        {
	            if ( CustomDecimal.ZERO.compareTo( tankReceipt.getReceiptAmt()) < 0 )
	            {
	                transBuilder.getTankTransBuilder().addTrans( tankReceipt.getTankId(), tankReceipt.getReceiptAmt(),
	                        transactionType, "FILL" );
	            }
	        }
	        stockReceipt.getTankTransactions().addAll( transBuilder.getTankTransBuilder().getTransactions() );
	        
	        //Add fuels transactions
	        final List<FuelReceiptProdDetailBean> fuelReceipts = this.getComp(
	                PRODUCT_DETAILS, AbstractDataTable.class ).getData();
	        for ( FuelReceiptProdDetailBean fuelReceipt : fuelReceipts )
	        {
	            if ( CustomDecimal.ZERO.compareTo( fuelReceipt.getReceiptAmt()) < 0 )
	            {
	                transBuilder.getProdTransBuilder().addTransWithMargin( fuelReceipt.getProduct().getProductId(),
	                        fuelReceipt.getReceiptAmt(), fuelReceipt.getMargin(), "RECEIPT", transactionType );
	            }
	        }
            stockReceipt.getProductTransactions().addAll( transBuilder.getProdTransBuilder().getTransactions() );
            final Integer companyPartyId = AppConfig.COMPANY_PARTY_ID.getValue( Integer.class );
            final BigDecimal invoiceAmt = new BigDecimal( this.getComp( TOTAL_INV_AMT, JTextField.class ).getText() );
            transBuilder.getPartyTransBuilder().addTrans( companyPartyId, invoiceAmt, transactionType + ":PROD COST", "DEBIT_S" );
            stockReceipt.getPartyTransactions().addAll( transBuilder.getPartyTransBuilder().getTransactions() );	        
	        final ReceiptSummary receiptSummary = new ReceiptSummary();
	        receiptSummary.setComments( EMPTY_STRING );
	        receiptSummary.setDate( this.todayInteger );
	        receiptSummary.setInvoiceNumber( transactionType );
	        receiptSummary.setTotalAmt( BunkUtil.setAsPrice( new BigDecimal(
                    this.getComp( TOTAL_INV_AMT, JTextField.class ).getText() ) ) );
	        receiptSummary.setType( this.receiptType );
	        stockReceipt.setReceiptSummary( receiptSummary );
	    }
	    else if ( StockReceipt.OIL_RECEIPT_TYPE.equals(
                this.receiptType ) )
        {
           final String transactionType = "RO" + this.getComp( INV_NO, JTextField.class ).getText();
           
            //Add oil transactions
            final List<OilReceiptProdDetailBean> oilReceiptBeans = this.getComp(
                    PRODUCT_DETAILS, AbstractDataTable.class ).getData();
            for ( OilReceiptProdDetailBean oilReceipt : oilReceiptBeans )
            {
                if ( CustomDecimal.ZERO.compareTo( oilReceipt.getReceiptAmt()) < 0 )
                {
                    transBuilder.getProdTransBuilder().addTransWithMargin( oilReceipt.getProduct().getProductId(),
                            oilReceipt.getReceiptAmt(), oilReceipt.getMargin(), "RECEIPT", transactionType );
                }
            }
            stockReceipt.getProductTransactions().addAll( transBuilder.getProdTransBuilder().getTransactions() );
            
            final Integer companyPartyId = AppConfig.COMPANY_PARTY_ID.getValue( Integer.class );
            final BigDecimal invoiceAmt = new BigDecimal( this.getComp( TOTAL_INV_AMT, JTextField.class ).getText() );
            transBuilder.getPartyTransBuilder().addTrans( companyPartyId, invoiceAmt, transactionType + ":PROD COST", "DEBIT_S" );
            stockReceipt.getPartyTransactions().addAll( transBuilder.getPartyTransBuilder().getTransactions() );            
            final ReceiptSummary receiptSummary = new ReceiptSummary();
            receiptSummary.setComments( EMPTY_STRING );
            receiptSummary.setDate( this.todayInteger );
            receiptSummary.setInvoiceNumber( transactionType );
            receiptSummary.setTotalAmt( BunkUtil.setAsPrice( new BigDecimal(
                    this.getComp( TOTAL_INV_AMT, JTextField.class ).getText() ) ) );
            receiptSummary.setType( this.receiptType );
            
            stockReceipt.setReceiptSummary( receiptSummary );
        }
	    else if ( StockReceipt.BWATER_RECEIPT_TYPE.equals(
                this.receiptType ) )
        {
	        final String additionalProdTransType = "RB" + this.getComp( RECEIPT_NO, JTextField.class ).getText();
            //Add additional products transactions
            final List<OilReceiptProdDetailBean> oilReceiptBeans = this.getComp(
                    PRODUCT_DETAILS, AbstractDataTable.class ).getData();
            for ( OilReceiptProdDetailBean oilReceipt : oilReceiptBeans )
            {
                if ( CustomDecimal.ZERO.compareTo( oilReceipt.getReceiptAmt()) < 0 )
                {
                    transBuilder.getProdTransBuilder().addTransWithMargin( oilReceipt.getProduct().getProductId(),
                            oilReceipt.getReceiptAmt(), oilReceipt.getMargin(), "RECEIPT",
                            additionalProdTransType );
                }
            }
            stockReceipt.getProductTransactions().addAll( transBuilder.getProdTransBuilder().getTransactions() );
            
            transBuilder.getPartyTransBuilder().addTrans( expensePartyId, new BigDecimal(
                    this.getComp( TOTAL_INV_AMT, JTextField.class ).getText() ),
                    additionalProdTransType + ":BATTERY WATER COST", "CREDIT" );
            stockReceipt.getPartyTransactions().addAll( transBuilder.getPartyTransBuilder().getTransactions() );
            
            final ReceiptSummary receiptSummary = new ReceiptSummary();
            receiptSummary.setComments( EMPTY_STRING );
            receiptSummary.setDate( this.todayInteger );
            receiptSummary.setInvoiceNumber( additionalProdTransType );
            receiptSummary.setTotalAmt( BunkUtil.setAsPrice( new BigDecimal(
                    this.getComp( TOTAL_INV_AMT, JTextField.class ).getText() ) ) );
            receiptSummary.setType( this.receiptType );
            
            stockReceipt.setReceiptSummary( receiptSummary );
        }
	    this.bunkManager.saveStockReceipt( stockReceipt );
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
        updateTotalAmount(); 
    }
}
