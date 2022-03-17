package bronz.accounting.bunk.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import bronz.accounting.bunk.framework.dao.TxnManager;
import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import bronz.accounting.bunk.framework.exceptions.BunkValidationException;

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
        if (requireTransaction) {
            try {
                transactionManager.start();
                result = method.invoke(target, params);
                transactionManager.commit();
            } catch (Throwable t) {
                transactionManager.rollback();
                if (t instanceof BunkValidationException) {
                    throw t;
                } else if (t instanceof InvocationTargetException && t.getCause() != null && t.getCause() instanceof BunkValidationException) {
                    throw t.getCause();
                }
                String failureText = "DB operation failed";
                if(annotation != null) {
                    failureText = annotation.failureExceptionText();
                }
                throw new BunkMgmtException(failureText, t);
            } finally {
                transactionManager.closeSession();
            }
        } else {
            result = method.invoke(target, params);
        }

        return result;
    }
}
