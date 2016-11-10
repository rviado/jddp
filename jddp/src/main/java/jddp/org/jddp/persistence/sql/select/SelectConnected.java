package org.jddp.persistence.sql.select;

public interface SelectConnected<R> extends SelectDetached<R> {

	public R execute();
	public Long executeCount();
	
	
}
