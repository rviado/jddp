package org.jddp.expression;


public interface BooleanExpression<E extends BooleanExpression<?>> extends RelationalExpression<E, BooleanExpression<?>, Boolean> {

	public BooleanExpression<?> and(BooleanExpression<?> bool);
	public BooleanExpression<?> and(Boolean bool);
	
	public BooleanExpression<?> or(BooleanExpression<?> bool);	
	public BooleanExpression<?> or(Boolean bool);
	
	public BooleanExpression<?> negate();
	
	
}
