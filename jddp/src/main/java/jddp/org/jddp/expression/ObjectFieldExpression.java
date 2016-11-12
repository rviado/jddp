package org.jddp.expression;

public interface ObjectFieldExpression extends FieldExpression<ObjectFieldExpression>, ObjectExpression<ObjectFieldExpression> {
	
	public BooleanFieldExpression castAsBoolean();
	public NumericFieldExpression castAsNumeric();
	public StringFieldExpression<?> castAsString();
	
}
