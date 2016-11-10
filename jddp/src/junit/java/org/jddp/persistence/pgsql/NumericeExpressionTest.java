package org.jddp.persistence.pgsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.sql.ResultSet;
import org.junit.Test;

import optional.packge._Sample;


public class NumericeExpressionTest extends BaseTest {
	
	
	
	@Test
	public void testNumericPlus() {
		NumericExpression<?> e = _Sample.newNumber(123).plus(456).plus(-789);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(3, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		String name3 = i.next().getName();

		i = p.iterator();
		
		assertEquals(123, i.next().getValue());
		assertEquals(456, i.next().getValue());
		assertEquals(-789, i.next().getValue());

	    assertEquals("(:" + name1 + " + :" +  name2 + ") + :" + name3, e.toString());
	    assertEquals("(123 + 456) + -789", e.unBoundVariables().toString());
	    
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(new BigDecimal((123 + 456) + -789), rs.getResultAt(0, 0)); 
		
	}
	
	@Test
	public void testNumericMinus() {
		NumericExpression<?> e = _Sample.newNumber(123).minus(456).minus(-789);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(3, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		String name3 = i.next().getName();

		i = p.iterator();
		
		assertEquals(123, i.next().getValue());
		assertEquals(456, i.next().getValue());
		assertEquals(-789, i.next().getValue());

	    assertEquals("(:" + name1 + " - :" +  name2 + ") - :" + name3, e.toString());
	    assertEquals("(123 - 456) - -789", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(new BigDecimal((123 - 456) - -789), rs.getResultAt(0, 0)); 
		
	}
	@Test
	public void testNumericMul() {
		NumericExpression<?> e = _Sample.newNumber(123).mul(456).mul(-789);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(3, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		String name3 = i.next().getName();

		i = p.iterator();
		
		assertEquals(123, i.next().getValue());
		assertEquals(456, i.next().getValue());
		assertEquals(-789, i.next().getValue());

	    assertEquals("(:" + name1 + " * :" +  name2 + ") * :" + name3, e.toString());
	    assertEquals("(123 * 456) * -789", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(new BigDecimal((123 * 456) * -789), rs.getResultAt(0, 0)); 
		
	}
	
	@Test
	public void testNumericDiv() {
		NumericExpression<?> e = _Sample.newNumber(123d).div(456d).div(-789d);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(3, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		String name3 = i.next().getName();

		i = p.iterator();
		
		assertEquals(123d, i.next().getValue());
		assertEquals(456d, i.next().getValue());
		assertEquals(-789d, i.next().getValue());

	    assertEquals("(:" + name1 + " / :" +  name2 + ") / :" + name3, e.toString());
	    assertEquals("(123.0 / 456.0) / -789.0", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    BigDecimal diff = new BigDecimal(((123d / 456d) / -789d)).subtract(rs.getResultAt(0, 0, BigDecimal.class));
	    assertTrue(diff.doubleValue() < 0.0000000000000001d); 
		
	}
	
	@Test
	public void testNumericEQ() {
		BooleanExpression<?> e = _Sample.newNumber(123d).eq(456d);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals(123d, i.next().getValue());
		assertEquals(456d, i.next().getValue());
		

	    assertEquals(":" + name1 + " = :" +  name2, e.toString());
	    assertEquals("123.0 = 456.0", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, (Boolean) rs.getResultAt(0, 0)); 
	    
	    BooleanExpression<?> e2 = _Sample.newNumber(123d).eq(123d);
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0, Boolean.class)); 
		
	}
	
	@Test
	public void testNumericNEQ() {
		BooleanExpression<?> e = _Sample.newNumber(123d).neq(456d);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals(123d, i.next().getValue());
		assertEquals(456d, i.next().getValue());
		

	    assertEquals(":" + name1 + " <> :" +  name2, e.toString());
	    assertEquals("123.0 <> 456.0", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0, Boolean.class)); 
	    
	    BooleanExpression<?> e2 = _Sample.newNumber(123d).neq(123d);
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0, Boolean.class)); 
		
	}
	
	
	@Test
	public void testNumericGT() {
		BooleanExpression<?> e = _Sample.newNumber(123d).gt(456d);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals(123d, i.next().getValue());
		assertEquals(456d, i.next().getValue());
		

	    assertEquals(":" + name1 + " > :" +  name2, e.toString());
	    assertEquals("123.0 > 456.0", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0, Boolean.class)); 
	    
	    BooleanExpression<?> e2 = _Sample.newNumber(1234d).gt(123d);
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0, Boolean.class)); 
		
	}
	
	@Test
	public void testNumericGTE() {
		BooleanExpression<?> e = _Sample.newNumber(123d).gte(456d);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals(123d, i.next().getValue());
		assertEquals(456d, i.next().getValue());
		

	    assertEquals(":" + name1 + " >= :" +  name2, e.toString());
	    assertEquals("123.0 >= 456.0", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0, Boolean.class)); 
	    
	    BooleanExpression<?> e2 = _Sample.newNumber(123d).gte(123d);
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0, Boolean.class)); 
		
	}
	
	@Test
	public void testNumericLT() {
		BooleanExpression<?> e = _Sample.newNumber(123d).lt(456d);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals(123d, i.next().getValue());
		assertEquals(456d, i.next().getValue());
		

	    assertEquals(":" + name1 + " < :" +  name2, e.toString());
	    assertEquals("123.0 < 456.0", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0, Boolean.class)); 
	    
	    BooleanExpression<?> e2 = _Sample.newNumber(123d).lt(123d);
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0, Boolean.class)); 
		
	}
	
	@Test
	public void testNumericLTE() {
		BooleanExpression<?> e = _Sample.newNumber(123d).lte(456d);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals(123d, i.next().getValue());
		assertEquals(456d, i.next().getValue());
		

	    assertEquals(":" + name1 + " <= :" +  name2, e.toString());
	    assertEquals("123.0 <= 456.0", e.unBoundVariables().toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0, Boolean.class)); 
	    
	    BooleanExpression<?> e2 = _Sample.newNumber(123d).lte(123d);
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0, Boolean.class)); 
		
	}
	
	@Test
	public void testNumericAvg() {
		NumericExpression<?> e = _Sample.$number.avg();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
	    assertEquals("AVG(number)", e.toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    BigDecimal diff = (rs.getResultAt(0, 0, BigDecimal.class)).subtract(new BigDecimal(average));
	    assertTrue(diff.doubleValue() < 0.0000000000000001d);
		
	}
	
	
	@Test
	public void testNumericIsNull() {
		BooleanExpression<?> e = _Sample.$number.isNull();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
	    assertEquals("number IS NULL", e.toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).limit(1).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0, Boolean.class)); 
	    
		
	}
	
	@Test
	public void testNumericIsNotNull() {
		BooleanExpression<?> e = _Sample.$number.isNotNull();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
	    assertEquals("number IS NOT NULL", e.toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).limit(1).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0, Boolean.class)); 
	    
		
	}
	
	@Test
	public void testNumericSum() {
		NumericExpression<?> e = _Sample.$number.sum();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
	    assertEquals("SUM(number)", e.toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    BigDecimal diff = (rs.getResultAt(0, 0, BigDecimal.class)).subtract(new BigDecimal(sum));
	    assertTrue(diff.doubleValue() < 0.0000000000000001d);
		
	}
	
	@Test
	public void testNumericMin() {
		NumericExpression<?> e = _Sample.$number.min();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
	    assertEquals("MIN(number)", e.toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    BigDecimal diff = (rs.getResultAt(0, 0, BigDecimal.class)).subtract(new BigDecimal(numbers1.get(1).toString()));
	    assertTrue(diff.doubleValue() < 0.0000000000000001d);
		
	}
	
	@Test
	public void testNumericMax() {
		NumericExpression<?> e = _Sample.$number.max();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
	    assertEquals("MAX(number)", e.toString());
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    BigDecimal diff = (rs.getResultAt(0, 0, BigDecimal.class)).subtract(new BigDecimal(numbers1.get(0).toString()));
	    assertTrue(diff.doubleValue() < 0.0000000000000001d);
		
	}
	
	@Test
	public void testInNumeric() {
		
		List<Number> c = Arrays.asList(456, 789, -123.0);
		
		NumericExpression<?> e = _Sample.newNumber(123);
		NumericExpression<?> e1 = _Sample.newNumber(-123);
		
		assertEquals("123", e.unBoundVariables().toString());
		
		BooleanExpression<?> in = e.in(c);
		BooleanExpression<?> in1 = e1.in(c);
		
		Set<VariableExpression> p = in.getBoundVariables();
		
		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		
		i = p.iterator();
		
		assertEquals(123, i.next().getValue());
		int idx = 0;
		for (Object actual : (Collection<?>) i.next().getValue()) {
			assertEquals(c.get(idx++), actual);
		}
		
		BooleanExpression<?> lit = in.unBoundVariables();
		
		assertEquals(":" + name1 + " = ANY(:" +  name2 + ")", in.toString());
		assertEquals("123 = ANY($${456, 789, -123.0}$$)", lit.toString());
		assertTrue(lit.getBoundVariables().isEmpty());
		
		ResultSet rs = dml.select(in).limit(1).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	    rs = dml.select(in1).limit(1).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testInMixedNumeric() {
		
		NumericExpression<?> ne = _Sample.newNumber(456).plus(_Sample.number);
		
		List<Object> c = Arrays.asList(456,789, null, -123.0, ne);
		
		NumericExpression<?> e = _Sample.newNumber(123);
		
		assertEquals("123", e.unBoundVariables().toString());
		
		BooleanExpression<?> in = e.in(c);
		
		Set<VariableExpression> p = in.getBoundVariables();
		
		assertEquals(3, p.size());
		
		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		String name3 = i.next().getName();
		
		
		i = p.iterator();
		
		assertEquals(123, i.next().getValue());
		int idx = 0;
		for (Object actual : (Collection<?>) i.next().getValue()) {
			if (c.get(idx) != null) {
				assertEquals(c.get(idx++), actual);
			}	
		}
		
		BooleanExpression<?> lit = in.unBoundVariables();
		
		assertEquals("((:" + name1 + " = ANY(:" +  name2 + ")) OR (:" + name1 + " = (:" + name3 + " + " + _Sample.number + "))) OR (:" + name1 + " IS NULL)", in.toString());
		assertEquals("((123 = ANY($${456, 789, -123.0}$$)) OR (123 = (456 + " + _Sample.number + "))) OR (" + 123 + " IS NULL)", lit.toString());
		assertTrue(lit.getBoundVariables().isEmpty());
		
		ResultSet rs = dml.select(in).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
		
	}
	
	@Test
	public void testNotInMixedNumeric() {
		
		NumericExpression<?> ne = _Sample.newNumber(456).plus(_Sample.number);
		
		List<Object> c = Arrays.asList(456,789, null, -123.0, ne);
		
		NumericExpression<?> e = _Sample.newNumber(123);
		
		assertEquals("123", e.unBoundVariables().toString());
		
		BooleanExpression<?> notIN = e.notIn(c);
		
		Set<VariableExpression> p = notIN.getBoundVariables();
		
		assertEquals(3, p.size());
		
		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		String name3 = i.next().getName();
		
		
		i = p.iterator();
		
		assertEquals(123, i.next().getValue());
		int idx = 0;
		for (Object actual : (Collection<?>) i.next().getValue()) {
			if (c.get(idx) != null) {
				assertEquals(c.get(idx++), actual);
			}	
		}
		
		BooleanExpression<?> lit = notIN.unBoundVariables();
		
		assertEquals("((:" + name1 + " != ALL(:" +  name2 + ")) AND (:" + name1 + " <> (:" + name3 + " + " + _Sample.number + "))) AND (:" + name1 + " IS NOT NULL)", notIN.toString());
		assertEquals("((123 != ALL($${456, 789, -123.0}$$)) AND (123 <> (456 + " + _Sample.number + "))) AND (" + 123 + " IS NOT NULL)", lit.toString());
		assertTrue(lit.getBoundVariables().isEmpty());
		
		ResultSet rs = dml.select(notIN).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
		
	}
	
	@Test
	public void testNegateInMixedNumeric() {
		
		NumericExpression<?> ne = _Sample.newNumber(-210).plus(_Sample.number.negate());
		
		List<Object> c = Arrays.asList(234, 789, null, -123.0, ne);
		
		NumericExpression<?> e = _Sample.newNumber(123);
		
		assertEquals("123", e.unBoundVariables().toString());
		
		BooleanExpression<?> in = e.in(c);
		
		Set<VariableExpression> p = in.getBoundVariables();
		
		assertEquals(3, p.size());
		
		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		String name3 = i.next().getName();
		
		
		i = p.iterator();
		
		assertEquals(123, i.next().getValue());
		int idx = 0;
		for (Object actual : (Collection<?>) i.next().getValue()) {
			if (c.get(idx) != null) {
				assertEquals(c.get(idx++), actual);
			}	
		}
		
		BooleanExpression<?> lit = in.unBoundVariables();
		
		assertEquals("((:" + name1 + " = ANY(:" +  name2 + ")) OR (:" + name1 + " = (:" + name3 + " + (- " + _Sample.number + ")))) OR (:" + name1 + " IS NULL)", in.toString());
		assertEquals("((123 = ANY($${234, 789, -123.0}$$)) OR (123 = (-210 + (- " + _Sample.number + ")))) OR (" + 123 + " IS NULL)", lit.toString());
		assertTrue(lit.getBoundVariables().isEmpty());
		
		ResultSet rs = dml.select(in).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
		
	}


}
