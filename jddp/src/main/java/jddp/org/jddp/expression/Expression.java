package org.jddp.expression;

import java.util.Set;

import org.jddp.persistence.util.DBType;



/**
 * Interface to represent an sql expression
 * 
 * @author Rodel T. Viado
 *
 * @param <E> - type of this Expression
 */
public interface Expression<E extends Expression<?>> {

	public final static	int STRING          = 0b00000001;
	public final static	int BOOLEAN         = 0b00000010;
	public final static	int NUMERIC         = 0b00000100;
	public final static	int JSONOBJECT      = 0b00001000;
	public final static	int ARRAY           = 0b00010000;
	public final static int NULL            = 0b00100000;
	public final static int PRIMITVE_ARRAY  = 0b01000000;

	public boolean isBoolean();
	public boolean isNumeric(); 
	public boolean isString();
	public boolean isArray();
	public boolean isJSONObject();
	public boolean isNil();
	public boolean isPrimitiveArray();
	public int getModifier();
	public DBType getDBType();
	
	
	public Class<?> getType();
	public Class<?> getArrayType();
	
	public BooleanExpression<?> castAsBoolean();
	public StringExpression<?> castAsString();
	public ObjectExpression<?> castAsObject();
	public NumericExpression<?> castAsNumeric();
	public <T extends Expression<?>> T castInto(DBType type);
	
	public NumericExpression<?> count();
	
	
	/**
	 * parenthesize this Expression
	 * @return an equivalent Expression that is parenthesized
	 */
	public E parenthesize();
	
	/**
	 * An Expression is a leaf if it contains only one Expression<br/>
	 * A parenthesized Expression is a leaf<br/>
	 * Everything else is not a leaf<br/>
	 * @return a boolean value 
	 */
	public boolean isLeaf();
	
	/**
	 * Attach an alias to this Expression
	 * @param alias the alias to attach
	 * @return an aliased Expression
	 */
	public E alias(String alias);
	
	/**
	 * Qualify the Expression
	 * @param qualifier the qualifier to qualify this Expression
	 * @return a qualified Expression
	 */
	public E qualify(String qualifier);
	
	/**
	 * unBound all variables bounded to this Expression
	 * by replacing  all variable place holder with the variable's actual value in literal form.
	 * The final result is an Equivalent Expression to this Expression but without any bounded variables.
	 * 
	 * @return an Expression with all variable place holder replaced with actual variable's literal value 
	 */
	public E unBoundVariables();
	
	
	/**
	 * @return a copy of this Expression
	 */
	public E copy();
	
	/**
	 * 
	 * @return all variables bounded to this expression
	 */
	public Set<VariableExpression> getBoundVariables();
	
	
	/**
	 * 
	 * @return
	 */
	public Set<FieldExpression<?>> getFields();
	
	/**
	 * 
	 * @return the alias if this is an aliased Expression null otherwise
	 */
	public String getAlias();
	
	/**
	 * 
	 * @return the qualifier if this is a qualified Expression
	 */
	public String getQualfier();
	
}
