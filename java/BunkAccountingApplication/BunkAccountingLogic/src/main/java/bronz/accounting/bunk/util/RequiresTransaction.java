package bronz.accounting.bunk.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresTransaction {
    String failureExceptionText();
}
