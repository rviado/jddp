package org.jddp.expression.pgsql;

import java.util.Collection;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.BooleanFieldExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.util.DBType;

public class BoolField extends Field<BooleanFieldExpression> implements BooleanFieldExpression {

	private final Bool thisAsBoolean;
	
	BoolField(FieldExpression<?> b, FieldExpression<?> owner, Integer i) {
		super((Field<?>) b, owner, i, BOOLEAN | ARRAY, DBType.BOOLEAN);
		_toString = new StringBuilder("CAST(").append(_toString).append(" as boolean)").toString();
		thisAsBoolean = new Bool(this);
	}
	
	public BoolField(BoolField b, FieldExpression<?> owner) {
		super(b, owner, null);
		if (!isBoolean()) {
			throw new IllegalArgumentException(xpath + "(" + type + ") : is not a boolean type");
		}
		thisAsBoolean = new Bool(this);
	}
	
	public BoolField(String xpath, String prefix, String fieldName, Class<?> arrayType, Class<?> type, DBType dbType, int modifier, EntityClass<?> entityClass,String[] accessor) {
		super(xpath, prefix, fieldName, arrayType, type, dbType, modifier , entityClass, accessor);
		if (!isBoolean()) {
			throw new IllegalArgumentException(xpath + "(" + type + ") : is not a boolean type");
		}
		if (prefix != null) {
			_toString = new StringBuilder("CAST(").append(asTextReference()).append(" as boolean)").toString();
		}
		
		thisAsBoolean = new Bool(this);
	}
	
	
	@Override
	public BooleanExpression<?> eq(BooleanExpression<?> e) {
		return thisAsBoolean.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(BooleanExpression<?> e) {
		return thisAsBoolean.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(BooleanExpression<?> e) {
		return thisAsBoolean.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(BooleanExpression<?> e) {
		return thisAsBoolean.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(BooleanExpression<?> e) {
		return thisAsBoolean.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(BooleanExpression<?> e) {
		return thisAsBoolean.gte(e);
	}

	@Override
	public BooleanExpression<?> eq(Boolean e) {
		return thisAsBoolean.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(Boolean e) {
		return thisAsBoolean.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(Boolean e) {
		return thisAsBoolean.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(Boolean e) {
		return thisAsBoolean.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(Boolean e) {
		return thisAsBoolean.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(Boolean e) {
		return thisAsBoolean.gte(e);
	}

	@Override
	public BooleanExpression<?> isNull() {
		return thisAsBoolean.isNull();
	}

	@Override
	public BooleanExpression<?> isNotNull() {
		return thisAsBoolean.isNotNull();
	}

	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		return thisAsBoolean.in(e);
	}
	
	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		return thisAsBoolean.in(c);
	}

	@Override
	public BooleanExpression<?> notIn(Expression<?> e) {
		return thisAsBoolean.notIn(e);
	}
	
	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		return thisAsBoolean.notIn(c);
	}

	@Override
	public BooleanExpression<?> and(BooleanExpression<?> bool) {
		return thisAsBoolean.and(bool);
	}

	@Override
	public BooleanExpression<?> or(BooleanExpression<?> bool) {
		return thisAsBoolean.or(bool);
	}

	@Override
	public BooleanExpression<?> and(Boolean bool) {
		return thisAsBoolean.and(bool);
	}

	@Override
	public BooleanExpression<?> or(Boolean bool) {
		return thisAsBoolean.or(bool);
	}

	@Override
	public BooleanExpression<?> negate() {
		return thisAsBoolean.negate();
	}
	

	@Override
	public boolean isLeaf() {
		return true;
	}
	@Override
	public BooleanFieldExpression unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public BooleanFieldExpression parenthesize() {
		return super.parenthesize();
	}

	@Override
	public BooleanFieldExpression alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public BooleanFieldExpression qualify(String qualifier) {
		return super.qualify(qualifier);
	}
	
	@Override 
	public BooleanFieldExpression copy() {
		BoolField b;
		if (this.i != null) {
			b = new BoolField(this, this.owner, this.i);
		} else {
			b = new BoolField(this.xpath, this.prefix, this.fieldName, this.arrayType, this.type, this.dbType, this.modifier, this.entityClass, this.accessor);
			b.owner = this.owner;
		}
		
		return b;
	}
	
}
