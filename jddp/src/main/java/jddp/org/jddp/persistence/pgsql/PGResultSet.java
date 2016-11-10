package org.jddp.persistence.pgsql;

import java.util.ArrayList;
import java.util.List;

import org.jddp.persistence.sql.ResultSet;
import org.jddp.persistence.sql.Row;

public class PGResultSet  implements ResultSet {
	private List<Row> rows = null;

	/**
	 * @return the rows
	 */
	public List<Row> getRows() {
		if (rows == null) {
			rows = new ArrayList<>();
		}
		return rows;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
	
	
	public int getRowCount() {
		return getRows().size();
	}
	
	public Row getRow(int index) {
		return rows.get(index);
	}
	
	public Object getResultAt(int row, int col) {
		return rows.get(row).getColumn(col);
	}
	
	public <T> T getResultAt(int row, int col, Class<T> type) {
		return rows.get(row).getColumn(col, type);
	}
	
}
