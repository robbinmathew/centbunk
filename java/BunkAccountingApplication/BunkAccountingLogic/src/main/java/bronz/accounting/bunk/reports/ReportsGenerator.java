package bronz.accounting.bunk.reports;

import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.reports.exception.ReportException;
import bronz.accounting.bunk.reports.model.ReportFormat;

public interface ReportsGenerator
{
    String generatePartyStatement( final ReportFormat format,
            final PartyClosingBalance party, final int startDate, final int endDate )
            throws ReportException;
    
    String generateClosingStatement( final ReportFormat format, final int date ) throws ReportException;
    
    String generateClosingStatement( final ReportFormat format, final int date, final String reportFolder )
            throws ReportException;
}
