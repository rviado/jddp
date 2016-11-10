package org.jddp.persistence.entity.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)


public @interface Index {
	public String fieldName();
	public String type() default "text";
	public String constraint() default "";
	public String using() default "btree";
	public boolean caseMatters() default false;
	public String accessor();
}
