package org.jddp.persistence.entity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Set;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.BooleanFieldExpression;
import org.jddp.expression.FieldExpression;
import org.jddp.expression.LiteralExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.NumericFieldExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.ObjectFieldExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.StringFieldExpression;
import org.jddp.persistence.sql.DDL;
import org.jddp.persistence.sql.DML;
import org.jddp.persistence.util.DBType;

public interface EntityManager<E> {
	
	public ObjectFieldExpression getObjectFieldExpression(String xpath);
	public ObjectFieldExpression newObjectFieldExpression(FieldExpression<?> f, FieldExpression<?> owner);
	public ObjectFieldExpression newObjectFieldExpression(FieldExpression<?> f, FieldExpression<?> owner, int i);
	
	public ObjectFieldExpression newObjectFieldExpression(String xpath, FieldExpression<?> owner);
	public ObjectFieldExpression newObjectFieldExpression(String xpath, FieldExpression<?> owner, int i);
	
	public StringFieldExpression getStringFieldExpression(String xpath);
	public StringFieldExpression newStringFieldExpression(FieldExpression<?> f, FieldExpression<?> owner);
	public StringFieldExpression newStringFieldExpression(String xpath, FieldExpression<?> owner);
	
	public BooleanFieldExpression getBooleanFieldExpression(String xpath);
	public BooleanFieldExpression newBooleanFieldExpression(FieldExpression<?> f, FieldExpression<?> owner);
	public BooleanFieldExpression newBooleanFieldExpression(String xpath, FieldExpression<?> owner);
	
	public NumericFieldExpression getNumericFieldExpression(String xpath);
	public NumericFieldExpression newNumericFieldExpression(FieldExpression<?> f, FieldExpression<?> owner);
	public NumericFieldExpression newNumericFieldExpression(String xpath, FieldExpression<?> owner);
	
	public NumericExpression<?> newNumber(Number n);
	public BooleanExpression<?> newBoolean(Boolean b);
	public StringExpression<?>  newString(String s);
	public ObjectExpression<?>  newObject(Object o, DBType dbType);
	public ObjectExpression<?>  newObject(Collection<?> c, DBType dbType);
	
	public LiteralExpression newLiteral(String s);
		
	public FieldExpression<?> getFieldExpression(String xpath);
	public <T extends FieldExpression<?>> T getFieldExpression(String xpath, Class<T> type);
	
	public FieldExpression<?> getPrimaryKey();
	public <T extends FieldExpression<?>> T getPrimaryKey(Class<T> pkeyType);
	public <T> T getPrimaryKeyValue(E obj);
	public <T> T getIndexValue(FieldExpression<?> f, E e, Class<T> type);
	
	public Set<FieldExpression<?>> getIndeces();
	public String getConstraint(FieldExpression<?> field);
	public boolean isIndexed(FieldExpression<?> field);
	
	public FieldExpression<?> getXPathAsField(String xpath);
	public boolean isValidXPath(String xpath);
	public String getXpathsAsString();
	
	public Collection<String> getXPaths();
	
	public  DML<E> getDML();
	public  DDL<E> getDDL();
	
	
	@SuppressWarnings("unchecked")
	public static <X extends EntityManager<T>, T> X newInstance(Class<T> entity, String rootElement) {
		try {
			InputStream config = EntityManager.class.getClassLoader().getResourceAsStream("META-INF/jddp/" + EntityManager.class.getName());
   		    char[] buf = new char[2048];
			Reader r = new InputStreamReader(config, "UTF-8");
			StringBuilder s = new StringBuilder();
			int n;
			int newLine = Character.getNumericValue('\n');
			int carriageReturn = Character.getNumericValue('\r');
			while (true) {
			    n = r.read(buf);
			    if (n < 0 || newLine == n  || carriageReturn == n) {
			    	break;
			    } 
			    s.append(buf, 0, n);
			 }
			r.close();
			Class<?> implClass = Class.forName(s.toString().trim());
			return (X) implClass.getConstructor(Class.class, String.class).newInstance(entity, rootElement);
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	
}
