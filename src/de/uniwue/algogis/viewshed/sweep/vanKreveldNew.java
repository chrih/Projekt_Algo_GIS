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

import java.util.PriorityQueue;

/**
 *
 * @author Christina Hempfling, Moritz Beck, Jona Kalkus, Bernhard Häussner
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
        for (HeightedPoint hp : Util.pointsOnLine(d, origin)) {
            int x = hp.getXCoor();
            int y = hp.getYCoor();
            statStruc.insert(hp);
            eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
            eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
            eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
            isInEventlist[x][y] = true;
        }

        // event list durchgehen
        while (!eventList.isEmpty()) {
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
     * 1+++2+++3
     * +++++++++
     * 4+++5+++6
     * +++++++++
     * 7++8++++9 
     * 
     * 1 = linke obere Ecke
     * 2 = oberer Rand
     * 3 = rechte obere Ecke
     * 4 = linker Rand
     * 5 = irgendwo mittendrin
     * 6 = rechter Rand
     * 7 = linke untere Ecke 
     * 8 = unterer Rand 
     * 9 = rechte untere Ecke
     * 
     */
    private void updateEventlist(Dem d, HeightedPoint origin, HeightedPoint hp) {
        int x = hp.getXCoor();
        int y = hp.getYCoor();
        // rechte Seite
        if (x == maxX) {
            // rechte untere Ecke
            if (y == maxY) {
                insertNeighboursInEventlist(d, hp, origin, 9);
                // rechte obere Ecke
            } else if (y == 0) {
                insertNeighboursInEventlist(d, hp, origin, 3);
            } else {
                insertNeighboursInEventlist(d, hp, origin, 6);
            }
            // linke Seite
        } else if (x == 0) {
            // linke untere Ecke
            if (y == maxY) {
                insertNeighboursInEventlist(d, hp, origin, 7);
                // linke obere Ecke
            } else if (y == 0) {
                insertNeighboursInEventlist(d, hp, origin, 1);
            } else {
                insertNeighboursInEventlist(d, hp, origin, 4);
            }
            // oberer Rand
        } else if (y == 0) {
            insertNeighboursInEventlist(d, hp, origin, 2);
        } else if (y == maxY) {
            insertNeighboursInEventlist(d, hp, origin, 8);
        } else {
            insertNeighboursInEventlist(d, hp, origin, 5);
        }
    }

    /**
     * 
     * @param d DEM
     * @param reference aktueller Pixel
     * @param origin Standpunkt
     * @param i Typ des Pixel, bestimmt die Lage im grid (siehe Kommentar zu Funktion updateEventlist)
     * 
     * findet alle Nachbarn eines Pixels, die noch nicht in der event list sind und fuegt sie ein
     * 
     * Bezeichner der Nachbarn:
     * +++++++++++++++++++++++++++++++
     * | leftup   | up   | rightup   |
     * +++++++++++++++++++++++++++++++
     * | left     |      | right     |
     * +++++++++++++++++++++++++++++++
     * | leftdown | down | rightdown |
     * +++++++++++++++++++++++++++++++
     * 
     */
    private void insertNeighboursInEventlist(Dem d, HeightedPoint reference, HeightedPoint origin, int i) {
        int x = reference.getXCoor();
        int y = reference.getYCoor();
        // alle moeglichen Nachbarn 
        HeightedPoint leftup, left, leftdown, down, rightdown, right, rightup, up;

        switch (i) {
            case 1:
                down = d.getHeightedPoint(x, y + 1);
                rightdown = d.getHeightedPoint(x + 1, y + 1);
                right = d.getHeightedPoint(x + 1, y);
                HeightedPoint[] temp1 = {right, rightdown, down};
                for (HeightedPoint hp : temp1) {
                    int xCoor = hp.getXCoor();
                    int yCoor = hp.getYCoor();
                    if (!isInEventlist[xCoor][yCoor]) {
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
                        isInEventlist[xCoor][yCoor] = true;
                    }
                }
                break;
            case 2:
                left = d.getHeightedPoint(x - 1, y);
                leftdown = d.getHeightedPoint(x - 1, y + 1);
                down = d.getHeightedPoint(x, y + 1);
                rightdown = d.getHeightedPoint(x + 1, y + 1);
                right = d.getHeightedPoint(x + 1, y);
                HeightedPoint[] temp2 = {left, leftdown, down, rightdown, right};
                for (HeightedPoint hp : temp2) {
                    int xCoor = hp.getXCoor();
                    int yCoor = hp.getYCoor();
                    if (!isInEventlist[xCoor][yCoor]) {
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
                        isInEventlist[xCoor][yCoor] = true;
                    }
                }
                break;
            case 3:
                left = d.getHeightedPoint(x - 1, y);
                leftdown = d.getHeightedPoint(x - 1, y + 1);
                down = d.getHeightedPoint(x, y + 1);
                HeightedPoint[] temp3 = {left, leftdown, down};
                for (HeightedPoint hp : temp3) {
                    int xCoor = hp.getXCoor();
                    int yCoor = hp.getYCoor();
                    if (!isInEventlist[xCoor][yCoor]) {
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
                        isInEventlist[xCoor][yCoor] = true;
                    }
                }
                break;
            case 4:
                down = d.getHeightedPoint(x, y + 1);
                rightdown = d.getHeightedPoint(x + 1, y + 1);
                right = d.getHeightedPoint(x + 1, y);
                rightup = d.getHeightedPoint(x + 1, y - 1);
                up = d.getHeightedPoint(x, y - 1);
                HeightedPoint[] temp4 = {up, rightup, right, rightdown, down};
                for (HeightedPoint hp : temp4) {
                    int xCoor = hp.getXCoor();
                    int yCoor = hp.getYCoor();
                    if (!isInEventlist[xCoor][yCoor]) {
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
                        isInEventlist[xCoor][yCoor] = true;
                    }
                }
                break;
            case 5:
                leftup = d.getHeightedPoint(x - 1, y - 1);
                left = d.getHeightedPoint(x - 1, y);
                leftdown = d.getHeightedPoint(x - 1, y + 1);
                down = d.getHeightedPoint(x, y + 1);
                rightdown = d.getHeightedPoint(x + 1, y + 1);
                right = d.getHeightedPoint(x + 1, y);
                rightup = d.getHeightedPoint(x + 1, y - 1);
                up = d.getHeightedPoint(x, y - 1);
                HeightedPoint[] temp5 = {up, down, right, left, rightup, rightdown, leftdown, leftup};
                for (HeightedPoint hp : temp5) {
                    int xCoor = hp.getXCoor();
                    int yCoor = hp.getYCoor();
                    if (!isInEventlist[xCoor][yCoor]) {
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
                        isInEventlist[xCoor][yCoor] = true;
                    }
                }
                break;
            case 6:
                leftup = d.getHeightedPoint(x - 1, y - 1);
                left = d.getHeightedPoint(x - 1, y);
                leftdown = d.getHeightedPoint(x - 1, y + 1);
                down = d.getHeightedPoint(x, y + 1);
                up = d.getHeightedPoint(x, y - 1);
                HeightedPoint[] temp6 = {up, leftup, left, leftdown, down};
                for (HeightedPoint hp : temp6) {
                    int xCoor = hp.getXCoor();
                    int yCoor = hp.getYCoor();
                    if (!isInEventlist[xCoor][yCoor]) {
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
                        isInEventlist[xCoor][yCoor] = true;
                    }
                }
                break;
            case 7:
                right = d.getHeightedPoint(x + 1, y);
                rightup = d.getHeightedPoint(x + 1, y - 1);
                up = d.getHeightedPoint(x, y - 1);
                HeightedPoint[] temp7 = {up, rightup, right};
                for (HeightedPoint hp : temp7) {
                    int xCoor = hp.getXCoor();
                    int yCoor = hp.getYCoor();
                    if (!isInEventlist[xCoor][yCoor]) {
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
                        isInEventlist[xCoor][yCoor] = true;
                    }
                }
                break;
            case 8:
                leftup = d.getHeightedPoint(x - 1, y - 1);
                left = d.getHeightedPoint(x - 1, y);
                up = d.getHeightedPoint(x, y - 1);
                right = d.getHeightedPoint(x + 1, y);
                rightup = d.getHeightedPoint(x + 1, y - 1);

                HeightedPoint[] temp8 = {left, leftup, up, rightup, right};
                for (HeightedPoint hp : temp8) {
                    int xCoor = hp.getXCoor();
                    int yCoor = hp.getYCoor();
                    if (!isInEventlist[xCoor][yCoor]) {
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
                        isInEventlist[xCoor][yCoor] = true;
                    }
                }
                break;
            case 9:
                leftup = d.getHeightedPoint(x - 1, y - 1);
                left = d.getHeightedPoint(x - 1, y);
                up = d.getHeightedPoint(x, y - 1);

                HeightedPoint[] temp9 = {up, leftup, left};
                for (HeightedPoint hp : temp9) {
                    int xCoor = hp.getXCoor();
                    int yCoor = hp.getYCoor();
                    if (!isInEventlist[xCoor][yCoor]) {
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
                        eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
                        isInEventlist[xCoor][yCoor] = true;
                    }
                }
                break;
            default:
                break;
        }
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
//        m.exportToFile("out.grd");
//    }
    
    @Override
    public String toString() {
        return "vknew";
    }
}
