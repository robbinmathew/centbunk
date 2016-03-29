package bronz.accounting.bunk.ui.product.panel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.text.JTextComponent;

import bronz.accounting.bunk.model.StockReceipt;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.products.model.ProductTransaction;
import bronz.accounting.bunk.tankandmeter.model.TankTransaction;
import bronz.accounting.bunk.ui.panel.BasePanel;
import bronz.accounting.bunk.ui.util.SwingUiBuilder.UiElement;
import bronz.accounting.bunk.ui.util.UiUtil;
import bronz.accounting.bunk.util.EntityNameCache;

public class ReceiptDetailPanel extends BasePanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final String DETAILS_TEXT = "DETAILS_TEXT";
	private final StockReceipt receipt;
	
	
    public ReceiptDetailPanel( final StockReceipt receipt, final int todayDate )
    {
        super( 10, 10, 450, 400 ); //Dummy values
        this.receipt = receipt;
        this.setOpaque( true );
        this.setBackground( Color.LIGHT_GRAY );
    }
	
    public void setBounds( final int x, final int y, final int width,
            final int height)
    {
        super.setBounds( x, y, width, height );
        if ( null != this.receipt )
        {
            populateDetails( width, height );
            UiUtil.addActionListeners( this );
        }
    }
    
    private void populateDetails( final int width, final int height )
    {
        this.uiBuilder.addElement( UiElement.JLABEL, "TITLE_LABEL", 10, 10, (width - 235), 20, String.format(
                "INVOICE NO:%3$-20S TOTAL AMT:%1$-10S DATE:%2$S",
                this.receipt.getReceiptSummary().getTotalAmt(),
                this.receipt.getReceiptSummary().getDateText(),
                this.receipt.getReceiptSummary().getInvoiceNumber() ) );
        this.uiBuilder.addElement( UiElement.TEXTAREA, DETAILS_TEXT, 10, 40,
                (width - 20), (height - 50), true, false );
    /*    this.uiBuilder.addElement( UiElement.JBUTTON, PENDING_CASH_BUTTON,
                (width - 220), 10, 200 , 20 , "PAY PENDING AMOUNT" );*/
        final StringBuffer detail = new StringBuffer();
        if ( 0 < this.receipt.getProductTransactions().size() )
        {
            detail.append( "Product transactions:-\n" );
            for ( final ProductTransaction transaction :
                    this.receipt.getProductTransactions() )
            {
                detail.append( String.format( "Product name:%1$-25S Rate:%2$-9.2f " +
                		"    Quantity:%3$-9.2f Balance:%4$-9.2f \n",
                		EntityNameCache.getProductName( transaction.getProductId() ),
                		transaction.getUnitPrice(), transaction.getQuantity(),
                		transaction.getBalance() ) );
            }
        }
        if ( 0 < this.receipt.getTankTransactions().size() )
        {
            detail.append( "\nTank transactions:-\n" );
            for ( final TankTransaction transaction :
                this.receipt.getTankTransactions() )
            {
                detail.append( String.format( "Tank name:%1$-25S Quantity:%2$-9.2f Balance:%3$-9.2f \n",
                        EntityNameCache.getTankName( transaction.getTankId() ), transaction.getQuantity(),
                        transaction.getBalance() ) );
            }
        }
        if ( 0 < this.receipt.getPartyTransactions().size() )
        {
            detail.append( "\nCash\\Bank transactions:-\n" );
            for ( final PartyTransaction transaction : this.receipt.getPartyTransactions() )
            {
                detail.append( String.format( "%1$-25S Amount:%2$-9.2f Detail:%3$S\n",
                        EntityNameCache.getPartyName( transaction.getPartyId() ), transaction.getAmount(),
                        transaction.getTransactionDetail() ) );
            }
        }
        ((JTextComponent) this.componentMap.get( DETAILS_TEXT )).setText( detail.toString() );
    }
        
	public void actionPerformed( final ActionEvent actionEvent )
	{
	}
}
