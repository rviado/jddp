package org.jddp.persistence.connection.providers;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {
	
	public Connection newConnection();
	public void shutDown();
	
	
	public static void closeQuietly(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}	
		} catch (SQLException e) {
			
		}
	}
	
	public static void rollbackQuietly(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.rollback();
			}	
		} catch (SQLException e) {
			
		}
	}
	
	
}
