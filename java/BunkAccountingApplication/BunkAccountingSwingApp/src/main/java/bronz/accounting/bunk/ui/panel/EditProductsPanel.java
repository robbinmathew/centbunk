package bronz.accounting.bunk.ui.panel;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import bronz.accounting.bunk.BunkAppInitializer;
import bronz.accounting.bunk.BunkManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.Product;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.product.model.ProductWrapper;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.ui.util.EntityConverter;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.swing.table.AbstractDataTable;
import bronz.utilities.swing.util.SwingUtil;
import bronz.utilities.swing.util.UIValidationException;

public class EditProductsPanel extends BasePanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final int PANEL_WIDTH = 800;
	private static final int PANEL_HEIGHT = 700;
	private static final String SUBMIT_BUTTON = "SUBMIT_BUTTON";
	
	private final BunkManager bunkManager;
    private final JDialog dialog;
    private final ActionListener actionListener;
    private final AbstractDataTable<ProductWrapper> oilProdsTable;
    private final AbstractDataTable<ProductWrapper> additionalProdsTable ;
    
    
	public EditProductsPanel( final HomePage frame, final ActionListener actionListener) throws BunkMgmtException
	{
        super( 50, 50, PANEL_WIDTH, PANEL_HEIGHT );
        this.actionListener = actionListener;
        this.bunkManager = frame.getBunkManager();
        this.uiBuilder.addElement( UiElement.JBUTTON, SUBMIT_BUTTON, PANEL_WIDTH/2-50, PANEL_HEIGHT - 80, 100, 20, "SUBMIT" );
        this.uiBuilder.addElement( UiElement.JLABEL, "", 10, 10, PANEL_WIDTH, 20, "Add/Edit products", UiUtil.SUB_TITLE_BOLD_FONT );
        this.uiBuilder.addElement( UiElement.JLABEL, "", PANEL_WIDTH/2-100, 40, 200, 20, "OIL PRODUCTS", UiUtil.LABEL_BOLD_FONT );
        this.oilProdsTable = new AbstractDataTable<ProductWrapper>( ProductWrapper.class,
                EntityConverter.convertToProductWrapper( frame.getBunkManager().getProductList( ProductDao.OIL_PRODUCTS ) ) );
        final JScrollPane oilScrollPane = new JScrollPane( oilProdsTable );
        final int oilPanelHieght = PANEL_HEIGHT - 300;
        oilScrollPane.setBounds( 15, 60, PANEL_WIDTH - 40, oilPanelHieght );
        this.add( oilScrollPane );
        
        this.uiBuilder.addElement( UiElement.JLABEL, "", PANEL_WIDTH/2-125, oilPanelHieght + 65, 250, 20,
                "ADDITIONAL PRODUCTS", UiUtil.LABEL_BOLD_FONT );
        
        this.additionalProdsTable = new AbstractDataTable<ProductWrapper>( ProductWrapper.class,
                EntityConverter.convertToProductWrapper( frame.getBunkManager().getProductList( ProductDao.ADDITIONAL_PRODUCTS ) ) );
        final JScrollPane additionalScrollPane = new JScrollPane( additionalProdsTable );
        additionalScrollPane.setBounds( 15, oilPanelHieght + 100, PANEL_WIDTH - 40, 100 );
        this.add( additionalScrollPane );
        
        UiUtil.addActionListeners( this );
        this.dialog = new JDialog( frame, "Add/Edit products", true );
        this.dialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        this.dialog.setBounds( 50, 50, PANEL_WIDTH, PANEL_HEIGHT );
        this.dialog.add( this );
        this.dialog.setVisible( true );
        this.dialog.setResizable( true );
        this.dialog.setLayout( null );
        
        
        this.validate();
        this.repaint();
	}
	
	protected void processPanelRequest() throws BunkMgmtException
    {
       int nextOilId = ProductDao.MIN_OIL_PROD_ID;
       int nextAddionalProdId = ProductDao.MIN_ADDITIONAL_PROD_ID;
       final Map<Integer, Product> prodMap = new HashMap<Integer, Product>();
       for (Product product : this.bunkManager.getAllProduct())
       {
           if (product.getProductId() > nextOilId &&
                   product.getProductId() < ProductDao.MIN_ADDITIONAL_PROD_ID)
           {
               nextOilId = product.getProductId();
           }
           if (product.getProductId() > nextAddionalProdId &&
                   product.getProductId() > ProductDao.MIN_ADDITIONAL_PROD_ID)
           {
               nextAddionalProdId = product.getProductId();
           }
           prodMap.put( product.getProductId(), product );
       }
       final List<Product> prodToBeUpdated = new ArrayList<Product>();
       final List<ProductTransaction> prodTransToBeUpdated = new ArrayList<ProductTransaction>();
       for (ProductWrapper productWrapper : this.oilProdsTable.getData())
       {
           if (productWrapper.getProduct() == null)
           {
               addNewProd( productWrapper, ++nextOilId, prodToBeUpdated, prodTransToBeUpdated );
           }
           else
           {
               updateProd( productWrapper, prodMap, prodToBeUpdated, prodTransToBeUpdated );
           }
       }
       for (ProductWrapper productWrapper : this.additionalProdsTable.getData())
       {
           if (productWrapper.getProduct() == null)
           {
               addNewProd( productWrapper, ++nextAddionalProdId, prodToBeUpdated, prodTransToBeUpdated );
           }
           else
           {
               updateProd( productWrapper, prodMap, prodToBeUpdated, prodTransToBeUpdated );
           }
       }
       this.bunkManager.saveProdDetails( prodToBeUpdated, prodTransToBeUpdated );
    }
	
	protected void validatePanel() throws UIValidationException
    {
	    cancelTableCellEditing( this.oilProdsTable, this.additionalProdsTable );
	    SwingUtil.validatePanelComponents( this );
    }
	
	public void actionPerformed( final ActionEvent actionEvent )
	{
		if ( actionEvent.getSource().equals( this.getComp( SUBMIT_BUTTON, JButton.class ) ) )
		{
		    if (submitPanel())
		    {
		        UiUtil.alertOperationComplete( this, "Products saved successfully");
		        BunkAppInitializer.refreshProductNameCache();
		        if (null != this.actionListener)
	            {
	                this.actionListener.actionPerformed( actionEvent );
	            }
	            this.dialog.dispose();
		    }
		}
		this.validate();
		this.repaint();
	}
	
	private void addNewProd(final ProductWrapper productWrapper, final int nextId, final List<Product> prodToBeUpdated,
	        final List<ProductTransaction> prodTransToBeUpdated ) throws BunkMgmtException
	{
	    final Product product = new Product();
        product.setProductId( nextId );
        product.setProductName( productWrapper.getProdName() );
        prodToBeUpdated.add( product );
        
        final ProductTransaction productTransaction = new ProductTransaction();
        productTransaction.setProductId( nextId );
        productTransaction.setDate( this.bunkManager.getTodayDate() );
        productTransaction.setDetail( "New product" );
        productTransaction.setBalance( CustomDecimal.ZERO );
        productTransaction.setMargin(productWrapper.getMargin());
        productTransaction.setQuantity( CustomDecimal.ZERO );
        productTransaction.setUnitPrice( productWrapper.getRate() );
        productTransaction.setTransactionType( "RATE_CHANGE" );
        prodTransToBeUpdated.add( productTransaction );
	}
	
	private void updateProd(final ProductWrapper productWrapper, final Map<Integer, Product> prodMap,
	        final List<Product> prodToBeUpdated, final List<ProductTransaction> prodTransToBeUpdated )
	        throws BunkMgmtException
    {
	    if(productWrapper.isProdUpdated())
        {
            final Product product = prodMap.get( productWrapper.getProduct().getProductId() );
            product.setProductName( productWrapper.getProdName() );
            prodToBeUpdated.add( product );
        }
        if(productWrapper.isRateUpdated())
        {
            final ProductTransaction productTransaction = new ProductTransaction();
            productTransaction.setProductId( productWrapper.getProduct().getProductId() );
            productTransaction.setDate( this.bunkManager.getTodayDate() );
            productTransaction.setDetail( "Rate/margin changed" );
            productTransaction.setBalance( productWrapper.getProduct().getClosingStock());
            productTransaction.setMargin(productWrapper.getMargin());
            productTransaction.setQuantity( CustomDecimal.ZERO );
            productTransaction.setUnitPrice( productWrapper.getRate() );
            productTransaction.setTransactionType( "RATE_CHANGE" );
            prodTransToBeUpdated.add( productTransaction );
        }
    }
}
