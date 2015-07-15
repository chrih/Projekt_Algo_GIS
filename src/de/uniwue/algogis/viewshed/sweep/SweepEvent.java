package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.HeightedPoint;

public class SweepEvent implements Comparable<SweepEvent> {

    public enum EventType {
        IN, CENTER, OUT
    }

    private HeightedPoint point, view;
    private EventType type;
    private double angle;
    private double distance;

    public SweepEvent(HeightedPoint p, EventType t, HeightedPoint viewport) {
        this.point = p;
        this.type = t;
        this.view = viewport;
        this.angle = calcAngle();
        this.distance = calcDistance();
    }

    public HeightedPoint getPoint() {
        return this.point;
    }

    public EventType getType() {
        return this.type;
    }

    public double getAngle() {
        return angle;
    }

    public double getDistance() {
        return distance;
    }

    /**
     * berechnet den Winkel vom Startpunkt aus zu allen vier Ecken eines Pixels
     *
     * atan2-Funktion berechnet die Winkel automatisch richtig fuer jeden
     * Quadranten
     *
     * @return Winkel zwischen Startpunkt und Eckpunkt eines Pixels
     */
    protected double calcAngle() {
        double dy = point.getYCoor() - view.getYCoor();
        double dx = point.getXCoor() - view.getXCoor();

        // Pixel hat selbe y-Koordinate wie Startpunkt, liegt rechts davon und Event hat Typ IN
        // dann wird die entsprechende Ecke des Pixels herausgesucht und der Winkel dazu berechnet
        if (dy == 0 && dx > 0 && type == EventType.IN) {
            double angle = Math.atan2(-0.5, dx - 0.5);
            angle += 2 * Math.PI;
            return angle;
        // Pixel hat selbe y-Koordinate wie Startpunkt, liegt rechts davon und Event hat Typ OUT    
        } else if (dy == 0 && dx > 0 && type == EventType.OUT) {
            double angle = Math.atan2(+0.5, dx - 0.5);
            return angle;
        } else if (type == EventType.CENTER) {
            double angle = Math.atan2(dy, dx);
            if (angle < 0) {
                angle += 2 * Math.PI;
            }
            return angle;
        // EventType IN: vier Ecken als moegliche Kandidaten
        // berechne alle vier Winkel zu den Ecken und nehme den kleinsten
        } else if (type == EventType.IN) {
            double a1 = Math.atan2(dy + 0.5, dx + 0.5);
            if (a1 < 0) {
                a1 += 2 * Math.PI;
            }
            double a2 = Math.atan2(dy - 0.5, dx + 0.5);
            if (a2 < 0) {
                a2 += 2 * Math.PI;
            }
            double a3 = Math.atan2(dy + 0.5, dx - 0.5);
            if (a3 < 0) {
                a3 += 2 * Math.PI;
            }
            double a4 = Math.atan2(dy - 0.5, dx - 0.5);
            if (a4 < 0) {
                a4 += 2 * Math.PI;
            }

            double angle = Math.min(Math.min(a1, a2), Math.min(a3, a4));
            return angle;
        } else {
            double a1 = Math.atan2(dy + 0.5, dx + 0.5);
            if (a1 < 0) {
                a1 += 2 * Math.PI;
            }
            double a2 = Math.atan2(dy - 0.5, dx + 0.5);
            if (a2 < 0) {
                a2 += 2 * Math.PI;
            }
            double a3 = Math.atan2(dy + 0.5, dx - 0.5);
            if (a3 < 0) {
                a3 += 2 * Math.PI;
            }
            double a4 = Math.atan2(dy - 0.5, dx - 0.5);
            if (a4 < 0) {
                a4 += 2 * Math.PI;
            }

            double angle = Math.max(Math.max(a1, a2), Math.max(a3, a4));
            return angle;
        }
    }

    protected double calcDistance() {
        return view.calcDistance(point);
    }

    @Override
    /**
     * vergleicht Winkel zwischen zwei SweepEvents und fuegt sie dementsprechend
     * in die priority queue ein sortiert zuerst nach Winkel, falls dieser
     * gleich ist nach Distanz zum Startpunkt und falls diese gleich ist nach
     * Typ (OUT vor IN)
     */
    public int compareTo(SweepEvent b) {
        if (angle < b.getAngle()) {
            return -1;
        } else if (angle > b.getAngle()) {
            return 1;
        } else if (distance < b.getDistance()) {
            return -1;
        } else if (distance > b.getDistance()) {
            return 1;
        } else if (this.type == EventType.IN) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return point + " " +  type.name() + "\t" + angle;
    }
}
