package dna.visualization.graph.util;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.util.DefaultMouseManager;

import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.toolTip.FreezeButton;
import dna.visualization.graph.toolTip.HighlightButton;
import dna.visualization.graph.toolTip.InfoLabel;
import dna.visualization.graph.toolTip.InfoLabel.LabelValueType;
import dna.visualization.graph.toolTip.ToolTip;

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
				switch (tt.getType()) {
				case BUTTON_FREEZE:
					((FreezeButton) tt).onRightClick();
					break;
				case BUTTON_HIGHLIGHT:
					((HighlightButton) tt).onRightClick();
					break;
				case INFO:
					break;
				}
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
				switch (tt.getType()) {
				case BUTTON_FREEZE:
					((FreezeButton) tt).onLeftClick();
					break;
				case BUTTON_HIGHLIGHT:
					((HighlightButton) tt).onLeftClick();
					break;
				case INFO:
					break;
				}
			}
		}
	}

	/** Called when right clicking a node. Toggles the tooltips. **/
	protected void mouseRightClickNode(GraphicNode node) {
		if (this.panel.isToolTipsEnabled()) {
			SpriteManager sm = this.panel.getSpriteManager();
			String nodeId = node.getId();
			String spriteId = ToolTip.spriteSuffixNodeId + nodeId;
			String spriteId2 = ToolTip.spriteSuffixDegree + nodeId;
			String spriteId3 = ToolTip.spriteSuffixButtonFreeze + nodeId;
			String spriteId4 = ToolTip.spriteSuffixButtonHighlight + nodeId;

			if (sm.hasSprite(spriteId) || sm.hasSprite(spriteId2)
					|| sm.hasSprite(spriteId3) || sm.hasSprite(spriteId4)) {
				if (sm.hasSprite(spriteId))
					sm.removeSprite(spriteId);

				if (sm.hasSprite(spriteId2))
					sm.removeSprite(spriteId2);

				if (sm.hasSprite(spriteId3))
					sm.removeSprite((spriteId3));

				if (sm.hasSprite(spriteId4))
					sm.removeSprite(spriteId4);
			} else {
				// add tooltips

				int pos = 40;

				Sprite sp1 = sm.addSprite(spriteId);
				InfoLabel il1 = new InfoLabel(sp1, "Node", LabelValueType.INT);
				il1.setDefaultStyle();
				il1.attachToNode(nodeId);
				il1.setValue(nodeId);

				il1.setPosition(pos, 270);
				pos += 30;

				Sprite sp2 = sm.addSprite(spriteId2);
				InfoLabel il2 = new InfoLabel(sp2, "Degree", LabelValueType.INT);
				il2.setDefaultStyle();
				il2.attachToNode(nodeId);
				il2.setValue("" + node.getDegree());
				il2.setPosition(pos, 270);
				pos += 30;

				Sprite sp3 = sm.addSprite(spriteId3);
				FreezeButton b3 = new FreezeButton(sp3, "Freeze", nodeId);
				b3.setPosition(pos, 270);
				pos += 30;
				Sprite sp4 = sm.addSprite(spriteId4);
				HighlightButton b4 = new HighlightButton(sp4, "Highlight",
						nodeId);
				b4.setPosition(pos, 270);
			}
		}
	}

	/** Called when left clicking a node. **/
	protected void mouseLeftClickNode(GraphicNode node) {
		// DO NOTHING
	}
}
