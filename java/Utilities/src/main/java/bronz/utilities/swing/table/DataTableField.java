package bronz.utilities.swing.table;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataTableField
{
    String columnName();
    String displayName() default "";
    int columnNum();
    //String defaultValue() default "";
    boolean allowNull() default true;
    boolean allowEmpty() default true;
    boolean isEditable() default true;
    int preferedWidth();
    boolean isCustomColEditor() default false;
    String getColEditorProviderClass() default "";
    String getColEditorProviderMethod() default "";
}
