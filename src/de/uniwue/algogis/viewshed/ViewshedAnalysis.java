package de.uniwue.algogis.viewshed;

public interface ViewshedAnalysis {
    
    public default Dem calculateViewshed(Dem d, Point origin) {
        HeightedPoint heightedOrigin = d.getHeightedPoint(origin.getXCoor(), origin.getYCoor());
        return calculateViewshed(d, heightedOrigin);
    }
    
    public abstract Dem calculateViewshed(Dem d, HeightedPoint origin);
    
}
