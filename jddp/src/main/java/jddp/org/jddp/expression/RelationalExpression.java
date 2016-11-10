package org.jddp.expression;

import java.util.Collection;

public interface RelationalExpression<ExpressionType extends Expression<?>, ArgType extends Expression<?>, PrimitiveArgType> extends Expression<ExpressionType> {
	
	public BooleanExpression<?> eq(ArgType e);
	public BooleanExpression<?> eq(PrimitiveArgType e);
	
	public BooleanExpression<?> neq(ArgType e);
	public BooleanExpression<?> neq(PrimitiveArgType e);
	
	public BooleanExpression<?> lt(ArgType e);
	public BooleanExpression<?> lt(PrimitiveArgType e);
	
	public BooleanExpression<?> lte(ArgType e);
	public BooleanExpression<?> lte(PrimitiveArgType e);
	
	public BooleanExpression<?> gt(ArgType e);
	public BooleanExpression<?> gt(PrimitiveArgType e);
	
	public BooleanExpression<?> gte(ArgType e);
	public BooleanExpression<?> gte(PrimitiveArgType e);
	
	public BooleanExpression<?> isNull();
	public BooleanExpression<?> isNotNull();
	
	public BooleanExpression<?> in(Expression<?> e);
	public BooleanExpression<?> in(Collection<?> c);
	
	public BooleanExpression<?> notIn(Expression<?> e);
	public BooleanExpression<?> notIn(Collection<?> c);
	
}
