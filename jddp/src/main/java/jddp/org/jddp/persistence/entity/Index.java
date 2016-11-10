package org.jddp.persistence.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Index {
	String indexName;
	List<String> columns = new ArrayList<String>();

	
	public Index(String name, String... columns) {
		this.indexName = name.toLowerCase();
		for (String c : columns) {
			this.columns.add(c.toLowerCase().trim());
		}
	}
	
	public Index(String name, List<String> columns) {
		this.indexName = name;
		for (String c : columns) {
			this.columns.add(c.toLowerCase().trim());
		}
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return indexName;
	}
	/**
	 * @return the columns
	 */
	public Collection<String> getColumns() {
		return Collections.unmodifiableCollection(columns);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = indexName + "(";
		String sep = "";
		for (String column : columns) {
			result += sep + column;
			sep = ",";
		}
		result += ")";
		return result;
	}
	
	
	
}
