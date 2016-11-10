package org.jddp.persistence.sql.select;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;


public interface SelectDistinctOn<R> extends SelectConditional<R> {
	public SelectDistinctOn<R> on(Expression<?> firstExpression, Expression<?>... expressions);
	public SelectConditional<R> where(BooleanExpression<?> whereCondition);
	public SelectFrom<R> from(Expression<?> e);
}
