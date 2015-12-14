package dna.visualization.graph.ToolTip;

import org.graphstream.ui.spriteManager.Sprite;

import dna.visualization.graph.GraphVisualization;

public abstract class ToolTip {

	public enum ToolTipType {
		BUTTON, INFO
	}

	public static final String GraphVisToolTipTypeKey = "dna.ttt";
	public static final String GraphVisToolTipNameKey = "dna.tt.name";

	public abstract ToolTipType getType();

	protected Sprite s;

	protected void setName(String name) {
		this.s.setAttribute(GraphVisToolTipNameKey, name);
	}

	public String getName() {
		return this.s.getAttribute(GraphVisToolTipNameKey);
	}

	// static methods
	public static ToolTip getFromSprite(Sprite s) {
		ToolTipType ttt = s.getAttribute(GraphVisToolTipTypeKey,
				ToolTipType.class);
		if (ttt != null) {
			switch (ttt) {
			case BUTTON:
				return Button.getFromSprite(s);
			case INFO:
				return InfoLabel.getFromSprite(s);
			}
		}

		// return null
		return null;
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
