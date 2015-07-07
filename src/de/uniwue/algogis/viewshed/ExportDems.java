package de.uniwue.algogis.viewshed;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ExportDems {
	
	public static void toPng(Dem d, String path) {
		BufferedImage bi = new BufferedImage(d.getNcols(), d.getNrows(), BufferedImage.TYPE_BYTE_GRAY);
		
		double min = min(d);
		double max = max(d);
		
		d.stream().forEach(p -> {
			int value = (int) Math.round((p.getHeight()-min)/(max-min)*255);
			int color = 0xFF000000 + value + (value<<8) + (value<<16); 
			bi.setRGB(p.getXCoor(), p.getYCoor(), color);
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
