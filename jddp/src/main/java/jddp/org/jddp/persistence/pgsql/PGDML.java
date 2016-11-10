package org.jddp.persistence.pgsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jddp.exception.JDDPException;
import org.jddp.expression.BooleanFieldExpression;
import org.jddp.expression.Expression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.NumericFieldExpression;
import org.jddp.expression.ObjectFieldExpression;
import org.jddp.expression.StringFieldExpression;
import org.jddp.expression.UUIDFieldExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.expression.ZonedDateTimeFieldExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.pgsql.entity.EntityMeta;
import org.jddp.persistence.pgsql.util.NamedParameter;
import org.jddp.persistence.sql.DML;
import org.jddp.persistence.sql.ResultSet;
import org.jddp.persistence.sql.delete.DeleteStatement;
import org.jddp.persistence.sql.select.SelectStatement;
import org.jddp.persistence.sql.update.UpdateStatement;
import org.jddp.util.json.JSONBuilder;

import com.owlike.genson.Genson;

public class PGDML<E> implements DML<E> {

	final EntityMeta<E> meta;
	
	public final static Genson JSON = JSONBuilder.JSON;
	
	private final String upsertSQL;
	private final String insertSQL;
	private final String deleteSQL;
	private final String batchDeleteSQL;
	private final String updateSQL;
	private final String retrieveSQL;
	private final String batchRetrieveSQL;
	private final String existsSQL;
	
	
	public PGDML(EntityClass<E> entityClass) {
		meta = new EntityMeta<E>(entityClass);
		
		insertSQL = initInsertSQL();
		updateSQL = initUpdateSQL();
		upsertSQL = initUpsertSQL();
	
		retrieveSQL = "SELECT " + meta.entityField + " FROM " + meta.tableName + " WHERE " + meta.primaryKey + " =  :" + meta.primaryKey.getFieldName();
		deleteSQL = "DELETE FROM " + meta.tableName + " WHERE " + meta.primaryKey + " =  :" + meta.primaryKey.getFieldName();
		existsSQL = "SELECT EXISTS(SELECT 1 FROM " +  meta.tableName + " WHERE " + meta.primaryKey + " =  :" + meta.primaryKey.getFieldName() + ")";
		
		batchDeleteSQL = "DELETE FROM " + meta.tableName + " WHERE ";
		batchRetrieveSQL = "SELECT " + meta.entityField + " FROM " + meta.tableName + " WHERE ";
		
	}
	
	public SelectStatement<List<E>> select() {
		return new Select<E, List<E>>(meta.entityClass);
	}

	public SelectStatement<ResultSet> select(Expression<?>... fields) {
		for (Expression<?> field : fields) {
			if (field.isArray() && "()".equals(field.toString())) {
				throw new JDDPException("Empty Array is not allowed in Select");
			}
		}	
		return new Select<E, ResultSet>(meta.entityClass, fields);
	}

	public DeleteStatement delete() {
		return new Delete<E>(meta.entityClass);
	}
	
	public UpdateStatement update() {
		return new Update<E>(meta.entityClass);
	}

	
	public int upsert(Connection connection, E entity) {
		Object pkeyValue = meta.entityClass.getPrimaryKeyValue(entity);
		PreparedStatement ps = null;
		
		try {
			NamedParameter np = new NamedParameter(connection, upsertSQL);
			
			np.setFieldValue(meta.primaryKey, pkeyValue);
			np.setFieldValue(meta.entityField, entity);
			
			for (FieldExpression<?> f : meta.indeces) {
				Object value = meta.entityClass.getIndexValue(f, entity, f.getType());
				np.setFieldValue(f, value);
			}
			
			ps = np.getPreparedStatement();
			return ps.executeUpdate();
			
		} catch (SQLException e) {
			throw new JDDPException(e);
		} finally {
			NamedParameter.closeQuietly(ps);
		}
	}
	
	public int insert(Connection connection, E entity) {
		Object pkeyValue = meta.entityClass.getPrimaryKeyValue(entity);
		PreparedStatement ps = null;
		
		try {
			NamedParameter np = new NamedParameter(connection, insertSQL);
		
			np.setFieldValue(meta.primaryKey, pkeyValue);
			np.setFieldValue(meta.entityField, entity);
	
			for (FieldExpression<?> f : meta.indeces) {
				Object value = meta.entityClass.getIndexValue(f, entity, f.getType());
				np.setFieldValue(f, value);
			}
			
			ps = np.getPreparedStatement();
			return ps.executeUpdate();
			
		} catch (SQLException e) {
			throw new JDDPException(e);
		} finally {
			NamedParameter.closeQuietly(ps);
		}
	}

	public int update(Connection connection, E entity) {
		Object pkeyValue = meta.entityClass.getPrimaryKeyValue(entity);
		PreparedStatement ps = null;
		
		try {
			NamedParameter np = new NamedParameter(connection, updateSQL);
			
			np.setFieldValue(meta.primaryKey, pkeyValue);
			np.setFieldValue(meta.entityField, entity);
			
			for (FieldExpression<?> f : meta.indeces) {
				Object value = meta.entityClass.getIndexValue(f, entity, f.getType());
				np.setFieldValue(f, value);
			}
			
			ps = np.getPreparedStatement();
			return ps.executeUpdate();
			
		} catch (SQLException e) {
			throw new JDDPException(e);
		} finally {
			NamedParameter.closeQuietly(ps);
		}
	}
	
	public int delete(Connection connection, E obj) {
		Object pkeyValue = meta.entityClass.getPrimaryKeyValue(obj);
		
		PreparedStatement ps = null;
		try {
			
			NamedParameter np = new NamedParameter(connection, deleteSQL);
			np.setFieldValue(meta.primaryKey, pkeyValue);
			
			ps = np.getPreparedStatement();
			return ps.executeUpdate();
			
		} catch (SQLException ex) {
			throw new JDDPException(ex);
		} finally {
			NamedParameter.closeQuietly(ps);
		}
	}
	
	public int delete(Connection connection, List<E> objs) {
		
		List<Object> pkeyValues = new ArrayList<>();
		for (E obj : objs) {
			pkeyValues.add(meta.entityClass.getPrimaryKeyValue(obj));
		}
		
		Expression<?> e;
		
		if (meta.primaryKey instanceof StringFieldExpression) {
			e = ((StringFieldExpression)meta.primaryKey).in(pkeyValues);
		} else if (meta.primaryKey instanceof BooleanFieldExpression) {
			e = ((BooleanFieldExpression)meta.primaryKey).in(pkeyValues);
		} else if (meta.primaryKey instanceof NumericFieldExpression) {
			e = ((BooleanFieldExpression)meta.primaryKey).in(pkeyValues);
		} else if (meta.primaryKey instanceof ObjectFieldExpression) {
			e = ((ObjectFieldExpression)meta.primaryKey).in(pkeyValues);
		} else {
			throw new IllegalArgumentException("Invalid field type -> " + meta.primaryKey.getClass());
		}
		
		PreparedStatement ps = null;
		
		try {
			
			NamedParameter np = new NamedParameter(connection, new StringBuilder(batchDeleteSQL).append(e).toString());
			
			for (VariableExpression v : e.getBoundVariables()) {
				np.setValue(v);
			}
			ps = np.getPreparedStatement();
			return ps.executeUpdate();
			
			
		} catch (Exception e1) {
			throw new JDDPException(e1);
		} finally {
			NamedParameter.closeQuietly(ps);
		}
		
	}

	public E retrieveByKey(Connection connection, Object pkeyValue) {
		java.sql.ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			NamedParameter np = new NamedParameter(connection, retrieveSQL);
			
			np.setFieldValue(meta.primaryKey, pkeyValue);
			
			ps = np.getPreparedStatement();
			rs = ps.executeQuery();
			while (rs.next()) {
				String json = rs.getString(1);
				if (json != null) {
					return JSON.deserialize(json, meta.entityClass.getClazz());
				}
			}
			return null;
		} catch (Exception e1) {
			throw new JDDPException(e1);
		} finally {
			NamedParameter.closeQuietly(rs);
			NamedParameter.closeQuietly(ps);
		}
	}
	
		
	public List<E> retrieve(Connection connection, List<?> pkeys) {
		List<E> l = new ArrayList<E>();
		
		Expression<?> e;
		
		if (meta.primaryKey instanceof StringFieldExpression) {
			e = ((StringFieldExpression)meta.primaryKey).in(pkeys);
		} else if (meta.primaryKey instanceof BooleanFieldExpression) {
			e = ((BooleanFieldExpression)meta.primaryKey).in(pkeys);
		} else if (meta.primaryKey instanceof NumericFieldExpression) {
			e = ((NumericFieldExpression)meta.primaryKey).in(pkeys);
		} else if (meta.primaryKey instanceof ObjectFieldExpression) {
			e = ((ObjectFieldExpression)meta.primaryKey).in(pkeys);
		} else if (meta.primaryKey instanceof UUIDFieldExpression) {
			e = ((UUIDFieldExpression)meta.primaryKey).in(pkeys);
		}  else  if (meta.primaryKey instanceof ZonedDateTimeFieldExpression) {
			e = ((ZonedDateTimeFieldExpression)meta.primaryKey).in(pkeys);
		}  
		
		else {
			throw new IllegalArgumentException("Invalid field type -> " + meta.primaryKey.getClass());
		}
		
		java.sql.ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			NamedParameter np = new NamedParameter(connection, new StringBuilder(batchRetrieveSQL).append(e).toString());
			
			for (VariableExpression v : e.getBoundVariables()) {
				np.setValue(v);
			}
			ps = np.getPreparedStatement();
			rs = ps.executeQuery();
			while (rs.next()) {
				String json = rs.getString(1);
				if (json != null) {
					l.add(JSON.deserialize(json, meta.entityClass.getClazz()));
				}
			}
			return l;
		} catch (Exception e1) {
			throw new JDDPException(e1);
		} finally {
			NamedParameter.closeQuietly(rs);
			NamedParameter.closeQuietly(ps);
		}
		
	}
	
	public boolean isExistsByKey(Connection connection, Object pkeyValue) {
		java.sql.ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			NamedParameter np = new NamedParameter(connection, existsSQL);
			np.setFieldValue(meta.primaryKey, pkeyValue);
			ps = np.getPreparedStatement();
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getBoolean(1);
			}
			return false;
		} catch (Exception e) {
			throw new JDDPException(e);
		} finally {
			NamedParameter.closeQuietly(rs);
			NamedParameter.closeQuietly(ps);
		}
	}
	

	public Object getPrimaryKeyValue(E obj) {
		return meta.entityClass.getPrimaryKeyValue(obj);
	}
	
	
	public List<Object> getPrimaryKeyValues(List<E> objs) {
		List<Object> keys = new ArrayList<>();
		for(E obj : objs) {
			keys.add(meta.entityClass.getPrimaryKeyValue(obj));
		}
		return keys;
	}
	
	public  <PKTYPE> List<PKTYPE> getPrimaryKeyValues(List<E> objs, Class<PKTYPE> type) {
		List<PKTYPE> keys = new ArrayList<>();
		for(E obj : objs) {
			keys.add(meta.entityClass.getPrimaryKeyValue(obj));
		}
		return keys;
	}
	
//-------------- PRIVATE 	
	
	private String initInsertSQL() {
		
		
		String columns = "(" + meta.primaryKey.getFieldName();
		String values = "(";
		
		if (meta.primaryKey.isJSONObject()) {
			values +=  "cast(:" + meta.primaryKey.getFieldName() + " as " + meta.primaryKey.getDBType()  + ")";
		} else  {
			values +=  ":" + meta.primaryKey.getFieldName();
		} 
		
		
		for (FieldExpression<?> f : meta.indeces) {
			columns +=   ", "   + f.getFieldName();
			if (f.isJSONObject()) {
				values  +=   ", cast(:"  + f.getFieldName() + " as " + f.getDBType()  + ")";
			} else {
				values  +=   ", :"  + f.getFieldName();
			}	
		}
		columns += ", " + meta.entityField.getFieldName() + ")";
		values  += ", cast(:" + meta.entityField.getFieldName() +" as jsonb))";
			
		return "INSERT INTO " + meta.tableName + " " + columns + " VALUES " + values; 
	}
	
	private String initUpsertSQL() {
		return initInsertSQL() + " ON CONFLICT (" + meta.primaryKey.getFieldName() + ") DO " + initUpdateOnConflictSQL();
	}
	
	private String initUpdateSQL() {
		
		String updateSQL = "UPDATE " + meta.tableName + " SET  "; 
		for (FieldExpression<?> f : meta.indeces) {
			if (f.isJSONObject()) {
				updateSQL +=  f.getFieldName() + " = cast(:" + f.getFieldName() + " as " + f.getDBType() + "), ";
			} else {
				updateSQL +=  f.getFieldName() + " = :" + f.getFieldName() + ", ";
			}	
		}
		
		
		updateSQL +=  meta.entityField.getFieldName() + " = cast(:" + meta.entityField.getFieldName() +" as jsonb) where " + meta.primaryKey;
		
		if (meta.primaryKey.isJSONObject()) {
			updateSQL += " = cast(:" + meta.primaryKey.getFieldName() + " as " + meta.primaryKey.getDBType() + ")";
		} else {
			updateSQL += " = :" + meta.primaryKey.getFieldName();	
		}
			
		return updateSQL;
	}
	
	private String initUpdateOnConflictSQL() {
		
		String updateSQL = "UPDATE SET "; 
		for (FieldExpression<?> f : meta.indeces) {
			if (f.isJSONObject()) {
				updateSQL +=  f.getFieldName() + " = cast(" + f.getFieldName() + " as jsonb), ";
			} else {
				updateSQL +=  f.getFieldName() + " = :" + f.getFieldName() + ", ";
			}
		}
		
		updateSQL +=  meta.entityField.getFieldName() + " = cast(:" + meta.entityField.getFieldName() + " as jsonb) where " + meta.tableName + "." + meta.primaryKey;
		
		if (meta.primaryKey.isJSONObject()) {
			updateSQL += " = cast(:" + meta.primaryKey.getFieldName() + " as jsonb)";
		} else {
			updateSQL += " = :" + meta.primaryKey.getFieldName();	
		}
		
		
		return updateSQL;
	}
}
