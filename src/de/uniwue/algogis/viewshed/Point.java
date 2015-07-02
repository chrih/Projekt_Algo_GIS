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

    private double height;
    private double slope;
    private int xCoor;
    private int yCoor;

    public Point(int x, int y, double h) {
        this.xCoor = x;
        this.yCoor = y;
        this.height = h;
    }

    public double getHeight() {
        return this.height;
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

    public double calcSlope(Point p1) {
        return ((p1.getHeight()-this.getHeight())/calcDistance(p1));
    }
}
