package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;
import org.jddp.persistence.util.DBType;

public class Unary extends AbstractExpression<Unary, Unary> {

	 private final boolean leaf;
	 
	Unary(Unary e) {
		super(e);
		leaf = e.isLeaf();
	}
	
	
	Unary(Expression<?> e, String operator, int modifier, DBType dbType) {
		super(e, modifier, dbType);
		if (e.isLeaf()) {
			_toString = new StringBuilder(this.toString()).append(" ").append(operator).toString();
		} else {
			_toString = new StringBuilder("(").append(this).append(") ").append(operator).toString();
		}
		leaf = false;
	}
	

	Unary(String operator, Expression<?> e, int modifier, DBType dbType) {
		super(e, modifier, dbType);
		if (e.isLeaf()) {
			_toString =  new StringBuilder(operator).append(" ").append(this).toString();
		} else {
			_toString = new StringBuilder(operator).append("(").append(this).append(")").toString() ;
		}
		leaf = false;
	}

	@Override
	public boolean isLeaf() {
		return leaf;
	}
	
	@Override
	public Unary unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public Unary parenthesize() {
		return super.parenthesize();
	}

	@Override
	public Unary alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public Unary qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public Unary copy() {
		return  new Unary(this);
	}
	
}
