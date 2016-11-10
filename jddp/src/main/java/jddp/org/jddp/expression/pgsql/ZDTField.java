package org.jddp.expression.pgsql;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Collection;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.ZonedDateTimeExpression;
import org.jddp.expression.ZonedDateTimeFieldExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.util.DBType;



public class ZDTField extends Field<ZonedDateTimeFieldExpression> implements StringExpression<ZonedDateTimeFieldExpression>, ZonedDateTimeFieldExpression {
	
	private final ZDT thisAsZonedDateTime;
	private final Stringify stringified;
	
	public ZDTField(ZDTField o, FieldExpression<?> owner) {
		super(o, owner, null);
		thisAsZonedDateTime = new ZDT(this);
		stringified = new Stringify(this);
	}
	
	public ZDTField(String xpath, String prefix, String fieldName, Class<?> arrayType, Class<?> type, DBType dbType,
			int modifier, EntityClass<?> entityClass, String[] accessor) {
		super(xpath, prefix, fieldName, arrayType, type, dbType, modifier, entityClass, accessor);
		thisAsZonedDateTime = new ZDT(this);
		stringified = new Stringify(this);
	}
	
	@Override
	public BooleanExpression<?> eq(ZonedDateTime zdt) {
		return thisAsZonedDateTime.eq(zdt);
	}

	@Override
	public BooleanExpression<?> neq(ZonedDateTime zdt) {
		return thisAsZonedDateTime.neq(zdt);
	}

	@Override
	public BooleanExpression<?> lt(ZonedDateTime zdt) {
		return thisAsZonedDateTime.lt(zdt);
	}

	@Override
	public BooleanExpression<?> lte(ZonedDateTime zdt) {
		return thisAsZonedDateTime.lte(zdt);
	}

	@Override
	public BooleanExpression<?> gt(ZonedDateTime zdt) {
		return thisAsZonedDateTime.gt(zdt);
	}

	@Override
	public BooleanExpression<?> gte(ZonedDateTime zdt) {
		return thisAsZonedDateTime.gte(zdt);
	}


	public BooleanExpression<?> eq(OffsetDateTime odt) {
		return thisAsZonedDateTime.eq(odt);
	}

	@Override
	public BooleanExpression<?> neq(OffsetDateTime odt) {
		return thisAsZonedDateTime.neq(odt);
	}

	@Override
	public BooleanExpression<?> lt(OffsetDateTime odt) {
		return thisAsZonedDateTime.lt(odt);
	}

	@Override
	public BooleanExpression<?> lte(OffsetDateTime odt) {
		return thisAsZonedDateTime.lte(odt);
	}

	@Override
	public BooleanExpression<?> gt(OffsetDateTime odt) {
		return thisAsZonedDateTime.gt(odt);
	}

	@Override
	public BooleanExpression<?> gte(OffsetDateTime odt) {
		return thisAsZonedDateTime.gte(odt);
	}
	
	@Override
	public BooleanExpression<?> between(ZonedDateTime zdt1, ZonedDateTime zdt2) {
		return thisAsZonedDateTime.between(zdt1, zdt2);
	}

	@Override
	public BooleanExpression<?> between(OffsetDateTime odt1, OffsetDateTime odt2) {
		return thisAsZonedDateTime.between(odt1, odt2);
	}
	
	@Override
	public ZonedDateTimeExpression<?> plus(Period p) {
		return thisAsZonedDateTime.plus(p);
	}

	@Override
	public ZonedDateTimeExpression<?> minus(Period p) {
		return thisAsZonedDateTime.minus(p);
	}

	@Override
	public ZonedDateTimeExpression<?> plus(Period p, Duration d) {
		return thisAsZonedDateTime.plus(p, d);
	}

	@Override
	public ZonedDateTimeExpression<?> minus(Period p, Duration d) {
		return thisAsZonedDateTime.minus(p, d);
	}

	@Override
	public ZonedDateTimeExpression<?> plus(Duration d) {
		return thisAsZonedDateTime.plus(d);
	}

	@Override
	public ZonedDateTimeExpression<?> minus(Duration d) {
		return thisAsZonedDateTime.minus(d);
	}

	@Override
	public ObjectExpression<?> age(ZonedDateTime zdt) {
		return thisAsZonedDateTime.age(zdt);
	}
	
	@Override
	public ObjectExpression<?> age(OffsetDateTime odt) {
		return thisAsZonedDateTime.age(odt);
	}
	
	@Override
	public BooleanExpression<?> in(Collection<?> c) {
		return thisAsZonedDateTime.in(c);
	}

	@Override
	public BooleanExpression<?> notIn(Collection<?> c) {
		return thisAsZonedDateTime.notIn(c);
	}

	@Override
	public BooleanExpression<?> inIgnoreCase(Collection<?> c) {
		return thisAsZonedDateTime.inIgnoreCase(c);
	}

	@Override
	public BooleanExpression<?> notInIgnoreCase(Collection<?> c) {
		return thisAsZonedDateTime.notInIgnoreCase(c);
	}
	
	@Override
	public ZonedDateTimeFieldExpression parenthesize() {
		return (ZonedDateTimeFieldExpression) super.parenthesize();
	}
	
	@Override
	public ZonedDateTimeFieldExpression unBoundVariables() {
		return (ZonedDateTimeFieldExpression) super.unBoundVariables();
	}
	@Override
	public ZonedDateTimeFieldExpression alias(String alias) {
		return (ZonedDateTimeFieldExpression) super.alias(alias);
	}

	@Override
	public ZonedDateTimeFieldExpression qualify(String qualifier) {
		return (ZonedDateTimeFieldExpression) super.qualify(qualifier);
	}
	
	@Override
	public ZonedDateTimeFieldExpression copy() {
		return new ZDTField(this, this.owner);
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
