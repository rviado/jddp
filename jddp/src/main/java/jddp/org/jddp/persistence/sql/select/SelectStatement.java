package org.jddp.persistence.sql.select;

import org.jddp.expression.Expression;

public interface SelectStatement<R> extends SelectDistinct<R> {
	public SelectFrom<R> from(Expression<?> e);
	public SelectDistinctOn<R> distinct();
}
