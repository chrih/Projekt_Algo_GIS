/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.Dem;
import de.uniwue.algogis.viewshed.HeightedPoint;
import de.uniwue.algogis.viewshed.Point;
import de.uniwue.algogis.viewshed.ModifiableDem;
import de.uniwue.algogis.viewshed.ViewshedAnalysis;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 *
 * @author Christina Hempfling, Moritz Beck, Jona Kalkus, Bernhard Haeussner
 */
public class vanKreveldNew implements ViewshedAnalysis {

    private PriorityQueue<SweepEvent> eventList;
    private ModifiableDem result;
    private StatusStructure statStruc;
    private boolean[][] isInEventlist;
    // maximale x- und <-Koordinate
    private int maxX;
    private int maxY;

    @Override
    public Dem calculateViewshed(Dem d, HeightedPoint origin) {

        maxX = d.getNcols() - 1;
        maxY = d.getNrows() - 1;
        // Ausgabe-DEM
        result = new ModifiableDem(d);
        // maximal noetige Kapazitaet der event list berechnen
        int listCapacity = calcMaxListCapacity(d, origin);
//        System.out.println("list cap: " + listCapacity);
        eventList = new PriorityQueue<SweepEvent>(listCapacity);
        // Baumstruktur mit origin als Wurzelelement
        statStruc = new StatusStructure(origin);

        // zweidimensionales Array, welches fuer jeden Pixel speichert, ob er schon einmal in die event list eingefuegt wurde 
        // false: noch nicht eingefuegt
        isInEventlist = new boolean[maxX + 1][maxY + 1];

        // Standpunkt origin ist immer sichtbar und muss auch nicht in die event list eingefuegt werden => auf true setzen
        result.setHeight(origin, 1);
        isInEventlist[origin.getXCoor()][origin.getYCoor()] = true;

        // sweep line beginnt waagerecht rechts des Standpunkts
        // Liste mit Punkten rechts des Startpunktes in den Baum einfuegen und in event list einfuegen
        for (HeightedPoint hp : pointsOnLine(d, origin)) {
            int x = hp.getXCoor();
            int y = hp.getYCoor();
            statStruc.insert(hp);
            eventList.offer(new SweepEventCached(hp, SweepEvent.EventType.IN, origin));
            eventList.offer(new SweepEventCached(hp, SweepEvent.EventType.CENTER, origin));
            eventList.offer(new SweepEventCached(hp, SweepEvent.EventType.OUT, origin));
            isInEventlist[x][y] = true;
        }

        // event list durchgehen
        while (!eventList.isEmpty()) {
//            System.out.println("size event list: " + eventList.size()); 
            // erstes Element aus der Liste holen
            SweepEvent s = eventList.poll();
            // Punkt dazu berechnen
            HeightedPoint p = s.getPoint();
            switch (s.getType()) {
                // wenn IN: Punkt in Baum einfuegen
                case IN:
                    statStruc.insert(p);
                    break;
                // wenn CENTER: berechne, ob Punkt sichtbar ist von Startpunkt aus
                case CENTER:
                    if (statStruc.isVisible(p)) {
                        // falls ja: in neues DEM einfuegen
                        result.setHeight(p, 1);
                    } else {
                        result.setHeight(p, 0);
                    }
                    break;
                // wenn OUT: aus Baum loeschen
                case OUT:
                    statStruc.delete(p);
                    break;
                default:
                    break;
            }
            // die event list updaten: Nachbarn des Pixels in die event list einfuegen
            updateEventlist(d, origin, p);
        }
        return result;
    }

    /**
     *
     * @param d DEM
     * @param origin Standpunkt
     * @param hp aktueller Pixel
     *
     * es gibt neun Moeglichkeiten, an denen der Pixel im grid liegen kann und
     * je nach Lage hat der Pixel unterschiedlich viele Nachbarn
     *
     * 1+++2+++3 +++++++++ 4+++5+++6 +++++++++ 7++8++++9
     *
     * 1 = linke obere Ecke 2 = oberer Rand 3 = rechte obere Ecke 4 = linker
     * Rand 5 = irgendwo mittendrin 6 = rechter Rand 7 = linke untere Ecke 8 =
     * unterer Rand 9 = rechte untere Ecke
     *
     */
    private void updateEventlist(Dem d, HeightedPoint origin, HeightedPoint hp) {
        int x = hp.getXCoor();
        int y = hp.getYCoor();
        LinkedList<HeightedPoint> neighbours = new LinkedList<HeightedPoint>();
        // rechte Seite
        if (x == maxX) {
            // rechte untere Ecke
            if (y == maxY) {
                neighbours.add(d.getHeightedPoint(x - 1, y));
                neighbours.add(d.getHeightedPoint(x, y - 1));
                neighbours.add(d.getHeightedPoint(x - 1, y - 1));
            } // rechte obere Ecke
            else if (y == 0) {
                neighbours.add(d.getHeightedPoint(x - 1, y));
                neighbours.add(d.getHeightedPoint(x, y + 1));
                neighbours.add(d.getHeightedPoint(x - 1, y + 1));
            } else {
                neighbours.add(d.getHeightedPoint(x, y - 1));
                neighbours.add(d.getHeightedPoint(x, y + 1));
                neighbours.add(d.getHeightedPoint(x - 1, y));
                neighbours.add(d.getHeightedPoint(x - 1, y - 1));
                neighbours.add(d.getHeightedPoint(x - 1, y + 1));
            }
        } // linke Seite
        else if (x == 0) {
            // linke untere Ecke
            if (y == maxY) {
                neighbours.add(d.getHeightedPoint(x, y - 1));
                neighbours.add(d.getHeightedPoint(x + 1, y));
                neighbours.add(d.getHeightedPoint(x + 1, y - 1));
                // linke obere Ecke
            } else if (y == 0) {
                neighbours.add(d.getHeightedPoint(x + 1, y));
                neighbours.add(d.getHeightedPoint(x + 1, y + 1));
                neighbours.add(d.getHeightedPoint(x, y + 1));
            } else {
                neighbours.add(d.getHeightedPoint(x, y - 1));
                neighbours.add(d.getHeightedPoint(x + 1, y - 1));
                neighbours.add(d.getHeightedPoint(x + 1, y));
                neighbours.add(d.getHeightedPoint(x + 1, y + 1));
                neighbours.add(d.getHeightedPoint(x, y + 1));
            }
            // oberer Rand
        } else if (y == 0) {
            neighbours.add(d.getHeightedPoint(x - 1, y));
            neighbours.add(d.getHeightedPoint(x - 1, y + 1));
            neighbours.add(d.getHeightedPoint(x, y + 1));
            neighbours.add(d.getHeightedPoint(x + 1, y + 1));
            neighbours.add(d.getHeightedPoint(x + 1, y));

        } else if (y == maxY) {
            neighbours.add(d.getHeightedPoint(x - 1, y));
            neighbours.add(d.getHeightedPoint(x - 1, y - 1));
            neighbours.add(d.getHeightedPoint(x, y - 1));
            neighbours.add(d.getHeightedPoint(x + 1, y - 1));
            neighbours.add(d.getHeightedPoint(x + 1, y));
        } else {
            neighbours.add(d.getHeightedPoint(x - 1, y));
            neighbours.add(d.getHeightedPoint(x - 1, y - 1));
            neighbours.add(d.getHeightedPoint(x + 1, y));
            neighbours.add(d.getHeightedPoint(x, y + 1));
            neighbours.add(d.getHeightedPoint(x + 1, y + 1));
            neighbours.add(d.getHeightedPoint(x + 1, y - 1));
            neighbours.add(d.getHeightedPoint(x - 1, y + 1));
            neighbours.add(d.getHeightedPoint(x + 1, y));
            neighbours.add(d.getHeightedPoint(x, y - 1));
        }

        for (HeightedPoint p : neighbours) {
            if (!isInEventlist[p.getXCoor()][p.getYCoor()]) {
                eventList.offer(new SweepEventCached(p, SweepEvent.EventType.IN, origin));
                eventList.offer(new SweepEventCached(p, SweepEvent.EventType.CENTER, origin));
                eventList.offer(new SweepEventCached(p, SweepEvent.EventType.OUT, origin));
                isInEventlist[p.getXCoor()][p.getYCoor()] = true;
            }
        }
    }

    /**
     *
     * @param d Eingabe-DEM
     * @param viewpoint Startpunkt, Punkt der Beobachtung
     * @return alle Punkte, die rechts neben dem Startpunkt liegen und die
     * gleiche y-Koordinate haben
     */
    private HeightedPoint[] pointsOnLine(Dem d, HeightedPoint viewpoint) {
        int xCoor = viewpoint.getXCoor();
        int yCoor = viewpoint.getYCoor();
        HeightedPoint[] onLine = new HeightedPoint[maxX - xCoor];
        int j = 0;
        for (int i = (xCoor + 1); i < (maxX + 1); i++) {
            onLine[j] = d.getHeightedPoint(i, yCoor);
            j++;
        }
        return onLine;
    }

    private int calcMaxListCapacity(Dem d, HeightedPoint origin) {
        double dist1 = origin.calcDistance(new Point(0, 0));
        double dist2 = origin.calcDistance(new Point(maxX, 0));
        double dist3 = origin.calcDistance(new Point(0, maxY));
        double dist4 = origin.calcDistance(new Point(maxX, maxY));

        double maxDist = Math.max(Math.max(dist1, dist2), Math.max(dist3, dist4));
        return ((int) maxDist) * 8;
    }

//    public static void main(String[] args) {
//        Dem d = new Dem("resources/dgm_2.grd");
//        HeightedPoint viewpoint = new HeightedPoint(250, 360, 530);
//
//        vanKreveldNew vkn = new vanKreveldNew();
//        Dem m = vkn.calculateViewshed(d, viewpoint);
//        m.exportToFile("out_new_test.grd");
//    }
}
