package de.uniwue.algogis.viewshed;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class App {
	public static void main(String[] args) {
		String path = "resources/small-version-of-dgm_2.grd";
		
		try (Scanner scanner = new Scanner(new File(path), "UTF-8");) {
			scanner.useLocale(Locale.US);
			
			scanner.next("ncols");
			int cols = scanner.nextInt();
			
			scanner.next("nrows");
			int rows =  scanner.nextInt();
			
			scanner.next("xllcorner");
			scanner.nextInt();
			
			scanner.next("yllcorner");
			scanner.nextInt();
			
			scanner.next("cellsize");
			scanner.nextInt();
			
			scanner.next("NODATA_value");
			double nullValue =  scanner.nextInt();
			
			double[][] data = new double[rows][cols];
			
			IntStream.range(0, rows).forEach(row -> {
				IntStream.range(0, cols).forEach(col -> {
					data[row][col] = scanner.nextDouble();
				});
			});
			
			System.out.println(Stream.of(data).map(Arrays::toString).collect(Collectors.joining(",\n")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
