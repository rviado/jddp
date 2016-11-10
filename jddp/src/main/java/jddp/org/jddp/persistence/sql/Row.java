package org.jddp.persistence.sql;

import java.util.List;

public interface Row {

	/**
	 * @return the columns
	 */
	public List<Object> getColumns();

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<Object> columns);
	
	public int getColumnCount();
	
	public <T> T getColumn(int index, Class<T> type);
	
	public Object getColumn(int index);
}
