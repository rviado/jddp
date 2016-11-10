package org.jddp.persistence.sql.delete;

import java.sql.Connection;

public interface DeleteConditional  {
	public DeleteConnected create(Connection connection);
	public DeleteDetached create();

}
