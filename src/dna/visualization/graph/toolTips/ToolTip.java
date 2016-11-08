package dna.visualization.graph.toolTips;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.Sprite;

import dna.graph.weights.Weight;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.toolTips.button.FreezeButton;
import dna.visualization.graph.toolTips.button.HighlightButton;
import dna.visualization.graph.toolTips.infoLabel.NetworkNodeKeyLabel;
import dna.visualization.graph.toolTips.infoLabel.NodeDegreeLabel;
import dna.visualization.graph.toolTips.infoLabel.NodeIdLabel;
import dna.visualization.graph.toolTips.infoLabel.NodeTypeWeightLabel;

/**
 * ToolTip is a wrapper class for the GraphStream Sprite class. Sprites are
 * objects inside the GraphStream graph that can be used to display information
 * and data. ToolTip introduces several methods that make the handling of
 * sprites more enjoyable.
 * 
 * <p>
 * 
 * Note that each ToolTip will uniquely identify one Sprite. Due to the nature
 * of Sprite handling in GraphStream this requires to save some data on each
 * Sprite: The ToolTipType and the ToolTips name. Therefore each implementation
 * of ToolTip should be represented by its own unique ToolTipType.
 * 
 * @author Rwilmes
 * @date 15.12.2015
 */
public abstract class ToolTip {

	/** ToolTipType used to identify different ToolTip implementations. **/
	public enum ToolTipType {
		BUTTON_FREEZE, BUTTON_HIGHLIGHT, INFO_NODE_ID, INFO_NODE_DEGREE, INFO_NODE_TYPE_WEIGHT, INFO_NETWORK_NODE_KEY, NONE
	}

	public static final String GraphVisToolTipTypeKey = "dna.ttt";
	public static final String GraphVisToolTipNameKey = "dna.tt.name";

	public static final String GraphVisToolTipActiveKey = "dna.tt.active";

	public static final String GraphVisToolTipStorageKey = "dna.tooltip.storage";

	public static final String spriteSuffixNodeId = "SPRITE1_";
	public static final String spriteSuffixDegree = "SPRITE2_";
	public static final String spriteSuffixButtonFreeze = "TT_BUTTON_FREEZE_";
	public static final String spriteSuffixButtonHighlight = "TT_BUTTON_HIGHLIGHT_";
	public static final String spriteSuffixButton = "TT_BUTTON_";

	/** Returns the objects ToolTipType. **/
	public abstract ToolTipType getType();

	/** Sets the ToolTipType on the Sprite. **/
	protected void setType() {
		this.s.setAttribute(ToolTip.GraphVisToolTipTypeKey, getType());
	}

	/** Sprite the ToolTip is wrapped around. **/
	protected Sprite s;

	/** Sets the name of the ToolTip onto the Sprite. **/
	protected void setName(String name) {
		this.s.setAttribute(GraphVisToolTipNameKey, name);
	}

	/** Stores the ToolTip object on the Sprite. **/
	public void storeThisOnSprite() {
		this.s.setAttribute(GraphVisToolTipStorageKey, this);
	}

	/** Returns the ToolTips name from the Sprite. **/
	public String getName() {
		return this.s.getAttribute(GraphVisToolTipNameKey);
	}

	/**
	 * Sets the position around the node based on the rotation-angle and the
	 * distance.
	 **/
	public void setPosition(double distance, double angle) {
		this.s.setPosition(Units.PX, distance, 0, angle);
	}

	/** Attaches the ToolTip to a node. **/
	public void attachToNode(String nodeId) {
		this.s.attachToNode(nodeId);
	}

	/**
	 * Returns a ToolTip object from a Sprite s. Used to identify which ToolTip
	 * belongs to a specific sprite.
	 **/
	public static ToolTip getFromSprite(Sprite s) {
		ToolTipType ttt = getToolTipTypeFromSprite(s);
		if (ttt != null) {
			switch (ttt) {
			case BUTTON_FREEZE:
				return FreezeButton.getFromSprite(s);
			case BUTTON_HIGHLIGHT:
				return HighlightButton.getFromSprite(s);
			case INFO_NODE_DEGREE:
				return NodeDegreeLabel.getFromSprite(s);
			case INFO_NODE_ID:
				return NodeIdLabel.getFromSprite(s);
			case INFO_NODE_TYPE_WEIGHT:
				return NodeTypeWeightLabel.getFromSprite(s);
			case INFO_NETWORK_NODE_KEY:
				return NetworkNodeKeyLabel.getFromSprite(s);
			}
		}

		// return null
		return null;
	}

	/**
	 * Returns the ToolTipType from a Sprite s. For more ToolTip implementations
	 * add more ToolTipTypes.
	 **/
	public static ToolTipType getToolTipTypeFromSprite(Sprite s) {
		if (!s.hasAttribute(GraphVisToolTipTypeKey))
			return ToolTipType.NONE;

		return s.getAttribute(GraphVisToolTipTypeKey, ToolTipType.class);
	}

	/** Default style of a tool-tip. **/
	public static final String default_style = "" + "shape:rounded-box; "
			+ "size:100px,30px; " + "fill-mode:plain; "
			+ "fill-color: rgba(220,220,220, 150); " + "stroke-mode:dots; "
			+ "stroke-color: rgb(40, 40, 40); " + "text-alignment:center;";

	/** Sets the default style. **/
	public void setDefaultStyle() {
		this.s.setAttribute(GraphVisualization.styleKey, default_style);
	}

	/** Retrieves the stored ToolTip from a Sprite. **/
	public static final ToolTip getToolTipFromSprite(Sprite s) {
		if (s.hasAttribute(GraphVisToolTipStorageKey))
			return s.getAttribute(GraphVisToolTipStorageKey, ToolTip.class);

		return null;
	}

	/*
	 * METHODS FOR DYNAMIC EVENTS IN GRAPH
	 */
	public abstract void onNodeWeightChange(Node n, Weight wNew, Weight wOld);

	public abstract void onEdgeAddition(Edge e, Node n1, Node n2);

	public abstract void onEdgeRemoval(Edge e, Node n1, Node n2);

}
