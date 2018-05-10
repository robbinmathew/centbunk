package bronz.accounting.bunk.webservice;

import bronz.accounting.bunk.webservice.timedtasks.ScanSalesPortalTask;
import bronz.utilities.general.DateUtil;
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

import java.util.Date;
import java.util.Timer;

public class BunkAccountingWebServiceLauncher
{
    public static final Logger LOG = LogManager.getLogger(BunkAccountingWebServiceLauncher.class );


    private static final long TASKS_INTERVAL = 6 * 60 * 60 * 1000;
   public static void main( String[] args )
   {
	   try
	   {
           AppConfig.IS_TEST_ENV.setValue(BunkAppInitializer.PROD);
           AppConfig.loadProperties();
           //Initialize logger only after the properties are loaded.
           LOG.info("Launching application..");

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
           jerseyServlet.setInitParameter(
               "jersey.config.server.provider.packages","org.codehaus.jackson.jaxrs");

           jerseyServlet.setInitParameter(
               "jersey.config.server.disableMoxyJson", "true");


           Timer timer = null;
           try {
               timer = scheduleTasks();
               jettyServer.start();
               jettyServer.join();
           } finally {
               if (jettyServer != null) {
                   jettyServer.destroy();
               }

               if (timer != null) {
                   timer.cancel();
               }
           }

           LOG.info("Bunk manager launched successfully");
	   }
	   catch ( final Throwable e )
	   {
           LOG.error("Unhandled error", e);
	   }
   }

   private static Timer scheduleTasks() {

       if ("ON".equals(AppConfig.SCHEDULED_TASKS_ENABLED.getStringValue()) ) {
           Timer timer = new Timer(true);
           ScanSalesPortalTask portalTask = new ScanSalesPortalTask();
           portalTask.run();
           timer.scheduleAtFixedRate(portalTask, DateUtil.nextByHourRounded(2), TASKS_INTERVAL);

           LOG.info("ScanSalesPortalTask scheduled. Next execution at " + new Date(portalTask.scheduledExecutionTime()));
           return timer;
       }
       return null;
   }

}
