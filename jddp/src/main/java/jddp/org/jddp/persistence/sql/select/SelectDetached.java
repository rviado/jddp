package org.jddp.persistence.sql.select;

import java.sql.Connection;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.UUIDExpression;
import org.jddp.expression.ZonedDateTimeExpression;

public interface SelectDetached<R> extends Expression<SelectDetached<R>> {

	public R execute(Connection connection);
	public Long executeCount(Connection connection);
	
	public String getSQL(boolean varAsLiterals);
	public String getCountSQL(boolean varAsLiterals);
	
	public String getSQL();
	public String getCountSQL();
	
	public StringExpression<?> asString();
	public UUIDExpression<?> asUUID();
	public ZonedDateTimeExpression<?> asZonedDateTime();
	public BooleanExpression<?> asBoolean();
	public NumericExpression<?> asNumeric();
	public ObjectExpression<?> asObject(Class<?> type);
	public ObjectExpression<?> asObjectCollection(Class<?> type);
	
	
	
}
