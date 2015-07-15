package de.uniwue.algogis.viewshed;

/**
 * When doing a viewshed analysis we want to find all points visible
 * from a given origin point.
 */
public interface ViewshedAnalysis {
    
    public default Dem calculateViewshed(Dem d, Point origin) {
        HeightedPoint heightedOrigin = d.getHeightedPoint(origin.getXCoor(), origin.getYCoor());
        return calculateViewshed(d, heightedOrigin);
    }
    
    public abstract Dem calculateViewshed(Dem d, HeightedPoint origin);
    
}
