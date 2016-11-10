package org.jddp.persistence.sql.update;

import java.sql.Connection;


public interface UpdateConditional  {
	public UpdateConnected create(Connection connection);
	public UpdateDetached create();

}
