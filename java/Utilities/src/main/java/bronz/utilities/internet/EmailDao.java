package bronz.utilities.internet;

public interface EmailDao
{
    String SEND_MAIL_USERNAME_PROP_NAME = "send.mail.username";
    String SEND_MAIL_USERPASSWORD_PROP_NAME = "send.mail.password";
    String READ_MAIL_USERNAME_PROP_NAME = "read.mail.username";
    String READ_MAIL_USERPASSWORD_PROP_NAME = "read.mail.password";
    
    void sendMail( final String toAddresses, final String bccAddresses,
            final String ccAddresses, final String subject,
            final String messageBody, final String[] attachments );
    
    void sendMail( final String toAddresses, final String bccAddresses,
            final String ccAddresses, final String subject,
            final String messageBody, final String attachmentFolder,
            final String[] attachments );
}