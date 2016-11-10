package org.jddp.expression.pgsql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.UUIDFieldExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.util.DBType;

public class UUIDField  extends Field<UUIDFieldExpression> implements StringExpression<UUIDFieldExpression>, UUIDFieldExpression {

	private final UID thisAsUUID;
	private final Stringify stringified;
	
	public UUIDField(UUIDField o, FieldExpression<?> owner) {
		super(o, owner, null);
		thisAsUUID = new UID(this);
		stringified = new Stringify(this);
	}

	public UUIDField(String xpath, String prefix, String fieldName, Class<?> arrayType, Class<?> type, DBType dbType,
			int modifier, EntityClass<?> entityClass, String[] accessor) {
		super(xpath, prefix, fieldName, arrayType, type, dbType, modifier, entityClass, accessor);
		thisAsUUID = new UID(this);
		stringified = new Stringify(this);
	}

	
	@Override
	public BooleanExpression<?> eq(UUID uuid) {
		return thisAsUUID.eq(uuid);
	}

	@Override
	public BooleanExpression<?> neq(UUID uuid) {
		return thisAsUUID.neq(uuid);
	}

	@Override
	public BooleanExpression<?> lt(UUID uuid) {
		return thisAsUUID.lt(uuid);
	}

	@Override
	public BooleanExpression<?> lte(UUID uuid) {
		return thisAsUUID.lte(uuid);
	}

	@Override
	public BooleanExpression<?> gt(UUID uuid) {
		return thisAsUUID.gt(uuid);
	}

	@Override
	public BooleanExpression<?> gte(UUID uuid) {
		return thisAsUUID.gte(uuid);
	}

	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		return thisAsUUID.in(convert(c));
	}

	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		return thisAsUUID.notIn(convert(c));
	}

	@Override
	public BooleanExpression<?> inIgnoreCase(Collection<?> c) {
		return thisAsUUID.inIgnoreCase(convert(c));
	}

	@Override
	public BooleanExpression<?> notInIgnoreCase(Collection<?> c) {
		return thisAsUUID.notInIgnoreCase(convert(c));
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
	public UUIDFieldExpression parenthesize() {
		return super.parenthesize();
	}
	
	@Override
	public UUIDFieldExpression unBoundVariables() {
		return super.unBoundVariables();
	}
	@Override
	public UUIDFieldExpression alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public UUIDFieldExpression qualify(String qualifier) {
		return super.qualify(qualifier);
	}
	
	@Override
	public UUIDFieldExpression copy() {
		return new UUIDField(this, this.owner);
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public StringExpression<?> concat(StringExpression<?> str) {
		return stringified.concat(str);
	}

	@Override
	public StringExpression<?> concat(String str) {
		return stringified.concat(str);
	}

	@Override
	public StringExpression<?> lower() {
		return stringified.lower();
	}

	@Override
	public StringExpression<?> upper() {
		return stringified.upper();
	}

	@Override
	public NumericExpression<?> charLength() {
		return stringified.charLength();
	}

	@Override
	public StringExpression<?> max() {
		return stringified.max();
	}

	@Override
	public StringExpression<?> min() {
		return stringified.min();
	}

	@Override
	public StringExpression<?> aggregate(String delimeter) {
		return stringified.aggregate(delimeter);
	}

	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter) {
		return stringified.aggregate(delimeter);
	}

	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderBy) {
		return stringified.aggregate(delimeter, orderBy);
	}

	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderBy) {
		return stringified.aggregate(delimeter, orderBy);
	}

	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderedBy, boolean ascending) {
		return stringified.aggregate(delimeter, orderedBy, ascending);
	}

	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderedBy, boolean ascending) {
		return stringified.aggregate(delimeter, orderedBy, ascending);
	}

	@Override
	public BooleanExpression<?> eqIgnoreCase(StringExpression<?> e) {
		return stringified.eqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> neqIgnoreCase(StringExpression<?> e) {
		return stringified.neqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(StringExpression<?> e) {
		return stringified.ltIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(StringExpression<?> e) {
		return stringified.lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(StringExpression<?> e) {
		return stringified.gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(StringExpression<?> e) {
		return stringified.gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> eqIgnoreCase(String e) {
		return stringified.eqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> neqIgnoreCase(String e) {
		return stringified.neqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(String e) {
		return stringified.ltIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(String e) {
		return stringified.lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(String e) {
		return stringified.gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(String e) {
		return stringified.gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> like(StringExpression<?> e) {
		return stringified.like(e);
	}

	@Override
	public BooleanExpression<?> like(String e) {
		return stringified.like(e);
	}

	@Override
	public BooleanExpression<?> ilike(StringExpression<?> e) {
		return stringified.ilike(e);
	}

	@Override
	public BooleanExpression<?> ilike(String e) {
		return stringified.ilike(e);
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(StringExpression<?> e) {
		return stringified.likeIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(String e) {
		return stringified.likeIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> eq(StringExpression<?> e) {
		return stringified.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(StringExpression<?> e) {
		return stringified.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(StringExpression<?> e) {
		return stringified.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(StringExpression<?> e) {
		return stringified.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(StringExpression<?> e) {
		return stringified.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(StringExpression<?> e) {
		return stringified.gte(e);
	}

	@Override
	public BooleanExpression<?> eq(String e) {
		return stringified.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(String e) {
		return stringified.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(String e) {
		return stringified.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(String e) {
		return stringified.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(String e) {
		return stringified.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(String e) {
		return stringified.gte(e);
	}

	@Override
	public BooleanExpression<?> isNull() {
		return stringified.isNull();
	}

	@Override
	public BooleanExpression<?> isNotNull() {
		return stringified.isNotNull();
	}

	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		return stringified.in(e);
	}

	@Override
	public BooleanExpression<?> notIn(Expression<?> e) {
		return stringified.notIn(e);
	}
}
