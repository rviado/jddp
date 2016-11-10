package org.jddp.persistence.pgsql.entity;

import java.util.Collections;
import java.util.Set;

import org.jddp.expression.FieldExpression;

public class EntityMeta<E> {
	
	public final EntityClass<E> entityClass;
	
	public final String  tableName; 
	public final FieldExpression<?> entityField;
	public final FieldExpression<?> primaryKey;
	public final Set<FieldExpression<?>> indeces;
	
	
	public EntityMeta(EntityClass<E> entityClass) {
		this.entityClass = entityClass;
		this.tableName = entityClass.getEntityName().toLowerCase() + "_nosql";
		this.entityField = entityClass.getJSONField();
		this.primaryKey = entityClass.getPrimaryKey();
		this.indeces = Collections.unmodifiableSet(entityClass.getIndeces());
	}
	
}
