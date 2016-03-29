package bronz.utilities.swing.table;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataTable
{
    int idCol() default 0;
    boolean uniqueIdCol() default false;
    int minNoOfRows() default 10;
    boolean fixNoOfRows() default false;
    String errorValidationMethod() default "";
    String warningValidationMethod() default "";
    String editableColumnListMethod() default "";
}
