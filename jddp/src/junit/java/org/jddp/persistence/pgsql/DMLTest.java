package org.jddp.persistence.pgsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.jddp.exception.JDDPException;
import org.jddp.persistence.connection.providers.ConnectionProvider;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.StringExpression;
import org.jddp.persistence.sample.SampleExtension;
import org.jddp.persistence.sql.ResultSet;
import org.jddp.persistence.sql.select.SelectDetached;
import org.junit.Test;

import optional.packge._Sample;
import optional.packge._Sample2;

public class DMLTest extends BaseTest {

	
	@Test
	public void testRetrieveByKey() {
		SampleExtension se = dml.retrieveByKey(con, key1);
		assertEquals(se.getPkey(), key1.toString());
	}
	
	@Test
	public void testRetrieve() {
		List<SampleExtension> ses = dml.retrieve(con, Arrays.asList(key1.toString(), key2));
		assertEquals(2, ses.size());
	}
	
	@Test
	public void testInsert() {
		UUID key = UUID.randomUUID();
		SampleExtension s1 = createSample(
				key, 
				"sample2", 
				true, 
				numbers1.get(1), 
				zdt2, 
				strings1, 
				numbers1,
				byteArray2
		);
		
		//insert
		dml.insert(con, s1);
		assertTrue(dml.isExistsByKey(con, key));
		List<SampleExtension> l = dml.select().where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(1, l.size());
		assertEquals(s1, l.get(0));
		
		
		try {
			dml.insert(con, s1);
			fail("Expected JDDPException did not occur");
		} catch (JDDPException e) {
			ConnectionProvider.rollbackQuietly(con);
		}
		
		dml.delete(con, s1);
	}
	
	@Test
	public void testUpdatePrimaryKey() {
		UUID key = UUID.randomUUID();
		SampleExtension s1 = createSample(
				key, 
				"sample2", 
				true, 
				numbers1.get(1), 
				zdt2, 
				strings1, 
				numbers1,
				byteArray2
		);
		
		//insert
		dml.insert(con, s1);
		assertTrue(dml.isExistsByKey(con, key));
		List<SampleExtension> l = dml.select().where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(1, l.size());
		assertEquals(s1, l.get(0));
		
		//Treat UUIDFieldExpressions as StringFieldExpression
		try {
			dml.update().set(_Sample.$pkey, UUID.randomUUID().toString()).where(_Sample.$pkey.eq(key)).create().execute(con);
			fail("Expected JDDPException did not occur");
		} catch (JDDPException e) {
			ConnectionProvider.rollbackQuietly(con);
		}
		
		//Treat UUIDFieldExpressions as UUIDFieldExpression
		try {
			dml.update().set(_Sample.$pkey, UUID.randomUUID()).where(_Sample.$pkey.eq(key)).create().execute(con);
			fail("Expected JDDPException did not occur");
		} catch (JDDPException e) {
			ConnectionProvider.rollbackQuietly(con);
		}
		
		dml.delete(con, s1);
	}
	
	
	@Test
	public void testUpdateIndex() {
		UUID key = UUID.randomUUID();
		SampleExtension s1 = createSample(
				key, 
				"sample2", 
				true, 
				numbers1.get(1), 
				zdt2, 
				strings1, 
				numbers1,
				byteArray2
		);
		
		//insert
		dml.insert(con, s1);
		assertTrue(dml.isExistsByKey(con, key));
		List<SampleExtension> l = dml.select().where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(1, l.size());
		assertEquals(s1, l.get(0));
		
		
		try {
			dml.update().set(_Sample.$boolean, true).where(_Sample.$pkey.eq(key)).create().execute(con);
			fail("Expected JDDPException did not occur");
		} catch (JDDPException e) {
			ConnectionProvider.rollbackQuietly(con);
		}
		
		dml.delete(con, s1);
	}
	
	@Test
	public void testByteArray() {
		UUID key = UUID.randomUUID();
		SampleExtension s1 = createSample(
				key, 
				"sample2", 
				true, 
				numbers1.get(1), 
				zdt2, 
				strings1, 
				numbers1,
				byteArray2
		);
		
		//upsert
		dml.upsert(con, s1);
		assertTrue(dml.isExistsByKey(con, key));
		List<SampleExtension> l = dml.select().where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(1, l.size());
		assertEquals(s1, l.get(0));
		
		
		
		dml.update().set(_Sample.byteArray, "rodel".getBytes()).where(_Sample.$pkey.eq(key)).create().execute(con);
		
		ResultSet rs = dml.
				select(
					_Sample.byteArray).
				where(
					_Sample.$pkey.eq(key)
				).create().execute(con);
		
		assertEquals("rodel", new String(rs.getResultAt(0, 0, byte[].class)));
		
		
		dml.update().set(_Sample.byteArray, new byte[] {}).where(_Sample.$pkey.eq(key)).create().execute(con);
		
		rs = dml.
				select(
					_Sample.byteArray).
				where(
					_Sample.$pkey.eq(key)
				).create().execute(con);
		
		assertEquals("", new String(rs.getResultAt(0, 0, byte[].class)));
		
		
		dml.update().set(_Sample.byteArray, _Sample.newObject("viado".getBytes())).where(_Sample.$pkey.eq(key)).create().execute(con);
		
		rs = dml.
				select(
					_Sample.byteArray).
				where(
					_Sample.$pkey.eq(key)
				).create().execute(con);
		
		assertEquals("viado", new String(rs.getResultAt(0, 0, byte[].class)));
		
		//try setting it as a Collection
		try {
			dml.update().set(_Sample.byteArray, Collections.EMPTY_LIST).where(_Sample.$pkey.eq(key)).create().execute(con);
			fail("Expected exception was not triggered to disallow setting of byte array to a collection");
		} catch (JDDPException e) {
			ConnectionProvider.rollbackQuietly(con);
		}
		
		//try setting it a a literal array
		try {
			ObjectExpression<?> literalArray = _Sample.newObject(Collections.EMPTY_LIST).unBoundVariables();
			dml.update().set(_Sample.byteArray, literalArray).where(_Sample.$pkey.eq(key)).create().execute(con);
			fail("Expected exception was not triggered to disallow setting of byte array to collection");
		} catch (JDDPException e) {
			ConnectionProvider.rollbackQuietly(con);
		}
		
		
		//try setting it as an ObjectExpression Collection 
		try {
			ObjectExpression<?> array = _Sample.newObject(Collections.EMPTY_LIST);
			dml.update().set(_Sample.byteArray, array).where(_Sample.$pkey.eq(key)).create().execute(con);
			fail("Expected exception was not triggered to disallow setting of byte array to collection");
		} catch (JDDPException e) {
			ConnectionProvider.rollbackQuietly(con);
		}
		
		//try setting it as an ObjectExpression with an Object Collection 
		try {
			ObjectExpression<?> array = _Sample.newObject((Object) Collections.EMPTY_LIST);
			dml.update().set(_Sample.byteArray, array).where(_Sample.$pkey.eq(key)).create().execute(con);
			fail("Expected exception was not triggered to disallow setting of byte array to collection");
		} catch (JDDPException e) {
			ConnectionProvider.rollbackQuietly(con);
		}
		
		//try setting with a StringExpression 
		try {
			StringExpression<?> nonArray = _Sample.newString("");
			dml.update().set(_Sample.byteArray, nonArray).where(_Sample.$pkey.eq(key)).create().execute(con);
			fail("Expected exception was not triggered to disallow setting  of byte array to a non-byte array/collection");
		} catch (JDDPException e) {
			ConnectionProvider.rollbackQuietly(con);
		}
	}
	
	@Test
	public void testUpsert() {
		UUID key = UUID.randomUUID();
		SampleExtension s1 = createSample(
				key, 
				"sample2", 
				true, 
				numbers1.get(1), 
				zdt2, 
				strings1, 
				numbers1,
				byteArray2
		);
		
		//upsert
		dml.upsert(con, s1);
		assertTrue(dml.isExistsByKey(con, key));
		List<SampleExtension> l = dml.select().where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(1, l.size());
		assertEquals(s1, l.get(0));
		
		
		s1.setByteArray("must not fail".getBytes());
		dml.upsert(con, s1);
		
		assertTrue(dml.isExistsByKey(con, key));
		l = dml.select().where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(1, l.size());
		assertEquals(s1, l.get(0));
		
		dml.delete(con, s1);
	}
	
	@Test
	public void testDML() {
		UUID key = UUID.randomUUID();
		SampleExtension s1 = createSample(
				key, 
				"sample2", 
				true, 
				numbers1.get(1), 
				zdt2, 
				strings1, 
				numbers1,
				byteArray2
		);
		
		//insert
		dml.insert(con, s1);
		assertTrue(dml.isExistsByKey(con, key));
		List<SampleExtension> l = dml.select().where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(1, l.size());
		assertEquals(s1, l.get(0));
		
		//update by entity
		SampleExtension updated = l.get(0);
		updated.setNumber(10000);
		dml.update(con, updated);
		l = dml.select().where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(updated, l.get(0));
		
		Long c = dml.select().where(_Sample.$pkey.eq(key)).create().executeCount(con);
		
		assertTrue(c != null);
		assertEquals(1L, c.longValue());
		
		//update by field
		List<Boolean> booleans = Arrays.asList(true, false,false);
		Long count = dml.
			update().
				set(_Sample.byteArray, "1234".getBytes()).
				set(_Sample.booleans, booleans).
			where(
				_Sample.$pkey.eq(key)
			).create().execute(con);
		
		assertEquals(new Long(1), count);
					
		ResultSet rs = dml.
				select(
						_Sample.byteArray, 
						_Sample.booleans).
				where(
						_Sample.$pkey.eq(key)
				).create().execute(con);
		
		assertEquals("1234", new String(rs.getResultAt(0, 0, byte[].class)));
		assertEquals(booleans, rs.getResultAt(0, 1, List.class));
		
		//set to an empty list using unset
		count = dml.
				update().
					unset(_Sample.booleans).
				where(
					_Sample.$pkey.eq(key)
				).create().execute(con);
		
		assertEquals(new Long(1), count);
		rs = dml.select(_Sample.booleans).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertTrue(rs.getResultAt(0, 0, List.class).isEmpty());
		
		//set it again
		count = dml.update().set(_Sample.booleans, booleans).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(new Long(1), count);
		rs = dml.select( _Sample.booleans).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(booleans, rs.getResultAt(0, 0, List.class));
		
		//set an element in the array using a field and boolean values
		count = dml.
			update().
				set(_Sample.booleans(0), _Sample.booleans(1)).
				set(_Sample.booleans(1), true).
				set(_Sample.booleans(2), true).
				set(_Sample.booleans(3), false).
			where(
				_Sample.$pkey.eq(key)
			).create().execute(con);
		assertEquals(new Long(1), count);
		rs = dml.select( _Sample.booleans).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(Arrays.asList(booleans.get(1), true, true, false), rs.getResultAt(0, 0, List.class));
		
		//unset elements in the array
		//note that elements are removed in place and shifted when they are removed, reason why index is 0
		count = dml.update().
				unset(_Sample.booleans(0)).
				unset(_Sample.booleans(0)).
				set(_Sample.booleans(0), _Sample.newBoolean(null)).
				unset(_Sample.booleans(0)).
				where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(new Long(1), count);
		rs = dml.select( _Sample.booleans).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertTrue(rs.getResultAt(0, 0, List.class).isEmpty());
		
		//set it again
		count = dml.update().set(_Sample.booleans, booleans).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(new Long(1), count);
		rs = dml.select( _Sample.booleans).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(booleans, rs.getResultAt(0, 0, List.class));
				
		//set to an empty list using set
		count = dml.update().set(_Sample.booleans, Collections.EMPTY_LIST).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(new Long(1), count);
		rs = dml.select(_Sample.booleans).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertTrue(rs.getResultAt(0, 0, List.class).isEmpty());
		
		//set using a primitve array
		count = dml.update().set(_Sample.booleans, new Boolean[] {true,false} ).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(new Long(1), count);
		rs = dml.select(_Sample.booleans).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(rs.getResultAt(0, 0, List.class).size(), 2);
		assertTrue((Boolean) rs.getResultAt(0, 0, List.class).get(0));

		//unset a a byte array
		count = dml.update().unset(_Sample.byteArray).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(new Long(1), count);
		rs = dml.select(_Sample.byteArray).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertTrue(rs.getResultAt(0, 0) == null);
		
		//set the  byte array 
		count = dml.update().set(_Sample.byteArray, "test".getBytes()).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(new Long(1), count);
		rs = dml.select(_Sample.byteArray).where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals("test", new String(rs.getResultAt(0, 0, byte[].class)));
		
		//delete
		count = dml.delete().where(_Sample.$pkey.eq(key)).create().execute(con);
		assertEquals(new Long(1), count);
		assertTrue(!dml.isExistsByKey(con, key));
	}
	
	@Test
	public void testSelectFromAnotherEntity() {
		SelectDetached<ResultSet> d = dml2.select(_Sample2.booleans(0).and(true)).limit(1).create()
				.parenthesize();
		ResultSet rs = dml.select(d).where(_Sample.$pkey.eq(key1).and(d.asBoolean())).create().execute(con);
		assertEquals(Boolean.class, rs.getResultAt(0, 0).getClass());
	}
	
	@Test
	public void testSelectFromAsArg() {
		SelectDetached<ResultSet> d = dml2.select(_Sample2.booleans(0)).limit(1).create()
				.parenthesize();
		ResultSet rs = dml.select(d).where(_Sample.$pkey.eq(key1).in(d)).create().execute(con);
		assertEquals(Boolean.class, rs.getResultAt(0, 0).getClass());
		assertEquals(true, rs.getResultAt(0, 0, Boolean.class));
	}
	
	@Test
	public void testSelectFromAsArg2() {
		SelectDetached<ResultSet> d1 = dml2.select(_Sample2.booleans(0)).limit(1).create()
				.parenthesize();
		
		SelectDetached<ResultSet> d2 = dml2.select(_Sample2._boolean).create()
				.parenthesize();
		ResultSet rs = dml.select(d1.asBoolean().notIn(d2)).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(Boolean.class, rs.getResultAt(0, 0).getClass());
		assertEquals(false, rs.getResultAt(0, 0, Boolean.class));
	}
	
	@Test
	public void testExpressionSubSelect() {
		SelectDetached<ResultSet> sub = dml.select(_Sample.$pkey).where(_Sample.$pkey.eq(key1)).create();
		
		ResultSet rs = dml.select(_Sample.pkey.count()).where(_Sample.$pkey.in(sub)).create().execute(con);
		
		assertEquals(Long.class, rs.getResultAt(0, 0).getClass());
		assertEquals(new Long(1), rs.getResultAt(0, 0, Long.class));
	}
	
	
	@Test
	public void testFromSubSelect() {
				
		ResultSet rs = dml.
				select(
					_Sample.newLiteral("viado").qualify("rodel")).
				from(
					dml.select(_Sample.$pkey.alias("viado")).where(_Sample.$pkey.eq(key1)).create().parenthesize().alias("rodel")
				).
				create().execute(con);
		
		assertEquals(UUID.class, rs.getResultAt(0, 0).getClass());
		assertEquals(key1, rs.getResultAt(0, 0, Long.class));
	}

}
