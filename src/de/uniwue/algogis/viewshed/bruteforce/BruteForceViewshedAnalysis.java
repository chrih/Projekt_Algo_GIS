package de.uniwue.algogis.viewshed.bruteforce;

import java.util.Collection;

import de.uniwue.algogis.viewshed.Dem;
import de.uniwue.algogis.viewshed.HeightedPoint;
import de.uniwue.algogis.viewshed.ModifiableDem;
import de.uniwue.algogis.viewshed.ViewshedAnalysis;

public class BruteForceViewshedAnalysis implements ViewshedAnalysis {

	@Override
	public Dem calculateViewshed(Dem d, HeightedPoint origin) {
		ModifiableDem result = new ModifiableDem(d);
		
		PointOnLineFinder<HeightedPoint> polf = new PointOnLineFinder<HeightedPoint>(d::getHeightedPoint);
		
		d.forEach(target -> {
			Collection<HeightedPoint> points = polf.findPointsOnLine(origin, target);
			boolean visible = points.stream().allMatch(p -> origin.calcSlope(p) < origin.calcSlope(target));
			result.setHeight(target, visible ? 1 : 0);
		});
		
		
		return result;
	}
	
}
