package bronz.accounting.bunk;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

import bronz.utilities.general.GeneralUtil;

/**
 * The enum to declare all the application constants.
 */
public enum AppConfig {
	 	FIRST_DAY_PROP_NAME( "bronz.first.day", false, true ),
	 	@Deprecated
	    AUTO_SUGGEST_DROP_DOWN_ENABLED("auto.suggest.dropdown.enabled", false, true ),
	    INTERNET_CONNECTION_PROP_NAME( "internet.connection", false, true ),
	    MYSQL_INSTALLATION_HOME_DIR( "mysql.installation.home.dir", false, true ),
	    AUTO_DB_BACKUP_FOLDER( "bronz.auto.database.backup.folder", false, true ),
	    IS_TEST_ENV( "bronz.bunkapp.test.env", false, true ),
	    KEY_STORE_PATH( "bronz.keystore.path", true, true ),
	    KEY_STORE_PASSWORD( "bronz.keystore.password", true, true ),

	    COMPANY_NAME( "bronz.company.name", true ),
	 	CODE_REVISION( "bronz.code.revision", true ),
	 	COMPANY_ADDRESS( "bronz.company.address", true ),
	    EXPENSES_PARTY_ID ( "expenses.party.id", true ),
	    COMPANY_PARTY_ID( "company.party.id", true ),
	    ENABLE_AUTO_DB_BACKUP( "bronz.auto.database.backup", true, true ),
	    MONTHLY_COMPANY_RENT( "company.monthly.rent.amount", true, true ),
	    OFFICE_CASH_PARTY_ID( "off_cash.party.id", true ),
	    OFFICE_CHEQUE_PARTY_ID( "off_cheque.party.id", true ),
	    DEFAULT_BANK_PARTY_ID( "default_bank.party.id", true ),
	    DB_PASSWORD_PROP_NAME( "database.password", true, true ),
	    DB_USERNAME_PROP_NAME( "database.username", true, true ),
	    DB_SCHEMA_NAME_PROP_NAME( "database.schema.name", true, true ),
	    DB_HOST_PROP_NAME( "database.hostname", true, true ),
	    ADMIN_MAIL_IDS_PROP_NAME( "administrator.mail.ids", true, true ),
	    REPORT_GENERATION_FOLDER_PROP_NAME( "reports.folder", true, true ),
	    DEFAULT_REPORT_PATH_PROP_NAME( "reports.temporaryreports.folderpath", true, true ),

	    EMAIL_SERVICE_REPORT_FOLDER_PROP_NAME( "reports.emailservice.folderpath", true, true ),
	    NOTIFICATION_EMAIL_SOURCE_ADDRESS( "emailservice.sourceEmail", true, true ),
	    NOTIFICATION_EMAIL_SOURCE_CREDS( "emailservice.sourceCreds", true, true ),
	    NOTIFICATION_EMAIL_TARGET_ADDRESS( "emailservice.targetEmail", true, true ),
	    NOTIFICATION_EMAIL_ENABLED( "emailservice.enabled", true, true ),
	;

	public static String getAppRootFolder() {
		String folderName = System.getProperty(CONFIG_DIR_PROP_NAME);
		if(StringUtils.isBlank(folderName)) {
			throw new IllegalArgumentException("Please specify the -Dapp.root=<basefolder>");
		}

		new File(folderName+"/logs").mkdirs();


		return folderName;
	}

	public static final String APP_PROP_FILE_NAME = "pbmsApp.properties";
	public static final String PROPERTIES_FILE_FOLDER = getAppRootFolder();

	private static final String CONFIG_DIR_PROP_NAME = "app.root";
	private final String propertyName;
	private final boolean isStatic;
	private final boolean isSecure;

	private AppConfig(final String propName, final boolean isStatic)
	{
		this(propName, isStatic, false);
	}

	private AppConfig(final String propName, final boolean isStatic, final boolean isSecure)
	{
		this.propertyName = propName;
		this.isStatic = isStatic;
		this.isSecure = isSecure;
	}

	public boolean isSecure() {
		return isSecure;
	}

	public void setValue(final String value)
	{
		System.setProperty( this.propertyName, value );
	}
	
	public String toString()
	{
		return this.propertyName;
	}
	
	public <T> T getValue(final Class<T> objectType)
	{
		return GeneralUtil.getPropValueFromSystemProperties(
			this.propertyName, objectType);
	}
	
	public String getStringValue()
	{
		return getValue(String.class);
	}
	
	public String getNullableStringValue()
	{
		return GeneralUtil.getPropValueFromSystemProperties(
			this.propertyName, String.class, null);
	}
	
	public Integer getIntValue()
	{
		return getValue(Integer.class);
	}
	
	public static void checkIfAllPropertiesAvailable() throws IOException
	{
        loadProperties();
		for (AppConfig config : AppConfig.values())
		{
			if (config.isStatic) {
				config.getStringValue();
			}
		}
		if ("ON".equals(ENABLE_AUTO_DB_BACKUP.getStringValue())) {
			MYSQL_INSTALLATION_HOME_DIR.getStringValue();
			AUTO_DB_BACKUP_FOLDER.getStringValue();
		}
	}
	
	public static void loadProperties() throws IOException
	{
        if(BunkAppInitializer.TEST_ENV.equals(AppConfig.IS_TEST_ENV.getStringValue())){
            GeneralUtil.readPropsOnClasspathToSystemProps("pbmsTestApp.properties");
        } else {
            GeneralUtil.readPropsToSystemProps( PROPERTIES_FILE_FOLDER +
                    APP_PROP_FILE_NAME );
        }
		GeneralUtil.readPropsOnClasspathToSystemProps("swVersion.properties");
		AppConfig.AUTO_SUGGEST_DROP_DOWN_ENABLED.setValue( "FALSE" );
	}
}
