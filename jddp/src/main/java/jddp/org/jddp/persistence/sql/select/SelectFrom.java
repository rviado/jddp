package org.jddp.persistence.sql.select;

import org.jddp.expression.BooleanExpression;


public interface SelectFrom<R> extends SelectConditional<R> {
	public SelectConditional<R> where(BooleanExpression<?> whereCondition);
}
