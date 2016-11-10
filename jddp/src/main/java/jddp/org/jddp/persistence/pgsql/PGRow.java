package org.jddp.persistence.pgsql;

import java.util.ArrayList;
import java.util.List;

import org.jddp.persistence.sql.Row;

public class PGRow implements Row {
	private List<Object> columns = null;

	/**
	 * @return the columns
	 */
	public List<Object> getColumns() {
		if (columns == null) {
			columns = new ArrayList<>();
		}
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<Object> columns) {
		this.columns = columns;
	}
	
	public int getColumnCount() {
		return getColumns().size();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getColumn(int index, Class<T> type) {
		return (T) columns.get(index);
	}
	
	public Object getColumn(int index) {
		return columns.get(index);
	}
}
