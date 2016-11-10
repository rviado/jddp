package org.jddp.expression;

public interface VariableExpression extends Expression<VariableExpression> {

	public String getName();
	public Object getValue();
	public String getValueAsLiteral();

}
