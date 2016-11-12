package org.jddp.persistence.pgsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.sql.ResultSet;
import org.junit.Test;

import optional.packge._Sample;


public class UUIDExpressionTest extends BaseTest {

	@Test
	public void testRetrieveUUID() {
	    ResultSet rs = dml.select(_Sample.$pkey).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(rs.getResultAt(0, 0).getClass(), UUID.class);
	    assertEquals(key1, rs.getResultAt(0, 0));
	}
	
	
	@Test
	public void testUUIDEQ() {
		
		BooleanExpression<?> e = _Sample.$pkey.eq(key1);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());
		
	    assertEquals(_Sample.$pkey + " = CAST(:" + p.iterator().next().getName() + " as uuid)", e.toString());
	    assertEquals(_Sample.$pkey + " = CAST($$" + key1 + "$$ as uuid)", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    e = _Sample.$pkey.eq(key1.toString());
	    
	    rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testUUIDNEQ() {
		
		BooleanExpression<?> e = _Sample.$pkey.neq(key1);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());
		
	    assertEquals(_Sample.$pkey + " <> CAST(:" + p.iterator().next().getName() + " as uuid)", e.toString());
	    assertEquals(_Sample.$pkey + " <> CAST($$" + key1 + "$$ as uuid)", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	    e = _Sample.$pkey.neq(key1.toString());
	    
	    rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testUUIDGT() {
		
		BooleanExpression<?> e = _Sample.$pkey.gt(key1);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());
		
		assertEquals(_Sample.$pkey + " > CAST(:" + p.iterator().next().getName() + " as uuid)", e.toString());
	    assertEquals(_Sample.$pkey + " > CAST($$" + key1 + "$$ as uuid)", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	    e = _Sample.$pkey.gt(key1.toString());
	    
	    rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testUUIDGTE() {
		
		BooleanExpression<?> e = _Sample.$pkey.gte(key1);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());
		
		assertEquals(_Sample.$pkey + " >= CAST(:" + p.iterator().next().getName() + " as uuid)", e.toString());
		
	    assertEquals(_Sample.$pkey + " >= CAST($$" + key1 + "$$ as uuid)", e.unBoundVariables().toString());
	    
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    e = _Sample.$pkey.gte(key1.toString());
	    
	    rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testUUIDLT() {
		
		BooleanExpression<?> e = _Sample.$pkey.lt(key1);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());
		
		assertEquals(_Sample.$pkey + " < CAST(:" + p.iterator().next().getName() + " as uuid)", e.toString());
	    assertEquals(_Sample.$pkey + " < CAST($$" + key1 + "$$ as uuid)", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	    e = _Sample.$pkey.lt(key1.toString());
	    
	    rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testUUIDLTE() {
		
		BooleanExpression<?> e = _Sample.$pkey.lte(key1);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());
		
		assertEquals(_Sample.$pkey + " <= CAST(:" + p.iterator().next().getName() + " as uuid)", e.toString());
	    assertEquals(_Sample.$pkey + " <= CAST($$" + key1 + "$$ as uuid)", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    e = _Sample.$pkey.lte(key1.toString());
	    
	    rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testUUIDInIgnoreCase() {
		
		UUID uuid1 = key1;
		UUID uuid2 = UUID.randomUUID();
		
		List<Object> utcs = Arrays.asList(uuid1, uuid2.toString(), _Sample.$pkey);
				
		BooleanExpression<?> e = _Sample.$pkey.inIgnoreCase(utcs);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		VariableExpression var = i.next();
		
		String name1 = var.getName();
		Object value = var.getValue();
		
		assertTrue(value instanceof Collection);

		Iterator<?> vi = ((Collection<?>) value).iterator();
		int idx = 0;
		while (vi.hasNext()) {
			assertEquals(utcs.get(idx++).toString(), vi.next());
		}
		
	    assertEquals("(" + _Sample.$pkey + " = ANY(:" + name1 +  ")) OR (" + _Sample.$pkey + " = " + _Sample.$pkey + ")", e.toString());
	    assertEquals("(" + _Sample.$pkey + " = ANY($${" + utcs.get(0) +  ", "  +  utcs.get(1) + "}$$)) OR (" + _Sample.$pkey + " = " + _Sample.$pkey + ")", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    rs = dml.select(e.unBoundVariables()).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	}
	
	
	
}
