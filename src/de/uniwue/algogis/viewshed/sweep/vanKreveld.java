/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.Dem;
import de.uniwue.algogis.viewshed.HeightedPoint;
import de.uniwue.algogis.viewshed.ModifiableDem;
import de.uniwue.algogis.viewshed.ViewshedAnalysis;
import java.util.PriorityQueue;

/**
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
            if (!p.equals(origin)) {
                eventList.offer(new SweepEvent(p, SweepEvent.EventType.IN, origin));
                eventList.offer(new SweepEvent(p, SweepEvent.EventType.OUT, origin));
                eventList.offer(new SweepEvent(p, SweepEvent.EventType.CENTER, origin));
            }
        }

        // Liste mit Punkten rechts des Startpunktes in den Baum einfuegen
        for (HeightedPoint hp : pointsOnLine(input, origin)) {
            statStruc.insert(hp);
        }

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

    /**
     *
     * @param input Eingabe-DEM
     * @param viewpoint Startpunkt, Punkt der Beobachtung
     * @return
     * alle Punkte, die rechts neben dem Startpunkt liegen und die gleiche x-Koordinate haben
     */
    private HeightedPoint[] pointsOnLine(ModifiableDem input, HeightedPoint viewpoint) {
        int xCoor = viewpoint.getXCoor();
        int maxXCoor = input.getNcols();
        HeightedPoint[] onLine = new HeightedPoint[maxXCoor - xCoor - 1];
        int j = 0;
        for (int i = (xCoor + 1); i < maxXCoor; i++) {
            onLine[j] = input.getHeightedPoint(i, viewpoint.getYCoor());
            j++;
        }
        return onLine;
    }

//    public static void main(String[] args) {
//        Dem test = new Dem("testfile_circle.grd");
//        HeightedPoint viewpoint = new HeightedPoint(5, 5, 60);
//
//        vanKreveld viewshed = new vanKreveld();
//        Dem result = viewshed.calculateViewshed(test, viewpoint);
//        result.exportToFile("testfile_circle_out.grd");
//    }
}
