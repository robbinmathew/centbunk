package bronz.accounting.bunk;


import javax.swing.SwingUtilities;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import bronz.accounting.bunk.ui.HomePage;

public class BunkApplicationTester
{
   public static void main( String[] args )
   {
	   try
	   {
           AppConfig.IS_TEST_ENV.setValue(BunkAppInitializer.TEST_ENV);
           AppConfig.loadProperties();
           LogManager.getLogger(
               BunkApplicationTester.class ).info("Test launch of bunk application..");

           SwingUtilities.invokeLater(
                   new Runnable()
                   {
                       public void run()
                       {
                    	   try
                    	   {
                    		   HomePage.getInstance();
                    	   }
                    	   catch ( final Throwable e )
                    	   {
                               e.printStackTrace();
                               LogManager.getLogger(
                                   BunkApplicationTester.class ).error("Unhandled error", e);
                    	   }
                       }
                   });
           LogManager.getLogger(
               BunkApplicationTester.class ).info("Bunk manager launched successfully");
	   }
	   catch ( final Throwable e )
	   {
           e.printStackTrace();
           LogManager.getLogger(
               BunkApplicationTester.class ).error("Unhandled error", e);
	   }
   }
}

