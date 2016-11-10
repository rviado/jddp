package org.jddp.util.bean;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;

public class Bean {
	
	static final String keywords[] = {
            "abstract",  "assert",       "boolean",    "break",      "byte",      "case",
            "catch",     "char",         "class",      "const",     "continue",
            "default",   "do",           "double",     "else",      "extends",
            "false",     "final",        "finally",    "float",     "for",
            "goto",      "if",           "implements", "import",    "instanceof",
            "int",       "interface",    "long",       "native",    "new",
            "null",      "package",      "private",    "protected", "public",
            "return",    "short",        "static",     "strictfp",  "super",
            "switch",    "synchronized", "this",       "throw",     "throws",
            "transient", "true",         "try",        "void",      "volatile",
            "while"
        };


	public static boolean isJavaKeyword(String s) {
           return (Arrays.binarySearch(keywords, s) >= 0);
	} 
	
	
	public static <T extends Annotation> T getAnnotation(Method m, Field f, Class<T> ann) {
		if (m.isAnnotationPresent(ann)) {
			return m.getAnnotation(ann) ;
		}
		
		if (f.isAnnotationPresent(ann)) {
			return f.getAnnotation(ann);
		}
		
		return null;
	}
	

	
	public static boolean hasFieldWithAnnotation(Class<?> clazz, Class<? extends Annotation> ann) throws IntrospectionException {
		for(PropertyDescriptor pd : Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors()){
			String name = pd.getName();
			
			Method getter = pd.getReadMethod();
			Method setter = pd.getWriteMethod();
			
			if (getter == null && !pd.getPropertyType().isPrimitive() ) {
				String getterMethodName = "is" + StringUtils.capitalize(name);
				getter = MethodUtils.getAccessibleMethod(clazz, getterMethodName, new Class[] {});
			}
			
			if (getter != null && setter != null) {
				if (getter.isAnnotationPresent(ann)) {
					return true;
				}
				Field f = getClassField(clazz, name);
				if (f.isAnnotationPresent(ann)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	public static Method getMethodWithAnnotation(Class<?> clazz, Class<? extends Annotation> ann) throws IntrospectionException {
		for(PropertyDescriptor pd : Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors()){
			String name = pd.getName();
			
			Method getter = pd.getReadMethod();
			Method setter = pd.getWriteMethod();
			
			if (getter == null && !pd.getPropertyType().isPrimitive() ) {
				String getterMethodName = "is" + StringUtils.capitalize(name);
				getter = MethodUtils.getAccessibleMethod(clazz, getterMethodName, new Class[] {});
			}
			
			if (getter != null && setter != null) {
				if (getter.isAnnotationPresent(ann)) {
					return getter;
				}
				Field f = getClassField(clazz, name);
				if (f.isAnnotationPresent(ann)) {
					return getter;
				}
			}
		}
		
		return null;
	}
	
	
	public static  Field getClassField(Class<?> clazz, String name) {
		if (isJavaKeyword(name)) {
			name = "_" + name;
		}
		Field field = FieldUtils.getField(clazz, name, true);
		
		if (field == null) {
			
			int i = 0;
			while (i < name.length() && field == null) {
				name = name.substring(0, i+1).toLowerCase() + name.substring(i+1);
				field = FieldUtils.getField(clazz, name, true);
				i++;
			}
		}
		
		return field;
	}
	
	public static  Class<?> getMethodReturnComponentType(Method method) {
		Class<?> returnType = method.getReturnType();
		if (Collection.class.isAssignableFrom(returnType)) {
			Type genericReturnType = method.getGenericReturnType();
			if(genericReturnType instanceof ParameterizedType){
			    ParameterizedType type = (ParameterizedType) genericReturnType;
			    Type[] typeArguments = type.getActualTypeArguments();
			    for(Type typeArgument : typeArguments){
			        returnType = (Class<?>) typeArgument;
			        break;
			    }
			}
		} else if (returnType.isArray()) {
			returnType = returnType.getComponentType();
		}
		
		return returnType;
	}
}
