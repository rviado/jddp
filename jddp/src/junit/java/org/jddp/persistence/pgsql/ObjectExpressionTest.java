package org.jddp.persistence.pgsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.NumericExpression;
import org.jddp.expression.ObjectExpression;
import org.jddp.expression.StringExpression;
import org.jddp.persistence.sample.Sample2;
import org.jddp.persistence.sample.Sample3;
import org.jddp.persistence.sample.SampleExtension;
import org.jddp.persistence.sql.ResultSet;
import org.jddp.persistence.sql.select.SelectDetached;
import org.jddp.util.json.JSONBuilder;
import org.junit.Test;

import optional.packge._Sample;

public class ObjectExpressionTest extends BaseTest {

	@Test
	public void testObjectByteArray() {
		
		ObjectExpression<?> byteArrayExpr = _Sample.byteArray;
		
	    ResultSet rs = dml.select(byteArrayExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof byte[]);
	    
	    assertEquals(new String(byteArray), new String(rs.getResultAt(0, 0, byte[].class)));
	}

	@Test
	public void testObjectByteArrayAsLiteral() {
		
		ObjectExpression<?> byteArrayExpr = _Sample.newObject(byteArray);
		
	    ResultSet rs = dml.select(byteArrayExpr.unBoundVariables()).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof byte[]);
	    
	    assertEquals(new String(byteArray), new String(rs.getResultAt(0, 0, byte[].class)));
	}
	
	@Test
	public void testObjectIntArray() {
		
		int[] intArray = new int[] {1,2,3};
		
		ObjectExpression<?> intArrayExpr = _Sample.newObject(intArray);
		
	    ResultSet rs = dml.select(intArrayExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof int[]);
	    
	    int[] result = rs.getResultAt(0, 0, int[].class);
	    for (int i = 0; i < intArray.length; i++) {
	    	assertEquals(intArray[i], result[i]);
	    }
	}
	
	@Test
	public void testObjectIntArrayAsLiteral() {
		
		int[] intArray = new int[] {1,2,3};
		
		ObjectExpression<?> intArrayExpr = _Sample.newObject(intArray);
		
	    ResultSet rs = dml.select(intArrayExpr.unBoundVariables()).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof int[]);
	    
	    int[] result = rs.getResultAt(0, 0, int[].class);
	    for (int i = 0; i < intArray.length; i++) {
	    	assertEquals(intArray[i], result[i]);
	    }
	    
	}
	
	@Test
	public void testObjectWithSameTypeObject() {
		
		ObjectExpression<?> parentExpr = _Sample.ObjectWithArray;
		ObjectExpression<?> childExpr = _Sample.objectWithArray().recursive;
		
	    ResultSet rs = dml.select(parentExpr, childExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(rs.getResultAt(0, 1) instanceof Sample2);
	    
	    Sample2 parent = rs.getResultAt(0, 0, Sample2.class);
	    Sample2 child = rs.getResultAt(0, 1, Sample2.class);
	    
	    assertEquals(parent.getRecursive(), child);
	}
	
	
	@Test
	public void testObjectArray() {
		
		ObjectExpression<?> parentExpr = _Sample.ObjectWithArray;
		ObjectExpression<?> childExpr = _Sample.objectWithArray().recursiveArraies;
		
	    ResultSet rs = dml.select(parentExpr, childExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(rs.getResultAt(0, 1) instanceof Collection);
	    
	    Sample2 parent = rs.getResultAt(0, 0, Sample2.class);
	    List<?> child = rs.getResultAt(0, 1, List.class);
	    
	    assertEquals(parent.getRecursiveArraies(), child);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testObjectArrayInArray() {
		
		ObjectExpression<?> rootExpr = _Sample.ObjectWithArray;
		ObjectExpression<?> parentExpr = _Sample.objectWithArray().ArrayWithSample3AsElementTypes;
		ObjectExpression<?> childExpr = _Sample.objectWithArray().arrayWithSample3AsElementTypes(0).arrayWithSample2AsElementTypes;
		ObjectExpression<?> childChildExpr = _Sample.objectWithArray().arrayWithSample3AsElementTypes().arrayWithSample2AsElementTypes(0);
		ObjectExpression<?> childChildExpr2 = _Sample.objectWithArray().arrayWithSample3AsElementTypes().arrayWithSample2AsElementTypes.element(0);
		ObjectExpression<?> childChildExpr3 = _Sample.objectWithArray().arrayWithSample3AsElementTypes(1).arrayWithSample2AsElementTypes(0);
		ObjectExpression<?> childChildExpr4 = _Sample.objectWithArray().arrayWithSample3AsElementTypes(1).ArrayWithSample3AsElementTypes;
		
		SelectDetached<ResultSet> select = dml.
			select(
				rootExpr, 
				parentExpr, 
				childExpr, 
				childChildExpr, childChildExpr2, childChildExpr3, childChildExpr4).
			where(
				_Sample.$pkey.eq(key1)
			).create();
		
				
	    ResultSet rs = select.execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(rs.getResultAt(0, 1) instanceof Collection);
	    assertTrue(rs.getResultAt(0, 2) instanceof Collection);
	    assertTrue(rs.getResultAt(0, 3) instanceof Sample2);
	    assertTrue(rs.getResultAt(0, 4) instanceof Sample2);
	    assertTrue(rs.getResultAt(0, 5) instanceof Sample2);
	    assertTrue(rs.getResultAt(0, 6) instanceof Sample3);
	    
	    Sample2 root = rs.getResultAt(0, 0, Sample2.class);
	    List<Sample3> parent = rs.getResultAt(0, 1, List.class);
	    List<Sample2> child =  rs.getResultAt(0, 2, List.class);
	    Sample2 childChild =   rs.getResultAt(0, 3, Sample2.class);
	    Sample2 childChild2 =  rs.getResultAt(0, 4, Sample2.class);
	    Sample2 childChild3 =  rs.getResultAt(0, 5, Sample2.class);
	    Sample3 childChild4 =  rs.getResultAt(0, 6, Sample3.class);
	    
	    assertEquals(root.getArrayWithSample3AsElementTypes(), parent);
	    assertEquals(parent.get(0).getArrayWithSample2AsElementTypes(), child);
	    assertEquals(parent.get(0).getArrayWithSample2AsElementTypes().get(0), childChild);
	    assertEquals(parent.get(0).getArrayWithSample2AsElementTypes().get(0), childChild2);
	    assertEquals(parent.get(1).getArrayWithSample2AsElementTypes().get(0), childChild3);
	    assertEquals(parent.get(1), childChild4);
	}
	
	
	@Test
	public void testObjectLiteralArray() {
		
		
		List<SampleExtension> ses = new ArrayList<>();
		
		SampleExtension s1 = createSample(
				key1, 
				"sample1", 
				true, 
				numbers1.get(0), 
				zdt1, 
				strings1, 
				numbers1,
				byteArray
		);
		
		SampleExtension s2 = createSample(
				key1, 
				"sample2", 
				true, 
				numbers1.get(1), 
				zdt2, 
				strings1, 
				numbers1,
				byteArray2
		);
		
		ses.add(s1);
		ses.add(s2);
		
		ObjectExpression<?> literalArray = _Sample.newObject(ses).unBoundVariables();
		assertEquals("$${"+ JSONBuilder.JSON.serialize(s1) + ", " + JSONBuilder.JSON.serialize(s2) +"}$$", literalArray.toString());
		
		SelectDetached<ResultSet> select = dml.
			select(
					literalArray
				).
			where(
				_Sample.$pkey.eq(key1)
			).create();
		
	    ResultSet rs = select.execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof List);
	    List<?> list = ((List<?>) rs.getResultAt(0, 0));
	    
	    assertEquals(2, list.size());
	    
	    assertEquals(ses.get(0), list.get(0));
	    assertEquals(ses.get(1), list.get(1));
	}
	
	
	@Test
	public void testObjectInArray() {
		
		
		List<SampleExtension> ses = new ArrayList<>();
		
		SampleExtension s1 = createSample(
				key1, 
				"sample1", 
				true, 
				numbers1.get(0), 
				zdt1, 
				strings1, 
				numbers1,
				byteArray
		);
		
		SampleExtension s2 = createSample(
				key1, 
				"sample2", 
				true, 
				numbers1.get(1), 
				zdt2, 
				strings1, 
				numbers1,
				byteArray2
		);
		
		ses.add(s1);
		ses.add(s2);
		ses.add(null);
		
		
		ObjectExpression<?> s1Obj = _Sample.newObject(s1);
		BooleanExpression<?> e = s1Obj.in(ses);
		
		
		
		SelectDetached<ResultSet> select = dml.
			select(
					e.unBoundVariables(), e, _Sample.newObject(ses)
				).
			where(
				_Sample.$pkey.eq(key1)
			).create();
		
	    ResultSet rs = select.execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Boolean);
	    assertTrue(rs.getResultAt(0, 0, Boolean.class));
	    
	    assertTrue(rs.getResultAt(0, 1) instanceof Boolean);
	    assertTrue(rs.getResultAt(0, 1, Boolean.class));
	    
	    assertTrue(rs.getResultAt(0, 2) instanceof List);
	    assertTrue(rs.getResultAt(0, 2, List.class).get(0).equals(s1));
	    assertTrue(rs.getResultAt(0, 2, List.class).get(1).equals(s2));
	    assertTrue(rs.getResultAt(0, 2, List.class).get(2)== null);
	}
	
	@Test
	public void testObjectNulls() {
		
		ObjectExpression<?> objectNull = _Sample.newObject((Object) null);
		StringExpression<?> stringNull = _Sample.newString(null);
		NumericExpression<?> numericNull = _Sample.newNumber(null);
		BooleanExpression<?> booleanNull = _Sample.newBoolean(null);
		
		SelectDetached<ResultSet> select = dml.
			select(
					objectNull, stringNull, numericNull, booleanNull
				).
			where(
				_Sample.$pkey.eq(key1)
			).create();
		
	    ResultSet rs = select.execute(con);
	    
	    assertEquals("null", rs.getResultAt(0, 0));
	    assertEquals(null, rs.getResultAt(0, 1));
	    assertEquals(null, rs.getResultAt(0, 2));
	    assertEquals(null, rs.getResultAt(0, 3));
	    
	}
	
	@Test
	public void testArrayWithPrimitivesAsElement() {
		
		SelectDetached<ResultSet> select = dml.
			select(
					_Sample.booleans(0)
				).
			where(
				_Sample.$pkey.eq(key1)
			).create();
		
	    ResultSet rs = select.execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0, Boolean.class));

	}
	
	@Test
	public void testObjectWithSameTypeObjectInArray() {
		
		ObjectExpression<?> parentExpr = _Sample.ObjectWithArray;
		ObjectExpression<?> childExpr = _Sample.objectWithArray().recursiveArraies(0);
		
	    ResultSet rs = dml.select(parentExpr, childExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(rs.getResultAt(0, 1) instanceof Sample2);
	    
	    Sample2 parent = rs.getResultAt(0, 0, Sample2.class);
	    Sample2 child = rs.getResultAt(0, 1, Sample2.class);
	    
	    assertEquals(parent.getRecursiveArraies().get(0), child);
	}
	
	@Test
	public void testObjectWithSameTypeObjectInArrayUsingElementAccessor() {
		
		ObjectExpression<?> parentExpr = _Sample.ObjectWithArray;
		ObjectExpression<?> childExpr = _Sample.objectWithArray().recursiveArraies.element(0);
		
	    ResultSet rs = dml.select(parentExpr, childExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(rs.getResultAt(0, 1) instanceof Sample2);
	    
	    Sample2 parent = rs.getResultAt(0, 0, Sample2.class);
	    Sample2 child = rs.getResultAt(0, 1, Sample2.class);
	    
	    assertEquals(parent.getRecursiveArraies().get(0), child);
	}
	
	
	@Test
	public void testObjectWithSameTypeObjectInArrayUsingElementAndFieldAccessor() {
		
		ObjectExpression<?> parentExpr = _Sample.ObjectWithArray;
		ObjectExpression<?> childExpr = _Sample.objectWithArray().recursiveArraies.element(0).field("number");
		
	    ResultSet rs = dml.select(parentExpr, childExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(Long.class.isAssignableFrom(rs.getResultAt(0, 1).getClass()));
	    
	    Sample2 parent = rs.getResultAt(0, 0, Sample2.class);
	    long child = rs.getResultAt(0, 1, Long.class);
	    
	    assertEquals(parent.getRecursiveArraies().get(0).getNumber(), child);
	}

	
	@Test
	public void testObjectUsingFieldsAccessor() {
		
		ObjectExpression<?> parentExpr = _Sample.ObjectWithArray;
		ObjectExpression<?> childExpr = _Sample.ObjectWithArray.fieldByPath(Arrays.asList("recursive", "number"));
		
	    ResultSet rs = dml.select(parentExpr, childExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(Long.class.isAssignableFrom(rs.getResultAt(0, 1).getClass()));
	    
	    Sample2 parent = rs.getResultAt(0, 0, Sample2.class);
	    long child = rs.getResultAt(0, 1, Long.class);
    
	    assertEquals(parent.getRecursiveArraies().get(0).getNumber(), child);
	}
	
	
	@Test
	public void testObjectUsingFieldsAsTextAccessor() {
		
		ObjectExpression<?> parentExpr = _Sample.ObjectWithArray;
		StringExpression<?> childExpr = _Sample.ObjectWithArray.fieldByPathAsText(Arrays.asList("recursive", "number"));
		
	    ResultSet rs = dml.select(parentExpr, childExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(String.class.isAssignableFrom(rs.getResultAt(0, 1).getClass()));
	    
	    Sample2 parent = rs.getResultAt(0, 0, Sample2.class);
	    String child = rs.getResultAt(0, 1, String.class);
	    
	    assertEquals(parent.getRecursiveArraies().get(0).getNumber(), new Long(child).longValue());
	}
	
	
	@Test
	public void testObjectWithSameTypeObjectInArrayUsingElementAsTextAccessor() {
		
		ObjectExpression<?> parentExpr = _Sample.ObjectWithArray;
		StringExpression<?> childExpr = _Sample.objectWithArray().recursiveArraies.elementAsText(0);
		
	    ResultSet rs = dml.select(parentExpr, childExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(rs.getResultAt(0, 1) instanceof String);
	    
	    Sample2 parent = rs.getResultAt(0, 0, Sample2.class);
	    String child = rs.getResultAt(0, 1, String.class);
	    
	    assertEquals(parent.getRecursiveArraies().get(0), JSONBuilder.JSON.deserialize(child, Sample2.class));
	    
	    
	}
	
	
	@Test
	public void testArrayLength() {
		
		ObjectExpression<?> parentExpr = _Sample.ObjectWithArray;
		NumericExpression<?> childExpr = _Sample.objectWithArray().numbers.length();
		
	    ResultSet rs = dml.select(parentExpr, childExpr).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    
	    assertTrue(rs.getResultAt(0, 0) instanceof Sample2);
	    assertTrue(Integer.class.isAssignableFrom(rs.getResultAt(0, 1).getClass()));
	    
	    Sample2 parent = rs.getResultAt(0, 0, Sample2.class);
	    Integer child = rs.getResultAt(0, 1, Integer.class);
	    
	    
	    
	    assertEquals(parent.getNumbers().size(), child.intValue());
	    
	}
	
	
	@Test
	public void testContains() {
		
		BooleanExpression<?> containment = _Sample.objectWithArray().numbers.contains(numbers1.get(1));
	    ResultSet rs = dml.select(containment).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertTrue(rs.getResultAt(0, 0) instanceof Boolean);
	    assertTrue(rs.getResultAt(0, 0, Boolean.class));
	    
	    containment = _Sample.objectWithArray().numbers.contains(_Sample.newObject(numbers1.get(1)));
	    rs = dml.select(containment).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertTrue(rs.getResultAt(0, 0) instanceof Boolean);
	    assertTrue(rs.getResultAt(0, 0, Boolean.class));
	    
	}
	
	
	@Test
	public void testContainedIn() {
		
		BooleanExpression<?> containment = _Sample.objectWithArray().numbers.containedIn(numbers1);
	    ResultSet rs = dml.select(containment).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertTrue(rs.getResultAt(0, 0) instanceof Boolean);
	    assertTrue(rs.getResultAt(0, 0, Boolean.class));
	    
	    containment = _Sample.objectWithArray().numbers.containedIn(_Sample.newObject((Object) numbers1));
	    rs = dml.select(containment).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertTrue(rs.getResultAt(0, 0) instanceof Boolean);
	    assertTrue(rs.getResultAt(0, 0, Boolean.class));
	    
	    containment = _Sample.objectWithArray().numbers.containedIn(_Sample.newObject((Object) numbers2));
	    rs = dml.select(containment).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertTrue(rs.getResultAt(0, 0) instanceof Boolean);
	    assertTrue(!rs.getResultAt(0, 0, Boolean.class));
	    
	}
	
	@Test
	public void testConcat() {
		
		ObjectExpression<?> concat = _Sample.objectWithArray().numbers.concat(numbers2);
	    ResultSet rs = dml.select(concat).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertTrue(rs.getResultAt(0, 0) instanceof Collection);
	    
	    @SuppressWarnings("unchecked")
		Collection<Double> result = rs.getResultAt(0, 0, Collection.class);
	    assertEquals(numbers1.size() + numbers2.size(), result.size() );
	    Iterator<Double> i = result.iterator();
	    for (Double n : numbers1) {
	    	assertEquals(n, i.next());
	    }
	    for (Double n : numbers2) {
	    	assertEquals(n, i.next());
	    }
	    
	    
	    
	}
}
