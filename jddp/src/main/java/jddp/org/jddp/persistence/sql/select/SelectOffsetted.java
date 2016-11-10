package org.jddp.persistence.sql.select;

import java.sql.Connection;

public interface SelectOffsetted<R>  {
	
	public SelectLimited<R> limit(Integer limit);

	public SelectDetached<R> create();
	public SelectConnected<R> create(Connection connection);
}


