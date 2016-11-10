package org.jddp.persistence.entity.annotation;

public @interface CompositeKey {
	public String[] accessors() default {};
	public String compositor() default "";
}
