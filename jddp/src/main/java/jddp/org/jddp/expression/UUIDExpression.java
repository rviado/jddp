package org.jddp.expression;

import java.util.UUID;

public interface UUIDExpression<E extends StringExpression<?>> extends StringExpression<E> {

	public BooleanExpression<?> eq(UUID uuid);
	public BooleanExpression<?> neq(UUID uuid);	
	public BooleanExpression<?> lt(UUID uuid);
	public BooleanExpression<?> lte(UUID uuid);
	public BooleanExpression<?> gt(UUID uuid);
	public BooleanExpression<?> gte(UUID uuid);
	
}
