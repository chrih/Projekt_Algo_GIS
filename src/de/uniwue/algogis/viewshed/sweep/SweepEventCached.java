package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.HeightedPoint;

/**
 * The same as {@link SweepEvent}, but caches the angle and distance calculations.
 */
public class SweepEventCached extends SweepEvent {

    // Initialize Caches with NaN indicating that value is not yet calculated:
    private double distance = Double.NaN;
    private double angle = Double.NaN;

    public SweepEventCached(HeightedPoint p, EventType t, HeightedPoint viewport) {
        super(p, t, viewport);
    }

    @Override
    public double calcAngle() {
        if(Double.isNaN(angle)) {
            angle = super.calcAngle();
        }
        return angle;
    }
    
    @Override
    public double calcDistance() {
        if (Double.isNaN(distance)) {
            distance = super.calcDistance();
        }
        return distance;
    }

}
