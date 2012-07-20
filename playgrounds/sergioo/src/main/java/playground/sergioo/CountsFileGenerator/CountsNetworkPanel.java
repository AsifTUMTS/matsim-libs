package playground.sergioo.CountsFileGenerator;

import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;

import playground.sergioo.CountsFileGenerator.CountsWindow.Labels;
import playground.sergioo.CountsFileGenerator.CountsWindow.Options;
import playground.sergioo.Visualizer2D.Layer;
import playground.sergioo.Visualizer2D.LayersWindow;
import playground.sergioo.Visualizer2D.PointsPainter;
import playground.sergioo.Visualizer2D.NetworkVisualizer.NetworkPanel;
import playground.sergioo.Visualizer2D.NetworkVisualizer.NetworkPainters.NetworkPainter;
import playground.sergioo.Visualizer2D.NetworkVisualizer.NetworkPainters.NetworkPainterManager;

public class CountsNetworkPanel extends NetworkPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Attributes

	//Methods
	public CountsNetworkPanel(LayersWindow window, NetworkPainter networkPainter) {
		super(window, networkPainter);
		addLayer(new Layer(new PointsPainter()));
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		double[] p = getWorld(e.getX(), e.getY());
		if(e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON3)
			camera.centerCamera(p);
		else {
			if(window.getOption().equals(Options.SELECT_LINK) && e.getButton()==MouseEvent.BUTTON1) {
				((NetworkPainter)getActiveLayer().getPainter()).getNetworkPainterManager().selectLink(p[0], p[1]);
				window.refreshLabel(Labels.LINK);
			}
			else if(window.getOption().equals(Options.SELECT_LINK) && e.getButton()==MouseEvent.BUTTON3) {
				((NetworkPainter)getActiveLayer().getPainter()).getNetworkPainterManager().unselectLink();
				window.refreshLabel(Labels.LINK);
			}
			else if(window.getOption().equals(Options.SELECT_NODE) && e.getButton()==MouseEvent.BUTTON1) {
				((NetworkPainter)getActiveLayer().getPainter()).getNetworkPainterManager().selectNode(p[0], p[1]);
				window.refreshLabel(Labels.NODE);
			}
			else if(window.getOption().equals(Options.SELECT_NODE) && e.getButton()==MouseEvent.BUTTON3) {
				((NetworkPainter)getActiveLayer().getPainter()).getNetworkPainterManager().unselectNode();
				window.refreshLabel(Labels.NODE);
			}
			else if(window.getOption().equals(Options.ZOOM) && e.getButton()==MouseEvent.BUTTON1)
				camera.zoomIn(p[0], p[1]);
			else if(window.getOption().equals(Options.ZOOM) && e.getButton()==MouseEvent.BUTTON3)
				camera.zoomOut(p[0], p[1]);
		}
		repaint();
	}
	@Override
	public String getLabelText(playground.sergioo.Visualizer2D.LayersWindow.Labels label) {
		try {
			return (String) NetworkPainterManager.class.getMethod("refresh"+label.getText(), new Class[0]).invoke(((NetworkPainter)getActiveLayer().getPainter()).getNetworkPainterManager(), new Object[0]);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return "";
	}
	public void addPoint(Coord point) {
		((PointsPainter)getLayer(1).getPainter()).addPoint(point);
	}
	public void selectPoint(Coord point) {
		((PointsPainter)getLayer(1).getPainter()).selectPoint(point);
	}
	public Link getSelectedLink() {
		return ((NetworkPainter)getLayer(0).getPainter()).getNetworkPainterManager().getSelectedLink();
	}
	
}
