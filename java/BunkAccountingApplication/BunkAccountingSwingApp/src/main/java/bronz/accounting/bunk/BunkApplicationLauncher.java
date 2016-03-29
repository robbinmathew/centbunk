package bronz.accounting.bunk;

import javax.swing.SwingUtilities;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import bronz.accounting.bunk.ui.HomePage;
import bronz.utilities.general.GeneralUtil;

public class BunkApplicationLauncher
{
   public static void main( String[] args )
   {
	   try
	   {
           AppConfig.IS_TEST_ENV.setValue(BunkAppInitializer.PROD);
           AppConfig.loadProperties();
           //Initialize logger only after the properties are loaded.
           LogManager.getLogger(
               BunkApplicationLauncher.class ).info("Launching application..");
           GeneralUtil.analyseJavaEnvironment();
           GeneralUtil.redirectStdoutAndErrToLog();
           SwingUtilities.invokeLater(
                   new Runnable()
                   {
                       public void run()
                       {
                           HomePage.getInstance();
                       }
                   });

           LogManager.getLogger(
               BunkApplicationLauncher.class ).info("Bunk manager launched successfully");
	   }
	   catch ( final Throwable e )
	   {
           LogManager.getLogger(
               BunkApplicationLauncher.class ).error("Unhandled error", e);
	   }
   }
}
