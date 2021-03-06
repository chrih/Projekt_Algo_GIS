package de.uniwue.algogis.viewshed;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Useful methods to export {@link Dem}s to various other formats.
 */
public class ExportDems {
    
    /**
     * Exports a Dem as a png graphic to the specified path.
     * The exported graphic is grayscale with the maximum value being white
     * and the minimum value being black;
     * The graphic is all black, if there is only one unique height in the Dem.
     */
    public static void toPng(Dem d, String path) {
        toPng(d, path, 1);
    }
    /**
     * Exports a Dem as a png graphic to the specified path.
     * 
     * The exported graphic is grayscale with the maximum value being white
     * and the minimum value being black;
     * The graphic is all black, if there is only one unique height in the Dem.
     * 
     * Every Point of the Dem will occupy a square 
     * of size scale * scale pixels in the graphic.
     */
    public static void toPng(Dem d, String path, int scale) {
        if (scale < 1) {
            throw new IllegalArgumentException("Scale must be greater than zero!");
        }
        BufferedImage bi = new BufferedImage(d.getNcols() * scale , d.getNrows() * scale, BufferedImage.TYPE_BYTE_GRAY);
        
        double min = min(d);
        double max = max(d);
        
        d.stream().forEach(p -> {
            int value = (int) Math.round((p.getHeight()-min)/(max-min)*255);
            int color = 0xFF000000 + value + (value<<8) + (value<<16);
            for (int i = 0; i < scale; i++) {
                for (int j = 0; j < scale; j++) {
                    bi.setRGB(p.getXCoor() * scale + i , p.getYCoor() * scale + j, color);
                }
            }
        });
        
        try {
            ImageIO.write(bi, "PNG", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static double max(Dem d) {
        return d.stream().mapToDouble(HeightedPoint::getHeight).max().getAsDouble();
    }

    
    private static double min(Dem d) {
        return d.stream().mapToDouble(HeightedPoint::getHeight).min().getAsDouble();
    }

    
}
