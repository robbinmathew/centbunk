package bronz.accounting.bunk.reports.model;

public enum ReportFormat
{
    EXCEL(".xls","Excel"),
    PDF(".pdf","PDF"),
    HTML(".html","HTML");
    private final String extension;
    private final String label;
    
    private ReportFormat( final String extension,
            final String label )
    {
        this.extension = extension;
        this.label = label;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getLabel()
    {
        return label;
    }
}
