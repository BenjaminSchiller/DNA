package dna.visualization.graph.util;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.util.DefaultMouseManager;

import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.ToolTip.FreezeButton;
import dna.visualization.graph.ToolTip.HighlightButton;
import dna.visualization.graph.ToolTip.InfoLabel;
import dna.visualization.graph.ToolTip.InfoLabel.LabelValueType;
import dna.visualization.graph.ToolTip.ToolTip;

public class GraphVisMouseManager extends DefaultMouseManager {

	protected GraphPanel panel;

	public GraphVisMouseManager(GraphPanel panel) {
		super();
		this.panel = panel;
	}

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
				Sprite sp1 = sm.addSprite(spriteId);
				InfoLabel il1 = new InfoLabel(sp1, "Node", LabelValueType.INT);
				il1.setDefaultStyle();
				il1.attachToNode(nodeId);
				il1.setValue(nodeId);
				il1.setPosition(30, 270);

				Sprite sp2 = sm.addSprite(spriteId2);
				InfoLabel il2 = new InfoLabel(sp2, "Degree", LabelValueType.INT);
				il2.setDefaultStyle();
				il2.attachToNode(nodeId);
				il2.setValue("" + node.getDegree());
				il2.setPosition(60, 270);

				Sprite sp3 = sm.addSprite(spriteId3);
				FreezeButton b3 = new FreezeButton(sp3, "Freeze", nodeId);
				b3.setPosition(90, 270);

				Sprite sp4 = sm.addSprite(spriteId4);
				HighlightButton b4 = new HighlightButton(sp4, "Highlight",
						nodeId);
				b4.setPosition(120, 270);
			}
		}
	}

	/** Called when left clicking a node. **/
	protected void mouseLeftClickNode(GraphicNode node) {
		// DO NOTHING
	}
}
