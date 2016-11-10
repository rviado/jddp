package org.jddp.persistence.pgsql.entity;


import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.jddp.exception.JDDPException;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.ObjectFieldExpression;
import org.jddp.expression.pgsql.BoolField;
import org.jddp.expression.pgsql.NumField;
import org.jddp.expression.pgsql.ObjField;
import org.jddp.expression.pgsql.StrField;
import org.jddp.expression.pgsql.UUIDField;
import org.jddp.expression.pgsql.ZDTField;
import org.jddp.persistence.entity.Compositor;
import org.jddp.persistence.entity.annotation.CompositeKey;
import org.jddp.persistence.entity.annotation.Entity;
import org.jddp.persistence.entity.annotation.Indeces;
import org.jddp.persistence.entity.annotation.Index;
import org.jddp.persistence.entity.annotation.PrimaryKey;
import org.jddp.persistence.pgsql.entity.Join.TYPE;
import org.jddp.persistence.pgsql.util.NameGenerator;
import org.jddp.persistence.pgsql.util.TypeUtil;
import org.jddp.persistence.util.DBType;
import org.jddp.persistence.util.FieldUtil;
import org.jddp.util.bean.Bean;


public class EntityClass<E> {

	
	Set<FieldExpression<?>> fields = new LinkedHashSet<>();
	Map<String, Join>  joins = new LinkedHashMap<>();
	Set<String> xpaths = new LinkedHashSet<String>();
	String entityName;
	String rootElement;
	final PrimaryKey primaryKey;
	Map<FieldExpression<?>, Index> indeces = new LinkedHashMap<>();
	
	
	final FieldExpression<?> pkey;
	final Compositor<E, ?> compositor;
	final String[] compositeKeys;
	private Set<Class<?>> visited = new HashSet<>();
	private Map<String, FieldExpression<?>> xpathToField = new HashMap<>();
	
	Class<E> clazz;
	

	
	public EntityClass(Class<E> clazz, String rootElement) {
		this.clazz = clazz;
		primaryKey =  clazz.getAnnotation(PrimaryKey.class);
		
		if (primaryKey != null) {
			CompositeKey ck = primaryKey.composite();
			compositeKeys = ck.accessors();
			String cn = NameGenerator.generateCompositorClassName(clazz, ck.compositor());
			compositor = newCompositor(cn);
			
			String pkeyName = primaryKey.fieldName() == null || primaryKey.fieldName().trim().isEmpty() ? "pkey" : primaryKey.fieldName().trim();  
			DBType dbType = DBType.from(primaryKey.type());
			
			switch (dbType) {
			case TIMESTAMP:
			case TIMESTAMPTZ:
				pkey = new ZDTField(pkeyName, null,  pkeyName, null, String.class, dbType, Expression.STRING, this, compositeKeys);
				break;
			case UUID:
				pkey = new UUIDField(pkeyName, null,  pkeyName, null, String.class, dbType, Expression.STRING, this, compositeKeys);
				break;	
			case BOOLEAN:
				pkey = new BoolField(pkeyName, null,  pkeyName, null, Boolean.class, dbType, Expression.BOOLEAN, this, compositeKeys);
				break;
			case NUMERIC:
				pkey = new NumField(pkeyName, null,  pkeyName, null, BigDecimal.class, dbType, Expression.NUMERIC, this, compositeKeys);
				break;
			case TEXT:
				pkey = new StrField(pkeyName, null,  pkeyName, null, String.class, dbType, Expression.STRING, this, compositeKeys);
				break;
			case JSON:
			case JSONB:
			default:
				pkey = new ObjField(pkeyName, null,  pkeyName, null, Object.class, dbType, Expression.JSONOBJECT, this, compositeKeys);
				break;
			}
			
		} else {
			throw new JDDPException("Entity " + clazz.getName() + " must have a primary key");
		}
		
		Indeces idx = clazz.getAnnotation(Indeces.class);
		if (idx != null) {
			for (Index i : idx.index()) {
				FieldExpression<?> ef;
				DBType dbType = DBType.from(i.type());
				switch (dbType) {
				case TIMESTAMP:
				case TIMESTAMPTZ:
					ef = new ZDTField(i.fieldName(), null, i.fieldName(), null, String.class, dbType, Expression.STRING, this, i.accessor().split("\\."));
					break;
				case UUID:
					ef = new UUIDField(i.fieldName(), null, i.fieldName(), null, String.class, dbType, Expression.STRING, this, i.accessor().split("\\."));
					break;	
				case BOOLEAN:
					ef = new BoolField(i.fieldName(), null, i.fieldName(), null, Boolean.class, dbType, Expression.BOOLEAN, this, i.accessor().split("\\."));
					break;
				case NUMERIC:
					ef = new NumField(i.fieldName(), null, i.fieldName(), null, BigDecimal.class, dbType, Expression.NUMERIC, this, i.accessor().split("\\."));
					break;
				case TEXT:
					ef = new StrField(i.fieldName(), null, i.fieldName(), null, String.class, dbType, Expression.STRING, this, i.accessor().split("\\."));
					break;
				case JSON:
				case JSONB:
				default:
					ef = new ObjField(i.fieldName(), null, i.fieldName(), null, Object.class, dbType, Expression.JSONOBJECT, this, i.accessor().split("\\."));
					break;
				}
				indeces.put(ef, i);
				fields.add(ef);
			}
		}
		
		Entity ann = clazz.getAnnotation(Entity.class);
		if (ann != null) {
			entityName = ann.name();
		}
		if (entityName == null || entityName.isEmpty()) {
			entityName = clazz.getSimpleName();
		}
		this.rootElement = rootElement;
		
		fields.add(new ObjField("$", null , rootElement, null, clazz, DBType.JSONB,  Expression.JSONOBJECT, this, new String[]{}));
		
		try {
			parse(rootElement, clazz, null);
			indeces = Collections.unmodifiableMap(indeces);
			fields = Collections.unmodifiableSet(fields);
			for (FieldExpression<?> f: fields) {
				xpathToField.put(f.getXpath(), f);
			}
			xpaths = Collections.unmodifiableSet(xpaths);
			joins = Collections.unmodifiableMap(joins);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public  <T> T getPrimaryKeyValue(E obj) {
		return (T) compositor.getComposedKey(obj);
	}
	
	
	/**
	 * @return the rootElement
	 */
	public String getRootElement() {
		return rootElement;
	}

	/**
	 * @return the clazz
	 */
	public Class<E> getClazz() {
		return clazz;
	}

	public Collection<String> getXPaths() {
		return xpaths;
	}
	
	
	
	public Join getJoinByAlias(String alias) {
		for (String xpath : joins.keySet()) {
			Join j = joins.get(xpath);
			if (j != null && alias.equals(j.getAlias())) {
				return j;
			}
		}
		return null;
	}
	
	public Set<Join> getRequiredJoins(String alias) {
		LinkedHashSet<Join> requiredJoins = new LinkedHashSet<Join>();
		List<Join> l = new ArrayList<Join>();
		
		Join j = getJoinByAlias(alias); 
		while (j != null) {
			l.add(0,j);
			j = j.previousJoin;
		}
		requiredJoins.addAll(l);
		return requiredJoins;
	}
		
	public FieldExpression<?> getPrimaryKey() {
		return pkey;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends FieldExpression<?>> T getPrimaryKey(Class<T> pkType) {
		return (T) pkey;
	}
	
	public boolean isPrimaryKey(FieldExpression<?> f) {
		return f.equals(pkey);
	}
	
	 public Set<FieldExpression<?>> getIndeces() {
         return indeces.keySet();
     }
	 
	 public Map<FieldExpression<?>, Index> getIndexedAnnotations() {
         return indeces;
     }
	 
	 public boolean isIndex(FieldExpression<?> f) {
		return indeces.containsKey(f);
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T getIndexValue(FieldExpression<?> f, E obj, Class<T> type) {
			if (!indeces.containsKey(f)) {
				return null;
			}
			
			Object o = obj;
			for (String getter : f.getAccessor()) {
				try {
					Method m = o.getClass().getMethod(getter);
					o = m.invoke(o);
					if (o == null) {
						return null;
					}
				} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new JDDPException("Error invoking getter(" + getter + ") for index " + f.getFieldName(), e);
				} 
				
			}
			
			return (T) o;
		}
	 
	 public String getConstraint(FieldExpression<?> f) {
			if (indeces.containsKey(f)) {
				Index a = indeces.get(f);
				return a.constraint();
			}
			if (f.equals(getField("$"))) {
				return "not null";
			}
			if (f.equals(pkey)) {
				return primaryKey.constraint();
			}
			
			return "";
		}
	 
	 
	 public String getDBType(FieldExpression<?> f) {
		 if (f.equals(pkey)) {
			return primaryKey.type();
		}
		 
		if (indeces.containsKey(f)) {
			Index a = indeces.get(f);
			return a.type();
		}
		if (f.equals(getField("$"))) {
			return "jsonb";
		}
		
		if (f.isString()) {
			return "text";
		} 
		
		if (f.isBoolean()) {
			return "boolean";
		} 
		
		if (f.isNumeric()) {
			return "numeric";
		}
		
		if (f.isJSONObject()) {
			return "jsonb";
		} 
		
		return "text";
	}
	 
	public FieldExpression<?> getField(String xpath) {
		return xpathToField.get(xpath);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends FieldExpression<?>>  T getField(String xpath, Class<T> c) {
		return  (T) xpathToField.get(xpath);
	}
	
	
	public ObjectFieldExpression getJSONField() {
		return (ObjectFieldExpression) getField("$");
	}
	
	
	/**
	 * @return the fields
	 */
	public Set<FieldExpression<?>> getFields() {
		return fields;
	}
	
	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}


	
	
	private void parse(String fieldPrefix, Class<?> clazz, String accessor) throws IntrospectionException  {
		if (!isEntity(clazz)) {
			return;
		}
		
		if (visited.contains(clazz)) {
			return;
		}
		
		visited.add(clazz);
		
		boolean isComplexTypeWithSimpleContent = Bean.hasFieldWithAnnotation(clazz, XmlValue.class);
		
		
		for(PropertyDescriptor pd : Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors()){
			String propertyName = pd.getName();
					
			Method getter = pd.getReadMethod();
			Method setter = pd.getWriteMethod();
			
			if (getter == null && !pd.getPropertyType().isPrimitive() ) {
				String getterMethodName = "is" + StringUtils.capitalize(propertyName);
				getter = MethodUtils.getAccessibleMethod(clazz, getterMethodName, new Class[] {});
			}
			
			if (getter != null && setter != null) {
				
				Class<?> returnType = getter.getReturnType();
				
				boolean isCollection = Collection.class.isAssignableFrom(returnType);// || returnType.isArray();
				Class<?> returnArrayType = null;
				if (isCollection) {
					returnArrayType = returnType;
					returnType = Bean.getMethodReturnComponentType(getter);
				}
				
				boolean isXmlValue = false;
				
				Field field = Bean.getClassField(clazz, propertyName);
				
				if (isComplexTypeWithSimpleContent) {
					boolean isAttribute = Bean.getAnnotation(getter, field, XmlAttribute.class) != null;
					if (!isAttribute) {
						isXmlValue =  Bean.getAnnotation(getter, field, XmlValue.class) != null;
					}	
				}
				
				String fieldName = propertyName;
				XmlElement ann = Bean.getAnnotation(getter, field, XmlElement.class);
				if (ann != null && !"##default".equals(ann.name())) {
					fieldName = ann.name();
				}
				
				//create a join for collections
				//Join j = null;
				if (isCollection && !joins.containsKey(fieldPrefix + "/" + fieldName)) {
					String xpath = fieldPrefix + "/" + fieldName;
					String joinAlias = generateUniqueAlias(fieldName);
					String joinReference = createJSONJoinReference(fieldPrefix, fieldName);
					Join required = getRequiredJoin(fieldPrefix);
					Join join = new Join(xpath, required , TYPE.LEFT, "jsonb_array_elements(" + joinReference + ")", joinAlias, "true", joinReference);
					joins.put(xpath, join);
					
				}
				
				boolean returnTypeIsComplexTypeWithSimpleContent = isEntity(returnType) && Bean.hasFieldWithAnnotation(returnType, XmlValue.class); 
				
				
				
				
				
				//If property is a an XmlValue then skip creation of a field
				//Why?
				//Because, a complex type with simple content is represented as
				//Class ComplexTypeWithSimpleContent {
				//          @XmlValue
				//			String value;
				//          @XmlAttribute
				//          String attr1;
				//			@XmlAttribute
				//			String attr2
				//}
								
				if (!returnTypeIsComplexTypeWithSimpleContent)
				{
					String xpath;
					
					//See explanation above
					if (isXmlValue) {
						//let this field be represented by the enclosing class
						xpath = fieldPrefix;
					} else {
						xpath = fieldPrefix + "/" + fieldName;
					}
					
					xpaths.add(xpath);
					String	jsonFieldPrefix = createJSONFieldPrefixReference(fieldPrefix);
					
					String getters = accessor == null ? getter.getName() : accessor + "." + getter.getName();
					
					
					FieldExpression<?> f;
					
					
					if (isCollection) {
						f = new ObjField(xpath, jsonFieldPrefix , fieldName, returnArrayType, returnType, DBType.JSONB, Expression.JSONOBJECT | Expression.ARRAY, this, getters.split("\\."));
					} else {
						if (TypeUtil.isBoolean(returnType)) {
							f = new BoolField(xpath, jsonFieldPrefix , fieldName, returnArrayType, returnType, DBType.BOOLEAN, Expression.BOOLEAN, this, getters.split("\\."));
						} else if (TypeUtil.isNumeric(returnType)) {
							f = new NumField(xpath, jsonFieldPrefix , fieldName, returnArrayType, returnType, DBType.NUMERIC, Expression.NUMERIC, this, getters.split("\\."));
						} else if (TypeUtil.isString(returnType)) {
							f = new StrField(xpath, jsonFieldPrefix , fieldName, returnArrayType, returnType, DBType.TEXT, Expression.STRING,  this, getters.split("\\."));
						} else {
							f = new ObjField(xpath, jsonFieldPrefix , fieldName, returnArrayType, returnType, DBType.JSONB, Expression.JSONOBJECT, this, getters.split("\\."));
						}
					}
					
					fields.add(f);
				}
				
				
				if (isEntity(returnType) && !visited.contains(returnType)) {
					String getters = accessor == null ? getter.getName() : accessor + "." + getter.getName();
					
					String classXPathLastComponent = StringUtils.capitalize(propertyName);
					
					//avoid path collision
					if (classXPathLastComponent.equals(propertyName)) {
						classXPathLastComponent = propertyName + "_";
					}
					
					String xpath = fieldPrefix + "/" + classXPathLastComponent;
					
					String	jsonFieldPrefix = createJSONFieldPrefixReference(fieldPrefix);
					FieldExpression<?> f = new ObjField(xpath, jsonFieldPrefix , fieldName, returnArrayType, returnType, DBType.JSONB, Expression.JSONOBJECT | (isCollection ? Expression.ARRAY : 0) , this, getters.split("\\."));
					fields.add(f);
					xpaths.add(xpath);
					
					
					//parse the 
					parse(fieldPrefix + "/" + fieldName, returnType, getters);
					
				} 
				
				 
				
			}
			
		}
		
		visited.remove(clazz);
	}
	
	public Collection<Join> getJoins() {
		return  joins.values();
	}
	
	private boolean isEntity(Class<?> clazz) {
		return clazz.isAnnotationPresent(Entity.class);
	}

	private String generateUniqueAlias(String alias) {
		int joinIdx = 0;
		String origAlias = alias;
		while (getJoinByAlias(alias) != null) {
			alias = origAlias + (joinIdx++);
		}
		return alias;
	}
	
	private Join getRequiredJoin(String prefix) {
		Join j = null;
		String previousPrefix = prefix;
		while (!previousPrefix.isEmpty()) {
			j = joins.get(previousPrefix);
			if (j != null) {
				return j;
			}
			previousPrefix = FieldUtil.getPrefix(previousPrefix);
		}
		return null;
	}
	
	private String createJSONReference(String prefix, String name) {
		Join j = getRequiredJoin(prefix);
	
		if (j != null) {
			if (name != null && !name.isEmpty()) {
				name = "/" + name; 
			} else {
				name = "";
			}
			if (prefix.equals(j.getXpath())) {
				return FieldUtil.toJSON(j.getAlias() + name);
			} else {
				return FieldUtil.toJSON(j.getAlias() + "/" + FieldUtil.removePrefix(j.getXpath(), prefix) + name);
			}
		}
		return null;
	}
	
	
	private String  createJSONJoinReference(String prefix, String name) {
		String ref = createJSONReference(prefix, name);
		if (ref == null) {
			if (name == null || name.isEmpty()) {
				name = "";
			} else {
				name = "/" + name;
			}
			return FieldUtil.toJSON(prefix + name);
		}
		return ref;
	
	}
	
	private String  createJSONFieldPrefixReference(String prefix) {
		
		String ref = createJSONReference(prefix, null);
		if (ref == null) {
			return FieldUtil.toJSON(prefix);
		}
		return ref; 
	}
	
		

	@SuppressWarnings("unchecked")
	private Compositor<E, ?> newCompositor(String className) {
		try {
			return (Compositor<E, ?>) Class.forName(className).newInstance();
		} catch (Throwable e) {
			throw new JDDPException(e);
		}
	}
	
	
}
