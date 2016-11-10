package org.jddp.persistence.pgsql.db.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.jddp.db.maintenance.DBManager;
import org.jddp.persistence.connection.providers.ConnectionProvider;


public class PGSQLDBManager implements DBManager {
	
	String url;
	String user;
	String password;
	Map<String, ConnectionProvider> connectionProviders = new HashMap<>();
	
	
	public PGSQLDBManager(String url, String user, String password) throws ClassNotFoundException {
		super();
		this.url = url;
		this.user = user;
		this.password = password;
		Class.forName("org.postgresql.Driver");
	}
	
	
	public boolean isDatabaseExist(String dbname) throws SQLException {
		
		Connection conn = DriverManager.getConnection(url, user, password);
		try {
			String s = "SELECT 1 AS result FROM pg_database WHERE datname='" + dbname +  "'";
			ResultSet x = conn.createStatement().executeQuery(s);
			boolean result = x.next();
			return result;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				
			}
		}
	}
	
	public void createDatabase(String dbname) throws SQLException {
		Connection conn = DriverManager.getConnection(url, user, password);
		try {
			String s = "CREATE DATABASE " + dbname + " ENCODING 'UTF8'";
			conn.createStatement().execute(s);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				
			}
		}	
	}
	
	
	public void dropDatabase(String dbname) throws SQLException {
		Connection conn = DriverManager.getConnection(url, user, password);
		
		String s = "DROP DATABASE IF EXISTS " + dbname;
		conn.createStatement().execute(s);
		conn.close();
	}
	
	
	public ConnectionProvider getConnectionPRovider(String id) {
		return connectionProviders.get(id);
	}
}
