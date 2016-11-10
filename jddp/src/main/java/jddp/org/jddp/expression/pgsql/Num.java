package org.jddp.expression.pgsql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.NumericExpression;
import org.jddp.persistence.util.DBType;

public class Num  extends AbstractExpression<Num, NumericExpression<?>> implements NumericExpression<NumericExpression<?>>  {

	private final boolean leaf;
	
	protected  Num(int modifier) {
		super(modifier, DBType.NUMERIC);
		leaf = true;
	}
	
	public Num(Number num) {
		super(new Var(num));
		leaf = true;
	}

	public Num(Expression<?> e) {
		super(e);
		if (!isNumeric()) {
			throw new IllegalArgumentException("(" + e.getClass() + ") " + e.unBoundVariables() + " : is not a numeric expression");
		}
		leaf = e.isLeaf();
	}
	
	@Override
	public BooleanExpression<?> eq(NumericExpression<?> e) {
		return new Bool(new Binary(this, "=", e, BOOLEAN, DBType.BOOLEAN));
	
	}

	@Override
	public BooleanExpression<?> neq(NumericExpression<?> e) {
		return new Bool(new Binary(this, "<>", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lt(NumericExpression<?> e) {
		return new Bool(new Binary(this, "<", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lte(NumericExpression<?> e) {
		return new Bool(new Binary(this, "<=", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gt(NumericExpression<?> e) {
		return new Bool(new Binary(this, ">", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gte(NumericExpression<?> e) {
		return new Bool(new Binary(this, ">=", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> eq(Number e) {
		return new Bool(new Binary(this, "=", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> neq(Number e) {
		return new Bool(new Binary(this, "<>", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lt(Number e) {
		return new Bool(new Binary(this, "<", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lte(Number e) {
		return new Bool(new Binary(this, "<=", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gt(Number e) {
		return new Bool(new Binary(this, ">", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gte(Number e) {
		return new Bool(new Binary(this, ">=", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

		
	@Override
	public NumericExpression<?> negate() {
		return  new Num(new Unary("-" ,  this, NUMERIC, DBType.NUMERIC));
	}

	@Override 
	public BooleanExpression<?> isNull() {
		Bool b = new Bool(new Unary(this, "IS NULL",  BOOLEAN, DBType.BOOLEAN));
		return b;
	}
	
	@Override 
	public BooleanExpression<?> isNotNull() {
		Bool b = new Bool(new Unary(this, "IS NOT NULL" ,  BOOLEAN, DBType.BOOLEAN));
		return b;
	}

	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		Binary r = new Binary(this, "=", new Func("ANY", e, Expression.NUMERIC | Expression.ARRAY, DBType.NUMERIC), Expression.BOOLEAN, DBType.BOOLEAN);
		return new Bool(r);
	}

	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		
		if (c.isEmpty()) {
			throw new IllegalArgumentException("List must not be empty");
		}
		
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
			Binary r = new Binary(this, "=", new Func("ANY", valueList, Expression.NUMERIC | Expression.ARRAY, DBType.NUMERIC), BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			NumericExpression<?> ne;
			if (!(e instanceof NumericExpression)) {
				ne = new Num(e);
			} else {
				ne = (NumericExpression<?>) e;
			}
			if(b == null) {
				b = this.eq(ne); 
			} else {
				b = b.or(this.eq(ne));
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
		Binary r = new Binary(this, "!=", new Func("ALL", e, Expression.NUMERIC | Expression.ARRAY, DBType.NUMERIC), Expression.BOOLEAN, DBType.BOOLEAN);
		return new Bool(r);
	}
	
	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		if (c.isEmpty()) {
			throw new IllegalArgumentException("List must not be empty");
		}
		
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
			Binary r = new Binary(this, "!=", new Func("ALL", valueList, Expression.NUMERIC | Expression.ARRAY, DBType.NUMERIC), BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			NumericExpression<?> ne;
			if (!(e instanceof NumericExpression)) {
				ne = e.castAsNumeric();
			} else {
				ne = (NumericExpression<?>) e;
			}
			if(b == null) {
				b = this.neq(ne); 
			} else {
				b = b.and(this.neq(ne));
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
	public NumericExpression<?> plus(NumericExpression<?> m) {
		return  new Num(new Binary(this, "+" , m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> plus(Number n) {
		return  new Num(new Binary(this, "+" , new Var(n), NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> minus(NumericExpression<?> m) {
		return  new Num(new Binary(this, "-" ,  m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> minus(Number n) {
		return  new Num(new Binary(this, "-" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> div(NumericExpression<?> m) {
		return  new Num(new Binary(this, "/" ,  m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> div(Number n) {
		return  new Num(new Binary(this, "/" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> mul(NumericExpression<?> m) {
		return  new Num(new Binary(this, "*" ,  m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> mul(Number n) {
		return  new Num(new Binary(this, "*" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> mod(NumericExpression<?> m) {
		return  new Num(new Binary(this, "%" ,  m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> mod(Number n) {
		return  new Num(new Binary(this, "%" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}
	
	@Override
	public NumericExpression<?> power(NumericExpression<?> m) {
		return  new Num(new Binary(this, "^" ,  m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> power(Number n) {
		return  new Num(new Binary(this, "^" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}
	
	@Override
	public NumericExpression<?> and(NumericExpression<?> m) {
		return  new Num(new Binary(this, "&" ,  m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> and(Number n) {
		return  new Num(new Binary(this, "&" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> or(NumericExpression<?> m) {
		return  new Num(new Binary(this, "|" ,  m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> or(Number n) {
		return  new Num(new Binary(this, "|" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> xor(NumericExpression<?> m) {
		return  new Num(new Binary(this, "#" ,  m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> xor(Number n) {
		return  new Num(new Binary(this, "#" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> not() {
		return  new Num(new Func("~", this, NUMERIC, DBType.NUMERIC));
	}
	
	@Override
	public NumericExpression<?> shiftLeft(NumericExpression<?> m) {
		return  new Num(new Binary(this, "<<" ,  m, NUMERIC, DBType.NUMERIC));
	}
	
	@Override
	public NumericExpression<?> shiftLeft(Number n) {
		return  new Num(new Binary(this, "<<" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}
	

	@Override
	public NumericExpression<?> shiftRight(NumericExpression<?> m) {
		return  new Num(new Binary(this, ">>" ,  m, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> shiftRight(Number n) {
		return  new Num(new Binary(this, ">>" ,  new Var(n), NUMERIC, DBType.NUMERIC));
	}

	@Override
	public NumericExpression<?> abs() {
		return new Num(new Func("ABS", this, NUMERIC, DBType.NUMERIC));
	}
	
	@Override
	public NumericExpression<?> max() {
		return new Num(new Func("MAX", this, NUMERIC, DBType.NUMERIC));
	}
	
	@Override
	public NumericExpression<?> min() {
		return new Num(new Func("MIN", this, NUMERIC, DBType.NUMERIC));
	}
	
	@Override
	public NumericExpression<?> sum() {
		return new Num(new Func("SUM", this, NUMERIC, DBType.NUMERIC));
	}
	
	@Override
	public NumericExpression<?> avg() {
		return new Num(new Func("AVG", this, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public boolean isLeaf() {
		return leaf;
	}
	
	@Override
	public NumericExpression<?> unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public NumericExpression<?> parenthesize() {
		return super.parenthesize();
	}

	@Override
	public NumericExpression<?> alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public NumericExpression<?> qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public NumericExpression<?> copy() {
		return  new Num(this);
	}	

}
