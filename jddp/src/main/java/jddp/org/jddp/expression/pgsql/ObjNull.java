package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;

public class ObjNull extends Obj  {

	public static ObjNull OBJECT_NULL = new ObjNull();
	
	private ObjNull() {
		super(Expression.NULL | Expression.JSONOBJECT);
		variables.clear();
		_toString = "'null'";
	}
}
