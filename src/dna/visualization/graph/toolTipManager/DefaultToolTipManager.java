package dna.visualization.graph.toolTipManager;

import org.graphstream.ui.spriteManager.SpriteManager;

import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.toolTip.ToolTip.ToolTipType;

/**
 * The default ToolTipManager used in the GraphVisualization.
 * 
 * @author Rwilmes
 * @date 16.12.2015
 */
public class DefaultToolTipManager extends ToolTipManager {

	private static int distance = 30;
	private static int offset = 30;
	private static double angle = 270;

	// constructor
	public DefaultToolTipManager(String name, SpriteManager sm, GraphPanel panel) {
		super(name, sm, panel, distance, offset, angle);

		// add additional tooltips here
		this.addToolTip("Node", ToolTipType.INFO);
		this.addToolTip("Degree", ToolTipType.INFO);
		this.addToolTip("Freeze", ToolTipType.BUTTON_FREEZE);
		this.addToolTip("Highlight", ToolTipType.BUTTON_HIGHLIGHT);
	}

}
