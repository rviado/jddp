package org.jddp.expression;

import java.util.Collection;

public interface ObjectExpression<E extends ObjectExpression<?>> extends  RelationalExpression<E, ObjectExpression<?>, Object> {
	
	//@>
	public BooleanExpression<?> contains(ObjectExpression<?> o);
	public BooleanExpression<?> contains(Object o);
	
	//<@
	public BooleanExpression<?> containedIn(ObjectExpression<?> o);
	public BooleanExpression<?> containedIn(Object o);
	
	//||
	public ObjectExpression<?> concat(ObjectExpression<?> o);
	public ObjectExpression<?> concat(Object o);
	
	//? key
	public BooleanExpression<?> isKeyExist(String key);
	
	//->i
	public ObjectExpression<?>  element(int i);
	//->>i
	public StringExpression<?>  elementAsText(int i);
	//->'name'
	public ObjectExpression<?>  field(String name);
	//->>'name'
	public StringExpression<?>  fieldAstext(String name);
	
	//#> '{String rootPath, String... paths}'
	public ObjectExpression<?>  fieldByPath(Collection<String> paths);
	//#>> '{String rootPath, String... paths}'
	public StringExpression<?>  fieldByPathAsText(Collection<String> paths);
	
	
	//- '{name}'
	public ObjectExpression<?>  unset(String name);
	
	//- i
	public ObjectExpression<?>  remove(int i);
		
	//#- '{rootName', names...}'
	public ObjectExpression<?>  unset(Collection<String> paths);
	public ObjectExpression<?>  unset(FieldExpression<?> f);	
	
	//jsonb_set
	public ObjectExpression<?>  set(Collection<String> paths, ObjectExpression<?> value);
	public ObjectExpression<?>  set(FieldExpression<?> f, ObjectExpression<?> value);
	
	//jsonb_array_length(jsonb)
	NumericExpression<?> length();

}
