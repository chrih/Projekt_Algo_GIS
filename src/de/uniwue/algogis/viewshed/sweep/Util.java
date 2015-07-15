package de.uniwue.algogis.viewshed.sweep;

import de.uniwue.algogis.viewshed.Dem;
import de.uniwue.algogis.viewshed.HeightedPoint;

public class Util {
    /**
    *
    * @param d Eingabe-DEM
    * @param viewpoint Startpunkt, Punkt der Beobachtung
    * @return
    * alle Punkte, die rechts neben dem Startpunkt liegen und die gleiche y-Koordinate haben
    */
   public static HeightedPoint[] pointsOnLine(Dem d, HeightedPoint viewpoint) {
       int xCoor = viewpoint.getXCoor();
       int maxXCoor = d.getNcols() - 1;
       HeightedPoint[] onLine = new HeightedPoint[maxXCoor - xCoor];
       int j = 0;
       for (int i = (xCoor + 1); i <= maxXCoor; i++) {
           onLine[j] = d.getHeightedPoint(i, viewpoint.getYCoor());
           j++;
       }
       return onLine;
   }


}
