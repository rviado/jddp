package org.jddp.expression.pgsql;

import java.util.Collection;
import java.util.UUID;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.UUIDExpression;
import org.jddp.expression.UUIDFieldExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.util.DBType;

public class UUIDField  extends Field<UUIDFieldExpression> implements UUIDFieldExpression {

	private final UID thisAsUUID;
	
	public UUIDField(UUIDField o, FieldExpression<?> owner) {
		super(o, owner, null);
		thisAsUUID = new UID(this);
	}

	public UUIDField(String xpath, String prefix, String fieldName, Class<?> arrayType, Class<?> type, DBType dbType,
			int modifier, EntityClass<?> entityClass, String[] accessor) {
		super(xpath, prefix, fieldName, arrayType, type, dbType, modifier, entityClass, accessor);
		thisAsUUID = new UID(this);
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
	public BooleanExpression<?> eq(StringExpression<?> e) {
		return thisAsUUID.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(StringExpression<?> e) {
		return thisAsUUID.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(StringExpression<?> e) {
		return thisAsUUID.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(StringExpression<?> e) {
		return thisAsUUID.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(StringExpression<?> e) {
		return thisAsUUID.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(StringExpression<?> e) {
		return thisAsUUID.gte(e);
	}

	@Override
	public BooleanExpression<?> eq(String e) {
		return thisAsUUID.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(String e) {
		return thisAsUUID.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(String e) {
		return thisAsUUID.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(String e) {
		return thisAsUUID.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(String e) {
		return thisAsUUID.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(String e) {
		return thisAsUUID.gte(e);
	}

	@Override
	public UUIDExpression<?> max() {
		return thisAsUUID.max();
	}

	@Override
	public UUIDExpression<?> min() {
		return thisAsUUID.min();
	}
	
	@Override
	public BooleanExpression<?> isNull() {
		return thisAsUUID.isNull();
	}

	@Override
	public BooleanExpression<?> isNotNull() {
		return thisAsUUID.isNotNull();
	}
	
	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		return thisAsUUID.in(c);
	}

	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		return thisAsUUID.notIn(c);
	}
	
	@Override
	public BooleanExpression<?> inIgnoreCase(Collection<?> c) {
		return thisAsUUID.inIgnoreCase(c);
	}

	@Override
	public BooleanExpression<?> notInIgnoreCase(Collection<?> c) {
		return thisAsUUID.notInIgnoreCase(c);
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
		return thisAsUUID.concat(str);
	}

	@Override
	public StringExpression<?> concat(String str) {
		return thisAsUUID.concat(str);
	}

	@Override
	public StringExpression<?> lower() {
		return thisAsUUID.lower();
	}

	@Override
	public StringExpression<?> upper() {
		return thisAsUUID.upper();
	}

	@Override
	public NumericExpression<?> charLength() {
		return thisAsUUID.charLength();
	}

	@Override
	public StringExpression<?> aggregate(String delimeter) {
		return thisAsUUID.aggregate(delimeter);
	}

	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter) {
		return thisAsUUID.aggregate(delimeter);
	}

	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderBy) {
		return thisAsUUID.aggregate(delimeter, orderBy);
	}

	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderBy) {
		return thisAsUUID.aggregate(delimeter, orderBy);
	}

	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderedBy, boolean ascending) {
		return thisAsUUID.aggregate(delimeter, orderedBy, ascending);
	}

	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderedBy, boolean ascending) {
		return thisAsUUID.aggregate(delimeter, orderedBy, ascending);
	}

	
	
	@Override
	public BooleanExpression<?> eqIgnoreCase(StringExpression<?> e) {
		return thisAsUUID.eqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> neqIgnoreCase(StringExpression<?> e) {
		return thisAsUUID.neqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(StringExpression<?> e) {
		return thisAsUUID.ltIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(StringExpression<?> e) {
		return thisAsUUID.lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(StringExpression<?> e) {
		return thisAsUUID.gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(StringExpression<?> e) {
		return thisAsUUID.gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> eqIgnoreCase(String e) {
		return thisAsUUID.eqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> neqIgnoreCase(String e) {
		return thisAsUUID.neqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(String e) {
		return thisAsUUID.ltIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(String e) {
		return thisAsUUID.lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(String e) {
		return thisAsUUID.gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(String e) {
		return thisAsUUID.gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> like(StringExpression<?> e) {
		return thisAsUUID.like(e);
	}

	@Override
	public BooleanExpression<?> like(String e) {
		return thisAsUUID.like(e);
	}

	@Override
	public BooleanExpression<?> ilike(StringExpression<?> e) {
		return thisAsUUID.ilike(e);
	}

	@Override
	public BooleanExpression<?> ilike(String e) {
		return thisAsUUID.ilike(e);
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(StringExpression<?> e) {
		return thisAsUUID.likeIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(String e) {
		return thisAsUUID.likeIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		return thisAsUUID.in(e);
	}

	@Override
	public BooleanExpression<?> notIn(Expression<?> e) {
		return thisAsUUID.notIn(e);
	}
}
