package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;
import org.jddp.persistence.util.DBType;

public class Binary extends AbstractExpression<Binary, Binary> {

	public Binary(Binary e) {
		super(e);
	}
	
	public Binary(Expression<?> e1, String operator, Expression<?> e2, int modifier, DBType dbType) {
		super(e1, modifier, dbType);
		StringBuilder str1;
		
		if (!e1.isLeaf()) {
			this._toString = "(" + this._toString + ")"; 
		}
		
		if (!e2.isLeaf()) {
			e2 = e2.parenthesize();
		}
		
		str1 = new StringBuilder(this._toString).append(" ").append(operator).append(" ").append(e2);
		
		
		variables.addAll(e2.getBoundVariables());
		fields.addAll(e2.getFields());
		_toString = str1.toString();
		
	}
	

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public Binary unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public Binary parenthesize() {
		return super.parenthesize();
	}

	@Override
	public Binary alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public Binary qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public Binary copy() {
		return  new Binary(this);
	}	

}
