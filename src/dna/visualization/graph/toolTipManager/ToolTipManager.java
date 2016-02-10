package dna.visualization.graph.toolTipManager;

import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import dna.graph.weights.Weight;
import dna.visualization.graph.GraphPanel;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.toolTips.ToolTip;
import dna.visualization.graph.toolTips.ToolTip.ToolTipType;
import dna.visualization.graph.toolTips.button.FreezeButton;
import dna.visualization.graph.toolTips.button.HighlightButton;
import dna.visualization.graph.toolTips.infoLabel.NodeDegreeLabel;
import dna.visualization.graph.toolTips.infoLabel.NodeIdLabel;

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
public class ToolTipManager extends GraphStyleRule {

	// registered components
	private GraphPanel panel;
	private SpriteManager sm;

	// class fields
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

	/** Returns if the node has ToolTips shown at the moment. **/
	protected static boolean isToolTipsActive(Node node) {
		return node.hasAttribute(ToolTip.GraphVisToolTipActiveKey);
	}

	/** Sets ToolTips shown flag. **/
	protected static void setToolTipsActive(Node node) {
		node.setAttribute(ToolTip.GraphVisToolTipActiveKey);
	}

	/** Removes ToolTips shown flag. **/
	protected static void setToolTipsInactive(Node node) {
		node.removeAttribute(ToolTip.GraphVisToolTipActiveKey);
	}

	/** Returns the ToolTip id based on the parameters. **/
	protected static String getToolTipId(String name, ToolTipType type,
			String nodeId) {
		return type + "_" + name + "_" + nodeId;
	}

	/** Gathers all ToolTips on the given node. **/
	protected ArrayList<ToolTip> getAllToolTips(Node node) {
		ArrayList<ToolTip> list = new ArrayList<ToolTip>();
		for (int i = 0; i < this.toolTipsTypes.size(); i++) {
			String toolTipId = getToolTipId(this.toolTipsNames.get(i),
					this.toolTipsTypes.get(i), node.getId());
			if (sm.hasSprite(toolTipId))
				list.add(ToolTip.getFromSprite(sm.getSprite(toolTipId)));
		}

		return list;
	}

	/** Hides all ToolTips from the given node. **/
	protected void hideAllToolTips(Node node) {
		for (int i = 0; i < this.toolTipsTypes.size(); i++) {
			String toolTipId = getToolTipId(this.toolTipsNames.get(i),
					this.toolTipsTypes.get(i), node.getId());
			if (sm.hasSprite(toolTipId))
				sm.removeSprite(toolTipId);
		}

		// remove flag
		setToolTipsInactive(node);
	}

	/**
	 * Initializes a ToolTip.
	 * 
	 * <p>
	 * 
	 * Note: Integrate new ToolTip implementations in this method, else they
	 * wont be rendered.
	 */
	protected void initToolTip(String name, ToolTipType ttt, Node node,
			int offset) {
		String nodeId = node.getId();
		ToolTip tt = null;

		// craft sprite
		String ttId = getToolTipId(name, ttt, nodeId);
		Sprite sprite = sm.addSprite(ttId);

		// switch on type
		switch (ttt) {
		case BUTTON_FREEZE:
			tt = new FreezeButton(sprite, name, nodeId);
			break;
		case BUTTON_HIGHLIGHT:
			tt = new HighlightButton(sprite, name, nodeId);
			break;
		case INFO_NODE_DEGREE:
			tt = new NodeDegreeLabel(sprite, name, node);
			break;
		case INFO_NODE_ID:
			tt = new NodeIdLabel(sprite, name, nodeId);
			break;
		case NONE:
			sm.removeSprite(ttId);
			break;
		}

		tt.setPosition(distance + offset, angle);
	}

	/** Shows all ToolTips on the given node. **/
	protected void showAllToolTips(Node node) {
		// iterate over all set tooltips
		for (int i = 0; i < this.toolTipsTypes.size(); i++) {
			initToolTip(this.toolTipsNames.get(i), this.toolTipsTypes.get(i),
					node, i * offset);
		}

		// set tooltips-active flag
		setToolTipsActive(node);
	}

	/** Called when a left-click on a node occurs. **/
	public void onMouseLeftClick(GraphicNode graphicNode) {
		// DO NOTHING
	}

	/** Called when a right-click on a node occurs. **/
	public void onMouseRightClick(GraphicNode graphicNode) {
		if (this.toolTipsNames.size() < 1)
			return;

		String nodeId = graphicNode.getId();
		Node node = this.panel.getGraph().getNode(nodeId);
		boolean toolTipsActive = isToolTipsActive(node);

		if (toolTipsActive) {
			// if active -> remove
			hideAllToolTips(node);
		} else {
			// if not active -> create
			showAllToolTips(node);
		}
	}

	/** Called when a node is being added. **/
	public void onNodeAddition(Node n) {
		// DO NOTHING
	}

	/** Called when a node is being removed. **/
	public void onNodeRemoval(Node node) {
		if (isToolTipsActive(node))
			hideAllToolTips(node);
	}

	/** Called when a node weight changes. **/
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
		for (ToolTip tt : getAllToolTips(n))
			tt.onNodeWeightChange(n, wNew, wOld);
	}

	/** Called when an edge is being added. **/
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		for (ToolTip tt : getAllToolTips(n1))
			tt.onEdgeAddition(e, n1, n2);

		for (ToolTip tt : getAllToolTips(n2))
			tt.onEdgeAddition(e, n1, n2);
	}

	/** Called when an edge is being removed. **/
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		for (ToolTip tt : getAllToolTips(n1))
			tt.onEdgeRemoval(e, n1, n2);

		for (ToolTip tt : getAllToolTips(n2))
			tt.onEdgeRemoval(e, n1, n2);
	}

	/** Called when an edge weight changes. **/
	public void onEdgeWeightChange(Edge e, Weight wNew, Weight wOld) {
		// DO NOTHING
	}

	@Override
	public String toString() {
		return "ToolTipManager: '" + this.name + "'";
	}
}
