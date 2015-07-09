package de.uniwue.algogis.viewshed;

import static org.junit.Assert.*;

import org.junit.Test;

public class PointTest {

    @Test
    public void testGetXCoor() {
        Point p = new Point(10, 0);
        assertEquals(10, p.getXCoor());
    }

    @Test
    public void testGetYCoor() {
        Point p = new Point(0, 10);
        assertEquals(10, p.getYCoor());
    }

    @Test
    public void testCalcDistance() {
        Point p1 = new Point(2, 4);
        Point p2 = new Point(6, 7);
        assertEquals(5.0, p1.calcDistance(p2), 0.0001);
    }
    
    @Test
    public void testCalcGradient() {
        Point p1 = new Point(2, 4);
        Point p2 = new Point(6, 7);
        assertEquals(0.75, p1.calcGradient(p2), 0.0001);
    }
    
    @Test
    public void testEquals() {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        Point p3 = new Point(1, 0);
        Point p4 = new Point(0, 2);
        String p5 = "test";
        assertEquals(p1, p1);
        assertEquals(p2, p1);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, p4);
        assertNotEquals(p1, p5);
        assertNotEquals(p5, p1);
    }

}
