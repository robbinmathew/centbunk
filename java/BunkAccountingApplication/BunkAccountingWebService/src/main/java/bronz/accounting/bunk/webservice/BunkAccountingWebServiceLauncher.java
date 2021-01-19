package bronz.accounting.bunk.webservice;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


import bronz.accounting.bunk.AppConfig;
import bronz.accounting.bunk.BunkAppInitializer;
import bronz.utilities.general.GeneralUtil;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.File;

public class BunkAccountingWebServiceLauncher {
    public static void main(String[] args) {
        try {


            AppConfig.IS_TEST_ENV.setValue(BunkAppInitializer.PROD);
            AppConfig.loadProperties();
            //Initialize logger only after the properties are loaded.
            LogManager.getLogger(BunkAccountingWebServiceLauncher.class).info("Launching application..");

            GeneralUtil.analyseJavaEnvironment();
            GeneralUtil.redirectStdoutAndErrToLog();


            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setWelcomeFiles(new String[]{ "main.html" });
            final Server jettyServer = new Server();
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(AppConfig.KEY_STORE_PATH.getStringValue());
            sslContextFactory.setKeyStorePassword(AppConfig.KEY_STORE_PASSWORD.getStringValue());
            sslContextFactory.setKeyManagerPassword(AppConfig.KEY_STORE_PASSWORD.getStringValue());

            HttpConfiguration httpsConfiguration = new HttpConfiguration();
            SecureRequestCustomizer secureRequestCustomizer = new SecureRequestCustomizer();
            httpsConfiguration.addCustomizer(secureRequestCustomizer);

            ServerConnector serverConnector = new ServerConnector(jettyServer,
                    new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                    new HttpConnectionFactory(httpsConfiguration));
            serverConnector.setHost("0.0.0.0");
            serverConnector.setPort(8001);
            serverConnector.setIdleTimeout(15000);

            jettyServer.setConnectors(new Connector[] { serverConnector });


            //Binds the web service
            ServletHolder jerseyServlet = context.addServlet(
                    org.glassfish.jersey.servlet.ServletContainer.class, "/api/*");

            //Binds the servlet to serve html, css and js
            DefaultServlet defaultServlet = new DefaultServlet();
            ServletHolder holderPwd = new ServletHolder("default", defaultServlet);

            String webResourceBaseFolder =  System.getProperty("webResourceBaseFolder"); //Use this prop to load the resources from a folder for development purpose

            if (StringUtils.isBlank(webResourceBaseFolder)) {
                ClassLoader loader = BunkAccountingWebServiceLauncher.class.getClassLoader();
                File indexLoc = new File(loader.getResource("web/login.html").getFile());
                webResourceBaseFolder = indexLoc.getParentFile().getAbsolutePath();
            }

            holderPwd.setInitParameter("resourceBase", webResourceBaseFolder);
            context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");


            context.addServlet(holderPwd, "/*");


            Constraint constraint = new Constraint();
            constraint.setName(Constraint.__FORM_AUTH);
            constraint.setRoles(new String[]{"user", "admin", "moderator"});
            constraint.setAuthenticate(true);

            ConstraintMapping constraintMapping = new ConstraintMapping();
            constraintMapping.setConstraint(constraint);
            constraintMapping.setPathSpec("/*");


            ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
                        securityHandler.addConstraintMapping(constraintMapping);
            securityHandler.setLoginService(InMemoryLoginService.getInstance().getLoginService());

            FormAuthenticator authenticator = new FormAuthenticator("/login.html", "/login-error.html", false);
            securityHandler.setAuthenticator(authenticator);


            context.setSecurityHandler(securityHandler);
            context.setContextPath("/*");

            context.setErrorHandler(new JsonErrorHandler());

            // Tells the Jersey Servlet which REST service/class to load.
            jerseyServlet.setInitParameter(
                    "jersey.config.server.provider.classnames",
                    BunkAccountingWebService.class.getCanonicalName());
            jerseyServlet.setInitParameter(
                    "jersey.config.server.provider.packages", "org.codehaus.jackson.jaxrs");

            jerseyServlet.setInitParameter(
                    "jersey.config.server.disableMoxyJson", "true");
            ContextHandlerCollection contexts = new ContextHandlerCollection();
            jettyServer.setHandler(contexts);

            NCSARequestLog requestLog = new NCSARequestLog();
            requestLog.setFilename(AppConfig.getAppRootFolder() + "/logs/accesslog_yyyy_mm_dd.log");
            requestLog.setFilenameDateFormat("yyyy_MM_dd");
            requestLog.setRetainDays(30);
            requestLog.setAppend(true);
            requestLog.setExtended(true);
            requestLog.setLogCookies(false);
            requestLog.setLogTimeZone("UTC");
            RequestLogHandler requestLogHandler = new RequestLogHandler();
            requestLogHandler.setRequestLog(requestLog);
            requestLogHandler.setHandler(context);
            contexts.addHandler(requestLogHandler);

            try {
                jettyServer.start();
                jettyServer.join();
            }catch (Throwable t) {
                LogManager.getLogger(BunkAccountingWebServiceLauncher.class).error("Jetty server start failed", t);
            } finally {
                jettyServer.destroy();
            }

            LogManager.getLogger(BunkAccountingWebServiceLauncher.class).info("Bunk manager launched successfully");
        } catch (final Throwable e) {
            LogManager.getLogger(BunkAccountingWebServiceLauncher.class).error("Unhandled error", e);
        }
    }
}
