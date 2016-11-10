package org.jddp.persistence.sql.select;

import org.jddp.expression.Expression;

public interface SelectGrouped<R> extends SelectLimited<R>, SelectOffsetted<R> {
	public SelectGrouped<R> groupBy(Expression<?> firstGroubBy, Expression<?>... thenGroupBy);
	public SelectOrdered<R> orderBy(Expression<?> firstExpression, Expression<?>... thenOrderBy);
}
