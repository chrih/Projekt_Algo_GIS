/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniwue.algogis.viewshed;

/**
 *
 * @author chrissy
 */
public class Point {

    private int xCoor;
    private int yCoor;

    public Point(int x, int y) {
        this.xCoor = x;
        this.yCoor = y;
    }

    public int getXCoor() {
        return this.xCoor;
    }

    public int getYCoor() {
        return this.yCoor;
    }

    public double calcDistance(Point p1) {
        double distX = this.getXCoor() - p1.getXCoor();
        double distY = this.getYCoor() - p1.getYCoor();
        return Math.sqrt(distX*distX + distY*distY);
    }

	@Override
	public String toString() {
		return "Point [xCoor=" + xCoor + ", yCoor=" + yCoor + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + xCoor;
		result = prime * result + yCoor;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (xCoor != other.xCoor)
			return false;
		if (yCoor != other.yCoor)
			return false;
		return true;
	}
    
}
