package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;
import org.jddp.persistence.util.DBType;

public class Func extends AbstractExpression<Func, Func> {

	private final boolean leaf;
	
	final String name;
	
	
	Func(Func f) {
		super(f);
		this.name = f.name;
		this.leaf = f.leaf;
	}
	
	Func(String name, Expression<?> e, int modifier, DBType dbType) {
		super(e, modifier, dbType);
		this.name = name;
		_toString = name + "(" + _toString + ")";
		this.leaf = true;
	}
	
	@Override
	public boolean isLeaf()      {return leaf;};
	
	public Func unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public Func parenthesize() {
		return super.parenthesize();
	}

	@Override
	public Func alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public Func qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public Func copy() {
		return  new Func(this);
	}	
}
