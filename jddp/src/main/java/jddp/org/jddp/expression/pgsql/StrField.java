package org.jddp.expression.pgsql;

import java.util.Collection;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.StringFieldExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.util.DBType;

public class StrField extends Field<StringFieldExpression<?>> implements StringFieldExpression<StringFieldExpression<?>> {

	private final Str thisAsString;
	
	StrField(FieldExpression<?> b, FieldExpression<?> owner, Integer i) {
		super((Field<?>) b, owner, i, STRING | ARRAY, DBType.TEXT);
//		if (!(b instanceof StringExpression)) {
//			//_toString = new StringBuilder("CAST(").append(_toString).append(" as text)").toString();
//		}
		thisAsString = new Str(this);
	}
	
	public StrField(StrField o, FieldExpression<?> owner) {
		super(o, owner, null);
		if (!isString()) {
			throw new IllegalArgumentException(xpath + "(" + type + ") : is not a string type");
		}
		thisAsString = new Str(this);
	}
	
	public StrField(String xpath, String prefix, String fieldName,  Class<?> arrayType, Class<?> type, DBType dbType, int modifier, EntityClass<?> entityClass,	String[] accessor) {
		super(xpath, prefix, fieldName, arrayType, type, dbType, modifier, entityClass, accessor);
		if (!isString()) {
			throw new IllegalArgumentException(xpath + "(" + type + ") : is not a string type");
		}
		_toString = asTextReference();
		//leaf = prefix == null;
		thisAsString = new Str(this);
	}

	@Override
	public BooleanExpression<?> eq(StringExpression<?> e) {
		return thisAsString.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(StringExpression<?> e) {
		return thisAsString.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(StringExpression<?> e) {
		return thisAsString.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(StringExpression<?> e) {
		return thisAsString.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(StringExpression<?> e) {
		return thisAsString.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(StringExpression<?> e) {
		return thisAsString.gte(e);
	}

	@Override
	public BooleanExpression<?> eq(String e) {
		return thisAsString.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(String e) {
		return thisAsString.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(String e) {
		return thisAsString.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(String e) {
		return thisAsString.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(String e) {
		return thisAsString.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(String e) {
		return thisAsString.gte(e);
	}

	@Override
	public BooleanExpression<?> isNull() {
		return thisAsString.isNull();
	}

	@Override
	public BooleanExpression<?> isNotNull() {
		return thisAsString.isNotNull();
	}

	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		return thisAsString.in(e);
	}
	
	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		return thisAsString.in(c);
	}

	@Override
	public BooleanExpression<?> notIn(Expression<?> e) {
		return thisAsString.in(e);
	}
	
	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		return thisAsString.notIn(c);
	}

	@Override
	public StringExpression<?> concat(StringExpression<?> str) {
		return thisAsString.concat(str);
	}

	@Override
	public StringExpression<?> concat(String str) {
		return thisAsString.concat(str);
	}

	@Override
	public StringExpression<?> lower() {
		return thisAsString.lower();
	}

	@Override
	public StringExpression<?> upper() {
		return thisAsString.upper();
	}

	@Override
	public NumericExpression<?> charLength() {
		return thisAsString.charLength();
	}

	@Override
	public StringExpression<?> max() {
		return thisAsString.max();
	}
	
	@Override
	public StringExpression<?> min() {
		return thisAsString.min();
	}
	
	@Override
	public StringExpression<?> aggregate(String delimeter) {
		return thisAsString.aggregate(delimeter);
	}
	
	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter) {
		return thisAsString.aggregate(delimeter);
	}
	
	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderBy) {
		return thisAsString.aggregate(delimeter, orderBy);
	}
	
	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderBy) {
		return thisAsString.aggregate(delimeter, orderBy);
	}
	
	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderBy, boolean ascending) {
		return thisAsString.aggregate(delimeter, orderBy, ascending);
	}
	
	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderBy, boolean ascending) {
		return thisAsString.aggregate(delimeter, orderBy, ascending);
	}
	
	@Override
	public BooleanExpression<?> eqIgnoreCase(StringExpression<?> e) {
		return thisAsString.eqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> neqIgnoreCase(StringExpression<?> e) {
		return thisAsString.neqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(StringExpression<?> e) {
		return thisAsString.ltIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(StringExpression<?> e) {
		return thisAsString.lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(StringExpression<?> e) {
		return thisAsString.gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(StringExpression<?> e) {
		return thisAsString.gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> eqIgnoreCase(String e) {
		return thisAsString.eqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> neqIgnoreCase(String e) {
		return thisAsString.neqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(String e) {
		return thisAsString.ltIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(String e) {
		return thisAsString.lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(String e) {
		return thisAsString.gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(String e) {
		return thisAsString.gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> inIgnoreCase(Collection<?> c) {
		return thisAsString.inIgnoreCase(c);
	}

	@Override
	public BooleanExpression<?> notInIgnoreCase(Collection<?> c) {
		return thisAsString.notInIgnoreCase(c);
	}
	
	
	@Override
	public BooleanExpression<?> like(StringExpression<?> s) {
		return thisAsString.like(s);
	}

	@Override
	public BooleanExpression<?> like(String s) {
		return thisAsString.like(s);
	}

	@Override
	public BooleanExpression<?> ilike(StringExpression<?> s) {
		return thisAsString.ilike(s);
	}

	@Override
	public BooleanExpression<?> ilike(String s) {
		return thisAsString.ilike(s);
	}
	
	@Override
	public BooleanExpression<?> likeIgnoreCase(StringExpression<?> s) {
		return thisAsString.likeIgnoreCase(s);
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(String s) {
		return thisAsString.likeIgnoreCase(s);
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	@Override
	public StringFieldExpression<?> unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public StringFieldExpression<?> parenthesize() {
		return super.parenthesize();
	}

	@Override
	public StringFieldExpression<?> alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public StringFieldExpression<?> qualify(String qualifier) {
		return super.qualify(qualifier);
	}


	@Override
	public StringFieldExpression<?> copy() {
		StrField s;
		
		if (this.i != null) {
			s = new StrField(this, this.owner, this.i);
		} else {
			s = new StrField(this.xpath, this.prefix, this.fieldName, this.arrayType, this.type, this.dbType, this.modifier, this.entityClass, this.accessor);
			s.owner = this.owner;
		}
		
		return s;
	}

	
	
}
