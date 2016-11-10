package org.jddp.persistence.sql;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public interface ResultSet {

	/**
	 * @return the rows
	 */
	
	public List<Row> getRows();

	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<Row> rows);
	
	
	public int getRowCount();
	
	public Row getRow(int index);
	
	public Object getResultAt(int row, int col);
	
	public <T> T getResultAt(int row, int col, Class<T> type);
	
}
