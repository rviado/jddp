package org.jddp.persistence.sql.select;

import org.jddp.expression.Expression;

public interface SelectConditional<R> extends SelectGrouped<R> {
	public SelectGrouped<R> groupBy(Expression<?> firstGroubBy, Expression<?>... thenGroupBy);

}
