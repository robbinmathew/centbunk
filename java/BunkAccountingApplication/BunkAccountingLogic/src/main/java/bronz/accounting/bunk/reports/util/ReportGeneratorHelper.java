package bronz.accounting.bunk.reports.util;

import java.io.File;
import java.util.Random;

import bronz.accounting.bunk.AppConfig;
import bronz.utilities.general.DateUtil;

public class ReportGeneratorHelper
{
	/**
	 * Gets the file name with the path and time stamp.
	 * 
	 * @param fileName
	 *            The file name to which path and time stamp is to be appended.
	 * 
	 * @return The fully qualified file name.
	 */
	public static String getFileName( final String fileName, final int date, final String extension )
	{
		return getFileName( fileName, date, extension,
				AppConfig.DEFAULT_REPORT_PATH_PROP_NAME.getValue(String.class ) );
	}
	
	/**
     * Gets the file name with the path and time stamp.
     * 
     * @param fileName
     *            The file name to which path and time stamp is to be appended.
     * 
     * @return The fully qualified file name.
     */
    public static String getFileName( final String fileName, final int date, final String extension,
            final String reportFolder )
    {
        return getFileName( fileName, reportFolder, date, extension );
    }
	
	/**
     * Gets the file name with the path and time stamp.
     * 
     * @param fileName
     *            The file name to which path and time stamp is to be appended.
     * 
     * @return The fully qualified file name.
     */
    public static String getCompiledSubReportFolder()
    {
    	final String subReportFolderPath = AppConfig.DEFAULT_REPORT_PATH_PROP_NAME.getStringValue() +
                "SUB_REPORT_TEMPLATES\\";
    	final File subReportFolder = new File(subReportFolderPath);
    	if (!subReportFolder.exists())
    	{
    		subReportFolder.mkdirs();
    	}
        return subReportFolderPath;
    }
    
    /**
     * Gets the file name with the path and time stamp.
     * 
     * @param fileName
     *            The file name to which path and time stamp is to be appended.
     * 
     * @return The fully qualified file name.
     */
    static String getFileName( final String fileName,
            final String destinationFolder, final int date, final String extension )
    {
        final StringBuilder fullFileName = new StringBuilder(
                destinationFolder );
        fullFileName.append( fileName );
        fullFileName.append( '_' );
        fullFileName.append( DateUtil.getDateString( date ) );
        fullFileName.append( '_' );
        fullFileName.append( new Random().nextInt( 9999 ) );
        
        fullFileName.append( extension );
        return fullFileName.toString();
    }
    
    static String getTitleName( final String title, final int startDate,
            final int endDate )
    {
        final StringBuilder fullFileName = new StringBuilder( title );
        fullFileName.append( " FROM " );
        fullFileName.append( DateUtil.getDateString( startDate ) );
        fullFileName.append( " TO " );
        fullFileName.append( DateUtil.getDateString( endDate ) );
        
        return fullFileName.toString();
    }
}
