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
 * @author chrissy
 */
public class vanKreveldNew implements ViewshedAnalysis {

    private PriorityQueue<SweepEvent> eventList;
    private ModifiableDem result;
    private StatusStructure statStruc;
    private boolean[][] isInEventlist;
    private int maxX;
    private int maxY;

    @Override
    public Dem calculateViewshed(Dem d, HeightedPoint origin) {

        maxX = d.getNcols() - 1;
        maxY = d.getNrows() - 1;
        result = new ModifiableDem(d);
        int listCapacity = calcMaxListCapacity(d, origin);
        eventList = new PriorityQueue<SweepEvent>(listCapacity);
        statStruc = new StatusStructure(origin);

        isInEventlist = new boolean[maxX + 1][maxY + 1];

        result.setHeight(origin, 1);
        isInEventlist[origin.getXCoor()][origin.getYCoor()] = true;

        // Liste mit Punkten rechts des Startpunktes in den Baum einfuegen
        for (HeightedPoint hp : pointsOnLine(d, origin)) {
            int x = hp.getXCoor();
            int y = hp.getYCoor();
            statStruc.insert(hp);
            eventList.offer(new SweepEvent(hp, SweepEvent.EventType.IN, origin));
            eventList.offer(new SweepEvent(hp, SweepEvent.EventType.CENTER, origin));
            eventList.offer(new SweepEvent(hp, SweepEvent.EventType.OUT, origin));
            isInEventlist[x][y] = true;
        }

        while (!eventList.isEmpty()) {
            SweepEvent s = eventList.poll();
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
            updateEventlist(d, origin, p);
        }
        return result;
    }

    private void updateEventlist(Dem d, HeightedPoint origin, HeightedPoint hp) {
        int x = hp.getXCoor();
        int y = hp.getYCoor();
        // rechte Seite
        if (x == maxX) {
            // rechte untere Ecke
            if (y == maxY) {
                getNeighbours(d, hp, origin, 9);
                // rechte obere Ecke
            } else if (y == 0) {
                getNeighbours(d, hp, origin, 3);
            } else {
                getNeighbours(d, hp, origin, 6);
            }
            // linke Seite
        } else if (x == 0) {
            // linke untere Ecke
            if (y == maxY) {
                getNeighbours(d, hp, origin, 7);
                // linke obere Ecke
            } else if (y == 0) {
                getNeighbours(d, hp, origin, 1);
            } else {
                getNeighbours(d, hp, origin, 4);
            }
            // oberer Rand
        } else if (y == 0) {
            getNeighbours(d, hp, origin, 2);
        } else if (y == maxY) {
            getNeighbours(d, hp, origin, 8);
        } else {
            getNeighbours(d, hp, origin, 5);
        }
    }

    private void getNeighbours(Dem d, HeightedPoint reference, HeightedPoint origin, int i) {
        int x = reference.getXCoor();
        int y = reference.getYCoor();
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
//        m.exportToFile("out.grd");
//    }
}
