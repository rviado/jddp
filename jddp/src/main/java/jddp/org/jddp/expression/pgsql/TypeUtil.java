package org.jddp.expression.pgsql;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jddp.exception.JDDPException;
import org.jddp.expression.Expression;


public class TypeUtil {
	
	public final static Map<Class<?>, Class<?>> wrapper = new HashMap<Class<?>, Class<?>>();
	static {
		wrapper.put(boolean.class, Boolean.class);
		wrapper.put(byte.class, Byte.class);
		wrapper.put(short.class, Short.class);
		wrapper.put(char.class, Character.class);
		wrapper.put(int.class, Integer.class);
		wrapper.put(long.class, Long.class);
		wrapper.put(float.class, Float.class);
		wrapper.put(double.class, Double.class);
	}

	
	public static Class<?> getType(Expression<?> e) {
		if (e.isBoolean()) {
			return Boolean.class;
		}
		
		if (e.isNumeric()) {
			return BigDecimal.class;
		} 
		
		if (e.isString()) {
			return String.class;
		}  

		return Object.class;
	}
	
	public static boolean isString(Class<?> valueClass) {
		return valueClass == String.class;
	}
	
	public static boolean isBoolean(Class<?> valueClass) {
		return Boolean.class.isAssignableFrom(valueClass) || boolean.class.isAssignableFrom(valueClass);
	}
	
	public static Boolean toBoolean(Object o) {
		if (o instanceof Boolean) {
			return (Boolean) o;
		}
		if (boolean.class.isAssignableFrom(o.getClass())) {
			return (boolean) o;
		}
		if (o instanceof String) {
			String s = o.toString().trim();
			return new Boolean(s);
		}
		throw new IllegalArgumentException("Cannot convert object to boolean -> " +  o); 
	}
	
	public static boolean isCollection(Class<?> valueClass) {
		return  valueClass != null && (Collection.class.isAssignableFrom(valueClass));
	}
	
	public static List<Object> toList(Object o) {
		List<Object> result = new ArrayList<>();
		if (o.getClass().isArray()) {
			int length = Array.getLength(o);
		    for (int i = 0; i < length; i ++) {
		        result.add(Array.get(o, i));
		    }
		} else if (o instanceof Collection){
			result.addAll((Collection<?>) o); 
		} else {
			throw new IllegalArgumentException("Cannot convert argument to a List -> " + o.getClass().getSimpleName());
		}
		return result;
	}
	
	public static Class<?> getElementType(Object o, Class<?> defaultClass) {
		if (o instanceof Collection) {
			Collection<?> c = (Collection<?>) o;
			Iterator<?> i = c.iterator();
			if (i.hasNext()) {
				return c.iterator().next().getClass();
			}
			return defaultClass;
		} else if (o.getClass().isArray()) {
			return o.getClass().getComponentType();
		}
		
		throw new JDDPException((o != null ? o.getClass().getName() : "null") + " is not a collection/array");
	}
	
	
		
	public static int getElementTypeModifier(Collection<?> c) {
		if (c.isEmpty()) {
			return Expression.JSONOBJECT;
		}
		Object obj = c.iterator().next();
		if (obj instanceof Expression) {
			return ((Expression<?>)obj).getModifier();
		}
		
		if (obj == null) {
			return Expression.JSONOBJECT;
		}
		
		if (isBoolean(obj.getClass())) {
			return Expression.BOOLEAN;
		}
		
		if (isNumeric(obj.getClass())) {
			return Expression.NUMERIC;
		}
		
		if (isString(obj.getClass())) {
			return Expression.STRING;
		}
		
		return Expression.JSONOBJECT;
		
	}
	
	public static int getModifier(Class<?> type) {
	return	
	(TypeUtil.isBoolean(type) ? Expression.BOOLEAN : 0) | 
	(TypeUtil.isNumeric(type) ? Expression.NUMERIC : 0) | 
	(TypeUtil.isString(type) ? Expression.STRING : 0)   | 
	(TypeUtil.isCollection(type) ? Expression.ARRAY : 0);
	}
	
	public static boolean isNumeric(Class<?> type) {
		return Number.class.isAssignableFrom(type) 
				|| int.class.isAssignableFrom(type) 
				|| long.class.isAssignableFrom(type)
				|| float.class.isAssignableFrom(type)
				|| byte.class.isAssignableFrom(type)
				|| double.class.isAssignableFrom(type)
				|| short.class.isAssignableFrom(type);
	}

	
	public static BigDecimal toBigDecimal(Object o) {
		
		if (o instanceof BigDecimal) {
			return (BigDecimal) o;
		}
		if (o instanceof Number) {
			return new BigDecimal(((Number) o).doubleValue());
		}
		
		Class<?> type = o.getClass();
		
		if (int.class.isAssignableFrom(type)) {
			return new BigDecimal((int) o);
		}
		if (long.class.isAssignableFrom(type)) {
			return new BigDecimal((long) o);
		}
		if (byte.class.isAssignableFrom(type)) {
			return new BigDecimal((byte) o);
		}
		if (float.class.isAssignableFrom(type)) {
			return new BigDecimal((float) o);
		}
		if (double.class.isAssignableFrom(type)) {
			return new BigDecimal((double) o);
		}
		if (short.class.isAssignableFrom(type)) {
			return new BigDecimal((short) o);
		}
		
		throw new IllegalArgumentException("Invalid argument, cannot cast value into numeric -> " + o);
	}
	
		
	public static boolean isTypeCompatible(Class<?> class1, Class<?> class2) {
		if (class1.isArray() && class2.isArray()) {
			class1 = class1.getComponentType();
			class2 = class2.getComponentType();
		}
		
		if (class1.isPrimitive()) {
			class1 = wrapper.get(class1);
		}
		
		if (class2.isPrimitive()) {
			class2 = wrapper.get(class2);
		}
		
		return class1.isAssignableFrom(class2);
		
	}
}
