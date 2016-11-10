package org.jddp.expression.pgsql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jddp.exception.JDDPException;
import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.pgsql.util.NamedParameter;
import org.jddp.persistence.util.DBType;
import org.jddp.persistence.util.FieldUtil;

public class Obj extends AbstractExpression<Obj, ObjectExpression<?>> implements ObjectExpression<ObjectExpression<?>>  {

	protected final Class<?> type;
	protected final Class<?> arrayType;
	
	private final boolean leaf;
	
	protected  Obj(int modifier) {
		super(modifier, DBType.JSONB);
		
		if (isArray()) {
			this.type = Object.class;
			arrayType = ArrayList.class;
		} else {
			this.type = Object.class;
			arrayType = null;
		}
		
		leaf = true;
	}
	
	public Obj(Object obj, DBType dbType) {
		super(new Var(obj, dbType));
		type = obj.getClass();
		arrayType = null;
		leaf = true;
	}
	
	public Obj(Collection<?> col, DBType dbType) {
		super(new Var(col, dbType));
		type = getElementClass(col);
		arrayType = col.getClass();
		leaf = true;
	}

	public Obj(Expression<?> e) {
		super(e);
		if (!isJSONObject() && !(e instanceof ObjectExpression)) {
			throw new IllegalArgumentException("(" + e.getClass() + ") " + debugExpression() + " : is not an object expression");
		}
		
		type = e.getType();
		arrayType = e.getArrayType();
		leaf = e.isLeaf();
	}
	
	public Obj(Expression<?> e, Class<?> type, Class<?> arrayType) {
		super(e);
		if (!isJSONObject()  && !(e instanceof ObjectExpression)) {
			throw new IllegalArgumentException("(" + e.getClass() + ") " + debugExpression() + " : is not an object expression");
		}
		this.type = type;
		this.arrayType = arrayType;
		leaf = e.isLeaf();
	}
		
	
	
	
	private String debugExpression() {
		String s = _toString;
		for (VariableExpression v : getBoundVariables()) {
			s = NamedParameter.setParameterAsLiteral(v.getName(), v.getValueAsLiteral(), s);
		}
		return s;
	}

	@Override
	public BooleanExpression<?> eq(ObjectExpression<?> e) {
		return new Bool(new Binary(this, "=", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> neq(ObjectExpression<?> e) {
		return new Bool(new Binary(this, "<>", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lt(ObjectExpression<?> e) {
		return new Bool(new Binary(this, "<", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lte(ObjectExpression<?> e) {
		return new Bool(new Binary(this, "<=", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gt(ObjectExpression<?> e) {
		return new Bool(new Binary(this, ">", e, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gte(ObjectExpression<?> e) {
		return new Bool(new Binary(this, ">=", e, BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> contains(ObjectExpression<?> e) {
		return new Bool(new Binary(this, "@>", e, BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> containedIn(ObjectExpression<?> e) {
		return new Bool(new Binary(this, "<@", e, BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public ObjectExpression<?> concat(ObjectExpression<?> o) {
		return new Obj(new Binary(this, "||", o, JSONOBJECT, DBType.JSONB));
	}
	
	@Override
	public BooleanExpression<?> isKeyExist(String key) {
		return new Bool(new Binary(this, "??", new Var(key), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> eq(Object e) {
		return new Bool(new Binary(this, "=", new Obj(e, this.dbType), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> neq(Object e) {
		return new Bool(new Binary(this, "<>", new Obj(e, this.dbType), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lt(Object e) {
		return new Bool(new Binary(this, "<", new Obj(e, this.dbType), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lte(Object e) {
		return new Bool(new Binary(this, "<=", new Obj(e, this.dbType), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gt(Object e) {
		return new Bool(new Binary(this, ">", new Obj(e, this.dbType), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gte(Object e) {
		return new Bool(new Binary(this, ">=", new Obj(e, this.dbType), BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> contains(Object e) {
		return new Bool(new Binary(this, "@>", new Obj(e, this.dbType), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public BooleanExpression<?> containedIn(Object e) {
		return new Bool(new Binary(this, "<@", new Obj(e, this.dbType), BOOLEAN, DBType.BOOLEAN));
	}
	
	@Override
	public ObjectExpression<?> concat(Object o) {
		return new Obj(new Binary(this, "||", new Obj(o, this.dbType), JSONOBJECT, DBType.JSONB));
	}

	@Override
	public BooleanExpression<?> isNull() {
		return new Bool(new Unary(this, "IS NULL", BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> isNotNull() {
		return new Bool(new Unary(this, "IS NOT NULL", BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		Binary r = new Binary(this, "=", new Func("ANY", e, Expression.JSONOBJECT | Expression.ARRAY, DBType.JSONB), Expression.BOOLEAN, DBType.BOOLEAN);
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
					if (dbType == DBType.JSONB) {
						exprList.add(new Obj(e, dbType));
					} else {
						objList.add(e);
					}	
				}	
			}
		}
		
		BooleanExpression<?> b = null;
		
		if (!objList.isEmpty()) {
			Var valueList = new Var(objList, this.getDBType());
			Binary r = new Binary(this, "=", new Func("ANY", valueList, Expression.JSONOBJECT | Expression.ARRAY, DBType.JSONB), BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			ObjectExpression<?> oe;
			if (!(e instanceof ObjectExpression)) {
				oe = e.castAsObject();
			} else {
				oe = (ObjectExpression<?>) e;
			}
			if(b == null) {
				b = this.eq(oe); 
			} else {
				b = b.or(this.eq(oe));
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
		Binary r = new Binary(this, "!=", new Func("ALL", e, Expression.JSONOBJECT | Expression.ARRAY, DBType.JSONB), Expression.BOOLEAN, DBType.BOOLEAN);
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
					if (dbType == DBType.JSONB) {
						exprList.add(new Obj(e, dbType));
					} else {
						objList.add(e);
					}	
				}	
			}
		}
		
		BooleanExpression<?> b = null;
		
		if (!objList.isEmpty()) {
			Var valueList = new Var(objList, this.getDBType());
			Binary r = new Binary(this, "!=", new Func("ALL", valueList, Expression.JSONOBJECT | Expression.ARRAY, DBType.JSONB), BOOLEAN, DBType.BOOLEAN);
			b = new Bool(r);
		}
		
		for (Expression<?> e : exprList) {
			ObjectExpression<?> oe;
			if (!(e instanceof ObjectExpression)) {
				oe = e.castAsObject();
			} else {
				oe = (ObjectExpression<?>) e;
			}
			if(b == null) {
				b = this.neq(oe); 
			} else {
				b = b.and(this.neq(oe));
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
	public ObjectExpression<?> element(int i) {
		return new Obj(new Binary(this, "->", new Var(i).unBoundVariables(), JSONOBJECT, DBType.JSONB), this.getType(), this.getArrayType());
	}

	@Override
	public StringExpression<?> elementAsText(int i) {
		return new Str(new Binary(this, "->>", new Var(i).unBoundVariables(), STRING, DBType.TEXT));
	}

	@Override
	public ObjectExpression<?> field(String name) {
		return new Obj(new Binary(this, "->", new Var(name).unBoundVariables(), JSONOBJECT, DBType.JSONB));
	}

	@Override
	public StringExpression<?> fieldAstext(String name) {
		return new Str(new Binary(this, "->>", new Var(name).unBoundVariables(), STRING, DBType.TEXT));
	}

	@Override
	public ObjectExpression<?> fieldByPath(Collection<String> paths) {
		String p = FieldUtil.getPathAsArray(paths);
		return new Obj(new Binary(this, "#>", new Var(p.toString()).unBoundVariables(), JSONOBJECT, DBType.JSONB));
	}

	@Override
	public StringExpression<?> fieldByPathAsText(Collection<String> paths) {
		String p = FieldUtil.getPathAsArray(paths);
		return new Str(new Binary(this, "#>>", new Var(p.toString()).unBoundVariables(), STRING, DBType.TEXT));
	}

	@Override
	public ObjectExpression<?> unset(String name) {
		return new Obj(new Binary(this, "-", new Var(name).unBoundVariables(), JSONOBJECT, DBType.JSONB));
	}
	
	@Override
	public ObjectExpression<?> remove(int i) {
		if (!this.isArray()) {
			throw new JDDPException(this + " is not an object array");
		}
		return new Obj(new Binary(this, "-", new Var(i).unBoundVariables(), JSONOBJECT, DBType.JSONB));
	}
	
	@Override
	public ObjectExpression<?> unset(FieldExpression<?> f) {
		String p = FieldUtil.getPathAsArray(f);
		return new Obj(new Binary(this, "#-",  new Var(p).unBoundVariables(), JSONOBJECT, DBType.JSONB));
	}
	
	@Override
	public ObjectExpression<?> unset(Collection<String> paths) {
		String p = FieldUtil.getPathAsArray(paths);
		return new Obj(new Binary(this, "#-",  new Var(p).unBoundVariables(), JSONOBJECT, DBType.JSONB));
	}
	
	@Override
	public ObjectExpression<?> set(Collection<String> paths, ObjectExpression<?> value) {
		String p = FieldUtil.getPathAsArray(paths);
		LIST args = new LIST(this, new Var(p).unBoundVariables(), value);
		return new Obj(new Func("jsonb_set", args, JSONOBJECT, DBType.JSONB));
	}
	
	@Override
	public ObjectExpression<?> set(FieldExpression<?> f, ObjectExpression<?> value) {
		String p = FieldUtil.getPathAsArray(f);
		LIST args = new LIST(this, new Var(p.toString()).unBoundVariables(), value);
		return new Obj(new Func("jsonb_set", args, JSONOBJECT, DBType.JSONB));
	}
	
	@Override 
	public NumericExpression<?> length() {
		if (!this.isArray()) {
			throw new JDDPException(this + " is not an object array");
		}
		return new Num(new Func("jsonb_array_length", this, NUMERIC, DBType.NUMERIC));
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public Class<?> getArrayType() {
		return arrayType;
	}
	
	@Override
	public boolean isLeaf() {
		return leaf;
	}
	
	private static Class<?> getElementClass(Collection<?> c) {
		if (c.isEmpty()) {
			return Object.class;
		}
		Object obj = null;
		
		Iterator<?> i = c.iterator();
		
		while (obj == null && i.hasNext()) {
			obj = i.next();
		}
		
		if (obj == null) {
			return Object.class;
		}
		return obj.getClass();
		
	}
	
	public ObjectExpression<?> unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public ObjectExpression<?> parenthesize() {
		return super.parenthesize();
	}

	@Override
	public ObjectExpression<?> alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public ObjectExpression<?> qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public ObjectExpression<?> copy() {
		return  new Obj(this);
	}
}
