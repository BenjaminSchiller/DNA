package dna.visualization.graph.toolTipManager;

import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.toolTip.FreezeButton;
import dna.visualization.graph.toolTip.HighlightButton;
import dna.visualization.graph.toolTip.InfoLabel;
import dna.visualization.graph.toolTip.InfoLabel.LabelValueType;
import dna.visualization.graph.toolTip.ToolTip;
import dna.visualization.graph.toolTip.ToolTip.ToolTipType;

/**
 * The ToolTipManager handles the ToolTips used in the GraphVisualization.
 * 
 * <p>
 * 
 * Every GraphPanel (which has ToolTips enabled) uses one ToolTipManager. The
 * manager handles all the ToolTips/Sprites shown when right-clicking a node.
 * 
 * @author Rwilmes
 * @date 16.12.2015
 */
public class ToolTipManager {

	private GraphPanel panel;

	private SpriteManager sm;

	private String name;
	private ArrayList<String> toolTipsNames;
	private ArrayList<ToolTipType> toolTipsTypes;

	// configuration
	private int distance;
	private int offset;
	private double angle;

	/** ToolTipManager constructor. **/
	public ToolTipManager(String name, SpriteManager sm, GraphPanel panel,
			int distance, int offset, double angle) {
		this.name = name;
		this.sm = sm;
		this.panel = panel;
		this.distance = distance;
		this.offset = offset;
		this.angle = angle;
		this.toolTipsNames = new ArrayList<String>();
		this.toolTipsTypes = new ArrayList<ToolTipType>();
	}

	/** Adds a tooltip of the given name and type to the manager. **/
	public void addToolTip(String name, ToolTipType type) {
		this.toolTipsNames.add(name);
		this.toolTipsTypes.add(type);
	}

	/** Removes the tooltip from the manager. **/
	public void removeToolTip(String name, ToolTipType type) {
		for (int i = 0; i < this.toolTipsTypes.size(); i++) {
			if (this.toolTipsTypes.get(i).equals(type)
					&& this.toolTipsNames.get(i).equals(name)) {
				this.toolTipsNames.remove(i);
				this.toolTipsTypes.remove(i);
			}
		}
	}

	/** Returns the managers name. **/
	public String getName() {
		return this.name;
	}

	/** Called when a node is getting removed. **/
	public void onNodeRemoval(Node node) {

	}

	/** Called when an edge is getting removed. **/
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {

	}

	/** Called when a right-click on a node occurs. **/
	public void onMouseRightClick(GraphicNode graphicNode) {
		if (this.toolTipsNames.size() < 1)
			return;

		String nodeId = graphicNode.getId();
		Node node = this.panel.getGraph().getNode(nodeId);
		boolean toolTipsActive = node
				.hasAttribute(ToolTip.GraphVisToolTipActiveKey);

		if (toolTipsActive) {
			// if active -> remove
			for (int i = 0; i < this.toolTipsTypes.size(); i++) {
				String toolTipId = this.toolTipsNames.get(i) + nodeId;
				if (sm.hasSprite(toolTipId))
					sm.removeSprite(toolTipId);
			}

			// remove flag
			node.removeAttribute(ToolTip.GraphVisToolTipActiveKey);
		} else {
			// if not active -> create
			for (int i = 0; i < this.toolTipsTypes.size(); i++) {
				String name = this.toolTipsNames.get(i);
				String ttId = name + nodeId;
				ToolTipType ttt = this.toolTipsTypes.get(i);

				Sprite sprite = sm.addSprite(ttId);
				ToolTip tt = null;
				switch (ttt) {
				case BUTTON_FREEZE:
					tt = new FreezeButton(sprite, name, nodeId);
					break;
				case BUTTON_HIGHLIGHT:
					tt = new HighlightButton(sprite, name, nodeId);
					break;
				case INFO:
					tt = new InfoLabel(sprite, name, nodeId,
							LabelValueType.INT, "" + 0);
					break;
				case NONE:
					sm.removeSprite(ttId);
					break;
				}

				tt.setPosition(distance + i * offset, angle);
			}

			// set tooltips-active flag
			node.addAttribute(ToolTip.GraphVisToolTipActiveKey);
		}
	}
}
