package dna.visualization.graph.rules;

import java.awt.Color;
import java.util.ArrayList;

import org.graphstream.graph.Node;

import dna.visualization.graph.GraphVisualization;

/** Utility class for graph style rules. **/
public class GraphStyleUtils {

	/** Returns the color of the node. **/
	public static Color getColor(Node n) {
		return n.getAttribute(GraphVisualization.colorKey);
	}

	/** Sets the node to a given color. **/
	public static void setColor(Node n, Color c) {
		n.setAttribute(GraphVisualization.colorKey, c);
	}

	/** Returns the size of the node. **/
	public static double getSize(Node n) {
		return n.getAttribute(GraphVisualization.sizeKey);
	}

	/** Sets the node to a given size. **/
	public static void setSize(Node n, double size) {
		n.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Sets the growth for the given index. **/
	public static void setGrowth(Node n, int index, double growth) {
		ArrayList<Double> growthList = n
				.getAttribute(GraphVisualization.growthListKey);

		// if out of bounds, fill with zeros
		if (growthList.size() <= index) {
			for (int i = 0; i < (index - growthList.size() + 1); i++) {
				growthList.add(0.0);
			}
		}

		// set growth
		growthList.set(index, growth);
	}

	/** Returns the growth list of the given node. **/
	public static ArrayList<Double> getGrowthList(Node n) {
		return n.getAttribute(GraphVisualization.growthListKey);
	}

	/** Returns the growth of the given node at the given index. **/
	public static double getGrowth(Node n, int index) {
		return getGrowthList(n).get(index);
	}

	/** Updates the nodes style. **/
	public static void updateStyle(Node n) {
		String style = "";
		// add color
		if (n.hasAttribute(GraphVisualization.colorKey)) {
			Color c = n.getAttribute(GraphVisualization.colorKey);
			style += "fill-color: rgb(" + c.getRed() + "," + c.getGreen() + ","
					+ c.getBlue() + "); ";
		}

		// calc size
		double size = (double) n
				.getAttribute(GraphVisualization.defaultSizeKey);

		ArrayList<Double> growthList = n
				.getAttribute(GraphVisualization.growthListKey);
		for (double d : growthList)
			size += d;

		if (size < 1)
			size = 1;

		style += "size: " + size + "px; ";

		// set style
		n.setAttribute(GraphVisualization.styleKey, style);
	}
}
