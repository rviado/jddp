package org.jddp.expression.pgsql;

import org.jddp.expression.Expression;

public class LIST extends AbstractExpression<LIST, LIST> {

	final int length;
	
	
	LIST(LIST l) {
		super(l);
		length = l.length;
	}
	
	LIST(int modifier, Expression<?> firstExpr, Expression<?>... exprs ) {
		super(firstExpr, modifier, firstExpr.getDBType());
		int l = 1;
		
		for (Expression<?> e : exprs) {
			l++;
			variables.addAll(e.getBoundVariables());
			fields.addAll(e.getFields());
			_toString = new StringBuilder(_toString).append(", ").append(e).toString();
		}
		length = l;
		
	}
	
	public LIST(Expression<?> firstExpr, Expression<?>... exprs ) {
		super(firstExpr);
		int l = 1;
		
		for (Expression<?> e : exprs) {
			l++;
			variables.addAll(e.getBoundVariables());
			fields.addAll(e.getFields());
			_toString = new StringBuilder(_toString).append(", ").append(e.toString()).toString();
		}
		length = l;
		
	}
	
	public LIST() {
		super(0, null);
		length = 0;
	}
	
	public LIST add(Expression<?> e) {
		if (length <= 0) {
			return new LIST(e);
		}
		return new LIST(this, e);
		 
	}
	
	public boolean isEmpty() {
		return length == 0;
	}
	
	public int length() {
		return length;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	public LIST unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public LIST parenthesize() {
		return super.parenthesize();
	}

	@Override
	public LIST alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public LIST qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public LIST copy() {
		return  new LIST(this);
	}	
	
}
