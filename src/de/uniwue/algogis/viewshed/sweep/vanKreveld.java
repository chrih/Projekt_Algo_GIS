package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.Dem;
import de.uniwue.algogis.viewshed.HeightedPoint;
import de.uniwue.algogis.viewshed.ModifiableDem;
import de.uniwue.algogis.viewshed.ViewshedAnalysis;

import java.util.PriorityQueue;

/**
 * The radial sweep line algorithm as described by van Kreveld (1996)
 * @date 08.07.2015
 * @author Christina Hempfling, Jona Kalkus, Moritz Beck, Bernhard Haeussner
 */
public class vanKreveld implements ViewshedAnalysis {
    // event list als Prioritaetswarteschlange
    PriorityQueue<SweepEvent> eventList;
    // Baum, in dem die Sweepevents gespeichert werden
    StatusStructure statStruc;

    @Override
    public Dem calculateViewshed(Dem d, HeightedPoint origin) {
        int listCapacity = d.getNcols() * d.getNrows() * 3;
        eventList = new PriorityQueue<SweepEvent>(listCapacity);
        ModifiableDem input = new ModifiableDem(d);
        statStruc = new StatusStructure(origin);
        for (HeightedPoint p : d) {
            // fuellen der eventlist mit allen punkten
            if (!p.equalsPosition(origin)) {
                eventList.offer(new SweepEvent(p, SweepEvent.EventType.IN, origin));
                eventList.offer(new SweepEvent(p, SweepEvent.EventType.OUT, origin));
                eventList.offer(new SweepEvent(p, SweepEvent.EventType.CENTER, origin));
            }
        }

        // Liste mit Punkten rechts des Startpunktes in den Baum einfuegen
        for (HeightedPoint hp : Util.pointsOnLine(d, origin)) {
            statStruc.insert(hp);
        }

        // the origin is always visible
        input.setHeight(origin, 1);

        // die event list durchgehen
        while (!eventList.isEmpty()) {
            SweepEvent s = eventList.poll();
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
        return "sweep";
    }
}
