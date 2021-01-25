package bronz.utilities.internet;

public interface EmailDao
{
    void sendMail( final String toAddresses, final String bccAddresses,
            final String ccAddresses, final String subject,
            final String messageBody, final String[] attachments );
    
    void sendMail( final String toAddresses, final String bccAddresses,
            final String ccAddresses, final String subject,
            final String messageBody, final String attachmentFolder,
            final String[] attachments );
}