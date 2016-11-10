package org.jddp.persistence.codegen;

public class Parameter {
	String className;
	String name;
	String package_;
	Boolean generateCompositor;
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @return the entityName
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param entityName the entityName to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the package_
	 */
	public String getPackage_() {
		return package_;
	}
	/**
	 * @param package_ the package_ to set
	 */
	public void setPackage_(String package_) {
		this.package_ = package_;
	}
	/**
	 * @return the generateCompositor
	 */
	public Boolean getGenerateCompositor() {
		return generateCompositor;
	}
	/**
	 * @param generateCompositor the generateCompositor to set
	 */
	public void setGenerateCompositor(Boolean generateCompositor) {
		this.generateCompositor = generateCompositor;
	}
	
	
	
	
}
