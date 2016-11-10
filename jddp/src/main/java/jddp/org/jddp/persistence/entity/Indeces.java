package org.jddp.persistence.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Indeces {
	String tableName;
	List<Index> indeces = new ArrayList<Index>();
	
	
	public Indeces(String tableName, List<Index> indeces) {
		this.tableName = tableName;
		this.indeces.addAll(indeces);
			
	}
	
	
	public Indeces(String tableName, Index... indeces) {
		this.tableName = tableName;
		for (Index i : indeces) {
			this.indeces.add(i);
		}	
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return tableName;
	}
	/**
	 * @return the indeces
	 */
	public Collection<Index> getIndeces() {
		return Collections.unmodifiableCollection(indeces);
	}

	public Index getIndex(String columnName) {
		columnName = columnName.trim().toLowerCase();
		for (Index i : indeces) {
			if (i.getColumns().contains(columnName)) {
				return i;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = tableName;
		for (Index i : indeces) {
			result += "\n\t" + i.toString();
		}
		
		return result;
	}
	
	
	
	
	
}
