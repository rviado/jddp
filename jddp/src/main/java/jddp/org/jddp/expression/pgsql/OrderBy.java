package org.jddp.expression.pgsql;

import java.util.Set;

import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.util.DBType;

public class OrderBy extends AbstractExpression<OrderBy, OrderBy>  {

	public enum DIRECTION { 
		DEFAULT, ASC, DESC;  
		
		public String toString() {return this == DEFAULT ? "" : this.name();} 
	}
	
	Expression<?> expression;
	DIRECTION direction;
	
	OrderBy(OrderBy o) {
		super(0, null);
		expression = o.expression;
		direction = o.direction;
	}
	
	public OrderBy(Expression<?> o) {
		super(0, null);
		expression = o;
		direction = DIRECTION.DEFAULT;
	}
	
	public OrderBy(Expression<?> o, DIRECTION direction) {
		super(0, null);
		expression = o;
		this.direction = direction; 
	}
	
	public OrderBy asc() {
		OrderBy o = new OrderBy(this);
		o.direction = DIRECTION.ASC;
		return o;
	}
	
	public OrderBy desc() {
		OrderBy o = new OrderBy(this);
		o.direction = DIRECTION.DESC;
		return o;
	}
	
	public OrderBy _default() {
		OrderBy o = new OrderBy(this);
		o.direction = DIRECTION.DEFAULT;
		return o;
	}
	
	@Override
	public Set<VariableExpression> getBoundVariables() {
		return expression.getBoundVariables();
	}
	
	
	public Set<FieldExpression<?>> getFields() {
		return expression.getFields();
	}
	
	
	
	public String toString() {
		switch(direction) {
		case ASC:
		case DESC:
			if (expression instanceof LIST && ((LIST) expression).length() > 1 ) {
				 return new StringBuilder("(").append(expression.toString()).append(") ").append(direction).toString();
			} else {
				return new StringBuilder(expression.toString()).append(" ").append(direction).toString();
			}
		default:
			return expression.toString();
		}
		
	}

	@Override
	public boolean isLeaf()      {return true;}

	@Override
	public Class<?> getType() {
		return getClass();
	}

	@Override
	public Class<?> getArrayType() {
		return null;
	}

	@Override
	public DBType getDBType() {
		return null;
	}

	
	public OrderBy unBoundVariables() {
		OrderBy o = copy();
		o.expression = o.expression.unBoundVariables();
		return o;
	}

	public OrderBy parenthesize() {
		OrderBy o = copy();
		o.expression = o.expression.parenthesize();
		return o;
	}
	
	@Override
	public OrderBy alias(String alias) {
		return super.alias(alias);
	}

	@Override
	public OrderBy qualify(String qualifier) {
		return super.qualify(qualifier);
	}

	@Override
	public OrderBy copy() {
		return new OrderBy(this);
	};
	
	
	
}
