package de.uniwue.algogis.viewshed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Dem implements Iterable<HeightedPoint> {
    
    protected double[][] data;
    private int ncols, nrows, xllcorner, yllcorner, cellsize;
    private double nodata;
    
    public Dem(int ncols, int nrows, int xllcorner, int yllcorner, int cellsize, double nodata, double[][] data) {
        this.ncols = ncols;
        this.nrows = nrows;
        this.xllcorner = xllcorner;
        this.yllcorner = yllcorner;
        this.cellsize = cellsize;
        this.nodata = nodata;
        this.data = data;
    }
    
    public Dem(String path) {
        try (Scanner scanner = new Scanner(new File(path), "UTF-8");) {
            scanner.useLocale(Locale.US);
            
            scanner.next("ncols");
            ncols = scanner.nextInt();
            
            scanner.next("nrows");
            nrows =  scanner.nextInt();
            
            scanner.next("xllcorner");
            xllcorner = scanner.nextInt();
            
            scanner.next("yllcorner");
            yllcorner = scanner.nextInt();
            
            scanner.next("cellsize");
            cellsize = scanner.nextInt();

            nodata = -9999; // default
            if (scanner.hasNext("NODATA_value")) {
                scanner.next("NODATA_value");
                nodata =  scanner.nextDouble();
            }
            
            data = new double[nrows][ncols];
            
            IntStream.range(0, nrows).forEach(row -> {
                IntStream.range(0, ncols).forEach(col -> {
                    data[row][col] = scanner.nextDouble();
                });
            });
            
            if (scanner.hasNext()) {
                throw new InputMismatchException();
            }
            
        } catch (FileNotFoundException e) {
            System.err.println("The file could not be found:");
            System.err.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("The input file is malformed (input mismatch)!");
        } catch (NoSuchElementException e) {
            System.err.println("The input file is malformed (no such element)!");
        }
    }
    
    public int getNcols() {
        return ncols;
    }
    
    public int getNrows() {
        return nrows;
    }
    
    public int getXllcorner() {
        return xllcorner;
    }
    
    public int getYllcorner() {
        return yllcorner;
    }
    
    public int getCellsize() {
        return cellsize;
    }
    
    public double getNodata() {
        return nodata;
    }
    
    public double getHeight(int x, int y) {
        return data[y][x];
    }
    
    public HeightedPoint getHeightedPoint(int x, int y) {
        return new HeightedPoint(x, y, getHeight(x, y));
    }
    
    public HeightedPoint getHeightedPoint(Point p) {
        return getHeightedPoint(p.getXCoor(), p.getYCoor());
    }
    
    public double[][] getDem() {
        double[][] copy = new double[nrows][ncols];
        for(int i = 0; i < data.length; i++) {
              System.arraycopy(data[i], 0, copy[i], 0, data[i].length);
            }
        return data;
    }
    
    @Override
    public String toString() {
        return Stream.of(data).map(Arrays::toString).collect(Collectors.joining(",\n"));
    }
    
    public void exportToFile(String path) {
        File file = new File(path);
        
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern("#0.0#");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append("ncols\t"+getNcols());
            writer.newLine();
            writer.append("nrows\t"+getNrows());
            writer.newLine();
            writer.append("xllcorner\t"+getXllcorner());
            writer.newLine();
            writer.append("yllcorner\t"+getYllcorner());
            writer.newLine();
            writer.append("cellsize\t"+getCellsize());
            writer.newLine();
            writer.append("NODATA_value\t"+getNodata());
            writer.newLine();
            
            for(int j = 0; j < nrows; j++) {
                for(int i = 0; i < ncols; i++) {
                    writer.append(df.format(data[j][i]));
                    if (i != ncols-1)
                        writer.append(" ");
                }
                if (j != nrows-1)
                    writer.newLine();
            }
            
        }
        catch (IOException e) {
            System.err.println("An error occurred while exporting the data.");
        }
        
        
    }
    
    public Stream<HeightedPoint> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Iterator<HeightedPoint> iterator() {
        return new DemIterator();
    }
    
    public class DemIterator implements Iterator<HeightedPoint> {
        private int x;
        private int y;
        
        @Override
        public boolean hasNext() {
            return y < getNrows();
        }
        @Override
        public HeightedPoint next() {
            if(!hasNext()) throw new NoSuchElementException();
            HeightedPoint next = getHeightedPoint(x, y);
            x++;
            if(x >= getNcols()) {
                x = 0;
                y++;
            }
            return next;
        }
        
    }

}
