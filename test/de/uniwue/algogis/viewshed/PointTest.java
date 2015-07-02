package de.uniwue.algogis.viewshed;

import static org.junit.Assert.*;

import org.junit.Test;

public class PointTest {

	@Test
	public void testGetHeight() {
		Point p = new Point(0, 0, 10.5);
		assertEquals(10.5, p.getHeight(), 0.0001);
	}

	@Test
	public void testGetXCoor() {
		Point p = new Point(10, 0, 0);
		assertEquals(10, p.getXCoor());
	}

	@Test
	public void testGetYCoor() {
		Point p = new Point(0, 10, 0);
		assertEquals(10, p.getYCoor());
	}

	@Test
	public void testCalcDistance() {
		Point p1 = new Point(1, 1, 0.1);
		Point p2 = new Point(5, 4, 0.2);
		assertEquals(5.0, p1.calcDistance(p2), 0.0001);
	}

	@Test
	public void testCalcSlope() {
		Point p1 = new Point(1, 1, 2.2);
		Point p2 = new Point(5, 4, 1.2);
		assertEquals(-0.2, p1.calcSlope(p2), 0.0001);
	}

}
