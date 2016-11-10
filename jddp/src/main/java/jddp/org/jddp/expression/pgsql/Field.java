package org.jddp.expression.pgsql;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.pgsql.entity.Join;
import org.jddp.persistence.pgsql.util.TypeUtil;
import org.jddp.persistence.util.DBType;


public abstract class Field<F extends Expression<?>> extends AbstractExpression<Field<?>, F> implements FieldExpression<F> {
	final String xpath;
	final String prefix;
	final String fieldName;
	final Class<?> type;
	final Class<?> arrayType;
	
	

	final EntityClass<?> entityClass;
	final String[] accessor;
	final Set<Join> requiredJoins = new LinkedHashSet<>();
	protected FieldExpression<?> owner;
	protected final Integer i;
	protected final DBType dbType;

	
	Field(Field<?> f, FieldExpression<?> owner, Integer i, int modifier, DBType dbType) {
		this(f.xpath, f.prefix, f.fieldName, f.arrayType, f.type, dbType, modifier, f.entityClass, f.accessor, i);
		this.owner = owner;
		_toString = initPathReference();
	}
	
	Field(Field<?> f, FieldExpression<?> owner, Integer i) {
		this(f.xpath, f.prefix, f.fieldName, f.arrayType, f.type, f.dbType, f.modifier, f.entityClass, f.accessor, i);
		this.owner = owner;
		_toString = initPathReference();
	}
	
	Field(String xpath, String prefix, String fieldName, Class<?> arrayType, Class<?> type, DBType dbType, int modifier, EntityClass<?> entityClass, String[] accessor) {
		this(xpath, prefix, fieldName, arrayType, type, dbType, modifier, entityClass, accessor, null);
	}
	
	private Field(String xpath, String prefix, String fieldName, Class<?> arrayType, Class<?> type, DBType dbType, int modifier, EntityClass<?> entityClass, String[] accessor, Integer i) {
		super(modifier, dbType);
		this.xpath = xpath;
		this.entityClass = entityClass;
		this.prefix = prefix;
		this.type = type;
		this.fieldName = fieldName;
		this.arrayType = arrayType;
		if (prefix != null) {
			String[] components = prefix.split("->"); 
			requiredJoins.addAll(entityClass.getRequiredJoins(components[0]));
		} 
		this.accessor = accessor;
		
		_toString = asObjectReference();
		fields.add(this);
		this.owner = null;
		this.i =i;
		this.dbType = dbType;
	}
	
	@Override 
	public FieldExpression<?> getOwner() {
		return owner;
	}
	
	public boolean isAssociation() {
		return !requiredJoins.isEmpty();
	}
	
	public String asTextReference() {
		if (prefix != null) {
			return prefix + "->>'" + fieldName + "'";
		}
		
		if (isJSONObject()) {
			return "CAST(" + fieldName + " as text)";
		}
		return fieldName;
	}
	
	public String asObjectReference() {
		if (prefix != null) {
			return prefix + "->'" + fieldName + "'";
		}
		return fieldName;
	}
	
	
	/**
	 * @return if an array returns the element position else null
	 */
	@Override
	public Integer getPosition() {
		return i;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the xpath
	 */
	public String getXpath() {
		return xpath;
	}
	
	/**
	 * @return the type
	 */
	@Override
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * @return the arrayType
	 */
	@Override
	public Class<?> getArrayType() {
		return arrayType;
	}
	
	@Override
	public boolean isIndex() {
		return entityClass.isIndex(this);
	}
	
	public boolean isPrimaryKey() {
		return	entityClass.isPrimaryKey(this);
	}
	
	
	public String toString() {
		return _toString;
	}
	

	@Override
	public boolean equals(Object other) {
		if (! (other instanceof Field)) {
			return false;
		}
		
		return xpath.equals(((Field<?>) other).xpath);
	}

	@Override
	public int hashCode() {
		return this.xpath.hashCode();
	}

	
	@Override
	public Set<String> getRequiredJoins() {
		Set<String> result = new LinkedHashSet<>();
		for (Join j : requiredJoins) {
			result.add(j.toString());
		}
		return result;
	}


	@Override
	public String[] getAccessor() {
		return accessor;
	}

	
	@Override
	public DBType getDBType() {
		return dbType;
	}
	
	private String initPathReference() {
		String path;
		
		if (isString()  || isBoolean() || isNumeric() ) {
			path = asTextReference();
		} else {
			path = asObjectReference();
		}
		
		
		if (isArray()) {
			if (i != null) {
				if (TypeUtil.isString(type)  || TypeUtil.isBoolean(type) || TypeUtil.isNumeric(type)) {
					path = asObjectReference() + "->>" + i;
				} else {
					path = asObjectReference() + "->" + i;
				}
			} else {
				path = asObjectReference();
			}
		}
		
		FieldExpression<?> current = this;
		
		ArrayList<FieldExpression<?>> indexedArrays = new ArrayList<>();
		List<String> references = new ArrayList<>();
		
		//get indexed arrays
		while (current != null) {
			if (current.isArray() && current.getPosition() != null ) {
					indexedArrays.add(0, current);
					references.add(0, current.asObjectReference());
			}	
			current = current.getOwner();
		}

		if (!indexedArrays.isEmpty()) {
			List<Join> joins = new ArrayList<>();
			LinkedHashMap<String, String> aliasMappedToNewReference = new LinkedHashMap<>();
			
			//find the joins for the indexed arrays
			for (FieldExpression<?> o : indexedArrays) {
				for (Join j : requiredJoins) {
					String oRef = o.asObjectReference(); 
					if (oRef.equals(j.getJoinReference())) {
						aliasMappedToNewReference.put(j.getAlias(), oRef +  "->" + o.getPosition());
					} else if (!references.contains(j.getJoinReference())){
						joins.add(j);
					} 
				}
			}
			
			List<Join> adjustedJoins = null;
			LinkedHashMap<String, Join> aliasMappedToNewJoin = new  LinkedHashMap<>();
			
			if (!joins.isEmpty()) {
				Map<String, Join> prevJoins = new LinkedHashMap<>();
				adjustedJoins = new ArrayList<>();
				
				for (Join j : joins) {
					String joinRef = j.getJoinReference().split("->")[0];
					if (aliasMappedToNewReference.keySet().contains(joinRef)) {
						String ref = aliasMappedToNewReference.get(joinRef) + j.getJoinReference().substring(joinRef.length());
						Join prevJoin = null;
						String[] components = ref.split("->");
						if (components.length >= 2) {
							prevJoin = prevJoins.get(components[0]);
						}
						String newAlias = createNewAlias(j.getAlias());
						Join newJoin = new Join(j.getXpath(), prevJoin, j.getJoinType(), "jsonb_array_elements(" + ref + ")", newAlias, j.getJoinCondition(), ref);
						aliasMappedToNewJoin.put(j.getAlias(), newJoin);
						adjustedJoins.add(newJoin);
					} else {
						adjustedJoins.add(j);
					}
					prevJoins.put(j.getAlias(), j);
				}
			}

			//when an array is indexed the joins are removed
			//and replaced with the new reference
			if (!aliasMappedToNewReference.isEmpty()) {
				for (String alias : aliasMappedToNewReference.keySet()) {
					if (path.startsWith(alias + "->")) {
						path = aliasMappedToNewReference.get(alias) + path.substring(alias.length());
						removeJoin(alias);
					}
				}
			}
		
			//make sure that all new reference are aliased correctly 
			for (String alias : aliasMappedToNewJoin.keySet()) {
				if (path.startsWith(alias + "->")) {
					path = aliasMappedToNewJoin.get(alias).getAlias() + path.substring(alias.length());
				}
			}
		
			if (adjustedJoins != null) {
				requiredJoins.addAll(adjustedJoins);
			}	
		}
		
		if (!isArray()) {
			if (isBoolean()) {
				path = "CAST(" + path + " as boolean)";
			} else if (isNumeric()) {
				path = "CAST(" + path + " as numeric)";
			} 
		}
		
		return path;
		
	}
	
	private String createNewAlias(String alias) {
		String s = "";
		FieldExpression<?> current = owner;
		while (current != null) {
			if (current.getPosition() != null) {
				s += current.getPosition();
			} else {
				s += current.getFieldName().substring(0, 1).toUpperCase();
			}
			current = current.getOwner();
		}
		
		return alias + s;
	}
	
	
	private void removeJoin(String alias) {
		Join oldJoin = null;
		for (Join j : requiredJoins) {
			if (alias.equals(j.getAlias())) {
				oldJoin = j;
				break;
			}
		}
		if (oldJoin != null) {
			requiredJoins.remove(oldJoin);
		}	
	}
}
