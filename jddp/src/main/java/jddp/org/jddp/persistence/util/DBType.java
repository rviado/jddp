package org.jddp.persistence.util;

public enum DBType {
	UUID, TIMESTAMP, TIMESTAMPTZ,
	BOOLEAN, NUMERIC, TEXT, JSON, JSONB
	;
		

	public static DBType from(String s) {
		s = s.toUpperCase();
		try {
			return valueOf(s);
		} catch (IllegalArgumentException | NullPointerException e) {
			return DBType.JSONB;
		}
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
