package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;

public class StrNull extends Str  {

	public static StrNull STRING_NULL = new StrNull();
	
	private StrNull() {
		super(Expression.NULL | Expression.STRING);
		variables.clear();
		_toString = "null";
	}
}
