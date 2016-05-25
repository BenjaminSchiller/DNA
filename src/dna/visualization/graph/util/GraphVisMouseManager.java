package dna.visualization.graph.util;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.util.DefaultMouseManager;

import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.toolTips.ToolTip;

/**
 * The GraphVisMouseManager extends the GraphStream DefaultMouseManager. It
 * overrides several methods to adapt to the special needs we have in the DNA
 * GraphVisualization environment.
 * 
 * <p>
 * 
 * Note: The GraphVisMouseManager only defines interactions with GraphStream
 * objects like Nodes, Edges and Sprites. The moving & zooming logic is defined
 * inside the GraphPanel initialization.
 * 
 * @author Rwilmes
 * @date 15.12.2015
 */
public class GraphVisMouseManager extends DefaultMouseManager {

	// the GraphPanel using this manager
	protected GraphPanel panel;

	/** Constructor. **/
	public GraphVisMouseManager(GraphPanel panel) {
		super();
		this.panel = panel;
	}

	/** Called when a mouse-click happens somewhere in the GraphStream area. **/
	@Override
	public void mousePressed(MouseEvent event) {
		curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());

		// if mouse button 3 -> do something and return
		if (event.getButton() == MouseEvent.BUTTON3) {
			if (curElement != null) {
				if (curElement instanceof GraphicNode)
					mouseRightClickNode((GraphicNode) curElement);

				if (curElement instanceof GraphicSprite)
					mouseRightClickSprite((GraphicSprite) curElement);
			}

			return;
		}

		// if mouse button 1 -> do something and return
		if (event.getButton() == MouseEvent.BUTTON1) {
			if (curElement != null) {
				if (curElement instanceof GraphicNode)
					mouseLeftClickNode((GraphicNode) curElement);

				if (curElement instanceof GraphicSprite)
					mouseLeftClickSprite((GraphicSprite) curElement);
			}

			return;
		}

		if (curElement != null && curElement instanceof GraphicSprite) {
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

	/**
	 * Called when a mouse-button is pressed continuously while the mouse is
	 * being dragged.
	 **/
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

	/** Called when right clicking a sprite. **/
	protected void mouseRightClickSprite(GraphicSprite sprite) {
		String spriteId = sprite.getId();
		if (this.panel.isToolTipsEnabled()) {
			SpriteManager sm = this.panel.getSpriteManager();
			if (sm.hasSprite(spriteId)) {
				ToolTip tt = ToolTip.getFromSprite(sm.getSprite(spriteId));
				tt.onRightClick();
			}
		}
	}

	/** Called when left clicking a sprite. **/
	protected void mouseLeftClickSprite(GraphicSprite sprite) {
		String spriteId = sprite.getId();
		if (this.panel.isToolTipsEnabled()) {
			SpriteManager sm = this.panel.getSpriteManager();
			if (sm.hasSprite(spriteId)) {
				ToolTip tt = ToolTip.getFromSprite(sm.getSprite(spriteId));
				tt.onLeftClick();
			}
		}
	}

	/** Called when right clicking a node. Toggles the tooltips. **/
	protected void mouseRightClickNode(GraphicNode node) {
		if (this.panel.isToolTipsEnabled())
			this.panel.getToolTipManager().onMouseRightClick(node);
	}

	/** Called when left clicking a node. **/
	protected void mouseLeftClickNode(GraphicNode node) {
		if (this.panel.isToolTipsEnabled())
			this.panel.getToolTipManager().onMouseLeftClick(node);
	}
}
