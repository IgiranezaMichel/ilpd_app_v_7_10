package Helper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD})
@Retention(RUNTIME)

public @interface TableProp {

    String name() default ".";
    String display() default ".";
    String reportName() default "";
    boolean isHtml() default false;
    boolean isNode() default false;
    boolean isForm() default false;
    boolean isButton() default false;
    boolean dismissed() default false;
    boolean isReport() default false;
    String attribute() default "";
    String type() default "text";
    String className() default "form-control";
    int order() default 0;
    String id() default "";

}
