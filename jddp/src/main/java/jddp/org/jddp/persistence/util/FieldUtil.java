package org.jddp.persistence.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jddp.expression.FieldExpression;


public class FieldUtil {

	
	public static String getPrefix(String xpath) {
		if (xpath != null) {
			int i = xpath.lastIndexOf("/");
			if (i >= 0) {
				return xpath.substring(0, i);
			}
		}
		return "";
	}

	public static String getPathAsArray(FieldExpression<?> ff) {
		return getPathAsArray(getPathAsList(ff));
		
	}
		
	public static String getPathAsArray(Collection<String> paths) {
		StringBuilder retVal = new StringBuilder("{");
		Iterator<String> iter = paths.iterator();
		for (int i = 0; i < paths.size(); i++) {
			if (i > 0) {
				retVal.append(", ");
			}
			retVal.append(iter.next());
		}
		retVal.append("}");
		return retVal.toString();
	}
	
	public static Collection<String> getPathAsList(FieldExpression<?> ff) {
		FieldExpression<?> current = ff;
		
		Integer pos = null;
		ArrayList<String> path = new ArrayList<>();
		
		while (current != null) {
			FieldExpression<?> owner = current.getOwner();
			
			if (owner != null) {
				path.add(0, current.getFieldName());
				if (current.isArray() && current instanceof FieldExpression) {
					pos = ((FieldExpression<?>)current).getPosition();
					if (pos != null) {
						path.add(1, pos.toString());
					} 
				}
			}
			current = owner;
		}
		
		return path;
	}
	
	public static String removePrefix(String prefix, String xpath) {
		if (xpath.startsWith(prefix)) {
			xpath = xpath.substring(prefix.length() + 1);
		}
		return xpath;
	}
	
	public static String toJSON(String xpath) {
		String[] components =  xpath.split("/");
		String json = components[0];
		for (int i = 1; i < components.length; i++) {
			json += "->" + squote(components[i]); 
		}
		return json;
	}
	
	public static String squote(String s) {
		return "'" + s + "'";
	}
}
