package dna.visualization.graph.util;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.Sprite;

import dna.visualization.graph.GraphVisualization;

public class GraphVisToolTip extends Sprite {

	// keys
	public static final String spriteSuffixNodeId = "SPRITE1_";
	public static final String spriteSuffixDegree = "SPRITE2_";

	// default style
	public static final String default_style = "" + "shape:rounded-box; "
			+ "size:100px,30px; " + "fill-mode:plain; "
			+ "fill-color: rgba(220,220,220, 150); " + "stroke-mode:dots; "
			+ "stroke-color: rgb(40, 40, 40); " + "text-alignment:justify;";

	public void setDefaultStyle() {
		this.setAttribute(GraphVisualization.styleKey, default_style);
	}

	public void setDefaultPos() {
		this.setPosition(Units.PX, 60, 0, 340);
	}

	public void setDefaultPos(int id) {
		if (id == 0)
			this.setDefaultPos();
		if (id == 1)
			this.setPosition(Units.PX, 76, 0, 318);
	}

	public void setText(String text) {
		this.setAttribute(GraphVisualization.labelKey, text);
	}

	public static void incrementValue(Sprite sprite) {
		String label = sprite.getAttribute(GraphVisualization.labelKey);
		String[] splits = label.split(" ");
		int value = Integer.parseInt(splits[1]) + 1;
		sprite.setAttribute(GraphVisualization.labelKey, splits[0] + " "
				+ value);
	}

	public static void decrementValue(Sprite sprite) {
		String label = sprite.getAttribute(GraphVisualization.labelKey);
		String[] splits = label.split(" ");
		int value = Integer.parseInt(splits[1]) - 1;
		sprite.setAttribute(GraphVisualization.labelKey, splits[0] + " "
				+ value);
	}

}
