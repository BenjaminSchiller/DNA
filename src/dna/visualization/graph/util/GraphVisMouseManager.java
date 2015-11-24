package dna.visualization.graph.util;

import java.awt.event.MouseEvent;

import org.graphstream.ui.view.util.DefaultMouseManager;

public class GraphVisMouseManager extends DefaultMouseManager {

	@Override
	public void mousePressed(MouseEvent event) {
		// if mouse button 3 -> return
		if (event.getButton() == MouseEvent.BUTTON3)
			return;

		curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());

		if (curElement != null) {
			mouseButtonPressOnElement(curElement, event);
		} else {
			x1 = event.getX();
			y1 = event.getY();
			mouseButtonPress(event);
			view.beginSelectionAt(x1, y1);
		}
	}
}
