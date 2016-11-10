package org.jddp.persistence.pgsql;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import org.jddp.db.maintenance.DBManager;
import org.jddp.db.maintenance.DBServiceFactory;
import org.jddp.exception.JDDPException;
import org.jddp.persistence.connection.providers.ConnectionProvider;
import org.jddp.persistence.sample.Sample2;
import org.jddp.persistence.sample.Sample3;
import org.jddp.persistence.sample.SampleExtension;
import org.jddp.persistence.sample.SampleExtension2;
import org.jddp.persistence.sql.DDL;
import org.jddp.persistence.sql.DML;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import optional.packge._Sample;
import optional.packge._Sample2;

public class BaseTest {
	static final UUID key1 = UUID.fromString("5ed00022-15ab-4bd6-96ee-2599621cb90d");
	static final UUID key2 = UUID.fromString("4ed00022-15ab-4bd6-96ee-2599621cb90d");
	static final List<String> keys = Arrays.asList(key1.toString(), key2.toString());
	static final List<Double> numbers1 = Arrays.asList(-333d, -546d);
	static final List<Double> numbers2 = Arrays.asList(111d, 222d, 888d);
	static final double sum = (numbers1.get(0) + numbers1.get(1));
	static final double average = sum/2;
	
	static final String stringValue1 = "String value";
	static final String stringValue2 = "String value 2nd entry"; 
	
	static final DBManager dbManager = new DBServiceFactory("jddp.sample").newDBManager();
	static final DDL<SampleExtension> ddl = _Sample.getDDL();
	static final DML<SampleExtension> dml = _Sample.getDML();
	
	static final DDL<SampleExtension2> ddl2 = _Sample2.getDDL();
	static final DML<SampleExtension2> dml2 = _Sample2.getDML();
	
	static final ZonedDateTime zdt1 = ZonedDateTime.parse("2016-10-01T18:50:23.000+08:00[Asia/Shanghai]");
	static final ZonedDateTime zdt2 = ZonedDateTime.parse("2016-10-01T18:50:23.111+08:00[Asia/Shanghai]");
	
	static final List<String> strings1 = Arrays.asList("a", "b", "c", "d");
	static final List<String> strings2 = Arrays.asList("e", "f", "g", "h");
	static final byte[] byteArray = "rodel viado".getBytes();
	
	static final byte[] byteArray2 = "rodel2 viado2".getBytes();
	
	Connection con;
	
	
	public BaseTest() {
		
	}
	
	@BeforeClass
	public static void setupDB() {
		Connection connection = null;
		try {
			if (!dbManager.isDatabaseExist("jddp")) {
				dbManager.createDatabase("jddp");
			}
			connection =  new DBServiceFactory("jddp.sample").newConnectionProvider().newConnection();
			connection.setAutoCommit(false);
			
			
			if (!ddl.isTableExists(connection)) {
				ddl.createTable(connection);
			}
			
			ddl.createIndecesIfAbsent(connection);
			
			if (!ddl2.isTableExists(connection)) {
				ddl2.createTable(connection);
			}
			
			ddl2.createIndecesIfAbsent(connection);
			
			SampleExtension se = new SampleExtension();
			
			se = createSample(
					key1, 
					stringValue1, 
					true, 
					numbers1.get(0), 
					zdt1, 
					strings1, 
					numbers1,
					byteArray
			);
			
			dml.upsert(connection, se);
			
			se = createSample(
					key2, 
					stringValue2, 
					false, 
					numbers1.get(1), 
					zdt2, 
					strings2, 
					numbers2,
					byteArray2
			);
			
			dml.upsert(connection, se);
			
			
			SampleExtension2 se2 = createSample2(
					key1, 
					stringValue1, 
					true, 
					numbers1.get(0), 
					zdt1, 
					strings1, 
					numbers1,
					byteArray
			);
			
			dml2.upsert(connection, se2);
			
			se2 = createSample2(
					key2, 
					stringValue2, 
					false, 
					numbers2.get(0), 
					zdt2, 
					strings2, 
					numbers2,
					byteArray2
			);
			
			dml2.upsert(connection, se2);
			
			connection.commit();
			
		} catch (Exception e) {
			ConnectionProvider.rollbackQuietly(connection);
			throw new JDDPException(e);
		} finally {
			ConnectionProvider.closeQuietly(connection);
		}
	}
	
	
	
	@Before
	public void setup() throws SQLException {
		con =  new DBServiceFactory("jddp.sample").newConnectionProvider().newConnection();
		con.setAutoCommit(false);
	}
	
	
	@After 
	public void tearup() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected static Sample2 createSample2(String s, Boolean b, Number n,  ZonedDateTime o, List<String> strings, List<Double> numbers, Sample2 s2) {
		Sample2 se2 = new Sample2();
		
		se2.setStrings(strings);
		se2.setNumbers(numbers);
		
		se2.setObject(o);
		se2.setString(s);
		se2.setNumber(n.longValue());
		se2.setBoolean(false);
		if (s2 != null) {
			se2.setRecursive(s2);
			se2.getRecursiveArraies().add(s2);
			se2.getArrayWithSample3AsElementTypes().add(createSample3(createSample2(s, b, n, o, strings, numbers, null)));
			se2.getArrayWithSample3AsElementTypes().add(createSample3(createSample2(s, b, n, o, strings, numbers, null)));
		}	
		return se2;
	}
	
	protected static Sample3 createSample3(Sample2 se2) {
		Sample3 se3 = new Sample3();
		se3.getArrayWithSample2AsElementTypes().add(se2);
		se3.setBoolean(true);
		se3.setBooleans(Arrays.asList(true, false));
		se3.setNumbers(numbers2);
		se3.setString("Sample 3");
		return se3;
	}
	
	protected static SampleExtension createSample(UUID uuid, String s, Boolean b, Number n, ZonedDateTime o, List<String> strings, List<Double> numbers, byte[] byteArray) {
		SampleExtension se = new SampleExtension();
		se.setPkey(uuid.toString());
		se.setStrings(strings);
		se.setNumbers(numbers);
		
		se.setObject(o);
		se.setTimestamp(o);
		se.setString(s);
		se.setNumber(n.longValue());
		se.setBoolean(true);
		se.getBooleans().add(true);
		se.getBooleans().add(false);
		
		Sample2 se2 = createSample2(s, b, n, o, strings, numbers, createSample2("string in a recursive array", true, 987654321, o,  Arrays.asList("1","2"), numbers, null));
		
		
		
		se.setObjectWithArray(se2);
		
		se.setByteArray(byteArray);
		
		return se;
	}
	
	protected static SampleExtension2 createSample2(UUID uuid, String s, Boolean b, Number n, ZonedDateTime o, List<String> strings, List<Double> numbers, byte[] byteArray) {
		SampleExtension2 se = new SampleExtension2();
		se.setPkey(uuid.toString());
		se.setStrings(strings);
		se.setNumbers(numbers);
		
		se.setObject(o);
		se.setTimestamp(o);
		se.setString(s);
		se.setNumber(n.longValue());
		se.setBoolean(true);
		se.getBooleans().add(true);
		se.getBooleans().add(false);
		
		Sample2 se2 = createSample2(s, b, n, o, strings, numbers, createSample2("string in a recursive array", true, 987654321, o,  Arrays.asList("1","2"), numbers, null));
		
		
		
		se.setObjectWithArray(se2);
		
		se.setByteArray(byteArray);
		
		return se;
	}
}
