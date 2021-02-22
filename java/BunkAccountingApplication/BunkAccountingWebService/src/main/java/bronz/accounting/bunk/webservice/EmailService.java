package bronz.accounting.bunk.webservice;

import bronz.accounting.bunk.AppConfig;
import bronz.utilities.internet.EmailDao;
import bronz.utilities.internet.EmailDaoImpl;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;

public class EmailService {
    private static EmailDao INSTANCE;
    public static void init() {
        if ("ON".equals(AppConfig.NOTIFICATION_EMAIL_ENABLED.getStringValue())) {
            INSTANCE = new EmailDaoImpl(AppConfig.NOTIFICATION_EMAIL_SOURCE_ADDRESS.getStringValue(), AppConfig.NOTIFICATION_EMAIL_SOURCE_CREDS.getStringValue());
        }
    }

    public static void notify(String subject, String message) {
        sendMailInternal(AppConfig.NOTIFICATION_EMAIL_TARGET_ADDRESS.getStringValue(), null, null, subject, message, null);
    }

    public static void notifyError(Throwable t, String context) {
        sendMailInternal(AppConfig.NOTIFICATION_EMAIL_TARGET_ADDRESS.getStringValue(), null, null, "Unexpected error in APP:" + t.getMessage(),  "Err context:" + context + ", exception\n" + ExceptionUtils.getStackTrace(t), null);
    }

    public static void notifyError(Throwable t, String context, String subject) {
        sendMailInternal(AppConfig.NOTIFICATION_EMAIL_TARGET_ADDRESS.getStringValue(), null, null, "ERROR:" + subject,  "Err context:" + context + ", exception\n" + ExceptionUtils.getStackTrace(t), null);
    }

    private static void sendMailInternal( final String toAddresses, final String bccAddresses, final String ccAddresses, final String subject,
                   final String messageBody, final String[] attachments ){
        if (INSTANCE != null) {
            try {
                INSTANCE.sendMail(toAddresses, bccAddresses, ccAddresses, subject, messageBody, attachments);
            } catch (Exception e) {
                LogManager.getLogger(EmailService.class).error("Failed to send the email: S: " + subject + " , Body:" + messageBody , e);
            }
        } else {
            LogManager.getLogger(EmailService.class).info("Ignored sending email To:" + toAddresses + " S:" + subject + " , Body:" + messageBody);
        }
    }


}
