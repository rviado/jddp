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
import org.jddp.expression.NumericExpression;
import org.jddp.expression.StringExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.persistence.sql.ResultSet;
import org.junit.Test;

import optional.packge._Sample;


public class StringExpressionTest extends BaseTest {

	@Test
	public void testRetrieveStringInArray() {
		ResultSet rs = dml.select(_Sample.strings(0)).where(_Sample.strings(0).eq(strings1.get(0))).create().execute(con);
		assertTrue(rs.getResultAt(0, 0) instanceof String);
		assertEquals(strings1.get(0), rs.getResultAt(0, 0));
	}
	
	@Test
	public void testRetrieveStrings() {
		ResultSet rs = dml.select(_Sample.strings).where(_Sample.strings(1).eq(strings1.get(1))).create().execute(con);
		assertTrue(List.class.isAssignableFrom(rs.getResultAt(0, 0).getClass()));
		assertEquals(strings1, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testRetrieveString() {
		ResultSet rs = dml.select(_Sample.$string).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(rs.getResultAt(0, 0).getClass(), String.class);
		assertTrue(stringValue1.equals(rs.getResultAt(0, 0, String.class)));
	}
	
	@Test
	public void testRetrieveStringViaJSON() {
		ResultSet rs = dml.select(_Sample.string).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(rs.getResultAt(0, 0).getClass(), String.class);
		assertTrue(stringValue1.equals(rs.getResultAt(0, 0, String.class)));
	}
	
	@Test
	public void testStringEqualityViaJSON() {
		ResultSet rs = dml.select(_Sample.string.eq(stringValue1)).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertTrue(rs.getResultAt(0, 0, Boolean.class));
	}
	
	
	@Test
	public void testStringEQ() {
		BooleanExpression<?> e = _Sample.newString("string1").eq("string2");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals("string1", i.next().getValue());
		assertEquals("string2", i.next().getValue());

	    assertEquals(":" + name1 + " = :" +  name2, e.toString());
	    assertEquals("$$string1$$ = $$string2$$", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	    BooleanExpression<?> e2 = _Sample.newString("string1").eq("string1");
	    
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	}
	
	
	
	
	@Test
	public void testStringNEQ() {
		BooleanExpression<?> e = _Sample.newString("string1").neq("string2");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals("string1", i.next().getValue());
		assertEquals("string2", i.next().getValue());

	    assertEquals(":" + name1 + " <> :" +  name2, e.toString());
	    assertEquals("$$string1$$ <> $$string2$$", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    BooleanExpression<?> e2 = _Sample.newString("string1").neq("string1");
	    
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testStringGT() {
		BooleanExpression<?> e = _Sample.newString("string1").gt("string2");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals("string1", i.next().getValue());
		assertEquals("string2", i.next().getValue());

	    assertEquals(":" + name1 + " > :" +  name2, e.toString());
	    assertEquals("$$string1$$ > $$string2$$", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	    BooleanExpression<?> e2 = _Sample.newString("string2").gt("string1");
	    
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testStringGTE() {
		BooleanExpression<?> e = _Sample.newString("string1").gte("string2");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals("string1", i.next().getValue());
		assertEquals("string2", i.next().getValue());

	    assertEquals(":" + name1 + " >= :" +  name2, e.toString());
	    assertEquals("$$string1$$ >= $$string2$$", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	    BooleanExpression<?> e2 = _Sample.newString("string1").gte("string1");
	    
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testStringLT() {
		BooleanExpression<?> e = _Sample.newString("string1").lt("string2");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals("string1", i.next().getValue());
		assertEquals("string2", i.next().getValue());

	    assertEquals(":" + name1 + " < :" +  name2, e.toString());
	    assertEquals("$$string1$$ < $$string2$$", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    BooleanExpression<?> e2 = _Sample.newString("string1").lt("string1");
	    
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	}
	
	
	@Test
	public void testStringLTE() {
		BooleanExpression<?> e = _Sample.newString("string1").lte("string2");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals("string1", i.next().getValue());
		assertEquals("string2", i.next().getValue());

	    assertEquals(":" + name1 + " <= :" +  name2, e.toString());
	    assertEquals("$$string1$$ <= $$string2$$", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    BooleanExpression<?> e2 = _Sample.newString("string1").lte("string1");
	    
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testStringEQIgnoreCase() {
		BooleanExpression<?> e = _Sample.newString("string1").eqIgnoreCase("String1");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals("string1", i.next().getValue());
		assertEquals("string1", i.next().getValue());

	    assertEquals("LOWER(:" + name1 + ") = :" +  name2, e.toString());
	    assertEquals("LOWER($$string1$$) = $$string1$$", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    BooleanExpression<?> e2 = _Sample.newString("string1").eqIgnoreCase("STRING1");
	    
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testStringNEQIgnoreCase() {
		BooleanExpression<?> e = _Sample.newString("string1").neqIgnoreCase("String1");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(2, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		String name2 = i.next().getName();
		

		i = p.iterator();
		
		assertEquals("string1", i.next().getValue());
		assertEquals("string1", i.next().getValue());

	    assertEquals("LOWER(:" + name1 + ") <> :" +  name2, e.toString());
	    assertEquals("LOWER($$string1$$) <> $$string1$$", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	    BooleanExpression<?> e2 = _Sample.newString("string1").neqIgnoreCase("STRING1");
	    
	    rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	}
	
	
	@Test
	public void testStringInIgnoreCaseUUID() {
		
		List<UUID> l = Arrays.asList(key1, key2);
		
		BooleanExpression<?> e = _Sample.$pkey.inIgnoreCase(l);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		VariableExpression v = i.next();
		String name1 = v.getName();
		Iterator<?> vi = ((Collection<?>) v.getValue()).iterator();
		
		for (UUID k : l) {
			assertEquals(k.toString(), vi.next());
		}
		
	    assertEquals(_Sample.$pkey + " = ANY(:" + name1 + ")", e.toString());
	    assertEquals(_Sample.$pkey + " = ANY($${" + key1 + ", " + key2 + "}$$)", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testStringInIgnoreCase() {
		
		List<Object> l = Arrays.asList("Rodel", "Viado", null, _Sample.strings(0));
		
		BooleanExpression<?> e = _Sample.$string.inIgnoreCase(l);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		VariableExpression v = i.next();
		String name1 = v.getName();
		Iterator<?> vi = ((Collection<?>) v.getValue()).iterator();
		
		
		assertEquals(l.get(0).toString().toLowerCase(), vi.next());
		assertEquals(l.get(1).toString().toLowerCase(), vi.next());
		
	    assertEquals("((LOWER(" + _Sample.$string + ") = ANY(:" + name1 + ")) OR (LOWER(" + _Sample.$string + ") = LOWER(" + _Sample.strings(0) + "))) OR (" + _Sample.$string + " IS NULL)" , e.toString());
	    assertEquals("((LOWER(" + _Sample.$string + ") = ANY($${" + l.get(0).toString().toLowerCase() + ", " + l.get(1).toString().toLowerCase() + "}$$)) OR (LOWER(" + _Sample.$string + ") = LOWER(" + _Sample.strings(0) + "))) OR (" + _Sample.$string + " IS NULL)" , e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	}
	
	
	@Test
	public void testStringNotInIgnoreCase() {
		
		List<Object> l = Arrays.asList("Rodel", "Viado", null, _Sample.strings(0));
		
		BooleanExpression<?> e = _Sample.$string.notInIgnoreCase(l);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		VariableExpression v = i.next();
		String name1 = v.getName();
		Iterator<?> vi = ((Collection<?>) v.getValue()).iterator();
		
		
		assertEquals(l.get(0).toString().toLowerCase(), vi.next());
		assertEquals(l.get(1).toString().toLowerCase(), vi.next());
		
	    assertEquals("((LOWER(" + _Sample.$string + ") != ALL(:" + name1 + ")) AND (LOWER(" + _Sample.$string + ") <> LOWER(" + _Sample.strings(0) + "))) AND (" + _Sample.$string + " IS NOT NULL)" , e.toString());
	    assertEquals("((LOWER(" + _Sample.$string + ") != ALL($${" + l.get(0).toString().toLowerCase() + ", " + l.get(1).toString().toLowerCase() + "}$$)) AND (LOWER(" + _Sample.$string + ") <> LOWER(" + _Sample.strings(0) + "))) AND (" + _Sample.$string + " IS NOT NULL)" , e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	}
	
	
	@Test
	public void testStringIn() {
		
		List<Object> l = Arrays.asList("Rodel", "Viado", null, _Sample.strings(0));
		
		BooleanExpression<?> e = _Sample.$string.in(l);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		VariableExpression v = i.next();
		String name1 = v.getName();
		Iterator<?> vi = ((Collection<?>) v.getValue()).iterator();
		
		
		assertEquals(l.get(0).toString(), vi.next());
		assertEquals(l.get(1).toString(), vi.next());
		
	    assertEquals("((" + _Sample.$string + " = ANY(:" + name1 + ")) OR (" + _Sample.$string + " = " + _Sample.strings(0) + ")) OR (" + _Sample.$string + " IS NULL)" , e.toString());
	    assertEquals("((" + _Sample.$string + " = ANY($${" + l.get(0).toString() + ", " + l.get(1).toString() + "}$$)) OR (" + _Sample.$string + " = " + _Sample.strings(0) + ")) OR (" + _Sample.$string + " IS NULL)" , e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testStringNotIn() {
		
		List<Object> l = Arrays.asList("Rodel", "Viado", null, _Sample.strings(0));
		
		BooleanExpression<?> e = _Sample.$string.notIn(l);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		VariableExpression v = i.next();
		String name1 = v.getName();
		Iterator<?> vi = ((Collection<?>) v.getValue()).iterator();
		
		
		assertEquals(l.get(0).toString(), vi.next());
		assertEquals(l.get(1).toString(), vi.next());
		
	    assertEquals("((" + _Sample.$string + " != ALL(:" + name1 + ")) AND (" + _Sample.$string + " <> " + _Sample.strings(0) + ")) AND (" + _Sample.$string + " IS NOT NULL)" , e.toString());
	    assertEquals("((" + _Sample.$string + " != ALL($${" + l.get(0).toString() + ", " + l.get(1).toString() + "}$$)) AND (" + _Sample.$string + " <> " + _Sample.strings(0) + ")) AND (" + _Sample.$string + " IS NOT NULL)" , e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testArrayOfString() {
		
		BooleanExpression<?> e = _Sample.strings(0).inIgnoreCase(Arrays.asList("A", key2));
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		
		i = p.iterator();
		
		assertEquals("[a, " + key2+ "]", i.next().getValue().toString());
		assertEquals("LOWER(" + _Sample.strings(0) + ") = ANY(:" + name1 + ")", e.toString());
		
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testInString() {
		StringExpression<?> e = _Sample.newString("string1");
		assertEquals("$$string1$$", e.unBoundVariables().toString());
		BooleanExpression<?> e2 = e.in(Arrays.asList("string1","string2","string3"));
		assertEquals("$$string1$$ = ANY($${string1, string2, string3}$$)", e2.unBoundVariables().toString());
		
		ResultSet rs = dml.select(e2).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(true, rs.getResultAt(0, 0)); 
	}

	
	@Test
	public void testStringConcat() {
		StringExpression<?> e = _Sample.numbers(1).castAsString().concat("string2");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		
		

		i = p.iterator();
		
		assertEquals("string2", i.next().getValue());
		

	    assertEquals("CONCAT(" + _Sample.numbers(1).castAsString() + ", :" +  name1 + ")", e.toString());
	    assertEquals("CONCAT(" + _Sample.numbers(1).castAsString() +", $$string2$$)", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(numbers1.get(1) + "string2", rs.getResultAt(0, 0)); 
	    
	}
	
	@Test
	public void testStringAggregate() {
		StringExpression<?> e = _Sample.string.aggregate("-");
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		
		i = p.iterator();
		
		assertEquals("-", i.next().getValue());

	    assertEquals("STRING_AGG(" + _Sample.string + ", :" +  name1 + ")", e.toString());
	    assertEquals("STRING_AGG(" + _Sample.string + ", $$-$$)", e.unBoundVariables().toString());
		
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	   //result is dependent on order of records on every call
	   //therefore we test for each case
	    assertTrue((stringValue1 + "-" + stringValue2).equals(rs.getResultAt(0, 0)) || 
	    		   (stringValue2 + "-" + stringValue1).equals(rs.getResultAt(0, 0))); 
	    
	}
	
	
	@Test
	public void testStringAggregateOrdered() {
		StringExpression<?> e = _Sample.string.aggregate("?", _Sample.string.lower());
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		
		i = p.iterator();
		
		assertEquals("?", i.next().getValue());

	    assertEquals("STRING_AGG(" + _Sample.string + ", :" +  name1 + " ORDER BY " + _Sample.string.lower() + ")", e.toString());
	    assertEquals("STRING_AGG(" + _Sample.string + ", $$?$$ ORDER BY " + _Sample.string.lower() + ")", e.unBoundVariables().toString());
		
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    assertTrue((stringValue1 + "?" + stringValue2).equals(rs.getResultAt(0, 0)));
	}
	
	
	@Test
	public void testStringAggregateOrderedAsc() {
		StringExpression<?> e = _Sample.string.aggregate("-", _Sample.string.lower(), true);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		
		i = p.iterator();
		
		assertEquals("-", i.next().getValue());

	    assertEquals("STRING_AGG(" + _Sample.string + ", :" +  name1 + " ORDER BY " + _Sample.string.lower() + " ASC)", e.toString());
	    assertEquals("STRING_AGG(" + _Sample.string + ", $$-$$ ORDER BY " + _Sample.string.lower() + " ASC)", e.unBoundVariables().toString());
		
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    assertTrue((stringValue1 + "-" + stringValue2).equals(rs.getResultAt(0, 0)));
	}
	
	@Test
	public void testStringAggregateOrderedDesc() {
		StringExpression<?> e = _Sample.string.aggregate("-", _Sample.string.lower(), false);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		
		i = p.iterator();
		
		assertEquals("-", i.next().getValue());

	    assertEquals("STRING_AGG(" + _Sample.string + ", :" +  name1 + " ORDER BY " + _Sample.string.lower() + " DESC)", e.toString());
	    assertEquals("STRING_AGG(" + _Sample.string + ", $$-$$ ORDER BY " + _Sample.string.lower() + " DESC)", e.unBoundVariables().toString());
		
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    assertTrue((stringValue2 + "-" + stringValue1).equals(rs.getResultAt(0, 0)));
	}
	
	@Test
	public void testStringAggregateOrdered2() {
		StringExpression<?> e = _Sample.string.aggregate(_Sample.newString("-"), _Sample.string.lower());
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		
		String name1 = i.next().getName();
		
		i = p.iterator();
		
		assertEquals("-", i.next().getValue());

	    assertEquals("STRING_AGG(" + _Sample.string + ", :" +  name1 + " ORDER BY " + _Sample.string.lower() + ")", e.toString());
	    assertEquals("STRING_AGG(" + _Sample.string + ", $$-$$ ORDER BY " + _Sample.string.lower() + ")", e.unBoundVariables().toString());
		
	    
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    assertTrue((stringValue1 + "-" + stringValue2).equals(rs.getResultAt(0, 0)));
	}
	
	@Test
	public void testStringMax() {
		StringExpression<?> e = _Sample.string.max();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());

		

	    assertEquals("MAX(" + _Sample.string + ")", e.toString());
	    assertEquals("MAX(" + _Sample.string + ")", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    assertEquals(stringValue2, rs.getResultAt(0, 0)); 
	    
	}
	
	
	@Test
	public void testStringMin() {
		StringExpression<?> e = _Sample.string.min();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());

		

	    assertEquals("MIN(" + _Sample.string + ")", e.toString());
	    assertEquals("MIN(" + _Sample.string + ")", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    assertEquals(stringValue1, rs.getResultAt(0, 0)); 
	    
	}
	
	@Test
	public void testStringCount() {
		NumericExpression<?> e = _Sample.string.count();
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());

	    assertEquals("COUNT(" + _Sample.string + ")", e.toString());
	    assertEquals("COUNT(" + _Sample.string + ")", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    assertEquals(2l, rs.getResultAt(0, 0)); 
	    
	}
	
	
	@Test
	public void testStringCharLength() {
		NumericExpression<?> e = _Sample.string.charLength();
		
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());

	    assertEquals("CHAR_LENGTH(" + _Sample.string + ")", e.toString());
	    assertEquals("CHAR_LENGTH(" + _Sample.string + ")", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).orderBy(_Sample.$pkey).desc().create().execute(con);
	    assertEquals(stringValue1.length(), rs.getResultAt(0, 0));
	    assertEquals(stringValue2.length(), rs.getResultAt(1, 0));
	    
	}
	
	@Test
	public void testStringCharLengthSum() {
		NumericExpression<?> e = _Sample.string.charLength().sum();
		Set<VariableExpression> p = e.getBoundVariables();
		assertEquals(0, p.size());

	    assertEquals("SUM(CHAR_LENGTH(" + _Sample.string + "))", e.toString());
	    assertEquals("SUM(CHAR_LENGTH(" + _Sample.string + "))", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).create().execute(con);
	    assertEquals(new Long((stringValue1.length() + stringValue2.length())), rs.getResultAt(0, 0));
	    
	}
	
	@Test
	public void testStringLower() {
		StringExpression<?> e = _Sample.string.lower();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
		
	    assertEquals("LOWER(" + _Sample.string + ")", e.toString());
	    assertEquals("LOWER(" + _Sample.string + ")", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).orderBy(_Sample.$pkey).desc().create().execute(con);
	    assertEquals(stringValue1.toLowerCase(), rs.getResultAt(0, 0));
	    assertEquals(stringValue2.toLowerCase(), rs.getResultAt(1, 0));
	    
	}
	
	@Test
	public void testStringUpper() {
		StringExpression<?> e = _Sample.string.upper();
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
		
	    assertEquals("UPPER(" + _Sample.string + ")", e.toString());
	    assertEquals("UPPER(" + _Sample.string + ")", e.unBoundVariables().toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.in(keys)).orderBy(_Sample.$pkey).desc().create().execute(con);
	    assertEquals(stringValue1.toUpperCase(), rs.getResultAt(0, 0));
	    assertEquals(stringValue2.toUpperCase(), rs.getResultAt(1, 0));
	    
	}
	
	
	


	



}
