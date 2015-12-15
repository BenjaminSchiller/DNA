package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import dna.graph.weights.Weight;
import dna.visualization.graph.ToolTip.InfoLabel;
import dna.visualization.graph.ToolTip.ToolTip;

public class ToolTipUpdater extends GraphStyleRule {

	protected SpriteManager spriteManager;

	public ToolTipUpdater(SpriteManager spriteManager) {
		this.spriteManager = spriteManager;
	}

	@Override
	public void onNodeAddition(Node n) {
		// DO NOTHING
	}

	@Override
	public void onNodeRemoval(Node n) {
		String nodeId = n.getId();
		if (spriteManager.hasSprite(ToolTip.spriteSuffixNodeId + nodeId))
			spriteManager.removeSprite(ToolTip.spriteSuffixNodeId + nodeId);

		if (spriteManager.hasSprite(ToolTip.spriteSuffixDegree + nodeId))
			spriteManager.removeSprite(ToolTip.spriteSuffixDegree + nodeId);

		if (spriteManager.hasSprite(ToolTip.spriteSuffixButtonFreeze + nodeId))
			spriteManager.removeSprite(ToolTip.spriteSuffixButtonFreeze
					+ nodeId);

		if (spriteManager.hasSprite(ToolTip.spriteSuffixButtonHighlight
				+ nodeId))
			spriteManager.removeSprite(ToolTip.spriteSuffixButtonHighlight
					+ nodeId);
	}

	@Override
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
		// DO NOTHING
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		String nodeId1 = n1.getId();
		String nodeId2 = n2.getId();
		if (spriteManager.hasSprite(ToolTip.spriteSuffixDegree + nodeId1)) {
			Sprite sprite = spriteManager.getSprite(ToolTip.spriteSuffixDegree
					+ nodeId1);
			InfoLabel.getFromSprite(sprite).increment();
		}
		if (spriteManager.hasSprite(ToolTip.spriteSuffixDegree + nodeId2)) {
			Sprite sprite = spriteManager.getSprite(ToolTip.spriteSuffixDegree
					+ nodeId2);
			InfoLabel.getFromSprite(sprite).increment();
		}
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		String nodeId1 = n1.getId();
		String nodeId2 = n2.getId();
		if (spriteManager.hasSprite(ToolTip.spriteSuffixDegree + nodeId1)) {
			Sprite sprite = spriteManager.getSprite(ToolTip.spriteSuffixDegree
					+ nodeId1);
			InfoLabel.getFromSprite(sprite).decrement();
		}
		if (spriteManager.hasSprite(ToolTip.spriteSuffixDegree + nodeId2)) {
			Sprite sprite = spriteManager.getSprite(ToolTip.spriteSuffixDegree
					+ nodeId2);
			InfoLabel.getFromSprite(sprite).decrement();
		}
	}

	@Override
	public void onEdgeWeightChange(Edge e, Weight wNew, Weight wOld) {
		// DO NOTHING
	}

	@Override
	public String toString() {
		return "ToolTipUpdater: '" + this.name + "'";
	}

}
