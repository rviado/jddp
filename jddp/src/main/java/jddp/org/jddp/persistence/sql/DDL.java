package org.jddp.persistence.sql;

import java.sql.Connection;

import org.jddp.expression.FieldExpression;
import org.jddp.persistence.entity.Indeces;



public interface DDL<E>  {
	
	public String getTableName();

	public boolean isColumnExists(Connection connection, String columnName);
	
	
	public boolean isTableExists(Connection connection);
	
	public boolean isIndexExists(Connection connection, FieldExpression<?> e);
	
	public int createTable(Connection connection);
	
	public int createIndeces(Connection connection);

	public int createIndecesIfAbsent(Connection connection);
	
	public boolean createTableIfAbsent(Connection connection);
	
	public int dropTable(Connection connection);

	public Indeces getExistingIndeces(Connection connection);
	
	public int truncateTable(Connection connection);

	public void refreshDB(Connection connection);
	
}
