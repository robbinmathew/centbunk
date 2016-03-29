package bronz.accounting.bunk.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import bronz.accounting.bunk.framework.dao.TxnManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;

/**
 * Created by pmathew on 1/30/16.
 */
public class TransactionSupportInvocationHandler implements InvocationHandler {
    private final Object target;
    private final TxnManager transactionManager;

    public TransactionSupportInvocationHandler(final Object target, final TxnManager transactionManager) {
        this.target = target;
        this.transactionManager = transactionManager;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] params) throws Throwable {
        Object result;
        final RequiresTransaction annotation = method.getDeclaredAnnotation(RequiresTransaction.class);
        final boolean requireTransaction = annotation != null;
        try {
            if(requireTransaction) {
                transactionManager.start();
            }
            result = method.invoke(target, params);
            if(requireTransaction) {
                transactionManager.commit();
            }
        } catch (Throwable t) {
            if(requireTransaction) {
                transactionManager.rollback();
            }
            String failureText = "DB operation failed";
            if(annotation != null) {
                failureText = annotation.failureExceptionText();
            }
            throw new BunkMgmtException(failureText, t);
        } finally {
            transactionManager.closeSession();
        }
        return result;
    }
}
