package org.jddp.expression.pgsql;

import java.util.List;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.LiteralExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.UUIDExpression;
import org.jddp.expression.ZonedDateTimeExpression;
import org.jddp.persistence.util.DBType;

public class Literal extends AbstractExpression<Literal, LiteralExpression> implements LiteralExpression {

	
	Literal(Expression<?> e) {
		super(e);
	}
	
	Literal(Literal e) {
		super(e);
	}
	
	public Literal(String literal, int modifier, DBType dbType) {
		super(modifier, dbType);
		variables.clear();
		_toString = literal;
	}
	
	
	public Literal(String literal) {
		super(0, null);
		variables.clear();
		_toString = literal;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public StringExpression<?> asString() {
		return new Str(new Literal(this.toString(), Expression.STRING, DBType.TEXT));
	}


	@Override
	public UUIDExpression<?> asUUID() {
		return new UID(new Literal(this.toString(), Expression.STRING, DBType.UUID));
	}


	@Override
	public ZonedDateTimeExpression<?> asZonedDateTime() {
		return new ZDT(new Literal(this.toString(), Expression.STRING, DBType.TIMESTAMPTZ));
	}


	@Override
	public BooleanExpression<?> asBoolean() {
		return new Bool(new Literal(this.toString(), Expression.BOOLEAN, DBType.BOOLEAN));
	}


	@Override
	public NumericExpression<?> asNumeric() {
		return new Num(new Literal(this.toString(), Expression.NUMERIC, DBType.NUMERIC));
	}


	@Override
	public ObjectExpression<?> asObject(Class<?> type) {
		return new Obj(new Literal(this.toString(), Expression.JSONOBJECT, DBType.JSONB), type, null);
	}


	@Override
	public ObjectExpression<?> asObjectCollection(Class<?> type) {
		return new Obj(new Literal(this.toString(), Expression.JSONOBJECT | Expression.ARRAY, DBType.JSONB), type, List.class);
	}

	@Override
	public LiteralExpression unBoundVariables() {
		return super.unBoundVariables();
	}
	
	@Override
	public LiteralExpression parenthesize() {
		return super.parenthesize();
	}

	@Override
	public LiteralExpression alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public LiteralExpression qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public LiteralExpression copy() {
		return  new Literal(this);
	}	
	
}
