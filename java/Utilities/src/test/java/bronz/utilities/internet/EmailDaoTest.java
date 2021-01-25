package bronz.utilities.internet;

import junit.framework.TestCase;

public class EmailDaoTest extends TestCase
{
    public void setUp()
    {
        System.setProperty( "mail.transport.protocol", "smtps" );
        System.setProperty( "mail.smtps.host", "smtp.gmail.com" );
        System.setProperty("mail.smtp.ssl.enable", "true");
        System.setProperty( "mail.smtps.port", "465" );
        System.setProperty( "mail.smtps.auth", "true" );
    }


    public void testSendEmail() throws Exception
    {
        final EmailDao emailDao = new EmailDaoImpl("", "");
        emailDao.sendMail( "Prabin Mathew Elanjickal <mathewprabin@yahoo.co.in>",
                null, null, "No subject pls", "Test mail body", null );
    }

}
