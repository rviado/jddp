package org.jddp.persistence.sql;

import java.sql.Connection;
import java.util.List;

import org.jddp.expression.Expression;
import org.jddp.persistence.sql.delete.DeleteStatement;
import org.jddp.persistence.sql.select.SelectStatement;
import org.jddp.persistence.sql.update.UpdateStatement;

public interface DML<E>   {
	
	public SelectStatement<List<E>> select();
	
	public SelectStatement<ResultSet> select(Expression<?>... fields);

	public DeleteStatement delete();
	
	public UpdateStatement update();
	
	public int upsert(Connection connection, E entity);
	
	public int insert(Connection connection, E entity);

	public int update(Connection connection, E entity);
	
	public int delete(Connection connection, E obj);
	
	public int delete(Connection connection, List<E> objs);

	public E retrieveByKey(Connection connection, Object pkeyValue);
		
	public List<E> retrieve(Connection connection, List<?> pkeys);
	
	public boolean isExistsByKey(Connection connection, Object pkeyValue);

	public Object getPrimaryKeyValue(E obj);
	
	public List<Object> getPrimaryKeyValues(List<E> objs);
	
	public  <PKTYPE> List<PKTYPE> getPrimaryKeyValues(List<E> objs, Class<PKTYPE> type);
	

}
