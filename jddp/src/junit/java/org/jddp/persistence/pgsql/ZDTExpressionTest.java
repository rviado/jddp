package org.jddp.persistence.pgsql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jddp.expression.BooleanExpression;
import org.jddp.expression.VariableExpression;
import org.jddp.expression.ZonedDateTimeExpression;
import org.jddp.persistence.sql.ResultSet;
import org.junit.Test;

import optional.packge._Sample;


public class ZDTExpressionTest extends BaseTest {

	@Test
	public void testRetrieveTimetstamp() {
	    ResultSet rs = dml.select(_Sample.$timestamp).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(rs.getResultAt(0, 0).getClass(), ZonedDateTime.class);
	    assertTrue(zdt1.isEqual(rs.getResultAt(0, 0, ZonedDateTime.class)));
	}
	
	@Test
	public void testTimetstampAge() {
		ZonedDateTime zdtTest = zdt1.minusYears(1).minusMonths(2).minusDays(3).minusHours(4).minusMinutes(5).minusSeconds(6);
		ResultSet rs = dml.select(_Sample.$timestamp.age(zdtTest)).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(rs.getResultAt(0, 0).getClass(), String.class);
	    assertEquals("P1Y2M3DT4H5M6.0S", rs.getResultAt(0, 0));
	}
	
	
	@Test
	public void testTimetstampPlusDurationOnly() {
		
		Duration d = Duration.ofHours(24);
		
		 ZonedDateTimeExpression<?> e = _Sample.$timestamp.plus(d);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
		
	    assertEquals(_Sample.$timestamp + " + interval $$" + d + "$$", e.toString());
		
	    ResultSet rs = dml.select(_Sample.$timestamp, e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    ZonedDateTime zdt1 = rs.getResultAt(0, 0, ZonedDateTime.class);
	    ZonedDateTime zdt2 = rs.getResultAt(0, 1, ZonedDateTime.class);
	    
	    assertTrue(zdt1.plusHours(24).isEqual(zdt2));
	    
	    BooleanExpression<?> b =  _Sample.$timestamp.eq( _Sample.$timestamp.plus(d));
	    rs = dml.select(b).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertFalse(rs.getResultAt(0, 0, Boolean.class));
	}
	
	@Test
	public void testTimetstampPlusPeriodOnly() {
		
		Period period = Period.of(1, 2, 3);
		
		 ZonedDateTimeExpression<?> e = _Sample.$timestamp.plus(period);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
		
	    assertEquals(_Sample.$timestamp + " + interval $$" + period + "$$", e.toString());
		
	    ResultSet rs = dml.select(_Sample.$timestamp, e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    ZonedDateTime zdt1 = rs.getResultAt(0, 0, ZonedDateTime.class);
	    ZonedDateTime zdt2 = rs.getResultAt(0, 1, ZonedDateTime.class);
	    
	    assertTrue(zdt1.plus(period).isEqual(zdt2));
	    
	    BooleanExpression<?> b =  _Sample.$timestamp.eq( _Sample.$timestamp.plus(period));
	    rs = dml.select(b).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertFalse(rs.getResultAt(0, 0, Boolean.class));
	}
	
	
	@Test
	public void testTimetstampPlusPeriodAndDuration() {
		
		Period period = Period.of(1, 2, 3);
		Duration duration = Duration.ofHours(24);
		
		 ZonedDateTimeExpression<?> e = _Sample.$timestamp.plus(period, duration);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
		
	    assertEquals(_Sample.$timestamp + " + interval $$" + period + duration.toString().substring(1) + "$$", e.toString());
		
	    ResultSet rs = dml.select(_Sample.$timestamp, e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    ZonedDateTime zdt1 = rs.getResultAt(0, 0, ZonedDateTime.class);
	    ZonedDateTime zdt2 = rs.getResultAt(0, 1, ZonedDateTime.class);
	    
	    assertTrue(zdt1.plus(period).plus(duration).isEqual(zdt2));
	    
	    BooleanExpression<?> b =  _Sample.$timestamp.eq( _Sample.$timestamp.plus(period));
	    rs = dml.select(b).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertFalse(rs.getResultAt(0, 0, Boolean.class));
	}
	
	
	@Test
	public void testTimetstampMinusDurationOnly() {
		
		Duration d = Duration.ofHours(24);
		
		 ZonedDateTimeExpression<?> e = _Sample.$timestamp.minus(d);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
		
	    assertEquals(_Sample.$timestamp + " - interval $$" + d + "$$", e.toString());
		
	    ResultSet rs = dml.select(_Sample.$timestamp, e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    ZonedDateTime zdt1 = rs.getResultAt(0, 0, ZonedDateTime.class);
	    ZonedDateTime zdt2 = rs.getResultAt(0, 1, ZonedDateTime.class);
	    
	    assertTrue(zdt1.minusHours(24).isEqual(zdt2));
	    
	    BooleanExpression<?> b =  _Sample.$timestamp.eq( _Sample.$timestamp.plus(d));
	    rs = dml.select(b).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertFalse(rs.getResultAt(0, 0, Boolean.class));
	}
	
	@Test
	public void testTimetstampMinusPeriodOnly() {
		
		Period period = Period.of(1, 2, 3);
		
		 ZonedDateTimeExpression<?> e = _Sample.$timestamp.minus(period);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
		
	    assertEquals(_Sample.$timestamp + " - interval $$" + period + "$$", e.toString());
		
	    ResultSet rs = dml.select(_Sample.$timestamp, e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    ZonedDateTime zdt1 = rs.getResultAt(0, 0, ZonedDateTime.class);
	    ZonedDateTime zdt2 = rs.getResultAt(0, 1, ZonedDateTime.class);
	    
	    assertTrue(zdt1.minus(period).isEqual(zdt2));
	    
	    BooleanExpression<?> b =  _Sample.$timestamp.eq( _Sample.$timestamp.plus(period));
	    rs = dml.select(b).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertFalse(rs.getResultAt(0, 0, Boolean.class));
	}
	
	
	@Test
	public void testTimetstampMinusPeriodAndDuration() {
		
		Period period = Period.of(1, 2, 3);
		Duration duration = Duration.ofHours(24);
		
		 ZonedDateTimeExpression<?> e = _Sample.$timestamp.minus(period, duration);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
		
	    assertEquals(_Sample.$timestamp + " - interval $$" + period + duration.toString().substring(1) + "$$", e.toString());
		
	    ResultSet rs = dml.select(_Sample.$timestamp, e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    ZonedDateTime zdt1 = rs.getResultAt(0, 0, ZonedDateTime.class);
	    ZonedDateTime zdt2 = rs.getResultAt(0, 1, ZonedDateTime.class);
	    
	    assertTrue(zdt1.minus(period).minus(duration).isEqual(zdt2));
	    
	    BooleanExpression<?> b =  _Sample.$timestamp.eq( _Sample.$timestamp.plus(period));
	    rs = dml.select(b).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertFalse(rs.getResultAt(0, 0, Boolean.class));
	}
	
	
	@Test
	public void testTimetstampEQ() {
		
		ZonedDateTime d1 = ZonedDateTime.now(ZoneOffset.UTC);
		
		BooleanExpression<?> e = _Sample.$timestamp.eq(d1);
		
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(0, p.size());
		
	    assertEquals(_Sample.$timestamp + " = $$" + OffsetDateTime.from(d1) + "$$", e.toString());
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	}
		
	@Test
	public void testTimestampInIgnoreCase() {
		
		ZonedDateTime d1 = zdt1;
		ZonedDateTime d2 = ZonedDateTime.now();
		
		OffsetDateTime d1Offset = OffsetDateTime.from(d1);
		OffsetDateTime d2Offset = OffsetDateTime.from(d2);
		
		List<String> utcs = Arrays.asList(d1Offset.toString(), d2Offset.toString());
				
		List<? extends Object> l = Arrays.asList(d1, d2.toString());
		BooleanExpression<?> e = _Sample.$timestamp.inIgnoreCase(l);
		Set<VariableExpression> p = e.getBoundVariables();

		assertEquals(1, p.size());

		Iterator<VariableExpression> i = p.iterator();
		VariableExpression var = i.next();
		
		String name1 = var.getName();
		Object value = var.getValue();
		
		assertTrue(value instanceof Collection);

		Iterator<?> vi = ((Collection<?>) value).iterator();
		for (String utc : utcs) {
			assertEquals(utc, vi.next());
		}
		
	    assertEquals(_Sample.$timestamp + " = ANY(:" + name1 +  ")", e.toString());
	    
	    assertEquals(_Sample.$timestamp + " = ANY($${" + utcs.get(0) +  ", "  +  utcs.get(1) + "}$$)", e.unBoundVariables().toString());
		
		
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    rs = dml.select(e.unBoundVariables()).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	}
	
	@Test
	public void testTimestampBetween() {
		
		ZonedDateTime d1 = zdt1;
		ZonedDateTime d2 = zdt1.plusYears(1);
		
		OffsetDateTime d1Offset = OffsetDateTime.from(d1);
		OffsetDateTime d2Offset = OffsetDateTime.from(d2);
		
		BooleanExpression<?> e = _Sample.$timestamp.between(d1, d2);
	    
	    assertEquals(0, e.getBoundVariables().size());
	    
	    assertEquals(_Sample.$timestamp + " BETWEEN $$" + d1Offset +"$$ AND $$" + d2Offset + "$$", e.unBoundVariables().toString());
	    
	    //test left inclusive
	    ResultSet rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    //test right inclusive
	    e = _Sample.$timestamp.between(zdt1.minusYears(1), zdt1);
	    rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    //left and right inclusive
	    e = _Sample.$timestamp.between(zdt1, zdt1);
	    rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(true, rs.getResultAt(0, 0));
	    
	    //test timestamp is not between
	    e = _Sample.$timestamp.between(zdt1.plusYears(1), zdt1.plusYears(1));
	    rs = dml.select(e).where(_Sample.$pkey.eq(key1)).create().execute(con);
	    assertEquals(false, rs.getResultAt(0, 0));
	    
	    
	}
	
}
