package org.jddp.persistence.sql.delete;

import java.sql.Connection;

public interface DeleteDetached {
	public Long execute(Connection connection);
	public String getSQL(boolean varAsLiterals);
	public String getSQL();
}
