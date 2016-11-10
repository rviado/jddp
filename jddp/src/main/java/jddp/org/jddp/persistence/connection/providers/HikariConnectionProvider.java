package org.jddp.persistence.connection.providers;

import java.sql.Connection;
import java.sql.SQLException;

import org.jddp.exception.JDDPException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariConnectionProvider extends HikariDataSource implements ConnectionProvider {

	public HikariConnectionProvider() {
		super();
	}

	public HikariConnectionProvider(HikariConfig configuration) {
		super(configuration);
	}

	@Override
	public Connection newConnection()  {
		try {
			return super.getConnection();
		} catch (SQLException e) {
			throw new JDDPException(e);
		}
	}

	@Override
	public void shutDown() {
		super.close();
	}

	
}
