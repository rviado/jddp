package org.jddp.persistence.sql.select;

import org.jddp.expression.Expression;

public interface SelectOrdered<R> extends  SelectLimited<R>, SelectOffsetted<R> {
	public SelectOrdered<R> orderBy(Expression<?> firstExpression, Expression<?>... thenOrderBy);
	public SelectOrdered<R>  asc();
	public SelectOrdered<R> desc();
}


