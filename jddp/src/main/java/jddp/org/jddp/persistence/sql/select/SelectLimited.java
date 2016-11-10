package org.jddp.persistence.sql.select;

import java.sql.Connection;

public interface SelectLimited<R>  {
	public SelectOffsetted<R> offset(Integer offset);
		
	public SelectDetached<R> create();
	public SelectConnected<R> create(Connection connection);
	
}


