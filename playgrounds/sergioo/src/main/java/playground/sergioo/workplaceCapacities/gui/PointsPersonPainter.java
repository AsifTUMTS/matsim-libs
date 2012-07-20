package playground.sergioo.workplaceCapacities.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.matsim.core.utils.geometry.CoordImpl;

import playground.sergioo.Visualizer2D.LayersPanel;
import playground.sergioo.Visualizer2D.Painter;
import playground.sergioo.workplaceCapacities.hits.PointPerson;

public class PointsPersonPainter extends Painter {
	
	
	//Attributes
	private Collection<PointPerson> points = new ArrayList<PointPerson>();
	private Color color = new Color(1,0,0,0.3f);
	private double minWeight;
	private double maxWeight;
	
	//Methods
	public PointsPersonPainter() {
		super();
	}
	public PointsPersonPainter(Color color) {
		super();
		this.color = color;
	}
	public void setWeightedPoints(List<PointPerson> points) {
		this.points = points;
		minWeight = Double.MAX_VALUE;
		maxWeight = 0;
		for(PointPerson value:points) {
			if(value.getWeight()<minWeight)
				minWeight = value.getWeight();
			if(value.getWeight()>maxWeight)
				maxWeight = value.getWeight();
		}
		if(minWeight==maxWeight)
			maxWeight++;
	}
	public Collection<PointPerson> getPoints() {
		return points;
	}
	public void addPoint(PointPerson point) {
		points.add(point);
	}
	public void clearPoints() {
		points.clear();
	}
	@Override
	public void paint(Graphics2D g2, LayersPanel layersPanel) {
		double maxSize = 10;
		for(PointPerson point:points)
			paintCircle(g2, layersPanel, new CoordImpl(point.getElement(0),point.getElement(1)), (point.getWeight()*Math.random())*maxSize/(maxWeight-minWeight), color);
	}

}
