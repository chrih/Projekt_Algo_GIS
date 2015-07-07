package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.HeightedPoint;

/*
 * TODO: Diese Klasse muss irgendwie geordnet werden können.
 *       Dazu kann man diese Klasse Comparable<SweepEvent> implementieren lassen
 *       oder dem Konstruktor von PriorityQueue einen Comparator übergeben.
 *
 *       Wahrscheinlich ist es auch sinnvoll, noch ein Attribut für den Winkel hinzuzufügen
 *        (welches dann für die Implementierung von Comparable<> verwendet werden kann).
 */
public class SweepEvent {
    public enum EventType {
        IN, CENTER, OUT
    }

    private HeightedPoint point;
    private EventType type;

    public SweepEvent(HeightedPoint p, EventType t) {
        this.point = p;
        this.type = t;
    }

    public HeightedPoint getPoint() {
        return this.point;
    }

    public EventType getType() {
        return this.type;
    }
}
