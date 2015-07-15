package de.uniwue.algogis.viewshed;

/**
 * A point with integer coordinates.
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
    
    /**
     * Calculates the euclidean distance to another point in 2D space.
     * @param to Other point
     * @return Distance between this point and <tt>to</tt>
     */
    public double calcDistance(Point to) {
        double distX = this.getXCoor() - to.getXCoor();
        double distY = this.getYCoor() - to.getYCoor();
        return Math.sqrt(distX*distX + distY*distY);
    }

    /**
     * Calculates the direction to the other point in a 2D plane.
     * @param to Other point
     * @return A value <tt>g</tt> such that <tt>dx * g = dy</tt>
     */
    public double calcGradient(Point to) {
       return (double) (to.getYCoor()-this.getYCoor()) / (to.getXCoor()-this.getXCoor());
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
