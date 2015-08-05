package dna.visualization.graph.rules;

import java.awt.Color;

import org.graphstream.graph.Node;

import dna.visualization.graph.GraphVisualization;

/** Utility class for graph style rules. **/
public class GraphStyleUtils {

	/** Returns the color of the node. **/
	public static Color getColor(Node n) {
		return n.getAttribute(GraphVisualization.colorKey2);
	}

	/** Sets the node to a given color. **/
	public static void setColor(Node n, Color c) {
		n.setAttribute(GraphVisualization.colorKey2, c);
	}

	/** Returns the size of the node. **/
	public static double getSize(Node n) {
		return n.getAttribute(GraphVisualization.sizeKey2);
	}

	/** Sets the node to a given size. **/
	public static void setSize(Node n, double size) {
		n.setAttribute(GraphVisualization.sizeKey2, size);
	}

	/** Updates the nodes style. **/
	public static void updateStyle(Node n) {
		String style = "";
		// add color
		if (n.hasAttribute(GraphVisualization.colorKey2)) {
			Color c = n.getAttribute(GraphVisualization.colorKey2);
			style += "fill-color: rgb(" + c.getRed() + "," + c.getGreen() + ","
					+ c.getBlue() + "); ";
		}

		// add size
		if (n.hasAttribute(GraphVisualization.sizeKey2)) {
			style += "size: " + n.getAttribute(GraphVisualization.sizeKey2)
					+ "px; ";
		}

		// set style
		n.setAttribute(GraphVisualization.styleKey, style);
	}

	/*
	 * 
	 * GraphVisualization.colorKey, "fill-color: rgb(" + red + "," + green + ","
	 * + blue + ");");
	 */
}
