package org.jddp.expression.pgsql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.UUIDExpression;
import org.jddp.persistence.util.DBType;

public class UID extends Stringify implements  UUIDExpression<StringExpression<?>> {

	private final boolean leaf;
	
	public UID(UUID uuid) {
		super(new Var(uuid.toString()).unBoundVariables());
		leaf = true;
	}

	public UID(Expression<?> e) {
		super(e);
		if (!isString() && (e.getDBType() != DBType.UUID)) {
			throw new IllegalArgumentException("(" + e.getClass() + ") " + e.unBoundVariables() + " : is not a UUID expression");
		}
		leaf = e.isLeaf();
	}
	
	@Override
	public BooleanExpression<?> eq(UUID uuid) {
		Expression<?> uuide = new Var(uuid.toString()).unBoundVariables();
		return new Bool(new Binary(this, "=", uuide, Expression.BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> neq(UUID uuid) {
		Expression<?> uuide = new Var(uuid.toString()).unBoundVariables();
		return new Bool(new Binary(this, "<>", uuide, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lt(UUID uuid) {
		Expression<?> uuide = new Var(uuid.toString()).unBoundVariables();
		return new Bool(new Binary(this, "<", uuide, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> lte(UUID uuid) {
		Expression<?> uuide = new Var(uuid.toString()).unBoundVariables();
		return new Bool(new Binary(this, "<=", uuide, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gt(UUID uuid) {
		Expression<?> uuide = new Var(uuid.toString()).unBoundVariables();
		return new Bool(new Binary(this, ">", uuide, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> gte(UUID uuid) {
		Expression<?> uuide = new Var(uuid.toString()).unBoundVariables();
		return new Bool(new Binary(this, ">=", uuide, BOOLEAN, DBType.BOOLEAN));
	}

	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		return super.in(convert(c));
	}

	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		return super.notIn(convert(c));
	}

	@Override
	public BooleanExpression<?> inIgnoreCase(Collection<?> c) {
		//case does not matter
		return super.in(convert(c));
	}

	@Override
	public BooleanExpression<?> notInIgnoreCase(Collection<?> c) {
		//case does not matter
		return super.notIn(convert(c));
	}


	private List<Object> convert(Collection<?> c) {
		List<Object> l = new ArrayList<>();
		for (Object e : c) {
			if (e instanceof Expression) {
				Expression<?> expr = (Expression<?>) e;
				if (expr.getDBType() != dbType) {
					if (expr.isJSONObject()) {
						expr = expr.castAsString();
					}
					expr = expr.castInto(dbType);
				}
				l.add(expr);
			} else  {
				if (e instanceof String) {
					l.add(e);
				} else {
					l.add(e.toString());
				}
			} 
		}
		return l;
	}
	

	@Override
	public boolean isLeaf() {
		return leaf;
	}
	
	@Override
	public UUIDExpression<?> parenthesize() {
		return (UUIDExpression<?>) super.parenthesize();
	}
	
	@Override
	public UUIDExpression<?> unBoundVariables() {
		return (UUIDExpression<?>)  super.unBoundVariables();
	}
	
	@Override
	public UUIDExpression<?> alias(String alias) {
		return  (UUIDExpression<?>) super.alias(alias);
	}
	
	@Override
	public UUIDExpression<?> qualify(String qualifer) {
		return  (UUIDExpression<?>) super.alias(qualifer);
	}
	
	@Override 
	public UUIDExpression<?> copy() {
		return new UID(this);
	}
}
