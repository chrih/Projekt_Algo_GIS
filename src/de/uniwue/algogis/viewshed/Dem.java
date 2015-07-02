package de.uniwue.algogis.viewshed;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Dem {
	
	private double[][] data;
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
			// TODO: alles weitere ignorieren, falls hier noch etwas in der Datei steht?
			
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
		return data[x][y];
	}
	
	public HeightedPoint getHeightedPoint(int x, int y) {
		return new HeightedPoint(x, y, data[x][y]);
	}
	
	public double[][] getDem() {
		double[][] copy = new double[data.length][];
		for(int i = 0; i < data.length; i++) {
		  double[] row = data[i];
		  copy[i] = new double[row.length];
		  System.arraycopy(row, 0, copy[i], 0, row.length);
		}
		return copy;
	}

}
