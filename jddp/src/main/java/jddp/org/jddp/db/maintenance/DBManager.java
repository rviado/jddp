package org.jddp.db.maintenance;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;

public interface DBManager {

	public boolean isDatabaseExist(String dbname) throws SQLException;
	public void createDatabase(String dbname) throws SQLException;
	public void dropDatabase(String dbname) throws SQLException;
	
	
	public static <X extends DBManager> X newInstance(String url, String user, String password) {
		try {
			InputStream config = DBManager.class.getClassLoader().getResourceAsStream("META-INF/jddp/" + DBManager.class.getName());
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
			return (X) implClass.getConstructor(String.class, String.class, String.class).newInstance(url, user, password);
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
}
