package de.uniwue.algogis.viewshed;

import static org.junit.Assert.*;

import org.junit.Test;

public class HeightedPointTest {

	@Test
	public void testGetHeight() {
		HeightedPoint p = new HeightedPoint(0, 0, 10.5);
		assertEquals(10.5, p.getHeight(), 0.0001);
	}

	@Test
	public void testCalcSlope() {
		HeightedPoint p1 = new HeightedPoint(1, 1, 2.2);
		HeightedPoint p2 = new HeightedPoint(5, 4, 1.2);
		assertEquals(-0.2, p1.calcSlope(p2), 0.0001);
	}

	@Test
	public void testEquals() {
		HeightedPoint p1 = new HeightedPoint(1, 2, 10.5);
		HeightedPoint p2 = new HeightedPoint(1, 2, 10.5);
		HeightedPoint p3 = new HeightedPoint(1, 0, 10.5);
		HeightedPoint p4 = new HeightedPoint(0, 2, 10.5);
		HeightedPoint p5 = new HeightedPoint(1, 1, 00.0);
		String p6 = "test";
		assertEquals(p1, p1);
		assertEquals(p2, p1);
		assertEquals(p1, p2);
		assertNotEquals(p1, p3);
		assertNotEquals(p1, p4);
		assertNotEquals(p1, p5);
		assertNotEquals(p1, p6);
		assertNotEquals(p6, p1);
	}

}
