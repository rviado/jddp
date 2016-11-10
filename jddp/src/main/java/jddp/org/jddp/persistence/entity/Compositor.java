package org.jddp.persistence.entity;

public interface Compositor<E, T> {
	public  T getComposedKey(E entity);
	
}
