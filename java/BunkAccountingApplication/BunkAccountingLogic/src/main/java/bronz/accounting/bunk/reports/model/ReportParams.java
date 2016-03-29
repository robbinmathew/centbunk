package bronz.accounting.bunk.reports.model;

public class ReportParams
{
    private String reportFileName;
    private String folderLocation;
    
    public ReportParams( final String reportFileName,
            final String folderLocation )
    {
        super();
        this.reportFileName = reportFileName;
        this.folderLocation = folderLocation;
    }
    
    public ReportParams()
    {
        super();
        this.reportFileName = null;
        this.folderLocation = null;
    }
    public String getFolderLocation()
    {
        return folderLocation;
    }
    public String getReportFileName()
    {
        return reportFileName;
    }

}
