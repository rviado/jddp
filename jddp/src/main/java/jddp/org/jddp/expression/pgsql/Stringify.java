package org.jddp.expression.pgsql;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.StringExpression;
import org.jddp.persistence.util.DBType;

public class Stringify extends Str {


	public Stringify(Expression<?> e) {
		super(e);
	}

	public Stringify(String str) {
		super(str);
	}

	protected StringExpression<?> coerce(StringExpression<?> e) {
		if (dbType != e.getDBType()) {
			return e.castInto(dbType);
		}
		return e;
	}

	protected StringExpression<?> coerce(String s) {
		if (dbType != DBType.TEXT) {
			return new Str(s).castInto(dbType);
		}
		return new Str(s);
	}
	
	@Override
	public BooleanExpression<?> eq(StringExpression<?> e) {
		return super.eq(coerce(e));
	}

	
	@Override
	public BooleanExpression<?> neq(StringExpression<?> e) {
		return super.neq(coerce(e));
	}

	@Override
	public BooleanExpression<?> lt(StringExpression<?> e) {
		return super.lt(coerce(e));
	}

	@Override
	public BooleanExpression<?> lte(StringExpression<?> e) {
		return super.lte(coerce(e));
	}

	@Override
	public BooleanExpression<?> gt(StringExpression<?> e) {
		return super.gt(coerce(e));
	}

	@Override
	public BooleanExpression<?> gte(StringExpression<?> e) {
		return super.gte(coerce(e));
	}

	@Override
	public BooleanExpression<?> eq(String s) {
		return super.eq(coerce(s));
	}
	
	@Override
	public BooleanExpression<?> neq(String s) {
		return super.neq(coerce(s));
	}

	@Override
	public BooleanExpression<?> lt(String s) {
		return super.lt(coerce(s));
	}

	@Override
	public BooleanExpression<?> lte(String s) {
		return super.lte(coerce(s));
	}

	@Override
	public BooleanExpression<?> gt(String s) {
		return super.gt(coerce(s));
	}

	@Override
	public BooleanExpression<?> gte(String s) {
		return super.gte(coerce(s));
	}

	@Override
	public BooleanExpression<?> like(String s) {
		return super.castAsString().like(s);
	}
	
	@Override
	public StringExpression<?> aggregate(String delimeter) {
		return super.castAsString().aggregate(delimeter);
	}
	
	@Override
	public StringExpression<?> aggregate(StringExpression<?> delimeter) {
		return super.castAsString().aggregate(delimeter);
	}
	
	@Override
	public StringExpression<?> concat(StringExpression<?> str) {
		return super.castAsString().concat(str);
	}

	@Override
	public StringExpression<?> concat(String str) {
		return super.castAsString().concat(str);
	}

	@Override
	public NumericExpression<?> charLength() {
		return super.castAsString().charLength();
	}
	
	@Override
	public BooleanExpression<?> like(StringExpression<?> s) {
		return super.castAsString().like(s);
	}
	
	@Override
	public StringExpression<?> lower() {
		return super.castAsString().lower();
	}

	@Override
	public StringExpression<?> upper() {
		return super.castAsString().upper();
	}

	
	@Override
	public BooleanExpression<?> eqIgnoreCase(StringExpression<?> e) {
		return super.castAsString().eqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> neqIgnoreCase(StringExpression<?> e) {
		return super.castAsString().neqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(StringExpression<?> e) {
		return super.castAsString().lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(StringExpression<?> e) {
		return super.castAsString().lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(StringExpression<?> e) {
		return super.castAsString().gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(StringExpression<?> e) {
		return super.castAsString().gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> eqIgnoreCase(String e) {
		return super.castAsString().eqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> neqIgnoreCase(String e) {
		return super.castAsString().neqIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> ltIgnoreCase(String e) {
		return super.castAsString().ltIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> lteIgnoreCase(String e) {
		return super.castAsString().lteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gtIgnoreCase(String e) {
		return super.castAsString().gtIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> gteIgnoreCase(String e) {
		return super.castAsString().gteIgnoreCase(e);
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(StringExpression<?> s) {
		return super.castAsString().likeIgnoreCase(s);
	}

	@Override
	public BooleanExpression<?> likeIgnoreCase(String s) {
		return super.castAsString().likeIgnoreCase(s);
	}

	
}
