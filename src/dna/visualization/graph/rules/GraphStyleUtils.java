package dna.visualization.graph.rules;

import java.awt.Color;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;

import dna.visualization.graph.GraphVisualization;

/** Utility class for graph style rules. **/
public class GraphStyleUtils {

	/** Returns the color of the element. **/
	public static Color getColor(Element e) {
		return e.getAttribute(GraphVisualization.colorKey);
	}

	/** Sets the element to a given color. **/
	public static void setColor(Element e, Color c) {
		e.setAttribute(GraphVisualization.colorKey, c);
	}

	/** Returns the size of the element. **/
	public static double getSize(Element e) {
		return e.getAttribute(GraphVisualization.sizeKey);
	}

	/** Sets the element to a given size. **/
	public static void setSize(Element e, double size) {
		e.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Increases the size of the given element by value x. **/
	public static void increaseSize(Element e, double x) {
		double size = (double) e.getAttribute(GraphVisualization.sizeKey) + x;
		if (size < 0)
			size = 0;

		// set attribute
		e.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Decreases the size of the given element by value x. **/
	public static void decreaseSize(Element e, double x) {
		GraphStyleUtils.increaseSize(e, -x);
	}

	/** Increases the size of the given element by value x. **/
	public static void increaseSize(Edge e, double x) {
		double size = (double) e.getAttribute(GraphVisualization.sizeKey) + x;

		// set attribute
		e.setAttribute(GraphVisualization.sizeKey, size);
	}

	/** Updates the elements style. **/
	public static void updateStyle(Element e) {
		String style = "";
		// add color
		if (e.hasAttribute(GraphVisualization.colorKey)) {
			Color c = e.getAttribute(GraphVisualization.colorKey);
			style += "fill-color: rgb(" + c.getRed() + "," + c.getGreen() + ","
					+ c.getBlue() + ");";
		}

		// calc size
		double size = (double) e.getAttribute(GraphVisualization.sizeKey);
		size = size < 0 ? 0 : size;

		style += "size: " + size + "px;";

		// set style
		e.setAttribute(GraphVisualization.styleKey, style);
	}

	/** Sets the label of the element. **/
	public static void setLabel(Element e, String text) {
		e.addAttribute(GraphVisualization.labelKey, text);
	}

	/** Returns the label of the element. **/
	public static String getLabel(Element e) {
		return e.getAttribute(GraphVisualization.labelKey);
	}

	/** Appends the text to the current label. **/
	public static void appendToLabel(Element e, String text) {
		if (e.hasAttribute(GraphVisualization.labelKey))
			setLabel(e, getLabel(e) + text);
		else
			setLabel(e, text);
	}
}
