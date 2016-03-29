package bronz.accounting.bunk.framework.dao;

import java.sql.Connection;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;

public interface DBUtil
{
    /**
     * Clears the transactions on and after the given date.
     *
     * @param date The date.
     *
     * @throws BunkMgmtException If it fails.
     */
    void clearTrans( final int date ) throws BunkMgmtException;
    
    void executeDbDump(final String backupType) throws BunkMgmtException;
    
    Connection getConnection() throws BunkMgmtException;

    void closeConnection(final Connection connection);

    int getNextDate() throws BunkMgmtException;
    int getFirstDay() throws BunkMgmtException;
    
    /**
     * SELECT * INTO OUTFILE 'data.txt'
    ->          FIELDS TERMINATED BY ','
    ->          FROM table2;

     */
}

