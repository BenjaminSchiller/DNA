package dna.visualization.graph.ToolTip;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.Sprite;

import dna.visualization.graph.GraphVisualization;

public abstract class ToolTip {

	public enum ToolTipType {
		BUTTON_FREEZE, BUTTON_HIGHLIGHT, INFO
	}

	public static final String GraphVisToolTipTypeKey = "dna.ttt";
	public static final String GraphVisToolTipNameKey = "dna.tt.name";

	public static final String spriteSuffixNodeId = "SPRITE1_";
	public static final String spriteSuffixDegree = "SPRITE2_";
	public static final String spriteSuffixButtonFreeze = "TT_BUTTON_FREEZE_";
	public static final String spriteSuffixButtonHighlight = "TT_BUTTON_HIGHLIGHT_";
	public static final String spriteSuffixButton = "TT_BUTTON_";

	public abstract ToolTipType getType();

	protected Sprite s;

	protected void setName(String name) {
		this.s.setAttribute(GraphVisToolTipNameKey, name);
	}

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

	// static methods
	public static ToolTip getFromSprite(Sprite s) {
		ToolTipType ttt = getToolTipTypeFromSprite(s);
		if (ttt != null) {
			switch (ttt) {
			case BUTTON_FREEZE:
				return FreezeButton.getFromSprite(s);
			case BUTTON_HIGHLIGHT:
				return HighlightButton.getFromSprite(s);
			case INFO:
				return InfoLabel.getFromSprite(s);
			}
		}

		// return null
		return null;
	}

	public static ToolTipType getToolTipTypeFromSprite(Sprite s) {
		return s.getAttribute(GraphVisToolTipTypeKey, ToolTipType.class);
	}

	// default style
	public static final String default_style = "" + "shape:rounded-box; "
			+ "size:100px,30px; " + "fill-mode:plain; "
			+ "fill-color: rgba(220,220,220, 150); " + "stroke-mode:dots; "
			+ "stroke-color: rgb(40, 40, 40); " + "text-alignment:center;";

	public void setDefaultStyle() {
		this.s.setAttribute(GraphVisualization.styleKey, default_style);
	}

}
