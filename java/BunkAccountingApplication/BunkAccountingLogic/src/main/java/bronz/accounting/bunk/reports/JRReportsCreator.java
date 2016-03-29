package bronz.accounting.bunk.reports;

import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.products.model.ProductClosingBalance;
import bronz.accounting.bunk.reports.exception.ReportException;
import bronz.accounting.bunk.reports.model.Report;
import bronz.accounting.bunk.tankandmeter.model.TankClosingStock;

public interface JRReportsCreator
{
    Report createPartyStatement( final PartyClosingBalance party, final int startDate, final int endDate )
            throws ReportException;
    
    Report createCreditAlonePartyStatement( final PartyClosingBalance party, final int startDate, final int endDate )
        throws ReportException;
    
    Report createProdSalesStatement( final ProductClosingBalance prod, final int startDate, final int endDate )
    	throws ReportException;
    
    Report createDSR( final ProductClosingBalance prod, final int startDate, final int endDate )
        	throws ReportException;
    
    Report createTankSalesStatement( final TankClosingStock tank, final int startDate, final int endDate )
		throws ReportException;
    
    Report createReceiptSummaryReport( final int startDate, final int endDate ) throws ReportException;
    
    Report createClosingStatement( final int date ) throws ReportException;
    
    Report createCreditStatusReport( final int date ) throws ReportException;
    
    Report createStockStatusReport( final int date ) throws ReportException;
    
    Report createCashSummaryStatement( final int date ) throws ReportException;
    
    Report createMonthlyCashSummaryStatement( final int date, final int today ) throws ReportException;
    
    Report createProdSalesStatement( final int startDate, final int endDate ) throws ReportException;
    
    Report createStockVariationStatement( final int date ) throws ReportException;
    
    Report createMonthlySalarySummaryStatement( final int date, final int today ) throws ReportException;
}
