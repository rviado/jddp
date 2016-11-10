package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;
import org.jddp.persistence.util.DBType;

public class Trinary extends AbstractExpression<Trinary, Trinary> {

	public Trinary(Trinary e) {
		super(e);
	}
	
	public Trinary(Expression<?> e1, String operator1, Expression<?> e2, String operator2, Expression<?> e3, int modifier, DBType dbType) {
		super(e1, modifier, dbType);
		StringBuilder str1;

		if (!e1.isLeaf()) {
			this._toString = "(" + this. _toString + ")"; 
		}
		
		if (!e2.isLeaf()) {
			e2 = e2.parenthesize();
		}
		
		if (!e3.isLeaf()) {
			e3 = e3.parenthesize();
		}
		
		
		str1 = new StringBuilder(this._toString).append(" ").append(operator1).append(" ").append(e2).append(" ").append(operator2).append(" ").append(e3);
		
		variables.addAll(e2.getBoundVariables());
		variables.addAll(e3.getBoundVariables());
		fields.addAll(e2.getFields());
		fields.addAll(e3.getFields());
		_toString = str1.toString();;
		
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public Trinary unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public Trinary parenthesize() {
		return super.parenthesize();
	}

	@Override
	public Trinary alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public Trinary qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public Trinary copy() {
		return  new Trinary(this);
	}
}
