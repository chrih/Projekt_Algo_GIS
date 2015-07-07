package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.HeightedPoint;

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
