package bronz.accounting.bunk.party.dao;

import java.util.List;

import bronz.accounting.bunk.party.exception.PartyDaoException;
import bronz.accounting.bunk.party.model.EmployeeMonthlyStatus;
import bronz.accounting.bunk.party.model.Party;
import bronz.accounting.bunk.party.model.PartyClosingBalance;
import bronz.accounting.bunk.party.model.PartyTransaction;
import bronz.accounting.bunk.party.model.Settlement;

public interface PartyDao
{
    String EMPLOYEES = "EMPLOYEES_TYPE";
    String BANKS = "BANKS_TYPE";
    String PARTIES = "PARTIES_TYPE";
    String ALL_ACTIVE_NON_EMPLOYEE_PARTIES = "ALL_ACTIVE_NON_EMPLOYEE_PARTIES";
    String ALL_ACTIVE = "ALL_ACTIVE";
    String ALL = "ALL_PARTIES";
    
    Integer MIN_EMP_ID = 1;
    Integer MIN_BANK_ID = 250;
    Integer MIN_PARTY_ID = 300;
    
    List<Party> getAllParties() throws PartyDaoException;
    void saveParties(final List<Party> parties) throws PartyDaoException;
    
    List<PartyClosingBalance> getPartyList( final String partyType, final Integer date )
            throws PartyDaoException;
    
    List<EmployeeMonthlyStatus> getEmployeesStatus(final Integer date ) throws PartyDaoException;
    
    PartyClosingBalance getParty( final Integer id,  final Integer date ) throws PartyDaoException;
    
    List<PartyTransaction> getTransForPartyByDate( final int partyId, final int startDate, final int endDate,
            final String transTypeFilter, final String detailFilter ) throws PartyDaoException;
    
    List<PartyTransaction> getTransByDate( final int startDate, final int endDate,
            final String transTypeFilter, final String detailFilter ) throws PartyDaoException;
    List<PartyTransaction> getPendingChequeAtOffice(final int date) throws PartyDaoException;
    
    void savePartyTransactions( final List<PartyTransaction> transactions ) throws PartyDaoException;
    
    Settlement getSettlement(final int date) throws PartyDaoException;
    
    void saveSettlement(final Settlement settlement) throws PartyDaoException;
    
}
