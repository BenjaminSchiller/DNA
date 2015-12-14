package dna.visualization.graph.util;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.util.DefaultMouseManager;

import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.ToolTip.GraphVisToolTip;

public class GraphVisMouseManager extends DefaultMouseManager {

	protected GraphPanel panel;

	public GraphVisMouseManager(GraphPanel panel) {
		super();
		this.panel = panel;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());

		if (curElement != null && curElement instanceof GraphicSprite)
			return;

		// if mouse button 3 -> return
		if (event.getButton() == MouseEvent.BUTTON3) {
			if (curElement != null) {
				if (curElement instanceof GraphicNode)
					mouseRightClickNode((GraphicNode) curElement);

				if (curElement instanceof GraphicSprite)
					mouseRightClickSprite((GraphicSprite) curElement);
			}

			return;
		}

		if (curElement != null) {
			if (!(curElement instanceof GraphicSprite))
				mouseButtonPressOnElement(curElement, event);
		} else {
			x1 = event.getX();
			y1 = event.getY();
			mouseButtonPress(event);
			view.beginSelectionAt(x1, y1);
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (curElement != null) {
			if (!(curElement instanceof GraphicSprite))
				elementMoving(curElement, event);
		} else {
			// if dragged with right mouse down -> dont grow selection
			if ((event.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK)
				return;

			view.selectionGrowsAt(event.getX(), event.getY());
		}
	}

	protected void mouseRightClickSprite(GraphicSprite sprite) {
		// DO NOTHING
	}

	/** Called when right clicking a node. Toggles the tooltips. **/
	protected void mouseRightClickNode(GraphicNode node) {
		if (this.panel.isToolTipsEnabled()) {
			SpriteManager sm = this.panel.getSpriteManager();
			String nodeId = node.getId();
			String spriteId = GraphVisToolTip.spriteSuffixNodeId + nodeId;
			String spriteId2 = GraphVisToolTip.spriteSuffixDegree + nodeId;

			if (sm.hasSprite(spriteId) || sm.hasSprite(spriteId2)) {
				if (sm.hasSprite(spriteId))
					sm.removeSprite(spriteId);

				if (sm.hasSprite(spriteId2))
					sm.removeSprite(spriteId2);
			} else {
				GraphVisToolTip tooltip1 = sm.addSprite(spriteId,
						GraphVisToolTip.class);
				tooltip1.setDefaultStyle();
				tooltip1.attachToNode(nodeId);
				tooltip1.setDefaultPos(0);
				tooltip1.setText("Node: " + nodeId);

				GraphVisToolTip tooltip2 = sm.addSprite(spriteId2,
						GraphVisToolTip.class);
				tooltip2.setDefaultStyle();
				tooltip2.attachToNode(nodeId);
				tooltip2.setDefaultPos(1);
				tooltip2.setText("Degree: " + node.getDegree());
			}
		}
	}
}
