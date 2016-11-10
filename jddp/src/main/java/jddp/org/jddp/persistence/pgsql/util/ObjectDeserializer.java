package org.jddp.persistence.pgsql.util;

import java.util.ArrayList;
import java.util.List;

import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.util.json.JSONBuilder;

import com.owlike.genson.Genson;

public class ObjectDeserializer {

	static Genson JSON = JSONBuilder.JSON;
		
	final Expression<?> e;
	
	final Class<?> type;
	final Class<?> arrayType;

		
	
	public ObjectDeserializer(Expression<?> e) {
		this.e = e;
		this.type = e.getType();
		this.arrayType = e.getArrayType();
	}

	
	public Object deserialize(Object object) {
		if (e.isNil()) {
			return object;
		}
		
		//non-arrays or a referenced element in an array
		if (!e.isArray() || elementIsReferencedInArray()) {
			return deserializeObject(object);
		} 
		
		//at this point the object is expected to be an array
		
		//field array
		if (e instanceof FieldExpression) {
			return deserializeArray(object);
		} 
		
		//Literal array
		if (object instanceof String) {
			String s = (String) object;
			s = "[" + s.substring(1, s.length() -1) + "]";
			
			return deserializeArray(s);
		}
		
		//Array of Objects (UDT array)
		Object[] objectArray = (Object[]) object;
		
		//deserialize into a list
		List<Object> result = new ArrayList<>();
		for (Object o : objectArray) {
			result.add(deserializeObject(o));
		}
		return result;
			
	}
	
	
	
	public boolean elementIsReferencedInArray() {
		return ((e instanceof FieldExpression) && ((FieldExpression<?>)e).getPosition() != null); 
	}
	
	private Object deserializeObject(Object object) {
		if (object instanceof String) {
			return JSON.deserialize((String) object, type);
		}
		return JSON.deserialize(object.toString(), type);
	}
	
	private Object deserializeArray(Object object) {
		
		Class<?> aType;
		
		aType = java.lang.reflect.Array.newInstance(type, 1).getClass();
		Object serializedArray; 
		
		if (object instanceof String) {
			serializedArray = JSON.deserialize((String) object, aType);
		} else {
			serializedArray = JSON.deserialize(object.toString(), aType);
		}
		
		
		List<Object> result = new ArrayList<>();
		for (Object o : (Object[])serializedArray) {
			result.add(o);
		}
		return result;
	
	}
	
}
