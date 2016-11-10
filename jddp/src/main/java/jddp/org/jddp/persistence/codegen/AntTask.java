package org.jddp.persistence.codegen;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AntTask extends Task  {
	
	private List<Parameter> parameters = new ArrayList<>();
	private String destination;
	
	
	
	
	public void addConfiguredEntity(Parameter parameter) {
		 this.parameters.add(parameter);
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}

	

	@Override
	public void execute() throws BuildException {
		try {
			for (Parameter param : parameters) {
				
				
				
				Class<?> clazz = Class.forName(param.getClassName());
				
				if (param.getPackage_() == null) {
					param.setPackage_(clazz.getPackage().getName());
					System.out.println("No Packaged configured : Generated sources will be generated under this Package -> " + param.getPackage_());
				} else {
					System.out.println("Packaged configured : Generated sources will be generated under this Package -> " + param.getPackage_());
				}
				
				System.out.println("Parsing " + param.getName() + " : " + param.getClassName());
				
				QBSourceGenerator QB = new QBSourceGenerator(null, destination);
				
				QB.createQueryBuilder(param.getClassName(), param.getName(), param.getPackage_());
				
				if (param.getGenerateCompositor() == null || param.getGenerateCompositor()) {
					PKSourceGenerator.generatePKCompositor(clazz, null, destination, param.getPackage_());
				} else {
					System.out.println("Generation of Primary Key Compositor is skipped for entity = " + param.getClassName());
				}
				
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new BuildException(e.getMessage(), e);
		}
	}

	
		
	
}
