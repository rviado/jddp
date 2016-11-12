package org.jddp.persistence.sql.update;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.BooleanFieldExpression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.NumericFieldExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.ObjectFieldExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.StringFieldExpression;
import org.jddp.expression.UUIDExpression;
import org.jddp.expression.UUIDFieldExpression;
import org.jddp.expression.ZonedDateTimeExpression;
import org.jddp.expression.ZonedDateTimeFieldExpression;

public interface UpdateStatement extends UpdateConditional {
	public UpdateConditional where(BooleanExpression<?> whereCondition);
	
	public UpdateStatement set(StringFieldExpression<?> field, String value);
	public UpdateStatement set(StringFieldExpression<?> field, StringExpression<?> value);
	
	public UpdateStatement set(NumericFieldExpression field, Number value);
	public UpdateStatement set(NumericFieldExpression field, NumericExpression<?> value);
	
	public UpdateStatement set(BooleanFieldExpression field, Boolean value);
	public UpdateStatement set(BooleanFieldExpression field, BooleanExpression<?> value);
	
	public UpdateStatement set(ObjectFieldExpression field, Object value);
	public UpdateStatement set(ObjectFieldExpression field, ObjectExpression<?> value);
	
	public UpdateStatement set(UUIDFieldExpression field, UUID value);
	public UpdateStatement set(UUIDFieldExpression field, UUIDExpression<?> value);
	
	public UpdateStatement set(ZonedDateTimeFieldExpression field, ZonedDateTime value);
	public UpdateStatement set(ZonedDateTimeFieldExpression field, OffsetDateTime value);
	public UpdateStatement set(ZonedDateTimeFieldExpression field, ZonedDateTimeExpression<?> value);
	
	
	public UpdateStatement unset(FieldExpression<?> field);

	
	
	
	
}
