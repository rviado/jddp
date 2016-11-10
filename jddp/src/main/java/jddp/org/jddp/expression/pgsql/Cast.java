package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;
import org.jddp.persistence.util.DBType;

public class Cast extends AbstractExpression<Cast, Cast> {

	 private final boolean leaf;
	 
	Cast(Cast e) {
		super(e);
		leaf = e.isLeaf();
	}
	
	Cast(Expression<?> e, int modifier, DBType dbType) {
		super(e, modifier, dbType);
		if (e.isLeaf()) {
			_toString = new StringBuilder("CAST(").append(this).append(" as ").append(dbType.toString()).append(")").toString();
		} else {
			_toString = new StringBuilder("CAST((").append(this).append(") as ").append(dbType.toString()).append(")").toString();
		}
		leaf = true;
	}
	
	@Override
	public boolean isLeaf() {
		return leaf;
	}
	
	@Override
	public Cast unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public Cast parenthesize() {
		return super.parenthesize();
	}

	@Override
	public Cast alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public Cast qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public Cast copy() {
		return  new Cast(this);
	}

		
}
