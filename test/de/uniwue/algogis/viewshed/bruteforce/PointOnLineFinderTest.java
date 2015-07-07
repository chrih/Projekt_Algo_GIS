package de.uniwue.algogis.viewshed.bruteforce;

import static org.junit.Assert.*;

import de.uniwue.algogis.viewshed.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PointOnLineFinderTest {
	
	@Parameters(name = "x*{0}, y*{1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {{ 1, 1 }, { 1, -1 }, { -1, 1 }, { -1,-1 } });
	}
	
	@Parameter(0)
	public int xFactor;
	
	@Parameter(1)
	public int yFactor;

	@Test
	public void testFindPointsOnLine() {
		Point p1 = p(1*xFactor, 2*yFactor);
		Point p2 = p(3*xFactor, 7*yFactor);

		int[][] expected = {{1,3},{2,3},{2,4},{2,5},{2,6},{3,6}};
		testFindPointsOnLineHelper(p1, p2, expected);
		testFindPointsOnLineHelper(p2, p1, expected);
	}

	@Test
	public void testFindPointsOnLineStraightLeft() {
		Point p1 = p(2*xFactor, 3*yFactor);
		Point p2 = p(5*xFactor, 3*yFactor);

		int[][] expected = {{3,3},{4,3}};
		testFindPointsOnLineHelper(p1, p2, expected);
		testFindPointsOnLineHelper(p2, p1, expected);
	}

	@Test
	public void testFindPointsOnLineStraightUp() {
		Point p1 = p(3*xFactor, 2*yFactor);
		Point p2 = p(3*xFactor, 5*yFactor);
		
		int[][] expected = {{3,3},{3,4}};
		testFindPointsOnLineHelper(p1, p2, expected);
		testFindPointsOnLineHelper(p2, p1, expected);
	}

	private void testFindPointsOnLineHelper(Point p1, Point p2,  int[][] expected) {
		PointOnLineFinder<Point> polf = new PointOnLineFinder<Point>(PointOnLineFinderTest::p);
		Collection<Point> result = polf.findPointsOnLine(p1, p2);
		
		assertEquals(expected.length, result.size());
		
		Stream.of(expected).forEach(c -> {
			Point p = p(c[0]*xFactor, c[1]*yFactor);
			assertThat(result, hasItem(p));
		});
		
		assertThat(result, not(hasItems(p1, p2)));
	}
	
	private static Point p(int x, int y) {
		return new Point(x, y);
	}

}
