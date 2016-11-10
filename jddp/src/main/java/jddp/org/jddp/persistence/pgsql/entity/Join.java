package org.jddp.persistence.pgsql.entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Join {

	public static enum TYPE  {
		LEFT, RIGHT, INNER, FULL
	};
	
	TYPE joinType;
	String tableExpression;
	String alias;
	String joinCondition;
	Join   previousJoin;
	String xpath;
	String joinReference;
	
	
	public Join(String xpath, Join previousJoin, TYPE joinType, String tableExpression, String alias, String joinCondition, String joinReference) {
		super();
		this.xpath = xpath;
		this.previousJoin = previousJoin;
		this.joinType = joinType;
		this.tableExpression = tableExpression;
		this.alias = alias;
		this.joinCondition = joinCondition;
		this.joinReference = joinReference;
	}

	
	
	/**
	 * @return the joinReference
	 */
	public String getJoinReference() {
		return joinReference;
	}



	public Set<Join> getRequiredJoins() {
		Set<Join> result = new LinkedHashSet<Join>();
		List<Join> tmp = new ArrayList<Join>();
		
		Join pjoin = previousJoin;
		while (pjoin != null) {
			tmp.add(pjoin);
			pjoin = pjoin.previousJoin;
		}
		result.addAll(tmp);
		return result;
	}
	
	/**
	 * @return the previousJoin
	 */
	public Join getPreviousJoin() {
		return previousJoin;
	}



	/**
	 * @return the xpath
	 */
	public String getXpath() {
		return xpath;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (alias == null) {
			return joinType.name() + " JOIN " + tableExpression + " ON " + joinCondition;
		} else {
			return joinType.name() + " JOIN " + tableExpression + " AS " + alias + " ON " + joinCondition;
		}
	}


	/**
	 * @return the joinType
	 */
	public TYPE getJoinType() {
		return joinType;
	}


	/**
	 * @return the tableExpression
	 */
	public String getTableExpression() {
		return tableExpression;
	}

	/**
	 * @return the joinCondition
	 */
	public String getJoinCondition() {
		return joinCondition;
	}


	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return toString().equals(obj.toString());
	}
	
}
