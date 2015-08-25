package dna.visualization.graph.rules;

import java.awt.Color;
import java.util.ArrayList;

import org.graphstream.graph.Edge;
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

	/** Returns the color of the edge. **/
	public static Color getColor(Edge e) {
		return e.getAttribute(GraphVisualization.colorKey);
	}

	/** Sets the edge to a given color. **/
	public static void setColor(Edge e, Color c) {
		e.setAttribute(GraphVisualization.colorKey, c);
	}

	/** Returns the size of the node. **/
	public static double getSize(Node n) {
		return n.getAttribute(GraphVisualization.sizeKey);
	}

	/** Sets the node to a given size. **/
	public static void setSize(Node n, double size) {
		n.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Increases the size of the given node by value x. **/
	public static void increaseSize(Node n, double x) {
		double size = (double) n.getAttribute(GraphVisualization.sizeKey) + x;
		if (size < 0)
			size = 0;

		// set attribute
		n.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Decreases the size of the given node by value x. **/
	public static void decreaseSize(Node n, double x) {
		GraphStyleUtils.increaseSize(n, -x);
	}

	/** Returns the size of the edge. **/
	public static double getSize(Edge e) {
		return e.getAttribute(GraphVisualization.sizeKey);
	}

	/** Sets the edge to a given size. **/
	public static void setSize(Edge e, double size) {
		e.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Increases the size of the given node by value x. **/
	public static void increaseSize(Edge e, double x) {
		double size = (double) e.getAttribute(GraphVisualization.sizeKey) + x;

		// set attribute
		e.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Decreases the size of the given node by value x. **/
	public static void decreaseSize(Edge e, double x) {
		GraphStyleUtils.increaseSize(e, -x);
	}

	/** Updates the nodes style. **/
	public static void updateStyle(Node n) {
		String style = "";
		// add color
		if (n.hasAttribute(GraphVisualization.colorKey)) {
			Color c = n.getAttribute(GraphVisualization.colorKey);
			style += "fill-color: rgb(" + c.getRed() + "," + c.getGreen() + ","
					+ c.getBlue() + ");";
		}

		// calc size
		double size = (double) n.getAttribute(GraphVisualization.sizeKey);

		if (size < 1)
			size = 1;

		style += "size: " + size + "px;";

		// set style
		n.setAttribute(GraphVisualization.styleKey, style);
	}

	/** Updates the nodes style. **/
	public static void updateStyle(Edge e) {
		String style = "";
		// add color
		if (e.hasAttribute(GraphVisualization.colorKey)) {
			Color c = e.getAttribute(GraphVisualization.colorKey);
			style += "fill-color: rgb(" + c.getRed() + "," + c.getGreen() + ","
					+ c.getBlue() + "); ";
		}

		// calc size
		double size = (double) e
				.getAttribute(GraphVisualization.defaultSizeKey);

		ArrayList<Double> growthList = e
				.getAttribute(GraphVisualization.growthListKey);
		for (double d : growthList)
			size += d;

		if (size < 1)
			size = 1;

		style += "size: " + size + "px; ";

		// set style
		e.setAttribute(GraphVisualization.styleKey, style);
	}
}
