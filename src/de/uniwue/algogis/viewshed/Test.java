package de.uniwue.algogis.viewshed;

import de.uniwue.algogis.viewshed.sweep.vanKreveld;
import de.uniwue.algogis.viewshed.bruteforce.BruteForceViewshedAnalysis;

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
        vanKreveld v = new vanKreveld();
        BruteForceViewshedAnalysis b = new BruteForceViewshedAnalysis();
        Dem viewshed_sweep;
        Dem viewshed_brute;
        if (args.length == 3) {
            System.out.println("Calculating Viewshed for the Point (" + x + "," + y + ")");
            Point p = new Point(x, y);
            viewshed_sweep = v.calculateViewshed(d, p);
            viewshed_brute = b.calculateViewshed(d, p);
        } else {
            double h = Double.parseDouble(args[3]);
            System.out.println("Calculating Viewshed for the Point (" + x + "," + y + ") at height " + h);
            HeightedPoint p = new HeightedPoint(x, y, h);
            viewshed_sweep = v.calculateViewshed(d, p);
            viewshed_brute = b.calculateViewshed(d, p);
        }
        ExportDems.toPng(viewshed_sweep, path + ".sweep.png", 20);
        ExportDems.toPng(viewshed_brute, path + ".brute.png", 20);
        System.out.println("Testdaten ausgegeben.");
    }
}