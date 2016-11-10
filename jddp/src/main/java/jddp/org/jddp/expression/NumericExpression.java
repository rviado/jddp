package org.jddp.expression;

public interface NumericExpression<E extends NumericExpression<?>> extends  RelationalExpression<E, NumericExpression<?>, Number> {
	
	public NumericExpression<?> plus(NumericExpression<?> m);
	public NumericExpression<?> plus(Number n);
	
	public NumericExpression<?> minus(NumericExpression<?> m);	
	public NumericExpression<?> minus(Number n);
	
	public NumericExpression<?> div(NumericExpression<?> m);	
	public NumericExpression<?> div(Number n);
	
	public NumericExpression<?> mul(NumericExpression<?> m);	
	public NumericExpression<?> mul(Number n);
	
	public NumericExpression<?> mod(NumericExpression<?> m);	
	public NumericExpression<?> mod(Number n);
	
	public NumericExpression<?> power(NumericExpression<?> m);	
	public NumericExpression<?> power(Number n);
	
	public NumericExpression<?> and(NumericExpression<?> m);	
	public NumericExpression<?> and(Number n);
	
	public NumericExpression<?> or(NumericExpression<?> m);	
	public NumericExpression<?> or(Number n);
	
	public NumericExpression<?> xor(NumericExpression<?> m);	
	public NumericExpression<?> xor(Number n);
	
	public NumericExpression<?> not();	
		
	public NumericExpression<?> shiftLeft(NumericExpression<?> m);	
	public NumericExpression<?> shiftLeft(Number n);
	
	public NumericExpression<?> shiftRight(NumericExpression<?> m);	
	public NumericExpression<?> shiftRight(Number n);

	
	public NumericExpression<?> abs();
	
	public NumericExpression<?> negate();
	
	public NumericExpression<?> max();
	public NumericExpression<?> min();

	public NumericExpression<?> sum();
	public NumericExpression<?> avg();
	
	
}
