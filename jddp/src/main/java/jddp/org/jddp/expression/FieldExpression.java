package org.jddp.expression;

import java.util.Set;

public interface FieldExpression<T extends Expression<?>> extends Expression<T> {
		
	public boolean isAssociation();
	public boolean isIndex();
	public boolean isPrimaryKey();

	public String asTextReference();
	public String asObjectReference();
	
	public String getPrefix();
	public String getFieldName();
	
	public String getXpath();
	
	public Set<String> getRequiredJoins();
	public Integer getPosition();
	
	String[] getAccessor();
	FieldExpression<?> getOwner();
	
	public String toString();
	public boolean equals(Object other);
	public int hashCode();
	
}
