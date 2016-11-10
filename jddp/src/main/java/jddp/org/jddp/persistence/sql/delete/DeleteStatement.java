package org.jddp.persistence.sql.delete;

import org.jddp.expression.BooleanExpression;

public interface DeleteStatement extends DeleteConditional {
	public DeleteConditional where(BooleanExpression<?> whereCondition);
}
