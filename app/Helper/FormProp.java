package Helper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD,TYPE})
@Retention(RUNTIME)

public @interface FormProp {

    String name() default ".id";
    String display() default ".";
    String formName() default ".";
    String attribute() default "";
    String newNull() default "";
    String type() default "text";
    String className() default "form-control";
    String id() default "";
    String reportName() default "";
    String eqValue() default ".";
    String eqProp() default ".";
    int order() default 0;
    int limit() default 500;
    boolean isLimited() default false;
    boolean isRel() default true;
    boolean isReport() default false;
    boolean isNaN() default true;
    boolean Aa() default false;
    boolean isCheck() default false;
    boolean escape() default false;
    boolean isRadio() default false;
    boolean isNode() default false;
    boolean isCal() default false;
    boolean isTime() default false;
    boolean isUpload() default false;
    boolean isDownload() default false;
    boolean isDisabled() default false;
    boolean isGroup() default false;

}
