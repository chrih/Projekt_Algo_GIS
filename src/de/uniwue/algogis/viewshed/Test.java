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
        ModifiableDem diff = new ModifiableDem(viewshed_sweep.getNcols(), viewshed_sweep.getNrows(), 0, 0, 1, -9999);
        for (int x1 = 0; x1 < viewshed_sweep.getNcols(); x1++) {
            for (int y1 = 0; y1 < viewshed_sweep.getNrows(); y1++) {
                diff.setHeight(x1, y1, (viewshed_sweep.getHeight(x1, y1) == viewshed_brute.getHeight(x1, y1) ? 0 : 1)); 
            }
        }
        ExportDems.toPng(viewshed_sweep, path + ".sweep.png", 1);
        ExportDems.toPng(viewshed_brute, path + ".brute.png", 1);
        ExportDems.toPng(diff          , path + "._diff.png", 1);
        System.out.println("Testdaten ausgegeben.");
    }
}