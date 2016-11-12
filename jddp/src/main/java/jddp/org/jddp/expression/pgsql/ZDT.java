package org.jddp.expression.pgsql;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.ZonedDateTimeExpression;
import org.jddp.persistence.util.DBType;

public class ZDT extends Stringify implements  ZonedDateTimeExpression<StringExpression<?>> {

	private final boolean leaf;
	
	public ZDT(ZonedDateTime zdt) {
		super(new Literal(OffsetDateTime.from(zdt).toString(), Expression.STRING, DBType.TIMESTAMPTZ).quote());
		leaf = true;
	}
	
	public ZDT(OffsetDateTime odt) {
		super(new Literal(odt.toString(), Expression.STRING, DBType.TIMESTAMPTZ).quote());
		leaf = true;
	}

	public ZDT(Expression<?> e) {
		super(e);
		if (!isString() && e.getDBType() != DBType.TIMESTAMPTZ && e.getDBType() != DBType.TIMESTAMP) {
			throw new IllegalArgumentException("(" + e.getClass() + ") " + e.unBoundVariables() + " : is not a ZonedDateTime expression");
		}
		leaf = e.isLeaf();
	}
	
	@Override
	public ZonedDateTimeExpression<?> max() {
		return new ZDT(new Func("MAX", coerce(this), STRING, DBType.TIMESTAMPTZ));
	}

	@Override
	public ZonedDateTimeExpression<?> min() {
		return new ZDT(new Func("MIN", coerce(this), STRING, DBType.TIMESTAMPTZ));
	}
	
	@Override
	public BooleanExpression<?> eq(ZonedDateTime zdt) {
		return super.eq(OffsetDateTime.from(zdt).toString());
	}

	@Override
	public BooleanExpression<?> neq(ZonedDateTime zdt) {
		return super.neq(OffsetDateTime.from(zdt).toString());
	}

	@Override
	public BooleanExpression<?> lt(ZonedDateTime zdt) {
		return super.lt(OffsetDateTime.from(zdt).toString());
	}

	@Override
	public BooleanExpression<?> lte(ZonedDateTime zdt) {
		return super.lte(OffsetDateTime.from(zdt).toString());
	}

	@Override
	public BooleanExpression<?> gt(ZonedDateTime zdt) {
		return super.gt(OffsetDateTime.from(zdt).toString());
	}

	@Override
	public BooleanExpression<?> gte(ZonedDateTime zdt) {
		return super.gte(OffsetDateTime.from(zdt).toString());
	}


	public BooleanExpression<?> eq(OffsetDateTime odt) {
		return super.eq(odt.toString());
	}
		
	@Override
	public BooleanExpression<?> neq(OffsetDateTime odt) {
		return super.neq(odt.toString());
	}

	@Override
	public BooleanExpression<?> lt(OffsetDateTime odt) {
		return super.lt(odt.toString());
	}

	@Override
	public BooleanExpression<?> lte(OffsetDateTime odt) {
		return super.lte(odt.toString());
	}

	@Override
	public BooleanExpression<?> gt(OffsetDateTime odt) {
		return super.gt(odt.toString());
	}

	@Override
	public BooleanExpression<?> gte(OffsetDateTime odt) {
		return super.gte(odt.toString());
	}
	
	@Override
	public BooleanExpression<?> between(ZonedDateTime zdt1, ZonedDateTime zdt2) {
		OffsetDateTime odt1, odt2;
		odt1 = OffsetDateTime.from(zdt1);
		odt2 = OffsetDateTime.from(zdt2);
		return between(odt1,odt2);
	}

	@Override
	public BooleanExpression<?> between(OffsetDateTime odt1, OffsetDateTime odt2) {
		return new Bool(new Trinary(
				this, "BETWEEN", 
				new Str(odt1.toString()).unBoundVariables(), 
				"AND", 
				new Str(odt2.toString()).unBoundVariables(), BOOLEAN, DBType.BOOLEAN ));
	}
	
	@Override
	public ZonedDateTimeExpression<?> plus(Period p) {
		return  new ZDT(new Binary(this, "+ interval", new Str(p.toString()).unBoundVariables() , modifier, dbType));
	}

	@Override
	public ZonedDateTimeExpression<?> minus(Period p) {
		return  new ZDT(new Binary(this, "- interval", new Str(p.toString()).unBoundVariables() , modifier, dbType));
	}
	
	@Override
	public ZonedDateTimeExpression<?> plus(Duration d) {
		return  new ZDT(new Binary(this, "+ interval", new Str(d.toString()).unBoundVariables() , modifier, dbType));
	}

	@Override
	public ZonedDateTimeExpression<?> minus(Duration d) {
		return  new ZDT(new Binary(this, "- interval", new Str(d.toString()).unBoundVariables() , modifier, dbType));
	}

	@Override
	public ZonedDateTimeExpression<?> plus(Period p, Duration d) {
		return  new ZDT(new Binary(this, "+ interval", new Str(p.toString() + d.toString().substring(1)).unBoundVariables() , modifier, dbType));
	}

	@Override
	public ZonedDateTimeExpression<?> minus(Period p, Duration d) {
		return  new ZDT(new Binary(this, "- interval", new Str(p.toString() + d.toString().substring(1)).unBoundVariables() , modifier, dbType));
	}

	@Override
	public ObjectExpression<?> age(ZonedDateTime zdt) {
		LIST le = new LIST(this, new ZDT(zdt));
		Obj obj = new Obj(new Func("AGE", le , Expression.JSONOBJECT, DBType.JSONB), String.class, null);
		
		return obj;
	}
	
	@Override
	public ObjectExpression<?> age(OffsetDateTime odt) {
		LIST le = new LIST(this, new ZDT(odt));
		Obj obj = new Obj(new Func("AGE", le , Expression.JSONOBJECT, DBType.JSONB), String.class, null);
		
		return obj;
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
		//case really does not matter
		return super.in(convert(c));
	}

	@Override
	public BooleanExpression<?> notInIgnoreCase(Collection<?> c) {
		//case really does not matter
		return super.notIn(convert(c));
	}
	
	private List<Object> convert(Collection<?> c) {
		List<Object> l = new ArrayList<>();
		for (Object o : c) {
			if (o instanceof Expression) {
				Expression<?> expr = (Expression<?>) o;
				if (expr.getDBType() != dbType) {
					if (expr.isJSONObject()) {
						expr = expr.castAsString();
					}
					
					expr = expr.castInto(dbType);
					
				}
				l.add(expr);
			} else {
				l.add(toStringInUTC(o));
			}	
		}
		return l;
	}
	
	private String toStringInUTC(Object o) {
		if (o instanceof OffsetDateTime) {
			return o.toString();
		}
		if (o instanceof ZonedDateTime) {
			return OffsetDateTime.from(((ZonedDateTime) o)).toString();
		}
		
		ZonedDateTime zdt;
		if (o instanceof String) {
			zdt = ZonedDateTime.parse((String) o);
		} else {
			zdt = ZonedDateTime.parse(o.toString());
		}
		return OffsetDateTime.from(zdt).toString();
	}
	
	
	@Override
	public boolean isLeaf() {
		return leaf;
	}
	
	@Override
	public ZonedDateTimeExpression<?> parenthesize() {
		return (ZonedDateTimeExpression<?>) super.parenthesize();
	}
	
	@Override
	public ZonedDateTimeExpression<?> unBoundVariables() {
		return (ZonedDateTimeExpression<?>)  super.unBoundVariables();
	}
	
	@Override
	public ZonedDateTimeExpression<?> alias(String alias) {
		return  (ZonedDateTimeExpression<?>) super.alias(alias);
	}
	
	@Override
	public ZonedDateTimeExpression<?> qualify(String qualifer) {
		return  (ZonedDateTimeExpression<?>) super.alias(qualifer);
	}
	
	@Override 
	public ZonedDateTimeExpression<?> copy() {
		return new ZDT(this);
	}
}
