package bronz.accounting.bunk.webservice;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.utilities.general.GeneralUtil;

public class BunkAccountingWebServiceLauncher
{
   public static void main( String[] args )
   {
	   try
	   {
           AppConfig.IS_TEST_ENV.setValue(BunkAppInitializer.PROD);
           AppConfig.loadProperties();
           //Initialize logger only after the properties are loaded.
           LogManager.getLogger(BunkAccountingWebServiceLauncher.class ).info("Launching application..");

           GeneralUtil.analyseJavaEnvironment();
           GeneralUtil.redirectStdoutAndErrToLog();


           ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
           context.setContextPath("/");

           final Server jettyServer = new Server();
           ServerConnector connector=new ServerConnector(jettyServer);
           connector.setPort(8081);
           connector.setHost("localhost");
           jettyServer.setConnectors(new Connector[]{connector});
           jettyServer.setHandler(context);

           ServletHolder jerseyServlet = context.addServlet(
               org.glassfish.jersey.servlet.ServletContainer.class, "/*");
           jerseyServlet.setInitOrder(0);
           context.setErrorHandler(new JsonErrorHandler());

           // Tells the Jersey Servlet which REST service/class to load.
           jerseyServlet.setInitParameter(
               "jersey.config.server.provider.classnames",
               BunkAccountingWebService.class.getCanonicalName());

           try {
               jettyServer.start();
               jettyServer.join();
           } finally {
               jettyServer.destroy();
           }

           LogManager.getLogger(BunkAccountingWebServiceLauncher.class ).info("Bunk manager launched successfully");
	   }
	   catch ( final Throwable e )
	   {
           LogManager.getLogger(BunkAccountingWebServiceLauncher.class ).error("Unhandled error", e);
	   }
   }
}
