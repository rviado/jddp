package org.jddp.persistence.pgsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.sql.ResultSet;
import org.junit.Test;

import optional.packge._Sample;


public class BooleanExpressionTest extends BaseTest {

	@Test
	public void testBooleanAndOr() {
		BooleanExpression<?> e = _Sample.newBoolean(true).or(false).and(true);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(3, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		String name3 = i.next().getName();

		i = p.iterator();
		
		assertEquals(true, i.next().getValue());
		assertEquals(false, i.next().getValue());
		assertEquals(true, i.next().getValue());

	    assertEquals("(:" + name1 + " OR :" +  name2 + ") AND :" + name3, e.toString());
	    assertEquals("(true OR false) AND true", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0)); 
	    
	}

	@Test
	public void testInBoolean() {
		BooleanExpression<?> e = _Sample.newBoolean(true);
		assertEquals("true", e.unBoundVariables().toString());
		assertEquals("true = ANY($${true, false, true}$$)", e.in(Arrays.asList(true,false,true)).unBoundVariables().toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0)); 
	}

	

	@Test
	public void testAndBoolean() {
		//To test: one parameter is variable true and one as a Boolean expression false 
		BooleanExpression<?> e = _Sample.newBoolean(true).and(_Sample.newBoolean(false));
		Set<VariableExpression> p = e.getBoundVariables();

		//test correct number of variables
		assertEquals(2, p.size());
		Iterator<VariableExpression> iter = p.iterator();
		VariableExpression ve1 = iter.next();
		VariableExpression ve2 = iter.next();

		//test type and value
		assertEquals(Boolean.class, ve1.getType());
		assertEquals(true, ve1.getValue());

		//test type and value
		assertEquals(Boolean.class, ve2.getType());
		assertEquals(false, ve2.getValue());

		//test name clash
		assertTrue(!ve1.getName().equals(ve2.getName()));
		
		//test named parameters
		assertEquals(":" + ve1.getName() + " AND :" +  ve2.getName(), e.toString());
		
		//test literal
		assertEquals("true AND false", e.unBoundVariables().toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0)); 
	}
	
	@Test
	public void testOrBoolean() {
		//To test: one parameter is variable true and one as a Boolean expression false 
		BooleanExpression<?> e = _Sample.newBoolean(true).or(_Sample.newBoolean(false));
		Set<VariableExpression> p = e.getBoundVariables();

		//test correct number of variables
		assertEquals(2, p.size());
		Iterator<VariableExpression> iter = p.iterator();
		VariableExpression ve1 = iter.next();
		VariableExpression ve2 = iter.next();

		//test type and value
		assertEquals(Boolean.class, ve1.getType());
		assertEquals(true, ve1.getValue());

		//test type and value
		assertEquals(Boolean.class, ve2.getType());
		assertEquals(false, ve2.getValue());

		//test name clash
		assertTrue(!ve1.getName().equals(ve2.getName()));
		
		//test named parameters
		assertEquals(":" + ve1.getName() + " OR :" +  ve2.getName(), e.toString());
		
		//test literal
		assertEquals("true OR false", e.unBoundVariables().toString());
		
		 ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		 assertEquals(true, rs.getResultAt(0, 0)); 
	}
	
	@Test
	public void testEqualBoolean() {
		//To test: one parameter is variable true and one as a Boolean expression false 
		BooleanExpression<?> e = _Sample.newBoolean(true).eq(_Sample.newBoolean(false));
		Set<VariableExpression> p = e.getBoundVariables();

		//test correct number of variables
		assertEquals(2, p.size());
		Iterator<VariableExpression> iter = p.iterator();
		VariableExpression ve1 = iter.next();
		VariableExpression ve2 = iter.next();

		//test type and value
		assertEquals(Boolean.class, ve1.getType());
		assertEquals(true, ve1.getValue());

		//test type and value
		assertEquals(Boolean.class, ve2.getType());
		assertEquals(false, ve2.getValue());

		//test name clash
		assertTrue(!ve1.getName().equals(ve2.getName()));
		
		//test named parameters
		assertEquals(":" + ve1.getName() + " = :" +  ve2.getName(), e.toString());
		
		//test literal
		assertEquals("true = false", e.unBoundVariables().toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0)); 
	}
	
	@Test
	public void testGTBoolean() {
		//To test: one parameter is variable true and one as a Boolean expression false 
		BooleanExpression<?> e = _Sample.newBoolean(true).gt(_Sample.newBoolean(false));
		Set<VariableExpression> p = e.getBoundVariables();

		//test correct number of variables
		assertEquals(2, p.size());
		Iterator<VariableExpression> iter = p.iterator();
		VariableExpression ve1 = iter.next();
		VariableExpression ve2 = iter.next();

		//test type and value
		assertEquals(Boolean.class, ve1.getType());
		assertEquals(true, ve1.getValue());

		//test type and value
		assertEquals(Boolean.class, ve2.getType());
		assertEquals(false, ve2.getValue());

		//test name clash
		assertTrue(!ve1.getName().equals(ve2.getName()));
		
		//test named parameters
		assertEquals(":" + ve1.getName() + " > :" +  ve2.getName(), e.toString());
		
		//test literal
		assertEquals("true > false", e.unBoundVariables().toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0)); 
	}

	@Test
	public void testLTBoolean() {
		//To test: one parameter is variable true and one as a Boolean expression false 
		BooleanExpression<?> e = _Sample.newBoolean(true).lt(_Sample.newBoolean(false));
		Set<VariableExpression> p = e.getBoundVariables();

		//test correct number of variables
		assertEquals(2, p.size());
		Iterator<VariableExpression> iter = p.iterator();
		VariableExpression ve1 = iter.next();
		VariableExpression ve2 = iter.next();

		//test type and value
		assertEquals(Boolean.class, ve1.getType());
		assertEquals(true, ve1.getValue());

		//test type and value
		assertEquals(Boolean.class, ve2.getType());
		assertEquals(false, ve2.getValue());

		//test name clash
		assertTrue(!ve1.getName().equals(ve2.getName()));
		
		//test named parameters
		assertEquals(":" + ve1.getName() + " < :" +  ve2.getName(), e.toString());
		
		//test literal
		assertEquals("true < false", e.unBoundVariables().toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0)); 
	}
	
	@Test
	public void testGTEBoolean() {
		//To test: one parameter is variable true and one as a Boolean expression false 
		BooleanExpression<?> e = _Sample.newBoolean(true).gte(_Sample.newBoolean(false));
		Set<VariableExpression> p = e.getBoundVariables();

		//test correct number of variables
		assertEquals(2, p.size());
		Iterator<VariableExpression> iter = p.iterator();
		VariableExpression ve1 = iter.next();
		VariableExpression ve2 = iter.next();

		//test type and value
		assertEquals(Boolean.class, ve1.getType());
		assertEquals(true, ve1.getValue());

		//test type and value
		assertEquals(Boolean.class, ve2.getType());
		assertEquals(false, ve2.getValue());

		//test name clash
		assertTrue(!ve1.getName().equals(ve2.getName()));
		
		//test named parameters
		assertEquals(":" + ve1.getName() + " >= :" +  ve2.getName(), e.toString());
		
		//test literal
		assertEquals("true >= false", e.unBoundVariables().toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0)); 
	}
	
	@Test
	public void testLTEBoolean() {
		//To test: one parameter is variable true and one as a Boolean expression false 
		BooleanExpression<?> e = _Sample.newBoolean(true).lte(_Sample.newBoolean(false));
		Set<VariableExpression> p = e.getBoundVariables();

		//test correct number of variables
		assertEquals(2, p.size());
		Iterator<VariableExpression> iter = p.iterator();
		VariableExpression ve1 = iter.next();
		VariableExpression ve2 = iter.next();

		//test type and value
		assertEquals(Boolean.class, ve1.getType());
		assertEquals(true, ve1.getValue());

		//test type and value
		assertEquals(Boolean.class, ve2.getType());
		assertEquals(false, ve2.getValue());

		//test name clash
		assertTrue(!ve1.getName().equals(ve2.getName()));
		
		//test named parameters
		assertEquals(":" + ve1.getName() + " <= :" +  ve2.getName(), e.toString());
		
		//test literal
		assertEquals("true <= false", e.unBoundVariables().toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testNEQBoolean() {
		//To test: one parameter is variable true and one as a Boolean expression false 
		BooleanExpression<?> e = _Sample.newBoolean(true).neq(_Sample.newBoolean(false));
		Set<VariableExpression> p = e.getBoundVariables();

		//test correct number of variables
		assertEquals(2, p.size());
		Iterator<VariableExpression> iter = p.iterator();
		VariableExpression ve1 = iter.next();
		VariableExpression ve2 = iter.next();

		//test type and value
		assertEquals(Boolean.class, ve1.getType());
		assertEquals(true, ve1.getValue());

		//test type and value
		assertEquals(Boolean.class, ve2.getType());
		assertEquals(false, ve2.getValue());

		//test name clash
		assertTrue(!ve1.getName().equals(ve2.getName()));
		
		//test named parameters
		assertEquals(":" + ve1.getName() + " <> :" +  ve2.getName(), e.toString());
		
		//test literal
		assertEquals("true <> false", e.unBoundVariables().toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testAndEntityField() {
		BooleanExpression<?> e = _Sample._boolean.and(_Sample._boolean);
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		assertEquals(_Sample._boolean + " AND " +  _Sample._boolean, e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
		
	}

	@Test
	public void testOrEntityField() {
		BooleanExpression<?> e = _Sample._boolean.or(_Sample._boolean);
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		assertEquals(_Sample._boolean + " OR " +  _Sample._boolean, e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
	}

	@Test
	public void testEqualEntityField() {
		BooleanExpression<?> e = _Sample._boolean.eq(_Sample._boolean);
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		assertEquals(_Sample._boolean + " = " +  _Sample._boolean, e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testGTEntityField() {
		BooleanExpression<?> e = _Sample._boolean.gt(_Sample._boolean);
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		assertEquals(_Sample._boolean + " > " +  _Sample._boolean, e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testLTEntityField() {
		BooleanExpression<?> e = _Sample._boolean.lt(_Sample._boolean);
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		assertEquals(_Sample._boolean + " < " +  _Sample._boolean, e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testGTEEntityField() {
		BooleanExpression<?> e = _Sample._boolean.gte(_Sample._boolean);
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		assertEquals(_Sample._boolean + " >= " +  _Sample._boolean, e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testLTEEntityField() {
		BooleanExpression<?> e = _Sample._boolean.lte(_Sample._boolean);
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		assertEquals(_Sample._boolean + " <= " +  _Sample._boolean, e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testNEQEntityField() {
		BooleanExpression<?> e = _Sample._boolean.neq(_Sample._boolean);
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		System.out.println(_Sample.booleans(0));
		assertEquals(_Sample._boolean + " <> " +  _Sample._boolean, e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testINEntityField() {
		BooleanExpression<?> e = _Sample._boolean.in(Arrays.asList(true,false, true));
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(1, p.size());
		assertEquals(_Sample._boolean + " = ANY(:" + p.iterator().next().getName()  + ")", e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testNOTINEntityField() {
		BooleanExpression<?> e = _Sample._boolean.notIn(Arrays.asList(true,false, true, null));
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(1, p.size());
		assertEquals("(" + _Sample._boolean + " != ALL(:" + p.iterator().next().getName()  + ")) AND (" + _Sample._boolean + " IS NOT NULL)", e.toString());
		//e.unBoundVariables();
		
		ResultSet rs = dml.select(_Sample._boolean).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
		
		rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testNegate() {
		BooleanExpression<?> e = _Sample._boolean.or(_Sample._boolean).negate();
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		
		assertEquals("NOT(" + _Sample._boolean + " OR " +  _Sample._boolean + ")", e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testISNULL() {
		BooleanExpression<?> e = _Sample._boolean.isNull();
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		assertEquals(_Sample._boolean + " IS NULL", e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(false, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testISNOTNULL() {
		BooleanExpression<?> e = _Sample._boolean.isNotNull();
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());
		assertEquals(_Sample._boolean + " IS NOT NULL", e.toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testParenthesize() {
		BooleanExpression<?> e = _Sample._boolean.or(2 > 5).parenthesize();
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(1, p.size());
		
		assertEquals("(" + _Sample._boolean + " OR :" + p.iterator().next().getName() + ")", e.toString());
		assertEquals("(" + _Sample._boolean + " OR " + p.iterator().next().getValue() + ")", e.unBoundVariables().toString());
		
		ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0));
	}
	



}
