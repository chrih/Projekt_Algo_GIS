package de.uniwue.algogis.viewshed.bruteforce;

import static org.junit.Assert.*;
import org.junit.Test;

import de.uniwue.algogis.viewshed.Dem;
import de.uniwue.algogis.viewshed.HeightedPoint;

public class BruteForceViewshedAnalysisTest {

	@Test
	public void testCalculateViewshedObstacles() {
		Dem d = new Dem("testfile_obstacles.grd");
		System.out.println(d);
		
		BruteForceViewshedAnalysis bfva = new BruteForceViewshedAnalysis();
		HeightedPoint origin = new HeightedPoint(0, 0, 1.8); 
		
		Dem r = bfva.calculateViewshed(d, origin);
		System.out.println(r);
		
		assertEquals(1, r.getHeight(0, 1), 0.001);
		assertEquals(0, r.getHeight(1, 1), 0.001);
		assertEquals(1, r.getHeight(1, 0), 0.001);
		assertEquals(1, r.getHeight(2, 0), 0.001);
		assertEquals(0, r.getHeight(3, 0), 0.001);
		assertEquals(1, r.getHeight(4, 0), 0.001);
		assertEquals(0, r.getHeight(5, 0), 0.001);
		assertEquals(0, r.getHeight(6, 0), 0.001);
	}

	@Test
	public void testCalculateViewshedCircle() {
		Dem d = new Dem("testfile_circle.grd");
		System.out.println(d);
		
		BruteForceViewshedAnalysis bfva = new BruteForceViewshedAnalysis();
		HeightedPoint origin = new HeightedPoint(5, 5, 60); 
		
		Dem r = bfva.calculateViewshed(d, origin);
		System.out.println(r);
		
		assertEquals(1, r.getHeight(5, 6), 0.001);
		assertEquals(1, r.getHeight(6, 5), 0.001);
	}

}
