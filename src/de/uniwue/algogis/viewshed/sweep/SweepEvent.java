package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.HeightedPoint;
import de.uniwue.algogis.viewshed.Point;

/*
 * TODO: Diese Klasse muss irgendwie geordnet werden können.
 *       Dazu kann man diese Klasse Comparable<SweepEvent> implementieren lassen
 *       oder dem Konstruktor von PriorityQueue einen Comparator übergeben.
 *
 *       Wahrscheinlich ist es auch sinnvoll, noch ein Attribut für den Winkel hinzuzufügen
 *        (welches dann für die Implementierung von Comparable<> verwendet werden kann).
 */
public class SweepEvent implements Comparable<SweepEvent>{
    public enum EventType {
        IN, CENTER, OUT
    }

    private HeightedPoint point, view;
    private EventType type;

    public SweepEvent(HeightedPoint p, EventType t, HeightedPoint viewport) {
        this.point = p;
        this.type = t;
        this.view = viewport;
    }

    public HeightedPoint getPoint() {
        return this.point;
    }

    public EventType getType() {
        return this.type;
    }

    public double calcAngle() {
    	double dy = point.getYCoor()-view.getYCoor();
    	double dx = point.getXCoor()-view.getXCoor(); 
    	
    	if (dy == 0 && dx > 0 && type == EventType.IN) {
    		double angle = Math.atan2(dy+0.5, dx+0.5);
    		angle += 2*Math.PI;
        	return angle;
    	} else if (dy == 0 && dx > 0 && type == EventType.OUT) {
    		double angle = Math.atan2(dy+0.5, dx-0.5);
        	return angle;
        } else if (type == EventType.CENTER) {
    		double angle =  Math.atan2(dy,dx);
        	if (angle < 0) {
        		angle += 2*Math.PI;
        	}
        	return angle;
    	} else if (type == EventType.IN) {
    		double a1 = Math.atan2(dy+0.5, dx+0.5);  
    		if (a1 < 0) a1 += 2*Math.PI;
    		double a2 = Math.atan2(dy-0.5, dx+0.5);
    		if (a2 < 0) a2 += 2*Math.PI;
    		double a3 = Math.atan2(dy+0.5, dx-0.5);
    		if (a3 < 0) a3 += 2*Math.PI;
    		double a4 = Math.atan2(dy-0.5, dx-0.5);
    		if (a4 < 0) a4 += 2*Math.PI;
    		
    		double angle = Math.min(Math.min(a1, a2), Math.min(a3, a4));
        	return angle;
    	} else {
    		double a1 = Math.atan2(dy+0.5, dx+0.5);  
    		if (a1 < 0) a1 += 2*Math.PI;
    		double a2 = Math.atan2(dy-0.5, dx+0.5);
    		if (a2 < 0) a2 += 2*Math.PI;
    		double a3 = Math.atan2(dy+0.5, dx-0.5);
    		if (a3 < 0) a3 += 2*Math.PI;
    		double a4 = Math.atan2(dy-0.5, dx-0.5);
    		if (a4 < 0) a4 += 2*Math.PI;
    		
    		double angle = Math.max(Math.max(a1, a2), Math.max(a3, a4));
        	if (angle < 0) {
        		angle += 2*Math.PI;
        	}
        	return angle;
    	}
    }
    
    public double calcDistance() {
    	return view.calcDistance(point);
    }
    
	@Override
	public int compareTo(SweepEvent b) {
		if(calcAngle() < b.calcAngle()) {
			return -1;
		} else if (calcAngle() > b.calcAngle()) {
			return 1;
		} else if (calcDistance() < b.calcDistance()) {
			return -1;
		} else if (calcDistance() > b.calcDistance()) {
			return 1;
		} else if (this.type == EventType.IN) {
			return -1;
		}
		return 0;
	}
}
