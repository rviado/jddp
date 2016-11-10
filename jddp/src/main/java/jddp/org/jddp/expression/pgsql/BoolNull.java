package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;

public final class BoolNull extends Bool {

	public static BoolNull BOOLEAN_NULL = new BoolNull();
	
	private BoolNull() {
		super(Expression.NULL | Expression.BOOLEAN);
		variables.clear();
		_toString = "null";
	}
	
}
