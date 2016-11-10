package org.jddp.expression;

import java.util.Collection;

public interface StringExpression<E extends StringExpression<?>> extends RelationalExpression<E, StringExpression<?>, String> {
	
	public StringExpression<?> concat(StringExpression<?> str);
	public StringExpression<?> concat(String str);
		
	public StringExpression<?> lower();
	public StringExpression<?> upper();
	
	public NumericExpression<?> charLength();
	public StringExpression<?> max();
	public StringExpression<?> min();
	public StringExpression<?> aggregate(String delimeter);
	public StringExpression<?> aggregate(StringExpression<?> delimeter);
	
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderBy);
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderBy);
	public StringExpression<?> aggregate(String delimeter, Expression<?> orderedBy, boolean ascending);
	public StringExpression<?> aggregate(StringExpression<?> delimeter, Expression<?> orderedBy, boolean ascending);
	
	public BooleanExpression<?> eqIgnoreCase(StringExpression<?> e);
	public BooleanExpression<?> neqIgnoreCase(StringExpression<?> e);	
	public BooleanExpression<?> ltIgnoreCase(StringExpression<?> e);
	public BooleanExpression<?> lteIgnoreCase(StringExpression<?> e);
	public BooleanExpression<?> gtIgnoreCase(StringExpression<?> e);
	public BooleanExpression<?> gteIgnoreCase(StringExpression<?> e);
	
	public BooleanExpression<?> eqIgnoreCase(String e);
	public BooleanExpression<?> neqIgnoreCase(String e);	
	public BooleanExpression<?> ltIgnoreCase(String e);
	public BooleanExpression<?> lteIgnoreCase(String e);
	public BooleanExpression<?> gtIgnoreCase(String e);
	public BooleanExpression<?> gteIgnoreCase(String e);

	public BooleanExpression<?> inIgnoreCase(Collection<?> c);
	public BooleanExpression<?> notInIgnoreCase(Collection<?> c);
	
	
	public BooleanExpression<?> like(StringExpression<?> c);
	public BooleanExpression<?> like(String c);
	
	public BooleanExpression<?> ilike(StringExpression<?> c);
	public BooleanExpression<?> ilike(String c);
	
	public BooleanExpression<?> likeIgnoreCase(StringExpression<?> c);
	public BooleanExpression<?> likeIgnoreCase(String c);
	
}
