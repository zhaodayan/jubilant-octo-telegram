package cn.mazu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface DisplayField {
	public abstract String cname();//显示的字段汉语名称
	public abstract int etag() default 0;//easyui画表格时用到的，field:'FX'，同一表格中区分列,同一实体中保证值不同，否则最末将覆盖前面相同值的那一列
	public abstract String dename() default "";//detail-entity，被某一子类引用，子类实体的完整名称；dename即detail name
	public abstract int trindex() default 1;//跨行列，表头tr>1
	public abstract String rowspan() default "1";
	public abstract String colspan() default "1";
	public abstract boolean exportflag() default true;//导出字段，默认导出
}
