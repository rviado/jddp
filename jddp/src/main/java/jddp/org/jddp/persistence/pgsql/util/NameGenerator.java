package org.jddp.persistence.pgsql.util;

public class NameGenerator {
	private static ThreadLocal<Integer> paramIdx = new  ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};
	
	protected static int postIncrementIdx() {
		int i = paramIdx.get();
		if (i >= Integer.MAX_VALUE || i < 0) {
			i = 0;
		}
		paramIdx.set(i+1);
		return i;
	}
	
	public static String newUniqueParameterName(Class<?> clazz) {
		return  "_"  + clazz.getSimpleName().charAt(0) + postIncrementIdx() + "_";
	}
	
	public static String generateCompositorClassName(Class<?> entity, String suppliedName) {
		String[] result = new String[2];
		if (suppliedName.isEmpty()) {
    		result[0] = entity.getPackage().getName();
    	 	result[1] = entity.getSimpleName().replaceFirst("Type$", "") + "PKCompositor";
    	} else if (!suppliedName.contains(".")) {
    		result[0] = entity.getPackage().getName();
    		result[1] = suppliedName;
    	} else {
    		int i = suppliedName.lastIndexOf(".");
    		result[0] = suppliedName.substring(0, i);
    		result[1] = suppliedName.substring(i + 1);
    	}
		return result[0] + "." + result[1];
	}
}
