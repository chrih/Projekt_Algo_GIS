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
public class HeightedPoint extends Point {
	
	private double height;

    public HeightedPoint(int x, int y, double h) {
    	super(x, y);
        this.height = h;
    }

    public double getHeight() {
        return this.height;
    }

    public double calcDistance(HeightedPoint p1) {
        double distX = this.getXCoor() - p1.getXCoor();
        double distY = this.getYCoor() - p1.getYCoor();
        return Math.sqrt(distX*distX + distY*distY);
    }

    public double calcSlope(HeightedPoint p1) {
        return ((p1.getHeight()-this.getHeight())/calcDistance(p1));
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = java.lang.Double.doubleToLongBits(height);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + getXCoor();
		result = prime * result + getYCoor();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HeightedPoint other = (HeightedPoint) obj;
		if (java.lang.Double.doubleToLongBits(height) != java.lang.Double.doubleToLongBits(other.height))
			return false;
		if (getXCoor() != other.getXCoor())
			return false;
		if (getYCoor() != other.getYCoor())
			return false;
		return true;
	}

}