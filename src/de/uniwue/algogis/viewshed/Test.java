package de.uniwue.algogis.viewshed;

import java.util.Arrays;
import java.util.function.Supplier;

import de.uniwue.algogis.viewshed.sweep.vanKreveld;
import de.uniwue.algogis.viewshed.sweep.VanKreveldList;
import de.uniwue.algogis.viewshed.sweep.vanKreveldNew;
import de.uniwue.algogis.viewshed.bruteforce.BruteForceViewshedAnalysis;

/**
 * Test and benchmark the various {@link ViewshedAnalysis} implementations.
 */
public class Test {
    public static void main(String args[]) {
        if (args.length < 1) {
            System.err.println("Please specify a resource file!");
            return;
        }
        if (args.length < 3) {
            System.err.println("Please specify a point!");
            return;
        }
        String path = args[0];
        Dem d = new Dem(path);
        
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        
        ViewshedAnalysis[] vas = {
                new vanKreveld(),
                new BruteForceViewshedAnalysis(),
                new BruteForceViewshedAnalysis(true),
                new vanKreveldNew(),
                new VanKreveldList()
                };
        
        Dem[] viewsheds;
        if (args.length >= 3) {
            System.out.println("Calculating Viewsheds for the Point (" + x + "," + y + ")");
            Point p = new Point(x, y);
            viewsheds = Arrays.stream(vas)
                    .map(va -> benchmark(va.toString(), () -> va.calculateViewshed(d, p)))
                    .toArray(n -> new Dem[n]);
        } else {
            double h = Double.parseDouble(args[3]);
            System.out.println("Calculating Viewshed for the Point (" + x + "," + y + ") at height " + h);
            HeightedPoint p = new HeightedPoint(x, y, h);
            viewsheds = Arrays.stream(vas)
                    .map(va -> benchmark(va.toString(), () -> va.calculateViewshed(d, p)))
                    .toArray(n -> new Dem[n]);
        }
        for (int i = 1; i < vas.length; i++) {
            ModifiableDem diff = new ModifiableDem(viewsheds[0].getNcols(), viewsheds[0].getNrows(), 0, 0, 1, -9999);
            for (HeightedPoint p : viewsheds[0]) {
                boolean isEqual = p.getHeight() == viewsheds[i].getHeightedPoint(p).getHeight();
                diff.setHeight(p.getXCoor(), p.getYCoor(), (isEqual ? 0 : 1)); 
            }
            ExportDems.toPng(diff, String.format("%s.%s.diff.png", path, vas[i].toString()), 1);
        }
        for (int i = 0; i < vas.length; i++) {
            ExportDems.toPng(viewsheds[i], String.format("%s.%s.png", path, vas[i].toString()), 1);
        }
        System.out.println("Testdaten ausgegeben.");
    }

    private static <R> R benchmark(String name, Supplier<R> dut) {
        long start = System.nanoTime();
        R result = dut.get();
        long end = System.nanoTime();
        System.out.println(String.format("%s %,15d ns", name, end - start));
        return result;
    }
}