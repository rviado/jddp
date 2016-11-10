package org.jddp.db.maintenance;

import java.sql.Connection;

import org.jddp.persistence.connection.providers.ConnectionProvider;
import org.jddp.persistence.sql.DDL;

public class DBBootstrapper {

	public static void createDatabseIfAbsent(String serverId, String dbname)  {
		try {
			DBManager manager = new DBServiceFactory(serverId).newDBManager();
			if (!manager.isDatabaseExist(dbname)) {
				manager.createDatabase(dbname);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void bootstrap(String serverId, DDL<?> ddl) {
		Connection connection = null;
		try {
			
			ConnectionProvider cf = new DBServiceFactory(serverId).newConnectionProvider();
			connection = cf.newConnection();
			connection.setAutoCommit(false);
			
			boolean mustRefresh = ddl.createTableIfAbsent(connection);
			connection.commit();
			
			if (mustRefresh) {
				ddl.refreshDB(connection);
				connection.commit();
			}
		} catch (Exception e) {
			ConnectionProvider.rollbackQuietly(connection);
			throw new RuntimeException(e);
		} finally {
			ConnectionProvider.closeQuietly(connection);
		}
	}
}
