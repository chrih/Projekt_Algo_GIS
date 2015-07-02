package de.uniwue.algogis.viewshed.bruteforce;

import java.util.Collection;
import java.util.LinkedList;

import de.uniwue.algogis.viewshed.Point;

public class PointOnLineFinder {

	public Collection<Point> findPointsOnLine(Point from, Point to) {
		Collection<Point> result = new LinkedList<>();
		
		int xStep = signum(to.getXCoor() - from.getXCoor());
		int yStep =  signum(to.getYCoor() - from.getYCoor());
		
		if(to.getXCoor() == from.getXCoor()) {
			int x = to.getXCoor();
			int yStart = from.getYCoor();
			int yEnd   = to.  getYCoor();
			
			generatePoints(result, x, yStart, yEnd, yStep, from, to);
		} else {
			double a = (double) (to.getYCoor()-from.getYCoor())/(to.getXCoor()-from.getXCoor());
			double b = from.getYCoor() + 0.5*yStep - a * (from.getXCoor() + 0.5*xStep);  
			
			for(int x = from.getXCoor(); x*xStep <= to.getXCoor()*xStep; x += xStep) {
				
				int yStart = (int) Math.max(Math.floor((a* x       +b)*yStep), from.getYCoor()*yStep)*yStep;
				int yEnd   = (int) Math.min(Math.floor((a*(x+xStep)+b)*yStep), to.  getYCoor()*yStep)*yStep;
				
				generatePoints(result, x, yStart, yEnd, yStep, from, to);
			}
		}
		
		return result;
	}
	
	private void generatePoints(Collection<Point> result, int x, int yStart, int yEnd, int yStep, Point from, Point to) {
		for(int y = yStart; y*yStep <= yEnd*yStep; y += yStep) {
			if(   (x != from.getXCoor() || y != from.getYCoor())
			   && (x != to.  getXCoor() || y != to.  getYCoor())) {
				result.add(new Point(x, y, 0));
			}
		}
	}
	
	/**
	 * Special sig function
	 * @param a Input
	 * @return -1 for negative, 1 for positive and zero.
	 */
	private static int signum(int a) {
		return a >= 0 ? 1 : -1;
	}

}
