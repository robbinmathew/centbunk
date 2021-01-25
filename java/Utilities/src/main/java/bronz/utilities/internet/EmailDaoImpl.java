package bronz.utilities.internet;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bronz.utilities.general.ValidationUtil;

public class EmailDaoImpl implements EmailDao
{
    private static final Logger LOG = LogManager.getLogger( EmailDaoImpl.class );

    private final String email;
    private final String creds;

    public EmailDaoImpl(final String email, final String creds) {
        this.email = email;
        this.creds = creds;
    }
    
    public void sendMail( final String toAddresses, final String bccAddresses,
            final String ccAddresses, final String subject,
            final String messageBody, final String[] attachments )
    {
        sendMail( toAddresses, bccAddresses, ccAddresses, subject, messageBody,
                "", attachments );
    }

    public void sendMail( final String toAddresses, final String bccAddresses,
            final String ccAddresses, final String subject,
            final String messageBody, final String attachmentFolder,
            final String[] attachments )
    {
        Transport transport = null;
        try
        {
            final Session session = Session.getInstance(
                    System.getProperties(), new javax.mail.Authenticator() {

                        protected PasswordAuthentication getPasswordAuthentication() {

                            return new PasswordAuthentication("bronz.app.mail@gmail.com", "20thCentury");

                        }

                    } );

            session.setDebug( false );

            // Define a new mail message 
            final Message message = new MimeMessage( session );
            final InternetAddress sender = new InternetAddress(this.email);
            message.setFrom( sender ); 
            addRecipients( Message.RecipientType.TO, toAddresses, message );
            addRecipients( Message.RecipientType.CC, ccAddresses, message );
            addRecipients( Message.RecipientType.BCC, bccAddresses, message );
            message.setSubject( subject );
                 
            // Create a message part to represent the body text 
            final BodyPart messageBodyPart = new MimeBodyPart(); 
            messageBodyPart.setText( messageBody ); 
                 
            //use a MimeMultipart as we need to handle the file attachments 
            final Multipart multipart = new MimeMultipart(); 
                 
            //add the message body to the mime message 
            multipart.addBodyPart( messageBodyPart ); 
                 
            // add any file attachments to the message 
            addAtachments( attachments, attachmentFolder, multipart ); 
            
            // Put all message parts in the message 
            message.setContent( multipart );
            
            transport = session.getTransport();
            transport.connect( sender.getAddress(), this.creds );
            // Send the message 
            transport.sendMessage( message,
                    message.getRecipients( Message.RecipientType.TO ) );
        }
        catch( final Exception e )
        {
            LOG.error( "Failed to send mail", e );
            throw new IllegalStateException( e );
        }
        finally
        {
            if ( null != transport )
            {
                try
                {
                    transport.close();
                }
                catch( MessagingException e )
                {
                    LOG.error( "Failed to close the mail box", e );
                }
            }
        }
    }
    
    private static void addRecipients( final Message.RecipientType type,
            final String addresses, final Message message )
            throws AddressException, MessagingException
    {
        if ( ValidationUtil.isNotBlank( addresses ) )
        {
            for ( String address : addresses.split( "&&" ) )
            {
                message.addRecipient( type, new InternetAddress( address ) );
            }
        }
    }
     
    private static void addAtachments( final String[] attachments,
            final String attachmentFolder, final Multipart multipart )
            throws MessagingException, AddressException 
    { 
        if ( null != attachments )
        {
            for( final String filename : attachments ) 
            { 
                final MimeBodyPart attachmentBodyPart = new MimeBodyPart(); 
                     
                //use a JAF FileDataSource as it does MIME type detection 
                final DataSource source = new FileDataSource(
                        attachmentFolder + "\\" + filename ); 
                attachmentBodyPart.setDataHandler( new DataHandler( source ) ); 
                     
                //assume that the filename you want to send is the same as the 
                //actual file name - could alter this to remove the file path 
                attachmentBodyPart.setFileName(filename); 
                
                //add the attachment 
                multipart.addBodyPart(attachmentBodyPart); 
            }
        }
    } 
     
}
