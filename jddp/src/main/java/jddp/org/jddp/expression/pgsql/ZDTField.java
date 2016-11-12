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



public class ZDTField extends Field<ZonedDateTimeFieldExpression> implements  ZonedDateTimeFieldExpression {
	
	private final ZDT thisAsZonedDateTime;
	
	public ZDTField(ZDTField o, FieldExpression<?> owner) {
		super(o, owner, null);
		thisAsZonedDateTime = new ZDT(this);
	}
	
	public ZDTField(String xpath, String prefix, String fieldName, Class<?> arrayType, Class<?> type, DBType dbType,
			int modifier, EntityClass<?> entityClass, String[] accessor) {
		super(xpath, prefix, fieldName, arrayType, type, dbType, modifier, entityClass, accessor);
		thisAsZonedDateTime = new ZDT(this);
	}
	
	
	@Override
	public ZonedDateTimeExpression<?> max() {
		return thisAsZonedDateTime.max();
	}

	@Override
	public ZonedDateTimeExpression<?> min() {
		return thisAsZonedDateTime.min();
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
	public BooleanExpression<?> eq(StringExpression<?> e) {
		return thisAsZonedDateTime.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(StringExpression<?> e) {
		return thisAsZonedDateTime.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(StringExpression<?> e) {
		return thisAsZonedDateTime.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(StringExpression<?> e) {
		return thisAsZonedDateTime.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(StringExpression<?> e) {
		return thisAsZonedDateTime.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(StringExpression<?> e) {
		return thisAsZonedDateTime.gte(e);
	}

	@Override
	public BooleanExpression<?> eq(String e) {
		return thisAsZonedDateTime.eq(e);
	}

	@Override
	public BooleanExpression<?> neq(String e) {
		return thisAsZonedDateTime.neq(e);
	}

	@Override
	public BooleanExpression<?> lt(String e) {
		return thisAsZonedDateTime.lt(e);
	}

	@Override
	public BooleanExpression<?> lte(String e) {
		return thisAsZonedDateTime.lte(e);
	}

	@Override
	public BooleanExpression<?> gt(String e) {
		return thisAsZonedDateTime.gt(e);
	}

	@Override
	public BooleanExpression<?> gte(String e) {
		return thisAsZonedDateTime.gte(e);
	}

	@Override
	public BooleanExpression<?> isNull() {
		return thisAsZonedDateTime.isNull();
	}

	@Override
	public BooleanExpression<?> isNotNull() {
		return thisAsZonedDateTime.isNotNull();
	}

	@Override
	public BooleanExpression<?> in(Expression<?> e) {
		return thisAsZonedDateTime.in(e);
	}

	@Override
	public BooleanExpression<?> notIn(Expression<?> e) {
		return thisAsZonedDateTime.notIn(e);
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
	public BooleanExpression<?> neqIgnoreCase(StringExpression<?> e) {
		return thisAsZonedDateTime.neqIgnoreCase(e);
	}
	
	
	@Override
	public ZonedDateTimeFieldExpression parenthesize() {
		return  super.parenthesize();
	}
	
	@Override
	public ZonedDateTimeFieldExpression unBoundVariables() {
		return super.unBoundVariables();
	}
	@Override
	public ZonedDateTimeFieldExpression alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public ZonedDateTimeFieldExpression qualify(String qualifier) {
		return  super.qualify(qualifier);
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
		return thisAsZonedDateTime.concat(str);
	}

	@Override
	public StringExpression<?> concat(String str) {
		return thisAsZonedDateTime.concat(str);
	}

	@Override
	public StringExpression<?> lower() {
		return thisAsZonedDateTime.lower();
	}

	@Override
	public StringExpression<?> upper() {
		return thisAsZonedDateTime.upper();
	}

	@Override
	public NumericExpression<?> charLength() {
		return thisAsZonedDateTime.charLength();
	}

	
	@Override
	public StringExpression<?> aggregate(String delimeter) {
		return thisAsZonedDateTime.aggregate(delimeter);
	}

	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter) {
		return thisAsZonedDateTime.aggregate(delimeter);
	}

	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderBy) {
		return thisAsZonedDateTime.aggregate(delimeter, orderBy);
	}

	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderBy) {
		return thisAsZonedDateTime.aggregate(delimeter, orderBy);
	}

	@Override
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderedBy, boolean ascending) {
		return thisAsZonedDateTime.aggregate(delimeter, orderedBy, ascending);
	}

	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderedBy, boolean ascending) {
		return thisAsZonedDateTime.aggregate(delimeter, orderedBy, ascending);
	}

	@Override
	public BooleanExpression<?> eqIgnoreCase(StringExpression<?> e) {
		return thisAsZonedDateTime.eqIgnoreCase(e);
	}
	

	@Override
	public BooleanExpression<?> ltIgnoreCase(StringExpression<?> e) {
		return thisAsZonedDateTime.ltIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(StringExpression<?> e) {
		return thisAsZonedDateTime.lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(StringExpression<?> e) {
		return thisAsZonedDateTime.gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(StringExpression<?> e) {
		return thisAsZonedDateTime.gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> eqIgnoreCase(String e) {
		return thisAsZonedDateTime.eqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> neqIgnoreCase(String e) {
		return thisAsZonedDateTime.neqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(String e) {
		return thisAsZonedDateTime.ltIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(String e) {
		return thisAsZonedDateTime.lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(String e) {
		return thisAsZonedDateTime.gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(String e) {
		return thisAsZonedDateTime.gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> like(StringExpression<?> e) {
		return thisAsZonedDateTime.like(e);
	}

	@Override
	public BooleanExpression<?> like(String e) {
		return thisAsZonedDateTime.like(e);
	}

	@Override
	public BooleanExpression<?> ilike(StringExpression<?> e) {
		return thisAsZonedDateTime.ilike(e);
	}

	@Override
	public BooleanExpression<?> ilike(String e) {
		return thisAsZonedDateTime.ilike(e);
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(StringExpression<?> e) {
		return thisAsZonedDateTime.likeIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(String e) {
		return thisAsZonedDateTime.likeIgnoreCase(e);
	}

	

	

}
