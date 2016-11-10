package org.jddp.persistence.connection.providers;

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.internal.SessionImpl;

public class HibernateConnectionProvider implements ConnectionProvider {
	SessionFactory sf;
	StandardServiceRegistry standardRegistry;
	String jdbcUrl = null;
	
	public HibernateConnectionProvider(String resourceName) {
		
		standardRegistry = new StandardServiceRegistryBuilder().configure(resourceName).build();
		Metadata metadata = new MetadataSources(standardRegistry).getMetadataBuilder().build();
		sf = metadata.getSessionFactoryBuilder().build();
	}


	@Override
	public Connection newConnection() {
		Session session = sf.openSession();
		return ((SessionImpl) session).connection();
	}


	@Override
	public void shutDown() {
		StandardServiceRegistryBuilder.destroy(standardRegistry);
	}
	
	
}
