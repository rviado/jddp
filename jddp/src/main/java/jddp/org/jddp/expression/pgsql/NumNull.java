package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;

public final class NumNull extends Num {

	public static NumNull NUMERIC_NULL = new NumNull();
	
	private NumNull() {
		super(Expression.NULL | Expression.NUMERIC);
		variables.clear();
		_toString = "null";
	}
		
	
}
