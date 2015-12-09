package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import dna.graph.weights.Weight;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.util.GraphVisToolTip;

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
		if (spriteManager
				.hasSprite(GraphVisToolTip.spriteSuffixNodeId + nodeId))
			spriteManager.removeSprite(GraphVisToolTip.spriteSuffixNodeId
					+ nodeId);

		if (spriteManager
				.hasSprite(GraphVisToolTip.spriteSuffixDegree + nodeId))
			spriteManager.removeSprite(GraphVisToolTip.spriteSuffixDegree
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
		if (spriteManager.hasSprite(GraphVisToolTip.spriteSuffixDegree
				+ nodeId1)) {
			Sprite sprite = spriteManager
					.getSprite(GraphVisToolTip.spriteSuffixDegree + nodeId1);
			if (sprite.hasAttribute(GraphVisualization.labelKey))
				GraphVisToolTip.incrementValue(sprite);

		}
		if (spriteManager.hasSprite(GraphVisToolTip.spriteSuffixDegree
				+ nodeId2)) {
			Sprite sprite = spriteManager
					.getSprite(GraphVisToolTip.spriteSuffixDegree + nodeId2);
			if (sprite.hasAttribute(GraphVisualization.labelKey))
				GraphVisToolTip.incrementValue(sprite);

		}
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		String nodeId1 = n1.getId();
		String nodeId2 = n2.getId();
		if (spriteManager.hasSprite(GraphVisToolTip.spriteSuffixDegree
				+ nodeId1)) {
			Sprite sprite = spriteManager
					.getSprite(GraphVisToolTip.spriteSuffixDegree + nodeId1);
			if (sprite.hasAttribute(GraphVisualization.labelKey))
				GraphVisToolTip.decrementValue(sprite);

		}
		if (spriteManager.hasSprite(GraphVisToolTip.spriteSuffixDegree
				+ nodeId2)) {
			Sprite sprite = spriteManager
					.getSprite(GraphVisToolTip.spriteSuffixDegree + nodeId2);
			if (sprite.hasAttribute(GraphVisualization.labelKey))
				GraphVisToolTip.decrementValue(sprite);

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
