package org.jddp.expression;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZonedDateTime;

public interface ZonedDateTimeExpression<E extends StringExpression<?>> extends StringExpression<E> {
	
	public BooleanExpression<?> eq(ZonedDateTime zdt);
	public BooleanExpression<?> eq(OffsetDateTime odt);
	
	public BooleanExpression<?> neq(ZonedDateTime zdt);
	public BooleanExpression<?> neq(OffsetDateTime odt);
	
	public BooleanExpression<?> lt(ZonedDateTime zdt);
	public BooleanExpression<?> lt(OffsetDateTime odt);
	
	public BooleanExpression<?> lte(ZonedDateTime zdt);
	public BooleanExpression<?> lte(OffsetDateTime odt);
	
	public BooleanExpression<?> gt(ZonedDateTime zdt);
	public BooleanExpression<?> gt(OffsetDateTime odt);
	
	public BooleanExpression<?> gte(ZonedDateTime zdt);
	public BooleanExpression<?> gte(OffsetDateTime odt);
	
	public BooleanExpression<?> between(ZonedDateTime zdt1, ZonedDateTime zdt2);
	public BooleanExpression<?> between(OffsetDateTime odt1, OffsetDateTime odt2);
	
	public ZonedDateTimeExpression<?> plus(Duration d);
	public ZonedDateTimeExpression<?> minus(Duration d);
	
	public ZonedDateTimeExpression<?> plus(Period p);
	public ZonedDateTimeExpression<?> minus(Period p);
	
	public ZonedDateTimeExpression<?> plus(Period p, Duration d);
	public ZonedDateTimeExpression<?> minus(Period p, Duration d);
	
	public ObjectExpression<?> age(ZonedDateTime zdt);
	public ObjectExpression<?> age(OffsetDateTime odt);
	
}
