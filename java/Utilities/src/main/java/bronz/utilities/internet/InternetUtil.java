package bronz.utilities.internet;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Folder;
import javax.mail.Store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InternetUtil
{
    private static final Logger LOG = LogManager.getLogger( InternetUtil.class );
    
    private InternetUtil()
    {
        super();
    }
    
    public static boolean isInternetAvailable()
    {
        boolean isConnected = checkConnectionToUrl( "http://www.google.com");
        // If connection not available, try connecting to another url
        if ( !isConnected )
        {
            isConnected = checkConnectionToUrl( "http://www.yahoo.com");
        }
        return isConnected;
    }
    
    private static boolean checkConnectionToUrl( final String urlToConnect )
    {
        boolean isConnected = false;
        try
        {
            final URL url = new URL( urlToConnect );
            final URLConnection conn = url.openConnection();  
            conn.setConnectTimeout( 5000 );  
            conn.setReadTimeout( 5000 );  
            conn.getInputStream();
            isConnected = true;
        }
        catch( final Exception e )
        {
            isConnected = false;
            LOG.debug( "Failed to connect to url", e );
        }
        return isConnected;
    }
     
    public static void readMailsWithImap( final String username,
            final String password, final String folderName )
            throws MessagingException, AddressException, IOException
    {
        LOG.info( "Reading mails for user :" + username );
        
        Folder folder = null;
        Store store = null;
        try
        {
            final Properties props = System.getProperties();
            
            LOG.debug( "Connecting to server " +
                    props.getProperty( "mail.imap.host" ) );
            final Session session = Session.getDefaultInstance( props );
     
            store = session.getStore( "imap" );
            //TODO to check if the redundant mail server mentioning is required
            store.connect( props.getProperty( "mail.imap.host" ), username, password );
            LOG.debug( "Connected to server" );
            
            if ( null != store.getDefaultFolder() &&
                    null != store.getDefaultFolder().getFolder( folderName ) )
            {
                folder = store.getDefaultFolder().getFolder( folderName );
                folder.open( Folder.READ_ONLY );
                for ( final Message message : folder.getMessages() )
                {
                    if ( Multipart.class.isInstance( message.getContent() ) )
                    {
                        /*final Multipart multipart =
                            (Multipart) message.getContent();*/
                        System.out.println( message.getSubject() );
                    }
                }
                
            }
/*              if (messagecontentObject instanceof Multipart)
              {
                  printData("Found Email with Attachment");
                  sender = ((InternetAddress) message.getFrom()[0]).getPersonal();
     
                  // If the "personal" information has no entry, check the address for the sender information
                  printData("If the personal information has no entry, check the address for the sender information.");
     
                  if (sender == null)
                  {
                      sender = ((InternetAddress) message.getFrom()[0]).getAddress();
                      printData("sender in NULL. Printing Address:" + sender);
                  }
                  printData("Sender -." + sender);
                  
                  // Get the subject information
                  subject = message.getSubject();
     
                  printData("subject=" + subject);
     
                  // Retrieve the Multipart object from the message
                  multipart = (Multipart) message.getContent();
                  
                  printData("Retrieve the Multipart object from the message");
                  
                  // Loop over the parts of the email
                  for (int i = 0; i < multipart.getCount(); i++)
                  {
                      // Retrieve the next part
                      part = multipart.getBodyPart(i);
     
                      // Get the content type
                      contentType = part.getContentType();
                      
                      // Display the content type
                      printData("Content: " + contentType);
     
                      if (contentType.startsWith("text/plain"))
                      {
                          printData("---------reading content type text/plain  mail -------------");
                      }
                      else
                      {
                          //Retrieve the file name
                          String fileName = part.getFileName();
                          printData("retrive the fileName="+ fileName);
                      }
                  }
              }
              else
              {
                  printData("Found Mail Without Attachment");
                  sender = ((InternetAddress) message.getFrom()[0]).getPersonal();
                  
                  // If the "personal" information has no entry, check the address for the sender information
                  printData("If the personal information has no entry, check the address for the sender information.");
     
                  if (sender == null)
                  {
                      sender = ((InternetAddress) message.getFrom()[0]).getAddress();
                      printData("sender in NULL. Printing Address:" + sender);
                  }
     
                 // Get the subject information
                  subject = message.getSubject();
                  printData("subject=" + subject);
              }
          }
*/
        }
        finally
        {
            if ( null != folder && folder.isOpen() )
            {
                folder.close( true );
            }
            if ( null != store && store.isConnected() )
            {
                store.close();
            }
        }
    }
    
    public static void sendMail( final String mailServer, final String from,
            final String to, final String subject, final String messageBody, 
            final String[] attachments ) throws MessagingException,
            AddressException 
    { 
        // Setup mail server 
        final Properties props = System.getProperties();
        props.put( "mail.transport.protocol", "smtps");
        props.put( "mail.smtps.host", mailServer);
        props.put( "mail.smtps.auth", "true");
        

        
        // Get a mail session 
        final Session session = Session.getDefaultInstance( props );/*, new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( "hismajesty.prabin@gmail.com", "isvuasna" );
             }   
        });*/
        
        session.setDebug( true );
        
        // Define a new mail message 
        final Message message = new MimeMessage( session ); 
        message.setFrom( new InternetAddress( from ) ); 
        message.addRecipient( Message.RecipientType.TO,
                new InternetAddress( to ) ); 
        message.setSubject(subject); 
             
        // Create a message part to represent the body text 
        final BodyPart messageBodyPart = new MimeBodyPart(); 
        messageBodyPart.setText( messageBody ); 
        Transport transport = session.getTransport();
             
        //use a MimeMultipart as we need to handle the file attachments 
        final Multipart multipart = new MimeMultipart(); 
             
        //add the message body to the mime message 
        multipart.addBodyPart( messageBodyPart ); 
             
        // add any file attachments to the message 
        addAtachments( attachments, multipart ); 
        
        // Put all message parts in the message 
        message.setContent( multipart ); 
        
        transport.connect
        ( mailServer, 465,"hismajesty.prabin@gmail.com", "isvuasna" );
        // Send the message 
        transport.sendMessage( message, message.getRecipients( Message.RecipientType.TO ) );
        
        transport.close();
    } 
     
    private static void addAtachments( final String[] attachments,
            final Multipart multipart ) throws MessagingException,
            AddressException 
    { 
        for(int i = 0; i<= attachments.length -1; i++) 
        { 
            final String filename = attachments[ i ]; 
            final MimeBodyPart attachmentBodyPart = new MimeBodyPart(); 
                 
            //use a JAF FileDataSource as it does MIME type detection 
            final DataSource source = new FileDataSource( filename ); 
            attachmentBodyPart.setDataHandler( new DataHandler( source ) ); 
                 
            //assume that the filename you want to send is the same as the 
            //actual file name - could alter this to remove the file path 
            attachmentBodyPart.setFileName(filename); 
            
            //add the attachment 
            multipart.addBodyPart(attachmentBodyPart); 
        } 
    } 
     
    public static void main(String[] args) 
        { 
            try 
            { 
                /*String server="smtp.gmail.com"; 
                String from="hismajesty.prabin@gmail.com"; 
                String to = "hismajesty.prabin@gmail.com"; 
                String subject="Test"; 
                String message="Testing"; 
          
             
                //InternetUtil.sendMail(server,from,to,subject,message,filenames);
          InternetUtil.readMailsWithImap( "imap.gmail.com", "993",
                  "hismajesty.prabin@gmail.com", "ipasswo", "inbox" );*/
                System.getProperties().load( new FileInputStream( "C:\\BunkManager\\pbmsApp.properties" ) );
                String[] filenames = 
                {"C:\\BunkManager\\pbmsApp.properties"};       
                ///final EmailDao dao = new EmailDaoImpl();
                ///dao.sendMail( "hismajesty.prabin@gmail.com&&mathewprabin@yahoo.co.in",
                ///        null, null, "subject", "messageBody", filenames );
            } 
            catch(Exception e) 
            { 
                e.printStackTrace(System.out);  
            } 
             
    } 
    
}
