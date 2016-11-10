package org.jddp.persistence.pgsql;


import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jddp.exception.JDDPException;
import org.jddp.expression.BooleanExpression;
import org.jddp.expression.BooleanFieldExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.NumericFieldExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.ObjectFieldExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.StringFieldExpression;
import org.jddp.expression.UUIDExpression;
import org.jddp.expression.UUIDFieldExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.expression.ZonedDateTimeExpression;
import org.jddp.expression.ZonedDateTimeFieldExpression;
import org.jddp.expression.pgsql.Obj;
import org.jddp.expression.pgsql.ObjNull;
import org.jddp.expression.pgsql.Var;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.pgsql.entity.EntityMeta;
import org.jddp.persistence.pgsql.util.NamedParameter;
import org.jddp.persistence.pgsql.util.TypeUtil;
import org.jddp.persistence.sql.update.UpdateConditional;
import org.jddp.persistence.sql.update.UpdateConnected;
import org.jddp.persistence.sql.update.UpdateDetached;
import org.jddp.persistence.sql.update.UpdateStatement;
import org.jddp.persistence.util.DBType;


public class Update<E>  implements UpdateStatement,  UpdateConditional, UpdateConnected {
	
	private final EntityMeta<E> meta;
	
	private Connection connection;
	private BooleanExpression<?> whereCondition;
	private List<FieldExpression<?>> fields = new ArrayList<>();
	private List<Expression<?>> values = new ArrayList<>();
	private String updateSQL;
	private Set<String> getters = new HashSet<>(); 
		
	public Update(EntityClass<E> entityClass) {
		meta = new EntityMeta<E>(entityClass);
		getters.addAll(Arrays.asList(entityClass.getPrimaryKey().getAccessor()));
		for (FieldExpression<?> idx : entityClass.getIndeces()) {
			String getter = reconstructGetter(idx.getAccessor());
			getters.add(getter);
		}
		
	}
	
	public Update(Update<E> ds) {
		meta = new EntityMeta<E>(ds.meta.entityClass);
		fields.addAll(ds.fields);
		values.addAll(ds.values);
		connection = ds.connection;
		whereCondition = ds.whereCondition;
		updateSQL = ds.updateSQL;
		getters.addAll(ds.getters);
		
	}
	
	@Override
	public UpdateStatement unset(FieldExpression<?> field) {
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (isArray(field)) {
			us.values.add(new Obj(Array.newInstance(field.getType(), 0), DBType.JSONB));
		} else {
			us.values.add(ObjNull.OBJECT_NULL);
		}	
		
		return us;
	}
	
	@Override
	public UpdateStatement set(StringFieldExpression field, String value) {
		
		if (value == null) {
			unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field))  {
			throw new JDDPException(field + " : " + value + " Cannot assign a string to an array");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(new Var(value));
		} else {
			us.values.add(new Obj((Object) value, DBType.JSONB));
		}
		return us;
	}

	@Override
	public UpdateStatement set(StringFieldExpression field, StringExpression<?> value) {
		
		if (value == null || value.isNil()) {
			return unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field) != isArray(value))  {
			throw new JDDPException(field + " : " + value + " Cannot assign an array to a non-array or vice versa");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(value);
		} else {
			us.values.add(value.castAsObject());
		}
		return us;	
	}

	
	@Override
	public UpdateStatement set(UUIDFieldExpression field, UUID value) {
		
		if (value == null) {
			unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field))  {
			throw new JDDPException(field + " : " + value + " Cannot assign a uuid to an array");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(new Var(value.toString()));
		} else {
			us.values.add(new Obj((Object) value.toString(), DBType.JSONB));
		}
		return us;
	}

	@Override
	public UpdateStatement set(UUIDFieldExpression field, UUIDExpression<?> value) {
		
		if (value == null || value.isNil()) {
			return unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field) != isArray(value))  {
			throw new JDDPException(field + " : " + value + " Cannot assign an array to a non-array or vice versa");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(value);
		} else {
			us.values.add(value.castAsString().castAsObject());
		}
		return us;	
	}
	
	
	
	@Override
	public UpdateStatement set(ZonedDateTimeFieldExpression field, ZonedDateTime value) {
		
		if (value == null) {
			unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field))  {
			throw new JDDPException(field + " : " + value + " Cannot assign a uuid to an array");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(new Var(OffsetDateTime.from(value).toString()));
		} else {
			us.values.add(new Obj((Object) OffsetDateTime.from(value).toString(), DBType.JSONB));
		}
		return us;
	}
	
	@Override
	public UpdateStatement set(ZonedDateTimeFieldExpression field, OffsetDateTime value) {
		
		if (value == null) {
			unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field))  {
			throw new JDDPException(field + " : " + value + " Cannot assign a uuid to an array");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(new Var(value.toString()));
		} else {
			us.values.add(new Obj((Object) value.toString(), DBType.JSONB));
		}
		return us;
	}

	@Override
	public UpdateStatement set(ZonedDateTimeFieldExpression field, ZonedDateTimeExpression<?> value) {
		
		if (value == null || value.isNil()) {
			return unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field) != isArray(value))  {
			throw new JDDPException(field + " : " + value + " Cannot assign an array to a non-array or vice versa");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(value);
		} else {
			us.values.add(value.castAsString().castAsObject());
		}
		return us;	
	}
	
	
	@Override
	public UpdateStatement set(NumericFieldExpression field, Number value) {
		if (value == null) {
			return unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field))  {
			throw new JDDPException(field + " : " + value + " Cannot assign a numeric to an array");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(new Var(value));
		} else {
			us.values.add(new Obj((Object) value, DBType.JSONB));
		}
		return us;
	}

	@Override
	public UpdateStatement set(NumericFieldExpression field, NumericExpression<?> value) {
		if (value == null || value.isNil()) {
			return unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field) != isArray(value))  {
			throw new JDDPException(field + " : " + value + " Cannot assign an array to a non-array or vice versa");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(value);
		} else {
			us.values.add(value.castAsString().castAsObject());
		}
		return us;
	}

	@Override
	public UpdateStatement set(BooleanFieldExpression field, Boolean value) {
		if (value == null) {
			return unset(field);
		}
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field))  {
			throw new JDDPException(field + " : " + value + " Cannot assign a boolean to an array");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(new Var(value));
		} else {
			us.values.add(new Obj((Object) value, DBType.JSONB));
		}
		return us;
	}

	@Override
	public UpdateStatement set(BooleanFieldExpression field, BooleanExpression<?> value) {
		if (value == null || value.isNil()) {
			return unset(field);
		}
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if (isArray(field) != isArray(value))  {
			throw new JDDPException(field + " : " + value + " Cannot assign an array to a non-array or vice versa");
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		if (field.getPrefix() == null) {
			us.values.add(value);
		} else {
			us.values.add(value.castAsString().castAsObject());
		}
		return us;
	}
	
	@Override
	public UpdateStatement set(ObjectFieldExpression field, ObjectExpression<?> value) {
		if (value == null || value.isNil()) {
			return unset(field);
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		if ((field.isJSONObject() != value.isJSONObject()) || field.isString() != value.isString() || field.isBoolean() != value.isBoolean() || field.isNumeric() != value.isNumeric()) {
			throw new JDDPException(field + " is not type compatible with value expression type " + value );
		}
		
		
		if (isArray(field) != isArray(value))  {
			//allow primitive arrays
			if (!(isArray(field) && value.isPrimitiveArray())) {
				throw new JDDPException(field + " : " + value + " Cannot assign an array to a non-array or vice versa");
			}	
		}
		
		Class<?> valueType = value.getType();

		if (isArray(field) && value.isPrimitiveArray()) {
			valueType = valueType.getComponentType();
		}
		
		if (!TypeUtil.isTypeCompatible(field.getType(), valueType)) {
			throw new JDDPException(field.getType() + " is not type compatible with " + valueType);
		}
			
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		us.values.add(value);
		return us;
	}
	
	@Override
	public UpdateStatement set(ObjectFieldExpression field, Object value) {
		if (value == null) {
			return unset(field);
		}
		
		if (value instanceof Expression) {
			if (value instanceof ObjectExpression) {
				return set(field, (ObjectExpression<?>) value);
			}
			throw new JDDPException(field + " is not type compatible with value expression type " + value );
		}
		
		disallowPrimaryKeyOrIndexUpdate(field);
		
		boolean isArray = (value instanceof Collection);
		
		if (isArray(field) != isArray)  {
			//allow primitive array
			if (!(isArray(field) && value.getClass().isArray())) {
				throw new JDDPException(field + " : " + value + " Cannot assign an array to a non-array or vice versa");
			}	
		}
		
		Class<?> valueType;
		
		if (isArray) {
			valueType = TypeUtil.getElementType(value, field.getType());
		} else if (isArray(field) && value.getClass().isArray()) {
			valueType = value.getClass().getComponentType();
		} else {
			valueType = value.getClass();
		}
		
		if (!TypeUtil.isTypeCompatible(field.getType(), valueType)) {
			throw new JDDPException(field.getType() + " is not type compatible with " + valueType);
		}
		
		Update<E> us = new Update<>(this);
		us.fields.add(field);
		us.values.add(new Obj(value, field.getDBType()));
		return us;
	}
	
	@Override
	public Long execute(Connection connection)  {
		PreparedStatement ps = null;
		
		try {
			NamedParameter np = new NamedParameter(connection, updateSQL);
			setParameters(np);
			ps = np.getPreparedStatement();
			return new Long(ps.executeUpdate());
		} catch (SQLException e) {
			throw new JDDPException("\n" + updateSQL, e);
		} finally {
			NamedParameter.closeQuietly(ps);
		}
			
	}

	@Override
	public Long execute() {
		return execute(connection);
	}
	
	@Override
	public UpdateDetached create() {
		Update<E> us = new Update<>(this);
		us.updateSQL = createUpdateSQL(false);
		return us;
	}
	
	@Override
	public UpdateConnected create(Connection connection) {
		Update<E> us = new Update<>(this);
		us.updateSQL = createUpdateSQL(false);
		us.connection = connection;
		return us;
	}

	@Override
	public UpdateConditional where(BooleanExpression<?> whereCondition) {
		Update<E> us = new Update<>(this);
		us.whereCondition = whereCondition;
		return us;
	}
	
	

	@Override
	public String getSQL(boolean varAsLiterals) {
		if (varAsLiterals) {
			return createUpdateSQL(true);
		}
		return updateSQL;
	}

	

	@Override
	public String getSQL() {
		return updateSQL;
	}

	
	
	private String createUpdateSQL(boolean varAsLiteral) {
		StringBuilder sql = new StringBuilder("UPDATE ").append(meta.tableName).append(" set ");
		int i = 0;
		
		
		ObjectExpression<?> currentJSON = null;
		
		int jsonIdx = 0;
		for (;i < fields.size(); i++) {
			
			if (i - jsonIdx > 0) {
				sql.append(", ");
			}
			
			FieldExpression<?> f = fields.get(i);
			
			
			if (f.getPrefix() != null) {
				
				if (currentJSON == null) {
					currentJSON = meta.entityClass.getJSONField();
				}
				
				Expression<?> v = values.get(i);
				
				if (varAsLiteral) {
					v = v.unBoundVariables();
				}
				
				if (!v.isNil()) {
					currentJSON =   currentJSON.set(f, (ObjectExpression<?>) v);
				}  else {
					currentJSON = currentJSON.unset(f);
				}
				jsonIdx++;
			} else {
				sql.append(f.getFieldName()).append(" = ");
				Expression<?> v = values.get(i);
				if (varAsLiteral) {
					sql.append(v.unBoundVariables().toString());
				} else {
					sql.append(v);
				}
			}
		}
		
		if (currentJSON != null) {
			if (i - jsonIdx > 0) {
				sql.append(", ");
			}
			String fieldName = meta.entityClass.getJSONField().getFieldName();
			sql.append(fieldName).append(" = ").append(currentJSON);
		}
		
		if (whereCondition != null) {
			Set<String> joins = new LinkedHashSet<>();
			
			StringBuilder subsql = new StringBuilder("SELECT ").append(meta.primaryKey.getFieldName()).append(" FROM ").append(meta.tableName);
			
			
			for (FieldExpression<?> ff : whereCondition.getFields()) {
				joins.addAll(ff.getRequiredJoins());
			}
			for (String j :  joins) {
				subsql.append("\n        ").append(j);
			}
			if (varAsLiteral) {
				subsql.append("\n    WHERE\n        ").append(whereCondition.unBoundVariables());
			} else {
				subsql.append("\n    WHERE\n        ").append( whereCondition);
			}
			sql.append("\nWHERE ").append(meta.primaryKey.getFieldName()).append(" IN ").append("(\n    ").append(subsql).append("\n)");
		}
		
		return sql.toString();
	}

	
	
	private void setParameters(NamedParameter np) throws SQLException {
		
		
		for (FieldExpression<?> f : fields) {
			for (VariableExpression val : f.getBoundVariables()) {
				np.setValue(val);
			}
		}

		for (Expression<?> v : values) {
			for (VariableExpression val : v.getBoundVariables()) {
				np.setValue(val);
			}
		}
		
		if (whereCondition != null) {
			for (VariableExpression val : whereCondition.getBoundVariables()) {
				np.setValue(val);
			}
		}
		
	}
	
	private boolean isArray(Expression<?> value) {
		if (value instanceof FieldExpression) {
			return isArray((FieldExpression<?>) value);
		}
		
		return value.isArray();
	}
	
	private boolean isArray(FieldExpression<?> value) {
		return value.isArray() && value.getPosition() == null;
	}
	
	
	//Primary key and Indeces are composed automatically when updating the jsonb entity.
	//To make sure that the index/primary key field is in sync with the jasonb fields 
	//We prevent any changes in jsonb that is a part of the composed key or index.
	private void disallowPrimaryKeyOrIndexUpdate(FieldExpression<?> field) {
		
		if (field.isPrimaryKey() || field.isIndex()) {
			throw new JDDPException(field + " cannot update a primary/index key");
		}
		
		String getter = reconstructGetter(field.getAccessor());
		
		
		if (getters.contains(getter)) {
			throw new JDDPException(field + " cannot update a jsonb field on its own that is part of a primary/index key, use update(" + meta.entityClass.getEntityName() + ") to update the whole jsonb entity");
		}
	}
	
		
	private String reconstructGetter(String[] accessor) {
		String s = "";
		
		for (String c : accessor) {
			if (s.isEmpty()) {
				s = c;
			} else {
				s += "." + c;
			}
		}
		return s;
	}
}
