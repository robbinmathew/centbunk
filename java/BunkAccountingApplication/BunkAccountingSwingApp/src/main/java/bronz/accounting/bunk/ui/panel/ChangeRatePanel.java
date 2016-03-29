package bronz.accounting.bunk.ui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;


import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.products.dao.ProductDao;
import bronz.accounting.bunk.products.model.ProdRateChange;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.ui.HomePage;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.util.BunkUtil;
import bronz.utilities.custom.CustomDecimal;
import bronz.utilities.general.ValidationUtil;
import bronz.utilities.swing.custom.ListComboBoxModel;
import bronz.utilities.swing.custom.ValidatableTextField.ValidationType;
import bronz.utilities.swing.table.GenericReadOnlyTableModel;
import bronz.utilities.swing.util.SwingUtil;
import bronz.utilities.swing.util.UIValidationException;

public class ChangeRatePanel extends BasePublicPanel implements ActionListener, KeyListener
{
	/**
	 * Serial Number.
	 */
	private static final long serialVersionUID = 1L;
	private static String FORM_PANEL = "FORM PANEL";
	private static String HISTORY_TABLE = "HISTORY TABLE";
	private static String SUBMIT_BUTTON = "SUBMIT";
	private static String PROD_COMBOBOX = "PRODUCT";
	private static String EXISTING_RATE = "EXISTING RATE";
	private static String NEW_RATE = "NEW RATE";
	private static String STOCK = "STOCK";
	private static String CASH_DIFF = "TOTAL CASH DIFFERENCE";
	private static String RATE_DIFF = "RATE DIFFERENCE";

	/**
	 * Parameterized constructor.
	 *
	 * @param parentFrame The parent frame.
	 * @param todayInteger The int equivalent of today.
	 * @param panelSize The screen size.
	 *
	 * @throws BaseLogicException If it fails to create panel. 
	 */
	public ChangeRatePanel( final HomePage parentFrame )
	        throws BunkMgmtException
	{
	    super( parentFrame, "Rate change");
		this.setBounds( this.panelSize );
		this.setVisible( true );
		this.setLayout( null );
		
		configurePanel();
		
		UiUtil.addActionListeners( this );
		getComp( PROD_COMBOBOX, JComboBox.class ).setSelectedIndex( 0 );
	}
	
	@Override
    public String getTitleSuffix()
    {
        return "Change rate";
    }
	
	private void configurePanel() throws BunkMgmtException
	{
	    final TableModel model = new GenericReadOnlyTableModel<ProdRateChange>(
                this.bunkManager.getRateChangesHistory(),
                Arrays.asList( "DATE", "PRODUCT", "OLD PRICE", "STOCK", "COMMENTS" ),
                Arrays.asList( "dateText", "prodName", "oldPrice", "stock","comments" ) );
	    final JTable historyTable = new JTable( model );
	    this.uiBuilder.addDetailPanelsWithoutSubOptions(
                FORM_PANEL, new BasePanel(), 50,
                HISTORY_TABLE, historyTable, 50 );
	    final List<ProductClosingBalance> productList =
	            this.bunkManager.getProductList( ProductDao.PRIMARY );
	    this.uiBuilder.addFormFields( FORM_PANEL,
                UiElement.JCOMBOBOX_HORIZONTAL, PROD_COMBOBOX, new ListComboBoxModel( productList ),
                UiElement.JTEXTFIELD_HORIZONTAL, EXISTING_RATE, ValidationType.DISABLED,
                UiElement.JTEXTFIELD_HORIZONTAL, NEW_RATE, ValidationType.POSITIVE_NUMERIC,
                UiElement.JTEXTFIELD_HORIZONTAL, RATE_DIFF, ValidationType.DISABLED,
                UiElement.JTEXTFIELD_HORIZONTAL, STOCK, ValidationType.DISABLED,
                UiElement.JTEXTFIELD_HORIZONTAL, CASH_DIFF, ValidationType.DISABLED,
                UiElement.JBUTTON, SUBMIT_BUTTON, "SAVE" );
	    this.getComp( NEW_RATE, JTextField.class ).addKeyListener( this );
	}
	
	/**
	 * Implements the method of ActionListener interface.
	 */
	public void actionPerformed( final ActionEvent actionEvent )
	{
		if( getComp( SUBMIT_BUTTON, JButton.class ).equals( actionEvent.getSource()))
		{
			boolean validationSuccess;
			try
			{
			    SwingUtil.validatePanelComponents( this );
				validationSuccess = true;
			}
			catch ( UIValidationException uiValidationExcepton)
			{
				validationSuccess = false;
			}
			
			if( validationSuccess )
			{
				try
				{
					processRequest();
				}
				catch ( Exception exception )
				{
				    UiUtil.alertUnexpectedError( this.parentFrame, exception );
				}
			}
		}
		else if ( getComp( PROD_COMBOBOX, JComboBox.class ).equals( actionEvent.getSource() ) )
		{
			final ProductClosingBalance product = (ProductClosingBalance) getComp(
			        PROD_COMBOBOX, JComboBox.class ).getSelectedItem();
			this.getComp( NEW_RATE, JTextField.class ).setText( product.getUnitSellingPrice().toPlainString() );
			this.getComp( EXISTING_RATE, JTextField.class ).setText( product.getUnitSellingPrice().toPlainString() );
			this.getComp( RATE_DIFF, JTextField.class ).setText( "0.00" );
			this.getComp( STOCK, JTextField.class ).setText( product.getClosingStock().toPlainString() );
			this.getComp( CASH_DIFF, JTextField.class ).setText( "0.00" );
		}
	}
	
	/**
	 * Processes the request based on the radio button selection.
	 * @throws BunkMgmtException 
	 */
	private void processRequest() throws BunkMgmtException
	{
		final ProductClosingBalance product = (ProductClosingBalance) getComp( PROD_COMBOBOX,
                JComboBox.class ).getSelectedItem();
		final BigDecimal difference = BunkUtil.setAsPrice( new BigDecimal( this.getComp(
		        RATE_DIFF, JTextField.class ).getText() ) );
		final BigDecimal newRate = BunkUtil.setAsPrice( new BigDecimal( this.getComp(
		        NEW_RATE, JTextField.class ).getText() ) );
		if ( BigDecimal.ZERO.compareTo( difference ) != 0 )
		{
		    final ProdRateChange prodRateChange = new ProdRateChange();
		    prodRateChange.setComments( "TOTAL CASH DIFF IS:" + this.getComp(
		            CASH_DIFF, JTextField.class ).getText() );
		    prodRateChange.setDate( this.todayInteger );
		    prodRateChange.setOldPrice( product.getUnitSellingPrice() );
		    prodRateChange.setProdId( product.getProductId() );
		    prodRateChange.setStock( product.getClosingStock() );
		    
		    final ProductTransaction transaction = new ProductTransaction();
		    transaction.setBalance( product.getClosingStock()  );
		    transaction.setUnitPrice( newRate );
		    transaction.setDate( this.todayInteger );
		    transaction.setDetail( "Rate changed from " + this.getComp(
                EXISTING_RATE, JTextField.class ).getText() + " to " + this.getComp(
                NEW_RATE, JTextField.class ).getText() );
		    transaction.setProductId( product.getProductId() );
		    transaction.setQuantity( CustomDecimal.ZERO );
		    transaction.setTransactionType( ProductTransaction.RECEIPT );
		    transaction.setMargin( product.getMargin() );
		    this.bunkManager.saveRateChange( prodRateChange, transaction );
		    
			UiUtil.alertOperationComplete( this.parentFrame, "New rate updated successfully" );
			this.parentFrame.setPublicPanel( new ChangeRatePanel( this.parentFrame ) );
		}
		else
		{
			UiUtil.alertValidationError( this.parentFrame, "New rate is same as existing rate" );
		}
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
        BigDecimal amount = new BigDecimal( this.getComp( EXISTING_RATE, JTextField.class ).getText());
        final String newRate = this.getComp( NEW_RATE, JTextField.class ).getText();
        if( ValidationUtil.isValidNumber( newRate ) )
        {
            amount = BunkUtil.setAsPrice( new BigDecimal( newRate ).subtract( amount ));
        }
        this.getComp( RATE_DIFF, JTextField.class ).setText( amount.toPlainString() );
        amount = BunkUtil.setAsPrice( amount.multiply( new BigDecimal( this.getComp(
                STOCK, JTextField.class ).getText() ) ) );
        this.getComp( CASH_DIFF, JTextField.class ).setText( amount.toPlainString() );
    }
}
