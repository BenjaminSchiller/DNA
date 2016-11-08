package dna.visualization.graph.toolTipManager;

import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.toolTips.ToolTip.ToolTipType;

public class NetflowToolTipManager extends ToolTipManager {

	private static int distance = 30;
	private static int offset = 30;
	private static double angle = 270;

	// constructor
	public NetflowToolTipManager(GraphPanel panel) {
		super("NetflowToolTipManager", panel.getSpriteManager(), panel,
				distance, offset, angle);

		// add additional tooltips here
		addToolTip("Node", ToolTipType.INFO_NETWORK_NODE_KEY);
		addToolTip("Type", ToolTipType.INFO_NODE_TYPE_WEIGHT);
		addToolTip("Degree", ToolTipType.INFO_NODE_DEGREE);
		addToolTip("NodeID", ToolTipType.INFO_NODE_ID);
		addToolTip("Freeze", ToolTipType.BUTTON_FREEZE);
		addToolTip("Highlight", ToolTipType.BUTTON_HIGHLIGHT);
	}

}
