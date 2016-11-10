package org.jddp.persistence.pgsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jddp.exception.JDDPException;
import org.jddp.expression.FieldExpression;
import org.jddp.persistence.entity.Indeces;
import org.jddp.persistence.entity.Index;
import org.jddp.persistence.pgsql.entity.EntityClass;
import org.jddp.persistence.sql.DDL;
import org.jddp.persistence.sql.select.SelectLimited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PGDDL<E>  implements DDL<E> {
	
	final static Logger logger = LoggerFactory.getLogger(PGDDL.class);
	
	protected final EntityClass<E> entityClass;
	
	protected final String  tableName; 
	protected final FieldExpression<?> entityField;
	protected final FieldExpression<?> primaryKey;
	protected final Set<FieldExpression<?>> indeces;

	public static final String IS_TABLE_EXISTS_SQL = "SELECT EXISTS (SELECT 1 FROM   information_schema.tables WHERE  table_schema = 'public'  AND  table_name = ?)";
	public static final String GET_INDECES_SQL = 
			"select\n" +
					//"t.relname as table_name,\n" +
					"i.relname as index_name,\n" +
					"array_to_string(array_agg(a.attname), ', ') as column_names\n" +
				"from\n" +
					"pg_class t,\n" +
					"pg_class i,\n" +
					"pg_index ix,\n" +
					"pg_attribute a\n" +
				"where\n" +
				    "t.oid = ix.indrelid\n" +
				    "and i.oid = ix.indexrelid\n" +
				    "and a.attrelid = t.oid\n" +
				    "and a.attnum = ANY(ix.indkey)\n" +
				    "and t.relkind = 'r'\n" +
				    "and lower(t.relname) = ?\n"  +
				"group by\n" +
				    "t.relname,\n" +
				    "i.relname\n" +
				"order by\n" +
				    "t.relname,\n" +
				    "i.relname\n";
	
	public static final String IS_COLUMN_EXIST_SQL = "SELECT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_name= ? and column_name= LOWER(?))";
	
			
	private final String dropTableSQL;
	private final String createIndexSQL;
	private final String createTableSQL;
	private final String truncateSQL;
	
	
	public PGDDL(EntityClass<E> entityClass) {
		
		this.entityClass = entityClass;
		this.tableName = entityClass.getEntityName().toLowerCase() + "_nosql";
		this.entityField = entityClass.getJSONField();
		this.primaryKey = entityClass.getPrimaryKey();
		this.indeces = entityClass.getIndeces();
		
		createTableSQL = initCreateTableSQL();
		dropTableSQL =    "DROP TABLE IF EXISTS " + tableName;
		createIndexSQL =  "CREATE INDEX ON " + tableName +" USING GIN (" + entityField.getFieldName() + ")";
		
		truncateSQL = "TRUNCATE " + tableName;
	}
	
	
	public String getTableName() {
		return tableName;
	}

	public boolean isColumnExists(Connection connection, String columnName) {
		try {
			PreparedStatement stmt = connection.prepareStatement(IS_COLUMN_EXIST_SQL);
			stmt.setString(1, tableName);
			stmt.setString(2, columnName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getBoolean(1);
			}	
		} catch (SQLException e) {
			throw new JDDPException(e);
		}
		return false;
	}
	
	
	public boolean isTableExists(Connection connection) {
		try {
			PreparedStatement stmt = connection.prepareStatement(IS_TABLE_EXISTS_SQL);
			stmt.setString(1, tableName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getBoolean(1);
			}	
		} catch (SQLException e) {
			throw new JDDPException(e);
		}
		return false;
	}
	
	public boolean isIndexExists(Connection connection, FieldExpression<?> e) {
		return getExistingIndeces(connection).getIndex(e.getFieldName()) != null;
	}
	
	public int createTable(Connection connection) {
		try {
			PreparedStatement stmt = connection.prepareStatement(createTableSQL);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new JDDPException(e);
		}	
	}
	
	public int createIndeces(Connection connection) {

		Indeces existingIndeces = getExistingIndeces(connection);

		int total = 0;
		for (FieldExpression<?> f : indeces) {
			Index i;
			if ((i = existingIndeces.getIndex(f.getFieldName())) == null) {
				String sql = "CREATE INDEX ON " + tableName + "(" + f.getFieldName() + ")";
				logger.info(sql);
				try {
					PreparedStatement stmt = connection.prepareStatement(sql);
					stmt.executeUpdate();
					total++;
				} catch (SQLException e) {
					throw new JDDPException(e);
				}
			} else {
				logger.info("Index already exists : {}", i);
			}

		}

		Index i;
		if ((i = existingIndeces.getIndex(entityField.getFieldName())) == null) {
			logger.info(createIndexSQL);
			try {
				PreparedStatement stmt = connection.prepareStatement(createIndexSQL);
				stmt.executeUpdate();
				total++;
			} catch (SQLException e) {
				throw new JDDPException(e);
			}	
		} else {
			logger.info("Index already exists : {}", i);
		}
		return total;
	}

	
	public int createIndecesIfAbsent(Connection connection) {

		int total = 0;
		
		List<FieldExpression<?>> fields = new ArrayList<FieldExpression<?>>();
		fields.addAll(indeces);
		
		for (FieldExpression<?> f : fields) {
			String type = entityClass.getDBType(f);
			
			if (!isColumnExists(connection, f.getFieldName())) {
				String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + f.getFieldName() + " " + type + " " + entityClass.getConstraint(f);
				logger.info(sql);
				try {
					PreparedStatement stmt = connection.prepareStatement(sql);
					stmt.executeUpdate();
					total++;
				} catch (SQLException e) {
					throw new JDDPException(e);
				}
			}
		}
		logger.info("{} columns altered", total);
		return total;
	}
	
	public boolean createTableIfAbsent(Connection connection) {
		
		boolean mustRefresh = false;
		if (!isTableExists(connection)) {
			createTable(connection);
			createIndeces(connection);					
		} else {
			mustRefresh = createIndecesIfAbsent(connection) > 0;
			createIndeces(connection);
		}
		
		return mustRefresh;
		
	}
	
	
	
	
	public int dropTable(Connection connection) {
		try {
			PreparedStatement stmt = connection.prepareStatement(dropTableSQL);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new JDDPException(e);
		}
	}

	public Indeces getExistingIndeces(Connection connection) {
		try {
			PreparedStatement stmt = connection.prepareStatement(GET_INDECES_SQL);
			stmt.setString(1, tableName);
			ResultSet rs = stmt.executeQuery();
			List<Index> idxs = new ArrayList<Index>();
			
			while (rs.next()) {
				Index i = new Index(rs.getString(1), rs.getString(2).split(","));
				idxs.add(i);
			}
			return  new Indeces(tableName, idxs);
		} catch (SQLException e) {
			throw new JDDPException(e);
		}
		
	}
	
	
	
	
	
	public int truncateTable(Connection connection) {
		try {
			PreparedStatement stmt = connection.prepareStatement(truncateSQL);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new JDDPException(e);
		}
	}

	
	public void refreshDB(Connection connection) {
		
		System.out.println("Refreshing " + tableName + " this may take a while...");
		
		 SelectLimited<List<E>> q = new PGDML<E>(entityClass).select().orderBy(primaryKey).limit(1000);
		 PGDML<E> dml = new PGDML<>(entityClass);
		 
		int startAt = 0;
		List<E> l;
		long count = 0;
		try {
			do {
				l = q.offset(startAt).create().execute(connection);
				for (E x : l) {
					dml.update(connection, x);
					if (++count % 5000 == 0) {
						logger.debug("{} records refreshed", count);
					}
					
				}
				connection.commit();
				startAt += 1000;
			} while (l.size() >= 1000); 	
			
			
		} catch (SQLException e) {
			throw new JDDPException(e);
		}
		
	}
	
	
	private String initCreateTableSQL() {
		
		String createTableNoSQL =  "CREATE TABLE IF NOT EXISTS " + tableName + " (";

		
		List<FieldExpression<?>> fields = new ArrayList<FieldExpression<?>>();
		fields.add(entityClass.getPrimaryKey());
		fields.addAll(indeces);
		fields.add(entityField);
		
		for (FieldExpression<?> f : fields) {
			String type = entityClass.getDBType(f);
			createTableNoSQL += f.getFieldName() + " " + type + " " + entityClass.getConstraint(f) + ", ";
		}
		createTableNoSQL +=  "CONSTRAINT " + tableName  + "_pkey PRIMARY KEY (" + primaryKey.getFieldName() + "))";
		return createTableNoSQL;
	}
	
	
}
