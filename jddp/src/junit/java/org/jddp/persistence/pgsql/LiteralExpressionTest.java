package org.jddp.persistence.pgsql;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Arrays;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.LiteralExpression;
import org.jddp.persistence.sample.Sample;
import org.jddp.persistence.sql.ResultSet;
import org.jddp.util.json.JSONBuilder;
import org.junit.Test;

import optional.packge._Sample;
import optional.packge._Sample2;


public class LiteralExpressionTest extends BaseTest {

	
	
	@Test
	public void testLiteralProjected() {
		
		Sample sample = new Sample();
		sample.setNumber(123);
		sample.setString("rodel");
		
		String json = JSONBuilder.JSON.serialize(sample);
		
		String jsonArray = JSONBuilder.JSON.serialize(Arrays.asList(sample));
		
		LiteralExpression fieldLiteral   = _Sample.newLiteral("Sample->>'string'");
		LiteralExpression numericLiteral = _Sample.newLiteral("123");
		LiteralExpression stringLiteral  = _Sample.newLiteral("'rodel'");
		LiteralExpression booleanLiteral = _Sample.newLiteral("Sample->>'string' <> 'rodel'");
		LiteralExpression nullLiteral    = _Sample.newLiteral("null");
		LiteralExpression objectLiteral    = _Sample.newLiteral("'" + json + "'");
		LiteralExpression objectArrayLiteral    = _Sample.newLiteral("'" + jsonArray + "'");
		
		ResultSet rs = dml.select(
				fieldLiteral,
				numericLiteral,
				stringLiteral,
				booleanLiteral,
				nullLiteral,
				objectLiteral,
				objectLiteral.asObject(Sample.class),
				objectArrayLiteral.asObjectCollection(Sample.class)
				
		).where(_Sample.$pkey.eq(key1)).create().execute(con);
		
		assertEquals(stringValue1, rs.getResultAt(0, 0));
		assertEquals(123, rs.getResultAt(0, 1));
		assertEquals("rodel", rs.getResultAt(0, 2));
		assertEquals(true, rs.getResultAt(0, 3));
		assertEquals(null, rs.getResultAt(0, 4));
		assertEquals(json, rs.getResultAt(0, 5));
		assertEquals(sample, rs.getResultAt(0, 6));
		assertEquals(Arrays.asList(sample), rs.getResultAt(0, 7));
	}
	
	@Test
	public void testLiteralInConditional() {
		BooleanExpression<?> s = _Sample.newLiteral("Sample->>'string' <> 'rodel'").asBoolean();
		ResultSet rs = dml.select(s).where(_Sample.$pkey.eq(key1).and(s)).create().execute(con);
		assertEquals(Boolean.class, rs.getResultAt(0, 0).getClass());
	}
	
	@Test
	public void testLiteralSelect() {
		BooleanExpression<?> s = _Sample.newLiteral("(Select Sample->>'string' <> 'rodel')").asBoolean();
		ResultSet rs = dml.select(s).where(_Sample.$pkey.eq(key1).and(s)).create().execute(con);
		assertEquals(Boolean.class, rs.getResultAt(0, 0).getClass());
	}

	
	@Test
	public void testLiteralSelectFromAnotherEntity() {
		BooleanExpression<?> s = _Sample.newLiteral(dml2.select(_Sample2.booleans(0)).limit(1).create().unBoundVariables().toString()).parenthesize().asBoolean();
		ResultSet rs = dml.select(s).where(_Sample.$pkey.eq(key1).and(s)).create().execute(con);
		assertEquals(Boolean.class, rs.getResultAt(0, 0).getClass());
	}
	
	
	@Test
	public void testLiteralExecuteFunction() {
		LiteralExpression s = _Sample.newLiteral("now()");
		ResultSet rs = dml.select(s).where(_Sample.$pkey.eq(key1)).create().execute(con);
		assertEquals(Timestamp.class, rs.getResultAt(0, 0).getClass());
	}
	
	
}
