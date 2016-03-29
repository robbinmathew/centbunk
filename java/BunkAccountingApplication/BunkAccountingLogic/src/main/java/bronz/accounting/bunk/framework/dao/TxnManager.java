package bronz.accounting.bunk.framework.dao;

public interface TxnManager
{
    void start();
    
    void commit();
    
    void rollback();

    void closeSession();
}
