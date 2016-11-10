package org.jddp.persistence.pgsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jddp.exception.JDDPException;
import org.jddp.expression.BooleanExpression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.pgsql.entity.EntityMeta;
import org.jddp.persistence.pgsql.util.NamedParameter;
import org.jddp.persistence.sql.delete.DeleteConditional;
import org.jddp.persistence.sql.delete.DeleteConnected;
import org.jddp.persistence.sql.delete.DeleteDetached;
import org.jddp.persistence.sql.delete.DeleteStatement;

public class Delete<E>  implements DeleteStatement,  DeleteConnected {

	private final EntityMeta<E> meta;
	
	private String deleteSQL;
	private Connection connection;
	private BooleanExpression<?> whereCondition;
	
	public Delete(EntityClass<E> entityClass) {
		meta = new EntityMeta<E>(entityClass);
	}
	
	public Delete(Delete<E> ds) {
		meta = new EntityMeta<E>(ds.meta.entityClass);
		deleteSQL = ds.deleteSQL;
		connection = ds.connection;
		whereCondition = ds.whereCondition;
	}
	
	@Override
	public Long execute(Connection connection) {
		PreparedStatement ps = null;
		try {
			
			NamedParameter np = new NamedParameter(connection, deleteSQL);
			for (VariableExpression val : whereCondition.getBoundVariables()) {
				np.setValue(val);
			}
			
			ps = np.getPreparedStatement();
			return new Long(ps.executeUpdate());
			
		} catch (Exception e) {
			throw new JDDPException(deleteSQL, e);
		} finally {
			NamedParameter.closeQuietly(ps);
		}
	}

	@Override
	public String getSQL(boolean varAsLiterals) {
		if (varAsLiterals) {
			return createDeleteSQL(varAsLiterals);
		}
		return deleteSQL;
	}

	@Override
	public String getSQL() {
		return deleteSQL;
	}

	@Override
	public Long execute() {
		return execute(connection);
	}
	
	@Override
	public DeleteDetached create() {
		Delete<E> ds = new Delete<>(this);
		ds.deleteSQL = createDeleteSQL(false);
		return ds;
	}
	
	@Override
	public DeleteConnected create(Connection connection) {
		Delete<E> ds = new Delete<>(this);
		ds.connection = connection;
		deleteSQL = createDeleteSQL(false);
		return ds;
	}

	@Override
	public DeleteConditional where(BooleanExpression<?> whereCondition) {
		Delete<E> ds = new Delete<>(this);
		ds.whereCondition = whereCondition;
		return ds;
	}
	
	
	private String createDeleteSQL(boolean varAsLiterals) {

		StringBuilder subsql = new StringBuilder();
		
		
		if (whereCondition != null && whereCondition.getFields().isEmpty()) {
			subsql.append("DELETE FROM ").append(meta.tableName);
			if (varAsLiterals) {
				subsql.append("\nWHERE ").append(whereCondition.unBoundVariables());
			} else {
				subsql.append("\nWHERE ").append(whereCondition);
			}
		} else {
			
			subsql.append("SELECT ").append(meta.primaryKey.getFieldName()).append(" FROM ").append(meta.tableName);
	
			if (whereCondition != null) {
				
				Set<String> requiredJoins = new LinkedHashSet<>();
				for (FieldExpression<?> f : whereCondition.getFields()) {
					requiredJoins.addAll(f.getRequiredJoins());
				}
				for (String j :  requiredJoins) {
					subsql.append("\n        " ).append(j);
				}
				if (varAsLiterals) {
					subsql.append(" \n    WHERE\n        ").append(whereCondition.unBoundVariables());
				} else {
					subsql.append(" \n    WHERE\n        ").append(whereCondition);
				}
			}
	
			subsql = new StringBuilder("DELETE FROM ").append(meta.tableName).append(" WHERE ").append(meta.primaryKey.getFieldName()).append(" IN \n(\n    ").append(subsql).append("\n)"); 
		}
		return subsql.toString();
	}

	
}
