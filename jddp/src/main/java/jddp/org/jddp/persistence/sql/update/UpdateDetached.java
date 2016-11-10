package org.jddp.persistence.sql.update;

import java.sql.Connection;

public interface UpdateDetached {
	public Long execute(Connection connection);

	public String getSQL(boolean varAsLiterals);
	
	
	public String getSQL();
	

}
