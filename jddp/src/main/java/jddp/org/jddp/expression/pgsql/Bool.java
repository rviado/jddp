package org.jddp.expression.pgsql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.pgsql.util.NamedParameter;
import org.jddp.persistence.util.DBType;

public class Bool extends AbstractExpression<Bool, BooleanExpression<?>> implements BooleanExpression<BooleanExpression<?>>  {

	private final boolean leaf;
	
	protected  Bool(int modifier) {
		super(modifier, DBType.BOOLEAN);
		leaf = true;
	}
	
	public Bool(Boolean bool) {
		super(new Var(bool));
		leaf = true;
	}

	public Bool(Expression<?> e) {
		super(e);
		if (!isBoolean()) {
			throw new IllegalArgumentException("(" + e.getClass() + ") " + e.unBoundVariables() + " : is not a boolean expression");
		}
		leaf = e.isLeaf();
	}
	
	@Override
	public BooleanExpression<?> and(BooleanExpression<?> bool) {
		return new Bool(new Binary(this, "AND", bool, BOOLEAN, DBType.BOOLEAN));
	}
	@Override
	public BooleanExpression<?> and(Boolean bool) {
		return new Bool(new Binary(this, "AND", new Var(bool), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> or(BooleanExpression<?> bool) {
		return new Bool(new Binary(this, "OR", bool, BOOLEAN, DBType.BOOLEAN));
	}
	@Override
	public BooleanExpression<?> or(Boolean bool) {
		return new Bool(new Binary(this, "OR", new Var(bool), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> negate() {
		return new Bool(new Unary("NOT", this, BOOLEAN, DBType.BOOLEAN));
	}

	@Override 
	public BooleanExpression<?> isNull() {
		return new Bool(new Unary(this, "IS NULL" ,  BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override 
	public BooleanExpression<?> isNotNull() {
		return new Bool(new Unary(this, "IS NOT NULL" ,  BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> eq(BooleanExpression<?> e) {
		return new Bool(new Binary(this, "=", e,  BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> eq(Boolean b) {
		return new Bool(new Binary(this, "=", new Var(b),  BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> neq(BooleanExpression<?> e) {
		return new Bool(new Binary(this, "<>", e,  BOOLEAN, DBType.BOOLEAN));
	}
	@Override
	public BooleanExpression<?> neq(Boolean b) {
		return new Bool(new Binary(this, "<>", new Var(b),  BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lt(BooleanExpression<?> e) {
		return new Bool(new Binary(this, "<", e,  BOOLEAN, DBType.BOOLEAN));
	}
	@Override
	public BooleanExpression<?> lt(Boolean b) {
		return new Bool(new Binary(this, "<", new Var(b),  BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lte(BooleanExpression<?> e) {
		return new Bool(new Binary(this, "<=", e,  BOOLEAN, DBType.BOOLEAN));
	}
	@Override
	public BooleanExpression<?> lte(Boolean b) {
		return new Bool(new Binary(this, "<=", new Var(b),  BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gt(BooleanExpression<?> e) {
		return new Bool(new Binary(this, ">", e,  BOOLEAN, DBType.BOOLEAN));
	}
	@Override
	public BooleanExpression<?> gt(Boolean b) {
		return new Bool(new Binary(this, ">", new Var(b),  BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gte(BooleanExpression<?> e) {
		return new Bool(new Binary(this, ">=", e,  BOOLEAN, DBType.BOOLEAN));
	}
	@Override
	public BooleanExpression<?> gte(Boolean b) {
		return new Bool(new Binary(this, ">=", new Var(b),  BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> unBoundVariables() {
		Bool b = new Bool(this);
		for (VariableExpression v : b.getBoundVariables()) {
			b._toString = NamedParameter.setParameterAsLiteral(v.getName(), v.getValueAsLiteral(), b._toString);
		}
		b.variables.clear();
		return b;
	}
	
	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		Binary r = new Binary(this, "=", new Func("ANY", e, Expression.BOOLEAN | Expression.ARRAY, DBType.BOOLEAN), Expression.BOOLEAN, DBType.BOOLEAN);
		return new Bool(r);
	}
	
	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		List<Object> objList = new ArrayList<>();
		List<Expression<?>> exprList = new ArrayList<>();
		
		boolean nullPresent = false;
		
		for (Object e : c) {
			if (e instanceof Expression) {
				exprList.add((Expression<?>) e);
			} else {
				if (e == null) {
					nullPresent = true;
				} else {
					objList.add(e);
				}	
			}
		}
		
		BooleanExpression<?> b = null;
		
		if (!objList.isEmpty()) {
			Var valueList = new Var(objList, this.getDBType());
			Binary r = new Binary(this, "=", new Func("ANY", valueList, Expression.BOOLEAN | Expression.ARRAY, DBType.BOOLEAN), Expression.BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			BooleanExpression<?> be;
			if (!(e instanceof BooleanExpression)) {
				be = e.castAsBoolean();
			} else {
				be = (BooleanExpression<?>) e;
			}
			if(b == null) {
				b = this.eq(be); 
			} else {
				b = b.or(this.eq(be));
			}
		}
		
		if (nullPresent) {
			if(b == null) {
				b = this.isNull(); 
			} else {
				b = b.or(this.isNull());
			}
		}
		return b;
	}
	
	@Override
	public BooleanExpression<?> notIn(Expression<?> e) {
		Binary r = new Binary(this, "!=", new Func("ALL", e, Expression.BOOLEAN | Expression.ARRAY, DBType.BOOLEAN), Expression.BOOLEAN, DBType.BOOLEAN);
		return new Bool(r);
	}

	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		List<Object> objList = new ArrayList<>();
		List<Expression<?>> exprList = new ArrayList<>();
		
		boolean nullPresent = false;
		
		for (Object e : c) {
			if (e instanceof Expression) {
				exprList.add((Expression<?>) e);
			} else {
				if (e == null) {
					nullPresent = true;
				} else {
					objList.add(e);
				}	
			}
		}
		
		BooleanExpression<?> b = null;
		
		if (!objList.isEmpty()) {
			Var valueList = new Var(objList, this.getDBType());
			Binary r = new Binary(this, "!=", new Func("ALL", valueList, Expression.BOOLEAN | Expression.ARRAY, DBType.BOOLEAN), Expression.BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			BooleanExpression<?> be;
			if (!(e instanceof BooleanExpression)) {
				be = e.castAsBoolean();
			} else {
				be = (BooleanExpression<?>) e;
			}
			if(b == null) {
				b = this.neq(be); 
			} else {
				b = b.and(this.neq(be));
			}
		}
		
		if (nullPresent) {
			if(b == null) {
				b = this.isNotNull(); 
			} else {
				b = b.and(this.isNotNull());
			}
		}
		return b;
	}
	
	

	@Override
	public boolean isLeaf()      {return leaf;};
	
	
	@Override
	public BooleanExpression<?> parenthesize() {
		return super.parenthesize();
	}
	
	@Override
	public BooleanExpression<?> alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public BooleanExpression<?> qualify(String qualifier) {
		return super.qualify(qualifier);
	}
	
	@Override
	public BooleanExpression<?> copy() {
		return new Bool(this);
	}
}
