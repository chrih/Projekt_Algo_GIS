package de.uniwue.algogis.viewshed;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class App {
    public static double[][] getHeightData(String path) {
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

            double nullValue = -9999; // default
            if (scanner.hasNext("NODATA_value")) {
                scanner.next("NODATA_value");
                nullValue =  scanner.nextDouble();
            }
            
            double[][] data = new double[rows][cols];
            
            IntStream.range(0, rows).forEach(row -> {
                IntStream.range(0, cols).forEach(col -> {
                    data[row][col] = scanner.nextDouble();
                });
            });
            // TODO: alles weitere ignorieren, falls hier noch etwas in der Datei steht?
            return data;
        } catch (FileNotFoundException e) {
            System.err.println("The file could not be found:");
            System.err.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("The input file is malformed (input mismatch)!");
        } catch (NoSuchElementException e) {
            System.err.println("The input file is malformed (no such element)!");
        }
        return null;
    }
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please specify a resource file!");
            return;
        }
        String path = args[0];
        double[][] data = getHeightData(path); // TODO: data in eigene Klasse kapseln (mit metadaten)
    }
}
