package bronz.accounting.bunk;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import bronz.accounting.bunk.model.StockReceipt;
import bronz.utilities.general.GeneralUtil;
import junit.framework.TestCase;

import javax.swing.*;

public class BunkManagerTest extends TestCase
{
    public static String TEST_PROPS_FILE = "src/main/resources/bronz/pbmsTestApp.properties";
    private BunkManager bunkManager;
    private int date;
    public void setUp()
    {
        try
        {
            this.date = 734520;
            //GeneralUtil.readPropsOnClasspathToSystemProps( TEST_PROPS_FILE);
            //this.bunkManager = BunkAppInitializer.getInstance().getBunkManager();
        }
        catch( final Exception exception )
        {
            exception.printStackTrace();
            fail( "Failed to create setup for test" );
        }
    }

    public void testReadReciepts() throws Exception
    {
        final List<StockReceipt> receipts =
            this.bunkManager.getStockReciepts( this.date );
        System.out.println( receipts );
        assertNotNull( receipts );
        assertTrue( receipts.size() > 0 );
        
        
    }

    public void testFlashDialog() throws Exception{
        JOptionPane pane = new JOptionPane("Message",
                JOptionPane.INFORMATION_MESSAGE);
        final JDialog dialog = pane.createDialog(null, "Title");

        Timer timer = new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
        dialog.dispose();
    }
}
