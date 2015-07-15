package de.uniwue.algogis.viewshed;

/**
 * A {@link Dem} with setters for height values.
 */
public class ModifiableDem extends Dem {

    public ModifiableDem(int ncols, int nrows, int xllcorner, int yllcorner, int cellsize, double nodata) {
        super(ncols, nrows, xllcorner, yllcorner, cellsize, nodata, new double[nrows][ncols]);
    }

    public ModifiableDem(Dem d) {
        super(d.getNcols(), d.getNrows(), d.getXllcorner(), d.getYllcorner(), d.getCellsize(), d.getNodata(), new double[d.getNrows()][d.getNcols()]);
    }

    public ModifiableDem(int ncols, int nrows, int xllcorner, int yllcorner, int cellsize, double nodata, double[][] data) {
        super(ncols, nrows, xllcorner, yllcorner, cellsize, nodata, data);
    }

    public ModifiableDem(String path) {
        super(path);
    }

    public void setDem(double[][] data) {
        for(int i = 0; i < data.length; i++) {
          System.arraycopy(data[i], 0, this.data, 0, data[i].length);
        }
    }

    public void setHeight(int x, int y, double height) {
        data[y][x] = height;
    }
    
    public void setHeight(HeightedPoint p) {
        setHeight(p, p.getHeight());
    }
    
    public void setHeight(Point p, double height) {
        setHeight(p.getXCoor(), p.getYCoor(), height);
    }
    
    
}
