package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.Dem;
import de.uniwue.algogis.viewshed.HeightedPoint;
import de.uniwue.algogis.viewshed.ModifiableDem;
import de.uniwue.algogis.viewshed.ViewshedAnalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * The same algorithm as {@link vanKreveld}, but using a {@link List} instead of
 * a priority queue for the events.
 */
public class VanKreveldList implements ViewshedAnalysis {
    // event list als Prioritaetswarteschlange
    List<SweepEventCached> eventList;
    // Baum, in dem die Sweepevents gespeichert werden
    StatusStructure statStruc;

    @Override
    public Dem calculateViewshed(Dem d, HeightedPoint origin) {
        int listCapacity = d.getNcols() * d.getNrows() * 3;
        eventList = new ArrayList<SweepEventCached>(listCapacity);
        ModifiableDem input = new ModifiableDem(d);
        statStruc = new StatusStructure(origin);
        for (HeightedPoint p : d) {
            // fuellen der eventlist mit allen punkten
            if (!p.equalsPosition(origin)) {
                eventList.add(new SweepEventCached(p, SweepEventCached.EventType.IN, origin));
                eventList.add(new SweepEventCached(p, SweepEventCached.EventType.OUT, origin));
                eventList.add(new SweepEventCached(p, SweepEventCached.EventType.CENTER, origin));
            }
        }
        eventList.sort(null);

        // Liste mit Punkten rechts des Startpunktes in den Baum einfuegen
        for (HeightedPoint hp : Util.pointsOnLine(d, origin)) {
            statStruc.insert(hp);
        }

        // the origin is always visible
        input.setHeight(origin, 1);

        // die event list durchgehen
        for (SweepEventCached s : eventList) {
            switch (s.getType()) {
                // wenn IN: Punkt in Baum einfuegen
                case IN:
                    statStruc.insert(s.getPoint());
                    break;
                // wenn CENTER: berechne, ob Punkt sichtbar ist von Startpunkt aus
                case CENTER:
                    if (statStruc.isVisible(s.getPoint())) {
                        // falls ja: in neues DEM einfuegen
                        input.setHeight(s.getPoint(), 1);
                    } else {
                        input.setHeight(s.getPoint(), 0);
                    }
                    break;
                // wenn OUT: aus Baum loeschen
                case OUT:
                    statStruc.delete(s.getPoint());
                    break;
                default:
                    break;
            }
        }
        return input;
    }

    @Override
    public String toString() {
        return "vkbfh";
    }
}
