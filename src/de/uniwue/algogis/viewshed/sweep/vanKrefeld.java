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
public class vanKrefeld implements ViewshedAnalysis {

    boolean[][] pointsVisible;
    PriorityQueue<SweepEvent> eventList;
    StatusStructure statStruc;

    @Override
    public Dem calculateViewshed(Dem d, HeightedPoint origin) {
        ModifiableDem result = new ModifiableDem(d);

        for (HeightedPoint p : d) {
            // fuellen der eventlist mit allen punkten
            if (!p.equals(origin)) {
                eventList.offer(new SweepEvent(p, SweepEvent.EventType.IN, origin));
                eventList.offer(new SweepEvent(p, SweepEvent.EventType.OUT, origin));
                eventList.offer(new SweepEvent(p, SweepEvent.EventType.CENTER, origin));
            }
        }
        for (SweepEvent s : eventList) {
            switch (s.getType()) {
                case IN:
                    statStruc.insert(s.getPoint());
                    break;
                case CENTER:
                    if (statStruc.isVisible(s.getPoint())) {
                        result.setHeight(s.getPoint(), 1);
                    } else {
                        result.setHeight(s.getPoint(), 0);
                    }
                    break;
                case OUT:
                    statStruc.delete(s.getPoint());
                    break;
                default:
                    break;
            }
        }
        return result;
    }
}
