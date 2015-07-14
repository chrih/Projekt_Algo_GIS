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
    
    /**
     * Compare this point to another point by position only, ignoring differences in height
     * @param p Other point
     * @return If <tt>this.x = p.x && this.y = p.y</tt>
     */
    public boolean equalsPosition(Point p) {
    	return p != null && p.getXCoor() == getXCoor() && p.getYCoor() == getYCoor(); 
    }
    
    /**
     * Calculate the slope to another point in 3D space.
     * @param to Other point
     * @return A value <tt>s</tt> such that <tt> to.height - this.height = s * distance</tt>.
     */
    public double calcSlope(HeightedPoint to) {
        return ((to.getHeight()-this.getHeight())/calcDistance(to));
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

    @Override
    public String toString() {
        return "HeightedPoint [getXCoor()=" + getXCoor() + ", getYCoor()=" + getYCoor() + ", height=" + height + "]";
    }
}