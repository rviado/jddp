package org.jddp.expression.pgsql;

import java.util.Collection;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.NumericFieldExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.util.DBType;

public class NumField extends Field<NumericFieldExpression> implements NumericFieldExpression {

	private final Num thisAsNumeric;
	
	public NumField(FieldExpression<?> b, FieldExpression<?> owner, Integer i) {
		super((Field<?>) b, owner, i, NUMERIC | ARRAY, DBType.NUMERIC);
		_toString = new StringBuilder("CAST(").append(_toString).append(" as numeric)").toString();
		thisAsNumeric = new Num(this);
	}
	
	public NumField(NumField n, FieldExpression<?> owner) {
		super(n, owner, null);
		if (!isNumeric()) {
			throw new IllegalArgumentException(xpath + "(" + type + ") : is not a numeric type");
		}
		thisAsNumeric = new Num(this);
	}
	
	public NumField(String xpath, String prefix, String fieldName, Class<?> arrayType, Class<?> type, DBType dbType, int modifier, EntityClass<?> entityClass,	String[] accessor) {
		super(xpath, prefix, fieldName, arrayType, type, dbType, modifier, entityClass, accessor);
		if (!isNumeric()) {
			throw new IllegalArgumentException(xpath + "(" + type + ") : is not a numeric type");
		}
		
		if (prefix != null) {
			_toString = new StringBuilder("CAST(").append(asTextReference()).append(" as numeric)").toString();
		} 
		thisAsNumeric = new Num(this);
	}
	
	
	
	@Override
	public BooleanExpression<?> eq(NumericExpression<?> e) {
		return thisAsNumeric.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(NumericExpression<?> e) {
		return thisAsNumeric.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(NumericExpression<?> e) {
		return thisAsNumeric.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(NumericExpression<?> e) {
		return thisAsNumeric.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(NumericExpression<?> e) {
		return thisAsNumeric.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(NumericExpression<?> e) {
		return thisAsNumeric.gte(e);
	}

	@Override
	public BooleanExpression<?> eq(Number e) {
		return thisAsNumeric.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(Number e) {
		return thisAsNumeric.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(Number e) {
		return thisAsNumeric.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(Number e) {
		return thisAsNumeric.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(Number e) {
		return thisAsNumeric.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(Number e) {
		return thisAsNumeric.gte(e);
	}

	@Override
	public BooleanExpression<?> isNull() {
		return thisAsNumeric.isNull();
	}

	@Override
	public BooleanExpression<?> isNotNull() {
		return thisAsNumeric.isNotNull();
	}
	
	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		return thisAsNumeric.in(e);
	}
	
	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		return thisAsNumeric.in(c);
	}

	@Override
	public BooleanExpression<?> notIn(Expression<?> e) {
		return thisAsNumeric.notIn(e);
	}
	
	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		return thisAsNumeric.notIn(c);
	}

	@Override
	public NumericExpression<?> plus(NumericExpression<?> m) {
		return thisAsNumeric.plus(m);
	}

	@Override
	public NumericExpression<?> plus(Number n) {
		return thisAsNumeric.plus(n);
	}

	@Override
	public NumericExpression<?> minus(NumericExpression<?> m) {
		return thisAsNumeric.minus(m);
	}

	@Override
	public NumericExpression<?> minus(Number n) {
		return thisAsNumeric.minus(n);
	}

	@Override
	public NumericExpression<?> div(NumericExpression<?> m) {
		return thisAsNumeric.div(m);
	}

	@Override
	public NumericExpression<?> div(Number n) {
		return thisAsNumeric.div(n);
	}

	@Override
	public NumericExpression<?> mul(NumericExpression<?> m) {
		return thisAsNumeric.mul(m);
	}

	@Override
	public NumericExpression<?> mul(Number n) {
		return thisAsNumeric.mul(n);
	}
	
	@Override
	public NumericExpression<?> power(NumericExpression<?> m) {
		return thisAsNumeric.power(m);
	}

	@Override
	public NumericExpression<?> power(Number n) {
		return thisAsNumeric.power(n);
	}

	@Override
	public NumericExpression<?> mod(NumericExpression<?> m) {
		return thisAsNumeric.mod(m);
	}

	@Override
	public NumericExpression<?> mod(Number n) {
		return thisAsNumeric.mod(n);
	}
	
	@Override
	public NumericExpression<?> negate() {
		return thisAsNumeric.negate();
	}
	
	@Override
	public NumericExpression<?> and(NumericExpression<?> m) {
		return thisAsNumeric.and(m);
	}

	@Override
	public NumericExpression<?> and(Number n) {
		return thisAsNumeric.and(n);
	}

	@Override
	public NumericExpression<?> or(NumericExpression<?> m) {
		return thisAsNumeric.or(m);
	}

	@Override
	public NumericExpression<?> or(Number n) {
		return thisAsNumeric.or(n);
	}

	@Override
	public NumericExpression<?> xor(NumericExpression<?> m) {
		return thisAsNumeric.xor(m);
	}

	@Override
	public NumericExpression<?> xor(Number n) {
		return thisAsNumeric.xor(n);
	}

	@Override
	public NumericExpression<?> not() {
		return thisAsNumeric.not();
	}

	@Override
	public NumericExpression<?> shiftLeft(NumericExpression<?> m) {
		return thisAsNumeric.shiftLeft(m);
	}

	@Override
	public NumericExpression<?> shiftLeft(Number n) {
		return thisAsNumeric.shiftLeft(n);
	}

	@Override
	public NumericExpression<?> shiftRight(NumericExpression<?> m) {
		return thisAsNumeric.shiftRight(m);
	}

	@Override
	public NumericExpression<?> shiftRight(Number n) {
		return thisAsNumeric.shiftRight(n);
	}

	@Override
	public NumericExpression<?> abs() {
		return thisAsNumeric.abs();
	}
	
	@Override
	public NumericExpression<?> max() {
		return thisAsNumeric.max();
	}
	
	@Override
	public NumericExpression<?> min() {
		return thisAsNumeric.min();
	}
	
	@Override
	public NumericExpression<?> sum() {
		return thisAsNumeric.sum();
	}
	
	@Override
	public NumericExpression<?> avg() {
		return thisAsNumeric.avg();
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	@Override
	public NumericFieldExpression unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public NumericFieldExpression parenthesize() {
		return super.parenthesize();
	}

	@Override
	public NumericFieldExpression alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public NumericFieldExpression qualify(String qualifier) {
		return super.qualify(qualifier);
	}
	
	@Override
	public NumericFieldExpression copy() {
		NumField s;
		if (this.i != null) {
			s = new NumField(this, this.owner, this.i);
		} else {
			s = new NumField(this.xpath, this.prefix, this.fieldName, this.arrayType, this.type, this.dbType, this.modifier, this.entityClass, this.accessor);
			s.owner = this.owner;
		}
		
		return s;
	}
}
