package org.jddp.expression;



public interface LiteralExpression extends Expression<LiteralExpression> {
	
	public StringExpression<?>		asString();
	public UUIDExpression<?>     		asUUID();
	public ZonedDateTimeExpression<?>  asZonedDateTime();
	public BooleanExpression<?>  	asBoolean();
	public NumericExpression<?>  	asNumeric();
	public ObjectExpression<?>   		asObject(Class<?> type);
	public ObjectExpression<?>   		asObjectCollection(Class<?> type);
	
	public LiteralExpression 	quote();
	public LiteralExpression 	quote(String q);
	
}
