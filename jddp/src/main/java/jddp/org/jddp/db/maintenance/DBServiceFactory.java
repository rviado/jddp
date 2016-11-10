package org.jddp.db.maintenance;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.jddp.persistence.connection.providers.ConnectionProvider;
import org.springframework.context.ApplicationContext;

public class DBServiceFactory {
	
	String serviceId;
	
	static final ApplicationContext context = new ClassPathXmlApplicationContext("conf/persistence.xml");

	public DBServiceFactory(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public DBManager newDBManager() {
		return context.getBean(serviceId + "." + "database.server", DBManager.class);
	}
	
	public ConnectionProvider newConnectionProvider() {
		return context.getBean(serviceId + "." + "ConnectionProvider", ConnectionProvider.class);
	}
	
	
}
