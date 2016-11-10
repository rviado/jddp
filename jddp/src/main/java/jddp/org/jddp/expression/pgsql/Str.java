package org.jddp.expression.pgsql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.util.DBType;

public class Str  extends AbstractExpression<Str, StringExpression<?>> implements StringExpression<StringExpression<?>>  {

	private final boolean leaf;
	
	protected  Str(int modifier) {
		super(modifier, DBType.TEXT);
		leaf = true;
	}
	
	public Str(String str) {
		super(new Var(str));
		leaf = true;
	}

	public Str(Expression<?> e) {
		super(e);
		if (!isString()) {
			throw new IllegalArgumentException("(" + e.getClass() + ") " + e.unBoundVariables() + " : is not a string expression");
		}
		leaf = e.isLeaf();
	}
	
	@Override
	public BooleanExpression<?> eq(StringExpression<?> e) {
		return  new Bool(new Binary(this, "=" ,  e, BOOLEAN, DBType.BOOLEAN));
	
	}

	@Override
	public BooleanExpression<?> neq(StringExpression<?> e) {
		return  new Bool(new Binary(this, "<>" ,  e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lt(StringExpression<?> e) {
		return  new Bool(new Binary(this, "<" ,  e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lte(StringExpression<?> e) {
		return  new Bool(new Binary(this, "<=" ,  e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gt(StringExpression<?> e) {
		return  new Bool(new Binary(this, ">" ,  e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gte(StringExpression<?> e) {
		return  new Bool(new Binary(this, ">=" ,  e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> eq(String e) {
		return new Bool(new Binary(this, "=", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> neq(String e) {
		return new Bool(new Binary(this, "<>", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lt(String e) {
		return new Bool(new Binary(this, "<", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lte(String e) {
		return new Bool(new Binary(this, "<=", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gt(String e) {
		return new Bool(new Binary(this, ">", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gte(String e) {
		return new Bool(new Binary(this, ">=", new Var(e), BOOLEAN, DBType.BOOLEAN));
	}

		
	@Override 
	public BooleanExpression<?> isNull() {
		BooleanExpression<?> b = new Bool(new Unary(this, "IS NULL" ,  BOOLEAN, DBType.BOOLEAN));
		return b;
	}
	
	@Override 
	public BooleanExpression<?> isNotNull() {
		Bool b = new Bool(new Unary(this, "IS NOT NULL" ,  BOOLEAN, DBType.BOOLEAN));
		return b;
	}
	

	
	
	@Override
	public StringExpression<?> concat(StringExpression<?> s) {
		LIST args = new LIST(STRING, this, s);
		return new Str(new Func("CONCAT", args, STRING, DBType.TEXT));
	}

	@Override
	public StringExpression<?> concat(String n) {
		LIST args = new LIST(STRING, this, new Var(n));
		return new Str(new Func("CONCAT", args, STRING, DBType.TEXT));
	}

	@Override
	public StringExpression<?> lower() {
		return new Str(new Func("LOWER", this, STRING, DBType.TEXT));
	}

	@Override
	public StringExpression<?> upper() {
		return new Str(new Func("UPPER", this, STRING, DBType.TEXT));
	}

	@Override
	public NumericExpression<?> charLength() {
		return new Num(new Func("CHAR_LENGTH", this, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public StringExpression<?> max() {
		return new Str(new Func("MAX", this, STRING, DBType.TEXT));
	}

	@Override
	public StringExpression<?> min() {
		return new Str(new Func("MIN", this, STRING, DBType.TEXT));
	}
	
	@Override
	public StringExpression<?> aggregate(String delimeter) {
		LIST args = new LIST(STRING, this, new Var(delimeter));
		return new Str(new Func("STRING_AGG", args, STRING, DBType.TEXT));
	}
	
	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter) {
		LIST args = new LIST(STRING, this, delimeter);
		return new Str(new Func("STRING_AGG", args, STRING, DBType.TEXT));
	}
	
	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderedBy) {
		Str v = new Str(delimeter);
		OrderBy ob = new OrderBy(orderedBy);
		Binary be = new Binary(v, "ORDER BY", ob, STRING, DBType.TEXT);
		LIST args = new LIST(STRING, this,  be);
		return new Str(new Func("STRING_AGG", args, STRING, DBType.TEXT));
	}
	
	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderedBy) {
		OrderBy ob = new OrderBy(orderedBy);
		Binary be = new Binary(delimeter, "ORDER BY", ob, STRING, DBType.TEXT);
		LIST args = new LIST(STRING, this, be);
		return new Str(new Func("STRING_AGG", args, STRING, DBType.TEXT));
	}
	
	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderedBy, boolean ascending) {
		Str v = new Str(delimeter);
		OrderBy ob = new OrderBy(orderedBy);
		ob = ascending ? ob.asc() : ob.desc();
		Binary be = new Binary(v, "ORDER BY", ob, STRING, DBType.TEXT);
		LIST args = new LIST(STRING, this,  be);
		return new Str(new Func("STRING_AGG", args, STRING, DBType.TEXT));
	}
	
	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderedBy, boolean ascending) {
		OrderBy ob = new OrderBy(orderedBy);
		ob = ascending ? ob.asc() : ob.desc();
		Binary be = new Binary(delimeter, "ORDER BY", ob, STRING, DBType.TEXT);
		LIST args = new LIST(STRING, this, be);
		return new Str(new Func("STRING_AGG", args, STRING, DBType.TEXT));
	}
	
	@Override
	public BooleanExpression<?> eqIgnoreCase(StringExpression<?> e) {
		return new Bool(new Binary(toLower(this), "=", toLower(e), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> neqIgnoreCase(StringExpression<?> e) {
		return new Bool(new Binary(toLower(this), "<>", toLower(e), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(StringExpression<?> e) {
		return new Bool(new Binary(toLower(this), "<", toLower(e), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> lteIgnoreCase(StringExpression<?> e) {
		return new Bool(new Binary(toLower(this), "<=", toLower(e), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> gtIgnoreCase(StringExpression<?> e) {
		return new Bool(new Binary(toLower(this), ">", toLower(e), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> gteIgnoreCase(StringExpression<?> e) {
		return new Bool(new Binary(toLower(this), ">=", toLower(e), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> eqIgnoreCase(String e) {
		return new Bool(new Binary(toLower(this), "=", new Str(e.toLowerCase()), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> neqIgnoreCase(String e) {
		return new Bool(new Binary(toLower(this), "<>", new Str(e.toLowerCase()), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(String e) {
		return new Bool(new Binary(toLower(this), "<", new Str(e.toLowerCase()), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> lteIgnoreCase(String e) {
		return new Bool(new Binary(toLower(this), "<=", new Str(e.toLowerCase()), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> gtIgnoreCase(String e) {
		return new Bool(new Binary(toLower(this), ">", new Str(e.toLowerCase()), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> gteIgnoreCase(String e) {
		return new Bool(new Binary(toLower(this), ">=", new Str(e.toLowerCase()), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		Binary r = new Binary(this, "=", new Func("ANY", e, Expression.STRING | Expression.ARRAY, DBType.TEXT), Expression.BOOLEAN, DBType.BOOLEAN);
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
			Binary r = new Binary(this, "=", new Func("ANY", valueList, Expression.STRING | Expression.ARRAY, DBType.TEXT), BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			StringExpression<?> se;
			if (!(e instanceof StringExpression)) {
				se = e.castAsString();
			} else {
				se = (StringExpression<?>) e;
			}
			
			if(b == null) {
				b = this.eq(se); 
			} else {
				b = b.or(this.eq(se));
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
		Binary r = new Binary(this, "!=", new Func("ALL", e, Expression.STRING | Expression.ARRAY, DBType.TEXT), Expression.BOOLEAN, DBType.BOOLEAN);
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
			Binary r = new Binary(this, "!=", new Func("ALL", valueList, Expression.STRING | Expression.ARRAY, DBType.TEXT), BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			StringExpression<?> se;
			if (!(e instanceof StringExpression)) {
				se = e.castAsString();
			} else {
				se = (StringExpression<?>) e;
			}
			
			if(b == null) {
				b = this.neq(se); 
			} else {
				b = b.and(this.neq(se));
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
	public BooleanExpression<?> inIgnoreCase(Collection<?> c) {
		
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
		StringExpression<?> _this =  toLower(this);
		
		if (!objList.isEmpty()) {
			List<String> l = new ArrayList<>();
			for (Object o : objList) {
				l.add(o.toString().toLowerCase());
			}
			
			Var valueList = new Var(l, dbType);
			
			Binary r = new Binary(_this, "=", new Func("ANY", valueList, Expression.STRING | Expression.ARRAY, DBType.TEXT), BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			StringExpression<?> se = toLower(e);
			if(b == null) {
				b = _this.eq(se); 
			} else {
				b = b.or(_this.eq(se));
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
	public BooleanExpression<?> notInIgnoreCase(Collection<?> c) {
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
		StringExpression<?> _this =  toLower(this);
		
		if (!objList.isEmpty()) {
			List<String> l = new ArrayList<>();
			for (Object o : objList) {
				l.add(o.toString().toLowerCase());
			}
			
			Var valueList = new Var(l, dbType);
			
			Binary r = new Binary(_this, "!=", new Func("ALL", valueList, Expression.STRING | Expression.ARRAY, DBType.TEXT), BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			StringExpression<?> se = toLower(e);
			if(b == null) {
				b = _this.neq(se); 
			} else {
				b = b.and(_this.neq(se));
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
	
	private StringExpression<?> toLower(Expression<?> e) {
		
		if (e instanceof VariableExpression) {
			String s = ((VariableExpression) e).getValueAsLiteral();
			return new Str(s.toLowerCase());
		} 
		
		if (e instanceof StringExpression) {
			StringExpression<?> se = (StringExpression<?>)e;
			if (dbType != DBType.TEXT) {
				return se;
			}
			return se.lower();
		}
		
		return new Str(e).lower();
	}

	@Override
	public boolean isLeaf() {
		return leaf;
	}

	@Override
	public BooleanExpression<?> like(StringExpression<?> s) {
		return new Bool(new Binary(this, "like", s, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> like(String s) {
		return new Bool(new Binary(this, "like", new Str(s), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> ilike(StringExpression<?> s) {
		return new Bool(new Binary(this, "ilike", s, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> ilike(String s) {
		return new Bool(new Binary(this, "ilike", new Str(s), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(StringExpression<?> s) {
		return new Bool(new Binary(this, "ilike", s, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(String s) {
		return new Bool(new Binary(this, "ilike", new Str(s), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public StringExpression<?> unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public StringExpression<?> parenthesize() {
		return super.parenthesize();
	}

	@Override
	public StringExpression<?> alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public StringExpression<?> qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public StringExpression<?> copy() {
		return  new Str(this);
	}
}
