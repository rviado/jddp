package org.jddp.persistence.sql.select;

import org.jddp.expression.BooleanExpression;


public interface SelectDistinct<R> extends SelectConditional<R> {
	public SelectConditional<R> where(BooleanExpression<?> whereCondition);
}
