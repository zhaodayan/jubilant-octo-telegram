package cn.mazu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/*    本注解标注在非@Transient的字段上面，出现查询条件的字段上面是为了可读性，若该字段为@Transient,但是又作为查询条件，则可以找个非@Transient的字段来替换。
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface FilterField {
	public abstract String fname();//查询条件名称
	//public abstract String oname();//操作名称，equal，like,between
	public abstract String o2fname() default "";//若是对象字段，要转换成的名称
}
